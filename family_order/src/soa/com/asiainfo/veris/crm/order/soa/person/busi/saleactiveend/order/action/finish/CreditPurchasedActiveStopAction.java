
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
/**
 * REQ202001030026关于和包信用购机无法提前结清的优化需求
 * 调用IBOSS信用购机退货接口
 * @author chenyw7
 *
 */
public class CreditPurchasedActiveStopAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
    	String serialNumber=mainTrade.getString("SERIAL_NUMBER","");
        IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));

        
        if((IDataUtil.isNotEmpty(tradeSaleActive)) && tradeSaleActive.size()>0)
    	{
        	
        	//查询tf_b_trade_other信用购机记录
            String relationTradeId = tradeSaleActive.getData(0).getString("RELATION_TRADE_ID");
            if(StringUtils.isNotEmpty(relationTradeId)){
            	IDataset otherDatas = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(relationTradeId, "CREDIT_PURCHASES");
            	if(DataUtils.isNotEmpty(otherDatas)){
            		String seq = otherDatas.getData(0).getString("RSRV_STR9");//比较seq
        			String mplOrdNo = otherDatas.getData(0).getString("RSRV_STR1");
        			String mplOrdDt = otherDatas.getData(0).getString("RSRV_STR2");
        			
        			int tradeId1 = Integer.valueOf(relationTradeId.substring(relationTradeId.length()-6))+1;
        	   	 	String rejSeq = "898"+"BIP2B191"+ SysDateMgr.getSysDateYYYYMMDDHHMMSS()+tradeId1;
        	   	 	
        			IData  inparam = new DataMap();
        	   	 	inparam.put("procTyp", "2");//信用购机  退货
        	   	 	inparam.put("cusMblNo", serialNumber);
        	   	 	inparam.put("ExSeq", seq);
        	   	 	inparam.put("mplOrdNo", mplOrdNo);
        	   	 	inparam.put("mplOrdDt", mplOrdDt);
        	   	 	inparam.put("REJ_SEQ", rejSeq);
        	   	 	
        	   	 	if(StringUtils.isNotEmpty(serialNumber) && StringUtils.isNotEmpty(seq) && StringUtils.isNotEmpty(mplOrdNo) && StringUtils.isNotEmpty(mplOrdDt)  && StringUtils.isNotEmpty(rejSeq)){
        	   	 		
	        	   	 	IDataset cancelReqInfos = IBossCall.getCancelRequestInfo(inparam);
	        	   	 	if(DataUtils.isNotEmpty(cancelReqInfos)){
		        	   	 	IData cancelReqInfo = cancelReqInfos.getData(0);
		        	   	 	IDataset rspInfo = cancelReqInfo.getDataset("REG_REJ_RSP"); 
		        	   	 	if(DataUtils.isNotEmpty(rspInfo)){
		        	   	 		
			        	   	 	IData REG_REJ_RSP= rspInfo.first();
			    				String RspCode=REG_REJ_RSP.getString("RSP_CODE");
			    				if("0000".equals(RspCode)){
			    					RspCode="成功";
			    					IData param=new DataMap();
			    					param.put("RSRV_STR10", inparam.getString("procTyp"));
			    					param.put("RSRV_STR11", inparam.getString("REJ_SEQ"));
			    					param.put("TRADE_ID", relationTradeId);
			    					param.put("RSRV_VALUE_CODE", "CREDIT_PURCHASES");
			    					Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER","UPD_RSRV_STR10_11_BY_TRADE_ID", param);
			    				}else{
			    					CSAppException.apperr(CrmCommException.CRM_COMM_103, "信用购机退货接口调用失败");
			    				}
		        	   	 		
		        	   	 	}else{
		        	   	 		CSAppException.apperr(CrmCommException.CRM_COMM_103, "信用购机退货接口调用失败");
		        	   	 	}
	        	   	 	}else{
	        	   	 		CSAppException.apperr(CrmCommException.CRM_COMM_103, "信用购机退货接口调用失败");
	        	   	 	}
	    				
        	   	 	}
            	}
            }
        	
    	}
        
		
	        

    }

}
