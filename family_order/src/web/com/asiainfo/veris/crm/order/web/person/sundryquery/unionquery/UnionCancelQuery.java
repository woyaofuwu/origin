
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

public abstract class UnionCancelQuery extends PersonBasePage
{

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
        data.put("cond_CANCEL_BEGIN_TIME", sysDate);
        data.put("cond_CANCEL_END_TIME", sysDate);
        setCondition(data);

        params.put("CANCEL_BEGIN_TIME", sysDate + SysDateMgr.START_DATE_FOREVER);
        params.put("CANCEL_END_TIME", sysDate + SysDateMgr.END_DATE);
        IDataOutput infos = CSViewCall.callPage(this, "SS.UnionQuerySVC.queryUnionCancelInfos", params, getPagination("navt"));

        setInfos(infos.getData());
    }

    /**
     * 统一退订信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUnionDetailInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        IData params = getData("cond", true);

        String dealState = params.getString("DEAL_STATE");

        if (StringUtils.equals(dealState, "1"))
        {
            params.put("WAIT_DEAL", "1");
            params.put("FAIL_COUNT", "0");
        }
        else if (StringUtils.equals(dealState, "0"))
        {
            params.put("DEAL_TAG", "0");
        }
        else if (StringUtils.equals(dealState, "2"))
        {
            params.put("WAIT_DEAL", "1");
            params.put("FAIL_COUNT", "3");
        }

        IDataOutput infos = CSViewCall.callPage(this, "SS.UnionQuerySVC.queryUnionCancelInfos", params, getPagination("navt"));

        if (null != infos)
            setCount(infos.getDataCount());

        setInfos(infos.getData());
    }

    /**
     * 替换系统下发信息中的换行
     * 
     * @param info
     */
    private void replaceRSPContent(IData info)
    {
        String rspContent = (String) info.get("RSP_CONTENT");
        rspContent = StringUtils.replace(rspContent, "\n", "<br/>");
        rspContent = StringUtils.replace(rspContent, "\"", "");
        info.put("RSP_CONTENT", rspContent);
    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset viceInfos);

    /**
     * 展示退订业务详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void showCancelDetailInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.UnionQuerySVC.showCancelDetailInfo", data);

        setInfos(result);
    }

    /**
     * 展示会话详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void showSessionDetailInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.UnionQuerySVC.showSessionDetailInfo", data);

        setInfos(formateInfo(result));
    }

    /**
     * 保存处理意见信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void updateUnionCancelInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        CSViewCall.call(this, "SS.UnionQuerySVC.updateUnionCancelInfos", data);
    }
}
