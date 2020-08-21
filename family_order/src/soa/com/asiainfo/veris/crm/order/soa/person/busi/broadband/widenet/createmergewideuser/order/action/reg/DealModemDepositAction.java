
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.reg;


import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * 光猫押金处理
 * @author zyc
 *
 */
public class DealModemDepositAction implements ITradeAction
{
	protected static Logger log = Logger.getLogger(DealModemDepositAction.class);
    /**
     * 光猫押金处理
     * 
     * @author 
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
        MergeWideUserCreateRequestData mergeWideUserCreateRd = ((MergeWideUserCreateRequestData)btd.getRD());
    	
    	int modemDeposit = mergeWideUserCreateRd.getModemDeposit();
    	
    	if (modemDeposit > 0 )
    	{
    	    List<OtherTradeData> otherTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
    	    
    	    if(otherTradeDatas != null && otherTradeDatas.size() > 0)
    	    {
    	    	for(int i = 0 ; i < otherTradeDatas.size() ; i++)
    	    	{
    	    		OtherTradeData otherTradeData = otherTradeDatas.get(i);
    	    		
    	    		String rsrvValueCode = otherTradeData.getRsrvValueCode() ;
    	    		//光猫押金
    	    		if(rsrvValueCode != null && rsrvValueCode.equals("FTTH"))
    	    		{
    	    			String rsrvTag1 = otherTradeData.getRsrvTag1();
    	    			//只有租赁的时候有押金
    	    			if(rsrvTag1 != null && "0".equals(rsrvTag1))
    	    			{
    	    				String tradeId = SeqMgr.getTradeId();
        	    			otherTradeData.setRsrvStr8(tradeId); //账务扣减押金的流水
        	    			IData inparams=new DataMap();
        	    			inparams.put("SERIAL_NUMBER", mergeWideUserCreateRd.getNormalSerialNumber());
    	    	            inparams.put("OUTER_TRADE_ID", tradeId); //光猫扣押金的流水ID，退还时需要此流水
    	    	            inparams.put("DEPOSIT_CODE_OUT", WideNetUtil.getOutDepositCode());
    	    	            inparams.put("DEPOSIT_CODE_IN", "9002"); //9002 光猫押金转入存折
    	    	            inparams.put("TRADE_FEE", modemDeposit);
    	    	            inparams.put("CHANNEL_ID", "15000");
    	    	            inparams.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    	    	            inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	    	            inparams.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
    	    	            inparams.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    	    	            inparams.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	    	            
    	    	            IData inAcct = AcctCall.transFeeInADSL(inparams);
    	    	            
    	    	            if(log.isDebugEnabled())
    	    	            {
    	    	            	log.debug("调用AM_CRM_TransFeeInADSL 接口返回结果:" + inAcct);
    	    	            }
    	    	            
    	    	            String result=inAcct.getString("RESULT_CODE","");
    	    	            
    	    	            if("".equals(result) || !"0".equals(result))
    	    	            {
    	    	                CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + inparams + "错误:" + inAcct.getString("RESULT_INFO"));
    	    	            }
    	    			}
    	    		}
    	    	}
    	    }
    	}
    }
}
