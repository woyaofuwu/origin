
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.specialtrademgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ProtectPassInfoQry
{
    /**
     * 查询用户CUST_ID
     * 
     * @param pd
     * @param td
     * @param data
     * @throws Exception
     */
    public static IDataset queryCustId(IData data) throws Exception
    {

        IDataset userInfos = Dao.qryByCode("TF_F_USER", "SEL_BY_SN", data);

        return userInfos;
    }

    /**
     * 查询用户是否已经办理服务密码保护
     * 
     * @param pd
     * @param USER_ID
     * @param RSRV_VALUE_CODE
     * @return
     * @throws Exception
     */
    public static IDataset queryUserOtherUserId(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRVVALUECODE_ALL", param);
    }

    /**
     * 查询用户是否开通了服务密码保护服务
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvcUserId(IData param) throws Exception
    {
        IDataset dataSet = new DatasetList();
        dataSet = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_SVC_ID", param);
        return dataSet;
    }
}
