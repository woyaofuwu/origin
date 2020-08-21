
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;

/**
 * PBOSS回单之后，根据PBOSS回单时间，修改老光猫的90天计算时间和新光猫的生效时间
 * 
 * @author yuyj3
 */
public class UpdWideModelTimeAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        
        String finishDate = SysDateMgr.getSysTime();
        IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(tradeId);
        if (IDataUtil.isEmpty(finishInfos))
        {
        	CSAppException.apperr(WidenetException.CRM_WIDENET_14);
        }else{
        	finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
        }
        
        IDataset modemInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "FTTH"); 
    	if(!IDataUtil.isNotEmpty(modemInfos)){
    		modemInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "FTTH_GROUP"); 
    	}
    	
    	if(IDataUtil.isNotEmpty(modemInfos))
    	{
    		IData modem = modemInfos.getData(0);
			StringBuilder buf = new StringBuilder();
    		for(int i=0;i<modemInfos.size();i++){
				modem = modemInfos.getData(i);
    			if("0".equals(modem.getString("MODIFY_TAG",""))&&"0".equals(modem.getString("RSRV_TAG1",""))){
    		    	IData param = new DataMap();
    				param.put("START_DATE",finishDate);
    	        	param.put("TRADE_ID",tradeId);
    	        	buf = new StringBuilder();
    	            buf.append(" UPDATE TF_B_TRADE_OTHER T ");
    	            buf.append(" SET T.START_DATE=to_date(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
    	            buf.append(" WHERE T.MODIFY_TAG='0' ");
    	            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    	            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    	            Dao.executeUpdate(buf, param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    			}else if(("2".equals(modem.getString("MODIFY_TAG",""))||"1".equals(modem.getString("MODIFY_TAG","")))&&("0".equals(modem.getString("RSRV_TAG1","")))){
    				IData param = new DataMap();
    				param.put("RSRV_DATE1",finishDate);
    	        	param.put("TRADE_ID",tradeId);
    	        	buf = new StringBuilder();
    	            buf.append(" UPDATE TF_B_TRADE_OTHER T ");
    	            buf.append(" SET T.RSRV_DATE1=to_date(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') ");
    	            buf.append(" WHERE T.MODIFY_TAG <> '0' ");
    	            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    	            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    	            Dao.executeUpdate(buf, param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    			}
    		}
    	}
        
    }
}
