package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetreminder;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.common.query.broadband.WidenetTradeQuery;

/**
 * 宽带催单
 * @author Administrator
 *
 */
public class WidenetReminderSVC extends CSBizService
{
	protected static Logger log = Logger.getLogger(WidenetReminderSVC.class);
    private static final long serialVersionUID = 1L;


    
    /**
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset queryTradeList(IData inData) throws Exception
    {
    	String serialNumber = inData.getString("SERIAL_NUMBER");
        
        //支持前台输入KD_也能查询到
        if (!"KD_".equals(serialNumber.substring(0, 3)))
        {
            serialNumber = "KD_" + serialNumber;
        }
        
        String tradeTypeCode = "600";
        IDataset tradeInfos = WidenetTradeQuery.queryUserCancelTrade(serialNumber, tradeTypeCode);
        
        return tradeInfos;
    }
    
    /**
     * 调用pboss催单接口
     * @param inData
     * @throws Exception
     */
    public void submitTrade(IData inData) throws Exception
    {
    	IDataset tradeList = new DatasetList(inData.getString("TRADE_LIST"));
        if (IDataUtil.isNotEmpty(tradeList))
        {
            for (int i = 0; i < tradeList.size(); i++)
            {
            	IData trade = tradeList.getData(i);
            	
            	IData inParam =  new DataMap();
            	
            	inParam.put("SUBSCRIBE_ID", trade.getString("TRADE_ID"));
            	inParam.put("TRADE_STAFF_ID", getVisit().getStaffId());
            	
            	//调用PBOSS催单接口
            	IDataOutput dataOutput = CSAppCall.callNGPf("PBOSS_ORDER_REMINDERS", inParam);
            	
            	IData head = dataOutput.getHead();//服开返回报文头
                String resultCode = head.getString("X_RESULTCODE", "-1");
                
                if (!"0".equals(resultCode))
                {
                    String resultInfo = head.getString("X_RESULTINFO");
                    CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
                }
                
                IDataset result = dataOutput.getData();
                
                if (IDataUtil.isNotEmpty(result))
                {
                    IData tmpData = result.getData(0);
                    String xResultCode = tmpData.getString("X_RESULTTYPE", "-1");
                    
                    if (!"0".equals(xResultCode))
                    {
                    	String resultInfo = tmpData.getString("X_RESULTINFO");
                    	CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
                    }
                }
            }
        }
    }
}
