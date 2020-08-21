
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.pos;

import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.fee.PosLogInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeePayMoneyInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;

public class PosBean extends CSBizBean
{

    public static final String POS_PAY_CODE = "m";

    public static void checkPosFee(String tradePosId, String orderId, String tradeId, String payMoney) throws Exception
    {
        // 查询正常状态缴费记录
        IDataset posSet = PosLogInfoQry.getPrePosLog(tradePosId, "0", "S");
        if (IDataUtil.isEmpty(posSet))
        {
            CSAppException.apperr(FeeException.CRM_FEE_116);
        }
        long amount = 0;
        for (int i = 0; i < posSet.size(); i++)
        {
            amount += posSet.getData(i).getLong("AMOUNT", 0);
        }
        if (!StringUtils.equals(payMoney, String.valueOf(amount)))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_25);
        }
        updatePosTradeId(tradePosId, orderId, tradeId);
    }

    public static void updatePosTradeId(String tradePosId, String orderId, String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRE_TRADE_ID", tradePosId);
        param.put("ORDER_ID", orderId);
        param.put("TRADE_ID", tradeId);

        Dao.executeUpdateByCodeCode("TL_B_POS_LOG", "UPD_TRADEID_BY_PRE_TRADEID", param);
    }

    // 标准化POS返回数字
    private void fmtNumber(IData data) throws Exception
    {
        Iterator keys = data.keySet().iterator();
        Iterator values = data.values().iterator();
        while (keys.hasNext())
        {
            Object key = keys.next();
            Object value = values.next();

            if ("AMOUNT".equals(key) || "POS_ID".equals(key) || "CARD_NO".equals(key) || "BANK_NO".equals(key) || "CERT_NO".equals(key) || "REF_NO".equals(key) || "BATCH_NO".equals(key) || "AUTH_NO".equals(key))
            {
                value = value.toString().trim();
                Pattern pattern = Pattern.compile("[0-9]*");
                if (!pattern.matcher((String) value).matches())
                {
                    value = null;
                }
                data.put((String) key, value);
            }
        }
    }

    /**
     * POS撤销日志
     * 
     * @param input
     * @throws Exception
     */
    public IData recordCancelPosLog(IData input) throws Exception
    {
    	IData returnData = new DataMap();
    	String logLevel = getLogLevel(input);
        // 如果撤销刷卡失败，且日志记录级别为0，则不需要记录POS刷卡日志
        if (StringUtils.equals(logLevel, "0") && StringUtils.equals(input.getString("STATUS"), "1"))
        {
            returnData.put("RESULT_CODE", "0");
            return returnData;
        }
    	
    	String transType = input.getString("TRANS_TYPE");
    	String date = input.getString("TRANS_DATE");
        String time = input.getString("TRANS_TIME");
        String sysTime = SysDateMgr.getSysTime();
        if(StringUtils.isBlank(date) || StringUtils.isBlank(time)){
        	input.put("TRANS_DATE_TIME", sysTime);
        }else{
        	Date transDateTime = SysDateMgr.string2Date(sysTime.substring(0, 4) + date + time, "yyyyMMddHHmmss");
            input.put("TRANS_DATE_TIME", SysDateMgr.date2String(transDateTime, "yyyy-MM-dd HH:mm:ss"));
        }
        
    	//调用返销接口
    	IDataset poslogs = CSAppCall.callAcct("AM_CRM_CancelPosLog", input, false).getData();
    	if(IDataUtil.isNotEmpty(poslogs)){
        	input.put("POS_TRADE_ID", poslogs.getData(0).getString("POS_TRADE_ID"));
        }
    	if (StringUtils.equals(transType, "M")){
    		/**
    		 * 手工调账只需要更新原记录状态
    		 */
    		returnData = updatePosLog(input);
    	}else{
    		/**
    		 * 撤销和退货需要插入返销流水
    		 * 同时更新原POS刷卡流水状态
    		 */
    		returnData = recordPosLog(input);
    		if (StringUtils.equals(returnData.getString("RESULT_CODE"), "0") 
    				&& StringUtils.isNotBlank(input.getString("OLD_POS_TRADE_ID")))
            {
    			returnData = updatePosLog(input);
            }
    	}
        return returnData;
    }

    /**
     * 记录POS刷卡台账信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData recordPosLog(IData input) throws Exception
    {
        IData returnData = new DataMap();
        returnData.put("RESULT_CODE", "1");

        String logLevel = getLogLevel(input);
        // 如果消费刷卡失败，且日志记录级别为0，则不需要记录POS刷卡日志
        if (StringUtils.equals(logLevel, "0") && StringUtils.equals(input.getString("STATUS"), "1"))
        {
            returnData.put("RESULT_CODE", "0");
            return returnData;
        }

        String posTradeId = "";
        String date = input.getString("TRANS_DATE");
        String time = input.getString("TRANS_TIME");
        String transType = input.getString("TRANS_TYPE");
        String sysTime = SysDateMgr.getSysTime();
        if(StringUtils.isBlank(date) || StringUtils.isBlank(time)){
        	input.put("TRANS_DATE_TIME", sysTime);
        }else{
        	Date transDateTime = SysDateMgr.string2Date(sysTime.substring(0, 4) + date + time, "yyyyMMddHHmmss");
            input.put("TRANS_DATE_TIME", SysDateMgr.date2String(transDateTime, "yyyy-MM-dd HH:mm:ss"));
        }

        // 如果是撤销则将金额修改为负数
        if (StringUtils.equals(transType, "A") || StringUtils.equals(transType, "T"))
        {
            input.put("AMOUNT", input.getLong("AMOUNT") * -1);
        }

        input.put("ACCEPT_TIME", sysTime);
        input.put("OPER_STAFF_ID", getVisit().getStaffId());
        input.put("LOCAL_ADDR", getVisit().getRemoteAddr());
        
        //记录临时台账流水，后续登记会替换掉TRADE_ID，保存到RSRV_STR1里面保留轨迹
        input.put("RSRV_STR1", input.getString("TRADE_ID"));
        //如果是是返销，直接从调用账务接口里面获取，不需要在调用账务插入接口
        if (StringUtils.equals(transType, "A") || StringUtils.equals(transType, "T")){
        	if (Dao.insert("TL_B_POS_LOG", input))
            {
                returnData.put("POS_TRADE_ID", input.getString("POS_TRADE_ID"));
                returnData.put("AMOUNT", input.getLong("AMOUNT"));
                returnData.put("RESULT_CODE", "0");
            }
        }else{		//消费时候直接调账务接口
        	IDataset poslogs = CSAppCall.callAcct("AM_CRM_InsertPosLog", input, false).getData();
            if(IDataUtil.isNotEmpty(poslogs)){
            	posTradeId = poslogs.getData(0).getString("POS_TRADE_ID");
            	
            	input.put("POS_TRADE_ID", posTradeId);
            	if (Dao.insert("TL_B_POS_LOG", input))
                {
                    returnData.put("POS_TRADE_ID", posTradeId);
                    returnData.put("AMOUNT", input.getLong("AMOUNT"));
                    returnData.put("RESULT_CODE", "0");
                }
            }
        }
        return returnData;
    }
    
    private String getLogLevel(IData input) throws Exception
    {
    	String logLevel = "0";
        IDataset commSet = ParamInfoQry.getCommparaByCode("CSM", "1800", "", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(commSet))
        {
            logLevel = commSet.getData(0).getString("PARA_CODE2", "0");
        }
        return logLevel;
    }

    /**
     * 更新原POS刷卡记录状态
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData updatePosLog(IData input) throws Exception
    {
        IData returnData = new DataMap();
        returnData.put("RESULT_CODE", "1");

        IData param = new DataMap();
        String transType = input.getString("TRANS_TYPE");
        // 主键ID
        param.put("POS_TRADE_ID", input.getString("OLD_POS_TRADE_ID"));

        if (StringUtils.equals(transType, "M"))
        {
            param.put("STATUS", "4");
            param.put("PRINT_RECEIPTS", "1");
            param.put("PRINT_STAFF_ID", getVisit().getStaffId());
        }else{
        	param.put("CANCEL_POS_TRADE_ID", input.getString("POS_TRADE_ID"));
            param.put("CANCEL_TRADE_ID", input.getString("TRADE_ID", ""));
            param.put("CANCEL_CERT_NO", input.getString("CERT_NO"));
            param.put("CANCEL_REF_NO", input.getString("REF_NO"));
            
        	if (StringUtils.equals(transType, "A"))
            {
                param.put("STATUS", "2");
            }
            else if (StringUtils.equals(transType, "T"))
            {
                param.put("STATUS", "3");
            }
        }
        param.put("CANCEL_TRANS_TYPE", transType);
        param.put("CANCEL_DATE", SysDateMgr.getSysTime());

        if (Dao.save("TL_B_POS_LOG", param))
        {
            returnData.put("RESULT_CODE", "0");
        }
        return returnData;
    }
    
    
    /**
     * 查询成功刷卡记录
     * 
     * @param posTradeId
     * @param status
     * @return
     * @throws Exception
     */
    public IDataset queryPosCost(IData input, Pagination pagination) throws Exception
    {
    	String cancelReason=input.getString("CANCEL_REASON", "1");
    	String serialNumber=input.getString("SERIAL_NUMBER", "");
    	String tradeId=input.getString("TRADE_ID", "");
        if(StringUtils.equals(cancelReason, "1") && StringUtils.isNotBlank(serialNumber)){
        	return this.queryPosLogBySn(serialNumber);
        }else if(StringUtils.equals(cancelReason, "2") && StringUtils.isNotBlank(tradeId)){
        	return this.queryPosLogByTrade(tradeId);
        }
        return new DatasetList();
    }
    
    public IDataset queryPosLogByTrade(String tradeId)throws Exception
    {
    	IDataset dataset = PosLogInfoQry.queryPosLogBytradeId(tradeId);
    	if(IDataUtil.isNotEmpty(dataset))
    	{
    		for(int i=0;i<dataset.size();i++)
    		{
    			IData data = dataset.getData(i);
    			String eparchyCode = CSBizBean.getTradeEparchyCode();
    			IDataset hTradeInfo = TradeHistoryInfoQry.queryByTradeIdCancelTag(tradeId, "2", eparchyCode);
    			if(IDataUtil.isEmpty(hTradeInfo))
    			{
    				dataset.remove(i);i--;continue;
    			}
    			IDataset payMoneyInfo = TradeFeePayMoneyInfoQry.queryByTradeIdPayMoneyCode(tradeId, "m", eparchyCode);
    			if(IDataUtil.isEmpty(payMoneyInfo))
    			{
    				dataset.remove(i);i--;continue;
    			}
    		}
    	}
    	return dataset;
    }
    
    public IDataset queryPosLogBySn(String serialNumber)throws Exception
    {
    	IDataset dataset = PosLogInfoQry.queryPosLogBySn(serialNumber);
    	
    	if(IDataUtil.isNotEmpty(dataset))
    	{
    		for(int i=0;i<dataset.size();i++)
    		{
    			IData data = dataset.getData(i);
    			String tradeId = data.getString("TRADE_ID");
    			String eparchyCode = CSBizBean.getTradeEparchyCode();
    			IDataset tradeInfo = TradeInfoQry.queryTradeByTradeId(tradeId, eparchyCode);
    			if(IDataUtil.isNotEmpty(tradeInfo))
    			{
    				dataset.remove(i);i--;continue;
    			}
    			IDataset tradeHisInfo = TradeHistoryInfoQry.queryByTradeId(tradeId, eparchyCode);
    			if(IDataUtil.isNotEmpty(tradeHisInfo))
    			{
    				dataset.remove(i);i--;continue;
    			}
    		}
    	}
    	return dataset;
    }
    
    public IData getPosReceipt(IData input) throws Exception
    {
    	IData posReceipt = new DataMap();
    	String posTradeId=input.getString("POS_TRADE_ID");
    	String sysdate = SysDateMgr.getSysDate();
    	IDataset posLosInfos = PosLogInfoQry.queryPosLogByPosTradeId(posTradeId);
    	if(IDataUtil.isNotEmpty(posLosInfos)){
    		IData posLogData = posLosInfos.getData(0);
    		long amount = posLogData.getLong("AMOUNT");
    		posReceipt.put("AMOUNT", String.format("%1$3.2f", amount/100.0));
			posReceipt.put("SERIAL_NUMBER", posLogData.getString("SERIAL_NUMBER"));
			posReceipt.put("POS_TRADE_ID", posTradeId);
			posReceipt.put("YYYY", sysdate.substring(0, 4));
			posReceipt.put("MM", sysdate.substring(5, 7));
			posReceipt.put("DD", sysdate.substring(8, 10));
			posReceipt.put("MERCH_ID", posLogData.getString("MERCH_ID"));
			posReceipt.put("TRANS_TYPE", "消费");
			posReceipt.put("TRANS_DATE_TIME", posLogData.getString("TRANS_DATE_TIME"));
			posReceipt.put("CARD_NO", posLogData.getString("CARD_NO"));
			posReceipt.put("REF_NO", posLogData.getString("REF_NO"));
			posReceipt.put("AMOUNT_UPPER", FeeUtils.floatToRMB(amount/100.0)); 
			
			posReceipt.put("TEMP_PATH", "templet/PosReceipt.html?template_code=POS_RECEIPT&version=001");
			posReceipt.put("TEMP_TYPE", ReceiptNotePrintMgr.PRINT_HTML);
    	}        
    	return posReceipt;
    }
}
