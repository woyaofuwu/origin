
package com.asiainfo.veris.crm.order.web.person.badnessinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class BadnessInfoQuery extends PersonQueryPage
{

    public void getServRequestType(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String reportTypeCode = data.getString("REPORT_TYPE_CODE");
        if (StringUtils.isNotBlank(reportTypeCode))
        {
            IDataset services = StaticUtil.getStaticListByParent("BAD_INFO_SERV_REQUEST_TYPE", reportTypeCode);
            setServices(services);
        }
    }

    private String getTitle(String tradeTypeCode)
    {
        if ("Q1".equals(tradeTypeCode))
        {
            return "点对点举报信息查询";
        }
        else if ("Q2".equals(tradeTypeCode))
        {
            return "点对点被举报信息查询";
        }
        else if ("Q3".equals(tradeTypeCode))
        {
            return "SP举报信息查询";
        }
        else if ("Q4".equals(tradeTypeCode))
        {
            return "SP被举报信息查询";
        }
        else if ("Q5".equals(tradeTypeCode))
        {
            return "行业应用举报信息查询";
        }
        else if ("Q6".equals(tradeTypeCode))
        {
            return "行业应用被举报信息查询";
        }
        else if ("Q7".equals(tradeTypeCode))
        {
            return "自有业务举报信息查询";
        }
        else if ("Q8".equals(tradeTypeCode))
        {
            return "自有业务被举报信息查询";
        }
        else if ("Q9".equals(tradeTypeCode))
        {
            return "其他运营商举报信息查询";
        }
        else if ("Q10".equals(tradeTypeCode))
        {
            return "WAP涉黄被举报信息查询";
        }
        return "信息查询";
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        IData data = getData();

        String sysdate = SysDateMgr.getSysDate();
        cond.put("REPORT_START_TIME", sysdate);
        cond.put("REPORT_END_TIME", sysdate);
        cond.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        setEditInfo(cond);

        cond.put("TITLE", getTitle(data.getString("TRADE_TYPE_CODE")));
        setCondition(cond);

        IDataset result = CSViewCall.call(this, "SS.BadnessInfoSVC.getReportCode", data);
        setReports(result);
    }

    public void queryBadnessInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.BadnessInfoSVC.queryBadnessInfos", data, getPagination("badInfoNav"));
        if (IDataUtil.isEmpty(output.getData()))
        {
            setAjax("ALERT_CODE", "0");
        }
        setCount(output.getDataCount());
        setBadInfos(output.getData());
    }

    public abstract void setBadInfo(IData info);

    public abstract void setBadInfos(IDataset infos);

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setReports(IDataset reports);

    public abstract void setServices(IDataset services);
}
