package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

/**
 *5.27	IPHONE6裸机后合约办理完工
 */
public class Ihone6SaleSyncHwFinishAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
    	IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));
    	if (IDataUtil.isEmpty(tradeSaleActive)){
            return;
        }
    	String iphone6_imei=tradeSaleActive.first().getString("RSRV_STR22");//IPHOIONE6 IMEI卡号（该活动特别处理）
    	if(StringUtils.isBlank(iphone6_imei)){
    		return;
    	}
    	String serial_number=mainTrade.getString("SERIAL_NUMBER");
        String priv_id=tradeSaleActive.first().getString("PRODUCT_ID");
        String package_id=tradeSaleActive.first().getString("PACKAGE_ID");
    	IDataset sysResults = HwTerminalCall.iContractImeiDeal(iphone6_imei, serial_number, priv_id, package_id);
    	if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//华为测接口文档有误，0为成功，其他失败
    		String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
    		if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
    		}
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为IPHONE6裸机后合约办理完工接口失败");
    	}
    }
}