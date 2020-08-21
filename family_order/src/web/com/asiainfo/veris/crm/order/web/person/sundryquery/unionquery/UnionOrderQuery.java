
package com.asiainfo.veris.crm.order.web.person.sundryquery.unionquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UnionOrderQuery extends PersonBasePage
{

    /**
     * 组装详细信息
     * 
     * @param info
     */
    private void assembleDealInfo(IData info)
    {
        String tag = info.getString("RSRV_TAG1");
        if (tag != null && "0".equals(tag))
        {
            StringBuilder dealInfo = new StringBuilder();
            dealInfo.append("处理人ID：");
            dealInfo.append(info.getString("RSRV_STR2"));
            dealInfo.append("<BR/>");

            dealInfo.append("处理人名称：");
            dealInfo.append(info.getString("RSRV_STR3"));
            dealInfo.append("<BR/>");

            dealInfo.append("处理时间：");
            dealInfo.append(info.getString("DEAL_TIME"));
            dealInfo.append("<BR/>");

            dealInfo.append("处理意见：");
            dealInfo.append(info.getString("RSRV_STR4"));
            dealInfo.append("<BR/>");

            info.put("DEALINFO", dealInfo);
        }
    }

    /**
     * 格式化返回信息
     * 
     * @param infos
     * @return
     * @throws Exception
     */
    private IDataset formateInfo(IDataset infos) throws Exception
    {
        for (int i = 0; i < infos.size(); i++)
        {
            IData info = infos.getData(i);
            replaceRSPContent(info);
            assembleDealInfo(info);
        }
        return infos;
    }

    /**
     * 初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData params = getData("cond", true);

        String sysDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        IData data = new DataMap();
        data.put("cond_ORDER_BEGIN_TIME", sysDate);
        data.put("cond_ORDER_END_TIME", sysDate);
        setCondition(data);

        params.put("ORDER_BEGIN_TIME", sysDate + SysDateMgr.START_DATE_FOREVER);
        params.put("ORDER_END_TIME", sysDate + SysDateMgr.END_DATE);
        IDataOutput infos = CSViewCall.callPage(this, "SS.UnionQuerySVC.queryUnionOrderInfos", params, getPagination("navt"));

        setInfos(formateInfo(infos.getData()));
    }

    /**
     * 统一订购查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUnionOrderInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        IData params = getData("cond", true);

        IDataOutput infos = CSViewCall.callPage(this, "SS.UnionQuerySVC.queryUnionOrderInfos", params, getPagination("navt"));

        if (null != infos)
            setCount(infos.getDataCount());

        setInfos(formateInfo(infos.getData()));
    }

    /**
     * 替换系统下发信息中的换行
     * 
     * @param info
     */
    private void replaceRSPContent(IData info)
    {
        String rspContent = (String) info.get("RSP_CONTENT");
        rspContent = StringUtils.replace(rspContent, "\n", "<BR/>");
        info.put("RSP_CONTENT", rspContent);
    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset viceInfos);

    /**
     * 展示订购详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void showOrderDetailInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.UnionQuerySVC.showOrderDetailInfo", data);

        setInfos(result);
    }

    /**
     * 保存处理意见信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void updateUnionOrderInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        CSViewCall.call(this, "SS.UnionQuerySVC.updateUnionOrderInfos", data);
    }

}
