package com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.merge;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.merge.intf.IMerge;

public class MergeTradeConfig {
	public static final String TYPEID = "MERGE_CONFIG_TYPE";

	private static IData mergeConfigData = new DataMap();

	static {
		try {
			IDataset configSet = StaticUtil.getStaticList(TYPEID);
			for (int i = 0, size = configSet.size(); i < size; i++) {
				try {
					IData configData = configSet.getData(i);
					String typeCode = configData.getString("DATA_ID");
					String className = configData.getString("DATA_NAME");
					if (StringUtils.isBlank(className))
						continue;
					mergeConfigData.put(typeCode, Class.forName(className).newInstance());
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据业务类型获取合并的类
	 * 
	 * @param typeCode
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static IMerge getMergeConfigByTypeCode(String typeCode) throws Exception {
		if (mergeConfigData.containsKey(typeCode))
			return (IMerge) mergeConfigData.get(typeCode);
		// 没有查到
		String className = StaticUtil.getStaticValue(TYPEID, typeCode);
		if (StringUtils.isBlank(className))
			return null;
		mergeConfigData.put(typeCode, Class.forName(className).newInstance());
		return (IMerge) mergeConfigData.get(typeCode);
	}

	/**
	 * 判断是否需要合并
	 * 
	 * @param typeCode
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static boolean checkIsMerge(String typeCode) throws Exception {
		if (mergeConfigData.containsKey(typeCode))
			return true;
		// 没有查到
		String className = StaticUtil.getStaticValue(TYPEID, typeCode);
		if (StringUtils.isBlank(className))
			return false;
		mergeConfigData.put(typeCode, Class.forName(className).newInstance());
		return true;
	}
}
