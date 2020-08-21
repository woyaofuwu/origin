
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.dreamnet;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.HttpUtil;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.dreamnet.WithinSetDreamNetQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;

public class WithinSetDreamNetQrySVC extends CSBizService
{

	private static final long serialVersionUID = 6677378755249887724L;

	/**
	 * 定单信息查询 data中需要放入查询参数: SERIAL_NUMBER 手机号码 必须 REMOVE_TAG 用户状态 可以为空(默认为0) DEAL_TAG 处理标识 可以为空(默认00:生效的订购关系) 01:历史定购信息
	 * 02:全部订购关系
	 */
	public IDataset withinSetDreamNet(IData data) throws Exception
	{

		IData userInfo = new DataMap();
		IData rst = new DataMap();
		// improtParser(data);

		IData param = new DataMap();

		param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));// 手机号码
		param.put("REMOVE_TAG", data.getString("REMOVE_TAG", "0"));// 默认正常用户
		param.put("DEAL_TAG", data.getString("DEAL_TAG", "00"));// 默认单条数据发送

		IDataset isett = UserInfoQry.getUserInfoBySerialNumber(data.getString("SERIAL_NUMBER"), data.getString("REMOVE_TAG", "0"), "00");

		if (IDataUtil.isNotEmpty(isett))
		{
			userInfo = isett.getData(0);
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_13, "0925" + ": " + "用户信息不存在,或者该用户为异地用户");
		}
		param.put("USER_ID", userInfo.getString("USER_ID"));
		param.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
		param.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
		param.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
		param.put("RSRV_STR9", data.getString("TAG_CHAR", "0"));// 0,全部；1,自有业务，2,梦网业务
		IDataset result = new DatasetList();

		if ("00".equals(param.getString("DEAL_TAG")))
		{// 生效的订购关系
			//result = WithinSetDreamNetQry.selPlatOrderInfo(param);
			result = selPlatOrderInfo(param);
			/*
			 * if (!"1".equals(data.getString("TAG_CHAR", "0"))) { if ("0".equals(data.getString("TAG_CHAR", "0"))) {
			 * param.put("TAG_CHAR", data.getString("TAG_CHAR", "1")); } IDataset result1 = new DatasetList(); result =
			 * WithinSetDreamNetQry.selPlatOrderInfo(param); result.addAll(result1); }
			 */
			if (!"2".equals(data.getString("TAG_CHAR", "0")))
			{
				if ("0".equals(data.getString("TAG_CHAR", "0")))
				{
					param.put("RSRV_STR9", "1");
				}

				IDataset result1 = new DatasetList();
				result1 = selOrderInfo(param);
				result.addAll(result1);
			}

			DataHelper.sort(result, "RSRV_STR10", 0, "BIZ_TYPE_CODE1", 0); // 排序
		}
		IDataset result2 = new DatasetList();
		if (result != null && result.size() >= 1)
		{
			for (int i = 0; i < result.size(); i++)
			{

				String giftSerialNmuber = result.getData(i).getString("GIFT_SERIAL_NUMBER");
				if (giftSerialNmuber != null && !giftSerialNmuber.equals(""))
				{
					result.getData(i).put("GIFT_TYPE", "1");
				}
				else
				{
					result.getData(i).put("GIFT_TYPE", "0");
				}
				String price = result.getData(i).getString("PRICE", "");
				if (price.length() > 0)
				{
					if (price.startsWith("."))
					{
						price = "0" + price;
					}
				}
				result.getData(i).put("PRICE", price);

				String biztypecode = result.getData(i).getString("BIZ_TYPE_CODE");
				String serviceID = result.getData(i).getString("SERVICE_ID");
				String serv_type = result.getData(i).getString("SERV_TYPE");
				String instId = result.getData(i).getString("INST_ID");
				result.getData(i).remove("INST_ID");
				if (biztypecode != null && !biztypecode.equals("") && biztypecode.equals("27") && serv_type.equals("0"))
				{
					result.getData(i).put("SERV_TYPE", "1");
				}
				if (biztypecode != null && !biztypecode.equals("") && biztypecode.equals("19") && serviceID.equals("98001901"))
				{
					IData param2 = new DataMap();
					IDataset musicattrs = new DatasetList();
					param2.put("USER_ID", userInfo.getString("USER_ID"));
					param2.put("RELA_INST_ID", instId);
					musicattrs = WithinSetDreamNetQry.selPlatAttrInfo(param2);

					for (int a = 0; a < musicattrs.size(); a++)
					{
						String attr_code = musicattrs.getData(a).getString("ATTR_CODE");
						String attr_value = musicattrs.getData(a).getString("ATTR_VALUE");

						if (attr_code.equals("302"))
						{
							if (attr_value.equals("2"))
							{
								result.getData(i).put("PRICE", "5");// 平台业务返回价格以元为单位
								result.getData(i).put("BIZ_NAME", "咪咕音乐高级会员");
								result.getData(i).put("BIZ_TYPE", "咪咕音乐高级会员");
								result.getData(i).put("BILL_TYPE", "2");
								result.getData(i).put("BILLFLG", "2");
								result2.add(result.getData(i));
							}
							else if (attr_value.equals("1"))
							{
								result.getData(i).put("PRICE", "0");
								result.getData(i).put("BIZ_NAME", "咪咕音乐普通会员");
								result.getData(i).put("BIZ_TYPE", "咪咕音乐普通会员");
								result.getData(i).put("BILL_TYPE", "0");
								result.getData(i).put("BILLFLG", "0");
							}
							else if (attr_value.equals("3"))
							{
								result.getData(i).put("PRICE", "6");
								result.getData(i).put("BIZ_NAME", "咪咕音乐特级会员");
								result.getData(i).put("BIZ_TYPE", "咪咕音乐特级会员");
								result.getData(i).put("BILL_TYPE", "2");
								result.getData(i).put("BILLFLG", "2");
								result2.add(result.getData(i));
							}
							break;
						}
					}
					continue;
				}

				if (biztypecode != null && !biztypecode.equals("") && biztypecode.equals("DX"))
				{
					IData param2 = new DataMap();
					IDataset musicattrs = new DatasetList();
					param2.put("USER_ID", userInfo.getString("USER_ID"));
					param2.put("RELA_INST_ID", instId);
					musicattrs = WithinSetDreamNetQry.selPlatAttrInfo(param2);

					for (int a = 0; a < musicattrs.size(); a++)
					{
						String attr_code = musicattrs.getData(a).getString("ATTR_CODE");
						String attr_value = musicattrs.getData(a).getString("ATTR_VALUE");

						if (attr_code.equals("8899"))
						{
							if (attr_value.equals("XSMT"))
							{
								result.getData(i).put("PRICE", "0");// 平台业务返回价格以元为单位
								result.getData(i).put("BIZ_NAME", "动感短信免费鉴赏会员业务");
								result.getData(i).put("BIZ_TYPE", "动感短信免费鉴赏会员业务");
								result.getData(i).put("BILLFLG", "0");
								result.getData(i).put("BILL_TYPE", "0");
							}
							else if (attr_value.equals("XSMTC3"))
							{
								result.getData(i).put("PRICE", "3");
								result.getData(i).put("BIZ_NAME", "动感短信普通会员业务");
								result.getData(i).put("BIZ_TYPE", "动感短信普通会员业务");
								result.getData(i).put("BILLFLG", "2");
								result.getData(i).put("BILL_TYPE", "2");
							}
							else if (attr_value.equals("XSMTC5"))
							{
								result.getData(i).put("PRICE", "5");
								result.getData(i).put("BIZ_NAME", "动感短信高级会员业务");
								result.getData(i).put("BIZ_TYPE", "动感短信高级会员业务");
								result.getData(i).put("BILLFLG", "2");
								result.getData(i).put("BILL_TYPE", "2");
							}
							break;
						}
					}
				}
				result2.add(result.getData(i));
			}
		}

		if (IDataUtil.isNotEmpty(result2)) {
			//查询用户的主产品对应的优惠
			String productId = userInfo.getString("PRODUCT_ID");
			IDataset newProductElements = UpcCall.queryOfferComRelOfferByOfferIdRelOfferType("P",productId,"D","0898");
			StringBuilder stringBuilder = new StringBuilder();
			//如果产品的构成不为空，如果构成为空就查必选组优惠
			if(IDataUtil.isNotEmpty(newProductElements)){
				for(Object temp :newProductElements){
					IData data2 = (IData)temp;
					stringBuilder.append(data2.getString("OFFER_CODE"));
					stringBuilder.append(",");
				}
			}else{
				//如果产品的构成不为空，如果构成为空就查必选组优惠
				IDataset groupList = UpcCall.queryOfferGroups(productId);
				for(Object temp :groupList){
					IData data2 = (IData)temp;
					String selectFlag = data2.getString("SELECT_FLAG");
					//如果是必选组
					if("0".equals(selectFlag)){
						IDataset offerList = UpcCall.queryGroupComRelOfferByGroupId(data2.getString("GROUP_ID"), "");
						for(Object temp2 :offerList){
							IData data3 = (IData)temp2;
							String discntCode = data3.getString("OFFER_CODE");
							IDataset userDiscntList = UserDiscntInfoQry.getAllDiscntByUser(userInfo.getString("USER_ID"), discntCode);
							if(IDataUtil.isNotEmpty(userDiscntList)){
								stringBuilder.append(discntCode);
								stringBuilder.append(",");
							}
						}
					}
				}
				
				
			}
			
			String strTemp = stringBuilder.toString();
			String feepolicyId = strTemp.substring(0, strTemp.length()-1);
			
			for (int i = 0; i < result2.size(); i++) {
				IData svcInfo = result2.getData(i);
				String spCode = svcInfo.getString("SP_ID", "");
				String bizCode = svcInfo.getString("BIZ_CODE", "");
				String serviceId = svcInfo.getString("SERVICE_ID");
				svcInfo.put("PRICE", "");
				IData acctResult = new DataMap();
				if ((StringUtils.isNotBlank(spCode) && StringUtils.isNotBlank(bizCode)) || StringUtils.isNotBlank(serviceId)) {
					acctResult = AcctCall.qrySpOrServiceRealFee(userInfo.getString("USER_ID"), spCode, bizCode, serviceId,feepolicyId);
				}
				if (IDataUtil.isNotEmpty(acctResult) ) {
					String realFee = acctResult.getString("REAL_FEE");
					svcInfo.put("PRICE", realFee);
				}
			}
		}
		return result2;

	}
	
	private IDataset selPlatOrderInfo(IData param) throws Exception
	{
		IDataset result = new DatasetList();
		
		String userId = param.getString("USER_ID");
		String productId = param.getString("PRODUCT_ID");
		String eparchyCode = param.getString("EPARCHY_CODE");
		String brandCode = param.getString("BRAND_CODE");
		String rsrvStr9 = param.getString("RSRV_STR9");
		IDataset dataset1 = WithinSetDreamNetQry.queryByIdTypeAndId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, eparchyCode);
		if(ArrayUtil.isNotEmpty(dataset1))
		{
			String offerName = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT).getOfferName();
			for(int i = 0 ; i < dataset1.size() ; i++)
			{
				IData data = dataset1.getData(i);
				IData temp = new DataMap();
				
	            temp.put("PRODUCT_MODE", "00");
				temp.put("NAME", offerName);
				temp.put("SERVICE_ID", data.getString("SERVICE_ID"));
				temp.put("TAG", data.getString("TAG"));
				temp.put("SMS_SHOW_MODE", data.getString("RSRV_STR4"));
				
				result.add(temp);
			}
		}
		
		//dataset2
		IDataset dataset2 = WithinSetDreamNetQry.queryByIdTypeAndId("B", brandCode, eparchyCode);
		if(ArrayUtil.isNotEmpty(dataset2))
		{
			String offerName = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT).getOfferName();
			for(int i = 0 ; i < dataset2.size() ; i ++)
			{
				IData data = dataset2.getData(i);
				
				IData temp = new DataMap();
				
	            temp.put("PRODUCT_MODE", "00");
				temp.put("NAME", offerName);
				temp.put("SERVICE_ID", data.getString("SERVICE_ID"));
				temp.put("TAG", data.getString("TAG"));
				temp.put("SMS_SHOW_MODE", data.getString("RSRV_STR4"));
				
				result.add(temp);
			}
		}
		
		IDataset dataset3 = WithinSetDreamNetQry.queryByDiscntCode(BofConst.ELEMENT_TYPE_CODE_DISCNT, userId, eparchyCode);
		if(ArrayUtil.isNotEmpty(dataset3))
		{
			for(int i = 0 ; i < dataset3.size() ; i++)
			{
				IData data = dataset3.getData(i);
				String discndCode = data.getString("DISCNT_CODE");
				
				IData temp = new DataMap();
				
	            temp.put("PRODUCT_MODE", "00");
				temp.put("NAME", OfferCfg.getInstance(discndCode, BofConst.ELEMENT_TYPE_CODE_DISCNT).getOfferName());
				temp.put("SERVICE_ID", data.getString("SERVICE_ID"));
				temp.put("TAG", data.getString("TAG"));
				temp.put("SMS_SHOW_MODE", data.getString("SMS_SHOW_MODE"));
				
				result.add(temp);
			}
		}
		
		IDataset dataset4 = WithinSetDreamNetQry.queryBySaleactivePackageId(userId, eparchyCode);
		result.addAll(dataset4);
		
		//查询tf_f_user_platsvc
		IDataset returnList = new DatasetList();
		IDataset userPlatSvcs = WithinSetDreamNetQry.queryPlatSvcByUserId(userId);
		if(ArrayUtil.isNotEmpty(userPlatSvcs))
		{
			for(int i = 0 ; i < userPlatSvcs.size() ; i++)
			{
				IData userPlatSvc = userPlatSvcs.getData(i);
				String serviceId = userPlatSvc.getString("SERVICE_ID");
				for(int k = 0 ; k < result.size() ; k++)
				{
					IData config = result.getData(k);
					String configSvcId = config.getString("SERVICE_ID");
					if(StringUtils.equals(serviceId, configSvcId))
					{
						//查询产商品服务
						IDataset spConfigs = new DatasetList();
						try{
							spConfigs = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null);
						}catch(Exception e){
							
						}
						if(ArrayUtil.isNotEmpty(spConfigs))
						{
							String spCode = spConfigs.getData(0).getString("SP_CODE");
							String bizCode = spConfigs.getData(0).getString("BIZ_CODE");
							String bizTypeCode = spConfigs.getData(0).getString("BIZ_TYPE_CODE");
							
							IDataset spInfos = UpcCall.qrySpServiceSpInfo(spCode, bizCode, bizTypeCode, null);
							if(ArrayUtil.isNotEmpty(spInfos))
							{
								for(int kk = 0 ; kk < spInfos.size() ; kk++)
								{
									IData spInfo = spInfos.getData(kk);
									String tagChar = "";
									if(StringUtils.equals("0", spInfo.getString("SERV_MODE")))
									{
										tagChar = "2";
									}
									if(StringUtils.equals("1", spInfo.getString("SERV_MODE")))
									{
										tagChar = "1";
									}
									
									if((StringUtils.equals("1", spInfo.getString("SERV_TYPE")) || StringUtils.equals("0", spInfo.getString("SERV_TYPE")) 
											|| StringUtils.equals("3", spInfo.getString("SERV_TYPE"))) && (StringUtils.equals("0", rsrvStr9) || StringUtils.equals(rsrvStr9, tagChar)))
									{
										IData temp = new DataMap();
										temp.put("PRODUCT_MODE", config.getString("PRODUCT_MODE"));
										temp.put("SMS_SHOW_MODE", config.getString("SMS_SHOW_MODE"));
										temp.put("NAME", config.getString("NAME"));
										temp.put("SERVICE_ID", serviceId);
										temp.put("INST_ID",userPlatSvc.getString("INST_ID"));
										temp.put("TAG", config.getString("TAG"));
										temp.put("SP_ID", spInfo.getString("SP_CODE"));
										temp.put("BIZ_CODE", spInfo.getString("BIZ_CODE"));
										//DECODE(B.SP_NAME, '注册类业务', '中国移动', B.SP_NAME) 
										temp.put("SP_NAME", spInfo.getString("SP_NAME").equals("注册类业务") ? "中国移动" : spInfo.getString("SP_NAME"));
										
										temp.put("BIZ_NAME", spConfigs.getData(0).getString("BIZ_NAME"));
										temp.put("GIFT_SERIAL_NUMBER", userPlatSvc.getString("GIFT_SERIAL_NUMBER"));
										temp.put("SP_SHORT_NAME", spInfo.getString("SP_SHORT_NAME"));
										temp.put("BIZ_TYPE", spInfo.getString("BIZ_TYPE"));
										temp.put("BIZ_TYPE_CODE", spInfo.getString("BIZ_TYPE_CODE"));
										
										String bizTypeCode1 = "";
										if(StringUtils.equals("04", spInfo.getString("BIZ_TYPE_CODE")))
										{
											bizTypeCode1 = "1";
										}else if(StringUtils.equals("05", spInfo.getString("BIZ_TYPE_CODE")))
										{
											bizTypeCode1 = "2";
										}
										if(StringUtils.equals("02", spInfo.getString("BIZ_TYPE_CODE")))
										{
											bizTypeCode1 = "3";
										}
										//decode(E.BIZ_TYPE_CODE, '04', '1', '05', '2', '02', '3') 
										temp.put("BIZ_TYPE_CODE1", bizTypeCode1);
										
										temp.put("ORG_DOMAIN", spInfo.getString("ORG_DOMAIN"));
										temp.put("START_DATE", userPlatSvc.getString("START_DATE"));
										temp.put("END_DATE", userPlatSvc.getString("END_DATE"));
										temp.put("BIZ_STATE_CODE", userPlatSvc.getString("BIZ_STATE_CODE"));
										temp.put("BILLFLG", spInfo.getString("BILL_TYPE"));
										temp.put("PRICE", spInfo.getString("PRICE"));
										temp.put("SERV_ATTR", spInfo.getString("BIZ_ATTR"));
										temp.put("SERV_CODE", spInfo.getString("SERV_CODE"));
										temp.put("CS_TEL", spInfo.getString("CS_TEL"));
										temp.put("REMARK", userPlatSvc.getString("BIZ_STATE_CODE"));
										temp.put("UPDATE_STAFF_ID", userPlatSvc.getString("UPDATE_STAFF_ID"));
										temp.put("UPDATE_DEPART_ID", userPlatSvc.getString("UPDATE_DEPART_ID"));
										temp.put("UPDATE_TIME", userPlatSvc.getString("UPDATE_TIME"));
										temp.put("PARTITION_ID", userPlatSvc.getString("PARTITION_ID"));
										temp.put("USER_ID", userPlatSvc.getString("USER_ID"));
										/*temp.put("PRODUCT_ID", userPlatSvc.getString("PRODUCT_ID"));
										temp.put("PACKAGE_ID", userPlatSvc.getString("PACKAGE_ID"));*/
										temp.put("PRODUCT_NO", spInfo.getString("PRODUCT_NO"));
										temp.put("FIRST_DATE", userPlatSvc.getString("FIRST_DATE"));
										temp.put("FIRST_DATE_MON", userPlatSvc.getString("FIRST_DATE_MON"));
										temp.put("GIFT_USER_ID", userPlatSvc.getString("GIFT_USER_ID"));
										temp.put("SERV_TYPE", spInfo.getString("SERV_TYPE"));
										temp.put("RSRV_STR2", spInfo.getString("RSRV_STR2"));
										
										//DECODE(C.SERV_MODE, '0', '2', '1', '1') 
										temp.put("TAG_CHAR", tagChar);
										
										temp.put("RSRV_STR10", spInfo.getString("SUB_SERV_MODE"));
										temp.put("SERVICE_TYPE", "Z");
										
										returnList.add(temp);
									}
								}
							}
						}
						
					}
				}
			}
		}
		
		
		return returnList;
	}
	
	private IDataset selOrderInfo(IData param) throws Exception
	{
		IDataset returnList = new DatasetList();	
		IDataset result = new DatasetList();
		
		String userId = param.getString("USER_ID");
		String productId = param.getString("PRODUCT_ID");
		String eparchyCode = param.getString("EPARCHY_CODE");
		String brandCode = param.getString("BRAND_CODE");
		String rsrvStr9 = param.getString("RSRV_STR9");
		
		IDataset dataset1 = WithinSetDreamNetQry.queryByIdTypeAndId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, eparchyCode);
		if(ArrayUtil.isNotEmpty(dataset1))
		{
			String offerName = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT).getOfferName();
			for(int i = 0 ; i < dataset1.size() ; i++)
			{
				IData data = dataset1.getData(i);
				IData temp = new DataMap();
				
	            temp.put("PRODUCT_MODE", "00");
				temp.put("NAME", offerName);
				temp.put("SERVICE_ID", data.getString("SERVICE_ID"));
				temp.put("TAG", data.getString("TAG"));
				temp.put("SMS_SHOW_MODE", data.getString("RSRV_STR4"));
				
				result.add(temp);
			}
		}
		
		//dataset2
		IDataset dataset2 = WithinSetDreamNetQry.queryByIdTypeAndId("B", brandCode, eparchyCode);
		if(ArrayUtil.isNotEmpty(dataset2))
		{
			String offerName = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT).getOfferName();
			for(int i = 0 ; i < dataset2.size() ; i ++)
			{
				IData data = dataset2.getData(i);
				
				IData temp = new DataMap();
				
	            temp.put("PRODUCT_MODE", "00");
				temp.put("NAME", offerName);
				temp.put("SERVICE_ID", data.getString("SERVICE_ID"));
				temp.put("TAG", data.getString("TAG"));
				temp.put("SMS_SHOW_MODE", data.getString("RSRV_STR4"));
				
				result.add(temp);
			}
		}
		
		IDataset dataset3 = WithinSetDreamNetQry.queryByDiscntCode(BofConst.ELEMENT_TYPE_CODE_DISCNT, userId, eparchyCode);
		if(ArrayUtil.isNotEmpty(dataset3))
		{
			for(int i = 0 ; i < dataset3.size() ; i++)
			{
				IData data = dataset3.getData(i);
				String discndCode = data.getString("DISCNT_CODE");
				
				IData temp = new DataMap();
				
	            temp.put("PRODUCT_MODE", "00");
				temp.put("NAME", OfferCfg.getInstance(discndCode, BofConst.ELEMENT_TYPE_CODE_DISCNT).getOfferName());
				temp.put("SERVICE_ID", data.getString("SERVICE_ID"));
				temp.put("TAG", data.getString("TAG"));
				temp.put("SMS_SHOW_MODE", data.getString("SMS_SHOW_MODE"));
				
				result.add(temp);
			}
		}
		
		IDataset dataset4 = WithinSetDreamNetQry.queryBySaleactivePackageId(userId, eparchyCode);
		result.addAll(dataset4);
		
		IDataset userSvcs = WithinSetDreamNetQry.querySvcByUserId(userId);
		if(ArrayUtil.isNotEmpty(userSvcs))
		{
			for(int i = 0 ; i < userSvcs.size() ; i++)
			{
				IData userSvc = userSvcs.getData(i);
				String serviceId = userSvc.getString("SERVICE_ID");
				OfferCfg offerCfg = OfferCfg.getInstance(serviceId, BofConst.ELEMENT_TYPE_CODE_SVC);
				IDataset serviceRsrvTag3 = UpcCall.queryServiceByRsrvTag3(BofConst.ELEMENT_TYPE_CODE_SVC, serviceId, "TD_B_SERVICE", "RSRV_TAG3");
				/*AND (B.RSRV_TAG3= :RSRV_STR9 OR :RSRV_STR9='0')
				   AND B.RSRV_TAG3 IS NOT NULL*/
				if(ArrayUtil.isNotEmpty(serviceRsrvTag3) && (serviceRsrvTag3.getData(0).getString("FIELD_VALUE").equals(rsrvStr9) || "0".equals(rsrvStr9)))
				{
					for(int k = 0 ; k < result.size() ; k++)
					{
						IData config = result.getData(k);
						if(StringUtils.equals(serviceId, result.getData(k).getString("SERVICE_ID")))
						{
							IData temp = new DataMap();
							temp.put("PRODUCT_MODE", config.getString("PRODUCT_MODE"));
							temp.put("SMS_SHOW_MODE", config.getString("SMS_SHOW_MODE"));
							temp.put("NAME", config.getString("NAME"));
							temp.put("SERVICE_ID", serviceId);
							temp.put("INST_ID",userSvc.getString("INST_ID"));
							temp.put("TAG", config.getString("TAG"));
							temp.put("SP_ID", "1001");
							temp.put("BIZ_CODE", "");
							temp.put("SP_NAME", "中国移动");						
							temp.put("BIZ_NAME", offerCfg.getOfferName());
							temp.put("GIFT_SERIAL_NUMBER", "");
							temp.put("SP_SHORT_NAME", "");
							temp.put("BIZ_TYPE", "");
							temp.put("BIZ_TYPE_CODE", "");
							temp.put("BIZ_TYPE_CODE1", "");						
							temp.put("ORG_DOMAIN", "");
							temp.put("OPR_SOURCE", "");
							temp.put("START_DATE", userSvc.getString("START_DATE"));
							temp.put("END_DATE", userSvc.getString("END_DATE"));
							temp.put("BIZ_STATE_CODE", "");
							temp.put("BILLFLG", "");
							temp.put("PRICE", "");
							temp.put("SERV_ATTR", "");
							temp.put("SERV_CODE", "");
							temp.put("CS_TEL", "");
							
							temp.put("REMARK", userSvc.getString("BIZ_STATE_CODE"));
							temp.put("UPDATE_STAFF_ID", userSvc.getString("UPDATE_STAFF_ID"));
							temp.put("UPDATE_DEPART_ID", userSvc.getString("UPDATE_DEPART_ID"));
							temp.put("UPDATE_TIME", userSvc.getString("UPDATE_TIME"));
							temp.put("PARTITION_ID", userSvc.getString("PARTITION_ID"));
							temp.put("USER_ID", userSvc.getString("USER_ID"));
							/*temp.put("PRODUCT_ID", userSvc.getString("PRODUCT_ID"));
							temp.put("PACKAGE_ID", userSvc.getString("PACKAGE_ID"));*/
							temp.put("SERIAL_NUMBER", "");
							temp.put("PRODUCT_NO", "");
							temp.put("FIRST_DATE", userSvc.getString("START_DATE"));
							temp.put("FIRST_DATE_MON", userSvc.getString("START_DATE"));
							temp.put("GIFT_USER_ID", "");
							temp.put("SUBSCRIBE_ID", userSvc.getString("USER_ID"));
							temp.put("SERV_TYPE", "0");
							
							temp.put("TAG_CHAR", "");
							
							temp.put("RSRV_STR2", "");
							temp.put("SERVICE_TYPE", "S");
							returnList.add(temp);
						}
					}
				}
			}
		}
			
		return returnList;
	}
	/**
	 * 
	 * @description 查询增值业务推荐数据接口
	 * @param @param data
	 * @param @return
	 * @param @throws Exception
	 * @return IDataset
	 * @author tanzheng
	 * @date 2019年7月15日
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private Logger logger = Logger.getLogger(WithinSetDreamNetQrySVC.class);
	public IData qryAdviceBusi(IData data) throws Exception
	{
		String serialNumber = data.getString("SERIAL_NUMBER");
		UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
		IDataset result = new DatasetList();
		IData param = new DataMap();
		/*param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "1235");
		IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);*/
		//修改成调用iop接口
		param.put("mobilePhone",serialNumber);
		String jsonStr = HttpUtil.sendPost("AdviceBusiness",param.toString());
		logger.debug("调用iop返回结果："+jsonStr);
		IData jsonData = new DataMap(jsonStr);
		IDataset dataset = new DatasetList();
		if(!"-1".equals(jsonData.getString("X_RESULT_CODE"))){
			dataset = jsonData.getDataset("RESULT_LIST");
			int size = dataset.size();
			//用户已经办理的增值服务不做返回
			for(int i=0;i<size;i++){
				IData tempData = dataset.getData(i);

				IDataset userPlatSvcDataset = UserPlatSvcInfoQry.qryPlatSvcByUserIdServiceId(ucaData.getUserId(), tempData.getString("SERVICE_ID"));
				if(IDataUtil.isNotEmpty(userPlatSvcDataset)){
					dataset.remove(i);
					size--;
				}
			}
		}
