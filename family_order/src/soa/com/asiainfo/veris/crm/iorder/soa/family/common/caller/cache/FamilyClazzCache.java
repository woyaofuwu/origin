package com.asiainfo.veris.crm.iorder.soa.family.common.caller.cache;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyParamInfoQry;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import org.mvel2.MVEL;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyClazzCache {

	private FamilyClazzCache() {

	}

	private static Map<String, Object> classMap = new HashMap<String, Object>();

	private static Map<String, Method> classMethodMap = new HashMap<String, Method>();

	public static List getClassInstance(IData role, String pointCode, String pointType) throws Exception {
		String roleCode = IDataUtil.chkParam(role, KeyConstants.ROLE_CODE);
		String roleType = IDataUtil.chkParam(role, KeyConstants.ROLE_TYPE);
		String busiType = IDataUtil.chkParam(role, KeyConstants.BUSI_TYPE);
		IDataset transInfos = FamilyParamInfoQry.queryTransParam(busiType, pointCode, roleCode, roleType);
		if (IDataUtil.isNotEmpty(transInfos)) {
			List clazzLs = new ArrayList();
			for (int i = 0; i < transInfos.size(); i++) {
				IData transInfo = transInfos.getData(i);
				String clazzName = transInfo.getString("PARAM_VALUE1");
				String condition = transInfo.getString("PARAM_VALUE2");
				String pointTypes = transInfo.getString("PARAM_VALUE3");

				if (pointTypes != null) {
					if (pointType == null || pointTypes.indexOf(pointType) == -1) {
						continue;
					}
				}
				
				if (!classMap.containsKey(clazzName)) {
					synchronized (classMap) {
						if (!classMap.containsKey(clazzName)) {
							Class<?> cls = Class.forName(clazzName);
							Object obj = cls.newInstance();
							classMap.put(clazzName, obj);
						}
					}
				}
				
				if (testCondition(role, condition)) {
					clazzLs.add(classMap.get(clazzName));
				}
			}
			return clazzLs;
		}
		return null;
	}

	public static List getClassInstance(IData role, String pointCode) throws Exception {
		return getClassInstance(role, pointCode, null);
	}

	public static Method getClassMethod(Class clazz, String methodName, Class[] methodTypes)
			throws NoSuchMethodException, SecurityException {
		String clazzName = clazz.getName();
		String key = clazzName + methodName;
		if (!classMethodMap.containsKey(clazzName)) {
			synchronized (classMethodMap) {
				if (classMethodMap.containsKey(clazzName)) {
					return classMethodMap.get(clazzName);
				}
				Method method = clazz.getMethod(methodName, methodTypes);
				if (method == null)
					return null;
				classMethodMap.put(key, method);
			}
		}

		return classMethodMap.get(key);
	}

	private static boolean testCondition(IData input, String cond) {
		if (StringUtils.isEmpty(cond)) {
			return true;
		}
		return (Boolean) MVEL.eval(cond, input);
	}

}
