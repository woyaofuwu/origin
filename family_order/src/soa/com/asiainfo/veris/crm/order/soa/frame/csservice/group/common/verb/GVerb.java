package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;

public abstract class GVerb{

	public GVerb(){
		
	}
	
	/**
	 * 子类继承改方法，生产实体信息
	 * @param ec
	 * @return
	 * @throws Exception
	 */
	public IData run(GroupBaseReqData reqData) throws Exception
	{
	    return null;
	}
	
}
