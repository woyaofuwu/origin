
package com.asiainfo.veris.crm.order.web.person.sundryquery.ipexpresstradelog;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：IP直通车工作记录查询 作者：GongGuang
 */
public abstract class IpExpressTradeLog extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 功能：IP直通车工作记录查询
     */
    public void queryIPExpressLog(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.IpExpressTradeLogSVC.queryIPExpressLog", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "获取IP直通车工作记录无数据";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCond(getData("cond", true));

    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
