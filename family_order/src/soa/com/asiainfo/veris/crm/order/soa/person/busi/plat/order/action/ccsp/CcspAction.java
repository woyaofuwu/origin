package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.ccsp;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class CcspAction implements IProductModuleAction {

	@Override
	public void executeProductModuleAction(ProductModuleTradeData dealPmtd,
			UcaData uca, BusiTradeData btd) throws Exception {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        List<AttrTradeData> attrTrade = pstd.getAttrTradeDatas();
        for(int i = 0 ; null != attrTrade && attrTrade.size() > i ; i++){
	        AttrTradeData attr = attrTrade.get(i);
        	if ("7901".equals(attr.getAttrCode())){
				pstd.setRsrvStr8(attr.getAttrValue());
			}
        }
	}
}
