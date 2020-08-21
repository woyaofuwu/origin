
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.staffdept;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.CrmDAO;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class StaffDeptInfoQry
{

    public static IDataset getActiveHisMore(String tradeTable, IData cond, Pagination pagination, String routeId) throws Exception
    {

        SQLParser parser = new SQLParser(cond);
        parser.addSQL("select a.trade_id, a.user_id, a.discnt_code , b.discnt_name , a.start_date  , a.end_date from  ");
        parser.addSQL(tradeTable);
        parser.addSQL(" a, td_b_discnt b where 1=1 ");
        parser.addSQL(" and b.discnt_code = a.discnt_code ");
        parser.addSQL(" and a.trade_id =:TRADE_ID");
        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset getActiveHisMore1(String tradeTable, IData cond, Pagination pagination, String routeId) throws Exception
    {

        SQLParser parser = new SQLParser(cond);
        parser.addSQL(" select a.trade_id,a.user_id,a.product_id ,a.product_name ,a.package_id,a.package_name ,a.start_date,a.end_date from  ");
        parser.addSQL(tradeTable);
        parser.addSQL(" a  where 1=1 ");
        parser.addSQL(" and a.trade_id =:TRADE_ID");
        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset getActiveHisMore2(String tradeTable, IData cond, Pagination pagination, String routeId) throws Exception
    {

        SQLParser parser = new SQLParser(cond);
        parser.addSQL("select a.trade_id, a.user_id, a.discnt_gift_id   , a.fee/100 fee  , a.payment_id ,a.start_date,a.end_date  from  ");
        parser.addSQL(tradeTable);
        parser.addSQL(" a  where 1=1 ");
        // parser.addSQL(" and b.discnt_gift_id = a.discnt_gift_id " );
        parser.addSQL(" and a.trade_id =:TRADE_ID");
        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset getActiveHisMore3(String tradeTable, IData cond, Pagination pagination, String routeId) throws Exception
    {

        SQLParser parser = new SQLParser(cond);
        parser.addSQL(" select a.trade_id,a.user_id, decode(a.fee_mode, 0, '营业费用', 1, '押金', 2, '预存') fee_mode ,a.oldfee/100 oldfee,a.fee/100 fee from  ");
        parser.addSQL(tradeTable);
        parser.addSQL(" a  where 1=1 ");
        parser.addSQL(" and a.trade_id =:TRADE_ID");
        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset getActiveHisMoreDis2(String tradeTable, IData cond, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(cond);
        parser.addSQL("select b.discnt_gift_name  from  ");
        parser.addSQL(" td_b_sale_deposit b where 1=1 ");
        parser.addSQL(" and b.discnt_gift_id = :DISCNT_ID ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset getActiveHisNameMore(String tradeTable, IData cond, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(cond);
        parser.addSQL("select b.discnt_name from  ");
        parser.addSQL("  td_b_discnt b where 1=1 ");
        parser.addSQL(" and b.discnt_code = :DISCNTCODE ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 查询客户原子信息
     * 
     * @param cond
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryBhTradeInfos(String tradeTable, IData cond, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(cond);
        parser.addSQL("select b.trade_type, a.accept_date,a.trade_staff_id ,a.trade_depart_id ,a.trade_id ,a.advance_pay/100 advance_pay from ");
        parser.addSQL(tradeTable);
        parser.addSQL(" a, td_s_tradetype b where 1=1 ");
        parser.addSQL(" and user_id = (select user_id from tf_f_user where serial_number =:SERIAL_NUMBER and remove_tag = '0')");
        parser.addSQL(" and a.trade_type_code = 240");
        parser.addSQL(" and b.trade_type_code = a.trade_type_code");
        parser.addSQL("  and b.eparchy_code = a.eparchy_code");
        parser.addSQL(" order by a.accept_date");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 查询客户原子信息
     * 
     * @param cond
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryBhTradeTypeInfos(String tradeTable, IData cond, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(cond);
        parser.addSQL("select b.trade_type from ");
        parser.addSQL("  td_s_tradetype b where 1=1 ");
        parser.addSQL(" and b.trade_type_code = :TRADETYPECODE");
        parser.addSQL("  and b.eparchy_code = :EPARCHYCODE");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
	 * 跟据员工岗位角色获取员工列表
	 */
	public static IDataset getStaffList(String staffId, String staffName, String departCode, String departName,Pagination pagination) throws Exception {
		IData cond = new DataMap();
		cond.put("STAFF_ID", staffId);
		cond.put("STAFF_NAME", staffName);
		cond.put("DEPART_CODE", departCode);
		cond.put("DEPART_NAME", departName);
		SQLParser parser = new SQLParser(cond);
		
		parser.addSQL(" select A.STAFF_NAME,");
		parser.addSQL("        A.STAFF_ID STAFF_ID,");
		parser.addSQL("        A.CITY_CODE,");
		parser.addSQL("        '[' || A.STAFF_ID || ']' || A.STAFF_NAME STAFF_TEXT,");
		parser.addSQL("        B.DEPART_CODE DEPART_CODE,");
		parser.addSQL("        B.DEPART_ID DEPART_ID,");
		parser.addSQL("        B.DEPART_NAME DEPART_NAME");
		parser.addSQL("   from TD_M_STAFF A, TD_M_DEPART B");
		parser.addSQL("  WHERE A.DIMISSION_TAG = '0'");
		parser.addSQL("    AND A.DEPART_ID = B.DEPART_ID");
		
		parser.addSQL("    AND A.STAFF_ID like '%'||:STAFF_ID||'%'");
		parser.addSQL("    AND A.STAFF_NAME like '%'||:STAFF_NAME||'%'");
		parser.addSQL("    AND B.DEPART_CODE = :DEPART_CODE");
		parser.addSQL("    AND B.DEPART_NAME like '%'||:DEPART_NAME||'%'");
		
		IDataset retList = Dao.qryByParse(parser, pagination, Route.CONN_SYS);
		
		return retList;
	}
	public static IData getTdMStaffValue(IData dataValue) throws Exception{
		CrmDAO dao =CrmDAO.createDAO(CrmDAO.class, Route.CONN_SYS);
		return dao.queryByPK("TD_M_STAFF", dataValue);
	}
	public static void addTdMStaffValue(IData bbossStaff) throws Exception{
		CrmDAO dao =CrmDAO.createDAO(CrmDAO.class, Route.CONN_SYS);
		dao.insert("TD_M_STAFF_BBOSS", bbossStaff);
	}
	public static void deleteTdMStaffValue(String staffid) throws Exception{
		if(StringUtils.isBlank(staffid)){
			return ;
		}
		IData param = new DataMap();
		param.put("STAFF_ID", staffid);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("  delete td_m_staff_bboss a ");
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" and  a.staff_id= :STAFF_ID ");
		parser.addSQL(" and  a.rsrv_tag1='2' ");
		Dao.executeUpdate(parser, Route.CONN_SYS);
	}
	public static void saveTdMStaff(IData dataValue) throws Exception{
		Dao.save("TD_M_STAFF", dataValue,  Route.CONN_SYS);
	}
	 /**
	 * 获取总部用户名
	 * @param provCode
	 * @return
	 * @throws Exception
	 */
	public static String getStaffNumber(String provCode)throws Exception{
		SQLParser parser = new SQLParser(null);
		parser.addSQL("select lpad(SEQ_BBOSS_STAFF_NUM.nextval,5,'0') SEQ_ID from dual");
		IDataset dataset = Dao.qryByParse(parser, Route.CONN_SYS);
		IData data = (IData) dataset.get(0);
		return provCode+(String) data.get("SEQ_ID");
	}
 /**
     * 获取工号信息
     * 
     * @param staff
     * @return
     * @throws Exception
     */
    public static IData getStaffInfo(IData param) throws Exception {
        IData data = new DataMap();
        if(StringUtils.isBlank(param.getString("STAFF_ID"))) {
            return data;
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.STAFF_ID, ");
        parser.addSQL(" A.STAFF_NAME, ");
        parser.addSQL(" A.EMAIL, ");
        parser.addSQL(" A.SERIAL_NUMBER, ");
        parser.addSQL(" A.CITY_CODE, ");
        parser.addSQL(" A.EPARCHY_CODE, ");
        parser.addSQL(" A.DEPART_ID, ");
        parser.addSQL(" B.DEPART_CODE, ");
        parser.addSQL(" B.DEPART_NAME ");
        parser.addSQL(" FROM TD_M_STAFF A, TD_M_DEPART B ");
        parser.addSQL(" WHERE A.DEPART_ID=B.DEPART_ID ");
        parser.addSQL(" AND A.STAFF_ID = :STAFF_ID ");
        IDataset dataset = Dao.qryByParse(parser, Route.CONN_SYS);
        if(dataset != null && dataset.size() > 0) {
            data = dataset.first();
        }
        return data;
    }
}
