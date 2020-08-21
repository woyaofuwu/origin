
package com.asiainfo.veris.crm.order.soa.script.rule.iot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bre.base.BreBase;
import com.ailk.bre.databus.BreRuleParam;
import com.ailk.bre.script.IBREScript;
import com.ailk.bre.tools.BreFactory;
import com.ailk.bre.tools.BreTipsHelp;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * 物联网网络服务优先，机卡绑定，区域限制
 * CRMCS_REQ_20190416_0029【两节】关于下发网络服务优先优化需求支撑系统改造通知</p>
 * CRMCS_REQ_20190426_0007  关于下发物联网机卡绑定（网络校验）需求支撑系统改造的通知
 * @author ouym3
 */

@SuppressWarnings("serial")
public class IotCheckOfferRelBeDependedBySpecialAttr extends BreBase implements IBREScript {

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		String brandCode ="";
		String prodId = "";
		//个人主台账
		IDataset mainData = databus.getDataset("TF_B_TRADE");
		if(IDataUtil.isNotEmpty(mainData)){
		     brandCode = mainData.getData(0).getString("BRAND_CODE", "");
		     prodId =  mainData.getData(0).getString("PRODUCT_ID" , "");
		}
		
		//集团主台账
		IData grpmainData = databus.getData("TF_B_TRADE");
		if(IDataUtil.isNotEmpty(grpmainData)){
				 brandCode = grpmainData.getString("BRAND_CODE", "");
		  }
		
