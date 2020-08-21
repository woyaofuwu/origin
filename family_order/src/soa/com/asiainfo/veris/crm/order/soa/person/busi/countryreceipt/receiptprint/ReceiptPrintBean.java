
package com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptprint;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.StateTaxUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.PrintDataSetBean;
public class ReceiptPrintBean extends CSBizBean
{
    /**
     * @Description 从流水中剔除已打印发票记录,得到可打印流水记录
     * @param receipts
     * @param trades
     * @return
     * @throws Exception
     */
    private void checkTrades(IDataset receipts, IDataset trades) throws Exception
    {
        if (IDataUtil.isEmpty(receipts) || IDataUtil.isEmpty(trades))
            return;

        for (int i = 0; i < receipts.size(); ++i)
        {
            String tradeId = receipts.getData(i).getString("TRADE_ID", "");
            String feeMode = receipts.getData(i).getString("FEE_MODE", "");

            for (int j = 0; j < trades.size(); ++j)
            {
                if (tradeId.equals(trades.getData(j).getString("TRADE_ID")) && (0 == feeMode.length() || feeMode.equals(trades.getData(j).getString("FEE_MODE"))))
                {
                    trades.remove(j);// 如果当前记录已经打印过发票,则从trades中删除
                    break;
                }
            }
        }
    }
    
 /**
     * 若补打的时候，已打过电子发票，则将其移除
     */
    private void checkPDFTrades(IDataset receipts,IDataset trades) throws Exception
    {
    	if (IDataUtil.isEmpty(receipts) || IDataUtil.isEmpty(trades))
            return;
        for (int i = 0; i < receipts.size(); ++i)
        {
        	String tradeId = receipts.getData(i).getString("TRADE_ID", "");
        	String assembFlag = receipts.getData(i).getString("ASSEMB_FLAG", "");
        	String operFee = String.valueOf(assembFlag.charAt(0));
        	String foreGift = String.valueOf(assembFlag.charAt(1));
        	String advancePay = String.valueOf(assembFlag.charAt(2));
        	for (int j = 0; j < trades.size(); ++j)
            {
        		if(tradeId.equals(trades.getData(j).getString("TRADE_ID"))&&
        		  "0".equals(trades.getData(j).getString("FEE_MODE"))&&"1".equals(operFee)){
        			trades.remove(j);
        		}else if(tradeId.equals(trades.getData(j).getString("TRADE_ID"))&&
        		  "1".equals(trades.getData(j).getString("FEE_MODE"))&&"1".equals(foreGift)){
        			trades.remove(j);
        		}else if(tradeId.equals(trades.getData(j).getString("TRADE_ID"))&&
        		  "2".equals(trades.getData(j).getString("FEE_MODE"))&&"1".equals(advancePay)){
        			trades.remove(j);
        		}
            }
        }
    }   

