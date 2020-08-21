package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.filter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.script.rule.score.TBCheckOpenTimeRule;

public class ScoreExchangeInOpenTimeFilter implements IFilterIn {

	private static transient Logger logger = Logger.getLogger(TBCheckOpenTimeRule.class);
	@Override
	public void transferDataInput(IData input) throws Exception {
		
		 if (logger.isDebugEnabled())
	            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 进入ScoreExchangeInOpenTimeFilter()  <<<<<<<<<<<<<<<<<<<");
		 
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		
		//获取用户资料，判断是否存在用户信息
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(ScoreException.CRM_SCORE_11);//1
		}
		
	    String dateStr = userInfo.getString("OPEN_DATE");
        if(StringUtils.isBlank(dateStr)){
        	CSAppException.apperr(ScoreException.CRM_SCORE_25,20171020);
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date  = formatter.parse(dateStr);
       
        //获取用户开户时间之后的六个月的时间
        Date sixMonthDate = DateUtils.addMonths(date, 6);
        long nowDate = System.currentTimeMillis();
        if(nowDate<sixMonthDate.getTime()){
        	CSAppException.apperr(ScoreException.CRM_SCORE_25,20171020);
        }
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ScoreExchangeInOpenTimeFilter()  <<<<<<<<<<<<<<<<<<<");

	}
}
