
package com.asiainfo.veris.crm.order.web.person.batprintinvoice;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatPrintInvoice extends PersonBasePage
{
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String today = pageutil.getSysDate();
        String staffId = data.getString("STAFF_ID", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String tradeId = data.getString("TRADE_ID", "");
        if (StringUtils.equals(staffId, ""))
        {
            staffId = getVisit().getStaffId();
        }
        if (StringUtils.equals(startDate, ""))
        {
            startDate = today;
        }

        if (StringUtils.equals(endDate, ""))
        {
            endDate = today;
        }
        if (!StringUtils.equals(serialNumber, "") || !StringUtils.equals(tradeId, ""))
        {
            queryPrintInfo(cycle);
        }
        data.put("STAFF_ID", staffId);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("TRADE_ID", tradeId);
        setCond(data);
    }

    /**
     * 免填单批量打印
     * @param cycle
     * @throws Exception
     */
    public void printTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.BatPrintInvoiceSVC.printTrade", data);
        this.setAjax(results);
    }

    /**
     * 打印电子工单
     * @param cycle
     * @throws Exception
     */
    public void printEdoc(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData returnData = new DataMap();
        returnData.put("RESULT_CODE", "0");
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        IDataset printDataSet = CSViewCall.call(this, "CS.PrintNoteSVC.getCnoteInfo", data);
        if (IDataUtil.isNotEmpty(printDataSet))
        {
            returnData.put("CNOTE_DATA", printDataSet.getData(0));
        }
        else
        {
            returnData.put("RESULT_CODE", "1");
        }
        setAjax(returnData);
    }

    /**
     * 打印信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryPrintInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        Pagination page = getPagination("recordNav");
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataOutput result = CSViewCall.callPage(this, "SS.BatPrintInvoiceSVC.queryPrintInfo", data, page);
        setRecordCount(result.getDataCount());
        setInfos(result.getData());
        setCond(data);
    }
    
    /**
     * 创建补打工单数据
     * @param cycle
     * @throws Exception
     */
    public void createPrintInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData resultInfo = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.BatPrintInvoiceSVC.createPrintInfo", data);
        if(IDataUtil.isNotEmpty(results)){
        	resultInfo = results.getData(0);
        }
        //如果有生成数据，则查询补打工单记录，否则直接返回提示
        if(StringUtils.equals(resultInfo.getString("RESULT_CODE"), "0")){
        	queryPrintInfo(cycle);
        }
        setAjax(resultInfo);
    }
    
    //REQ201811130004优化物联网卡相关界面及功能——BOSS侧
    //读取电子工单数据  wuhao5
    public void importData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();    
        IData returnData = new DataMap();
        IDataset results = CSViewCall.call(this, "SS.BatPrintInvoiceSVC.importData", data);
        if (IDataUtil.isNotEmpty(results) && results.size() > 0)
        {
        	returnData = results.getData(0);       	
            setAjax(returnData);
        }
    }
    public void getTradeBatPicIdSyn(IRequestCycle cycle) throws Exception
    {
        IData data = getData();    
        CSViewCall.call(this, "SS.BatPrintInvoiceSVC.getTradeBatPicIdSyn", data);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordCount(long recordCount);

}
