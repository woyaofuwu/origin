package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import java.io.PrintWriter;
import org.apache.log4j.Logger;
import java.io.StringWriter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean.BatDealBBossCloudMasMebBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * @description 用于处理行业网关云MAS业务
 * @author chenkh
 * @date 2015年4月9日
 */
public class BbossIAGWCloudMASDealBean extends CSBizService {
	/**
     *
     */
	private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BbossIAGWCloudMASDealBean.class);

	/**
	 * @Title: isIAGWCloudMAS
	 * @Description: 判断是否为行业网关云MAS业务
	 * @param productId
	 * @return
	 * @throws Exception
	 * @return boolean
	 * @author chenkh
	 * @time 2015年4月9日
	 */
	public static boolean isIAGWCloudMAS(String productId) throws Exception {
		boolean isCloudMAS = false;

		IData isSendCloudMas = StaticInfoQry.getStaticInfoByTypeIdDataId(
				"BBOSS_SENDTOIAGW_CLOUDMAS", productId);

		if (IDataUtil.isNotEmpty(isSendCloudMas)) {
			isCloudMAS = true;
		}

		return isCloudMAS;
	}

	/**
	 * @Title: isSMSorMMS
	 * @Description: 判断MAS的类型，是短信还是彩信。
	 * @param product_spec_code
	 * @return
	 * @throws Exception
	 * @return String
	 * @author chenkh
	 * @time 2015年5月4日
	 */
	public static String isSMSorMMS(String product_spec_code) throws Exception {
		if ("110156".equals(product_spec_code)
				|| "110159".equals(product_spec_code)
				|| "110162".equals(product_spec_code)) {
			return "MMS";
		} else {
			return "SMS";
		}
	}

	public static String getBizInCodeA(String product_spec_code)
			throws Exception {
		if ("110156".equals(product_spec_code)
				|| "110159".equals(product_spec_code)
				|| "110162".equals(product_spec_code)) {
			return "02";
		} else {
			return "01";
		}
	}

	/**
	 * @Title: getServiceIdByProductId
	 * @Description: 根据不同的产品来取得不同的服务id(成员用)
	 * @param productId
	 * @return
	 * @throws Exception
	 * @return String
	 * @author chenkh
	 * @time 2015年7月16日
	 */
	public static String getServiceIdByProductId(String productId)
			throws Exception {
		String productSpecCode = GrpCommonBean.productToMerch(productId, 0);
		if ("110154".equals(productSpecCode)
				|| "110157".equals(productSpecCode)
				|| "110160".equals(productSpecCode)) {
			return "9999111";
		} else if ("110155".equals(productSpecCode)
				|| "110158".equals(productSpecCode)
				|| "110161".equals(productSpecCode)) {
			return "9999131";
		} else if ("110156".equals(productSpecCode)
				|| "110159".equals(productSpecCode)
				|| "110162".equals(productSpecCode)) {
			return "9999121";
		}

		return "";
	}

	/**
	 * @Title: makDataForBlackWhite
	 * @Description: 准备黑白名单表数据
	 * @param userId
	 * @param grpUserId
	 * @param productId
	 * @param serialNumber
	 * @param operState
	 * @return
	 * @throws Exception
	 * @return IData
	 * @author chenkh
	 * @time 2015年7月16日
	 */
	public static IData makDataForBlackWhite(String userId, String grpUserId,
			String productId, String serialNumber, String operState)
			throws Exception {
		IData blackWhiteData = new DataMap();
		String servCode = getServCodeByUserId(grpUserId, productId);
		String bizCode = getBizCodeByUserIdAndProduct(grpUserId, productId);
		String bizInCodeA = getBizInCodeA(GrpCommonBean.productToMerch(
				productId, 0));
		String startTime = SysDateMgr
				.getSysDate(SysDateMgr.PATTERN_STAND_SHORT);
		IData grpUserInfo = UcaInfoQry.qryUserInfoByUserIdForGrp(grpUserId);
		String groupId = UcaInfoQry.qryCustInfoByCustId(
				grpUserInfo.getString("CUST_ID")).getString("GROUP_ID");

		blackWhiteData.put("USER_ID", userId);
		blackWhiteData.put("USER_TYPE_CODE", "QB");
		blackWhiteData.put("SERVICE_ID", getServiceIdByProductId(productId));
		blackWhiteData.put("BIZ_IN_CODE", servCode);
		blackWhiteData.put("BIZ_IN_CODE_A", bizInCodeA);
		blackWhiteData.put("EC_USER_ID", grpUserId);
		blackWhiteData.put("EC_SERIAL_NUMBER",
				grpUserInfo.getString("SERIAL_NUMBER"));
		blackWhiteData.put("SERV_CODE", servCode);
		blackWhiteData.put("SERIAL_NUMBER", serialNumber);
		blackWhiteData.put("GROUP_ID", groupId);
		blackWhiteData.put("BIZ_CODE", bizCode);
		blackWhiteData.put("BIZ_NAME", "行业网关云MAS");
		blackWhiteData.put("OPER_STATE", operState);
		blackWhiteData.put("START_DATE", startTime);
		blackWhiteData.put("END_DATE", SysDateMgr.getTheLastTime());
		if ("01".equals(operState)) {
			blackWhiteData.put("MODIFY_TAG", "0");
			blackWhiteData.put("INST_ID", SeqMgr.getInstId());
		} else if ("02".equals(operState)) {
			blackWhiteData.put("MODIFY_TAG", "1");
			IDataset memberList = UserBlackWhiteInfoQry
					.qryCloudMASListByUserIdServCodeSN(grpUserId, serialNumber,
							servCode);
			if (IDataUtil.isNotEmpty(memberList)) {
				blackWhiteData.put("INST_ID",
						memberList.getData(0).getString("INST_ID"));
			} else {
				blackWhiteData.put("INST_ID", SeqMgr.getInstId());
			}
			blackWhiteData.put("END_DATE", SysDateMgr.getSysTime());
		}
		blackWhiteData.put("UPDATE_TIME", SysDateMgr.getSysTime());

		return blackWhiteData;
	}

	/**
	 * @Title: makDataForGrpPlatSVC
	 * @Description: 行业网关云MAS需要写入TF_B_TRADE_GRP_PLATSVC表，此方法用于准备数据
	 * @param userid
	 * @param groupid
	 * @param operState
	 * @param product_spec_code
	 * @param productParamInfos
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 * @return IData
	 * @author chenkh
	 * @time 2015年4月9日
	 */
	public static IData makDataForGrpPlatSVC(String userid, String groupid,
			String operState, String product_spec_code,
			IData productParamInfos, String serialNumber) throws Exception {
		IData grpPlatSVCData = new DataMap();
		// 1 将共用的属性放入Map中
		String servCode = "";
		String bizCode = "";
		String engSign = "";
		String chnSign = "";
		String defaultEcgnLang = "";
		String isTextSign = "";
		String serviceId = "";
		String adminNum = "";
		String modifyTag = "2";
		String bizAttr = "1";
		String ecCode = "";
		String ecBaseInCodeA = "01";
		String authCode = "0";
		String dayMaxItem = "0";
		String monMaxItem = "0";
		String authCodeVal = "0";// 是否支持模糊匹配
		String forbidStartTime_a = "";
		String forbidEndTime_a = "";
		String forbidStartTime_b = "";
		String forbidEndTime_b = "";
		String forbidStartTime_c = "";
		String forbidEndTime_c = "";
		String forbidStartTime_d = "";
		String forbidEndTime_d = "";
		IDataset groupInfos = GrpInfoQry.qryGrpInfoByGroupIdAndRemoveTag(
				groupid, "0");
        String serv_type=productParamInfos.getString(product_spec_code+"8801","51");//业务类型  ：51 内部管理类；52 外部服务类；53 营销推广类；54 公益类
        String confirmFlag=productParamInfos.getString(product_spec_code+"8802","1");//白名单二次确认管理方式  0：是  1：否'
		if (groupInfos != null && groupInfos.size() > 0) {
			if ("110154".equals(product_spec_code)
					//|| "110157".equals(product_spec_code)
					|| "110160".equals(product_spec_code)) {
				ecCode = groupInfos.getData(0).getString("RSRV_STR3", "");
			} else if ("110155".equals(product_spec_code)
					|| "110158".equals(product_spec_code)
					|| "110161".equals(product_spec_code)) {
				ecCode = groupInfos.getData(0).getString("RSRV_STR3", "");
			} else if ("110156".equals(product_spec_code)
					//|| "110159".equals(product_spec_code)
					|| "110162".equals(product_spec_code)) {
				ecCode = groupInfos.getData(0).getString("RSRV_STR8", "");
			}else if ("110157".equals(product_spec_code)) {//省行业网关短流程云MAS需求要求写死企业代码
				ecCode = "400527";
				servCode = productParamInfos.getString(product_spec_code + "4010");// 服务代码或者彩信接入号
				if ("0".equals(productParamInfos.getString(product_spec_code + "4011"))){
					/*if(servCode.startsWith("106509652")){
						ecCode = "400527";
					}else */if(servCode.startsWith("106509022")){
						ecCode = "400528";
					}else if(servCode.startsWith("106509622")){
						ecCode = "400536";
					}else if(servCode.startsWith("106509052")){
						ecCode = "400941";
					}else{
						
					}
				}else{
					ecCode = "400529";
					if(servCode.startsWith("106509752")){
						ecCode = "400529";
					}else if(servCode.startsWith("106509122")){
						ecCode = "400530";
					}else if(servCode.startsWith("106509722")){
						ecCode = "400567";
					}else if(servCode.startsWith("106509152")){
						ecCode = "400942";
					}else{
						
					}
				}
			}else if ("110159".equals(product_spec_code)) {//省行业网关短流程云MAS需求要求写死企业代码
				ecCode = "400527";
				/*servCode = productParamInfos.getString(product_spec_code + "4010");// 服务代码或者彩信接入号
				if(servCode.startsWith("106509652")){
					ecCode = "400527";
				}else if(servCode.startsWith("106509022")){
					ecCode = "400528";
				}else if(servCode.startsWith("106509622")){
					ecCode = "400536";
				}else if(servCode.startsWith("106509052")){
					ecCode = "400941";
				}else{
					
				}*/
			}
		} else {
			CSAppException.apperr(GrpException.CRM_GRP_713, "取得集团客户企业代码失败！");
		}
		if ("110154".equals(product_spec_code)
				|| "110155".equals(product_spec_code)
				|| "110156".equals(product_spec_code)) {
			if ("110154".equals(product_spec_code)) {
				serviceId = "999911";
			} else if ("110155".equals(product_spec_code)) {
				serviceId = "999913";
			} else if ("110156".equals(product_spec_code)) {
				serviceId = "999912";
				ecBaseInCodeA = "02";

			}
			servCode = productParamInfos.getString(product_spec_code + "4006");// 服务代码或者彩信接入号
			adminNum = "13111111111";
			defaultEcgnLang = "中文".equals(productParamInfos.getString(product_spec_code
					+ "4003")) ? "1" : "2";// 缺省签名语言
			chnSign = productParamInfos.getString(product_spec_code + "4004",
					"云MAS业务");// 中文短信正文签名
			engSign = productParamInfos.getString(product_spec_code + "4005",
					"cloudMas");// 英文短信正文签名
			bizCode = productParamInfos.getString(product_spec_code + "4007");// 业务代码
			bizAttr = "0".equals(productParamInfos.getString(product_spec_code + "4008")) ? "2"
					: "1";// 名单类型
			isTextSign = "1".equals(productParamInfos
					.getString(product_spec_code + "4012"))? "1" : "0";// 是否支持短信/彩信正文签名
			dayMaxItem = productParamInfos.getString(
					product_spec_code + "4025", "0");// 每日最大下发条数(0表示不限制)
			monMaxItem = productParamInfos.getString(
					product_spec_code + "4026", "0");// 每月下发的最大条数(0表示不限制)
			IDataset tempdData = UserAttrInfoQry.getUserAttrByUserId(userid,
					"1101630017");// 是否支持模糊匹配
			if (IDataUtil.isNotEmpty(tempdData)) {
				authCodeVal = tempdData.getData(0).getString("ATTR_VALUE");

			}
			authCode = "是".equals(authCodeVal) ? "1" : "0";
			forbidStartTime_a = productParamInfos.getString(product_spec_code
					+ "4027" + "1", "");// 不允许下发开始时间
			forbidEndTime_a = productParamInfos.getString(product_spec_code
					+ "4028" + "1", "");// 不允许下发结束时间
			forbidStartTime_b = productParamInfos.getString(product_spec_code
					+ "4027" + "2", "");
			forbidEndTime_b = productParamInfos.getString(product_spec_code
					+ "4028" + "2", "");
			forbidStartTime_c = productParamInfos.getString(product_spec_code
					+ "4027" + "3", "");
			forbidEndTime_c = productParamInfos.getString(product_spec_code
					+ "4028" + "3", "");
			forbidStartTime_d = productParamInfos.getString(product_spec_code
					+ "4027" + "4", "");
			forbidEndTime_d = productParamInfos.getString(product_spec_code
					+ "4028" + "4", "");
			if (StringUtils.isNotEmpty(forbidStartTime_a)
					&& StringUtils.isNotEmpty(forbidEndTime_a)) {
				if (StringUtils.isNotEmpty(forbidStartTime_b)
						&& StringUtils.isNotEmpty(forbidEndTime_b)) {
					forbidStartTime_a = forbidStartTime_a + ","
							+ forbidStartTime_b;
					forbidEndTime_a = forbidEndTime_a + "," + forbidEndTime_b;
				}
				if (StringUtils.isNotEmpty(forbidStartTime_c)
						&& StringUtils.isNotEmpty(forbidEndTime_c)) {
					forbidStartTime_a = forbidStartTime_a + ","
							+ forbidStartTime_c;
					forbidEndTime_a = forbidEndTime_a + "," + forbidEndTime_c;
				}
				if (StringUtils.isNotEmpty(forbidStartTime_d)
						&& StringUtils.isNotEmpty(forbidEndTime_d)) {
					forbidStartTime_a = forbidStartTime_a + ","
							+ forbidStartTime_d;
					forbidEndTime_a = forbidEndTime_a + "," + forbidEndTime_d;
				}

			}
		}
		if ("110157".equals(product_spec_code)
				|| "110158".equals(product_spec_code)
				|| "110159".equals(product_spec_code)
				|| "110160".equals(product_spec_code)
				|| "110161".equals(product_spec_code)
				|| "110162".equals(product_spec_code)) {
			if ("110157".equals(product_spec_code)
					|| "110160".equals(product_spec_code)) {
				serviceId = "999911";

			} else if ("110158".equals(product_spec_code)
					|| "110161".equals(product_spec_code)) {
				serviceId = "999913";
			} else if ("110159".equals(product_spec_code)
					|| "110162".equals(product_spec_code)) {
				serviceId = "999912";
				ecBaseInCodeA = "02";
				

			}
			servCode = productParamInfos.getString(product_spec_code + "4002");// 服务代码或者彩信接入号
			adminNum = productParamInfos.getString(product_spec_code + "4004",
					"13111111111");// 管理员手机号码
			defaultEcgnLang = "中文".equals(productParamInfos.getString(product_spec_code
					+ "4007"))  ? "1" : "2";// 缺省签名语言
			chnSign = productParamInfos.getString(product_spec_code + "4008",
					"云MAS业务");// 中文短信正文签名
			engSign = productParamInfos.getString(product_spec_code + "4009",
					"cloudMas");// 英文短信正文签名
			bizCode = productParamInfos.getString(product_spec_code + "4010");// 业务代码
			bizAttr = "0".equals(productParamInfos.getString(product_spec_code + "4011")) ? "2"
					: "1";// 名单类型
			isTextSign = "1".equals(productParamInfos
					.getString(product_spec_code + "4014")) ? "1" : "0";// 是否支持短信/彩信正文签名
			dayMaxItem = productParamInfos.getString(
					product_spec_code + "4025", "0");// 每日最大下发条数(0表示不限制)
			monMaxItem = productParamInfos.getString(
					product_spec_code + "4026", "0");// 每月下发的最大条数(0表示不限制)
			IDataset tempdData = UserAttrInfoQry.getUserAttrByUserId(userid,
					"1101644003");// 是否支持模糊匹配
			if (IDataUtil.isNotEmpty(tempdData)) {
				authCodeVal = tempdData.getData(0).getString("ATTR_VALUE");

			}
			authCode = "是".equals(authCodeVal)? "1" : "0";
			forbidStartTime_a = productParamInfos.getString(product_spec_code
					+ "4027" + "1", "");// 不允许下发开始时间
			forbidEndTime_a = productParamInfos.getString(product_spec_code
					+ "4028" + "1", "");// 不允许下发结束时间
			forbidStartTime_b = productParamInfos.getString(product_spec_code
					+ "4027" + "2", "");
			forbidEndTime_b = productParamInfos.getString(product_spec_code
					+ "4028" + "2", "");
			forbidStartTime_c = productParamInfos.getString(product_spec_code
					+ "4027" + "3", "");
			forbidEndTime_c = productParamInfos.getString(product_spec_code
					+ "4028" + "3", "");
			forbidStartTime_d = productParamInfos.getString(product_spec_code
					+ "4027" + "4", "");
			forbidEndTime_d = productParamInfos.getString(product_spec_code
					+ "4028" + "4", "");
			if (StringUtils.isNotEmpty(forbidStartTime_a)
					&& StringUtils.isNotEmpty(forbidEndTime_a)) {
				if (StringUtils.isNotEmpty(forbidStartTime_b)
						&& StringUtils.isNotEmpty(forbidEndTime_b)) {
					forbidStartTime_a = forbidStartTime_a + ","
							+ forbidStartTime_b;
					forbidEndTime_a = forbidEndTime_a + "," + forbidEndTime_b;
				}
				if (StringUtils.isNotEmpty(forbidStartTime_c)
						&& StringUtils.isNotEmpty(forbidEndTime_c)) {
					forbidStartTime_a = forbidStartTime_a + ","
							+ forbidStartTime_c;
					forbidEndTime_a = forbidEndTime_a + "," + forbidEndTime_c;
				}
				if (StringUtils.isNotEmpty(forbidStartTime_d)
						&& StringUtils.isNotEmpty(forbidEndTime_d)) {
					forbidStartTime_a = forbidStartTime_a + ","
							+ forbidStartTime_d;
					forbidEndTime_a = forbidEndTime_a + "," + forbidEndTime_d;
				}

			}
		}

		// 3 根据不同的operState确定MODIFY_TAG的值（暂时不知道服开那边有没有用起来，但先处理了）
		if ("01".equals(operState)) {
			modifyTag = "0";
		} else if ("02".equals(operState)) {
			modifyTag = "1";
		} else if ("08".equals(operState)) {
			modifyTag = "2";
		}
		
		//BUG20180529104142 关于云MAS业务订购同步网关遗漏SI信息的优化  SI_BASE_IN_CODE处应该填写SI的代码，字段值为ACCESS_NUMBER字段的前9位
		String serviceIds = "9999111_9999121_9999131_999911_999912_999913";
		String siBaseInCode = "";
		String siBaseInCodeA = "";
		if(serviceIds.contains(serviceId)&&servCode.length()>=9){
			siBaseInCode = servCode.substring(0,9);
			if(serviceId.equals("999911")||serviceId.equals("9999111")){
				siBaseInCodeA = "01";//短信
			}
			if(serviceId.equals("999912")||serviceId.equals("9999121")){
				siBaseInCodeA = "02";//彩信
			}
			if(serviceId.equals("999913")||serviceId.equals("9999131")){
				siBaseInCodeA = "03";//网信
			}
		}
	
		grpPlatSVCData.put("INST_ID", SeqMgr.getInstId());

		// 非新增业务INST_ID取资料表的数据
		if (!"01".equals(operState)) {
			IData platsvcparam = UserGrpPlatSvcInfoQry
					.getuserPlatsvcbyserverid(userid, serviceId);// 取平台服务表已经存在的参数
			grpPlatSVCData.put("INST_ID", platsvcparam.getString("INST_ID"));
		}
		grpPlatSVCData.put("USER_ID", userid);
		grpPlatSVCData.put("SERIAL_NUMBER", serialNumber);
		grpPlatSVCData.put("GROUP_ID", groupid);
		grpPlatSVCData.put("CS_TEL", "");
		grpPlatSVCData.put("ACCESS_NUMBER", servCode);
		grpPlatSVCData.put("BIZ_STATE_CODE", "A");
		grpPlatSVCData.put("BIZ_CODE", bizCode);
		grpPlatSVCData.put("BIZ_NAME", "省行业网关云MAS");
		grpPlatSVCData.put("SERV_CODE", servCode);
		grpPlatSVCData.put("BILLING_TYPE", "00");
		grpPlatSVCData.put("ACCESS_MODE", "01");// 不送网关
		grpPlatSVCData.put("BIZ_STATUS", "A");
		grpPlatSVCData.put("BIZ_ATTR", bizAttr);
		grpPlatSVCData.put("PRICE", "0");
		grpPlatSVCData.put("BIZ_TYPE_CODE", "004");
		grpPlatSVCData.put("USAGE_DESC", "");// 非必填
		grpPlatSVCData.put("BIZ_PRI", "01");
		grpPlatSVCData.put("INTRO_URL", "");// 非必填
		grpPlatSVCData.put("CS_URL", "");// 非必填
		grpPlatSVCData.put("PRE_CHARGE", "0");
		grpPlatSVCData.put("MAX_ITEM_PRE_DAY", dayMaxItem);
		grpPlatSVCData.put("IS_TEXT_ECGN", isTextSign);
		grpPlatSVCData.put("MAX_ITEM_PRE_MON", monMaxItem);
		grpPlatSVCData.put("DEFAULT_ECGN_LANG", defaultEcgnLang);
		grpPlatSVCData.put("TEXT_ECGN_EN", engSign);
		grpPlatSVCData.put("ADMIN_NUM", adminNum);
		grpPlatSVCData.put("TEXT_ECGN_ZH", chnSign);
		grpPlatSVCData.put("AUTH_CODE", authCode);// 目前取值有问题
		grpPlatSVCData.put("FORBID_START_TIME_A", forbidStartTime_a);
		grpPlatSVCData.put("FORBID_END_TIME_A", forbidEndTime_a);
		grpPlatSVCData.put("FORBID_START_TIME_B", forbidStartTime_b);
		grpPlatSVCData.put("FORBID_END_TIME_B", forbidEndTime_b);
		grpPlatSVCData.put("FORBID_START_TIME_C", forbidStartTime_c);
		grpPlatSVCData.put("FORBID_END_TIME_C", forbidEndTime_c);
		grpPlatSVCData.put("FORBID_START_TIME_D", forbidStartTime_d);
		grpPlatSVCData.put("FORBID_END_TIME_D", forbidEndTime_d);
		grpPlatSVCData.put("FIRST_DATE", SysDateMgr.getSysTime());
		grpPlatSVCData.put("START_DATE", SysDateMgr.getSysTime());
        grpPlatSVCData.put("SERV_TYPE", "内部管理类".equals(serv_type) ? "51" : 
    		"外部服务类".equals(serv_type) ? "52":"营销推广类".equals(serv_type) ? "53":"54");
        grpPlatSVCData.put("CONFIRMFLAG", "是".equals(confirmFlag) ? "0" : "1");
		if ("02".equals(operState)) {
			grpPlatSVCData.put("END_DATE", SysDateMgr.getSysTime());
		} else {
			grpPlatSVCData.put("END_DATE", SysDateMgr.getTheLastTime());
		}
		grpPlatSVCData.put("REMARK", "");// 非必填
		grpPlatSVCData.put("OPR_EFF_TIME", SysDateMgr.getSysTime());
		grpPlatSVCData.put("RSRV_STR1", "");// 短信上行码
		grpPlatSVCData.put("RSRV_STR4", ecCode);
		grpPlatSVCData.put("RSRV_TAG2", "1");// 集团客户等级
		grpPlatSVCData.put("RSRV_TAG3", "P");
		grpPlatSVCData.put("IS_NEED_PF", "1");
		grpPlatSVCData.put("OPER_STATE", operState);
		grpPlatSVCData.put("BIZ_IN_CODE", servCode);
		grpPlatSVCData.put("SI_BASE_IN_CODE", siBaseInCode);
		grpPlatSVCData.put("SI_BASE_IN_CODE_A", siBaseInCodeA);
		grpPlatSVCData.put("SERVICE_ID", serviceId);
		grpPlatSVCData.put("EC_BASE_IN_CODE", servCode);
		grpPlatSVCData.put("EC_BASE_IN_CODE_A", ecBaseInCodeA);

		grpPlatSVCData.put("PLAT_SYNC_STATE", "1");
		grpPlatSVCData.put("BILLING_MODE", "1");
		grpPlatSVCData.put("DELIVER_NUM", "0");
		grpPlatSVCData.put("MODIFY_TAG", modifyTag);
		return grpPlatSVCData;
	}
	
	 /**
     * @Title: isCloudMAS
     * @Description: 判断是否为云MAS业务
     * @param productId
     * @return
     * @throws Exception
     * @return boolean
     * @author chenkh
     * @time 2015年4月9日
     */
    public static boolean isCloudMAS(String productId) throws Exception
    {
        boolean isCloudMAS = false;
        
        IData isSendCloudMas = StaticInfoQry.getStaticInfoByTypeIdDataId("BBOSS_CLOUDMAS_PROD", productId);
        
        if (IDataUtil.isNotEmpty(isSendCloudMas))
        {
            isCloudMAS = true;
        }
        
        return isCloudMAS;
    }

    public static boolean isIAGWTOSIMS(String productId) throws Exception
    {
        boolean isSIMS = false;
        
        IData isSendSims = StaticInfoQry.getStaticInfoByTypeIdDataId("BBOSS_IAGWTOSIMS", productId);
        
        if (IDataUtil.isNotEmpty(isSendSims))
        {
        	isSIMS = true;
        }
        
        return isSIMS;
    }
	/**
	 * @Title: isBlackWhiteExist
	 * @Description: 查询黑白名单表是否存在信息，存在不需要发省行业网关
	 * @param serialNumber
	 * @param userId
	 * @param servCode
	 * @return
	 * @throws Exception
	 * @return boolean
	 * @author chenkh
	 * @time 2015年4月13日
	 */
	public static boolean isBlackWhiteExist(String serialNumber, String userId,
			String servCode) throws Exception {
		IDataset memberList = UserBlackWhiteInfoQry
				.qryCloudMASListByUserIdServCodeSN(userId, serialNumber,
						servCode);
		if (IDataUtil.isNotEmpty(memberList)) {
			return true;
		}

		return false;
	}

	/**
	 * @Title: getServCodeByUserId
	 * @Description: 取得省行业网关云MAS服务代码
	 * @param userId
	 * @param productId
	 * @return
	 * @throws Exception
	 * @return String
	 * @author chenkh
	 * @time 2015年4月14日
	 */
	public static String getServCodeByUserId(String userId, String productId)
			throws Exception {
		String merchId = GrpCommonBean.productToMerch(productId, 0);
		if ("110154".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101544006");
			if (IDataUtil.isNotEmpty(attrInfo)) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110155".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101554006");
			if (IDataUtil.isNotEmpty(attrInfo)) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110156".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101564006");
			if (IDataUtil.isNotEmpty(attrInfo)) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110157".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101574002");
			if (IDataUtil.isNotEmpty(attrInfo)) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110158".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101584002");
			if (IDataUtil.isNotEmpty(attrInfo)) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110159".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101594002");
			if (IDataUtil.isNotEmpty(attrInfo)) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110160".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101604002");
			if (IDataUtil.isNotEmpty(attrInfo)) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110161".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101614002");
			if (IDataUtil.isNotEmpty(attrInfo)) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110162".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101624002");
			if (IDataUtil.isNotEmpty(attrInfo)) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}

		return "";
	}

	/**
	 * @Title: isBBossCloudMasFromIntf
	 * @Description: 判断从ADC/MAS接口过来的数据是不是bboss行业网关云MAS业务
	 * @return
	 * @throws Exception
	 * @return boolean
	 * @author chenkh
	 * @time 2015年4月14日
	 */
	public static boolean isBBossCloudMasFromIntf(IData inparam)
			throws Exception {
		// 1- 先判断报文体是否符合,不符合直接返回
		String busiSign = inparam.getString("BUSI_SIGN", "");
		if (!IntfField.SubTransCode.IAGWGrpMemBiz.value.equals(busiSign)) {
			return false;
		}

		// 2-
		// 再根据串来找集团user_id，进而查询user_product表，通过user_id对应的product_id是不是省行业网关云MAS
		String bizCode = inparam.getString("BIZ_CODE", "");
		String servCode = inparam.getString("SERV_CODE", "");
		if (StringUtils.isBlank(bizCode)) {
			return false;
		}
		if (StringUtils.isBlank(servCode)) {
			return false;
		}

		IDataset serDatas = UserGrpPlatSvcInfoQry
				.getuserPlatsvcbybizcodeservcode(bizCode, servCode);

		if (IDataUtil.isEmpty(serDatas)) {
			return false;
		}
		IData pltSvc = serDatas.getData(0);
		String userId = pltSvc.getString("USER_ID");
		IData grpUserData = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId,
				"0"); // 查询集团用户信息
		if (IDataUtil.isEmpty(grpUserData)) {
			return false;
		}

		String grpProductId = GrpCommonBean.productToMerch(
				grpUserData.getString("PRODUCT_ID", ""), 0);
		if (StringUtils.isEmpty(grpProductId) || !isIAGWCloudMAS(grpProductId)) {
			return false;
		}

		// 如果是行业网关云MAS业务，需要存入几个数据
		inparam.put("GROUP_ID", pltSvc.getString("GROUP_ID"));
		inparam.put("PRODUCT_ID", grpUserData.getString("PRODUCT_ID", ""));
		inparam.put("USER_ID", userId);
		return true;
	}

	/**
	 * @Title: dealBbossCloudMasIntf
	 * @Description: 处理接口数据，建立成员批量
	 * @param data
	 * @return
	 * @throws Exception
	 * @return IDataset
	 * @author chenkh
	 * @time 2015年4月14日
	 */
	public static IDataset dealBbossCloudMasIntf(IData data) throws Exception {
		// 准备返回数据
		IDataset resultSet = new DatasetList();
		IData result_data = new DataMap();
		IData retData = new DataMap();

		try {
			// 1- 生成批量数据
			retData = BatDealBBossCloudMasMebBean.createBatForCloudMas(data);

			// 2- 批量启动
			CSAppCall.call("SS.BatDealBBossBeanSvc.startBatDealBBossMember",retData);

			// 3- 登记黑白名单表
			regBlackWhiteForCloudMas(data);
		} catch (Exception e) {
			IData resErrInfo = dealErrItfCode(e);
			result_data.put("STATUS", "1");
			result_data.put("SUBSTATUS",
					resErrInfo.getString("SUBSTATUS", "99"));
			result_data.put("STATUSDESC",
					resErrInfo.getString("STATUSDESC", "其它错误"));
			result_data.put("MODIFY_TAG", "1");
			resultSet.add(result_data);
			return resultSet;
		}
		result_data.put("STATUS", "2");
		result_data.put("SUBSTATUS", "00");
		result_data.put("STATUSDESC", "TradeOK!");
		result_data.put("MODIFY_TAG", "1");
		result_data.put("ORDER_ID", retData.getString("BATCH_ID"));
		resultSet.add(result_data);
		return resultSet;
	}

	/**
	 * @Title: regBlackWhiteForCloudMas
	 * @Description: 成员新增登记黑白名单表
	 * @param data
	 * @throws Exception
	 * @return void
	 * @author chenkh
	 * @time 2015年4月20日
	 */
	public static void regBlackWhiteForCloudMas(IData data) throws Exception {
		// 1- 取得操作编码
		String operCode = IDataUtil.getMandaData(data, "OPR_CODE");
		String productId = data.getString("PRODUCT_ID", "");
		String ecUserId = data.getString("USER_ID", "");
		String servCode = getServCodeByUserId(data.getString("USER_ID", ""),productId);
		String serialNumber = data.getString("MOB_NUM", "");

		// 2- 判断是不是为成员新增操作
		if ("01".equals(operCode)) {
			// 如果是新增成员操作，则新增黑白名单表信息
			String instId = SeqMgr.getInstId();
			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if (IDataUtil.isEmpty(userInfo)) {
				CSAppException.apperr(CrmUserException.CRM_USER_112);
			}
			String userId = userInfo.getString("USER_ID", "");
			String partitionId = userId.substring(userId.length() - 4,userId.length());
			String bizCode = getBizCodeByUserIdAndProduct(userId, productId);
			String userTypeCode = "QB";
			String serviceId = "99991"; // 随便给一个服务id，暂时没用起来
			String bizInCode = servCode;
			String startDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			String endDate = SysDateMgr.getTheLastTime();
			UserBlackWhiteInfoQry.insCloudMASList(instId, partitionId, userId,
					userTypeCode, serviceId, bizInCode, ecUserId, serialNumber,
					servCode, bizCode, startDate, endDate);
		} else if ("02".equals(operCode)) {
			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if (IDataUtil.isEmpty(userInfo)) {
				CSAppException.apperr(CrmUserException.CRM_USER_112);
			}
			String userId = userInfo.getString("USER_ID", "");
			UserBlackWhiteInfoQry.delCloudMASListByUserIdServCodeSN(userId,	serialNumber, servCode);
		}
	}

	/**
	 * @Title: getBizCodeByUserIdAndProduct
	 * @Description: 取得集团用户业务代码
	 * @param userId
	 * @param productId
	 * @return
	 * @throws Exception
	 * @return String
	 * @author chenkh
	 * @time 2015年4月19日
	 */
	public static String getBizCodeByUserIdAndProduct(String userId,
			String productId) throws Exception {
		String merchId = GrpCommonBean.productToMerch(productId, 0);
		if ("110154".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101544007");
			if (IDataUtil.isNotEmpty(attrInfo.getData(0))) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110155".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101554007");
			if (IDataUtil.isNotEmpty(attrInfo.getData(0))) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110156".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101564007");
			if (IDataUtil.isNotEmpty(attrInfo.getData(0))) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110157".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101574010");
			if (IDataUtil.isNotEmpty(attrInfo.getData(0))) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110158".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101584010");
			if (IDataUtil.isNotEmpty(attrInfo.getData(0))) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110159".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101594010");
			if (IDataUtil.isNotEmpty(attrInfo.getData(0))) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110160".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101604010");
			if (IDataUtil.isNotEmpty(attrInfo.getData(0))) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110161".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101614010");
			if (IDataUtil.isNotEmpty(attrInfo.getData(0))) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		if ("110162".equals(merchId)) {
			IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(
					userId, "1101624010");
			if (IDataUtil.isNotEmpty(attrInfo.getData(0))) {
				return attrInfo.getData(0).getString("ATTR_VALUE");
			}
		}
		return "";
	}

	/**
	 * 异常信息转换
	 * 
	 * @param e
	 * @return
	 * @throws Exception
	 */
	public static IData dealErrItfCode(Exception e) throws Exception {
		IData resErrData = new DataMap();
		String SUBSTATUS = "";
		String STATUSDESC = "";

		Throwable u = Utility.getBottomException(e);// 取有用的异常信息
		int leng = 512;
		StringWriter sw = new StringWriter();
		u.printStackTrace(new PrintWriter(sw));
		String errStr = sw.toString();
		int errLong = (int) errStr.length();
		if (errLong < 512) {
			leng = errLong;
		}

		String errInfo = Utility.parseExceptionMessage(e);

		boolean isNull = errInfo.contains("●");// 处理空指针异常情况
		String[] errInfos = errInfo.split("●");

		if (isNull && !"-1".equals(errInfos[0]))// 如果把空指针异常或在td_s_erritfcode表里没有配置的异常走else
		{
			SUBSTATUS = errInfos[0];
			STATUSDESC = errInfos[1];
		} else {
			SUBSTATUS = "99";
			STATUSDESC = errStr.substring(0, leng);
		}
		resErrData.put("SUBSTATUS", SUBSTATUS);
		resErrData.put("STATUSDESC", STATUSDESC);
		return resErrData;
	}

	public static IData prepareProductData(String user_id) throws Exception {
		IDataset productParam = UserAttrInfoQry.getUserAttrByUserId(user_id);
		IData retProductData = new DataMap();
		for (int i = 0, sizeI = productParam.size(); i < sizeI; i++) {
			String attr_code = productParam.getData(i).getString("ATTR_CODE");
			String attr_value = productParam.getData(i).getString("ATTR_VALUE");
			retProductData.put(attr_code, attr_value);
		}

		return retProductData;
	}

	public static IData prepareProductDataForChg(IDataset productParams,
			String userId) throws Exception {
		IData retProductData = new DataMap();

		if (IDataUtil.isEmpty(productParams)) {
			return retProductData;
		}
		// 因为变更时子类每次只处理一个产品，因此产品参数的List中一般只会有一条记录
		IData productParamInfo = productParams.getData(0);

		// 2获取前台传递过来的所有参数值
		IDataset params = productParamInfo.getDataset("PRODUCT_PARAM");

		if (IDataUtil.isEmpty(params)) {
			return retProductData;
		}
		// 3过滤参数，获取add状态参数
		params = GrpCommonBean.getOthererParam(params);

		// 4获取user表的数据
		retProductData = prepareProductData(userId);

		// 5将老数据更新为新数据
		for (int i = 0, sizeI = params.size(); i < sizeI; i++) {
			IData param = params.getData(i);
			retProductData.put(param.getString("ATTR_CODE"),
					param.getString("ATTR_VALUE"));
		}

		return retProductData;
	}

	/**
	 * @Title: synGroupDataToIAGW
	 * @Description: 判断是否需要同步集团资料到行业网关，如需要，则调用客管接口
	 * @param group_id
	 * @param product_spec_code
	 * @param eparchyCode
	 * @param cityCode
	 * @param staffId
	 * @param departId
	 * @throws Exception
	 * @return void
	 * @author chenkh
	 * @time 2015年7月9日
	 */
	public static void synGroupDataToIAGW(String group_id,
			String product_spec_code, String eparchyCode, String cityCode,
			String staffId, String departId) throws Exception {
		if (ProvinceUtil.isProvince(ProvinceUtil.HAIN)) {
			// 海南需要客管同步集团信息到行业网关
			IData params = new DataMap();
			params.put("GROUP_ID", group_id);
			String masType = BbossIAGWCloudMASDealBean
					.isSMSorMMS(product_spec_code);
			params.put("MAS_TYPE", masType);
			params.put("OPER_CODE", "01");
			params.put("TRADE_EPARCHY_CODE", eparchyCode);
			params.put("TRADE_CITY_CODE", cityCode);
			params.put("TRADE_STAFF_ID", staffId);
			params.put("TRADE_DEPART_ID", departId);
			IDataset ret = CSAppCall
					.call("CM.CustGroupSVC.synLocalMas", params);
			if ("-1".equals(ret.getData(0).getString("X_RESULTCODE", ""))) {
				CSAppException.apperr(
						GrpException.CRM_GRP_713,
						"客管同步集团资料到行业网关失败！"
								+ ret.getData(0).getString("X_RESULTINFO"));
			}
		}
	}
	

	/**
	 * @Title: dealTradeOlcomTagByOrder
	 * @Description: 将一个order中不需要重新发服开的trade的olcomTag改为0
	 * @param orderId
	 * @throws Exception
	 * @return void
	 * @author chenkh
	 * @time 2015年7月14日
	 */
	public static void dealTradeOlcomTagByOrder(String orderId)
			throws Exception {
		IDataset tradeInfos = UOrderSubInfoQry.qryOrderSubByOrderId(orderId);
		for (int i = 0, sizeI = tradeInfos.size(); i < sizeI; i++) {
			IData tradeInfo = tradeInfos.getData(i);
			String tradeId = tradeInfo.getString("TRADE_ID");
			IDataset tradeDetail = TradeInfoQry.getMainTradeByTradeId(tradeId);
			if (IDataUtil.isEmpty(tradeDetail)) {
				continue;
			}
			String productId = GrpCommonBean.productToMerch(tradeDetail
					.getData(0).getString("PRODUCT_ID"), 0);
			if (!isIAGWCloudMAS(productId)) {
				TradeInfoQry.updOlcomTagByTradeIdCancelTag(tradeId, "0");
			}
		}
	}

	  /**
     * 
     * @Description: 用于判断是否为信控行业网关云mas业务工单
     * @author mawm 
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean isIAGWCloudMASCredit(String productId) throws Exception
    {
    	String tradeStaffId=CSBizBean.getVisit().getStaffId(); 
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug(">>>>>>isIAGWCloudMASCredit.StaffId="+tradeStaffId);
    		logger.debug(">>>>>>isIAGWCloudMASCredit.productId="+productId);
    	}    	
    	if(!"CREDIT00".equals(tradeStaffId)) {
    		 return false;
    	}
    	 if ("110163".equals(productId) || "110154".equals(productId) || "110155".equals(productId) || "110156".equals(productId) || "110157".equals(productId) || "110158".equals(productId) || "110159".equals(productId) || "110160".equals(productId)
	                || "110161".equals(productId) || "110162".equals(productId) || "010101017".equals(productId) || "010101016".equals(productId))
	    {
	            return true;
	    }
        return false;
    }
    
	/**
	 * @Title: isCloudMasRspMeb
	 * @Description: 是否为反向归档的云mas成员业务
	 * @param serialNum
	 * @param grpUserId
	 * @param mebUserId
	 * @param productId
	 * @return
	 * @throws Exception
	 * @return boolean
	 * @author chenkh
	 * @time 2015年7月15日
	 */
	public static boolean isCloudMasRspMeb(String serialNum, String grpUserId,
			String mebUserId, String productId) throws Exception {
		String servCode = getServCodeByUserId(grpUserId, productId);
		if (StringUtils.isNotEmpty(servCode)
				&& StringUtils.isNotEmpty(serialNum)
				&& StringUtils.isNotEmpty(mebUserId)) {
			boolean isBlackWhiteExist = isBlackWhiteExist(serialNum, mebUserId,
					servCode) ? false : true;
			return isBlackWhiteExist;
		}

		return false;
	}

	/**
	 * @Title: isCloudMasPfSendIAGWBack
	 * @Description: 对于省行业网关云MAS业务，在发送完行业网关后，当服开回单后，直接完工。
	 * @param tradeId
	 * @return
	 * @throws Exception
	 * @return boolean
	 * @author chenkh
	 * @time 2015年7月20日
	 */
	public static boolean isCloudMasPfSendIAGWBack(String tradeId)
			throws Exception {
		String appType = "";
		IDataset tradeInfo = TradeInfoQry.getMainTradeByTradeId(tradeId);
		if (IDataUtil.isEmpty(tradeInfo)) {
			return false;
		}
		boolean flag = isIAGWCloudMASPfBack(tradeId, tradeInfo);
		if (!flag) {
			return false;
		}
		String orderId = tradeInfo.getData(0).getString("ORDER_ID");
		String acceptMonth = orderId.substring(4, 6);
		IData orderInfo = UOrderInfoQry.qryOrderByPk(orderId, acceptMonth, "0");
		if (IDataUtil.isEmpty(orderInfo)) {
			IData orderHisInfo = UOrderInfoQry.qryOrderByPk(orderId,
					acceptMonth, "C");
			if (IDataUtil.isEmpty(orderInfo)) {
				return false;
			}
			appType = orderHisInfo.getString("APP_TYPE");
		} else {
			appType = orderInfo.getString("APP_TYPE");
		}
		if ("C".equals(appType)) {
			return true;
		}

		return false;
	}

	/**
	 * @Title: isIAGWCloudMASPfBack
	 * @Description: 用于判断是否为行业网关云mas业务（服开回单用）
	 * @param tradeId
	 * @param tradeInfo
	 * @return
	 * @throws Exception
	 * @return boolean
	 * @author chenkh
	 * @time 2015年7月23日
	 */
	public static boolean isIAGWCloudMASPfBack(String tradeId,
			IDataset tradeInfo) throws Exception {
		String productId = tradeInfo.getData(0).getString("PRODUCT_ID");
		String tradeTypeCode = tradeInfo.getData(0)
				.getString("TRADE_TYPE_CODE");

		// 成员业务
		if ("4694".equals(tradeTypeCode) || "4695".equals(tradeTypeCode)
				|| "4697".equals(tradeTypeCode)) {
			IDataset mainProductInfo = ProductMebInfoQry
					.getProductMebByPidC(productId);
			if (mainProductInfo.isEmpty()) {
				return false;
			}
			productId = mainProductInfo.getData(0).getString("PRODUCT_ID");
		}

		productId = GrpCommonBean.productToMerch(productId, 0);
		if (isIAGWCloudMAS(productId) || "110163".equals(productId)
				|| "110164".equals(productId) || "010101017".equals(productId)
				|| "010101016".equals(productId)) {
			return true;
		}

		return false;
	}
}
