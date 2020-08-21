
package com.asiainfo.veris.crm.order.web.person.fixedtelephone.changeAcctDiscnt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangeAcctDiscnt extends PersonBasePage
{

    public void loadTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("ACCT_ID", data.getString("ACCT_ID"));
        param.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));

        IDataset acctDiscntList = CSViewCall.call(this, "SS.changeAcctDiscntSVC.loadTradeInfo", param);

        this.setDiscntList(acctDiscntList);

        IDataset oldDiscntInfoset = CSViewCall.call(this, "SS.changeAcctDiscntSVC.selectOldInfo", param);

        if (IDataUtil.isNotEmpty(oldDiscntInfoset))
        {
            IData oldDiscntInfo = oldDiscntInfoset.getData(0);
            this.setOldDiscntDaatInfo(oldDiscntInfo);
        }

        int len = acctDiscntList.size();
        int i;
        for (i = 0; i < len; i++)
        {
            IData acctDiscnt = acctDiscntList.getData(i);
            acctDiscnt.put("CODE_NAME", acctDiscnt.getString("DISCNT_CODE") + "          " + acctDiscnt.getString("DISCNT_NAME"));

        }

    }

    public void onSubmitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        if (StringUtils.isBlank(data.getString("SERIAL_NUMBER")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IDataset result = CSViewCall.call(this, "SS.changeAcctDisnct.tradeReg", data);
        this.setAjax(result);
    }

    public abstract void setDiscntList(IDataset foreGifts);

    public abstract void setOldDiscntDaatInfo(IData oldDiscntDaatInfo);
}
