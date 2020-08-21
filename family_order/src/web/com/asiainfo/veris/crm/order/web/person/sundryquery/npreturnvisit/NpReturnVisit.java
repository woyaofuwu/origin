
package com.asiainfo.veris.crm.order.web.person.sundryquery.npreturnvisit;

import java.util.Calendar;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NpReturnVisit extends PersonBasePage
{
    public void getInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //IDataOutput output = CSViewCall.callPage(this, "SS.NpReturnVisitSVC.getNpOutInfos", data, getPagination("infofonav"));
        IDataset rtDataset = CSViewCall.call(this, "SS.NpReturnVisitSVC.getNpOutInfos", data);
        setInfos(rtDataset);
        //setInfosCount(output.getDataCount());

    }

    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        Calendar c = Calendar.getInstance();
        String sysTime = DateFormatUtils.format(c.getTime(), "yyyy-MM-dd");
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        String first = DateFormatUtils.format(c.getTime(), "yyyy-MM-dd");
        param.put("START_DATE", first);
        param.put("END_DATE", sysTime);
        param.put("TRADE_STAFF_ID", getVisit().getStaffId());
        param.put("IS_RETURN", "未回访");
        param.put("AREA_CODE", getVisit().getCityCode());
        setCondition(param);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.NpReturnVisitSVC.submitNpInfo", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long size);
}
