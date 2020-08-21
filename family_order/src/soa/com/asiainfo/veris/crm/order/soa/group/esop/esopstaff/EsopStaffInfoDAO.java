package com.asiainfo.veris.crm.order.soa.group.esop.esopstaff;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class EsopStaffInfoDAO {
	
	/**
	 * 获取登陆情况
	 * @param staffId
	 * @param LoginId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryStaffLog(String staffId, String LoginId) throws Exception{
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("LOG_ID", LoginId);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select t.*,t.rowid RID from TL_M_STAFFLOG t where in_time>=trunc(sysdate-3)"); 
        parser.addSQL(" and staff_id= :STAFF_ID");
        parser.addSQL(" and log_id = :LOG_ID");
        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_LOG));
    }
	
	/**
	 * 获取员工的下级员工
	 * @param staff_manager_id
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryByStaffManager(String staff_manager_id) throws Exception{
		IData param = new DataMap();
        param.put("MANAGER_STAFF_ID", staff_manager_id);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select staff_id from td_m_staff t "); 
        parser.addSQL(" where manager_staff_id= :MANAGER_STAFF_ID");
        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_SYS));
	}
	
	/**
	 * 获取员工数据角色
	 * @param staffId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryStaffDataRight(String staffId) throws Exception{
		IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select data_code from tf_m_staffdataright where data_code in ('ESOP0001','ESOP0002','ESOP0003','ESOP0004','ESOP0005') "); 
        parser.addSQL(" and staff_id=:STAFF_ID");
        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_SYS));
	}

    /**
     * 查询访问信息
     * 
     * @param param
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryStaffLoginInfos(IData param, Pagination pg) throws Exception {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT LG.STAFF_ID,TO_CHAR(LG.IN_TIME,'YYYY-MM') MONTH_CODE,SUM(LG.ONLINE_TIME) ONLINE_TIME,COUNT(1) LOGIN_TIMES ");
        parser.addSQL(" FROM ");
        parser.addSQL(" (SELECT ROUND(nvl((NVL(A.OUT_TIME,A.ACTIVE_TIME) - A.IN_TIME)*86400,0)) ONLINE_TIME, A.* FROM TL_M_STAFFLOG A ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND A.SUBSYS_CODE = 'EOP' ");
        parser.addSQL(" AND A.STAFF_ID=:STAFF_ID ");
        parser.addSQL(" AND A.IN_TIME >= TO_DATE(:START_DATE,'YYYY-MM-DD') ");
        parser.addSQL(" AND A.IN_TIME <= TO_DATE(:END_DATE,'YYYY-MM-DD') ");
        parser.addSQL(" ) LG ");
        parser.addSQL(" GROUP BY LG.STAFF_ID,TO_CHAR(LG.IN_TIME,'YYYY-MM') ORDER BY TO_CHAR(LG.IN_TIME,'YYYY-MM') DESC,LG.STAFF_ID DESC ");

        IDataset ss = Dao.qryByParse(parser, pg, Route.CONN_LOG);

        return ss;
    }
}
