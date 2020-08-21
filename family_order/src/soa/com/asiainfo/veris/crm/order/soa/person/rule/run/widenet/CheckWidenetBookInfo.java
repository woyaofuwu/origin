
package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;


/**
 * 校验宽带预约登记规则
 * @author zyz
 *
 */
public class CheckWidenetBookInfo extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;
    
    static Logger logger=Logger.getLogger(CheckWidenetBookInfo.class);

    /**
     * 是否已经办理了宽带判断
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String serialNumber = databus.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        IData data=new DataMap();
        data.put("SERIAL_NUMBER", "KD_" + serialNumber);
        /**
         * 判断用户是否已经登记过
         * UserInfoQry.getUserInfoBySerailNumber("0", "KD_" + serialNumber);
         */
        //获取用户信息
        IData widenetInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
        
        if(IDataUtil.isNotEmpty(userInfo)){
            IDataset  userOhterInfo=WidenetInfoQry.getUserOtherInfoByUserId(userInfo.getString("USER_ID"));
            if(IDataUtil.isNotEmpty(userOhterInfo)){
            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "6080324", "用户已经登记过");
            	return true;
            }
        }
        
        /**
         * 近三个月已办理过宽带业务,进行拦截。
         * 
         */
        //当前时间的90天前
        data.put("DESTROY_TIME", get90DayBeforeDate());
        //SEL_USERINFO_BY_SERIAL_NUMBER
        IDataset dataList=WidenetInfoQry.getWidenetThreeMonthInfo(data);
        if(IDataUtil.isNotEmpty(dataList)){
        	//如果存在记录，则拦截提示：近三个月已办理过宽带业务
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "6080322", "近三个月已办理过宽带业务");
        	return true;
        }
        
        
        if (IDataUtil.isNotEmpty(widenetInfo))
        {// 已办理宽带
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
            if (IDataUtil.isNotEmpty(widenetInfos))
            {//是宽带客户
            	String openDate=widenetInfo.getString("OPEN_DATE");
            	//当月开户的宽带可以办理
            	if(isNowMonth(openDate)){
            	    return false;
            	}else{
	               BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "6080325", "用户已经是宽带客户");
	               return true;
            	}
            }
        }else{
        	/**
        	 * //KD_" + serialNumber
	           //600
        	 */
        	IDataset tradeInfo=WidenetInfoQry.getTradeInfoBySerialNumber("KD_" + serialNumber);
	    	if (IDataUtil.isNotEmpty(tradeInfo))
	        {
	        	String accept_date=tradeInfo.getData(0).getString("ACCEPT_DATE");
	        	if(isNowMonth(accept_date)){
	        		//受理时间为系统当月
	        	    return false;
	        	}else{
	        	   BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "6080321", "宽带开户不在当前月份");
		               return true;
	        	}
	        }
        }
        
        return false;
    }
    /**
     * 获取系统当前时间前90天的时间
     * @return
     */
   public static String  get90DayBeforeDate()throws Exception{
	    String beforeDate=null;
		 try {
			  Calendar calendar = Calendar.getInstance(); //得到日历
			  calendar.setTime(new Date());//把当前时间赋给日历
			  calendar.add(Calendar.DAY_OF_MONTH, -90);  //设置为前90天
			  beforeDate=SysDateMgr.getDateByTimeMillis(calendar.getTimeInMillis(), SysDateMgr.PATTERN_STAND_YYYYMMDD);
			 return beforeDate+SysDateMgr.START_DATE_FOREVER;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beforeDate;
   }
   
   /**
    * 根据输入的时间，判断是否为系统当前月份
    * @param openDate
    * @return
    */
   public static boolean isNowMonth(String openDate)throws Exception{
	    try {
	    	//转换为yyyyMM  openDate:2014-02-27 17:18:28
		    if (openDate != null && openDate.length() >= 10)
	        {
		    	openDate = openDate.replaceAll("-", "");
		    	openDate = openDate.substring(0, 6);
	        }
		    //获取系统当前时间
	       String  nowDate=SysDateMgr.getSysDateYYYYMMDD();
	       nowDate=nowDate.substring(0, 6);
	       if(nowDate.equals(openDate)){
	    	   //是系统当月
	    	   return true;
	       }
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
		return false;
   }
}
