
package com.asiainfo.veris.crm.order.soa.person.busi.moblieStore;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


public class MobileStoreSVC extends CSBizService
{
	
	private static Logger logger = Logger.getLogger(MobileStoreSVC.class);
   /**
    * 呼叫转移办理
    * @param input
    * @return
    * @throws Exception
    */
	public IData callForwarding(IData input) throws Exception {
		MobileStoreBean  bean = (MobileStoreBean)BeanManager.createBean(MobileStoreBean.class);
		return bean.callForwarding(input);
	}
    /**
     * 移动商城业务办理资格校验
     * @param input
     * @return
     * @throws Exception
     */
	public IData checkServiceRuleUmmp(IData input) throws Exception {
		MobileStoreBean  bean = (MobileStoreBean)BeanManager.createBean(MobileStoreBean.class);
		return bean.checkServiceRuleUmmp(input);
	}
	/**
	 * 6.4现金支付业务办理
	 */
	public IData dealCashPayMent(IData input) throws Exception {
		MobileStoreBean  bean = (MobileStoreBean)BeanManager.createBean(MobileStoreBean.class);
	    return bean.dealCashPayMent(input);
	}
	/**
	 * 营销案办理接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData saleActiveOrder(IData input) throws Exception {
		MobileStoreBean  bean = (MobileStoreBean)BeanManager.createBean(MobileStoreBean.class);
		return bean.saleActiveOrder(input);
	}
	/**
	 * 通用活动办理预校验接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkCommActive(IData input) throws Exception{
		MobileStoreBean  bean = (MobileStoreBean)BeanManager.createBean(MobileStoreBean.class);
		return bean.checkCommActive(input);
	}
	
	/**
	 * 通用活动办理同步接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData dealCommActive(IData input) throws Exception{
		MobileStoreBean  bean = (MobileStoreBean)BeanManager.createBean(MobileStoreBean.class);
		return bean.dealCommActive(input);
	}
	/**
	 * 开通/关闭高频骚扰电话拦截服务
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData interceptHarassmentCall(IData input) throws Exception {
		MobileStoreBean  bean = BeanManager.createBean(MobileStoreBean.class);
		return bean.interceptHarassmentCall(input);
	}

	/**
	 * 移动商城接口规范----6.66	用户宽带信息查询接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData qryBroadbandInfo(IData input) throws Exception {
		MobileStoreBean  bean = BeanManager.createBean(MobileStoreBean.class);
		return bean.qryBroadbandInfo(input);
	}
}
