package com.asiainfo.veris.crm.iorder.soa.group.layout;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TempletGroupBizSVC extends CSBizService 
{
	private static final Logger logger = Logger.getLogger(TempletGroupBizSVC.class.getName());

    private static final long serialVersionUID = 1L;
    
    public IDataset queryInfo(IData input) throws Exception
    {
    	IData param=new DataMap();
    	param.put("BPM_TEMPLET_ID",input.getString("BPM_TEMPLET_ID",""));
    	param.put("NODE_ID",input.getString("NODE_ID",""));
    	param.put("VALID_TAG", input.getString("STATUS",""));
    	
    	TempletGroupBizBean bean = new TempletGroupBizBean();
    	return bean.queryByTempletNodeId(param);
    }
    
    public IDataset queryInfoByNodeType(IData input) throws Exception
    {
    	IData param=new DataMap();
    	param.put("BPM_TEMPLET_ID",input.getString("BPM_TEMPLET_ID",""));
    	param.put("NODE_TYPE",input.getString("NODE_TYPE",""));
    	param.put("VALID_TAG", input.getString("VALID_TAG",""));
    	
    	TempletGroupBizBean bean = new TempletGroupBizBean();
    	return bean.queryByTempletNodeType(param);
    }
}
