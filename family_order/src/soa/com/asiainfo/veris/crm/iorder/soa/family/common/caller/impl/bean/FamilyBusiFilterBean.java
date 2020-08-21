package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.bean;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.IBaseBean;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.cache.FamilyClazzCache;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IFilter;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class FamilyBusiFilterBean implements IBaseBean {

	public static IData filterOffers(IData param) throws Exception {
		IData converAfterData = new DataMap();
		String busiType = IDataUtil.chkParam(param, "BUSI_TYPE");
		String roleCode = IDataUtil.chkParam(param, "ROLE_CODE");
		String roleType = IDataUtil.chkParam(param, "ROLE_TYPE");
		IData trans = FamilyCallerUtil.getData(param, "TRANS_DATA");
		IData cond = FamilyCallerUtil.getData(param, "COND_DATA");

		if (StringUtils.isBlank(busiType) || StringUtils.isBlank(roleCode) || StringUtils.isBlank(roleType))
			return converAfterData;

		List<IFilter> points = FamilyClazzCache.getClassInstance(param,
				FamilyConstants.TriggerPoint.FILTER_OFFERS.toString());
		if (null != points) {
			for (IFilter point : points) {
				IData tmp = point.trans(cond, trans);// 每次返回的是所有的结果
				if (IDataUtil.isNotEmpty(tmp)) {
					converAfterData.putAll(tmp);
					trans = converAfterData;
				}
			}
		}
		return converAfterData;
	}
}
