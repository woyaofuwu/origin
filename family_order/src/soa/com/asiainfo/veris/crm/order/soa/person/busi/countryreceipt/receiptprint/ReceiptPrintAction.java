
package com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptprint;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.print.IAfterPrintAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.StateTaxUtil;

public class ReceiptPrintAction implements IAfterPrintAction
{
    /**
     * @Description 预存款补打发票,更新账务可打金额
     * @param inparams
     * @return
     * @throws Exception
     */
    public void action(IData param) throws Exception
    {
    	IDataset printedSJReceipt = TicketInfoQry.qryPrintedSJReceipt(param.getString("TRADE_ID",""));//查询是否已经收据补打过发票
		if(IDataUtil.isEmpty(printedSJReceipt)){
			//预存款补打发票时,更新原来的收据票据状态
    		IData updParam = new DataMap();
        	updParam.put("TRADE_ID", param.getString("TRADE_ID", ""));
        	updParam.put("OPER_TYPE", "5");// 收据换成发票标志
        	updParam.put("TICKET_STATE_CODE", "0");
        	String advancePayTicketTypeCode = "F";
        	if(StringUtils.equals(param.getString("TICKET_TYPE_CODE", ""),"D"))//如果当前打印移动发票的话,则更新移动收据
        		advancePayTicketTypeCode = "B";
        	
        	updParam.put("TICKET_TYPE_CODE", advancePayTicketTypeCode);
        	updParam.put("FEE_MODE", param.getString("FEE_MODE", ""));

            // 更新原有收据发票记录,第一次收据补打发票时更新成功,后续更新记录为0,所以此处不做返回值判断
            int num = Dao.executeUpdateByCodeCode("TF_B_TICKET", "UPDATE_TICKET_STATE_CODE", updParam);
            if(num < 1)
            	CSAppException.apperr(TicketException.CRM_TICKET_6, param.getString("TRADE_ID", ""), param.getString("FEE_MODE", ""), "0",
            			advancePayTicketTypeCode);
		}
    	
        
    	if(!StringUtils.equals("9721", param.getString("TRADE_TYPE_CODE",""))){//商务电话开户,费用不同步给账务,不需要更新预存款可打金额
    		StateTaxUtil.updateAcctPrintFee(param.getString("SERIAL_NUMBER", ""), param.getString("TRADE_ID", ""), 
    				"-" + param.getString("TOTAL_FEE", ""), "false");
    	}
    }
}
