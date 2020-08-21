
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CmnetTradeQueryQry
{
    public static IDataset getCmentTradeInfo(IData inparams, Pagination page) throws Exception // Pagination page
    {
        return Dao.qryByCode("TF_B_TRADE", "SEL_CMNET_TRADE", inparams, page);// page
    }

    public static IDataset getDiscntset(IData inparams, Pagination page) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_CMNET_DISCNT_BY_USERID", inparams, page);
    }

    public static IDataset getSvcset(IData inparams, Pagination page) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_CMNET_SERVICE_BY_USERID", inparams, page);
    }

    public static IDataset getUserinfo(IData inparams, Pagination page) throws Exception
    {
        return Dao.qryByCode("TF_F_USER", "SEL_USERS_BY_SN", inparams, page);
    }
}
