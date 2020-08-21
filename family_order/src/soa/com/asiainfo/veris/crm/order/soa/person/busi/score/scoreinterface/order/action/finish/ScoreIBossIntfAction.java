
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;

//合版本 新增action duhj 2017/5/2
/**
 * REQ201702080017_关于积分业务的若干优化需求
 * <br/>
 * 在积分扣减、积分回退、积分充值、积分冲正CRM成功登记台账时，
 * 新增记录数据到中心库表：ucr_cen1.TL_B_TRADE_SCORE
 * @author zhuoyingzhi
 * @date 20170308
 *
 */
public class ScoreIBossIntfAction implements ITradeFinishAction
{
	
    public static Logger logger=Logger.getLogger(ScoreIBossIntfAction.class);

	
    public void executeAction(IData mainTrade) throws Exception
    {
    	
        IDataset tradeScoreInfo = TradeScoreInfoQry.queryTradeScoreByTradeId(mainTrade.getString("TRADE_ID",""));
        String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE","");
        

        if("329".equals(tradeTypeCode)){
        	//积分扣减、积分回退、积分充值、积分冲正
            if(IDataUtil.isNotEmpty(tradeScoreInfo)){
            	String rsrvStr5=tradeScoreInfo.getData(0).getString("RSRV_STR5","");
            	/**
            	 * 1.积分回退   BIP5A034
            	 * 2.积分冲正   BIP5A036
            	 * 3.积分扣减   BIP5A056
            	 * 4.积分充值   BIP5A032
            	 */
            	if("BIP5A034".equals(rsrvStr5)||
            			"BIP5A036".equals(rsrvStr5)||
            			"BIP5A056".equals(rsrvStr5)||
            			"BIP5A032".equals(rsrvStr5)){
            		//记录TL_B_TRADE_SCORE信息
	           		 IData param = new DataMap(mainTrade);
	           		 	   param.put("STATUS", "0");
	           		 	   param.put("RSRV_STR10", mainTrade.getString("ACCEPT_DATE",""));
	        		 Dao.executeUpdateByCodeCode("TL_B_TRADE_SCORE", "INSERT_TL_TRADE_SCORE_BY_TRADE_ID", param,Route.CONN_CRM_CEN);
	        		 
            	}
            }        	
        	
        }else if("340".equals(tradeTypeCode)){
        	//积分转赠
        	String rsrvStr4=mainTrade.getString("RSRV_STR4","");
        	/**
        	 * 1.积分转赠   BIP5A053
        	 */
        	if("BIP5A053".equals(rsrvStr4)){
        		//记录TL_B_TRADE_SCORE信息
        		IData param4=new DataMap(mainTrade);
    		 	   param4.put("STATUS", "0");
       		 	   param4.put("RSRV_STR10", mainTrade.getString("ACCEPT_DATE",""));
       		 	   
       		 	   //RSRV_STR8读取TF_B_TRADE_SCORE里面的RSRV_STR8
       		 	   if(IDataUtil.isNotEmpty(tradeScoreInfo)){
       		 		   param4.put("RSRV_STR8", tradeScoreInfo.getData(0).getString("RSRV_STR8",""));
       		 	   }else{
       		 		   param4.put("RSRV_STR8", "");
       		 	   }
       		 	   Dao.executeUpdateByCodeCode("TL_B_TRADE_SCORE", "INSERT_TL_TRADE_SCORE_BY_TRADE_ID", param4,Route.CONN_CRM_CEN);
        	}
        }
    }
}
