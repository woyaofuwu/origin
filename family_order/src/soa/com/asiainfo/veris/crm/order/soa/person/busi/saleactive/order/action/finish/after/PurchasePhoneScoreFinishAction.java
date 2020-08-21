package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class PurchasePhoneScoreFinishAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {

		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
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

				//查看是否存在对办理的该活动有积分的特殊处理配置
				IDataset saleActiveConfigs = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "5669", productId, packageId);
				
				//如果存在特殊处理的配置
				if (IDataUtil.isNotEmpty(saleActiveConfigs))
				{
					IData saleActiveConfig = saleActiveConfigs.getData(0);

					String dealProductId = saleActiveConfig.getString("PARA_CODE2");//处理产品
					String dealProductName = saleActiveConfig.getString("PARA_CODE3");//没用
					String dealPackageId = saleActiveConfig.getString("PARA_CODE4");//处理包
					String dealPackageName = saleActiveConfig.getString("PARA_CODE5");//没用
					String dealProductType = saleActiveConfig.getString("PARA_CODE6");//没用
					String scoreType = saleActiveConfig.getString("PARA_CODE7");// 积分类型
					String consumeScore = saleActiveConfig.getString("PARA_CODE8");//积分扣减值，正值
					String addScore = saleActiveConfig.getString("PARA_CODE9");// 赠送积分，正值
					String ruleId = saleActiveConfig.getString("PARA_CODE10");// 积分规则
					
					
					//查询该用户在tf_f_user_sale_active表是否有dealProductId、dealPackageId的活动记录
					IDataset userSaleActiveInfos = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, dealProductId, dealPackageId);
					
					//如果有，则取出RELATION_TRADE_ID作为赠送积分的积分子台帐的TRADE_ID，再取出updateTime,  updateStaffId,  updateDepartId
					if (IDataUtil.isNotEmpty(userSaleActiveInfos)) 
					{
						IData userSaleActiveInfo = userSaleActiveInfos.getData(0);
						String relationTradeId = userSaleActiveInfo.getString("RELATION_TRADE_ID");
						String updateTime = userSaleActiveInfo.getString("UPDATE_TIME");
						String updateStaffId = userSaleActiveInfo.getString("UPDATE_STAFF_ID");
						String updateDepartId = userSaleActiveInfo.getString("UPDATE_DEPART_ID");
						
						//调用账务接口，赠送积分
						AcctCall.userScoreOnlyLog(serialNumber, "1", addScore, scoreType, relationTradeId, updateTime, "240", "2015老客户约定消费送积分营销活动");

						//添加赠送积分的子台账数据
						//将用户的dealProductId、dealPackageId活动的relationTradeId、updateTime, updateStaffId, updateDepartId，作为赠送积分子台帐的tradeId、updateTime, updateStaffId, updateDepartId
						IData scoreTradeDataNew = createTradeScoreData(relationTradeId, mainTrade, scoreType, addScore, "", "营销活动赠送积分后台补充积分台账表数据", updateTime, updateStaffId, updateDepartId);
						TradeScoreInfoQry.insTradeScore(scoreTradeDataNew);

						
						//调用账务接口，扣减积分
						AcctCall.userScoreOnlyLog(serialNumber, "2", consumeScore, scoreType, tradeId, mainTradeUpdateTime, "244", "2015老客户约定消费送积分营销活动");

						//添加扣减积分的子台账数据
						//将用户主台帐的relationTradeId、updateTime, updateStaffId, updateDepartId，作为扣减积分子台帐的tradeId、updateTime, updateStaffId, updateDepartId
						//积分子台帐积分值为负值
						IData scoreTradeOffsetData = createTradeScoreData(tradeId, mainTrade, scoreType, "-"+consumeScore, ruleId, "营销活动扣减积分后台补充积分台账表数据", mainTradeUpdateTime, mainTradeUpdateStaffId, mainTradeUpdateDepartId);
						TradeScoreInfoQry.insTradeScore(scoreTradeOffsetData);
					}
				}
			}
		}
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