		if (!"PWLW".equals(brandCode)&&!"WLWG".equals(brandCode)) {
			return false;// 仅处理物联网的校验
		}
		if (IDataUtil.isNotEmpty(mainData)||IDataUtil.isNotEmpty(grpmainData)) {
			/**加载配置信息**/
			IDataset discnt_config_list = new DatasetList();
			IDataset apnname_config_list = new DatasetList();
			IDataset apnname_config_9032 = new DatasetList();
			String discnt_config_codestr = ""; // 订购资费字符串
			String svc_config_codestr = ""; // 订购的用户策略服务字符串
			
			//订购资费依赖服务的参数
			String discntRelySvc_discnt_codestr = ""; // 订购资费字符串
			String discntRelySvc_Svc_codestr = ""; // 订购的用户策略服务字符串
			
			Map<String, String> discnt_config_map = new HashMap<String, String>(); // 资费与全网编码映射
			Map<String, String> svccode_config_map = new HashMap<String, String>();// 服务与全网编码影射

			IDataset configList = CommparaInfoQry.getCommByParaAttr("CSM", "1566", "ZZZZ");
			
			for (int i = 0; i < configList.size(); i++) {
				IData configData = configList.getData(i);
				String offerCode = configData.getString("PARAM_CODE");
				String svcConfigData = configData.getString("PARA_CODE2");
				String offerType = configData.getString("PARA_CODE4");
				// 获取网络优先接入服务的服务编码
				IDataset discntConfig = null;
				if("D".equals(offerType)){
				     discntConfig = CommparaInfoQry.getCommparaByAttrCode1("CSM", "9013",  offerCode, "ZZZZ", null);
				}else if("S".equals(offerType)){
					 discntConfig = CommparaInfoQry.getCommparaByAttrCode1("CSM", "9014",  offerCode, "ZZZZ", null);
				}
				IDataset svcConfig = CommparaInfoQry.getCommparaByAttrCode1("CSM", "9014", svcConfigData,  "ZZZZ", null);
				
				if (IDataUtil.isNotEmpty(discntConfig)) {
					for (int j = 0; j < discntConfig.size(); j++) {
						String discntCode = discntConfig.getData(j).getString("PARAM_CODE");
						configData.put("DISCNT_CODE", discntCode);
						if(discntRelySvc_discnt_codestr.indexOf(discntCode) == -1){
						    discntRelySvc_discnt_codestr = discntRelySvc_discnt_codestr + discntCode + ",";
						}
						discnt_config_map.put(discntCode, offerCode);
					}
				}
				
				if (IDataUtil.isNotEmpty(svcConfig)) {
					for (int j = 0; j < svcConfig.size(); j++) {
						String svcCode = svcConfig.getData(j).getString("PARAM_CODE");
						configData.put("SERVICE_CODE", svcCode);
						if(discntRelySvc_Svc_codestr.indexOf(svcCode) == -1){
						     discntRelySvc_Svc_codestr = discntRelySvc_Svc_codestr + svcCode + ",";
						}
						svccode_config_map.put(svcCode, svcConfigData);
					}
				}
			}
			discnt_config_list.addAll(configList);
			apnname_config_list = CommparaInfoQry.getOnlyByAttr("CSM", "9031", "ZZZZ");
			apnname_config_9032 = CommparaInfoQry.getOnlyByAttr("CSM", "9032", "ZZZZ");
			
			/** 订购校验资费依赖服务校验**/
			if (checkPCCPolicyDg(databus, discnt_config_list, apnname_config_list, discntRelySvc_discnt_codestr, 
					discntRelySvc_Svc_codestr, discnt_config_map, svccode_config_map,apnname_config_9032)) {
				return true;
			}
			/** 策略变更校验**/
	        if(checkUpdatePolicy(databus, discnt_config_list ,apnname_config_list, discntRelySvc_discnt_codestr, 
					discntRelySvc_Svc_codestr, discnt_config_map, svccode_config_map,apnname_config_9032)){
	        	return true;
	        }		
			/**退订资费依赖服务校验**/
			if (checkPCCPolicyTD(databus, discnt_config_list, apnname_config_list, discntRelySvc_discnt_codestr,
					discntRelySvc_Svc_codestr, discnt_config_map, svccode_config_map)) {
				return true;
			}
			/**限制批量开卡不允许同时订购频选优先的限制*/
			if(checkBatchOpenCard(databus)){
				return true;
			}
			/**还有个只有开通4G的物联卡，车联网用户才可以订购频选优先服务*/
			if(checkCarNet4G(databus ,prodId)){
				return true;
			}
			/**
			 * 2、用户退订数据服务产品时，如果用户存在有效的用户策略且退订该数据通信服务产品后用户不存在其他可依赖的数据通信服务产品，则校验打回“用户已经订购用户策略，必须存在数据通信服务产品”;
	           3、用户订购数据通信服务产品时，如果用户存在有效的用户策略，则判断新增的APN是否支持PCC策略：如果不支持PCC策略，则校验打回“用户订购的APN+APNNAME不支持PCC策略”
	           4、用户变更数据通信服务产品时（旧资费包下的85），如果用户存在有效的用户策略，则判断变更的APN是否支持PCC策略：如果不支持PCC策略，则校验打回“用户订购的APN+APNNAME不支持PCC策略”"
			 */
	        if(check85CommunicateSvc(databus , apnname_config_list)){
	        	return true;
	        }
			
			/**达量降速需求策略校验拦截*/
			if(checkTactics(databus)){
				return true;
			}
    	}
    	return false;
	}

	/**
	 * @param databus
	 * @return
	 */
	private boolean check85CommunicateSvc(IData databus ,IDataset apnname_config_list) throws Exception{
		IDataset communicateInfoDataList = CommparaInfoQry.getCommparaByCode1("CSM", "9014", "I00010100085", "ZZZZ");
		StringBuilder sbCommnicateSvcs = new StringBuilder();
		for(int i =0 ;i<communicateInfoDataList.size();i++){
			sbCommnicateSvcs.append(communicateInfoDataList.getData(i).getString("PARAM_CODE"));
			sbCommnicateSvcs.append("|");
		}
		String strCommunicateSvcs = sbCommnicateSvcs.toString();  //数据通信服务code，集合
		int tdCommunicateSvcNum = 0;  // 退订数据通信服务个数
		int dgCommunicateSvcNum = 0 ; //订购数据通信服务个数
		int existCommunicateSvcNum = 0 ; //存量数据通信服务个数
		int existPolicySvcNum = 0;    //用户策略服务
		
		IDataset policySvcList = CommparaInfoQry.getCommparaByCode1("CSM", "9014", "I00010100108", "ZZZZ");
		StringBuilder sbPolicySvc = new StringBuilder();
		for(int i =0 ;i<policySvcList.size();i++){
			sbPolicySvc.append(policySvcList.getData(i).getString("PARAM_CODE"));
			sbPolicySvc.append("|");
		}
		String strPolicySvcs = sbPolicySvc.toString();  //用户服务code，集合
		IDataset svcList = databus.getDataset("TF_B_TRADE_SVC");
		IDataset attrTradeList = databus.getDataset("TF_B_TRADE_ATTR");
		IDataset userSvcList = databus.getDataset("TF_F_USER_SVC_AFTER");
		for(int i=0 ;i<svcList.size() ;i++){
			IData svcData = svcList.getData(i);
			String serviceId= svcData.getString("SERVICE_ID");
			String modifyTag = svcData.getString("MODIFY_TAG");
			if(StringUtils.equals(modifyTag, "0")){
				if(strCommunicateSvcs.indexOf(serviceId) != -1){
					dgCommunicateSvcNum++;
				}
			}
			if(StringUtils.equals(modifyTag, "1")){
				if(strCommunicateSvcs.indexOf(serviceId) != -1){
					tdCommunicateSvcNum++;
				}
			}
		}
		for(int i=0 ;i<userSvcList.size() ;i++){
			IData svcData = userSvcList.getData(i);
			String serviceId= svcData.getString("SERVICE_ID");
			String modifyTag = svcData.getString("MODIFY_TAG");
			if(StringUtils.equals(modifyTag, "USER")){
				if(strCommunicateSvcs.indexOf(serviceId) != -1){
					existCommunicateSvcNum++;
				}
				if(strPolicySvcs.indexOf(serviceId) != -1){
					existPolicySvcNum ++;
				}
			}
		}
		
		List<String> apnNameList = new ArrayList<String>();  //订购数据通信服务apnname集合
		for(int i=0 ;i < attrTradeList.size() ;i++){
			IData attrData = attrTradeList.getData(i);
			String elementId= attrData.getString("ELEMENT_ID");
			String attrCode = attrData.getString("ATTR_CODE");
			String attrValue = attrData.getString("ATTR_VALUE");
			String modifyTag = attrData.getString("MODIFY_TAG");
			if(strCommunicateSvcs.indexOf(elementId) !=-1){
				if("APNNAME".equals(attrCode) && "0".equals(modifyTag)){
					apnNameList.add(attrValue);
				}
			}
		}
		
		//用户退订数据服务产品时，如果用户存在有效的用户策略且退订该数据通信服务产品后用户不存在其他可依赖的数据通信服务产品，则校验打回“用户已经订购用户策略，必须存在数据通信服务产品”;
		if(tdCommunicateSvcNum>0 && existCommunicateSvcNum == 0){
			if(existPolicySvcNum>0){
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "用户已经订购用户策略，必须存在数据通信服务产品!");
				return true;
			}
		}
		//3、用户订购数据通信服务产品时，如果用户存在有效的用户策略，则判断新增的APN是否支持PCC策略：如果不支持PCC策略，则校验打回“用户订购的APN+APNNAME不支持PCC策略”
		//4、用户变更数据通信服务产品时（旧资费包下的85），如果用户存在有效的用户策略，则判断变更的APN是否支持PCC策略：如果不支持PCC策略，则校验打回“用户订购的APN+APNNAME不支持PCC策略”"
		if(dgCommunicateSvcNum > 0 || apnNameList.size() >0){
			if(existPolicySvcNum > 0){
				for(String apnName : apnNameList){
					IDataset cmiotConfigList = DataHelper.filter(apnname_config_list, "PARAM_CODE=" + apnName);
					if (cmiotConfigList.size() > 0) {
						String pccFlag = cmiotConfigList.first().getString("PARA_CODE5"); // 是否打开PCC能力
						if (StringUtils.isBlank(pccFlag) || StringUtils.equals("0", pccFlag)) {
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "用户订购的APN+APNNAME不支持PCC策略!");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * @param databus
	 * @param prodId
	 * @return
	 */
	private boolean checkCarNet4G(IData databus, String prodId) throws Exception{
		IDataset proConfig = CommparaInfoQry.getCommparaByAttrCode1("CSM", "9015", "I00010700001", "ZZZZ", null);
		if (DataUtils.isEmpty(proConfig)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "I00011100001产品配置异常，请联系管理员");
		}
		IDataset configData = CommparaInfoQry.getCommparaByCode1("CSM", "9014", "I00010100153", "ZZZZ");
		if (IDataUtil.isEmpty(configData)) {
			return false;
		}
		IDataset discntList = databus.getDataset("TF_B_TRADE_DISCNT");
		String discntCode = configData.first().getString("PARAM_CODE");
		IDataset discntData = DataHelper.filter(discntList, "INST_TYPE=D,DISCNT_CODE=" + discntCode);
		if (IDataUtil.isNotEmpty(discntData)) { //如果订购了频选优先产品
			IDataset discntDataset = DataHelper.filter(proConfig, "PARAM_CODE=" + prodId);
			if (IDataUtil.isNotEmpty(discntDataset)) {// 是车联网用户
				IData tradeData = databus.getData("TF_B_TRADE");
				String userId = tradeData.getString("USER_ID");
				if (!isLteCardUser(userId)) {
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "物联网4G用户才可以订购频选优先服务！");
					return true;
				}
			} else { // 不是车联网用户
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "车联网用户才可以订购频选优先服务！");
				return true;
			}
		}
		return false;
	}
	
	
    /**
     * @Description: 是否4G卡用户
     * @param userId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 23, 2014 5:33:17 PM
     */
    public boolean isLteCardUser(String userId) throws Exception
    {
        IDataset resDatas = UserResInfoQry.queryUserResByUserIdResType(userId, "1");

        if (IDataUtil.isNotEmpty(resDatas))
        {
            String simCardNo = resDatas.getData(0).getString("RES_CODE");

            // 调用资源接口
            IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", "1");

            if (IDataUtil.isNotEmpty(simCardDatas))
            {
                String simTypeCode = simCardDatas.getData(0).getString("RES_TYPE_CODE", "0").substring(1);// 对应老系统的simtypecode

                IDataset assignParaInfoData = ResParaInfoQry.checkUser4GUsimCard(simTypeCode);

                if (StringUtils.isNotBlank(simTypeCode) && IDataUtil.isNotEmpty(assignParaInfoData))
                {
                    return true;
                }
            }
            else
            {
                // CSAppException.apperr(ResException.CRM_RES_86, simCardNo);
                return false;// 因测试资料不全 暂时返回false
            }
        }
        else
        {
            // CSAppException.apperr(ResException.CRM_RES_85);
            return false;// 因测试资料不全 暂时返回false
        }
        return false;
    }

	/**
	 * 限制批量开卡不允许同时订购频选优先的限制
	 * @param databus
	 * @return
	 * @throws Exception 
	 */
	private boolean checkBatchOpenCard(IData databus) throws Exception {
		String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
		if ("500".equals(tradeTypeCode)) {
			IDataset discntList = databus.getDataset("TF_B_TRADE_DISCNT");
			IDataset configData = CommparaInfoQry.getCommparaByCode1("CSM", "9014", "I00010100153", "ZZZZ");
			if (IDataUtil.isEmpty(configData)) {
				return false;
			}
			String discntCode = configData.first().getString("PARAM_CODE");
			IDataset discntDataset = DataHelper.filter(discntList, "INST_TYPE=D,DISCNT_CODE="+discntCode);
			if(IDataUtil.isNotEmpty(discntDataset)){
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "批量开卡不允许同时订购频选优先产品!");
				return true;
			}
		}
		return false;
	}


	/**
	 * 网络服务优先，机卡绑定 ，区域限制同停通开校验，用户服务策略必须一一对应，apnname必须开放pcc能力
	 * <p>
	 * CRMCS_REQ_20190416_0029 关于下发网络服务优先优化需求支撑系统改造通知
	 * </p>
	 * @author ouym3
	 */
	private boolean checkUpdatePolicy(IData databus, IDataset discnt_config_list, IDataset apnname_config_list, 
			String discntRelySvc_discnt_codestr, String discntRelySvc_Svc_codestr,
			Map<String, String> discnt_config_map, Map<String, String> svccode_config_map, IDataset apnname_config_9032) throws Exception {

		IDataset svcList = databus.getDataset("TF_B_TRADE_SVC");
		IDataset attrTradeList = databus.getDataset("TF_B_TRADE_ATTR");
		IDataset discntList = databus.getDataset("TF_B_TRADE_DISCNT");
		IDataset allAttrTradeList = databus.getDataset("TF_F_USER_ATTR_AFTER"); // 获取用户所有的属性
		IDataset userDiscntList = databus.getDataset("TF_F_USER_DISCNT_AFTER");// 获得用户所有的资费
		IDataset userSvcList = databus.getDataset("TF_F_USER_SVC_AFTER");

		IDataset attrTradeList1 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=usrSessionPolicyCode,MODIFY_TAG=0");
		IDataset attrTradeList2 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=01");// 开通
		IDataset attrTradeList3 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=03");// 变更
		IDataset attrTradeList4 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=02");// 删除
		IDataset attrTradeList5 = DataHelper.filter(allAttrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=03");// 变更
		List<String> dgOfferList = new ArrayList<String>();
		
		boolean isHasDgDiscnt = false;// 是否已经订购类型资费或者服务
		String hasDgDiscntCode = "";
		for (int i = 0; i < userDiscntList.size(); i++) {
			IData discntRecord = userDiscntList.getData(i);
			String discntCode = discntRecord.getString("DISCNT_CODE");
			String modifyTag = discntRecord.getString("MODIFY_TAG");
			if ("USER".equals(modifyTag)) {
				if (discntRelySvc_discnt_codestr.indexOf(discntCode) != -1) {
					isHasDgDiscnt = true;
					hasDgDiscntCode = discntCode;
					break;
				}
			}
		}
		if(!isHasDgDiscnt){
			for (int i = 0; i < userSvcList.size(); i++) {
				IData discntRecord = userSvcList.getData(i);
				String discntCode = discntRecord.getString("SERVICE_ID");
				String modifyTag = discntRecord.getString("MODIFY_TAG");
				if ("USER".equals(modifyTag)) {
					if (discntRelySvc_discnt_codestr.indexOf(discntCode) != -1) {
						isHasDgDiscnt = true;
						hasDgDiscntCode = discntCode;
						break;
					}
				}
			}
		}
		
		for (int i = 0; i < discntList.size(); i++) {
			IData discntTrade = discntList.getData(i);
			String oprTag = discntTrade.getString("MODIFY_TAG");
			String discntCode = discntTrade.getString("DISCNT_CODE");
			if ("0".equals(oprTag)) {
				if (discntRelySvc_discnt_codestr.indexOf(discntCode) != -1) {
					dgOfferList.add(discntCode);
				}
			}
		}
		
		for (int j = 0; j < svcList.size(); j++) {
			IData svcTrade = svcList.getData(j);
			String oprTag = svcTrade.getString("MODIFY_TAG");
			String svcCode = svcTrade.getString("SERVICE_ID");
			if("0".equals(oprTag)){
				if(discntRelySvc_Svc_codestr.indexOf(svcCode) != -1 || discntRelySvc_discnt_codestr.indexOf(svcCode) != -1){
					dgOfferList.add(svcCode);
				}
			}
		}
//		
//		/** 变更校验 **/
//		if (IDataUtil.isNotEmpty(attrTradeList1) && (IDataUtil.isNotEmpty(attrTradeList2) || IDataUtil.isNotEmpty(attrTradeList3))) {
//			for (int i = 0; i < attrTradeList1.size(); i++) {
//				boolean isExistDiscode = false;
//				IData attrTradeData = attrTradeList1.getData(i);
//				String attrValue = attrTradeData.getString("ATTR_VALUE"); // 策略值
//				for (String discntCode : dgOfferList) {
//					IDataset filterData = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode) + ",PARA_CODE3=" + attrValue);
//					if (IDataUtil.isNotEmpty(filterData)) {
//						isExistDiscode = true;
//						break;
//					}
//				}
//				if (!isExistDiscode) { // 如果当前订单里面没有订购相关资费，则查看存量是或否订购资费
//					for (int j = 0; j < userDiscntList.size(); j++) {
//						IData discntRecord = userDiscntList.getData(0);
//						String discntCode = discntRecord.getString("DISCNT_CODE");
//						String modifyTag = discntRecord.getString("MODIFY_TAG");
//						if ("USER".equals(modifyTag)) {
//							IDataset filterData = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode) + ",PARA_CODE3=" + attrValue);
//							if (IDataUtil.isNotEmpty(filterData)) {
//								isExistDiscode = true;
//								break;
//							}
//						}
//					}
//					//无存量资费，查询是否有存量服务
//					if(!isExistDiscode){
//						for (int j = 0; j < userSvcList.size(); j++) {
//							IData discntRecord = userSvcList.getData(0);
//							String discntCode = discntRecord.getString("SERVICE_ID");
//							String modifyTag = discntRecord.getString("MODIFY_TAG");
//							if ("USER".equals(modifyTag)) {
//								IDataset filterData = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode) + ",PARA_CODE3=" + attrValue);
//								if (IDataUtil.isNotEmpty(filterData)) {
//									isExistDiscode = true;
//									break;
//								}
//							}
//						}
//					}
//				}
//				if (!isExistDiscode) {
//					IDataset filterDiscnt = DataHelper.filter(discnt_config_list, "PARA_CODE3=" + attrValue);
//					String errorTips = filterDiscnt.first().getString("PARAM_NAME");
//					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购" + errorTips + "服务策略必须订购" + errorTips);
//					return true;
//				}
//			}
//		}
		// 只变更策略，不变变更资费
		if (IDataUtil.isNotEmpty(attrTradeList1)) {
			if (dgOfferList.size() == 0) {
				String policyValue = attrTradeList1.first().getString("ATTR_VALUE");
				IDataset filterDiscnt = DataHelper.filter(discnt_config_list, "PARA_CODE3=" + policyValue);
				String errorTips = "";
				String strTipsInfo = "";
				if (filterDiscnt.size() != 0) {
					errorTips = filterDiscnt.first().getString("PARAM_NAME");
					strTipsInfo = "变更" + errorTips + "策略必须订购或已经订购" + errorTips + "!";
				} else {
					strTipsInfo = "变更用户服务策略必须订购与该策略对应的资费产品!";
				}
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, strTipsInfo);
				return true;
			}
		}
		// 删除策略
		if (IDataUtil.isNotEmpty(attrTradeList4)) {
			if (isHasDgDiscnt) {
				String relaInstId = attrTradeList.first().getString("RELA_INST_ID");
				IDataset policyDatas = DataHelper.filter(allAttrTradeList, "RELA_INST_ID="+relaInstId+",ATTR_CODE=usrSessionPolicyCode") ;
				String policyValue = policyDatas.first().getString("ATTR_VALUE");
				IDataset filterDiscnt = DataHelper.filter(discnt_config_list, "PARA_CODE3=" + policyValue);
				String errorTips = filterDiscnt.first().getString("PARAM_NAME");
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "已订购" + errorTips + "不能删除" + errorTips + "服务策略！");
				return true;
			}
		}
		/** 校验apnname是否开通pcc能力 **/
		int apnNumber = 0;
		IDataset svcAttrList = DataHelper.filter(allAttrTradeList, "INST_TYPE=S,ATTR_CODE=APNNAME");
		for (int i = 0; i < svcAttrList.size(); i++) {
			IData svcAttrData = svcAttrList.getData(i);
			String apnNameValue = svcAttrData.getString("ATTR_VALUE");
			IDataset cmiotConfigList = DataHelper.filter(apnname_config_list, "PARAM_CODE=" + apnNameValue);
			if (cmiotConfigList.size() > 0) {
				apnNumber++;
				String pccFlag = cmiotConfigList.first().getString("PARA_CODE5"); // 是否打开PCC能力
				if (StringUtils.isBlank(pccFlag) || StringUtils.equals("0", pccFlag)) {
					if (dgOfferList.size() > 0 ) {
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购用户接入策略时,所有APN必须支持PCC能力!");
						return true;
					}
				}
			}
		}
        //是否订购通用服务
		if (dgOfferList.size() > 0 && apnNumber == 0) {
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购用户接入策略时,必须订购该策略依赖的通信服务!");
			return true;
		}
		/**订购用户策略服务产品，策略代码对应的APNNAME必须与依赖的数据通信服务的APNNAME一致*/
		if (IDataUtil.isNotEmpty(attrTradeList1)) {
			for(int j =0 ; j<attrTradeList1.size();j++){
				String attrValue = attrTradeList1.getData(j).getString("ATTR_VALUE"); // 策略值
				IDataset configDataSet = DataHelper.filter(discnt_config_list, "PARA_CODE3=" + attrValue);
				if(IDataUtil.isNotEmpty(configDataSet)){
					String paramCode1 = configDataSet.first().getString("PARA_CODE1");
					if(!"WLYX".equals(paramCode1)){
						continue;
					}
				}else{
					continue;
				}
				IDataset policyData = DataHelper.filter(apnname_config_9032, "PARAM_CODE=" + attrValue);
				if(IDataUtil.isEmpty(policyData)){
					continue;
				}else{
					String apnName  = policyData.first().getString("PARA_CODE5");
					if(StringUtils.isBlank(apnName)){
						continue;
					}
				}
				boolean isExistPolicyVal = false;
				for (int i = 0; i < svcAttrList.size(); i++) {
					IData svcAttrData = svcAttrList.getData(i);
					String apnNameValue = svcAttrData.getString("ATTR_VALUE");
					IDataset configList = DataHelper.filter(apnname_config_9032, "PARAM_CODE=" + attrValue+",PARA_CODE5="+apnNameValue);
					if(IDataUtil.isNotEmpty(configList)){
						isExistPolicyVal = true;
						break;
					}
				}
				if(!isExistPolicyVal){
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购用户策略服务产品，策略代码对应的APNNAME必须与依赖的数据通信服务的APNNAME一致!");
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean checkPCCPolicyDg(IData databus, IDataset discnt_config_list, IDataset apnname_config_list, String discnt_config_codestr, String svc_config_codestr,
			Map<String, String> discnt_config_map, Map<String, String> svccode_config_map , IDataset apnname_config_9032) throws Exception {
		if (discnt_config_list.isEmpty()) {
			return false;
		}
		IDataset svcList = databus.getDataset("TF_B_TRADE_SVC");
		IDataset attrTradeList = databus.getDataset("TF_B_TRADE_ATTR");
		IDataset discntList = databus.getDataset("TF_B_TRADE_DISCNT");
		IDataset allAttrTradeList = databus.getDataset("TF_F_USER_ATTR_AFTER"); // 获取用户所有的属性
		IDataset userDiscntList = databus.getDataset("TF_F_USER_DISCNT_AFTER");// 获得用户所有的资费
		IDataset userSvcList = databus.getDataset("TF_F_USER_SVC_AFTER");

		IDataset attrTradeList1 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=usrSessionPolicyCode,MODIFY_TAG=0");
		IDataset attrTradeList2 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=01");// 开通
		IDataset attrTradeList3 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=03");// 变更
		IDataset attrTradeList4 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=02");// 删除
		IDataset attrTradeList5 = DataHelper.filter(allAttrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=03");// 变更
		
		boolean isHasDgDiscnt = false;//是否已经订购类型资费
		boolean isHasDgSvc = false;
		//String hasDgDiscntCode = "";
		for (int i = 0; i < userDiscntList.size(); i++) {
			IData discntRecord = userDiscntList.getData(i);
			String discntCode = discntRecord.getString("DISCNT_CODE");
			String modifyTag = discntRecord.getString("MODIFY_TAG");
			if ("USER".equals(modifyTag)) {
				if (discnt_config_codestr.indexOf(discntCode) != -1) {
					isHasDgDiscnt = true;
					//hasDgDiscntCode = discntCode;
					break;
				}
			}
		}
		for (int i = 0; i < svcList.size(); i++) {
			IData svcRecord = svcList.getData(i);
			String svcCode = svcRecord.getString("SERVICE_ID");
			String modifyTag = svcRecord.getString("MODIFY_TAG");
			if ("USER".equals(modifyTag)) {
				if (discnt_config_codestr.indexOf(svcCode) != -1) {
					isHasDgSvc = true;
					//hasDgDiscntCode = discntCode;
					break;
				}
			}
		}
		boolean isHasDgUserSvc = false; //是否已经订购用户服务
		for(int j=0 ; j<userSvcList.size() ; j++){
			IData svcRecord = userSvcList.getData(j);
			String servcieCode = svcRecord.getString("SERVICE_ID");
			String modifyTag = svcRecord.getString("MODIFY_TAG");
			if ("2".equals(modifyTag)) {
				if (svc_config_codestr.indexOf(servcieCode) != -1) {
					isHasDgUserSvc = true;
					break;
				}
			}
		}
		
		List<String> dgDiscntList = new ArrayList<String>(); // 所有订购相关资费集合
		IDataset dgSvcDataList = new DatasetList(); // 所有订购的相关服务策略集合
		/**统计所有订购类型的资费**/
		for (int i = 0; i < discntList.size(); i++) {
			IData discntTrade = discntList.getData(i);
			String oprTag = discntTrade.getString("MODIFY_TAG");
			String discntCode = discntTrade.getString("DISCNT_CODE");
			if ("0".equals(oprTag)) {
				if (discnt_config_codestr.indexOf(discntCode) != -1) {
					dgDiscntList.add(discntCode);
				}
			}
		}
		
		for (int i = 0; i < svcList.size(); i++) {
			IData discntTrade = svcList.getData(i);
			String oprTag = discntTrade.getString("MODIFY_TAG");
			String discntCode = discntTrade.getString("SERVICE_ID");
			if ("0".equals(oprTag)) {
				if (discnt_config_codestr.indexOf(discntCode) != -1) {
					dgDiscntList.add(discntCode);
				}
			}
		}
		/**统计所有订购类型服务**/
		for (int j = 0; j < svcList.size(); j++) {
			IData svcTrade = svcList.getData(j);
			String oprTag = svcTrade.getString("MODIFY_TAG");
			String svcCode = svcTrade.getString("SERVICE_ID");
			if (svc_config_codestr.indexOf(svcCode) == -1 || !StringUtils.equals(oprTag, "0")) {
				continue;
			}
			// 订单中的属性
			String elementId = svcTrade.getString("SERVICE_ID");
			String instId = svcTrade.getString("INST_ID");
			IData dgSvcData = new DataMap();
			dgSvcData.put("SVC_CODE", svcCode);
			// dgSvcCode = svcCode;

			for (int h = 0; h < attrTradeList.size(); h++) {
				IData attrTrade = attrTradeList.getData(h);
				if (elementId.equals(attrTrade.getString("ELEMENT_ID")) && instId.equals(attrTrade.getString("RELA_INST_ID"))) {
					if ("0".equals(attrTrade.getString("MODIFY_TAG"))) {
						if ("usrSessionPolicyCode".equals(attrTrade.getString("ATTR_CODE"))) {
							// dgPolicyValue = attrTrade.getAttrValue();
							dgSvcData.put("POLICY_VALUE", attrTrade.getString("ATTR_VALUE"));
							break;
						}
					}
				}
			}
			dgSvcDataList.add(dgSvcData); //
		}
		
		/**从资费开始校验**/
		for (String discntCode : dgDiscntList) {
			boolean isExistSvc = false;
			boolean isPolicyValiable = false;
			for (int i = 0; i < dgSvcDataList.size(); i++) {
				IData svcData = dgSvcDataList.getData(i);
				String svcCode = svcData.getString("SVC_CODE");
				String policyValue = svcData.getString("POLICY_VALUE");
				IDataset filterData = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode) + ",PARA_CODE2=" + svccode_config_map.get(svcCode) + ",PARA_CODE3=" + policyValue);
				IDataset svcFillter = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode) + ",PARA_CODE2=" + svccode_config_map.get(svcCode));
				if (!filterData.isEmpty()) {
					isPolicyValiable = true;
				}
				if (!svcFillter.isEmpty()) {
					isExistSvc = true;
				}
			}
			
			if (!isExistSvc && !isHasDgUserSvc) {
				IDataset filterDataset = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode));
				String errorTips = filterDataset.first().getString("PARAM_NAME");
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购" + errorTips + "必须订购用户策略服务产品！");
				return true;
			}
			if (!isPolicyValiable && !isHasDgUserSvc) {
				IDataset filterDataset = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode));
				String errorTips = filterDataset.first().getString("PARAM_NAME");
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购" + errorTips + "与用户策略服务产品，则用户策略服务产品的策略必须为 " + errorTips + "接入类型");
				return true;
			}
			//重新订购了资费，但是没修改策略
			if(!isExistSvc && isHasDgUserSvc && IDataUtil.isEmpty(attrTradeList1)){
				IDataset filterDataset = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode));
				String errorTips = filterDataset.first().getString("PARAM_NAME");
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购" + errorTips + "与用户策略服务产品，则用户策略服务产品的策略必须为 " + errorTips + "接入类型");
				return true;
			}
			//重新订购了资费，但是选错策略
			if(!isExistSvc && isHasDgUserSvc && IDataUtil.isNotEmpty(attrTradeList1)){
				String policyValue = attrTradeList1.first().getString("ATTR_VALUE");
				IDataset filterDataset = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode)+",PARA_CODE3="+policyValue);
				if(IDataUtil.isEmpty(filterDataset)){
					filterDataset = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode));
					String errorTips = filterDataset.first().getString("PARAM_NAME");
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购" + errorTips + "与用户策略服务产品，则用户策略服务产品的策略必须为 " + errorTips + "接入类型");
					return true;
				}
				if(IDataUtil.isEmpty(attrTradeList3) && IDataUtil.isEmpty(attrTradeList5)){
					filterDataset = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode));
					String errorTips = filterDataset.first().getString("PARAM_NAME");
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购" + errorTips + "与用户策略服务产品，则用户策略服务产品的操作类型必须为变更状态");
					return true;
				}
			}
		}

		/**从服务开始校验**/
		for (int i = 0; i < dgSvcDataList.size(); i++) {
			IData svcData = dgSvcDataList.getData(i);
			String svcCode = svcData.getString("SVC_CODE");
			String policyValue = svcData.getString("POLICY_VALUE");
			if (StringUtils.isBlank(policyValue)) {
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购用户策略服务必须开通策略服务产品策略！");
				return true;
			}
			boolean isExistDiscnt = false;
			for (String discntCode : dgDiscntList) {
				IDataset filterData = DataHelper.filter(discnt_config_list, "PARA_CODE2=" + svccode_config_map.get(svcCode) + ",PARA_CODE3=" + policyValue + ",PARAM_CODE=" + discnt_config_map.get(discntCode));
				if (IDataUtil.isNotEmpty(filterData)) {
					isExistDiscnt = true;
				}
			}
			if(!isExistDiscnt){
				IDataset filterDiscnt = DataHelper.filter(discnt_config_list, "PARA_CODE3="+policyValue);
				String errorTips = filterDiscnt.first().getString("PARAM_NAME");
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "订购" + errorTips + "用户服务策略，则用户必须订购 " + errorTips );
			}
		}
		return false;
   }

	
	private boolean checkPCCPolicyTD(IData databus, IDataset discnt_config_list, IDataset apnname_config_list,
			String discnt_config_codestr, String svc_config_codestr, Map<String, String> discnt_config_map,
			Map<String, String> svccode_config_map) throws Exception {
		if (discnt_config_list.isEmpty()) {
			return false;
		}
		IDataset svcList = databus.getDataset("TF_B_TRADE_SVC");
		IDataset attrTradeList = databus.getDataset("TF_B_TRADE_ATTR");
		IDataset discntList = databus.getDataset("TF_B_TRADE_DISCNT");

		List<String> tdDiscntList = new ArrayList<String>(); // 所有订购相关资费集合
		IDataset tdSvcDataList = new DatasetList(); // 所有订购的相关服务策略集合
		List<String> dgDiscntList =new ArrayList<String>();
		/**统计所有退订类型的资费和服务**/
		for (int i = 0; i < discntList.size(); i++) {
			IData discntTrade = discntList.getData(i);
			String oprTag = discntTrade.getString("MODIFY_TAG");
			String discntCode = discntTrade.getString("DISCNT_CODE");
			if ("1".equals(oprTag)) {
				if (discnt_config_codestr.indexOf(discntCode) != -1) {
					tdDiscntList.add(discntCode);
				}
			}else if("0".equals(oprTag)){
				if(discnt_config_codestr.indexOf(discntCode)!=-1){
					dgDiscntList.add(discntCode);
				}
			}
		}
		
		for (int i = 0; i < svcList.size(); i++) {
			IData discntTrade = svcList.getData(i);
			String oprTag = discntTrade.getString("MODIFY_TAG");
			String discntCode = discntTrade.getString("SERVICE_ID");
			if ("1".equals(oprTag)) {
				if (discnt_config_codestr.indexOf(discntCode) != -1) {
					tdDiscntList.add(discntCode);
				}
			}else if("0".equals(oprTag)){
				if(discnt_config_codestr.indexOf(discntCode)!=-1){
					dgDiscntList.add(discntCode);
				}
			}
		}
		/**统计所有退订类型服务**/
		for (int j = 0; j < svcList.size(); j++) {
			IData svcTrade = svcList.getData(j);
			String oprTag = svcTrade.getString("MODIFY_TAG");
			String svcCode = svcTrade.getString("SERVICE_ID");
			if (svc_config_codestr.indexOf(svcCode) == -1 || !StringUtils.equals(oprTag, "1")) {
				continue;
			}
			// 订单中的属性
			String elementId = svcTrade.getString("SERVICE_ID");
			String instId = svcTrade.getString("INST_ID");
			IData dgSvcData = new DataMap();
			dgSvcData.put("SVC_CODE", svcCode);
			// dgSvcCode = svcCode;

			for (int h = 0; h < attrTradeList.size(); h++) {
				IData attrTrade = attrTradeList.getData(h);
				if (elementId.equals(attrTrade.getString("ELEMENT_ID")) && instId.equals(attrTrade.getString("RELA_INST_ID"))) {
					if ("1".equals(attrTrade.getString("MODIFY_TAG"))) {
						if ("usrSessionPolicyCode".equals(attrTrade.getString("ATTR_CODE"))) {
							// dgPolicyValue = attrTrade.getAttrValue();
							dgSvcData.put("POLICY_VALUE", attrTrade.getString("ATTR_VALUE"));
							break;
						}
					}
				}
			}
			tdSvcDataList.add(dgSvcData); //
		}
		
		
		/**从资费开始校验**/
		for (String discntCode : tdDiscntList) {
			boolean isExistSvc = false;
			boolean isPolicyValiable = false;
			for (int i = 0; i < tdSvcDataList.size(); i++) {
				IData svcData = tdSvcDataList.getData(i);
				String svcCode = svcData.getString("SVC_CODE");
				String policyValue = svcData.getString("POLICY_VALUE");
				IDataset filterData = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode) + ",PARA_CODE2=" + svccode_config_map.get(svcCode) + ",PARA_CODE3=" + policyValue);
				IDataset svcFillter = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode) + ",PARA_CODE2=" + svccode_config_map.get(svcCode));
				if (!filterData.isEmpty()) {
					isPolicyValiable = true;
				}
				if (!svcFillter.isEmpty()) {
					isExistSvc = true;
				}
			}
			if (!isExistSvc && dgDiscntList.size() == 0) {
				IDataset filterDataset = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode));
				String errorTips = filterDataset.first().getString("PARAM_NAME");
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "退订" + errorTips + "必须退订用户策略服务产品！");
				return true;
			}
			if (!isPolicyValiable && dgDiscntList.size() == 0) {
				IDataset filterDataset = DataHelper.filter(discnt_config_list, "PARAM_CODE=" + discnt_config_map.get(discntCode));
				String errorTips = filterDataset.first().getString("PARAM_NAME");
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "退订" + errorTips + "与用户策略服务产品，则用户策略服务产品的策略必须为 " + errorTips + "接入类型");
				return true;
			}
		}

		/**从服务开始校验**/
		for (int i = 0; i < tdSvcDataList.size(); i++) {
			IData svcData = tdSvcDataList.getData(i);
			String svcCode = svcData.getString("SVC_CODE");
			String policyValue = svcData.getString("POLICY_VALUE");
			if (StringUtils.isBlank(policyValue)) {
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "退订用户策略服务必须关闭策略服务产品策略！");
				return true;
			}
			boolean isExistDiscnt = false;
			for (String discntCode : tdDiscntList) {
				IDataset filterData = DataHelper.filter(discnt_config_list, "PARA_CODE2=" + svccode_config_map.get(svcCode) + ",PARA_CODE3=" + policyValue + ",PARAM_CODE=" + discnt_config_map.get(discntCode));
				if (IDataUtil.isNotEmpty(filterData)) {
					isExistDiscnt = true;
				}
			}
			if(!isExistDiscnt){
				IDataset filterDiscnt = DataHelper.filter(discnt_config_list, "PARA_CODE3="+policyValue);
				String errorTips = filterDiscnt.first().getString("PARAM_NAME");
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "退订" + errorTips + "用户服务策略，则用户必须退订 " + errorTips );
				return true;
			}
		}
		return false;
	}
	
	private boolean checkTactics(IData databus) throws Exception {
		String discnt_config_codestr = "20122485";
		
		//IDataset svcList = databus.getDataset("TF_B_TRADE_SVC");
		IDataset attrTradeList = databus.getDataset("TF_B_TRADE_ATTR");
		IDataset discntList = databus.getDataset("TF_B_TRADE_DISCNT");
		IDataset allAttrTradeList = databus.getDataset("TF_F_USER_ATTR_AFTER"); // 获取用户所有的属性
		//IDataset userDiscntList = databus.getDataset("TF_F_USER_DISCNT_AFTER");// 获得用户所有的资费
		//IDataset userSvcList = databus.getDataset("TF_F_USER_SVC_AFTER");
		String apnName = "";		//APNNAME
		String ServiceCode = "";
		String servicecode53 = "";	//53降速关停策略

		//IDataset attrTradeList1 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=usrSessionPolicyCode,MODIFY_TAG=0");
		//IDataset attrTradeList2 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=01");// 开通
		//IDataset attrTradeList3 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=03");// 变更
		//IDataset attrTradeList4 = DataHelper.filter(attrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=02");// 删除
		//IDataset attrTradeList5 = DataHelper.filter(allAttrTradeList, "INST_TYPE=S,ATTR_CODE=OperType,ATTR_VALUE=03");// 变更
		
		
		for (int i = 0; i < discntList.size(); i++) {
			IData discntTrade = discntList.getData(i);
			String oprTag = discntTrade.getString("MODIFY_TAG");
			String discntCode = discntTrade.getString("DISCNT_CODE");
			String instId = discntTrade.getString("INST_ID");
			if ("0".equals(oprTag)) {
				if (discnt_config_codestr.indexOf(discntCode) != -1) {
					//有对应优惠
					
					
					
					System.out.println("chenhh==验证达量降速是否为53降速关停类型策略");
					//验证达量降速是否为53降速关停类型策略-----
					IDataset attrTradeList02 = DataHelper.filter(attrTradeList, "RELA_INST_ID="+instId+",ATTR_CODE=ServiceCode");
					System.out.println("chenhh6==attrTradeList02:"+attrTradeList02);
					if (!attrTradeList02.isEmpty()){
						ServiceCode = attrTradeList02.getData(0).getString("ATTR_VALUE","");
						System.out.println("chenhh6==ServiceCode:"+ServiceCode);
						IDataset configList = CommparaInfoQry.getCommparaByCode1("CSM", "9053",ServiceCode,"53",null);	//查询53类型降速关停策略
						if (configList.isEmpty()){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "开通的达量限速策略类型不是53降速关停类型策略。");
							return true;
						}
					}
					//------------------------------
					
					
					
					
					
					
					//先判断是否开通或新增53策略，没有就取USER
					System.out.println("chenhh==限制开通非53类型限速关停策略");
					//限制开通非53类型限速关停策略------------------
					IDataset configList = CommparaInfoQry.getInfoParaCode3("CSM", "9053","S00010100085");	//53类型策略
					for (int j = 0; j < configList.size(); j++) {
						servicecode53 +=configList.getData(j).getString("PARAM_CODE","")+",";
					}
					System.out.println("chenhh6==servicecode53:"+servicecode53);
					System.out.println("chenhh6==attrTradeList:"+attrTradeList);
						IDataset attrTradeListSC = new DatasetList();	//取出所有 新增/变更 APN对应的ServiceCode
						for (int j = 0; j < attrTradeList.size(); j++) {
							IData attrTrade = attrTradeList.getData(j);
							String tag = attrTrade.getString("MODIFY_TAG");
							String attrCode = attrTrade.getString("ATTR_CODE");
							System.out.println("chenhh6==tag:"+tag);
							System.out.println("chenhh6==attrCode:"+attrCode);
							if (("0".equals(tag) || "2".equals(tag)) && "ServiceCode".equals(attrCode) ) {
								//IDataset attrTradeList03 = DataHelper.filter(attrTradeList, "RELA_INST_ID="+inst+",ATTR_CODE=ServiceCode");
								//attrTradeListSC.addAll(attrTradeList03);
								System.out.println("chenhh6==attrTrade:"+attrTrade);
								attrTradeListSC.add(attrTrade);
							}
						}
						System.out.println("chenhh6==attrTradeListSC:"+attrTradeListSC);
						for (int j = 0; j < attrTradeListSC.size(); j++) {
							String  servicecode = attrTradeListSC.getData(j).getString("ATTR_VALUE");
							if (servicecode53.indexOf(servicecode) == -1) {
								BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "限制开通/变更非53类型限速关停策略，请 开通/变更 53类型策略。");
								return true;
							}
						}
					//----------------------------
					
					
					System.out.println("chenhh==判断是否又开通53类型策略，没有就看USER");
					//判断是否又开通53类型策略，没有就看USER
					IDataset attrTradeList01 = DataHelper.filter(attrTradeList, "RELA_INST_ID="+instId+",ATTR_CODE=APNNAME");
					if (!attrTradeList01.isEmpty()) {
						apnName = attrTradeList01.getData(0).getString("ATTR_VALUE","");//得到APNNAME
						//IDataset tradeAttrList = DataHelper.filter(attrTradeList, "ATTR_CODE=APNNAME,ATTR_VALUE="+apnName);
						//IDataset userAttrList = DataHelper.filter(allAttrTradeList, "ATTR_CODE=APNNAME,ATTR_VALUE="+apnName);
						IDataset userAttrList = new DatasetList();		//已开通策略集合
						if (!allAttrTradeList.isEmpty()) {
							for (int j = 0; j < allAttrTradeList.size(); j++) {
								String attrCode = allAttrTradeList.getData(j).getString("ATTR_CODE");
								
								if ("ServiceCode".equals(attrCode)) {
									userAttrList.add(allAttrTradeList.getData(j));
								}
							}
						}
						System.out.println("chenhh6==userAttrList:"+userAttrList);
						for (int j = 0; j < userAttrList.size(); j++) {
							String relaInstId = userAttrList.getData(j).getString("RELA_INST_ID");
							//过滤其它APN策略
							IDataset userAttrList1 = DataHelper.filter(allAttrTradeList, "RELA_INST_ID="+relaInstId+",ATTR_CODE=APNNAME,ATTR_VALUE="+apnName);
							if (userAttrList1.isEmpty()) continue;
							
							String attrValue = userAttrList.getData(j).getString("ATTR_VALUE");
							System.out.println("chenhh6==attrValue:"+attrValue);
							if (servicecode53.indexOf(attrValue) == -1) {
								//USER_ATTR为非53策略就去看TRADE_ATTR是否有变更53策略
								IDataset tradeAttrList1 = DataHelper.filter(attrTradeList, "RELA_INST_ID="+relaInstId+",MODIFY_TAG=2,ATTR_CODE=ServiceCode");
								System.out.println("chenhh6==tradeAttrList1:"+tradeAttrList1);
								if (tradeAttrList1.isEmpty() || 
										servicecode53.indexOf(tradeAttrList1.getData(0).getString("ATTR_VALUE")) == -1) {
									BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180801, "当前开通达量降速的APN策略中含有非53降速关停类型策略，请先变更该APN策略为53降速关停类型策略。");
									return true;
								}
							}
						}
						
					}
					
					
					
				}
			}
		}
		
		return false;
	}
}
