package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserCreditInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;

public class ScoreExchangeInFilter implements IFilterIn {

	@Override
	public void transferDataInput(IData input) throws Exception {
		
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String ruleId = IDataUtil.chkParam(input, "RULE_ID");
		String count = IDataUtil.chkParam(input, "COUNT");

		//获取用户资料，判断是否存在用户信息
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(ScoreException.CRM_SCORE_11);//1
		}
		
		//判断是否存在积分兑换规则
		IDataset ruleData = ExchangeRuleInfoQry.queryByRuleId(ruleId, CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(ruleData))
        {
            CSAppException.apperr(ScoreException.CRM_SCORE_12);//2
        }
        
        //String rsrvStr1 = ruleData.getData(0).getString("RSRV_STR1","");//获取对VIP级别限定规则
		String rsrvStr2 = ruleData.getData(0).getString("RSRV_STR2","");//获取对用户地域限定规则
		String rsrvStr5 = ruleData.getData(0).getString("RSRV_STR5","");//获取对用户优惠限定的规则
		String rsrvStr6 = ruleData.getData(0).getString("RSRV_STR6","");//获取对星级限定的规则
		
		Boolean isMember = false;//是否是特殊名单中的号码
		IData troopParam = new DataMap();
		troopParam.put("USER_ID", userInfo.getString("USER_ID"));
		troopParam.put("TROOP_ID","201505040001");
		IDataset isInTroop = Dao.qryByCode("TF_SM_TROOP_MEMBER", "SEL_BY_USERID_TROOPID", troopParam);
		if(IDataUtil.isNotEmpty(isInTroop))
		{
			isMember = true;
		}
		
		if(!isMember)
		{
			/***   REQ201502060007 关于俱乐部活动修改目标客户群及积分值需求   取消了大客户的判断 增加了星级的判断
			//如果存在对VIP级别限定规则
			if(StringUtils.isNotBlank(rsrvStr1))
			{
				//获取大客户信息
	    		IDataset custDs = CustVipInfoQry.getCustVipByUserId(userInfo.getString("USER_ID"), "0");
	    		
	    		//目标客户如果不是VIP大客户
	    		if(IDataUtil.isEmpty(custDs))
	    		{
	    			CSAppException.apperr(ScoreException.CRM_SCORE_13);//3
	    		}
	    		else
	    		{
	    			//获取CUST表中的用户VIP数据
	    			IData custVipInfo = custDs.getData(0);
	    			
	    			String custVipClass = "XXXX";
	    			custVipClass = custVipInfo.getString("VIP_TYPE_CODE", "-1") + "#" + custVipInfo.getString("VIP_CLASS_ID", "-1");

	    			if (rsrvStr1.indexOf(custVipClass) < 0)
	    			{
	        			CSAppException.apperr(ScoreException.CRM_SCORE_14);//4
	        		}
	    		}
			}
			//VIP级别限定规则结束
			 * ***/
			
			//如果存在对用户地域限定规则
			if(StringUtils.isNotBlank(rsrvStr2))
			{
				String custVipCity = "";
				//获取“当前业务区”  参考的“客户资料综合查询”模块中的，“当前业务区”取法 来源自GetUser360ViewSVC.java（70434版本）中的qryUserInfo
				input.put("USER_ID", userInfo.getString("USER_ID"));
	            IDataset userCityInfo = Qry360InfoDAO.qryUserCityInfo(input);
	            if(StringUtils.isNotBlank(userInfo.getString("CITY_CODE")) && IDataUtil.isNotEmpty(userCityInfo))
	            {
	            	custVipCity = userCityInfo.getData(0).getString("CITY_CODE", "");
	            }
	            if(StringUtils.isNotBlank(userInfo.getString("CITY_CODE")) && IDataUtil.isEmpty(userCityInfo))
	            {
	            	custVipCity = userInfo.getString("CITY_CODE","");
	            }
	            
	            /**该方案存在bug，使用下面的方案
				if (rsrvStr2.indexOf(custVipCity) < 0)
				{
	    			CSAppException.apperr(ScoreException.CRM_SCORE_15,rsrvStr2);//5
	    		}
	    		**/
				
	            //新方案
	            Boolean equalState = false;//默认不存在匹配的
				String[] rsrvStrArray = StringUtils.split(rsrvStr2, "|");
				
				for (int i = 0; i < rsrvStrArray.length; i++){
					if(rsrvStrArray[i].equals(custVipCity))
					{
						equalState = true;
					}
				}
				
				if(!equalState)//如果没有匹配的业务区则报错
				{
					CSAppException.apperr(ScoreException.CRM_SCORE_15);//5
				}
			}
			//用户地域限定规则结束
	        
			//如果存在对用户优惠限定的规则
			if(StringUtils.isNotBlank(rsrvStr5))
			{
				//获取用户的优惠，一次判断是否存在rsrvStr5配置的|270|655|这类格式的
				IDataset userDiscntList = UserDiscntInfoQry.getAllValidDiscntByUserId(userInfo.getString("USER_ID"));
				for (int index = 0, size = userDiscntList.size(); index < size; index++)
				{
					IData userDiscnt = userDiscntList.getData(index);
					String discntCode = userDiscnt.getString("DISCNT_CODE");
					
					/**该方案存在bug，使用下面的方案
					if (rsrvStr5.indexOf(discntCode) > 0)
					{
						CSAppException.apperr(ScoreException.CRM_SCORE_13);//3
					}
					**/
					
					//新方案
					Boolean equalState = false;//默认不存在匹配的
					String[] rsrvStrArray = StringUtils.split(rsrvStr5, "|");
					
					for (int i = 0; i < rsrvStrArray.length; i++){
						if(rsrvStrArray[i].equals(discntCode))
						{
							equalState = true;
						}
					}
					
					if(equalState)//如果用户存在匹配的优惠，则报错
					{
						CSAppException.apperr(ScoreException.CRM_SCORE_13);//3
					}
				}
			}
			//用户优惠限定的规则结束
			
			//如果存在对星级限定的规则
			if(StringUtils.isNotBlank(rsrvStr6))
			{
				IData userCredit = UserCreditInfoQrySVC.getUserCreditInfos(userInfo.getString("USER_ID"));
				String userCreditValue = userCredit.getString("CREDIT_CLASS");

				/**该方案存在bug，使用下面的方案
				if(rsrvStr6.indexOf(userCreditValue) < 0)
				{
					CSAppException.apperr(ScoreException.CRM_SCORE_14);//4
				}
				**/
				
				//新方案
				Boolean equalState = false;//默认不存在匹配的
				String[] rsrvStrArray = StringUtils.split(rsrvStr6, "|");
				
				for (int i = 0; i < rsrvStrArray.length; i++){
					if(rsrvStrArray[i].equals(userCreditValue))
					{
						equalState = true;
					}
				}
				
				if(!equalState)//如果用户没有匹配的星级则报错
				{
					CSAppException.apperr(ScoreException.CRM_SCORE_14);//4
				}
				
			}
			//星级限定的规则结束
		}
        
		
        //活动有效期限定
		IDataset checkActiveTimes = CommparaInfoQry.get333ComparaByCodeAndValue("CSM", "333", ruleId, "ZZZZ");
		
