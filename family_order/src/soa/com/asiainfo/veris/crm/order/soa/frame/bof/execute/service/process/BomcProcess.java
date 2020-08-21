package com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.TraceTradeData;
import com.wade.trace.TraceContext;

public class BomcProcess {

	public static void recordBomc(BusiTradeData btd) throws Exception{
		String traceId = TraceContext.getTraceId();//调用wadeApi
		if(StringUtils.isNotBlank(traceId)){
			String parentId = TraceContext.getCurrentProbeId();//调用wadeapi
			String cancelTag = btd.getMainTradeData().getCancelTag();
			TraceTradeData ttd = new TraceTradeData();
			ttd.setXTraceId(traceId);
			ttd.setCancelTag(cancelTag);
			ttd.setXParentTraceId(parentId);
			btd.add(null, ttd);
		}
	}
	
	public static IData getRecord(String tradeId, String cancelTag) throws Exception{
		String traceId = TraceContext.getTraceId();//调用wadeApi
		if(StringUtils.isNotBlank(traceId)){
			String parentId = TraceContext.getCurrentProbeId();;//调用wadeApi获取
			IData bomc = new DataMap();
			bomc.put("TRADE_ID", tradeId);
			bomc.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
			bomc.put("X_TRACE_ID", traceId);
			bomc.put("X_PARENT_TRACE_ID", parentId);
			bomc.put("CANCEL_TAG", cancelTag);
			return bomc;
		}
		else{
			return null;
		}
		
	}
	
	public static void insertRecord(String tradeId, String cancelTag) throws Exception{
		IData record = getRecord(tradeId, cancelTag);
		if(record!=null){
			Dao.insert("TF_B_TRACE_TRADE", record);
		}
	}
}
