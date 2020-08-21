
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatDealQuery extends PersonBasePage
{

    public void initQuery(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        cond.put("cond_BATCH_ID", getData().getString("cond_BATCH_ID"));
        cond.put("cond_SERIAL_NUMBER", getData().getString("cond_SERIAL_NUMBER"));
        cond.put("cond_END_DATE", SysDateMgr.getSysDate());
        cond.put("cond_START_DATE", SysDateMgr.addDays(cond.getString("cond_END_DATE"), -7));
        setCondition(cond);
    }

    public void queryBatDealBySN(IRequestCycle cycle) throws Exception
    {
        IData cond = getData("cond");
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatDealBySN", cond, getPagination("taskNav"));
        IDataset result = output.getData();

        setInfos(result);
        if (result == null || result.size() == 0)
        {
            cond.put("cond_HAS_DATA", "false");
        }
        else
        {
            cond.put("cond_HAS_DATA", "true");
        }
        setBatchTaskListCount(output.getDataCount());
        setInfos(output.getData());
        setCondition(cond);
    }

    public abstract void setBatchTaskListCount(long batchTaskListCount);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
