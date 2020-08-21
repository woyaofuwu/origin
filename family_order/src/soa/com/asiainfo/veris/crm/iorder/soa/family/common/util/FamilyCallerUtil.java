package com.asiainfo.veris.crm.iorder.soa.family.common.util;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class FamilyCallerUtil {
	private static final Logger logger = Logger.getLogger(FamilyCallerUtil.class);

	public static void addErrorInfo(IData inParam, String checkClassName, String errorCode, String errorInfo)
			throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("---报错报错了---" + checkClassName + "----" + errorCode + ":" + errorInfo);

		IDataset errorInfos = inParam.getDataset("RULE_INFO");
		if (errorInfos == null) {
			errorInfos = new DatasetList();
			inParam.put("RULE_INFO", errorInfos);
		}
		IData errorData = new DataMap();
		errorData.put("ERROR_CODE", errorCode);
		errorData.put("ERROR_INFO", errorInfo);
		errorInfos.add(errorData);
	}

	public static IData getData(IData param, String colName) {
		if (param.containsKey(colName)) {
			IData result = param.getData(colName);
			if (IDataUtil.isEmpty(result)) {
				result = new DataMap(param.getString(colName, "{}"));
			}
			return result;
		}
		return null;
	}

	public static IDataset getDataset(IData param, String colName) {
		if (param.containsKey(colName)) {
			IDataset result = param.getDataset(colName);
			if (IDataUtil.isEmpty(result)) {
				result = new DatasetList(param.getString(colName, "[]"));
			}
			return result;
		}
		return null;
	}

	public static boolean checkInParam(IData param, String className, String key, String value) throws Exception {
		if (StringUtils.isBlank(value)) {
			addErrorInfo(param, className, "-1", "【" + key + "】不能为空！");
			return true;
		}
		return false;
	}
	
    /**
     * 添加规则报错
     * duhj
     * @param input 入参
     * @param infos 规则结果集
     */
    public static void addBreerr(IData input, IDataset infos) throws Exception
    {
        if (IDataUtil.isEmpty(infos))
        {
            return;
        }

        IDataset ruleInfo = infos.getData(0).getDataset("TIPS_TYPE_ERROR");

        if (IDataUtil.isEmpty(ruleInfo))
        {
            return;
        }

        for (int iIndex = 0, iSize = ruleInfo.size(); iIndex < iSize; iIndex++)
        {
            IData rule = ruleInfo.getData(iIndex);
            String tipsCode = rule.getString("TIPS_CODE");
            String tipsInfo = rule.getString("TIPS_INFO");
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", tipsCode, tipsInfo);
        }

    }
	
	
}
