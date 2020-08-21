package com.asiainfo.veris.crm.order.web.person.gprslimit;

import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class GprsLimit extends PersonBasePage{
	
	private static Logger logger = Logger.getLogger(GprsLimit.class);
	 
    public void queryImportData(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        String alertInfo = "";
        IDataOutput infos = CSViewCall.callPage(this, "SS.GprsLimitSVC.queryImportData", data, getPagination("pagin"));

        if (IDataUtil.isEmpty(infos.getData()))
        {
            alertInfo = "查询无数据!";
        }

        setInfos(infos.getData());
        setCount(infos.getDataCount());

        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }
    
    public void queryDayData(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        String alertInfo = "";
        IDataset infos = CSViewCall.call(this, "SS.GprsLimitSVC.queryDayData", data);

        if (IDataUtil.isEmpty(infos))
        {
            alertInfo = "查询无数据!";
        }

        setInfos(infos);
        
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }
    
    public void initEditPage(IRequestCycle cycle) throws Exception
   	{
   		IData pageData = getData();
		String serialNumber = pageData.getString("SERIAL_NUMBER");
		String isLimit = pageData.getString("IS_LIMIT");
		serialNumber = URLDecoder.decode(URLDecoder.decode(serialNumber,"UTF-8"),"UTF-8");
		isLimit = URLDecoder.decode(URLDecoder.decode(isLimit,"UTF-8"),"UTF-8");
		pageData.put("SERIAL_NUMBER", serialNumber);
		pageData.put("IS_LIMIT", isLimit);
		setCond(pageData);
   	}
    
    public void submitGprsNotLimit(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        inputData.put("SERIAL_NUMBER", inputData.getString("cond_SERIAL_NUMBER_DAY"));
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.GprsUnLockSVC.tradeReg", inputData);

        // 设置页面返回数据
        setAjax(result);
    }
    
    
    public void submitGprsLimit(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        inputData.put("SERIAL_NUMBER", inputData.getString("cond_SERIAL_NUMBER_DAY"));
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.GprsLockSVC.tradeReg", inputData);

        // 设置页面返回数据
        setAjax(result);
    }
    
	 public abstract void setCond(IData cond);
	 public abstract void setInfos(IDataset dataset);
	 public abstract void setCount(long count);
}
