package com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;

/**
 * 前台参数过滤转换
 * 
 * @author wangyongchao
 *
 */
public interface IFilter {
	public static final Logger logger = Logger.getLogger(IFilter.class);

	/**
	 * 
	 * @param cond:条件参数
	 * @param trans:需转换或者过滤的参数
	 * @return
	 * @throws Exception
	 */
	public IData trans(IData cond, IData trans) throws Exception;
}
