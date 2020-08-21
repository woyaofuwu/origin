
package com.asiainfo.veris.crm.order.web.person.sundryquery.plat.mobilepayment;

import java.sql.Timestamp;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 手机支付小额支付对账结果查询
 * 
 * @author xiekl
 */
public abstract class SmallPaymentCheckUpResultQuery extends PersonBasePage
{

    /**
     * 按钮导出
     * 
     * @param cycle
     * @throws Exception
     */
    public void exportDatas(IRequestCycle cycle) throws Exception
    {
        IData cond = this.getData("cond");
        cond.put("X_RESULTCODE", "");
        this.setCondition(cond);
        IData param = this.getData("cond", true);

        Timestamp startTime = SysDateMgr.encodeTimestamp(cond.getString("START_TIME"));
        Timestamp endTime = SysDateMgr.encodeTimestamp(cond.getString("END_TIME"));
        if ((endTime.getTime() - startTime.getTime()) > (365L * 24 * 3600 * 1000))// 主要转为LONG型比较
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "账务结束时间和账务开始时间间隔不能大于一年");
        }

        IDataset dataset = CSViewCall.call(this, "SS.AccountInfoQuerySVC.querySmallPaymentCheckUpResult", param);

        // 这种做法很变态，sql发布中文乱码，先这么解决吧
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            data.put("RECON_STATE", this.pageutil.getStaticValue("RECON_STATE", data.getString("RECON_STATE")));
            data.put("CANCEL_FLAG", this.pageutil.getStaticValue("CANCEL_FLAG", data.getString("CANCEL_FLAG")));
        }

        String fileName = "SmallPaymentReport.xls";
        String xmlPath = "export/sundryquery/SmallPaymentReport.xml";
        IData params = new DataMap();
        params.put("posX", "0");
        params.put("posY", "0");
        params.put("ftpSite", "personserv");

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        // 将数据写入文件并返回文件ID
        String fileId = ImpExpUtil.beginExport(null, params, fileName, new IDataset[]
        { dataset }, ExcelConfig.getSheets(xmlPath));
        // 获取文件下载的URL
        String url = ImpExpUtil.getDownloadPath(fileId, fileName);
        setAjax("URL", this.encodeUrl(url));
    }

    public void querySmallPaymentCheckUpResult(IRequestCycle cycle) throws Exception
    {
        IData cond = this.getData("cond");

        Timestamp startTime = SysDateMgr.encodeTimestamp(cond.getString("START_TIME"));
        Timestamp endTime = SysDateMgr.encodeTimestamp(cond.getString("END_TIME"));

        if ((endTime.getTime() - startTime.getTime()) > (365L * 24 * 3600 * 1000))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "账务结束时间和账务开始时间间隔不能大于一年");
        }

        cond.put("X_RESULTCODE", "");
        this.setCondition(cond);
        IData param = this.getData("cond", true);

        IDataOutput dataset = CSViewCall.callPage(this, "SS.AccountInfoQuerySVC.querySmallPaymentCheckUpResult", param, this.getPagination("queryNav"));
        this.setInfos(dataset.getData());
        this.setPageCount(dataset.getDataCount());
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long pageCount);
}
