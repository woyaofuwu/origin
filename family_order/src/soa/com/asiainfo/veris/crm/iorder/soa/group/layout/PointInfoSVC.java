package com.asiainfo.veris.crm.iorder.soa.group.layout;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PointInfoSVC extends CSBizService 
{
	private static final Logger logger = Logger.getLogger(PointInfoSVC.class.getName());

    private static final long serialVersionUID = 1L;
    
    /**
     * 按point_one,point_two查询数据
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryPoint(IData input) throws Exception
    {
    	IData param=new DataMap();
    	param.put("POINT_ONE",input.getString("POINT_ONE",""));
    	param.put("POINT_TWO",input.getString("POINT_TWO",""));
    	
    	PointInfoBean bean = new PointInfoBean();
    	return bean.queryByPointOneTwo(param);
    }
}
