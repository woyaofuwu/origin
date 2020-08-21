
package com.asiainfo.veris.crm.order.soa.script.rule.iot;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.IotConstants;

/**
 * 关于下发物联网专用APN上公网业务支撑系统改造方案的通知 2018.8.13
 * 1:允许APN访问类型为“全量上公网”的APN开通物联网/车联网通用数据通信服务，不再限定为CMIOT、CMM2M(之前没有限制的不用处理)
 * 2:物联网、车联网用户开通专用/通用数据通信服务、NB用户开通NB数据通信服务时,应校验其开通的APN产品范围和访问类型是否匹配
 * 3:NB用户开通NB数据通信服务时,应校验其开通的APN产品范围是否匹配
 * 4:校验专用/通用数据通信服务/NB数据通信服务的APN与PCRF业务策略是否匹配（通用APN即CMIOT、CMM2M则无须校验）
 * 5:一个用户最多可同时开通五个专用数据通信服务和一个通用数据通信服务(如果原来有这个校验的，要改一下校验逻辑)
 * @author lihb3
 *
 */
public class CheckDirectApnSurfPublicNet extends BreBase implements IBREScript{
	
	private static final long serialVersionUID = 1L;

	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		//专用APN上公网业务的相关校验
		if(checkDirectApnSurfPublicNetworkOrder(databus)){
			return true;
		}
		
