
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class IbPlaySynQry
{

    public static IDataset queryHisIbPlatSyn(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_HIS_IBPLAT_SYN", params, pagination);
    }

    public static IDataset queryIbPlatSyn(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_IBPLAT_SYN", params, pagination);
    }

}
