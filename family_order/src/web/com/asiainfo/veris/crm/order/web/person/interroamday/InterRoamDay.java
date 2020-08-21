
package com.asiainfo.veris.crm.order.web.person.interroamday;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class InterRoamDay extends PersonBasePage
{
    public void geneDelDiscntDate(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        IDataset returnInfo = CSViewCall.call(this, "SS.InterRoamDaySVC.GeneDelDiscntDate", data);

        setAjax(returnInfo);
    }

    public void geneNewDiscntDate(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        IDataset returnInfo = CSViewCall.call(this, "SS.InterRoamDaySVC.GeneNewDiscntDate", data);

        setAjax(returnInfo);
    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        CSViewCall.call(this, "SS.InterRoamDaySVC.checkUserInterRoamSvcInfoIsOrder", userInfo);
        IData acctDayInfo = new DataMap();
        acctDayInfo.put("NOW_DATE", SysDateMgr.getSysDate());
        IDataset interRoamDayDiscntInfos = CSViewCall.call(this, "SS.InterRoamDaySVC.GetInterRoamDayDiscntInfo", userInfo);     
        setAcctDayInfo(acctDayInfo);
        setInterRoamDayDiscntInfos(interRoamDayDiscntInfos);
    }
    /**
     * 国漫订购关系查询
     * @param cycle
     * @throws Exception
     */
    public void interRoamQuery(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        IData acctDayInfo = new DataMap();
        acctDayInfo.put("NOW_DATE", SysDateMgr.getSysDate());
        IDataset interRoamDayDiscntInfos = CSViewCall.call(this, "SS.InterRoamDaySVC.interRoamQuery", userInfo);

        setAcctDayInfo(acctDayInfo);
        setInterRoamDayDiscntInfos(interRoamDayDiscntInfos);
    }
    
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.put("REMARK", getData().getString("REMARK"));
        param.put("IS_FROM_FOREGROUND", "1");	//作为是否是从前台过来的标识
        
        param.putAll(data);

        IDataset dataset = CSViewCall.call(this, "SS.InterRoamDayRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setAcctDayInfo(IData acctDayInfo);

    public abstract void setInterRoamDayDiscntInfos(IDataset interRoamDayDiscntInfos);
}
