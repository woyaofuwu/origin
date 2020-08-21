package com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data;

import java.util.ArrayList;
import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeLimitInfoQry;

/**
 * @desc trade依赖关系的数据结构
 * @author danglt
 * @date 2018年12月6日
 * @version v1.0
 */
public final class TradeDependRelaData {
	
	private IData tradeData;

	private List<TradeDependRelaData> childTradeList;// 子节点集合

	public TradeDependRelaData() {
	}

	public TradeDependRelaData(IData tradeData) {
		this.tradeData = tradeData;
	}

	public IData getTradeData() {
		return tradeData;
	}

	public void setTradeData(IData tradeData) {
		this.tradeData = tradeData;
	}

	public List<TradeDependRelaData> getChildTradeList() {
		return childTradeList;
	}

	public void setChildTradeList(List<TradeDependRelaData> childTradeList) {
		this.childTradeList = childTradeList;
	}

	/**
	 * @desc 解析依赖节点数据
	 * @param orderedList
	 *            接收解析后的数据
	 * @author danglt
	 * @date 2018年12月17日
	 */
	public void parse(IDataset orderedList) {
		orderedList.add(this.tradeData);
		if (childTradeList != null) {
			for (int i = 0; i < this.childTradeList.size(); i++) {
				childTradeList.get(i).parse(orderedList);
			}
		}
	}
	
	
	/**
	 * @desc 查询依赖的子工单，并设置工单依赖级别
	 * @param tradeId
	 *            父工单
	 * @param parentLevel
	 *            父工单依赖级别>0
	 * @param tradeList
	 *            所有工单数据
	 * @throws Exception
	 * @author danglt
	 * @date 2018年11月12日
	 */
	public static TradeDependRelaData queryLimitTrades(IData parentTradeData, int parentLevel, IDataset tradeList)
			throws Exception {
		TradeDependRelaData tradeDependRelaData = new TradeDependRelaData(parentTradeData);
		tradeDependRelaData.getTradeData().put("LEVEL_CLASS", parentLevel);
		IDataset limitTrades = TradeLimitInfoQry.getLimitTradeByTradeId(parentTradeData.getString("TRADE_ID"));
		if (IDataUtil.isEmpty(limitTrades)) {
			tradeDependRelaData.getTradeData().put("HAS_CHILD", false);
			tradeDependRelaData.setChildTradeList(null);
			return tradeDependRelaData;
		} else {
			tradeDependRelaData.getTradeData().put("HAS_CHILD", true);
		}

		List<TradeDependRelaData> childTradeList = new ArrayList<TradeDependRelaData>();
		for (int i = 0, s = limitTrades.size(); i < s; i++) {
			String limitTradeId = limitTrades.getData(i).getString("TRADE_ID");
			for (int j = 0, n = tradeList.size(); j < n; j++) {
				if (limitTradeId.equals(tradeList.getData(j).getString("TRADE_ID"))) {
					childTradeList.add(queryLimitTrades(tradeList.getData(j), parentLevel + 1, tradeList));
				}
			}
		}
		tradeDependRelaData.setChildTradeList(childTradeList);

		return tradeDependRelaData;
	}
}

