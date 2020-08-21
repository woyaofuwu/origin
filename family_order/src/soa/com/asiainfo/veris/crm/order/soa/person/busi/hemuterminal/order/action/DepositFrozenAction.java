package com.asiainfo.veris.crm.order.soa.person.busi.hemuterminal.order.action;  

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

/**
 * 和商务用户退机解冻押金
 * @author Administrator
 *
 */
public class DepositFrozenAction implements ITradeFinishAction
{
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	 String actionType = mainTrade.getString("RSRV_STR4");//1:换，2：退，3：领
    	 String isHSW = mainTrade.getString("RSRV_STR2");//是否为和商务1：是，0：不是
    	 String depositTradeId = mainTrade.getString("RSRV_STR6");//押金转移流水
    	 String serialNumber = mainTrade.getString("RSRV_STR3");//手机号码
    	 if ("2".equals(actionType)&&"1".equals(isHSW))
         {
             //客户确认不开通魔百和,调用账务的接口进行押金返回
             IData param = new DataMap();
             param.put("OUTER_TRADE_ID", depositTradeId);
             param.put("SERIAL_NUMBER", serialNumber);
             param.put("TRADE_FEE", "10000");
             param.put("CHANNEL_ID", "15000");
             param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
             param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
             param.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
             param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
             param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

             AcctCall.transFeeOutADSL(param);

         }
    	
    }
}
