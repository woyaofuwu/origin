
package com.asiainfo.veris.crm.order.web.person.interboss.mobileoperation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CrossRegServiceQuery extends PersonBasePage
{

    /**
     * 查询客户资料
     * 
     * @param cycle
     * @throws Exception
     */
    public void getCustInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        IDataset dataset = CSViewCall.call(this, "SS.RemoteCrossRegServiceSVC.getCrossRegserviceInfo", param);
        setInfos(dataset);
    }

    public abstract void setInfos(IDataset infos);

}
