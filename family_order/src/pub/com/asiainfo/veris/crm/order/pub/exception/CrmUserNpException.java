
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum CrmUserNpException implements IBusiException // 用户异常
{

    CRM_USER_NP_101("用户【%s】状态为携出欠费停机！"), CRM_USER_NP_511("查询用户携转资料异常！！"), CRM_USER_NP_513("查询已经恢复的用户携转资料异常！"), CRM_USER_NP_521("用户携转标识USER_TAG_SET为空异常！"), CRM_USER_NP_522("该用户当前携转标识USER_TAG_SET=【%s】，该状态不能携入复机！！"), CRM_USER_NP_540016(
            "该号码【%s】用户资料表或携转资料表存在多条记录，信息异常！"), CRM_USER_NP_540010("该号码【%s】没有任何可以携入复机的记录！！"), CRM_USER_NP_540017("该号码【%s】没有携转资料信息！！"), CRM_USER_NP_540018("该号码【%s】有多条携转资料记录！！"), CRM_USER_NP_540019("该号码【%s】没有号码归属地信息！！"), CRM_USER_NP_696("【%s】"),
    CRM_USER_NP_116029("该号码没有办理携入申请业务，或携入申请已生效"), CRM_USER_NP_638("FLOWID=【%s】的记录有未完工的已生效工单，请核查！"), CRM_USER_NP_125096("查询NP台帐表异常！"), CRM_USER_NP_125097("SOA参数取值错误，COMMANDCODE=【%s】！"), CRM_USER_NP_125098("ACK返回成功：修改NP表状态时异常！"), CRM_USER_NP_125009(
            "ACK返回失败-人工干预：修改NP表状态时异常！"), CRM_USER_NP_124098("ACK接收方根据FLOW_ID=【%s】修改TRADE_NP表状态异常！"), CRM_USER_NP_124110("ACK返回成功：根据FLOWID=【%s】查询NP台帐表异常！！"), CRM_USER_NP_124111("ACK返回成功：根据FLOWID=【%s】查询NP台帐表记录异常！"), CRM_USER_NP_124099(
            "ACK接收方根据TRADE_ID=【%s】修改TRADE_NP表状态异常！"), CRM_USER_NP_125101("携号转网：响应返回时根据消息ID查询NP台帐表异常！"), CRM_USER_NP_196("SOA参数COMMANDCODE=【%s】取值异常！"), CRM_USER_NP_125003("响应返回成功：修改NP表状态时异常！"), CRM_USER_NP_125301("响应返回失败-完全撤销：修改携转申请撤销工单取消标识异常！"),
    CRM_USER_NP_125302("响应返回失败-完全撤销：将撤消后的工单搬迁到历史台帐表发生异常！"), CRM_USER_NP_125303("响应返回失败-完全撤销：台帐表内记录删除发生异常！"), CRM_USER_NP_125304("响应返回失败-完全撤销：修改携转申请撤销NP台帐表取消标识异常！"), CRM_USER_NP_125305("携入申请取消-完全撤销：查询原携转申请工单异常，FLOWID=【%s】！"), CRM_USER_NP_125306(
            "携入申请取消-完全撤销：修改历史台帐表内原携入申请开户工单发生异常，TRADE_ID=【%s】！"), CRM_USER_NP_125307("携入申请取消-完全撤销：将开户工单由历史表搬到台帐主表发生异常，TRADE_ID=【%s】！"), CRM_USER_NP_125308("携入申请取消-完全撤销：删除历史台帐表内该条工单发生异常，TRADE_ID=【%s】！"), CRM_USER_NP_125309(
            "携转申请取消：修改NP子台帐表工单异常，TRADE_ID=【%s】！"), CRM_USER_NP_125005("响应返回失败-待时重发：修改NP表状态时异常！"), CRM_USER_NP_125006("响应返回失败-人工干预：修改NP表状态时异常！"), CRM_USER_NP_125211("告知返回-根据FLOWID=【%s】查询NP台帐表异常！"),
    CRM_USER_NP_124210("告知返回成功：根据FLOWID=【%s】查询NP台帐表异常！"), CRM_USER_NP_125209("告知返回-根据TRADE_ID=【%s】查询NP台帐表异常！"), CRM_USER_NP_125112("告知返回-根据TRADE_ID=【%s】查询主台帐表异常！"), CRM_USER_NP_125113("告知申请返回成功：NP台帐时间修改异常！"), CRM_USER_NP_125114(
            "告知返回成功-携出生效：NP台帐内容修改异常！"), CRM_USER_NP_125115("告知返回成功-携出生效：修改主台帐执行时间异常！"), CRM_USER_NP_125118("告知返回成功-携出生效：修改【TF_B_ORDER】执行时间异常！"), CRM_USER_NP_125116("告知返回成功：NP台帐内容修改异常！"), CRM_USER_NP_125117("告知生效返回成功：修改主台帐执行时间异常！"),
    CRM_USER_NP_125201("告知返回失败-完全撤销：修改携转申请撤销工单取消标识异常！"), CRM_USER_NP_125202("告知返回失败-完全撤销：将撤消后的工单搬迁到历史台帐表发生异常！"), CRM_USER_NP_125203("告知返回失败-完全撤销：台帐表内记录删除发生异常！"), CRM_USER_NP_125204("告知返回失败-完全撤销：修改携转申请撤销NP台帐表取消标识异常！"), CRM_USER_NP_125405(
            "携入申请取消-完全撤销：查询原携转申请工单异常，FLOWID=【%s】！"), CRM_USER_NP_125406("携入申请取消-完全撤销：修改历史台帐表内原携入申请开户工单发生异常，TRADE_ID=【%s】！"), CRM_USER_NP_125407("携入申请取消-完全撤销：将开户工单由历史表搬到台帐主表发生异常，TRADE_ID=【%s】！"), CRM_USER_NP_125408(
            "携入申请取消-完全撤销：删除历史台帐表内该条工单发生异常，TRADE_ID=【%s】！"), CRM_USER_NP_125409("携转申请取消-完全撤销：修改NP子台帐表工单异常，TRADE_ID=【%s】！"), CRM_USER_NP_125108("告知返回失败-待时重发：修改NP子台帐表工单异常！"), CRM_USER_NP_125109("告知返回失败-人工干预：修改NP子台帐表工单异常！"), CRM_USER_NP_115006(
            "获取COMMANDCODE=【%s】错误，应该为【ACT_NOTIFY】！"), CRM_USER_NP_115007("根据手机号【%s】查询用户资料异常！"), CRM_USER_NP_115008("该手机号【%s】用户携转标识异常！"), CRM_USER_NP_209522("号码归还时，查询携出方用户资料异常"), CRM_USER_NP_116034("serial_number=[%s]的用户当前已经是已携出或者携入携出状态!"),
    CRM_USER_NP_116037("sserial_number=[%s]的用户当前已经有未完工的携出生效工单!"), CRM_USER_NP_115001("获取携入申请业务子台账资料失败！USER_ID=【%s】!"), CRM_USER_NP_325659("ModifyUserNpInfoNpOut携出申请生成NpOut用户资料不成功,查询携出申请历史NP台帐无记录."),CRM_USER_NP_121212("携转业务月末两天不允许进行办理！"), CRM_USER_NP_9527("【%s】");
    private final String value;

    private CrmUserNpException(String value)
    {

        this.value = value;
    }

    @Override
    public String getValue()
    {

        return value;
    }

}
