
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.filter;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cibp.CibpExceptionFilter;

/**
 * IVR兑换错误输出转换
 * 
 * @author huangsl
 */
public class IVRScoreExchangeExceptionFilter implements IFilterException
{

    protected static final Logger log = Logger.getLogger(CibpExceptionFilter.class);

    public IData transferException(Throwable e, IData input) throws Exception
    {
        try
        {
        	String error =  Utility.parseExceptionMessage(e); 
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
            String serialNumber = input.getString("SERIAL_NUMBER");
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            IDataset scoreInfo = AcctCall.queryUserScore(userInfo.getString("USER_ID"));
            
            if(errorArray[0].equals("CRM_USER_782")){
            	input.put("X_RESULTCODE", "7950");
            	input.put("X_RESULTINFO", "积分不足");
            	input.put("SCORE1", "0");
            	input.put("SCORE2", "0");
            	if(IDataUtil.isNotEmpty(scoreInfo)){
                	input.put("SCORE3", scoreInfo.getData(0).getString("SUM_SCORE"));
            	}else{
            		input.put("SCORE3", "0");
            	}
				return input;
            }else{
            	input.put("X_RESULTCODE", "0000");
            	input.put("X_RESULTINFO", errorArray[1]);
            	input.put("SCORE1", "0");
            	input.put("SCORE2", "0");
            	if(IDataUtil.isNotEmpty(scoreInfo)){
                	input.put("SCORE3", scoreInfo.getData(0).getString("SUM_SCORE"));
            	}else{
            		input.put("SCORE3", "0");
            	}
				return input;
            }
        }
        catch (Exception ex)
        {
        	input.put("X_RESULTCODE", "2998");
        	input.put("X_RESULTINFO", "其他错误");
        	input.put("SCORE1", "0");
        	input.put("SCORE2", "0");
        	input.put("SCORE3", "0");
			return input;
        }

    }

}
