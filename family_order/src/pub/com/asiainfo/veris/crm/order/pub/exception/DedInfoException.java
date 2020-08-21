
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum DedInfoException implements IBusiException // 订单异常
{
    CRM_DedInfo_1("被举号请输入非86开头的手机号！"), //
    CRM_DedInfo_01("举报手机号不能为空"), //
    CRM_DedInfo_02("举报手机号为错误的手机号！"), //
    CRM_DedInfo_03("调用一级BOSS出错！"), //
    CRM_DedInfo_04("该地市编码[<span class='star'>%s</span>]无法获得有效数据库连接！"), //
    CRM_DedInfo_05("用户品牌信息不全！"), //
    CRM_DedInfo_06("用户品牌参数表信息不全！"), //
    CRM_DedInfo_07("该举报用户资料不全，请于系统管理员联系！"), //
    CRM_DedInfo_08("被举报号码不能为空！"), //
    CRM_DedInfo_09("被举报号码有非法字符！"), //
    CRM_DedInfo_10("获取不良信息无数据！"), //
    CRM_DedInfo_11("获取无数据！"), //
    CRM_DedInfo_12("无重复举报记录！"), //
    CRM_DedInfo_13("请选择不良信息后，再继续办理业务！"), //
    CRM_DedInfo_14("请配置正确的加黑业务类型参数!"), //
    CRM_DedInfo_15("不良信息举报处理时一级客服返回报错，还请联系总部核查工单处理报错的原因，谢谢！"), //
    CRM_DedInfo_16("请配置URL地址！请与系统管理员联系"), //
    CRM_DedInfo_17("查询开始时间不能为空"), //
    CRM_DedInfo_18("查询结束时间不能为空"), //
    CRM_DedInfo_19("举报时间不能为空"), //
    CRM_DedInfo_20("最多只可以查询用户当月和上月的短信详单信息，请修改查询开始时间"), //
    CRM_DedInfo_21("查询开始时间与查询结束时间必须为同一月份"), //
    CRM_DedInfo_22("用户资料不存在"), //
    CRM_DedInfo_23("被举报号码未有向举报号码发送短信的记录！"), //
    CRM_DedInfo_24("查询该不良信息工单无数据，请稍后再进行查询，谢谢！"), //
    CRM_DedInfo_25("请选择不良信息后，再继续办理业务！"), //
    CRM_DedInfo_26("不良信息举报催办时一级客服返回报错，还请联系总部核查工单处理报错的原因，谢谢！"), //
    CRM_DedInfo_27("此业务不能进行催办！"), //
    CRM_DedInfo_28("查询信息无资料！"), //
    CRM_DedInfo_29("调用一级客服接口进行举报回退出错"), //
    CRM_DedInfo_30("满足条件的记录不存在"), //
    CRM_DedInfo_31("此号码已经存在，不能再加入"), //
    CRM_DedInfo_32("请选择信息后，再继续办理业务！"), //
    CRM_DedInfo_33("获取HLR黑名单无数据！"), //
    CRM_DedInfo_34("请选择HLR黑名单信息后，再继续办理业务！"), //
    CRM_DedInfo_35("HLR解黑参数为空！"), //
    CRM_DedInfo_36("HLR解黑参数解黑处理方式PARA_CODE5为空！"), //
    CRM_DedInfo_37("HLR解黑参数为空！"), //
    CRM_DedInfo_38("HLR解黑参数业务类型PARA_CODE1为空！"), //
    CRM_DedInfo_39("服务请求标识不能为空！"), //
    CRM_DedInfo_40("举报手机号不能为空！"), //
    CRM_DedInfo_41("受理省编码不能为空！"), //
    CRM_DedInfo_42("被举报号码不能为空！"), //
    CRM_DedInfo_43("服务请求类别不能为空！"), //
    CRM_DedInfo_44("服务号码不能为空！"), //
    CRM_DedInfo_45("预订业务类型不能为空！"), //
    CRM_DedInfo_46("调用IBOSS接口失败:[%s]"), //
    CRM_DedInfo_47("HLR黑名单数据,不是在已加黑状态,INDICT_SEQ:[%s]"), //
    CRM_DedInfo_48("接口调用失败，举报日期格式长度不正确!"), //
    CRM_DedInfo_49("超出预定义服务请求编码级别！nameList size: [%s]  LEVEL_LIST length: [%s]"), //
    CRM_DedInfo_50("一次性导入不能大于1000条记录"), //
    CRM_DedInfo_51("举报手机号码必须为11位数字，号码 [%s] 错误 "), //
    CRM_DedInfo_52("用户举报时间格式不正确，格式必须如下：2013-08-08 08:08:08 "), //
    CRM_DedInfo_53("上载时间格式不正确，格式必须如下：2013-08-08 08:08:08 "), //
    CRM_DedInfo_54("举报对象类型字段值不合法，必须为01、02、03、04。 "), //
    CRM_DedInfo_55("修改服务请求类别字段值不合法。 "), //
    CRM_DedInfo_56("内容分类字段值不合法。 "), //
    CRM_DedInfo_57("处理信息字段值不合法。 "), //
    CRM_DedInfo_58("STATE字段不能为空且只能为0或1 "), //
    CRM_DedInfo_59("查不到该用户有效的语音服务状态！"), //
    CRM_DedInfo_60("该用户是停机用户，不允许办理！"), //
    CRM_DedInfo_61("该用户的提示信息已存在！"), //
    CRM_DedInfo_62("该用户的推荐信息已存在！"), //
    CRM_DedInfo_63("该用户的提示信息不存在！"), //
    CRM_DedInfo_64("元素类型不能为空！"), //
    CRM_DedInfo_65("元素类型的值只能是1或者2！"), //
    CRM_DedInfo_66("元素ID不能为空！"), //
    CRM_DedInfo_67("提示信息不能为空！"), //
    CRM_DedInfo_68("该用户的推荐信息不存在！"), //
    CRM_DedInfo_69("只能是服务或者优惠！"), CRM_DedInfo_70("举报手机号为外省手机号！"), CRM_DedInfo_71("超出预定义服务请求编码级别！nameList size: [%s] , LEVEL_LIST length: [%s] "), CRM_DedInfo_72("文件解析失败！"), CRM_DedInfo_73("创建临时目录出错！"), CRM_DedInfo_74("举报对像类型为空，请联系系统管理员！"),
    CRM_DedInfo_75("一级，二级，三级服务类型错误，请联系系统管理员！"), CRM_DedInfo_76("此业务不能进行举报查询，被举报号码为非本省号码时才可以进行异地举报查询！"), CRM_DedInfo_77("不良信息回复举报失败：%s"), CRM_DedInfo_78("该笔工单被举报号码归属省不为海南，回复失败!"), CRM_DedInfo_79("不良信息回退失败：%s"), CRM_DedInfo_80("不良信息催办失败：%s"),
    CRM_DedInfo_81("该号码已存在于黑白名单中，无法新增！"), CRM_DedInfo_82("该号码不存在于黑白名单中！"), CRM_DedInfo_83("短信白名单用户，无法加黑名单！"), CRM_DedInfo_84("举报手机号无正常数据！"), CRM_DedInfo_85("用户已经是黑名单用户，无法办理业务"), CRM_DedInfo_86("不良信息数据信息不存在。请联系系统管理员!"), CRM_DedInfo_87(
            "当前状态不是已发送短信状态,失败!"), CRM_DedInfo_88("该用户非HLR黑名单!</br>INDICT_SEQ：%s"), CRM_DedInfo_89("不良信息举报处理,异地举报受理失败：%s"), CRM_DedInfo_90("删除失败，该号码是%s!"), CRM_DedInfo_91("该用户已是HLR黑名单,INDICT_SEQ：%s,手机号码：%s"), CRM_DedInfo_92("HLR黑名单数据,不是在已加黑状态,或该用户非HLR黑名单,手机号码：%s")
            ,CRM_DedInfo_93("服务请求分类为不良网站时，被举报号码必须为9"),
            CRM_DedInfo_94("被举报号码位长不能小于5！"),
	            CRM_DedInfo_95("被举报号码以13、14、15、17、18开头位长必须为11位！"),
	            CRM_DedInfo_96("被举报号码不能与举报号码相同！");

    private final String value;

    private DedInfoException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
