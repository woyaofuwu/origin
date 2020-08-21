package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformPageSaveHBean
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformPageSaveH(IDataset param) throws Exception
    {
        return Dao.insert("TF_BH_EOP_PAGE_SAVE", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
