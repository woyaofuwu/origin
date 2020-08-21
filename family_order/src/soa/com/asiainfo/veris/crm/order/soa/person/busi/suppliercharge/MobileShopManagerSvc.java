package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge;

import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.wade.container.util.log.Log;

@SuppressWarnings("serial")
public class MobileShopManagerSvc extends CSBizService
{
	public IDataset querySuppInfo(IData param) throws Exception
    {
		MobileShopManagerBean bean = (MobileShopManagerBean) BeanManager.createBean(MobileShopManagerBean.class);
        return bean.querySuppInfo(param);
    }
	
	public IDataset queryMobileShopInfo(IData param) throws Exception
    {
		MobileShopManagerBean bean = (MobileShopManagerBean) BeanManager.createBean(MobileShopManagerBean.class);
        return bean.queryMobileShopInfo(param,getPagination());
    }
	
	public IData deleteMobileInShop(IData param) throws Exception
	{
		MobileShopManagerBean bean = (MobileShopManagerBean) BeanManager.createBean(MobileShopManagerBean.class);
		return bean.deleteMobileInShop(param);
	}
	
	public IData mobileShopInsert(IData param) throws Exception
	{
		MobileShopManagerBean bean = (MobileShopManagerBean) BeanManager.createBean(MobileShopManagerBean.class);
		return bean.mobileShopInsert(param);
	}

}
