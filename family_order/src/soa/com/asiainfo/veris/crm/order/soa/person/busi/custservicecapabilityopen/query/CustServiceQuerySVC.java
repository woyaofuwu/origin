
package com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.query;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserWhitCardChoiceSnInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;

public class CustServiceQuerySVC extends CSBizService
{


	/***
	 * PUK码查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
    public IData queryUserPUKCode(IData param) throws Exception
    {
    	String contactId = param.getString("CONTACT_ID","");//客服四期全网接触ID
        if (contactId != null && !"".equals(contactId)){  //客服四期 
        	 IDataset dataset = CSAppCall.call("SS.QueryInfosSVC.queryUserPUKCode", param);
             return dataset.getData(0);
         }
    	//校验入参
		CheckParam.checkUserPUKCode(param);

		CustServiceBean queryUserPUKCodeBean = (CustServiceBean) BeanManager.createBean(CustServiceBean.class);
		
		IData result = queryUserPUKCodeBean.queryUserPUKCode(param);
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "OK");
		result.put("X_RSPCODE","0000");
		result.put("X_RSPTYPE", "0");
		result.put("X_RSPDESC", "受理成功");
		result.put("INTF_TYPE", "01");
		return result;
    }


	/***
	 *备卡信息查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData queryUserSimBak(IData param) throws Exception
	{
    	String contactId = param.getString("CONTACT_ID","");//客服四期全网接触ID
        if (contactId != null && !"".equals(contactId)){  //客服四期 
        	 IDataset dataset = CSAppCall.call("SS.QueryInfosSVC.queryUserSimBak", param);
             return dataset.getData(0);
         }
		//校验入参
		CheckParam.checkQueryUserSimBak(param);

		CustServiceBean queryUserSimBakBean = (CustServiceBean) BeanManager.createBean(CustServiceBean.class);

		IData result = queryUserSimBakBean.queryUserSimBak(param).first();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "OK");
		result.put("X_RSPCODE","0000");
		result.put("X_RSPTYPE", "0");
		result.put("X_RSPDESC", "受理成功");
		result.put("INTF_TYPE", "01");
		return result;
	}

	/***
	 *国际漫游业务日套餐状态查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData queryUserInterRoamDay(IData param) throws Exception
	{
    	String contactId = param.getString("CONTACT_ID","");//客服四期全网接触ID
        if (contactId != null && !"".equals(contactId)){  //客服四期 
        	 IDataset dataset = CSAppCall.call("SS.QueryInfosSVC.queryUserInterRoamDay", param);
             return dataset.getData(0);
         }
		//校验入参
		CheckParam.checkQueryUserInterRoamDay(param);

		CustServiceBean queryUserInterRoamDayBean = (CustServiceBean) BeanManager.createBean(CustServiceBean.class);
		IData result = queryUserInterRoamDayBean.queryUserInterRoamDay(param).first();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "OK");
		result.put("X_RSPCODE","0000");
		result.put("X_RSPTYPE", "0");
		result.put("X_RSPDESC", "受理成功");
		result.put("INTF_TYPE", "01");
		return result;
	}


	/**
	 * 本地营销案查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData queryUserSaleActive(IData param) throws Exception
	{
    	String contactId = param.getString("CONTACT_ID","");//客服四期全网接触ID
        if (contactId != null && !"".equals(contactId)){  //客服四期 
        	 IDataset dataset = CSAppCall.call("SS.QueryInfosSVC.queryUserSaleActive", param);
             return dataset.getData(0);
         }
		//校验入参
		CheckParam.checkQueryUserSaleActive(param);

		CustServiceBean queryUserSaleActiveBean = (CustServiceBean) BeanManager.createBean(CustServiceBean.class);

		IData result = queryUserSaleActiveBean.queryUserSaleActive(param).first();
		result.put("INTF_TYPE", "01");
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		return result;
	}



	/**
	 * GPRS状态查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData queryUserGPRS(IData param) throws Exception {
    	String contactId = param.getString("CONTACT_ID","");//客服四期全网接触ID
        if (contactId != null && !"".equals(contactId)){  //客服四期 
        	 IDataset dataset = CSAppCall.call("SS.QueryInfosSVC.queryUserGPRS", param);
             return dataset.getData(0);
         }
		//校验入参
		CheckParam.checkQueryUserGPRS(param);

		CustServiceBean queryUserGPRSBean = (CustServiceBean) BeanManager.createBean(CustServiceBean.class);

		IData result = queryUserGPRSBean.getUserGPRS(param);
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "OK");
		result.put("X_RSPCODE", "0000");
		result.put("X_RSPTYPE", "0");
		result.put("X_RSPDESC", "受理成功");
		result.put("INTF_TYPE", "01");
		return result;
	}


	/**
	 * 充值卡记录查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData queryValueCardUse(IData param) throws Exception
	{
    	String contactId = param.getString("CONTACT_ID","");//客服四期全网接触ID
        if (contactId != null && !"".equals(contactId)){  //客服四期 
        	 IDataset dataset = CSAppCall.call("SS.QueryInfosSVC.valueCardUseQuery", param);
             return dataset.getData(0);
         }
		//校验入参
		CheckParam.checkQueryValueCardUse(param);

		CustServiceBean queryUserBean = (CustServiceBean) BeanManager.createBean(CustServiceBean.class);

		IDataset resultdata = queryUserBean.valueCardUseQuery(param);
		IData redata = new DataMap();		
		redata.put("OPR_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, SysDateMgr.getSysTime()));
		redata.put("RECHARGE_CHARD",resultdata);
		redata.put("INTF_TYPE", "01");
		return redata;
	}






	/**
	 * 已参加的营销活动查询
	 * @param data
	 * 必传手机号码SERIAL_NUMBER
	 * 可传PRODUCT_ID
	 * PROCESS_TAG 活动状态，传入0 为正常活动，传入9为所有活动
	 * START_DATE，END_DATE  开始，结束时间  'yyyy-mm-dd hh24:mi:ss'
	 * @return
	 * @throws Exception
	 */
	public IData getSaleActiveInfo(IData data) throws Exception
	{
		IData results = new DataMap();
		// 校验入参
		CheckParam.checkGetUserInfo(data);

		//验证身份凭证
		IData userData = CheckParam.isCheckIdentAuth(data);
		String userId = userData.getString("USER_ID");

		String eparchyCode = getUserEparchyCode();
		String serialNumber = data.getString("SERIAL_NUMBER");
		//获取用户USER_ID
		data.put("EPARCHY_CODE", eparchyCode);
		data.put("REMOVE_TAG", "0");
		data.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE","00"));


		UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);

