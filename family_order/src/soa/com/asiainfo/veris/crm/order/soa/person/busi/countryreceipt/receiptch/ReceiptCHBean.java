
package com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptch;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.print.AfterPrintActionMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.StateTaxUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.PrintDataSetBean;

public class ReceiptCHBean extends CSBizBean
{
    /**
     * @Description 获取可冲红的票据记录
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryCHReceipt(IData param) throws Exception
    {
        String acceptTime = param.getString("ACCEPT_TIME", "");
        String acceptMonth = 0 == acceptTime.length() ? "" : acceptTime.substring(acceptTime.indexOf("-") + 1);

        // 查找可冲红发票记录
        IDataset receipts = TicketInfoQry.qryChReceipt(param.getString("TRADE_ID", ""), param.getString("SERIAL_NUMBER", ""), param.getString("STAFF_ID"), acceptMonth, acceptTime, param.getString("TICKET_ID"));

        // 剔除非boss收款补录业务
        IDataset result = new DatasetList();
        for (int i = 0, size = receipts.size(); i < size; ++i)
        {
            IData receipt = receipts.getData(i);
            if (!"1102".equals(receipt.getString("TRADE_TYPE_CODE", "")))
                result.add(receipt);
        }

        return result;
    }

    /**
     * @Description 发票冲红后台动作
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset submitPrintReceipt(IData input) throws Exception
    {
        String printId = input.getString("checkvalue", "");
        if (0 == printId.length())
        	CSAppException.apperr(TicketException.CRM_TICKET_9);

        // 根据PRINT_ID获取发票记录
        IDataset records = TicketInfoQry.qryReceiptInfo(printId);
        if (IDataUtil.isEmpty(records))
            CSAppException.apperr(TicketException.CRM_TICKET_5, printId);

        IData record = records.getData(0);
        String tradeId = record.getString("TRADE_ID");

        // 根据tradeid获取台账信息
        IData tradeInfo = UTradeInfoQry.qryTradeByTradeId(tradeId, "0", null);
        if (IDataUtil.isEmpty(tradeInfo))
            tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);

        if (IDataUtil.isEmpty(tradeInfo))
            CSAppException.apperr(TradeException.CRM_TRADE_65, tradeId);

        if ("2".equals(record.getString("FEE_MODE"))){// 预存款
        	if(!StringUtils.equals("9721", tradeInfo.getString("TRADE_TYPE_CODE",""))){//商务电话开户,费用不同步给账务,不需要更新预存款可打金额
        		// 检查是否可以打印
                StateTaxUtil.updateAcctPrintFee(record.getString("SERIAL_NUMBER", ""), tradeId, 
                		tradeInfo.getString("ADVANCE_PAY", ""), "true");
        	}
        }

        //AfterPrintActionMgr.getInstance().addAction(tradeId, new ReceiptCHAction());
        IData param = new DataMap();
        param.put("ORDER_ID", tradeInfo.getString("ORDER_ID", ""));
        param.put("EPARCHY_CODE", getTradeEparchyCode());
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
    	String printId = param.getString("checkvalue", "");
        if (0 == printId.length())
        	CSAppException.apperr(TicketException.CRM_TICKET_9);
    	
        // 根据PRINT_ID获取发票记录
        IDataset records = TicketInfoQry.qryReceiptInfo(printId);
        if (IDataUtil.isEmpty(records))
            CSAppException.apperr(TicketException.CRM_TICKET_5, printId);

        IData record = records.getData(0);
        String tradeId = record.getString("TRADE_ID");
        String feeMode = record.getString("FEE_MODE");
    	return PrintDataSetBean.printDataSet(-1,tradeId,feeMode,param.getString("STAFF_NAME",""),
    			param.getString("DEPART_NAME",""));//-1为负数发票
    }
}
