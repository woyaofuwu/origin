
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserDeductInfoQry
{
    /**
     * 根据AccId查询用户的个人支付业务信息
     * 
     * @param accId
     * @param destroyTag
     * @return
     * @throws Exception
     */
    public static IDataset getUserDeductByAccId(String accId, String destroyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", accId);
        param.put("DESTROY_TAG", destroyTag);

        return Dao.qryByCode("TF_F_USER_DEDUCT", "SEL_BY_ACCT", param);
    }

    /**
     * 根据serialNumber查询用户的个人支付业务信息
     * 
     * @param serialNumber
     * @param destroyTag
     * @return
     * @throws Exception
     */
    public static IDataset getUserDeductBySn(String serialNumber, String destroyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("BANK_ACCOUNT_NO", serialNumber);
        param.put("DESTROY_TAG", destroyTag);

        return Dao.qryByCode("TF_F_USER_DEDUCT", "SEL_BY_BANKACCT", param);
    }

    /**
     * 根据UserId查询用户的个人支付业务信息
     * 
     * @param userId
     * @param destroyTag
     * @return
     * @throws Exception
     */
    public static IDataset getUserDeductByUserId(String userId, String destroyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DESTROY_TAG", destroyTag);

        return Dao.qryByCode("TF_F_USER_DEDUCT", "SEL_BY_USER", param);
    }
}
