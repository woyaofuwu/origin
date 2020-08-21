
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryHIServiceQry extends CSBizBean
{
    public static IDataset getSubscriberSvcVarBySvcId(String serviceID) throws Exception
    {
        IData params = new DataMap();
        params.put("SERVICE_ID", serviceID);
        return Dao.qryByCode("TD_O_SUBSCRIBER_SVC_VAR", "SEL_BY_SVCID", params);
    }

    public static IDataset getUserResInfos(String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("USER_RES_TYPE", "1");
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_ONE", params);
    }

    /**
     * 获取VPMN资料 <BR>
     * 
     * @param user_id
     *            用户标识
     * @return
     * @throws Exception
     */
    public static IDataset getUserVPMNInfos(String user_id) throws Exception
    {
        IData inParams = new DataMap();
        inParams.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_VPMN", "SEL_BY_USERID", inParams);
    }

    public static IDataset queryHIServiceBySN(String serialNumber, String eparchyCode, Pagination pagination, String serviceID) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("RSRV_STR2", serviceID);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_SN_IMEI_NEW", params, pagination);
    }
}
