
package com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.receiptzf;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.TaxUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.StateTaxUtil;

public class ReceiptZFBean extends CSBizBean{
    /**
     * @Description 获取可作废发票记录
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryZFReceipt(IData data) throws Exception{
        String acceptTime = data.getString("ACCEPT_TIME", "");
        String acceptMonth = 0 == acceptTime.length() ? "" : acceptTime.substring(acceptTime.indexOf("-") + 1);

        // 查找可作废发票记录
        IDataset Receipts = TicketInfoQry.qryZfReceipt(data.getString("SERIAL_NUMBER", ""), data.getString("TRADE_ID", ""), acceptMonth, acceptTime, data.getString("TRADE_STAFF_ID", ""), data.getString("TICKET_ID", ""));

        // 剔除非boss收款补录业务
        IDataset result = new DatasetList();
        for (int i = 0, size = Receipts.size(); i < size; ++i){
            IData receipt = Receipts.getData(i);
            if (!"1102".equals(receipt.getString("TRADE_TYPE_CODE", "")))
                result.add(receipt);
        }
        return result;
    }

    /**
     * @Description 发票作废,本意是用于前台发票作废,但收据作废也需要作废,也是同样的逻辑,故此处收据作废也调用
     * @param data
     * @return
     * @throws Exception
     */
    public void ReceiptZF(IData data) throws Exception{
        // 根据PRINT_ID获取发票记录
        String printId = data.getString("PRINT_ID");
        IDataset records = TicketInfoQry.qryReceiptInfo(printId);
        if (IDataUtil.isEmpty(records))
            CSAppException.apperr(TicketException.CRM_TICKET_5, printId);

        // 更新tf_bh_ticket发票记录,设置TICKET_TYPE_CODE=4
        IData record = records.getData(0);
        IData param = new DataMap();
        param.put("TRADE_ID", record.getString("TRADE_ID", ""));
        param.put("OPER_TYPE", "4");// 发票作废标志
        param.put("TICKET_STATE_CODE", record.getString("TICKET_STATE_CODE", ""));
        param.put("TICKET_TYPE_CODE", record.getString("TICKET_TYPE_CODE", ""));
        param.put("FEE_MODE", record.getString("FEE_MODE", ""));
        int num = Dao.executeUpdateByCodeCode("TF_B_TICKET", "UPDATE_TICKET_STATE_CODE", param,Route.getJourDb());
        if (num < 1)
            CSAppException.apperr(TicketException.CRM_TICKET_6, record.getString("TRADE_ID", ""), record.getString("FEE_MODE", ""), 
            		record.getString("TICKET_STATE_CODE", ""),record.getString("TICKET_TYPE_CODE", ""));

        if ("6".equals(record.getString("TICKET_STATE_CODE"))){// 如果作废冲红发票
            param.put("TICKET_STATE_CODE", "7");
            param.put("OPER_TYPE", "0");// 原发票重新置为正常发票
            // 更新原有发票记录
            num = Dao.executeUpdateByCodeCode("TF_B_TICKET", "UPDATE_TICKET_STATE_CODE", param,Route.getJourDb());
            if (num < 1)
                CSAppException.apperr(TicketException.CRM_TICKET_6, record.getString("TRADE_ID", ""), record.getString("FEE_MODE", ""), 
                		record.getString("TICKET_STATE_CODE", ""),record.getString("TICKET_TYPE_CODE", ""));
        }

        if ("2".equals(record.getString("FEE_MODE", "")) && TaxUtils.stateTaxTicket(record.getString("TICKET_TYPE_CODE", ""))
        		&& StringUtils.equals("0", record.getString("TICKET_STATE_CODE", ""))){// 预存款发票作废
        	if(!StringUtils.equals("9721", record.getString("TRADE_TYPE_CODE",""))){//商务电话开户,费用不同步给账务,不需要更新预存款可打金额
        		StateTaxUtil.updateAcctPrintFee(record.getString("SERIAL_NUMBER", ""), record.getString("TRADE_ID", ""), 
        				record.getString("FEE", ""), "false");// 发票作废更新,有问题直接抛出异常,不先进行检查
        	}
        }
        /**
         * 国税已用发票作废
         * REQ201501270018关于票据作废界面走域权控制的优化 
         * 调用新接口：
            StateTaxUtil.stateTaxInvoiceCancel(record.getString("TRADE_TYPE_CODE", ""),record.getString("TAX_NO"),
        		record.getString("TICKET_ID"),record.getString("FWM", ""),record.getString("TRADE_STAFF_ID"),
        		record.getString("FEE", ""),record.getString("TICKET_TYPE_CODE", "")); 
         */
         
        
        IData inData = new DataMap(); 
    	inData.put("TAX_NO", record.getString("TAX_NO"));
        inData.put("TICKET_ID", record.getString("TICKET_ID"));
        inData.put("FWM", record.getString("FWM", ""));
        inData.put("FEE", record.getString("FEE", ""));// 开票类型
        inData.put("STAFF_ID", record.getString("TRADE_STAFF_ID")); 
        inData.put("TICKET_TYPE_CODE", record.getString("TICKET_TYPE_CODE", ""));//票样类型
        //需求新增传值：
        String tradeTypeCode=record.getString("TRADE_TYPE_CODE", "");
        String tradeTypeName ="";
        if(!"".equals(tradeTypeCode)){
        	tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
        }
        inData.put("TRADE_TYPE_NAME", tradeTypeName);// 业务名
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);// 业务类别
        inData.put("TRADE_ID", record.getString("TRADE_ID", ""));//业务ID
         
        StateTaxUtil.stateTaxInvoiceCancel(inData);
    }
}
