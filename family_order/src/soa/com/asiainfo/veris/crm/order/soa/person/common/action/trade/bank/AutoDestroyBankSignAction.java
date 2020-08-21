
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.bank;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * 过户销户银行自动解约
 * 
 * @author liutt
 */
public class AutoDestroyBankSignAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        CSAppCall.call("SS.BankPaymentManageIntfSVC.autoDestroyBankSign", params);

    }

}
