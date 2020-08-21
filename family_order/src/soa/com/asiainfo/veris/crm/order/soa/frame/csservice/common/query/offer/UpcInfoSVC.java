package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.offer;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

public class UpcInfoSVC extends CSBizService
{
    public IDataset qryUpcInfosByServiceNameAndParamData(IData paramData) throws Exception
    {
        String realServiceName = paramData.getString("REAL_SERVICE_NAME");
        if (StringUtils.isBlank(realServiceName))
        {
            return null;
        }
        if("queryBrandNameByChaVal".equals(realServiceName)){
        	String brandName =  UpcCallIntf.queryBrandNameByChaVal(paramData.getString("BRAND_CODE"));
        	IDataset results = new DatasetList();
        	IData data = new DataMap();
        	data.put("BRAND_NAME", brandName);
        	results.add(data);
        	
        	return results;
        }
        
        paramData.remove("REAL_SERVICE_NAME");
        IData result = null;//UpcCallIntf.call(realServiceName, paramData);
        IDataset results = result.getDataset("OUTDATA");
        
        return results;
    }
}
