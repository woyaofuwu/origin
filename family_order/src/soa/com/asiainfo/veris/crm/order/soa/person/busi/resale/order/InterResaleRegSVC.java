package com.asiainfo.veris.crm.order.soa.person.busi.resale.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class InterResaleRegSVC extends OrderService {

	@Override
	public String getOrderTypeCode() throws Exception {
		
//		1001	开户 业务类型:5432 
//		1002	换卡 业务类型:5433 
//		1003	业务功能变更 业务类型:5434 
//		1004	停复机 业务类型:5435 
//		1008	销户 业务类型:5436       销户只插主台账，不插服务台账，由pf那边针对这个业务类型来发起销户。
//		1009	查询业务功能 业务类型:5437   不发联指  PF_OLCOM_TAG配置为空
		
		String oprCode =  this.input.getString("OPR_CODE", "");
		String isStop =  this.input.getString("IS_STOP", "");
	            
        if ("1001".equals(oprCode))
        {
        	this.input.put("ORDER_TYPE_CODE", "5432");
        }
        else if ("1002".equals(oprCode))
        {
        	this.input.put("ORDER_TYPE_CODE", "5433");
        }
        else if ("1003".equals(oprCode))
        {
        	this.input.put("ORDER_TYPE_CODE", "5434");
        }
        else if ("1004".equals(oprCode))
        {
        	if(isStop.equals("1")){		//全停
        		this.input.put("ORDER_TYPE_CODE", "5439");		//5439 欠费停机
        	}else if(isStop.equals("2")){	//半停
        		this.input.put("ORDER_TYPE_CODE", "5438");		//5438 高额半停机
        	}else{
        		this.input.put("ORDER_TYPE_CODE", "5435");
        	}
        }
        else if ("1008".equals(oprCode))
        {
        	this.input.put("ORDER_TYPE_CODE", "5436");
        }else if ("1009".equals(oprCode))
        {
        	this.input.put("ORDER_TYPE_CODE", "5437");
        }
        
        return this.input.getString("ORDER_TYPE_CODE", "");
	}

	@Override
	public String getTradeTypeCode() throws Exception {
		
//		1001	开户 业务类型:5432 
//		1002	换卡 业务类型:5433 
//		1003	业务功能变更 业务类型:5434 
//		1004	停复机 业务类型:5435 
//		1008	销户 业务类型:5436       销户只插主台账，不插服务台账，由pf那边针对这个业务类型来发起销户。
//		1009	查询业务功能 业务类型:5437   不发联指  PF_OLCOM_TAG配置为空
		
		String oprCode =  this.input.getString("OPR_CODE", "");
		String isStop =  this.input.getString("IS_STOP", "");
	            
        if ("1001".equals(oprCode))
        {
        	this.input.put("TRADE_TYPE_CODE", "5432");
        }
        else if ("1002".equals(oprCode))
        {
        	this.input.put("TRADE_TYPE_CODE", "5433");
        }
        else if ("1003".equals(oprCode))
        {
        	this.input.put("TRADE_TYPE_CODE", "5434");
        }
        else if ("1004".equals(oprCode))
        {
        	if(isStop.equals("1")){		//全停
        		this.input.put("TRADE_TYPE_CODE", "5439");		//5439 欠费停机
        	}else if(isStop.equals("2")){	//半停
        		this.input.put("TRADE_TYPE_CODE", "5438");		//5438 高额半停机
        	}else{
        		this.input.put("TRADE_TYPE_CODE", "5435");
        	}
        }
        else if ("1008".equals(oprCode))
        {
        	this.input.put("TRADE_TYPE_CODE", "5436");
        }else if ("1009".equals(oprCode))
        {
        	this.input.put("TRADE_TYPE_CODE", "5437");
        }
        
        return this.input.getString("TRADE_TYPE_CODE", "");
	}
	
	/**
	 * 业务提交后规则校验，如有特殊场景可重载该方法
	 */
	public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
	{
		
	}

}
