package com.asiainfo.veris.crm.order.soa.script.rule.iot;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.IotConstants;

/**
 * 关于下发物联网达量降速业务支撑系统改造方案的通知 2018.8.8
 * 1:只有开通4G服务且流量月套餐在100M以上（含）的用户才能订购自动达量降速（月包）产品
 * 2,每个APN只可订购一个降速产品
 * 3,订购降速产品的时候,必须有同一APN的流量套餐及数据通信服务,且数据通信服务的PCRF如果为空，则需要同步开通降速策略，
 *   如果不为空则需要为关停降速类策略.
 * 4,退订降速策略,需同时退订速产品
 * 5:降速产品关联的PCRF业务策略必须为关停降速类策略
 * 6:加入流量共享的APN不允许订购自动达量降速产品
 * 7:退流量套餐，如果有对应的降速资费，要同时退订。
 * @author lihb3
 *
 */
public class CheckSlowdownDataRule extends BreBase implements IBREScript{
	
	private static final long serialVersionUID = 1L;

	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {

		IDataset mainData = databus.getDataset("TF_B_TRADE");
    	String brandCode = mainData.getData(0).getString("BRAND_CODE","");
    	
    	if(!"PWLW".equals(brandCode))
    	{
			return false;//仅处理物联网的校验
		}
		
		//达量降速业务的相关校验
		if(checkSlowdownData(databus)){
			return true;
		}
		
    	return false;
    }

	private boolean checkSlowdownData(IData databus) throws Exception {
		//获取自动达量降速（月包）产品的资费编码
		IDataset autoSlowdownDiscntConfig = CommparaInfoQry.getInfoParaCode1_2("CSM","9013","I00010101602","I00010101010");
		if(IDataUtil.isEmpty(autoSlowdownDiscntConfig)){
			return false;
		}
		
		//获取客户自发起降速产品的资费编码
		IDataset manualSlowdownDiscntConfig = CommparaInfoQry.getInfoParaCode1_2("CSM","9013","I00010101603","I00010101010");
		if(IDataUtil.isEmpty(manualSlowdownDiscntConfig)){
			return false;
		}
		
		StringBuilder autoSlowdownDiscntsb = new StringBuilder(100); 
		for(int i = 0; i < autoSlowdownDiscntConfig.size(); i++){
			autoSlowdownDiscntsb.append(autoSlowdownDiscntConfig.getData(i).getString("PARAM_CODE")).append(",");
		}
		
		StringBuilder manualSlowdownDiscntsb = new StringBuilder(100); 
		for(int i = 0; i < manualSlowdownDiscntConfig.size(); i++){
			manualSlowdownDiscntsb.append(manualSlowdownDiscntConfig.getData(i).getString("PARAM_CODE")).append(",");
		}
		
		String autoSlowdownDiscnts = autoSlowdownDiscntsb.toString();
		String allSlowdownDiscnts = autoSlowdownDiscntsb.append(manualSlowdownDiscntsb).toString();
		String[] autoSlowdownDiscntCodes = autoSlowdownDiscnts.split(",");
				
		IDataset attrTradeList = databus.getDataset("TF_B_TRADE_ATTR");
		IDataset discntTradeList = databus.getDataset("TF_B_TRADE_DISCNT");
    	IDataset userDiscntList = databus.getDataset("TF_F_USER_DISCNT_AFTER");// 获得用户所有的资费
    	IDataset userAttrList = databus.getDataset("TF_F_USER_ATTR_AFTER");// 获得用户所有的属性
    	IDataset userApnList = DataHelper.filter(userAttrList, "ATTR_CODE=APNNAME");
				
		//1.只有开通4G服务且流量月套餐在100M以上（含）的用户才能订购自动达量降速（月包）产品	
		IDataset autoSlDiscntTradeDatas = new DatasetList();//自动降速资费数据
		for(String autoSlowdownDiscntID : autoSlowdownDiscntCodes){
			IDataset autoSLdiscntTradeList = DataHelper.filter(discntTradeList, "MODIFY_TAG=0,DISCNT_CODE="+autoSlowdownDiscntID);
			if(IDataUtil.isNotEmpty(autoSLdiscntTradeList)){		
				autoSlDiscntTradeDatas.add(autoSLdiscntTradeList.first());
				//先判断流量套餐
				boolean gprsSizeCheck = false;
				for(int i = 0; i < userDiscntList.size(); i++){
					IData config9013 = IotConstants.IOT_DISCNT_CONFIG.getData(userDiscntList.getData(i).getString("DISCNT_CODE"));
					if (IDataUtil.isEmpty(config9013)){
						continue;
					}						
					String para18 = config9013.getString(IotConstants.IOT_GPRS_SIZE); //这里存放资费包含的数据流量大小
					if(StringUtils.isNotBlank(para18) && para18.length() > 2){
						if(para18.endsWith("GB") || Integer.parseInt(para18.substring(0, para18.length()-2)) >= 100){
							gprsSizeCheck = true;
							break;
						}
					}
				}
				
				String  autoSLdiscntInstId = autoSLdiscntTradeList.first().getString("INST_ID");		
				IDataset autoSlowdownDiscntApnList = DataHelper.filter(userAttrList, "MODIFY_TAG=0,ATTR_CODE=APNNAME,RELA_INST_ID="+autoSLdiscntInstId);				
					
				//再判断数据通信服务的APN
				boolean serviceApncheck = false;
				if(gprsSizeCheck){//因为降速产品和数据通信服务的APN需要一致，所以可以先判断资费的
					if(IDataUtil.isNotEmpty(autoSlowdownDiscntApnList) && !"CMMTM".equals(autoSlowdownDiscntApnList.first().getString("ATTR_VALUE"))){
						serviceApncheck = true;
					}
				}
				
				if(!gprsSizeCheck || !serviceApncheck){
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "只有开通4G服务且流量月套餐在100M以上（含）的用户才能订购自动达量降速（月包）产品!");
					return true;	
				}	
			}
		}
		
