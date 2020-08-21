
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.undofinish;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class UndoTopSetBoxDepositAction implements ITradeFinishAction
{
	/**
	 * 宽带撤单魔百和押金处理
	 */
    public void executeAction(IData mainTrade) throws Exception
    {
        //魔百和押金转移流水ID
        String topSetBoxDepositTradeId = mainTrade.getString("RSRV_STR4");
        
        //判断用户是否已经成功付费
        boolean isPayCost = false;
        
        //非先装后付模的 默认已经付费成功
        if (!"A".equals(mainTrade.getString("RSRV_STR1")))
        {
        	isPayCost = true;
        }
        else
        {
        	if ("Y".equals(mainTrade.getString("RSRV_STR8")))
            {
            	isPayCost = true;
            }
        }
        
        if (StringUtils.isNotBlank(topSetBoxDepositTradeId))
        {
            //宽带撤单魔百和押金处理
        	IData param = new DataMap();
        	
        	String serialNumber = mainTrade.getString("SERIAL_NUMBER","");
        	
        	serialNumber = serialNumber.substring(3, serialNumber.length());
        	
        	param.put("OUTER_TRADE_ID", topSetBoxDepositTradeId);
        	param.put("SERIAL_NUMBER", serialNumber);
        	param.put("TRADE_FEE", 0);
        	param.put("CHANNEL_ID", "15000");
            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
            param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            
            //取TF_B_TRADE_OTHER RSRV_VALUE_CODE = TOPSETBOX
            String tradeId = mainTrade.getString("TRADE_ID");
            IDataset tradeOtherInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,"TOPSETBOX");
            if(IDataUtil.isNotEmpty(tradeOtherInfos))
            {
            	param.put("TRADE_FEE", tradeOtherInfos.getData(0).getString("RSRV_STR6","0"));
            	//预留字段RSRV_TAG1如果为N表示服务开通那边已经取消魔百和开通了。完工不需要再调账务接口转账
            	if("N".equals(tradeOtherInfos.getData(0).getString("RSRV_TAG1","0"))){
            		return;
            	}
            }
            
            //支付了费用才需要调用账务接口退费
            if (isPayCost)
            {
            	AcctCall.transFeeOutADSL(param);
            }
        }
    }
}
