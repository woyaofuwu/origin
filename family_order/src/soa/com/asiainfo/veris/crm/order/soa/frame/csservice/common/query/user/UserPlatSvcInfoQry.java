package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;

public class UserPlatSvcInfoQry
{

	/**
	 * 查询有效的实体卡订购信息
	 * 
	 * @param param
	 * @param isCancel
	 * @return
	 * @throws Exception
	 */
	public static IDataset getEntityCardPlatsvcInfo(IData param) throws Exception
	{

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* ");
		parser.addSQL(" FROM TF_F_USER_PLATSVC A,TF_F_USER_PLATSVC_ENTITYCARD B");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND A.USER_ID = B.USER_ID ");
		parser.addSQL(" AND A.SERVICE_ID = B.SERVICE_ID ");
		parser.addSQL(" AND B.RSRV_DATE1 > SYSDATE ");
		parser.addSQL(" AND A.USER_ID = TO_NUMBER(:USER_ID) ");
		parser.addSQL(" AND A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		parser.addSQL(" AND A.SERVICE_ID = :SERVICE_ID ");
		parser.addSQL(" AND A.BIZ_CODE = :BIZ_CODE ");
		parser.addSQL(" AND A.SP_CODE = :SP_CODE ");
		parser.addSQL(" AND A.BIZ_STATE_CODE <> 'E' AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE ");

		return Dao.qryByParse(parser);
		// return new DatasetList();
	}

	/**
	 * @Function: getGrpPlatSvcByUserId
	 * @Description: 查询用户PLATSVC信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午7:23:14 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-5-3 updata v1.0.0 修改原因
	 */
	public static IDataset getGrpPlatSvcByUserId(String user_id, String product_id) throws Exception
	{
		IDataset result = new DatasetList();

		// 根据集团产品先查出成员产品
		String mebProductId = ProductMebInfoQry.getMemberProductIdByProductId(product_id);
		if (StringUtils.isEmpty(mebProductId))
		{
			return result;
		}
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("OFFER_CODE", mebProductId);
		param.put("OFFER_TYPE", "P");
		param.put("REL_OFFER_TYPE", "Z");
		IDataset platSvcRel = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_UID_OFFERTYPE_CODE_RELOFFERTYPE", param);

		if (IDataUtil.isEmpty(platSvcRel))
			return result;

		IDataset idatas = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_PLATSVC", param);
		if (IDataUtil.isEmpty(idatas))

			for (int i = 0; i < idatas.size(); i++)
			{
				IData idata = idatas.getData(i);
				String platsvc_inst_id = idata.getString("INST_ID");
				String service_id = idata.getString("ELEMENT_ID");

				IDataset platsvcRelTemp = DataHelper.filter(platSvcRel, "REL_OFFER_INS_ID=" + platsvc_inst_id);
				if (IDataUtil.isNotEmpty(platsvcRelTemp))
				{
					IData offerreldata = platsvcRelTemp.getData(0);
					IDataset platnames = PlatSvcInfoQry.queryPlatSvcByServiceId(service_id);

					idata.put("ELEMENT_NAME", platnames.getData(0).getString("SERVICE_NAME"));

					idata.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(mebProductId));
					idata.put("PACKAGE_NAME", UPackageInfoQry.getNameByPackageId(offerreldata.getString("GROUP_ID")));
					result.add(idata);

				}

			}

		return result;

	}

	public static IDataset getPlatSvcAttrByUserIdSId(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		return Dao.qryByCode("TF_F_USER_PLATSVC_ATTR", "SEL_PLATSVC_ATTR_BY_USERID", param);
	}

