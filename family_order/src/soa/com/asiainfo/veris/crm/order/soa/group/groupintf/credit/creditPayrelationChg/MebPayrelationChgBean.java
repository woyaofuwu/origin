
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit.creditPayrelationChg;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager.CustManagerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.BatGrpWorkPhoneMemCreateTrans;



public class MebPayrelationChgBean extends MemberBean
{
    private static Logger logger = Logger.getLogger(BatGrpWorkPhoneMemCreateTrans.class);
    private String dealFlag = "";

    protected MebPayrelationChgReqData reqData = null;
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        this.dealUserPayRelas();
    }
    /**
     * 集团代付关系暂停恢复处理
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-7
     */
    public void dealUserPayRelas() throws Exception
    {
        //集团代付关系暂停
        if("7811".equals(this.getTradeTypeCode())){
            this.stopUserPayRelas();
        }
        //集团代付关系恢复
        else if("7812".equals(this.getTradeTypeCode())){
            this.reStartUserPayRelas();
        }
    }
    /**
     * 集团代付关系暂停
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-7
     */
    public void stopUserPayRelas() throws Exception{
        String userId = reqData.getUca().getUserId();       // 成员用户
        String userIdA = reqData.getGrpUca().getUserId();   // 集团用户
        String acctId = reqData.getAcctId();                //  集团账户
        String insertDate = reqData.getInsertDate();
        String productId = reqData.getGrpUca().getProductId(); //产品编码
        String productName = "";
        String systime = SysDateMgr.getSysTime();
        String lastCycleLastAcct = PayRelaInfoQry.getPayrelationbyEndCycleId(insertDate);
        
        if(StringUtils.isNotBlank(productId)){
    		OfferCfg offerCfg = OfferCfg.getInstance(productId, "P");
    		if(offerCfg != null){    			 
    			productName = offerCfg.getOfferName();
    		}
    	}
    

        //获取用户的特殊付费关系
        IDataset specPayRelas = UserSpecialPayInfoQry.qryUserSpecialPayByUGAID(userId, userIdA, acctId);
        if(IDataUtil.isNotEmpty(specPayRelas)){
            for(int i=0;i<specPayRelas.size();i++){
                //可暂停的特殊付费关系处理
                IData eachSpec = specPayRelas.getData(i);
                String specEndCycleId = eachSpec.getString("END_CYCLE_ID");  //原结束账期，恢复时用
                eachSpec.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());           //暂停
                eachSpec.put("END_CYCLE_ID", lastCycleLastAcct);             //信控发起的上个月底
                eachSpec.put("RSRV_STR2", "CREDIT_STOP"+"|"+specEndCycleId); //标识该条记录是集团信控代付关系暂停的，便于后期的代付关系恢复
                eachSpec.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());        //
                eachSpec.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());        //
                eachSpec.put("UPDATE_TIME", systime);                        
                eachSpec.put("REMARK", "信控发起集团代付关系暂停");            
                this.addTradeUserSpecialepay(eachSpec);
            }
        }
        
        //可暂停的付费关系处理
        IDataset payRelas = PayRelaInfoQry.getAllPayrelationbyUA(userId, acctId, null);
        if(IDataUtil.isNotEmpty(payRelas)){
            for(int j=0;j<payRelas.size();j++){
                IData eachPayRela = payRelas.getData(j);
                String payEndCycleId = eachPayRela.getString("END_CYCLE_ID");   //原结束账期，恢复时用
                eachPayRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());           //暂停
                eachPayRela.put("END_CYCLE_ID", lastCycleLastAcct);             //信控发起的上个月底
                eachPayRela.put("RSRV_STR10", "CREDIT_STOP"+"|"+payEndCycleId);               //标识该条记录是集团信控代付关系暂停的，便于后期的代付关系恢复
                eachPayRela.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());        //
                eachPayRela.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());        //
                eachPayRela.put("UPDATE_TIME", systime);                   
                eachPayRela.put("REMARK", "信控发起集团代付关系暂停");        //
                this.addTradePayrelation(eachPayRela);
            }
        }
        
        //完工短信处理
        if(IDataUtil.isNotEmpty(specPayRelas) || IDataUtil.isNotEmpty(payRelas)){
        	//是关键人同时是集团代付成员，则不发个人短信
        	IData gkDt = CustManagerInfoQry.getGroupKeyMemByGrpCustIdAndMemSn(reqData.getGrpUca().getCustId(), reqData.getUca().getSerialNumber());
        	if(gkDt==null || gkDt.isEmpty()){
        		Date date = DateUtils.addDays(DateUtils.parseDate(lastCycleLastAcct, SysDateMgr.PATTERN_TIME_YYYYMMDD), 1);
                String startCycleId = DateFormatUtils.format(date, SysDateMgr.PATTERN_STAND_YYYYMMDD);
                //String noticeContent = "尊敬的客户，您好！您所在集团为您办理的集团代付费用，由于集团账户已欠费，将转由您个人付费，从"+startCycleId+"开始生效。如需帮助，请咨询10086。中国移动。";
                //REQ202003160006_关于优化信控代付费关系取消后集团联系人收到的提醒短信内容的需求-guonj-20200330
                //REQ202004300043 优化集团统付业务短信模板的需求  update by chenjg-20200520 
                String noticeContent = "尊敬的客户，您好！您所在集团为您办理的集团代付费用，代付产品为"+productName+"，由于集团账户已欠费，将转由您个人付费，从"+startCycleId+"开始生效，如需帮助，请咨询10086，中国移动。";
                this.bizData.getTrade().put("RSRV_STR10", noticeContent);
        	}
        }
        
        //标识集团代付已暂停
        this.markGrpUserSpecPayCreditState(userIdA, "CRDIT_STOP");
    }
    /**
     * 集团代付关系恢复
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-7
     */
    public void reStartUserPayRelas() throws Exception{
        String userId = reqData.getUca().getUserId();       // 成员用户
        String userIdA = reqData.getGrpUca().getUserId();   // 集团用户
        String acctId = reqData.getAcctId();                // 集团账户
        String insertDate = reqData.getInsertDate();
        String systime = SysDateMgr.getSysTime();
        //获取用户可恢复的特殊付费关系
        IDataset specPayRelas = UserSpecialPayInfoQry.qryUserSpecialPayByUGAIDForRestart(userId, userIdA, acctId);
        if(IDataUtil.isNotEmpty(specPayRelas)){
            for(int i=0;i<specPayRelas.size();i++){
                //可恢复的特殊付费关系处理
                IData eachSpec = specPayRelas.getData(i);
                String rsrvStr2 = eachSpec.getString("RSRV_STR2", "CREDIT_STOP|"+SysDateMgr.getEndCycle20501231());
                String[] arrs = rsrvStr2.split("\\|");
                String specEndCycleId = arrs.length>1 ? arrs[1] : SysDateMgr.getEndCycle20501231();
                eachSpec.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());                       //暂停
                eachSpec.put("END_CYCLE_ID", specEndCycleId);                            //信控发起的上个月底
                eachSpec.put("RSRV_STR2", "CREDIT_RESTART");                               //标识该条记录是集团信控代付关系暂停的，便于后期的代付关系恢复
                eachSpec.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());        //
                eachSpec.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());        //
                eachSpec.put("UPDATE_TIME", systime);                                   
                eachSpec.put("REMARK", "信控发起集团代付关系恢复");            
                this.addTradeUserSpecialepay(eachSpec);
            }
        }
        
        //可恢复的付费关系处理
        IDataset payRelas = PayRelaInfoQry.getAllPayrelationbyUAForRestart(userId, acctId);
        if(IDataUtil.isNotEmpty(payRelas)){
            for(int j=0;j<payRelas.size();j++){
                IData eachPayRela = payRelas.getData(j);
                String rsrvStr10 = eachPayRela.getString("RSRV_STR10", "CREDIT_STOP|"+SysDateMgr.getEndCycle20501231());
                String[] arrs2 = rsrvStr10.split("\\|");
                String payEndCycleId = arrs2.length>1 ? arrs2[1] : SysDateMgr.getEndCycle20501231();
                eachPayRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());           //暂停
                eachPayRela.put("END_CYCLE_ID", payEndCycleId);                 //信控发起的上个月底
                eachPayRela.put("RSRV_STR10", "CREDIT_RESTART");                //标识该条记录是集团信控代付关系暂停的，便于后期的代付关系恢复
                eachPayRela.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());        //
                eachPayRela.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());        //
                eachPayRela.put("UPDATE_TIME", systime);                        
                eachPayRela.put("REMARK", "信控发起集团代付关系恢复");            //
                this.addTradePayrelation(eachPayRela);
            }
        }
        
        //完工短信处理
        if(IDataUtil.isNotEmpty(specPayRelas) || IDataUtil.isNotEmpty(payRelas)){
        	//是关键人同时是集团代付成员，则不发个人短信
        	IData gkDt = CustManagerInfoQry.getGroupKeyMemByGrpCustIdAndMemSn(reqData.getGrpUca().getCustId(), reqData.getUca().getSerialNumber());
        	if(gkDt==null || gkDt.isEmpty()){
        		String sCycle = insertDate.substring(0, 8)+"01";       //恢复本月生效
                String noticeContent = "尊敬的客户，您好！您所在的集团，由于集团账户欠费已结清，将由集团继续为您代付相关费用，从"+sCycle+"开始生效。如需帮助，请咨询10086。中国移动。";
                this.bizData.getTrade().put("RSRV_STR10", noticeContent);
        	}
        }
        
        //标识集团代付已恢复
        this.markGrpUserSpecPayCreditState(userIdA, "");
    }
    /**
     * 下发短信
     * @param noticeContent
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-8
     */
    public void sendRegSms(String noticeContent) throws Exception{
        String serialNumber = reqData.getUca().getSerialNumber();
        String userId = reqData.getUca().getUserId();
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

    /**
     * 修改付费关系表
     * 
     * @throws Exception
     */
    public void infoRegPayRela() throws Exception
    {
        String userId = reqData.getUca().getUserId();       // 成员用户
        String userIdA = reqData.getGrpUca().getUserId();   // 集团用户
        String acctId = reqData.getAcctId();                //集团账户
        IDataset specialDataset = UserSpecialPayInfoQry.qryUserSpecialPay(userId, userIdA);
        IDataset tempSpecialDataset = new DatasetList();
        for (int i = 0; i < specialDataset.size(); i++)
        {
            IData tempData = specialDataset.getData(i);
            if (acctId.equals(tempData.getString("ACCT_ID", "")))
            {
                tempSpecialDataset.add(tempData);
            }
        }

        IDataset acctInfos = new DatasetList();
        IDataset tempAcctInfos = PayRelaInfoQry.getPayrelationByUserIdAndAcctId(acctId, userId, null);
        for (int i = 0; i < tempAcctInfos.size(); i++)
        {
            IData tempAcctInfo = tempAcctInfos.getData(i);
            for (int j = 0; j < tempSpecialDataset.size(); j++)
            {
                IData tempData = tempSpecialDataset.getData(j);
                if (tempAcctInfo.getString("PAYITEM_CODE", "").equals(tempData.getString("PAYITEM_CODE", "")) && tempAcctInfo.getString("LIMIT", "").equals(tempData.getString("LIMIT", ""))
                        && tempAcctInfo.getString("LIMIT_TYPE", "").equals(tempData.getString("LIMIT_TYPE", "")))
                {
                    acctInfos.add(tempAcctInfo);
                }
            }
        }

        if (dealFlag.equals("stop"))
        {
            IDataset payRelaSet = new DatasetList();
            String endCycle = "";
            IData tempAcctInfo = new DataMap();
            for (int i = 0; i < acctInfos.size(); i++)
            {
                IData acctInfo = acctInfos.getData(i);
                String startCycleId = acctInfo.getString("START_CYCLE_ID", "");
                String endCycleId = acctInfo.getString("END_CYCLE_ID", "");
                // 分散账期改动
                String lastTimeThisAcctday = DiversifyAcctUtil.getLastDayThisAcct(userId);
                lastTimeThisAcctday = java.sql.Date.valueOf(lastTimeThisAcctday.split(" ")[0]).toString();
                lastTimeThisAcctday = lastTimeThisAcctday.replace("-", "").substring(0, 8);
                String acctTag = acctInfo.getString("ACT_TAG", "");
                if ((Integer.parseInt(lastTimeThisAcctday)) >= (Integer.parseInt(startCycleId)) && (Integer.parseInt(lastTimeThisAcctday)) < (Integer.parseInt(endCycleId)) && "1".equals(acctTag))
                {
                    String instId = SeqMgr.getInstId();
                    IData payRela = new DataMap();
                    payRela.putAll(acctInfo);
                    tempAcctInfo.putAll(acctInfo);
                    endCycle = acctInfo.getString("END_CYCLE_ID", "");
                    payRela.put("END_CYCLE_ID", SysDateMgr.getNowCyc());

                    // 状态属性：0-增加，1-删除，2-变更
                    payRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                    IData payRelaADD = new DataMap();
                    payRelaADD.putAll(tempAcctInfo);
                    payRelaADD.put("USER_ID", userId); // 用户标识
                    payRelaADD.put("INST_ID", instId); // 实例标识
                    payRelaADD.put("ACT_TAG", "2"); // 作用标志：0-不作用，1-作用
                    payRelaADD.put("START_CYCLE_ID", SysDateMgr.getNextCycle());
                    payRelaADD.put("END_CYCLE_ID", endCycle);

                    // 状态属性：0-增加，1-删除，2-变更
                    payRelaADD.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                    payRelaSet.add(payRela);
                    payRelaSet.add(payRelaADD);

                }
                super.addTradePayrelation(payRelaSet);
            }
        }
        else
        {
            IDataset payitemCodeSet = new DatasetList();
            IDataset tempAcctInfos1 = new DatasetList();
            IDataset payRelaSet = new DatasetList();
            for (int i = 0; i < acctInfos.size(); i++)
            {
                IData acctInfo = acctInfos.getData(i);
                String payitemCode = acctInfo.getString("PAYITEM_CODE", "");
                if (!"".equals(payitemCode) && payitemCodeSet.toString().indexOf(payitemCode) == -1)
                {
                    payitemCodeSet.add(payitemCode);
                }
            }
            for (int i = 0; i < payitemCodeSet.size(); i++)
            {
                IDataset temp = DataHelper.filter(acctInfos, "PAYITEM_CODE=" + payitemCodeSet.get(i).toString());
                if (null != temp && temp.size() > 0)
                {
                    tempAcctInfos1.add(temp);
                }
            }

            for (int k = 0; k < tempAcctInfos1.size(); k++)
            {
                IDataset acctInfosGrps = (IDataset) tempAcctInfos1.get(k);
                String endCycleId = "";
                for (int i = 0; i < acctInfosGrps.size(); i++)
                {
                    IData acctInfosGrp = acctInfosGrps.getData(i);
                    String tempEndCycleId = acctInfosGrp.getString("END_CYCLE_ID", "");
                    if (i == 0)
                    {
                        endCycleId = tempEndCycleId;
                    }
                    if (Integer.parseInt(endCycleId) < Integer.parseInt(tempEndCycleId))
                    {
                        endCycleId = tempEndCycleId;
                    }
                }
                for (int i = 0; i < acctInfosGrps.size(); i++)
                {
                    IData acctInfosGrp = acctInfosGrps.getData(i);
                    String acctTag = acctInfosGrp.getString("ACT_TAG", "");
                    // 分散账期改造
                    String startCycleId = acctInfosGrp.getString("START_CYCLE_ID", "");
                    String lastTimeThisAcctday = DiversifyAcctUtil.getLastDayThisAcct(userId);
                    String firstDayThisAcct = DiversifyAcctUtil.getFirstDayThisAcct(userId);
                    lastTimeThisAcctday = java.sql.Date.valueOf(lastTimeThisAcctday.split(" ")[0]).toString();
                    lastTimeThisAcctday = lastTimeThisAcctday.replace("-", "").substring(0, 8);
                    firstDayThisAcct = java.sql.Date.valueOf(firstDayThisAcct.split(" ")[0]).toString();
                    firstDayThisAcct = firstDayThisAcct.replace("-", "").substring(0, 8);
                    if ("2".equals(acctTag))
                    {
                        IData payRela = new DataMap();
                        payRela.putAll(acctInfosGrp);
                        if (Integer.parseInt(firstDayThisAcct) < Integer.parseInt(startCycleId))
                        {
                            payRela.put("END_CYCLE_ID", lastTimeThisAcctday);
                        }
                        else if (Integer.parseInt(firstDayThisAcct) == Integer.parseInt(startCycleId))
                        {

                        }
                        else if (Integer.parseInt(firstDayThisAcct) > Integer.parseInt(startCycleId))
                        {
                            String instId = SeqMgr.getInstId();
                            payRela.put("END_CYCLE_ID", SysDateMgr.getLastCycle());
                            IData payRelaAdd = new DataMap();
                            payRelaAdd.putAll(acctInfosGrp);
                            payRelaAdd.put("USER_ID", userId); // 用户标识
                            payRelaAdd.put("INST_ID", instId); // 实例标识
                            payRelaAdd.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
                            payRelaAdd.put("START_CYCLE_ID", SysDateMgr.getNowCyc());
                            payRelaAdd.put("END_CYCLE_ID", endCycleId);

                            // 状态属性：0-增加，1-删除，2-变更
                            payRelaAdd.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                            payRelaSet.add(payRelaAdd);
                        }
                        payRela.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用

                        // 状态属性：0-增加，1-删除，2-变更
                        payRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                        payRelaSet.add(payRela);
                    }
                }
                for (int j = 0; j < acctInfosGrps.size(); j++)
                {
                    if (null == payRelaSet || payRelaSet.size() == 0)
                    {
                        break;
                    }
                    IData acctInfosGrp = acctInfosGrps.getData(j);
                    String acctTag = acctInfosGrp.getString("ACT_TAG", "");
                    // 分散账期改造
                    String startCycleId = acctInfosGrp.getString("START_CYCLE_ID", "");
                    String tempEndCycId = acctInfosGrp.getString("END_CYCLE_ID", "");
                    String lastTimeThisAcctday = DiversifyAcctUtil.getLastDayThisAcct(userId);
                    lastTimeThisAcctday = java.sql.Date.valueOf(lastTimeThisAcctday.split(" ")[0]).toString();
                    lastTimeThisAcctday = lastTimeThisAcctday.replace("-", "").substring(0, 8);
                    if (!"2".equals(acctTag) && (Integer.parseInt(lastTimeThisAcctday) == (Integer.parseInt(tempEndCycId)) && Integer.parseInt(lastTimeThisAcctday) >= Integer.parseInt(startCycleId)))
                    {
                        IData payRela = new DataMap();
                        payRela.putAll(acctInfosGrp);
                        payRela.put("END_CYCLE_ID", endCycleId);

                        // 状态属性：0-增加，1-删除，2-变更
                        payRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                        payRelaSet.add(payRela);
                    }
                }
            }
            super.addTradePayrelation(payRelaSet);
        }
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new MebPayrelationChgReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (MebPayrelationChgReqData) getBaseReqData();
    }
    /**
     * add by chenzg@20161206
     */
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reqData.setTradeTypeCode(map.getString("TRADE_TYPE_CODE"));
        reqData.setPageRequestData(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setAcctId(map.getString("ACCT_ID"));
        reqData.setInsertDate(map.getString("INSERT_DATE"));
        reqData.setOweFeeCycle(map.getString("OWE_FEE_CYCLE"));
        reqData.setIfsms("true".equals(map.getString("IF_SMS", "false"))); // 是否发短信
        reqData.setNeedRule(false);
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForMebNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return reqData.getTradeTypeCode();
    }

    @Override
    protected void chkTradeBefore(IData map) throws Exception
    {

    }
    /**
     * 标识集团用户代付关系的暂停恢复
     * @param grpUserId
     * @param state
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-2-23
     */
    public void markGrpUserSpecPayCreditState(String grpUserId, String state) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", grpUserId);
    	param.put("STATE", state);
    	StringBuilder sql = new StringBuilder();
    	sql.append("UPDATE TF_F_USER A ");
    	sql.append("SET A.RSRV_STR10 = :STATE ");
    	sql.append("WHERE A.USER_ID = :USER_ID ");
    	Dao.executeUpdate(sql, param);
    }
    public static void main(String[] args) throws Exception{
        /*Date date = DateUtils.addDays(DateUtils.parseDate("20161130", SysDateMgr.PATTERN_TIME_YYYYMMDD), 1);
        String startCycleId = DateFormatUtils.format(date, SysDateMgr.PATTERN_TIME_YYYYMMDD);
        */
    }
}
