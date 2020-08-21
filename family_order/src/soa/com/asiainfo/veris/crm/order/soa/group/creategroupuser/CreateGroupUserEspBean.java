package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateGroupUserEspBean extends GroupOrderBaseBean{

	public void actOrderDataOther(IData map) throws Exception
    {		  // 生成主用户
        GrpInvoker.ivkProduct(map, BizCtrlType.CreateUser, "CreateUserClass");
    }

	
	@Override
	protected String setOrderTypeCode() throws Exception {
		String productId = EOS.getData(0).getString("PRODUCT_ID");
		if("7010".equals(productId)){
			return "2990";
		}else if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
			return "3080";
		}else if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
			return "3010";
		}else if("7016".equals(productId)){
			return "3846";
		}
		return "";
	}
	
	
}
