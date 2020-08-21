
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum GgthException implements IBusiException
{

    CRM_GGTH_01("获取用户刮刮卡资料失败:%s！"), //
    CRM_GGTH_02("验证码错误！"), //
    CRM_GGTH_03("奖品档次编码不能为空"), //
    CRM_GGTH_04("奖品编码不能为空"), //
    CRM_GGTH_05("卡号不能为空"), //
    CRM_GGTH_06("校验码不能为空"), //
    CRM_GGTH_07("参与原因不能为空"), //
    CRM_GGTH_08("兑奖类型不能为空"), //
    CRM_GGTH_09("兑换的奖品必须是非实物类的电子刮刮卡奖品!"), //
    CRM_GGTH_10("此奖品不能有多条参数配置"), //
    CRM_GGTH_11("此奖品参数配置不存在或者错误"), //
    CRM_GGTH_12("报错信息：s%");

    private final String value;

    private GgthException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }

}
