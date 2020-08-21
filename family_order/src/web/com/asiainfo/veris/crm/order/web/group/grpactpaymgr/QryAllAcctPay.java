
package com.asiainfo.veris.crm.order.web.group.grpactpaymgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;

public abstract class QryAllAcctPay extends CSBasePage
{
    public abstract void setInfo(IData info);

    public abstract void setAcctInfo(IData info);

    public abstract void setAcctInfos(IDataset infos);

    public abstract void setPayInfo(IData info);

    public abstract void setPayInfos(IDataset infos);

    public abstract void setCondition(IData condition);
}
