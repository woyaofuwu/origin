
package com.asiainfo.veris.crm.order.web.person.sundryquery.terminalqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TerminalBookInfoQry extends PersonBasePage
{
    public void init(IRequestCycle cycle) throws Exception
    {
        IData inits = this.getData("cond", true);
        inits.put("cond_START_DATE", SysDateMgr.getOtherSecondsOfSysDate(-60 * 60 * 24 * 7));
        inits.put("cond_END_DATE", SysDateMgr.getSysDate());

        this.setCond(inits);
    }

    public void qryTerminalBookInfo(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);
        IDataOutput result = CSViewCall.callPage(this, "SS.TerminalBookQuerySVC.queryTerminalBooks", param, this.getPagination("pagin"));

        setPaginCount(result.getDataCount());
        this.setAjax(result.getData());
        this.setInfos(result.getData());
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setPaginCount(long count);
}
