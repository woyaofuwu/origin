package com.asiainfo.veris.crm.order.soa.frame.bof.data.consts; 

/**
 * @description 
 * 	视频流量包信息：
 * 		商品名称								商品类型编码	商品编码				价格		ServiceIdList
 *		视频定向流量月包（话费扣费）-9元		10100			9991010000000010003 	9元			某个APP的ServiceID
 *		视频定向流量月包（话费扣费）-18元		10100			9991010000000010006 	18元		N个APP对应的ServiceID，1<=N<=3
 *		视频定向流量月包（话费扣费）-24元		10100			9991010000000010001 	24元		某个APP的ServiceID
 * @author chenmw3
 * @date 2017-04-21
 *
 */
public class VideoFlowConst{
		//资费属性的ATTR_CODE
		public static final String discntAttrCodeService1 = "APP_SERVICE_ID_1";
		public static final String discntAttrCodeService2 = "APP_SERVICE_ID_2";
		public static final String discntAttrCodeService3 = "APP_SERVICE_ID_3";
		//资费属性特殊值
		public static final String discntAttrSpecialValueService = "-1";
		//视频流量资费的全量编码
		public static final String ctrmProductId_9 = "APP_9";
		public static final String ctrmProductId_18 = "APP_18";
		public static final String ctrmProductId_24 = "APP_24";
}
