
package com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.telephone.TelInfoQry;
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

public class DealPbossFinishDate implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
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
        if (tradeTypeCode == "9701" || tradeTypeCode == "9706" || tradeTypeCode == "9750" || tradeTypeCode == "9751")
        {// 开户的结束时间也要往后偏移
            // // 资费的处理
            IDataset tradeDiscntDataset = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);// .selectPbossEndData(tradeId,
            // finishDate);
            String endDate = tradeDiscntDataset.getData(0).getString("END_DATE");
            String startDate = tradeDiscntDataset.getData(0).getString("START_DATE");
            int day = SysDateMgr.dayInterval(startDate, endDate);
            String discntEndDate = SysDateMgr.addDays(finishDate, day);
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
        // TradeWideNetInfoQry.updateStartDate(tradeId, finishDate);
        // TradeWideNetActInfoQry.updateStartDate(tradeId, finishDate);
        TradeUserAcctDayInfoQry.updateStartDate(tradeId, finishDate);
        TradeAccountAcctDayInfoQry.updateStartDate(tradeId, finishDate);
        String startCycle = SysDateMgr.getDateForYYYYMMDD(finishDate);
        TradeAcctConsignInfoQry.updateStartDate(tradeId, startCycle.substring(0, 6));
        TradeAcctInfoQry.updateStartDate(tradeId, startCycle);

        if ("9701".equals(tradeTypeCode) || "9751".equals(tradeTypeCode))
        {
            String portNum = "";
            String roomName = "";
            String areaName = "";
            String trunkId = "";
            String switchId = "";
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
                else if (StringUtils.equals(attrcode, "RM_TRUNKID"))
                {//
                    trunkId = attrvalue;
                }
                else if (StringUtils.equals(attrcode, "RM_BOARDID"))
                {
                    switchId = attrvalue;
                }
            }
            String instId = "";
            String userId = mainTrade.getString("USER_ID");
            if (StringUtils.isNotBlank(portNum) && StringUtils.isNotBlank(roomName) && StringUtils.isNotBlank(areaName))
            {
                IDataset tradeTelephoneInfo = TelInfoQry.getTelTradeInfo(tradeId);

                if (IDataUtil.isNotEmpty(tradeTelephoneInfo))
                {
                    instId = tradeTelephoneInfo.getData(0).getString("INST_ID");
                    TelInfoQry.updateTeleRsrv123(instId, userId, roomName, areaName, portNum,tradeId);
                }
            }
            if (StringUtils.isNotBlank(switchId) && StringUtils.isNotBlank(trunkId))
            {
                String[] trunkIds = trunkId.split(",");
                for (int i = 0; i < trunkIds.length; i++)
                {
                    String trunckIdReal = trunkIds[i];
                    int trunckIdNum = Integer.parseInt(trunckIdReal);
                    String updateStaffId = CSBizBean.getVisit().getStaffId();
                    String updateDepartId = CSBizBean.getVisit().getDepartId();
                    TelInfoQry.insertTrunk("", switchId, trunckIdNum, userId, finishDate, instId, updateStaffId, updateDepartId, "PBOSS回传");
                }
            }
        }

    }

}
