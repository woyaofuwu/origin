
package com.asiainfo.veris.crm.order.soa.person.busi.callingCheck;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 
 * 
 * @author zyz
 *
 */
public class CallingCheckSVC extends CSBizService
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private static Logger log=Logger.getLogger(CallingCheckSVC.class);
	 
	/**
	 * 核验近三个月内主叫记录，当月除外，是否存在
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData checkVoiceRecord(IData data)throws Exception{
		IData result=new DataMap();
		 try {
			 result.put("check_result", CallingCheckBean.checkVoiceRecord(data));
			 return result;
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}
	
	/**
	 * 通过手机号码获取主叫号码个数
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData checkCallingNumBySerialNumber(IData data)throws Exception{
		IData result=new DataMap();
		 try {
			 result.put("check_result", CallingCheckBean.checkCallingNumBySerialNumber(data));
			 return result;
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}
	/**
	 * 添加主叫核验日志
	 * @param input
	 * @throws Exception
	 */
	public static void insertCallingCheckLog(IData input) throws Exception {
		try {
			CallingCheckBean.insertCallingCheckLog(input);
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}
    
	/**
	 * 验证手机号码当天的访问数次
	 * 一天最多只能访问3次
	 * @param serial_number
	 * @return
	 * @throws Exception
	 */
	public static IData checkSerialNumberVisitCount(IData data)throws Exception{
		IData result=new DataMap();
		try {
			String serial_number=data.getString("SERIAL_NUMBER");
			result.put("check_result",CallingCheckBean.checkSerialNumberVisitCount(serial_number));
			return result;
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}
	
}
