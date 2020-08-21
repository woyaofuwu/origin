package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * 校验是宽带开户是否有未完工工单
 * 
 * @author likai3
 */
public class CheckWidenetBookTrade extends BreBase implements IBREScript {

	private static final long serialVersionUID = 1L;

	public boolean run(IData databus, BreRuleParam param) throws Exception {
		String xChoiceTag = databus.getString("X_CHOICE_TAG");
		if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 查询号码时校验
		{
			String serialNumber = databus.getString("SERIAL_NUMBER");
//			IDataset TradeInfos = TradeInfoQry
//					.CheckIsExistNotGHFinishedTrade("KD_" + serialNumber);
			IDataset TradeInfos = TradeInfoQry
			.CheckIsExistNotGHFinishedTrade(serialNumber.indexOf("KD_")>-1 ? serialNumber:"KD_" + serialNumber);
			if (!StringUtils.equals(TradeInfos.getData(0).getString("ROW_COUNT"), "0")) {
				// CSAppException.apperr(TradeException.CRM_TRADE_0);
				String errorMsg = "该服务号码[" + serialNumber+ "]存在宽带未完工工单，业务不能继续！";
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"20141111", errorMsg);
				return true;
			}
		}
		return false;
	}
}