		if(IDataUtil.isNotEmpty(checkActiveTimes)){
			String sysdate = SysDateMgr.getSysTime();
			String startTime = checkActiveTimes.getData(0).getString("PARA_CODE26");
	        String endTime = checkActiveTimes.getData(0).getString("PARA_CODE27");
	        
	        if(StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)){
	        	//无活动有效期配置
	        	CSAppException.apperr(ScoreException.CRM_SCORE_16);//6 未转义给短厅
	        }
	        
	        if ((sysdate.compareTo(startTime) < 0) || (sysdate.compareTo(endTime) > 0))
            {
	        	//活动有效期已过
	        	CSAppException.apperr(ScoreException.CRM_SCORE_17);//7
            }
	        
			//有效期内是否办理过活动
			IDataset scoreTrade = TradeScoreInfoQry.getTradeScoreTradeBySn(ruleId,startTime,endTime,serialNumber,"movies");
			if(IDataUtil.isNotEmpty(scoreTrade))
			{
				CSAppException.apperr(ScoreException.CRM_SCORE_18);//8
			}
			
			
			
			//获取扩展规则
			IDataset extendRules=CommparaInfoQry.getCommparaInfos("CSM", "330", ruleId);
			if(IDataUtil.isNotEmpty(extendRules)){
				IData extendRule=extendRules.getData(0);
				
				//验证用户在活动期间兑换的积分次数
				IData scoreParam=new DataMap();
				scoreParam.put("TRADE_ELEMENT", ruleId);
				scoreParam.put("BEGIN_DATE", startTime);
				scoreParam.put("END_DATE", endTime);
				int exchangeTimes=TradeScoreInfoQry.queryTradeScoreRecode(scoreParam);
						
				//规则配置在活动期间只能兑换的次数
				String maxExhcangeTimes=extendRule.getString("PARA_CODE2","");
				if(!maxExhcangeTimes.equals("")){
					int maxExhcangeTimesInt=Integer.parseInt(maxExhcangeTimes);
					if(exchangeTimes>=maxExhcangeTimesInt){
						CSAppException.apperr(ScoreException.CRM_SCORE_23);
					}
				}
			}
			
			
		}
		//活动有效期限定规则结束
		
		//积分判断
		String score = "0";
		//查用户积分
		IDataset scoreInfo = AcctCall.queryUserScore(userInfo.getString("USER_ID"));
		if (IDataUtil.isEmpty(scoreInfo))
		{
			// 获取用户积分无数据!
			CSAppException.apperr(ScoreException.CRM_SCORE_19);//9
		}
	        
		if(StringUtils.isNotBlank(scoreInfo.getData(0).getString("SUM_SCORE")))
		{
			score = scoreInfo.getData(0).getString("SUM_SCORE");
		}
	        
		int exchangeScore = Integer.parseInt(count) * Integer.parseInt(ruleData.getData(0).getString("SCORE"));
		if(Integer.parseInt(score) < exchangeScore)
		{
			//用户积分为[%s],所需积分最少为[%s],不足本次兑换！
			CSAppException.apperr(ScoreException.CRM_SCORE_20, score, exchangeScore);//10
		}
		
	}
}
