package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.DiscntPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;

public class DiscntInfoQry {
	/**
	 * 根据producte_id/package_id/discnt_code查询必须优惠信息
	 * 
	 * @param productId
	 *            ，package_id，discnt_code
	 * @return
	 * @throws Exception
	 */
	public static IDataset getForcedDiscntByProdPackDiscnt(String productId, String packageId, String discntCode, String strtype) throws Exception {
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("DISCNT_CODE", discntCode);
		IDataset forceDisList =  UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(strtype, productId,"1", null);
       // UpcCall.qryAtomOffersFromGroupByOfferId(strtype, productId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
		if (IDataUtil.isNotEmpty(forceDisList)) {
			for (int i = 0; i < forceDisList.size(); i++) {
				IData tempforceSvc = forceDisList.getData(i);
				//String s1 = tempforceSvc.getString("ELEMENT_ID","");
				String s1 = tempforceSvc.getString("OFFER_CODE","");
				String pak = tempforceSvc.getString("GROUP_ID","");
				if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(tempforceSvc.getString("OFFER_TYPE",""))
						&&s1.equals(discntCode)&&pak.equals(packageId)){
					//System.out.println("-----------------getForcedDiscntByProdPackDiscnt--------------------tempforceSvc:"+tempforceSvc);
					tempforceSvc.put("DISCNT_CODE", tempforceSvc.getString("ELEMENT_ID",""));
					tempforceSvc.put("DISCNT_NAME", tempforceSvc.getString("ELEMENT_NAME",""));
					tempforceSvc.put("DISCNT_EXPLAIN", tempforceSvc.getString("ELEMENT_EXPLAIN",""));
	    		}
				else {
					forceDisList.remove(i);
					i--;
				}
			}
		}
		return forceDisList;
		// Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PRODPACKDISCNT", param,Route.CONN_CRM_CEN);
	}

	/**
	 * 根据优惠编码，优惠类型，地州查询TD_B_QRY_DISCNT
	 * 
	 * @param disCode
	 * @param typeCode
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntByCodeType(String disCode, String typeCode, String eparchyCode) throws Exception {
		IData param = new DataMap();
		param.put("DISCNT_CODE", disCode);
		param.put("DISCNT_TYPE", typeCode);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TD_B_QRY_DISCNT", "SEL_BY_CODE_TYPE", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据产品编码查询优惠信息
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntByProduct(String productId) throws Exception {
		return UDiscntInfoQry.getDiscntByProduct(productId);
	}

	/**
	 * 根据优惠编码获取优惠描述信息
	 * 
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	public static String getDiscntExplanByDiscntCode(String discntCode) throws Exception {

		return UDiscntInfoQry.getDiscntExplainByDiscntCode(discntCode);
	}

	/**
	 * 根据优惠编码查询优惠信息
	 * 
	 * @param discntCode
	 * @return
	 * @throws Exception
	 *             add by xiaozb 20140401
	 */
	public static IData getDiscntInfoByCode2(String discntCode) throws Exception {
		IData discnt = UDiscntInfoQry.getDiscntInfoByPk(discntCode);

		if (IDataUtil.isEmpty(discnt)) {
			return null;
		}
		return discnt;
	}

	/**
	 * 根据资费编码查询资费信息
	 * 
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntInfoByDisCode(String discntCode) throws Exception {
		IDataset discntInfos = new DatasetList();
		IData discnt = UDiscntInfoQry.getDiscntInfoByPk(discntCode);
		if (IDataUtil.isNotEmpty(discnt))
			;
		discntInfos.add(discnt);

		return discntInfos;
	}

	public static IDataset getDiscntIsValid(String discnt_type_code, String discnt_code) throws Exception {
		IData param = new DataMap();
		param.put("DISCNT_TYPE_CODE", discnt_type_code);
		param.put("DISCNT_CODE", discnt_code);
		IDataset res1 = UDiscntInfoQry.queryDiscntTypeByDiscntCode(discnt_code);
		if (IDataUtil.isNotEmpty(res1)) {
			for (int j = 0; j < res1.size(); j++) {
				if ((discnt_type_code.equals(res1.getData(j).getString("FIELD_VALUE", "")))) {
					res1.remove(j);
					j--;
				}
			}
		} else {
			res1 = new DatasetList();
		}
		return res1;// Dao.qryByCode("TD_B_DTYPE_DISCNT",
					// "SEL_BY_GPRS_DISCNT_CODE", param, Route.CONN_CRM_CEN);

	}

	/**
	 * 作用：getDiscntNowValid
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntNowValid(String userId, String discntCode, Pagination pagination) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);

		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNT_NOW_VALID", param, pagination);
	}

	/**
	 * @Description: 获取优惠类型
	 * @param elementId
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Jun 14, 2014 10:25:43 AM
	 */
	public static String getDiscntTypeByDiscntCode(String elementId) throws Exception {
		String discntType = "";

//		IDataset discntTypes = queryDiscntTypeByDiscntCode(elementId);  
		
		discntType=UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);

