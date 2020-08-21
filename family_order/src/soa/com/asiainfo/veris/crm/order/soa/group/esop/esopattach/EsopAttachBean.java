package com.asiainfo.veris.crm.order.soa.group.esop.esopattach;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class EsopAttachBean
{
    public static void insertFtpFile(IDataset data) throws Exception
    {
        Dao.insert("WD_F_FTPFILE", data, Route.CONN_CRM_CEN);
    }

    public static void insertTempEsopTabAttach(IDataset tempEsopTabAttachInfos) throws Exception
    {
        Dao.insert("TEMP_ESOP_TAB_ATTACH", tempEsopTabAttachInfos, Route.getJourDb(BizRoute.getRouteId()));
    }
}
