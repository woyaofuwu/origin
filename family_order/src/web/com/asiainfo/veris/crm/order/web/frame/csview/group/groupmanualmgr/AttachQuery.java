
package com.asiainfo.veris.crm.order.web.frame.csview.group.groupmanualmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.ftpmgr.FtpFileAction;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class AttachQuery extends CSBasePage
{

    /**
     * ajax传参方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void ajaxSetPospecNumber(IRequestCycle cycle) throws Exception
    {

    }

    public void deleteFile(IRequestCycle cycle) throws Throwable
    {
        FtpFileAction ftpFileAction = new FtpFileAction();
        ftpFileAction.setVisit(getVisit());
        IData data = getData();
        String param = data.getString("param");
        param = param.substring(0, param.lastIndexOf(","));
        ftpFileAction.deletes(param);
        queryFileInfo(cycle);
    }

    public void initial(IRequestCycle cycle) throws Exception
    {

    }

    public void queryFileInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        Pagination page = getPagination("infonav");
        IDataOutput ido = CSViewCall.callPage(this, "SS.AttachQuerySVC.queryFileInfobyName", data, page);
        IDataset set = ido.getData();
        String message = new String();
        if (IDataUtil.isEmpty(set))
        {
            message = "没有符合条件的查询结果！";
        }
        else
        {
            message = "查询成功！";
        }
        setMessage(message);
        setInfos(set);
        setCondition(getData());
        setInfoCount(ido.getDataCount());
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset dataset);

    public abstract void setMessage(String message);
}
