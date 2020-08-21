package com.asiainfo.veris.crm.iorder.pub.family.consts;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zhangxi
 *
 */
public class Family360Constants {

	//我的家消费公共class
	public static final String BILL_COMMON = "e_navy-lighter e_ico-pic-xxs e_hide-phone e_ico-";

	//我的家消费图片
	public static final Map<Integer, String> BILL_IMG = new HashMap<Integer, String>() {

		private static final long serialVersionUID = 1L;

		{
			put(1, "package2"); // 套餐及固定费
			put(2, "call"); // 语音通信费
			put(3, "msg"); // 短彩信费
			put(4, "net"); // 上网费
			put(5, "diamond"); // 自有增值业务费用
			put(6, "bill"); // 代收费业务费用
			put(7, "money"); // 其他费用
			put(8, "fare"); // 优惠及减免
		}
	};

}
