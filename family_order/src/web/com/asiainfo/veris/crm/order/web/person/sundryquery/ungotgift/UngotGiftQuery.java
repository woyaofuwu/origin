
package com.asiainfo.veris.crm.order.web.person.sundryquery.ungotgift;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UngotGiftQuery extends PersonBasePage
{
    public void init(IRequestCycle cycle) throws Exception
    {
        IData result = CSViewCall.callone(this, "SS.UngotGiftQuerySVC.queryUngotGiftCondition", new DataMap());
        if (IDataUtil.isNotEmpty(result))
        {
            IDataset areas = result.getDataset("AREAS");
            IDataset actives = result.getDataset("ACTIVES");
            IDataset packages = result.getDataset("PACKAGES");
            String startDate = result.getString("START_DATE");
            String endDate = result.getString("END_DATE");
            IData cond = new DataMap();
            cond.put("cond_START_DATE", startDate);
            cond.put("cond_END_DATE", endDate);
            this.setCond(cond);
            this.setAreas(areas);
            this.setActives(actives);
            this.setPackages(packages);
        }
    }

    public void queryUngotGiftList(IRequestCycle cycle) throws Exception
    {
        IDataset result = CSViewCall.call(this, "SS.UngotGiftQuerySVC.queryUngotGiftList", this.getData("cond", true), this.getPagination());
        this.setAjax(result);
        this.setInfos(result);
    }

    public abstract void setActives(IDataset actives);

    public abstract void setAreas(IDataset areas);

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setPackages(IDataset packages);

    public abstract void setPaginCount(long count);
}
