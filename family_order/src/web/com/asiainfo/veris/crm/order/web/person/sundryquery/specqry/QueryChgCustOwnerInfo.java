
package com.asiainfo.veris.crm.order.web.person.sundryquery.specqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryChgCustOwnerInfo extends PersonBasePage
{

    /**
     * 初始化界面提示信息
     * 
     * @param cycle
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        // setHintInfo("要求【起始、终止】日期时间段不能超过31天~");
        IData inputData = this.getData();
        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.SundryQrySVC.initCityArea", inputData);
        this.setAreas(result);
        inputData.put("cond_START_DATE", SysDateMgr.getFirstDayOfThisMonth4WEB());
        inputData.put("cond_END_DATE", SysDateMgr.getSysDate());
        this.setCondition(inputData);
    }

    /**
     * 执行欠费拆机用户押金清单查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryChgCustOwnerInfo(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        Pagination page = getPagination("pageNav");
        inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        String startDate = inputData.getString("START_DATE", "");
        if (StringUtils.isNotBlank(startDate))
        {
            inputData.put("ACCEPT_MONTH", startDate.substring(5, 7));
        }
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.SundryQrySVC.queryChgCustOwnerInfo", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        setPageCount(result.getDataCount());
        // 设置页面返回数据
        setInfos(dataset);
        setCondition(this.getData("cond"));
    }

    public abstract void setAreas(IDataset areas);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

}
