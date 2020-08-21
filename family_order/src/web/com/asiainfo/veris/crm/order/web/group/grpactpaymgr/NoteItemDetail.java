
package com.asiainfo.veris.crm.order.web.group.grpactpaymgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;

public abstract class NoteItemDetail extends CSBasePage
{
    public abstract void setDetInfo(IData detInfo);

    public abstract void setInfos(IDataset infos);
}
