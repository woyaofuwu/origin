
package com.asiainfo.veris.crm.order.web.person.bank;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 银行签约信息查询
 */
public abstract class SignedBankPayment extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(SignedBankPayment.class);

    public static final String msgSuc = "S";

    public static final String msgFail = "F";

    public static final String msgKey = "MSG";

    public static final String msgTypeKey = "MSG_TYPE";

    public static final String msgNameKey = "NAME";

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData("cond");

        IDataset infos = CSViewCall.call(this, "SS.SignedBankPaymentSVC.getBankInfo", pageData);

        setInfos(infos);
    }

    public abstract void setInfos(IDataset bankInfo);

}
