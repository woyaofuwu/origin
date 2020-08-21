package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EweConfigQrySVC extends CSBizService{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static IDataset qryEweConfigByConfigName(IData param) throws Exception
	{
		String configName = param.getString("CONFIG_NAME");
		String status = param.getString("STATUS");

		return EweConfigQry.qryByConfigName(configName,status);
	}

	public static IDataset qryParamValueByParamName(IData param)throws Exception{
		String configName = param.getString("CONFIG_NAME");
		String paramName = param.getString("PARAMNAME");
		String status = param.getString("STATUS");
		return EweConfigQry.qryDistinctValueDescByParamName(configName,paramName,status);
	}
}
