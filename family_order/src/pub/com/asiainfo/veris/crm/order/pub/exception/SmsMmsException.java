
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum SmsMmsException implements IBusiException
{

    CRM_SMSMMS_001("查询短信列表时无数据！"), CRM_SMSMMS_002("查询彩信列表时无数据！"), CRM_SMSMMS_003("查询彩信轨迹时无数据！"), CRM_SMSMMS_004("查询短信轨迹时无数据！"), CRM_SMSMMS_005("没有可以显示的数据！");

    private final String value;

    private SmsMmsException(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        // TODO Auto-generated method stub
        return value;
    }

}
