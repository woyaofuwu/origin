
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum SparkPlansMgrException implements IBusiException // 品牌异常
{
    CRM_SPARKPLANS_1("所选择的记录不存在,请确认！"), //
    CRM_SPARKPLANS_2("剩余量不够,请确认！"), //
    CRM_SPARKPLANS_3("更新调出记录出错！"), //
    CRM_SPARKPLANS_4("插入调入记录出错！"), CRM_SPARKPLANS_5("更新调入记录出错！"), CRM_SPARKPLANS_6("调入员工工号[%s]和调出的员工工号[%s]不能相同！"), CRM_SPARKPLANS_7("回收员工工号[%s]和调入的员工工号[%s]不能相同！"), CRM_SPARKPLANS_8("超过最大导入条数[%s]");

    private final String value;

    private SparkPlansMgrException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
