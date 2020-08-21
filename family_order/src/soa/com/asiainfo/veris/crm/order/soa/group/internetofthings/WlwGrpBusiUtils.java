package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class WlwGrpBusiUtils
{
	private static final Logger logger = Logger.getLogger(WlwGrpBusiUtils.class);	
	
	 /**
	  * 通过paramAttr加载配置
	  * @return
	  * @throws Exception
	  */
	public static IData loadConfigData() throws Exception {
		IData configData = new DataMap();
		IDataset configList = CommparaInfoQry.getCommByParaAttr("CSM", "9013", "0898");
		for (int i = 0; i < configList.size(); i++) {
			IData config = configList.getData(i);
			String strP5 = config.getString("PARA_CODE5", "");
	        String strP20 = config.getString("PARA_CODE20", "");
	        if (!strP20.startsWith("NB") && "1".equals(strP5))
	        {
	        	configData.put(config.getString("PARAM_CODE"), config);
	        }
		}      
		return configData;
	}
	
	/**
	 * 捞取物联网的20G以上的套餐
	 * @param paramAttr
	 * @param paraCode1
	 * @return
	 * @throws Exception
	 */
	public static IData loadConfigData(String paramAttr,String paraCode1) throws Exception {
		IData configData = new DataMap();
		IDataset configList = CommparaInfoQry.getCommparaByCode1("CSM", paramAttr, paraCode1,"0898");
		for (int i = 0; i < configList.size(); i++) {
			IData config = configList.getData(i);
	        configData.put(config.getString("PARAM_CODE"), config);
		}      
		return configData;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IData loadConfigPolicyData() throws Exception
	{
		IDataset configList = CommparaInfoQry.getCommparaAllCol("CSM", "4007", "WlwPolicySVC", "0898");
	    IData configData = new DataMap();
	    if(IDataUtil.isNotEmpty(configList))
	    {
	    	for (int i = 0; i < configList.size(); i++) {
				IData config = configList.getData(i);
				configData.put(config.getString("PARA_CODE1"), config);
			}
	    }
	    return configData;
	}
	
    
}