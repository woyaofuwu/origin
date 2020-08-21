
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum BankPaymentManageException implements IBusiException
{

    CRM_BANKPAYMENT_1("1202:该用户未办理总对总缴费签约业务！"), //
    CRM_BANKPAYMENT_2("1203:该用户已经办理总对总缴费签约业务！"), //
    CRM_BANKPAYMENT_3("1205:该用户当前已有10个关联副号码记录！"), //
    CRM_BANKPAYMENT_4("1210:该副号码用户已办理总对总缴费签约业务！"), //
    CRM_BANKPAYMENT_5("1211:副号码用户已将主号码设为黑名单用户！"), //
    CRM_BANKPAYMENT_6("1208:该用户已作为副号码与其他签约号码关联！"), //
    CRM_BANKPAYMENT_7("4005:副号码用户资料不存在！"), //
    CRM_BANKPAYMENT_8("1212:副号码状态异常！"), //
    CRM_BANKPAYMENT_9("1306:调用一级接口解约副号码检验出错！"), //
    CRM_BANKPAYMENT_10("1300:调用一级接口关联副号码检验出错！"), //
    CRM_BANKPAYMENT_11("1301:调用一级接口副号签约信息同步出错[%s]"), //
    CRM_BANKPAYMENT_19("1213:该用户关联副号码信息已失效！"), //
    CRM_BANKPAYMENT_20("1201:该用户关联副号码信息已失效:[%s]"), //
    CRM_BANKPAYMENT_50("查询用户无业务办理数据！"), //
    CRM_BANKPAYMENT_51("1305:调用一级接口主号签约信息同步出错！"), //
    CRM_BANKPAYMENT_52("1305:调用一级接口主号签约信息同步出错:返回为空！"), //
    CRM_BANKPAYMENT_53("1305:调用一级接口主号签约信息同步出错:[%s]"), //
    CRM_BANKPAYMENT_55("1209:用户月结日不允许办理解约！"), //
    CRM_BANKPAYMENT_56("1305:调用一级接口主号签约信息同步出错!"), //
    CRM_BANKPAYMENT_57("1301:调用一级接口副号签约信息同步出错:返回为空！"), //
    CRM_BANKPAYMENT_100("1300:调用一级接口关联副号码检验出错[%s]"), CRM_BANKPAYMENT_200("未配置证件类型转换关系，请配置3128参数！"), //
    CRM_BANKPAYMENT_201("1304:调用一级接口银行账号签约校验出错[%s]"), CRM_BANKPAYMENT_202("调用一级接口银行账号签约校验出错:返回为空！"), //
    CRM_BANKPAYMENT_203("调用一级接口主号签约信息同步出错[%s]"), CRM_BANKPAYMENT_204("查询用户信息无资料！"), //
    CRM_BANKPAYMENT_205("预约单生成失败！"), CRM_BANKPAYMENT_206("调用帐务缴费接口失败！"), CRM_BANKPAYMENT_207("副号码签约记录无法处理！"), //
    CRM_BANKPAYMENT_208("用户无有效的签约或关联主号码记录！"), CRM_BANKPAYMENT_209("调用一级接口缴费充值出错:[%s]"), //
    CRM_BANKPAYMENT_210("1213:未实名登记！"), //
    CRM_BANKPAYMENT_211("信息不一致！"), CRM_BANKPAYMENT_212("450062:证件类型错误！"), CRM_BANKPAYMENT_213("不存在预约签约订单"), //
    CRM_BANKPAYMENT_214("未配置服务状态转换关系，请配置3129参数！"), //
    CRM_BANKPAYMENT_215("不存在需要二次确认的订单！"), CRM_BANKPAYMENT_216("找不到主号签约子订单！"), //
    CRM_BANKPAYMENT_217("找不到主号签约信息变更子订单！"), CRM_BANKPAYMENT_218("回复内容错误，不支持此操作！"), //
    CRM_BANKPAYMENT_219("2995:关联副号码业务不需要二次确认！"), //
    CRM_BANKPAYMENT_220("1206:该副号码用户已办理总对总缴费签约业务！"), //
    CRM_BANKPAYMENT_221("1207:副号码用户已将主号码设为黑名单用户！"), //
    CRM_BANKPAYMENT_222("1208:该用户已作为副号码与其他签约号码关联！"), //
    CRM_BANKPAYMENT_223("4005:用户资料不存在！"), //
    CRM_BANKPAYMENT_224("1204:用户语音服务状态不正常，无法受理！"), CRM_BANKPAYMENT_225("1208:该用户已作为副号码与其他签约号码关联！"), //
    CRM_BANKPAYMENT_226("1300:调用一级接口关联副号码检验出错:[%s]"), CRM_BANKPAYMENT_227("1300:调用一级接口关联副号码检验出错:返回为空！"), //
    CRM_BANKPAYMENT_228("副号码效验处理失败！"), CRM_BANKPAYMENT_229("该用户无相关副号码信息！"), //
    CRM_BANKPAYMENT_230("获取主号签约信息失败！"), CRM_BANKPAYMENT_231("获取副号关联信息失败！"), CRM_BANKPAYMENT_232("100403:用户没有有效的签约信息！"), //
    CRM_BANKPAYMENT_233("1202:该用户当前无有效的总对总缴费签约记录！"), //
    CRM_BANKPAYMENT_234("1209:用户月结日不允许办理解约！"), //
    CRM_BANKPAYMENT_235("调用一级接口主号签约信息变更同步出错:[%s]"), CRM_BANKPAYMENT_236("1204:副号码用户的语音服务状态为[%s],不能办理此业务！"), //
    CRM_BANKPAYMENT_237("调用一级接口副号解约信息同步出错:[%s]"), CRM_BANKPAYMENT_238("1202:主号签约信息不存在！"), //
    CRM_BANKPAYMENT_239("2998:输入参数[%s]不正确！"), //
    CRM_BANKPAYMENT_250("1401:调用[%s]接口进行副号签约出错：[%s]"), CRM_BANKPAYMENT_251("副号码订单处理失败！"), //
    CRM_BANKPAYMENT_252("4015: 不存在签约订单！"), //
    CRM_BANKPAYMENT_253("1216:非自动缴费不允许变更阀值和额度！"), //
    CRM_BANKPAYMENT_254("1215:未做任何变更！"), //
    CRM_BANKPAYMENT_255("100402:用户信息不存在！"), //
    CRM_BANKPAYMENT_256("100007:没有配置银行卡签约缴费平台信息！"), //
    CRM_BANKPAYMENT_257("100007:没有查到该号码对应的签约信息！"), //
    CRM_BANKPAYMENT_258("100003:没有查到该号码对应的用户信息！"), //
    CRM_BANKPAYMENT_259("操作类型输入不对！"), CRM_BANKPAYMENT_260("1212:证件号不一致！"), CRM_BANKPAYMENT_261("副号码用户已将所有号码设为黑名单！"), //
    CRM_BANKPAYMENT_262("%s:解约单生成失败！:%s"), //
    CRM_BANKPAYMENT_263("用户没有订购关键时刻缴费提醒服务，删除不成功！"), //
    CRM_BANKPAYMENT_264("MODIFY_TAG值不正确，业务未受理！"), //
    CRM_BANKPAYMENT_265("根据INST_ID[%s]未找到对应的ATTR记录！"), //
    CRM_BANKPAYMENT_266("未知X_TAG:[%s]"), CRM_BANKPAYMENT_267("营业库TD_S_SUPERBANK表不存在该记录SUPER_BANK_CODE:[%s]"), //
    CRM_BANKPAYMENT_268("营业库TD_S_SUPERBANK_CTT表不存在该记录SUPER_BANK_CODE:[%s]"), //
    CRM_BANKPAYMENT_269("营业库TD_B_BANK表不存在该记录BANK_CODE:[%s]"), //
    CRM_BANKPAYMENT_270("营业库TD_B_BANK_CTT表不存在该记录BANK_CODE:[%s]"), //
    CRM_BANKPAYMENT_271("未知tableName:[%s]"), CRM_BANKPAYMENT_272("1214:证件类型不一致！"), //
    CRM_BANKPAYMENT_273("2001:该用户无有效的签约记录！"), ;

    private final String value;

    private BankPaymentManageException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
