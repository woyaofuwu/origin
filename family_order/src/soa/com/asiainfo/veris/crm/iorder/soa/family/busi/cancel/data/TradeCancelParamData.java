package com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data;

import com.ailk.common.data.IData;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

/**
 * @desc 该对象成员变量不提供setter方法，即对象创建后成员变量（引用）不能修改。
 * @author danglt
 * @date 2018年12月18日
 * @version v1.0
 */
public class TradeCancelParamData {
	
	private TradeCancelReqData reqData;

	private FullMainTradeData mainTradeData;

	public TradeCancelParamData(TradeCancelReqData reqData, IData tradeData) {
		if (reqData == null) {
			String strError = "参数检查: 入参【reqData】为空！";
			Utility.error("-1", null, strError);
		}

		this.reqData = reqData;

		if (IDataUtil.isEmpty(tradeData)) {
			String strError = "参数检查: 入参【tradeData】不存在或者为空！";
			Utility.error("-1", null, strError);
		}

		this.mainTradeData = new FullMainTradeData(tradeData);
	}

	public TradeCancelReqData getReqData() {
		return reqData;
	}

	public FullMainTradeData getMainTradeData() {
		return mainTradeData;
	}
	
}

