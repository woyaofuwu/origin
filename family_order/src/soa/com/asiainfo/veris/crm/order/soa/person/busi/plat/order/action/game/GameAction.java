
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.game;

import java.math.BigDecimal;

import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

public class GameAction implements IProductModuleAction
{

    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
    	
    	//将不需要调用服务的判断提前@tanzheng@20190611
    	if (btd.getTradeTypeCode().equals("3800"))
    	{
    		return;
    	}
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatOfficeData officeData = PlatOfficeData.getInstance(dealPmtd.getElementId());

        if (!PlatConstants.PLAT_GAME.equals(officeData.getBizTypeCode()))
        {
            return;
        }

        // 充值，点播现在走服务开通
        if (PlatConstants.OPER_ADD_MONEY.equals(pstd.getOperCode()) || PlatConstants.OPER_ORDER_PLAY.equals(pstd.getOperCode()))
        {
            pstd.setIsNeedPf("0");
            PlatSvcData psd = (PlatSvcData) pstd.getPmd();
            if (PlatConstants.OPER_ADD_MONEY.equals(pstd.getOperCode()))
            {
                BigDecimal yuanPrice = new BigDecimal(officeData.getPrice());// 以元为单位的price
                BigDecimal fenPrice = yuanPrice.multiply(new BigDecimal("100")); // 以分为单位的price
                IBossCall.callGameAddMoney(CSBizBean.getVisit(), uca.getSerialNumber(), String.valueOf(fenPrice.intValue()), psd.getOfficeData().getSpCode(), psd.getOfficeData().getBizCode(), PlatUtils.getOperNumb("BIP2B143", SeqMgr.getTradeId()));
            }
            // else
            // {
            // IBossCall.callGameOrderPlay(CSBizBean.getVisit(), uca.getSerialNumber(), psd.getOfficeData().getSpCode(),
            // psd.getOfficeData().getBizCode());
            // }
        }
    }

}