		//2:每个APN只可订购一个降速产品
//		IDataset slDiscntTradeDatas = new DatasetList();//所有降速资费数据		
//		String[] originApns = new String[20];
//		for(int i = 0; i < userDiscntList.size(); i++){
//			IData userDiscntdata = userDiscntList.getData(i);
//			String discntCode = userDiscntdata.getString("DISCNT_CODE");
//			if(!"1".equals(userDiscntdata.getString("MODIFY_TAG")) && allSlowdownDiscnts.indexOf(discntCode)>=0){//降速产品
//				slDiscntTradeDatas.add(userDiscntdata);
//				String slDiscntTradeInstId = userDiscntdata.getString("INST_ID");//降速资费的InstId
//				IDataset slowdownDiscntApnList = DataHelper.filter(userAttrList, "ATTR_CODE=APNNAME,RELA_INST_ID="+slDiscntTradeInstId);
//				String slDiscntApn = slowdownDiscntApnList.first().getString("ATTR_VALUE");
//				for(String originApn : originApns){
//					if(slDiscntApn.equals(originApn)){
//						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "每个APN只可订购一个降速产品！重复APN："+slDiscntApn);
//						return true;
//					}
//				}
//				originApns[i] = slDiscntApn;
//			}
//		}
		
		//3.订购降速产品的时候,必须有同一APN的流量套餐及数据通信服务,且数据通信服务的PCRF必须为空，并需要同步开通降速策略。
//		for(int i = 0; i < slDiscntTradeDatas.size(); i++){
//			IData discntTradeData = slDiscntTradeDatas.getData(i);
//			String slDiscntTradeInstId = discntTradeData.getString("INST_ID");//降速资费的InstId
//			IDataset slowdownDiscntApnList = DataHelper.filter(userAttrList, "ATTR_CODE=APNNAME,RELA_INST_ID="+slDiscntTradeInstId);
//			String slDiscntApn = slowdownDiscntApnList.first().getString("ATTR_VALUE");
//			IDataset slowdownDiscntServiceCodeList = DataHelper.filter(userAttrList, "ATTR_CODE=ServiceCode,RELA_INST_ID="+slDiscntTradeInstId);
//			String slDiscntServiceCode = slowdownDiscntServiceCodeList.first().getString("ATTR_VALUE");
//			boolean existsSameApnGprsDiscnt = false; //是否存在同一APN的流量套餐
//			boolean existsSameApnGprsSvc = false; //是否存在同一APN的数据通信服务
//			boolean existsSameApnGprsSvcPcrf = false;//是否同时订购了同一APN的数据通信服务PCRF策略
//			for(int j = 0; j < userApnList.size(); j++){
//				IData attrTradeData = userApnList.getData(j);
//				String userApnAttrRelaInstId = attrTradeData.getString("RELA_INST_ID");
//				String userApnAttrValue = attrTradeData.getString("ATTR_VALUE");
//				String userApnAttrInstType = attrTradeData.getString("INST_TYPE");
//				if(userApnAttrValue.equals(slDiscntApn) && !userApnAttrRelaInstId.equals(slDiscntTradeInstId) && "D".equals(userApnAttrInstType)){
//					existsSameApnGprsDiscnt = true;
//				}
//				if(userApnAttrValue.equals(slDiscntApn) && "S".equals(userApnAttrInstType)){
//					existsSameApnGprsSvc = true;
//					IDataset addDiscntApnAttrTradeList = DataHelper.filter(attrTradeList, "MODIFY_TAG=0,ATTR_CODE=ServiceCode,INST_TYPE=S,ATTR_VALUE="+slDiscntServiceCode);
//					if(IDataUtil.isNotEmpty(addDiscntApnAttrTradeList)){						
//						existsSameApnGprsSvcPcrf = true;
//					}else{//没有新增服务的降速策略则判断是否原来就有未退订的关停策略
//						IDataset userServiceCodeAttrList = DataHelper.filter(userAttrList, "MODIFY_TAG=USER,ATTR_CODE=ServiceCode,INST_TYPE=S,ATTR_VALUE="+slDiscntServiceCode);
//						if(IDataUtil.isNotEmpty(userServiceCodeAttrList)){
//							existsSameApnGprsSvcPcrf = true;
//						}
//					}
//				}				
//				if(existsSameApnGprsDiscnt && existsSameApnGprsSvc && existsSameApnGprsSvcPcrf){
//					break;
//				}
//			}
//			
//			if(!existsSameApnGprsDiscnt){
//				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "没有找到与降速产品同一APN的流量套餐,APNNAME:"+slDiscntApn);
//				return true;
//			}
//			if(!existsSameApnGprsSvc){
//				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "没有找到与降速产品同一APN的数据通信服务,APNNAME:"+slDiscntApn);
//				return true;
//			}
//			if(!existsSameApnGprsSvcPcrf){
//				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "没有找到与降速产品同一ServiceCode的数据通信服务,ServiceCode:"+slDiscntServiceCode);
//				return true;
//			}
//		}
				
