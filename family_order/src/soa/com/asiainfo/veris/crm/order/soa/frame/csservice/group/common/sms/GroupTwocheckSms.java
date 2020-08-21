
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.sms;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.template.GroupSmsTemplateBean;

public class GroupTwocheckSms
{

    /**
     * 处理二次确认短信
     * 
     * @param secSmsInfos
     * @return
     * @throws Exception
     */
    protected static IData dealSecSms(IDataset secSmsInfos, IData idata, String tradeTypeCode) throws Exception
    {
        IData reusltData = new DataMap();

        if (IDataUtil.isEmpty(secSmsInfos))
        {
            reusltData.put("IsNeedTwoSMSFlag", false);

            return reusltData;
        }

        // 查询当前业务类型限制多久需要发送
        int amount = idata.getInt("AMOUNT", TwoCheckSms.getSucSmsLimitHour(tradeTypeCode));

        // 获取插入TF_B_ORDER_PRE表 所需数据
        IData preOderData = new DataMap();
        preOderData.putAll(idata);

        IData template = secSmsInfos.getData(0);

        template.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));

        // 判断是不是支持分散
        String templateReplaced = template.getString("TEMPLATE_REPLACED");

        String productPreDate = idata.getString("PRODUCT_PRE_DATE");
        if (StringUtils.isNotBlank(productPreDate))
        {
            template.put("TEMPLATE_REPLACED", "预约开通时间为[" + productPreDate + "]。" + templateReplaced);
        }

        template.put("SMS_CONTENT", template.getString("TEMPLATE_REPLACED"));

        template.put("SMS_TYPE", BofConst.GRP_BUSS_SEC);// 集团二次确认短信
        preOderData.put("PRE_TYPE", BofConst.GRP_BUSS_SEC);// 集团二次确认短信 放在order_pre表

        template.put("OPR_SOURCE", "1");// 业务订购类

        reusltData = TwoCheckSms.twoCheck(tradeTypeCode, amount, preOderData, template);

        reusltData.put("IsNeedTwoSMSFlag", true);

        return reusltData;
    }

    // 1,查询当前业务类型是否需要发送短信
    // 2,如果需要,得到模板id,得到模板内容,插入TI_O_SMS
    public static IData judgeSecSms(String tradeTypeCode, IData idata) throws Exception
    {

        IData reusltData = new DataMap();

        /*boolean hasPriv = StaffPrivUtil.isFuncDataPriv(idata.getString("STAFF_ID"), "GRP_SECSMS_CANCEL");

        if (hasPriv)
        {
            reusltData.put("IsNeedTwoSMSFlag", false);
            return reusltData;
        }*/

        // 该业务类型需要发短信
        // 获取短信模板,如果没有配置,则认为也不需要发送二次确认短信
        IDataset templates = TwoCheckSms.queryTradeSmsTemplate(tradeTypeCode, idata);

        if (IDataUtil.isEmpty(templates))
        {

            reusltData.put("IsNeedTwoSMSFlag", false);

            return reusltData;
        }

        // 查询当前业务类型限制多久需要发送
        int amount = idata.getInt("AMOUNT", TwoCheckSms.getSucSmsLimitHour(tradeTypeCode));

        // 获取插入TF_B_ORDER_PRE表 所需数据
        IData preOderData = new DataMap();
        preOderData.putAll(idata);

        // 得到模板变量
        IData varName = TemplateBean.getTemplateVar(templates);

        GroupSmsTemplateBean.getTemplateValues(idata, varName);

        // 模板替换
        IDataset idsTemplate = TemplateBean.replaceTemplate(templates, idata);

        IData template = idsTemplate.getData(0);
        template.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));

        // 判断是不是支持分散
        String templateReplaced = template.getString("TEMPLATE_REPLACED");

        String productPreDate = idata.getString("PRODUCT_PRE_DATE");
        if (StringUtils.isNotBlank(productPreDate))
        {
            template.put("TEMPLATE_REPLACED", "预约开通时间为[" + productPreDate + "]。" + templateReplaced);
        }

        template.put("SMS_CONTENT", template.getString("TEMPLATE_REPLACED"));

        template.put("SMS_TYPE", BofConst.GRP_BUSS_SEC);// 集团二次确认短信
        preOderData.put("PRE_TYPE", BofConst.GRP_BUSS_SEC);// 集团二次确认短信 放在order_pre表

        template.put("OPR_SOURCE", "1");// 业务订购类

        reusltData = TwoCheckSms.twoCheck(tradeTypeCode, amount, preOderData, template);

        reusltData.put("IsNeedTwoSMSFlag", true);

        return reusltData;

    }

    // 1,查询当前业务类型是否需要发送短信
    // 2,如果需要,得到模板id,得到模板内容,插入TI_O_SMS
    public static IData judgeSecSms(String tradeTypeCode, IData idata, IData tradeAllData, BaseReqData reqData, BizData bizData) throws Exception
    {

        IData reusltData = new DataMap();

        /*boolean hasPriv = StaffPrivUtil.isFuncDataPriv(idata.getString("STAFF_ID"), "GRP_SECSMS_CANCEL");

        if (hasPriv)
        {
            reusltData.put("IsNeedTwoSMSFlag", false);
            return reusltData;
        }*/
               
        //针对新校讯通特殊批量不做二次确认短信
        if (tradeTypeCode.equals("3644") && (null!=idata.getString("IS_SECCONFIRM_TAG")&&!"".equals(idata.getString("IS_SECCONFIRM_TAG"))&&!idata.getBoolean("IS_SECCONFIRM_TAG")))
        {
        	reusltData.put("IsNeedTwoSMSFlag", false);
        	return reusltData;
        }

        // 该业务类型需要发短信
        // 获取短信模板,如果没有配置,则认为也不需要发送二次确认短信
        IDataset templates = TwoCheckSms.queryTradeSmsTemplate(tradeTypeCode, idata);

        // 过滤短信配置
        templates = GroupSmsTemplateBean.filterSmsTemplates(templates, tradeAllData, bizData, reqData, idata);

        if (IDataUtil.isEmpty(templates))
        {

            reusltData.put("IsNeedTwoSMSFlag", false);

            return reusltData;
        }

        // 发送二次确认短信
        IDataset secSmsInfos = new DatasetList();

        // 解析模板
        GroupSmsTemplateBean.dealTemplate(templates, new DatasetList(), new DatasetList(), secSmsInfos, tradeAllData, bizData, reqData, idata);

        // 处理二次确认短信
        return dealSecSms(secSmsInfos, idata, tradeTypeCode);

    }

}
