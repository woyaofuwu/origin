
package com.asiainfo.veris.crm.order.web.person.sundryquery.monitorinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryMonitorInfo extends PersonBasePage
{

    /**
     * 监控信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryMonitorInfos(IRequestCycle cycle) throws Exception
    {
        Pagination page = getPagination("pageNav");
        IData inputData = this.getData();
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.QueryMonitorInfoSVC.queryMonitorInfos", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        setPageCount(result.getDataCount());
        // 设置页面返回数据
        setInfos(dataset);
        setCondition(inputData);
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

}