/*		if (String.isNotEmpty(discntTypes)) {
			discntType = discntTypes.getData(0).getString("DISCNT_TYPE_CODE").trim();
		}*/

		return discntType;
	}

	public static IDataset getProductMebDiscntByProductId(String productId) throws Exception {
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("select t4.discnt_code,t4.discnt_name,t3.force_tag,t4.discnt_explain,t3.default_tag, ");
		parser.addSQL(" t3.update_staff_id,t3.update_depart_id,t3.update_time ,t3.enable_tag,t4.define_months,t4.months ");
		parser.addSQL(" from td_b_product_meb t1 ,td_b_product_package  t2,td_b_package_element t3,td_b_discnt t4 ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and t1.product_id = :PRODUCT_ID ");
		parser.addSQL(" and t1.product_id_b=t2.product_id ");
		parser.addSQL(" and t2.package_id=t3.package_id ");
		parser.addSQL(" And t3.element_id=t4.discnt_code ");
		parser.addSQL(" and sysdate BETWEEN t4.start_date and t4.end_date ");

		IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
		return dataset;

	}

	public static IDataset getProductMebDiscntByProductIdAndStaffId(String productId, String staffId) throws Exception {
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("TRADE_STAFF_ID", staffId);
		return Dao.qryByCode("TD_B_PROD_DISCNT_MEMBER", "SEL_BY_PID_5", param, Route.CONN_CRM_CEN);

	}

	public static IDataset getUserDiscntInfo(String userId) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_NOW", param);

	}

	/**
	 * 查询一个用户定购某个集团用户的产品的优惠信息
	 * 
	 * @param params
	 *            参数信息
	 * @param pagination
	 * @throws Exception
	 * @author zhouli
	 */
	public static IDataset getUserProductDis(String userId, String userIdA, Pagination pagination) throws Exception {
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("USER_ID_A", userIdA);

		IDataset discntInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_USERIDA", params, pagination);

		if (IDataUtil.isEmpty(discntInfos))
			return discntInfos;

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(discntInfos, params, null);// 填充productId,packageId

		for (int i = 0; i < discntInfos.size(); i++) {
			IData discnt = discntInfos.getData(i);
			String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discnt.getString("DISCNT_CODE"));
			discnt.put("DISCNT_NAME", discntName);
			discnt.put("ELEMENT_NAME", discntName);
		}

		return discntInfos;
	}

	/**
	 * 通过USER_ID、USER_ID_A、PRODUCT_ID、PACKAGE_ID、SERVICE_ID、INST_ID查询用户某条服务
	 * 
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserSingleProductDisForGrp(String userId, String userIdA, String productId, String packgeId, String serviceId, String instId, Pagination pagination) throws Exception {
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("USER_ID_A", userIdA);
		params.put("PRODUCT_ID", productId);
		params.put("PACKAGE_ID", packgeId);
		params.put("DISCNT_CODE", serviceId);
		params.put("INST_ID", instId);
		IDataset discntInfos = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_DISCNT_CODE", params, pagination, Route.CONN_CRM_CG);
		if (IDataUtil.isEmpty(discntInfos))
			return discntInfos;

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(discntInfos, params, null);// 填充productId和packageId

		// 传了productId或者packageId 在过滤下
		if (StringUtils.isNotBlank(productId) && StringUtils.isBlank(packgeId)) {
			discntInfos = DataHelper.filter(discntInfos, "PRODUCT_ID=" + productId);
		} else if (StringUtils.isNotBlank(productId) && StringUtils.isNotBlank(packgeId)) {
			discntInfos = DataHelper.filter(discntInfos, "PRODUCT_ID=" + productId + ",PACKAGE_ID=" + packgeId);
		} else if (StringUtils.isBlank(productId) && StringUtils.isNotBlank(packgeId)) {
			discntInfos = DataHelper.filter(discntInfos, "PACKAGE_ID=" + packgeId);
		}

		for (int i = 0; i < discntInfos.size(); i++) {
			IData discnt = discntInfos.getData(i);
			String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discnt.getString("DISCNT_CODE"));
			discnt.put("DISCNT_NAME", discntName);
		}

		return discntInfos;
	}

	/**
	 * 根据产品编码和包类型查询优惠信息
	 * 
	 * @param productId
	 * @param pkgTypeCode
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryByPidPkgTypeCode(String productId, String pkgTypeCode, String eparchyCode) throws Exception {
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_TYPE_CODE", pkgTypeCode);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TD_B_DISCNT", "SEL_BY_DISCNT_ELEMENT", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据产品编码和rsrv5查询优惠信息
	 * 
	 * @param productId
	 * @param rsrv5
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryDiscntInfoByProdIdRsrv5(String productId, String rsrv5) throws Exception {
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("RSRV_STR5", rsrv5);
		return Dao.qryByCode("TD_B_DISCNT", "SEL_BY_PRODUCT_RSRV5", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 查询折扣信息
	 * 
	 * @author fengsl
	 * @date 2013-02-26
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntCodeByTradeStaffID(String productId, Pagination pagination) throws Exception {
		IData param = new DataMap();
		param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT a.discnt_code, b.discnt_explain discnt_name ");
		parser.addSQL("  FROM td_b_prod_discnt_member A, td_b_discnt B ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("   AND a.product_id = :PRODUCT_ID ");
		parser.addSQL("   AND A.discnt_Code = B.discnt_Code ");
		parser.addSQL("   AND (SYSDATE BETWEEN B.start_date AND B.end_date) ");
		parser.addSQL("   AND (SYSDATE BETWEEN a.start_date AND a.end_date) ");
		parser.addSQL("   AND (b.eparchy_code = :EPARCHY_CODE OR b.eparchy_code = 'ZZZZ') ");
		parser.addSQL(" ORDER BY b.discnt_code ");

		IDataset dataset = Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
		DiscntPrivUtil.filterDiscntListByPriv(CSBizBean.getVisit().getStaffId(), dataset);
		return dataset;
	}

	public static IDataset queryDiscntInfoByName(String discntCode, String discntName, Pagination pagination) throws Exception {
		IData param = new DataMap();
		param.put("DISCNT_CODE", discntCode);
		param.put("DISCNT_NAME", discntName);
		return Dao.qryByCodeParser("TD_B_DISCNT", "SEL_BY_CODE_NAME", param, pagination, Route.CONN_CRM_CEN);
	}

	public static IDataset queryDiscntInfoByNameNoCache(String discntCode, String discntName, Pagination pagination) throws Exception {
		IData param = new DataMap();
		param.put("DISCNT_CODE", discntCode);
		param.put("DISCNT_NAME", discntName);
		return Dao.qryByCodeParser("TD_B_DISCNT", "SEL_BY_CODE_NAME_NO_CACHE", param, pagination, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据DISCNT_TYPE_CODE查询TD_B_DISCNT
	 * 
	 * @param discntTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntInfoByTypeCode(String discntTypeCode) throws Exception {
		IData iparam = new DataMap();

		iparam.put("DISCNT_TYPE_CODE", discntTypeCode);

		IDataset ds = UpcCallIntf.queryPackageElementsByProductIdDisctypeCode("D", "", "C");
		return ds;
		//return Dao.qryByCode("TD_B_DISCNT", "SEL_BY_DISCNT_TYPE_CODE", iparam, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据优惠类型查询TD_B_DTYPE_DISCNT表信息
	 * 
	 * @param discntType
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntsByDtype(String discntType) throws Exception {
		IData param = new DataMap();
		param.put("DISCNT_TYPE_CODE", discntType);
		return Dao.qryByCode("TD_B_DTYPE_DISCNT", "SEL_BY_GPRS_DISCNT_ALL", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据包编码查询优惠信息
	 * 
	 * @param packageId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntsByPkgId(String packageId) throws Exception {
		IData cond = new DataMap();
		cond.put("PACKAGE_ID", packageId);

		return Dao.qryByCode("TD_B_DISCNT", "SEL_BY_PACKID", cond, Route.CONN_CRM_CEN);
	}

	public static IDataset queryDiscntsByPkgIdEparchy(String packageId, String eparchyCode) throws Exception {
		IData cond = new DataMap();
		cond.put("PACKAGE_ID", packageId);
		cond.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TD_B_DISCNT", "SEL_BY_PACKID_EPARCHY", cond, Route.CONN_CRM_CEN);
	}
	/**
	 * 查询用户是否存在不允许办理统一付费关系主副卡的优惠
	 * 
	 * @param userId
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-5-21
	 */
	public static IDataset queryLimitDiscnts(String userId, String roleCodeB) throws Exception {
		IData iparam = new DataMap();
		iparam.put("USER_ID", userId);
		iparam.put("PARAM_CODE", roleCodeB);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_UNIONPAY_LIMIT_DISCNT", iparam);
	}

	public static IDataset queryPresentInfos(String acctId, String presentSerialNumber, String startDate, String endDate, Pagination pagination) throws Exception {
		IData param = new DataMap();
		param.put("ACCT_ID", acctId);
		param.put("PRESENT_SERIAL_NUMBER", presentSerialNumber);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);

		return Dao.qryByCodeParser("TF_F_PRESENT_DISCNT", "SEL_CTT_PRESENT_DISCNT", param, pagination, Route.getCrmDefaultDb());
	}

	/**
	 * @Description：TODO
	 * @param:@param dependDiscnts
	 * @param:@param custId
	 * @param:@return
	 * @return boolean
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-8-8下午08:07:58
	 */
	public static boolean qryHaveSomeDiscnts(String dependDiscnts, String custId) throws Exception {
		boolean result = false;
		IDataset lsUser = UserInfoQry.getUserInfoByCusts(custId);
		mark:
		for(Object temp:lsUser){
			IData tempData = (IData) temp;
			String userId = tempData.getString("USER_ID");
			IDataset discntList = DiscntInfoQry.getUserDiscntInfo(userId);
			for(Object temp2:discntList){
				IData tempData2 = (IData) temp2;
				if(dependDiscnts.contains(tempData2.getString("DISCNT_CODE"))){
					result = true ;
					break mark;
				}
			}
		}
		return result;
	}
	
	 
	public static IDataset qryDiscntsByCompara(IData params)
	    	throws Exception{
	    	IData param = new DataMap();
	    	param.put("USER_ID",params.getString("USER_ID"));
	        param.put("SUBSYS_CODE",params.getString("SUBSYS_CODE"));
	        param.put("PARAM_ATTR", params.getString("PARAM_ATTR"));
	        param.put("PARA_CODE1", params.getString("PARA_CODE1"));
	        param.put("PARAM_CODE", params.getString("PARAM_CODE"));
	        return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_DISCNT_BY_COMPARAM", param, Route.CONN_CRM_CG);
    }
	
	public static IDataset qryDiscntsByCompara2(IData params)
	    	throws Exception{
	    	IData param = new DataMap();
	    	param.put("USER_ID",params.getString("USER_ID"));
	        param.put("SUBSYS_CODE",params.getString("SUBSYS_CODE"));
	        param.put("PARAM_ATTR", params.getString("PARAM_ATTR"));
	        param.put("PARA_CODE1", params.getString("PARA_CODE1"));
	        param.put("PARAM_CODE", params.getString("PARAM_CODE"));
	        return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_DISCNT_BY_COMPARAM2", param, Route.CONN_CRM_CG);
    }

}
