
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class ActiveStockInfoQry
{

	public static IDataset queryByResKind(String resKindCode, String staffId, String cityCode, String eparchyCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("RES_KIND_CODE", resKindCode);
		cond.put("STAFF_ID", staffId);
		cond.put("CITY_CODE", cityCode);
		cond.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TF_F_ACTIVE_STOCK", "SEL_BY_RESKIND", cond);
	}

	public static IDataset querysparkPlans(String staffIdS, String staffIdE, String cityCode, String departId, String staffId, Pagination pagination) throws Exception
	{
		IData cond = new DataMap();
		cond.put("STAFF_ID_S", staffIdS);
		cond.put("STAFF_ID_E", staffIdE);
		cond.put("CITY_CODE", cityCode);
		cond.put("DEPART_ID", departId);
		cond.put("STAFF_ID", staffId);

		SQLParser sql = new SQLParser(cond);
		sql.addSQL("SELECT ");
		sql.addSQL(" 	a.STAFF_ID, ");
		sql.addSQL(" 	a.CITY_CODE, ");
		sql.addSQL(" 	a.WARNNING_VALUE_D, ");
		sql.addSQL(" 	a.WARNNING_VALUE_U, ");
		sql.addSQL(" 	(a.WARNNING_VALUE_D - a.WARNNING_VALUE_U) SURPLUS_VALUE,");
		sql.addSQL(" 	a.eparchy_code, ");
		sql.addSQL(" 	a.RES_KIND_CODE, ");
		sql.addSQL(" 	c.depart_id ");
		sql.addSQL("  from tf_f_active_stock a, td_m_staff c");
		sql.addSQL(" where 1=1");
		sql.addSQL("   AND a.staff_id = c.staff_id");
		sql.addSQL("   AND a.WARNNING_VALUE_D > 0");
		sql.addSQL("   AND (a.CITY_CODE = :CITY_CODE OR :CITY_CODE IS NULL)");
		sql.addSQL("   AND (c.depart_id=:DEPART_ID OR :DEPART_ID IS NULL)");
		sql.addSQL("   AND (a.STAFF_ID=:STAFF_ID OR :STAFF_ID IS NULL)");
		sql.addSQL(" AND a.STAFF_ID >=:STAFF_ID_S");
		sql.addSQL(" AND a.STAFF_ID <= :STAFF_ID_E");
		
		IDataset result = new DatasetList(); 
		IDataset ids = Dao.qryByParse(sql, pagination);
		IDataset commparaInfos9221 = CommparaInfoQry.getCommparaAllColByParser("CSM","9211",null,"0898");
		if(IDataUtil.isNotEmpty(ids))
		{
			for(int i=0; i<ids.size(); i++)
			{
			    IData id = ids.getData(i);
			    String condFactor3 = id.getString("RES_KIND_CODE");
			    if("MFTY".equals(condFactor3))//优惠体验套餐
			    {
			    	IData data = new DataMap();
			        id.put("PACKAGE_ID", "MFTY");
			        id.put("PACKAGE_NAME", "免费体验礼包");
			        id.put("PRODUCT_NAME", "免费体验礼包");
			        
			        result.add(id);
			    }
			    //add by xuzh5 宽带提速优惠免费礼包 2018-9-20 14:20:11
			    else if("90T".equals(condFactor3)){
				        id.put("PACKAGE_ID", "90T");
				        id.put("PACKAGE_NAME", "90天免费提速包");
				        id.put("PRODUCT_NAME", "90天免费提速包");
				        result.add(id);
			    } else if("180T".equals(condFactor3)){
			    	 	id.put("PACKAGE_ID", "180T");
				        id.put("PACKAGE_NAME", "180天免费提速包");
				        id.put("PRODUCT_NAME", "180天免费提速包");
				        result.add(id);
			    } else if("360T".equals(condFactor3)){
			    	 	id.put("PACKAGE_ID", "360T");
				        id.put("PACKAGE_NAME", "360天免费提速包");
				        id.put("PRODUCT_NAME", "360天免费提速包");
				        result.add(id);
			    }else if("FTTH".equals(condFactor3)){
			    	 
			    	 id.put("PACKAGE_ID","1");
			    	 id.put("START_DATE", "2018-10-18");
				     id.put("END_DATE", "2050-12-31");
			    	 id.put("PACKAGE_NAME", "0元申领光猫礼包");
			    	 id.put("PRODUCT_NAME", "0元申领光猫礼包");
			    	 result.add(id);
			    }//k3
			    
			    //end by xuzh5 宽带提速优惠免费礼包 2018-9-20 14:20:11

			    else {
			    	//REQ202004020012新增全家WiFiVIP体验套餐
			        if(IDataUtil.isNotEmpty(commparaInfos9221)){
				    	for(int j=0; j<commparaInfos9221.size(); j++){
				    		String paraCode12 = commparaInfos9221.getData(j).getString("PARA_CODE12");
				    		String paramCode = commparaInfos9221.getData(j).getString("PARAM_CODE");
				    		String paraCode3 = commparaInfos9221.getData(j).getString("PARA_CODE3");
				    		String resKindCode = paramCode.substring(paramCode.length()-4, paramCode.length());
				    		if("VIP".equals(paraCode12)&&resKindCode.equals(condFactor3)){
				    			id.put("PACKAGE_ID", paramCode);
						        id.put("PACKAGE_NAME", paraCode3);
						        id.put("PRODUCT_NAME", paraCode3);
						        result.add(id);
						        break;
				    		}
				    	}
				    }
			    	
				    IDataset datas = UpcCall.qrySaleActiveCatalogByFactor(condFactor3);
				    if(IDataUtil.isNotEmpty(datas))
				    {
				        IData data = datas.getData(0);
				        id.put("PACKAGE_ID", data.getString("OFFER_CODE"));
				        id.put("PACKAGE_NAME", data.getString("DESCRIPTION"));
				        id.put("START_DATE", data.getString("VALID_DATE"));
				        id.put("END_DATE", data.getString("EXPIRE_DATE"));
				        id.put("PRODUCT_NAME", data.getString("CATALOG_NAME"));
				        
				        result.add(id);
				    }
			    }
			}
		}
		
//		SQLParser sql = new SQLParser(cond);
//        sql.addSQL("SELECT ");
//        sql.addSQL("    a.STAFF_ID, ");
//        sql.addSQL("    a.CITY_CODE, ");
//        sql.addSQL("    a.WARNNING_VALUE_D, ");
//        sql.addSQL("    a.WARNNING_VALUE_U, ");
//        sql.addSQL("    (a.WARNNING_VALUE_D - a.WARNNING_VALUE_U) SURPLUS_VALUE,");
//        sql.addSQL("    a.eparchy_code, ");
//        sql.addSQL("    a.res_kind_code, ");
//        sql.addSQL("    b.package_id, ");
//        sql.addSQL("    b.START_DATE, ");
//        sql.addSQL("    b.END_DATE, ");
//        sql.addSQL("    b.EXT_DESC PACKAGE_NAME, ");
//        sql.addSQL("    c.depart_id, ");
//        sql.addSQL("    e.PRODUCT_NAME ");
//        sql.addSQL("  from tf_f_active_stock a, td_b_package_ext b, td_m_staff c, td_b_product_package d, td_b_product e");
//        sql.addSQL(" where a.res_kind_code = b.cond_factor3");
//        sql.addSQL("   AND a.staff_id = c.staff_id");
//        sql.addSQL("   AND b.package_id = d.package_id");
//        sql.addSQL("   AND d.product_id = e.product_id");
//        sql.addSQL("   AND b.cond_factor3 is not null");
//        sql.addSQL("   AND a.WARNNING_VALUE_D > 0");
//        sql.addSQL("   AND d.end_date > sysdate");
//        sql.addSQL("   AND e.product_mode = '02'");
//        sql.addSQL("   AND (a.CITY_CODE = :CITY_CODE OR :CITY_CODE IS NULL)");
//        sql.addSQL("   AND (c.depart_id=:DEPART_ID OR :DEPART_ID IS NULL)");
//        sql.addSQL("   AND (a.STAFF_ID=:STAFF_ID OR :STAFF_ID IS NULL)");
//        sql.addSQL(" AND a.STAFF_ID >=:STAFF_ID_S");
//        sql.addSQL(" AND a.STAFF_ID <= :STAFF_ID_E");

//		IDataset ids = Dao.qryByParse(sql, pagination);

		return result;
	}

	public static IDataset querySparkPlans(String eparchyCode, String staffId, String resKindCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("EPARCHY_CODE", eparchyCode);
		cond.put("STAFF_ID", staffId);
		cond.put("RES_KIND_CODE", resKindCode);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("select (a.WARNNING_VALUE_D - a.WARNNING_VALUE_U) SURPLUS_VALUE, a.* ");
		sql.append("  from tf_f_active_stock a");
		sql.append(" where a.eparchy_code = :EPARCHY_CODE");
		sql.append(" AND a.STAFF_ID =:STAFF_ID");
		sql.append(" AND a.RES_KIND_CODE = :RES_KIND_CODE");

		IDataset ids = Dao.qryBySql(sql, cond);

		return ids;
	}
}
