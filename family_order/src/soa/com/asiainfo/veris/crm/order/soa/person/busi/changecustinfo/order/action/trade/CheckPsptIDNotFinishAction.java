package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;

public class CheckPsptIDNotFinishAction implements ITradeAction
{
    private static Logger logger = Logger.getLogger(CheckPsptIDNotFinishAction.class);

    public void executeAction(BusiTradeData btd) throws Exception
    {
            List<CustomerTradeData> customerInfos = btd.get("TF_B_TRADE_CUSTOMER");
            List<CustPersonTradeData> custpersonInfos = btd.get("TF_B_TRADE_CUST_PERSON");

            String month = SysDateMgr.getCurMonth();

            if (customerInfos != null && customerInfos.size() > 0) {
                String psptId = customerInfos.get(0).getPsptId();
                if (null != psptId && !"".equals(psptId))
                {
                	IDataset tradelist = TradeCustomerInfoQry.getTradeByPsptId(psptId, month);
                	if(IDataUtil.isNotEmpty(tradelist) && tradelist.size()>0){
                		CSAppException.apperr(CrmCommException.CRM_COMM_103, "证件号码存在未完工工单不允许提交！");
                	}
                }
            }

            if (custpersonInfos != null && custpersonInfos.size() > 0) {
                String userPsptId = custpersonInfos.get(0).getRsrvStr7();
            	if (null != userPsptId && !"".equals(userPsptId))
                {
            		IDataset tradelist = TradeCustomerInfoQry.getTradeByPsptId(userPsptId, month);
	            	if(IDataUtil.isNotEmpty(tradelist) && tradelist.size()>0){
	            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "证件号码存在未完工工单不允许提交！");
	            	}
                }
            }

    }
}
