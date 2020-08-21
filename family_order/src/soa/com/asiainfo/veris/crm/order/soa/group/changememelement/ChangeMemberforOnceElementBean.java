package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class ChangeMemberforOnceElementBean extends GroupOrderBaseBean {

    public void actOrderDataOther(IData map) throws Exception {
    	 GrpInvoker.ivkProduct(map, BizCtrlType.ChangeMemberDis, "ChangeOnceAttrClass");
    }

	@Override
	protected String setOrderTypeCode() throws Exception {
		return "0";
	}

}
