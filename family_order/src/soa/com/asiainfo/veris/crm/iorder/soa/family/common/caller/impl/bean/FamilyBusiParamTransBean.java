package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.bean;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.service.bean.IBaseBean;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.cache.FamilyClazzCache;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IParamTrans;

public class FamilyBusiParamTransBean implements IBaseBean {

	public IData getTransParamters(IData param) throws Exception {
		List<IParamTrans> points = FamilyClazzCache.getClassInstance(param,
				FamilyConstants.TriggerPoint.BUSI_TRANS.toString());
		if (points != null) {
			IParamTrans trans = points.get(0);
			if (trans != null) {
				return trans.getTransParamters(param);
			}
		}
		return null;
	}

}
