
package com.asiainfo.veris.crm.order.web.person.interboss.ibossquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryIbTrade extends PersonBasePage
{
	
	 /**
     * 初始化
     */
	public void init(IRequestCycle cycle) throws Exception
    {
		loadTradeTypeList(cycle);
    }
    /**
     * 查询一级BOSS台账
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryIbTradeInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = getData("cond");
        Pagination page = this.getPagination("pageNav");
        String all_element = data.getString("TRADE_TYPE");
        String remark = "";
        String bipcode = "";
        String activitycode = "";
        if(all_element.indexOf("&")>0)
        {
        	remark = all_element.split("&")[0];
        	bipcode = all_element.split("&")[1];
        	activitycode = all_element.split("&")[2];
        	data.put("BIPCODE", bipcode);
        	data.put("ACTIVITYCODE", activitycode);            
        }
        IDataOutput result = CSViewCall.callPage(this, "SS.QueryIbTradeSVC.qryIbTradeBySNAndReqTime", data, page);
        IDataset dataset = result.getData();
//        for(int i=0; i<dataset.size(); i++)
//        {
//        	dataset.getData(i).put("REMARK", remark);   	
//        }
        setCount(result.getDataCount());
        setInfos(dataset);
        setCond(getData("cond"));
    }
    /**
     *一级boss台账页面业务类型提取
     * @param cycle
     * @throws Exception 
     */
    public void loadTradeTypeList(IRequestCycle cycle) throws Exception
    {   	
    	IData input = new DataMap();
        IDataset dataset = CSViewCall.call(this, "SS.QueryIbTradeSVC.qryTradeTypeList", input);
        IDataset tradeTypeList = new DatasetList();
        for(int i=0; i<dataset.size(); i++)
    	{
    		IData data = dataset.getData(i);
    		String ALL_ELEMENT = data.getString("REMARK")+"&"+data.getString("BIPCODE")+"&"+data.getString("ACTIVITYCODE");
    		data.put("ALL_ELEMENT", ALL_ELEMENT);
    		tradeTypeList.add(data);
    	}
        setTradeTypeList(tradeTypeList);
    }
    /**
     *一级boss台账页面对应流水号LOG提取
     * @param cycle
     * @throws Exception 
     */
    public void queryIbTradelogInfo(IRequestCycle cycle) throws Exception
    {
    	boolean isseelogRightPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "isseelogRight");
    	if(isseelogRightPriv)
    	{
    		IData param = getData();
        	String transido = param.getString("TRANSIDO");
        	IData input = new DataMap();
        	input.put("TRANSIDO", transido);
        	IDataset dataset = CSViewCall.call(this, "SS.QueryIbTradeSVC.qryTradelogList", input);
        	setTradeLogList(dataset);
    	}
    	else
    	{
    		 String alertInfo = "您没有权限查看该日志库！";
             this.setAjax("ALERT_INFO", alertInfo);
    	}
    }
    
    public abstract void setCond(IData cond);
    public abstract void setInfos(IDataset infos);
    public abstract void setCount(long count);
    public abstract void setTradeTypeList(IDataset tradeTypeList);
    public abstract void setTradeLogList(IDataset TradeLogList);
}
