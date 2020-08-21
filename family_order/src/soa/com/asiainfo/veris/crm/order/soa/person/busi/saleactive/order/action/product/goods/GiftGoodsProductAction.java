
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.goods;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleGoodsInfoQry;

/**
 * 该action废除，放到tradeAction中去实现。
 * 
 * @author Mr.Z
 */
public class GiftGoodsProductAction implements IProductModuleAction
{

    @SuppressWarnings("unchecked")
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        SaleGoodsTradeData goodsTradeData = (SaleGoodsTradeData) dealPmtd;
        String resTypeCode = goodsTradeData.getResTypeCode();

        if (!"D".equals(resTypeCode))
            return;

//        IDataset goodsInfos = SaleGoodsInfoQry.querySaleGoodsByGoodsId(goodsTradeData.getElementId());
//        IData goodsInfo = goodsInfos.getData(0);
        IData goodsInfo = UpcCall.qryOfferComChaTempChaByCond(goodsTradeData.getElementId(), BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
        String resCheck = goodsInfo.getString("RES_CHECK");

        if (!"1".equals(resCheck))
            return;

        IData param = new DataMap();
        param.put("PRODUCT_ID", goodsTradeData.getProductId());
        param.put("PACKAGE_ID", goodsTradeData.getPackageId());
        param.put("ELEMENT_ID", goodsTradeData.getElementId());
        param.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("RES_TRADE_CODE", "ICheckArticlesInfo");
        // TODO CALL 礼品校验接口
        IData chkGiftData = new DataMap();

        goodsTradeData.setGoodsName(chkGiftData.getString("GIFT_NAME", ""));
        goodsTradeData.setResId(chkGiftData.getString("PROPERTY", ""));
        goodsTradeData.setResCode(chkGiftData.getString("RES_NO", ""));
    }

}
