package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class TraceTradeData extends BaseTradeData{

	private String xTraceId;
	private String xParentTraceId;
	private String cancelTag;
	
	public void setXTraceId(String xTraceId){
		this.xTraceId=xTraceId;
	}
	
	public String getXTraceId(){
		return this.xTraceId;
	}
	public void setXParentTraceId(String xParentTraceId){
		this.xParentTraceId=xParentTraceId;
	}
	
	public String getXParentTraceId(){
		return this.xParentTraceId;
	}
	public void setCancelTag(String cancelTag){
		this.cancelTag=cancelTag;
	}
	
	public String getCancelTag(){
		return this.cancelTag;
	}

	public TraceTradeData()
	{
	
	}

	public TraceTradeData(IData data)
	{
		this.xTraceId = data.getString("X_TRACE_ID");
		this.xParentTraceId = data.getString("X_PARENT_TRACE_ID");
		this.cancelTag = data.getString("CANCEL_TAG");
	}
	
	public TraceTradeData clone()
	{
		TraceTradeData traceTradeData = new TraceTradeData();
		traceTradeData.setXTraceId(this.getXTraceId());
		traceTradeData.setXParentTraceId(this.getXParentTraceId());
		traceTradeData.setCancelTag(this.getCancelTag());
		
		return traceTradeData;
	}
	
	public IData toData()
	{
		IData data = new DataMap();
		data.put("X_TRACE_ID", this.xTraceId);
		data.put("X_PARENT_TRACE_ID", this.xParentTraceId);
		data.put("CANCEL_TAG", this.cancelTag);
		
		return data;
	}
	
	public String getTableName()
	{
		return "TF_B_TRACE_TRADE";
	}
	
	public String toString()
	{
		IData data = new DataMap();
		data.put(getTableName(), this.toData());
		return data.toString();
	}
}