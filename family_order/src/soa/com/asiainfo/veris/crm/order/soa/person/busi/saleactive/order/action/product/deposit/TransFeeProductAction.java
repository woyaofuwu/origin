
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.deposit;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class TransFeeProductAction implements IProductModuleAction
{
    @SuppressWarnings("unchecked")
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        SaleDepositTradeData saleDepositTD = (SaleDepositTradeData) dealPmtd;
        String payMode = saleDepositTD.getPayMode();

        if (!payMode.equals("1"))
            return;

        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        String preType = saleActiveReqData.getPreType();
        String isConFirm = saleActiveReqData.getIsConfirm();
        //宽带开户支付模式：P：预先支付  A：先装后付
        String widePayMode = saleActiveReqData.getWidePayMode();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            return;

        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleActiveReqData.getCallType()))
            return;

        //先装后付模式的宽带开户，营销活动预受理登记不进行转账操作，不记录otherFee表
        if ("A".equals(widePayMode))
        {
        	return;
        }
        
        OtherFeeTradeData otherFeeTradeData = new OtherFeeTradeData();
        otherFeeTradeData.setOperType(BofConst.OTHERFEE_SAME_TRANS);
        otherFeeTradeData.setUserId(uca.getUserId());
        otherFeeTradeData.setOperFee(saleDepositTD.getFee());
        otherFeeTradeData.setActionCode(saleDepositTD.getADiscntCode());
        otherFeeTradeData.setPaymentId(saleDepositTD.getPaymentId());
        otherFeeTradeData.setInDepositCode(saleDepositTD.getInDepositCode());
        otherFeeTradeData.setOutDepositCode(saleDepositTD.getOutDepositCode());
        otherFeeTradeData.setStartDate(saleActiveReqData.getStartDate());
        otherFeeTradeData.setRsrvStr4(saleDepositTD.getRsrvStr4());
        otherFeeTradeData.setRsrvStr5(saleDepositTD.getRsrvStr5());
        btd.add(uca.getSerialNumber(), otherFeeTradeData);
    }
}
