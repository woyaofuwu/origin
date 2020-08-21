
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleCreditData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.BaseProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleCreditInfoQry;

public class SaleCreditTrade extends BaseProductModuleTrade implements IProductModuleTrade
{
    public ProductModuleTradeData createSubProductModuleTrade(ProductModuleData dealPmd, List<ProductModuleData> pmds, UcaData uca, BaseReqData brd, ProductTimeEnv env) throws Exception
    {
        BaseSaleCreditData saleCreditData = (BaseSaleCreditData) dealPmd;

//        IDataset saleCreditInfos = SaleCreditInfoQry.queryByCreditId(saleCreditData.getElementId(), uca.getUserEparchyCode());
//        IData saleCreditInfo = saleCreditInfos.getData(0);
        IData saleCreditInfo = UpcCall.qryOfferComChaTempChaByCond(saleCreditData.getElementId(), saleCreditData.getElementType());
        
        CreditTradeData creditTradeData = new CreditTradeData();
        creditTradeData.setUserId(uca.getUserId());
        creditTradeData.setElementId(saleCreditData.getElementId());
        creditTradeData.setCreditValue(saleCreditInfo.getString("CREDIT_VALUE"));
        creditTradeData.setCreditMode("addCredit");
        creditTradeData.setCreditGiftMonths(saleCreditInfo.getString("CREDIT_GIFT_MONTHS"));
        creditTradeData.setStartDate(saleCreditData.getStartDate());
        creditTradeData.setEndDate(saleCreditData.getEndDate());
        creditTradeData.setModifyTag(saleCreditData.getModifyTag());
        creditTradeData.setRemark(saleCreditData.getRemark());

        return creditTradeData;
    }

}
