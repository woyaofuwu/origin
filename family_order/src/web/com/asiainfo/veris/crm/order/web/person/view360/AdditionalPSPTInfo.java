
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AdditionalPSPTInfo extends PersonBasePage
{
	protected static Logger log = Logger.getLogger(AdditionalPSPTInfo.class);
    /**
     * 信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.GetAdditionalPSPTInfoSVC.qryAdditionalPSPTInfo", data, getPagination("AcctInfoNav"));
        setCond(data);
        setInfos(output.getData());
        setInfosCount(output.getDataCount());
    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
