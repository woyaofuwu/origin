
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;

public class PreliminaryAcceptSaleactiveFinishAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
    	String tradeId = mainTrade.getString("TRADE_ID","");
    	String sn = mainTrade.getString("SERIAL_NUMBER","");
    	IDataset ds = UserSaleGoodsInfoQry.getByRelationTradeId(tradeId);
		if(IDataUtil.isNotEmpty(ds))
		{
			IData data = ds.getData(0);
			data.put("RES_NO", data.getString("RES_CODE",""));
    		data.put("SERIAL_NUMBER", sn);
    		//释放终端预占
    		IDataset retDataset =HwTerminalCall.releaseResTempOccupy(data);
    		
		}
    }

}
