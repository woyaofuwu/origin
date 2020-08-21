
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.reg;


import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * 魔百和押金处理
 * @author zyc
 *
 */
public class DealTopSetBoxDepositAction implements ITradeAction
{
	protected static Logger log = Logger.getLogger(DealTopSetBoxDepositAction.class);
	
	/**
	 * 魔百和押金处理
	 */
    public void executeAction(BusiTradeData btd) throws Exception
    {
        MergeWideUserCreateRequestData mergeWideUserCreateRd = ((MergeWideUserCreateRequestData)btd.getRD());
    	
    	int topSetBoxDeposit = mergeWideUserCreateRd.getTopSetBoxDeposit();
    	
    	if (topSetBoxDeposit > 0)
    	{
    	    //新生成一个TradeId,魔百和转押金时调用此传入此tradeId，可以多单独返销
            btd.getMainTradeData().setRsrvStr4(SeqMgr.getTradeId());
    	    
    	    //调用账务的接口进行押金返回
            IData params=new DataMap(); 
            
            params.put("OUTER_TRADE_ID", btd.getMainTradeData().getRsrvStr4());
            params.put("SERIAL_NUMBER", mergeWideUserCreateRd.getNormalSerialNumber());
            params.put("DEPOSIT_CODE_OUT", WideNetUtil.getOutDepositCode());
            params.put("DEPOSIT_CODE_IN", "9016");
            params.put("TRADE_FEE", topSetBoxDeposit);
            params.put("CHANNEL_ID", "15000");
            params.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            params.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
            params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            
            //调用接口，将【现金类】——>【押金】
            IData resultData = AcctCall.transFeeInADSL(params);
            
            if(log.isDebugEnabled())
            {
            	log.debug("调用AM_CRM_TransFeeInADSL 接口返回结果:" + resultData);
            }
            
            String result=resultData.getString("RESULT_CODE","");
            
            if("".equals(result) || !"0".equals(result))
            {
                CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + params + "错误:" + resultData.getString("RESULT_INFO"));
            }
            
            
    	}
    }
}