//		potting(result,dataset);
		IData resultData = new DataMap();
		resultData.put("X_RESULT_CODE", "0000");
		resultData.put("X_RESULT_MSG", "查询成功！");
		resultData.put("RESULT_LIST", dataset);
		return resultData;	
		
	}

	/**
	 * @description
	 * @param @param result
	 * @param @param dataset
	 * @return void
	 * @author tanzheng
	 * @date 2019年7月15日
	 * @param result
	 * @param dataset
	 */
	private void potting(IDataset result, IDataset dataset) {
		if(IDataUtil.isEmpty(dataset)){
			logger.error("advice setting is empty!");
		}else{
			for(int i=0;i<dataset.size();i++){
				IData data = dataset.getData(i);
				IData resData = new DataMap();
				resData.put("SERVICE_ID", data.getString("PARAM_CODE"));
				resData.put("SERVICE_NAME", data.getString("PARAM_NAME"));
				resData.put("SP_CODE", data.getString("PARA_CODE1"));
				resData.put("BIZ_CODE", data.getString("PARA_CODE2"));
				resData.put("BIZ_TYPE_CODE", data.getString("PARA_CODE3"));
				resData.put("ORDER_NO", data.getString("PARA_CODE4"));
				resData.put("PRICE", data.getString("PARA_CODE5"));
				result.add(resData);
			}
		}
	}
	
	public IData twoCheck(IData input) throws Exception {
		
		String serviceIdStr = input.getString("SERVICE_ID");
		String spCode = input.getString("SP_CODE");
		String bizCode = input.getString("BIZ_CODE");
		String bizTypeCode = input.getString("BIZ_TYPE_CODE");
		String[] serviceIdArry = serviceIdStr.split(",");
		String[] spCodeArry = spCode.split(",");
		String[] bizCodeArry = bizCode.split(",");
		String[] bizTypeCodeArry = bizTypeCode.split(",");
		String serialNumber = input.getString("SERIAL_NUMBER");
		String result = "";
		for(int i = 0 ;i < serviceIdArry.length ; i++ ){
			String serviceId = serviceIdArry[i];
			IData param = new DataMap();
			param.put("SUBSYS_CODE", "CSM");
			param.put("PARAM_ATTR", "1235");
			param.put("PARAM_CODE", serviceId);
			IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
			String smsContent = null;
			if(IDataUtil.isNotEmpty(dataset)){
				smsContent = dataset.first().getString("PARA_CODE20");
			}
			
			if(StringUtils.isEmpty(smsContent)){
				logger.error(serialNumber+" order "+"serviceId fail because sms is empty!");
				continue;
			}
			
			IData sendInfo = new DataMap();
			sendInfo.put("REMARK", "0000订购增值服务");
			sendInfo.put("SERIAL_NUMBER", serialNumber);
			sendInfo.put("SMS_CONTENT", smsContent);
			sendInfo.put("SMS_TYPE",BofConst.ORDER_PLAT);
			sendInfo.put("OPR_SOURCE", "1");
			
			// 插二次短信表
			IData preOderData = new DataMap();
			preOderData.put("SVC_NAME", "SS.PlatRegSVC.tradeRegIntf");
			preOderData.put("PRE_TYPE",BofConst.ORDER_PLAT);
			preOderData.put("SERIAL_NUMBER", serialNumber);
			preOderData.put("OPER_CODE", PlatConstants.OPER_ORDER);
			preOderData.put("BIZ_TYPE_CODE", bizTypeCodeArry[i]);
			preOderData.put("SP_CODE", spCodeArry[i]);
			preOderData.put("BIZ_CODE", bizCodeArry[i]);
			
//        preOderData.put("IN_MODE_CODE", getVisit().getInModeCode());// 接入编码
//        preOderData.put("TRADE_CITY_CODE", getVisit().getCityCode());// 工号所在地州
//        preOderData.put("TRADE_DEPART_ID", getVisit().getDepartId());// 操作部门
//        preOderData.put("TRADE_STAFF_ID", getVisit().getStaffId());// 操作工号
//        preOderData.put("BIZ_TYPE", "74");// 业务类型代码 74-国内一卡多号业务
//        preOderData.put("CHANNEL_ID", "04");// 受理渠道  01-WEB， 03-WAP，04-SMS，70-客户端
//        preOderData.put("CATEGORY", input.getString("CATEGORY"));// 副号码类型 0：虚拟副号码；1：实体副号码
			
			result += TwoCheckSms.twoCheck("-1", 2, preOderData, sendInfo).getString("REQUEST_ID")+",";
		}
		IData dataMap = new DataMap();
		if(StringUtils.isNotBlank(result)){
			result.substring(0, result.length()-1);
		}
		dataMap.put("REQUEST_ID", result);
		dataMap.put("X_RESULT_CODE", "0000");
		dataMap.put("X_RESULT_MSG", "操作成功！");
		return dataMap;
	}
}
