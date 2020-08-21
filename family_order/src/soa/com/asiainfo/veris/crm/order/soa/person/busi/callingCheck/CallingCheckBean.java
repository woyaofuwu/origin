
package com.asiainfo.veris.crm.order.soa.person.busi.callingCheck;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

/**
 * 
 * @author zyz
 * 
 */
public class CallingCheckBean extends CSBizBean
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private static Logger log=Logger.getLogger(CallingCheckBean.class);
   
   
 	/**
 	 * 添加核验操作日志
 	 * @param input
 	 * @return
 	 * @throws Exception
 	 */
	public static boolean insertCallingCheckLog(IData input) throws Exception {
		try {
			IData  param=new DataMap();
			
			//用户号码
			param.put("USER_NUMBER", input.getString("SERIAL_NUMBER"));
			param.put("USER_ID", input.getString("USER_ID"));
			//核验时间
			param.put("CHECK_DATE", SysDateMgr.getSysTime());
			//核验号码1
			param.put("CHECK_NUMBER1",input.getString("CHECK_NUMBER1").trim());
			param.put("CHECK_NUMBER2",input.getString("CHECK_NUMBER2").trim());
			param.put("CHECK_NUMBER3",input.getString("CHECK_NUMBER3").trim());
			//操作人员
			param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
			//操作人员部门
			param.put("UPDATE_DEPART_ID", getVisit().getDepartCode());
			//核验结果
			param.put("CHECK_RESULT", input.getString("CHECK_RESULT"));
			if(input.getBoolean("CHECK_RESULT")){
				param.put("CHECK_RESULT", "核验通过");
			}else{
				param.put("CHECK_RESULT", "核验未通过");
			}
			return Dao.insert("TL_F_CALLING_CHECK", param);
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}

	}
	/**
	 * 核验近三个月内主叫记录，当月除外，是否存在
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static boolean checkVoiceRecord(IData data)throws Exception{
		 try {
			 IData result=AcctCall.getCheckVoiceRecordInfo(data).getData(0);
			 String  resultinfo=result.getString("RETURN_CODE");
			 if("0000".equals(resultinfo)){
				 String checkResult=result.getString("CHECK_RESULT");
				 //0不存在，1存在    {0|0}
				 if(checkResult.contains("0")){
					 return false;
				 }else{
					 //存在
					 return true;
				 }
			 }
			 return false;
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}

	
	/**
	 * 通过手机号码获取主叫核验号码个数
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static boolean checkCallingNumBySerialNumber(IData data)throws Exception{
		 try {
			 String serial_number=data.getString("SERIAL_NUMBER");
			 int pageCount=data.getInt("pageCount");
			 
			 IData result=AcctCall.getCallingCheckNumBySerialNumber(serial_number).getData(0);
			 
			 int  count=result.getInt("VOICE_COUNT");
			 if(count == pageCount){
				 //个数一致
				 return true;
			 }else{
				 return false;
			 }
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}

	/**
	 * 验证当天服务号的访问次数是否超过三次
	 * @param serial_number
	 * @return
	 * @throws Exception
	 */
	public static  boolean checkSerialNumberVisitCount(String serial_number)throws Exception{
		try {
			IData param=new DataMap();
			
		    String begin=SysDateMgr.getSysDate()+SysDateMgr.START_DATE_FOREVER;
		    
		    //系统时间，加一天(2016-09-05)
		    String end=SysDateMgr.addDays(1)+SysDateMgr.START_DATE_FOREVER;
		    
			param.put("SERIAL_NUMBER", serial_number);
			param.put("BEGINDATE", begin);
			param.put("ENDDATE", end);
			
			IDataset list=Dao.qryByCode("TL_F_CALLING_CHECK", "SEL_CALLING_CHECK_BY_SERIAL_NUMBER", param);
			if(IDataUtil.isNotEmpty(list)){
			    int count=list.getData(0).getInt("SERIALNUMBER");
			    if(count < 3){
			    	//当天未超过三次,可以继续访问
			    	return true;
			    }
			}
			return false;
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}
}
