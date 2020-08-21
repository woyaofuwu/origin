
package com.asiainfo.veris.crm.order.web.person.badnessinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class BadnessInfoStatisticalForm extends PersonQueryPage
{

    // 导出数据:
    public void exportDatas(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        String fileName = "不良信息查询报表.xls";
        IDataset cond = CSViewCall.call(this, "SS.BadnessInfoSVC.getMonths", data);
        setCondition(cond.getData(0));
        IDataset output = CSViewCall.call(this, "SS.BadnessInfoSVC.queryBadnessInfosForm", data);
        String xmlString = "";
        if ("F1".equals(tradeTypeCode))// 2.3.1举报信息量统计报表
        {
            xmlString = "export/badnessinfo/BadnessInfosForm.xml";

        }
        else if ("F2".equals(tradeTypeCode))// 2.3.2被举报信息量统计报表
        {
            xmlString = "export/badnessinfo/BadnessInfosForm.xml";
        }
        else if ("F3".equals(tradeTypeCode))// 2.3.3被举报处理率统计报表
        {
            xmlString = "export/badnessinfo/BadnessInfosForm3.xml";

        }
        else if ("F4".equals(tradeTypeCode))// 2.3.4被举报处理及时率统计报表
        {
            xmlString = "export/badnessinfo/BadnessInfosForm4.xml";

        }
        else if ("F5".equals(tradeTypeCode))// 2.3.5被举报信息处理情况统计报表
        {
            xmlString = "export/badnessinfo/BadnessInfosForm5.xml";

        }
        else if ("F6".equals(tradeTypeCode))// 2.3.6被举报信息已处理量按内容分类
        {
            xmlString = "export/badnessinfo/BadnessInfosForm6.xml";

        }
        else if ("F7".equals(tradeTypeCode))// 2.3.7被举报信息省外和省内举报量
        {
            xmlString = "export/badnessinfo/BadnessInfosForm7.xml";

        }
        else if ("F8".equals(tradeTypeCode))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            xmlString = "export/badnessinfo/BadnessInfosForm8.xml";

        }
        else if ("F9".equals(tradeTypeCode))// 2.3.9点对点被举报信息已处理量按被举报信息品牌分类
        {
            xmlString = "export/badnessinfo/BadnessInfosForm9.xml";

        }
        else if ("F10".equals(tradeTypeCode))// 2.3.10 点对点被举报信息已处理量按入网时间分类
        {
            xmlString = "export/badnessinfo/BadnessInfosForm10.xml";
        }
        else if ("F11".equals(tradeTypeCode))// 2.3.11 点对点被举报信息已处理量中按欠费金额分类
        {
            xmlString = "export/badnessinfo/BadnessInfosForm11.xml";

        }
        else if ("F12".equals(tradeTypeCode))// 2.3.12 SP被举报信息量按SP类型分类
        {
            xmlString = "export/badnessinfo/BadnessInfosForm12.xml";
        }
        else if ("F13".equals(tradeTypeCode))// 2.3.13 行业应用被举报信息量按行业应用类型分类
        {
            xmlString = "export/badnessinfo/BadnessInfosForm12.xml";

        }
        else if ("F14".equals(tradeTypeCode))// 2.3.14 自有业务被举报信息量按自有业务类型分类
        {
            xmlString = "export/badnessinfo/BadnessInfosForm12.xml";

        }
        else if ("F15".equals(tradeTypeCode))// 2.3.15 其他运营商被举报号码已处理量按归属运营商分类
        {
            xmlString = "export/badnessinfo/BadnessInfosForm15.xml";

        }
        else if ("F16".equals(tradeTypeCode))// 2.3.16 被举报信息已处理量分省按处理意见统计报表
        {
            xmlString = "export/badnessinfo/BadnessInfosForm16.xml";
        }
        else if ("F17".equals(tradeTypeCode))// 2.3.17 本省举报按被举报归属省统计报表
        {
            xmlString = "export/badnessinfo/BadnessInfosForm17.xml";
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

    private String getTitle(String tradeTypeCode)
    {
        if ("F12".equals(tradeTypeCode))
        {
            return "SP被举报信息量按SP类型分类";
        }
        else if ("F14".equals(tradeTypeCode))
        {
            return "自有业务按类型分类";
        }
        else if ("F3".equals(tradeTypeCode))
        {
            return "被举报处理率统计";
        }
        else if ("F4".equals(tradeTypeCode))
        {
            return "被举报处理及时率统计";
        }
        else if ("F16".equals(tradeTypeCode))
        {
            return "被举报信息按处理意见统计";
        }
        else if ("F5".equals(tradeTypeCode))
        {
            return "被举报信息处理情况统计";
        }
        else if ("F2".equals(tradeTypeCode))
        {
            return "被举报信息量统计";
        }
        else if ("F7".equals(tradeTypeCode))
        {
            return "被举报信息省外和省内举报量";
        }
        else if ("F6".equals(tradeTypeCode))
        {
            return "被举报信息已处理量按内容分类";
        }
        else if ("F9".equals(tradeTypeCode))
        {
            return "点对点被举报信息按品牌分类";
        }
        else if ("F11".equals(tradeTypeCode))
        {
            return "点对点被举报信息按欠费金额分类";
        }
        else if ("F10".equals(tradeTypeCode))
        {
            return "点对点被举报信息按入网时间分类";
        }
        else if ("F8".equals(tradeTypeCode))
        {
            return "点对点被举报信息是否实名制";
        }
        else if ("F1".equals(tradeTypeCode))
        {
            return "举报信息量统计报表";
        }
        else if ("F15".equals(tradeTypeCode))
        {
            return "其他运营商按归属运营商分类";
        }
        else if ("F13".equals(tradeTypeCode))
        {
            return "行业应用类型分类";
        }
        return "类型分类";
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

        String month = SysDateMgr.getCurMonth();
        if (Integer.parseInt(month) - 1 <= 0)
        {
            cond.put("MONTH1", 12);
        }
        else
        {
            cond.put("MONTH1", Integer.parseInt(month) - 1);
        }

        cond.put("MONTH2", month);

        if (Integer.parseInt(month) + 1 >= 12)
        {
            cond.put("MONTH3", 12);
        }
        else
        {
            cond.put("MONTH3", Integer.parseInt(month) + 1);
        }

        cond.put("TITLE", getTitle(data.getString("TRADE_TYPE_CODE")));
        setCondition(cond);

        IDataset result = CSViewCall.call(this, "SS.BadnessInfoSVC.getReportCode", data);
        setReports(result);
    }

    public void queryBadnessInfosForm(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset cond = CSViewCall.call(this, "SS.BadnessInfoSVC.getMonths", data);
        setCondition(cond.getData(0));

        IDataOutput output = CSViewCall.callPage(this, "SS.BadnessInfoSVC.queryBadnessInfosForm", data, getPagination("badInfoNav"));
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
}
