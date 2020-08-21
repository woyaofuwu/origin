package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * order
 * eoms地址信息查询
 * @author ckh
 * @date 2018/3/28.
 */
public class EopEomsInfoQry
{
    public static IDataset qryEomsAddrInfo(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_B_EOP_EOMS_INFO", "SEL_EOMS_ADDR_INFO", param, pagination, Route.getJourDbDefault());
    }
}