		String startDate  = data.getString("START_DATE","2000-01-01" + SysDateMgr.START_DATE_FOREVER);
		String endDate    = data.getString("END_DATE", SysDateMgr.END_DATE_FOREVER);
		String processTag = data.getString("PROCESS_TAG","9");
		String productId = ucaData.getProductId();

		data.put("USER_ID", userId);
		data.put("START_DATE", startDate);
		data.put("END_DATE", endDate);
		data.put("PROCESS_TAG", processTag);
		data.put("PRODUCT_ID", data.getString("PRODUCT_ID","0"));

		IDataset saleActive = UserSaleActiveInfoQry.getSaleActiveInfoOnInterface(data);
		IData tradehData = new DataMap();
		String inModeCode = "0";
		for(int i=0;i<saleActive.size();i++)
		{
			IData theSaleActive = saleActive.getData(i);
			inModeCode = "0";
			String relationTradeId = theSaleActive.getString("RELATION_TRADE_ID", "");
			if(!("".equals(relationTradeId)))
			{
				tradehData.clear();
				tradehData.put("TRADE_ID", relationTradeId);
				/*IDataset tradeList = TradeHistoryInfoQry.getgetSaleActiveInfoTradeId(relationTradeId);
				if(tradeList != null && tradeList.size() > 0)
				{
					inModeCode = tradeList.getData(0).getString("IN_MODE_CODE", "0");
				}*/
			}
			/**
			 * 合版本   duhj 2017.5.3
			 */
			//theSaleActive.put("ACITVITY_NO",theSaleActive.getString("CAMPN_CODE",""));
			theSaleActive.put("ACITVITY_NO",theSaleActive.getString("PACKAGE_ID",""));
			//theSaleActive.put("ACITVITY_NAME",theSaleActive.getString("CAMPN_NAME",""));
			theSaleActive.put("ACITVITY_NAME",theSaleActive.getString("PACKAGE_NAME",""));
			theSaleActive.put("IN_MODE_CODE", inModeCode);
			theSaleActive.put("JOINED_DATE", SysDateMgr.decodeTimestamp(theSaleActive.getString("ACCEPT_DATE", ""), SysDateMgr.PATTERN_TIME_YYYYMMDD));
			theSaleActive.put("START_DATE", SysDateMgr.decodeTimestamp(theSaleActive.getString("START_DATE", ""), SysDateMgr.PATTERN_TIME_YYYYMMDD));
			theSaleActive.put("END_DATE", SysDateMgr.decodeTimestamp(theSaleActive.getString("END_DATE", ""), SysDateMgr.PATTERN_TIME_YYYYMMDD));
		}

		results.put("OPR_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, SysDateMgr.getSysTime()));
		results.put("JOINED_ACTIVITIES",saleActive);

		return results;
	}


