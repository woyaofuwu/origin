
package com.asiainfo.veris.crm.iorder.web.person.Welfare;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * @Description 权益弹窗
 * @Auther: zhenggang
 * @Date: 2020/7/9 17:13
 * @version: V1.0
 */
public abstract class WelfareTrade extends PersonBasePage
{
    public abstract void setInfo(IData info);

    public void onInit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.WelfareOfferSVC.getPopPageParam", data);
        setInfo(result);
    }

    public void getPrintData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData printData = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        IDataset printDataSet = CSViewCall.call(this, "SS.WelfareOfferSVC.getPrintData", data);
        if (printDataSet == null || printDataSet.isEmpty())
        {
            printDataSet = new DatasetList();
        }
        printData.put("PRINT_DATA", printDataSet);
        setAjax(printData);
    }
}
