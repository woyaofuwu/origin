
package com.asiainfo.veris.crm.order.pub.consts;

/**
 * 移动商场补换卡状态枚举类
 */
public enum ExchangeCradStateEnum
{
	WAIT_PAY("PP","待付款"),
    WAIT_CONFIRM("PC","待确认"),
    WAIT_STOCK("PK","待商户备货"),
    WAIT_SEND("PD","待商户发货"),
    WAIT_SIGN("PS","待签收"),
    SUCCESS("SS","已完成"),
    TIME_OUT("OT","超时未付撤销"),
    JS_SUCCESS("1","接受成功"),
    XK_SUCCESS("2","写卡成功"),
    YJ_SUCCESS("3","邮寄成功"),
    JH_SUCCESS("4","激活成功"),
    ONLINE_AUDIT_FALSE("5","在线公司审核不通过"),
    JH_FALSE("6","激活失败")
    ;

    private final String value;
    private final String description;
    private ExchangeCradStateEnum(String value, String description)
    {

        this.value = value;
        this.description = description;
    }

    public String getValue()
    {
        return value;
    }
    public String getDescription()
    {
        return description;
    }
    public static String getDescriptionByValue(String value){
        String result = "";
        for(ExchangeCradStateEnum e : ExchangeCradStateEnum.values()){
            if(e.getValue().equals(value)){
                result = e.getDescription();
            }
        }
        return result ;
    }

}
