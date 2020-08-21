package com.asiainfo.veris.crm.iorder.soa.group.param.esop.checklineproduct;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckLineProductSVC extends CSBizService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void checkLinePro(IData data) throws Exception
	{
		CheckLineProductBean.checkLineProduct(data);
	}

}