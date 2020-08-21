
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class AppTrackLogQry extends GroupBasePage
{
    /**
     * 删除跟踪日志
     * 
     * @param cycle
     * @throws Exception
     */
    public void delAppTrackLogs(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);

        CSViewCall.call(this, "SS.AppTrackLogQrySVC.delAppTrackLogs", param);

        setHintInfo("删除成功~~！");

        setInfos(new DatasetList());
    }

    public abstract IData getCondition();

    public abstract IDataset getInfos();

    /**
     * 初始化页面方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");

        IDataset logDayList = StaticUtil.getStaticList("LOG_DAY");

        DataHelper.sort(logDayList, "DATA_ID", IDataset.TYPE_INTEGER);

        IData condition = new DataMap();

        condition.put("LOG_DAYS", logDayList);

        condition.put("LOG_DAY", SysDateMgr.getCurDay());

        setCondition(condition);
    }

    /**
     * 应用跟踪日志查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryAppTrackLogs(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);

        IDataset dataset = CSViewCall.call(this, "SS.AppTrackLogQrySVC.qryAppTrackLogs", param);

        if (dataset.size() == 0)
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            setHintInfo("查询成功~~！");
        }

        setInfos(dataset);

    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

}
