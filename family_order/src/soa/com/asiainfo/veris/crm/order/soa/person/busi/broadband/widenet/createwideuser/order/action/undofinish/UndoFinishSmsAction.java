
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.undofinish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSmsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class UndoFinishSmsAction implements ITradeFinishAction
{

    /**
     * 获取模板拼串信息
     * 
     * @param filterSmsInfos
     * @param regSmsInfos
     * @param sucSmsInfos
     * @param rtd
     * @param relaInfo
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected IDataset dealTemplate(IDataset tradeSmsInfos, RegTradeData rtd, IData tradeInfo) throws Exception
    {
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());
        IDataset smsInfos = new DatasetList();
        for (Object obj : tradeSmsInfos)
        {
            IData data = new DataMap();
            data.putAll((IData) obj);
            exector.prepare(rtd, tradeInfo);// 模板变量解析
            String content = TemplateBean.getTemplate(data.getString("TEMPLATE_ID"));
            String replaceContent = exector.applyTemplate(content);
            int idx = replaceContent.indexOf("@{");
            if (idx > -1 && replaceContent.indexOf("}", idx) > -1)
            {
                replaceContent = exector.applyTemplate(replaceContent);
            }
            data.put("NOTICE_CONTENT", replaceContent);
            smsInfos.add(data);
        }
        return smsInfos;
    }

    /**
     * 直接写ti_o_sms表
     * 
     * @param regSmsInfos
     * @param btd
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected void dealUndoTradeSms(IDataset tradeSmsInfos, IData mainTrade) throws Exception
    {
        // 没有登记短信，则返回
        if (IDataUtil.isEmpty(tradeSmsInfos))
        {
            return;
        }

        for (Object obj : tradeSmsInfos)
        {
            IData tradeSmsInfo = (IData) obj;

            IData smsData = smsData = getWidenetCommonSmsInfo(mainTrade, tradeSmsInfo);

            smsData.put("NOTICE_CONTENT", tradeSmsInfo.getString("NOTICE_CONTENT"));

            SmsSend.insSms(smsData);
        }

    }

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String cancelTag = mainTrade.getString("CANCEL_TAG");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String inModeCode = mainTrade.getString("IN_MODE_CODE");

        String tradeId = mainTrade.getString("TRADE_ID");
        String productId = "99999999";// 默认发
        IDataset userSaleActiveInfos = UserSaleActiveInfoQry.getBook2ValidSaleActiveByTradeId(tradeId, serialNumber);
        if (IDataUtil.isNotEmpty(userSaleActiveInfos))
        {
            productId = userSaleActiveInfos.getData(0).getString("PRODUCT_ID");
        }

        RegTradeData regData = new RegTradeData(mainTrade);
        IDataset tradeSmsInfos = TradeSmsInfoQry.getTradeSmsInfos(tradeTypeCode, "ZZZZ", productId, cancelTag, eparchyCode, inModeCode, null);

        // 未获取到短信配置，则直接返回
        if (IDataUtil.isEmpty(tradeSmsInfos))
        {
            return;
        }
        IDataset smsInfos = this.dealTemplate(tradeSmsInfos, regData, null);

        dealUndoTradeSms(smsInfos, mainTrade);

    }

    /**
     * 公用信息 宽带短信
     * 
     * @author chenzm
     * @param btd
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected IData getWidenetCommonSmsInfo(IData mainTrade, IData smsInfo) throws Exception
    {

        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = "";
        if (serialNumber.substring(0, 3).equals("KD_"))
        {
            serialNumber = serialNumber.substring(3);
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            userId = userInfo.getString("USER_ID");
        }
        else
        {
            userId = "-1";
        }
        IData smsData = new DataMap();

        smsData.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
        smsData.put("RECV_OBJECT", serialNumber);
        smsData.put("RECV_ID", userId);
        smsData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
        smsData.put("IN_MODE_CODE", mainTrade.getString("IN_MODE_CODE"));
        smsData.put("SMS_PRIORITY", "5000");
        smsData.put("CANCEL_TAG", mainTrade.getString("CANCEL_TAG"));
        smsData.put("REMARK", "业务短信通知");
        smsData.put("NOTICE_CONTENT_TYPE", smsInfo.getString("SMS_TYPE"));
        smsData.put("SMS_TYPE_CODE", "I0");

        return smsData;
    }

}
