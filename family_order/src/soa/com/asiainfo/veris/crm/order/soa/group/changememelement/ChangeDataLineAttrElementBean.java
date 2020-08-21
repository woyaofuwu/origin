package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class ChangeDataLineAttrElementBean extends GroupOrderBaseBean
{
    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
    	// 成员新增
    	GrpInvoker.ivkProduct(map, BizCtrlType.CreateMember, "ChangeUserClass");
    }

	@Override
	protected String setOrderTypeCode() throws Exception {
		// TODO Auto-generated method stub
		return "3002";
	}

}
