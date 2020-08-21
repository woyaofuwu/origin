package com.asiainfo.veris.crm.iorder.soa.group.param.stopWideNetOneKeyFlowMain;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class StopWideNetOneKeyFlowMainBean extends CSBizBean
{

    /**
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData queryUserInfosForInit(IData param) throws Exception
    {

        String userId = param.getString("USER_ID");
        
        
        IData userInfo = new DataMap();
        IData productInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);//查询产品信息productInfo
        userInfo.put("PRODUST_INFO", productInfo);
        IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);//查询银行信息
        userInfo.put("ACCT_INFO", acctInfo);
        IData groupInfo = UcaInfoQry.qryGrpInfoByUserId(userId);//查询集团客户信息
        userInfo.put("GROUP_INFO", groupInfo);
        IDataset resInfo = UserResInfoQry.getUserResInfoByUserId(userId);//查询res资源信息
        userInfo.put("RES_INFO", resInfo);
        IData prodInfo = UcaInfoQry.qryMainProdInfoByUserIdForGrp(userId);
        userInfo.put("PROD_INFO", prodInfo);
        IDataset svcstateInfo = UserSvcStateInfoQry.queryUserSvcStateInfo(userId);
        userInfo.put("SVCSTATE_INFO", svcstateInfo);
        return userInfo;
    }
    
    /**
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData qryWaittingPayOrderByStaffId(IData param) throws Exception
    {
        boolean bResult = Dao.qryByRecordCount("TD_S_CPARAM", "ExistStaffPay", param);
        IData result = new DataMap();
        result.put("IS_EXIST_STAFF_PAY", bResult);
        return result;
    }
    
}