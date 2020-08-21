
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class EspProductOrderStateSynBackSVC extends GroupOrderService
{   
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void synResultBack(IData idata) throws Exception
	 {
		String tradeId = IDataUtil.chkParam(idata, "OrderNumber");
		String resultCode = IDataUtil.chkParam(idata, "ResultCode");

		IData hisTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", idata.getString("TRADE_EPARCHY_CODE"));
        if (IDataUtil.isEmpty(hisTradeInfos))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_70, tradeId);// 获取台帐历史表资料:没有该笔业务!%s
        }
        String producttradeId = hisTradeInfos.getString("RSRV_STR5");
        IData prohisTradeInfo = UTradeHisInfoQry.qryTradeHisByPk(producttradeId, "0", idata.getString("TRADE_EPARCHY_CODE"));
        if (IDataUtil.isEmpty(prohisTradeInfo))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_70, tradeId);// 获取台帐历史表资料:没有该笔业务!%s
        }
        EspProductOrderStateSynBackBean bean = new EspProductOrderStateSynBackBean();
		if("1".equals(resultCode)){
			bean.updateSuccessOrder(prohisTradeInfo);
		}else{
			if (!StringUtils.equals("0", prohisTradeInfo.getString("CANCEL_TAG")))
	        {
	            StringBuilder msg = new StringBuilder(50);
	            msg.append("订单[").append(tradeId);
	            msg.append("]已经被返销，不能再次返销！");
	            CSAppException.apperr(TradeException.CRM_TRADE_95, msg.toString());// 获取台帐历史表资料:没有该笔业务!%s
	        }
	        IData pubData = this.getPublicData(prohisTradeInfo);
	        pubData.put("LOGIN_EPARCHY_CODE", idata.getString("TRADE_EPARCHY_CODE"));
	        String newOrderId = bean.createCancelOrder(prohisTradeInfo, pubData);
	        bean.createCancelTrade(newOrderId, prohisTradeInfo, pubData);
		}
	 }
	
	/**
     * 获取一些公共信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    private IData getPublicData(IData hisTrade) throws Exception
    {
        IData pubData = new DataMap();
        pubData.put("CANCEL_TAG", "2");// 2=被返销
        pubData.put("ORDER_ID", hisTrade.getString("ORDER_ID", ""));
        pubData.put("TRADE_ID", hisTrade.getString("TRADE_ID", ""));
        pubData.put("SYS_TIME", SysDateMgr.getSysTime());
        pubData.put("STAFF_ID", "");
        pubData.put("DEPART_ID", "");
        pubData.put("CITY_CODE", "");
        return pubData;
    }
}