
package com.asiainfo.veris.crm.order.soa.script.rule.iot;

import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bre.base.BreBase;
import com.ailk.bre.databus.BreRuleParam;
import com.ailk.bre.script.IBREScript;
import com.ailk.bre.tools.BreFactory;
import com.ailk.bre.tools.BreTipsHelp;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class IotCheck extends BreBase implements IBREScript
{
    //正向查询（通过CRM编码查询配置）
	public static Map<String, IData> DISCNT_CONFIG_MAP = new HashMap<String, IData>();

	public static Map<String, IData> SVC_CONFIG_MAP = new HashMap<String, IData>();

	public static Map<String, IData> PRODUCT_CONFIG_MAP = new HashMap<String, IData>();

	public static Map<String, IData> DISCNT_CONFIG_RATE = new HashMap<String, IData>();
	
	  //反向查询（通过物联网平台的编码查询配置）
	   public static Map<String, IData> WLW_DISCNT_CONFIG_MAP = new HashMap<String, IData>();

	    public static Map<String, IData> WLW_SVC_CONFIG_MAP = new HashMap<String, IData>();

	    public static Map<String, IData> WLW_PRODUCT_CONFIG_MAP = new HashMap<String, IData>();

	    public static Map<String, IData> WLW_DISCNT_CONFIG_RATE = new HashMap<String, IData>();

	static {
		try {
			PRODUCT_CONFIG_MAP = loadConfig("9015");
			SVC_CONFIG_MAP = loadConfig("9014");
			DISCNT_CONFIG_MAP = loadConfig("9013");
			DISCNT_CONFIG_RATE = loadConfig("9018");
			
			WLW_PRODUCT_CONFIG_MAP = loadConfigWLW("9015");
			WLW_SVC_CONFIG_MAP = loadConfigWLW("9014");
			WLW_DISCNT_CONFIG_MAP = loadConfigWLW("9013");
			WLW_DISCNT_CONFIG_RATE = loadConfigWLW("9018");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, IData> loadConfig(String paramAttr) throws Exception {
		Map<String, IData> configMap = new HashMap<String, IData>();
		IDataset configList = CommparaInfoQry.getCommByParaAttr("CSM", paramAttr, "ZZZZ");
		for (int i = 0; i < configList.size(); i++) {
			IData config = configList.getData(i);
			configMap.put(config.getString("PARAM_CODE"), config);
		}
		return configMap;
	}
	
	public static Map<String, IData> loadConfigWLW(String paramAttr) throws Exception {
        Map<String, IData> configMap = new HashMap<String, IData>();
        IDataset configList = CommparaInfoQry.getCommByParaAttr("CSM", paramAttr, "ZZZZ");
        for (int i = 0; i < configList.size(); i++) {
            IData config = configList.getData(i);
            configMap.put(config.getString("PARA_CODE1"), config);
        }
        return configMap;
    }
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
    	IDataset mainData = databus.getDataset("TF_B_TRADE");
    	String brandCode = mainData.getData(0).getString("BRAND_CODE","");
    	if(!"PWLW".equals(brandCode)){
			return false;//仅处理物联网的校验
		}	
    	if(IDataUtil.isNotEmpty(mainData)){
    		
			// NB-IOT的校验
			if(checkNbIot(databus)) {
				return true;
			}
			// 机器卡定向流量资费的校验
			if(checkCardDirectIotDiscnt(databus)) {
				return true;
			}
    	}
    	return false;
    }
    
    /**
     * NB-IOT的校验
     * 
     * @param databus
     * @return
     * @throws Exception
     */
    private boolean checkNbIot(IData databus) throws Exception {
    	IDataset discntList = databus.getDataset("TF_B_TRADE_DISCNT");
		IDataset svcList = databus.getDataset("TF_B_TRADE_SVC");
		IData smsDiscnt = null;//短信年包
		IData dataDiscnt = null;//流量年包
		IData testDiscnt = null;//测试期套餐
		IData nbIotBaseSvc = null;//数据通信服务
		IData nbIotSmsSvc = null;//短信基础通信服务
		if (IDataUtil.isNotEmpty(discntList)) {
			for (int k = 0; k < discntList.size(); k++) {
				IData tradeDiscnt = discntList.getData(k);
				IData discntConfig = DISCNT_CONFIG_MAP.get(tradeDiscnt.getString("DISCNT_CODE"));
				if (discntConfig != null) {
					if ("I00011100002".equals(discntConfig.getString("PARA_CODE2"))) {
						// 获取流量年包
						dataDiscnt = tradeDiscnt;
					} else if ("I00011100005".equals(discntConfig.getString("PARA_CODE2"))) {
						// 获取短信年包
						smsDiscnt = tradeDiscnt;
					} else if ("I00011100012".equals(discntConfig.getString("PARA_CODE2"))) {
						// 获取测试期套餐
						testDiscnt = tradeDiscnt;
					}
				}
			}
		}
		if (IDataUtil.isNotEmpty(svcList)) {
			
			for (int k = 0; k < svcList.size(); k++) {
				IData tradeSvc = svcList.getData(k);
				IData svcConfig = SVC_CONFIG_MAP.get(tradeSvc.getString("SERVICE_ID"));
				if (svcConfig != null) {
					if (svcConfig.getString("PARA_CODE3").equals("S00011100009")) {
						//获取短信基础通信服务
						nbIotSmsSvc = tradeSvc;
					} else if (svcConfig.getString("PARA_CODE3").equals("S00011100008")) {
						//获取数据通信服务
						nbIotBaseSvc = tradeSvc;
					}
				}
			}
		}
		//校验优惠
		if(checkNbIotDiscnt(databus, dataDiscnt, smsDiscnt, testDiscnt)) {
			return true;
		}
		//校验服务
		if(checkNbIotSvc(databus, smsDiscnt, testDiscnt, nbIotBaseSvc, nbIotSmsSvc)) {
			return true;
		}
		return false;
	}
	/**
	 * NB-IOT优惠的校验
	 * 
	 * @param databus
	 * @param dataDiscnt
	 * @param smsDiscnt
	 * @param testDiscnt
	 * @return
	 * @throws Exception
	 */
	private boolean checkNbIotDiscnt(IData databus, IData dataDiscnt, IData smsDiscnt, IData testDiscnt) throws Exception {
		if (IDataUtil.isNotEmpty(dataDiscnt)) {
			// 流量年包的有效时间必须为12个自然月的整数倍。例如：生效时间为2018.1，订购2年套餐包，则失效时间必须为2019.12.31 23:59:59
			if (checkRuleEndDate(databus, dataDiscnt)) {
				return true;
			}

			// 短信年包套餐的失效依赖于流量年包，且时间须与流量年包套餐的失效时间一致
			if (IDataUtil.isNotEmpty(smsDiscnt) && checkNbIotSmsDiscntEndDate(databus, dataDiscnt, smsDiscnt)) {
				return true;
			}
		} else {
			// 短信年包依赖于流量年包，单独订购报错，还要考虑是否用户已经订购
			if (IDataUtil.isNotEmpty(smsDiscnt) && !isUserPkg(databus, "CSM", "9013", "I00011100002", "ZZZZ")) {
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, 
						"NB-IOT校验失败:NB-IOT短信年包依赖于NB-IOT流量年包，不能单独订购！");
				return true;
			}
		}
		if (IDataUtil.isNotEmpty(testDiscnt)) {
			if (checkNbIotTestDiscntIsOne(databus, testDiscnt)) {
				//NB-IOT测试期产品1个用户仅能订购1次
				return true;
			}
			if (checkBeforeNbIotTestDiscnt(databus, testDiscnt)) {
				//NB-IOT测试期产品订购时，判断是否已订购正式期套餐，是的话就报错
				return true;
			}
		}
		return false;
	}
	
	/**
	 * NB-IOT服务的校验
	 * 
	 * @author pengxin
	 * @param databus
	 * @return
	 * @throws Exception
	 */
	private boolean checkNbIotSvc(IData databus, IData smsDiscnt, IData testDiscnt, IData nbIotBaseSvc, IData nbIotSmsSvc) throws Exception {
		//数据通信服务校验，当LOWPOWERMODE为PSM时，必填RAUTAUTIMER
		//数据通信服务APNNAME为CMNBIOT，LOWPOWERMODE字段选择PSM，RAUTAUTIMER 字段的填写最小值54分钟，最大值310小时，以秒为单位填写
		if (IDataUtil.isNotEmpty(nbIotBaseSvc)) {
			IDataset attrTradeList = DataHelper.filter(databus.getDataset("TF_B_TRADE_ATTR"), "RELA_INST_ID=" + nbIotBaseSvc.getString("INST_ID"));
			if (IDataUtil.isNotEmpty(attrTradeList)) {
				String apnName = null;
				String lowPowerMode = null;
				String rauTauTimer = null;
				for(int i = 0; i < attrTradeList.size(); i++) {
					IData attr = attrTradeList.getData(i);
					if ("LOWPOWERMODE".equals(attr.getString("ATTR_CODE"))) {
						lowPowerMode = attr.getString("ATTR_VALUE");
					} else if ("APNNAME".equals(attr.getString("ATTR_CODE"))) {
						apnName = attr.getString("ATTR_VALUE");
					} else if ("RAUTAUTIMER".equals(attr.getString("ATTR_CODE"))) {
						rauTauTimer = attr.getString("ATTR_VALUE");
					}
				}
				if(StringUtils.equals(lowPowerMode, "PSM")) {
					if(StringUtils.isEmpty(rauTauTimer)) {
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, 
								"NB-IOT校验失败:NB-IOT数据通信服务当LOWPOWERMODE为PSM时，RAUTAUTIMER必填！");
						return true;
					} else {
						long min = 54 * 60;
						long max = 310 * 3600;
						long rtt = Long.valueOf(rauTauTimer);
						if(StringUtils.equals(apnName, "CMNBIOT") 
								&& rtt < min && rtt > max) {
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, 
									"NB-IOT校验失败:数据通信服务APNNAME为CMNBIOT，LOWPOWERMODE字段选择PSM，RAUTAUTIMER 字段的填写最小值54分钟，最大值310小时，以秒为单位填写！");
							return true;
						}
					}
				}
			}
		}
		
		// 存在NB-IOT短信基础通信服务，则判断用户是否订购或已订购NB-IOT短信年包或NB-IOT测试期套餐包
		if(IDataUtil.isNotEmpty(nbIotSmsSvc) && IDataUtil.isEmpty(smsDiscnt) && IDataUtil.isEmpty(testDiscnt) 
				&& !isUserPkg(databus, "CSM", "9013", "I00011100005", "ZZZZ")
				&& !isUserPkg(databus, "CSM", "9013", "I00011100012", "ZZZZ")) {
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, 
					"NB-IOT校验失败:短信基础通信服务依赖于NB短信年包或测试期套餐！");
			return true;
		}
		return false;
	}
	/**
     * 机器卡定向流量资费的校验
     * 定向流量资费可以重复订购，所以要循环整个TRADE_ATTR
     * 
     * @author pengxin
     * @param databus
     * @return
     * @throws Exception
     */
	private boolean checkCardDirectIotDiscnt(IData databus) throws Exception {
		IDataset attrTradeList = databus.getDataset("TF_B_TRADE_ATTR");
		
		if(IDataUtil.isEmpty(attrTradeList)) {
			return false;
		}
		String apnName = null;
		for(int i = 0; i < attrTradeList.size(); i++) {
			IData attr = attrTradeList.getData(i);
			//判断是否为资费的属性
			IData discntConfig = StringUtils.equals(attr.getString("INST_TYPE"), "D") ? DISCNT_CONFIG_MAP.get(attr.getString("ELEMENT_ID")) : null;
			if(null != discntConfig) {
				if (("I00010101002".equals(discntConfig.getString("PARA_CODE2")) 
						|| "I00010101004".equals(discntConfig.getString("PARA_CODE2")) 
						|| "I00010101006".equals(discntConfig.getString("PARA_CODE2")))
						&& "APNNAME".equals(attr.getString("ATTR_CODE"))) {
					//机器卡定向流量资费的APNNAME属性
					if(apnName == null) {
						apnName = attr.getString("ATTR_VALUE");
					} else {
						if(!apnName.equals(attr.getString("ATTR_VALUE"))) {
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, "机器卡校验失败:定向流量资费与物联网专用数据通信服务attrvalue必须一致！");
							return true;
						}
					}
				}
			} else {
				//判断是否为服务的属性
				IData svcConfig = StringUtils.equals(attr.getString("INST_TYPE"), "S") ? SVC_CONFIG_MAP.get(attr.getString("ELEMENT_ID")) : null;
				if(null != svcConfig) {
					if ("I00010100092".equals(svcConfig.getString("PARA_CODE1")) && "APNNAME".equals(attr.getString("ATTR_CODE"))) {
						//物联网专用数据通信服务的APNNAME属性
						if(apnName == null) {
							apnName = attr.getString("ATTR_VALUE");
						} else {
							if(!apnName.equals(attr.getString("ATTR_VALUE"))) {
								BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, "机器卡校验失败:定向流量资费与物联网专用数据通信服务attrvalue必须一致！");
								return true;
							}
						}
					}
				}
			}
    	}
		return false;
	}

	/**
	 * NB-IOT流量年包的校验。 校验规则：流量年包的有效时间必须为12个自然月的整数倍。
	 * 例如：生效时间为2018.1，订购2年套餐包，则失效时间必须为2019.12.31 23:59:59
	 * 
	 * @author pengxin
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	private boolean checkRuleEndDate(IData databus, IData dataDiscnt) throws Exception {
		//通过配置获得偏移量
		IData pkgEleInfo = PkgElemInfoQry.getElementByElementId(dataDiscnt.getString("PACKAGE_ID"), "D", dataDiscnt.getString("DISCNT_CODE"));
		//判断偏移量是否与生失效时间吻合
		String ruleEndDate = SysDateMgr.endDateOffset(dataDiscnt.getString("START_DATE"), 
				pkgEleInfo.getString("END_OFFSET"), pkgEleInfo.getString("END_UNIT"));
		if(!StringUtils.equals(dataDiscnt.getString("END_DATE"), ruleEndDate)) {
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, "NB-IOT校验失败:NB-IOT流量年包的有效时间有误！");
			return true;
		}
		IDataset attrTradeList = DataHelper.filter(databus.getDataset("TF_B_TRADE_ATTR"), "RELA_INST_ID=" + dataDiscnt.getString("INST_ID"));
		if (IDataUtil.isNotEmpty(attrTradeList)) {
			String promiseUseMonths = null;
			for(int i = 0; i < attrTradeList.size(); i++) {
				IData attr = attrTradeList.getData(i);
				if ("PromiseUseMonths".equals(attr.getString("ATTR_CODE"))) {
					promiseUseMonths = attr.getString("ATTR_VALUE");
					break;
				}
			}
			if(!StringUtils.equals(promiseUseMonths, pkgEleInfo.getString("END_OFFSET"))) {
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, "NB-IOT校验失败:NB-IOT流量年包的承诺在网时间（月）有误！");
				return true;
			}
		}
		return false;
	}
	/**
	 * NB-IOT测试期产品订购时，判断是否已订购正式期套餐，是的话就报错
	 * 
	 * @param databus
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	private boolean checkBeforeNbIotTestDiscnt(IData databus, IData testDiscnt) throws Exception {
		// 获得NB-IOT正式期套餐
		IDataset nbDiscntConfig = CommparaInfoQry.getCommparaByAttrCode2("CSM", "9013", "I00011100002", "ZZZZ", null);
		String userId = databus.getString("USER_ID");
		
		// 判断用户是否已经订购过NB-IOT正式期套餐
		IDataset userNbIotDiscntInfos = null;
		for (int i = 0; i < nbDiscntConfig.size(); i++) {
			userNbIotDiscntInfos = UserDiscntInfoQry.queryUserAllDiscntByUserIdAndDiscntCode(userId, nbDiscntConfig.getData(i).getString("PARAM_CODE"));
		}
		if (IDataUtil.isNotEmpty(userNbIotDiscntInfos)) {
			if ("0".equals(testDiscnt.getString("MODIFY_TAG"))) {
				String testDiscntCode = testDiscnt.getString("DISCNT_CODE");
				String testDiscntName = StaticUtil.getStaticValue(CSBizBean.getVisit(), 
						"TD_B_DISCNT", "DISCNT_CODE", "DISCNT_NAME", testDiscntCode);
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, 
						"NB-IOT校验失败:NB-IOT测试期优惠【" + testDiscntCode + "|" + testDiscntName + 
						"】根据集团规范，不能再用户订购了正式期之后订购");
				return true;
			}
		}
		return false;
	}
	/**
	 * NB-IOT测试期套餐的校验
	 * 校验规则：测试期产品1个用户仅能订购1次
	 * @param databus
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	private boolean checkNbIotTestDiscntIsOne(IData databus, IData testDiscnt) throws Exception {
		// 获得NB-IOT测试期套餐
		IDataset testConfig = CommparaInfoQry.getCommparaByAttrCode2("CSM", "9013", "I00011100012", "ZZZZ", null);
		String userId = databus.getString("USER_ID");
		
		// 判断用户是否已经订购过NB-IOT测试期套餐
		IDataset userNbIotTestDiscntInfos = null;
		for (int i = 0; i < testConfig.size(); i++) {
			userNbIotTestDiscntInfos = UserDiscntInfoQry.queryUserAllDiscntByUserIdAndDiscntCode(userId, testConfig.getData(i).getString("PARAM_CODE"));
		}
		if (IDataUtil.isNotEmpty(userNbIotTestDiscntInfos)) {
			if ("0".equals(testDiscnt.getString("MODIFY_TAG"))) {
				String testDiscntCode = testDiscnt.getString("DISCNT_CODE");
				String testDiscntName = StaticUtil.getStaticValue(CSBizBean.getVisit(), 
						"TD_B_DISCNT", "DISCNT_CODE", "DISCNT_NAME", testDiscntCode);
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, 
						"NB-IOT校验失败:NB-IOT测试期优惠【" + testDiscntCode + "|" + testDiscntName + 
						"】根据集团规范，只能订购一次，" + "用户已经订购一次这个优惠，不允许再次订购。");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * NB-IOT短信年包套餐的失效依赖于NB-IOT流量年包，且时间须与流量年包套餐的失效时间一致
	 * 
	 * @author pengxin
	 * @param databus
	 * @param smsDiscnt
	 * @param DataDiscnt
	 * @return
	 * @throws Exception
	 */
	private boolean checkNbIotSmsDiscntEndDate(IData databus, IData smsDiscnt, IData DataDiscnt) throws Exception {
		int compareTag = SysDateMgr.compareTo(smsDiscnt.getString("END_DATE"), DataDiscnt.getString("END_DATE"));
		if (compareTag != 0) {
			//短信年包失效时间必须等于流量年包
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, 
					"NB-IOT短信年包套餐的失效依赖于NB-IOT流量年包，且时间须与流量年包套餐的失效时间一致");
			return true;
		}
		return false;
	}
	
	/**
	 * 判断用户是否已经订购了某产品包
	 * 
	 * @author pengxin
	 * @param databus
	 * @param strSubsysCode
	 * @param iParamAttr
	 * @param paraCode2
	 * @param strEparchyCode
	 * @return
	 * @throws Exception
	 */
	private boolean isUserPkg(IData databus, String strSubsysCode, String iParamAttr, String paraCode2, String strEparchyCode) throws Exception {
		IDataset userNbIotDiscnt = null;
		//获得NB-IOT流量年包
		IDataset nbIotDiscntList = new DatasetList();
		nbIotDiscntList.addAll(CommparaInfoQry.getCommparaByAttrCode2(strSubsysCode, iParamAttr, paraCode2, strEparchyCode, null));
		for (int i = 0; i < nbIotDiscntList.size(); i++) {
			IData nbIotDiscnt = nbIotDiscntList.getData(i);
			String userId = databus.getDataset("TF_B_TRADE").getData(0).getString("USER_ID");
			IDataset userDiscnts = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
			userNbIotDiscnt = DataHelper.filter(userDiscnts, "DISCNT_CODE=" + nbIotDiscnt.getString("PARAM_CODE"));	
		}
		if (IDataUtil.isEmpty(userNbIotDiscnt)) {
			return false;
		}
		return true;
	}
}
