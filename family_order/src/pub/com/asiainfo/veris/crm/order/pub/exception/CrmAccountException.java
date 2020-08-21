
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum CrmAccountException implements IBusiException // 账户异常
{
    CRM_ACCOUNT_1("服务号码为【%s】的成员无账户信息！"), //
    CRM_ACCOUNT_10("高级付费关系变更，调用账务接口生成付费帐目编码时出错"), //
    CRM_ACCOUNT_100("错误数据！该账户号码不存在！"), //
    CRM_ACCOUNT_101("查询用户欠费失败！"), //
    CRM_ACCOUNT_102("[账户标识：%s]没有变更，付费方式也没有变更！"), //
    CRM_ACCOUNT_103("无默认付费帐户！"), //
    CRM_ACCOUNT_104("多个默认付费帐户！"), //
    CRM_ACCOUNT_105("无帐户资料！"), //
    CRM_ACCOUNT_106("没有找到默认付费账户!"), //
    CRM_ACCOUNT_107("该号码[%s]无默认付费帐目！"), //
    CRM_ACCOUNT_108("该号码[%s]无默认付费帐目！"), //
    CRM_ACCOUNT_109("业务前特殊限制表判断：没有找到用户默认账户！"), //
    CRM_ACCOUNT_11("高级付费关系变更，调用账务接口生成付费帐目编码时出错：%s"), //
    CRM_ACCOUNT_110("业务前特殊限制表判断-用户当前存在高级付费关系不能办理业务！"), //
    CRM_ACCOUNT_112("查询帐户资料无信息！"), //
    CRM_ACCOUNT_113("【%s】的默认付费账户信息不存在，业务不能继续！"), //
    CRM_ACCOUNT_114("查询用户默认帐户资料失败"), //
    CRM_ACCOUNT_115("defaultacct-用户默认帐户资料不存在"), //
    CRM_ACCOUNT_116("【%s】的默认付费账户信息不存在，业务不能继续！"), //
    CRM_ACCOUNT_117("【%s】的默认付费账户与挂账集团不在同一个地市，不能办理该业务！"), //
    CRM_ACCOUNT_118("被代付号码的项目只能同时被一种代付方式作用!"), //
    CRM_ACCOUNT_119("查询服务号码无账户信息"), //
    CRM_ACCOUNT_12("根据ACCT_ID获取有效默认付费关系出错!"), //
    CRM_ACCOUNT_120("查询合并挂账成员号码账户信息异常，业务不能继续！"), //
    CRM_ACCOUNT_121("查询宽带账户信息没有数据！"), //
    CRM_ACCOUNT_122("查询用户默认帐户失败！"), //
    CRM_ACCOUNT_123("查询帐户信息出错"), //
    CRM_ACCOUNT_124("查询%s合同号付费关系资料失败"), //
    CRM_ACCOUNT_125("成员号码账户信息异常，业务不能继续！"), //
    CRM_ACCOUNT_126("传入指定结账日为空!"), //
    CRM_ACCOUNT_127("调用账务接口生成付费帐目编码时出错"), //
    CRM_ACCOUNT_128("调用账务接口生成付费帐目编码时出错，请联系管理员！"), //
    CRM_ACCOUNT_129("分账方式只能通过营业厅才能办理！"), //
    CRM_ACCOUNT_13("查询%s用户帐户资料失败"), //
    CRM_ACCOUNT_130("该号码无默认付费账户！"), //
    CRM_ACCOUNT_131("该号码无默认付费账户信息不存在，业务不能继续"), //
    CRM_ACCOUNT_132("该用户不存在用户结账日！"), //
    CRM_ACCOUNT_133("该用户已经存在账户！"), //
    CRM_ACCOUNT_134("该用户已拥有付费账目编码为[%s]的高级付费关系，不能再次新增，请选择个人付费方式！"), //
    CRM_ACCOUNT_135("请选择付费账目!"), //
    CRM_ACCOUNT_136("输入缴费流水号错误!"), //
    CRM_ACCOUNT_137("获取用户帐期信息无数据!"), //
    CRM_ACCOUNT_138("查询%s合同号查询账户资料失败"), //
    CRM_ACCOUNT_139("合同号不能为空"), //
    CRM_ACCOUNT_14("根据用户标识[%s]查询默认帐户资料不存在！"), //
    CRM_ACCOUNT_140("无高级付费关系信息，不能进行集团统付彩铃属性维护操作！"), //
    CRM_ACCOUNT_142("支付余额不足！"), CRM_ACCOUNT_15("根据账户标识[%s]查询帐户资料不存在！"), //
    CRM_ACCOUNT_16("获取[SERIAL_NUMBER]默认付费账户错误！"), //
    CRM_ACCOUNT_17("获取该号码宽带帐号信息无数据"), //
    CRM_ACCOUNT_18("获取银行名称出错"), //
    CRM_ACCOUNT_19("获取用户付费关系信息无记录!"), //
    CRM_ACCOUNT_2("查询%s用户付费关系资料失败"), //
    CRM_ACCOUNT_20("获取用户账户信息无数据!"), //
    CRM_ACCOUNT_21("获取帐户信息无数据"), //
    CRM_ACCOUNT_22("获取账户关系失败！"), //
    CRM_ACCOUNT_23("获取账户结账日信息失败！"), //
    CRM_ACCOUNT_24("查询用户帐户资料失败"), //
    CRM_ACCOUNT_25("宽带帐户不能为空"), //
    CRM_ACCOUNT_26("没有该银行账号的信息！"), //
    CRM_ACCOUNT_27("没有修改任何账户资料，无需办理相应业务！"), //
    CRM_ACCOUNT_28("您没有做账户信息处理!"), //
    CRM_ACCOUNT_29("请选择账户！"), //
    CRM_ACCOUNT_3("该账号已经被其他用户选占，请重新输入并校验"), //
    CRM_ACCOUNT_30("特殊限制判断:该银行帐号只能对应唯一的帐户名称！"), //
    CRM_ACCOUNT_31("未查询到帐户余额，不能办理"), //
    CRM_ACCOUNT_32("无此用户的账户信息!"), //
    CRM_ACCOUNT_33("无默认付费帐户！"), //
    CRM_ACCOUNT_34("无帐户资料！"), //
    CRM_ACCOUNT_35("查询账目编码不存在,请检查"), //
    CRM_ACCOUNT_36("用户【%s】的账户信息不存在，业务不能继续！"), //
    CRM_ACCOUNT_37("帐户余额不足，不能开户"), //
    CRM_ACCOUNT_38("帐户资料错误，家庭关系有效的情况下帐户资料已经无正常数据"), //
    CRM_ACCOUNT_39("账户资料查询失败，请确认后再操作"), //
    CRM_ACCOUNT_4("该账户不能为自己代付！"), //
    CRM_ACCOUNT_40("账户资料错误，家庭关系有效的情况下账户户资料已经无正常数据"), //
    CRM_ACCOUNT_41("产品账户%s为非独立账户!请重新选择"), //
    CRM_ACCOUNT_42("当前账户不是集团统付账户"), //
    CRM_ACCOUNT_43("分账新增,必须同时输入代扣号和付费账目编码,输入代扣号有误"), //
    CRM_ACCOUNT_44("分账新增,必须同时输入代扣号和付费账目编码,输入账目编码有误"), //
    CRM_ACCOUNT_45("分账信息不能单独删除付费账户,或者账目编码"), //
    CRM_ACCOUNT_46("分账信息不能单独删除付费账户和账目编码,必须同时删除付费限额"), //
    CRM_ACCOUNT_47("赠送号码付费关系资料不存在!"), //
    CRM_ACCOUNT_48("付费账目编码为空，请重新选择！"), //
    CRM_ACCOUNT_49("付费账目项的值为【%s】，数据错误！请联系系统管理员。"), //
    CRM_ACCOUNT_5("该账户不能为自己统付！"), //
    CRM_ACCOUNT_50("该用户不存在此帐户的付费帐目，删除无效"), //
    CRM_ACCOUNT_51("该用户不存在高级付费关系"), //
    CRM_ACCOUNT_52("该用户存在普通付费关系，已由此帐户付费，新增无效"), //
    CRM_ACCOUNT_53("该用户的帐目已由此帐户付费，新增无效"), //
    CRM_ACCOUNT_54("该用户的账户【%s】有【%s】个合账付费关系，不允许办理合并挂账业务！"), //
    CRM_ACCOUNT_55("该用户的账户【%s】有【%s】个有效高级付费关系，不允许办理合并挂账业务！"), //
    CRM_ACCOUNT_56("该用户已经存在高级付费关系"), //
    CRM_ACCOUNT_57("该账户还存在有效的付费关系，无法注销！"), //
    CRM_ACCOUNT_58("该号码[%s]无默认付费帐目！"), //
    CRM_ACCOUNT_59("根据代扣号查询无账户信息"), //
    CRM_ACCOUNT_6("该账户已存在代付绑定关系，业务不能继续办理！"), //
    CRM_ACCOUNT_60("获取车务通产品帐目编码失败！"), //
    CRM_ACCOUNT_61("请选择付费账目！"), //
    CRM_ACCOUNT_62("请选择是否定制详细账目！"), //
    CRM_ACCOUNT_63("商品账户%s为非独立账户!请重新选择"), //
    CRM_ACCOUNT_64("修改前后的分账信息没有变化"), //
    CRM_ACCOUNT_65("用户[%s]默认账户不存在"), //
    CRM_ACCOUNT_66("用户默认帐户资料不存在"), //
    CRM_ACCOUNT_67("原来不存在分账付费,请确认"), //
    CRM_ACCOUNT_68("找不到acctId！"), //
    CRM_ACCOUNT_69("未查询到帐户余额,不能办理"), //
    CRM_ACCOUNT_7("该账户已存在统付绑定关系，业务不能继续办理！"), //
    CRM_ACCOUNT_70("未查询到帐户余额，不能办理"), //
    CRM_ACCOUNT_71("对不起，您的话费余额不足，不能订购此套餐"), //
    CRM_ACCOUNT_72("该帐户已是此用户的默认帐户，新增无效"), //
    CRM_ACCOUNT_73("账务接口查询消费额异常！"), //
    CRM_ACCOUNT_74("账务接口查询返回结果异常！"), //
    CRM_ACCOUNT_75("用户的默认付费帐户存在帐户优惠"), //
    CRM_ACCOUNT_76("用户无默认付费帐户资料"), //
    CRM_ACCOUNT_77("根据银行账号没有找到用户！"), //
    CRM_ACCOUNT_78("办理过组合产品且合帐用户请至家庭缴费界面办理!"), //
    CRM_ACCOUNT_79("帐户资料不存在"), //
    CRM_ACCOUNT_8("该账户已经存在宽带账户表，请重新输入并校验！"), //
    CRM_ACCOUNT_80("帐户余额不足,不能办理手机支付开户"), //
    CRM_ACCOUNT_81("代扣号对应的账户不存在"), //
    CRM_ACCOUNT_82("账务编码不能为空"), //
    CRM_ACCOUNT_83("查询用户帐户托收信息出错！"), //
    CRM_ACCOUNT_84("该用户是合帐用户，并且该服务号码是合帐户的代表号(即票据打印号码)，请先到普通付费关系变更界面取消合帐关系，或者更换代表号，然后再办理销号业务"), //
    CRM_ACCOUNT_85("账户资料不存在"), //
    CRM_ACCOUNT_87("根据帐户id查询关系表中的用户id时出错"), //
    CRM_ACCOUNT_88("代表号不存在原号码的帐户中，请输入正确的代表号"), //
    CRM_ACCOUNT_89("代表号不存在目标号码的帐户中，请输入正确的代表号"), //
    CRM_ACCOUNT_9("该账户与成员不在同一个地州，您没有集团成员全省高级付费关系变更权限，不能办理业务！请联系权限管理员分配！"), //
    CRM_ACCOUNT_90("高级付费关系变更，调用账务接口生成付费帐目编码时出错"), //
    CRM_ACCOUNT_91("帐务鉴权失败,原因:%s"), //
    CRM_ACCOUNT_92("没有选择明细帐目，无法完成拼串"), //
    CRM_ACCOUNT_93("没有选择综合帐目，无法完成拼串"), //
    CRM_ACCOUNT_94("根据一级综合帐目编码查询到二级明细帐目无记录"), //
    CRM_ACCOUNT_95("根据一级综合帐目编码未查询到二级明细帐目"), //
    CRM_ACCOUNT_96("该号码[%s]无默认付费帐户信息！"), //
    CRM_ACCOUNT_97("该号码[%s]无默认付费帐目！"), //
    CRM_ACCOUNT_98("该号码【%s】没有账户信息！"), //
    CRM_ACCOUNT_99("错误数据！该号码没有与之相对应的账户！"), 
    CRM_ACCOUNT_141("根据集团客户ID[%s]获取账户信息无数据!"), 
    CRM_ACCOUNT_143("银行编码不存在！"),
    CRM_ACCOUNT_144("调用账务接口生成付费帐目编码时出错!"),
    CRM_ACCOUNT_145("获取集团用户[%s]的账户余额出错!"),
    CRM_ACCOUNT_146("集团产品（用户%s）已欠费，不能办理该业务!");
    
    private final String value;

    private CrmAccountException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
