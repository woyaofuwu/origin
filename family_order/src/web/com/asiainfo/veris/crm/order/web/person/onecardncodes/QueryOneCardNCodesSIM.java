
package com.asiainfo.veris.crm.order.web.person.onecardncodes;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryOneCardNCodesSIM extends PersonBasePage
{

    public void querySimcardInfos(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData inparam = new DataMap();
        inparam.put("NUMBER", data.getString("NUMBER"));
        inparam.put("TYPE", data.getString("ONECARDCODE_QYERY_TYPE"));
        inparam.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset output = CSViewCall.call(this, "SS.QueryOneCardNCodesSIMSVC.querySimCardInfos", inparam);

        // setCount(output.getDataCount());
        // pageutil.getStaticValue(type_id, data_id)
        setAjax(output);
        setInfos(output);
        setInfo(output.getData(0));
        setEditInfo(inparam);

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData data);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset datas);
}
