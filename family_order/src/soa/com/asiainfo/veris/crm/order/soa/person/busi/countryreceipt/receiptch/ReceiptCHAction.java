
package com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptch;

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

public class ReceiptCHAction implements IAfterPrintAction
{
    /**
     * @Description 预存款补打发票,更新账务可打金额
     * @param inparams
     * @return
     * @throws Exception
     */
    public void action(IData input) throws Exception{
        IData param = new DataMap();
        param.put("TRADE_ID", input.getString("TRADE_ID", ""));
        param.put("OPER_TYPE", "7");// 发票冲红标志
        param.put("TICKET_STATE_CODE", "0");
        param.put("TICKET_TYPE_CODE", input.getString("TICKET_TYPE_CODE", ""));
        param.put("FEE_MODE", input.getString("FEE_MODE", ""));

        // 更新原有发票记录,一个台账只会有一个TICKET_TYPE_CODE=0&FEE_MODE&TICKET_STATE_CODE的记录
        int num = Dao.executeUpdateByCodeCode("TF_B_TICKET", "UPDATE_TICKET_STATE_CODE", param);
        if (num < 1)
            CSAppException.apperr(TicketException.CRM_TICKET_6, input.getString("TRADE_ID", ""), input.getString("FEE_MODE", ""), "0",
            		input.getString("TICKET_TYPE_CODE", ""));

        if (StringUtils.equals("2", input.getString("FEE_MODE", "")) && 
        		!StringUtils.equals("9721", input.getString("TRADE_TYPE_CODE",""))){
            // 通知账务更新预存款可打金额
            StateTaxUtil.updateAcctPrintFee(input.getString("SERIAL_NUMBER", ""), input.getString("TRADE_ID", ""), 
            		input.getString("TOTAL_FEE", "").substring(1), "false");
        }
    }
}
