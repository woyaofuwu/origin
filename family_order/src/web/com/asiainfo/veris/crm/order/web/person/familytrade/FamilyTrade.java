
package com.asiainfo.veris.crm.order.web.person.familytrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FamilyTrade extends PersonBasePage
{

    /**
     * 添加成员时校验成员号码
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkAddMeb(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        CSViewCall.call(this, "SS.FamilyTradeSVC.checkAddMeb", pageData);

        pageData.put("START_DATE", SysDateMgr.getSysTime());
        setAjax(pageData);
    }

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        setViceInfos(CSViewCall.call(this, "SS.FamilyTradeSVC.queryFamilyMebList", data));
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.FamilyTrade.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);
}
