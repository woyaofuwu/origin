package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.undofinish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class UndoDestroyTopSetBoxAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		// TODO Auto-generated method stub
		String tradeId = mainTrade.getString("TRADE_ID");
    	IDataset topSetBoxInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "TOPSETBOX");   
    	if(IDataUtil.isNotEmpty(topSetBoxInfos))
    	{
    		IData topSetBox = topSetBoxInfos.getData(0);
    		String rsrvTag1 = topSetBox.getString("RSRV_TAG1","");
    		if("Y".equals(rsrvTag1))
    		{
    			//此处需要修改，修改为判断魔百和是否已完工，如已完工则走退订流程，如未完工则需要走撤单流程
    			String topBoxOpenTradeId = topSetBox.getString("RSRV_STR18","");
    			if(topBoxOpenTradeId != null && !"".equals(topBoxOpenTradeId))
    			{
    				//查询是否已经完工
    				StringBuilder sql = new StringBuilder();
    				sql.append("SELECT * FROM TF_B_TRADE WHERE TRADE_ID = :TRADE_ID");
    				IData param = new DataMap();
    				param.put("TRADE_ID", topBoxOpenTradeId);
    				
    				IDataset tradeInfos = Dao.qryBySql(sql, param);
    				if(IDataUtil.isNotEmpty(tradeInfos))
    				{
    					//未完工，不处理，不能进行宽带开户撤单
    					
    				}
    				else
    				{
    					//已完工
    					IData data = new DataMap();
    	    	        data.put("SERIAL_NUMBER", topSetBox.getString("RSRV_STR1"));
    	    	        
    	    	        //标识为宽带开户撤单调用的
    	    	        data.put("IS_MERGE_WIDE_CANCEL", "1");
    	    	        
    	    	        //标识为进行退机顶盒
                        data.put("IS_RETURN_TOPSETBOX", "1");
    	    	        
    	    	        data.put(Route.ROUTE_EPARCHY_CODE, mainTrade.getString("TRADE_EPARCHY_CODE"));
    	    	        IDataset dataset = CSAppCall.call("SS.DestroyTopSetBoxRegSVC.tradeReg", data);
    	    	        
    	    	        IData otherData = new DataMap();
    	    	        otherData.put("RSRV_TAG1", "C");
    	    	        otherData.put("RSRV_TAG2", "1");  //RSRV_TAG2 = 1 已撤单
    	    	        otherData.put("RSRV_STR19", dataset.getData(0).getString("ORDER_ID","")); //退订魔百和订单ID
    	    	        otherData.put("RSRV_STR20", dataset.getData(0).getString("TRADE_ID","")); //退订魔百和台账ID
    	    	        otherData.put("RSRV_VALUE_CODE", "TOPSETBOX");
    	    	        otherData.put("TRADE_ID", tradeId);
    	    	        //更新TF_B_TRADE_OHTER表，标记魔百和已撤单
    	    	        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TOPSETBOX_CANCELTAG", otherData);
    				}
    			}
    		}
    	}
	}
}
