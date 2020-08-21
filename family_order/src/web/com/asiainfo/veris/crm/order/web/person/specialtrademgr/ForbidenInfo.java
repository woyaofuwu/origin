
package com.asiainfo.veris.crm.order.web.person.specialtrademgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 禁查清单
 */
public abstract class ForbidenInfo extends PersonBasePage
{
    public void cancelForbidenInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData temp = getData("hidden", true);
        data.putAll(temp);

        IDataOutput output = CSViewCall.callPage(this, "SS.ForbidenInfoSVC.cancelForbidenInfo", data, getPagination("ForbidenInfoNav"));

    }

    public void insertForbidenInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData temp = getData("hidden", true);
        data.putAll(temp);

        IDataOutput output = CSViewCall.callPage(this, "SS.ForbidenInfoSVC.insertForbidenInfo", data, getPagination("ForbidenInfoNav"));

    }

    public void queryForbidenInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData temp = getData("cond", true);
        data.putAll(temp);

        IDataOutput output = CSViewCall.callPage(this, "SS.ForbidenInfoSVC.getForbidenInfo", data, getPagination("ForbidenInfoNav"));

        IData idata = output.getData().getData(0);
        IDataset otherresults = new DatasetList();

        if (IDataUtil.isNotEmpty(idata))
        {
            otherresults = idata.getDataset("OTHERRESULTS");

            if (IDataUtil.isNotEmpty(otherresults))
            {
                IData otherresult = otherresults.getData(0);

                setInfo(otherresult);
            }
        }

        IData results = idata.getData("RESULTS");

        setUserInfo(results);

        setEditInfo(results);

        setCondition(temp);
    }

    public abstract void setCondition(IData condition);

    public abstract void setEditInfo(IData info);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setUserInfo(IData userInfo);

}
