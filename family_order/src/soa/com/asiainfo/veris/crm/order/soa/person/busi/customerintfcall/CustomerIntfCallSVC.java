/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.customerintfcall;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CusCall;

/**
 * @CREATED by cxy@2014-11-20
 */
public class CustomerIntfCallSVC extends CSBizService
{

	static Logger logger = Logger.getLogger(CustomerIntfCallSVC.class); 	
    private static final long serialVersionUID = 1838322381138748859L;
 

    public IDataset qryByParams(IData inparams) throws Exception
    { 
    	return CusCall.qryByParams(inparams);
    }
    
}
