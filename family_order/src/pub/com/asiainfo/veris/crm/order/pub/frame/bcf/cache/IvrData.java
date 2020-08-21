
package com.asiainfo.veris.crm.order.pub.frame.bcf.cache;

import java.io.Serializable;

public final class IvrData implements Serializable
{
    public String SERIAL_NUMBER_B; // 主叫号码

    public String SERIAL_NUMBER; // 受理服务号码

    public String CALL_LEVEL; // 受理号码等级:0-普通用户;1-红名单;2-黑名单(科创)

    public String CALL_EPARCHY_CODE; // 主叫号码/呼入号码归属地,默认为ZZZZ

    public String IS_NATIVE; // 是否本省号码 --0:本省号码 1:外省号码

    public String IVRCALLID; // 呼入流水号：科创出入IVRCALLID
}
