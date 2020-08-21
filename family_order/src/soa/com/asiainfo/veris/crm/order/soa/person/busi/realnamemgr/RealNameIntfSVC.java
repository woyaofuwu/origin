package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RealNameIntfSVC extends CSBizService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 下发采集验证工单
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData realNameCreateTrade(IData data) throws Exception
    {
		RealNameIntfBean bean = (RealNameIntfBean) BeanManager.createBean(RealNameIntfBean.class);
	     return bean.realNameCreateTrade(data);
    }
	
	/**
	 * 采集验证工单处理结果反馈
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData realNameResult(IData data) throws Exception
    {
		RealNameIntfBean bean = (RealNameIntfBean) BeanManager.createBean(RealNameIntfBean.class);
	     return bean.realNameResult(data);
    }
	
	/**
	 * 采集验证工单处理结果接口查询
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData resultQuery(IData data) throws Exception
    {
		RealNameIntfBean bean = (RealNameIntfBean) BeanManager.createBean(RealNameIntfBean.class);
	     return bean.resultQuery(data);
    }
	
	/**
	 * AEE 调用同步图片信息给东软
	 * @param data
	 * @throws Exception
	 */
	public IData synRealNamePicInfo(IData data) throws Exception
    {
		RealNameIntfBean bean = (RealNameIntfBean) BeanManager.createBean(RealNameIntfBean.class);
	     return bean.synRealNamePicInfo(data);
    }
	
	/**
	 * 客户端登陆认证
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData doLogin(IData data) throws Exception
    {
		RealNameIntfBean bean = (RealNameIntfBean) BeanManager.createBean(RealNameIntfBean.class);
	     return bean.doLogin(data);
    }
	
}
