package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class DestoryDatalineGroupMemberSVC  extends GroupOrderService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final IDataset crtTrade(IData map) throws Exception
    {
       // ChangeSuperTeleMebElementSimple bean = new ChangeSuperTeleMebElementSimple();
		
		DestoryDatalineGroupMemberBean bean = new DestoryDatalineGroupMemberBean();
        return bean.crtTrade(map);

    }

}

