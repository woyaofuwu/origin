package com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces;

import com.ailk.common.data.IData;

public interface ISpecialDeal {
	public <T> T deal(IData input, Object... objs) throws Exception;

}
