package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;



public class FamilyOperPreBean extends CSBizBean{

	public static IDataset getUserRelationByUserIdB(IData input) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID_B", input.getString("USER_ID_B"));
		param.put("RELATION_TYPE_CODE", input.getString("RELATION_TYPE_CODE"));
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERRELA_BY_IDB", param);
	}
	public static IDataset getAllRoleB(IData input) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID_A", input.getString("USER_ID_A"));
		param.put("RELATION_TYPE_CODE", input.getString("RELATION_TYPE_CODE"));
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_BY_A", param);
	}
	public static IDataset getOfferInfo(IData input) throws Exception{
		return Dao.qryByCode("PM_OFFER", "SEL_OFFER_NAME_BY_OFFERCODE_CACHE", input,Route.CONN_UPC);
	}
	public static IDataset getAllUserByUserIdA(String user_id_a) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		param.put("RELATION_TYPE_CODE", "45");
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_MEMBER_BY_IDA", param);
	}
	public static IDataset getUserProduct(String user_id) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		return Dao.qryByCodeParser("TF_F_USER_PRODUCT", "SEL_BY_PRODUCTID_USERID", param);
	}
	
	public static IDataset getShareUser(String user_id) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT * FROM TF_F_USER_SHARE_RELA T WHERE T.USER_ID_B=:USER_ID AND T.ROLE_CODE='02' AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
		return Dao.qryBySql(sql, param);
	}
	
	public static IDataset getMainShareUser(String shareId) throws Exception{
		IData param = new DataMap();
		param.put("SHARE_ID", shareId);
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT * FROM TF_F_USER_SHARE_RELA T WHERE T.SHARE_ID=:SHARE_ID AND T.ROLE_CODE='01' AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
		return Dao.qryBySql(sql, param);
	}
	
	public static IDataset getDiscntCode(String user_id) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT * FROM TF_F_USER_DISCNT T WHERE T.USER_ID=:USER_ID AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
		return Dao.qryBySql(sql, param);
	}
}
