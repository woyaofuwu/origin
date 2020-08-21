package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * order
 * ewe配置查询
 * @author ckh
 * @date 2018/2/27.
 */
public class EweConfigQry
{
    public static IData queryEomsTranConfig(String configName, String paramValue, String valueDesc, String operType)
            throws Exception
    {
        IData param = new DataMap();
        param.put("CONFIGNAME", configName);
        param.put("PARAMVALUE", paramValue);
        param.put("VALUEDESC", valueDesc);
        param.put("RSRV_STR1", operType);
        IDataset configs = Dao.qryByCode("TD_B_EWE_CONFIG", "SEL_CONFIG_EOMS_TRANS", param, Route.CONN_CRM_CEN);
        if (DataUtils.isEmpty(configs))
        {
            return new DataMap();
        }
        else
        {
            return configs.first();
        }
    }
    
    public static IData queryEomsInterName(String configName, String paramName, String valueDesc, String productId, String lineType, String status) throws Exception
    {
    	IData param = new DataMap();
        param.put("CONFIGNAME", configName);
        param.put("PARAMNAME", paramName);
        param.put("VALUEDESC", valueDesc);
        param.put("RSRV_STR1", productId);
        param.put("RSRV_STR2", lineType);
        param.put("VALID_TAG", status);
        IDataset configs = Dao.qryByCode("TD_B_EWE_CONFIG", "SEL_BY_CONFIGPARAMNAMESTR1STR2_VALID", param, Route.CONN_CRM_CEN);
        if (DataUtils.isEmpty(configs))
        {
            return new DataMap();
        }
        else
        {
            return configs.first();
        }
    }
    
    /**
	 * 按configName和paramName查询
	 * @param configName
	 * @param paramName
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public static IData qryByConfigParamNameRsrv1(String configName, String paramName,String rsrv_str1, String status) throws Exception
	{
		IData param = new DataMap();
		param.put("PARAMNAME", paramName);
		param.put("CONFIGNAME", configName);
		param.put("RSRV_STR1", rsrv_str1);
		param.put("VALID_TAG", status);
        IDataset configs = Dao.qryByCodeParser("TD_B_EWE_CONFIG", "SEL_BY_CONFIGPARAMNAMESTR1_VALID", param, Route.CONN_CRM_CEN);
        if (DataUtils.isEmpty(configs))
        {
            return new DataMap();
        }
        else
        {
            return configs.first();
        }
	}
	
	/**
	 * 按configName查询
	 * @param configName
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryByConfigName(String configName, String status) throws Exception
	{
	    IData param = new DataMap();
	    param.put("CONFIGNAME", configName);
	    param.put("VALID_TAG", status);
	    return Dao.qryByCode("TD_B_EWE_CONFIG", "SEL_BY_CONFIGNAME_VALID", param, Route.CONN_CRM_CEN);
	}
	
	public static IDataset qryDistinctValueDescByParamName(String configName, String paramName, String status) throws Exception
	{
		IData param = new DataMap();
		param.put("CONFIGNAME", configName);
		param.put("PARAMNAME", paramName);
		param.put("VALID_TAG", status);
        return Dao.qryByCode("TD_B_EWE_CONFIG", "SEL_BY_CONFIGPARAMNAME_VALID", param, Route.CONN_CRM_CEN);
	}
	
	public static IDataset qryDistinctValueDescByParamName(String configName, String paramName) throws Exception
	{
		IData param = new DataMap();
		param.put("CONFIGNAME", configName);
		param.put("PARAMNAME", paramName);
        return Dao.qryByCode("TD_B_EWE_CONFIG", "SEL_DISTINCT_VALUEDESC_BY_CONFIG", param, Route.CONN_CRM_CEN);
	}
}
