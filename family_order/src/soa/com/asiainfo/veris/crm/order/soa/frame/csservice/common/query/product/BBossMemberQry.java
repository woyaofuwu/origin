package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BBossMemberQry {
	/**
	 * chenyi
	 * 2015-2-3
	 * 查询成员接口需要返回信息
	 * @param attrCode
	 * @return
	 * @throws Exception
	 */
	 public static IDataset qryBBossMemInfp(String productSpecNum) throws Exception
	  {
		 IData param = new DataMap();
	     param.put("PRODUCT_ID", productSpecNum);

	     SQLParser parser = new SQLParser(param);
	     parser.addSQL("  select PRODUCT_ID, ");
	     parser.addSQL("  ORDER_TYPE, ");
	     parser.addSQL("  RSP_OBJ   , ");
	     parser.addSQL("  RSP_KEY   , ");
	     parser.addSQL("  RSP_VALUE , ");
	     parser.addSQL("  REQ_VALUE  , ");
	     parser.addSQL("  REQ_KEY ,   ");
	     parser.addSQL("  RSRV_STR1 , ");
	     parser.addSQL("  RSRV_STR2 , ");
	     parser.addSQL("  RSRV_STR3 , ");
	     parser.addSQL("  RSRV_STR4 , ");
	     parser.addSQL("  RSRV_STR5 , ");
	     parser.addSQL("  RSRV_TAG1 , ");
	     parser.addSQL("  RSRV_TAG2 , ");
	     parser.addSQL("  RSRV_TAG3 , ");
	     parser.addSQL("  RSRV_TAG4 , ");
	     parser.addSQL("  RSRV_TAG5  ");
	     parser.addSQL("  FROM TD_S_BBOSS_MEMBER    ");
	     parser.addSQL("   WHERE 1=1   ");
	     parser.addSQL("   AND   PRODUCT_ID= :PRODUCT_ID  ");
	     return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	  }
}
