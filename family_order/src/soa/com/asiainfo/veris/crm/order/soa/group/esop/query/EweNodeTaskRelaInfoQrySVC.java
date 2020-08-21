package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EweNodeTaskRelaInfoQrySVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;
	
	public static boolean insertNodeTaskRela(IData param) throws Exception
	{ 
		return EweNodeTaskRelaInfoQry.insertNodeTaskRela(param);
	}	
	
}
