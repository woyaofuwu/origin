package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.bean;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.IBaseBean;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.cache.FamilyClazzCache;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class FamilyBusiCheckBean implements IBaseBean {

	@SuppressWarnings("unchecked")
	public IDataset busiCheck(IData param, String triggerType, boolean isCatch) throws Exception {
		IDataset errors = new DatasetList();
		List<ICheck> points = FamilyClazzCache.getClassInstance(param, FamilyConstants.TriggerPoint.CHECK.toString(),
				triggerType);
		if (null != points) {
			for (ICheck point : points) {
				point.check(param);
			}
			IDataset errs = handlerErrorInfo(param, isCatch);// 处理当前是否报错
			if (IDataUtil.isNotEmpty(errs)) {
				errors.addAll(errs);
			}
		}
		IDataset errs = busiCheckSubRoles(param, triggerType, isCatch);// 子集合
		if (IDataUtil.isNotEmpty(errs)) {
			errors.addAll(errs);
		}
		return errors;
	}

	private IDataset busiCheckSubRoles(IData param, String triggerType, boolean isCatch) throws Exception {
		IDataset subRoles = FamilyCallerUtil.getDataset(param, "SUB_ROLES");
		if (IDataUtil.isNotEmpty(subRoles)) {
			for (int i = 0; i < subRoles.size(); i++) {
				IData data = new DataMap(subRoles.getData(i));
				data.put("BUSI_TYPE", param.getString("BUSI_TYPE"));
				IDataset result = busiCheck(data, triggerType, isCatch);
				if (IDataUtil.isNotEmpty(result)) {
					return result;
				}
			}
		}
		return null;
	}

	public static IDataset handlerErrorInfo(IData checkParam, boolean isCatch) throws Exception {
		IDataset errorInfos = checkParam.getDataset("RULE_INFO");
		if (IDataUtil.isEmpty(errorInfos))
			return null;
		if (isCatch) {
			return errorInfos;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0, size = errorInfos.size(); i < size; i++) {
			sb.append((i + 1)).append("、").append(errorInfos.getData(i).getString("ERROR_CODE"))
					.append("," + errorInfos.getData(i).getString("ERROR_INFO")).append(";");
		}
		CSAppException.apperr(TradeException.CRM_TRADE_95, sb.toString());
		return null;
	}

}
