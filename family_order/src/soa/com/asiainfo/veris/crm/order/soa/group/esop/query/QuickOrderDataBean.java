package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class QuickOrderDataBean
{
    /**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformQuickOrderData(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_QUICKORDER_DATA", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryWorkformQuickOrderDataInfos(String ibsysid) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        
        StringBuilder sql = new StringBuilder(100);
        sql.append(" SELECT * FROM TF_B_EOP_QUICKORDER_DATA T WHERE T.IBSYSID =:IBSYSID ");
        
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    } 
    
    public static int delWorkformQuickOrderData(String ibsysid) throws Exception{
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        
        StringBuilder sql = new StringBuilder(100);
        sql.append(" DELETE FROM TF_B_EOP_QUICKORDER_DATA T WHERE T.IBSYSID =:IBSYSID ");
        
        return Dao.executeUpdate(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

}
