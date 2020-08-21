
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.util.SaleActiveActionUtil;

public class ReleaseGoodsFinishAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeSaleGoodsList = TradeSaleGoodsInfoQry.getTradeSaleGoods(tradeId, BofConst.MODIFY_TAG_DEL);

        if (IDataUtil.isEmpty(tradeSaleGoodsList))
            return;

        IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(tradeId);

        if (tradeSaleActive.getData(0).getString("RSRV_TAG3", "").equals("S"))
            return;

        for (int index = 0, size = tradeSaleGoodsList.size(); index < size; index++)
        {
            IData goodsData = tradeSaleGoodsList.getData(index);
            String resTypeCode = goodsData.getString("RES_TYPE_CODE");
            String resTag = goodsData.getString("RES_TAG");
            
            //如果是和目尝鲜活动，归还摄像头标记，在此判断 
            if(StringUtils.isNotEmpty(mainTrade.getString("RSRV_STR10"))
            		&&"4".equals(resTypeCode)){
            	if("1".equals(mainTrade.getString("RSRV_STR10"))){
            		IData intfCommonParam = SaleActiveActionUtil.buildReleaseActionParams(new SaleGoodsTradeData(goodsData), mainTrade, tradeSaleActive.getData(0));
                    intfCommonParam.put("X_CHOICE_TAG", "1"); // 返销
                    CSAppCall.call("CS.TerminalCheckSVC.releaseTerminalByResNo", intfCommonParam);
            	}
            	continue;
            }

            if (!"1".equals(resTag))
                continue;

            IData intfCommonParam = SaleActiveActionUtil.buildReleaseActionParams(new SaleGoodsTradeData(goodsData), mainTrade, tradeSaleActive.getData(0));

            intfCommonParam.put("X_CHOICE_TAG", "1"); // 返销

            if ("4".equals(resTypeCode))
            {
                CSAppCall.call("CS.TerminalCheckSVC.releaseTerminalByResNo", intfCommonParam);
            }
            else if ("D".equals(resTypeCode))
            {
                IData data = ResCall.releaseGiftGoods4Sale(goodsData.getString("RES_ID"), mainTrade.getString("TRADE_ID"), Integer.parseInt(goodsData.getString("GOODS_NUM", "1")));

                if (!"0".equals(data.getString("X_RESULTCODE", "1")))
                {
                    CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_38);
                }
            }
        }
    }
}