    /**
     * @Description 获取可补打的流水记录
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryTradeReceipt(IData param) throws Exception
    {
        String acceptMonth = param.getString("ACCEPT_TIME").substring(5, 7);
        IDataset printTrades = new DatasetList();
        IDataset printFees = TradeHistoryInfoQry.getPrintFee(param.getString("TRADE_ID", ""), param.getString("SERIAL_NUMBER", ""), acceptMonth, param.getString("ACCEPT_TIME", ""), param.getString("TRADE_STAFF_ID", ""));
        printTrades.addAll(printFees);
        IDataset printAdvancePays = TradeHistoryInfoQry.getPrintAdvancePay(param.getString("TRADE_ID", ""), param.getString("SERIAL_NUMBER", ""), acceptMonth, param.getString("ACCEPT_TIME", ""), param.getString("TRADE_STAFF_ID", ""));
        printTrades.addAll(printAdvancePays);

        IDataset printRecords = TicketInfoQry.qryPrintedReceipts(param.getString("TRADE_ID", ""), param.getString("SERIAL_NUMBER", ""), param.getString("TRADE_STAFF_ID", ""));
        IDataset printPDFRecordes = TicketInfoQry.queryPrintPDFLogByTradeIdSerialNumber(param.getString("TRADE_ID", ""), param.getString("SERIAL_NUMBER", ""), param.getString("TRADE_STAFF_ID", ""));
                
        checkTrades(printRecords, printTrades);
        checkPDFTrades(printPDFRecordes,printTrades);
        return printTrades;
    }

    /**
     * @Description 发票补打后台动作
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset submitPrintReceipt(IData input) throws Exception{
    	String checkValue = input.getString("checkvalue","");
    	if(0 == checkValue.length())
    		CSAppException.apperr(TicketException.CRM_TICKET_7);
    	
    	String[] params = StringUtils.split(checkValue,",");//参数为TRADE_ID,FEE_MODE
    	String tradeId = params[0];
    	String feeMode = params[1];
    	
        //电子发票冲红
    	
    	
    	//根据tradeid获取台账信息
    	IData tradeInfo = UTradeInfoQry.qryTradeByTradeId(tradeId, "0", CSBizBean.getTradeEparchyCode());

        if (IDataUtil.isEmpty(tradeInfo))
            tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", CSBizBean.getTradeEparchyCode());
    	
    	if(IDataUtil.isEmpty(tradeInfo))
    		CSAppException.apperr(TradeException.CRM_TRADE_65,tradeId);
    	
    	if("2".equals(feeMode)){//预存款
    		if(!StringUtils.equals("9721", tradeInfo.getString("TRADE_TYPE_CODE",""))){//商务电话开户,费用不同步给账务,不需要更新预存款可打金额
    			//检查是否可以打印
        		StateTaxUtil.updateAcctPrintFee(tradeInfo.getString("SERIAL_NUMBER",""), tradeInfo.getString("TRADE_ID",""), 
        				"-"+tradeInfo.getString("ADVANCE_PAY",""), "true");
    		}
    		
    		//AfterPrintActionMgr.getInstance().addAction(tradeId, new ReceiptPrintAction());
    	}
    	
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("FEE_MODE", feeMode);
		IDataset rs = new DatasetList();
		rs.add(param);
		return rs;
    }
    
    /**
     * @Description 构建打印数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset printTrade(IData param) throws Exception{
    	String checkValue = param.getString("checkvalue","");
    	if(0 == checkValue.length())
    		CSAppException.apperr(TicketException.CRM_TICKET_7);
    	
    	String[] params = StringUtils.split(checkValue,",");//参数为TRADE_ID,FEE_MODE
    	String tradeId = params[0];
    	String feeMode = params[1];
    	
    	//如果是跨区补卡发票打印，打印前判断一次此发票是否已打印过,直接返回结果，由前台报错不能打印发票
        RegTradeData rtd = new RegTradeData(tradeId);
        MainTradeData mtd = rtd.getMainTradeData();
        if(mtd ==null)
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据TRADE_ID=【"+tradeId+"】无法找到对应的订单记录");
        }
        if ("149".equals(rtd.getMainTradeData().getTradeTypeCode())) {
	        IDataset printRecords = TicketInfoQry.qryPrintedReceipts(rtd.getTradeId(),rtd.getMainTradeData().getSerialNumber(), "");
	        IDataset printPDFRecordes = TicketInfoQry.queryPrintPDFLogByTradeIdSerialNumber(param.getString("TRADE_ID", ""), param.getString("SERIAL_NUMBER", ""), param.getString("TRADE_STAFF_ID", ""));//还没上线的，先留着，后面有要求，再补
	        if (IDataUtil.isNotEmpty(printRecords)||IDataUtil.isNotEmpty(printPDFRecordes)) {
	            for (int j = 0; j < printRecords.size(); j++)
	            {
	                String printedTradeId = printRecords.getData(j).getString("TRADE_ID", "");
	                if (printedTradeId.equals(rtd.getTradeId()) )
	                {
	                	IData printInfo = new DataMap();
	                	printInfo.put("IS_INVOICE_PRINTED", "1");//如果当前记录已经打印过发票,则标识已打印  
	                    IDataset printRes = new DatasetList();
	                    printRes.add(printInfo);
	                    return printRes;
	                }
	            }
		    }
	    }           
    	return PrintDataSetBean.printDataSet(1,tradeId,feeMode,param.getString("STAFF_NAME",""),
    			param.getString("DEPART_NAME",""));//1为正数发票
    }
}
