package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;

public class SaleActiveScoreFinishAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {

		String tradeId = mainTrade.getString("TRADE_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String userId = mainTrade.getString("USER_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String mainTradeUpdateTime = mainTrade.getString("UPDATE_TIME");
		String mainTradeUpdateStaffId = mainTrade.getString("UPDATE_STAFF_ID");
		String mainTradeUpdateDepartId = mainTrade.getString("UPDATE_DEPART_ID");

		//获取本次交易的营销活动
		IDataset saleActiveTradeDatas = TradeSaleActive.getTradeSaleActiveByTradeId(tradeId);
		if (IDataUtil.isNotEmpty(saleActiveTradeDatas))
		{
			IData saleActiveTradeData = saleActiveTradeDatas.getData(0);

			//如果是新增操作
			if (saleActiveTradeData.getString("MODIFY_TAG").equals(BofConst.MODIFY_TAG_ADD))
			{
				String productId = saleActiveTradeData.getString("PRODUCT_ID");//本次办理的营销活动产品
				String packageId = saleActiveTradeData.getString("PACKAGE_ID");//本次办理的营销活动包
				String relaTradeId = saleActiveTradeData.getString("RELATION_TRADE_ID","");//本次办理的营销活动包台账流水

				int rsrvNum5 = Integer.parseInt(saleActiveTradeData.getString("RSRV_NUM5","0")); //办理的活动本月是否已经赠送积分
				int curMonth = Integer.parseInt(SysDateMgr.getCurMonth());
				System.out.println("--------------SaleActiveScoreFinishAction--------------RSRV_NUM5:"+rsrvNum5+",curMonth:"+curMonth);
				//查看是否存在对办理的该活动有积分的特殊处理配置
				IDataset saleActiveConfigs = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "2611", productId, packageId);
				
				//如果存在特殊处理的配置
				if (IDataUtil.isNotEmpty(saleActiveConfigs) && rsrvNum5 < curMonth  )
				{
					IData saleActiveConfig = saleActiveConfigs.getData(0);

					String scoreType = saleActiveConfig.getString("PARA_CODE7","");// 积分类型
					int addScore = Integer.parseInt(saleActiveConfig.getString("PARA_CODE9","0"));// 赠送积分，正值
					String scoreExchangeRuleId = saleActiveConfig.getString("PARA_CODE10","");// 积分兑换编码

					//调用账务接口，赠送积分
					AcctCall.userScoreModify(userId, "ALL", scoreType, tradeTypeCode, addScore, tradeId);

					//添加赠送积分的子台账数据
					IData scoreTradeDataNew = createTradeScoreData(tradeId, mainTrade, scoreType, saleActiveConfig.getString("PARA_CODE9","0"), "", "营销活动赠送积分后台补充积分台账表数据", mainTradeUpdateTime, mainTradeUpdateStaffId, mainTradeUpdateDepartId);
					TradeScoreInfoQry.insTradeScore(scoreTradeDataNew);
					
					//调用积分兑换接口
					IData input = new DataMap();
		            input.put("SERIAL_NUMBER", serialNumber);
		            input.put("RULE_ID", scoreExchangeRuleId);
		            input.put("COUNT", "1");
			        IDataset ids = CSAppCall.call("SS.ScoreExchangeRegSVC.infTradeReg", input);
			        //更新活动的RSRV_NUM5记录是否已经赠送计费
			        updateSaleActiveRsrvNum5(userId,productId,packageId,relaTradeId,SysDateMgr.getCurMonth());
			        
				}
			}
		}
	}
	
	public static int updateSaleActiveRsrvNum5(String userId,String productId,String packageId,String relaTradeId,String rsrvNum5) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("PRODUCT_ID", productId);
		params.put("PACKAGE_ID", packageId);
		params.put("RELATION_TRADE_ID", relaTradeId);
		params.put("RSRV_NUM5", rsrvNum5);
		return Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_SALEACTIVE_RSRV_NUM5", params);
	}

	public IData createTradeScoreData(String tradeId, IData mainTradeData, String scoreTypeCode, String scorechanged, String ruleId,
			String remark, String updateTime, String updateStaffId, String updateDepartId) throws Exception {
		IData scoreTradeData = new DataMap();
		String userId = mainTradeData.getString("USER_ID");

		scoreTradeData.put("TRADE_ID", tradeId);
		scoreTradeData.put("ACCEPT_MONTH", mainTradeData.getString("ACCEPT_MONTH"));
		scoreTradeData.put("USER_ID", userId);
		scoreTradeData.put("SERIAL_NUMBER", mainTradeData.getString("SERIAL_NUMBER"));
		scoreTradeData.put("ID_TYPE", "0");
		scoreTradeData.put("SCORE_TYPE_CODE", scoreTypeCode);
		scoreTradeData.put("YEAR_ID", "ZZZZ");
		scoreTradeData.put("START_CYCLE_ID", "-1");
		scoreTradeData.put("END_CYCLE_ID", "-1");

		// 获取用户的积分
		int score = UcaDataFactory.getUcaByUserId(userId).getUserScore();
		scoreTradeData.put("SCORE", score);

		scoreTradeData.put("SCORE_CHANGED", scorechanged);
		scoreTradeData.put("VALUE_CHANGED", "0");
		scoreTradeData.put("SCORE_TAG", "1");

		// 赠送不需要rule_id，扣减需要rule_id
		scoreTradeData.put("RULE_ID", ruleId);

		scoreTradeData.put("ACTION_COUNT", "");
		scoreTradeData.put("RES_ID", "");
		scoreTradeData.put("GOODS_NAME", "");
		scoreTradeData.put("CANCEL_TAG", "0");
		scoreTradeData.put("UPDATE_TIME", updateTime);
		scoreTradeData.put("UPDATE_STAFF_ID", updateStaffId);
		scoreTradeData.put("UPDATE_DEPART_ID", updateDepartId);
		scoreTradeData.put("REMARK", remark);
		scoreTradeData.put("RSRV_STR1", "");
		scoreTradeData.put("RSRV_STR2", "");
		scoreTradeData.put("RSRV_STR3", "");
		scoreTradeData.put("RSRV_STR4", "");
		scoreTradeData.put("RSRV_STR5", "");
		scoreTradeData.put("RSRV_STR6", "");
		scoreTradeData.put("RSRV_STR7", "");
		scoreTradeData.put("RSRV_STR8", "");
		scoreTradeData.put("RSRV_STR9", "");
		scoreTradeData.put("RSRV_STR10", "");

		return scoreTradeData;
	}
}
