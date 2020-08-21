
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.deposit;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GiftFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class GiftFeeProductAction implements IProductModuleAction
{
    @SuppressWarnings("unchecked")
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        SaleDepositTradeData saleDepositTd = (SaleDepositTradeData) dealPmtd;
        String depositType = saleDepositTd.getDepositType();

        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        String preType = saleActiveReqData.getPreType();
        String isConFirm = saleActiveReqData.getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            return;

        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleActiveReqData.getCallType()))
            return;

        if (depositType.equals("1"))
        {
            GiftFeeTradeData giftFeeData = new GiftFeeTradeData();
            giftFeeData.setUserId(uca.getUserId());
            giftFeeData.setFeeMode("2");
            giftFeeData.setFeeTypeCode(StringUtils.defaultIfEmpty(saleDepositTd.getPaymentId(), "0"));
            giftFeeData.setFee(StringUtils.defaultIfEmpty(saleDepositTd.getFee(), "0"));
            giftFeeData.setUserIdA(StringUtils.defaultIfEmpty(saleDepositTd.getUserId(), ""));
            giftFeeData.setDiscntGiftId(saleDepositTd.getElementId());
            giftFeeData.setLimitMoney(StringUtils.defaultIfEmpty(saleDepositTd.getLimitMoney(), "0"));
            giftFeeData.setMonths(StringUtils.defaultIfEmpty(saleDepositTd.getMonths(), "0"));
            giftFeeData.setRemark("单独赠送优惠，将帐务管理优惠编码传入帐务");

            btd.add(uca.getSerialNumber(), giftFeeData);
        }
    }
}
