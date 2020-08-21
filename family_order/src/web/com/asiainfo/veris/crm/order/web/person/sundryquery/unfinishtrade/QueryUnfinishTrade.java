
package com.asiainfo.veris.crm.order.web.person.sundryquery.unfinishtrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：未完工工单查询 作者：GongGuang
 */
public abstract class QueryUnfinishTrade extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 初始化
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        // 设置起止时间
        String endDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        String firstData = endDate;
        IData data = getData("cond", true);
        data.put("cond_START_DATE", firstData);
        data.put("cond_FINISH_DATE", endDate);
        data.put("cond_TRADE_STAFF_ID", this.getVisit().getStaffId());
        this.setCondition(data);
    }

    /**
     * 功能：未完工工单查询
     */
    public void queryUnfinishTrade(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        inparam.put("START_DATE", inparam.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER);
        inparam.put("FINISH_DATE", inparam.getString("FINISH_DATE") + SysDateMgr.END_DATE);
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryUnfinishTradeSVC.queryUnfinishTrade", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【未完工工单】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setTradeTraceInfos(IDataset infos);
    public abstract void setPFTraceInfos(IDataset infos);
    
    /*REQ201707210024 未完工单界面功能优化*/
    public void queryUnfinishTradeTrace(IRequestCycle cycle) throws Exception{
    	IData inparam=getData();
    	inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
    	inparam.put("TRADE_ID", inparam.getString("TRADE_ID"));
    	inparam.put("ORDER_ID", inparam.getString("ORDER_ID"));
    	String serial_number = inparam.getString("SERIAL_NUMBER");
    	String trade_type_code = inparam.getString("TRADE_TYPE_CODE");
    	String trade_id = inparam.getString("TRADE_ID");
    	
    	//查询台账轨迹
    	IDataset dataset = CSViewCall.call(this, "SS.QueryUnfinishTradeSVC.queryUnfinishTradeTrace", inparam);
    	if(IDataUtil.isNotEmpty(dataset)){
    		for(int i=0;i<dataset.size();i++){
    			IData data = dataset.getData(i);
    			data.put("SERIAL_NUMBER", serial_number);
    			data.put("TRADE_TYPE_CODE", trade_type_code);
    		}
    	}
    	
    	//调用服开接口  查询服开轨迹
    	IDataset  pfdataset = CSViewCall.call(this,"SS.QueryUnfinishTradeSVC.queryUnfinishPFTrace", inparam);
    	String alertInfo ="";
    	if(IDataUtil.isNotEmpty(pfdataset)){
    		for(int i=0;i<pfdataset.size();i++){
    			IData data = pfdataset.getData(i);
    			if(IDataUtil.isNotEmpty(data.getData("SUBSCRIBE"))){
    				data.put("STATUS", data.getData("SUBSCRIBE").getString("SUBSCRIBE_STATUS"));
    				data.put("DESC", data.getData("SUBSCRIBE").getString("SUBSCRIBE_REMARK"));
    				data.put("ORDER_TYPE", "长流程工单");
    			}
    			if(IDataUtil.isNotEmpty(data.getData("IBPLATSYN"))){
    				data.put("STATUS", data.getData("IBPLATSYN").getString("IBPLATSYN_STATUS"));
    				data.put("DESC", data.getData("IBPLATSYN").getString("IBPLATSYN_REMARK"));
    				data.put("ORDER_TYPE", "数指工单");
    			}
    			if(IDataUtil.isNotEmpty(data.getData("OLCOMWORK"))){
    				data.put("STATUS", data.getData("OLCOMWORK").getString("OLCOMWORK_STATUS"));
    				data.put("DESC", data.getData("OLCOMWORK").getString("OLCOMWORK_REMARK"));
    				data.put("ORDER_TYPE", "联指工单");
    			}
    			data.put("SERIAL_NUMBER", serial_number);
    			data.put("TRADE_TYPE_CODE", trade_type_code);
    			data.put("TRADE_ID", trade_id);
    		}
    	}else{
			alertInfo = "未查询到工单的服开轨迹信息";
		}
		
    	
    	this.setAjax("ALERT_INFO", alertInfo);
    	setTradeTraceInfos(dataset);
    	setPFTraceInfos(pfdataset);
    }

}