		//4.1,退订降速产品，需同时退对应的降速策略;
//		String[] allSlowdownDiscntCodes = allSlowdownDiscnts.split(","); 
//		for(String slowdownDiscntCode : allSlowdownDiscntCodes){
//			IDataset cancelSlowdownDiscntTradeList = DataHelper.filter(discntTradeList, "MODIFY_TAG=1,DISCNT_CODE="+slowdownDiscntCode);
//			if(IDataUtil.isNotEmpty(cancelSlowdownDiscntTradeList)){
//				for(int i = 0; i < cancelSlowdownDiscntTradeList.size(); i++){
//					IDataset cancelDiscntServiceCodeAttrTradeList = DataHelper.filter(attrTradeList, "MODIFY_TAG=1,ATTR_CODE=ServiceCode,INST_TYPE=D,RELA_INST_ID="+cancelSlowdownDiscntTradeList.getData(i).getString("INST_ID"));//这条数据肯定有
//					IDataset cancelSvcServiceCodeAttrTradeList = DataHelper.filter(attrTradeList, "MODIFY_TAG=1,ATTR_CODE=ServiceCode,INST_TYPE=S,ATTR_VALUE="+cancelDiscntServiceCodeAttrTradeList.getData(i).getString("ATTR_VALUE"));					
//				    if(IDataUtil.isEmpty(cancelSvcServiceCodeAttrTradeList)){
//						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "退订降速产品，需同时退对应的降速策略.降速产品编码："+slowdownDiscntCode);
//						return true;
//				    }
//				}				
//			}
//		}
		