	/**
	 * @Function: getMobileOrderSvc
	 * @Description: ITF_CRM_GETORDERSVC 一级BOSS手机客户端接口-已订购业务查询
	 * @param: @param data(ROUTE_EPARCHY_CODE、IDTYPE、IDVALUE)
	 * @param: @return
	 * @return：IData
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @throws Exception
	 * @date: 5:07:06 PM Jul 24, 2013 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* Jul 24, 2013 longtian3 v1.0.0 TODO:
	 */
	public IDataset getMobileOrderSvc(IData input) throws Exception
	{
		CheckParam.checkGetOrderSvc(input);
		CustServiceBean bean = BeanManager.createBean(CustServiceBean.class);
		IData data = bean.queryUserProductInfo(input).first();

		IDataset dataset = new DatasetList();
		dataset.add(data);
		return dataset;
	}

	/**
	 * @Function: getMobileUserInfo
	 * @Description: ITF_CRM_GetUserInfo4Mobile 一级BOSS手机客户端接口-个人信息查询
	 * @param: @param data
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 5:02:46 PM Jul 24, 2013 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* Jul 24, 2013 longtian3 v1.0.0 TODO:
	 */
	public IData getMobileUserInfo(IData input) throws Exception {

		IData ret = new DataMap();
		IDataset userInfos = new DatasetList();

		String sn = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		/**
		 * 如果是白卡放号流程，
		 * 伪造数据返回 成功；
		 * */
		IData whiteCardInfo = UserWhitCardChoiceSnInfoQry.qryWhiteCardChoiceSnInfoBySn(input.getString("SERIAL_NUMBER"));
		if (IDataUtil.isNotEmpty(whiteCardInfo))
		{
			IData userParam = new DataMap();
			userParam.put("","");
			userParam.put("CITY_CODE", "0898");
			userParam.put("STAR_TIME", whiteCardInfo.getString("START_DATE"));
			userParam.put("STAR_LEVEL", "12");
			userParam.put("STAR_SCORE", "0");

			userInfos.add(userParam);
			ret.put("USER_INFO",userInfos);
			ret.put("OPR_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, SysDateMgr.getSysTime()));
			ret.put("INTF_TYPE", "01");
			return ret;
		}


		String channelId = input.getString("CHANNEL_ID","");
		 if (channelId == null || "".equals(channelId)){
			 channelId = input.getString("BIZ_TYPE_CODE", "");
		 }
		if(!CustServiceHelper.isCustomerServiceChannel(channelId)){
			IDataset dataset = CSAppCall.call("SS.QueryInfoSVC.getMobileUserInfo", input);
            return dataset.getData(0);
		}

		CustServiceBean bean = BeanManager.createBean(CustServiceBean.class);
		IData userInfo = bean.getMobileUserInfo(input);
		IDataset infos = new DatasetList();
		IData queryData = new DataMap();

		//2.添加用户信誉度信息
		queryData = AcctCall.getUserCreditInfos("0", userInfo.getString("USER_UNIQUE")).getData(0);
		if ("-1".equals(queryData.getString("CREDIT_CLASS", "0"))) {
			userInfo.put("STAR_LEVEL", "0");
		} else {
			userInfo.put("STAR_LEVEL", queryData.getString("CREDIT_CLASS", "0"));
		}
		String starScore = queryData.getString("STAR_SCORE", "0");
		if ("".equals(starScore) || starScore == null) {
			starScore = "0";
		}
		userInfo.put("STAR_SCORE", starScore);
		if (!"0".equals(userInfo.getString("STAR_LEVEL"))) {
			String starTime = queryData.getString("STAR_TIME");
			userInfo.put("STAR_TIME", starTime);
		}

	    userInfo.putAll(CustServiceHelper.buildUserCreditInfo(userInfo));
	    
	    userInfo.put("CITY_CODE", "0898");//12月批次需求约定，海南省份的该字段返回 0898
	    
		userInfos.add(userInfo);
		ret.put("USER_INFO",userInfos);
		ret.put("OPR_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, SysDateMgr.getSysTime()));
		ret.put("INTF_TYPE", "01");
		return ret;
	}



	/**
	 * 最低消费查询
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public IData queryMiniExpenditure(IData inparams) throws Exception {
		CheckParam.checkGetUserInfo(inparams);
		IData result = new DataMap();
		CustServiceBean bean = BeanManager.createBean(CustServiceBean.class);

		IDataset infos = bean.queryMiniExpenditure(inparams);
		result.put("OPR_TIME",Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, SysDateMgr.getSysTime()));
		result.put("MINI_EXPENDITURE_INFO", infos);//miniExpenditureInfo
		return result;
	}

	/***
	 * 专项月租查询
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public IData querySpecialMonthRent(IData inparams) throws Exception {
		CheckParam.checkGetUserInfo(inparams);
		IData result = new DataMap();
		CustServiceBean bean = BeanManager.createBean(CustServiceBean.class);

		IDataset infos = bean.querySpecialMonthRent(inparams);
		result.put("OPR_TIME",Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, SysDateMgr.getSysTime()));
		result.put("SPECIAL_MONTH_RENT",infos);  //specialMonthRent
		return result;
	}
}
