package com.asiainfo.veris.crm.order.soa.person.busi.unpaidorderdeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PaymentCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;

public class UnpaidOrderDealBean extends CSBizBean
{
	
	public IDataset getUnpaidOrderInfo(IData param) throws Exception
	{
		String staffId = this.checkParam(param, "TRADE_STAFF_ID");
		String startDate = this.checkParam(param, "START_DATE");
		String endDate = this.checkParam(param, "END_DATE");
		String subscribeState = this.checkParam(param, "SUBSCRIBE_STATE");
		IDataset result = new DatasetList();
		
		subscribeState ="X";
		IDataset unPaidTrades = TradeInfoQry.qryTradesByStaffIdDate(staffId, startDate, endDate, subscribeState, param.getString("SERIAL_NUMBER"));
		result.addAll(unPaidTrades);
		
		subscribeState = "Y";
		IDataset unPrintTrades = TradeInfoQry.qryTradesByStaffIdDate(staffId, startDate, endDate, subscribeState, param.getString("SERIAL_NUMBER"));
		result.addAll(unPrintTrades);
		
		return result;
	}
	
	public IDataset cancelTrade(IData param) throws Exception
	{
		String tradeId = this.checkParam(param, "TRADE_ID");
		String orderId = this.checkParam(param, "ORDER_ID");
		
		IDataset oldTrades = TradeInfoQry.getTradeInfobyTradeId(tradeId);
		
		if(IDataUtil.isEmpty(oldTrades))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TRADE_ID=【"+tradeId+"】未找到对应的订单记录！");
		}
		IData oldTrade = oldTrades.getData(0);
		
		String intfId = oldTrade.getString("INTF_ID");
		String tradeTypeCode =oldTrade.getString("TRADE_TYPE_CODE");
		
		if("131".equals(tradeTypeCode) && "TF_B_TRADE,".equals(intfId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地停开机无法取消！");
		}
		
		//BUG20180522105701  统一付费取消工单后数据未结束问题  add by tanzheng
		if("325".equals(tradeTypeCode)){
			 	IData data = new DataMap();

			 	data.put("TRADE_ID", tradeId);

	            String sql = "DELETE FROM TP_F_UNIONPAY_MEMBER WHERE TRADE_ID=:TRADE_ID ";

	            Dao.executeUpdate(new StringBuilder(sql), data);
		}
		
		if(StringUtils.equals("X", oldTrade.getString("SUBSCRIBE_STATE")) || StringUtils.equals("Y", oldTrade.getString("SUBSCRIBE_STATE")))
		{
			
			IData pubData = this.getPublicData(oldTrade);// 操作员/trade_id/cancel_tag等相关信息
			String newOrderId = this.createCancelOrder(oldTrade, pubData);
			pubData.put("NEW_ORDER_ID", newOrderId);
			this.modifyTradeAndTradeStaff(oldTrade, pubData);
			this.cancelOtherTrades(pubData, orderId);
			this.createCancelTrade(newOrderId, oldTrade, pubData);//UPD_TRADE_CANCEL_TAG
			if(StringUtils.equals("Y", oldTrade.getString("SUBSCRIBE_STATE")))
			{
				IDataset tradeFees = this.getTradeFees(param);
				if(IDataUtil.isNotEmpty(tradeFees))
				{
					PaymentCall.doTradeRefund(tradeId);
				}
			}
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TRADE_ID=【"+tradeId+"】未找到对应的订单记录！");
		}
		IDataset results = new DatasetList();
		IData result= new DataMap();
		result.put("RESULT_CODE", "1");
		results.add(result);
		return results;
	}
	
