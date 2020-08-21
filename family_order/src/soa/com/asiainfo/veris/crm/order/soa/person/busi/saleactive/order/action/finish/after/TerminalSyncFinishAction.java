
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.util.SaleActiveActionUtil;

public class TerminalSyncFinishAction implements ITradeFinishAction
{
    @SuppressWarnings("unchecked")
    public void executeAction(IData mainTrade) throws Exception
    {
        IDataset tradeSaleGoodsList = TradeSaleGoodsInfoQry.getTradeSaleGoods(mainTrade.getString("TRADE_ID"), BofConst.MODIFY_TAG_ADD);

        if (IDataUtil.isEmpty(tradeSaleGoodsList))
        {
            return;
        }

        IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));

        if (tradeSaleActive.getData(0).getString("RSRV_TAG3", "").equals("T"))
        {
            return;
        }

        MainTradeData mainTradeData = new MainTradeData(mainTrade);

        for (int index = 0, size = tradeSaleGoodsList.size(); index < size; index++)
        {
            SaleGoodsTradeData saleGoodsTradeData = new SaleGoodsTradeData(tradeSaleGoodsList.getData(index));

            String resTypeCode = saleGoodsTradeData.getResTypeCode();

            if (!"4".equals(resTypeCode))
            {
                continue;
            }

            String resTag = saleGoodsTradeData.getResTag();

            String resCheckTag = getResCheckTag(saleGoodsTradeData);

            if (!"1".equals(resTag) || "-1".equals(resCheckTag))
            {
                continue;
            }
            
            String resCode = saleGoodsTradeData.getResCode();
            if(StringUtils.isBlank(resCode) || "0".equals(resCode))
            {
            	continue;
            }

            IData intfCommonParam = SaleActiveActionUtil.buildOccupyActionParams(saleGoodsTradeData, mainTradeData, mainTrade, tradeSaleActive.getData(0));

            intfCommonParam.put("X_CHOICE_TAG", resCheckTag);
            
            CSAppCall.call("CS.TerminalCheckSVC.occupyTerminalByResNo", intfCommonParam);
        }
    }

    private String getResCheckTag(SaleGoodsTradeData saleGoodsTradeData)
    {
        String modifyTag = saleGoodsTradeData.getModifyTag();

        if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
        {
            return "0"; // 销售
        }

        if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
        {
            return "1"; // 返销
        }

        return "-1";
    }
}
