
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 客户资料完工后，立即触发缓存刷新
 * @author mengqx 20180810
 */
public class ModifyCustCookieClearAction implements ITradeFinishAction
{
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String serialNumber = mainTrade.getString("SERIAL_NUMBER","");

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("CLEAR_TYPE", 0);//表示服务号码

		//刷新缓存
		CSAppCall.call("CS.UcaCookieClearSVC.clear", param);
		
	}
}
