
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp.product;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class EspProductOrderStateSynBackBean extends CSBizBean
{   
	public String createCancelOrder(IData hisTradeData, IData pubData) throws Exception
    {
        IData newOrder = new DataMap();
        String newOrderId = SeqMgr.getOrderId();// 生成新的order_id
        newOrder.put("ORDER_ID", newOrderId);
        newOrder.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
        newOrder.put("ORDER_TYPE_CODE", hisTradeData.getString("TRADE_TYPE_CODE"));
        newOrder.put("TRADE_TYPE_CODE", hisTradeData.getString("TRADE_TYPE_CODE"));
        newOrder.put("PRIORITY", hisTradeData.getString("PRIORITY"));
        newOrder.put("ORDER_STATE", "0");
        newOrder.put("NEXT_DEAL_TAG", "0");
        newOrder.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        String custId = hisTradeData.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_105, hisTradeData.getString("SERIAL_NUMBER"));
        }
        newOrder.put("CUST_ID", custId);
        //newOrder.put("CUST_NAME", hisTradeData.getString("CUST_NAME"));
        newOrder.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE", ""));
        //newOrder.put("PSPT_ID", custInfo.getString("PSPT_ID", ""));
        newOrder.put("EPARCHY_CODE", hisTradeData.getString("EPARCHY_CODE"));
        newOrder.put("CITY_CODE", hisTradeData.getString("CITY_CODE"));

        newOrder.put("OPER_FEE", hisTradeData.getString("OPER_FEE"));
        newOrder.put("FOREGIFT", hisTradeData.getString("FOREGIFT"));
        newOrder.put("ADVANCE_PAY", hisTradeData.getString("ADVANCE_PAY"));
        newOrder.put("FEE_STATE", hisTradeData.getString("FEE_STATE"));

        newOrder.put("CANCEL_TAG", "2");
        newOrder.put("CANCEL_DATE", hisTradeData.getString("ACCEPT_DATE"));
        newOrder.put("CANCEL_STAFF_ID", hisTradeData.getString("TRADE_STAFF_ID"));
        newOrder.put("CANCEL_DEPART_ID", hisTradeData.getString("TRADE_DEPART_ID"));
        newOrder.put("CANCEL_CITY_CODE", hisTradeData.getString("TRADE_CITY_CODE"));
        newOrder.put("CANCEL_EPARCHY_CODE", hisTradeData.getString("TRADE_EPARCHY_CODE"));

        newOrder.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("FINISH_DATE", "");
        newOrder.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));

        newOrder.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newOrder.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));

        newOrder.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));
        newOrder.put("SUBSCRIBE_TYPE", "0");// 默认写个0吧
        Dao.insert("TF_B_ORDER", newOrder, Route.getJourDb(BizRoute.getRouteId()));
        return newOrderId;
    } 
	
	/**
     * 生成新订单数据
     * @throws Exception
     */
    public IData createCancelTrade(String newOrderId, IData hisTradeData, IData pubData) throws Exception
    {
        IData newTradeData = new DataMap();
        newTradeData.putAll(hisTradeData);

        /********* 费用 *******************************/
        long lOperFee = -hisTradeData.getLong("OPER_FEE", 0);
        long lAdvancePay = -hisTradeData.getLong("ADVANCE_PAY", 0);
        long lforegift = -hisTradeData.getLong("FOREGIFT", 0);
        String strFeeState = hisTradeData.getString("FEE_STATE","0");
        newTradeData.put("SUBSCRIBE_TYPE", hisTradeData.getString("SUBSCRIBE_TYPE"));
        newTradeData.put("OPER_FEE", String.valueOf(lOperFee));
        newTradeData.put("ADVANCE_PAY", String.valueOf(lAdvancePay));
        newTradeData.put("FOREGIFT", String.valueOf(lforegift));
        newTradeData.put("FEE_STATE", strFeeState);
        newTradeData.put("FEE_TIME", (strFeeState == "0") ? "" : pubData.getString("SYS_TIME"));
        newTradeData.put("FEE_STAFF_ID", (strFeeState == "0") ? "" : pubData.getString("STAFF_ID"));

        String subscribeType = hisTradeData.getString("SUBSCRIBE_TYPE");
        if (StringUtils.equals("97", subscribeType))
        {
            newTradeData.put("SUBSCRIBE_STATE", "1");
        }
        
        newTradeData.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
        
        newTradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        newTradeData.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newTradeData.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        newTradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        newTradeData.put("CANCEL_TAG", pubData.getString("CANCEL_TAG"));// 2=返销 3=取消
        newTradeData.put("CANCEL_DATE", hisTradeData.getString("ACCEPT_DATE"));
        newTradeData.put("CANCEL_STAFF_ID", hisTradeData.getString("TRADE_STAFF_ID"));
        newTradeData.put("CANCEL_DEPART_ID", hisTradeData.getString("TRADE_DEPART_ID"));
        newTradeData.put("CANCEL_CITY_CODE", hisTradeData.getString("TRADE_CITY_CODE"));
        newTradeData.put("CANCEL_EPARCHY_CODE", hisTradeData.getString("TRADE_EPARCHY_CODE"));
        // 以下字段取默认值
        newTradeData.put("BPM_ID", "");
        newTradeData.put("SUBSCRIBE_STATE", "0");
        newTradeData.put("NEXT_DEAL_TAG", "0");
        newTradeData.put("OLCOM_TAG", hisTradeData.getString("OLCOM_TAG"));
        newTradeData.put("FINISH_DATE", "");
        newTradeData.put("EXEC_ACTION", "");
        newTradeData.put("EXEC_RESULT", "");
        newTradeData.put("EXEC_DESC", "");
        newTradeData.put("PF_WAIT", hisTradeData.getString("PF_WAIT"));

        newTradeData.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newTradeData.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));

        newTradeData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        newTradeData.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());
        
        newTradeData.put("CHANNEL_TRADE_ID", "");
        newTradeData.put("CHANNEL_ACCEPT_TIME", "");
        newTradeData.put("CANCEL_TYPE_CODE", "");
        newTradeData.put("RSRV_TAG1", "");
        newTradeData.put("BOOK_DATE", hisTradeData.getString("BOOK_DATE", ""));

        newTradeData.put("ORDER_ID", newOrderId);// 新的订单号
		if (!Dao.insert("TF_B_TRADE", newTradeData, Route.getJourDbDefault()))
		{
			CSAppException.apperr(TradeException.CRM_TRADE_304, pubData.getString("TRADE_ID"));
		}

        return newTradeData;
    }
    
    public void updateSuccessOrder(IData hisTradeData) throws Exception
    {
    	IData param = new DataMap();
    	param.put("TRADE_ID", hisTradeData.getString("TRADE_ID"));
    	param.put("RSRV_STR3", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPD_RSRV3_BY_TRADEID", param, Route.getJourDbDefault());
    }
}