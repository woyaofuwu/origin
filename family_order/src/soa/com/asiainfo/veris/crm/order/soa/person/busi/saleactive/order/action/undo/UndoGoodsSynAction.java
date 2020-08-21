
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.util.SaleActiveActionUtil;

public class UndoGoodsSynAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeSaleGoodsList = TradeSaleGoodsInfoQry.getTradeSaleGoods(tradeId, BofConst.MODIFY_TAG_ADD);

        if (IDataUtil.isEmpty(tradeSaleGoodsList))
        {
            return;
        }
        
        IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));

        for (int index = 0, size = tradeSaleGoodsList.size(); index < size; index++)
        {
            IData goodsData = tradeSaleGoodsList.getData(index);
            String resTypeCode = goodsData.getString("RES_TYPE_CODE");
            String resTag = goodsData.getString("RES_TAG");

            if (!"1".equals(resTag))
                continue;

            IData intfCommonParam = SaleActiveActionUtil.buildReleaseActionParams(new SaleGoodsTradeData(goodsData), mainTrade, tradeSaleActive.getData(0));

            intfCommonParam.put("X_CHOICE_TAG", "1"); // 返销

            if ("4".equals(resTypeCode))
            {
            	boolean tag = true;
            	IDataset otherDatas = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(tradeId, "CREDIT_PURCHASES");
        		if(DataUtils.isNotEmpty(otherDatas)){
        			String resCode = goodsData.getString("RES_CODE");
        			if(StringUtils.isNotBlank(resCode)){
        				IDataset retDataset = HwTerminalCall.getTerminalByImei(resCode, "2");//查询非空闲
        				if(IDataUtil.isNotEmpty(retDataset)){
        					IDataset outData = retDataset.first().getDataset("OUTDATA");
        					if(DataUtils.isEmpty(outData)){
        						tag = false;
        					}
        					
        				}
        			}
        			
        		}
        		if(tag){
        			CSAppCall.call("CS.TerminalCheckSVC.releaseTerminalByResNo", intfCommonParam);
        		}
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
