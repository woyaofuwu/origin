
package com.asiainfo.veris.crm.order.web.person.sundryquery.usereasyprompt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：便捷服务意见查询 作者：GongGuang
 */
public abstract class QueryUserEasyPrompt extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 初始化
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        // 设置起止时间
        String endDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        String firstData = SysDateMgr.addDays(endDate, -6); // 得到今天再减6天
        IData data = getData("cond", true);
        data.put("cond_ACCEPT_DATE1", firstData);
        data.put("cond_ACCEPT_DATE2", endDate);
        this.setCondition(data);
    }

    /**
     * 功能：便捷服务意见查询
     */
    public void queryUserEasyPrompt(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryUserEasyPromptSVC.queryUserEasyPrompt", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【便捷服务意见】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
