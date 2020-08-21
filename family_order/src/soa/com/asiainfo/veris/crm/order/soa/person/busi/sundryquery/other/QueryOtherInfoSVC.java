
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryOtherInfoSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;
	private static transient Logger logger = Logger.getLogger(QueryOtherInfoSVC.class);
	/**
	 * 用户状态查询接口
	 * @param input billID 手机号码
	 * @return 见 《中移在线实名认证电渠线上选号接口规范-V1.0.13》2.5.4章节
	 * @throws Exception
	 * @author tanzheng
	 */
	public IData queryUserStateInfo(IData input) throws Exception
    {
		 if (logger.isInfoEnabled())
         {
		 	logger.info("用户状态查询接口>>>" + input.toString());
         }
		 QueryOtherInfoBean queryOtherInfo = (QueryOtherInfoBean) BeanManager.createBean(QueryOtherInfoBean.class);
		 return queryOtherInfo.queryOtherInfo(input);
    }
	/**
	 * 
	 * @Description：查询可用号码
	 * @param:@param input
	 * @param:@return
	 * @param:@throws Exception
	 * @return IData
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-23上午11:12:00
	 */
	public IData queryAbleResNo(IData input) throws Exception {
		if (logger.isInfoEnabled())
        {
		 	logger.info("查询可用号码接口>>>" + input.toString());
        }
		
		QueryOtherInfoBean queryOtherInfo = (QueryOtherInfoBean) BeanManager.createBean(QueryOtherInfoBean.class);
		return queryOtherInfo.queryAbleResNo(input);
		
	}
	/**
	 * 号码预占
	 * @Description：TODO
	 * @param:@param input
	 * @param:@return
	 * @param:@throws Exception
	 * @return IData
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-23下午03:08:55
	 */
	public IData preemptionResNo(IData input) throws Exception {
		if (logger.isInfoEnabled())
        {
		 	logger.info("号码预占接口>>>" + input.toString());
        }
		
		QueryOtherInfoBean queryOtherInfo = (QueryOtherInfoBean) BeanManager.createBean(QueryOtherInfoBean.class);
		return queryOtherInfo.preemptionResNo(input);
	}
	/**
	 * 
	 * @Description：订单校验
	 * @param:@param input
	 * @param:@return
	 * @param:@throws Exception
	 * @return IData
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-23下午04:24:56
	 */
	public IData checkOrderPre(IData input) throws Exception {
		if (logger.isInfoEnabled())
        {
		 	logger.info("订单预校验接口>>>" + input.toString());
        }
		
		QueryOtherInfoBean queryOtherInfo = (QueryOtherInfoBean) BeanManager.createBean(QueryOtherInfoBean.class);
		return queryOtherInfo.checkOrderPre1(input);
		
	}
	/**
	 * 
	 * @Description 一证多号查询接口
	 * @param:@param input
	 * @param:@return
	 * @param:@throws Exception
	 * @return IData
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-12-11下午03:23:00
	 */
	public IData queryMoreNum(IData input) throws Exception {
		if (logger.isInfoEnabled())
        {
		 	logger.info("一证多号查询接口>>>" + input.toString());
        }
		
		QueryOtherInfoBean queryOtherInfo = (QueryOtherInfoBean) BeanManager.createBean(QueryOtherInfoBean.class);
		return queryOtherInfo.queryMoreNum(input);
	}
}
