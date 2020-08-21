package com.asiainfo.veris.crm.order.web.frame.csview.person.base;

import com.ailk.biz.util.StaticUtil;
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.BizEnv;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class PersonBasePage extends CSBasePage
{
	public IData userInfoView;

	public IData custInfoView;

	public IData acctInfoView;
	
	public static int AUTH_CACHE_TIMEOUT = 300;

	/**
	 * 业务规则校验
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void checkBeforeTrade(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset infos = CSViewCall.call(this, "CS.CheckTradeSVC.checkBeforeTrade", data);
		setAjax(infos.getData(0));

	}

	/**
	 * 校验新业务推荐信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void checkPushInfo(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		IData userInfo = new DataMap(data.getString("HINT_INFO"));

		// 加载三户资料
		String open_date = userInfo.getString("OPEN_DATE", "").substring(0, 10);
		String current_date = SysDateMgr.getSysTime().toString().substring(0, 10);

		if (current_date.equals(open_date))
		{
			userInfo.put("TRADE_TYPE_CODE_A", "A");
		}

		IDataset RecomdList = new DatasetList();
		IData tempdata = new DataMap();

		tempdata.putAll(userInfo);
		RecomdList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRecomdInfo", tempdata);

		IData authData = new DataMap();
		if (IDataUtil.isNotEmpty(RecomdList))
		{

			authData.put("PUSH_FLAG", "1");
		} else
		{
			authData.put("PUSH_FLAG", "0");
		}

		setAjax(authData);
	}

	/**
	 * 校验新业务推荐信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void checkPushInfoForUser360View(IRequestCycle cycle) throws Exception
	{
		IData pgData = getData();
		IData tempData = new DataMap();
		tempData.put("USER_ID", pgData.getString("USER_ID"));
		tempData.put(Route.ROUTE_EPARCHY_CODE, pgData.getString("ROUTE_EPARCHY_CODE"));
		// 加载三户资料
		IDataset userDataset = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoByUserId", tempData);
		if (IDataUtil.isEmpty(userDataset))
		{
			return;
		}
		IData userData = userDataset.getData(0);
		String open_date = userData.getString("OPEN_DATE", "").substring(0, 10);
		String current_date = SysDateMgr.getSysTime().toString().substring(0, 10);

		if (current_date.equals(open_date))
		{
			userData.put("TRADE_TYPE_CODE_A", "A");
		}

		tempData.clear();
		tempData.putAll(userData);
		IDataset RecomdList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRecomdInfo", tempData);

		IData authData = new DataMap();
		authData.put("USER_INFO", userData);
		if (IDataUtil.isNotEmpty(RecomdList))
		{

			authData.put("PUSH_FLAG", "1");
		} else
		{
			authData.put("PUSH_FLAG", "0");
		}

		setAjax(authData);
	}

	protected void cleanupAfterRender(IRequestCycle cycle)
	{
		setUserInfoView(null);
		setCustInfoView(null);
		setAcctInfoView(null);

		super.cleanupAfterRender(cycle);
	}

	/**
	 * 获取发展员工配置信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void getDevelopStaffConfig(IRequestCycle cycle) throws Exception
	{
		IData resultData = new DataMap();
		IData data = getData();
		IDataset configDataSet = CSViewCall.call(this, "CS.DevelopStaffSVC.getDevelopStaffConfig", data);
		if (null != configDataSet && configDataSet.size() > 0)
		{
			resultData = configDataSet.getData(0);
		}
		setAjax(resultData);
	}

	/**
	 * 客户信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void getHintInfo(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		/**
         * 密码解密
         * */
		logger.debug(">>>>>>PersonBasePage 密码解密<<<<<<<<<");
		String typeTypeCode=data.getString("TRADE_TYPE_CODE","");
		String custId=data.getString("CUST_ID","");
		String userId=data.getString("USER_ID","");
		String sn=data.getString("SERIAL_NUMBER","");
		if(typeTypeCode!=null && !"2101".equals(typeTypeCode)){
			String checkTag="0";
	    	IData indata = new DataMap();
			indata.put("PARAM_ATTR", "6899"); 
			IDataset idset = CSViewCall.call(this, "CS.GetInfosSVC.getCommCommparaSVC", indata);
			if(idset!=null && idset.size()>0){
				checkTag=idset.getData(0).getString("PARA_CODE1");
			}
			String tradeTypeCodeTag = "0"; //默认不拦截
			indata.put("TRADE_TYPE_CODE", typeTypeCode); 
	    	IDataset ids = CSViewCall.call(this, "CS.GetInfosSVC.getTradeCommparaSVC", indata);
			if(ids!=null && ids.size()>0){
				//tradeTypeCodeTag=ids.getData(0).getString("PARA_CODE1");
				if("1".equals(ids.getData(0).getString("PARA_CODE1")))	tradeTypeCodeTag = "1";
			}  
			
			
			
	    	//获取session_id
	    	String sessionId =cycle.getRequestContext().getRequest().getSession().getId();
	//        HttpSession session = SessionFactory.getInstance().getSession();
	//        if (session != null) {
	//        	sessionId = session.getId();
	//        }
	    	String AUTH_SPEC_UID= String.valueOf(SharedCache.get("USERIDCACHE"+sessionId));//如果是在别的页面鉴权过的，这个值可能为空
	    	String AUTH_SPEC_CID=String.valueOf(SharedCache.get("CUSTIDCACHE"+sessionId));//如果是在别的页面鉴权过的，这个值可能为空
	    	String AUTH_SPEC_SN=String.valueOf(SharedCache.get("SERIALNUMBERCACHE"+sessionId));//这个一定会有。业务上一定将这个值传过来比对
	    	String authState=String.valueOf(SharedCache.get("AUTHSTATECACHE"+sessionId));//校验状态标记
	    	if("0".equals(authState)){//必须鉴权的才进行校验
	    		String checkPassTag=String.valueOf(SharedCache.get("CHECKPASSTAG"+sessionId));//校验通过标记
	    		if(checkPassTag==null || "".equals(checkPassTag) || !"0".equals(checkPassTag)){//必须鉴权的情况下，如果不是鉴权通过（=0通过)来到这里，则有问题
	    			logger.error(">>>>>>PersonBasePage error<<<<<<<<<checkPassTag="+checkPassTag+"<<<<<<<<checkPassTag is error>>>>>>>>>>typeTypeCode="+typeTypeCode);
	        		if(!"0".equals(checkTag)&& !"0".equals(tradeTypeCodeTag)){
	        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
	        		}
	    		} 
	    	}
		    if(userId!=null && !"".equals(userId)){ 
		    	if(StringUtils.isNotBlank(AUTH_SPEC_UID) && !"null".equals(AUTH_SPEC_UID) && AUTH_SPEC_UID.indexOf("xxyy")>-1){ 
		        	Des desObj = new Des();
		        	AUTH_SPEC_UID=desObj.getDesPwd(AUTH_SPEC_UID);
		        	if(!AUTH_SPEC_UID.equals(userId)){
		        		logger.error(">>>>>>PersonBasePage error<<<<<<<<<AUTH_SPEC_UID="+AUTH_SPEC_UID+"<<<<<<<<AUTH_SPEC_UID is error>>>>>>>>>>typeTypeCode="+typeTypeCode);
		        		if(!"0".equals(checkTag)&& !"0".equals(tradeTypeCodeTag)){
		        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
		        		}
		        	}
		    	}else{
		    		logger.error(">>>>>>PersonBasePage error<<<<<<<<<AUTH_SPEC_UID="+AUTH_SPEC_UID+"<<<<<<<<AUTH_SPEC_UID is null>>>>>>>>>>typeTypeCode="+typeTypeCode);
		    		Des desObj = new Des();
		    		AUTH_SPEC_UID=desObj.setEncPwd(data.getString("USER_ID",""))+"xxyy"; 
					SharedCache.set("USERIDCACHE"+sessionId, AUTH_SPEC_UID, AUTH_CACHE_TIMEOUT); //放到缓存
		    	}
		    }
		    if(custId!=null && !"".equals(custId)){ 
		    	if(StringUtils.isNotBlank(AUTH_SPEC_CID) && !"null".equals(AUTH_SPEC_CID) && AUTH_SPEC_CID.indexOf("xxyy")>-1){ 
		        	Des desObj = new Des();
		        	AUTH_SPEC_CID=desObj.getDesPwd(AUTH_SPEC_CID);
		        	if(!AUTH_SPEC_CID.equals(custId)){
		        		logger.error(">>>>>>PersonBasePage error<<<<<<<<<AUTH_SPEC_CID="+AUTH_SPEC_CID+"<<<<<<<<AUTH_SPEC_CID is error>>>>>>>>>>typeTypeCode="+typeTypeCode);
		        		if(!"0".equals(checkTag)&& !"0".equals(tradeTypeCodeTag)){
		        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
		        		}
		        	}
		    	} else{
		    		logger.error(">>>>>>PersonBasePage error<<<<<<<<<AUTH_SPEC_CID="+AUTH_SPEC_CID+"<<<<<<<<AUTH_SPEC_CID is null>>>>>>>>>>typeTypeCode="+typeTypeCode);
		    		Des desObj = new Des();
		    		AUTH_SPEC_CID=desObj.setEncPwd(data.getString("CUST_ID",""))+"xxyy"; 
					SharedCache.set("CUSTIDCACHE"+sessionId, AUTH_SPEC_CID, AUTH_CACHE_TIMEOUT); //放到缓存
		    	}
		    }
		    if(sn!=null && !"".equals(sn)){ 
		    	if(StringUtils.isNotBlank(AUTH_SPEC_SN) && !"null".equals(AUTH_SPEC_SN) && AUTH_SPEC_SN.indexOf("xxyy")>-1){ 
		        	Des desObj = new Des();
		        	AUTH_SPEC_SN=desObj.getDesPwd(AUTH_SPEC_SN);
		        	if(!AUTH_SPEC_SN.equals(sn)&& !"0".equals(tradeTypeCodeTag)){
		        		logger.error(">>>>>>PersonBasePage error<<<<<<<<<AUTH_SPEC_SN="+AUTH_SPEC_SN+"<<<<<<<<AUTH_SPEC_SN is error>>>>>>>>>>typeTypeCode="+typeTypeCode);
	            		if(!"0".equals(checkTag)){
	            			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
	            		}
		        	}
		    	}else{
		    		logger.error(">>>>>>PersonBasePage error<<<<<<<<<AUTH_SPEC_SN="+AUTH_SPEC_SN+"<<<<<<<<AUTH_SPEC_SN is null>>>>>>>>>>typeTypeCode="+typeTypeCode);
		    		Des desObj = new Des();
		    		String serialNum_enc=desObj.setEncPwd(data.getString("SERIAL_NUMBER",""))+"xxyy"; 
					SharedCache.set("SERIALNUMBERCACHE"+sessionId, serialNum_enc, AUTH_CACHE_TIMEOUT); //放到缓存
		    	} 
	    	} 
    	} 
		IData hintInfo = new DataMap();
		hintInfo.put("RESULT_CODE", "1");
		data.put("TRADE_STAFF_ID", getVisit().getStaffId());
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset hintInfos = CSViewCall.call(this, "CS.HintInfoSVC.getHintInfo", data);
		if (IDataUtil.isNotEmpty(hintInfos))
		{
			hintInfo = hintInfos.getData(0);
		}
		setAjax(hintInfo);
	}

	/**
	 * 获取业务受理费用列表
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void getTradeFee(IRequestCycle cycle) throws Exception
	{
		IData data = getData();

		IData inparam = new DataMap();
		inparam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
		inparam.put("PRODUCT_ID", data.getString("PRODUCT_ID", "-1"));
		inparam.put("VIP_CLASS_ID", data.getString("VIP_CLASS_ID", "Z"));
		inparam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset feeList = CSViewCall.call(this, "CS.FeeMgrSVC.getTradeFee", inparam);
		if (IDataUtil.isEmpty(feeList))
		{
			feeList = new DatasetList();
		}
		setAjax(feeList);
	}

	public void setAcctInfoView(IData acctInfoView)
	{
		this.acctInfoView = acctInfoView;
	}

	public void setCustInfoView(IData custInfoView)
	{
		this.custInfoView = custInfoView;
	}

	/**
	 * 认证完后，得到三户信息，刷新界面基本信息展示块
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void setUCAViewInfos(IRequestCycle cycle) throws Exception
	{
		IData data = getData();

		IData ucaParams = new DataMap(data.getString("UCAInfoParam"));

		IData userInfo = new DataMap(ucaParams.getString("USER_INFO"));
		userInfo.put("BRAND_NAME", UpcViewCall.getBrandNameByBrandCode(this, userInfo.getString("BRAND_CODE")));
		userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", userInfo.getString("PRODUCT_ID")));

		setUserInfoView(userInfo);
		setCustInfoView(new DataMap(ucaParams.getString("CUST_INFO")));
		setAcctInfoView(new DataMap(ucaParams.getString("ACCT_INFO")));
	}

	public void setUserInfoView(IData userInfoView)
	{
		this.userInfoView = userInfoView;
	}

	/**
	 * @chenxy3@REQ201501130023 关于密码修改身份证件校验的需求 2015-1-20
	 *                          要求配置在COMMPARA表中的6890的业务要单独鉴权，即每次点查询都要认证
	 * */
	public void awayCheck(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		String tradeTypecode = data.getString("TRADE_TYPE_CODE");
		String serialNumber = data.getString("SERIAL_NUMBER");
		IData indata = new DataMap();
		indata.put("TRADE_TYPE_CODE", tradeTypecode);
		indata.put("SERIAL_NUMBER", serialNumber);
		IDataset idset = CSViewCall.call(this, "CS.GetInfosSVC.getCommparaSVC", indata);
		if (idset != null && idset.size() > 0)
		{
			String para_code1 = idset.getData(0).getString("PARA_CODE1");
			if (!"".equals(para_code1) && "1".equals(para_code1) && !"SUPERUSR".equals(getVisit().getStaffId()))
			{
				data.put("AWAYCHECK", "1");
			} else
			{
				data.put("AWAYCHECK", "0");
			}
		}
		setAjax(data);
	}

	/**
	 * 老用户免费领取4G手机营销活动
	 * */
	public void checkProdNeedOldCustSn(IRequestCycle cycle) throws Exception
	{
		IData params = getData();
		IData rtnData = new DataMap();
		String ifNeedCheckProd = "N";
		String ifNewUserActive="";
		String productId = params.getString("PRODUCT_ID", "");
		String checkType = params.getString("CHECK_TYPE", "");
		if ("CHECKPROD".equals(checkType))
		{
			params.put("PARAM_ATTR", "9957");
			IDataset commpara2017 = CSViewCall.call(this, "SS.SaleActiveCheckSnSVC.check2017ActiveCommpara", params);
			
			if(IDataUtil.isNotEmpty(commpara2017)){
				/**
				 * 2017年老客户感恩大派送活动开发需求
				 * 20170906 chenxy3
				 * */
				for(int j=0; j < commpara2017.size(); j++){
					String commProd = commpara2017.getData(j).getString("PARAM_CODE", "");
					if (productId.equals(commProd))
					{
						ifNeedCheckProd = "Y";
						ifNewUserActive="NEW";
						break;
					}
				}
			}else{
				IDataset commparas = CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.checkProdParaInfo", params);
		    	if (logger.isDebugEnabled())
		            logger.debug(">>>>> 进入 PersonBasePage>>>>>commparas:"+commparas);
				if (commparas != null && commparas.size() > 0)
				{
					for (int i = 0; i < commparas.size(); i++)
					{
						String commProd = commparas.getData(i).getString("PARAM_CODE", "");
						if (productId.equals(commProd))
						{
							ifNeedCheckProd = "Y"; 
							break;
						} 
					}
				}
			} 

			
			IData results = CSViewCall.call(this, "CS.SaleActiveQuerySVC.checkCreditPurchases", params).first();
			rtnData.put("CREDIT_PURCHASES", results.getString("CREDIT_PURCHASES"));
			
			rtnData.put("IF_NEED", ifNeedCheckProd);
			rtnData.put("IF_NEW_USER_ACTIVE", ifNewUserActive);
		}
		else if("CHECKPACK".equals(checkType))
		{
			String packageId = params.getString("PACKAGE_ID", "");
			IDataset commparas = CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.checkProdParaInfo", params);
	    	if (logger.isDebugEnabled())
	            logger.debug(">>>>> 进入 PersonBasePage>>>>>commparas:"+commparas+",packageId:"+packageId);
			if (commparas != null && commparas.size() > 0)
			{
				for (int i = 0; i < commparas.size(); i++)
				{
					String commProd = commparas.getData(i).getString("PARAM_CODE", "").substring(0, 8);
					String commPack = commparas.getData(i).getString("PARAM_CODE", "").substring(9, 17);
					if (logger.isDebugEnabled())
			            logger.debug(">>>>> 进入 PersonBasePage>>>>>commProd:"+commProd+",commPack:"+commPack);
					if (productId.equals(commProd) && packageId.equals(commPack))
					{
						ifNeedCheckProd = "Y";
						break;
					}
				}
			}
			IDataset commparasSIM = CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.checkSIMInfo", params);
			if (commparasSIM != null && commparasSIM.size() > 0)
			{
				ifNeedCheckProd = "SIM";
				CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.sendSMSTOhebao", params);
				
			}
			rtnData.put("IF_NEED", ifNeedCheckProd);
		
		}else if("NEW_USER_ACTIVE".equals(checkType))
		{/**
		* 2017年老客户感恩大派送活动开发需求
		* 20170906 chenxy3
		* */ 
			IDataset userProds = CSViewCall.call(this, "SS.SaleActiveCheckSnSVC.check2017ActiveInfo", params);
			if (logger.isDebugEnabled())
	            logger.debug(">>>>> 进入 PersonBasePage>>>>>userProds:"+userProds);
			if (userProds != null && userProds.size() > 0)
			{
				rtnData = userProds.getData(0);
			}
		}
		else
		{
			IDataset userProds = CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.checkIfUseHaveNeedProd", params);
			if (logger.isDebugEnabled())
	            logger.debug(">>>>> 进入 PersonBasePage>>>>>userProds:"+userProds);
			if (userProds != null && userProds.size() > 0)
			{
				rtnData = userProds.getData(0);
			}
		}
		this.setAjax(rtnData);
	}
	
	//查询红包余额--wangsc10--20190311--start
	public void queryProdRedMoney(IRequestCycle cycle) throws Exception
	{
		IData rtnData = new DataMap();
		IData params = getData();
		String SMS_CODE = params.getString("SMS_CODE", "");
		String RED_CODE = "";
		String RED_YE = "0";
		
		IDataset queryRedMoney = CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.queryRedMoney", params);
		if(queryRedMoney != null && queryRedMoney.size() > 0){
			RED_CODE = queryRedMoney.getData(0).getString("RED_CODE", "");
			RED_YE = queryRedMoney.getData(0).getString("RED_YE", "0");
		}
		rtnData.put("RED_CODE", RED_CODE);
		rtnData.put("SMS_CODE", SMS_CODE);
		rtnData.put("RED_YE", RED_YE);
		this.setAjax(rtnData);
	}
	//end
	/**
	 * 实时营销
	 * <p>
	 * Title: newcheckPushInfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-7 下午08:22:22
	 */
	public void newcheckPushInfo(IRequestCycle cycle) throws Exception
	{
		
		IData data = getData();
		IData userInfo = new DataMap(data.getString("HINT_INFO"));

		// 加载三户资料
		String open_date = userInfo.getString("OPEN_DATE", "").substring(0, 10);
		String current_date = SysDateMgr.getSysTime().toString().substring(0, 10);

		if (current_date.equals(open_date))
		{
			userInfo.put("TRADE_TYPE_CODE_A", "A");
		}
		IData authData = new DataMap();
		String serialNum = userInfo.getString("SERIAL_NUMBER");
		String userId = userInfo.getString("USER_ID");
		String tradeStaffId = getVisit().getStaffId();
		
		StringBuilder sb = new StringBuilder(1000);
        sb.append("REALTIMEMARKETINGSESSION_").append(serialNum).append("_").append(userId).append("_").append(tradeStaffId);
        String cacheKey = sb.toString();
        IDataset listEvERelationLimitCache = (IDataset) SharedCache.get(cacheKey);
        if(IDataUtil.isNotEmpty(listEvERelationLimitCache))
        {
        	authData.put("PUSH_FLAG", "0");
    		setAjax(authData);
        	return;
        }
		

		IDataset RecomdList = new DatasetList();
		IData tempdata = new DataMap();
		tempdata.putAll(userInfo);
		RecomdList = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.newgetRecomdInfo", tempdata);

		IDataset rst = new DatasetList();
		IData rt = new DataMap();
		rt.put("SERIAL_NUMBER", serialNum);
		rt.put("USER_ID", userId);
		rt.put("TRADE_STAFF_ID", tradeStaffId);
		rst.add(rt);
		SharedCache.delete(cacheKey);
		if(StringUtils.isNotBlank(BizEnv.getEnvString("REALTIMEMARKETINGSESSION")))
		{
			AUTH_CACHE_TIMEOUT = Integer.parseInt(BizEnv.getEnvString("REALTIMEMARKETINGSESSION"));
		}
		
        SharedCache.set(cacheKey, rst, AUTH_CACHE_TIMEOUT);
		
        authData.put("PUSH_FLAG", "0");
		setAjax(authData);
	}

	/**
	 * 客户资料综合查询-实时营销
	 * <p>
	 * Title: newcheckPushInfoForUser360View
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-4-17 下午08:37:02
	 */
	public void newcheckPushInfoForUser360View(IRequestCycle cycle) throws Exception
	{
		IData pgData = getData();
		IData tempData = new DataMap();
		tempData.put("USER_ID", pgData.getString("USER_ID"));
		tempData.put(Route.ROUTE_EPARCHY_CODE, pgData.getString("ROUTE_EPARCHY_CODE"));
		// 加载三户资料
		IDataset userDataset = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoByUserId", tempData);
		if (IDataUtil.isEmpty(userDataset))
		{
			return;
		}
		IData userData = userDataset.getData(0);
		String open_date = userData.getString("OPEN_DATE", "").substring(0, 10);
		String current_date = SysDateMgr.getSysTime().toString().substring(0, 10);
		userData.put("TRADE_TYPE_CODE", "2101");
		if (current_date.equals(open_date))
		{
			userData.put("TRADE_TYPE_CODE_A", "A");
		}
		
		IData authData = new DataMap();
		String serialNum = userData.getString("SERIAL_NUMBER");
		String userId = userData.getString("USER_ID");
		String tradeStaffId = getVisit().getStaffId();
		
		StringBuilder sb = new StringBuilder(1000);
        sb.append("REALTIMEMARKETINGSESSION_").append(serialNum).append("_").append(userId).append("_").append(tradeStaffId);
        String cacheKey = sb.toString();
        IDataset listEvERelationLimitCache = (IDataset) SharedCache.get(cacheKey);
        if(IDataUtil.isNotEmpty(listEvERelationLimitCache))
        {
        	authData.put("USER_INFO", userData);
        	authData.put("PUSH_FLAG", "0");
    		setAjax(authData);
        	return;
        }

		tempData.clear();
		tempData.putAll(userData);
		IDataset RecomdList = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.newgetRecomdInfo", tempData);

		IDataset rst = new DatasetList();
		IData rt = new DataMap();
		rt.put("SERIAL_NUMBER", serialNum);
		rt.put("USER_ID", userId);
		rt.put("TRADE_STAFF_ID", tradeStaffId);
		rst.add(rt);
		SharedCache.delete(cacheKey);
		if(StringUtils.isNotBlank(BizEnv.getEnvString("REALTIMEMARKETINGSESSION")))
		{
			AUTH_CACHE_TIMEOUT = Integer.parseInt(BizEnv.getEnvString("REALTIMEMARKETINGSESSION"));
		}
		
        SharedCache.set(cacheKey, rst, AUTH_CACHE_TIMEOUT);
		
		authData.put("USER_INFO", userData);
		authData.put("PUSH_FLAG", "0");
		setAjax(authData);
	}

    /**
     * 页面获取导航按钮配置信息
     * @param cycle
     * @throws Exception
     */
    public void loadFnNavButtons(IRequestCycle cycle) throws Exception {
        IData inParam = getData();
        IDataset buttonInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.loadNavButtonConfig", inParam);
        if (IDataUtil.isEmpty(buttonInfo)) {
            buttonInfo = new DatasetList();
        }
        setAjax(buttonInfo);
    }

	private static Logger logger = Logger.getLogger(PersonBasePage.class);

	/**
	 * 规则页面
	 * @param cycle
	 * @throws Exception
	 */
	public void tpRulePageCheck(IRequestCycle cycle) throws Exception{
		String showTag = StaticUtil.getStaticValue(this.getVisit(),"TD_S_COMMPARA",
				new java.lang.String[]{"SUBSYS_CODE","PARAM_ATTR","PARAM_CODE","PARA_CODE1"},
				"PARA_CODE2",
				new java.lang.String[]{"CSM","812","TP_FUNC_SWITCH","1000"});
		IData ajax = new DataMap();
		ajax.put("SHOW_TAG",showTag);
		setAjax(ajax);
	}
}
