
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 用户账期变更发送短信
 * 
 * @author liuke
 */
public class ChangeAcctDaySms implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // 新系统通过INTF_ID来判断是否存在用户账期变更的台账，减少无用的查询
        String intfId = mainTrade.getString("INTF_ID");
        if (StringUtils.isNotBlank(intfId) && StringUtils.indexOf(intfId, "TF_B_TRADE_USER_ACCTDAY,") < 0)
        {
            return;
        }

        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeUserAcctDays = TradeUserAcctDayInfoQry.getTradeUserAcctDayInfoByTradeId(tradeId);
        if (IDataUtil.isNotEmpty(tradeUserAcctDays))
        {
            // 排重
            tradeUserAcctDays = DataHelper.distinct(tradeUserAcctDays, "USER_ID", "");

            for (int i = 0; i < tradeUserAcctDays.size(); i++)
            {
                String userId = tradeUserAcctDays.getData(i).getString("USER_ID");

                IDataset userAcctDays = TradeUserAcctDayInfoQry.getTradeUserAcctDayInfoByTradeIdUserId(tradeId, userId);

                // 存在且为2条记录的才为有账期变更存在,需发短信
                if (IDataUtil.isNotEmpty(userAcctDays) && userAcctDays.size() == 2)
                {
                    String oldAcctDay = userAcctDays.getData(0).getString("ACCT_DAY");
                    String newAcctDay = userAcctDays.getData(1).getString("ACCT_DAY");
                    String newStartDate = userAcctDays.getData(1).getString("START_DATE");

                    IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);

                    String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
                    String tradeTypeCodeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
                    String noticeContent = "尊敬的客户：您办理了" + tradeTypeCodeName + "业务，账期日由" + oldAcctDay + "日变更为" + newAcctDay + "日，于" + newStartDate + "生效。谢谢您的使用。";
                    sendSmsByChangeAcctDay(userInfo, noticeContent);
                }
            }
        }
    }

    /**
     * 账期变更发送短信
     * 
     * @param serialNumber
     * @param noticeContent
     * @param brandCode
     * @throws Exception
     */
    private void sendSmsByChangeAcctDay(IData userInfo, String noticeContent) throws Exception
    {
        IData smsData = new DataMap();
        smsData.put("RECV_OBJECT", userInfo.getString("SERIAL_NUMBER"));
        smsData.put("NOTICE_CONTENT", noticeContent);
        smsData.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
        smsData.put("RECV_ID", userInfo.getString("USER_ID"));
        SmsSend.insSms(smsData);
    }
}
