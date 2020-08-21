package com.asiainfo.veris.crm.order.web.person.bat.batsaleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatSaleActive extends PersonBasePage{
	
	public void getBatchId(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData result = new DataMap();
        IDataset results = CSViewCall.call(this, "CS.BatDealSVC.initBatchId", pageData);
        if (IDataUtil.isNotEmpty(results))
        {
            result = (IData) (results.get(0));
        }
        setBatchId(result);
    }
	
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData(); 
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset checkResult = CSViewCall.call(this, "SS.BatSaleActiveSVC.getSaleProductCond", data);
        this.setProducts(checkResult);
        this.setTradeStaffId(getVisit().getStaffId());
        getBatchId(cycle);
    }
    
    public void getPackages(IRequestCycle cycle) throws Exception
    {

    	IData data = getData(); 
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset condition = CSViewCall.call(this, "SS.BatSaleActiveSVC.getSaleProductCond", data); 
        this.setPackages(condition);

    }
    
    public void importBatData(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        CSViewCall.call(this, "SS.BatSaleActiveSVC.importBatData", pageData);
    }
    
    public void dealSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        CSViewCall.call(this, "SS.BatSaleActiveSVC.dealSubmit", pageData);
    }
    
    public void queryImportData(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        String alertInfo = "";
        IDataOutput infos = CSViewCall.callPage(this, "SS.BatSaleActiveSVC.queryImportData", data, getPagination("pagin"));

        if (IDataUtil.isEmpty(infos.getData()))
        {
            alertInfo = "查询无数据!";
        }

        setInfos(infos.getData());
        setCount(infos.getDataCount());

        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }

	 public abstract void setBatchId(IData batchId);
	 public abstract void setPackages(IDataset deposits);
	 public abstract void setProducts(IDataset servs);
	 public abstract void setCond(IData cond);
	 public abstract void setInfos(IDataset dataset);
	 public abstract void setCount(long count);
	 public abstract void setTradeStaffId(String tradeStaffId);
}
