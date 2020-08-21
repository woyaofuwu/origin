package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.action.undofinish;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeCancelFee;


public class CancelSaleActiveAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		//宽带营销活动
		String saleActiveTradeId = mainTrade.getString("RSRV_STR5","");
		if(saleActiveTradeId != null && !"".equals(saleActiveTradeId))
		{
			cancelTrade(saleActiveTradeId);
		}
	}
	
	/**
	 * 营销活动返销，如果已完工，则返销，如未完工，则搬到历史表中
	 * @param tradeId
	 * @throws Exception
	 */
	private void cancelTrade(String tradeId) throws Exception
	{
		if(tradeId != null && !"".equals(tradeId))
		{
			//查询是否已完工
			IData tradeInfos =  UTradeInfoQry.qryTradeByTradeId(tradeId,"0");
			if(IDataUtil.isEmpty(tradeInfos))
			{
				IData hisTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);
				if(IDataUtil.isNotEmpty(hisTradeInfos))
				{
					IData pdData = new DataMap();
					pdData.put("REMARKS", "家庭IMS固话撤单，营销活动自动返销");
					pdData.put("CANCEL_TYPE", "1");  //如果该值传1 则会强制返销费用
					pdData.put("TRADE_ID", tradeId);
					pdData.put("IS_CHECKRULE", false);
			        pdData.put(Route.ROUTE_EPARCHY_CODE,hisTradeInfos.getString("TRADE_EPARCHY_CODE"));
			        CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", pdData);
				}
			}
			else
			{
				//未完工，将工单移到历史表
				dealUnfinishTrade(getPublicData(tradeInfos),tradeInfos);
			}
		}
	}
	
	private IData getPublicData(IData tradeInfo) throws Exception {
		IData pubData = new DataMap();
		pubData.put("ORDER_ID", tradeInfo.getString("ORDER_ID", ""));
		pubData.put("TRADE_ID", tradeInfo.getString("TRADE_ID", ""));
		pubData.put("NEW_ORDER_ID", SeqMgr.getOrderId());
		pubData.put("SYS_TIME", SysDateMgr.getSysTime());
		pubData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		pubData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		pubData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		pubData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		return pubData;
	}
	
	private void dealUnfinishTrade(IData pubData, IData tradeInfo) throws Exception
    {
        IData tradeData = new DataMap();
        tradeData.putAll(tradeInfo);
        tradeData.put("CANCEL_TAG", "1");
        tradeData.put("SUBSCRIBE_STATE", "A");
        tradeData.put("FINISH_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("CANCEL_TAG", "1");
        tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        tradeData.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        tradeData.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        tradeData.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
        tradeData.put("REMARK","家庭IMS固话开户撤单，未完工营销活动搬迁到历史表");
        
        if (!Dao.insert("TF_BH_TRADE", tradeData, Route.getJourDb(BizRoute.getTradeEparchyCode())))
        {
            String msg = "家庭IMS固话撤单，营销活动订单【" + tradeInfo.getString("TRADE_ID") + "】搬迁至历史表失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
        
        if (!Dao.delete("TF_B_TRADE", tradeInfo, Route.getJourDb(BizRoute.getTradeEparchyCode())))
        {
            String msg = "家庭IMS固话撤单，营销活动删除订单【" + tradeInfo.getString("TRADE_ID") + "】失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
        
        //费用返销
        IData pageData = new DataMap();
        pageData.put("CANCEL_TYPE", "1");  //如果该值传1 则会强制返销费用
        TradeCancelFee.cancelRecvFee(tradeInfo,pageData);
    }
}

