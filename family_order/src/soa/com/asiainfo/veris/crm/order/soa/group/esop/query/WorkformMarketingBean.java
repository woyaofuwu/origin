package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WorkformMarketingBean {
	
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformEoms(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_MARKETING", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset selMarketingByCondition2 (IData param) throws Exception{
    	return Dao.qryByCodeParser("TF_B_EOP_MARKETING", "SEL_MARKETING_CONDITION2", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset selMarketingByProductNO (IData param) throws Exception{
    	return Dao.qryByCodeParser("TF_B_EOP_MARKETING", "SEL_MARKETING_BYPROUDCTNO", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}