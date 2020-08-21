
package com.asiainfo.veris.crm.order.web.group.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.ftpmgr.FtpFileAction;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QuerySaleActiveFile extends GroupBasePage
{

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setLogCount(long logCount);

    /**
     * 查询营销活动文件列表信息
     * @param cycle
     * @throws Exception
     */
    public void querySaleFileInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("TRADE_TAG", "1");
        
        IDataOutput output = CSViewCall.callPage(this, "SS.SaleActiveFileSVC.querySaleFileInfos", data, getPagination("LogNav"));
        
        setInfos(output.getData());
        setLogCount(output.getDataCount());
        setCondition(data);
    }
    
    /**
     * 
     * @param cycle
     * @throws Throwable
     */
    public void deleteFile(IRequestCycle cycle) throws Throwable
    {
        FtpFileAction ftpFileAction = new FtpFileAction();
        ftpFileAction.setVisit(getVisit());
        IData data = getData();
        String param = data.getString("param");
        param = param.substring(0, param.lastIndexOf(","));
        ftpFileAction.deletes(param);
        
        IData inparams = new DataMap();
        inparams.put("FILE_ID", param);        
        CSViewCall.call(this, "SS.SaleActiveFileSVC.delSaleFileByFileId", inparams);
        
        querySaleFileInfos(cycle);
    }
}
