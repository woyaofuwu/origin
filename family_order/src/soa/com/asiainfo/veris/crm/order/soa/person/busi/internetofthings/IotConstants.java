
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.WlwBusiHelper;

public final class IotConstants
{
    // 开户，客户资料变更,停开机，补换卡，立即销户，批开，信控停开机，信控销户
    public final  static String ALLOW_TRADE_TYPE_CODE = "10_60_80_110_131_133_141_142_190_191_192_237_272_274_276_500_2016_7101_7110_7210_7220_7230_7240_73_279_268_277_280";

    public final  static String IOT_SVC = "99010000";

    public final  static String IOT_TEST_DISCNT = "20122125,20122108";
    
    
    //区分物联网配置的后缀（9013、9014）
    /** 物联网通用APN在配置表的para19字段的配置后缀 */
    public final static String IOT_COMMON_APN_CONFIG_PARA = "_COMMON_APN"; 
    
    /** 物联网定向APN在配置表的para19字段的配置后缀 */
	public final static String IOT_DIRECT_APN_CONFIG_PARA = "_DIRECT_APN";

    /** 物联网测试期资费在配置表的para20字段的配置后缀 */
    public final static String IOT_TEST_DISCNT_CONFIG_PARA = "_TEST";
    
    /** 物联网NB体验类资费在配置表的para20字段的配置后缀 */
    public final static String IOT_EXPERIENCE_DISCNT_CONFIG_PARA = "_EXPERIENCE";
    
    /** 物联网短信类资费在配置表的para20字段的配置后缀 */
    public final static String IOT_SMS_DISCNT_CONFIG_PARA = "_SMS";
    
    /** 物联网流量类资费在配置表的para20字段的配置后缀 */
    public final static String IOT_FLOW_DISCNT_CONFIG_PARA = "_GPRS";

    /** 物联网需要偏移的正式期资费在配置表的para20字段的配置后缀 */
    public final static String IOT_NORMAL_DISCNT_CONFIG_PARA = "_NORMAL";

	/** 物联网资费（9013） */
    public static IData IOT_DISCNT_CONFIG = new DataMap();
    
    /** 物联网服务（9014） */
    public static IData IOT_SVC_CONFIG = new DataMap();
    
    /** 物联网产品（9015） */
    public static IData IOT_PRODUCT_CONFIG = new DataMap();
	
	 /** 物联网APN信息 */
    public static IData IOT_APN_CONFIG = new DataMap();
    
    /** 物联网PCRF信息） */
    public static IData IOT_PCRF_CONFIG = new DataMap();
    
    /** 物联网流量套餐包含的流量在9013属性配置的PARA_CODE18字段,单位为M/G,非流量套餐为空*/
    public final static String IOT_GPRS_SIZE = "PARA_CODE18";
    
	public final static String PA_DISCOUNT = "Discount"; // Discount 固费折扣率
	public final static String PA_APPROVALNUM = "ApprovalNum"; // ApprovalNum 审批文号
	
    static
    {
        try
        {
        	IOT_PRODUCT_CONFIG = WlwBusiHelper.loadConfigData("9015");
            IOT_SVC_CONFIG = WlwBusiHelper.loadConfigData("9014");
            IOT_DISCNT_CONFIG = WlwBusiHelper.loadConfigData("9013");
            IOT_APN_CONFIG = WlwBusiHelper.loadConfigData("9031");
            IOT_PCRF_CONFIG = WlwBusiHelper.loadConfigData("9032");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
	
    //车联网主体产品（个人）
    public static String CAR_PRODUCT_CODE_P = "I00010700001";
    
	public final static String DIRECT_GPRS_SVC_CODE = "I00010100092";//物联网专用数据通信服务
	public final static String CAR_DIRECT_GPRS_SVC_CODE = "I00010100093";//车联网专用数据通信服务
	public final static String COMM_GPRS_SVC_CODE = "I00010100085";//物联网通用数据通信服
	public final static String NB_GPRS_SVC_CODE = "I00011100008"; //NB-IOT数据通信服务
}
