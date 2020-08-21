package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * order
 *
 * @author ckh
 * @data 2018/1/18.
 */
public class OpTaskInstHisQry
{
    public static void insertOpTaskInstHis(IDataset dataset) throws Exception
    {
        Dao.insert("TF_FH_OP_TASK_INST", dataset, Route.getJourDb(BizRoute.getRouteId()));
    }
}
