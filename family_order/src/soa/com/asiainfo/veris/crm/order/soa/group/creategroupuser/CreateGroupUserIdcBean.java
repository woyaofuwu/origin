package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateGroupUserIdcBean extends GroupOrderBaseBean{
	public void actOrderDataOther(IData map) throws Exception
    {		  // 生成主用户
        GrpInvoker.ivkProduct(map, BizCtrlType.CreateUser, "CreateUserClass");
    }

	
	@Override
	protected String setOrderTypeCode() throws Exception {
		return "3851";
	}
}
