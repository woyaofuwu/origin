package com.asiainfo.veris.crm.order.soa.person.busi.wechatScore;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.WeChatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.WeChatScoreQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserCreditInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;
import com.ailk.org.apache.commons.lang3.StringUtils;

public class WeChatScoreSVC extends CSBizService{

	
	private static final long serialVersionUID = 1L;
	private static transient Logger logger = Logger.getLogger(WeChatScoreSVC.class);
	
	/**
	 * 查询客户信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData queryCustInfo(IData param)throws Exception{
		WeChatScoreBean bean = BeanManager.createBean(WeChatScoreBean.class);
		
		IData result = new DataMap();
		if(IDataUtil.isEmpty(param)){
			result.put("result", "false");
    		result.put("notice", "参数为空！");
    		return result;
		}
		
		if(param.getString("Msisdn","").equals("")){
			result.put("result", "false");
    		result.put("notice", "传入的手机号为空！");
    		return result;
		}
		
		IData custInfo=bean.queryCustInfo(param.getString("Msisdn"));
		
		if(IDataUtil.isEmpty(custInfo)){
			result.put("result", "false");
    		result.put("notice", "客户信息有可能已经失效，未获取客户信息！");
    		return result;
		}
		
		
		custInfo.put("result", "true");
		custInfo.put("notice", "操作成功");
		
		return custInfo;
		
	}
	
	/**
	 * 修改用户信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData modifyCustInfo(IData param)throws Exception{
		String serialNumber=param.getString("Msisdn","");
		String birthday=param.getString("Birthday","");
		String sex=param.getString("Sex","");
		
		//性别的枚举值厂家未定
		
		
		IData result = new DataMap();
		
		if(IDataUtil.isEmpty(param)){
			result.put("result", "false");
    		result.put("notice", "参数为空！");
    		return result;
		}
		if(serialNumber.equals("")){
			result.put("result", "false");
    		result.put("notice", "参数手机号为空！");
    		return result;
		}
		if(birthday.equals("")&&sex.equals("")){
			result.put("result", "false");
    		result.put("notice", "参数生日和性别不能同时为空！");
    		return result;
		}
//		if(){
//			result.put("X_RESULTCODE", "395");
//    		result.put("X_RECORDNUM", "1");
//    		result.put("X_RESULTINFO", "参数性别为空！");
//    		return result;
//		}
		
		if(!sex.equals("")){
			if(sex.equals("1")){
				sex="M";
			}else{
				sex="F";
			}
		}
		
		
		IData input=new DataMap();
		
		//1.查询客户信息， 并将客户信息设置到参数中
		IDataset custInfoList = CustomerInfoQry.queryCustInfoBySN(serialNumber);
		if(custInfoList.isEmpty()){
			result.put("result", "false");
    		result.put("notice", "获取客户信息无数据！");
    		return result;
		}
		IData custInfo = UcaInfoQry.qryPerInfoByCustId(custInfoList.getData(0).getString("CUST_ID"));
		if(custInfo.isEmpty()){
			result.put("result", "false");
    		result.put("notice", "获取客户详细信息无数据！");
    		return result;
		}
		
		input.put("USE", custInfo.getString("RSRV_STR5",""));
		input.put("USE_PSPT_TYPE_CODE", custInfo.getString("RSRV_STR6",""));
		input.put("USE_PSPT_ID", custInfo.getString("RSRV_STR7",""));
		input.put("USE_PSPT_ADDR", custInfo.getString("RSRV_STR8",""));
		
		custInfo.put("CITY_CODE_A", custInfoList.getData(0).getString("CITY_CODE_A"));
		input.putAll(custInfo);
//		input.putAll(custInfoList.getData(0));
		
		
		if(!sex.equals(""))
			input.put("SEX", sex);
		if(!birthday.equals(""))
			input.put("BIRTHDAY", birthday);
		
		
		//3.登记客户信息
		input.put("SERIAL_NUMBER",serialNumber);
		CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", input);
		
		result.put("result", "true");
		result.put("notice", "操作成功");
    		
		return result;
	}
	
	public IDataset queryWeChatScoreRequest(IData param) throws Exception
    {
		String isCheck=param.getString("NORMAL_USER_CHECK", "");
		String serialNumber=param.getString("SERIAL_NUMBER");
		
		boolean isEmpty=false;
		if(isCheck.equals("on")){	//勾选了在网用户，查询在网用户信息
        	IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
        	if(IDataUtil.isNotEmpty(userInfo)){
        		param.put("USER_ID", userInfo.getString("USER_ID"));
        	}else{
        		isEmpty=true;
        	}
        }
		
		IDataset result=null;
		if(isEmpty){
			result=new DatasetList();
		}else{
			WeChatScoreBean bean = BeanManager.createBean(WeChatScoreBean.class);
			result=bean.queryWeChatScoreRequest(param, getPagination());
		}
		
		return result;
		
    } 
	
	
	public IData dealWeChatScoreTran(IData param)throws Exception{
		IData result=new DataMap();
		if(IDataUtil.isEmpty(param)){
			result.put("result", "false");
    		result.put("notice", "参数不能为空！");
    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,"参数不能为空！");
    		return result;
		}
		
		if(param.getString("Msisdn", "").equals("")){
			result.put("result", "false");
    		result.put("notice", "参数手机号不能为空！");
    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,"参数手机号不能为空！");
    		return result;
		}
		if(param.getString("OrderId", "").equals("")){
			result.put("result", "false");
    		result.put("notice", "订单号不能为空！");
    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,"订单号不能为空！");
    		return result;
		}
		if(param.getString("ActivityId", "").equals("")){
			result.put("result", "false");
    		result.put("notice", "活动ID不能为空！");
    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,"活动ID不能为空！");
    		return result;
		}
		
		//关于开发赠送积分活动需求（俱乐部微信平台） by songlm 20160222 start
		param.put("GivePoint", "");//初始化赠送的积分值，防止接口传入该值
		param.put("PeriodOfValidity", "");//初始化积分有效期值，防止接口传入该值
		
		result = configAndRuleCheck(param);//读取活动配置，同时进行规则校验
		
		//如果校验不通过，则返回校验结果，不再继续
		if(IDataUtil.isNotEmpty(result))
		{
			return result;
		}
		//end

		try {
			param.put("SERIAL_NUMBER", param.getString("Msisdn"));
			param.put(Route.ROUTE_EPARCHY_CODE, param.getString("ROUTE_EPARCHY_CODE","0898"));
			
			//关于开发赠送积分活动需求（俱乐部微信平台） by songlm 20160222 start
			//调用接口前保证含有赠送积分值和积分有效期入参
			if(StringUtils.isBlank(param.getString("GivePoint", ""))){
	    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,"BOSS的赠送积分值配置有误！");
			}
			if(StringUtils.isBlank(param.getString("PeriodOfValidity", ""))){
	    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,"BOSS的积分值有效期配置有误！");
			}
			//end
			
			IDataset tradeResult=CSAppCall.call("SS.WeChatScoreRegSVC.tradeReg", param);
			
			result.put("result", "true");
    		result.put("notice", "操作成功");
    		//获取用户积分
    		result.put("customerpoint", UcaDataFactory.getNormalUca(param.getString("Msisdn")).getUserScore());
    		
    		//交易信息
    		result.put("USER_ID", tradeResult.getData(0).getString("USER_ID"));
    		result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
    		result.put("TRADE_TYPE_CODE", "2015");
    		
    		//关于开发赠送积分活动需求（俱乐部微信平台） by songlm 20160222 start
    		//补充几个出参
    		result.put("serialnumber", param.getString("Msisdn"));
    		result.put("tradeid", tradeResult.getData(0).getString("TRADE_ID"));
    		result.put("dealtime", SysDateMgr.getSysTime());
    		//end
    		
    		return result;
			
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			String errInfo = e.getMessage();
			if(e.getMessage() == null || "null".equals(e.getMessage()) || StringUtils.isBlank(e.getMessage()))
	    	{
				errInfo = "未知错误！";
	    	}
			result.put("result", "false");
    		result.put("notice", errInfo);
    		
    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,errInfo);
		}
		
		return result;
	}

	/** 
	 * 新增校验接口
	 */
	public IData checkWeChatScoreTran(IData param) throws Exception
	{
		IData result = new DataMap();//初始化返回值
		
		//入参非空校验
		if(IDataUtil.isEmpty(param)){
			result.put("result", "false");
    		result.put("notice", "参数不能为空！");
    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,"参数不能为空！");
    		return result;
		}
		
