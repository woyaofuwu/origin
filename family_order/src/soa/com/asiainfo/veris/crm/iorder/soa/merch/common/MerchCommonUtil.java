package com.asiainfo.veris.crm.iorder.soa.merch.common;

import com.ailk.org.apache.commons.lang3.StringUtils;

public class MerchCommonUtil {

	// 标识不需要打印的 PROCESS_TAG_SET 第几位
	public static final int PROCESS_TAGSET_INDEX = 36;
	// 不需要打印对应的值
	public static final String NO_PRINT_PROCESS_TAG_VALUE = "N";
	// 配置每页的打印业务数量,默认是5
	public static final String PER_PAGE_PRINT_COUNT_CONFIG = "PER_PAGE_PRINT_COUNT_CONFIG";

	/**
	 * 是否限制打印或者发短信
	 * 
	 * @param processTagSet
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static boolean isLimit(String processTagSet) throws Exception {
		if (StringUtils.isBlank(processTagSet))
			return false;
		if (processTagSet.length() > PROCESS_TAGSET_INDEX) {
			String tagValue = processTagSet.substring(PROCESS_TAGSET_INDEX - 1, PROCESS_TAGSET_INDEX);
			if (StringUtils.equalsIgnoreCase(tagValue, NO_PRINT_PROCESS_TAG_VALUE))
				return true;
		}
		return false;
	}

}