    	return false;
    }

	private boolean checkDirectApnSurfPublicNetworkOrder(IData databus) throws Exception {
		IDataset svcTradeList = databus.getDataset("TF_B_TRADE_SVC");
		IDataset attrTradeList = databus.getDataset("TF_B_TRADE_ATTR");
		
		//2.物联网、车联网用户开通专用/通用数据通信服务、NB用户开通NB数据通信服务时,应校验其开通的APN产品范围和访问类型是否匹配
		StringBuilder directGprsSvcIdsb = new StringBuilder(100); //专用数据通信服务
		StringBuilder carDirectGprsSvcIdsb = new StringBuilder(100); //车联网专用数据通信服务
		StringBuilder commonGprsSvcIdsb = new StringBuilder(200); //通用数据通信服务
		StringBuilder nbGprsSvcIdsb = new StringBuilder(100); //NB数据通信服务
		IDataset directDataSvcConfig = CommparaInfoQry.getCommparaByCode1("CSM","9014",IotConstants.DIRECT_GPRS_SVC_CODE,"ZZZZ");
		IDataset carDirectDataSvcConfig = CommparaInfoQry.getCommparaByCode1("CSM","9014",IotConstants.CAR_DIRECT_GPRS_SVC_CODE,"ZZZZ");
		IDataset commonDataSvcConfig = CommparaInfoQry.getCommparaByCode1("CSM","9014",IotConstants.COMM_GPRS_SVC_CODE,"ZZZZ");
		IDataset nbDataSvcConfig = CommparaInfoQry.getCommparaByCode1("CSM","9014",IotConstants.NB_GPRS_SVC_CODE,"ZZZZ");
		if(IDataUtil.isNotEmpty(directDataSvcConfig)){
			for(int i = 0; i < directDataSvcConfig.size(); i++){
				directGprsSvcIdsb.append(directDataSvcConfig.getData(i).getString("PARAM_CODE")).append(",");
			}
		}		
		if(IDataUtil.isNotEmpty(carDirectDataSvcConfig)){
			for(int i = 0; i < carDirectDataSvcConfig.size(); i++){
				carDirectGprsSvcIdsb.append(carDirectDataSvcConfig.getData(i).getString("PARAM_CODE")).append(",");
			}
		}		
		if(IDataUtil.isNotEmpty(commonDataSvcConfig)){
			for(int i = 0; i < commonDataSvcConfig.size(); i++){
				commonGprsSvcIdsb.append(commonDataSvcConfig.getData(i).getString("PARAM_CODE")).append(",");
			}
		}		
		if(IDataUtil.isNotEmpty(nbDataSvcConfig)){
			for(int i = 0; i < nbDataSvcConfig.size(); i++){
				nbGprsSvcIdsb.append(nbDataSvcConfig.getData(i).getString("PARAM_CODE")).append(",");
			}
		}
		
		String directGprsSvcIds = directGprsSvcIdsb.toString();
		String carDirectGprsSvcIds = carDirectGprsSvcIdsb.toString();
		String commonGprsSvcIds = commonGprsSvcIdsb.toString();
		String nbGprsSvcIds = nbGprsSvcIdsb.toString();
		String[] allDataServiceIds = directGprsSvcIdsb.append(carDirectGprsSvcIdsb).append(commonGprsSvcIdsb).append(nbGprsSvcIdsb).toString().split(","); 
		for(String gprsServiceId : allDataServiceIds){//所有数据通信服务循环校验
			IDataset addGprsServiceTradeList = DataHelper.filter(svcTradeList, "MODIFY_TAG=0,SERVICE_ID="+gprsServiceId);
			if(IDataUtil.isNotEmpty(addGprsServiceTradeList)){
				IDataset userDiscntsList = databus.getDataset("TF_F_USER_PRODUCT_AFTER");
				String mainProductId = "";//获取用户主产品
				for(int i = 0; i < userDiscntsList.size(); i++){
					if("1".equals(userDiscntsList.getData(i).getString("MAIN_TAG"))){
						mainProductId = userDiscntsList.getData(i).getString("PRODUCT_ID");
					}
				}
				String pbossProductId = "";
				IDataset mainProductIdConfig = CommparaInfoQry.getCommNetInfo("CSM","9015", mainProductId);
				if(IDataUtil.isNotEmpty(mainProductIdConfig)){
					pbossProductId = mainProductIdConfig.first().getString("PARA_CODE1");
				}
				
				for(int i = 0; i < addGprsServiceTradeList.size(); i++){//同一数据通信服务可能存同时订购多个
					String serviceId = addGprsServiceTradeList.getData(i).getString("SERVICE_ID");
					String serviceInstId = addGprsServiceTradeList.getData(i).getString("INST_ID");
					String productRange = "";
					IDataset addGprsServiceApnAttrTradeList = DataHelper.filter(attrTradeList, "MODIFY_TAG=0,ATTR_CODE=APNNAME,RELA_INST_ID="+serviceInstId);	
					String gprsServiceApnName = addGprsServiceApnAttrTradeList.first().getString("ATTR_VALUE");
					IDataset apnConfigInfos = CommparaInfoQry.getCommNetInfo("CSM","9031",gprsServiceApnName);
					if(IDataUtil.isEmpty(apnConfigInfos)){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "开通的APN没有在系统中配置！APN："+gprsServiceApnName);
						return true;
					}
					IData apnConfigInfo = apnConfigInfos.first();
					productRange = apnConfigInfo.getString("PARA_CODE3"); //产品范围1：物联卡2：车联网3：NB-IoT可以填写多个，用逗号分隔例如：”1,2,3”
					if(directGprsSvcIds.indexOf(serviceId) >=0){//物联网专用数据通信服务的APN访问类型必须是2：定向访问												
						if(!"2".equals(apnConfigInfo.getString("PARA_CODE2"))){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "专用数据通信服务的APN访问类型必须是定向访问类型！APN："+gprsServiceApnName);
							return true;
						}	
						if(StringUtils.isNotBlank(productRange) && productRange.indexOf("1")<0){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "开通的APN产品范围不包括物联卡(机器卡、物联通)！APN："+gprsServiceApnName);
							return true;
						}
					}		
					if(carDirectGprsSvcIds.indexOf(serviceId) >=0){//车联网专用数据通信服务的APN访问类型必须是2：定向访问												
						if(!"2".equals(apnConfigInfo.getString("PARA_CODE2"))){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "车联网专用数据通信服务的APN访问类型必须是定向访问类型！APN："+gprsServiceApnName);
							return true;
						}	
						if(StringUtils.isNotBlank(productRange) && productRange.indexOf("2")<0){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "开通的APN产品范围不包括车联网！APN："+gprsServiceApnName);
							return true;
						}
					}	
					if(commonGprsSvcIds.indexOf(serviceId) >=0){//通用数据通信服务的APN访问类型必须是1：全量上公网（通用）											
						if(!"1".equals(apnConfigInfo.getString("PARA_CODE2"))){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "通用数据通信服务的APN访问类型必须是全量上公网（通用）类型！APN："+gprsServiceApnName);
							return true;
						}							
						if(StringUtils.isNotBlank(productRange) && IotConstants.CAR_PRODUCT_CODE_P.equals(pbossProductId) && productRange.indexOf("2")<0){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "开通的APN产品范围不包括车联网！APN："+gprsServiceApnName);
							return true;
						}else if(StringUtils.isNotBlank(productRange) && !IotConstants.CAR_PRODUCT_CODE_P.equals(pbossProductId) && productRange.indexOf("1")<0){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "开通的APN产品范围不包括物联卡(机器卡、物联通)！APN："+gprsServiceApnName);
							return true;
						}
					}
					//3:NB用户开通NB数据通信服务时,应校验其开通的APN产品范围是否匹配
					if(nbGprsSvcIds.indexOf(serviceId) >=0){											
				        if(StringUtils.isNotBlank(productRange) && productRange.indexOf("3")<0){
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "开通的APN产品范围不包括NB-IoT！APN："+gprsServiceApnName);
							return true;
				        }
					}
					
					//4.校验专用/通用数据通信服务/NB数据通信服务的APN与PCRF业务策略是否匹配(通用APN不校验)
					if("CMIOT_CMM2M_CMMTM".indexOf(gprsServiceApnName) < 0){
						IDataset addGprsServiceServiceCodeAttrTradeList = DataHelper.filter(attrTradeList, "MODIFY_TAG=0,ATTR_CODE=ServiceCode,RELA_INST_ID="+serviceInstId);
						if(IDataUtil.isNotEmpty(addGprsServiceServiceCodeAttrTradeList)){
							String pcrfValue = addGprsServiceServiceCodeAttrTradeList.first().getString("ATTR_VALUE");
							boolean apnAndPrrfCheck = false;
							IDataset apnAndPcrfConfigInfos = CommparaInfoQry.getCommNetInfo("CSM","9032",pcrfValue);
							if(IDataUtil.isNotEmpty(apnAndPcrfConfigInfos)){
								for(int k = 0; k < apnAndPcrfConfigInfos.size(); k++){//同一策略值可能对应多个APN
									if(gprsServiceApnName.equals(apnAndPcrfConfigInfos.getData(k).getString("PARA_CODE5"))){
										apnAndPrrfCheck = true;
										break;
									}
								}
							}
							if(!apnAndPrrfCheck){
								BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180813, "数据通信服务的APN与PCRF业务策略不匹配！APN："+gprsServiceApnName+"PCRF:"+pcrfValue);
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
}
