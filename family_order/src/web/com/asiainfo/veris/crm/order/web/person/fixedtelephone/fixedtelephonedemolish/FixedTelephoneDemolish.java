
package com.asiainfo.veris.crm.order.web.person.fixedtelephone.fixedtelephonedemolish;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public class FixedTelephoneDemolish extends PersonBasePage
{
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        if (StringUtils.isBlank(data.getString("SERIAL_NUMBER")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IDataset result = CSViewCall.call(this, "SS.FixTelDemolishRegSVC.tradeReg", data);
        this.setAjax(result);
    }
}
