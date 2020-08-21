package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.check.submit;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;

public class CheckPhoneSubmit implements ICheck {

	@Override
	public boolean check(IData checkInparam) throws Exception {
		String sn = checkInparam.getString("SERIAL_NUMBER");
		FamilyCallerUtil.addErrorInfo(checkInparam, "CheckPhoneSubmit", "-1", "测试2");
		return false;
	}

}
