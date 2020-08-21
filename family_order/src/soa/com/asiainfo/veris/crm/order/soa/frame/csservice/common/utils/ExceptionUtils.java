
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;


public class ExceptionUtils
{
    private static Logger logger = Logger.getLogger(ExceptionUtils.class);
    public static void getExceptionInfo(Throwable e,IData exceptionRet)
    {
     
        if (e instanceof InvocationTargetException)
        {
           getExceptionInfo(((InvocationTargetException) e).getTargetException(), exceptionRet);
        }
        else
        {
            String msg = e.getMessage();
            msg = (msg == null) ? "处理失败" : msg;
            String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
            
            StringBuffer sb = new StringBuffer();
            for (StackTraceElement stackTraceElement : e.getStackTrace())
            {
                sb.append(stackTraceElement + "\n");
            }
            String tStr = sb.toString();

            String resultDetail = ((tStr.length() > 4000) ? tStr.substring(0, 4000) : tStr); 
            
            exceptionRet.put("RESULT_INFO", rspDesc); 
            exceptionRet.put("RESULT_DETAIL", resultDetail);
            exceptionRet.put("RESULT_CODE", "-1"); 
            
            if(logger.isDebugEnabled())
            {
                logger.error(rspDesc, e);
            } 
        }
      
    }
    public static IData getExceptionInfo(Throwable e)
    {
        IData exceptionRet = new DataMap();
        
         getExceptionInfo(e,exceptionRet);
         
         return exceptionRet; 
    }


   

}
