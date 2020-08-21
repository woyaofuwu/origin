package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformErrorLogBean {
	
	   /**
     *
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static boolean insertErrorLogInfo(IData param) throws Exception
    {
        return Dao.insert("TF_B_EWE_ERROR_LOG", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
}