
package com.asiainfo.veris.crm.order.web.person.sundryquery.querysalestopdetail;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 营销活动提前终止明细查询 wuxd3
 */
public abstract class QuerySaleStopDetail extends PersonBasePage
{

    public void init(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        IDataset result = CSViewCall.call(this, "SS.QuerySaleStopDetailSVC.init", inputData);
        if (IDataUtil.isNotEmpty(result))
        {
            this.setAreas(result);
        }
    }

    public void queryDetailInfo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond",true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataOutput ibplatDs = CSViewCall.callPage(this, "SS.QuerySaleStopDetailSVC.queryDetailInfo", inparam, getPagination("page"));
        IDataset results = ibplatDs.getData();
        if (IDataUtil.isEmpty(results))
        {

        }
        this.setListInfos(results);
        this.setListCount(ibplatDs.getDataCount());
    }
    
    public void exportExcel(IRequestCycle cycle) throws Exception
    {
        String fileName = "QuerySaleStopDetail.xls";
        String xmlPath = "export/sundryquery/querysalestopdetail/QuerySaleStopDetail.xml";
        IData inparam = getData("cond",true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset output = CSViewCall.call(this, "SS.QuerySaleStopDetailSVC.queryDetailInfo", inparam);

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

    public abstract void setAreas(IDataset areas);

    public abstract void setCondition(IData result);

    public abstract void setListCount(long count);

    public abstract void setListInfo(IData result);

    public abstract void setListInfos(IDataset results);

}