		//4.2 退订降速策略,需同时退订降速产品.退订降速策略分2种情况，一种是退订策略，一种是退订数据通信服务
//		IDataset cancelSvcServiceCodeAttrTradeList = DataHelper.filter(attrTradeList, "MODIFY_TAG=1,ATTR_CODE=ServiceCode,INST_TYPE=S");
//		if(IDataUtil.isNotEmpty(cancelSvcServiceCodeAttrTradeList)){
//			for(int i = 0; i < cancelSvcServiceCodeAttrTradeList.size(); i++){
//				String slAttrValue = cancelSvcServiceCodeAttrTradeList.getData(i).getString("ATTR_VALUE");
//				IData config9032 = IotConstants.IOT_PCRF_CONFIG.getData(slAttrValue);
//				if(IDataUtil.isNotEmpty(config9032) && "52".equals(config9032.getString("PARA_CODE2"))){
//					IDataset cancelDiscntServiceCodeAttrTradeList = DataHelper.filter(attrTradeList, "MODIFY_TAG=1,ATTR_CODE=ServiceCode,INST_TYPE=D,ATTR_VALUE="+slAttrValue);
//					if(IDataUtil.isEmpty(cancelDiscntServiceCodeAttrTradeList)){
//						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "退订降速策略,需同时退订降速产品!");
//						return true;
//					}
//				}
//			}
//		}
		
		//5:降速产品关联的PCRF业务策略必须为关停降速类策略
//		for(int i = 0; i < slDiscntTradeDatas.size(); i++){
//			IData discntTradeData = slDiscntTradeDatas.getData(i);
//			IDataset userRelaDiscntServiceCodeList = DataHelper.filter(userAttrList, "ATTR_CODE=ServiceCode,RELA_INST_ID="+discntTradeData.getString("INST_ID"));
//			String discntServiceCode = userRelaDiscntServiceCodeList.first().getString("ATTR_VALUE");
//			IDataset userRelaDiscntApnList = DataHelper.filter(userAttrList, "ATTR_CODE=APNNAME,RELA_INST_ID="+discntTradeData.getString("INST_ID"));
//			String discntApnName = userRelaDiscntApnList.first().getString("ATTR_VALUE");
//			IData config9032 = IotConstants.IOT_PCRF_CONFIG.getData(discntServiceCode);
//			if(IDataUtil.isEmpty(config9032)){
//				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "此策略值的系统配置不存在"+discntServiceCode);
//				return true;
//			}		
//			if(!discntApnName.equals(config9032.getString("PARA_CODE5"))){
//				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "策略编码"+discntServiceCode+"与APNNAME"+discntApnName+"不匹配！");
//				return true;
//			}
//			if(!"52".equals(config9032.getString("PARA_CODE2"))){
//				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "策略编码"+discntServiceCode+"的用途不是关停降速!");
//				return true;
//			}
//		}
		
