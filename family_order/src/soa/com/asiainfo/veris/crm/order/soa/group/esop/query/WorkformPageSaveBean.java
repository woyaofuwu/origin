package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformPageSaveBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformPageSave(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_PAGE_SAVE", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset qryPageSaveByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_PAGE_SAVE", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void delPageSaveByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_PAGE_SAVE", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
