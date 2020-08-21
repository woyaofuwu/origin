
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit.creditPayrelationChg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager.CustManagerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class CreateMebPayrelationChgBatBean extends CSBizBean
{

	private static Logger logger = Logger.getLogger(CreateMebPayrelationChgBatBean.class);

    public IDataset crtBat(IData inParam) throws Exception
    {
        IDataset resultList = new DatasetList();
        StringBuilder builder = new StringBuilder(50);
        String userId = inParam.getString("USER_ID");
        String tradeTypeCode = inParam.getString("TRADE_TYPE_CODE");
        String fireSource = inParam.getString("FIRE_SOURCE", "");
        String systime = SysDateMgr.getSysTime();

        // 查询集团用户资料
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        // 查询集团客户资料
        String grpCustId = userInfo.getString("CUST_ID", "");
        IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
        if (IDataUtil.isEmpty(grpCustInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_716);
        }

        // 查询账户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        IData acctInfo = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
        if (IDataUtil.isEmpty(acctInfo))
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_114);
        }
        String acctId = acctInfo.getString("ACCT_ID");
        IData accountInfos = UcaInfoQry.qryAcctInfoByAcctId(acctId);
        if (IDataUtil.isEmpty(accountInfos))
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_112);
        }
        //查询属于集团用户的代付关系
        IDataset payRelas = null;
        String batchTaskName = "";
        //暂停
        if("7811".equals(tradeTypeCode)){
            payRelas = PayRelaInfoQry.getAdvPayRelaByGrpUserIdAndAcctId(userId, acctId);
            batchTaskName = "集团信控代付关系暂停";
        }
        //恢复
        else if("7812".equals(tradeTypeCode)){
            payRelas = PayRelaInfoQry.getAdvPayRelaForRestart(userId, acctId);
            batchTaskName = "集团信控代付关系恢复";
        }
        
        if (IDataUtil.isEmpty(payRelas)){
            CSAppException.apperr(GrpException.CRM_GRP_718);
        }
        //查询集团产品用户已欠费账期(月数)
        //IData oweFeeInfo = AcctCall.qryOweCustInfoByUserId(userId);
        String oweMonths = "";//oweFeeInfo.getString("OWE_FEE_CYCLE", "0");
        // 创建集团信控代付关系暂停恢复批量任务
        IData BatData = new DataMap();
        IData CondStrData = new DataMap();
        CondStrData.put("USER_ID", userId);
        CondStrData.put("ACCT_ID", acctId);
        CondStrData.put("IF_SMS", "true");      //默认发完工短信
        CondStrData.put("TRADE_TYPE_CODE", tradeTypeCode);
        CondStrData.put("INSERT_DATE", systime);
        CondStrData.put("OWE_FEE_CYCLE", oweMonths);
        BatData.put("BATCH_OPER_TYPE", "PAYRELATIONSTATECHG");
        BatData.put("BATCH_TASK_NAME", batchTaskName);
        BatData.put("SMS_FLAG", "0");
        BatData.put("CODING_STR", CondStrData.toString());
        BatData.put("CREATE_TIME", systime);
        BatData.put("ACTIVE_FLAG", "1");
        BatData.put("ACTIVE_TIME", systime);
        BatData.put("DEAL_TIME", systime);
        BatData.put("DEAL_STATE", "1");
        BatData.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        //排重号码，避免重复发起
        IDataset disPayRelas = new DatasetList();
        for(int i=0;i<payRelas.size();i++){
            IData each = new DataMap();
            each.put("SERIAL_NUMBER", payRelas.getData(i).getString("SERIAL_NUMBER"));
            if(!disPayRelas.contains(each)){
                disPayRelas.add(each);
            }
        }
        String batchId = BatDealBean.createBat(BatData, disPayRelas);
        builder.append("批次号[" + batchId + "]");
        
        //下发短信处理:发送集团客户经理
        IData custManageData = CustManagerInfoQry.getCustmanagerSnByGrpUserId(userId);
        if(IDataUtil.isNotEmpty(custManageData)){
            String grpCustName = grpCustInfos.getString("CUST_NAME");
            String acctName = acctId+"|"+accountInfos.getString("PAY_NAME");
            String noticeContent = "";
            //暂停短信
            if("7811".equals(tradeTypeCode)){
                //暂停时代付关系截止到当前时间的上个月底
                noticeContent = "尊敬的客户经理，您好！您所分管的"+grpCustName+"集团，由于集团账户"+acctName+"已欠费，集团代付费用将转由成员个人付费，从"+SysDateMgr.getFirstDayOfThisMonth()+"开始生效。如需帮助，请咨询10086。中国移动。 ";
            }
            //恢复短信
            else if("7812".equals(tradeTypeCode)){
                noticeContent = "尊敬的客户经理，您好！您所分管的"+grpCustName+"集团，由于集团账户欠费已结清，将由集团继续代付成员费用，从"+SysDateMgr.getFirstDayOfThisMonth()+"开始生效。如需帮助，请咨询10086。中国移动。";
            }
            this.sendRegSms(custManageData.getString("SERIAL_NUMBER"), "-1", noticeContent);
        }
        //下发短信处理:发送集团联系人/管理员(是集团联系人/管理员，同时设置了接收欠费预警短信，则发送)
        IDataset grpMembs = GrpMebInfoQry.queryGrpMebManagerAndLinkerByCustId(grpCustId);
        if(IDataUtil.isNotEmpty(grpMembs)){
            String grpCustName = grpCustInfos.getString("CUST_NAME");
            String acctName = acctId+"|"+accountInfos.getString("PAY_NAME");
            for(int i=0;i<grpMembs.size();i++){
                IData eachMemb = grpMembs.getData(i);
                String noticeContent = "";
                //暂停短信
                if("7811".equals(tradeTypeCode)){
                    //暂停时代付关系截止到当前时间的上个月底
                    noticeContent = "尊敬的集团联系人/管理员，您好！由于集团账户"+acctName+"已欠费，集团代付费用将转由成员个人付费，从"+SysDateMgr.getFirstDayOfThisMonth()+"开始生效。如需帮助，请咨询10086。中国移动。";
                }
                //恢复短信
                else if("7812".equals(tradeTypeCode)){
                    noticeContent = "尊敬的集团联系人/管理员，您好！您所在"+grpCustName+"集团由于集团账户欠费已结清，将由集团继续代付成员费用，从"+SysDateMgr.getFirstDayOfThisMonth()+"开始生效。如需帮助，请咨询10086。中国移动。";
                }
                this.sendRegSms(eachMemb.getString("SERIAL_NUMBER"), eachMemb.getString("USER_ID","-1"), noticeContent);
            }
        }
        //如果是"CRM-集团业务-其他业务-集团代付信控特殊开机 "发起，则需要通知账务信控修改标识
        if("CRMWEB".equals(fireSource)){
        	//调账务接口通知账户该集团用户已恢复集团代付
        	IData callAcctData = new DataMap();
        	callAcctData.put("USER_ID", userId);
    		IDataset acctRets = AcctCall.noticeAcctChgGrpUserFlag(callAcctData);
        }
        IData retData = new DataMap();
        retData.put("ORDER_ID", builder.toString());
        resultList.add(retData);
        return resultList;
    }
    
    /**
     * 下发短信
     * @param serialNumber
     * @param userId
     * @param noticeContent
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-8
     */
    public void sendRegSms(String serialNumber, String userId, String noticeContent) throws Exception{
        if(StringUtils.isBlank(noticeContent)){
            return ;
        }
        IData smsdata = new DataMap();
        smsdata.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        smsdata.put("RECV_OBJECT", serialNumber);// 手机号码
        smsdata.put("NOTICE_CONTENT", noticeContent);
        smsdata.put("RECV_ID", userId);
        smsdata.put("SMS_TYPE_CODE", "20");// 用户办理业务通知
        smsdata.put("FORCE_START_TIME", "");
        smsdata.put("FORCE_END_TIME", "");
        smsdata.put("REMARK", "集团信控短信通知");
        SmsSend.insSms(smsdata);
    }

}
