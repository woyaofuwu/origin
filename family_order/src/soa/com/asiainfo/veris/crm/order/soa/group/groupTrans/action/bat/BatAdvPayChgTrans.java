
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatAdvPayChgTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        String operType = IDataUtil.chkParam(condData, "OPER_TYPE");// 1:新增,0:删除
        IDataUtil.chkParam(condData, "PAYITEM_CODE");
        IDataUtil.chkParam(condData, "LIMIT_TYPE");
        IDataUtil.chkParam(condData, "LIMIT7");
        IDataUtil.chkParam(condData, "START_CYCLE");
        IDataUtil.chkParam(condData, "END_CYCLE");
        IDataUtil.chkParam(condData, "COMPLEMENT_TAG");

        // 查询用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(serial_number);

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
        //add by chenzg@20160926 REQ201609180012关于优化集团统付业务限制条件的要求
        CheckForGrp.chkPayRelaMebStateAndFeeInfo(userinfo);
        //add by chenzg@20161208 校验集团产品用户是否欠费
        CheckForGrp.chkGrpUserIsOwnFee(grpUserId);
        // 校验成员是否为VPMN成员
        String memUserId = userinfo.getString("USER_ID");
        String eparchyCode = userinfo.getString("EPARCHY_CODE");

        IData acctData = GroupBatTransUtil.getMemAcctData(memUserId, eparchyCode);

        batData.put("mem_user_id", memUserId);
        batData.put("MEM_ACCT_ID", acctData.getString("ACCT_ID"));
        batData.put("GRP_USER_ID", grpUserId);

        chkMemberInfo(operType, memUserId, batData);
        
        String userCityCode = "";//成员的city_code
        String  groupAcctCityCode  = "";//集团账户的city_code
        userCityCode = userinfo.getString("CITY_CODE","");
        String allowBrankCode = condData.getString("ALLOW_BRANK_CODE","");
        if(StringUtils.isNotBlank(allowBrankCode) && "VPMN".equals(allowBrankCode)){
            
            String acctId = condData.getString("ACCT_ID","");
            if(StringUtils.isNotBlank(acctId)){
                IData acctInfo = UcaInfoQry.qryAcctInfoByAcctIdForGrp(acctId);
                if (IDataUtil.isEmpty(acctInfo)){
                        CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_105);
                }
                groupAcctCityCode = acctInfo.getString("CITY_CODE","");
            }
            
            if(!"".equals(groupAcctCityCode) && !"".equals(userCityCode))
            {
                if("HNSJ".equals(groupAcctCityCode) || "HNHN".equals(groupAcctCityCode))
                {
                    if(!groupAcctCityCode.equals(userCityCode))
                    {
                        CSAppException.apperr(BatException.CRM_BAT_98);
                    }
                }
                else
                {
                    if("HNSJ".equals(userCityCode) || "HNHN".equals(userCityCode))
                    {
                        CSAppException.apperr(BatException.CRM_BAT_99);
                    }
                }
            }
        }
        
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String acctId = IDataUtil.chkParam(condData, "ACCT_ID");// 集团帐户ID
        String operType = IDataUtil.chkParam(condData, "OPER_TYPE");// 1:新增,0:删除
        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID", "8000"));
        svcData.put("ACCT_ID", acctId);// 集团帐户ID
        svcData.put("OPER_TYPE", operType);
        svcData.put("PAYITEM_CODE", IDataUtil.chkParam(condData, "PAYITEM_CODE"));
        svcData.put("LIMIT_TYPE", IDataUtil.chkParam(condData, "LIMIT_TYPE"));
        svcData.put("LIMIT", IDataUtil.chkParam(condData, "LIMIT7"));
        svcData.put("START_CYCLE_ID", IDataUtil.chkParam(condData, "START_CYCLE"));
        svcData.put("END_CYCLE_ID", IDataUtil.chkParam(condData, "END_CYCLE"));
        svcData.put("COMPLEMENT_TAG", IDataUtil.chkParam(condData, "COMPLEMENT_TAG"));
        svcData.put("crmSmsOrder", condData.getString("crmSmsOrder", ""));      //下发订购短信提醒
        svcData.put("acctSmsOrder", condData.getString("acctSmsOrder", ""));     //月初话费短信提醒
    }

    /**
     * 检查成员资料
     * 
     * @param operType
     * @throws Exception
     */

    public void chkMemberInfo(String operType, String mem_user_id, IData batData) throws Exception
    {
        String memAcctId = batData.getString("MEM_ACCT_ID");

        IData info = new DataMap();
        info.put("USER_ID", mem_user_id);

        if ("1".equals(operType))
        {
            IDataset purchase = UserSaleActiveInfoQry.queryPurchaseInfo(info);

            if (IDataUtil.isNotEmpty(purchase))
            {
                for (int i = 0, size = purchase.size(); i < size; i++)
                {
                    IData data = purchase.getData(i);
                    IDataset comInfos = CommparaInfoQry.getCommparaAllColByParser("CSM", "9987", data.getString("PRODUCT_ID"), "0898");
                    if (IDataUtil.isNotEmpty(comInfos))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_655);
                    }
                }
            }
        }

        IData payrelationInfo = UcaInfoQry.qryDefaultPayRelaByUserId(mem_user_id);
        if (IDataUtil.isEmpty(payrelationInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_735);

        }
        // 一卡多号 rela=30和97
        IDataset relationInfos = RelaUUInfoQry.queryRelationInfo(mem_user_id);
        if (IDataUtil.isNotEmpty(relationInfos))
        {
            IData relationInfo = relationInfos.getData(0);
            payrelationInfo = UcaInfoQry.qryDefaultPayRelaByUserId(relationInfo.getString("USER_ID_A"));
            String acctIda = payrelationInfo.getString("ACCT_ID"); // 主卡成员默认付费账户id
            if (acctIda.equals(memAcctId)) // 如果成员账户与主卡成员账户一样则报错
            {
                CSAppException.apperr(GrpException.CRM_GRP_656);
            }
        }
        // add by lixiuyu@20111110 统一付费业务副卡的号码办理集团统付业务时（包括所有的集团统付办理途径均要限制），要进行提示限制办理
        IDataset relaDatas = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(mem_user_id, "56", "2");
        if (IDataUtil.isNotEmpty(relaDatas))
        {
            CSAppException.apperr(GrpException.CRM_GRP_635);
        }
    }

}
