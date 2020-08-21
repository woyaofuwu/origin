package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge;

import org.apache.log4j.Logger;
import java.math.BigDecimal;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;


public class SupplierChargeBean extends CSBizBean
{
	protected static Logger log = Logger.getLogger(SupplierChargeBean.class);
	/**
     * 打印收据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset printReceipt(IData input) throws Exception
    {
    	String serialNum= input.getString("SERIAL_NUMBER","");
        String tradeId = input.getString("TRADE_ID","");
        String receiName=input.getString("RECEIPT_NAME","");
        String feeMode=input.getString("FEE_MODE","");
        String totalFee = input.getString("FEE_AMOUNT", "");
        String corpname = StaticUtil.getStaticValueDataSource(getVisit(),Route.CONN_RES, "RES_SUPPLIER", new String[]
                { "SUPPLIER_NO", "SUPPLIER_TYPE_ID" }, "SUPPLIER_NAME", new String[]
                { input.getString("CHNL_ID"), "M" });
        String account = input.getString("YEAR")+String.format("%02d", input.getInt("ACCEPT_MONTH"));
        Double feeAmount = Double.parseDouble(totalFee);//String.format("%1$3.2f", feeAmount / 100.0);
        StringBuilder reamrkSb = new StringBuilder();
        reamrkSb.append("卖场名称："+ corpname + "~");
        reamrkSb.append("费用账期年月："+account);
        String feeName = input.getString("FEE_NAME");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String tradeTypeName = "";
        if(!"".equals(tradeTypeCode)){
        	tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
        }
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        IData receiptData = new DataMap();
        IDataset printRes = new DatasetList();
        
        receiptData.put("REMARK", reamrkSb);
        receiptData.put("TRADE_NAME", ReceiptNotePrintMgr.TRADE_NAME);
        receiptData.put("TRADE_TYPE", tradeTypeName);
        receiptData.put("ALL_MONEY_LOWER", totalFee);
        receiptData.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(feeAmount));
        receiptData.put("FEE_TYPE", feeName);
        receiptData.put("FEE", totalFee);
        receiptData.put("TRADE_ID", tradeId);
        receiptData.put("STAFF_NAME", getVisit().getStaffName());
        receiptData.put("STAFF_ID", getVisit().getStaffId());
        receiptData.put("DEPART_NAME", getVisit().getDepartName());
        receiptData.put("OPERATION_DATE", SysDateMgr.getSysDate());

        IDataset templetItem = ReceiptNotePrintMgr.getReceiptTempletItems(tradeTypeCode, ReceiptNotePrintMgr.RECEIPT_P0002, "0", tradeEparchyCode);
        IData printData = ReceiptNotePrintMgr.parsePrintData(receiptData, templetItem);

        IData printInfo = new DataMap();
        printInfo.put("NAME", receiName);
        printInfo.put("PRINT_DATA", printData);
        printInfo.put("TYPE", ReceiptNotePrintMgr.RECEIPT_P0002);
        printInfo.put("FEE_MODE", feeMode);
        printInfo.put("TOTAL_FEE", new BigDecimal(feeAmount*100));
        printInfo.put("HAS_TICKET", "1");
        printInfo.put("TRADE_ID", tradeId);
        printInfo.put("SERIAL_NUMBER", serialNum);
        printInfo.put("EPARCHY_CODE", tradeEparchyCode);
        printInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
        printRes.add(printInfo);

        return printRes;
    }
}