package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveactivate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleActiveExtendSVC extends CSBizService {

	private static final long serialVersionUID = 95765701314354878L;

	/**
	 * 查询指定的营销活动
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IDataset queryExtendSaleActives(IData params) throws Exception
    {
		SaleActiveExtendBean saleActiveExtendBean = BeanManager.createBean(SaleActiveExtendBean.class);
        IDataset returnDataset = saleActiveExtendBean.queryExtendSaleActive(params);
        return returnDataset;
    }
	
	/**
	 * 提交事件
	 * @param params
	 * @throws Exception
	 */
	public void tradeReg(IData params) throws Exception
    {
		SaleActiveExtendBean saleActiveExtendBean = BeanManager.createBean(SaleActiveExtendBean.class);
        saleActiveExtendBean.tradeReg(params);
    }
	
}
