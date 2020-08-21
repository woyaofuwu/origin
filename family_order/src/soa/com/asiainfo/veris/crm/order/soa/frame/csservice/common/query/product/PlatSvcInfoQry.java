package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class PlatSvcInfoQry
{
	/**
	 * 根据服务ID查询平台服务的局数据信息
	 * 
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getPlatInfoByServiceId(String serviceId, String sp_code, String biz_type_code, String biz_name, String biz_code, String rsrv_str1) throws Exception
	{
		IData param = new DataMap();
		param.put("SERVICE_ID", serviceId);
		param.put("SP_CODE", sp_code);
		param.put("BIZ_TYPE_CODE", biz_type_code);
		param.put("BIZ_NAME", biz_name);
		param.put("BIZ_CODE", biz_code);
		param.put("RSRV_STR1", rsrv_str1);
		return Dao.qryByCodeParser("TD_B_PLATSVC", "SEL_BY_SERVICE_ID", param);
	}

	/**
	 * 根据tab_name,sql_ref,sp_code,biz_code,biz_type_code查询SP企业和业务信息
	 */
	public static IDataset getSpBizInfo(IData inparams) throws Exception
	{
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SP_BIZ", inparams);
	}

	/**
	 * @author luoz
	 * @date 2013-08-20
	 * @description 查询业务信息
	 * @param pd
	 * @param param
	 * @param isCancel
	 * @return
	 * @throws Exception
	 */
	public static IDataset getSpBizInfo(String serviceId, String bizTypeCode, String bizCode, String spCode, boolean isCancel) throws Exception
	{
		IData param = new DataMap();
		if (serviceId != null || !"".equals(serviceId))
		{
			param.put("SERVICE_ID", serviceId);
		}
		param.put("BIZ_TYPE_CODE", bizTypeCode);
		param.put("BIZ_CODE", bizCode);
		param.put("SP_CODE", spCode);

		if (!isCancel)
		{
			return Dao.qryByCodeParser("TD_B_PLATSVC", "SEL_SP_BIZ_INFO_A", param);
		} else
		{
			return Dao.qryByCodeParser("TD_B_PLATSVC", "SEL_SP_BIZ_INFO", param);
		}
	}

	public static IDataset getSpCode(String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT p.sp_code group_id FROM td_b_product_meb t ,td_b_attr_biz b,td_B_platsvc_pf p ");
		parser.addSQL(" WHERE to_char(t.product_id)=to_char(b.attr_obj) ");
		parser.addSQL(" AND b.id=p.service_id ");
		parser.addSQL(" AND p.pf_opertype='CY' ");
		parser.addSQL(" AND t.product_id_b=:PRODUCT_ID ");
		parser.addSQL(" AND ROWNUM<2 ");
		IDataset set = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
		return set;
	}

	/**
	 * 根据sp_code和biz_code捞取TD_B_SPPUNISH的SP处罚信息，
	 * 再根据commpara表的3766参数看该业务的操作码是否也因违规而被处罚导致业务不能订购
	 * 
	 * @param spCode
	 *            企业代码
	 * @param bizCode
	 *            业务代码
	 * @param operCode
	 *            操作码
	 * @return 如果返回的结果集有值，则不抛错，否则抛出平台异常，SP暂停服务
	 * @throws Exception
	 */
	public static IDataset getSpPunish(String spCode, String bizCode, String operCode, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("SP_CODE", spCode);
		param.put("OPERATOR_CODE", bizCode);
		IDataset spPunish = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SPPUNISH", param, page, Route.CONN_CRM_CEN);
		if (spPunish != null && spPunish.size() > 0)
		{
			param.clear();
			param.put("PARAM_CODE", spPunish.getData(0).getString("PUNISH_TYPE"));
			param.put("PARA_CODE1", operCode);
			return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_COMPARA", param, page, Route.CONN_CRM_CEN);
		} else
		{
			return null;
		}
	}

	public static IDataset qryPlatSvc4All(String SP_CODE, String BIZ_CODE, String BIZ_TYPE_CODE) throws Exception
	{
		IDataset upcDatas = UpcCall.qryOffersBySpCond(SP_CODE, BIZ_CODE, BIZ_TYPE_CODE);
		IDataset resultList = new DatasetList();
		if (ArrayUtil.isNotEmpty(upcDatas))
		{
			for (int i = 0; i < upcDatas.size(); i++)
			{
				IData upcData = upcDatas.getData(i);
				IDataset spInfos = UpcCall.querySpServiceAndProdByCond(upcData.getString("SP_CODE"), upcData.getString("BIZ_CODE"), upcData.getString("BIZ_TYPE_CODE"), null);
				if (ArrayUtil.isNotEmpty(spInfos))
				{
					IData temp = new DataMap();

					temp.put("SERVICE_ID", spInfos.getData(0).getString("OFFER_CODE"));
					temp.put("SERVICE_NAME", spInfos.getData(0).getString("OFFER_NAME"));

					temp.put("SP_CODE", upcData.getString("SP_CODE"));
					temp.put("BIZ_CODE", upcData.getString("BIZ_CODE"));
					temp.put("BIZ_TYPE_CODE", upcData.getString("BIZ_TYPE_CODE"));
					temp.put("BIZ_STATE_CODE", upcData.getString("BIZ_STATE_CODE"));
					temp.put("SERV_TYPE", upcData.getString("SERV_TYPE"));
					temp.put("ORG_DOMAIN", upcData.getString("ORG_DOMAIN"));

					resultList.add(temp);
				}
			}
		}
		return resultList;
	}

	public static IDataset qryPlatSvcByAll(String SP_CODE, String BIZ_CODE, String BIZ_TYPE_CODE) throws Exception
	{
		/*
		 * IData param = new DataMap(); param.put("SP_CODE", SP_CODE);
		 * param.put("BIZ_CODE", BIZ_CODE); param.put("BIZ_TYPE_CODE",
		 * BIZ_TYPE_CODE);
		 */
		// return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SPBIZCODE", param);
		IDataset upcDatas = UpcCall.queryPlatSvc2(SP_CODE, BIZ_CODE, BIZ_TYPE_CODE, "A");
		IDataset resultList = new DatasetList();
		if (ArrayUtil.isNotEmpty(upcDatas))
		{
			for (int i = 0; i < upcDatas.size(); i++)
			{
				IData upcData = upcDatas.getData(i);
				IDataset spInfos = UpcCall.querySpServiceAndProdByCond(upcData.getString("SP_CODE"), upcData.getString("BIZ_CODE"), upcData.getString("BIZ_TYPE_CODE"), null);
				if (ArrayUtil.isNotEmpty(spInfos))
				{
					IData temp = new DataMap();

					temp.put("SERVICE_ID", spInfos.getData(0).getString("OFFER_CODE"));
					temp.put("SERVICE_NAME", spInfos.getData(0).getString("OFFER_NAME"));

					temp.put("SP_CODE", upcData.getString("SP_CODE"));
					temp.put("BIZ_CODE", upcData.getString("BIZ_CODE"));
					temp.put("BIZ_TYPE_CODE", upcData.getString("BIZ_TYPE_CODE"));
					temp.put("BIZ_STATE_CODE", upcData.getString("BIZ_STATE_CODE"));
					temp.put("SERV_TYPE", upcData.getString("SERV_TYPE"));
					temp.put("ORG_DOMAIN", upcData.getString("ORG_DOMAIN"));

					resultList.add(temp);
				}
			}
		}
		return resultList;
	}

	public static IDataset qryUserMobileWallet(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "OPEN_WRITE_CARD_PURCHASE", param);
	}

	public static IDataset qryUserMPayInfo(String userId, String bizTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("BIZ_TYPE_CODE", bizTypeCode);
		IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID_NEW", param);
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
				}
			}
		}
		return dataset;
	}

	public static IDataset queryByPk2(String serviceId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERVICE_ID", serviceId);
		IDataset svcs = Dao.qryByCodeParser("TD_B_PLATSVC", "SEL_BY_PK2", inparams, Route.CONN_CRM_CEN);

		return svcs;
	}

	public static IDataset queryByPkgId(String packageId) throws Exception
	{
		IData cond = new DataMap();
		cond.put("PACKAGE_ID", packageId);
		IDataset svcs = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_PACKID", cond, Route.CONN_CRM_CEN);

		return svcs;
	}

	/**
	 * @Function: queryDiscntPackagesByPID()
	 * @Description: 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: yuyj3
	 */
	public static IData queryDiscntPackagesByPID(String productId) throws Exception
	{
		// IData param = new DataMap();
		// param.put("PRODUCT_ID", productId);
		// param.put("ELEMENT_TYPE_CODE", "P");
		// param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		// param.put("BIZ_TYPE_CODE", "51");
		// param.put("PACKAGE_TYPE_CODE", pkgTypeCode); // 0-基础优惠包，2-可选优惠包
		// return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_PRODUCTID", param);

		IData resultData = new DataMap();

		// 0：必选
		IData tmp = UpcCall.queryAllOffersByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "2", "", BizRoute.getTradeEparchyCode());

		if (IDataUtil.isNotEmpty(tmp))
		{
			IDataset offerGroupRelList = tmp.getDataset("OFFER_GROUP_REL_LIST");

			if (IDataUtil.isNotEmpty(offerGroupRelList))
			{
				// 必选服务包
				IDataset basePackages = new DatasetList();
				// 可选服务包
				IDataset optionPackages = new DatasetList();

				IData basePackage = null;
				IData optionPackage = null;

				for (int i = 0; i < offerGroupRelList.size(); i++)
				{
					IDataset groupComRelList = offerGroupRelList.getData(i).getDataset("GROUP_COM_REL_LIST");

					// 基础服务包
					if ("0".equals(offerGroupRelList.getData(i).getString("SELECT_FLAG")) && IDataUtil.isNotEmpty(groupComRelList))
					{
						for (int j = 0; j < groupComRelList.size(); j++)
						{
							basePackage = new DataMap();

							basePackage.put("SERVICE_ID", groupComRelList.getData(j).getString("OFFER_CODE"));
							basePackage.put("SERVICE_NAME", groupComRelList.getData(j).getString("OFFER_NAME"));

							basePackages.add(basePackage);
						}

						resultData.put("B_P", basePackages);
					}

					// 优惠服务包
					if ("2".equals(offerGroupRelList.getData(i).getString("SELECT_FLAG")) && IDataUtil.isNotEmpty(offerGroupRelList))
					{
						for (int k = 0; k < groupComRelList.size(); k++)
						{
							optionPackage = new DataMap();

							optionPackage.put("SERVICE_ID", groupComRelList.getData(k).getString("OFFER_CODE"));
							optionPackage.put("SERVICE_NAME", groupComRelList.getData(k).getString("OFFER_NAME"));

							optionPackages.add(optionPackage);
						}

						resultData.put("O_P", optionPackages);
					}
				}
			}
		}

		return resultData;
	}

	public static IDataset queryEndPlatSvcEntityCard(String routeId) throws Exception
	{
		IData param = new DataMap();
		return Dao.qryByCodeParser("TF_F_USER_PLATSVC", "SEL_END_PLATSVC_ENTITYCARD", param, routeId);
	}

	public static IDataset queryOldPlatSmsConfig(String inModeCode, String bizTypeCode, String spCode, String bizCode, String operCode, String oprSource, String billType, String smsProcessTag) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", "-1");
		param.put("BRAND_CODE", "ZZZZ");
		param.put("PRODUCT_ID", "ZZZZ");
		param.put("EPARCHY_CODE", "ZZZZ");
		param.put("SMS_PROCESS_TAG", smsProcessTag);
		param.put("BILLING_TYPE", billType);

		param.put("IN_MODE_CODE", inModeCode);
		param.put("BIZ_TYPE_CODE", bizTypeCode);
		param.put("SP_CODE", spCode);
		param.put("BIZ_CODE", bizCode);
		param.put("OPER_CODE", operCode);
		param.put("OPR_SOURCE", oprSource);
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SMSINFO", param, Route.CONN_CRM_CEN);
	}

	public static IDataset queryPlatOrderInfobyUserId00(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN00_NOW", param);
	}

	public static IDataset queryPlatOrderInfobyUserId01(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN02_NOW", param);
	}

	public static IDataset queryPlatOrderInfobyUserId02(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN03_NOW", param);
	}

	public static IDataset queryPlatOrderInfobyUserId04(String userId, String rsrvStr9, String productId, String brandCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		// param.put("RSRV_STR9", rsrvStr9);
		param.put("PRODUCT_ID", productId);
		param.put("BRAND_CODE", brandCode);
		param.put("EPARCHY_CODE", eparchyCode);
		// return Dao.qryByCode("TF_F_USER_PLATSVC",
		// "SEL_PLATORDERINFO_BY_SN04", param);
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN04_1", param);
		IDataset returnList = new DatasetList();
		if (ArrayUtil.isNotEmpty(userPlatSvcs))
		{
			for (int i = 0; i < userPlatSvcs.size(); i++)
			{
				IData userPlatSvc = userPlatSvcs.getData(i);
				String serviceId = userPlatSvc.getString("SERVICE_ID");
				// 查询
				IDataset spConfigs = new DatasetList();
				try
				{
					spConfigs = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null);
				} catch (Exception e)
				{

				}
				if (ArrayUtil.isNotEmpty(spConfigs))
				{
					String spCode = spConfigs.getData(0).getString("SP_CODE");
					String bizCode = spConfigs.getData(0).getString("BIZ_CODE");
					String bizTypeCode = spConfigs.getData(0).getString("BIZ_TYPE_CODE");

					IDataset spInfos = UpcCall.qrySpServiceSpInfo(spCode, bizCode, bizTypeCode, null);
					if (ArrayUtil.isNotEmpty(spInfos))
					{
						for (int kk = 0; kk < spInfos.size(); kk++)
						{
							IData spInfo = spInfos.getData(kk);
							String servType = spInfo.getString("SERV_TYPE");
							String bizStateCode = spInfo.getString("BIZ_STATE_CODE");
							String bizStatus = spInfo.getString("BIZ_STATUS");
							String rsrv_str9 = spInfo.getString("RSRV_STR9");
							/*
							 * AND E.SERV_TYPE IN ('1', '0') AND
							 * C.BIZ_STATE_CODE <> 'E' AND C.BIZ_STATUS <> 'E'
							 * AND (C.RSRV_STR9 = :RSRV_STR9 OR :RSRV_STR9 = '0'
							 * OR :RSRV_STR9 IS NULL)
							 */
							if ((StringUtils.equals("0", servType) || StringUtils.equals("1", servType)) && !StringUtils.equals("E", bizStateCode) && !StringUtils.equals("E", bizStatus) && (StringUtils.equals(rsrvStr9, rsrv_str9) || rsrvStr9.equals("0") || rsrvStr9.equals("")))
							{
								IData temp = new DataMap();
								temp.put("SERVICE_ID", userPlatSvc.getString("SERVICE_ID"));
								temp.put("INST_ID", userPlatSvc.getString("INST_ID"));
								temp.put("SP_ID", spInfo.getString("SP_CODE"));
								temp.put("BIZ_CODE", spInfo.getString("BIZ_CODE"));
								temp.put("TAG", spInfo.getString("BIZ_CODE").equals("-SJQB") ? "M" : spInfo.getString("BIZ_CODE"));
								temp.put("SP_NAME", spInfo.getString("SP_NAME").equals("注册类业务") ? "中国移动" : spInfo.getString("SP_NAME"));
								temp.put("BIZ_NAME", spConfigs.getData(0).getString("BIZ_NAME"));
								temp.put("GIFT_SERIAL_NUMBER", userPlatSvc.getString("GIFT_SERIAL_NUMBER"));
								temp.put("SP_SHORT_NAME", spInfo.getString("SP_SHORT_NAME"));
								temp.put("BIZ_TYPE", spInfo.getString("BIZ_TYPE"));
								temp.put("BIZ_TYPE_CODE", spInfo.getString("BIZ_TYPE_CODE"));
								temp.put("RSRV_STR4", userPlatSvc.getString("RSRV_STR4"));
								String bizTypeCode1 = spInfo.getString("BIZ_TYPE_CODE");
								if (StringUtils.equals("04", spInfo.getString("BIZ_TYPE_CODE")))
								{
									bizTypeCode1 = "01";
								} else if (StringUtils.equals("05", spInfo.getString("BIZ_TYPE_CODE")))
								{
									bizTypeCode1 = "02";
								}
								if (StringUtils.equals("03", spInfo.getString("BIZ_TYPE_CODE")))
								{
									bizTypeCode1 = "03";
								}
								temp.put("BIZ_TYPE_CODE1", bizTypeCode1);
								temp.put("ORG_DOMAIN", spInfo.getString("ORG_DOMAIN"));
								temp.put("START_DATE", userPlatSvc.getString("START_DATE"));
								temp.put("END_DATE", userPlatSvc.getString("END_DATE"));
								temp.put("BIZ_STATE_CODE", userPlatSvc.getString("BIZ_STATE_CODE"));
								temp.put("BILLFLG", spInfo.getString("BILL_TYPE"));
								temp.put("PRICE", null != spInfo.getString("PRICE") && !"".equals(spInfo.getString("PRICE")) ? Double.parseDouble(spInfo.getString("PRICE")) / 1000 : "");
								temp.put("SERV_ATTR", spInfo.getString("BIZ_ATTR"));
								temp.put("SERV_CODE", spInfo.getString("SERV_CODE"));
								temp.put("CS_TEL", spInfo.getString("CS_TEL"));
								temp.put("REMARK", userPlatSvc.getString("REMARK"));
								temp.put("UPDATE_STAFF_ID", userPlatSvc.getString("UPDATE_STAFF_ID"));
								temp.put("UPDATE_DEPART_ID", userPlatSvc.getString("UPDATE_DEPART_ID"));
								temp.put("UPDATE_TIME", userPlatSvc.getString("UPDATE_TIME"));
								temp.put("USER_ID", userPlatSvc.getString("USER_ID"));
								temp.put("SERIAL_NUMBER", userPlatSvc.getString("SERIAL_NUMBER"));
								temp.put("FIRST_DATE", userPlatSvc.getString("FIRST_DATE"));
								temp.put("FIRST_DATE_MON", userPlatSvc.getString("FIRST_DATE_MON"));
								temp.put("GIFT_USER_ID", userPlatSvc.getString("GIFT_USER_ID"));
								temp.put("SERV_TYPE", spInfo.getString("SERV_TYPE"));
								temp.put("TAG_CHAR", spInfo.getString("SERV_MODE"));
								temp.put("RSRV_STR10", spInfo.getString("RSRV_STR10"));
								temp.put("SERVICE_TYPE", "Z");
								temp.put("BIZ_DESC", spInfo.getString("BIZ_DESC"));

								returnList.add(temp);
							}

						}
					}
				}
			}
		}
		return returnList;
	}

	public static IDataset queryPlatOrderInfobyUserId05(String userId, String rsrvStr9, String productId, String brandCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RSRV_STR9", rsrvStr9);
		param.put("PRODUCT_ID", productId);
		param.put("BRAND_CODE", brandCode);
		param.put("EPARCHY_CODE", eparchyCode);
		// return Dao.qryByCode("TF_F_USER_PLATSVC",
		// "SEL_PLATORDERINFO_BY_SN05", param);
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN05_1", param);
		IDataset returnList = new DatasetList();
		if (ArrayUtil.isNotEmpty(userPlatSvcs))
		{
			for (int i = 0; i < userPlatSvcs.size(); i++)
			{
				IData userPlatSvc = userPlatSvcs.getData(i);
				String serviceId = userPlatSvc.getString("SERVICE_ID");
				// 查询
				IDataset spConfigs = new DatasetList();
				try
				{
					spConfigs = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null);
				} catch (Exception e)
				{

				}
				if (ArrayUtil.isNotEmpty(spConfigs))
				{
					String spCode = spConfigs.getData(0).getString("SP_CODE");
					String bizCode = spConfigs.getData(0).getString("BIZ_CODE");
					String bizTypeCode = spConfigs.getData(0).getString("BIZ_TYPE_CODE");

					IDataset spInfos = UpcCall.qrySpServiceSpInfo(spCode, bizCode, bizTypeCode, null);
					if (ArrayUtil.isNotEmpty(spInfos))
					{
						for (int kk = 0; kk < spInfos.size(); kk++)
						{
							IData spInfo = spInfos.getData(kk);
							String servType = spInfo.getString("SERV_TYPE");
							if (StringUtils.equals("0", servType) || StringUtils.equals("1", servType))
							{
								IData temp = new DataMap();
								temp.put("SERVICE_ID", userPlatSvc.getString("SERVICE_ID"));
								temp.put("INST_ID", userPlatSvc.getString("INST_ID"));
								temp.put("SP_ID", spInfo.getString("SP_CODE"));
								temp.put("BIZ_CODE", spInfo.getString("BIZ_CODE"));
								temp.put("TAG", spInfo.getString("BIZ_CODE").equals("-SJQB") ? "M" : spInfo.getString("BIZ_CODE"));
								temp.put("SP_NAME", spInfo.getString("SP_NAME").equals("注册类业务") ? "中国移动" : spInfo.getString("SP_NAME"));
								temp.put("BIZ_NAME", spConfigs.getData(0).getString("BIZ_NAME"));
								temp.put("GIFT_SERIAL_NUMBER", userPlatSvc.getString("GIFT_SERIAL_NUMBER"));
								temp.put("SP_SHORT_NAME", spInfo.getString("SP_SHORT_NAME"));
								temp.put("BIZ_TYPE", spInfo.getString("BIZ_TYPE"));
								temp.put("BIZ_TYPE_CODE", spInfo.getString("BIZ_TYPE_CODE"));
								String bizTypeCode1 = spInfo.getString("BIZ_TYPE_CODE");
								if (StringUtils.equals("04", spInfo.getString("BIZ_TYPE_CODE")))
								{
									bizTypeCode1 = "01";
								} else if (StringUtils.equals("05", spInfo.getString("BIZ_TYPE_CODE")))
								{
									bizTypeCode1 = "02";
								}
								if (StringUtils.equals("03", spInfo.getString("BIZ_TYPE_CODE")))
								{
									bizTypeCode1 = "03";
								}
								temp.put("BIZ_TYPE_CODE1", bizTypeCode1);
								temp.put("ORG_DOMAIN", spInfo.getString("ORG_DOMAIN"));
								temp.put("START_DATE", userPlatSvc.getString("START_DATE"));
								temp.put("END_DATE", userPlatSvc.getString("END_DATE"));
								temp.put("BIZ_STATE_CODE", userPlatSvc.getString("BIZ_STATE_CODE"));
								temp.put("BILLFLG", spInfo.getString("BILL_TYPE"));
								temp.put("PRICE", null != spInfo.getString("PRICE") && !"".equals(spInfo.getString("PRICE")) ? Integer.parseInt(spInfo.getString("PRICE")) / 1000 : "");
								temp.put("SERV_ATTR", spInfo.getString("BIZ_ATTR"));
								temp.put("SERV_CODE", spInfo.getString("SERV_CODE"));
								temp.put("CS_TEL", spInfo.getString("CS_TEL"));
								temp.put("REMARK", userPlatSvc.getString("REMARK"));
								temp.put("UPDATE_STAFF_ID", userPlatSvc.getString("UPDATE_STAFF_ID"));
								temp.put("UPDATE_DEPART_ID", userPlatSvc.getString("UPDATE_DEPART_ID"));
								temp.put("UPDATE_TIME", userPlatSvc.getString("UPDATE_TIME"));
								temp.put("USER_ID", userPlatSvc.getString("USER_ID"));
								temp.put("SERIAL_NUMBER", userPlatSvc.getString("SERIAL_NUMBER"));
								temp.put("FIRST_DATE", userPlatSvc.getString("FIRST_DATE"));
								temp.put("FIRST_DATE_MON", userPlatSvc.getString("FIRST_DATE_MON"));
								temp.put("GIFT_USER_ID", userPlatSvc.getString("GIFT_USER_ID"));
								temp.put("SERV_TYPE", spInfo.getString("SERV_TYPE"));
								temp.put("TAG_CHAR", spInfo.getString("SERV_MODE"));
								temp.put("RSRV_STR10", spInfo.getString("RSRV_STR10"));
								temp.put("SERVICE_TYPE", "Z");
								temp.put("BIZ_DESC", spInfo.getString("BIZ_DESC"));

								returnList.add(temp);
							}

						}
					}
				}
			}
		}
		return returnList;

	}

	public static IDataset queryPlatOrderInfobyUserId06(String userId, String rsrvStr9, String productId, String brandCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RSRV_STR9", rsrvStr9);
		param.put("PRODUCT_ID", productId);
		param.put("BRAND_CODE", brandCode);
		param.put("EPARCHY_CODE", eparchyCode);
		// return Dao.qryByCode("TF_F_USER_PLATSVC",
		// "SEL_PLATORDERINFO_BY_SN06", param);
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN06_1", param);
		IDataset returnList = new DatasetList();
		if (ArrayUtil.isNotEmpty(userPlatSvcs))
		{
			for (int i = 0; i < userPlatSvcs.size(); i++)
			{
				IData userPlatSvc = userPlatSvcs.getData(i);
				String serviceId = userPlatSvc.getString("SERVICE_ID");
				// 查询
				IDataset spConfigs = new DatasetList();
				try
				{
					spConfigs = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null);
				} catch (Exception e)
				{

				}
				if (ArrayUtil.isNotEmpty(spConfigs))
				{
					String spCode = spConfigs.getData(0).getString("SP_CODE");
					String bizCode = spConfigs.getData(0).getString("BIZ_CODE");
					String bizTypeCode = spConfigs.getData(0).getString("BIZ_TYPE_CODE");

					IDataset spInfos = UpcCall.qrySpServiceSpInfo(spCode, bizCode, bizTypeCode, null);
					if (ArrayUtil.isNotEmpty(spInfos))
					{
						for (int kk = 0; kk < spInfos.size(); kk++)
						{
							IData spInfo = spInfos.getData(kk);
							String servType = spInfo.getString("SERV_TYPE");

							if (StringUtils.equals("0", servType) || StringUtils.equals("1", servType))
							{
								IData temp = new DataMap();
								temp.put("SERVICE_ID", userPlatSvc.getString("SERVICE_ID"));
								temp.put("INST_ID", userPlatSvc.getString("INST_ID"));
								temp.put("SP_ID", spInfo.getString("SP_CODE"));
								temp.put("BIZ_CODE", spInfo.getString("BIZ_CODE"));
								temp.put("TAG", spInfo.getString("BIZ_CODE").equals("-SJQB") ? "M" : spInfo.getString("BIZ_CODE"));
								temp.put("SP_NAME", spInfo.getString("SP_NAME").equals("注册类业务") ? "中国移动" : spInfo.getString("SP_NAME"));
								temp.put("BIZ_NAME", spConfigs.getData(0).getString("BIZ_NAME"));
								temp.put("GIFT_SERIAL_NUMBER", userPlatSvc.getString("GIFT_SERIAL_NUMBER"));
								temp.put("SP_SHORT_NAME", spInfo.getString("SP_SHORT_NAME"));
								temp.put("BIZ_TYPE", spInfo.getString("BIZ_TYPE"));
								temp.put("BIZ_TYPE_CODE", spInfo.getString("BIZ_TYPE_CODE"));
								String bizTypeCode1 = spInfo.getString("BIZ_TYPE_CODE");
								if (StringUtils.equals("04", spInfo.getString("BIZ_TYPE_CODE")))
								{
									bizTypeCode1 = "01";
								} else if (StringUtils.equals("05", spInfo.getString("BIZ_TYPE_CODE")))
								{
									bizTypeCode1 = "02";
								}
								if (StringUtils.equals("03", spInfo.getString("BIZ_TYPE_CODE")))
								{
									bizTypeCode1 = "03";
								}
								temp.put("BIZ_TYPE_CODE1", bizTypeCode1);
								temp.put("ORG_DOMAIN", spInfo.getString("ORG_DOMAIN"));
								temp.put("START_DATE", userPlatSvc.getString("START_DATE"));
								temp.put("END_DATE", userPlatSvc.getString("END_DATE"));
								temp.put("BIZ_STATE_CODE", userPlatSvc.getString("BIZ_STATE_CODE"));
								temp.put("BILLFLG", spInfo.getString("BILL_TYPE"));
								temp.put("PRICE", null != spInfo.getString("PRICE") && !"".equals(spInfo.getString("PRICE")) ? Integer.parseInt(spInfo.getString("PRICE")) / 1000 : "");
								temp.put("SERV_ATTR", spInfo.getString("BIZ_ATTR"));
								temp.put("SERV_CODE", spInfo.getString("SERV_CODE"));
								temp.put("CS_TEL", spInfo.getString("CS_TEL"));
								temp.put("REMARK", userPlatSvc.getString("REMARK"));
								temp.put("UPDATE_STAFF_ID", userPlatSvc.getString("UPDATE_STAFF_ID"));
								temp.put("UPDATE_DEPART_ID", userPlatSvc.getString("UPDATE_DEPART_ID"));
								temp.put("UPDATE_TIME", userPlatSvc.getString("UPDATE_TIME"));
								temp.put("USER_ID", userPlatSvc.getString("USER_ID"));
								temp.put("SERIAL_NUMBER", userPlatSvc.getString("SERIAL_NUMBER"));
								temp.put("FIRST_DATE", userPlatSvc.getString("FIRST_DATE"));
								temp.put("FIRST_DATE_MON", userPlatSvc.getString("FIRST_DATE_MON"));
								temp.put("GIFT_USER_ID", userPlatSvc.getString("GIFT_USER_ID"));
								temp.put("SERV_TYPE", spInfo.getString("SERV_TYPE"));
								temp.put("TAG_CHAR", spInfo.getString("SERV_MODE"));
								temp.put("RSRV_STR10", spInfo.getString("RSRV_STR10"));
								temp.put("SERVICE_TYPE", "Z");
								temp.put("BIZ_DESC", spInfo.getString("BIZ_DESC"));

								returnList.add(temp);
							}

						}
					}
				}
			}
		}
		return returnList;
	}

	public static IDataset queryPlatOrderInfobyUserId01(String userId, String rsrvStr9, String productId, String brandCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("RSRV_STR9", rsrvStr9);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("BRAND_CODE", brandCode);
		// return Dao.qryByCode("TF_F_USER_SVC", "SEL_PLATORDERINFO_BY_SN01",
		// param);
		IDataset dataset1 = Dao.qryByCode("TF_F_USER_SVC", "SEL_PLATORDERINFO_BY_SN01_1", param);
		IData pkgElementInfos = UpcCall.queryAllOffersByOfferId("P", productId, "", "", BizRoute.getTradeEparchyCode());

		IDataset offerComRelList = pkgElementInfos.getDataset("OFFER_COM_REL_LIST");
		IDataset result = new DatasetList();
			for (int i = 0; i < dataset1.size(); i++)
			{
				IData data = dataset1.getData(i);
			boolean isFind = false;
					for (int j = 0; j < offerComRelList.size(); j++)
					{
						IData offer = offerComRelList.getData(j);
						String offerCode = offer.getString("OFFER_CODE");
						String offerType = offer.getString("OFFER_TYPE");

						if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_SVC, offerType))
						{
							if (StringUtils.equals(offerCode, data.getString("SERVICE_ID")))
							{
								dataset1.remove(i);
								i--;
						isFind = true;
						break;
					}
				}
			}
			if (!isFind) {

								IDataset serviceRsrvConfigs = UpcCall.queryServiceByRsrvTag3(BofConst.ELEMENT_TYPE_CODE_SVC, data.getString("SERVICE_ID"), "TD_B_SERVICE", null);
								IData temp = new DataMap();
								String rsrvStr2 = "";
								String rsrvStr3 = "";
								String rsrvStr4 = "";
								String rsrvStr5 = "";

								String rsrvTag3 = "";
								if (ArrayUtil.isNotEmpty(serviceRsrvConfigs))
								{
									for (int kk = 0; kk < serviceRsrvConfigs.size(); kk++)
									{
										IData serviceRsrvConfig = serviceRsrvConfigs.getData(kk);
										if (StringUtils.equals("RSRV_STR2", serviceRsrvConfig.getString("FIELD_NAME")))
										{
											rsrvStr2 = serviceRsrvConfig.getString("FIELD_VALUE");
										}
										if (StringUtils.equals("RSRV_STR3", serviceRsrvConfig.getString("FIELD_NAME")))
										{
											rsrvStr3 = serviceRsrvConfig.getString("FIELD_VALUE");
										}
										if (StringUtils.equals("RSRV_STR4", serviceRsrvConfig.getString("FIELD_NAME")))
										{
											rsrvStr4 = serviceRsrvConfig.getString("FIELD_VALUE");
										}
										if (StringUtils.equals("RSRV_STR5", serviceRsrvConfig.getString("FIELD_NAME")))
										{
											rsrvStr5 = serviceRsrvConfig.getString("FIELD_VALUE");
										}
										if (StringUtils.equals("RSRV_TAG3", serviceRsrvConfig.getString("FIELD_NAME")))
										{
											rsrvTag3 = serviceRsrvConfig.getString("FIELD_VALUE");
										}
									}
								}
								
								temp.put("SERVICE_ID", data.getString("SERVICE_ID"));
								temp.put("INST_ID", data.getString("INST_ID"));
								temp.put("SP_ID", "1001");
								temp.put("BIZ_CODE", "");
								temp.put("SP_NAME", "中国移动");
								try {
									temp.put("BIZ_NAME", OfferCfg.getInstance(data.getString("SERVICE_ID"), "S")
											.getOfferName());
								} catch (Exception e) {
									continue;
								}
				
								temp.put("GIFT_SERIAL_NUMBER", "");
								temp.put("SP_SHORT_NAME", "");
								temp.put("BIZ_TYPE", "");
								temp.put("BIZ_TYPE_CODE", "");
								temp.put("BIZ_TYPE_CODE1", "");
								temp.put("ORG_DOMAIN", "");
								temp.put("OPR_SOURCE", "");
								temp.put("START_DATE", data.getString("START_DATE"));
								temp.put("END_DATE", data.getString("END_DATE"));
								temp.put("BIZ_STATE_CODE", "");
								temp.put("SERV_ATTR", "");
								temp.put("SERV_CODE", "");
								temp.put("CS_TEL", "");

								temp.put("REMARK", data.getString("REMARK"));
								temp.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID"));
								temp.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID"));
								temp.put("UPDATE_TIME", data.getString("UPDATE_TIME"));
								temp.put("PARTITION_ID", data.getString("PARTITION_ID"));
								temp.put("USER_ID", data.getString("USER_ID"));
								/*
								 * temp.put("PRODUCT_ID",
								 * data.getString("PRODUCT_ID"));
								 * temp.put("PACKAGE_ID",
								 * data.getString("PACKAGE_ID"));
								 */

								temp.put("SERIAL_NUMBER", "");
								temp.put("PRODUCT_NO", "");
								temp.put("OPER_CODE", "");
								temp.put("GIFT_USER_ID", "");

								temp.put("FIRST_DATE", data.getString("START_DATE"));
								temp.put("FIRST_DATE_MON", data.getString("START_DATE"));
								temp.put("SUBSCRIBE_ID", data.getString("USER_ID"));
								temp.put("SERV_TYPE", "0");
								temp.put("SERVICE_TYPE", "S");

								/*
								 * B.RSRV_STR2 RSRV_STR10, B.RSRV_STR5 BIZ_DESC
								 * B.RSRV_STR4 BILLFLG
								 */
								temp.put("RSRV_STR10", rsrvStr2);
								temp.put("BIZ_DESC", rsrvStr5);
								temp.put("BILLFLG", rsrvStr4);

								temp.put("PRICE", null != rsrvStr3 && !"".equals(rsrvStr3) ? Integer.parseInt(rsrvStr3) / 1000 : "");
								temp.put("TAG_CHAR", rsrvTag3);

								result.add(temp);
							}
						}
			
			//短厅传入的参数 TAG_CHAR ：0,全部(自有+梦网)；1,自有业务，2,梦网业务
			for (int k = 0; k < result.size(); k++)
			{
				String tagChar = "";
				tagChar = result.getData(k).getString("TAG_CHAR");
				if("0".equals(rsrvStr9)){
					if(!"1".equals(tagChar)&&!"2".equals(tagChar)){
						result.remove(k);
						k--;
					}
				}else{
					if(!tagChar.equals(rsrvStr9)){
						result.remove(k);
						k--;
					}
				}
			}
		return result;
	}

	public static IDataset queryPlatOrderInfoSN00(String removeTag, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("REMOVE_TAG", removeTag);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN00", param);
	}

	public static IDataset queryPlatOrderInfoSN01(String removeTag, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("REMOVE_TAG", removeTag);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN04", param);
	}

	public static IDataset queryPlatOrderInfoSN02(String removeTag, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("REMOVE_TAG", removeTag);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN02", param);
	}

	public static IDataset queryPlatOrderInfoSN03(String removeTag, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("REMOVE_TAG", removeTag);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN03", param);
	}

	public static IDataset queryPlatOrderInfoSN04(String removeTag, String userId, String rsrvStr9, String productId, String eparchyCode, String brandCode) throws Exception
	{
		IData param = new DataMap();
		param.put("REMOVE_TAG", removeTag);
		param.put("USER_ID", userId);
		param.put("RSRV_STR9", rsrvStr9);
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("BRAND_CODE", brandCode);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN04", param);
	}

	public static IDataset queryPlatOrderInfoSN05(String removeTag, String userId, String rsrvStr9, String productId, String eparchyCode, String brandCode) throws Exception
	{
		IData param = new DataMap();
		param.put("REMOVE_TAG", removeTag);
		param.put("USER_ID", userId);
		param.put("RSRV_STR9", rsrvStr9);
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("BRAND_CODE", brandCode);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN05", param);
	}

	public static IDataset queryPlatOrderInfoSN06(String removeTag, String userId, String rsrvStr9, String productId, String eparchyCode, String brandCode) throws Exception
	{
		IData param = new DataMap();
		param.put("REMOVE_TAG", removeTag);
		param.put("USER_ID", userId);
		param.put("RSRV_STR9", rsrvStr9);
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("BRAND_CODE", brandCode);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN06", param);
	}

	public static IDataset queryPlatSmsConfig(String inModeCode, String bizTypeCode, String spCode, String bizCode, String operCode, String oprSource, String billType, String servMode, String feeModeTag) throws Exception
	{
		IData param = new DataMap();
		param.put("IN_MODE_CODE", inModeCode);
		param.put("BIZ_TYPE_CODE", bizTypeCode);
		param.put("SP_CODE", spCode);
		param.put("BIZ_CODE", bizCode);
		param.put("OPER_CODE", operCode);
		param.put("OPR_SOURCE", oprSource);
		param.put("BILL_TYPE", billType);
		param.put("SERV_MODE", servMode);
		param.put("FEEMODE_TAG", feeModeTag);
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_PLATSMS", param, Route.CONN_CRM_CEN);
	}

	public static IData queryPlatsvcByPk(String serviceId) throws Exception
	{
		IData platsvcInfo = new DataMap();
		try
		{
			platsvcInfo = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PLATSVC, serviceId);
		} catch (Exception e)
		{

		}

		if (IDataUtil.isNotEmpty(platsvcInfo))
		{
			platsvcInfo.put("SERVICE_NAME", platsvcInfo.getString("OFFER_NAME", ""));
		}

		return platsvcInfo;
	}

	public static IDataset queryPlatSvcByServiceId(String serviceId) throws Exception
	{
		IData platSvcData = UPlatSvcInfoQry.qryServInfoBySvcId(serviceId);
		if (IDataUtil.isEmpty(platSvcData))
		{
			return null;
		}

		IDataset platSvcDataset = new DatasetList();
		platSvcDataset.add(platSvcData);
		return platSvcDataset;
	}

	public static IDataset queryPlatSvcBySpBizCode(String spCode, String bizCode) throws Exception
	{
		IData param = new DataMap();
		param.put("SP_CODE", spCode);
		param.put("BIZ_CODE", bizCode);
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SP_BIZ_CODE", param, Route.CONN_CRM_CEN);
	}

	public static IDataset queryPlatSvcByUserIdForWap(String userId, String subSys, String paramAttr, String paramCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		// return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_PK_FOR_WAP", pparam);
		// 1.查询td_d_commpara表

		IDataset commparas = CommparaInfoQry.getCommparaAllCol(subSys, paramAttr, paramCode, "0898");

		// 2.取出用户已有的platsvc数据
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID", param);
		IDataset dataset = new DatasetList();

		if (ArrayUtil.isNotEmpty(userPlatSvcs))
		{
			for (int i = 0; i < commparas.size(); i++)
			{
				IData commpara = commparas.getData(i);
				String paraCode1 = commpara.getString("PARA_CODE1");
				for (int j = 0; j < userPlatSvcs.size(); j++)
				{
					IData userPlatSvc = userPlatSvcs.getData(j);
					String userPlatSvcId = userPlatSvc.getString("SERVICE_ID");
					if (StringUtils.equals(paraCode1, userPlatSvcId) && StringUtils.equals("A", userPlatSvc.getString("BIZ_STATE_CODE")))
					{
						commparas.remove(i);
						i--;
						break;
					}
				}
			}
			dataset = commparas;
		} else
		{
			dataset = commparas;
		}

		// dataset
		for (int i = 0; i < dataset.size(); i++)
		{
			IData data = dataset.getData(i);
			String serviceId = data.getString("PARA_CODE1");
			IDataset upcDatas = new DatasetList();
			try
			{
				upcDatas = UpcCall.querySpServiceAndProdByCond(null, null, null, serviceId);
			} catch (Exception e)
			{

			}
			for (int j = 0; j < upcDatas.size(); j++)
			{
				data.put("ELEMENT_DESC", upcDatas.getData(j).getString("DESCRIPTION"));
				data.put("BIZ_TYPE_CODE", upcDatas.getData(j).getString("BIZ_TYPE_CODE"));
				data.put("SP_CODE", upcDatas.getData(j).getString("SP_CODE"));
				data.put("BIZ_CODE", upcDatas.getData(j).getString("BIZ_CODE"));
			}

		}

		IDataset resultList = new DatasetList();
		for (int i = 0; i < dataset.size(); i++)
		{
			IData data = dataset.getData(i);
			String spCode = data.getString("SP_CODE");
			String bizCode = data.getString("BIZ_CODE");
			String bizTypeCode = data.getString("BIZ_TYPE_CODE");
			// UPC.Out.SpQueryFSV.qrySpServiceSpInfo
			IDataset spInfos = UpcCall.qrySpServiceSpInfo(spCode, bizCode, bizTypeCode, "A");
			for (int j = 0; j < spInfos.size(); j++)
			{
				if (StringUtils.equals("A", spInfos.getData(j).getString("BIZ_STATE_CODE")))
				{
					resultList.add(data);
				}
			}
		}
		if (ArrayUtil.isNotEmpty(resultList))
		{
			return resultList;
		} else
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String bizTypeCode = data.getString("BIZ_TYPE_CODE");
				IDataset upcDatas = UpcCall.querySpServiceParamByCond(null, null, bizTypeCode, null);
				for (int j = 0; j < upcDatas.size(); j++)
				{
					IData upcData = upcDatas.getData(j);
					String biz_type_code = upcData.getString("BIZ_TYPE_CODE");
					if (StringUtils.equals("1", biz_type_code.substring(0, 1)))
					{
						resultList.add(data);
					}
				}
			}
			return resultList;
		}

	}

	public static IDataset queryPlatSvcInfo(IData iData) throws Exception
	{
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SVCID", iData, Route.CONN_CRM_CEN);
	}

	public static IDataset queryPlatSvcInfos(String SERVICE_ID) throws Exception
	{
		IData cond = new DataMap();
		cond.put("SERVICE_ID", SERVICE_ID);
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_PK", cond);
	}

	public static IDataset queryPlatSvcInfosBySpBizCode(String BIZ_CODE, String SP_CODE) throws Exception
	{
		IData cond = new DataMap();
		cond.put("SERVICE_CODE", BIZ_CODE);
		cond.put("SP_CODE", SP_CODE);
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SPBIZCODE_NOSTATE", cond, Route.CONN_CRM_CEN);
	}

	public static IDataset queryPlatSvcs(String cond, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("COND", cond);
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_COND", param, pagination, Route.CONN_CRM_CEN);
	}

	/**
	 * 查询平台服务信息
	 */
	public static IDataset queryServiceInfo(IData inParam) throws Exception
	{

		IData param = new DataMap();
		param.put("BIZ_TYPE_CODE", inParam.getString("BIZ_TYPE_CODE", "").trim());
		param.put("SP_CODE", inParam.getString("SP_CODE", "").trim());
		param.put("SERVICE_ID", inParam.getString("SERVICE_ID", "").trim());
		param.put("BIZ_CODE", inParam.getString("BIZ_CODE", "").trim());
		param.put("BIZ_NAME", inParam.getString("BIZ_NAME", "").trim());
		param.put("SERVICE_ID", inParam.getString("SERVICE_ID", "").trim());
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SERVICE_ID", param);
	}

	public static IDataset queryServIdBySpId(IData iData) throws Exception
	{

		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SP_BIZ_SVC", iData, Route.CONN_CRM_CEN);
	}

	public static IDataset querySpBizBySvcId(String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERVICE_ID", serviceId);
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_SP_BIZ_BY_SVCID", param);
	}

	/**
	 * @Function: querySpBizList
	 * @Description: 热点业务查询
	 * @param: @param availMonth
	 * @param: @param eparchyCode
	 * @param: @return
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @throws Exception
	 * @date: 4:23:42 PM Jul 27, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* Jul 27,
	 *        2013 longtian3 v1.0.0 TODO:
	 */
	public static IDataset querySpBizList(String availMonth, String eparchyCode, String state) throws Exception
	{
		IData param = new DataMap();
		param.put("AVAIL_MONTH", availMonth);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("STATE", state);

		return Dao.qryByCode("TD_B_PLATSVC", "SEL_SP_BIZ_LIST", param, Route.CONN_CRM_CEN);
	}

	public static IDataset querySpByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_SP_BY_USERID", param);
	}

	public static IDataset querySpOrderByUserId(String userId, String bizCode, String spCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_SPORDER_BY_USERID_NEW", param);
		IDataset result = new DatasetList();
		for (int i = 0; i < userPlatSvcs.size(); i++)
		{
			IData userPlatSvc = userPlatSvcs.getData(i);
			String serviceId = userPlatSvc.getString("SERVICE_ID");
			IDataset upcDatas = new DatasetList();
			try
			{
				upcDatas = UpcCall.querySpServiceAndInfoAndParamByCond(serviceId, spCode, bizCode, null);
			} catch (Exception e)
			{

			}
			for (int j = 0; j < upcDatas.size(); j++)
			{
				IData upcData = upcDatas.getData(j);

				String servType = upcData.getString("SERV_TYPE");
				String bizStateCode = upcData.getString("BIZ_STATE_CODE");
				String orgDomain = upcData.getString("ORG_DOMAIN");
				if (StringUtils.equals("1", servType) && StringUtils.equals("DSMP", orgDomain) && (StringUtils.equals("A", bizStateCode) || StringUtils.equals("N", bizStateCode)))
				{
					IData tempMap = new DataMap();

					tempMap.putAll(userPlatSvc);
					tempMap.putAll(upcData);

					result.add(tempMap);
				}
			}
		}
		return result;
	}

	/**
	 * 查询服务信息
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySvcBizInfo(IData inparams) throws Exception
	{
		IDataset platsvcInfos = new DatasetList();
		IData platSvcInfoData = UPlatSvcInfoQry.qryServInfoBySvcId(inparams.getString("SERVICE_ID"));
		if (IDataUtil.isNotEmpty(platSvcInfoData))
		{
			platsvcInfos.add(platSvcInfoData);
		}
		return platsvcInfos;
	}

	public static IDataset querySvcBySpCodeAndBizCode(String spCode, String bizCode) throws Exception
	{
		IData param = new DataMap();
		param.put("SP_CODE", spCode);
		param.put("BIZ_CODE", bizCode);
		IDataset platsvcs = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SP_BIZ_CODE", param, Route.CONN_CRM_CEN);

		return platsvcs;
	}

	public static IDataset querySwicthService(IData param) throws Exception
	{

		IData qryParam = new DataMap();
		qryParam.put("USER_ID", param.get("USER_ID"));

		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SW_SERVICE", param);
	}

	public static IDataset queryUIOInfo(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_UIA_INFO", param);
	}

	public static IDataset queryUserBIZTYPEBIZ54(String bizTypeCode, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("BIZ_TYPE_CODE", bizTypeCode);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_BIZTYPEBIZ54", param);
	}

	public static IDataset queryUserPlatSvcs(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID", param);
	}

	public static IDataset queryUserPlatSvcs1(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID_1_NOW", param);
	}

	public static IDataset queryUserPlatSvcs2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID_2_NOW", param);
	}

	public static IDataset queryUserPlatSvcs(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID_NP", param, eparchyCode);
	}

	public static IDataset queryUserSwitch(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_SWITCH", param);

		IDataset upcDatas = UpcCall.querySpServiceByServType("3");
		IDataset resultList = new DatasetList();
		for (int i = 0; i < userPlatSvcs.size(); i++)
		{
			IData userPlatSvc = userPlatSvcs.getData(i);
			String serviceId = userPlatSvc.getString("SERVICE_ID");
			for (int j = 0; j < upcDatas.size(); j++)
			{
				IData upcData = upcDatas.getData(j);
				String offerCode = upcData.getString("OFFER_CODE");
				if (StringUtils.equals(serviceId, offerCode))
				{
					IData temp = new DataMap();
					upcData.put("SERVICE_NAME", upcData.getString("OFFER_NAME"));

					temp.putAll(userPlatSvc);
					temp.putAll(upcData);

					resultList.add(temp);
				}
			}

		}
		return resultList;

	}

	/**
	 * 依据旧的局数据查询配置关系 author: zhangbo18
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryOfficeData(IData param) throws Exception
	{
		// return Dao.qryByCode("TD_B_OFFICEDATA_RELATION",
		// "SEL_BY_OLD_OFFDATA", param, Route.CONN_CRM_CEN);
		return UpcCall.qryOldSpOfficeData(param.getString("OLD_SP_CODE"), param.getString("OLD_BIZ_CODE"));
	}

	/**
	 * 依据新的局数据查询配置关系 author: zhangbo18
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryNewOfficeData(IData param) throws Exception
	{
		// return Dao.qryByCode("TD_B_OFFICEDATA_RELATION",
		// "SEL_BY_NEW_OFFDATA", param, Route.CONN_CRM_CEN);
		return UpcCall.qryNewSpOfficeData(param.getString("NEW_SP_CODE"), param.getString("NEW_BIZ_CODE"));
	}

	public static IDataset querySvcAllBySpCodeAndBizCode(String spCode, String bizCode, String bizTypeCode) throws Exception
	{
		/*
		 * IData param = new DataMap(); param.put("SP_CODE", spCode);
		 * param.put("BIZ_CODE", bizCode); param.put("BIZ_TYPE_CODE",
		 * bizTypeCode); IDataset platsvcs = Dao.qryByCode("TD_B_PLATSVC",
		 * "SEL_ALLINFO_BY_SPBIZCODE", param, Route.CONN_CRM_CEN);
		 * 
		 * return platsvcs;
		 */
		IDataset platsvcs = UpcCall.qryOffersBySpCond(spCode, bizCode, bizTypeCode);
		return platsvcs;
	}

	public static IDataset querySpTariff(String spCode, String bizCode, String bizTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("SP_CODE", spCode);
		param.put("SP_BIZ_CODE", bizCode);
		param.put("BIZ_TYPE_CODE", bizTypeCode);

		StringBuilder sql = new StringBuilder(1000);
		sql.append("SELECT * FROM ucr_param.td_b_sp_tariff t ");
		sql.append("WHERE remind_tag=1 and first_rate_type=0 ");
		sql.append("AND t.sp_code=:SP_CODE AND t.sp_biz_code=:SP_BIZ_CODE AND t.biz_type_code=:BIZ_TYPE_CODE");

		return Dao.qryBySql(sql, param, "cen2");
	}

	public static IDataset queryBizServiceByBizTypeCode(String serviceId, String bizTypeCode) throws Exception
	{
		//IData param = new DataMap();
		//param.put("SERVICE_ID", serviceId);
		//param.put("BIZ_TYPE_CODE", bizTypeCode);
		//return Dao.qryByCode("TD_B_PLATSVC", "QRY_BIZ_TYPE_SERVICE", param);
		return UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, bizTypeCode, null);
	}
	
	/**
	 * 查询重点业务
	 * huangzl3
	 */
	public static IDataset queryKeyBusiness() throws Exception
	{
		IData param = new DataMap();
		return Dao.qryByCode("TD_S_COMMPARA", "QRY_KEYBUSINESS", param, Route.CONN_CRM_CEN);
	}
}
