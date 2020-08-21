
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryChkRealNameQry extends CSBizBean
{

    public static IDataset getUserRealNameInfo(String serialNumber, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_REALNAME_BY_SN", params, pagination);

    }
    
    public static IDataset getUserRealNameInfoValid(String serialNumber, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_REALNAME_BY_SN_VALID", params, pagination);

    }

    public static IDataset getUserRealNameInfoByUserId(String userId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_REALNAME_BY_USER_ID", params, pagination);

    }
}
