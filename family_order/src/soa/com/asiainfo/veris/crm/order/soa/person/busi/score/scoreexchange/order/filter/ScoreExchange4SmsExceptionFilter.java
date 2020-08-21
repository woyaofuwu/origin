
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.filter;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;

/**
 * 生活平台兑换错误输出转换给短厅
 * 
 * @author huangsl
 */
public class ScoreExchange4SmsExceptionFilter implements IFilterException
{
    public IData transferException(Throwable e, IData input) throws Exception
    {
        try
        {
        	String error =  Utility.parseExceptionMessage(e); 
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
           
            if(errorArray[0].equals("CRM_SCORE_11")||errorArray[0].equals("19311")){//1 ok
            	input.put("X_RESULTCODE", "2370");
            	input.put("X_RESULTINFO", "用户不存在");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_12")||errorArray[0].equals("19312")){//2 ok
            	input.put("X_RESULTCODE", "2375");
            	input.put("X_RESULTINFO", "CRM无该礼品兑换编码");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_13")||errorArray[0].equals("19313")){//3 ok
            	input.put("X_RESULTCODE", "2373");
            	input.put("X_RESULTINFO", "员工及代理商无法参加");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_14")||errorArray[0].equals("19314")){//4 ok
            	input.put("X_RESULTCODE", "2377");
            	input.put("X_RESULTINFO", "非目标星级客户");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_15")||errorArray[0].equals("19315")){//5 ok
            	input.put("X_RESULTCODE", "2378");
            	input.put("X_RESULTINFO", "非目标地区客户");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_17")||errorArray[0].equals("19317")){//7 ok
            	input.put("X_RESULTCODE", "2371");
            	input.put("X_RESULTINFO", "活动有效期已过");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_18")||errorArray[0].equals("19318")){//8 ok
            	input.put("X_RESULTCODE", "2372");
            	input.put("X_RESULTINFO", "有效期内用户已办理过该活动");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_19")||errorArray[0].equals("19319")){//9 ok
            	input.put("X_RESULTCODE", "2379");
            	input.put("X_RESULTINFO", "获取用户积分无数据");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_20")||errorArray[0].equals("19320")){//10 ok
            	input.put("X_RESULTCODE", "2374");
            	input.put("X_RESULTINFO", "积分不足");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_6")||errorArray[0].equals("1936")){
            	input.put("X_RESULTCODE", "2376");
            	input.put("X_RESULTINFO", "通知和生活平台下发电子券失败");
				return input;
            }else if(errorArray[0].equals("CRM_SCORE_23")||errorArray[0].equals("19323")){
            	input.put("X_RESULTCODE", "2380");
            	input.put("X_RESULTINFO", "活动的参加人数已经满额，无法继续参加");
				return input;
            }else{
            	input.put("X_RESULTCODE", "9999");
            	input.put("X_RESULTINFO", errorArray[1]);
				return input;
            }
        }
        catch (Exception ex)
        {
        	input.put("X_RESULTCODE", "2998");
        	input.put("X_RESULTINFO", "其他错误");
			return input;
        }

    }

}
