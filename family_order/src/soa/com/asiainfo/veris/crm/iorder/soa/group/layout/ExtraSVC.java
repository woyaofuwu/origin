package com.asiainfo.veris.crm.iorder.soa.group.layout;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ExtraSVC extends CSBizService 
{
	private static final Logger logger = Logger.getLogger(ExtraSVC.class.getName());

    private static final long serialVersionUID = 1L;
    
    public IDataset queryInfo(IData input) throws Exception
    {
    	IData param=new DataMap();
    	param.put("EXTRA_ID",input.getString("EXTRA_ID",""));
    	param.put("EXTRA_KEY",input.getString("EXTRA_KEY",""));
    	param.put("EXTRA_TYPE",input.getString("EXTRA_TYPE",""));
    	param.put("VALID_TAG",input.getString("STATUS",""));
    	
    	ExtraBean bean = new ExtraBean();
    	return bean.queryByExtraIdTypeKey(param);
    }
    
    public IDataset queryAllInfos(IData input) throws Exception
    {
    	IData param=new DataMap();
    	param.put("EXTRA_ID",input.getString("EXTRA_ID",""));
    	param.put("EXTRA_TYPE",input.getString("EXTRA_TYPE",""));
    	param.put("VALID_TAG",input.getString("STATUS",""));
    	
    	ExtraBean bean = new ExtraBean();
    	return bean.queryByExtraIdType(param);
    }
}
