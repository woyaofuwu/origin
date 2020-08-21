package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;

/**
 * PBOSS回单之后，根据PBOSS回单时间，修改产品生效时间
 */
public class DealWidenetTime implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeEparchyCode = mainTrade.getString("TRADE_EPARCHY_CODE");
        String finishDate = "";
        IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(tradeId);
        if (IDataUtil.isEmpty(finishInfos))
        {
            return ;
        }
        
        finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
        if (StringUtils.isBlank(finishDate))
        {
        	return ;
        }
        
    	IData param = new DataMap();
    	param.put("START_DATE",finishDate);
    	param.put("TRADE_ID",tradeId);
        StringBuilder buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_WIDENET ");
        buf.append(" SET START_DATE = TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        buf.append(" WHERE TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        buf.append(" AND MODIFY_TAG = '0' ");
        Dao.executeUpdate(buf, param, Route.getJourDb(tradeEparchyCode));

    	param.put("END_DATE",finishDate);
    	param.put("TRADE_ID",tradeId);
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_WIDENET ");
        buf.append(" SET END_DATE = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        buf.append(" WHERE TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        buf.append(" AND MODIFY_TAG = '1' ");
        Dao.executeUpdate(buf, param, Route.getJourDb(tradeEparchyCode));
    }

}
