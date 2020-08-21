
package com.asiainfo.veris.crm.order.web.person.badnessinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class BadnessInfoStatistic extends PersonQueryPage
{
    // 导出数据:
    public void exportDatas(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        String fileName = "不良信息查询报表.xls";
        IDataset cond = CSViewCall.call(this, "SS.BadnessInfoSVC.getMonths", data);
        setCondition(cond.getData(0));
        IDataset output = CSViewCall.call(this, "SS.BadnessInfoSVC.staticsBadnessInfos", data);
        String xmlString = "";
        if ("S1".equals(tradeTypeCode))// 点对点被举报号码统计
        {
            xmlString = "export/badness/StaticsBadnessInfos.xml";

        }
        else if ("S3".equals(tradeTypeCode))// 行业应用被举报号码统计
        {
            xmlString = "export/badness/StaticsBadnessInfos3.xml";
        }
        else if ("S5".equals(tradeTypeCode))// 其他运营被举报号码统计
        {
            xmlString = "export/badness/StaticsBadnessInfos5.xml";

        }
        else if ("S6".equals(tradeTypeCode) || "S8".equals(tradeTypeCode) || "S10".equals(tradeTypeCode))// S6
        // 点对点被举报情况汇总统计;S8
        // 行业应用被举报情况汇总统计;
        // S10
        // 其他运营被举报情况汇总统计
        {
            xmlString = "export/badness/StaticsBadnessInfos6.xml";

        }
        else
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "无法识别的业务类型编码：" + tradeTypeCode);
        }

        IData params = new DataMap();
        params.put("posX", "0");
        params.put("posY", "0");
        params.put("ftpSite", "personserv");

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        // 将数据写入文件并返回文件ID
        String fileId = ImpExpUtil.beginExport(null, params, fileName, new IDataset[]
        { output }, ExcelConfig.getSheets(xmlString));
        // 获取文件下载的URL
        String url = ImpExpUtil.getDownloadPath(fileId, fileName);
        setAjax("URL", url);
    }

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
        if ("S4".equals(tradeTypeCode))
        {
            return "自有业务被举报号码统计";
        }
        else if ("S2".equals(tradeTypeCode))
        {
            return "SP被举报号码统计";
        }
        else if ("S7".equals(tradeTypeCode))
        {
            return "SP被举报情况汇总统计";
        }
        else if ("S9".equals(tradeTypeCode))
        {
            return "自有业务被举报情况汇总统计";
        }
        else if ("S1".equals(tradeTypeCode))
        {
            return "点对点被举报号码统计";
        }
        else if ("S3".equals(tradeTypeCode))
        {
            return "行业应用被举报号码统计";
        }
        else if ("S5".equals(tradeTypeCode))
        {
            return "其他运营被举报号码统计";
        }
        else if ("S6".equals(tradeTypeCode))
        {
            return "点对点被举报情况汇总统计";
        }
        else if ("S8".equals(tradeTypeCode))
        {
            return "行业应用被举报情况汇总统计";
        }
        else if ("S10".equals(tradeTypeCode))
        {
            return "其他运营被举报情况汇总统计";
        }
        return "汇总统计";
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

    /* S系列 */
    public void queryBadnessInfosNew(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.BadnessInfoSVC.staticsBadnessInfos", data, getPagination("badInfoNav"));
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

    public void staticsBadnessInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.BadnessInfoSVC.staticsBadnessInfos", data, getPagination("badInfoNav"));
        if (IDataUtil.isEmpty(output.getData()))
        {
            setAjax("ALERT_CODE", "0");
        }
        setCount(output.getDataCount());
        setBadInfos(output.getData());
    }
}
