package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.action.undofinish;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeCancelFee;


public class CancelResAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		//宽带营销活动
		String sn = mainTrade.getString("SERIAL_NUMBER","");
		String saletradeId = mainTrade.getString("RSRV_STR5","");
		String usersn = mainTrade.getString("RSRV_STR1","");
		String tradeId = mainTrade.getString("TRADE_ID","");
		String mode = "TRADE_CANCEL";
		// 释放固话号码预占
		ResCall.releaseAllResByNo(sn, "0", tradeId + "订单取消", mode);
		
		if(StringUtils.isNotBlank(saletradeId))
		{
			IDataset ds = UserSaleGoodsInfoQry.getByRelationTradeId(saletradeId);
			if(IDataUtil.isNotEmpty(ds))
			{
				IData data = ds.getData(0);
				data.put("RES_NO", data.getString("RES_CODE",""));
	    		data.put("SERIAL_NUMBER", usersn);
	    		//释放终端预占
	    		IDataset retDataset =HwTerminalCall.releaseResTempOccupy(data);
	    		
	    		Dao.delete("TF_F_USER_SALE_GOODS", data);
			}
		}
		
	}
}

