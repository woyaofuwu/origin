package com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;

/**
 * 业务校验
 * 
 * @author wangyongchao
 *
 */
public interface ICheck {
	public static final Logger logger = Logger.getLogger(ICheck.class);

	boolean check(IData checkInparam) throws Exception;

}
