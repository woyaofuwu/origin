
package com.asiainfo.veris.crm.order.web.person.changesvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：用户预约产品查询 作者：GongGuang
 */
public abstract class QueryChangeSvcReconds extends PersonBasePage
{

    public void queryIntegrateCustInvoice(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataOutput reconds = CSViewCall.callPage(this, "SS.QueryChangeSvcRecondsSVC.getChangeRecondsInfo", inparam, getPagination("page"));
        IDataset results = reconds.getData();
        this.setListInfos(results);
        this.setListCount(reconds.getDataCount());
    }

    public abstract void setCondition(IData result);

    public abstract void setRowIndex(int infos);

    public abstract void setListCount(long count);

    public abstract void setListInfos(IDataset results);
    
    public abstract void setTipInfo(String tipInfo);
    
    public abstract void setListInfo(IData info);
}
