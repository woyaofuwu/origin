
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum DbException implements IBusiException // 数据库异常
{
    CRM_DB_1("地州编码不能为空!"), //
    CRM_DB_2("调用储存过程INSERT_INTO_BH_TRADE失败:%s"), //
    CRM_DB_3("调用储存过程proc_for_specialOpenAndStop失败:%s"), //
    CRM_DB_4("调用储存过程proc_wastesms_resume失败:%s"), //
    CRM_DB_5("调用储存过程proc_wastesms_stop_and_open失败:%s"), //
    CRM_DB_6("该地市编码[<span class='star'>%s</span>]无法获得有效数据库连接！"), //
    CRM_DB_7("根据地州编码%s找不到对应的数据库！"), //
    CRM_DB_8("随e行手机号归属地州与主号码不一致! "), //
    CRM_DB_9("数据库配置错误！"), //
    CRM_DB_10("调用储存过程P_CFG_EXECUTESQL失败:%s"); //

    private final String value;

    private DbException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
