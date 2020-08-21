
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


public class EspFileIntfBean extends CSBizBean
{
	
  public static IDataset getOrderMemberInfo() throws Exception{
	  
	 return Dao.qryByCodeParser("TD_B_ESP_MEMB_SYNINFO", "SEL_ORDER_MEMBINFOS",null,Route.CONN_CRM_CEN);	
  }
  public static void insertEspMemberDealResult(IData retResult) throws Exception
  {
    
    Dao.insert("TD_B_ESP_MEMB_INFO_DEALRESULT", retResult,Route.CONN_CRM_CEN);
  }
  public static int UpdateSynMebInfo(IData retResult) throws Exception
  {
	    SQLParser sqlParser = new SQLParser(retResult);
		sqlParser.addSQL("  update TD_B_ESP_MEMB_SYNINFO  set  FLAG = '1' ");
		sqlParser.addSQL(", RESULT_CODE = :RSP_CODE ,RESULT_INFO = :RSP_DESC ");
		sqlParser.addSQL(" ,UPDATE_TIME = sysdate , UPDATE_STAFF_ID  = :UPDATE_STAFF_ID, UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
		sqlParser.addSQL(" WHERE SYN_ID = :SYN_ID  AND MEMBER_NUMBER = :MEMBER_NUMBER AND PRODUCT_ORDER_ID=:PRODUCT_ORDER_ID");
		return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
  }
//  public static IDataset getElementsAndExtends(String serial_number) throws Exception
//  {
//    IData input=new DataMap();
//    input.put("MEMBER_NUMBER", serial_number);
//    return Dao.qryByCode("TD_B_ESP_MEMB_ELEMENT", "SEL_MEMB_EXTENDS_INFO", input,Route.CONN_CRM_CEN);
//  } 
//  public static IDataset getElementsAttrInfo(String serial_number,IData membInfo) throws Exception
//  {
//    IData input=new DataMap();
//    input.put("MEMBER_NUMBER", serial_number);
//    input.put("MEMBET_PATE_PLANID", membInfo.getString("MEMBET_PATE_PLANID"));
//    return Dao.qryByCode("TD_B_ESP_MEMB_ELEMENT", "SEL_MEMB_ELEMENT_ATTRS", input,Route.CONN_CRM_CEN);
//  } 
}