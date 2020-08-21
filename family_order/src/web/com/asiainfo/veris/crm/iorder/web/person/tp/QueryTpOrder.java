package com.asiainfo.veris.crm.iorder.web.person.tp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class QueryTpOrder extends PersonBasePage {

    public abstract void setCond(IData cond);
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    public abstract void setReportPage(IData reportPage);
    public abstract void setResult(IData result);
    public abstract void setCount(long count);
    public abstract void setRowIndex(int rowIndex);

    public void init(IRequestCycle cycle) throws Exception {

        IData param = getData();
        setCond(param);
    }

    public void queryTpOrderInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.TpOrderSVC.queryTpOrderInfos", data, getPagination("NavBar"));
        setCount(output.getDataCount());
        setInfos(output.getData());
    }

    public void queryTpOrderDetail(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.TpOrderDetailSVC.queryTpOrderDetail", data, getPagination("NavBar1"));
        setCount(output.getDataCount());
        setInfos(output.getData());
    }

    public void submitAction(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String tabInfos = data.getString("TABLE_INFOS");

        if(StringUtils.isNotEmpty(tabInfos)){
            IDataset tinfos = new DatasetList(tabInfos);
            data.put("TABLE_INFOS",tinfos);
        }
        IData result = CSViewCall.callone(this,"SS.TpOrderSVC.submitAction",data);
        setAjax(result);
//        IDataOutput output = CSViewCall.callPage(this, "SS.TpOrderSVC.auditTpOrder", data, null);
//        setAjax(output.getData());
    }

}
