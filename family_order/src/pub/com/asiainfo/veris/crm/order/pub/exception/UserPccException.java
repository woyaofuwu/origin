
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum UserPccException implements IBusiException // 订单异常
{
    CRM_UserPccInfo_01("输入批售号码或导入号码文件都为空，请检查文件！"), CRM_UserPccInfo_02("村通工程特殊号码维护:该用户非村通公话产品，不能办理该业务！"), CRM_UserPccInfo_03("村通工程特殊号码维护:获取特殊号码最大个数失败！"), CRM_UserPccInfo_04("村通工程特殊号码维护:没有修改特殊号码！"), CRM_UserPccInfo_05(
            "村通工程特殊号码维护:一个村通工程号最多可以申请【%s】个特殊号码！"), CRM_UserPccInfo_06("村通工程特殊号码维护:该【%s】特殊号码是重复号码！"), CRM_UserPccInfo_07("输入文件为空，请检查文件！"), CRM_UserPccInfo_08("必填字段为空，请检查文件！"), CRM_UserPccInfo_09("村通工程特殊号码维护:该【%s】特殊号码是受理号码！"), CRM_UserPccInfo_10(
            "导入数据不能超过10000条！"),
    CRM_USERPCCINFO_02("该用户【%s】使用数据流量至100G后，不再允许订购大中华提速包.");

    private final String value;

    private UserPccException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
