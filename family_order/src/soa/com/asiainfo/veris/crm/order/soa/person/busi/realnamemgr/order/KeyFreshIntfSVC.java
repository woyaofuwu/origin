package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order;
 
import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class KeyFreshIntfSVC extends CSBizService
{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	/**
	 * 客户端密钥更新
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData keyFreshQuery(IData data) throws Exception
    {
		KeyFreshIntfBean bean = (KeyFreshIntfBean) BeanManager.createBean(KeyFreshIntfBean.class);
	     return bean.KeyFreshQuery(data);
    }
	
	//strat接收在线公司私钥接口-wangsc10-20180919
	/**
	 * 接收在线公司私钥接口TEST
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public IData receivingOnLineCompanyInterface(IData data) throws Exception
    {
		KeyFreshIntfBean bean = (KeyFreshIntfBean) BeanManager.createBean(KeyFreshIntfBean.class);
	     return bean.receivingOnLineCompanyInterface(data.getString("KEY"));
    }
	//end
	
	/**
	 * 客户端密钥下发
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData sendPushKey(IData data) throws Exception
    {
		KeyFreshIntfBean bean = (KeyFreshIntfBean) BeanManager.createBean(KeyFreshIntfBean.class);
	     return bean.sendPushKey(data);
    }
	/**
	 * 客户端密钥下发
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData pushCardkey(IData data) throws Exception
    {
		KeyFreshIntfBean bean = (KeyFreshIntfBean) BeanManager.createBean(KeyFreshIntfBean.class);
	     return bean.pushCardKey(data);
    }
}
