
package com.asiainfo.veris.crm.iorder.soa.family.common.caller;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.bean.FamilyBusiCheckBean;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.bean.FamilyBusiDealBean;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.bean.FamilyBusiFilterBean;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.bean.FamilyBusiParamTransBean;

public class FamilyCallerBean {

	public static IData filterOffers(IData param) throws Exception {
		FamilyBusiFilterBean bean = BeanManager.createBean(FamilyBusiFilterBean.class);
		param.put("PARAM_CODE", FamilyConstants.TriggerPoint.FILTER_OFFERS.toString());
		return bean.filterOffers(param);
	}

	public static IData busiCheckNoCatch(IData param, String triggerType) throws Exception {
		FamilyBusiCheckBean bean = BeanManager.createBean(FamilyBusiCheckBean.class);
		param.put("PARAM_CODE", FamilyConstants.TriggerPoint.CHECK.toString());
		IDataset errors = bean.busiCheck(param, triggerType, false);
		IData result = new DataMap();
		result.put("RULE_INFO", errors);
		return result;
	}

	public static IData busiCheck(IData param, String triggerType) throws Exception {
		FamilyBusiCheckBean bean = BeanManager.createBean(FamilyBusiCheckBean.class);
		param.put("PARAM_CODE", FamilyConstants.TriggerPoint.CHECK.toString());
		IDataset errors = bean.busiCheck(param, triggerType, true);
		IData result = new DataMap();
		result.put("RULE_INFO", errors);
		return result;
	}

	public static IData busiSpecDeal(IData param, Object... objs) throws Exception {
		FamilyBusiDealBean bean = BeanManager.createBean(FamilyBusiDealBean.class);
		param.put("PARAM_CODE", FamilyConstants.TriggerPoint.SPECIAL_DEAL.toString());
		return bean.deal(param, objs);
	}

	public static IData busiParamTrans(IData param) throws Exception {
		FamilyBusiParamTransBean bean = BeanManager.createBean(FamilyBusiParamTransBean.class);
		param.put("PARAM_CODE", FamilyConstants.TriggerPoint.BUSI_TRANS.toString());
		return bean.getTransParamters(param);
	}

}
