
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.action;

import java.sql.Timestamp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAccountAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAcctConsignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetActInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;

/**
 * 依据PBOSS竣工时间更新相关台帐时间
 * 
 * @author chenzm
 */
public class ProcessPbossFinishBroadbandDateAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String finishDate = "";

        IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(tradeId);
        if (IDataUtil.isEmpty(finishInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_14);
        }
        finishDate = finishInfos.getData(0).getString("FINISH_DATE");
        if (StringUtils.isBlank(finishDate))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_15);
        }

        if ("9712".equals(tradeTypeCode) || "9711".equals(tradeTypeCode))
        {// 开户的结束时间也要往后偏移
            // // 资费的处理
            IDataset tradeDiscntDataset = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);// .selectPbossEndData(tradeId,
            // finishDate);
            String endDate = tradeDiscntDataset.getData(0).getString("END_DATE");
            String startDate = tradeDiscntDataset.getData(0).getString("START_DATE");
            int day = SysDateMgr.dayInterval(startDate, endDate);
            String discntEndDate = SysDateMgr.addDays(finishDate, day) + SysDateMgr.END_DATE;
            TradeDiscntInfoQry.updateStartEndDate(tradeId, finishDate, discntEndDate);
            //
        }
        else
        {
            TradeDiscntInfoQry.updateStartDate(tradeId, finishDate);
        }
        // 处理产品
        TradeProductInfoQry.updateStartDate(tradeId, finishDate);

        // 更新服务时间
        String finishDate2 = finishDate.substring(0, 10) + SysDateMgr.START_DATE_FOREVER;
        TradeSvcInfoQry.updateStartDate(tradeId, finishDate2);

        // 处理attr
        TradeAttrInfoQry.updateStartDate(tradeId, finishDate);

        TradeResInfoQry.updateStartDate(tradeId, finishDate);

        TradeUserInfoQry.updateOpenDate(tradeId, finishDate);
        TradePayRelaInfoQry.updateStartDate(tradeId, finishDate);
        TradeWideNetInfoQry.updateStartDate(tradeId, finishDate);
        TradeWideNetActInfoQry.updateStartDate(tradeId, finishDate);
        TradeUserAcctDayInfoQry.updateStartDate(tradeId, finishDate);
        TradeAccountAcctDayInfoQry.updateStartDate(tradeId, finishDate);

        Timestamp timestamp = SysDateMgr.encodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, SysDateMgr.getSysDate());
        String startDate = DateFormatUtils.format(timestamp.getTime(), SysDateMgr.PATTERN_TIME_YYYYMMDD);

        TradeAcctConsignInfoQry.updateStartDate(tradeId, startDate);
        TradeAcctInfoQry.updateStartDate(tradeId, startDate);

        if ("9711".equals(tradeTypeCode))
        {
            String portNum = "";
            String roomName = "";
            String areaName = "";
            IDataset tradePbossAttrList = TradePbossAttrInfoQry.getTradePbossAttrByTrade(tradeId);

            for (int t = 0; t < tradePbossAttrList.size(); t++)
            {
                IData tradePbossInfo = tradePbossAttrList.getData(t);
                String attrcode = tradePbossInfo.getString("ATTR_CODE");
                String attrvalue = tradePbossInfo.getString("ATTR_VALUE");
                if (StringUtils.equals(attrcode, "CF_PORT"))
                {// 端口号
                    portNum = attrvalue;
                }
                else if (StringUtils.equals(attrcode, "CF_ROOM"))
                {// 机房号
                    roomName = attrvalue;
                }
                else if (StringUtils.equals(attrcode, "CF_TTAREA"))
                {// 片区名称
                    areaName = attrvalue;
                }
            }

            if (tradePbossAttrList.size() > 0)
            {
                TradeWideNetInfoQry.updateTradeWidenetPboss(tradeId, portNum, roomName, areaName);
            }

        }
    }

}
