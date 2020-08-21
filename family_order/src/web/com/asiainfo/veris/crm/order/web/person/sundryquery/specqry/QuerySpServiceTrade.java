
package com.asiainfo.veris.crm.order.web.person.sundryquery.specqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QuerySpServiceTrade extends PersonBasePage
{

    /**
     * 初始化界面提示信息
     * 
     * @param cycle
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        inputData.put("cond_BIZ_STATUS", "A");
        this.setCondition(inputData);
    }

    /**
     * 执行新业务产品资料查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySpServiceTrade(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        Pagination page = getPagination("pageNav");
        inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.SundryQrySVC.querySpServiceTrade", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }

        // 设置页面返回数据
        setInfos(dataset);
        setPageCount(result.getDataCount());
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);
}
