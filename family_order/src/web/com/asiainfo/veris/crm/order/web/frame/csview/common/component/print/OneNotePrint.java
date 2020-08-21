
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.print;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OneNotePrint extends PersonBasePage
{

    /**
     * 根据拼串生成打印的数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void getOneNotePrintData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData info = new DataMap();
        IDataset prints = CSViewCall.call(this, "CS.PrintNoteSVC.getOneNotePrintData", data);
        if (null != prints && !prints.isEmpty())
        {
            // 取出打印数据存放到PRINT_DATA，交给前台直接打印
            info.put("PRINT_DATA", prints.getData(0).getData("PRINT_DATA"));
        }
        this.setAjax(info);
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData info = new DataMap();
        info.putAll(data);
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        if ("".equals(serialNumber))
        {
            data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
            IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.getSnByOrder", data);
            if (null != infos && infos.size() > 0)
            {
                info = infos.getData(0);
            }
        }
        setCond(info);
    }

    /**
     * 查询一单清业务
     * 
     * @param cycle
     * @throws java.lang.Exception
     */
    public void queryPrintTrades(IRequestCycle cycle) throws java.lang.Exception
    {
        IData data = getData();
        IData info = new DataMap();
        IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.queryPrintTrades", data);
        if (null != infos && infos.size() > 0)
        {
            info = infos.getData(0);
        }

        setCond(info.getData("COND_INFO"));
        setInfos(info.getDataset("TRADE_INFOS"));
    }

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    /**
     * 更新打印标记
     * 
     * @param cycle
     * @throws java.lang.Exception
     */
    public void updataPrintTag(IRequestCycle cycle) throws java.lang.Exception
    {
        IData data = getData();
        IData info = new DataMap();
        IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.updataPrintTag", data);
        if (null != infos && infos.size() > 0)
        {
            info = infos.getData(0);
        }
        this.setAjax(info);
    }

}
