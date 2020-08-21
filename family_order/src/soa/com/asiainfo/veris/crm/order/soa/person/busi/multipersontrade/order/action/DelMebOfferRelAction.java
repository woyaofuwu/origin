package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserCommUtil;

public class DelMebOfferRelAction implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐 
		System.out.println("discntTrades111===="+discntTrades);
        if (discntTrades != null && discntTrades.size() > 0) {
        	for (DiscntTradeData discntTrade : discntTrades) {
        		
        		IData offerRel = new DataMap();
				offerRel.put("OFFER_INST_ID", discntTrade.getInstId());
				offerRel.put("END_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				
				Dao.update("TF_F_USER_OFFER_REL", offerRel);
            }
        }
	}

}
