package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class DestroyGroupMemberSVC extends GroupOrderService{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IDataset crtOrder(IData inparam) throws Exception
	 {
		
    	// 调用OrderBaseBean,生成集团台账和成员台账
		 DestroyGroupMemberBean bean = new DestroyGroupMemberBean();
    	return bean.crtOrder(inparam);
	 }

}
