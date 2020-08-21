package com.asiainfo.veris.crm.order.soa.person.busi.selfhelpnew;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService; 
 
public class NewTerminalReconQuerySVC  extends CSBizService{

	private static final long serialVersionUID = 8436008007830978415L;
	protected static Logger log = Logger.getLogger(NewTerminalReconQuerySVC.class);

    public IDataset queryPayOrder(IData input) throws Exception{   
    	
    	IDataset rtnDatas = new DatasetList();
    	
    	NewTerminalReconQueryBean bean = BeanManager.createBean(NewTerminalReconQueryBean.class);
    	
    	rtnDatas = bean.queryPayOrder(input,getPagination());  
        
    	return rtnDatas; 
    }
    	 
}
