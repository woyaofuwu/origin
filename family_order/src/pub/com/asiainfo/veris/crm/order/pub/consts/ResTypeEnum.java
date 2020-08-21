
package com.asiainfo.veris.crm.order.pub.consts;

public enum ResTypeEnum
{
	IPTV("rsclM.22"), // IPTV
	MODEM("rsclM.15"), // 光猫
    TOPSET("rsclM.14"), // 机顶盒
    PHONE("rsclM.01") // 手机
    ;

    private final String value;

    private ResTypeEnum(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
