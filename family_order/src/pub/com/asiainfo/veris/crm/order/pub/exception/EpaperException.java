
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum EpaperException implements IBusiException
{
    CRM_SEALADM_CERT_1("查询印章管理员角色证书无数据！"), CRM_SEALADM_CERT_2("角色ID为[%s]的印章管理员证书已经申请，不能重复申请！"), CRM_SEALADM_CERT_3("印章管理员角色证书申请入库失败！"), CRM_SEALADM_CERT_4("印章管理员角色证书申请接口调用失败，操作结果[%s]，错误描述[%s]！"), CRM_SEALADM_CERT_5("吊销时查询印章管理员角色证书[%s]不存在！"),
    CRM_SEALADM_CERT_6("印章管理员角色证书[%s]已吊销，不能重复操作！"), CRM_SEALADM_CERT_7("印章管理员角色证书吊销出库失败！"), CRM_SEALADM_CERT_8("印章管理员角色证书[%s]调用失败,操作结果[%s]，错误描述[%s]！"), CRM_Hall_CERT_1("此营业厅印章类型为:[%s]的印章证书已申请，不能再重复申请！"), CRM_Hall_CERT_2("吊销时查询营业厅证书[%s]不存在！"),
    CRM_Hall_CERT_3("营业厅印章证书[%s]已吊销，不能重复操作！"), CRM_Hall_CERT_4("营业厅印章证书吊销出库失败！"), CRM_Hall_CERT_5("营业厅印章证书[%s]调用失败,操作结果[%s]，错误描述[%s]！"), CRM_Hall_CERT_6("此营业厅没有申请营业厅印章证书！"), CRM_Hall_CERT_7("此营业厅印章证书不存在！"), CRM_MOULAGE_1("新增印模信息失败！"), CRM_MOULAGE_2(
            "新增印模信息入库日志失败！"), CRM_EPAPER_1("上传失败，文件对象流为空！"), CRM_EPAPER_2("定单标识TRADE_ID不能为空！"), CRM_EPAPER_3("库名称LIB_NAME不能为空！"), CRM_EPAPER_4("库标识LIB_ID不能为空！"), CRM_EPAPER_5("根据库ID[%s][%s]查询库对象不存在！"), CRM_EPAPER_6("上传印库文件出错！"), CRM_EPAPER_SEAL1(
            "同一营业厅，同一类型的印章只能申请一次，不能重复申请！"), CRM_EPAPER_SEAL2("印章管理员证书没有申请！"), CRM_EPAPER_SEAL3("印章申请失败！"), CRM_EPAPER_SEAL4("申请印章入库日志失败！"), CRM_EPAPER_SEAL5("印章审批失败！"), CRM_EPAPER_SEAL6("印章审批时添加日志失败！"), CRM_EPAPER_SEAL7("不能对未审批或审批未通过的印章进行签名！"),
    CRM_EPAPER_SEAL8("印章签名作废接口调用失败,操作结果[%s]，错误描述[%s]！"), CRM_EPAPER_SEAL9("印章签名取证接口调用失败,操作结果[%s]，错误描述[%s]！"), CRM_EPAPER_SEAL10("印章签名验证接口调用失败,操作结果[%s]，错误描述[%s]！"), CRM_EPAPER_SEAL11("印章签名接口调用失败,操作结果[%s]，错误描述[%s]！"), CRM_EPAPER_SEAL12(
            "此营业厅标识下的此印章类型的印章已存在，不能重复！"), CRM_EPAPER_SEAL13("不能对未签名的印章执行作废操作！"), CRM_EPAPER_SEAL14("不能对未签名的印章进行取证！"), CRM_EPAPER_SEAL15("不能对未签名的印章进行验证！"), CRM_EPAPER_CRTPDF1("PDF文档签名接口调用失败,操作结果[%s]，错误描述[%s]！"), CRM_EPAPER_CRTPDF2(
            "PDF文档签名验证接口调用失败,操作结果[%s]，错误描述[%s]！"), CRM_EPAPER_CRTPDF3("trade_id为[%s]的电子受理单未生成！"), CRM_EPAPER_CRTPDF4("PDF文档签名取证接口调用失败,操作结果[%s]，错误描述[%s]！"), CRM_EPAPER_TEMPLATE1("新增电子模板入库失败！"), CRM_EPAPER_TEMPLATE2("新增模板入库日志失败！"),
    CRM_EPAPER_TEMPLATE3("电子模板修改失败！"), CRM_EPAPER_TEMPLATE4("修改模板入库日志失败！"), CRM_EPAPER_TEMPLATE5("根据模板标识[%s]删除模板不存在！"), CRM_EPAPER_TEMPLATE6("模板[%s]记录删除失败！"), CRM_EPAPER_TEMPLATE7("模板[%s]文件删除失败！"), CRM_EPAPER_TEMPLATE8("删除模板入库日志失败！"),
    CRM_EPAPER_TEMPLATE9("电子模板审批失败！"), CRM_EPAPER_TEMPLATE10("电子模板发布失败！"), CRM_EPAPER_TEMPLATE11("模板发布入库日志失败！"), CRM_EPAPER_TRADE1("订单标识TRADE_ID不能为空！"), CRM_EPAPER_TRADE2("根据定单标识[%s]查询定单信息无数据"), CRM_EPAPER_TRADE3("业务类型TRADE_TYPE_CODE不能为空"),
    CRM_EPAPER_TRADE4("根据业务类型[%s]匹配电子模板无数据"), CRM_EPAPER_TRADE5("该业务流水号[%s]已生成电子受理单！"), CRM_EPAPER_TRADE6("根据模板ID[%s]查询业务受理单模板不存在！"), CRM_EPAPER_TRADE7("印章信息没有配置或配置错误！"), CRM_EPAPER_TRADE8("根据印章库标识[%s]查询印章对象不存在!"), CRM_EPAPER_TRADE9(
            "根据订单标识[%s]获取签名信息不存在！"), CRM_EPAPER_TRADE10("受理单未生成!"), CRM_EPAPER_WEBSERVICE1("TD_S_COMMPARA表没有配置PARAM_CODE=[%s]的服务");

    private final String value;

    private EpaperException(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
