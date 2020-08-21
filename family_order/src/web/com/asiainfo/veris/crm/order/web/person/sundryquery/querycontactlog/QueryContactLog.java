
package com.asiainfo.veris.crm.order.web.person.sundryquery.querycontactlog;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 未完工工单查询
 */
public abstract class QueryContactLog extends PersonBasePage
{
    public void exportExcel(IRequestCycle cycle) throws Exception
    {
        String fileName = "接触信息维护日志查询.xls";
        String xmlPath = "export/other/QueryUnfinishTrade.xml";
        IData data = getData();
        IData temp = getData("cond", true);
        data.putAll(temp);
        IDataset output = CSViewCall.call(this, "SS.QueryContactLogSVC.queryContactLogs", data);

        IData params = new DataMap();
        params.put("posX", "0");
        params.put("posY", "0");
        params.put("ftpSite", "personserv");

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        // 将数据写入文件并返回文件ID
        String fileId = ImpExpUtil.beginExport(null, params, fileName, new IDataset[]
        { output }, ExcelConfig.getSheets(xmlPath));
        // 获取文件下载的URL
        String url = ImpExpUtil.getDownloadPath(fileId, fileName);
        setAjax("url", url);
        // return url;
        // return xmlPath;

    }

    public void queryContactLogs(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData temp = getData("cond", true);
        data.putAll(temp);

        IDataOutput output = CSViewCall.callPage(this, "SS.QueryContactLogSVC.queryContactLogs", data, getPagination("LogNav"));

        setInfos(output.getData());
        setLogCount(output.getDataCount());
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setLogCount(long logCount);

}
