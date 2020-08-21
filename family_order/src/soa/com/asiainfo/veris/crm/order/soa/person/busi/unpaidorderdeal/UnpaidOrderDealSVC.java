package com.asiainfo.veris.crm.order.soa.person.busi.unpaidorderdeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.batprintinvoice.BatPrintInvoiceBean;
import com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptprint.ReceiptPrintBean;

public class UnpaidOrderDealSVC extends CSBizService
{
	private static final long serialVersionUID = 4564327309457706639L;

	public IDataset getUnpaidOrderInfo(IData param) throws Exception
	{
		UnpaidOrderDealBean bean = BeanManager.createBean(UnpaidOrderDealBean.class);
		
		IDataset tradeInfos = bean.getUnpaidOrderInfo(param);
		
		if(IDataUtil.isNotEmpty(tradeInfos))
		{
			for(int i=0,size = tradeInfos.size();i<size;i++)
			{
				IData data = tradeInfos.getData(i);
				String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
				IDataset ctrlParas = CommparaInfoQry.getCommparaAllCol("CSM","9876",tradeTypeCode,"0898");
				if(IDataUtil.isNotEmpty(ctrlParas))
				{
					data.put("CAN_CANCEL", "FALSE");
				}
				else
				{
					data.put("CAN_CANCEL", "TRUE");
				}
			}
		}
		return tradeInfos;
	}
	
	public IDataset cancelTrade(IData param) throws Exception
	{
		UnpaidOrderDealBean bean = BeanManager.createBean(UnpaidOrderDealBean.class);
		return bean.cancelTrade(param);
		
	}
	
	public IDataset getTradeFees(IData param) throws Exception
	{
		UnpaidOrderDealBean bean = BeanManager.createBean(UnpaidOrderDealBean.class);
		return bean.getTradeFees(param);
	}
	
	public IDataset getOrderFees(IData param) throws Exception
	{
		UnpaidOrderDealBean bean = BeanManager.createBean(UnpaidOrderDealBean.class);
		return bean.getOrderFees(param);
	}
	
	public IDataset printTradeNote(IData param) throws Exception
	{
		IDataset printNoteInfos = new DatasetList();
		ReceiptPrintBean bean = BeanManager.createBean(ReceiptPrintBean.class);
		IData input = new DataMap();
		input.put("TRADE_ID", param.getString("TRADE_ID"));
		input.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
		input.put("ACCEPT_TIME", param.getString("ACCEPT_TIME"));
		input.put("TRADE_STAFF_ID", param.getString("TRADE_STAFF_ID"));
		IDataset needPrintTrades = bean.queryTradeReceipt(input);
		
		if(IDataUtil.isNotEmpty(needPrintTrades))
		{
			for(int i=0;i<needPrintTrades.size();i++)
			{
				IData temp = needPrintTrades.getData(i);
				String checkedValue = temp.getString("TRADE_ID")+","+temp.getString("FEE_MODE");
				input.clear();
				input.put("checkvalue", checkedValue);
				input.put("STAFF_NAME", getVisit().getStaffName());
				input.put("DEPART_NAME", getVisit().getDepartName());
				IDataset noteInfos = bean.printTrade(param);
				printNoteInfos.addAll(noteInfos);
			}
		}
		return printNoteInfos;
	}
	
	public IData printTradeReceipt(IData param) throws Exception
	{
		UnpaidOrderDealBean bean = BeanManager.createBean(UnpaidOrderDealBean.class);
		BatPrintInvoiceBean bbean = BeanManager.createBean(BatPrintInvoiceBean.class);
		
		IDataset tradeReceipts = bean.getTradeReceipts(param);
		
		if(IDataUtil.isEmpty(tradeReceipts))
		{
			IData input = new DataMap();
			String acceptDate = param.getString("ACCEPT_TIME");
			input.put("TRADE_ID", param.getString("TRADE_ID"));
			input.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
			input.put("STAFF_ID", param.getString("TRADE_STAFF_ID"));
			input.put("START_DATE", acceptDate.substring(0, 10)+SysDateMgr.START_DATE_FOREVER);
			input.put("END_DATE", acceptDate.substring(0, 10));
	        IData result = bbean.createPrintInfo(input);
	        if(!StringUtils.equals("0", result.getString("RESULT_CODE","1")))
	        {
	        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"打印错误，根据订单TRADE_ID=【"+param.getString("TRADE_ID")+"】无法生成打印数据!");
	        }
		}
		
		param.put("PRINT_TRADE_ID", param.getString("TRADE_ID"));
		IData printData = bbean.printTrade(param);
		printData.put("TRADE_ID", param.getString("TRADE_ID"));
		printData.put("USER_ID", param.getString("USER_ID"));
		return printData;
	}
}
