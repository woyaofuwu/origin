
package com.asiainfo.veris.crm.order.web.person.callingCheck;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 
 * @author zyz
 * @version 1.0
 *
 */
public abstract class CallingCheck extends PersonBasePage
{ 

	/**
	 * 初始化
	 * @param cycle
	 * @throws Exception
	 */
	public void initPage(IRequestCycle cycle) throws Exception{
		 
		
	}
	
	
	/**
	 *  主叫核验提交
	 * @param cycle
	 * @throws Exception
	 */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
    	//页面入参
        IData data = getData();
        //手机号码
        String serial_number=data.getString("SERIAL_NUMBER");
        
        
        IData input=new DataMap();
        //核验号码1
        String CHECK_NUMBER1=data.getString("CHECK_NUMBER1").trim();
        input.put("CHECK_NUMBER1", CHECK_NUMBER1);
        //核验号码1
        String CHECK_NUMBER2=data.getString("CHECK_NUMBER2").trim();
        input.put("CHECK_NUMBER2", CHECK_NUMBER2);

        //核验号码1
        String CHECK_NUMBER3=data.getString("CHECK_NUMBER3").trim();
        input.put("CHECK_NUMBER3", CHECK_NUMBER3);

        
        input.put("SERIAL_NUMBER", serial_number);
        input.put("USER_ID", data.getString("USER_ID"));
        
        //核验标识
        boolean checkFlag=false;
        
        IData  checkResult=new DataMap();
        
        //验证当天服务号的访问次数是否超过三次
        checkFlag=CSViewCall.call(this,"SS.CallingCheckSVC.checkSerialNumberVisitCount",input).getData(0).getBoolean("check_result");
        if(checkFlag){
        	checkFlag=false;
            String called_numbers="";
            if(!"".equals(CHECK_NUMBER1)&&CHECK_NUMBER1 !=null&&
               !"".equals(CHECK_NUMBER2)&&CHECK_NUMBER2 !=null&&
               !"".equals(CHECK_NUMBER3)&&CHECK_NUMBER3 !=null){
               //三个核验号码都有值
                called_numbers=CHECK_NUMBER1+"|"+CHECK_NUMBER2+"|"+CHECK_NUMBER3;
                input.put("CALLED_NUMBERS", called_numbers);
    			checkFlag = CSViewCall
    					.call(this, "SS.CallingCheckSVC.checkVoiceRecord", input)
    					.getData(0).getBoolean("check_result");
            }else{
                if(!"".equals(CHECK_NUMBER1)&&CHECK_NUMBER1 !=null&&
                   !"".equals(CHECK_NUMBER2)&&CHECK_NUMBER2 !=null){
                	//两个   核验号码1和核验号码2
                	called_numbers=CHECK_NUMBER1+"|"+CHECK_NUMBER2;
                	input.put("CALLED_NUMBERS", called_numbers);
                	input.put("pageCount", 2);
                	//
                	checkFlag=checkCalling(input);
                }else if(!"".equals(CHECK_NUMBER1)&&CHECK_NUMBER1 !=null&&
            	   !"".equals(CHECK_NUMBER3)&&CHECK_NUMBER3 !=null){
            		//两个    核验号码1和核验号码3
                	called_numbers=CHECK_NUMBER1+"|"+CHECK_NUMBER3;
                	input.put("CALLED_NUMBERS", called_numbers);
                	input.put("pageCount", 2);
                	checkFlag=checkCalling(input);
            	}else{
                	//一个   核验号码1
            		called_numbers=CHECK_NUMBER1;
            		input.put("CALLED_NUMBERS", called_numbers);
                	input.put("pageCount", 1);
                	checkFlag=checkCalling(input);
                }
            }
            if(checkFlag){
            	//通过 
            	checkResult.put("checkFlag", 0);
            }else{
            	//未通过
            	checkResult.put("checkFlag", 1);
            }
            //核验结果
            input.put("CHECK_RESULT", checkFlag);
            //添加核验日志
            CSViewCall.call(this, "SS.CallingCheckSVC.insertCallingCheckLog", input);
        }else{
        	//当天核验超过3次
        	checkResult.put("CHECK_RESULT", serial_number+",单日核验已经超过三次.");
        	checkResult.put("checkFlag", "3");
        }
        setAjax(checkResult);
    } 
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public  boolean checkCalling(IData input) throws Exception
    {
    	boolean checkFlag=false;
        try {
			boolean checkCallingNum = CSViewCall
					.call(this,
							"SS.CallingCheckSVC.checkCallingNumBySerialNumber",
							input).getData(0).getBoolean("check_result");
			if (checkCallingNum) {
				//个数正常
				checkFlag = CSViewCall
						.call(this, "SS.CallingCheckSVC.checkVoiceRecord",
								input).getData(0).getBoolean("check_result");
			} else {
				checkFlag = false;
			}
			return checkFlag;
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
    }
}
