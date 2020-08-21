
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.widenet;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;

public class DealPbossPrintNameAction implements ITradeAction
{
    /**
     *宽带及固话业务的产品、服务、资费名称保存，pboss打单使用。
     * 
     * @author yuyj3
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<ProductTradeData> productTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        for (ProductTradeData productTradeData : productTradeDatas)
        {
            String productName = UProductInfoQry.getProductNameByProductId(productTradeData.getProductId());
            productTradeData.setRsrvStr5(productName);
        }

        List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        for (DiscntTradeData discntTradeData : discntTradeDatas)
        {
            String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntTradeData.getElementId());
            discntTradeData.setRsrvStr5(discntName);
        }

        List<SvcTradeData> svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        for (SvcTradeData svcTradeData : svcTradeDatas)
        {
            String serviceName = USvcInfoQry.getSvcNameBySvcId(svcTradeData.getElementId());
            svcTradeData.setRsrvStr5(serviceName);
        }
    }

}
