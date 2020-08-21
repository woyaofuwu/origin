
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweAsynBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;

public class TradeFinishCallEOSNewDataAction implements ITradeFinishAction
{
	 private static  Logger logger = Logger.getLogger(TradeFinishCallEOSNewDataAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {
    	logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程NewDataACTION>>>>>>>>>>>>>>>>>>");
        String tradeId = mainTrade.getString("TRADE_ID");
//    	System.out.println("TradeFinishCallEOSNewDataAction-executeAction tradeId:"+tradeId);

//    	System.out.println("TradeFinishCallEOSNewDataAction-executeAction mainTrade:"+mainTrade);

        logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程NewDataACTION--TRADEID>>>>"+tradeId+">>>>>>>>>>>>>>");
  
        IDataset extTrades = TradeExtInfoQry.getTradeEsopInfoTradeId(tradeId);
        if (IDataUtil.isEmpty(extTrades))
            return;

        IData extTrade = extTrades.getData(0);
        String eosTag = extTrade.getString("RSRV_STR10");
        String newFlag =  extTrade.getString("RSRV_STR5");
        String dataFlag =  extTrade.getString("RSRV_STR7");
        

        if (!"EOS".equals(eosTag))
            return;

        if(!"NEWFLAG".equals(newFlag))
        	return;
        if(!"DATAHANGE".equals(dataFlag))
        	return;
        
        
        //集团专线的特殊处理,开户变更,改由pboss调用esop接口
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");

		if("3018".equals(tradeTypeCode)|| "3088".equals(tradeTypeCode) || "2991".equals(tradeTypeCode)|| "3850".equals(tradeTypeCode)){
			
			String orderId  =  mainTrade.getString("ORDER_ID");
        	IDataset orderList = TradeInfoQry.queryTradeByOrerId(orderId,"0");
//    		System.out.println("TradeFinishCallEOSNewDataAction-orderList:"+orderList.size()+"! tradeId:"+tradeId);
        	if(orderList.size() > 1){
        		return;
        	}
//        	System.out.println("TradeFinishCallEOSNewDataAction-准备执行驱动流程!");

			IData data = new DataMap();

        	data.put("IBSYSID", extTrade.getString("ATTR_VALUE"));
        	//data.put("SUB_IBSYSID", result.getString("SUB_IBSYSID"));
        	data.put("BUSIFORM_ID", extTrade.getString("RSRV_STR4"));
        	data.put("NODE_ID", extTrade.getString("RSRV_STR1"));
        	data.put("RECORD_NUM", extTrade.getString("RSRV_STR6"));
        	logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务WorkformDriveSVC>>>>>>>>>>>>>>>>>"+data.toString());

            //CSAppCall.call("SS.WorkformDriveSVC.execute", data);
        	EweAsynBean.saveAsynInfo(data);
        	
//        	//完工保存checkinWorkSheet数据
//        	CSAppCall.call("SS.WorkformCheckinSVC.record", data);	
		}
		
		
    }
       

}
