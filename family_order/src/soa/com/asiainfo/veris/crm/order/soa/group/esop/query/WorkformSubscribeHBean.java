package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformSubscribeHBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static boolean insertWorkformSubscribeH(IData param) throws Exception
    {
        return Dao.insert("TF_BH_EOP_SUBSCRIBE", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryScribeHInfoByIbsysid(String ibsysid) throws Exception {
        IData param =new DataMap();
        param.put("IBSYSID", ibsysid);
        StringBuilder sql = new StringBuilder(200);
        sql.append(" SELECT * FROM TF_BH_EOP_SUBSCRIBE ");
        sql.append(" WHERE IBSYSID = :IBSYSID ");
        IDataset infos = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        return infos;
    }

	public static IDataset qrySubScribeInfoByProductNo(IData param) throws Exception{
		IDataset infos = Dao.qryByCodeParser("TF_BH_EOP_SUBSCRIBE", "SEL_BY_PRODUCTNO", param, Route.getJourDb(BizRoute.getRouteId()));
		return infos;
    }

    public static IDataset qryScribeHInfoByIbsysidForOpen(IData param) throws Exception {
        StringBuilder sql = new StringBuilder(200);
        sql.append(" SELECT * FROM TF_BH_EOP_SUBSCRIBE ");
        sql.append(" WHERE IBSYSID = :IBSYSID ");
        sql.append(" AND BPM_TEMPLET_ID = 'EDIRECTLINEOPENPBOSS' ");
        IDataset infos = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        return infos;
    }
    
    public static IDataset getSubScribeInfoByProductNo(IData param) throws Exception {
        StringBuilder sql = new StringBuilder(200);
        sql.append(" SELECT B.ATTR_VALUE PRODUCTNO,B.SUB_IBSYSID,A.GROUP_ID, A.*,b.record_num ");
        sql.append(" FROM TF_BH_EOP_SUBSCRIBE A, TF_BH_EOP_ATTR B ");
        sql.append(" WHERE A.BPM_TEMPLET_ID = 'ECHANGERESOURCECONFIRM' ");
        sql.append(" AND A.DEAL_STATE = '9' ");
        sql.append(" AND A.IBSYSID = B.IBSYSID ");
        sql.append(" AND B.NODE_ID ='archive' ");
        sql.append(" AND B.ATTR_CODE = 'PRODUCTNO' ");
        sql.append(" AND B.ATTR_VALUE = :PRODUCTNO ");
        IDataset infos = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        return infos;
    }
    
    public static IDataset getSubScribeInfoByIbsysid(IData param) throws Exception {
        StringBuilder sql = new StringBuilder(200);
        sql.append(" SELECT 'B' TAB_TYPE ");
        sql.append("   FROM TF_B_EOP_SUBSCRIBE A ");
        sql.append("  WHERE A.IBSYSID=:IBSYSID ");
        sql.append(" union all ");
        sql.append(" SELECT 'BH' TAB_TYPE ");
        sql.append("   FROM TF_BH_EOP_SUBSCRIBE A ");
        sql.append("   WHERE A.IBSYSID=:IBSYSID ");
        IDataset infos = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        return infos;
    }
}
