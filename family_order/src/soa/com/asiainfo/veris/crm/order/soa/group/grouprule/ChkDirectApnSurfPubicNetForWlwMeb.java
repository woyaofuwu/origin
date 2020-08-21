
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
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
public class ChkDirectApnSurfPubicNetForWlwMeb extends BreBase implements IBREScript
{
	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkDiscntAttrCodeForWlwNbMeb.class);
    
    private String directGprsSvcIds = "";//专用数据通信服务
    private String carDirectGprsSvcIds = "";//车联网专用数据通信服务
    private String commonGprsSvcIds = "";//通用数据通信服务
    private String nbGprsSvcIds = "";//NB数据通信服务

	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkDirectApnSurfPubicNetForWlwMeb() >>>>>>>>>>>>>>>>>>");
        	logger.debug(databus);
        }

        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */
        String mainProductId = "";//获取用户主产品
        mainProductId = databus.getString("PRODUCT_ID");// 集团产品
        
        IDataset userElements = null;
        String userElementsStr = "";
        String subTransCode = databus.getString("X_SUBTRANS_CODE","");
        
        
        //批量进来的
        if(StringUtils.isNotBlank(subTransCode) 
        		&& StringUtils.equals(subTransCode, "GrpBat"))
        {
            userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            if (StringUtils.isNotBlank(userElementsStr))
            {
            	userElements = new DatasetList(userElementsStr);
            }            
        }
        else 
        {
            userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
            
            if(StringUtils.isBlank(userElementsStr))
            {
            	userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            }
            
            if (StringUtils.isNotBlank(userElementsStr))
            {
            	userElements = new DatasetList(userElementsStr);
            }
        }
		
        if (StringUtils.isNotBlank(userElementsStr))
        {
        	userElements = new DatasetList(userElementsStr);
        	if(IDataUtil.isNotEmpty(userElements))
        	{
        		String allDataServiceIds[] = combineParamCodeForSvc();
        		int size = userElements.size();
        		for (int i = 0; i < size; i++)
                {
        			IDataset svcTradeList = new DatasetList();
        			IDataset attrTradeList = new DatasetList();
        			
        			IData element = userElements.getData(i);
        			String eleTypeCode = element.getString("ELEMENT_TYPE_CODE","");
        			String modifyTag = element.getString("MODIFY_TAG","");
        			//String discntCode = element.getString("ELEMENT_ID","");
        			if((BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag)) 
        					&& BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode))
        			{
        				svcTradeList.add(element);
        				String attrParam = element.getString("ATTR_PARAM","");
        				if(StringUtils.isNotBlank(attrParam))
        				{
        					IDataset attrDataset = new DatasetList(attrParam);
        					if(IDataUtil.isNotEmpty(attrDataset))
            				{
            					attrTradeList.addAll(attrDataset);
            				}
        				}
        				//专用APN上公网业务的相关校验
        				boolean flag = checkDirectApnSurfPublicNetworkOrder(allDataServiceIds,svcTradeList,
        						attrTradeList,mainProductId,databus,errCode);
        				if(!flag)
        				{
        					return false;
        				}
        			}
                }
        	}
        }
        
		return true;
    }

	private String[] combineParamCodeForSvc() throws Exception
	{
		//2.物联网、车联网用户开通专用/通用数据通信服务、NB用户开通NB数据通信服务时,应校验其开通的APN产品范围和访问类型是否匹配
		StringBuilder directGprsSvcIdsb = new StringBuilder(100); //专用数据通信服务
		StringBuilder carDirectGprsSvcIdsb = new StringBuilder(100); //车联网专用数据通信服务
		StringBuilder commonGprsSvcIdsb = new StringBuilder(200); //通用数据通信服务
		StringBuilder nbGprsSvcIdsb = new StringBuilder(100); //NB数据通信服务
		//物联网专用数据通信服务
		IDataset directDataSvcConfig = CommparaInfoQry.getCommparaByCode1("CSM","9014",IotConstants.DIRECT_GPRS_SVC_CODE,"ZZZZ");
		//车联网专用数据通信服务
		IDataset carDirectDataSvcConfig = CommparaInfoQry.getCommparaByCode1("CSM","9014",IotConstants.CAR_DIRECT_GPRS_SVC_CODE,"ZZZZ");
		//物联网通用数据通信服
		IDataset commonDataSvcConfig = CommparaInfoQry.getCommparaByCode1("CSM","9014",IotConstants.COMM_GPRS_SVC_CODE,"ZZZZ");
		//NB-IOT数据通信服务
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
		
		directGprsSvcIds = directGprsSvcIdsb.toString();
		carDirectGprsSvcIds = carDirectGprsSvcIdsb.toString();
		commonGprsSvcIds = commonGprsSvcIdsb.toString();
		nbGprsSvcIds = nbGprsSvcIdsb.toString();
		String[] allDataServiceIds = directGprsSvcIdsb.append(carDirectGprsSvcIdsb).append(commonGprsSvcIdsb).append(nbGprsSvcIdsb).toString().split(",");
		
		return allDataServiceIds;
	}
	
	private boolean checkDirectApnSurfPublicNetworkOrder(String allDataServiceIds[],IDataset svcTradeList,
			IDataset attrTradeList,String mainProductId,IData databus,String errCode) throws Exception
	{
		String err = "";
		for(String gprsServiceId : allDataServiceIds){//所有数据通信服务循环校验
			IDataset addGprsServiceTradeList = DataHelper.filter(svcTradeList, "MODIFY_TAG=0,ELEMENT_ID="+gprsServiceId);
			if(IDataUtil.isNotEmpty(addGprsServiceTradeList)){
				//IDataset userDiscntsList = databus.getDataset("TF_F_USER_PRODUCT_AFTER");
				//String mainProductId = "";//获取用户主产品
				//for(int i = 0; i < userDiscntsList.size(); i++){
				//	if("1".equals(userDiscntsList.getData(i).getString("MAIN_TAG"))){
				//		mainProductId = userDiscntsList.getData(i).getString("PRODUCT_ID");
				//	}
				//}
				String pbossProductId = "";
				IDataset mainProductIdConfig = CommparaInfoQry.getCommNetInfo("CSM","9015", mainProductId);
				if(IDataUtil.isNotEmpty(mainProductIdConfig)){
					pbossProductId = mainProductIdConfig.first().getString("PARA_CODE1");
				}
				
				for(int i = 0; i < addGprsServiceTradeList.size(); i++){//同一数据通信服务可能存同时订购多个
					String serviceId = addGprsServiceTradeList.getData(i).getString("ELEMENT_ID");
					//String serviceInstId = addGprsServiceTradeList.getData(i).getString("INST_ID");
					String productRange = "";
					IDataset addGprsServiceApnAttrTradeList = DataHelper.filter(attrTradeList, "ATTR_CODE=APNNAME");
					
					if(IDataUtil.isNotEmpty(addGprsServiceApnAttrTradeList))
					{
						for(int m = 0; m < addGprsServiceApnAttrTradeList.size(); m++)
						{
							IData addGprsApnAttr = addGprsServiceApnAttrTradeList.getData(m);
							String gprsServiceApnName = addGprsApnAttr.getString("ATTR_VALUE");
							if(StringUtils.isNotBlank(gprsServiceApnName))
							{
								IDataset apnConfigInfos = CommparaInfoQry.getCommNetInfo("CSM","9031",gprsServiceApnName);
								if(IDataUtil.isEmpty(apnConfigInfos)){
									err = "开通的APN没有在系统中配置！APN："+gprsServiceApnName;
									BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode,err);
									return false;
								}
								IData apnConfigInfo = apnConfigInfos.first();
								productRange = apnConfigInfo.getString("PARA_CODE3"); //产品范围1：物联卡2：车联网3：NB-IoT可以填写多个，用逗号分隔例如：”1,2,3”
								if(directGprsSvcIds.indexOf(serviceId) >=0)
								{//物联网专用数据通信服务的APN访问类型必须是2：定向访问												
									if(!"2".equals(apnConfigInfo.getString("PARA_CODE2")))
									{
										err = "专用数据通信服务的APN访问类型必须是定向访问类型！APN："+gprsServiceApnName;
										BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,  errCode,err);
										return false;
									}	
									if(StringUtils.isNotBlank(productRange) && productRange.indexOf("1")<0)
									{
										err = "开通的APN产品范围不包括物联卡(机器卡、物联通)！APN："+gprsServiceApnName;
										BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,  errCode,err);
										return false;
									}
								}
								if(carDirectGprsSvcIds.indexOf(serviceId) >=0)
								{//车联网专用数据通信服务的APN访问类型必须是2：定向访问												
									if(!"2".equals(apnConfigInfo.getString("PARA_CODE2")))
									{
										err = "车联网专用数据通信服务的APN访问类型必须是定向访问类型！APN："+gprsServiceApnName;
										BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,  errCode,err);
										return false;
									}	
									if(StringUtils.isNotBlank(productRange) && productRange.indexOf("2")<0)
									{
										err = "开通的APN产品范围不包括车联网！APN："+gprsServiceApnName;
										BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,  errCode,err);
										return false;
									}
								}	
								if(commonGprsSvcIds.indexOf(serviceId) >=0)
								{//通用数据通信服务的APN访问类型必须是1：全量上公网（通用）											
									if(!"1".equals(apnConfigInfo.getString("PARA_CODE2")))
									{
										err = "通用数据通信服务的APN访问类型必须是全量上公网（通用）类型！APN："+gprsServiceApnName;
										BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,  errCode,err);
										return false;
									}							
									if(StringUtils.isNotBlank(productRange) && IotConstants.CAR_PRODUCT_CODE_P.equals(pbossProductId) && productRange.indexOf("2")<0)
									{
										err = "开通的APN产品范围不包括车联网！APN："+gprsServiceApnName;
										BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,  errCode,err);
										return false;
									}
									else if(StringUtils.isNotBlank(productRange) && !IotConstants.CAR_PRODUCT_CODE_P.equals(pbossProductId) && productRange.indexOf("1")<0)
									{
										err = "开通的APN产品范围不包括物联卡(机器卡、物联通)！APN："+gprsServiceApnName;
										BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,  errCode,err);
										return false;
									}
								}
								//3:NB用户开通NB数据通信服务时,应校验其开通的APN产品范围是否匹配
								if(nbGprsSvcIds.indexOf(serviceId) >=0)
								{											
							        if(StringUtils.isNotBlank(productRange) && productRange.indexOf("3")<0)
							        {
							        	err = "开通的APN产品范围不包括NB-IoT！APN："+gprsServiceApnName;
										BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode,err);
										return false;
							        }
								}
								
								//4.校验专用/通用数据通信服务/NB数据通信服务的APN与PCRF业务策略是否匹配(通用APN不校验)
								if("CMIOT_CMM2M_CMMTM".indexOf(gprsServiceApnName) < 0){
									IDataset addGprsServiceServiceCodeAttrTradeList = DataHelper.filter(attrTradeList, "ATTR_CODE=ServiceCode");
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
											err = "数据通信服务的APN与PCRF业务策略不匹配！APN："+gprsServiceApnName+"PCRF:"+pcrfValue;
											BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode,err);
											return false;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
}
