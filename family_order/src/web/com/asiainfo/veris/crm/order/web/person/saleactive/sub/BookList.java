
package com.asiainfo.veris.crm.order.web.person.saleactive.sub;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BookList extends PersonBasePage
{
    public abstract IDataset getInfos();

    public void querySaleBookList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        IDataset infos = CSViewCall.call(this, "SS.SaleActiveSVC.querySaleBook", params);
        setInfos(infos);
    }

    public abstract void setInfos(IDataset infos);

}
