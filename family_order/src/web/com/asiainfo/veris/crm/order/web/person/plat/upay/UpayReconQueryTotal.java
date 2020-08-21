
package com.asiainfo.veris.crm.order.web.person.plat.upay;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UpayReconQueryTotal extends PersonBasePage
{

	/**
     * 和包电子券对账查询(总体结果)
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public void queryUpayReconTotal(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);
        IDataOutput result = CSViewCall.callPage(this, "SS.UpayReconQuerySVC.queryUpayReconTotal", param, this.getPagination("PaginBar"));

        this.setInfos(result.getData());
        this.setPaginCount(result.getDataCount());
        this.setCond(this.getData("cond", true));
    }
    
    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setPaginCount(long paginCount);
}
