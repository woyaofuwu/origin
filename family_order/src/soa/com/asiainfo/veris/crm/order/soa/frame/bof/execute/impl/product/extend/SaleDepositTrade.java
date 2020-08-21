
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleDepositData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.BaseProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;

/**
 * 职责：生成订单对象，且仅仅是普通公用的订单对象。 特殊的处理，比如赠送他人的，放在GiftUserProductAction中处理。
 * 
 * @author Mr.Z
 */
public class SaleDepositTrade extends BaseProductModuleTrade implements IProductModuleTrade
{
    public ProductModuleTradeData createSubProductModuleTrade(ProductModuleData dealPmd, List<ProductModuleData> pmds, UcaData uca, BaseReqData brd, ProductTimeEnv env) throws Exception
    {
        BaseSaleDepositData saleDepositData = (BaseSaleDepositData) dealPmd;
        BaseSaleActiveReqData baseSaleActiveReqData = (BaseSaleActiveReqData) brd;
        SaleDepositTradeData saleDepositTradeData = null;

        if (BofConst.MODIFY_TAG_ADD.equals(saleDepositData.getModifyTag()))
        {
            saleDepositTradeData = new SaleDepositTradeData();
            String rsrvStr4 = "";
            String rsrvStr5 = "";
            
            IDataset datas = UpcCall.qryOfferGiftByExtGiftId(saleDepositData.getElementId());//SaleDepositInfoQry.querySaleDepositById(saleDepositData.getElementId(), uca.getUserEparchyCode());
            IData depositData = datas.getData(0);
            String relId = depositData.getString("REL_ID");
            IData extInfo = UpcCall.queryTempChaByCond(relId, "TD_B_SALE_DEPOSIT", null, "A");
            if(IDataUtil.isNotEmpty(extInfo))
            {
            	depositData.putAll(extInfo);
                rsrvStr4 = extInfo.getString("RSRV_STR4");
                rsrvStr5 = extInfo.getString("RSRV_STR5");
            }
            
            saleDepositTradeData.setUserId(uca.getUserId());
            saleDepositTradeData.setUserIdA(uca.getUserId());
            saleDepositTradeData.setElementId(saleDepositData.getElementId());
            saleDepositTradeData.setCampnId(saleDepositData.getCampnId());
            saleDepositTradeData.setInstId(SeqMgr.getInstId());
            String startDate = ProductModuleCalDate.calStartDate(saleDepositData, env);
            saleDepositTradeData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(saleDepositData, startDate);
            saleDepositTradeData.setEndDate(endDate);
            saleDepositTradeData.setProductId(saleDepositData.getProductId());
            saleDepositTradeData.setPackageId(saleDepositData.getPackageId());
            saleDepositTradeData.setDepositType(depositData.getString("GIFT_TYPE"));
            saleDepositTradeData.setADiscntCode(depositData.getString("GIFT_OBJ_ID"));
            saleDepositTradeData.setMonths(depositData.getString("GIFT_CYCLE"));
            saleDepositTradeData.setRemark(saleDepositData.getRemark());
            saleDepositTradeData.setPayMode(StringUtils.defaultIfEmpty(saleDepositData.getPayMode(), ""));
            saleDepositTradeData.setInDepositCode(StringUtils.defaultIfEmpty(saleDepositData.getInDepositCode(), ""));
            saleDepositTradeData.setOutDepositCode(StringUtils.defaultIfEmpty(saleDepositData.getOutDepositCode(), ""));
            saleDepositTradeData.setRelationTradeId(brd.getTradeId());
            saleDepositTradeData.setModifyTag(saleDepositData.getModifyTag());
            saleDepositTradeData.setPaymentId(StringUtils.defaultIfEmpty(saleDepositData.getPaymentId(), "0"));
            saleDepositTradeData.setFee(StringUtils.defaultIfEmpty(saleDepositData.getFee(), "0"));
            saleDepositTradeData.setRsrvNum5(saleDepositData.getProductElementFee());
            saleDepositTradeData.setRsrvStr4(rsrvStr4);
            saleDepositTradeData.setRsrvStr5(rsrvStr5);

            if (saleDepositData.getAttrs() != null && saleDepositData.getAttrs().size() > 0)
            {
                AttrTrade.createAttrTradeData(saleDepositTradeData, saleDepositData.getAttrs(), uca);
            }

            if (!BofConst.DEPOSIT_TYPE_GIFT.equals(depositData.getString("GIFT_TYPE")))
                return saleDepositTradeData;

            saleDepositTradeData.setPaymentId(depositData.getString("PAYMENT_ID", "0"));
            saleDepositTradeData.setFee(depositData.getString("MONEY", "0"));

            if (StringUtils.isBlank(saleDepositData.getFee()))
                return saleDepositTradeData;

            saleDepositTradeData.setFee(saleDepositData.getFee());
        }
        else if (BofConst.MODIFY_TAG_DEL.equals(saleDepositData.getModifyTag()))
        {
            SaleDepositTradeData userSaleDepositTradeData = brd.getUca().getUserSaleDepositByInstId(saleDepositData.getInstId());
            saleDepositTradeData = userSaleDepositTradeData.clone();
            saleDepositTradeData.setModifyTag(saleDepositData.getModifyTag());
            saleDepositTradeData.setEndDate(saleDepositData.getEndDate());
            saleDepositTradeData.setRsrvStr3(baseSaleActiveReqData.getTradeId());
        }

        return saleDepositTradeData;
    }

}
