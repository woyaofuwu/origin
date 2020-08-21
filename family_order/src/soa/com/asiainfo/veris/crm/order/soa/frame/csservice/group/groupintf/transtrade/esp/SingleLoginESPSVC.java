package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public  class SingleLoginESPSVC extends CSBizService {

	/**
	 * 
	 */
	private static final Logger log=Logger.getLogger(SingleLoginESPSVC.class);
	private static final long serialVersionUID = 1L;
	private static StringBuilder getInterFaceSQL;

	static
	{
		getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' "); 
	}
	
	public static IData qeyESPSceret (IData data) throws Exception
	{
		log.debug("---------input params---------"+data);
		IData result =new DataMap();
		IData param1 = new DataMap();
		String cityId="";
		String countyId="";
		String staffId=data.getString("UCODE");
		param1.put("PARAM_NAME", "singleLoginESP");
    	IDataset bizUrls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
    	String bizUrl = "";
    	if (bizUrls != null && bizUrls.size() > 0)
    	{
    		bizUrl = bizUrls.getData(0).getString("PARAM_VALUE", "");
    	}else{
    		result.put("X_RESULTCODE", "-1");
    		result.put("X_RESULTINFO", "singleLoginESP地址在TD_S_BIZENV表未配置");
        	return result;
    	}
    	data.put("ESP_URL", bizUrl);
		if(StaffPrivUtil.isFuncDataPriv(staffId, "SYS_ESP_PROV"))
			//省级权限
		{
			cityId="0000";
			countyId="000000";
		}
		else 
		{
			//查询配置的数据字典，查询对应的业务区
			IDataset eparchyCodes=CommparaInfoQry.getCommPkInfo("CSM","5678", data.getString("CITY_CODE"), data.getString("EPARCHY_CODE"));
			if(IDataUtil.isNotEmpty(eparchyCodes))
			{
				cityId=eparchyCodes.getData(0).getString("PARA_CODE1", "8980");
				countyId=eparchyCodes.getData(0).getString("PARA_CODE2", "898001");
				
			}
			else 
			{//如果取不到，就取默认的，业务区，param_code=1
				 eparchyCodes=CommparaInfoQry.getCommPkInfo("CSM", "5678", "1", data.getString("EPARCHY_CODE"));
				 cityId=eparchyCodes.getData(0).getString("PARA_CODE1", "8980");
				 countyId=eparchyCodes.getData(0).getString("PARA_CODE2", "898001");
			}
			//市级权限
			if(StaffPrivUtil.isFuncDataPriv(staffId, "SYS_ESP_CITY")) 	countyId="000001";
		}

		data.put("CITY_ID", cityId);
		data.put("COUNTY_ID", countyId);
		log.debug("------------------------call iboss get sceret----------------------------------"+data);
		IDataset dataset =IBossCall.dealInvokeUrl("BIP5B051_T5001051_0_0", "IBOSS", data);
		log.debug("------------------------return esp sceret----------------------------------"+dataset);
		if(IDataUtil.isNotEmpty(dataset))
		{
			result=dataset.getData(0);
			result.putAll(data);
		}
/*		result.put("X_RESULTCODE", "00");
		result.put("X_RESULTINFO", "成功");
		result.put("TOKEN", "1111111111");*/
		return result;	
	}

}