	 public void cancelOtherTrades(IData pubData,String orderId)throws Exception
	 {
		 IDataset tradeInfos = TradeInfoQry.getTradeInfobyOrd2(orderId);
		 if (IDataUtil.isNotEmpty(tradeInfos)) 
		 {
			 for (int i = 0, size = tradeInfos.size(); i < size; i++) 
			 {
				 IData param = new DataMap();
				 IData data = new DataMap();
				 data.putAll(tradeInfos.getData(i));
				 param.put("TRADE_ID", tradeInfos.getData(i).getString("TRADE_ID"));
		        
				 int cnt = Dao.executeUpdateByCodeCode("TF_B_TRADE", "DEL_BY_TRADEID", param, Route.getJourDb());
				 if (cnt > 0)
				 {
					 //param.put("CANCEL_TAG", "1");// 被返销
					 data.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
					 data.put("CANCEL_STAFF_ID", pubData.getString("STAFF_ID"));
					 data.put("CANCEL_DEPART_ID", pubData.getString("DEPART_ID"));
					 data.put("CANCEL_CITY_CODE", pubData.getString("CITY_CODE"));
					 data.put("CANCEL_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
					 Dao.insert("TF_BH_TRADE", data, Route.getJourDb());
				 }
			 
			 }
			 
		 }
	 }
	
	 public IDataset cancelOrder(IData inParam)throws Exception
	 {
		String orderId = this.checkParam(inParam, "ORDER_ID");
		IDataset tradeInfos = TradeInfoQry.queryTradeByOrerId(orderId, "0");
		if (IDataUtil.isEmpty(tradeInfos)) 
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据ORDER_ID=【"
					+ orderId + "】未找到可以取消的订单记录！");
		}
		String newOrderId = "";
		for (int i = 0, size = tradeInfos.size(); i < size; i++) 
		{
			IData oldTrade = tradeInfos.getData(i);
			String subscribeState = oldTrade.getString("SUBSCRIBE_STATE");
			String tradeId = oldTrade.getString("TRADE_ID");
			if(!StringUtils.equals("Y", subscribeState))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "订单TRADE_ID=【"
						+ tradeId + "】不是未打印状态，无法取消！");
			}
			IData pubData = this.getPublicData(oldTrade);// 操作员/trade_id/cancel_tag等相关信息
			if(StringUtils.isBlank(newOrderId))
			{
				newOrderId = this.createCancelOrder(oldTrade, pubData);
			}
			this.modifyTradeAndTradeStaff(oldTrade, pubData);
			this.createCancelTrade(newOrderId, oldTrade, pubData);// UPD_TRADE_CANCEL_TAG
			IDataset tradeFees = this.getTradeFees(tradeId);
			if (IDataUtil.isNotEmpty(tradeFees)) 
			{
				PaymentCall.doTradeRefund(tradeId);
			}
		}
	    	
		IDataset results = new DatasetList();
		IData result= new DataMap();
		result.put("RESULT_CODE", "1");
		results.add(result);
		return results;
	}
	
	public IDataset getTradeReceipts(IData param) throws Exception
	{
		String tradeId = this.checkParam(param, "TRADE_ID");
		return TradeReceiptInfoQry.getPrintNoteInfoByTradeId(tradeId);
	}
	
	public IDataset getTradeFees(IData param) throws Exception
	{
		String tradeId = this.checkParam(param, "TRADE_ID");
		return TradefeeSubInfoQry.getTradeFeeSubByTradeId(tradeId);
	}
	public IDataset getTradeFees(String tradeId) throws Exception
	{
		return TradefeeSubInfoQry.getTradeFeeSubByTradeId(tradeId);
	}
	
	public IDataset getOrderFees(IData param) throws Exception
	{
		IDataset orderFees = new DatasetList();
		String orderId = this.checkParam(param, "ORDER_ID");
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT TRADE_ID FROM TF_B_TRADE A WHERE ORDER_ID =:ORDER_ID AND SUBSCRIBE_STATE ='X' ");
		IDataset tradeDatas = Dao.qryBySql(sql, param, Route.getJourDb());
		if(IDataUtil.isNotEmpty(tradeDatas))
		{
			for(int i=0;i<tradeDatas.size();i++)
			{
				IData temp = tradeDatas.getData(i);
				String tradeId = temp.getString("TRADE_ID");
				
				IDataset tradeFees = TradefeeSubInfoQry.qryTradeFeeSubByTradeId(tradeId, this.getTradeEparchyCode());
				if(IDataUtil.isNotEmpty(tradeFees))
				{
					orderFees.addAll(tradeFees);
				}
			}
		}
		return orderFees;
	}
	
	private IData getPublicData(IData hisTrade) throws Exception
    {
        IData pubData = new DataMap();
        pubData.put("CANCEL_TAG", "3");// 2=被返销
        pubData.put("ORDER_ID", hisTrade.getString("ORDER_ID", ""));
        pubData.put("TRADE_ID", hisTrade.getString("TRADE_ID", ""));
        pubData.put("SYS_TIME", SysDateMgr.getSysTime());
        pubData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        pubData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        pubData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        pubData.put("LOGIN_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return pubData;
    }
	
	public void modifyTradeAndTradeStaff(IData hisTrade, IData pubData) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", pubData.getString("TRADE_ID"));
        
        int cnt = Dao.executeUpdateByCodeCode("TF_B_TRADE", "DEL_BY_TRADEID", param, Route.getJourDb());
        if (cnt < 1)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_3051, pubData.getString("TRADE_ID"));
        }
        param.putAll(hisTrade);
        param.put("CANCEL_TAG", "1");// 被返销
        param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        param.put("CANCEL_STAFF_ID", pubData.getString("STAFF_ID"));
        param.put("CANCEL_DEPART_ID", pubData.getString("DEPART_ID"));
        param.put("CANCEL_CITY_CODE", pubData.getString("CITY_CODE"));
        param.put("CANCEL_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        if (!Dao.insert("TF_BH_TRADE", param, Route.getJourDb()))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_3051, pubData.getString("TRADE_ID"));
        }
    }
	
	public IData createCancelTrade(String newOrderId, IData hisTradeData,IData pubData) throws Exception
    {
        IData newTradeData = new DataMap();
        newTradeData.putAll(hisTradeData);

        /********* 费用 *******************************/
        long lOperFee = -hisTradeData.getLong("OPER_FEE", 0);
        long lAdvancePay = -hisTradeData.getLong("ADVANCE_PAY", 0);
        long lforegift = -hisTradeData.getLong("FOREGIFT", 0);
        String strFeeState = (lOperFee + lAdvancePay + lforegift == 0) ? "0" : "1";
        newTradeData.put("SUBSCRIBE_TYPE", hisTradeData.getString("SUBSCRIBE_TYPE"));
        newTradeData.put("OPER_FEE", String.valueOf(lOperFee));
        newTradeData.put("ADVANCE_PAY", String.valueOf(lAdvancePay));
        newTradeData.put("FOREGIFT", String.valueOf(lforegift));
        newTradeData.put("FEE_STATE", strFeeState);
        newTradeData.put("FEE_TIME", (strFeeState == "0") ? "" : pubData.getString("SYS_TIME"));
        newTradeData.put("FEE_STAFF_ID", (strFeeState == "0") ? "" : pubData.getString("STAFF_ID"));

        String subscribeType = hisTradeData.getString("SUBSCRIBE_TYPE");
        if (StringUtils.equals("97", subscribeType))
        {
            newTradeData.put("SUBSCRIBE_TYPE", "1");
        }

        newTradeData.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
        
        newTradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        newTradeData.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newTradeData.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        newTradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        newTradeData.put("CANCEL_TAG", pubData.getString("CANCEL_TAG"));// 2=返销 3=取消
        newTradeData.put("CANCEL_DATE", hisTradeData.getString("ACCEPT_DATE"));
        newTradeData.put("CANCEL_STAFF_ID", hisTradeData.getString("TRADE_STAFF_ID"));
        newTradeData.put("CANCEL_DEPART_ID", hisTradeData.getString("TRADE_DEPART_ID"));
        newTradeData.put("CANCEL_CITY_CODE", hisTradeData.getString("TRADE_CITY_CODE"));
        newTradeData.put("CANCEL_EPARCHY_CODE", hisTradeData.getString("TRADE_EPARCHY_CODE"));
        // 以下字段取默认值
        newTradeData.put("BPM_ID", "");
        newTradeData.put("SUBSCRIBE_STATE", "P");
        newTradeData.put("NEXT_DEAL_TAG", "0");
        newTradeData.put("OLCOM_TAG", hisTradeData.getString("OLCOM_TAG"));
        newTradeData.put("FINISH_DATE", "");
        newTradeData.put("EXEC_ACTION", "");
        newTradeData.put("EXEC_RESULT", "");
        newTradeData.put("EXEC_DESC", "");

        newTradeData.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newTradeData.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));

        newTradeData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        newTradeData.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());
        newTradeData.put("REMARK", "未支付/打印订单取消");
        //newTradeData.put("INTF_ID", "TF_B_TRADE,");

        // 待确认
        newTradeData.put("CHANNEL_TRADE_ID", "");
        newTradeData.put("CHANNEL_ACCEPT_TIME", "");
        newTradeData.put("CANCEL_TYPE_CODE", "");
        newTradeData.put("RSRV_TAG1", "");

        String processTagSet = hisTradeData.getString("PROCESS_TAG_SET");

        String tradeTypeCode = hisTradeData.getString("TRADE_TYPE_CODE");

        String rsrvStr7 = hisTradeData.getString("RSRV_STR7");

        String agentTag = processTagSet.substring(3, 4);
        if (("1".equals(agentTag) || "700".equals(tradeTypeCode))
                && StringUtils.isNotEmpty(rsrvStr7))
        {
            //买断开户专用，处理费用
            newTradeData.put("RSRV_STR7", "-" + rsrvStr7);
        }

        newTradeData.put("ORDER_ID", newOrderId);// 新的订单号
        if (!Dao.insert("TF_B_TRADE", newTradeData, Route.getJourDb()))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_304, pubData.getString("TRADE_ID"));
        }

        return newTradeData;
    }
	
	public String createCancelOrder(IData hisTradeData, IData pubData) throws Exception
    {
        IData newOrder = new DataMap();
        String newOrderId = SeqMgr.getOrderId();// 生成新的order_id
        newOrder.put("ORDER_ID", newOrderId);
        newOrder.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
        newOrder.put("ORDER_TYPE_CODE", hisTradeData.getString("TRADE_TYPE_CODE"));
        newOrder.put("TRADE_TYPE_CODE", hisTradeData.getString("TRADE_TYPE_CODE"));
        newOrder.put("PRIORITY", hisTradeData.getString("PRIORITY"));
        newOrder.put("ORDER_STATE", "2");
        newOrder.put("NEXT_DEAL_TAG", "0");
        newOrder.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        String custId = hisTradeData.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isNotEmpty(custInfo))
        {
        	newOrder.put("CUST_ID", custId);
            newOrder.put("CUST_NAME", hisTradeData.getString("CUST_NAME")); 
            newOrder.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE", ""));
            newOrder.put("PSPT_ID", custInfo.getString("PSPT_ID", ""));
        }else{
        	newOrder.put("PSPT_TYPE_CODE", "");
            newOrder.put("PSPT_ID", "");
            newOrder.put("CUST_ID", 0);
            newOrder.put("CUST_NAME", ""); 
        }
        
        
        newOrder.put("EPARCHY_CODE", hisTradeData.getString("EPARCHY_CODE"));
        newOrder.put("CITY_CODE", hisTradeData.getString("CITY_CODE"));

        newOrder.put("OPER_FEE", hisTradeData.getString("OPER_FEE"));
        newOrder.put("FOREGIFT", hisTradeData.getString("FOREGIFT"));
        newOrder.put("ADVANCE_PAY", hisTradeData.getString("ADVANCE_PAY"));
        newOrder.put("FEE_STATE", hisTradeData.getString("FEE_STATE"));

        newOrder.put("CANCEL_TAG", "3");
        newOrder.put("CANCEL_DATE", hisTradeData.getString("ACCEPT_DATE"));
        newOrder.put("CANCEL_STAFF_ID", hisTradeData.getString("TRADE_STAFF_ID"));
        newOrder.put("CANCEL_DEPART_ID", hisTradeData.getString("TRADE_DEPART_ID"));
        newOrder.put("CANCEL_CITY_CODE", hisTradeData.getString("TRADE_CITY_CODE"));
        newOrder.put("CANCEL_EPARCHY_CODE", hisTradeData.getString("TRADE_EPARCHY_CODE"));

        newOrder.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("FINISH_DATE", "");
        newOrder.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));

        newOrder.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newOrder.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));

        newOrder.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));
        newOrder.put("SUBSCRIBE_TYPE", "0");// 默认写个0吧
        Dao.insert("TF_B_ORDER", newOrder, Route.getJourDb());
        return newOrderId;
    }
	
	public String checkParam(IData param ,String key) throws Exception
	{
		String value = param.getString(key);
		if(StringUtils.isBlank(value))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "参数【"+key+"】必须传入");
		}
		return value;
	}

}
