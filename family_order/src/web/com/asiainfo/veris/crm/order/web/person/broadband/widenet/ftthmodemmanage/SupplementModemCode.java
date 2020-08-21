
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.ftthmodemmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SupplementModemCode extends PersonBasePage
{ 
    
    /**
     * 查询用户可补录光猫信息
     * @param cycle
     * @throws Exception
     */
    
    public void queryModemSupplementInfo(IRequestCycle cycle)throws Exception
    {
    	IData pagedata = getData(); 
        IDataset results = CSViewCall.call(this, "SS.FTTHModemManageSVC.queryModemSupplementInfo", pagedata); 
        setInfos(results);
        setAjax(results);
    }
    
    /**
     * 查询用户可补录商务光猫信息
     * @param cycle
     * @throws Exception
     */
    public void queryBusiModemSupplementInfo(IRequestCycle cycle)throws Exception
    {
    	IData pagedata = getData(); 
        IDataset results = CSViewCall.call(this, "SS.FTTHBusiModemManageSVC.queryBusiModemSupplementInfo", pagedata); 
        setInfos(results);
        setAjax(results);
    }
    
    /**
     * 
     * 判断用户规则
     * 
     * */
    public void checkFTTHBusi(IRequestCycle cycle) throws Exception
    { 
        IData pagedata = getData(); 
        IData checks=new DataMap();
        IDataset results = CSViewCall.call(this, "SS.FTTHBusiModemManageSVC.checkFTTHBusi", pagedata); 
        if(results==null || results.size()==0){
        	checks.put("RTNCODE", "9");
        	checks.put("RTNMSG", "该用户不存在7341 集团商务宽带产品！"); 
        }
        this.setAjax(checks); 
    } 
    
    /**
     * 校验终端，预占终端
     * @param cycle
     * @throws Exception
     */
    public void checkModemId(IRequestCycle cycle)throws Exception
    {
    	IData pagedata = getData(); 
    	String serial_number = pagedata.getString("SERIAL_NUMBER");
    	if(serial_number.length() > 11){//商务宽带去掉宽带号码KD_
    		serial_number = serial_number.substring(3, serial_number.length());
    	}
    	pagedata.put("SERIAL_NUMBER", serial_number);
    	IDataset results = CSViewCall.call(this, "SS.FTTHModemManageSVC.queryModem", pagedata);
    	IData retData = results.first();
    	setInfo(retData);
    	setAjax(results);
    }
    
    /**
     * 光猫补录提交
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        request.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset output = CSViewCall.call(this, "SS.SupplementModemCodeRegSVC.tradeReg", request);
        this.setAjax(output);
    }
    
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    
}