		//6:加入流量共享的APN不允许订购自动达量降速产品
//		for(int i = 0; i < autoSlDiscntTradeDatas.size(); i++){
//			IData discntTradeData = autoSlDiscntTradeDatas.getData(i);
//			String slDiscntRelaInstId = discntTradeData.getString("INST_ID");
//			IDataset slowdownDiscntApnList = DataHelper.filter(userAttrList, "ATTR_CODE=APNNAME,RELA_INST_ID="+slDiscntRelaInstId);
//			String slDiscntApn = slowdownDiscntApnList.first().getString("ATTR_VALUE");
//			String  userGprsDiscntInstId = ""; //获得降速产品对应的流量套餐的INSTID
//			for(int j = 0; j < userApnList.size(); j++){
//				IData attrTradeData = userApnList.getData(j);
//				String userdiscntApn = attrTradeData.getString("ATTR_VALUE");
//				if(slDiscntApn.equals(userdiscntApn) && !slDiscntRelaInstId.equals(attrTradeData.getString("RELA_INST_ID"))){
//					userGprsDiscntInstId = attrTradeData.getString("RELA_INST_ID");
//					break;
//				}
//			}
//						
//			IDataset canShareAttrTradeList = DataHelper.filter(userAttrList, "ATTR_CODE=CanShare,RELA_INST_ID="+userGprsDiscntInstId);
//			if(IDataUtil.isNotEmpty(canShareAttrTradeList)){
//				String canShare = canShareAttrTradeList.first().getString("ATTR_VALUE");
//				if("1".equals(canShare)){
//					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "加入流量共享的APN不允许订购自动达量降速产品,APNNAME:"+slDiscntApn);
//					return true;
//				}
//			}
//		}
		
		//7:退流量套餐，如果有对应的降速资费，要同时退订
//		IDataset cancelGprsDiscntTradeList = DataHelper.filter(discntTradeList, "MODIFY_TAG=1");
//		if(IDataUtil.isNotEmpty(cancelGprsDiscntTradeList)){
//			for(int i = 0; i < cancelGprsDiscntTradeList.size(); i++){
//				IData discntTradeData = cancelGprsDiscntTradeList.getData(i);
//				String discntCode = discntTradeData.getString("DISCNT_CODE");
//				IData config9013 = IotConstants.IOT_DISCNT_CONFIG.getData(discntCode);
//				if (IDataUtil.isEmpty(config9013)){
//					continue;
//				}						
//				String para18 = config9013.getString(IotConstants.IOT_GPRS_SIZE); //这里存放资费包含的数据流量大小
//				if(StringUtils.isNotBlank(para18) && para18.endsWith("B")){
//					String discntInstId = discntTradeData.getString("INST_ID");
//					IDataset cancelDiscntApnAttrTradeList = DataHelper.filter(attrTradeList, "MODIFY_TAG=1,INST_TYPE=D,ATTR_CODE=APNNAME,RELA_INST_ID="+discntInstId);
//					if(IDataUtil.isNotEmpty(cancelDiscntApnAttrTradeList)){
//						String discntApn = cancelDiscntApnAttrTradeList.first().getString("ATTR_VALUE");//流量套餐的apn
//						IDataset cancelSlDiscntApnAttrTradeList = DataHelper.filter(attrTradeList, "MODIFY_TAG=1,INST_TYPE=D,ATTR_CODE=APNNAME,ATTR_VALUE="+discntApn);
//						boolean cancelGprsDiscntCheck = false;
//						for(int j = 0; j < cancelSlDiscntApnAttrTradeList.size(); j++){
//							IData slDiscntTradeData = cancelSlDiscntApnAttrTradeList.getData(i);
//							if(!discntInstId.equals(slDiscntTradeData.getString("RELA_INST_ID"))){//apn相同，但是对应的资费不相同								
//								cancelGprsDiscntCheck = true;
//								break;
//							}
//						}	
//						if(!cancelGprsDiscntCheck){
//							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180810, "退订流量套餐必须同时退订对应的降速产品,流量套餐:"+discntCode);
//							return true;
//						}
//					}
//				}
//			}
//		}
		
		return false;
	}
	
}