
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum IntfIAGWException implements IBusiException // 品牌异常
{
    CRM_IGU_01("用户手机号码错误"), //
    CRM_IGU_02("操作编码错误"), //
    CRM_IGU_03("用户生效时间错误"), //
    CRM_IGU_04("业务代码错误"), //
    CRM_IGU_05("业务接入号错误"), //
    CRM_IGU_06("业务接入号的属性错误"), //
    CRM_IGU_07("用户无签约关系（业务代码、业务接入号和业务接入号的属性组合）"), //
    CRM_IGU_08("用户已在名单内"), //
    CRM_IGU_09("用户不在名单内"), //
    CRM_IGU_10("该业务已终止"), //
    CRM_IGU_11("该业务已暂停"), //
    CRM_IGU_96("信息代码错误"), //
    CRM_IGU_97("信息值错误"), //
    CRM_IGU_98("落地方内部错误"), //
    CRM_IGU_99("其它错误");//

    private final String value;

    private IntfIAGWException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
