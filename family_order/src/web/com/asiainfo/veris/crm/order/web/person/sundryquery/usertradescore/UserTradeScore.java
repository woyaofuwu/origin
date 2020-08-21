
package com.asiainfo.veris.crm.order.web.person.sundryquery.usertradescore;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class UserTradeScore extends PersonQueryPage
{

    /**
     * 初始化页面
     * 
     * @param cycle
     * @throws Exception
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData result = CSViewCall.callone(this, "SS.QueryUserTradeScoreSVC.queryInitCondition", new DataMap());
        if (IDataUtil.isNotEmpty(result))
        {
            IData data = new DataMap();
            data.put("cond_ACCEPT_START", result.getString("START_DATE")); // 得到本月的第一天
            data.put("cond_ACCEPT_END", result.getString("END_DATE"));// 格式为YYYY-MM-DD
            setCondition(data);
        }
    }

    /**
     * 初始化页面
     * 
     * @param cycle
     * @throws Exception
     */
    public void initServicePage(IRequestCycle cycle) throws Exception
    {
        IData param = getData();

        IData result = CSViewCall.callone(this, "SS.QueryUserTradeScoreSVC.queryInitCondition", new DataMap());
        if (IDataUtil.isNotEmpty(result))
        {
            IData data = new DataMap();
            data.put("cond_ACCEPT_START", result.getString("START_DATE")); // 得到本月的第一天
            data.put("cond_ACCEPT_END", result.getString("END_DATE"));// 格式为YYYY-MM-DD
            // BOSS业务 接口接入数据处理
            data.put("cond_SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
            data.put("cond_IN_MODE_CODE", param.getString("IN_MODE_CODE", ""));
            setCondition(data);
        }
    }

    /**
     * 查询积分兑换明细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUserTradeScore(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IPage page = cycle.getPage();
        data.put("OBJECT_ID", page.getPageName());
        String alertInfo = "";
        if (StringUtils.isNotBlank(data.getString("SERIAL_NUMBER")))
        {
            IDataOutput infos = CSViewCall.callPage(this, "SS.QueryUserTradeScoreSVC.queryUserTradeScore", data, getPagination("olcomnav"));

            if (IDataUtil.isEmpty(infos.getData()))
            {
                alertInfo = "获取积分兑换明细无数据!";
            }
            setInfos(infos.getData());
            setInfosCount(infos.getDataCount());
            // IData param = new DataMap();
            setCondition(getData("cond", true));

        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
    }

    public abstract void setCondition(IData condition);

    public abstract void setDetaiInfos(IDataset detaiInfos);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long count);

}
