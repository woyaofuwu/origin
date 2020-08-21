
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleGoodsData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.BaseProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;

/**
 * 职责：生成实物订单对象，且仅仅是公用的实物订单对象。 对于特殊的，比如实物是终端的情况、实物是礼品的情况；计算酬金的逻辑等，都放在action中处理。 对实物处理的productAction有：
 * TerminalCheckProductAction 实物终端 预占、设置订单对象字段 GiftGoodsProductAction 实物为礼品 预占、设置订单对象字段 DeviceSubSidyFeeProductAction
 * 终端补贴 设置订单对象字段 DeputyFeeProductAction 酬金计算 设置订单对象字段
 * 
 * @author Mr.Z
 */
public class SaleGoodsTrade extends BaseProductModuleTrade implements IProductModuleTrade
{
    public ProductModuleTradeData createSubProductModuleTrade(ProductModuleData dealPmd, List<ProductModuleData> pmds, UcaData uca, BaseReqData brd, ProductTimeEnv env) throws Exception
    {
        BaseSaleGoodsData saleGoodsData = (BaseSaleGoodsData) dealPmd;
        SaleGoodsTradeData saleGoodsTradeData = null;
        BaseSaleActiveReqData baseSaleActiveReqData = (BaseSaleActiveReqData) brd;

        if (BofConst.MODIFY_TAG_ADD.equals(saleGoodsData.getModifyTag()))
        {
            saleGoodsTradeData = new SaleGoodsTradeData();
//            IDataset datas = SaleGoodsInfoQry.querySaleGoodsByGoodsId(saleGoodsData.getElementId());
            IData goodsData = UpcCall.qryOfferComChaTempChaByCond(saleGoodsData.getElementId(), saleGoodsData.getElementType());//datas.getData(0);
            IData offerInfo = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_SALEGOODS, saleGoodsData.getElementId());
            
            saleGoodsTradeData.setUserId(uca.getUserId());
            saleGoodsTradeData.setElementId(saleGoodsData.getElementId());
            saleGoodsTradeData.setInstId(SeqMgr.getInstId());
            saleGoodsTradeData.setProductId(saleGoodsData.getProductId());
            saleGoodsTradeData.setPackageId(saleGoodsData.getPackageId());
            saleGoodsTradeData.setCancelDate(SysDateMgr.getTheLastTime());
            saleGoodsTradeData.setDestroyFlag(goodsData.getString("DESTROY_FLAG", ""));
            saleGoodsTradeData.setGiftMode(goodsData.getString("GIFT_MODE", ""));
            saleGoodsTradeData.setGoodsNum("1");
            saleGoodsTradeData.setGoodsState("0");
            saleGoodsTradeData.setGoodsValue(goodsData.getString("GOODS_VALUE", ""));
            saleGoodsTradeData.setGoodsName(offerInfo.getString("OFFER_NAME", ""));
            saleGoodsTradeData.setResTag(goodsData.getString("RES_TAG", ""));
            saleGoodsTradeData.setResTypeCode(goodsData.getString("GOODS_TYPE_CODE", ""));
            saleGoodsTradeData.setResId(goodsData.getString("RES_ID", ""));
            saleGoodsTradeData.setResCode(saleGoodsData.getResCode());
            saleGoodsTradeData.setPostAddress(saleGoodsData.getPostAddress());
            saleGoodsTradeData.setPostCode(saleGoodsData.getPostCode());
            saleGoodsTradeData.setPostName(saleGoodsData.getPostName());
            saleGoodsTradeData.setRelationTradeId(brd.getTradeId());
            saleGoodsTradeData.setModifyTag(saleGoodsData.getModifyTag());
            saleGoodsTradeData.setRemark(saleGoodsData.getRemark());
            saleGoodsTradeData.setRsrvStr2(baseSaleActiveReqData.getNetOrderId());
            saleGoodsTradeData.setRsrvStr10(baseSaleActiveReqData.getCampnType());
            saleGoodsTradeData.setRsrvStr8(saleGoodsData.getSaleStaffId());
            saleGoodsTradeData.setRsrvNum5(saleGoodsData.getProductElementFee());
        }
        else if (BofConst.MODIFY_TAG_DEL.equals(saleGoodsData.getModifyTag()))
        {
            SaleGoodsTradeData userSaleGoodsTradeData = brd.getUca().getUserSaleGoodsByInstId(saleGoodsData.getInstId());
            saleGoodsTradeData = userSaleGoodsTradeData.clone();
            saleGoodsTradeData.setModifyTag(saleGoodsData.getModifyTag());
            saleGoodsTradeData.setGoodsState("1");
            saleGoodsTradeData.setCancelDate(saleGoodsData.getEndDate());
            saleGoodsTradeData.setRsrvStr3(baseSaleActiveReqData.getTradeId());
            if ("4".equals(saleGoodsTradeData.getResTypeCode()))
            {
                saleGoodsTradeData.setResTag(saleGoodsData.getResTag());
            }
        }

        return saleGoodsTradeData;
    }

}
