package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.action.undofinish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.intelligentnk.IntelligentNetworKingBean;

public class UndoIntelligentNetworkKing implements ITradeFinishAction{
	@Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	String newListNo = mainTrade.getString("RSRV_STR4");
    	
    	if(StringUtils.isNotBlank(newListNo))
    	{
    		IData input = new DataMap();
        	input.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
        	IntelligentNetworKingBean bean = new IntelligentNetworKingBean();
        	IDataset orderInfo = Dao.qryByCode("TF_B_INTELLIGENTNET", "SEL_BY_PK", input,Route.getJourDbDefault());
 
        	if(IDataUtil.isNotEmpty(orderInfo))
        	{
        		IData param = new DataMap();
            	param.put("OprNumb", orderInfo.getData(0).getString("OPER_NUMB"));
            	param.put("OprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            	param.put("BizType", orderInfo.getData(0).getString("BIZ_TYPE"));
            	param.put("newListNo", newListNo);
            	param.put("BizVersion", orderInfo.getData(0).getString("BIZ_VERSION")); 
            	bean.tdCancelSyn(param);
        	}
    	}
    	
    }
}
