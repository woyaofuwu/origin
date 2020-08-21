
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ForegiftInfo extends PersonBasePage
{
    /**
     * 保证金信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataOutput foregiftInfo = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryForegiftInfo", data, null);

        setCond(data);
        setForegiftInfos(foregiftInfo.getData());
    }

    public abstract void setCond(IData cond);

    public abstract void setForegiftInfos(IDataset foregiftInfos);
}
