package com.asiainfo.veris.crm.iorder.soa.group.layout;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PageAreaPartSVC extends CSBizService
{
	private static final Logger logger = Logger.getLogger(PageAreaPartSVC.class.getName());

    private static final long serialVersionUID = 1L;
    
    public IDataset queryInfo(IData input) throws Exception
    {
    	IData param=new DataMap();
    	param.put("AREA_ID",input.getString("AREA_ID",""));
    	param.put("VALID_TAG",input.getString("STATUS",""));
    	
    	PageAreaPartBean bean = new PageAreaPartBean();
    	return bean.queryByAreaId(param);
    }
}
