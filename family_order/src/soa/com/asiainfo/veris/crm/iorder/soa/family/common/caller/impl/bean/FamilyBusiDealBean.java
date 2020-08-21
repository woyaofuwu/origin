package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.bean;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.service.bean.IBaseBean;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.cache.FamilyClazzCache;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ISpecialDeal;

public class FamilyBusiDealBean implements IBaseBean {

	public IData deal(IData param, Object... objs) throws Exception {
		List<ISpecialDeal> points = FamilyClazzCache.getClassInstance(param,
				FamilyConstants.TriggerPoint.SPECIAL_DEAL.toString());
		if (null != points) {
			for (ISpecialDeal point : points) {
				point.deal(param, objs);
			}
		}
		return null;
	}

}
