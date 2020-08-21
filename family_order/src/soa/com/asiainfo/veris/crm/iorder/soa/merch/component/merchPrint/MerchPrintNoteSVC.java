package com.asiainfo.veris.crm.iorder.soa.merch.component.merchPrint;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;

public class MerchPrintNoteSVC extends CSBizService {
	private static final long serialVersionUID = -9085328084003758338L;

	/**
	 * 获取普通打印数据
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset getPrintData(IData input) throws Exception {
		MerchReceiptNotePrintMgr notPrint = new MerchReceiptNotePrintMgr();
		return notPrint.printTradeReceipt(input);
	}
	
	/**
	 * 购物车获取电子工单数据
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset getCnoteInfo(IData input) throws Exception
    {
    	IDataset reDs = new DatasetList();
		reDs = TradeReceiptInfoQry.getCnoteInfoByTradeId(input.getString("TRADE_ID"));
		//购物车
		if(IDataUtil.isEmpty(reDs))
		{
			String tradeId = input.getString("TRADE_ID");
			String eparchyCode = input.getString("TRADE_ID");
			IData tradeInfo = UTradeInfoQry.qryTradeByTradeId(tradeId, "0", eparchyCode);
			if(IDataUtil.isEmpty(tradeInfo))
			{
				tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", eparchyCode);
			}
			
			if(IDataUtil.isNotEmpty(tradeInfo) && StringUtils.equals("5178", tradeInfo.getString("TRADE_TYPE_CODE")))
			{
				IData data = new DataMap();
    			data.put("TRADE_ID", tradeId);
				data.put("TRADE_TYPE_CODE", "110");
				data.put("NOTE_TYPE", "1");
				data.put("ACCEPT_DATE", tradeInfo.getString("ACCEPT_DATE"));
				data.put("USER_ID", tradeInfo.getString("USER_ID"));
				data.put("SERIAL_NUMBER", tradeInfo.getString("SERIAL_NUMBER"));
				data.put("CUST_NAME", tradeInfo.getString("CUST_NAME"));
				data.put("BRAND_CODE", tradeInfo.getString("BRAND_CODE"));
				data.put("CUST_ID", tradeInfo.getString("CUST_ID"));
				data.put("VERIFY_MODE", tradeInfo.getString(""));
				data.put("TRADE_STAFF_ID", tradeInfo.getString("TRADE_STAFF_ID"));
				data.put("ORG_INFO", tradeInfo.getString("TRADE_DEPART_ID"));
				data.put("RECEIPT_INFO1", tradeInfo.getString(""));
				data.put("RECEIPT_INFO2", tradeInfo.getString(""));
				data.put("RECEIPT_INFO3", tradeInfo.getString(""));
				data.put("RECEIPT_INFO4", tradeInfo.getString(""));
				data.put("RECEIPT_INFO5", tradeInfo.getString(""));
				data.put("NOTICE_CONTENT", tradeInfo.getString(""));
				data.put("ORDER_ID", tradeInfo.getString("ORDER_ID"));
				reDs.add(data);
			}
    	}
    	
    	//2016111610405400202251_REQ201611070014电子化存储新版客户兼容性优化
        IDataset otherTradeDs = TradeReceiptInfoQry.getCnoteOtherTradeInfoByTradeId(input.getString("TRADE_ID"));
        if (otherTradeDs != null && otherTradeDs.size() > 0) {
        	
        	for(int i=0,size=otherTradeDs.size();i<size;i++)
        	{
        		IData temp = otherTradeDs.getData(i);
        		temp.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(temp.getString("BRAND_CODE")));
        		temp.put("TRADE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(temp.getString("TRADE_STAFF_ID")));
        		temp.put("ORG_NAME", UDepartInfoQry.getDepartNameByDepartId(temp.getString("ORG_INFO")));
        		
        		String tradeId = temp.getString("TRADE_ID");
        		String custId = temp.getString("CUST_ID");
        		
        		IData customer = UcaInfoQry.qryCustomerInfoByCustId(custId);
        		if(IDataUtil.isNotEmpty(customer))
        		{
        			temp.put("ID_CARD", customer.getString("PSPT_ID"));
        		}
        		else
        		{
        			IDataset tradeCustomers = TradeCustomerInfoQry.getTradeCustomerByTradeId(tradeId);
        			if(IDataUtil.isNotEmpty(tradeCustomers))
            		{
        				temp.put("ID_CARD", tradeCustomers.getData(0).getString("PSPT_ID"));
            		}
        			else
        			{
        				temp.put("ID_CARD", "0");
        			}
        		}
        	}
        	
            reDs.add(otherTradeDs);
        }
        
        /**
		 * REQ201805170033_在线公司电子稽核报表优化需求
		 * @author zhuoyingzhi
		 * @date 20180614
		 */
        if(IDataUtil.isNotEmpty(reDs)){
        	IData reDinfo=reDs.getData(0);
    		String  tradeTypeCode=reDinfo.getString("TRADE_TYPE_CODE", "");
    		String  tradeType=UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
    		//往原来的集合里面添加
    		reDs.getData(0).put("TRADE_TYPE", tradeType);//业务类型名称
        }
		/****************REQ201805170033_在线公司电子稽核报表优化需求_end**********************************/
		
        return reDs;
    }
}
