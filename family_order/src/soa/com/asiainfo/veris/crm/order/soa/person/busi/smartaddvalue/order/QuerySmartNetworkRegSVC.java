/**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     **/
package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
 
public class QuerySmartNetworkRegSVC extends CSBizService{
	static Logger logger = Logger.getLogger(DredgeSmartNetworkRegSVC.class);
	private static final long serialVersionUID = 1L;
    //查询已有记录
    public IDataset querySmartNetwork(IData input) throws Exception{
    	IDataset idata= QuerySmartNetworkIntfBean.querySmartNetwork(input);  
        return idata; 
    }
}