package com.asiainfo.veris.crm.order.soa.group.destroyonekey;

import com.ailk.common.data.IData;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DestroyJKDTOneKeySVC  extends CSBizService{
	 private static final long serialVersionUID = 1L;

	    /**
	     * 创建批量任务daidl
	     * 
	     * @param inParam
	     * @return
	     * @throws Exception
	     */
	    public IDataset crtBat(IData inParam) throws Exception
	    {
	    	DestroyJKDTOneKeyBean bean = new DestroyJKDTOneKeyBean();

	        return bean.crtBat(inParam);
	    }
}
