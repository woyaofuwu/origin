package com.asiainfo.veris.crm.iorder.soa.group.layout;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PageTempletSVC extends CSBizService 
{
	private static final Logger logger = Logger.getLogger(PageTempletSVC.class.getName());

    private static final long serialVersionUID = 1L;
    
    public IDataset queryInfo(IData input) throws Exception
    {
    	IData param=new DataMap();
    	param.put("TEMPLET_ID",input.getString("TEMPLET_ID",""));
    	param.put("TEMPLET_TYPE",input.getString("TEMPLET_TYPE",""));
    	param.put("VALID_TAG",input.getString("STATUS",""));
    	
    	PageTempletBean bean = new PageTempletBean();
    	return bean.queryByTempletIdType(param);
    }
}
