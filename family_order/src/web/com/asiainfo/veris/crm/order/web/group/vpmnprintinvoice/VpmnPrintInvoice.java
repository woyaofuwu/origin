
package com.asiainfo.veris.crm.order.web.group.vpmnprintinvoice;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class VpmnPrintInvoice extends GroupBasePage
{

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setHintInfo(String infos);

    public abstract void setCondition(IData condition);

    public abstract void setInfoCount(long infoCount);

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("cond_STAFF_ID", getVisit().getStaffId());
        data.put("cond_START_DATE", SysDateMgr.getSysDate());
        data.put("cond_END_DATE", SysDateMgr.getSysDate());
        setCondition(data);
        setHintInfo("请输入号码或时间查询，要求【开始、结束】日期时间段不能超过31天");
    }

    /**
     * 打印信息查询
     * 
     * @author liujy
     */
    public void queryPrints(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String today = SysDateMgr.getSysDate();
        String serialNumber = param.getString("cond_SERIAL_NUMBER", "");
        String staffIdOne = param.getString("cond_STAFF_ID", "");

        String staffIdTwo = getVisit().getStaffId();

        if (staffIdOne == null || staffIdOne.equals(""))
        {

            staffIdOne = staffIdTwo;

        }
        String startDate = param.getString("cond_START_DATE");

        if (startDate == null || startDate.equals(""))
        {

            startDate = today;

        }
        String endDate = param.getString("cond_END_DATE");
        if (endDate == null || endDate.equals(""))
        {

            endDate = today;

        }
        IData date = new DataMap();

        date.put("START_DATE", startDate);
        date.put("END_DATE", endDate);
        date.put("STAFF_ID", staffIdOne);
        date.put("SERIAL_NUMBER", serialNumber);

        IDataOutput dataOutput = CSViewCall.callPage(this, "SS.BookTradeSVC.queryPrints", date, getPagination("pageNav"));

        if (null != dataOutput && dataOutput.getData().size() > 0)
        {
            setHintInfo("查询成功~~！");
        }
        else
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        setCondition(param);
        setInfos(dataOutput.getData());
        setInfoCount(dataOutput.getDataCount());

    }

    public void redirectToMsgBox(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String tradeId = param.getString("TRADE_ID");

        if (StringUtils.isBlank(tradeId))
        {
            CSViewException.apperr(GrpException.CRM_GRP_828);
        }
        param.put("TRADE_ID", tradeId);
        param.put("CANCEL_TAG", "0");
        IDataset resultData = CSViewCall.call(this, "SS.BookTradeSVC.redirectToMsgBox", param);
        setAjax(resultData);
    }

}
