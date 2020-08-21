
package com.asiainfo.veris.crm.order.web.person.interboss.mobileoperation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RemoteQueryCustInfo extends PersonBasePage
{
    /**
     * 查询客户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getCustInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        IDataset dataset = CSViewCall.call(this, "SS.RemoteQueryCustInfoSVC.getCustInfo", param);
        IData retData = dataset.getData(0);
        setEditInfo(retData);

    }

    public abstract void setEditInfo(IData info);

}
