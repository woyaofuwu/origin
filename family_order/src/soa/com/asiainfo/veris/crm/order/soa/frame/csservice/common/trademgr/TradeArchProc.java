package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trademgr;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch.ArchProcProxy;
import com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch.IArchProc;

public final class TradeArchProc {
	private static final String defaultIntfId = "TF_B_TRADE_INTEGRALACCT,TF_B_TRADE_INTEGRALPLAN,TF_B_TRADE_SCORERELATION,TF_B_TRADE_RATE,TF_B_TRADE_ADDR,TF_B_TRADE_ACCESS_ACCT,TF_B_TRADE_WIDENET,TF_B_TRADE_SVC,TF_B_TRADE_ACCT_CONSIGN,TF_B_TRADE_ACCOUNT,TF_B_TRADE_DISCNT,TF_B_TRADE_CUSTOMER,TF_B_TRADE_CUST_PERSON,TF_B_TRADE_RELATION_AA,TF_B_TRADE_RELATION,TF_B_TRADE_RELATION_BB,TF_B_TRADE_PAYRELATION,TF_B_TRADE_USER,TF_B_TRADE_ATTR,TF_B_TRADE_BLACKWHITE,TF_B_TRADE_GRP_MEB_PLATSVC,TF_B_TRADE_GRP_MERCH,TF_B_TRADE_GRP_MERCHP,TF_B_TRADE_GRP_MERCHP_DISCNT,TF_B_TRADE_GRP_MERCH_DISCNT,TF_B_TRADE_GRP_MERCH_MEB,TF_B_TRADE_GRP_MOLIST,TF_B_TRADE_GRP_PACKAGE,TF_B_TRADE_GRP_PLATSVC,TF_B_TRADE_IMPU,TF_B_TRADE_OTHER,TF_B_TRADE_USER_PAYITEM,TF_B_TRADE_USER_PAYPLAN,TF_B_TRADE_PRODUCT,TF_B_TRADE_RES,TF_B_TRADE_SALE_ACTIVE,TF_B_TRADE_SALE_DEPOSIT,TF_B_TRADE_SALE_GOODS,TF_B_TRADE_SVCSTATE,TF_B_TRADE_PLATSVC,TF_B_TRADE_VPN,TF_B_TRADE_VPN_MEB,TF_B_TRADE_RENT,TF_B_TRADE_POST,TF_B_TRADEFEE_SUB,TF_B_TRADE_USER_ACCTDAY,TF_B_TRADE_ACCOUNT_ACCTDAY,TF_B_TRADE_OCS,TF_B_TRADE_BANK_MAINSIGN,TF_B_TRADE_BANK_SUBSIGN,TF_B_TRADE_SHARE,TF_B_TRADE_SHARE_INFO,TF_B_TRADE_SHARE_RELA,TF_B_TRADE_CUST_FAMILY,TF_B_TRADE_CUST_FAMILYMEB,TF_B_TRADE_TELEPHONE,TF_B_TRADE_RELATION_XXT,TF_B_TRADE_USER_SPECIALEPAY,TF_B_TRADE_ACCT_DISCNT,TF_B_TRADE_WIDENET_ACT,TF_B_TRADE_GRP_CENPAY,TF_B_TRADE_MEB_CENPAY,TF_B_TRADE_GRP_MERCH_MB_DIS,TF_B_TRADE_BFAS_IN,TF_B_TRADE_RESOURCE_INTER,TF_B_TRADE_USER_DATALINE,TF_B_TRADE_SALEACTIVE_BOOK,TF_B_TRADE_DATAPCK,TF_B_TRADE_MEB_OUTSTOCK,TF_B_TRADE_OFFER_REL,TF_B_TRADE_PRICE_PLAN";//默认的intfId

	//归档的存储过程转java实现
	public static void executeProc(String tradeId,String acceptMonth,String cancelTag)throws Exception{
		IDataset tradeInfo = BofQuery.qryTradeInfo(tradeId, acceptMonth, cancelTag);
		if(IDataUtil.isEmpty(tradeInfo)){
			//抛异常
			CSAppException.apperr(TradeException.CRM_TRADE_335,tradeId,acceptMonth,cancelTag);
		}
		String intfId = tradeInfo.first().getString("INTF_ID",defaultIntfId);

		String[] intfIdArr = intfId.split(",");
		
		for(String tabName:intfIdArr){
			IArchProc archProc = ArchProcProxy.getInstance(tabName);
			
			archProc.arch(tabName, tradeId, acceptMonth,cancelTag);
		}
	}
}
