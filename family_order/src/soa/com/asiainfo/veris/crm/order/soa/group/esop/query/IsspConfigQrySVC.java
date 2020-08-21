package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IsspConfigQrySVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;

	//通过配置名和参数名查参数值
	public static IDataset getParamValue(IData idata) throws Exception{
		
		String configName = idata.getString("CONFIGNAME");
		String paramName = idata.getString("PARAMNAME");
		return IsspConfigQry.getParamValue(configName,paramName);
	}
	
	//通过配置名和参数名,参数描述 查询参数值
	public static IDataset getParamValueByDesc(IData idata) throws Exception{
			
			String configName = idata.getString("CONFIGNAME");
			String paramName = idata.getString("PARAMNAME");
			String Desc = idata.getString("VALUEDESC");
			
			return IsspConfigQry.getParamValueByDesc(configName,paramName,Desc);
	}
	public static IDataset getIsspConfig(IData idata) throws Exception {
		String configName = idata.getString("CONFIGNAME");
		return IsspConfigQry.getIsspConfig(configName);
	}
}
