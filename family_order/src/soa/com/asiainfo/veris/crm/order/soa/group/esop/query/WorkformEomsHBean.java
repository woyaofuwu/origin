package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformEomsHBean
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformEoms(IDataset param) throws Exception
    {
        return Dao.insert("TF_BH_EOP_EOMS", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset getHEomsDatasetByIbsysidRecordNum(String ibsysid,String recordNum) throws Exception
	{
		IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum); 

        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT * FROM TF_BH_EOP_EOMS T" );
        sql.append(" WHERE T.IBSYSID = :IBSYSID ");
        sql.append(" AND T.RECORD_NUM = :RECORD_NUM ");
        sql.append(" ORDER BY T.INSERT_TIME DESC,T.GROUP_SEQ DESC ");
        return Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
	}
}
