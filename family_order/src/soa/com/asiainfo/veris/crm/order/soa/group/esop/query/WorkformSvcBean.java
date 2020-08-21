package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformSvcBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformSvc(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_SVC", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset qrySvcByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_SVC", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void delSvcByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_SVC", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qrySvcBySubIbsysidAndRecordNum(String subIbsysid, String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCode("TF_B_EOP_SVC", "SEL_BY_SUBIBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qrySvcBySubIbsysidAndRecordNumOrderByCode(String subIbsysid, String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCode("TF_B_EOP_SVC", "SEL_BY_SUBIBSYSID_RECORDNUM_ORDER", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
