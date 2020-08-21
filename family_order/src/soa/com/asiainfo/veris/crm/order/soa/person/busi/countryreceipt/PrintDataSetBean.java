package com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt;
 
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.fee.FeeListMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.FeeNotePrintMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;

public class PrintDataSetBean extends CSBizBean {
	public static IDataset printDataSet(int type,String tradeId,String feeMode,
			String staffName,String departName) throws Exception {
		if(StringUtils.isEmpty(tradeId))
			CSAppException.apperr(TicketException.CRM_TICKET_4);
		if(StringUtils.isEmpty(feeMode))
			CSAppException.apperr(TicketException.CRM_TICKET_8);
		
		//根据trade_id获取费用信息
		RegTradeData<BaseTradeData> rtd = new RegTradeData(tradeId);
		MainTradeData trade = rtd.getMainTradeData();
		
        String tradeTypeCode = rtd.getTradeTypeCode();
        String tradeType = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);;
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        
        int totalOperMoney = 0;
        if(StringUtils.equals("0", feeMode)){
        	totalOperMoney = Integer.parseInt(trade.getOperFee());
        	//流量直充业务费用保存在预留字段5 lihb3
        	if(totalOperMoney == 0 && "UMMP_FLOW".equals(trade.getRsrvStr4())){
        		totalOperMoney = Integer.parseInt(trade.getRsrvStr5());
        	}
        }else
        	totalOperMoney = Integer.parseInt(trade.getAdvancePay());

        if(-1 == type)
        	totalOperMoney = -totalOperMoney;
     
        IData receiptData = new DataMap();
        String fee = String.format("%1$3.2f", totalOperMoney / 100.0);
        receiptData.put("SERIAL_NUMBER", trade.getSerialNumber());
        receiptData.put("TRADE_NAME", ReceiptNotePrintMgr.TRADE_NAME);
        receiptData.put("CUST_NAME", trade.getCustName());
        receiptData.put("TRADE_TYPE", tradeType);
        receiptData.put("CUR_CYCLE", SysDateMgr.getNowCyc());
        receiptData.put("ALL_MONEY_LOWER", fee);
        receiptData.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(totalOperMoney / 100.0));
        receiptData.put("ALL_MONEY", fee);
        receiptData.put("TRADE_ID", tradeId);
        receiptData.put("STAFF_NAME", staffName);
        receiptData.put("DEPART_NAME", departName);
        receiptData.put("OPERATION_DATE", SysDateMgr.getSysDate());
        
        
        if(tradeTypeCode.equals("9115")){	//如果是铁通终端销售，需要存放一些销售终端的信息 
        	receiptData.put("RSRV_STR1", trade.getRsrvStr1());
        	receiptData.put("RSRV_STR2", trade.getRsrvStr2());
        	receiptData.put("RSRV_STR3", trade.getRsrvStr3());
        	receiptData.put("RSRV_STR4", trade.getRsrvStr4());
        }
        
        processOtherFeeItem(rtd,receiptData);//跨区补卡发票打印特殊处理
        
        IData receiptFee = FeeNotePrintMgr.getPrintReceiptFee(tradeId,tradeTypeCode,feeMode,type);
        if(IDataUtil.isEmpty(receiptFee.getData(feeMode)))
        	CSAppException.apperr(TradeException.CRM_TRADE_217);
        
        receiptData.putAll(receiptFee.getData(feeMode));
        
        IDataset templetItem = ReceiptNotePrintMgr.getReceiptTempletItems(tradeTypeCode, ReceiptNotePrintMgr.RECEIPT_P0001, "0", tradeEparchyCode);
     //重打每次只打一种费用的发票，只打营业与预存
        if(StringUtils.equals("0", feeMode)){
        	  receiptData.put("MERGE_FEE_LIST_OPER_FLAG", "1" ); 
        }else
        {
        	receiptData.put("MERGE_FEE_LIST_ADVANCE_FLAG", "1"); 
	    }
         //end   
        
        IData printInfo = new DataMap();
        IData printData = ReceiptNotePrintMgr.parsePrintData(receiptData, templetItem);
            
       
        
        String printId = SeqMgr.getPrintId();        
        printInfo.put("PRINT_ID", printId);
        printInfo.put("USER_ID", trade.getUserId());
        printInfo.put("EPARCHY_CODE", trade.getEparchyCode());
               
        printInfo.put("NAME", "打印发票");
        printInfo.put("PRINT_DATA", printData);
        printInfo.put("TYPE", ReceiptNotePrintMgr.RECEIPT_P0001);
        printInfo.put("FEE_MODE", feeMode);
        printInfo.put("TOTAL_FEE", totalOperMoney);
        printInfo.put("HAS_TICKET", "1");
        printInfo.put("TRADE_ID", tradeId);
        printInfo.put("SERIAL_NUMBER", trade.getSerialNumber());
        printInfo.put("EPARCHY_CODE", tradeEparchyCode);
        printInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
        if(-1 == type)
        	printInfo.put("CH_TICKET", "1");
        if(StringUtils.equals("2", feeMode))
        	printInfo.put("ADVANCE_TICKET", "1");
        
        printInfo.put("BD_CH_FLAG", "1");//补打&冲红标志
        
        IDataset printRes = new DatasetList();
        printRes.add(printInfo);

        return printRes;
	}
	public static void processOtherFeeItem(RegTradeData rtd,IData printInfo) throws Exception {
        IDataset feeLists = TradefeeSubInfoQry.getFeeListByTrade(rtd.getTradeId());

		for (int i = 0; i < feeLists.size(); i++)
        {
            IData feeData = feeLists.getData(i);
            String tmpFeeMode = feeData.getString("FEE_MODE");
            String feeTypCode = feeData.getString("FEE_TYPE_CODE");
            String feeTypeDes = FeeListMgr.getFeeTypeCodeDesc(tmpFeeMode, feeTypCode);
            int payFee = feeData.getInt("FEE", 0);
            //
            if ("149".equals(rtd.getMainTradeData().getTradeTypeCode())) {
            	String payFeeStr = String.format("%1$3.2f", payFee / 100.0);
            	if ("0".equals(feeTypCode)) {            		
            		printInfo.put("SERVICE_MONEY",payFeeStr);
            	} else if ("10".equals(feeTypCode) && payFee>0) {
                	printInfo.put("SIM_CARD_FEE2_DISCRIBER",feeTypeDes+"：");
            		printInfo.put("SIM_CARD_FEE2",payFeeStr);
            	}
            }   		
         }     
	}
}
