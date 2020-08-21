
package com.asiainfo.veris.crm.order.web.person.sundryquery.queryusernpinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryUserNpInfo extends PersonBasePage
{

    public void getUserNpInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.QueryUserNpInfoSVC.getUserNpTradeInfos", data, getPagination("userNpINfonav"));

        setNpUserInfos(output.getData());
        setNpUserInfosCount(output.getDataCount());
    }

    public abstract void setNpUserInfo(IData info);

    public abstract void setNpUserInfos(IDataset infos);

    public abstract void setNpUserInfosCount(long size);

    public void updateDealTag(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.QueryUserNpInfoSVC.updateDealTag", data);
        setAjax(dataset);

    }

}
