
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.terminalAfterSales;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


/**
 * 
 * 
 * @author zyz
 * @version 1.0
 *
 */
public class AfterSalesQry extends CSBizBean
{
	
   static Logger logger=Logger.getLogger(AfterSalesQry.class);


	   /**
	    * 检查优惠券接口查询
	    * @param data
	    * @return
	    * @throws Exception
	    */
    public static IDataset queryCheckCouponInfo(IData data) throws Exception
    {
    	try {
		    IData param=new DataMap();
		    param.put("DISCOUNTNO", data.getString("DISCOUNT_NO"));
		    param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		    return Dao.qryByCode("TL_B_USER_COUPONS", "SEL_USER_CHECK_COUPONS_INFO", param);
		} catch (Exception e) {
			 throw e;
		}
    }
    
    /**
     * 使用优惠证券
     * @param data
     * @return
     * @throws Exception
     */
    public static  int  useCouponInfo(IData data) throws Exception{
    	try {
    		
    		StringBuilder sql=new StringBuilder();
    		
    		sql.append("  update tl_b_user_coupons  t set");
    		//收银员终端系统用户名
    		sql.append("  t.rsrv_str3='"+data.getString("USER")+"'");
    		//网点代码
    		sql.append("  ,t.rsrv_str4='"+data.getString("SITE_CODE")+"'");
    		 //业务单号
    		sql.append("  ,t.rsrv_str5='"+data.getString("SERVICE_NO")+"'");
    		
    		//使用金额
    		sql.append("  ,t.SPEND_VALUE='"+data.getString("AMOUNT")+"'");
    		
    		//使用状态
    		sql.append("  ,ticket_state='1' ");
    		//条件
    		sql.append(" where t.serial_number='"+data.getString("SERIAL_NUMBER")+"'");
    		sql.append(" and t.ticket_code='"+data.getString("DISCOUNT_NO")+"'");
    		sql.append(" and t.ticket_state='0' ");//0未使用
    		sql.append(" and t.ticket_end_date >= sysdate ");//要在有效期内
    		IData param=new DataMap();
    		return Dao.executeUpdate(sql, param);
		} catch (Exception e) {
			throw e;
		}
    }
    
    /**
     * 使用优惠证券后修改限额
     * @param data
     * @return
     * @throws Exception
     */
    public static  int  useCouponQuota(IData data) throws Exception{
    	try {
    		
    		StringBuilder sql = new StringBuilder();
    		
    		//查询审批工单号的sql
    		String sql_1 = "SELECT T.RSRV_STR1 FROM TL_B_USER_COUPONS T WHERE T.TICKET_CODE = '"+data.getString("DISCOUNT_NO")+"'";
    		IDataset ticket = Dao.qryBySql(new StringBuilder(sql_1), new DataMap());
    		String audit_Order_Id = "";
    		if(ticket.size()>0){
    			audit_Order_Id = ticket.getData(0).getString("RSRV_STR1");
    		}else{
    			return 0;
    		}
    		 
    		sql.append("  update tl_b_coupons_quota t set");
    		//更新限额
    		sql.append("  t.balance = to_number(balance)-to_number('"+data.getString("AMOUNT")+"') * 100");
    		//更新已使用总额
    		sql.append("  ,t.AMOUNTS = to_number(amounts)+to_number('"+data.getString("AMOUNT")+"') * 100");
    		 //以审批工单号作为修改的标准
    		sql.append("  where t.AUDIT_ORDER_ID = '"+audit_Order_Id+"'");

    		IData param=new DataMap();
    		return Dao.executeUpdate(sql, param);
		} catch (Exception e) {
			throw e;
		}
    }
    
    /**
     * 更换串号通知接口
     * @param data
     * @throws Exception
     */
    public static void updateImeiNoInfo(IData data) throws Exception{
    	try {
			IData param=new DataMap();
			param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
			param.put("CUSTNAME", data.getString("CUST_NAME"));
			param.put("SERVICENO", data.getString("SERVICE_NO"));
			param.put("SERVICETYPE", data.getString("SERVICE_TYPE"));
			param.put("IMEI", data.getString("IMEI"));
			param.put("NEWIMEI", data.getString("NEWIMEI"));
			param.put("UPDATETIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
    		Dao.insert("TL_B_USER_IMEI", param);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
    	
    }

    /**
     * 根据服务号获取优惠信息
     * @param input
     * @return
     * @throws Exception
     */
    public static  IDataset  getCouponInfoServiceno(IData input) throws Exception {
    	try {
			StringBuilder sql=new StringBuilder();
		     IData param=new DataMap();
		     	sql.append("select * from  tl_b_user_coupons t");
		     	sql.append(" where t.rsrv_str5='"+input.getString("SERVICENO")+"'");
		     	
    		return Dao.qryBySql(sql, param);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
    }
    
}
