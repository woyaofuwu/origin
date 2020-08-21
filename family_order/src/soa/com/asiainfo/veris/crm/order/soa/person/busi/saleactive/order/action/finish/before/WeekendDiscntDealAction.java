package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.before;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


public class WeekendDiscntDealAction implements ITradeAction{
	private static Logger logger = Logger.getLogger(WeekendDiscntDealAction.class);
	public void executeAction(BusiTradeData btd) throws Exception{
		String tradeTypeCode=btd.getTradeTypeCode();


		List<DiscntTradeData> discntInfos = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		if (discntInfos == null || discntInfos.size() <= 0) {
			return;
		}
		String acceptDate = btd.getRD().getAcceptTime();
		String startdate= getSaturday(acceptDate)+" 00:00:00";
		String enddate= getSunday(acceptDate)+" 23:59:59";
		boolean weekend = isWeekend(acceptDate);
		if(weekend)
		{
			startdate = acceptDate;
		}
        if (logger.isDebugEnabled())
            logger.debug(">>>>> 进入 WeekendDiscntDealAction>>>>>AcceptDate:"+acceptDate+",startdate:"+startdate+",enddate:"+enddate
            +",weekend:"+weekend);	

		for (int i = 0; i < discntInfos.size(); i++) 
		{
			DiscntTradeData discntInfo = discntInfos.get(i);
			String discntCode = discntInfo.getDiscntCode();
			String modifytag = discntInfo.getModifyTag();
			IDataset weekendDiscnts=CommparaInfoQry.getCommPkInfo("CSM", "7615", discntCode,CSBizBean.getUserEparchyCode());
			if (logger.isDebugEnabled())
	            logger.debug(">>>>> 进入 WeekendDiscntDealAction>>>>>weekendDiscnts:"+weekendDiscnts);	
			if (IDataUtil.isNotEmpty(weekendDiscnts) && modifytag.equals(BofConst.MODIFY_TAG_ADD)) {
				
				if("110".equals(tradeTypeCode) || "150".equals(tradeTypeCode))
				{
					//验证用户是否已经办理过套餐
			        String userId=btd.getRD().getUca().getUserId();
			        IDataset userDiscnt=getUserDiscnt(userId, "7615", CSBizBean.getUserEparchyCode());
			        if(IDataUtil.isNotEmpty(userDiscnt)){
			        	CSAppException.apperr(ProductException.CRM_PRODUCT_521,"【"+userDiscnt.getData(0).getString("DISCNT_CODE", "")+"】");
			        }
				}
				
				discntInfo.setStartDate(startdate);
				discntInfo.setEndDate(enddate);
			}
		}
	}
	public static String getSaturday(String acceptDate)throws Exception
	{
		Calendar c1 = Calendar.getInstance();
		c1.setTime(SysDateMgr.string2Date(acceptDate,SysDateMgr.PATTERN_STAND_YYYYMMDD));
		int week= c1.get(Calendar.DAY_OF_WEEK);		
		if(week==1)
		{			
			c1.add(Calendar.DATE, -1);
			return SysDateMgr.date2String(c1.getTime(),SysDateMgr.PATTERN_STAND_YYYYMMDD);
		}
		else{ 

			c1.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
			return SysDateMgr.date2String(c1.getTime(),SysDateMgr.PATTERN_STAND_YYYYMMDD);
		}
	}
	public static String getSunday(String acceptDate)throws Exception
	{
		Calendar c1 = Calendar.getInstance();
		c1.setTime(SysDateMgr.string2Date(acceptDate,SysDateMgr.PATTERN_STAND_YYYYMMDD));
		int week= c1.get(Calendar.DAY_OF_WEEK);
		c1.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		if (week==1)
		{
			return SysDateMgr.date2String(c1.getTime(),SysDateMgr.PATTERN_STAND_YYYYMMDD);
		}
		else
			return SysDateMgr.addDays(SysDateMgr.date2String(c1.getTime(),SysDateMgr.PATTERN_STAND_YYYYMMDD),7);
	}
	
	public static boolean isWeekend(String acceptDate)throws Exception
	{
		Calendar c1 = Calendar.getInstance();
		c1.setTime(SysDateMgr.string2Date(acceptDate,SysDateMgr.PATTERN_STAND_YYYYMMDD));
		int week= c1.get(Calendar.DAY_OF_WEEK);
		if ( week >=2 && week <= 6 )
		    return false;
		else
			return true;
	}
	public IDataset getUserDiscnt(String user_id, String param_attr,String eparchy_code) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", user_id);
		params.put("PARAM_ATTR", param_attr);
		params.put("EPARCHY_CODE", eparchy_code);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_COMMPARA_CODE", params);
	}
}
