
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

/**
 * @真情回馈类业务异常
 */
public enum UECMmsException implements IBusiException
{

    CRM_UECMMS_1("彩信模板：[%s]不存在!"), CRM_UECMMS_2("输入参数MMS_TEMPLATE_PARAMS配置出错，未找到PAEAM_NAME!"), CRM_UECMMS_3("彩信模板：[%s]未配置[%s]参数"), CRM_UECMMS_4("获取彩信流水号失败!"), CRM_UECMMS_5("目标用户：[%s]不存在!");

    private final String value;

    private UECMmsException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
