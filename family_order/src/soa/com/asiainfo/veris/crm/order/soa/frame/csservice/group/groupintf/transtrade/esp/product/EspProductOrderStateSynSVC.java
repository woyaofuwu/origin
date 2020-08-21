
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class EspProductOrderStateSynSVC extends GroupOrderService
{   
	 /**
	 * ESP产品订购状态同步
	 */
	private static final long serialVersionUID = 1L;

	public IDataset crtTrade(IData idata) throws Exception
	 {
		EspProductOrderStateSynBean bean = new EspProductOrderStateSynBean();
        return bean.crtTrade(idata);
	 }
}