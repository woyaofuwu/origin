
package com.asiainfo.veris.crm.order.web.person.sundryquery.specqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryChangeSnInfo extends PersonBasePage
{

    /**
     * 初始化界面提示信息
     * 
     * @param cycle
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        inputData.put("cond_START_DATE", SysDateMgr.getAddMonthsNowday(-1, SysDateMgr.getSysDate()));
        inputData.put("cond_END_DATE", SysDateMgr.getSysDate());
        this.setCondition(inputData);
    }

    /**
     * 执行改号业务查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryChangeSnInfo(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        Pagination page = getPagination("pageNav");
        inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        if (this.hasPriv("SYS012"))
        {
            inputData.put(StrUtil.getNotFuzzyKey(), true);
        }
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.SundryQrySVC.queryChangeSnInfo", inputData, page);

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