	/**
	 * @Function: getPlatSvcByUserBizType
	 * @Description: 查询用户PLATSVC信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午7:35:09 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-5-3 updata v1.0.0 修改原因
	 */
	public static IDataset getPlatSvcByUserBizType(String user_id, String biz_type_code) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", user_id);
		// modify by lijun17 2017-3-14 param.put("BIZ_TYPE_CODE",
		// biz_type_code);
		// IDataset idata = Dao.qryByCode("TF_F_USER_PLATSVC",
		// "SEL_BY_USER_BIZTYPE", param);
		IDataset platSvcInfos = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BIZTYPE_BY_USERID", param);
		IDataset result = new DatasetList();
		if (IDataUtil.isNotEmpty(platSvcInfos))
		{
			for (int i = 0; i < platSvcInfos.size(); i++)
			{
				IData platSvcInfo = platSvcInfos.getData(i);
				String serviceId = platSvcInfo.getString("SERVICE_ID", "");
				if (StringUtils.isNotBlank(serviceId))
				{
					IDataset platInfos = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, biz_type_code, null);
					if (IDataUtil.isNotEmpty(platInfos))
					{
						platSvcInfo.put("BIZ_TYPE_CODE", platInfos.getData(0).getString("BIZ_TYPE_CODE", ""));
						platSvcInfo.put("BIZ_CODE", platInfos.getData(0).getString("BIZ_CODE", ""));
						platSvcInfo.put("SP_CODE", platInfos.getData(0).getString("SP_CODE", ""));
						result.add(platSvcInfo);
					}
				}
			}
		}
		return result;
	}
	//REQ202003050012_关于开发融合套餐增加魔百和业务优惠体验权益的需求
	public static IDataset getInvalidPlatSvcByUserBizType(String user_id, String biz_type_code) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", user_id);
		IDataset platSvcInfos = Dao.qryByCodeParser("TF_F_USER_PLATSVC", "SEL_BIZTYPE_BY_USERID2", param);
		IDataset result = new DatasetList();
		if (IDataUtil.isNotEmpty(platSvcInfos))
		{
			for (int i = 0; i < platSvcInfos.size(); i++)
			{
				IData platSvcInfo = platSvcInfos.getData(i);
				String serviceId = platSvcInfo.getString("SERVICE_ID", "");
				String startDate = platSvcInfo.getString("START_DATE", "");
				if (StringUtils.isNotBlank(serviceId))
				{
					IDataset platInfos = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, biz_type_code, null);
					if (IDataUtil.isNotEmpty(platInfos))
					{
						platSvcInfo.put("BIZ_TYPE_CODE", platInfos.getData(0).getString("BIZ_TYPE_CODE", ""));
						platSvcInfo.put("BIZ_CODE", platInfos.getData(0).getString("BIZ_CODE", ""));
						platSvcInfo.put("SP_CODE", platInfos.getData(0).getString("SP_CODE", ""));
						platSvcInfo.put("START_DATE", startDate);
						result.add(platSvcInfo);
					}
				}
			}
		}
		return result;
	}

	public static IDataset getPlatSvcByUserId(String userId, String bizType) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "2689");
		param.put("PARAM_CODE", bizType);
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_SERVICE_ID_FOR_WAP", param);

		for (int i = 0; i < userPlatSvcs.size(); i++)
		{
			IData userPlatSvc = userPlatSvcs.getData(i);
			String serviceId = userPlatSvc.getString("SERVICE_ID");
			IDataset datas = new DatasetList();
			try
			{
				datas = UpcCall.querySpServiceAndProdByCond(null, null, null, serviceId);
			} catch (Exception e)
			{

			}
			for (int j = 0; j < datas.size(); j++)
			{
				IData data = datas.getData(j);

				userPlatSvc.put("SP_CODE", data.getString("SP_CODE"));
				userPlatSvc.put("BIZ_CODE", data.getString("BIZ_CODE"));
				userPlatSvc.put("BIZ_TYPE_CODE", data.getString("BIZ_TYPE_CODE"));
			}
		}
		return userPlatSvcs;
	}

	public static IDataset getPlatSvcByUserIdSN(String userId, String sn) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERIAL_NUMBER", sn);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USERPLATSVCINFO_BY_SERVICEID", param);
	}

	public static IDataset getUserDiscntInfo(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "2690");
		param.put("PARAM_CODE", serviceId);
		param.put("INFO_CODE", "401");
		return Dao.qryByCode("TF_F_USER_PLATSVC_ATTR", "SEL_BY_USER_ID_SERVICE_ID_FOR_WAP", param);
	}

	/**
	 * 根据手机号码查询最近开户的纪录
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserInfoBySNOpenDate(IData param) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_SNOPENDATE", param);
	}

	/**
	 * 获取用户所有end_date>sysdate的平台服务
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserPlatSvc(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATSVC_BY_USERID", param);
	}

	/**
	 * 获取用户所有end_date>sysdate的平台服务
	 * 
	 * @author hefeng
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserPlatSvc(String userId, String productId, String brandCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("BRAND_CODE", brandCode);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN_TEST_1", param);
	}

	/**
	 * @author luoz
	 * @date 2013-08-20
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserPlatSvc(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USERPLATSVCINFO_BY_SN", param);
	}

	public static IDataset getUserPlatSvc(String userId, String productId, String brandCode, String eparchyCode, String rsrv_str9) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("BRAND_CODE", brandCode);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("RSRV_STR9", rsrv_str9);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN_TEST", param);

	}

	/**
	 * 根据tab_name,sql_ref,eparchy_code查询用户平台服务信息
	 */
	public static IDataset getUserPlatSvcAttr(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_PLATSVC_ATTR", "SEL_BY_PLATSVC_ATTR", inparams);
	}

	public static IDataset getUserPlatSvcAttrInfoByUserId(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC_ATTR", "SEL_USER_PLAT_SVC_ATTR", param, eparchyCode);
	}

	/**
	 * 按生效时间范围查询用户平台业务订购关系
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserPlatSvcById(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_PLATSVC", inparams);
	}

	/**
	 * 查询用户某个平台服务的首次订购时间
	 * 
	 * @param pd
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserPlatSvcFirstDate(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PKINFO_ONE", param);
	}

	/**
	 * 查询用户某个平台服务的本月首次订购时间
	 * 
	 * @param pd
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserPlatSvcFirstDateMon(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PKMONINFO_ONE", param);
	}

	/**
	 * 根据tab_name,sql_ref,eparchy_code查询用户平台服务信息
	 */
	public static IDataset getUserPlatSvcParser(IData inparams) throws Exception
	{

		SQLParser parser = new SQLParser(inparams);
		parser.addSQL(" SELECT 1 FROM TF_F_USER_PLATSVC A ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
		parser.addSQL(" AND A.USER_ID = :USER_ID ");
		parser.addSQL(" AND A.BIZ_STATE_CODE <> 'E' ");
		parser.addSQL(" AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE ");
		String start = " AND A.SERVICE_ID IN (";

		String serviceIdSet = inparams.getString("SERVICEID_SET");
		String[] serviceIdSets = serviceIdSet.split(",");
		for (int i = 0; i < serviceIdSets.length; i++)
		{
			if (serviceIdSets[i] != null && serviceIdSets[i].length() > 0)
			{
				if (i == 0)
				{
					start = start + serviceIdSets[i];
				} else
				{
					start = start + ", " + serviceIdSets[i];
				}
			}
		}
		parser.addSQL(start + ") ");

		return Dao.qryByParse(parser);
	}

	/**
	 * 查询用户有效的平台服务
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserValidPlatSvc(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATSVC_BY_USERID", param);
	}

	/**
	 * 获取无线音乐会员的会员级别
	 * 
	 * @param pd
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserWirelessMusicMemberLevel(String userId, String serviceId, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		return Dao.qryByCode("TF_F_USER_PLATSVC_ATTR", "SEL_USER_MEMBER_LEVEL", param, page);
	}

	/**
	 * 查询用户某个WAP平台业务订购关系
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getWapUserInfo(IData param) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USER_WAP", param);
	}

	/**
	 * 取用户的服务
	 * 
	 * @param userId
	 * @param bizTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset idsSvc(String userId, String bizTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("BIZ_TYPE_CODE", bizTypeCode);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_BIZTYPEBIZ54", param);
	}

	/**
	 * 查询用户的某服务是否在某段时期内首次订购
	 * 
	 * @param userId
	 * @param serviceId
	 * @param timeType
	 * @param timeValue
	 * @return
	 * @throws Exception
	 */
	public static boolean isFirstPeriod(String userId, String serviceId, String timeType, String timeValue, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		param.put("TIME_TYPE", timeType);
		param.put("TIME_VALUE", timeValue);
		IDataset result = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_ISPERIODFIRST", param, page);
		if (result != null && result.size() > 0)
		{
			if (Integer.parseInt(result.getData(0).getString("IS_FIRST")) > 0)
			{
				return false;
			} else
			{
				return true;
			}
		} else
		{
			return true;
		}
	}
	
	public static boolean isOrderIn3Months(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		IDataset result = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_ISIN3MONTH", param);
		if (result != null && result.size() > 0)
		{
			if (Integer.parseInt(result.getData(0).getString("IS_IN3MONTH")) > 0)
			{
				return true;
			} 
			else
			{
				return false;
			}
		} 
		else
		{
			return false;
		}
	}

	public static IDataset qryPlatOrderInfoBySn(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN22_NEW", param);

	}

	public static IDataset qryPlatSvcByUserIdServiceId(String userId, String sp_service_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SP_SERVICE_ID", sp_service_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* FROM TF_F_USER_PLATSVC A ");
		parser.addSQL(" WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		parser.addSQL(" and A.user_id = :USER_ID ");
		parser.addSQL("  AND (A.SERVICE_ID = :SP_SERVICE_ID) ");
		parser.addSQL(" AND (A.BIZ_STATE_CODE = 'A' OR A.BIZ_STATE_CODE = 'N' OR A.BIZ_STATE_CODE = 'L') ");
		parser.addSQL(" AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE ");
		return Dao.qryByParse(parser);

	}
	
	/**
	 * 是否和留言呼转服务
	 * @author: wuwangfeng
	 * @date: 2020/4/20 20:18
	 */
	public static String isAndMessageCallChangeSVC(String userId, String serviceId) throws Exception
	{	
		String flag = "0";
		IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "5203");
        param.put("PARAM_CODE", "120");
        param.put("EPARCHY_CODE", "0898");
        IDataset commparaSet = Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_ALL_COL", param, Route.CONN_CRM_CEN);
        IData commpara = commparaSet.getData(0);
        String[] paramName = commpara.getString("PARAM_NAME").split("\\|");
        String[] paraCode1 = commpara.getString("PARA_CODE1").split("\\|");
        for (int i = 0; i < paramName.length; i++) {       	
        	IDataset userList = UserPlatSvcInfoQry.qryPlatSvcByUserIdServiceId(userId, paramName[i]);
        	if(IDataUtil.isNotEmpty(userList) && userList.size()>0){
        		for (int j = 0; j < paraCode1.length; j++) {
        			if (paraCode1[j].equals(serviceId)) {
        				return flag = "1";
        			}
				}
        	}
		}
        
        return flag;
	}

	/**
	 * @Description: 最近MONTH_VALUE是否办理过平台业务
	 * @param userId
	 * @param serviceId
	 * @param monthValue
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Sep 2, 2014 3:51:54 PM
	 */
	public static IDataset qryUserPlatByUserServiceIdMonth(String userId, String serviceId, String monthValue) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		param.put("MONTH_VALUE", monthValue);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USER_SERVICE_ID_MONTH", param);
	}
	/**
	 * @Description: 最近N个月内是否办理过平台业务
	 */
	public static IDataset qryUserPlatByUserServiceIdMonthNew(String userId, String serviceId, String monthValue) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		param.put("MONTH_VALUE", monthValue);
		//return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USER_SERVICE_ID_MONTH", param);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* FROM TF_F_USER_PLATSVC A ");
		parser.addSQL(" WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		parser.addSQL(" AND A.user_id = :USER_ID ");
		parser.addSQL(" AND (A.SERVICE_ID = :SERVICE_ID) ");
		parser.addSQL(" and add_months((a.end_date),to_number(:MONTH_VALUE)) > sysdate  ");
		return Dao.qryByParse(parser);
	}

	public static IDataset qryUserPlatSvcByUserIdAndServiceId(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);

		return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_USERID_SVCID", param, Route.CONN_CRM_CG);

	}

	/**
	 * 根据条件查询用户所有的订购关系
	 * 
	 * @param pd
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUserPlatSvcs(String userId, String bizTypeCode, String operCode, String spCode, String bizCode, String bizStateCode) throws Exception
	{
		IData param = new DataMap();
		/*
		 * param.put("USER_ID", userId); param.put("BIZ_TYPE_CODE",
		 * bizTypeCode); param.put("SP_CODE", spCode); param.put("BIZ_CODE",
		 * bizCode); param.put("OPER_CODE", operCode);
		 * 
		 * SQLParser parser = new SQLParser(param);parser.addSQL(
		 * " select c.*,z.biz_name,p.service_name,z.bill_type,z.price,m.sp_name,p.sp_code,p.biz_code,p.biz_type_code,"
		 * +
		 * " z.serv_mode from td_b_platsvc p,td_m_sp_biz z,td_m_sp_info m,tf_f_user_platsvc_trace c  "
		 * ); parser.addSQL(" where c.partition_id = mod(:USER_ID,10000) ");
		 * parser.addSQL(" and c.user_id = :USER_ID ");
		 * parser.addSQL(" and c.service_id = p.service_id ");
		 * parser.addSQL(" and p.sp_code = z.sp_code ");
		 * parser.addSQL(" and p.biz_code = z.biz_code ");
		 * parser.addSQL(" and p.sp_code = m.sp_code ");
		 * parser.addSQL(" and p.biz_type_code = :BIZ_TYPE_CODE ");
		 * parser.addSQL(" and p.sp_code = :SP_CODE ");
		 * parser.addSQL(" and p.biz_code = :BIZ_CODE ");
		 * parser.addSQL(" and c.oper_code = :OPER_CODE "); if
		 * ("1".equals(bizStateCode)) { // 查正常的
		 * parser.addSQL(" and c.biz_state_code in ('A','N','L')"); } else if
		 * ("2".equals(bizStateCode)) { // 查退订和预退订的
		 * parser.addSQL(" and c.biz_state_code in ('E','P')"); }
		 * parser.addSQL(" and sysdate between c.start_date and c.end_date ");
		 * 
		 * return Dao.qryByParse(parser, pagination);
		 */

		param.put("USER_ID", userId);
		param.put("OPER_CODE", operCode);

		String sqlRef = "SEL_BY_USER_ID_2";
		if ("1".equals(bizStateCode))
		{
			// 查正常的
			sqlRef = "SEL_BY_USER_ID";
		} else if ("2".equals(bizStateCode))
		{
			// 查退订和预退订的
			sqlRef = "SEL_BY_USER_ID_1";
		}

		return Dao.qryByCodeParser("TF_F_USER_PLATSVC_TRACE", sqlRef, param);

	}

	/**
	 * 查询用户的当月139邮箱的订购记录
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset query139MailOrderRecord(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_139MAIL_MONTH_ORDER", param);
	}

	public static IDataset query139MailSvc(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_139MAILSVC", param);
		IDataset resultList = new DatasetList();
		if (ArrayUtil.isNotEmpty(userPlatSvcs))
		{
			for (int i = 0; i < userPlatSvcs.size(); i++)
			{
				IData userPlatSvc = userPlatSvcs.getData(i);
				String serviceId = userPlatSvc.getString("SERVICE_ID");
				IDataset tempList = new DatasetList();
				tempList.addAll(UpcCall.querySpServiceAndProdByCond("920931", "+MAILMF", null, serviceId));

				tempList.addAll(UpcCall.querySpServiceAndProdByCond("920931", "+MAILBZ", null, serviceId));

				tempList.addAll(UpcCall.querySpServiceAndProdByCond("920931", "+MAILVIP", null, serviceId));

				if (ArrayUtil.isNotEmpty(tempList))
				{
					IData temp = new DataMap();

					temp.put("USER_ID", userId);
					temp.put("SERVICE_ID", serviceId);
					temp.put("START_DATE", userPlatSvc.getString("START_DATE"));
					temp.put("END_DATE", userPlatSvc.getString("END_DATE"));
					temp.put("BIZ_STATE_CODE", userPlatSvc.getString("BIZ_STATE_CODE"));
					temp.put("BIZ_CODE", tempList.getData(0).getString("BIZ_CODE"));

					resultList.add(temp);
				}
			}
		}
		return resultList;

	}

	public static IDataset queryValid139MailSvc(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_VALID139MAILSVC", param);
		return userPlatSvcs;

	}

	public static IDataset queryAllPlatSvcInfoByUserId(String productId, String userId, String rsrvStr9, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		// param.put("SERV_MODE", sTagChar);// 0,全部；1,自有业务，2,梦网业务
		param.put("RSRV_STR9", rsrvStr9);// 0,全部；1,自有业务，2,梦网业务

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN06_NEW", param);
	}

	public static IDataset queryByUseridBiztype(String userId, String biztype, String state) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		/*
		 * param.put("BIZ_TYPE_CODE", biztype); param.put("BIZ_STATE_CODE",
		 * state);
		 */
		IDataset resultList = new DatasetList();
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_BIZTYPE", param);
		for (int i = 0; i < userPlatSvcs.size(); i++)
		{
			IData userPlatSvc = userPlatSvcs.getData(i);
			String serviceId = userPlatSvc.getString("SERVICE_ID");
			IDataset upcDatas = new DatasetList();
			try
			{
				upcDatas = UpcCall.querySpServiceAndProdByCond(null, null, biztype, serviceId);
			} catch (Exception e)
			{

			}
			if (ArrayUtil.isNotEmpty(upcDatas))
			{
				resultList.add(userPlatSvc);
			}
		}
		return resultList;
	}

	public static IDataset queryCheckCancelPlatSvcs(String userId, String serviceId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("PARAM_ATTR", "3715");
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_RELA_PLATSVC_BYDEL", param);
	}

	/**
	 * 查询3g上网本的状态
	 * 
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryE3GState(IData inParam) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_PLATSVC_E3G", inParam);
	}

	/**
	 * 查询飞信到期的大包月业务
	 * 
	 * @param userId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryFetionDuePlatSvc(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		StringBuilder sql = new StringBuilder("SELECT SERVICE_ID,USER_ID,BIZ_STATE_CODE,");
		sql.append(" TO_CHAR(FIRST_DATE,'YYYY-MM-DD HH24:MI:SS') FIRST_DATE,TO_CHAR(FIRST_DATE_MON,'YYYY-MM-DD HH24:MI:SS') FIRST_DATE_MON,");
		sql.append(" TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE+1/24/3600,'YYYY-MM-DD HH24:MI:SS') END_DATE");
		sql.append(" FROM TF_F_USER_PLATSVC WHERE  SERVICE_ID IN(40153395,40153396,40153397) AND USER_ID = to_number(:USER_ID) AND PARTITION_ID = mod(to_number(:USER_ID),10000) AND END_DATE BETWEEN SYSDATE AND SYSDATE + 7");
		return Dao.qryBySql(sql, param, eparchyCode);
	}

	public static IDataset queryHisPlatSvcInfoByUserId(String productId, String userId, String rsrvStr9, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		// param.put("SERV_MODE", sTagChar);// 0,全部；1,自有业务，2,梦网业务
		param.put("RSRV_STR9", rsrvStr9);// 0,全部；1,自有业务，2,梦网业务

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN05_NEW", param);
	}

	public static IDataset queryInCardNo(String inCardNo, String orgDomain) throws Exception
	{
		IData param = new DataMap();
		param.put("IN_CARD_NO", inCardNo);
		// param.put("ORG_DOMAIN", orgDomain);
		// return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_IN_CARD_NO",
		// param);
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_IN_CARD_NO", param);
		for (int i = 0; i < userPlatSvcs.size(); i++)
		{
			// and a.ORG_DOMAIN = :ORG_DOMAIN
			IData userPlatSvc = userPlatSvcs.getData(i);
			String serviceId = userPlatSvc.getString("SERVICE_ID");
			IDataset upcDatas = new DatasetList();
			try
			{
				upcDatas = UpcCall.querySpServiceAndProdByCond(null, null, null, serviceId);
			} catch (Exception e)
			{

			}
			for (int j = 0; j < upcDatas.size(); j++)
			{
				IData upcData = upcDatas.getData(j);
				if (!StringUtils.equals(orgDomain, upcData.getString("ORG_DOMAIN")))
				{
					userPlatSvcs.remove(i);
					i--;
				}
			}
		}
		return userPlatSvcs;
	}

	public static IDataset queryMembSubInfo(String userId, String bizTypeCode) throws Exception
	{
		// 处理输入参数
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("BIZ_TYPE_CODE", bizTypeCode);
		IDataset queryResult = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_BIZTYPE_NEW", params);// "SEL_BY_USER_BIZTYPE",
		// params )
		// ;2013-06-17
		if (queryResult == null || queryResult.size() == 0)
		{
			return null;
		}
		if ("E".equals(queryResult.get(0, "BIZ_STATE_CODE").toString().trim().toUpperCase()))
		{
			return null;
		}
		return queryResult;
	}

	public static IDataset queryNormalPlatSvcInfoByUserId(String productId, String userId, String rsrvStr9, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		// param.put("SERV_MODE", sTagChar);// 0,全部；1,自有业务，2,梦网业务
		param.put("RSRV_STR9", rsrvStr9);// 0,全部；1,自有业务，2,梦网业务

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN04_NEW", param);
	}

	public static IDataset queryPlatInfoBySn07(String userId, String tag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RSRV_STR9", tag);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN07_NEW", param);
	}

	public static IDataset queryPlatorderAllinfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN03", param);
	}

	public static IDataset queryPlatorderHisinfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN02", param);
	}

	public static IDataset queryPlatorderinfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN01", param);
	}


	
	/**
	 * SEL_USER_PLATSVC_BY_USERID查询TF_F_USER_PLATSVC信息
	 * SEL_USER_PLATSVC_BY_USERID 的语句 中的 TD_B_PLATSVC 表拆分出来
	 * @param userId
	 * @param paramAttr
	 * @param paramCode
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPlatSvcByCommpara_1(String userId, String paramAttr, String paramCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", paramAttr);
		param.put("PARAM_CODE", paramCode);
		param.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USER_PLATSVC_BY_USERID_1", param);
	}
	
	/**
	 * SEL_USER_PLATSVC_BY_USERID查询TF_F_USER_PLATSVC信息
	 * SEL_USER_PLATSVC_BY_USERID 的语句 中的 TD_B_PLATSVC 表拆分出来
	 * @param userId
	 * @param paramAttr
	 * @param paramCode
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPlatSvcByCommpara_2(String userId, String paramAttr, String paramCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", paramAttr);
		param.put("PARAM_CODE", paramCode);
		param.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USER_PLATSVC_BY_USERID_2", param);
	}
	
	public static IDataset queryPlatSvcByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID", param);
	}
	
	public static IDataset queryPlatSvcByUserId1(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset plats = new DatasetList();
        IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID3", param);
        for(Object result:dataset){          
            String service_id = ((IData) result).getString("SERVICE_ID");
            String offerType = "Z";
            String bizStateCode = "A";
            IDataset results = UpcCall.querySpServiceByIdAndBizStateCode(service_id, offerType, bizStateCode);
            for(Object plat:results){
                IData pl = (IData)plat;
                pl.putAll((IData)result);
                plats.add(pl);
            }
        }

        return plats;
    }

	public static IDataset queryPlatSvcByUserIdNew(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BIZTYPE_BY_USERID", param);
	}
	
	
	public static IDataset queryPlatSvcByUpdateTime(String userId,String startTime,String endTime) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("START_TIME", startTime);
		param.put("END_TIME", endTime);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_UPDATE_TIME", param);
	}
	
	
	public static IDataset queryTradePlatSvcByInstId(String userId,String instId,String serviceId,String bizStateCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("INST_ID", instId);
		param.put("SERVICE_ID", serviceId);
		param.put("BIZ_STATE_CODE", bizStateCode);

		return Dao.qryByCode("TF_B_TRADE_PLATSVC", "SEL_BY_INST_ID", param);
	}

	public static IDataset queryPlatSvcByUserId(String userId, String productId, String tagChar, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("RSRV_STR9", tagChar);// 0,全部；1,自有业务，2,梦网业务

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN14", param);
	}

	/**
	 * add by ouyk
	 * 
	 * @param userId
	 * @param spCode
	 * @param bizCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPlatsvcCountByUserIdSpCodeBizCode(String userId, String spCode, String bizCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SP_CODE", spCode);
		param.put("BIZ_CODE", bizCode);
		IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_SPCODE_BIZCODE_NEW", param);
		IDataset upcList = new DatasetList();
		IData temp = new DataMap();
		String serviceId = "";
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				temp = dataset.getData(i);
				serviceId = temp.getString("SERVICE_ID");
				upcList = UpcCall.querySpServiceAndProdByCond(spCode, bizCode, "", serviceId);
				if (IDataUtil.isEmpty(upcList))
				{
					dataset.remove(i);
					i--;
				}
			}
		}
		IDataset countData = new DatasetList();
		IData count = new DataMap();
		count.put("COUNT(1)", dataset.size());
		countData.add(count);
		return countData;
	}

	public static IDataset queryPlatSvcCounts(String USER_ID, String SP_CODE, String SERVICE_CODE) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", USER_ID);
		cond.put("SP_CODE", SP_CODE);
		cond.put("SERVICE_CODE", SERVICE_CODE);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_MFIRST_BY_SPBIZCODE", cond);
	}

	/**
	 * 查询原用户是否订购手机支付业务
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPlatSvcInfo(IData inparams) throws Exception
	{

		// 处理输入参数
		// IData inparams = new DataMap();

		inparams.put("USER_ID", "-1");
		// inparams.put("BIZ_TYPE_CODE", params.getString("BIZ_CODE_TYPE"));
		IDataset queryResult = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_BIZTYPE", inparams);
		IDataset result = new DatasetList();
		if (queryResult != null && queryResult.size() > 0)
		{
			result = queryResult;
		} else
			result = new DatasetList();
		return result;
	}

	/**
	 * 判断用户是否开通某个平台服务
	 * 
	 * @param userId
	 * @param bizTypeCode
	 * @return
	 * @throws Exception
	 * 
	 *             本次改造,TD_B_PLATSVC没有了，调用产商品接口;原sql分为俩步查询 duhj 2017/03/07
	 *             modify by duhj 2017/03/07
	 */
	public static IDataset queryPlatSvcInfo(String userId, String bizTypeCode) throws Exception
	{
		IData param = new DataMap();
		IDataset results = new DatasetList();
		param.put("USER_ID", userId);
		// 原sql SEL_BY_USER_BIZCODE
		IDataset platInfo = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_BIZCODE_NEW", param);// 第一步

		if (IDataUtil.isNotEmpty(platInfo))
		{
			for (int i = 0; i < platInfo.size(); i++)
			{
				IData temp = platInfo.getData(i);
				String serviceId = temp.getString("SERVICE_ID");// 第二步

				// 原sql条件 B.BIZ_STATE_CODE IN ('A',
				// 'Z'),在此分俩次查询,为了使下面方法queryPlatSvc多用性,后续参数值没写死
				IDataset res1 = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, bizTypeCode, "A");
				IDataset res2 = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, bizTypeCode, "N");
				IDataset upcCallList = new DatasetList();

				upcCallList.addAll(res1);
				upcCallList.addAll(res2);

				if (IDataUtil.isEmpty(upcCallList))
				{
					platInfo.remove(i);
					i--;
				} else
				{
					temp.put("BIZ_CODE", upcCallList.getData(0).getString("BIZ_CODE"));
					temp.put("BIZ_TYPE_CODE", upcCallList.getData(0).getString("BIZ_TYPE_CODE"));
					temp.put("SP_CODE", upcCallList.getData(0).getString("SP_CODE"));
					temp.put("ORG_DOMAIN", upcCallList.getData(0).getString("ORG_DOMAIN"));

				}
			}
		}

		return platInfo;
	}

	/**
	 * 查询原用户是否订购手机支付业务
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPlatSvcInfoByUserId(String userId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);

		IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_BIZTYPE", inparam);

		if (IDataUtil.isEmpty(dataset))
		{
			dataset = new DatasetList();
		}

		return dataset;
	}

	public static IDataset queryPlatSvcStateByUserIdAndSvcList(String userId, StringBuilder svcList) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TF_F_USER_PLATSVC_TRACE WHERE USER_ID = :USER_ID AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		sql.append("AND SERVICE_ID IN (").append(svcList).append(")");

		return Dao.qryBySql(sql, param);
	}

	/**
	 * 根据SEL_SYSERVICE_SUBTHREE_2查询信息
	 * 
	 * @param userId
	 * @param spCode
	 * @param bizCode
	 * @param operCode
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPlatSvcSubThree(String userId, String spCode, String bizCode, String operCode, String month) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SP_CODE", spCode);
		param.put("BIZ_CODE", bizCode);
		param.put("OPER_CODE", operCode);
		param.put("MONTH", month);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_SYSERVICE_SUBTHREE_2", param);
	}

	/**
	 * 查询预约服务关系
	 * 
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPreServiceRelation(IData inParam) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PRESERV_RELATION", inParam);
	}

	/**
	 * 查询要删除的平台服务的关联服务
	 * 
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryRelaPlatSvcByDel(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("PARAM_ATTR", "3715");
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		// return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_RELA_PLATSVC_BYDEL",
		// param);
		IDataset resultList = new DatasetList();
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_RELA_PLATSVC_BYDEL_1", param);
		if (ArrayUtil.isNotEmpty(userPlatSvcs))
		{
			for (int i = 0; i < userPlatSvcs.size(); i++)
			{
				IData userPlatSvc = userPlatSvcs.getData(i);
				userPlatSvc.put("SERVICE_NAME_L", UpcCall.queryPlatSvc(userPlatSvc.getString("SERVICE_ID_L"), BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null).getData(0).getString("BIZ_NAME"));
				resultList.add(userPlatSvc);
			}
		}

		param.clear();
		param.put("USER_ID", userId);
		IDataset userPlatSvcs2 = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_RELA_PLATSVC_BYDEL_2", param);
		if (ArrayUtil.isNotEmpty(userPlatSvcs2))
		{
			for (int i = 0; i < userPlatSvcs2.size(); i++)
			{
				IData userPlatSvc = userPlatSvcs2.getData(i);
				String service_id = userPlatSvc.getString("SERVICE_ID");

				IDataset upcDatas = UpcCall.queryPlatSvc(service_id, BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null);
				if (ArrayUtil.isNotEmpty(upcDatas))
				{
					String bizTypeCode = upcDatas.getData(0).getString("BIZ_TYPE_CODE");
					// B.PARA_CODE1 = C.Biz_Type_Code
					IDataset comminfos = ParamInfoQry.queryComminfoByParamCodeParaCode1And2("3715", serviceId, "CSM", bizTypeCode, BofConst.ELEMENT_TYPE_CODE_PRODUCT, CSBizBean.getUserEparchyCode());
					if (ArrayUtil.isNotEmpty(comminfos))
					{
						IData temp = new DataMap();
						temp.put("SVC_TYPE", BofConst.ELEMENT_TYPE_CODE_PLATSVC);
						temp.put("SERVICE_ID_L", service_id);
						temp.put("SERVICE_NAME_L", upcDatas.getData(0).getString("BIZ_NAME"));
						resultList.add(temp);
					}

				}
			}
		}

		param.clear();
		param.put("PARAM_ATTR", "3715");
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		IDataset userPlatSvcs3 = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_RELA_PLATSVC_BYDEL_3", param);
		if (ArrayUtil.isNotEmpty(userPlatSvcs3))
		{
			for (int i = 0; i < userPlatSvcs3.size(); i++)
			{
				IData userPlatSvc = userPlatSvcs3.getData(i);
				userPlatSvc.put("SERVICE_NAME_L", OfferCfg.getInstance(userPlatSvc.getString("SERVICE_ID_L"), BofConst.ELEMENT_TYPE_CODE_SVC).getOfferName());
				resultList.add(userPlatSvc);
			}
		}

		return resultList;
	}

	/**
	 * 查询校园季前营销要到期的服务信息
	 * 
	 * @param serviceId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySchoolSaleDuePlatSvc(String serviceId, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERVICE_ID", serviceId);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_SERVICEID_USERID", param);
	}

	public static IDataset querySvcByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_SVC_BY_USERID", param);
	}

	public static IDataset querySvcByUserIdStr9(String userId, String tagChar) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RSRV_STR9", tagChar);
    	IDataset userPlatSvcSpInfo =new DatasetList();
    	 IDataset userPlatSvcInfos=Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_SVC_BY_USERID_STR9", param);
         IDataset userSpInfos=UPlatSvcInfoQry.qryServInfoByrsrvStr("1");
         for(int j=0; j<userPlatSvcInfos.size();j++){
  	       IData temp = userPlatSvcInfos.getData(j);
  	      for(int k=0; k<userSpInfos.size();k++){
  	    	     if(temp.getString("SERVICE_ID").equals(userSpInfos.getData(k).getString("SERVICE_ID"))){
  	    	    	temp.putAll(userSpInfos.getData(k));
  	    	    	userPlatSvcSpInfo.add(temp);
  	    	     }
  	      }  
     }
        return userPlatSvcInfos; 
    //.qryByCode("TF_F_USER_PLATSVC", "SEL_SVC_BY_USERID_STR9", param);
	}
	
	/**
	 * BUG20190226160500产品变更短信提醒内容存在bug,产品内容有null值展示在短信里。具体见附件，请优化。wangsc10-20190228-根据SERVICE_ID查
	 * 
	 * @param serviceId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySvcByUserIdServiceId(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
    	IDataset userPlatSvcSpInfo =new DatasetList();
    	IDataset userPlatSvcInfos=Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_SVC_BY_USERID_STR9", param);
        for(int j=0; j<userPlatSvcInfos.size();j++){
  	     IData temp = userPlatSvcInfos.getData(j);
  	     IDataset userSpInfos=UPlatSvcInfoQry.qryServInfoByServiceId(temp.getString("SERVICE_ID"));
	  	   for(int k=0; k<userSpInfos.size();k++){
		    	temp.putAll(userSpInfos.getData(k));
		    	userPlatSvcSpInfo.add(temp);
	  	   }  
  	      
       }
        return userPlatSvcInfos; 
    //.qryByCode("TF_F_USER_PLATSVC", "SEL_SVC_BY_USERID_STR9", param);
	}


	public static IDataset querySvcByUserIdTag3(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return new DatasetList();//Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_SVC_BY_USERID_TAG3", param);
	}

	public static IDataset querySvcInfoByUserIdAndSvcId(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USERSWITCHINFO_BY_SN", param);
	}

	public static IDataset querySvcInfoByUserIdAndSvcIdPf(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USERSWITCHINFO_BY_SN_PF", param);
	}

	public static IDataset queryUiaBySerialNumber(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_UIA_INFO", param);
	}

	public static IDataset queryUiaVipInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_UIA_VIP_INFO", param);
	}

	public static IDataset queryUserAttrByUserIdForWap(String userId, String attrCode, String serviceId) throws Exception
	{
		IData pparam = new DataMap();
		pparam.put("USER_ID", userId);
		pparam.put("SUBSYS_CODE", "CSM");
		pparam.put("PARAM_ATTR", "3700");
		pparam.put("INFO_VALUE", attrCode);
		pparam.put("PARAM_CODE", serviceId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_ATTR_BY_USER_ID_FOR_WAP", pparam);
	}

	public static IDataset queryUserColorRingOrderByTC(String userId) throws Exception
	{
		IData param = new DataMap();
		// 如果该用户有通过套餐订购的铃音盒，退订无线音乐平台时，不退订铃音盒，否则退订
		param.put("USER_ID", userId);
		IDataset resultList = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_SVC20_TC", param);
		return resultList;
	}

	/**
	 * 获得平台业务属性，
	 * 
	 * @param serialNumber
	 * @param infoCode
	 *            ==302
	 * @param serviceId
	 *            ==98001901
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserPlatAttr(String serialNumber) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER_PLATSVC_ATTR", "SEL_BY_SN", param);
	}

	/**
	 * 查询增值服务
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 *             add by longtian3 2013-07-25
	 */
	public static IDataset queryUserPlatByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_SVC_BY_USERID", param);
	}

	public static IDataset queryUserPlatByUserIdAndBizType(String userId, String bizTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("BIZ_TYPE_CODE", bizTypeCode);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_BIZTYPE", param);
	}

	public static IDataset queryUserPlatByUserIdAndServiceId(String userId, String bizTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("BIZ_TYPE_CODE", bizTypeCode);
		IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID_NEW_2", param);
		IDataset upcList = new DatasetList();
		IData temp = new DataMap();
		String serviceId = "";
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				temp = dataset.getData(i);
				serviceId = temp.getString("SERVICE_ID");
				upcList = UpcCall.querySpServiceAndProdByCond("", "", bizTypeCode, serviceId);
				if (IDataUtil.isEmpty(upcList))
				{
					dataset.remove(i);
					i--;
				} else
				{
					temp.put("BIZ_CODE", upcList.getData(0).getString("BIZ_CODE"));
					temp.put("SP_CODE", upcList.getData(0).getString("SP_CODE"));
					temp.put("BIZ_TYPE_CODE", upcList.getData(0).getString("BIZ_TYPE_CODE"));
				}
			}
		}
		return dataset;
	}

	/**
	 * 查询用户服务信息
	 * 
	 * @param userId
	 * @param bizTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserPlatByUserType(String userId, String bizTypeCode) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("BIZ_TYPE_CODE", bizTypeCode);
		IDataset platSvcInfos = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_SERVICEID_BY_USERID", params);
		IDataset result = new DatasetList();
		if (IDataUtil.isNotEmpty(platSvcInfos))
		{
			for (int i = 0; i < platSvcInfos.size(); i++)
			{
				IData platSvcInfo = platSvcInfos.getData(i);
				String serviceId = platSvcInfo.getString("SERVICE_ID", "");
				if (StringUtils.isNotBlank(serviceId))
				{
					IDataset platInfos = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, bizTypeCode, null);
					if (IDataUtil.isNotEmpty(platInfos))
					{
						platSvcInfo.put("BIZ_TYPE_CODE", platInfos.getData(0).getString("BIZ_TYPE_CODE", ""));
						platSvcInfo.put("BIZ_CODE", platInfos.getData(0).getString("BIZ_CODE", ""));
						platSvcInfo.put("SP_CODE", platInfos.getData(0).getString("SP_CODE", ""));
						result.add(platSvcInfo);
					}
				}
			}
		}
		return result;
	}

	public static IDataset queryUserPlatOtherInfoByUserId(String userId, String bizTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("BIZ_TYPE_CODE", bizTypeCode);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_FOR_CHANGEMPHONE", param);
	}

	/**
	 * 查询用户已有平台服务，用于产品变更前台展示
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static IDataset queryUserPlatSvc(String userId) throws Exception
	{

		IData param = new DataMap();

		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "USER_PLATSVC_SEL", param);
	}

	// ADD BY HH
	public static IDataset queryUserPlatSVC(IData param) throws Exception
	{
		return Dao.qryByCodeParser("TF_F_USER_PLATSVC", "SEL_EWPT_37", param);
	}

	public static IDataset queryuserPlatsvcAttr(IData param) throws Exception
	{
		return Dao.qryByCode("TF_F_USER_PLATSVC_ATTR", "SEL_PLAT_ATTR", param);
	}

	// 查询平台服务
	public static IDataset queryUserPlatSvcById(String userId, String svcId) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", svcId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_FIRST_DATE_SYN", param);
	}

	public static IDataset queryUserSvcForIntf2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		StringBuilder sql = new StringBuilder(2000);
		sql.append(" SELECT T.SERVICE_ID,T.BIZ_STATE_CODE,T.INST_ID, ");
		sql.append("        TO_CHAR(T.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
		sql.append("        TO_CHAR(T.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE ");
		sql.append("   FROM TF_F_USER_PLATSVC T ");
		sql.append("  WHERE T.USER_ID =:USER_ID ");
		sql.append("    AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		sql.append("    AND SYSDATE <= T.END_DATE ");
		sql.append("    AND T.BIZ_STATE_CODE = 'A' ");
		sql.append("    AND NOT EXISTS (SELECT 1 ");
		sql.append("           FROM TD_S_COMMPARA C ");
		sql.append("          WHERE C.SUBSYS_CODE = 'CSM' ");
		sql.append("            AND C.PARAM_ATTR = '1' ");
		sql.append("            AND C.PARA_CODE1 = 'SJYYT' ");
		sql.append("            AND C.END_DATE > SYSDATE ");
		sql.append("            AND C.PARAM_CODE = T.SERVICE_ID) ");

		return Dao.qryBySql(sql, param);
	}

	/**
	 * 通过用户ID和服务ID查询用户的平台服务
	 * 
	 * @param userId
	 * @param platSvcId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserPlatSvcByUserIdAndServiceId(String userId, String platSvcId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", platSvcId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_SVCID", param);
	}

	public static IDataset queryUserPlatSvcByUserIdForWap(String userId, String bizTypeCode) throws Exception
	{
		IData pparam = new DataMap();
		pparam.put("USER_ID", userId);
		pparam.put("SUBSYS_CODE", "CSM");
		pparam.put("PARAM_ATTR", "2689");
		pparam.put("PARAM_CODE", bizTypeCode);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_SERVICE_ID_FOR_WAP", pparam);
	}

	public static IDataset queryUserPlatSvcInfos(String USER_ID, String SERVICE_ID) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", USER_ID);
		cond.put("SERVICE_ID", SERVICE_ID);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATSVCS_BY_USERID_SPSERVICE", cond);
	}

	public static IDataset queryUserPlatUiaInfo(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_UIA_INFO", param);
	}

	public static IDataset queryUserSvc3020ProductPck(String userId) throws Exception
	{

		IData qryParam = new DataMap();
		qryParam.put("USER_ID", userId);
		qryParam.put("ELEMENT_ID", "3020");

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_SVC_3020", qryParam);
	}

	public static IDataset queryUserSvcForIntf(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLAT_BY_USERID_FOR_INTF", param);
	}

	/**
	 * 获得平台业务属性，
	 * 
	 * @param serialNumber
	 * @param infoCode
	 *            ==302
	 * @param serviceId
	 *            ==98001901
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserSwitchinfos(String userId) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USER_ALL_SWITCHINFOS_NEW", param);

		IDataset result = new DatasetList();
		if (ArrayUtil.isNotEmpty(userPlatSvcs))
		{
			IDataset upcDatas = UpcCall.querySpServiceByServType("3");
			for (int i = 0; i < userPlatSvcs.size(); i++)
			{
				IData userPlatSvc = userPlatSvcs.getData(i);
				String serviceId = userPlatSvc.getString("SERVICE_ID");
				for (int k = 0; k < upcDatas.size(); k++)
				{
					IData upcData = upcDatas.getData(k);
					String offerCode = upcData.getString("OFFER_CODE");
					if (StringUtils.equals(serviceId, offerCode))
					{
						IData temp = new DataMap();
						temp.put("SERVICE_ID", serviceId);
						temp.put("SP_CODE", upcData.getString("SP_CODE"));
						temp.put("BIZ_CODE", upcData.getString("BIZ_CODE"));
						temp.put("BIZ_TYPE_CODE", upcData.getString("BIZ_TYPE_CODE"));
						temp.put("SERVICE_NAME", upcData.getString("SERVICE_NAME", upcData.getString("OFFER_NAME")));
						temp.put("ORG_DOMAIN", upcData.getString("ORG_DOMAIN"));
						temp.put("BIZ_STATE_CODE", 1);
						result.add(temp);

					}
				}
			}
		}
		Map<String, Map> map = new HashMap<String, Map>();
		if (ArrayUtil.isNotEmpty(result))
		{
			for (int i = 0; i < result.size(); i++)
			{
				IData temp = result.getData(i);
				String serviceId = temp.getString("SERVICE_ID");
				if (map.get(serviceId) == null)
				{
					map.put(serviceId, temp);
				} else
				{
					map.remove(serviceId);

					temp.put("BIZ_STATE_CODE", Integer.parseInt(temp.getString("BIZ_STATE_CODE")) + 1);
					map.put(serviceId, temp);
				}

			}
		}
		result.clear();
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext())
		{
			String serviceId = (String) iterator.next();
			Map list = map.get(serviceId);
			result.add(list);
		}

		return result;
	}

	public static IDataset queryuserWlan(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USER_WLAN", param);
	}

	public static IDataset queryV2CPRecode(IData param) throws Exception
	{

		IData qryParam = new DataMap();
		qryParam.put("USER_ID", param.get("USER_ID"));

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_V2CP", param);
	}


	public static int updDelUser(String userId, String strBizTypeCode) throws Exception
	{
//		IData param = new DataMap();
//		param.put("USER_ID", userId);
//		param.put("BIZ_TYPE_CODE", strBizTypeCode);
//		return Dao.executeUpdateByCodeCode("TF_F_USER_PLATSVC", "UPDATE_DEL_USER", param);
		 
		StringBuilder svcIds = new StringBuilder(""); 
		IDataset platInfos = UpcCall.queryPlatSvc(null, null, strBizTypeCode, null);
		if(IDataUtil.isNotEmpty(platInfos)){
			for(int i=0; i<platInfos.size(); i++){
				IData data = platInfos.getData(i);
				IDataset temp = UpcCall.queryOfferNameByOfferId(data.getString("SP_SERVICE_ID"));
				if(IDataUtil.isEmpty(temp)){
					continue;
				}
				String svcId = temp.getData(0).getString("OFFER_CODE"); 
				svcIds.append("'");
				svcIds.append(svcId);
				if(i==(platInfos.size()-1)){
					svcIds.append("'");
				}else{
					svcIds.append("',");
				}
			}
		}
		
		IData param = new DataMap();
		param.put("USER_ID", userId);
		StringBuilder sql = new StringBuilder("UPDATE TF_F_USER_PLATSVC A SET A.BIZ_STATE_CODE = 'E', A.UPDATE_TIME = SYSDATE, A.END_DATE = SYSDATE,A.REMARK = '补换卡退订BOSS多的手机支付业务' ");
		sql.append(" WHERE A.SERVICE_ID IN ( ");
		sql.append(svcIds);
		sql.append(" ) ");
		sql.append(" AND A.USER_ID = TO_NUMBER(:USER_ID) ");
		sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
		sql.append(" AND A.BIZ_STATE_CODE IN ('N', 'A') ");
		sql.append(" AND A.END_DATE > SYSDATE "); 
		return Dao.executeUpdate(sql, param);
	}
	
	/****************************************************************************************
	 * 本次改造，查询TD_B_PLATSVC改为调用产商品接口 modify by duhj 2017/03/18
	 * 根据用户标识、企业编码、企业业务代码查询用户增值服务信息
	 * 
	 * @param userId
	 *            用户标识
	 * @param spCode
	 *            企业编码
	 * @param bizCode
	 *            企业业务代码
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserPlatSvcInfo(String userId, String spCode, String bizCode) throws Exception
	{
		IData param = new DataMap();
		param.put("VUSER_ID", userId);
		// param.put("VSP_CODE", spCode);
		// param.put("VBIZ_CODE", bizCode);

		IDataset platInfo = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_BIZSP_NEW", param);// 原sql
																									// SEL_BY_USERID_BIZSP

		IData temp = new DataMap();
		String serviceId = "";
		if (IDataUtil.isNotEmpty(platInfo))
		{
			for (int i = 0; i < platInfo.size(); i++)
			{
				temp = platInfo.getData(i);
				serviceId = temp.getString("SERVICE_ID");// 第二步

				IDataset res = UpcCall.querySpServiceParamByCond(serviceId, "Z", spCode, null, bizCode);
				if (IDataUtil.isEmpty(res))
				{
					platInfo.remove(i);
					i--;
				} else
				{
					temp.put("BIZ_CODE", res.getData(0).getString("BIZ_CODE"));
					temp.put("SP_CODE", res.getData(0).getString("SP_CODE"));
					temp.put("BIZ_STATE_CODE", res.getData(0).getString("BIZ_STATE_CODE"));

				}
			}
		}

		return platInfo;

	}

	public static IDataset queryUserPlatSvcInfoByUidDate(String userId, String timePoint) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TIME_POINT", timePoint);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_DATE", param);
	}

	public static IDataset queryFamilyCircle(IData param) throws Exception
	{
		String serialNumber = param.getString("SERIAL_NUMBER");
		String status = param.getString("STATUS");

		IData params = new DataMap();
		params.put("SQL_REF", "SEL_ALL_MEMBER_BY_SN");
		params.put("MSISDN", serialNumber);
		params.put("STATUS", status);
		params.put("GROUP_TYPE", "01");
		IDataset result = Dao.qryByCode("TF_F_USER_FAMILY_CIRCLE", "SEL_ALL_MEMBER_BY_SN", params);

		return result;
	}

	public static IDataset querySafeGroup(IData param) throws Exception
	{
		StringBuilder sql = new StringBuilder("SELECT * FROM TF_F_USER_FAMILY_CIRCLE T WHERE  " + "T.STATUS = :STATUS AND " + "T.GROUP_TYPE = :GROUP_TYPE ");
		if (!"".equals(param.getString("GROUP_NAME", "")))
		{
			sql.append("AND T.GROUP_NAME LIKE  '%" + param.getString("GROUP_NAME", "") + "%' ");
		}
		if (!"".equals(param.getString("TARGET_NAME", "")))
		{
			sql.append("AND T.TARGET_NAME LIKE '%" + param.getString("TARGET_NAME", "") + "%' ");
		}
		if (!"".equals(param.getString("TARGET_MSISDN", "")))
		{
			sql.append("AND T.TARGET_MSISDN = '" + param.getString("TARGET_MSISDN", "") + "'");
		}
		return Dao.qryBySql(sql, param);
	}

	public static IDataset queryUserValidPlatSvc(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "QRY_USER_VALID_PLATSVC", param);

	}

	public static IDataset queryUserPlatSvcByState(String userId, String serviceId, String bizStateCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		param.put("BIZ_STATE_CODE", bizStateCode);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "QRY_USER_PLATSVC_BY_STATE", param);

	}

	public static IDataset getPlatSvcAttrByUserIdSIdAttr(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		return Dao.qryByCode("TF_F_USER_PLATSVC_ATTR", "SEL_PLATSVC_ATTR_BY_USERIDATTR", param);
	}

	// 根据用户ID获取所有有效的SERVICE_ID
	public static IDataset getPlatSvcService(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_USERPLATSVCINFO_BYID", param);
	}

	/**
	 * add by duhj
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryNormalPlatSvcInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		IDataset platInfo = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN04_NEW_01", param);

		IData temp = new DataMap();
		String serviceId = "";
		if (IDataUtil.isNotEmpty(platInfo))
		{
			for (int i = 0; i < platInfo.size(); i++)
			{
				temp = platInfo.getData(i);
				serviceId = temp.getString("SERVICE_ID");

				IDataset res = UpcCall.qrySpServiceSpInfo(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
				if (IDataUtil.isEmpty(res))
				{
					platInfo.remove(i);
					i--;
				} else
				{
					temp.put("SP_ID", res.getData(0).getString("SP_ID"));
					temp.put("BIZ_CODE", res.getData(0).getString("BIZ_CODE"));
					temp.put("BIZ_TYPE_CODE", res.getData(0).getString("BIZ_TYPE_CODE"));
					temp.put("SERVICE_NAME", res.getData(0).getString("BIZ_NAME"));
					temp.put("START_DATE", res.getData(0).getString("VALID_DATE"));
					temp.put("END_DATE", res.getData(0).getString("EXPIRE_DATE"));

				}
			}
		}

		return platInfo;
	}
	
	public static IDataset queryPlatSvcInfoByUserIdForAbility(String userId) throws Exception
    {
    	IData param = new DataMap();
    	IDataset result = new DatasetList();
        param.put("USER_ID", userId);       
//    	return Dao.qryByCodeParser("TF_F_USER_PLATSVC", "SEL_PLATSVC_FOR_ABILITY", param);
        IDataset userPlatSvcs = Dao.qryByCodeParser("TF_F_USER_PLATSVC", "SEL_BIZTYPE_BY_USERID", param);
        if(IDataUtil.isNotEmpty(userPlatSvcs)){
        	for(Object obj : userPlatSvcs){
        		IData userPlatSvc = (IData) obj;
        		String serviceId = userPlatSvc.getString("SERVICE_ID","");
        		IDataset spInfos = UpcCall.qrySpInfoByOfferId(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
        		if(IDataUtil.isNotEmpty(spInfos)){
        			IData spInfo = spInfos.getData(0);
        			if((StringUtils.equals("N", spInfo.getString("BIZ_STATE_CODE","")) 
        			    || StringUtils.equals("A", spInfo.getString("BIZ_STATE_CODE","")) 
        			    || StringUtils.equals("L", spInfo.getString("BIZ_STATE_CODE","")))
        			    && (StringUtils.equals("1", spInfo.getString("SERV_TYPE","")) 
                		    || StringUtils.equals("0", spInfo.getString("SERV_TYPE","")) 
                			|| StringUtils.equals("3", spInfo.getString("SERV_TYPE","")))
        			  )
        			{
        				userPlatSvc.putAll(spInfo);
        				result.add(userPlatSvc);
        			}
        		}
        	}
        }
        
        return result;
    }
	/**
	 * 
	 * @description 查询失效的平台服务
	 * @param @param userId
	 * @param @param serviceId
	 * @param @return
	 * @param @throws Exception
	 * @return IDataset
	 * @author tanzheng
	 * @date 2019年3月20日
	 */
	public static IDataset queryUserInValidPlatSvc(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "QRY_USER_INVALID_PLATSVC", param);

	}
}
 