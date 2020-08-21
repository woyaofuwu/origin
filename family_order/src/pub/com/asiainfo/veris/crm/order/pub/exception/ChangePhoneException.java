
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ChangePhoneException implements IBusiException
{

    CRM_CHANGEPHONE_1("号码[%s]六个月内存在有效的改号记录！"), CRM_CHANGEPHONE_2("向其他省查询老号码出错 [%s][%s]"), CRM_CHANGEPHONE_3("修改关系表出错"), CRM_CHANGEPHONE_21("该用户不是有效的改号用户:[%s]"), CRM_CHANGEPHONE_22("调用一级客服接口进行改号平台取消出错:[%s]"), CRM_CHANGEPHONE_200("查询用户数据无信息！"),
    CRM_CHANGEPHONE_201("201，号码信息为空！"), CRM_CHANGEPHONE_202("8000,没有配置param_attr参数！"), CRM_CHANGEPHONE_203("你输入的号码，不是改号号码！"), CRM_CHANGEPHONE_204("8002,该号码不是改号用户，或已过期不能续约！"), CRM_CHANGEPHONE_205("更新续约信息失败！"), CRM_CHANGEPHONE_210("号码[%s],没有客户资料!"),
    CRM_CHANGEPHONE_2107("2107:手机号对应的用户不存在"), CRM_CHANGEPHONE_2108("2108:vip卡号对应的大客户不存在"), CRM_CHANGEPHONE_2109("2998:资料错误"), CRM_CHANGEPHONE_2906("号码[%s]已经激活,请先做激活取消再做预申请！"), CRM_CHANGEPHONE_2907("号码[%s]存在有效的改号预受理记录！"), CRM_CHANGEPHONE_2908(
            "没有配置证件类型转换数:[%s]参数！"), CRM_CHANGEPHONE_2909("号码[%s]当前状态为[%s],不能办理改号业务!"), CRM_CHANGEPHONE_2910("未查询到帐户余额,不能办理！"), CRM_CHANGEPHONE_2911("该用户办理了国漫一卡多号业务！"), CRM_CHANGEPHONE_2912("该用户办理了国际租机业务！"), CRM_CHANGEPHONE_2913("该用户是代付关系的主付费号码！"),
    CRM_CHANGEPHONE_2914("该用户是办理银行托收或银行代扣的号码！"), CRM_CHANGEPHONE_2915("该号码无账户资料!"), CRM_CHANGEPHONE_2916("该用户没有语音服务！"), CRM_CHANGEPHONE_2917("该用户是TD无线座机! "), CRM_CHANGEPHONE_2918("该用户有未完工工单！"), CRM_CHANGEPHONE_2919("没有此用户，或此用户已销户:[%s]!"),
    CRM_CHANGEPHONE_2920("向其他省查询老号码出错![%s],[%s]"), CRM_CHANGEPHONE_2921("激活用新号码不是本地号码！"), CRM_CHANGEPHONE_2922("调用一级客服接口 进行 改号平台激活出错[%s]!"), CRM_CHANGEPHONE_2923("调用一级客服接口 进行 改号平台激活出错！"), CRM_CHANGEPHONE_2924("没有配置证件类型转换数:[%s]参数！"),
    CRM_CHANGEPHONE_2925("落地方不符合改号业务办理条件:[%s]"), CRM_CHANGEPHONE_2926("没有配置[%s]参数！"), CRM_CHANGEPHONE_2927("新老号码都不是本地号码，不能办理此业务！"), CRM_CHANGEPHONE_2928("调用一级客服接口进行 改号预申请 出错![%s]"), CRM_CHANGEPHONE_2929("号码[%s]没有有效的改号预受理记录！"), CRM_CHANGEPHONE_2930(
            "调用接口进行改号激活/取消同步出错![%s]"), CRM_CHANGEPHONE_2931("新原号码客户信息不匹配！"), CRM_CHANGEPHONE_2932("关联期未满一个月，不能办理此业务！"), CRM_CHANGEPHONE_2933("该号码没有有效的改号预受理记录！[%s]"), CRM_CHANGEPHONE_2934("该号码没有有效的改号预受理记录！"), CRM_CHANGEPHONE_2935("该号码没有有效的改号预受理记录！"),
    CRM_CHANGEPHONE_2936("该号码没有有效的改号预受理记录！");

    private final String value;

    private ChangePhoneException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