		if(param.getString("Msisdn", "").equals("")){
			result.put("result", "false");
    		result.put("notice", "参数手机号不能为空！");
    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,"参数手机号不能为空！");
    		return result;
		}
		
		if(param.getString("ActivityId", "").equals("")){
			result.put("result", "false");
    		result.put("notice", "活动ID不能为空！");
    		CSAppException.apperr(WeChatException.CRM_WECHAT_0,"活动ID不能为空！");
    		return result;
		}
		
		result = configAndRuleCheck(param);//读取活动配置，同时进行规则校验
		
		//如果校验结果为空，代表校验通过
		if(IDataUtil.isEmpty(result))
		{
			result.put("result", "true");
    		result.put("notice", "校验通过");
		}
		
		return result;
	}
	
	/**
	 * 读取活动配置，同时进行规则校验
	 */
	public IData configAndRuleCheck(IData param) throws Exception
	{
		IData checkResult = new DataMap();//初始化校验结果
		String msisdn = param.getString("Msisdn", "");//手机号
		String activityId = param.getString("ActivityId", "");//活动编码
		
		//获取用户资料，判断是否存在用户信息
		IData userInfo = UcaInfoQry.qryUserInfoBySn(msisdn);
		if(IDataUtil.isEmpty(userInfo))
		{
			checkResult.put("result", "false");
			checkResult.put("notice", "用户不存在！");
			//checkResult.put("X_RESULTCODE", "2016022301");
			//checkResult.put("X_RESULTINFO", "用户不存在！");
			CSAppException.apperr(WeChatException.CRM_WECHAT_1);
			return checkResult;
		}
		
		String userId = userInfo.getString("USER_ID","0");
		
		//根据传入的活动编号读取配置
		IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "534", activityId, "0898");
		
		//无该活动配置
		if (IDataUtil.isEmpty(commparaInfos))
        {
			checkResult.put("result", "false");
			checkResult.put("notice", "活动不存在！");
			//checkResult.put("X_RESULTCODE", "2016022302");
			//checkResult.put("X_RESULTINFO", "活动不存在！");
			CSAppException.apperr(WeChatException.CRM_WECHAT_2);
			return checkResult;
        }
		
		IData commparaInfo = commparaInfos.getData(0);
		String paraCode1 = commparaInfo.getString("PARA_CODE1","");//赠送的积分值
		String paraCode2 = commparaInfo.getString("PARA_CODE2","");//赠送的积分有效期
		String paraCode3 = commparaInfo.getString("PARA_CODE3","");//星级
		String paraCode4 = commparaInfo.getString("PARA_CODE4","");//积分余额
		String paraCode5 = commparaInfo.getString("PARA_CODE5","");//赠送次数
		String paraCode6 = commparaInfo.getString("PARA_CODE6","");//步长
		String paraCode7 = commparaInfo.getString("PARA_CODE7","");//总赠送次数（每年）
		String paraCode8 = commparaInfo.getString("PARA_CODE8","");//活动名额
		String paraCode17 = commparaInfo.getString("PARA_CODE17","");//业务区
		String paraCode26 = commparaInfo.getString("PARA_CODE26","");//活动开始时间
		String paraCode27 = commparaInfo.getString("PARA_CODE27","");//活动结束时间
		String paraCode9 = commparaInfo.getString("PARA_CODE9","");//限制优惠
		String paraCode10 = commparaInfo.getString("PARA_CODE10","");//活动大类
		String paraCode11 = commparaInfo.getString("PARA_CODE11","");//活动名额/每天
		String paraCode12 = commparaInfo.getString("PARA_CODE12","");//活动名额/每月
		String paraCode13 = commparaInfo.getString("PARA_CODE13","");//白名单客户群ID
		
		//A、‘活动开始时间’与‘活动结束时间’字段，规定用户参加活动时间(活动请求时间)要介于该两个字段值内
		if(StringUtils.isNotBlank(paraCode26) && StringUtils.isNotBlank(paraCode27))
		{
			String sysdate = SysDateMgr.getSysTime();
			String startTime = SysDateMgr.decodeTimestamp(paraCode26, SysDateMgr.PATTERN_STAND);//统一为yyyy-MM-dd HH:mm:ss格式
	        String endTime = SysDateMgr.decodeTimestamp(paraCode27, SysDateMgr.PATTERN_STAND);//统一为yyyy-MM-dd HH:mm:ss格式
	        
	        if ((sysdate.compareTo(startTime) < 0) || (sysdate.compareTo(endTime) > 0))
            {
	        	//活动有效期已过
	        	checkResult.put("result", "false");
				checkResult.put("notice", "不在活动有效期！");
				//checkResult.put("X_RESULTCODE", "2016022303");
				//checkResult.put("X_RESULTINFO", "不在活动有效期！");
				CSAppException.apperr(WeChatException.CRM_WECHAT_3);
	        	return checkResult;
            }
		}
		
		   
		    IDataset paraCode13List = new DatasetList();            
	            if(StringUtils.isNotBlank(paraCode13))
	            {
	        	IData paraCode13Param = new DataMap();
	        	 	        	
	                paraCode13Param.put("USER_ID", userId);	                
	                paraCode13Param.put("TROOP_ID", paraCode13);
	                paraCode13List = Dao.qryByCode("TF_SM_TROOP_MEMBER", "SEL_BY_USERID_TROOPID", paraCode13Param);                               
	            }
	            
	if (IDataUtil.isEmpty(paraCode13List)) {// 如果不是白名单客户 ，则 地区 和 星级，需要做校验 

	    // C、‘用户地区’字段，规定用户的业务区范围
	    if (StringUtils.isNotBlank(paraCode17)) {
		String custVipCity = "";
		IData tmpInput = new DataMap();
		// 获取“当前业务区” 参考的“客户资料综合查询”模块中的，“当前业务区”取法
			tmpInput.put("USER_ID", userId);
            IDataset userCityInfo = Qry360InfoDAO.qryUserCityInfo(tmpInput);
            if(StringUtils.isNotBlank(userInfo.getString("CITY_CODE")) && IDataUtil.isNotEmpty(userCityInfo))
            {
            	custVipCity = userCityInfo.getData(0).getString("CITY_CODE", "");
            }
            if(StringUtils.isNotBlank(userInfo.getString("CITY_CODE")) && IDataUtil.isEmpty(userCityInfo))
            {
            	custVipCity = userInfo.getString("CITY_CODE","");
            }


		Boolean equalState = false;// 默认不存在匹配的
		String[] rsrvStrArray = StringUtils.split(paraCode17, "|");

		for (int i = 0; i < rsrvStrArray.length; i++) {
		    if (rsrvStrArray[i].equals(custVipCity)) {
			equalState = true;
		    }
		}

		if (!equalState)// 如果没有匹配的业务区则报错
		{
		    checkResult.put("result", "false");
		    checkResult.put("notice", "号码不在活动业务区！");
		    // checkResult.put("X_RESULTCODE", "2016022304");
		    // checkResult.put("X_RESULTINFO", "号码不在活动业务区！");
		    CSAppException.apperr(WeChatException.CRM_WECHAT_4);
		    return checkResult;
		}
	    }

	    // D、‘用户星级’字段，规定用户的星级
		if(StringUtils.isNotBlank(paraCode3))
		{
			IData userCredit = UserCreditInfoQrySVC.getUserCreditInfos(userId);
		String userCreditValue = userCredit.getString("CREDIT_CLASS");

		Boolean equalState = false;// 默认不存在匹配的
		String[] rsrvStrArray = StringUtils.split(paraCode3, "|");

			for (int i = 0; i < rsrvStrArray.length; i++){
				if(rsrvStrArray[i].equals(userCreditValue))
				{
					equalState = true;
				}
			}

		if (!equalState)// 如果用户没有匹配的星级则报错
		{
		    checkResult.put("result", "false");
		    checkResult.put("notice", "不是目标星级用户！");
		    // checkResult.put("X_RESULTCODE", "2016022305");
		    // checkResult.put("X_RESULTINFO", "不是目标星级用户！");
		    CSAppException.apperr(WeChatException.CRM_WECHAT_5);
		    return checkResult;
		}
	    }

	}
	
	//R、‘活动名额/每天’字段，指在规定的‘活动开始时间’与‘活动结束时间’字段值内，每天成功参与活动能支持最大的用户数(剔重) 
	if(StringUtils.isNotBlank(paraCode11) && StringUtils.isNumeric(paraCode11))
	{
		IDataset activityCounts = new DatasetList();
		activityCounts = WeChatScoreQry.queryAllUserCountByDay(activityId);			 
		
		if(IDataUtil.isNotEmpty(activityCounts))
		{
			String activityCount = activityCounts.getData(0).getString("VCOUNT","0");//获取活动已办理用户数（剔重）
			
			if (Integer.parseInt(activityCount) >= Integer.parseInt(paraCode11))
            {
				//已达活动能支持最大的用户数
				checkResult.put("result", "false");
				checkResult.put("notice", "已达每天活动名额能支持最大的用户数！");
				CSAppException.apperr(WeChatException.CRM_WECHAT_11);
				return checkResult;
            }
		}
	}
	
	//S、‘活动名额/每月’字段，指在规定的‘活动开始时间’与‘活动结束时间’字段值内，每月（自然月）成功参与活动能支持最大的用户数(剔重)
	if(StringUtils.isNotBlank(paraCode12) && StringUtils.isNumeric(paraCode12))
	{
		IDataset activityCounts = new DatasetList();
		activityCounts = WeChatScoreQry.queryAllUserCountByMonth(activityId);			 
		
		if(IDataUtil.isNotEmpty(activityCounts))
		{
			String activityCount = activityCounts.getData(0).getString("VCOUNT","0");//获取活动已办理用户数（剔重）
			
			if (Integer.parseInt(activityCount) >= Integer.parseInt(paraCode12))
            {
				//已达活动能支持最大的用户数
				checkResult.put("result", "false");
				checkResult.put("notice", "已达每月活动名额能支持最大的用户数！");
				CSAppException.apperr(WeChatException.CRM_WECHAT_12);
				return checkResult;
            }
		}
	}
	
		//E、‘积分余额’字段，规定用户账户积分余额的最低值
		if(StringUtils.isNotBlank(paraCode4) && StringUtils.isNumeric(paraCode4))
		{
			//查用户积分
			IDataset scoreInfo = AcctCall.queryUserScore(userId);
			
			if (IDataUtil.isEmpty(scoreInfo))
			{
				// 获取用户积分无数据!
				checkResult.put("result", "false");
				checkResult.put("notice", "用户积分无数据！");
				//checkResult.put("X_RESULTCODE", "9999");
				//checkResult.put("X_RESULTINFO", "用户积分无数据！");
				CSAppException.apperr(WeChatException.CRM_WECHAT_0,"用户积分无数据！");
				return checkResult;
			}
			
			String score = scoreInfo.getData(0).getString("SUM_SCORE","0");//用户积分值
			int scoreLimit = Integer.parseInt(paraCode4);//配置的积分余额最低值
			
			if(Integer.parseInt(score) < scoreLimit)
			{
				//用户积分低于配置的积分余额最低值
				checkResult.put("result", "false");
				checkResult.put("notice", "用户积分余额不满足要求！");
				//checkResult.put("X_RESULTCODE", "2016022306");
				//checkResult.put("X_RESULTINFO", "用户积分余额不满足要求！");
				CSAppException.apperr(WeChatException.CRM_WECHAT_6);
				return checkResult;
			}
		}
		
		//F、‘赠送次数’字段，单位为：**次/每个用户/步长；规定了在‘活动开始时间’与‘活动结束时间’字段值内，且以步长为划分单位，用户能享受最多的积分赠送次数
		//G、‘步长’字段，可任选‘每天’、‘每周’、‘每月’等其中一个
		//如果能进到该段，证明已经跳过A判断，是在‘活动开始时间’与‘活动结束时间’内了，不再进行活动时间的期间判断
		if(StringUtils.isNotBlank(paraCode5) && StringUtils.isNumeric(paraCode5) && StringUtils.isNotBlank(paraCode6))
		{
			IDataset joinCounts = new DatasetList();
			String tmp = "";
				
			//每天
			if("d".equals(paraCode6) || "D".equals(paraCode6))
			{
				tmp = "天";
				joinCounts = WeChatScoreQry.queryCountByDay(activityId, userId);
			}
			
			//每周
			if("w".equals(paraCode6) || "W".equals(paraCode6))
			{
				tmp = "周";
				joinCounts = WeChatScoreQry.queryCountByWeek(activityId, userId);
			}
			
			//每月
			if("m".equals(paraCode6) || "M".equals(paraCode6))
			{
				tmp = "月";
				joinCounts = WeChatScoreQry.queryCountByMonth(activityId, userId);
			}
			
			if(IDataUtil.isNotEmpty(joinCounts))
			{
				String joinCount = joinCounts.getData(0).getString("VCOUNT","0");//办理量
				
				if (Integer.parseInt(joinCount) >= Integer.parseInt(paraCode5))
	            {
					checkResult.put("result", "false");
					checkResult.put("notice", "已达每" + tmp + "赠送次数限制！");
					//checkResult.put("X_RESULTCODE", "2016022307");
					//checkResult.put("X_RESULTINFO", "已达每" + tmp + "赠送次数限制！");
					CSAppException.apperr(WeChatException.CRM_WECHAT_7,"已达每" + tmp + "赠送次数限制！");
					return checkResult;
	            }
			}
		}

		//I、‘总赠送次数’，单位为：**次/每个用户/每年；规定每年中(自然年份)，用户能享受最多的积分赠送次数
		if(StringUtils.isNotBlank(paraCode7) && StringUtils.isNumeric(paraCode7))
		{
			IDataset userActivityCounts = WeChatScoreQry.queryTotalCountByUser(activityId, userId);//获取该用户系统所在年内该活动的办理量
			if(IDataUtil.isNotEmpty(userActivityCounts))
			{
				String userActivityCount = userActivityCounts.getData(0).getString("VCOUNT","0");//获取该用户系统所在年内该活动的办理量
				
				if (Integer.parseInt(userActivityCount) >= Integer.parseInt(paraCode7))
	            {
					//用户已达活动总赠送次数
					checkResult.put("result", "false");
					checkResult.put("notice", "已达年度单用户总赠送次数！");
					//checkResult.put("X_RESULTCODE", "2016022308");
					//checkResult.put("X_RESULTINFO", "已达年度单用户总赠送次数！");
					CSAppException.apperr(WeChatException.CRM_WECHAT_8);
					return checkResult;
	            }
			}
		}
		
		//J、‘活动名额’字段，规定某活动能支持最大的用户数(剔重) 
		if(StringUtils.isNotBlank(paraCode8) && StringUtils.isNumeric(paraCode8))
		{
			IDataset activityCounts = new DatasetList();
			if(StringUtils.isNotBlank(paraCode10))
			{
				//获取大类下所有活动已办理用户数（剔重）
				activityCounts = WeChatScoreQry.queryTypeActivityCount(paraCode10);//获取大类活动已办理用户数（剔重）
			}
			else
			{
				activityCounts = WeChatScoreQry.queryActivityCount(activityId);//获取单活动已办理用户数（剔重）
			}
			
			if(IDataUtil.isNotEmpty(activityCounts))
			{
				String activityCount = activityCounts.getData(0).getString("VCOUNT","0");//获取活动已办理用户数（剔重）
				
				if (Integer.parseInt(activityCount) >= Integer.parseInt(paraCode8))
	            {
					//已达活动能支持最大的用户数
					checkResult.put("result", "false");
					checkResult.put("notice", "已达活动能支持最大的用户数！");
					//checkResult.put("X_RESULTCODE", "2016022309");
					//checkResult.put("X_RESULTINFO", "已达活动能支持最大的用户数！");
					CSAppException.apperr(WeChatException.CRM_WECHAT_9);
					return checkResult;
	            }
			}
		}
		
		//Q、限制优惠，即不允许移动员工或代理商办理
		if(StringUtils.isNotBlank(paraCode9))
		{
			//获取用户的优惠，一次判断是否存在paraCode9配置的|270|655|这类格式的
			IDataset userDiscntList = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
			for (int index = 0, size = userDiscntList.size(); index < size; index++)
			{
				IData userDiscnt = userDiscntList.getData(index);
				String discntCode = userDiscnt.getString("DISCNT_CODE");

				Boolean equalState = false;//默认不存在匹配的
				String[] rsrvStrArray = StringUtils.split(paraCode9, "|");
				
				for (int i = 0; i < rsrvStrArray.length; i++){
					if(rsrvStrArray[i].equals(discntCode))
					{
						equalState = true;
					}
				}
				
				if(equalState)//如果用户存在匹配的优惠，则报错
				{
					//员工或代理商不能办理
					checkResult.put("result", "false");
					checkResult.put("notice", "员工或代理商不能办理！");
					CSAppException.apperr(WeChatException.CRM_WECHAT_10);
					return checkResult;
				}
			}
		}
		
		
		
		//补充规则1，未配置赠送的积分值
		if(StringUtils.isBlank(paraCode1))
		{
			checkResult.put("result", "false");
			checkResult.put("notice", "BOSS未配置赠送的积分值！");
			//checkResult.put("X_RESULTCODE", "9999");
			//checkResult.put("X_RESULTINFO", "BOSS未配置赠送的积分值！");
			CSAppException.apperr(WeChatException.CRM_WECHAT_0,"BOSS未配置赠送的积分值！");
			return checkResult;
		}
		
		//将赠送积分值传出，作用于赠送积分接口
		param.put("GivePoint", paraCode1);//赠送的积分值
		
		int addMonths = 3;//初始化积分有效期为3个月
		//如果配置了积分有效期，则取配置值，否则使用初始化值
		if(StringUtils.isNotBlank(paraCode1) && StringUtils.isNumeric(paraCode2))
		{
			addMonths = Integer.parseInt(paraCode2);
		}
		
		//计算积分的截止日期
		String periodOfValidity = SysDateMgr.getAddMonthsLastDay(addMonths).substring(0, 10);//获取addMonths个月后的最后一天（含当前月）
		
		//将积分有效期传出，作用于赠送积分接口
		param.put("PeriodOfValidity", periodOfValidity);//积分有效期
		
		
		return checkResult;
	}
	
}
