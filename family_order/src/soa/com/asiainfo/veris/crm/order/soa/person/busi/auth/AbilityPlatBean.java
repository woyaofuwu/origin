package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.util.TimeUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.Utility;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.service.bean.BeanManager;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityPlatCheckRelativeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sundryquery.other.SyncActivateTimeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdAssistant;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.KjPrintBean;
import com.asiainfo.veris.crm.order.soa.person.busi.cmonline.selfterminal.SelfTerminalUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.ShareMealBean;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.InterfaceUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.QueryInfoBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryInfoUtil;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryListInfo;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AbilityPlatBean extends CSBizBean {
	static Logger logger = Logger.getLogger(AbilityPlatBean.class);

	/**
	 * 能力开放平台总接口
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String CHECK_CODE_CACHE_KEY = "com.ailk.personserv.service.busi.common.abilityplat_";//验证码缓存存放key
	 /**
	 * 随机短信验证码下发(在一级能力平台做)
	 * 给指定用户下发短信验证码，来验证用户身份真实性
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
//	public IData issuedSmsCode(IData Idata) throws Exception
//	{
//		IDataUtil.chkParam(Idata, "SERIAL_NUMBER");
//		
//		IData data = new DataMap();
//审计不通过，暂时屏蔽 ,需要用到时，自行修改		String serialNumber = Idata.getString("SERIAL_NUMBER");
//		
//		int second = 1800;// 验证码有效时间 单位：秒
//		int allowVerifyCount=3;//默认可以验证次数
//		
//		//生成短信验证码
//		String verifyCode = RandomStringUtils.randomNumeric(6);
//		String msg = "【中国移动】 尊敬的用户，您的验证码为："+verifyCode+"，该验证码有"+second/60+"分钟内有效，请及时填写。";//短信内容
//		//发送短信通知
//		IData inparam = new DataMap();
//        inparam.put("NOTICE_CONTENT", msg);
//        inparam.put("RECV_OBJECT", serialNumber);
//        inparam.put("RECV_ID", serialNumber);
//        inparam.put("SMS_PRIORITY", "5000");
//        inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
//        inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
//        inparam.put("REMARK", "能力开放平台接口申请随机验证码");
//        SmsSend.insSms(inparam);
//        
//        IData cacheData = new DataMap();
//        cacheData.put("VERIFYCODE", verifyCode);
//        cacheData.put("CREATETIME", SysDateMgr.getSysTime());//发送验证码时间
//        cacheData.put("FAILTIME", SysDateMgr.getOtherSecondsOfSysDate(second));//验证码失效时间
//        cacheData.put("VERIFYCOUNT", 0);//验证次数
//        cacheData.put("ALLOWVERIFYCOUNT", allowVerifyCount);//允许验证次数
//
//        //保存短信验证码
//        SharedCache.set(CHECK_CODE_CACHE_KEY+"_"+serialNumber,cacheData);
//       // data.put("INFO", cacheData);
//        data.put("BIZ_CODE", "0000");
//        data.put("BIZ_DESC", "随机短信验证码下发成功");
//        
//        return data;
//	
//	}
	/** 
	* 随机短信验证码校验(在一级能力平台做)
	* 对用户输入的短信验证码进行校验。当验证码输入错误次数超过3次时，需重新申请新验证码
	* @return 1 - 验证码正确；2 - 验证码错误；3 - 校验失败次数超过3次限制，该验证码已作废,请重新申请；4 - 验证码失效，请重新申请；5 - 验证码格式错误（或为空），请重试
	*/
	public IData checkSmsCode(IData Idata) 
	{
		IData data = new DataMap();
    	data.put("BIZ_CODE", "0000");
		data.put("BIZ_DESC", "调随机短信验证码校验接口成功");		
   try{
	   data.put("IDENT_VOUCHER", "IdentVoucheruser"+SeqMgr.getUserId());
		IDataUtil.chkParam(Idata, "SERIAL_NUMBER");
    	String serialNumber = Idata.getString("SERIAL_NUMBER");
    	String verifyCode = Idata.getString("SMS_CODE", "");				
    	if(StringUtils.isEmpty(verifyCode))
    	{
    		data.put("SMSCODECHECKRESULT", "5");
    		return data;
    	}
    	else
    	{
        	Object  sharedCache = SharedCache.get(CHECK_CODE_CACHE_KEY+"_"+serialNumber);
        	if(null == sharedCache){
        		data.put("SMSCODECHECKRESULT", "4");
        		return data;
        	}else{
        		IData smsVerifyCode=(DataMap)sharedCache;
        		String cacheVerifyCode=smsVerifyCode.getString("VERIFYCODE");//缓存中验证码
        		int allowVerifyCount=smsVerifyCode.getInt("ALLOWVERIFYCOUNT");//允许验证次数
        		int verifyCount=smsVerifyCode.getInt("VERIFYCOUNT");//已校验次数
        		String failTime=smsVerifyCode.getString("FAILTIME");//验证码失效时间
        		String nowDate =  SysDateMgr.getSysTime();
        		java.util.Date nowDateTime=SysDateMgr.string2Date(nowDate, "yyyy-MM-dd HH:mm:ss");
        		java.util.Date failDateTime=SysDateMgr.string2Date(failTime, "yyyy-MM-dd HH:mm:ss");
        		if(nowDateTime.after(failDateTime)){//当前时间在失效时间之后
        			data.put("SMSCODECHECKRESULT", "4");
            		SharedCache.delete(CHECK_CODE_CACHE_KEY+"_"+serialNumber);
            		return data;
        		}
        		if(verifyCount>=allowVerifyCount){
        			data.put("SMSCODECHECKRESULT", "3");
        			SharedCache.delete(CHECK_CODE_CACHE_KEY+"_"+serialNumber);
        			return data;
        		}
    	    	if(cacheVerifyCode.equals(verifyCode)){
    	    		data.put("SMSCODECHECKRESULT", "1");
    	            SharedCache.delete(CHECK_CODE_CACHE_KEY+"_"+serialNumber);   
    	            return data;
    	    	}else{
    	    		data.put("SMSCODECHECKRESULT", "2");
    	    		smsVerifyCode.put("VERIFYCOUNT", verifyCount+1);//验证次数
    	    		SharedCache.set(CHECK_CODE_CACHE_KEY+"_"+serialNumber, smsVerifyCode);
    	    		return data;
    	    	}
        	}
    	}
    }catch(Exception e){
    	data.put("BIZ_CODE", "2999");
		data.put("BIZ_DESC","调随机短信验证码校验接口失败"+e.getMessage());
		data.put("IDENT_VOUCHER", "");
		data.put("SMSCODECHECKRESULT", "");
		return data;
		
    }
    
	}

	/**
	 * 入网资格校验
	 * 入网资格校验是指在用户办理入网时，由第三方合作伙伴平台发起的，对入网用户身份的校验。入网资格校验主要校验以下内容（其他判断逻辑与省BOSS一致）：
	 * 1、是否黑名单用户； 2、该客户资料下面的用户是否有欠费； 3、判断该客户是否达到最大用户数限制；
	 * 4、客户在网用户数量验证，后付费用户的用户数量限制为可配置，数量由各省定义。
	 * 
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData checkComeInNet(IData Idata) throws Exception {		 
		IDataUtil.chkParam(Idata, "ID_CARD_TYPE");
		IDataUtil.chkParam(Idata, "ID_CARD_NUM");
        Idata.put("PSPT_TYPE_CODE", Idata.getString("ID_CARD_TYPE"));
        Idata.put("PSPT_ID", Idata.getString("ID_CARD_NUM"));
		String psptTypeCode = Idata.getString("PSPT_TYPE_CODE");
		String psptId = Idata.getString("PSPT_ID");
		String custName = Idata.getString("CUSTOMER_NAME");
		IData data = new DataMap();
		data.put("BIZ_CODE",  "0000");
		data.put("BIZ_DESC",  "正常");
		data.put("RESULTE_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

		/*IDataset custInfoList = getCustInfoByPspt(custName,psptTypeCode, psptId);
		if(IDataUtil.isEmpty(custInfoList)){
			data.put("BIZ_CODE", "2043");
			data.put("BIZ_DESC", "业务号码与客户姓名、证件号码信息不匹配！"); 
			return data;
		}*/
	  
  
		//IDataset custList = getCustInfoByPspt(psptTypeCode, psptId); // 该证件下的用户资料
		//是否大于16岁
		if(psptId.length()==15){
			psptId = IdcardUtils.conver15CardTo18(psptId);
		}
		int age=SelfTerminalUtil.getAge(psptId.substring(6,14));
		if(age<16){
			data.put("BIZ_CODE",  "0001");
			data.put("BIZ_DESC",  "16岁以下不能办理");
			return data;
		}
		
		//获取证件类型
		IDataset custList =new DatasetList();
		//证件类型转换
        String custPsptTypeCode="0";
        IDataset cardList=CommparaInfoQry.getCommNetInfo("CSM", "3838", "IDCARD");
    	for(int i=0;i<cardList.size();i++){
    		if(psptTypeCode.equals(cardList.getData(i).getString("PARA_CODE3"))){
    			custPsptTypeCode=cardList.getData(i).getString("PARA_CODE2","0");
    			break;
    		}
    	}
    	IData custData=new DataMap();
    	custData.put("PSPT_TYPE_CODE", custPsptTypeCode);
    	custList.add(custData);
    	
		if(IDataUtil.isNotEmpty(custList)){
			boolean codebool = checkPostCard(psptTypeCode);
			// 检查证件类型是否合法
			if (!codebool) {
				data.put("BIZ_CODE",  "3A11");
				data.put("BIZ_DESC",  "证件类型不正确");
				 return data;
			}
			// 检查黑名单
			// UCustBlackInfoQry.isBlackCust(pspt_type_code_ab, pspt_id);
			boolean checkBlackCust = UCustBlackInfoQry.isBlackCust(custList.getData(0).getString("PSPT_TYPE_CODE"), psptId);
			if (checkBlackCust) {
				data.put("BIZ_CODE",  "3000");
				data.put("BIZ_DESC",  "该用户有黑名单信息");
				 return data;
			}
			
			//当月开户超过2个
			int monthCount = UserInfoQry.getRealNameUserCountByPspt3(custName, psptId, "00");
			if(monthCount>=2){
				data.put("BIZ_CODE",  "3006");
				data.put("BIZ_DESC",  "证件号码【" + psptId + "】当月入网的数量已达到最大值【2个】，请下月再办理！");
				return data;
			}
			
			IData rDualInfo;
			// 读取配置的客户开户限制数
			String strTagNumber = "";
			// 全身配置一个统一限制数
			rDualInfo = AbilityPlatCheckRelativeQry.getTagInfo();
			strTagNumber = rDualInfo.getString("TAG_NUMBER");
			// 检查是否超过了数量
			//IDataset custInfo = CustPersonInfoQry.qryPerInfoByPsptId(custList.getData(0).getString("PSPT_TYPE_CODE"), psptId);
			int rCount = UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);
				if (!"0".equals(strTagNumber)) {
					if (rCount >= Integer.parseInt(strTagNumber)) {
						data.put("BIZ_CODE",  "3006");
						data.put("BIZ_DESC",  "同一证件号码下面客户开户数不能超过" + strTagNumber + "个");
						 return data;				
					}
				}				
				// 根据证件类型查找全网开户限制数
				IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "2552", psptTypeCode, "ZZZZ");
				if (openLimitResult.isEmpty())
				{// 如果本地配置没有该业务类型的限制数量配置，则直接返回
					return data;
				}

				if (!"".equals(custName) && !"".equals(psptId) && !"".equals(psptTypeCode))
				{

					
					// 调用全网证件号码查验接口
					IData param = new DataMap();
					param.put("CUSTOMER_NAME", custName);
					param.put("IDCARD_TYPE", custPsptTypeCode);
					param.put("IDCARD_NUM", psptId);

					// 调用全网证件号码查验接口
					NationalOpenLimitBean bean = BeanManager.createBean(NationalOpenLimitBean.class);
					IDataset callResult = new DatasetList();
					try
					{
						callResult = bean.idCheck(param);
					} catch (Exception e)
					{
						
						data.put("BIZ_CODE", "3006"); 
						data.put("BIZ_DESC","校验【全网一证多号】出现异常，请联系系统管理员！" + custName + "|" + psptTypeCode + "|" + psptId );
						return data;
					}	

					if (IDataUtil.isNotEmpty(callResult))
					{
						if ("0".equals(callResult.getData(0).getString("X_RESULTCODE")))
						{
							int openNum = callResult.getData(0).getInt("TOTAL", 0);
							int untrustresult = callResult.getData(0).getInt("UN_TRUST_RESULT", 0);
							if (openNum >= 0)
							{
								if (untrustresult > 0)
								{
									data.put("BIZ_CODE", "23043");
									data.put("BIZ_DESC", "开户人有不良信息，不满足开户条件，禁止开户");
									return data;
								}
								if (IDataUtil.isNotEmpty(openLimitResult))
								{
									int openLimitNum = openLimitResult.getData(0).getInt("PARA_CODE1", 0);

									if (openNum >= openLimitNum)
									{
										
										data.put("BIZ_CODE", "3006"); 
										data.put("BIZ_DESC","【全网一证多号】校验: 证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + openLimitNum + "个】，请更换其它证件！" );
										return data;
									} else
									{
										// 查询携转业务41工单的数量，判断一证五号加入当前已申请携入成功的工单判断，如用户证件A已经成功申请了2笔携入，证件A调用集团一证五号接口返回开户数为3，
										// 当前该证件开户数在我省系统判断即为5；
										IDataset ds = TradeHistoryInfoQry.getInfosByTradeTypeCode("40", psptTypeCode, psptId);// 携转开户
										if (DataSetUtils.isNotBlank(ds))
										{
											int count = ds.getData(0).getInt("COUNT", 0);
											if ((count + openNum) >= openLimitNum)
											{
												
												data.put("BIZ_CODE", "3006"); 
												data.put("BIZ_DESC","【全网一证多号】校验: 证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + openLimitNum + "个】，请更换其它证件！" );
												return data;
											}
										}
									}
								}
							}
						} else
						{
							if ("2998".equals(callResult.getData(0).getString("X_RESULTCODE")))
							{
								
								data.put("BIZ_CODE", "3006"); 
								data.put("BIZ_DESC","【全网一证多号】校验: "+callResult.getData(0).getString("X_RESULTINFO"));
								return data;
							} else
							{
								
								data.put("BIZ_CODE", "3006"); 
								data.put("BIZ_DESC","校验【全网一证多号】出现异常，请联系系统管理员！");
								return data;
							}
						}
					}
					
				}
			
				// 存在欠费号码
				// 根据客户标记获取用户是否有欠费判断
				if (custList != null && custList.size() > 0) {
					IData oweFeeData = getOweFeeUserById(custList);
					if (!oweFeeData.isEmpty()) {
						data.put("BIZ_CODE",  "3003");
		    			data.put("BIZ_DESC",  "该客户有号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元，不能再次使用该证件办理开户业务！");
		    			 return data;
					}
				}
		}else{
			data.put("BIZ_CODE",  "2043");
			data.put("BIZ_DESC",  "用户身份信息错误：证件号码不正确");
            return data;
		}
			
		return data;
	}
	/**
	 * 查询欠费信息
	 * 
	 * @return
	 * @throws Exception
	 */

	public IData getOweFeeUserById(IDataset custList) throws Exception {
		IData oweFeeData = new DataMap();
		IData custData = null;
		IData userData = null;
		IDataset userList = null;
		IData owefeeData = null;
		double dFee = 0;// 往月欠费
		int iOnlineNum = 0;// 当前证件下在网用户数
		boolean isExistsOweFeeFlag = false;// 存在欠费用户标记
		String oweFeeSerialNumber = "";// 欠费号码
		for (int i = 0; i < custList.size(); i++) {
			custData = custList.getData(i);
			String cust_id = custData.getString("CUST_ID");
			IData oweCustData = new DataMap();
			oweCustData.put("CUST_ID", cust_id);
			userList = QueryListInfo.getUserInfoByCustId(cust_id, null);
			if (userList != null && userList.size() > 0) {
				iOnlineNum += userList.size();// 统计在网用户数
				// 未找到欠费用户时，才查欠费信息，找到一条则不查询，提示第一条欠费信息即可
				if (!isExistsOweFeeFlag) {
					// 根据用户标识查询欠费信息
					for (int j = 0; j < userList.size(); j++) {
						userData = userList.getData(j);
						String userId = userData.getString("USER_ID");

						IData iparam = new DataMap();

						iparam.put("EPARCHY_CODE", userData.getString(
								"EPARCHY_CODE", ""));
						iparam.put("USER_ID", userId);
						iparam.put("ID", userId);
						iparam.put("ID_TYPE", "1");
						iparam.put(Route.ROUTE_EPARCHY_CODE, userData.getString(
								Route.ROUTE_EPARCHY_CODE, ""));

						// 调用账户流程查询欠费信息
						// IData doweFee = UserInfoQry.getOweFeeCTT(iparam);
						IData doweFee = AcctCall.getOweFeeByUserId(userId);
						dFee = Double.parseDouble(doweFee
								.getString("LAST_OWE_FEE"));
						if (dFee > 0) {// 找到有往月欠费用户则退出循环，提示欠费信息
							isExistsOweFeeFlag = true;
							oweFeeSerialNumber = userData
									.getString("SERIAL_NUMBER");
							break;
						}
					}
				}
			}
		}
		// 存在欠费用户时，返回欠费号码，欠费金额，在网用户数
		if (isExistsOweFeeFlag) {
			String strFee = String.valueOf(((float) dFee) / 100);
			oweFeeData.put("OWE_FEE_SERIAL_NUMBER", oweFeeSerialNumber);
			oweFeeData.put("OWE_FEE", strFee);
			oweFeeData.put("ONLINE_NUM", iOnlineNum);
			oweFeeData.put("IS_EXISTS_OWE_FEE_FLAG", true);
		}
		return oweFeeData;

	}

	/**
	 * 查询手机号码欠费信息
	 * @param custList
	 * @return
	 * @throws Exception
	 * @author zhaohj3
	 * @date 2019-7-25 11:43:51
	 */
	public IData getPhonenOweFeeUserById(IDataset custList) throws Exception {
		IData oweFeeData = new DataMap();
		IData custData = null;
		IData userData = null;
		IDataset userList = null;
		double dFee = 0;// 往月欠费
		int iOnlineNum = 0;// 当前证件下在网用户数
		boolean isExistsOweFeeFlag = false;// 存在欠费用户标记
		String oweFeeSerialNumber = "";// 欠费号码
		for (int i = 0; i < custList.size(); i++) {
			custData = custList.getData(i);
			String cust_id = custData.getString("CUST_ID");
			IData oweCustData = new DataMap();
			oweCustData.put("CUST_ID", cust_id);
			userList = UserInfoQry.getAllNormalUserInfoByCustId(cust_id);
			if (IDataUtil.isNotEmpty(userList)) {
				iOnlineNum += userList.size();// 统计在网用户数
				// 未找到欠费用户时，才查欠费信息，找到一条则不查询，提示第一条欠费信息即可
				if (!isExistsOweFeeFlag) {
					// 根据用户标识查询欠费信息
					for (int j = 0; j < userList.size(); j++) {
						userData = userList.getData(j);
						String serialNumber = userData.getString("SERIAL_NUMBER");
						IData msiSdnMap = MsisdnInfoQry.getCrmMsisonBySerialnumberNew(serialNumber);
						if (IDataUtil.isNotEmpty(msiSdnMap)) { // 只查询手机用户的欠费信息
							String userId = userData.getString("USER_ID");
							// 调用账户流程查询欠费信息
							IData doweFee = AcctCall.getOweFeeByUserId(userId);
							dFee = Double.parseDouble(doweFee.getString("LAST_OWE_FEE"));
							if (dFee > 0) {// 找到有往月欠费用户则退出循环，提示欠费信息
								isExistsOweFeeFlag = true;
								oweFeeSerialNumber = userData.getString("SERIAL_NUMBER");
								break;
							}
						}
					}
				}
			}
		}
		// 存在欠费用户时，返回欠费号码，欠费金额，在网用户数
		if (isExistsOweFeeFlag) {
			String strFee = String.valueOf(((float) dFee) / 100);
			oweFeeData.put("OWE_FEE_SERIAL_NUMBER", oweFeeSerialNumber);
			oweFeeData.put("OWE_FEE", strFee);
			oweFeeData.put("ONLINE_NUM", iOnlineNum);
			oweFeeData.put("IS_EXISTS_OWE_FEE_FLAG", true);
		}
		return oweFeeData;
	}

	/*
	 * 00 居民身份证 01 临时居民身份证 02 户口簿（仅用于未成年客户） 03 军人身份证件 04 武装警察身份证件 05 港澳居民往来内地通行证
	 * 06 台湾居民来往大陆通行证 07 护照 99 其他证件
	 */
	public boolean checkPostCard(String param) throws Exception {
		IDataset psptSet = AbilityPlatCheckRelativeQry.getAbilityPsptTypeCode();

		for (int i = 0; i < psptSet.size(); i++) {
			if (psptSet.getData(i).getString("DATA_ID").equals(param)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 根据证件类型和证件号码获取客户资料
	 * 
	 * @author
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset getCustInfoByPspt(String psptTypeCode, String psptId)
			throws Exception {

		IDataset custList = new DatasetList();// 先给初始值，便于后面判断
        IDataset codes = getRealyPsptTypeCodeByPnet(psptTypeCode);
        if (IDataUtil.isNotEmpty(codes))
        {
            StringBuilder sb = new StringBuilder(20);
            for (int j = 0; j < codes.size(); j++)
            {
                if (j == codes.size() - 1)
                {
                    sb.append("").append(codes.getData(j).getString("PSPT_TYPE_CODE", "")).append("");
                }
                else
                {
                    sb.append("").append(codes.getData(j).getString("PSPT_TYPE_CODE", "")).append(",");
                }
            }
            psptTypeCode = sb.toString(); // 身份证的模糊查询

        }
        else
        {
            psptTypeCode = codes.getData(0).getString("PSPT_TYPE_CODE");
        }
//        //查所有的地州库
//        String[] connNames = Route.getAllCrmDb();
//		if (connNames == null) {
//			connNames = new String[]{"0898"};
//		}
//		IDataset list = new DatasetList();
//		for (int index = 0, count = connNames.length; index < count; index++) {
//			String routeId = connNames[index];
        custList = QueryListInfo.getCustInfoByPsptCustType("0", psptTypeCode,psptId);
//			if(!list.containsAll(tempSet)){
//				list.addAll(tempSet);
//			}
//		}
        return custList;
	}
	public static IDataset getRealyPsptTypeCodeByPnet(String pnetTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PNET_TYPE_CODE", pnetTypeCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT  DATA_ID PSPT_TYPE_CODE FROM   TD_S_STATIC WHERE TYPE_ID=:PNET_TYPE_CODE");
        return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
    }
	/**
	 * 业务办理资格校验
	 * 对于已在网的老用户，如果需要购买合约机、办理合约、办理其他移动业务或充值，需要校验用户状态是否正常，能否办理新合约，能否办理商品或产品中包含的业务，能否进行充值等。主要判断内容如下：
		1、	号码状态是否正常；
		2、	是否容许办理新合约；
		3、	新业务是否与号码已订购业务冲突；
		4、	该号码是否容许办理该新业务。
		主要校验业务包括：老用户购买合约机、购买移动增值业务、办理流量包、宽带续包校验、充值号码校验等
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData checkCustomerDoService(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");//01手机号，02宽带
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "CHECK_TYPE");
		input.put("NUMTYPE", input.getString("SERVICE_TYPE").substring(1));
		input.put("CHECKTYPE", input.getString("CHECK_TYPE"));
		input.put("GOODSID", input.getString("GOODS_ID"));
			
		IData param = new DataMap();
		String numType=input.getString("NUMTYPE", "");
		param.put("NUMTYPE", numType);
		param.put("MOBILENO", input.getString("SERIAL_NUMBER"));
		//1：号码状态校验;2：能否订购新合约（保留，暂未生效）；3：业务办理资格校验；4：充值号码校验
		param.put("CHECKTYPE", input.getString("CHECKTYPE"));
//		if("2".equals(input.getString("CHECKTYPE"))||"3".equals(input.getString("CHECKTYPE"))){
//			IDataUtil.chkParam(input, "GOODSID");
//		}
		param.put("GOODSID", input.getString("GOODSID",""));
		IData data=new DataMap();
		data.put("BIZ_CODE", "0000");
		data.put("BIZ_DESC", "校验OK");
		try{
			
		//param.put("PRODUCT_LIST", input.getString("PRODUCT_LIST",""));
		List xmListList = (List) input.get("PRODUCT_LIST");
		if(!"null".equals(xmListList)&& xmListList !=null ){
			JSONArray json = JSONArray.fromObject(xmListList);
			DatasetList xmList = DatasetList.fromJSONArray(json);
			input.remove("PRODUCT_LIST");
			param.put("PRODUCT_LIST", xmList);
		}
		
		IData result=checkCustomerService(param);
		if(IDataUtil.isNotEmpty(result)){
			data.put("BIZ_CODE", result.getString("BIZORDERRESULT"));
			data.put("BIZ_DESC", result.getString("RESERVE"));
			data.put("OPRT", SysDateMgr.getSysDate("yyyyMMddHHmmss"));					
		}
		}catch(Exception e){
			data.put("BIZ_CODE", "2999");
			data.put("BIZ_DESC", e.getMessage());
		}		
		data.put("NUMTYPE",input.get("NUMTYPE"));
		data.put("SERIAL_NUMBER",input.get("SERIAL_NUMBER"));
		data.put("OPRT", DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")); // 处理时间
		
		return data;
	}
	/**
	 * 6.3	业务办理资格校验
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkCustomerService(IData input) throws Exception {
		
		// 判断产品用户状态是否正常，非正常状态不能添加成员
		String  numType = input.getString("NUMTYPE", ""); //类型  1-手机号；2-宽带号码
		String  number=input.getString("MOBILENO","");  //服务号码 
		String  checkType=input.getString("CHECKTYPE",""); //校验类型 
		String  goodsID=input.getString("GOODSID","");//商品id 
		//String  checkID=input.getString("CHECKID",""); //办理的产品编码
//		List xmListList = (List) input.get("PRODUCT_LIST");
//		JSONArray json = JSONArray.fromObject(xmListList);
//		DatasetList xmList = DatasetList.fromJSONArray(json);
//		input.remove("PRODUCT_LIST");
//		input.put("PRODUCT_LIST", xmList);		
		IDataset productList=input.getDataset("PRODUCT_LIST");
		
		String eparchyCode = RouteInfoQry.getEparchyCodeBySnForCrm(number);
		
		IData  data=new DataMap();
		data.put("BIZORDERRESULT","0000");
 		data.put("RESERVE","业务办理验证正确");  
		//宽带验证 
		if("3".equals(numType)){
			IData iparam = new DataMap();
			iparam.put("SERIAL_NUMBER", number);
		    IDataset  dataSet= AbilityPlatCheckRelativeQry.getUserInfoBySn(iparam);
  		     if(("1".equals(checkType))&&(dataSet==null||dataSet.size()==0)){
	  		    	 data.put("BIZORDERRESULT","2009");
		   		     data.put("RESERVE","用户号码状态不正常");
		   		     return data;
	  		 }
  		     if(("2".equals(checkType)||"3".equals(checkType))&&(dataSet!=null&&dataSet.size()>0))
  		     {
	  		     for (int i = 0; i < dataSet.size(); i++) {
				    IData  datas=dataSet.getData(i);
				    boolean  bool=  AbilityPlatCheckRelativeQry.getWidenetAcctList(datas);
				    if(bool){
			    	     data.put("BIZORDERRESULT","3004");
			   		     data.put("RESERVE","用户已经办理了宽带");
			   		     return  data;
				    }
			     }
  		     }
  		    data.put("BIZORDERRESULT","0000");
   		    data.put("RESERVE","业务办理验证正确");
			return  data;
		}
		else if("1".equals(numType)){
				//1-号码状态校验;
		        if("1".equals(checkType)){
		        	if(!"".equals(number)){
				        	boolean  bool= this.checkUserStates(number);
				        	if(bool){
				        		data.put("BIZORDERRESULT","0000");
				        		data.put("RESERVE","用户号码状态正常");
				        	}
				        	else{
				        		data.put("BIZORDERRESULT","2009");
				        		data.put("RESERVE","用户号码状态不正常");
				        	}
				    }else{
				    	data.put("BIZORDERRESULT","2999");
		        		data.put("RESERVE","用户服务号码为空");
				    }
		        	
		        	/**
		        	 * REQ201612010011_能力开放平台业务
		        	 * @author zhuoyingzhi
		        	 * 20170207
		        	 * 增加对用户的实名制校验
		        	 */
		         //手机号码
		         String serial_number=input.getString("SERIAL_NUMBER", "");
		         
		         IData userInfo=UcaInfoQry.qryUserInfoBySn(serial_number);
		         if(IDataUtil.isNotEmpty(userInfo)){
		        	 String custId=userInfo.getString("CUST_ID", "");
		        	 IData  custInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
		        	 if(IDataUtil.isNotEmpty(custInfo)){
		        		 //实名制标识
		        		 String isRealName=custInfo.getString("IS_REAL_NAME", "");
		        		 if("".equals(isRealName)||"0".equals(isRealName)){
		        			 //非实名制
						    	data.put("BIZORDERRESULT","4034");
				        		data.put("RESERVE","实名制验证本地库验证不通过");
		        		 }
		        	 }
		         }
		         /**********************结束******************************/
		        }
		        //2-能否订购新合约；
		        if("2".equals(checkType)){
				   
		    		if(IDataUtil.isNotEmpty(productList)){
		    		 //校验产品入参之间的关系，productType=102XX
					 try{
					     AbilityRuleCheck.checkParamRelation(number,productList,eparchyCode);
					 }catch(Exception ex){
					  if(logger.isDebugEnabled()){
						  logger.debug("-------入参校验错误--------"+ex.getMessage());	
					  }
					  data.put("BIZORDERRESULT", "3004");
					  data.put("RESERVE", ex.getMessage()); 
					  return data;
					 }	
				    for(int j=0;j<productList.size();j++){
				    String checkID=productList.getData(j).getString("PRODUCT_ID","");
				    String qwproductType=productList.getData(j).getString("PRODUCT_TYPE","");
		        	IDataset dataSet = AbilityPlatCheckRelativeQry.getBossProByCtrmId(goodsID,checkID,eparchyCode);
		        	IData dataMap = new DataMap();
		        	if(IDataUtil.isNotEmpty(dataSet)){
		        		for(int i=0;i<dataSet.size();i++){
		        			dataMap = dataSet.getData(i);
		        			if("10200".equals(qwproductType)||"10100".equals(qwproductType)){//视频流量定向流量包（话费扣费-用户自选ServiceID）
	                            String serviceIdList= productList.getData(j).getString("SERVICE_ID_LIST","");
	                            dataMap.put("PRODUCT_TYPE", qwproductType);
	                            dataMap.put("SERVICE_ID_LIST", serviceIdList);
	                           }
		        			if("1".equals(dataMap.getString("CTRM_PRODUCT_TYPE"))){   //终端不做校验
		        				continue;
		        			}else if("2".equals(dataMap.getString("CTRM_PRODUCT_TYPE"))){   //合约
		        				dataMap.put("SERIAL_NUMBER", number);
		        				data = executeContract(dataMap,input,eparchyCode,number);
		        			}else if("3".equals(dataMap.getString("CTRM_PRODUCT_TYPE"))){   //套餐
		        				//这里只是判断是否订购新合约，不做校验
								
		        			}
		        		}
		        		if (!"0000".equals(data.getString("BIZORDERRESULT"))) // 验证产品变更失败
						{
							return data;
						}		        			        		
		        	}
				    }
		    		}
		        }
		        //3-业务办理资格校验
		        if("3".equals(checkType)){

		     		 if(IDataUtil.isNotEmpty(productList)){
				     //校验产品入参之间的关系，productType=102XX
				     try{
				     	AbilityRuleCheck.checkParamRelation(number,productList,eparchyCode);
				      }catch(Exception ex){
				       if(logger.isDebugEnabled()){
				       logger.debug("-------入参校验错误--------"+ex.getMessage());	
				        }
				        data.put("BIZORDERRESULT", "3004");
				        data.put("RESERVE", ex.getMessage()); 
				       return data;
				      }			  	   
				    for(int h=0;h<productList.size();h++){
				    String checkID=productList.getData(h).getString("PRODUCT_ID");
				    String qwproductType=productList.getData(h).getString("PRODUCT_TYPE","");
		        	IDataset  dataSet=AbilityPlatCheckRelativeQry.getBossProByCtrmId(goodsID,checkID,eparchyCode);
		        	IData product = new DataMap();
		        	IDataset eleIdList = new DatasetList();
		        	boolean flag = false;
		        	String product_id = "";
		        	if(IDataUtil.isNotEmpty(dataSet)){
		        		for (int i = 0; i < dataSet.size(); i++) {
		        			product = dataSet.getData(i);
		        			IData eleInfo = new DataMap();
		        			if("10200".equals(qwproductType)||"10100".equals(qwproductType)){//视频流量定向流量包（话费扣费-用户自选ServiceID）		                
                                String serviceIdList= productList.getData(h).getString("SERVICE_ID_LIST","");
                                eleInfo.put("PRODUCT_TYPE", qwproductType);
                                eleInfo.put("SERVICE_ID_LIST", serviceIdList);
	                        }
		        			if("1".equals(product.getString("CTRM_PRODUCT_TYPE"))){ //终端产品
								continue; 
							}else if ("2".equals(product.getString("CTRM_PRODUCT_TYPE"))) {								
								data = executeContract(product,input,eparchyCode,number);
								if (!"0000".equals(data.getString("BIZORDERRESULT"))) {
									return data;
								}
							} else if ("3".equals(product.getString("CTRM_PRODUCT_TYPE"))) {								
								eleInfo.put("ELEMENT_ID", product.getString("ELEMENT_ID"));
								eleInfo.put("MODIFY_TAG", "0");
								if ("P".equals(product.getString("ELEMENT_TYPE_CODE"))) {
									eleInfo.put("ELEMENT_ID", product.getString("PRODUCT_ID"));
									//判断是不是超享卡内优惠变更
									IData userProInfo = UcaInfoQry.qryUserMainProdInfoBySn(number);
									if (checkID.startsWith("qwc") && userProInfo != null && product.getString("PRODUCT_ID","").equals(userProInfo.getString("PRODUCT_ID"))&&"10004445".equals(userProInfo.getString("PRODUCT_ID"))) {
										flag = true;
									}
								}
								eleInfo.put("ELEMENT_TYPE_CODE", product.getString("ELEMENT_TYPE_CODE"));
								
								// 关于一级能力开放平台新增、改造和家固话相关接口的通知
								String productId = product.getString("PRODUCT_ID");
								IDataset commpara = CommparaInfoQry.getCommparaByCodeCode1("CSM", "7359", "HJGH_PRODUCT_CONFIG", productId); // 和家固话资费配置
								if (IDataUtil.isNotEmpty(commpara)) {
									long discntFee = Long.parseLong(commpara.getData(0).getString("PARA_CODE3")); // 套餐资费
									IData userOweFee = AcctCall.queryOweFee(number);
									if (IDataUtil.isNotEmpty(userOweFee)) {
										long acctBalance = userOweFee.getLong("RSRV_NUM3"); // 实时结余，单位/分
										if (acctBalance < discntFee) {
											data.put("BIZORDERRESULT", "2998");
											data.put("RESERVE", "话费余额小于办理的套餐资费");
											return data;
										}
									} else {
										data.put("BIZORDERRESULT", "2998");
										data.put("RESERVE", "查询话费余额失败");
										return data;
									}

									IData userInfo = UcaInfoQry.qryUserInfoBySn(number);
									if (IDataUtil.isEmpty(userInfo)) {
										data.put("BIZORDERRESULT", "2998");
										data.put("RESERVE", "根据号码[" + number + "]未查询到有效用户信息");
										return data;
									}
									eleInfo.put("ELEMENT_ID", product.getString("PRODUCT_ID"));
									eleInfo.put("MODIFY_TAG", "0");
									eleInfo.put("ELEMENT_TYPE_CODE", "P");
								}
								eleIdList.add(eleInfo);
							}
						}
		        		if(flag){ //超享卡优惠内变更，重新做下元素处理
		  	  				IDataset elements = builderElements(number, checkID);
		  	  				eleIdList.clear();
		  	  				eleIdList.addAll(elements);
		  	  			}
		        		if (IDataUtil.isNotEmpty(eleIdList)) {
		        			data = executeProduct(eleIdList,input,eparchyCode,number);
		        			if (!"0000".equals(data.getString("BIZORDERRESULT"))) {
								return data;
							}
						}
		        		
		        	} else{
		        		data.put("BIZORDERRESULT","2999");
			    		data.put("RESERVE","商品或者产品的映射关系没有配置.请联系管理员!");
			    		return data;
		        	}
				    }
		        	data.put("BIZORDERRESULT","0000");
		    		data.put("RESERVE","业务办理验证正确");
		     		}
		        }
				return data;
		}
		else{
			//只有产品编码---得到boss编码 
        	data.put("BIZORDERRESULT","2001");
    		data.put("RESERVE","不存在您指定的验证类型. (01为手机号；02为宽带号码)");
    		return  data;
		}
	}
	/**
	 * 执行产品信息
	 * 
	 * @author tanjl
	 * @param productInfos
	 * @param platSvcInfos
	 * @throws Exception
	 */
	public IData executeProduct(IDataset productInfos, IData input, String eparchyCode, String number) throws Exception 
	{

		IData data = new DataMap();
		data.put("BIZORDERRESULT", "0000");
		data.put("RESERVE", "可以受理该业务");

		// 产品信息
		if (IDataUtil.isNotEmpty(productInfos)) 
		{
			String productId = productInfos.getData(0).getString("ELEMENT_ID","");
			try {
				IData infoParam = new DataMap();
				 if("10200".equals(productInfos.getData(0).getString("PRODUCT_TYPE",""))||"10100".equals(productInfos.getData(0).getString("PRODUCT_TYPE",""))){ 			
			       IData returnData=AbilityRuleCheck.checkVideopckrule(number, productInfos.getData(0),eparchyCode);   
			       if(IDataUtil.isNotEmpty(returnData)){
			          productInfos.add(returnData);
			       }
				 }
				infoParam.put("ELEMENTS", productInfos);
				infoParam.put("SERIAL_NUMBER", number);
				infoParam.put("IN_MODE_CODE", input.getString("IN_MODE_CODE"));
				infoParam.put("KIND_ID", input.getString("KIND_ID"));
				infoParam.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
				IDataset retnInfo = new DatasetList();
				// 关于一级能力开放平台新增、改造和家固话相关接口的通知
				IDataset commpara = CommparaInfoQry.getCommparaByCodeCode1("CSM", "7359", "HJGH_PRODUCT_CONFIG", productId); // 和家固话产品配置
				if (IDataUtil.isNotEmpty(commpara)) { // 不做预受理校验
//					infoParam = jointOrderElement(infoParam);
//					infoParam.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
//					infoParam.put("CHECK_CUSTOMER_DO_SERVICE", "1");
//					retnInfo = CSAppCall.call("SS.IMSLandLineRegSVC.tradeReg", infoParam);// 和固话产品校验
				} else {
					retnInfo = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", infoParam);
				}

				if (retnInfo != null && retnInfo.size() > 0 && StringUtils.isBlank(retnInfo.getData(0).getString("ORDER_ID"))) 
				{
					// data.put("BIZORDERRESULT","3004");
					data.put("BIZORDERRESULT", "2013");
					data.put("RESERVE", retnInfo.getData(0).getString("X_RESULTINFO").substring(0, 100));
				}

			} 
			catch (Exception e) 
			{
				String errorStr = getMessage(e);
				if (StringUtils.isNotEmpty(errorStr)) 
				{
					// errorStr = errorStr.substring(errorStr.indexOf("@"));
					// errorStr = errorStr.substring(0,errorStr.indexOf(":"));
					String[] errorMessage = errorStr.split("`");
					if (errorMessage.length >= 2) 
					{
						String strExceptionMessage = errorMessage[1];
						boolean bEM = strExceptionMessage.contains("产品无法办理");
						if (bEM) 
						{
							data.put("BIZORDERRESULT", "2013");
							data.put("RESERVE", errorMessage[1]);
						} 
						else 
						{
							data.put("BIZORDERRESULT", "3004");
							data.put("RESERVE", errorMessage[1]);
						}
					} 
					else 
					{
						data.put("BIZORDERRESULT", "3004");
						data.put("RESERVE", errorStr);
					}
				}
			}
		}
		return data;
	}
	
	/**
	 * 执行合约计划
	 * 
	 */
	public IData executeContract(IData input, IData paramInput, String eparchyCode, String number) throws Exception {
		
		IData data = new DataMap();
		data.put("BIZORDERRESULT", "0000");
		data.put("RESERVE", "能订购新合约");
		// String number=input.getString("SERIAL_NUMBER","");
		IDataset midRes = UserInfoQry.getUserInfoBySn(number, "0");
		if (midRes == null || midRes.size() == 0) 
		{
			CSAppException.apperr(CrmUserException.CRM_USER_17);// ("830013", "查询用户信息无资料！");
		}
		String userId = midRes.getData(0).getString("USER_ID");
		// 有一个字段检查的类型. 检查的产品ID
		// String currentProId = midRes.getData(0).getString("PRODUCT_ID"); //
		// 用户当前的产品Id
		boolean bool = false;
		String passProId = "";
		// 产品信息
		try {

			String checkProId = input.getString("CHECK_ELEMENT_ID"); // 需要验证的产品id
			String checkProType = input.getString("CHECK_ELEMENT_TYPE"); // 需要验证的产品类型

			if (StringUtils.isNotBlank(checkProId) && StringUtils.isNotBlank(checkProType)) 
			{ 	// 这两个不为空，
				// 才需要验证下面的内容
				if ("D".equalsIgnoreCase(checkProType)) 
				{ // 产品的
					// 找出用户预约的优惠
					IDataset distcntSet = AbilityPlatCheckRelativeQry.getUserProductById(userId);
					if (distcntSet != null) 
					{
						for (int i = 0; i < distcntSet.size(); i++) 
						{
							IData disdata = new DataMap();
							disdata = distcntSet.getData(i);
							String disCode = disdata.getString("DISCNT_CODE");
							// 开始业务判断 --
							if (checkProId.indexOf(disCode) >= 0) 
							{ // 当月或者次月满足
								bool = true;
								// 继续往下判断
								break;
							}
						}
						
					}
				}
				IDataset changeInfo = AbilityPlatCheckRelativeQry.getUserChangeInfo(userId); // 预约的变更的套餐
				if ("P".equalsIgnoreCase(checkProType)) 
				{ // 产品
					if (changeInfo != null) 
					{
						for (int i = 0; i < changeInfo.size(); i++) 
						{
							passProId = changeInfo.getData(i).getString("PRODUCT_ID");
							// 开始业务判断 --
							if (checkProId.indexOf(passProId) >= 0) 
							{ // 当月或者次月满足
								bool = true;
								// 继续往下判断
								break;
							}
						}
					}
				}

				if (!bool) 
				{ // 当月和次月都不满足
					if (changeInfo != null && changeInfo.size() > 1) 
					{ // 有预约的产品变更
						// 验证失败
						// data.put("BIZORDERRESULT", "3004");
						data.put("BIZORDERRESULT", "2000");
						data.put("RESERVE", "业务办理验证失败,已有预约的产品变更！");
						return data;
					} 
					else 
					{
						IDataset productInfos = new DatasetList(); // 产品元素
						String[] strs = checkProId.split("\\|");
						for (int i = 0; i < strs.length; i++) 
						{
							if (strs[i].startsWith("qwc")) 
							{
								IDataset elements = builderElements(number, strs[i]);
								data = executeProduct(elements, paramInput, eparchyCode, number);
							} 
							else 
							{
								productInfos.clear();
								input.put("MODIFY_TAG", "0");
								if ("P".equalsIgnoreCase(checkProType)) 
								{
									input.put("PRODUCT_ID", strs[i]);
									input.put("ELEMENT_TYPE_CODE", checkProType);
								}
								else 
								{
									input.put("ELEMENT_ID", strs[i]);
									input.put("ELEMENT_TYPE_CODE", checkProType);
								}

								productInfos.add(input);
								data = executeProduct(productInfos, paramInput, eparchyCode, number);

								// 没有预约的产品变更
								// 校验产品变更

							}

							if (!"0000".equals(data.getString("BIZORDERRESULT"))) // 验证产品变更失败
							{
								return data;
							}
						}

					}

				}

			}

			IData saleActiveInfo = new DataMap();
			saleActiveInfo.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
			saleActiveInfo.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
			saleActiveInfo.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
			saleActiveInfo.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE"));
			saleActiveInfo.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
			saleActiveInfo.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
			saleActiveInfo.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
			saleActiveInfo.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
			saleActiveInfo.put("SERIAL_NUMBER", number);
			saleActiveInfo.put("ACTION_TYPE", "0");
			saleActiveInfo.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
			IDataset retMap = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleActiveInfo);

			IData contractData = (retMap != null && retMap.size() > 0) ? retMap.getData(0) : null;

			if (StringUtils.isBlank(contractData.getString("ORDER_ID")))
			{
				data.put("BIZORDERRESULT", "3004");
				data.put("RESERVE", contractData.getString("X_RESULTINFO", "不能办理此合约"));
			}

		} catch (Exception ex) {

			String errorStr = getMessage(ex);
			if (StringUtils.isNotEmpty(errorStr)) {
				// errorStr = errorStr.substring(errorStr.indexOf("@"));
				// errorStr = errorStr.substring(0,errorStr.indexOf(":"));
				String[] errorMessage = errorStr.split("`");
				data.put("BIZORDERRESULT", "3004");
				data.put("RESERVE", errorMessage[1]);
			}

		}

		return data;

	}
	/**
	 * 处理异常
	 * @param e
	 * @return
	 */
	private String getMessage(Exception e) {
    	Throwable t = Utility.getBottomException(e);
    	String s = "";
    	if(t != null){
    		s = t.getMessage();
    	}

    	if(StringUtils.isNotBlank(s)){
    		if(s.length() > 120){
    			s = s.substring(0, 120);
    		}
    	}
    	return s;
    }
	
	/**
	 * 连带订购处理
	 * @param param
	 * @return
	 */
	public IData jointOrderElement(IData param) throws Exception {
		String serialNumber = param.getString("SERIAL_NUMBER");
		IDataset elements = param.getDataset("ELEMENTS");
		if (IDataUtil.isEmpty(elements)) {
			return param;
		}
		String productId = elements.getData(0).getString("ELEMENT_ID");

		IData data = new DataMap();
		data.put("AUTH_SERIAL_NUMBER", serialNumber);
		IDataset wideDataset = CSAppCall.call("SS.IMSLandLineSVC.checkAuthSerialNum", data); // 校验并拼装宽带信息

		IData infoParam = new DataMap();
		if (IDataUtil.isNotEmpty(wideDataset)) {
			IData wideData = wideDataset.first();
			infoParam.put("WIDE_USER_ID", wideData.getString("WIDE_USER_ID"));
			infoParam.put("WIDE_END_DATE", wideData.getString("WIDE_END_DATE"));
			infoParam.put("WIDE_START_DATE", wideData.getString("WIDE_START_DATE"));
			infoParam.put("WIDE_ADDRESS", wideData.getString("WIDE_ADDRESS")); // 宽带安装地址
			infoParam.put("WIDE_STATE", wideData.getString("WIDE_STATE"));
			infoParam.put("WIDE_STATE_NAME", wideData.getString("WIDE_STATE_NAME"));
			infoParam.put("WIDE_PRODUCT_NAME", wideData.getString("WIDE_PRODUCT_NAME"));
			infoParam.put("RSRV_STR4", wideData.getString("RSRV_STR4")); // 给PBOSS自动预约派单与回单用
		}

		// 根据产品ID获取组合商品以及其对应的必选依赖元素，拼装SELECTED_ELEMENTS
		IDataset productElements = ProductElementsCache.getProductElements(productId);//根据product_id获取该产品下的所有元素
		infoParam.put("SELECTED_ELEMENTS", productElements.toString());

		infoParam.put("SERIAL_NUMBER", serialNumber);
		infoParam.put("AUTH_SERIAL_NUMBER", serialNumber);
//		infoParam.put("WIDE_SERIAL_NUMBER", "0" + areaCode + ftNo);
		infoParam.put("OLD_FIX_NUMBER", ""); // 固话号码
//		infoParam.put("FIX_NUMBER", ftNo); // 固话号码
		infoParam.put("PRODUCT_ID", productId);
		infoParam.put("PRODUCT_NAME", UpcCall.qryOfferNameByOfferTypeOfferCode(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT));
		infoParam.put("BRAND", "IMS固话");
		infoParam.put("TRADE_TYPE_CODE", "6800");
		infoParam.put("PRODUCT_TYPE_CODE", "IMSP");

		return infoParam;
	}
	
	/**
	 * 检查用户状态是否正常
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean checkUserStates(String number) throws Exception {
		// 根据号码找地州
		IData iparam = new DataMap();
		iparam.put("SERIAL_NUMBER", number);
		iparam.put("REMOVE_TAG", "0");
		boolean bool = false;
		IDataset dataSet = AbilityPlatCheckRelativeQry.getUserInfoBySn(iparam);
		for (int i = 0; i < dataSet.size(); i++) {
			String stats = dataSet.getData(i).getString("USER_STATE_CODESET");
			if (null != stats && "" != stats) {
				if ("0".equals(stats)) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}
	/**
	 * 手机宽带业务办理资格校验
	 * 1、号码状态是否正常；2、校验该号码是否允许办理对应宽带业务
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData checkSnBroadband(IData Idata) throws Exception
	{
		Idata.put("NUMTYPE", Idata.getString("SERVICE_TYPE"));
		String numType=IDataUtil.chkParam(Idata, "NUMTYPE");//01：手机号码 02：固话号码 03：宽带帐号 04：vip卡号 05：集团编码；本期只有01：手机号码
		String sn = IDataUtil.chkParam(Idata, "SERIAL_NUMBER");
		IData data = new DataMap();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        
        if(IDataUtil.isEmpty(userInfo))
        {
        	data.put("BIZ_CODE", "2999");
    		data.put("BIZ_DESC", "用户资料不存在");
    		
    		return data;
        }
		
        if(!"0".endsWith(userInfo.getString("USER_STATE_CODESET")))
        {
        	data.put("BIZ_CODE", "2009");
    		data.put("BIZ_DESC", "用户状态不正常，不能办理宽带业务");
    		
 			return data;
    	}         	
        
        IDataset ids = BroadBandInfoQry.getBroadBandAccessAcctBySerialNumber(sn);
		
        if(IDataUtil.isNotEmpty(ids))
        {
        	data.put("BIZ_CODE", "3999");
    		data.put("BIZ_DESC", "用户已经办理了宽带业务");
    		return data;
        }
        
        data.put("BIZ_CODE", "0000");
		data.put("BIZ_DESC", "用户可以办理宽带业务");
		
        return data;
	}
	/**
	 * 用户状态查询
	 * 用户状态查询是由外部平台发起的，旨在请求查询用户目前的状态，如申请停机、欠费停机
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData getUserState(IData Idata)
	{
		IData data = new DataMap();
        data.put("BIZ_CODE", "0000");
		data.put("BIZ_DESC", "查询用户状态成功");
		try{
		// 入参校验
        IDataUtil.chkParam(Idata, "SERIAL_NUMBER");
        String serviceType =IDataUtil.chkParam(Idata, "SERVICE_TYPE");
        String serialNumber = Idata.getString("SERIAL_NUMBER");
        if("02".equals(serviceType)){
        	String areaCode = IDataUtil.chkParam(Idata, "AREA_CODE");
        	serialNumber = areaCode+serialNumber;
        }
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        		
        if(IDataUtil.isEmpty(userInfo))
        {
        	IData users=queryAllUserInfo(serialNumber);//所有状态的号码
        	if(IDataUtil.isNotEmpty(users)){
        		String removaTag=users.getString("REMOVE_TAG");
        		if("1".equals(removaTag)||"3".equals(removaTag)){//预销号
        			data.put("USERSTAUTS","03");
        		}else if("2".equals(removaTag)||"4".equals(removaTag)||"5".equals(removaTag)||"6".equals(removaTag)){
        			data.put("USERSTAUTS","04");
        		}       		
        	}else{
        	 data.put("USERSTAUTS", "99");
        	}
        }
        else
        {
//        	00	正常
//        	01	单向停机
//        	02	停机
//        	03	预销户
//        	04	销户
//        	05	过户
//        	06	改号
//        	99	此号码不存在	
        	
//        	0	开通
//        	1	申请停机
//        	2	挂失停机
//        	3	并机停机
//        	4	局方停机
//        	5	欠费停机
//        	6	申请销号
//        	7	高额停机
//        	8	欠费预销号
//        	9	欠费销号
//        	A	欠费半停机
//        	B	高额半停机
//        	C	非实名半停机
//        	E	转网销号停机
//        	F	申请预销停机
//        	G	申请半停机
//        	H	非实名停机
//        	I	申请停机(收月租)
//        	L	骚扰电话停机
//        	M	黑名单停机
//        	O	过期停机
//        	R	关联半停机
//        	S	关联停机
//        	T	骚扰电话半停机
//        	Z	垃圾信息治理特殊停机
        	
        	String stauts = userInfo.getString("USER_STATE_CODESET");
        	String acctTag= userInfo.getString("ACCT_TAG");
        	if("0".equals(stauts))
        	{
        		if("2".equals(acctTag)){
        			data.put("BIZ_CODE", "2008");
        			data.put("BIZ_DESC", "用户未激活！");
        		}else{
        			data.put("USERSTAUTS","00");
        		}        		 
        	}
        	else if("8".equals(stauts))
        	{
        		 data.put("USERSTAUTS","03");
        	}
        	else if("9".equals(stauts) || "6".equals(stauts))
        	{
        		 data.put("USERSTAUTS","04");
        	}else if("A".equals(stauts)||"B".equals(stauts)||"C".equals(stauts)||"G".equals(stauts)||"R".equals(stauts)||"T".equals(stauts)){
        		 data.put("USERSTAUTS","01");
        	}
        	else
        	{
        		 data.put("USERSTAUTS","02");
        	}
        }
		}catch(Exception e){
			 data.put("USERSTAUTS","");
			data.put("BIZ_CODE", "2999");
			data.put("BIZ_DESC", "查询用户状态失败："+e.getMessage());
		}       
		return data;
	}
	public IData queryAllUserInfo(String serialNumber) throws Exception 
	{
		IData data=new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);
		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT * from TF_F_USER where SERIAL_NUMBER=:SERIAL_NUMBER order by IN_DATE desc ");
		IDataset dataset = Dao.qryByParse(parser);
	    if(IDataUtil.isEmpty(dataset)){
	    	return null;
	    }
		return dataset.getData(0);
	}
	/**
	 * 用户归属地市查询
	 * 用户归属地市查询是由外部平台发起的，旨在请求查询用户目前的号码归属地市
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData getUserEparchyCode(IData Idata) 
	{
		IData data = new DataMap();
		data.put("BIZ_CODE", "0000");
		data.put("BIZ_DESC", "查询用户归属地市成功");
		try{
		// 入参校验
        IDataUtil.chkParam(Idata, "SERIAL_NUMBER");
        IDataUtil.chkParam(Idata, "SERVICE_TYPE");
               
        String serialNumber = Idata.getString("SERIAL_NUMBER");
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo))
        {
        	 
              IDataset dataset = AcctCall.queryMpAreaCode(serialNumber);
              if(logger.isDebugEnabled()){
				  logger.debug("-------dataset--------"+dataset);	
			  }
            if (IDataUtil.isEmpty(dataset))
            {
    			data.put("PROVICE_CODE", "");
            	data.put("AREA_CODE", "");
            	data.put("BIZ_CODE", "2043");
    			data.put("BIZ_DESC", "用户身份信息错误！");
    			return data;
            }
            for (int i = 0; i < dataset.size(); i++)
             {
               IData dataArea = dataset.getData(i);
    		   data.put("PROVICE_CODE", dataArea.getString("PROV_CODE"));
    		   data.put("AREA_CODE", dataArea.getString("AREA_CODE"));
    		   data.put("IF_LOCAL", "1");//是否本省号码	0是 1否
               }	
        }
        else
        {
        	data.put("IF_LOCAL", "0");
        	//data.put("CITY_CODE", userInfo.getString("CITY_CODE"));
        	data.put("PROVICE_CODE", "898");
        	String eparchyCode=userInfo.getString("EPARCHY_CODE");
        	data.put("AREA_CODE", eparchyCode.replaceAll("^(0+)", ""));
        }
		}catch(Exception e){
			data.put("BIZ_CODE", "2043");
			data.put("BIZ_DESC", "查询用户归属地市失败："+e.getMessage());
	    	data.put("PROVICE_CODE", "");
	    	data.put("AREA_CODE", "");
		}
        
		return data;
	}
	/**
	 * 销售订单信息同步接口
	 * 支持将用户办理终端、合约类业务、套餐类业务、号卡类业务、宽带类业务的数据下发给省份。接口采用异步方式，省公司接收到请求后先返回已接收，省公司处理完成后，通过业务订购结果反馈将订单处理结果反馈到第三方合作伙伴平台（对长流程业务，如入网，开户成功即返回结果，物流、写卡等环节线下处理）。
              一个交易中存在一个或多个子订单，一个子订单内只包含一种产品，一个交易内的所有子订单在一次交易中同步到省公司。
              支付环节由第三方平台处理，客户支付完毕后在调用此能力将销售订单同步给后端系统处理。
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData sellOrderSynchro(IData input) throws Exception {
		chkParamNoStr(input, "BILL_DAY", "802127"); // 订单对账标识
		List xmListList = (List) input.get("TLIST_INF");
		JSONArray json = JSONArray.fromObject(xmListList);
		DatasetList xmList = DatasetList.fromJSONArray(json);
		input.remove("TLIST_INF");
		input.put("TLIST_INF", xmList);
		
		logger.debug("-----sellOrderSynchro,input:"+input);
        IDataset tlistNums= input.getDataset("TLIST_INF");//订单信息
//		int tlistNum = input.getDataset("TLIST_NUM").size();
		IData retnData = new DataMap();

		// 存储多个订单的TID
		List<String> tidList = new ArrayList<String>();

		if (tlistNums.size() > 0) {
			for (int k = 0; k < tlistNums.size(); k++) {				
				IData  tlist=tlistNums.getData(k);
				tidList.add(tlist.getString("TID"));
				tlist.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));//
				tlist.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));//
				tlist.put("BILL_DAY", input.getString("BILL_DAY"));
				retnData=saveListOrderInfo(tlist);
				if(!"0000".equals(retnData.getString("BIZ_CODE"))){
					return retnData;
				}
			}
			logger.error("=================开始处理订单==============");
			// 处理订单
			try{
			retnData=dealListOrderInfo(input, tidList);
			}catch(Exception e){
				retnData.put("BIZ_CODE", "2999");
				retnData.put("BIZ_DESC", "订单处理失败："+e.getMessage());
			}
			logger.error("=================结束处理订单==============");
		}
		if("0000".equals(retnData.getString("BIZ_CODE"))){
			//--掉账务接口开具发票
			if (tlistNums.size() > 0) {
				for (int k = 0; k < tlistNums.size(); k++) {
					IData  tlist=tlistNums.getData(k);
					String phoneOprType = tlist.getString("PHONE_OPR_TYPE");
	                String needInvoice = tlist.getString("NEED_INVOICE");
	                String chargeType = tlist.getString("CHARGE_TYPE");
	                if("02".equals(phoneOprType) && "1".equals(needInvoice) && ("0".equals(chargeType) || "2".equals(chargeType) || "6".equals(chargeType))){
	                	//查询台帐 调用开票接口
	                	IData printData = new DataMap();
	        			buildPrintData(printData,tlist,input);
	        			try {
							KjPrintBean bean = BeanManager.createBean(KjPrintBean.class);
							bean.printKJForSC(printData);
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
	        			//结束--
	                }
				}
			}
		}
//		retnData.put("BIZ_CODE", "0000");
//		retnData.put("BIZ_DESC", "信息同步成功");
	
		return retnData;
	}
	private void buildPrintData(IData printData, IData tlist, IData retnData) throws Exception {
		String strPrintId = SeqMgr.getPrintId(); // 新生成发票PRINT_ID
		printData.put("PRINT_ID", strPrintId);
		printData.put("TYPE", "P0001"); // 票据类型：发票P0001、收据P0002、免填单(业务受理单)P0003
		printData.put("CUST_TYPE", "PERSON");//客户类型
		printData.put("APPLY_CHANNEL", "0");// 开票发起渠道：：0-营业个人业务;1-集团有ACCTID业务；2-集团无ACCTID业务；3-账务
		printData.put("TOTAL_MONEY", tlist.getString("PAYMENT"));
		printData.put("PAY_NAME", tlist.getString("INVOICE_NAME",""));
		printData.put("ABILITY", "1");
		printData.put("NAME", "销售订单同步");
		printData.put("TAG", "NKFP");
		String tradeId = (String) retnData.getDataset("TRADE_ID_S").get(0);
		printData.put("TRADE_ID", tradeId);
		IDataset mainTradeInfos = TradeInfoQry.getTradeAndBHTradeByTradeId(printData.getString("TRADE_ID"));//查询台账及历史台账
		if(IDataUtil.isNotEmpty(mainTradeInfos)){
			IData mainTradeInfo=mainTradeInfos.getData(0);
			printData.putAll(mainTradeInfo);
		}
	}

	/**
	 * 保存销售订单信息
	 * 
	 * @param pd
	 * @param data
	 * @param conn
	 * @throws Exception
	 */
	public IData saveListOrderInfo(IData data) throws Exception {
		IData ret=new DataMap();
		IDataset orderLists = data.getDataset("ORDER_LIST");

		String retnCode = "0";
		String msg = "订购成功";
		ret.put("BIZ_CODE","0000");
		ret.put("BIZ_DESC","订购成功");
		// 独立事务用来保存接收到的订单信息，以免在登记订单的过程异常而导致订单信息丢失
		DBConnection conn = null;
		try {
			conn = SessionManager.getInstance().getAsyncConnection("cen1");
			chkParamNoStr(data, "TID", "802127"); // 订单编码
			chkParamNoStr(data, "OUT_TID", "802127"); // 合作渠道订单编码
			chkParamNoStr(data, "CHANNEL_ID", "802127"); // 渠道ID
			chkParamNoStr(data, "CREATE_TIME", "802127"); // 订单创建时间
															// CREATE_TIME
															// PAY_MENT
			chkParamNoStr(data, "PAY_TIME", "802127"); // 订单支付时间
			chkParamNoStr(data, "DISTRIBUTION", "802127"); // 是否需省公司配送
			chkParamNoStr(data, "BUYER_NICK", "802127"); // 买家名称
			chkParamNoStr(data, "PAYMENT", "802127"); // 买家名称
			chkParamNoStr(data, "ORDER_LIST", "802127"); // 子订单信息
			 //20170421 视频流量定向包需求新增字段
			chkParamNoStr(data, "PHONE_OPR_TYPE", "802127");//业务号码操作类型
			chkParamNoStr(data, "NEED_INVOICE", "802127");//是否需要开发票

			data.put("UNI_CHANNEL_ID", data.getString("UNI_CHANNEL_ID",""));
			data.put("PAY_MENT", data.getString("PAYMENT"));
			data.put("UPDATE_TIME", SysDateMgr.getSysTime());
			data.put("CREATE_TIME", data.getString("CREATE_TIME"));
			data.put("PAY_TIME", data.getString("PAY_TIME"));

			insertIntoCtrmTList(conn, data);

			// 订单中子订单信息信息

			IData orderList = new DataMap();
			if (orderLists != null && orderLists.size() > 0) {
				for (int i = 0; i < orderLists.size(); i++) {
					orderList = orderLists.getData(i);
					String eparchyCode = getMofficeBySN(orderList.getString("PHONE", ""));
					orderList.put("TID", data.getString("TID")); // 订单ID
					chkParamNoStr(orderList, "OID", "802127"); // 子订单ID
					chkParamNoStr(orderList, "OUT_OID", "802127"); // 合作渠道子订单编码
					chkParamNoStr(orderList, "GOODS_ID", "802127"); // 商品编码
					chkParamNoStr(orderList, "TITLE", "802127"); // 商品标题
					chkParamNoStr(orderList, "NUM", "802127"); // 购买数量
					chkParamNoStr(orderList, "PRICE", "802127"); // 价格
					chkParamNoStr(orderList, "PROVINCE", "802127"); // 商品所在地省份
					chkParamNoStr(orderList, "CITY", "802127"); // 商品所在地城市
					chkParamNoStr(orderList, "TOTAL", "802127"); // 该子订单总金额
					chkParamNoStr(orderList, "ADJEST_FEE", "802127"); // 手工调整金额
					// chkParamNoStr(orderList, "MOBILE_NO","802127"); //业务号码类型
					chkParamNoStr(orderList, "ORDER_STATUS", "802127"); // 订单状态
					IDataUtil.chkParam(orderList, "MOBILE_NO");// 业务号码类型
 	                IDataUtil.chkParam(orderList, "PHONE");// 业务号码	 
					orderList.put("ACCEPT_DATE", SysDateMgr.getSysTime());
					orderList.put("ORDER_STATUS", data.getString("ORDER_STATUS"));
					orderList.put("UPDATE_TIME", SysDateMgr.getSysTime());
					orderList.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID"));
					orderList.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID"));
                    insertIntoCtrmOrder(conn, orderList);
 
					/* 根据商品ID查询子订单下面的产品信息 */
					IDataset productIds = orderList.getDataset("PRODUCT_LIST");//产品信息
						//AbilityOpenPlatFormIntfSVC.getIDatasetSpecl("PRODUCE_ID",orderList.getString("PRODUCE_ID"));

					if (productIds != null && productIds.size() > 0) {
						// 子订单产品信息
						IData productInfo = new DataMap();

						for (int h = 0; h < productIds.size(); h++) {

							productInfo = productIds.getData(h);
							chkParamNoStr(productInfo, "PRODUCT_ID", "802127");
							 //视频定向流量包add
	                        String productType=IDataUtil.chkParam(productInfo, "PRODUCT_TYPE","");
	                        String serviceIdList=productInfo.getString("SERVICE_ID_LIST","");
	                        productInfo.put("CTRM_PRODUCT_TYPE_CODE",productType);
	                        productInfo.put("CTRM_PRODUCT_SERVICEID",serviceIdList);
	                        
							productInfo.put("TID", data.getString("TID"));
							productInfo.put("OID", orderList.getString("OID"));
							productInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
							productInfo.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID"));
							productInfo.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID"));
							productInfo.put("PHONE", orderList.getString("PHONE",""));
							productInfo.put("CTRM_PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
							productInfo.put("STATUS", "0");

							/* 查询产品对应关系表信息 */
							IData inparam = new DataMap();
							inparam.put("EPARCHY_CODE", eparchyCode);
							inparam.put("CTRM_PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
							IDataset relaProductIds = QueryListInfo.queryListInfoForRelation(inparam);

							if (relaProductIds == null || relaProductIds.size() <= 0) {
								String errors = "订单产品ID【"+ productInfo.getString("PRODUCT_ID")+ "】不存在本地产品的映射关系，请管理员进行配置！";
								//CSAppException.appError("", errors);
								ret.put("BIZ_CODE","2999");
								ret.put("BIZ_DESC",errors);
								return ret;
							}

							for (int k = 0; k < relaProductIds.size(); k++) {
								IData relaMap = relaProductIds.getData(k);
								// relation 表中RSRV_STR1 为改营销活动是否需要IMEI办理
								// 如果为1则表示不需要 默认为空需要
								// RSRV_STR2 为改合约类型，对应TD_B_CTRM_CONTRACT表中
								// CONTRACT_TYPE 值
								// 能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品
								// 其中1和2不做处理，3的时候调用产品变更接口
								if ("1".equals(relaMap.getString("CTRM_PRODUCT_TYPE"))|| "3".equals(relaMap.getString("RSRV_STR2"))) {
									productInfo.put("STATUS", "1");// 终端产品默认为已处理完
																	// 号卡类合约也默认处理完
								}
								if ("3".equals(relaMap.getString("CTRM_PRODUCT_TYPE"))) {
									chkParamNoStr(orderList, "PHONE", "802127"); // 业务号码
								}
								//productInfo.put("PID", QueryListInfo.getSeqId());
								productInfo.put("PID", SeqMgr.getCtrmProId());
								productInfo.put("CONTRACT_ID", relaMap.getString("CONTRACT_ID", "-1"));
								productInfo.put("PRODUCT_ID", relaMap.getString("PRODUCT_ID", "-1"));
								productInfo.put("PACKAGE_ID", relaMap.getString("PACKAGE_ID", "-1"));
								productInfo.put("ELEMENT_ID", relaMap.getString("ELEMENT_ID", "-1"));
								productInfo.put("ELEMENT_TYPE_CODE", relaMap.getString("ELEMENT_TYPE_CODE"));
								productInfo.put("CTRM_PRODUCT_TYPE", relaMap.getString("CTRM_PRODUCT_TYPE"));
								productInfo.put("RSRV_STR1", relaMap.getString("RSRV_STR1"));
								productInfo.put("RSRV_STR2", relaMap.getString("RSRV_STR2"));
								productInfo.put("RSRV_STR3", relaMap.getString("RSRV_STR3"));
								productInfo.put("RSRV_STR4", relaMap.getString("RSRV_STR4"));
								productInfo.put("RSRV_STR5", relaMap.getString("RSRV_STR5"));
								productInfo.put("RSRV_STR6", "IS_ABILITY_TRANS");//省能力平台过来标志
								insertIntoCtrmOrderProduct(conn,productInfo);
							}

						}
					}

			          // 子订单扩展属性
 	                IDataset orderAddStr=orderList.getDataset("ORDER_ADD");
 	                if (IDataUtil.isNotEmpty(orderAddStr))
 	                {
 	                    IDataset orderAdds = new DatasetList(orderAddStr);
 	                    IData orderAdd = new DataMap();

 	                    for (int k = 0; k < orderAdds.size(); k++)
 	                    {
 	                        orderAdd = orderAdds.getData(k);
                            String addId=orderAdd.getString("ADD_ID");// 订单扩展属性ID
                            String addValue=orderAdd.getString("ADD_VALUE"); // 订单扩展属性值
 	                        IData orderAddr = new DataMap();
 	                        chkParamNoStr(orderAdd, "ADD_ID","802127"); // 订单扩展属性ID
						    chkParamNoStr(orderAdd,"ADD_VALUE", "802127"); // 订单扩展属性值
 	                        IDataUtil.chkParam(orderAdd, "ADD_ID"); // 订单扩展属性ID
                            IDataUtil.chkParam(orderAdd, "ADD_VALUE"); // 订单扩展属性值
                            orderAddr.put("OID", orderList.getString("OID")); // 子订单ID
                            orderAddr.put("ADDID", addId); // 子订单ID
                            orderAddr.put("ADDVALUE", addValue); // 子订单ID
                            insertIntoCtrmOrderAttr(conn,orderAddr);
 	                        
 	                    }
 	                }

				}
			}

			conn.commit();

		} catch (Exception e1) {
			logger.info(e1);
			if(conn != null)
			{
				conn.rollback();
			}
			
			retnCode = "2";
			msg = "订购同步失败"+e1.getMessage();
			ret.put("BIZ_CODE","3013");
			ret.put("BIZ_DESC",msg);
			//CSAppException.appError("",e1.getMessage());
		} finally {
			if(conn != null)
			{
				conn.close();
			}
		}
		return ret;
	}

	/**
	 * 处理订单登记
	 * 
	 * @param pd
	 * @param data
	 * @param tidList
	 * @throws Exception
	 */
	public IData dealListOrderInfo(IData data, List tidList) throws Exception {
		IData ret=new DataMap();
		IDataset tradeIds = new DatasetList();
		ret.put("BIZ_CODE","0000");
		ret.put("BIZ_DESC","订购成功");
		String productType="";
		if (tidList != null && tidList.size() > 0) {
			IData param = new DataMap();
			// 循环处理订单
			IDataset orderList = null;
			for (int i = 0; i < tidList.size(); i++) {
				String tid = (String) tidList.get(i);
				
				param.put("TID", tid);
				orderList = QueryListInfo.queryListInfoForTlist(param);
				// 循环处理子订单信息
				IDataset subOrderList = null;

				for (int j = 0; j < orderList.size(); j++) {
					IData orderInfo = orderList.getData(j);
					param.clear();
					param.put("TID", tid);
					param.put("OID", orderInfo.getString("OID"));
					param.put("STATUS", "0");
					subOrderList = QueryListInfo.queryListInfoForTidTlist(param);
					
					boolean hasVasFlag = false;    // 是否有合约产品
					boolean productFlag = false;   // 是否有产品变更
					boolean mainProFlag = false;   //是否是主产品变更
					boolean fxproductFlag = false;

					List<String> pidList = new ArrayList<String>();// 保存要更新状态的子订单产品表PID
					IDataset productParams = new DatasetList();
					IData saleActiveInfo = null;         //保存调用营销活动的参数

					IDataset eleIdList = new DatasetList();
					IDataset elements = new DatasetList();
					String route = getMofficeBySN(orderInfo.getString("PHONE"));
					IData proSmsInfo = new DataMap();
					
					IData userProInfo = UcaInfoQry.qryUserMainProdInfoBySn(orderInfo.getString("PHONE"));
					
					for (int k = 0; k < subOrderList.size(); k++) {
						IData subInfo = subOrderList.getData(k);
						IData eleInfo = new DataMap();
						data.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
						
						// 能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品
						// 其中1插表时已处理，2不做处理，3的时候调用产品变更接口
						if ("2".equals(subInfo.getString("CTRM_PRODUCT_TYPE"))) {
							hasVasFlag = true;
							if ("2".equals(orderInfo.getString("DISTRIBUTION"))) {
								saleActiveInfo = new DataMap();
								saleActiveInfo.put("EPARCHY_CODE", route);
								saleActiveInfo.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
								saleActiveInfo.put("%101!",orderInfo.getString("CHANNEL_ID",""));    //业务订购渠道
								saleActiveInfo.put("%103!",subInfo.getString("PRODUCT_ID"));     //本地合约编码
								if(subInfo.getString("ACCEPT_DATE")==null||"".equals(subInfo.getString("ACCEPT_DATE"))){
									saleActiveInfo.put("%105!",SysDateMgr.getSysDate()); 
								}else{
								saleActiveInfo.put("%105!",subInfo.getString("ACCEPT_DATE")); 
								}//合约订购时间
								saleActiveInfo.put("%107!",subInfo.getString("CTRM_PRODUCT_ID"));  //集团的合约编码
								saleActiveInfo.put("PARAM_CODE","ABILITY_CONTRACT"); 
								saleActiveInfo.put("PARA_CODE1",subInfo.getString("PACKAGE_ID"));
								saleActiveInfo.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
								saleActiveInfo.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
								saleActiveInfo.put(Route.ROUTE_EPARCHY_CODE, route);
								saleActiveInfo.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE"));
								saleActiveInfo.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
								saleActiveInfo.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
								saleActiveInfo.put("PRODUCT_ID", subInfo.getString("PRODUCT_ID"));
								saleActiveInfo.put("PACKAGE_ID", subInfo.getString("PACKAGE_ID"));
								saleActiveInfo.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
								saleActiveInfo.put("ACTION_TYPE", "0");
								saleActiveInfo.put("NO_TRADE_LIMIT", "TRUE");
								saleActiveInfo.put("PID", subInfo.getString("PID"));
								
							} else {
								continue;
							}

						} else if ("3".equals(subInfo.getString("CTRM_PRODUCT_TYPE"))) {
							productFlag = true;
							pidList.add(subInfo.getString("PID"));
							
							eleInfo.put("ELEMENT_ID", subInfo.getString("ELEMENT_ID"));
							eleInfo.put("MODIFY_TAG", "0");
							if ("P".equals(subInfo.getString("ELEMENT_TYPE_CODE"))) {
								mainProFlag = true;
								proSmsInfo.put("EPARCHY_CODE", route);
								proSmsInfo.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
								proSmsInfo.put("%101!",orderInfo.getString("CHANNEL_ID")); //业务订购渠道
								proSmsInfo.put("%102!",subInfo.getString("PRODUCT_ID"));       //本地套餐编码
								proSmsInfo.put("%104!",orderInfo.getString("CREATE_TIME",""));   //套餐订购时间
								proSmsInfo.put("%106!",subInfo.getString("CTRM_PRODUCT_ID"));  //集团的产品编码
								proSmsInfo.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
								proSmsInfo.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
								proSmsInfo.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
								proSmsInfo.put("PARAM_CODE","ABILITY_VAS");  
								
								eleInfo.put("ELEMENT_ID", subInfo.getString("PRODUCT_ID"));
								
								
								
								if (userProInfo != null && subInfo.getString("PRODUCT_ID","").equals(userProInfo.getString("PRODUCT_ID"))&&"10004445".equals(userProInfo.getString("PRODUCT_ID"))) {
									fxproductFlag = true;
									elements = builderElements(orderInfo.getString("PHONE"), subInfo.getString("CTRM_PRODUCT_ID"));
									continue;
								}
								
								if (userProInfo != null && subInfo.getString("PRODUCT_ID","").equals(userProInfo.getString("PRODUCT_ID"))) {
									continue;
								}
							}
							eleInfo.put("ELEMENT_TYPE_CODE", subInfo.getString("ELEMENT_TYPE_CODE"));
							//视频流量包才需要校验    add   
                            productType=subInfo.getString("CTRM_PRODUCT_TYPE_CODE","");
                            if("10200".equals(productType)||"10100".equals(productType)){ 
                            	 try{ 
                            		 IData productInfo=new DataMap();
                            		 productInfo.put("PRODUCT_TYPE", productType);
                            		 productInfo.put("SERVICE_ID_LIST", subInfo.getString("CTRM_PRODUCT_SERVICEID",""));
                            		 productInfo.put("PRODUCT_ID", subInfo.getString("CTRM_PRODUCT_ID",""));
                            		 subInfo.putAll(productInfo);
                            		 if(logger.isDebugEnabled()){
                            	        	logger.debug("-------subInfo入参--------"+subInfo);	
                            	        }
                                   //校验产品入参之间的关系，productType=102XX
                                     AbilityRuleCheck.checkParamRelation(orderInfo.getString("PHONE",""),subOrderList,route);
                                     //校验互斥关系以及拼数据
                                     IData retData=AbilityRuleCheck.checkVideopckrule(orderInfo.getString("PHONE"), subInfo,route);   
                                     if(IDataUtil.isNotEmpty(retData)){                        
                                    	 eleInfo.putAll(retData);                                           
                                     }
                                     }catch(Exception e){                                                  
                                      IData errorResult=new DataMap();
                                      IDataset result=new DatasetList();
                                      String error=e.getMessage();
                                      errorResult.put("X_RESULTCODE", "-1");
                                      errorResult.put("X_RESULTINFO", error);
                                      result.add(errorResult);
                                      AbilityRuleCheck.updateOrderProductStatus(result,orderInfo.getString("OID"),"");
                                      ret.put("BIZ_CODE","3013");
                              		  ret.put("BIZ_DESC","视频流量校验失败："+error);
                                      return ret;
                                    }     
                               
                            }
                			//视频流量包才需要校验  end 
							eleIdList.add(eleInfo);
						}
					}
					
					param.clear();
					
					param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
					param.put("UPDATE_TIME", SysDateMgr.getSysTime());
					param.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
					param.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
					param.put("OID", orderInfo.getString("OID"));
					param.put("TID", tid);

					IData retnData = new DataMap();
					if(fxproductFlag){
						//elements
						try {// 如果处理失败也更改子订单状态
							IData infoParam = new DataMap();
							infoParam.put("ELEMENTS", elements);
							infoParam.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
							infoParam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
							infoParam.put("KIND_ID", data.getString("KIND_ID"));
						    IDataset set  =  CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", infoParam);
							retnData= (set != null && set.size()>0)?set.getData(0):new DataMap();
							tradeIds.add(retnData.getString("TRADE_ID"));
							logger.info("产品变更接口返回参数："+set.toString());
							if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) 
									&& !"-1".equals(retnData.getString("ORDER_ID")) && mainProFlag)
		                    {
								proSmsInfo.put("TRADE_ID",retnData.getString("TRADE_ID"));
								logger.info("产品变更: "+proSmsInfo.toString());
								QueryInfoUtil.sendSMS(proSmsInfo);//发送短信
		                    }
						} catch (Exception e) {
							String msg = null;
//					        if (e instanceof InvocationTargetException)
//					        {
//					            Throwable targetEx = ((InvocationTargetException) e)
//					                    .getTargetException();
//					            if (targetEx != null)
//					            {
//					                msg = targetEx.getMessage();
//					            }
//					        } else
//					        {
					            msg = e.getMessage();
//					        }
							logger.info(msg);
							retnData.put("X_CHECK_TAG", "-1");
							//retnData.put("X_RESULTINFO", e.getMessage());
							retnData.put("X_RESULTINFO", msg);
							retnData.put("X_RESULTCODE", "-1");
							ret.put("BIZ_CODE","3013");
                    		ret.put("BIZ_DESC","调产品变更失败："+msg);
                           
						}
						
					}
					
					if (productFlag&&!fxproductFlag) {

						try {// 如果处理失败也更改子订单状态
							IData infoParam = new DataMap();
							infoParam.put("ELEMENTS", eleIdList);
							infoParam.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
							infoParam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
							infoParam.put("KIND_ID", data.getString("KIND_ID"));
						    IDataset set  =  CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", infoParam);
							retnData= (set != null && set.size()>0)?set.getData(0):new DataMap();
							tradeIds.add(retnData.getString("TRADE_ID"));
							logger.info("产品变更接口返回参数："+set.toString());
							if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) 
									&& !"-1".equals(retnData.getString("ORDER_ID")) && mainProFlag)
		                    {
								proSmsInfo.put("TRADE_ID",retnData.getString("TRADE_ID"));
								logger.info("产品变更: "+proSmsInfo.toString());
								QueryInfoUtil.sendSMS(proSmsInfo);//发送短信
		                    }
						} catch (Exception e) {
							if(logger.isDebugEnabled()){
                	        	logger.debug("-------产品变更报错了--------"+e.getMessage());	
                	        }
							
							String msg = null;
					        if (e instanceof InvocationTargetException)
					        {
					            Throwable targetEx = ((InvocationTargetException) e)
					                    .getTargetException();
					            if (targetEx != null)
					            {
					                msg = targetEx.getMessage();
					            }
					        } else
					        {
					            msg = e.getMessage();
					        }
							
							logger.info(msg);						
							retnData.put("X_CHECK_TAG", "-1");
							//retnData.put("X_RESULTINFO", e.getMessage());
							retnData.put("X_RESULTINFO", msg);
							retnData.put("X_RESULTCODE", "-1");
							ret.put("BIZ_CODE","3013");
                    		ret.put("BIZ_DESC","调产品变更失败："+msg);
                         
						}
					}
					
					//营销活动放到产品变更后面执行
					if (saleActiveInfo != null) {

						IData cData = new DataMap();
						cData.put("STATUS", "2");
						cData.put("ACCEPT_RESULT", "1");
						IData contractData = new DataMap();
						try {
							
							IDataset retMap = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf",saleActiveInfo);
							contractData = (retMap != null && retMap.size() > 0) ? retMap.getData(0):new DataMap() ;
							tradeIds.add(contractData.getString("TRADE_ID"));
							logger.info("营销活动接口输入参数："+contractData.toString());
							if (StringUtils.isNotBlank(contractData.getString("ORDER_ID")) 
									&& !"-1".equals(contractData.getString("ORDER_ID")))
		                    {
								cData.put("STATUS", "1");
								saleActiveInfo.put("TRADE_ID",contractData.getString("TRADE_ID"));
								logger.info("营销活动短信: "+saleActiveInfo.toString());
								QueryInfoUtil.sendSMS(saleActiveInfo);//发送短信
		                    }
						} catch (Exception e) {
							String msg = null;
					        if (e instanceof InvocationTargetException)
					        {
					            Throwable targetEx = ((InvocationTargetException) e)
					                    .getTargetException();
					            if (targetEx != null)
					            {
					                msg = targetEx.getMessage();
					            }
					        } else
					        {
					            msg = e.getMessage();
					        }
							
							logger.info(msg);
							//String msg = e.getMessage() == null? "":e.getMessage();
							msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
							cData.put("REMARK", msg);
							ret.put("BIZ_CODE","3013");
                    		ret.put("BIZ_DESC","调营销活动失败："+msg);
						}
						
						cData.put("TRADE_ID", contractData.getString("TRADE_ID", "-1"));
						cData.put("PID", saleActiveInfo.getString("PID"));
						cData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
						cData.put("UPDATE_TIME", SysDateMgr.getSysTime());
						cData.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
						cData.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
						productParams.add(cData);
					}
					
					// 如果没有合约产品且子订单都登记成功则修改子订单状态
					if (!hasVasFlag) {
						param.put("ORDER_STATUS", "07");
						param.put("ACCEPT_RESULT", "1");

						if (productFlag && (StringUtils.isBlank(retnData.getString("ORDER_ID")) 
								|| "-1".equals(retnData.getString("ORDER_ID")))) {
							param.put("ORDER_STATUS", "90");
							param.put("ACCEPT_RESULT", "2");
						}
						updateInfoForTid(param);
					}
					String retnCode = "2";
					
					if ( productFlag && StringUtils.isNotBlank(retnData.getString("ORDER_ID")) 
							&& !"-1".equals(retnData.getString("ORDER_ID"))) {
						retnCode = "1";
					}
					String msg = retnData.getString("X_RESULTINFO", "");
					msg = msg.length() > 200 ? msg.substring(0, 200) : msg;

					// 更新子订单产品记录表
					for (int k = 0; k < pidList.size(); k++) {
						String pid = pidList.get(k);
						IData inputData = new DataMap();
						inputData.put("PID", pid);
						inputData.put("TRADE_ID", retnData.getString("TRADE_ID", "-1"));
						inputData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
						inputData.put("STATUS", retnCode);
						inputData.put("ACCEPT_RESULT", retnCode);
						inputData.put("REMARK", msg);
						inputData.put("UPDATE_TIME", SysDateMgr.getSysTime());
						inputData.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
						inputData.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
						productParams.add(inputData);
					}
					if (productParams != null && productParams.size() > 0)
						updateBatchInfo("TF_B_CTRM_TLIST","UPD_CTRM_ORDER_PRODUCT", productParams);
				}
			}
		}
		data.put("TRADE_ID_S", tradeIds);
		return ret;
	}
	/**
	 * 更新订单表中的状态
	 * @param updateOrder
	 * @throws Exception
	 */
	public void updateInfoForTid(IData param) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST","UPD_CTRM_ORDER_TID_OID", param,Route.CONN_CRM_CEN);
		
	}
	/**
	 * 批量更新表数据
	 * @param updateOrder
	 * @throws Exception
	 */
	public void updateBatchInfo(String tabName,String sqlRef,IDataset params) throws Exception{
		Dao.executeBatchByCodeCode(tabName, sqlRef, params,Route.CONN_CRM_CEN);
	}
	public void insertIntoCtrmTList(DBConnection conn,IData data) throws Exception{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		dao.insert(conn, "TF_B_CTRM_TLIST", data);
	}
	
	public void insertIntoCtrmOrder(DBConnection conn,IData data) throws Exception{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		dao.insert(conn, "TF_B_CTRM_ORDER", data);
	}
	
	public void insertIntoCtrmOrderProduct(DBConnection conn,IData data) throws Exception{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		dao.insert(conn, "TF_B_CTRM_ORDER_PRODUCT", data);
	}
	
	public void insertIntoCtrmOrderAttr(DBConnection conn,IData data) throws Exception{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		dao.insert(conn, "TF_B_CTRM_ORDER_ATTR", data);
	}
	public String getMofficeBySN(String serialNumber) throws Exception {
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);

		IDataset tmp = UserInfoQry.getMofficeBySN(data);
		if (IDataUtil.isNotEmpty(tmp)) {
			IData data2 = tmp.getData(0);
			return data2.getString("EPARCHY_CODE");
		} else {// 携转号码无moffice信息
			IDataset out = TradeNpQry.getValidTradeNpBySn(serialNumber);
			if (IDataUtil.isNotEmpty(out)) {
				return out.getData(0).getString("AREA_CODE");
			} else {
				return null;
			}
		}
	}
	/***********************************************************************************
     * 针对飞享套餐特殊处理<BR/>
     * 飞享套餐传入的产品ID为"qwc + 数字"，需要转换为省内产品编码<BR/>
     * 1.如果用户主产品和转换后的产品不一致，那么需要进行主产品变更<BR/>
     * 2.如果用户主产品和转换后的产品一致，只需要进行产品内元素变更<BR/>
     * 
     * @param serialNumber	用户号码
     * @param newProductID	变更产品
     * @return
     * @throws Exception
     */
    public IDataset builderElements(String serialNumber, String newProductID)throws Exception{
		//1.根据用户号码查询用户信息
        IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        if(IDataUtil.isEmpty(userInfos))
        	CSAppException.apperr(CrmUserException.CRM_USER_1);
        //2.查询用户主产品信息
        String userID = userInfos.getData(0).getString("USER_ID");
        IDataset userMainProduct = UserProductInfoQry.queryUserMainProduct(userID);
        if(IDataUtil.isEmpty(userMainProduct))
        	CSAppException.apperr(CrmUserException.CRM_USER_45, userID);
        //3.对传入的产品进行转换
		IDataset configElementList = qryConfigElements(newProductID, userInfos.getData(0).getString("EPARCHY_CODE"));
		String productID = configElementList.getData(0).getString("PRODUCT_ID");
		
		//4.1.如果是主产品变更，需要设置参数进行主产品变更
		if(! productID.equals(userMainProduct.getData(0).getString("PRODUCT_ID"))){
			IData item = new DataMap();
			item.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			item.put("ELEMENT_ID", productID);
			item.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			configElementList.add(item);
			
		//4.2.如果是元素变更，需要删除原产品下指定优惠
		}else{
			//4.2.1.查询用户已订购的飞享套餐优惠
			IDataset orderDiscnt = UserDiscntInfoQry.getFXDiscntByUserId(userID);
			if(IDataUtil.isEmpty(orderDiscnt))
				CSAppException.apperr(CrmUserException.CRM_USER_914, productID);
			//4.2.2.处理相同的元素：如果用户已订购了变更后套餐的元素，不用给予删除、添加操作
			dealDuplicateElements(configElementList, orderDiscnt);
			if(IDataUtil.isEmpty(configElementList))
				return configElementList;
			//4.2.2.退订这些优惠
			for(int i = 0; i < orderDiscnt.size(); i++){
				//如果是GPRS优惠，不能退订 
				if(IDataUtil.isNotEmpty(DiscntInfoQry.getDiscntIsValid("5", orderDiscnt.getData(i).getString("DISCNT_CODE"))))
					continue;
				IData item = new DataMap();
				item.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
				item.put("ELEMENT_ID", orderDiscnt.getData(i).getString("DISCNT_CODE"));
				item.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
				item.put("INST_ID", orderDiscnt.getData(i).getString("INST_ID"));
				configElementList.add(item);
			}
		}
		return configElementList;
	}
    /************************************************************************************
	 * 将传入的产品编码转换为省内的产品编码<BR/>
	 * 使用TD_S_COMMPARA表PARAM_ATTR为2801配置进行产品转换<BR/>
	 * 
	 * @param newProductID	转换前的产品编码
	 * @param eparchyCode	地州
	 * @return
	 * @throws Exception
	 */
	private IDataset qryConfigElements(String newProductID, String eparchyCode)throws Exception{
		//1.查询产品转换关系
		IDataset configElementList = CommparaInfoQry.getCommPkInfo("CSM", "2801", newProductID, eparchyCode);
		//2.没有查询到转换关系，抛出异常
		if(configElementList.isEmpty())
			CSAppException.apperr(ParamException.CRM_PARAM_359);
		
		IData configElement = configElementList.getData(0);
		String countStr = configElement.getString("PARA_CODE2");//必选元素个数
		//3.如果【必选元素个数】，不在5个内，配置已经有问题，抛出异常
		if(! countStr.matches("[1-5]"))
			CSAppException.apperr(ParamException.CRM_PARAM_145);
		String productID = configElement.getString("PARA_CODE1"), elementItem = null;
		//4.解析配置元素信息：ELEMENT_ID + '_' + ELEMENT_TYPE_CODE
		IDataset result = new DatasetList();
		IData item = null;
		for(int i = 0, len = Integer.parseInt(countStr); i < len; i++){
			elementItem = configElement.getString("PARA_CODE" + (i + 3));//取 PARA_CODE3（包括在内）后的PARA_CODE2个元素
			String[] elementInfo = elementItem.split("_");
			if(elementInfo.length != 2)
				CSAppException.apperr(ParamException.CRM_PARAM_146);
				
			item = new DataMap();
			item.put("PRODUCT_ID", productID);
			item.put("ELEMENT_TYPE_CODE", elementInfo[1]);
			item.put("ELEMENT_ID", elementInfo[0]);
			item.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			result.add(item);
		}
		return result;
	}
	/*****************************************************************************
	 * 去重：新增列表和删除列表中相同的元素<BR/>
	 * @param addList	新增列表
	 * @param delList	删除列表
	 */
	private void dealDuplicateElements(IDataset addList, IDataset delList){
		if(IDataUtil.isEmpty(addList) || IDataUtil.isEmpty(delList))
			return;
		
		for(int i = 0; i < addList.size(); i++){
			String addTypeCode = addList.getData(i).getString("ELEMENT_TYPE_CODE", ""), addID = addList.getData(i).getString("ELEMENT_ID", "");
			
			for(int j = 0; j < delList.size(); j++){
				if(addTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT) && addID.equals(delList.getData(j).getString("DISCNT_CODE"))){
					addList.remove(i--);
					delList.remove(j--);
				}
			}
		}
	}
	/**
	 * 校验传入在是否为空
	 * 
	 * @param data
	 * @param keys
	 * @throws Exception
	 */
	public void chkParamNoStr(IData data, String keys, String errorCode)
			throws Exception {
		String key = data.getString(keys, "");
		if ("".equals(key)) {
			CSAppException.appError(errorCode, "传入在字段" + keys + "值不能为空！");
		}
	}
	/**
	 * 退订订单信息同步
	 *  支持将用户办理终端、合约类业务、套餐类业务、号卡类业务、宽带类业务的退订数据下发给省份。接口采用异步方式，省公司接收到请求后先返回已接收，省公司处理完成后，通过退订订单结果反馈将订单处理结果反馈到对接平台。
                       退订内容为相应订单内的全部或部分商品，如果退订单内部分商品需在退款描述中说明。
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData refundOrderSynchro(IData data) throws Exception{
		chkParamNoStr(data, "REFUND_TYPE", "802127");//退款类型
		chkParamNoStr(data, "REFUND_ID", "802127");//退款单编码
		chkParamNoStr(data, "TID", "802127");//订单编码
		chkParamNoStr(data, "OID", "802127");//子订单编码
		chkParamNoStr(data, "TRADE_MONEY", "802127");//订单总金额
		chkParamNoStr(data, "REFUND_TIME", "802127");//发起退款时间
		chkParamNoStr(data, "REFUND_MONEY", "802127");//退款金额		
		chkParamNoStr(data, "CHANNEL_ID", "802127");		
		chkParamNoStr(data, "OUT_REFUND_ID", "802127");	
		IData ret =new DataMap();       
        try{
        ret=refundOrderSynchro1(data);
        }catch(Exception ex){
        	ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "退订订单信息同步失败："+ex.getMessage());
        }       
		return ret;
	}
	/**
	 * 订单退款同步
	 * @param input
	 * @throws Exception
	 */
	public IData refundOrderSynchro1(IData input) throws Exception{
		IData ret =new DataMap();
		 ret.put("BIZ_CODE", "0000");
	     ret.put("BIZ_DESC", "退订订单信息同步成功！");
		boolean contractFlag = false;
		String serialNumber = "";
		
		//查询合约信息
		IData param = new DataMap();
		param.put("TID", input.getString("TID"));
		param.put("OID", input.getString("OID"));
		param.put("CTRM_PRODUCT_TYPE", "2");
		IDataset orderContractInfos = CSAppCall.call("SS.ShoppingOrderSVC.queryProOrderByCtrmType", param);
		IData orderContractInfo = new DataMap();
		if (IDataUtil.isNotEmpty(input) && IDataUtil.isNotEmpty(orderContractInfos)) {
			orderContractInfo = orderContractInfos.getData(0);
			serialNumber = orderContractInfo.getString("PHONE");
			IData inputData = new DataMap();
			input.put("SERIAL_NUMBER", 	orderContractInfo.getString("PHONE"));
			inputData.put("PID", orderContractInfo.getString("PID"));
			inputData.put("TRADE_ID", "-1");
			inputData.put("ACCEPT_DATE", SysDateMgr.getSysDate());
			inputData.put("STATUS", "5");
			inputData.put("ACCEPT_RESULT", "1");
			inputData.put("UPDATE_TIME", SysDateMgr.getSysDate());
			inputData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
			inputData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
			
			if("1".equals(orderContractInfo.getString("RSRV_STR2"))){      //1--预存购机直接无条件返回成功
				//同步信息插入订单子产品表
				contractFlag = true;
				input.put("RSP_TIME", SysDateMgr.getSysTime());
				inputData.put("STATUS", "4");
				CSAppCall.call("SS.ShoppingOrderSVC.updCtrmOrderProduct", inputData);
				
			}else if("2".equals(orderContractInfo.getString("RSRV_STR2"))){   //2--购机赠送费用
				//调返销流程,确定TRADE_ID不为空的时候才调用合约返销流程，否则认为合约还未执行.
				contractFlag = true;
				input.put("RSP_TIME", SysDateMgr.getSysTime());
			
				if(!"".equals(orderContractInfo.getString("TRADE_ID")) && !"-1".equals(orderContractInfo.getString("TRADE_ID"))){
					IData inParam = new DataMap();
					inParam.put("SERIAL_NUMBER", 	orderContractInfo.getString("PHONE"));
					inParam.put("TRADE_ID", 		orderContractInfo.getString("TRADE_ID"));
					inParam.put(Route.ROUTE_EPARCHY_CODE, input.getString(Route.ROUTE_EPARCHY_CODE,"0898"));
	            	try{
	            		IDataset retnList = CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", inParam);
	            		IData contractData = (retnList != null && retnList.size() > 0) ? retnList.getData(0):new DataMap() ;
	            	    if (StringUtils.isNotBlank(contractData.getString("ORDER_ID")) && !"-1".equals(contractData.getString("ORDER_ID"))){
	            	    	inputData.put("STATUS", "4");
	            	    }
	    			}catch(Exception ex){
	    				ret.put("BIZ_CODE", "3014");
	    			    ret.put("BIZ_DESC", "退订失败："+ex.getMessage());
	    				SessionManager.getInstance().rollback();
	    				logger.error("lixm6refundOrderSynchro= "+ex.getMessage());
	    			}
				}

				CSAppCall.call("SS.ShoppingOrderSVC.updCtrmOrderProduct", inputData);
				
				//查询产品信息
				param.put("CTRM_PRODUCT_TYPE", "3");
				IDataset orderProductInfos = CSAppCall.call("SS.ShoppingOrderSVC.queryProOrderByCtrmType", param);
				
				if (IDataUtil.isNotEmpty(orderProductInfos)) {
					inputData.put("STATUS", "5");
					IData orderProductInfo = orderProductInfos.getData(0);
					if(!"".equals(orderProductInfo.getString("TRADE_ID")) && !"-1".equals(orderProductInfo.getString("TRADE_ID"))){
						IData inparam = new DataMap();
						inparam.put("SERIAL_NUMBER", orderProductInfo.getString("PHONE"));
				        IDataset errTradeInfos = CSAppCall.call("SS.CancelChangeProductSVC.queryErrorInfoTrade", inparam);
				        if (IDataUtil.isEmpty(errTradeInfos)){
				            IDataset cancelTradeInfos = CSAppCall.call("SS.CancelChangeProductSVC.queryChangeProductTrade", inparam);
				            //存在预约产品变更，需要进行取消处理
				            if (IDataUtil.isNotEmpty(cancelTradeInfos)){
				            	inparam.put("TRADE_ID", orderProductInfo.getString("TRADE_ID"));
				            	try{
				            		IDataset retnList =  CSAppCall.call("SS.CancelChangeProductSVC.cancelChangeProductTrade", inparam);
				            		IData proData = (retnList != null && retnList.size() > 0) ? retnList.getData(0):new DataMap() ;
				            	    if (StringUtils.isNotBlank(proData.getString("ORDER_ID")) 
											&& !"-1".equals(proData.getString("ORDER_ID"))){
				            	    	inputData.put("STATUS", "4");
				            	    }
				            	}catch(Exception ex){
				            		ret.put("BIZ_CODE", "3014");
				    			    ret.put("BIZ_DESC", "退订失败："+ex.getMessage());
									logger.error("lixm6refundOrderSynchro产品取消= "+ex.getMessage());
				    			}
				            	
				            	CSAppCall.call("SS.ShoppingOrderSVC.updCtrmOrderProduct", inputData);
				            }
				        }
					}
				}
			}
			
			
			if (contractFlag) {
				//发短信
				input.put("PARAM_CODE", "RETURN_ORDER");
				input.put("%101!", input.getString("CHANNEL_ID"));
				input.put("%103!", orderContractInfo.getString("PRODUCT_ID"));
				input.put("%105!", SysDateMgr.getSysTime());
				input.put("%107!", orderContractInfo.getString("CTRM_PRODUCT_ID"));
				input.put("PARAM_CODE1", orderContractInfo.getString("PACKAGE_ID"));
				input.put("EPARCHY_CODE", input.getString(Route.ROUTE_EPARCHY_CODE));
				QueryInfoUtil.sendSMS(input);//发送短信
				
				if (!"".equals(orderContractInfo.getString("NEW_IMEI",""))) {
					//把用户营销活动绑定的IMEI终止
					IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
					param.put("USER_ID", userInfo.getString("USER_ID"));
					param.put("IMEI", orderContractInfo.getString("NEW_IMEI"));
					QueryInfoUtil.updUserImei(param, userInfo.getString("EPARCHY_CODE"));
				}
			}
		}
		
		input.put("UPDATE_TIME", SysDateMgr.getSysTime());
		input.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
		input.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
		Dao.insert("TF_B_CTRM_REFUND", input,Route.CONN_CRM_CEN);
		input.put("RSRV_STR6", "IS_ABILITY_TRANS");//省能力平台过来标志
		Dao.insert("TF_B_CTRM_REFUND_SUB", input,Route.CONN_CRM_CEN);
		return ret;
	}

	/**
	 * 商品信息同步
	 * @param Idata
	 * @return
	 * @throws Exception 
	 * @throws Exception
	 */
	public IData goodsInfoSyn(IData Idata) throws Exception 
	{
	      IData returndata = new DataMap();
	      try{
	      returndata = goodsInfoSynchro(Idata);
	      }catch(Exception ex){
	       returndata.put("BIZ_CODE","2999");
	       returndata.put("BIZ_DESC","商品信息同步失败："+ ex.getMessage());  
	      }
	      return returndata;
	}
	/**
	 * 参数校验
	 * @param Idata
	 * @throws Exception
	 */
	public IData checkParams(IData paramList) throws Exception
	{		
		IData data =new DataMap();
    	data.put("BIZ_CODE","0000");
    	data.put("BIZ_DESC","商品信息同步成功！");
		String goodsOrg=IDataUtil.chkParam(paramList, "GOODS_ORG");//所属组织校验
		String provinceName=StaticUtil.getStaticValue("PRO_CODE_FB", goodsOrg);//.getStaticList("PRO_CODE_FB");		  
		if(StringUtils.isEmpty(provinceName)){
//		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该省份编码不存在！");
		 data.put("BIZ_CODE","4025");
		 data.put("BIZ_DESC","该省份编码不存在！");
		 return data;
		}
		String goodsPrice=paramList.getString("GOODS_PRICE","");//商品价格
		if(!isNumeric(goodsPrice)){
		 data.put("BIZ_CODE","4000");
		 data.put("BIZ_DESC","商品价格格式错误,请输入数字！");
		 return data;
//		CSAppException.apperr(CrmCommException.CRM_COMM_103, "商品价格格式错误,请输入数字！");   
		}
//		String startDate=paramList.getString("START_DATE","");
//		String endDate=paramList.getString("END_DATE","");
		String createTime=paramList.getString("CREATE_TIME","");
		String changTime=paramList.getString("CHANGE_TIME","");
//		if(!"8".equals(startDate.length())){
//		CSAppException.apperr(CrmCommException.CRM_COMM_103, "商品生效开始时间格式错误,正确格式为YYYYMMDD！");
//		}
//		if(!"8".equals(endDate.length())){
//		 CSAppException.apperr(CrmCommException.CRM_COMM_103, "商品生效结束时间格式错误,正确格式为YYYYMMDD！");
//		}
		if(StringUtils.isNotBlank(createTime)&&createTime.length()!=14){
		 data.put("BIZ_CODE","4001");
		 data.put("BIZ_DESC","商品创建时间格式错误,正确格式为YYYYMMDDHHMMSS！");
		 return data;
//	     CSAppException.apperr(CrmCommException.CRM_COMM_103, "商品创建时间格式错误,正确格式为YYYYMMDDHHMMSS！");   
	     }
	     if(StringUtils.isNotBlank(changTime)&&changTime.length()!=14){
	      data.put("BIZ_CODE","4001");
		  data.put("BIZ_DESC","商品变更时间格式错误,正确格式为YYYYMMDDHHMMSS！");
		  return data;
//	     CSAppException.apperr(CrmCommException.CRM_COMM_103, "商品变更时间格式错误,正确格式为YYYYMMDDHHMMSS！");   
	     }
	     String goodsType=paramList.getString("GOODS_TYPE");
		 if(goodsType.length()!=5||!isNumeric(goodsType)){
		 data.put("BIZ_CODE","4027");
		 data.put("BIZ_DESC","商品类型格式错误,请按照规范传，如09001！");
		 return data;
//		 CSAppException.apperr(CrmCommException.CRM_COMM_103, "商品类型格式错误,请按照规范传，如09001！");  
		}
		 return data;
	}
	public static boolean isNumeric(String str){
		 for (int i = 0; i < str.length(); i++){			
		  if (!Character.isDigit(str.charAt(i))){
			    return false;
			}
		}
			  return true;
		}

	 /**
     * 商品信息同步接口
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData goodsInfoSynchro(IData input) throws Exception
    {
    	IData data =new DataMap();
    	data.put("BIZ_CODE","0000");
    	data.put("BIZ_DESC","商品信息同步成功！");
    	IDataUtil.chkParam(input, "OPR_NO");//同步工单号
         /**
          * 处理平台传值获取不到的问题
          */
    	List xmListList= (List) input.get("GOODS_INF");
            JSONArray json = JSONArray.fromObject(xmListList);
            DatasetList xmList = DatasetList.fromJSONArray(json);
            input.remove("GOODS_INF");
            input.put("GOODS_INF", xmList);
    	
        IDataset goodInfos=input.getDataset("GOODS_INF");
        
    	if(IDataUtil.isEmpty(goodInfos)){
    		data.put("BIZ_CODE","2999");
    		data.put("BIZ_DESC","商品信息列表为空！");
    		return data;
    		//CSAppException.apperr(CrmCommException.CRM_COMM_103,"商品信息列表不能为空");
    		
    	}
    	int goodsNum = goodInfos.size();
        for (int i = 0; i < goodsNum; i++)
        {
            IDataset paramLists = goodInfos;
            if (IDataUtil.isNotEmpty(paramLists.getData(i)))
            {
                IData paramList = paramLists.getData(i);
      	       
                 String goodsAreaStr = "";
//                IDataset goodsAreas = paramList.getDataset("GOODS_AREALIST");;
//
//                for (int j = 0; j < goodsAreas.size(); j++)
//                {
//                    IData goodsArea = goodsAreas.getData(j);
//                    if ("".equals(goodsAreaStr))
//                    {
//                        goodsAreaStr = goodsArea.getString("GOODS_AREA");
//                    }
//                    else
//                    {
//                        goodsAreaStr = goodsAreaStr + "|" + goodsArea.getString("GOODS_AREA");
//                    }
//                }
                
                IDataUtil.chkParam(paramList, "GOODS_ORG");//所属组织
                IDataUtil.chkParam(paramList, "GOODS_TYPE");//商品类型
                IData ret=checkParams(paramList);//校验入参
                if(!"0000".equals(ret.getString("BIZ_CODE"))){
                	return ret;
                }
                paramList.put("GOODS_AREA", paramList.getString("GOODS_AREA","000"));
                paramList.put("OPR_NO",input.getString("OPR_NO"));
                paramList.put("GOODS_ORG", paramList.getString("GOODS_ORG"));
                paramList.put("GOODS_TYPE",paramList.getString("GOODS_TYPE"));	
                paramList.put("UPDATE_TIME", SysDateMgr.getSysTime());
                paramList.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
                paramList.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
                goodsInfoSynchro(paramList, "g");

                IDataset productInfs = new DatasetList(paramList.getString("PRODUCT_INF"));
                for (int k = 0; k < productInfs.size(); k++)
                {
                    IData productInf = productInfs.getData(k);
                    IDataUtil.chkParam(productInf, "IS_SINGLE_CHOICE");//是否可复选
                    IDataUtil.chkParam(productInf, "IS_MANDATORY");//是否必选
                    IDataUtil.chkParam(productInf, "PRODUC_GROUP_ID");//是否必选
                    productInf.put("GOODS_ID", paramList.getString("GOODS_ID"));
                    productInf.put("IS_SINGLECHOICE", productInf.getString("IS_SINGLE_CHOICE"));
                    productInf.put("IS_MANDATORY", productInf.getString("IS_MANDATORY"));
                    productInf.put("CHANGE_TYPE", paramList.getString("CHANGE_TYPE"));
                    productInf.put("GROUP_INDEX", productInf.getString("PRODUC_GROUP_ID"));
                    productInf.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    productInf.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
                    productInf.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
                    goodsInfoSynchro(productInf, "ps");

                    IDataset productList = new DatasetList(productInf.getString("PRODUCT_LIST"));
                    for (int L = 0; L < productList.size(); L++)
                    {
                        IData productAddInfo = productList.getData(L);

                        productAddInfo.put("GOODS_ID", paramList.getString("GOODS_ID"));
                        productAddInfo.put("CHANGE_TYPE", paramList.getString("CHANGE_TYPE"));
                        productAddInfo.put("GROUP_INDEX", productInf.getString("PRODUC_GROUP_ID"));
                        productAddInfo.put("PRODUCT_TYPE", productAddInfo.getString("PRODUCT_CLASS",""));
                        productAddInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                        productAddInfo.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
                        productAddInfo.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
                        goodsInfoSynchro(productAddInfo, "p");
                        
//                        IDataset productAttrs = new DatasetList(productInf.getString("PRODUCT_ATTRLIST")); 
//                        for (int m = 0; m < productAttrs.size(); m++)
//                        {
//                        	IData productAttr = productAttrs.getData(m);
//                        	
//                        	productAttr.put("GOODS_ID", paramList.getString("GOODS_ID"));
//                        	productAttr.put("CHANGE_TYPE", paramList.getString("CHANGE_TYPE"));
//                        	productAttr.put("GROUP_INDEX", productInf.getString("PRODUC_GROUP_ID"));
//                        	productAttr.put("PRODUCT_TYPE", productAddInfo.getString("PRODUCT_CLASS"));
//                        	productAttr.put("UPDATE_TIME", SysDateMgr.getSysTime());
//                        	productAttr.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
//                        	productAttr.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
//                        	productAttr.put("PRODUCT_ID", productAddInfo.getString("PRODUCT_ID"));
//                        	 goodsInfoSynchro(productAttr, "par");                      	
//                        	
//                        }

                    }
                }

                IDataset goodsAdds = paramList.getDataset("GOODS_ADDLIST");
                if(IDataUtil.isNotEmpty(goodsAdds)){
                	 for (int a = 0; a < goodsAdds.size(); a++)
                     {
                         IData goodsAdd = goodsAdds.getData(a);
                         IData googsData = new DataMap();
                             googsData.put("GOODS_ID", paramList.getString("GOODS_ID"));
                             googsData.put("CHANGE_TYPE", paramList.getString("CHANGE_TYPE"));
                             googsData.put("ADD_ID", goodsAdd.getString("ADD_ID")); // 扩展属性ID
                             googsData.put("ADD_NAME", goodsAdd.getString("ADD_NAME")); // 扩展属性值
                             goodsInfoSynchro(googsData, "attr");

                     }
                }
               
            }
        }
        return data;
    }
    /**
     * 商品信息同步
     * 
     * @param pd
     * @param data
     * @throws Exception
     */
    public boolean goodsInfoSynchro(IData data, String tag) throws Exception
    {
        boolean isreturn = false;
        if ("g".equals(tag))
        {
            if ("1".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.insert("TD_B_CTRM_GOODS", data, Route.CONN_CRM_CEN);
            }
            else if ("2".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.save("TD_B_CTRM_GOODS", data, new String[]
                { "GOODS_ID" }, Route.CONN_CRM_CEN);
            }
            else if ("3".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.delete("TD_B_CTRM_GOODS", data, new String[]
                { "GOODS_ID" }, Route.CONN_CRM_CEN);
            }
        }
        else if ("ps".equals(tag))
        {
            if ("1".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.insert("TD_B_CTRM_GOODS_GROUP", data, Route.CONN_CRM_CEN);
            }
            else if ("2".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.save("TD_B_CTRM_GOODS_GROUP", data, new String[]
                { "GOODS_ID", "GROUP_INDEX" }, Route.CONN_CRM_CEN);
            }
            else if ("3".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.delete("TD_B_CTRM_GOODS_GROUP", data, new String[]
                { "GOODS_ID", "GROUP_INDEX" }, Route.CONN_CRM_CEN);
            }
        }
        else if ("p".equals(tag))
        {
            if ("1".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.insert("TD_B_CTRM_GOODS_PRODUCT", data, Route.CONN_CRM_CEN);
            }
            else if ("2".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.save("TD_B_CTRM_GOODS_PRODUCT", data, new String[]
                { "GOODS_ID", "GROUP_INDEX", "PRODUCT_ID" }, Route.CONN_CRM_CEN);
            }
            else if ("3".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.delete("TD_B_CTRM_GOODS_PRODUCT", data, new String[]
                { "GOODS_ID", "GROUP_INDEX", "PRODUCT_ID" }, Route.CONN_CRM_CEN);
            }
        }
        else if ("attr".equals(tag))
        {
            if ("1".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.insert("TD_B_CTRM_GOODS_ATTR", data, Route.CONN_CRM_CEN);
            }
            else if ("2".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.save("TD_B_CTRM_GOODS_ATTR", data, new String[]
                { "GOODS_ID", "ADD_ID" }, Route.CONN_CRM_CEN);
            }
            else if ("3".equals(data.getString("CHANGE_TYPE")))
            {
                isreturn = Dao.delete("TD_B_CTRM_GOODS_ATTR", data, new String[]
                { "GOODS_ID", "ADD_ID" }, Route.CONN_CRM_CEN);
            }
        }
//        else if ("par".equals(tag))
//        {
//            if ("1".equals(data.getString("CHANGE_TYPE")))
//            {
//                isreturn = Dao.insert("TD_B_CTRM_GOODS_PRODUCT_ATTR", data, Route.CONN_CRM_CEN);
//            }
//            else if ("2".equals(data.getString("CHANGE_TYPE")))
//            {
//                isreturn = Dao.save("TD_B_CTRM_GOODS_PRODUCT_ATTR", data, new String[]
//                 { "GOODS_ID", "GROUP_INDEX", "PRODUCT_ID" , "ATTR_ID"}, Route.CONN_CRM_CEN);
//            }
//            else if ("3".equals(data.getString("CHANGE_TYPE")))
//            {
//                isreturn = Dao.delete("TD_B_CTRM_GOODS_ATTR", data, new String[]
//                { "GOODS_ID", "GROUP_INDEX", "PRODUCT_ID" , "ATTR_ID"}, Route.CONN_CRM_CEN);
//            }
//        }
        return isreturn;

    }
    /**
     * 8.6.9.	已订购业务查询
     * 查询用户已订购且未失效的产品信息。
                          已订购业务定义分为四类，包括套餐类、增值业务类、服务功能类、营销活动等其他类
     * @param Idata
     * @return
     * @throws Exception
     */
    public IData getOrderedSvc(IData input) throws Exception
	{
    	IData ret =new DataMap();
    	IData data=new DataMap();
        IDataset dataset=new DatasetList();
        ret.put("BIZ_CODE", "0000");
    	ret.put("BIZ_DESC", "查询已订购业务成功！");
    	try{
    	String serialNumber =IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	IDataUtil.chkParam(input, "SERVICE_TYPE");//01：手机号码,02：固话号码 ,03：宽带帐号 ,04：vip卡号 ,05：集团编码；本期只有01：手机号码	    	
    	String busiType=IDataUtil.chkParam(input, "BUSI_TYPE");//01：套餐类,02：增值业务类,03：服务功能类,04：营销活动等其他类
    	IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);    	
        if (IDataUtil.isEmpty(userInfo))
        {
        	ret.put("BIZ_CODE", "2010");
         	ret.put("BIZ_DESC", "用户订购业务暂停验证！");
            return ret;
//            CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
        }
        String userId = userInfo.getString("USER_ID");
        String eparchyCode=userInfo.getString("EPARCHY_CODE");
        IDataset infos=new DatasetList();
    	if("01".equals(busiType)){//套餐类
    		infos=getDicntSvc(userId);//产品信息ProductInfo []		    		
    		data.put("PRODUCT_TYPE", "01");
 	         
    	}else if ("02".equals(busiType)){//增值业务类
    		infos=getPlatSvc(userId,eparchyCode);
    		data.put("PRODUCT_TYPE", "02");
    		
    	}else if ("03".equals(busiType)){//服务功能类
    		infos=getNormalSvc(userId);
    		data.put("PRODUCT_TYPE", "03");
    	}else{//营销活动等其他类
    		infos=getActiveSvc(userId);
    		data.put("PRODUCT_TYPE", "04");
    	}
    	data.put("PRODUCT_INF", infos);
    	dataset.add(data);//业务信息BIZ_INFOLIST
    	ret.put("BIZ_INFOLIST", dataset);
    	}catch(Exception e){
    	    logger.debug("-----------getOrderedSvc---"+e);
    		ret.put("BIZ_CODE", "2999");
        	ret.put("BIZ_DESC", "查询已订购业务失败！");
        	return ret;
    	}
    	return ret;
	}
    /**
     * 套餐类
     * @param userId
     * @return
     * @throws Exception
     */
    public IDataset getDicntSvc(String userId) throws Exception
	{
    	IDataset ret=new DatasetList();	    	 
    	 IDataset queryInfos = UserDiscntInfoQry.queryUserNormalDiscntsByUserIdNew1(userId);//UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
         if(IDataUtil.isNotEmpty(queryInfos)){
        	 for(int i=0;i<queryInfos.size();i++ ){
            	 IData data=new DataMap();
        		 IDataset temp = new DatasetList();
        		 IData result=new DataMap();	        		
        		 IData info=queryInfos.getData(i);
        		 result.put("PRODUCT_EFFDATE", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));//生效时间
        		 result.put("PRODUCT_EXPDATE", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("END_DATE")));//失效时间
        		 String discntCode=info.getString("DISCNT_CODE","");
//        		 IDataset discntInfo=getDiscntInfo(discntCode);
        		 OfferCfg offercfg = OfferCfg.getInstance(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);
        		 if(offercfg != null){
	        		 result.put("PRODUCT_ID", discntCode);//产品编码
	        		 result.put("PRODUCT_NAME", offercfg.getOfferName());//产品名称
	        		 result.put("PRODUCT_DESC", offercfg.getDescription());//产品描述	        		 
        		 }
        		 temp.add(result);//产品名称PRODUCT_LIST
//        		 data.put("BIZ_TYPE", "01");	        		 
//        		 data.put("PRODUCT_CODE", info.getString("PRODUCT_ID",""));
        		 data.put("VALID_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));	        		 	        		 
        		 data.put("PRODUCT_LIST", temp);
            	 ret.add(data);//产品信息PRODUCT_INF
        	 }	
        	 //进行排序操作
             if(!ret.isEmpty()){
                // DataHelper.sort(ret, "VALID_TIME", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
                
             } 
         }	        
    	
         return ret;
	}
    /**
     * 增值类
     * @param userId
     * @return
     * @throws Exception
     */
    public IDataset getPlatSvc(String userId,String eparchyCode) throws Exception
	{
    	IDataset ret=new DatasetList();	    
    	IDataset queryInfos=UserPlatSvcInfoQry.queryPlatSvcInfoByUserIdForAbility(userId);
    	logger.debug("------getPlatSvc------queryInfos:"+queryInfos);
    	if(IDataUtil.isNotEmpty(queryInfos)){
    		for(int i=0;i<queryInfos.size();i++){	
        		IData data=new DataMap();    			
    			IData info=queryInfos.getData(i);
    			String serviceId=info.getString("SERVICE_ID");
//    			String productId=getProductIdByPlatSvcId(serviceId,eparchyCode);//TODO lijun17
    			IDataset mountOfferInfos = UpcCall.qryMountOfferByOfferId(serviceId,BofConst.ELEMENT_TYPE_CODE_PLATSVC);
    	    	logger.debug("------getPlatSvc------mountOfferInfos:"+mountOfferInfos);
    			String productId = "";
    			if(IDataUtil.isNotEmpty(mountOfferInfos)){
    				productId = mountOfferInfos.getData(0).getString("PRODUCT_ID","");
    			}
    			if(StringUtils.isNotEmpty(productId)){
    				data.put("BIZ_TYPE", "01");
    				data.put("PRODUCT_CODE", productId);
    			}else{
    				data.put("BIZ_TYPE", "02");
    				data.put("SP_CODE", info.getString("SP_CODE"));
	    			data.put("BIZ_CODE", info.getString("BIZ_CODE"));
    			}    		
    			data.put("VALID_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));	

       		 	ret.add(data);//产品信息PRODUCT_INF
    		}
        	 //进行排序操作
             if(!ret.isEmpty()){
                 DataHelper.sort(ret, "VALID_TIME", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
                
             } 
    	}	    	
    	return ret;
	}
    /**
     * 服务功能类
     * @param userId
     * @return
     * @throws Exception
     */
    public IDataset getNormalSvc(String userId) throws Exception
	{
    	IDataset ret=new DatasetList();	 
    	IDataset queryInfos=UserSvcInfoQry.queryUserAllSvcForAbility(userId);
    	if(IDataUtil.isNotEmpty(queryInfos)){
    		for(int i=0;i<queryInfos.size();i++){
        		IData data=new DataMap();
    			IData info=queryInfos.getData(i);
    			data.put("SERVICE_ID", info.getString("SERVICE_ID"));
    			data.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(info.getString("SERVICE_ID","")));
    			data.put("VALID_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));	 
       		 	ret.add(data);//产品信息PRODUCT_INF   			
    		}	
        	 //进行排序操作
             if(!ret.isEmpty()){
                 DataHelper.sort(ret, "VALID_TIME", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
                
             } 
    	}
    	return ret;
	}
    /**
     * 营销活动类
     * @param userId
     * @return
     * @throws Exception
     */
    public IDataset getActiveSvc(String userId) throws Exception
	{
    	IDataset ret=new DatasetList();	 
    	IDataset queryInfos=UserSaleActiveInfoQry.queryUserSaleActiveByUserId(userId);//正常状态下的
    	if(IDataUtil.isNotEmpty(queryInfos)){
    		IDataset dataset=new DatasetList();	 
    		for(int i=0;i<queryInfos.size();i++){
        		IData data=new DataMap();
    			IData info=queryInfos.getData(i);
    			info.put("ACTION_ID", info.getString("CAMPN_ID"));//营销活动id
    			info.put("ACTION_NAME",info.getString("PRODUCT_NAME") + "-" + info.getString("PACKAGE_NAME"));//营销活动名称
    			info.put("ACTION_DESC", info.getString("PRODUCT_NAME"));//营销活动描述
    			info.put("ACTION_EFFDATE",Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));//营销活动生效时间
    			info.put("ACTION_EXPDATE", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("END_DATE")));//营销活动失效时间
    			dataset.add(info);
    			data.put("ACTION_LIST", dataset);//营销活动列表
    			data.put("VALID_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));//生效时间
       		 	ret.add(data);//产品信息PRODUCT_INF
    		}
        	 //进行排序操作
             if(!ret.isEmpty()){
                 DataHelper.sort(ret, "VALID_TIME", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
                
             } 
    	}
    	 
    	return ret;
	}
    /**字符串类型转换
     * @throws ParseException **/
    public static String ToStr(String time) throws ParseException {
    String str="";
    if(StringUtils.isNotEmpty(time)){
     SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     Date changedate = formatter.parse(time);
     SimpleDateFormat format = new SimpleDateFormat("YYYYMMDDHHMMSS");
    str = format.format(changedate);
    }
     return str;
    } 


//    public static IDataset getDiscntInfo(String discntCode) throws Exception
//    {
//        IData param = new DataMap();
//        param.put("DISCNT_CODE", discntCode);
//        return Dao.qryByCodeParser("TD_B_DISCNT", "SEL_BY_DISCNTCODE_FOR_ABILITY", param, Route.CONN_CRM_CEN);
//    }
    public static String getProductIdByPlatSvcId(String svcId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_ID", svcId);
        param.put("EPARCHY_CODE", eparchyCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.PRODUCT_ID, B.PACKAGE_ID, B.MAIN_TAG, B.ELEMENT_ID, ");
        parser.addSQL("B.ELEMENT_TYPE_CODE, A.FORCE_TAG PACKAGE_FORCE_TAG, ");
        parser.addSQL("A.DEFAULT_TAG PACKAGE_DEFAULT_TAG, B.FORCE_TAG ELEMENT_FORCE_TAG, ");
        parser.addSQL("B.DEFAULT_TAG ELEMENT_DEFAULT_TAG ");
        parser.addSQL("FROM TD_B_PRODUCT_PACKAGE A, TD_B_PACKAGE_ELEMENT B ");
        parser.addSQL("WHERE A.PACKAGE_ID = B.PACKAGE_ID ");
        parser.addSQL("AND B.ELEMENT_ID = :ELEMENT_ID ");
        parser.addSQL("AND (A.EPARCHY_CODE = :EPARCHY_CODE OR A.EPARCHY_CODE = 'ZZZZ') ");
        IDataset productInfos = Dao.qryByParse(parser);
        if (IDataUtil.isEmpty(productInfos))
        {
            return "";
        }
        return productInfos.first().getString("PRODUCT_ID");
    }
    /**
     *  8.6.20  查询订单状态
     * 查询第三方合作伙伴平台发起订单的状态，根据订单号进行查询。
     * @param IData
     * @return
     * @throws Exception
     */
    public IData queryOrderStatus(IData IData) 
	{
    	IData ret =new DataMap();
    	ret.put("BIZ_CODE", "0000");
    	ret.put("BIZ_DESC", "查询订单状态成功！");
    	try{
    	String orderType=IDataUtil.chkParam(IData, "ORDER_TYPE");//订单类型
    	IDataUtil.chkParam(IData, "SC_TID");//订单编码 
    	String restatus="";
    	String status="";
    	if("1".equals(orderType)){//销售订单
    		IDataUtil.chkParam(IData, "SC_OID");//子订单编码
    		restatus=getSaleOrderStatus(IData);
    		restatus=restatus.trim();
    		if("07".equals(restatus)){    			
    	    	ret.put("STATUS", "07");//交易成功	
    		}else if("90".equals(restatus)){
    			ret.put("STATUS", "08");//交易失败
    		}else if("01".equals(restatus)){//后面数据都是造的，如果有再改成实际的
    			ret.put("STATUS", "01");//审核通过
    		}else if("02".equals(restatus)){
    			ret.put("STATUS", "02");//已下发状态
    		}else if("03".equals(restatus)){
    			ret.put("STATUS", "03");//下发失败状态
    		}else if("04".equals(restatus)){
    			ret.put("STATUS", "04");//处理中状态
    		}else if("05".equals(restatus)){
    			ret.put("STATUS", "05");//已发货状态
    		}else if("06".equals(restatus)){
    			ret.put("STATUS", "06");//已签收状态
    		}else if("09".equals(restatus)){
    			ret.put("STATUS", "09");//交易取消状态
    		}else if("10".equals(restatus)){
    			ret.put("STATUS", "90");//异常状态
    		}else{
    			ret.put("STATUS", "00");//待处理
    		}
    	}else{//退订订单
    		restatus=getRefundOrderStatus(IData);
    		restatus=restatus.trim();
    		if("4".equals(restatus)){
    			ret.put("STATUS", "23");//退款成功
    		}else if("5".equals(restatus)){
    			ret.put("STATUS", "21");//退款失败
    		}else if("20".equals(restatus)){
    			ret.put("STATUS", "20");//待审核状态退订订单
    		}else if("22".equals(restatus)){
    			ret.put("STATUS", "22");//审核通过状态退订订单
    		}else if("90".equals(restatus)){
    			ret.put("STATUS", "90");//异常状态退订订单
    		}else if("24".equals(restatus)){
    			ret.put("STATUS", "24");//退货失败退订订单的验证
    		}else if("25".equals(restatus)){
    			ret.put("STATUS", "25");//退款中状态的退订订单的验证
    		}else if("26".equals(restatus)){
    			ret.put("STATUS", "26");//退款成功状态的退订订单的验证
    		}else if("27".equals(restatus)){
    			ret.put("STATUS", "27");//退款失败状态的退订订单的验证
    		}else{
    			ret.put("STATUS", "00");//待处理
    		}
    	}   
    	}catch(Exception e){
    		ret.put("BIZ_CODE", "4A05");
	    	ret.put("BIZ_DESC", "该笔交易不存在！");
	    	ret.put("STATUS", "");
    	}
    	 	
    	return ret;
	}
    public String getSaleOrderStatus(IData IData) throws Exception
	{
       IData param = new DataMap();
       param.put("TID", IData.getString("SC_TID"));
       param.put("OID", IData.getString("SC_OID"));
       IDataset dataset=Dao.qryByCodeParser("TF_B_CTRM_ORDER", "SEL_SALESTATUS_FOR_ABILITY", param, Route.CONN_CRM_CEN);
       if(IDataUtil.isEmpty(dataset)){
    	   CSAppException.apperr(CrmCommException.CRM_COMM_103,"未找到该订单信息!");   
       }
       String status=dataset.getData(0).getString("ORDER_STATUS");
       return status;
	}
    public String getRefundOrderStatus(IData IData) throws Exception
	{
       String status="";
       IData param = new DataMap();
	   param.put("TID", IData.getString("SC_TID"));
	   IDataset dataset=Dao.qryByCodeParser("TF_B_CTRM_ORDER_PRODUCT", "SEL_RESTATUS_FOR_ABILITY", param, Route.CONN_CRM_CEN);
	   if(IDataUtil.isNotEmpty(dataset)){
	   for(int i=0;i<dataset.size();i++){
		    status= dataset.getData(i).getString("STATUS");
		   if("5".equals(status)){
			   return status;				   
		   }
	   }
	   }else{
		   CSAppException.apperr(CrmCommException.CRM_COMM_103,"未找到该订单信息!"); 
	   }
	   return status;
	}
    
    /**
     * 用户实名制查询
     * add by pengyue
     */
    public IData checkUserRealName(IData input) 
    {
        IData resultData = new DataMap();
        resultData.put("BIZ_CODE", "0000");
        resultData.put("BIZ_DESC", "用户实名制查询成功！");
        try{
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String serviceType = IDataUtil.chkParam(input, "SERVICE_TYPE");
        
        //手机号码
        IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isNotEmpty(userInfo)){
            String custId=userInfo.getString("CUST_ID", "");
            IData custInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
            if(IDataUtil.isNotEmpty(custInfo)){
                //实名制标识
                String isRealName=custInfo.getString("IS_REAL_NAME", "");
                if("1".equals(isRealName)){
                    resultData.put("QUERY_RESULT", "0"); 
                }else{
                    resultData.put("QUERY_RESULT", "1");  //有客户资料未实名制
                }
            }else{
                resultData.put("QUERY_RESULT", "");  //无客户资料
                resultData.put("BIZ_CODE", "2031");
                resultData.put("BIZ_DESC", "无客户资料！");
            }
        }
        }catch(Exception e){
        	 resultData.put("BIZ_CODE", "2999");
             resultData.put("BIZ_DESC", "查询失败："+e.getMessage());
             resultData.put("QUERY_RESULT", "");
        }
       
        return resultData;
    }
    /**
     * 6.4.4.1 属性信息同步
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData attrInfoSynchro(IData param) throws Exception {
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "操作成功!");
        try{
		List xmListList = (List) param.get("ATTR_INFO_LIST");
		JSONArray json = JSONArray.fromObject(xmListList);
		DatasetList xmList = DatasetList.fromJSONArray(json);
		param.remove("ATTR_INFO_LIST");
		param.put("ATTR_INFO_LIST", xmList);
        IDataset attrInfoList = param.getDataset("ATTR_INFO_LIST");
        if(IDataUtil.isNotEmpty(attrInfoList)){
            for(int i=0;i<attrInfoList.size();i++){
            	IData attrInfo = attrInfoList.getData(i);
                String attrId = IDataUtil.chkParam(attrInfo, "ATTR_ID");
                IData attrParam = new DataMap();
                attrParam.put("ATTR_ID", attrId);
                if(13!=attrId.length()||!("PP".equals(attrId.substring(0,2)))){
                    result.put("BIZ_CODE", "4009");
                    result.put("BIZ_DESC", "属性编码格式错误");
                    return result;
                }
                attrParam.put("ATTR_NAME", IDataUtil.chkParam(attrInfo, "ATTR_NAME"));
                attrParam.put("CHANGE_TYPE", IDataUtil.chkParam(attrInfo, "CHANGE_TYPE"));
                attrParam.put("ATTR_TYPE", IDataUtil.chkParam(attrInfo, "ATTR_TYPE"));
                attrParam.put("ATTR_DESC", IDataUtil.chkParam(attrInfo, "ATTR_DESC"));
                attrParam.put("ATTR_UNITE", attrInfo.getString("ATTR_UNITE"));
                attrParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
                attrParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                attrParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                dbExecute(attrParam, "TD_B_CTRM_ATTRINFO",new String[]{"ATTR_ID"});
                
                //属性
                IDataset attrValueList = attrInfo.getDataset("ATTR_VALUE_LIST");
                if(IDataUtil.isNotEmpty(attrValueList)){
                    for(int m=0;m<attrValueList.size();m++){
                        IData attrValue = attrValueList.getData(m);
                        IData valueParam = new DataMap();
                        valueParam.put("CHANGE_TYPE", attrInfo.getString("CHANGE_TYPE"));
                        valueParam.put("ATTR_ID", attrInfo.getString("ATTR_ID"));
                        valueParam.put("ATTR_VALUE_ID",  attrValue.getString("ATTR_VALUE_ID"));
                        valueParam.put("ATTR_VALUE_NAME", attrValue.getString("ATTR_VALUE_NAME"));
                        valueParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
                        valueParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                        valueParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                        dbExecute(valueParam, "TD_B_CTRM_ATTRVAL",new String[]{"ATTR_VALUE_ID"});
                    }                   
                }               
            }           
        }else{
            result.put("BIZ_CODE", "4000");
            result.put("BIZ_DESC", "属性信息不能为空");
        }
        }catch(Exception ex){
        	result.put("BIZ_CODE", "2999");
            result.put("BIZ_DESC", "属性信息同步失败："+ex.getMessage());
        }
        return result;
    }
    
    /**
     * 6.4.4.1 产品信息同步
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData commonProductInfoSynchro(IData param) throws Exception {
        IData result = new DataMap();
        String oprNo = IDataUtil.chkParam(param, "OPR_NO");
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "操作成功!");  
        try{
		List xmListList = (List) param.get("PRODUCT_INFO_LIST");
		JSONArray json = JSONArray.fromObject(xmListList);
		DatasetList xmList = DatasetList.fromJSONArray(json);
		param.remove("PRODUCT_INFO_LIST");
		param.put("PRODUCT_INFO_LIST", xmList);
        IDataset productInfoList = param.getDataset("PRODUCT_INFO_LIST");
        if(IDataUtil.isNotEmpty(productInfoList)){
            for(int i=0;i<productInfoList.size();i++){
                IData productInfo = productInfoList.getData(i);
                IData productParam = new DataMap();
                String productId = IDataUtil.chkParam(productInfo, "PRODUCT_ID");
                productParam.put("OPR_NO", oprNo);
                productParam.put("PRODUCT_ID", productId);
                productParam.put("PRODUCT_NAME", IDataUtil.chkParam(productInfo, "PRODUCT_NAME"));
                productParam.put("PRODUCT_ORGANIZE", IDataUtil.chkParam(productInfo, "PRODUCT_ORGANIZE"));
                String startDate="";
                String endDate="";
                if(StringUtils.isNotBlank(productInfo.getString("START_DATE"))){
                	startDate = changeTimePattern(productInfo.getString("START_DATE"));	
                	  productParam.put("START_DATE", startDate);
                }
                if(StringUtils.isNotBlank(productInfo.getString("END_DATE"))){
                	endDate = changeTimePattern(productInfo.getString("END_DATE"));	
                	 productParam.put("END_DATE", endDate);
                }  
                productParam.put("START_DATE", startDate);
                productParam.put("END_DATE", endDate);
                String createTime = changeTimePattern(IDataUtil.chkParam(productInfo, "CREATE_TIME"));
                String changeTime = changeTimePattern(IDataUtil.chkParam(productInfo, "CHANGE_TIME"));
                productParam.put("PRODUCT_DESC", IDataUtil.chkParam(productInfo, "PRODUCT_DESC"));
                productParam.put("PRODUCT_PRICE", productInfo.getInt("PRODUCT_PRICE"));
                productParam.put("PRODUCT_TYPE", IDataUtil.chkParam(productInfo, "PRODUCT_TYPE"));
                productParam.put("CHANGE_TYPE", IDataUtil.chkParam(productInfo, "CHANGE_TYPE"));
                productParam.put("CREATE_TIME", createTime);
                productParam.put("CHANGE_TIME", changeTime);
                productParam.put("PRODUCT_DEMO", productInfo.getString("PRODUCT_DEMO"));
                productParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
                productParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                productParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                dbExecute(productParam, "TD_B_CTRM_COMMON_PRODUCT",new String[]{"PRODUCT_ID"});
                
                //属性
                IDataset productAttrList = productInfo.getDataset("PRODUCT_ATTR_LIST");
                if(IDataUtil.isNotEmpty(productAttrList)){
                    for(int m=0;m<productAttrList.size();m++){
                        IData productAttr = productAttrList.getData(m);
                        IData attrParam = new DataMap();
                        attrParam.put("CHANGE_TYPE", productInfo.getString("CHANGE_TYPE"));
                        attrParam.put("PRODUCT_ID", productId);
                        attrParam.put("PRODUCT_ATTR_ID", IDataUtil.chkParam(productAttr, "PRODUCT_ATTR_ID"));
                        attrParam.put("PRODUCT_ATTR_VALUE", productAttr.getString("PRODUCT_ATTR_VALUE"));
                        attrParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
                        attrParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                        attrParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                        dbExecute(attrParam, "TD_B_CTRM_COMMON_ATTR",new String[]{"PRODUCT_ATTR_ID"});
                    }                   
                }               
            }           
        }else{
            result.put("BIZ_CODE", "4000");
            result.put("BIZ_DESC", "产品信息不能为空");
        }
        }catch(Exception ex){
           result.put("BIZ_CODE", "2999");
           result.put("BIZ_DESC", "产品信息同步失败："+ex.getMessage());	
        }
        return result;
    }
    /**
	/**
 * 数据库操作
 * 
 * @param param
 * @param tableName
 * @return
 * @throws Exception
 */
public boolean dbExecute(IData param, String tableName, String[] main) throws Exception {
    boolean flag = false;
    if ("1".equals(param.getString("CHANGE_TYPE"))) {
        flag = Dao.insert(tableName, param, Route.CONN_CRM_CEN);
    } else if ("2".equals(param.getString("CHANGE_TYPE"))) {
        flag = Dao.save(tableName, param, main, Route.CONN_CRM_CEN);
    } else if ("3".equals(param.getString("CHANGE_TYPE"))) {
        flag = Dao.delete(tableName, param, main, Route.CONN_CRM_CEN);
    }
    return flag;
}
public static String changeTimePattern(String strDate)throws Exception{
	DateFormat sdf1 = new SimpleDateFormat("yyyyMMddhhmmss");  
	Date date = (Date) sdf1.parse(strDate);  
	DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	return sdf2.format(date);
}
/**
 * 终端出库/换货
 * 
 * @param pd
 * @param data
 * @return
 * @throws Exception
 */
public IData orderStatusSynchro(IData data)  {
	IData ret=new DataMap();
	ret.put("BIZ_CODE","0000");
	ret.put("BIZ_DESC","同步成功！");
	try{
	IDataUtil.chkParam(data, "TID");
	IDataUtil.chkParam(data, "OID");	
	String updateTime=IDataUtil.chkParam(data, "UPDATE_TIME");
	if(14!=updateTime.length()){
	ret.put("BIZ_CODE","4009");
	ret.put("BIZ_DESC","状态变更时间格式不对，应该为YYYYMMDDHHMMSS格式！");	
	}
	IDataUtil.chkParam(data, "OPT_TYPE");
	IDataUtil.chkParam(data, "IMEI");	
	String oldIMEI = "";	
	IData param = new DataMap();
	// 根据TID和OID查询对应的子订单同步信息,主要的用途是用来获取用户的手机号码
	param.put("TID", data.getString("TID"));
	param.put("OID", data.getString("OID"));
	IDataset orderInfo = QueryListInfo.queryListInfoForOrderTlist(param);	
	if (orderInfo.size() <= 0) {
		String errors = "订单ID【" + data.getString("TID") + "】,子订单ID【"+ data.getString("OID") + "】不存在同步订单记录！";
		ret.put("BIZ_CODE","2999");
		ret.put("BIZ_DESC",errors);
		return ret;
		//CSAppException.appError("", errors);
	}

	// 订单是否需要省公司配送
	data.put("SERIAL_NUMBER", orderInfo.getData(0).getString("PHONE"));
	// 根据TID和OID查询对应的子订单产品同步信息
	param.put("CTRM_PRODUCT_TYPE", "2");
	IDataset orderProductInfos = QueryListInfo.queryListInfoForProorderTlist(param);
	if(IDataUtil.isNotEmpty(orderProductInfos)){	
	IData orderProductInfo = new DataMap();
	for (int i = 0; i < orderProductInfos.size(); i++) {
		orderProductInfo = orderProductInfos.getData(i);
		// 处理合约 调用更换IMEI接口 OPT_TYPE:1-绑定IMEI；2-更换IMEI
		if ("2".equals(data.getString("OPT_TYPE", ""))&& !"".equals(data.getString("IMEI", ""))&& "1".equals(orderInfo.getData(0).getString("DISTRIBUTION"))) {
			// 20140906 更换终端 未完成 参数有问题
			// executeExchangeTrade(data);
			String newImei = data.getString("IMEI","");			
	       CSAppCall.call("SS.ModifySaleActiveIMEIRegSVC.tradeReg", data);
		}
	}
	}
	param.clear();
	param.put("ORDER_STATUS", data.getString("STATUS"));
	param.put("UPDATE_TIME", data.getString("UPDATE_TIME"));
	param.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
	param.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
	param.put("OID", data.getString("OID"));
	param.put("TID", data.getString("TID"));
	param.put("RSRV_STR1", orderInfo.getData(0).getString("RSRV_STR1"));
	param.put("RSRV_STR2", orderInfo.getData(0).getString("RSRV_STR2"));	
	oldIMEI = orderInfo.getData(0).getString("RSRV_STR1");
	//绑定IMEI 插RSRV_STR1保存最新的IMEI号  RSRV_STR2保存旧的IMEI号
	if ("1".equals(data.getString("OPT_TYPE",""))&& !"".equals(data.getString("IMEI",""))) {
		param.put("RSRV_STR1", data.getString("IMEI"));
		data.put("PARAM_CODE", "ADD_IMEI");
	}else if("2".equals(data.getString("OPT_TYPE",""))&& !"".equals(data.getString("IMEI",""))) {
		param.put("RSRV_STR1", data.getString("IMEI"));
		param.put("RSRV_STR2", orderInfo.getData(0).getString("RSRV_STR1"));
		data.put("PARAM_CODE", "MOD_IMEI");
	}	
	updateInfoById(param);
	
	//把同步过来的IMEI保存到TF_F_USER_IMEI表中
	if (!"".equals(data.getString("IMEI",""))) {
		param.clear();
		IData userinfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
		//把用户旧的IMEI终止
		param.put("USER_ID", userinfo.getString("USER_ID"));
		param.put("END_DATE", SysDateMgr.getSysTime());
		QueryInfoUtil.updateUserIMEI(param, userinfo.getString("EPARCHY_CODE"));
		
		//插入新的IMEI
		param.clear();
		param.put("USER_ID", userinfo.getString("USER_ID"));
		param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		param.put("IMEI", data.getString("IMEI"));
		param.put("START_DATE", SysDateMgr.getSysTime());
		param.put("END_DATE", SysDateMgr.getTheLastTime());
		QueryInfoUtil.insertUserIMEI(param, userinfo.getString("EPARCHY_CODE"));
		
		data.put("%101!", data.getString("CHANNEL_ID",""));
		data.put("%108!", data.getString("IMEI"));
		data.put("%109!", oldIMEI);
		data.put("EPARCHY_CODE", userinfo.getString("EPARCHY_CODE"));
		QueryInfoUtil.sendSMS(data);
	}
	}catch(Exception ex){
		ret.put("BIZ_CODE","2999");
		ret.put("BIZ_DESC","同步失败："+ex.getMessage());
	}
	return ret;
}
/**
 * 更新订单表中的状态
 * @param updateOrder
 * @throws Exception
 */
public void updateInfoById(IData param) throws Exception{
	Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST", "UPD_CTRM_ORDER_BYID", param,Route.CONN_CRM_CEN);
	
}


/**
 * 实名制校验
 * @param Idata
 * @return
 * @throws Exception
 */
public IData checkIsRealName(IData input) throws Exception 
{

   IData resultData = new DataMap();
   resultData.put("BIZ_CODE", "0000");
   resultData.put("BIZ_DESC", "查询成功");
   resultData.put("RESULTE_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
   try{
   String custName=IDataUtil.chkParam(input, "CUSTOMER_NAME");
   String psptType=IDataUtil.chkParam(input, "ID_CARD_TYPE");
   String psptId=IDataUtil.chkParam(input, "ID_CARD_NUM");
   //String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
   //1、用户的姓名和身份信息是否真实准确；（调在线公司接口验证）
   
   //2、针对传业务号码的请求，需要校验该业务号码与用户的姓名和身份信息是否匹配
   String serviceType=input.getString("SERVICE_TYPE","");
   String serialNumber=input.getString("SERVICE_NO","");
   if(StringUtils.isEmpty(serialNumber)){
		IDataset custInfoList = getCustInfoByPspt(custName,psptType, psptId);
		if(IDataUtil.isEmpty(custInfoList)){
			resultData.put("BIZ_CODE", "2043");
			resultData.put("BIZ_DESC", "业务号码与客户姓名、证件号码信息不匹配！"); 
			return resultData;
		}
	  
  }
   if(StringUtils.isNotBlank(serialNumber)){//手机号
	   	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	   	if(IDataUtil.isEmpty(userInfo)){
	   		resultData.put("BIZ_CODE", "2043");
	        resultData.put("BIZ_DESC", "用户信息不存在！");
	        return resultData;
	   	}
       	 String custId=userInfo.getString("CUST_ID", "");
       	 IData  custInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
       	 if(IDataUtil.isNotEmpty(custInfo)){
       		 //实名制标识
       		 String isRealName=custInfo.getString("IS_REAL_NAME", "");
       		 if("".equals(isRealName)||"0".equals(isRealName)){
       			 //非实名制
       			resultData.put("BIZ_CODE","2031");
       			resultData.put("BIZ_DESC","未实名登记");
       			return resultData;
       		 }
       	 }
        	
	   	IDataset custList = getCustInfoByPspt(custId,custName,psptType, psptId);
		   	if(IDataUtil.isEmpty(custList)){
		   		resultData.put("BIZ_CODE", "2043");
		        resultData.put("BIZ_DESC", "业务号码与客户姓名、证件号码信息不匹配！"); 
		        return resultData;
		   	}
	   }        
   }catch(Exception e){
   resultData.put("BIZ_CODE", "2043");
   resultData.put("BIZ_DESC", "查询失败："+e.getMessage());
   }
  
   return resultData;
}
/**
* @Description: 获得客户资料
* @param psptTypeCode
* @param psptId
* @return
* @throws Exception
* @author: lixs5
* @date: 2015年5月29日 下午3:12:23
*/
public IDataset getCustInfoByPspt(String custId,String custName,String psptTypeCode, String psptId) throws Exception
{
   IDataset custList = new DatasetList();// 先给初始值，便于后面判断
   IDataset codes = getRealyPsptTypeCodeByPnet(psptTypeCode);
   if (IDataUtil.isNotEmpty(codes))
   {
       StringBuilder sb = new StringBuilder(20);
       for (int j = 0; j < codes.size(); j++)
       {
           if (j == codes.size() - 1)
           {
               sb.append("'").append(codes.getData(j).getString("PSPT_TYPE_CODE", "")).append("'");
           }
           else
           {
               sb.append("'").append(codes.getData(j).getString("PSPT_TYPE_CODE", "")).append("',");
           }
       }
       psptTypeCode = sb.toString(); // 身份证的模糊查询

   }
   else
   {
       psptTypeCode = codes.getData(0).getString("PSPT_TYPE_CODE");
   }
   IDataset list=getPerInfoByPsptId(custId,custName,psptTypeCode, psptId);
   return list;
}
public IDataset getCustInfoByPspt(String custName,String psptTypeCode, String psptId) throws Exception
{
   IDataset custList = new DatasetList();// 先给初始值，便于后面判断
   IDataset codes = getRealyPsptTypeCodeByPnet(psptTypeCode);
   if (IDataUtil.isNotEmpty(codes))
   {
       StringBuilder sb = new StringBuilder(20);
       for (int j = 0; j < codes.size(); j++)
       {
           if (j == codes.size() - 1)
           {
               sb.append("'").append(codes.getData(j).getString("PSPT_TYPE_CODE", "")).append("'");
           }
           else
           {
               sb.append("'").append(codes.getData(j).getString("PSPT_TYPE_CODE", "")).append("',");
           }
       }
       psptTypeCode = sb.toString(); // 身份证的模糊查询

   }
   else
   {
       psptTypeCode = codes.getData(0).getString("PSPT_TYPE_CODE");
   }
   IDataset list=getPerInfoByPsptId(custName,psptTypeCode, psptId);
   return list;
}
public static IDataset getPerInfoByPsptId(String custName,String psptTypeCode, String psptId) throws Exception
{
   IData params = new DataMap();
   params.put("VPSPT_ID", psptId);
   params.put("VPSPT_TYPE_CODE", psptTypeCode);
   params.put("CUST_NAME", custName);
   //加上15位的判断
   if(StringUtils.isNotEmpty(psptId)&&(18==psptId.length())){
	   params.put("VPSPT_ID_15", PasswdAssistant.standPsptId("0", psptId));
   }
   SQLParser parser = new SQLParser(params);
   parser.addSQL("SELECT  /*+index(a, IDX_TF_F_CUST_PERSON_PSPTID)*/to_char(cust_id) cust_id, pspt_type_code, pspt_id, to_char(pspt_end_date, 'yyyy-mm-dd hh24:mi:ss') pspt_end_date,");
   parser.addSQL("pspt_addr, cust_name, sex, to_char(birthday, 'yyyy-mm-dd hh24:mi:ss') birthday, nationality_code,");
   parser.addSQL("local_native_code, population, language_code, folk_code, phone, post_code, fax_nbr, email, contact, contact_phone,");
   parser.addSQL("home_address, work_name, work_depart, job, job_type_code, educate_degree_code, religion_code, revenue_level_code,");
   parser.addSQL("marriage, character_type_code, webuser_id, web_passwd, contact_type_code, community_id FROM  tf_f_cust_person a ");
   String str = params.getString("VPSPT_TYPE_CODE");
   if (str.split(",").length > 1)
   {
       parser.addSQL(" WHERE pspt_type_code  in (" + str + ") ");
   }
   else
   {
       parser.addSQL(" WHERE pspt_type_code = :VPSPT_TYPE_CODE");
   }
   parser.addSQL(" AND ( pspt_id = :VPSPT_ID");
   
   //加上15位的判断
   if(StringUtils.isNotEmpty(psptId)&&(18==psptId.length())){
	   parser.addSQL(" or  pspt_id = :VPSPT_ID_15");
   }
   parser.addSQL(" ) ");
   
   parser.addSQL(" AND cust_name = :CUST_NAME");
   return Dao.qryByParse(parser);
}
public static IDataset getPerInfoByPsptId(String custId,String custName,String psptTypeCode, String psptId) throws Exception
{
   IData params = new DataMap();
   params.put("VPSPT_ID", psptId);
   params.put("VPSPT_TYPE_CODE", psptTypeCode);
   params.put("CUST_ID", custId);
   params.put("CUST_NAME", custName);
   //加上15位的判断
   if(StringUtils.isNotEmpty(psptId)&&(18==psptId.length())){
	   params.put("VPSPT_ID_15", PasswdAssistant.standPsptId("0", psptId));
   }
   SQLParser parser = new SQLParser(params);
   parser.addSQL("SELECT  /*+index(a, IDX_TF_F_CUST_PERSON_PSPTID)*/to_char(cust_id) cust_id, pspt_type_code, pspt_id, to_char(pspt_end_date, 'yyyy-mm-dd hh24:mi:ss') pspt_end_date,");
   parser.addSQL("pspt_addr, cust_name, sex, to_char(birthday, 'yyyy-mm-dd hh24:mi:ss') birthday, nationality_code,");
   parser.addSQL("local_native_code, population, language_code, folk_code, phone, post_code, fax_nbr, email, contact, contact_phone,");
   parser.addSQL("home_address, work_name, work_depart, job, job_type_code, educate_degree_code, religion_code, revenue_level_code,");
   parser.addSQL("marriage, character_type_code, webuser_id, web_passwd, contact_type_code, community_id FROM  tf_f_cust_person a ");
   String str = params.getString("VPSPT_TYPE_CODE");
   if (str.split(",").length > 1)
   {
       parser.addSQL(" WHERE pspt_type_code  in (" + str + ") ");
   }
   else
   {
       parser.addSQL(" WHERE pspt_type_code = :VPSPT_TYPE_CODE");
   }
   parser.addSQL(" AND ( pspt_id = :VPSPT_ID");
   
   //加上15位的判断
   if(StringUtils.isNotEmpty(psptId)&&(18==psptId.length())){
	   parser.addSQL(" or  pspt_id = :VPSPT_ID_15");
   }
   parser.addSQL(" ) ");
   
   parser.addSQL(" AND cust_id = :CUST_ID");
   parser.addSQL(" AND cust_name = :CUST_NAME");
   return Dao.qryByParse(parser);
}
/**
 * 渠道信息同步接口
 * @param Idata
 * @return
 * @throws Exception
 */
public IData channelSynchro(IData Idata) throws Exception 
{
	IData returnData = new DataMap();
	List xmListList = (List) Idata.get("CHANNEL_DATA");
	JSONArray json = JSONArray.fromObject(xmListList);
	DatasetList xmList = DatasetList.fromJSONArray(json);
	Idata.remove("CHANNEL_DATA");
	Idata.put("CHANNEL_DATA", xmList);
	
	IDataset channelInfo = Idata.getDataset("CHANNEL_DATA");
	String oprType = Idata.getString("OPR_TYPE");
	String oprTime = Idata.getString("OPR_TIME");
	
	if(IDataUtil.isNotEmpty(channelInfo))
	{	
		try{
			for(int k = 0; k < channelInfo.size(); k++)
	    		{	
					IData channelData = new DataMap();
					String channelCode = channelInfo.getData(k).getString("CHANNEL_ID","");
					
					channelData.put("CHANNEL_TYPE",  channelInfo.getData(k).getString("CHANNEL_TYPE",""));				
					channelData.put("CHANNEL_CODE", channelCode);
					channelData.put("CHANNEL_SMS_NAME", channelInfo.getData(k).getString("CHANNEL_SMS_NAME",""));  								
					channelData.put("CHANNEL_NAME", channelInfo.getData(k).getString("CHANNEL_NAME",""));
					channelData.put("RSRV_STR1", channelInfo.getData(k).getString("RSRV_STR1",""));
					channelData.put("RSRV_STR2", channelInfo.getData(k).getString("RSRV_STR2",""));
					channelData.put("RSRV_STR3", channelInfo.getData(k).getString("RSRV_STR3",""));
					channelData.put("RSRV_STR4", channelInfo.getData(k).getString("RSRV_STR4",""));
					channelData.put("RSRV_STR5", channelInfo.getData(k).getString("RSRV_STR5",""));
					channelData.put("OPR_TYPE", oprType);
					if(oprType.equals("1")){
						IDataset map = qryChannelInfoByCode(channelCode, Route.CONN_CRM_CEN);
						if(IDataUtil.isNotEmpty(map)){
							IDataset shopDataList = channelInfo.getData(k).getDataset("SHOP_DATA");
							if(IDataUtil.isNotEmpty(shopDataList)){
								for(int i=0; i<shopDataList.size();i++){
									IData shopData = new DataMap();
									String shopCode = shopDataList.getData(i).getString("SHOP_CODE","");
									shopData.put("CHANNEL_CODE",channelCode);
									shopData.put("SHOP_CODE", shopCode);
									shopData.put("SHOP_TYPE", shopDataList.getData(i).getString("SHOP_TYPE",""));
									shopData.put("SHOP_NAME", shopDataList.getData(i).getString("SHOP_NAME",""));
									shopData.put("RSRV_STR1", shopDataList.getData(i).getString("RSRV_STR1",""));
									shopData.put("RSRV_STR2", shopDataList.getData(i).getString("RSRV_STR2",""));
									shopData.put("RSRV_STR3", shopDataList.getData(i).getString("RSRV_STR3",""));
									shopData.put("RSRV_STR4", shopDataList.getData(i).getString("RSRV_STR4",""));
									shopData.put("RSRV_STR5", shopDataList.getData(i).getString("RSRV_STR5",""));
									shopData.put("OPR_TYPE",oprType);
									
									IDataset shopList = qryShopInfoByCode(channelCode,shopCode, Route.CONN_CRM_CEN);
									if(IDataUtil.isNotEmpty(shopList)){
										returnData.put("BIZ_CODE", "9999");
										returnData.put("BIZ_DESC", "渠道信息同步失败：店铺编码"+shopCode+"已存在，请勿重复新增！");
										return returnData;
									}
									shopData.put("CREATE_TIME",oprTime);
									shopData.put("UPDATE_TIME",oprTime);	
								    shopInfoSynchro(shopData);
									
								}
							}
						continue;
						}else{
						channelData.put("CREATE_TIME", oprTime);
						channelData.put("UPDATE_TIME",oprTime);
						channelInfoSynchro(channelData);
						}
					}
					if(oprType.equals("2")){
						channelData.put("UPDATE_TIME",oprTime);
						channelInfoSynchro(channelData);
					}
					
					IDataset shopDataList = channelInfo.getData(k).getDataset("SHOP_DATA");
					if(IDataUtil.isNotEmpty(shopDataList)){
						for(int i=0; i<shopDataList.size();i++){
							IData shopData = new DataMap();
							String shopCode = shopDataList.getData(i).getString("SHOP_CODE","");
							shopData.put("CHANNEL_CODE",channelCode);
							shopData.put("SHOP_CODE", shopCode);
							shopData.put("SHOP_TYPE", shopDataList.getData(i).getString("SHOP_TYPE",""));
							shopData.put("SHOP_NAME", shopDataList.getData(i).getString("SHOP_NAME",""));
							shopData.put("RSRV_STR1", shopDataList.getData(i).getString("RSRV_STR1",""));
							shopData.put("RSRV_STR2", shopDataList.getData(i).getString("RSRV_STR2",""));
							shopData.put("RSRV_STR3", shopDataList.getData(i).getString("RSRV_STR3",""));
							shopData.put("RSRV_STR4", shopDataList.getData(i).getString("RSRV_STR4",""));
							shopData.put("RSRV_STR5", shopDataList.getData(i).getString("RSRV_STR5",""));
							shopData.put("OPR_TYPE",oprType);
							if(oprType.equals("1")){
								IDataset shopList = qryShopInfoByCode(channelCode,shopCode, Route.CONN_CRM_CEN);
								if(IDataUtil.isNotEmpty(shopList)){
									returnData.put("BIZ_CODE", "9999");
									returnData.put("BIZ_DESC", "渠道信息同步失败：店铺编码"+shopCode+"已存在，请勿重复新增！");
									return returnData;
								}
								shopData.put("CREATE_TIME",oprTime);
								shopData.put("UPDATE_TIME",oprTime);
							}
							if(oprType.equals("2")){
							
								shopData.put("UPDATE_TIME",oprTime);
							}
							shopInfoSynchro(shopData);
							
						}
					}
			}
		}catch(Exception e){
			returnData.put("BIZ_CODE", "9999");
			returnData.put("BIZ_DESC", "渠道信息同步失败："+e.getMessage());
			return returnData;
		}
	}
	else 
	{
		returnData.put("BIZ_CODE", "9999");
    	returnData.put("BIZ_DESC", "无渠道信息,渠道信息同步失败!");
		
		return returnData;
	}
	
	returnData.put("BIZ_CODE", "0000");
	returnData.put("BIZ_DESC", "渠道信息同步成功");
	
	return returnData;
}
public static IDataset qryChannelInfoByCode(String channelCode,String routeId) throws Exception
{
    IData params = new DataMap();
    params.put("CHANNEL_CODE", channelCode);
    SQLParser parser = new SQLParser(params);
    
    parser.addSQL("select *  from  TI_CTRM_CHANNEL_INFO where  CHANNEL_CODE = :CHANNEL_CODE ");  
    return Dao.qryByParse(parser,routeId);
}
public static IDataset qryShopInfoByCode(String channelCode,String shopCode,String routeId) throws Exception
{
    IData params = new DataMap();
    params.put("SHOP_CODE", shopCode);
    params.put("CHANNEL_CODE", channelCode);
    SQLParser parser = new SQLParser(params);
    
    parser.addSQL("select *  from  TI_CTRM_SHOP_INFO where  SHOP_CODE = :SHOP_CODE and CHANNEL_CODE = :CHANNEL_CODE ");  
    return Dao.qryByParse(parser,routeId);
}
/**
 * 渠道信息同步
 * @param input
 * @throws Exception
 */
public static void channelInfoSynchro(IData input) throws Exception{
	//OPR_TYPE 操作类型:1-新增；2-修改；
	if("1".equals(input.getString("OPR_TYPE"))){
		Dao.insert("TI_CTRM_CHANNEL_INFO", input, Route.CONN_CRM_CEN);
	}else if("2".equals(input.getString("OPR_TYPE"))){
		
		Dao.executeUpdateByCodeCode("TI_CTRM_CHANNEL_INFO", "UPD_CHANNEL_BY_CHANNEL_CODE", input, Route.CONN_CRM_CEN);
	}
}
/**
 * 店铺信息同步
 * @param input
 * @throws Exception
 */
public static void shopInfoSynchro(IData input) throws Exception{
	//OPR_TYPE 操作类型:1-新增；2-修改；
	if("1".equals(input.getString("OPR_TYPE"))){
		Dao.insert("TI_CTRM_SHOP_INFO", input, Route.CONN_CRM_CEN);
	}else if("2".equals(input.getString("OPR_TYPE"))){
		Dao.executeUpdateByCodeCode("TI_CTRM_SHOP_INFO", "UPD_SHOP_BY_SHOP_CODE", input, Route.CONN_CRM_CEN);
	}
}
	/**特定产品订购关系查询
	 * 查询用户是否存在指定产品的订购关系及生效时间，失效时间。如同一商品存在多条订购关系，
	 * 按订购时间顺序返回多条。如不存在订购关系，也需返回一条记录。
	 * 如果查询时入参为一级能开的商品编码时，不限订购渠道，或是省侧与此商品映射的本省商品，
	 * 如存在订购关系，均认为是存在订购关系，需返回订购记录。
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData querySpecGoodsOrder(IData Idata) throws Exception {
		// 入参校验
		String serialNumber = IDataUtil.chkParam(Idata, "SERIAL_NUMBER");
		String goodsType = IDataUtil.chkParam(Idata, "GOODS_TYPE");//编码类型,1：省编码2：一级能开编码
	
		String goodsIdList = IDataUtil.chkParam(Idata, "GOODS_ID_LIST");//当编码类型是1时，填写省公司编码。当编码类型是2时，填写一级能开编码。
																		//多个goodsId用英文分号分隔，如goodsId1;goodsId2;goodsId3
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		IData data = new DataMap();
		data.put("BIZ_CODE", "0000");
		data.put("BIZ_DESC", "特定产品订购关系查询成功");
		data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		if(IDataUtil.isEmpty(userInfo)){
			data.put("BIZ_CODE", "2043");
			data.put("BIZ_DESC", "用户信息不存在！");
	     return data;
		}
		IDataset AllbizInfoList = new DatasetList();
		String userId = userInfo.getString("USER_ID");
		String acctTag =userInfo.getString("ACCT_TAG");
		String goodsIdStrs[] = goodsIdList.split(";");
		if("2".equals(goodsType)){
			for(int i=0;i<goodsIdStrs.length;i++){
				 IDataset crmProducts=getCrmProductsInfo(goodsIdStrs[i]);//根据全网编码查出省内编码
	                if(IDataUtil.isEmpty(crmProducts)){
	                    CSAppException.apperr(CrmCommException.CRM_COMM_103,"商品或产品映射关系不存在！");
	                }
	                if(crmProducts.size()>1){
	                	for(int j=0;j<crmProducts.size();j++){
		                	IData ctmProduct = crmProducts.getData(j);
		                	String elementTypeCode = ctmProduct.getString("ELEMENT_TYPE_CODE"); 
		                	String ctrmProductType = ctmProduct.getString("CTRM_PRODUCT_TYPE"); 
		                	String orderId ="";
		                	if("P".equals(elementTypeCode)){
		                		if("2".equals(ctrmProductType)){
		                			orderId = ctmProduct.getString("PACKAGE_ID");
		                		}else{
		                			orderId = ctmProduct.getString("PRODUCT_ID");
		                		}
		                		IDataset bizInfoList =getBizInfoList2(goodsIdStrs[i],elementTypeCode, userId,orderId,ctrmProductType);
			                	AllbizInfoList.addAll(bizInfoList);
		                	}
		                	
		                }
	                }else{
	                	for(int j=0;j<crmProducts.size();j++){
		                	IData ctmProduct = crmProducts.getData(j);
		                	String elementTypeCode = ctmProduct.getString("ELEMENT_TYPE_CODE"); 
		                	String ctrmProductType = ctmProduct.getString("CTRM_PRODUCT_TYPE"); 
		                	String orderId ="";
		                	if("P".equals(elementTypeCode)){
		                		if("2".equals(ctrmProductType)){
		                			orderId = ctmProduct.getString("PACKAGE_ID");
		                		}else{
		                			orderId = ctmProduct.getString("PRODUCT_ID");
		                		}
		                	}else if("S".equals(elementTypeCode) ||"D".equals(elementTypeCode)){
		                		orderId = ctmProduct.getString("ELEMENT_ID");
		                	}
		                	IDataset bizInfoList =getBizInfoList2(goodsIdStrs[i],elementTypeCode, userId,orderId,ctrmProductType);
		                	AllbizInfoList.addAll(bizInfoList);
		                }
	                }
	                
			}
		}else{
			for(int i=0;i<goodsIdStrs.length;i++){		
				if(!isNumeric(goodsIdStrs[i])){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "商品编码格式错误,请输入数字！");   
					}
				IData offerInfoP=new DataMap();
				IData offerInfoD=new DataMap();
				IData offerInfoS=new DataMap();
				try {//因调产商品接口查不到数据时报错，所以此处try catch一把
					offerInfoP =UpcCall.queryOfferByOfferId("P",goodsIdStrs[i]);
				} catch (Exception e) {
					offerInfoP=new DataMap();
				}
				try {
					offerInfoD =UpcCall.queryOfferByOfferId("D",goodsIdStrs[i]);
				} catch (Exception e) {
					offerInfoD=new DataMap();
				}
				try {
					offerInfoS =UpcCall.queryOfferByOfferId("S",goodsIdStrs[i]);
				} catch (Exception e) {
					offerInfoS=new DataMap();
				}
//				IData offerInfoP =UpcCall.queryOfferByOfferId(goodsIdStrs[i],"P");
//				IData offerInfoD =UpcCall.queryOfferByOfferId(goodsIdStrs[i],"D");
//				IData offerInfoS =UpcCall.queryOfferByOfferId(goodsIdStrs[i],"S");
				if (IDataUtil.isEmpty(offerInfoP) && IDataUtil.isEmpty(offerInfoD) && IDataUtil.isEmpty(offerInfoS)) {
					data.put("BIZ_CODE", "4024");
					data.put("BIZ_DESC", "产品编号非法！");
					return data;
				}
				IDataset offerList = new DatasetList();
				if (IDataUtil.isNotEmpty(offerInfoP)) {
					offerList.add(offerInfoP);									
				}
				if (IDataUtil.isNotEmpty(offerInfoD)) {
					offerList.add(offerInfoD);				
				}
				if (IDataUtil.isNotEmpty(offerInfoS)) {
					offerList.add(offerInfoS);					
				}
				for (int j = 0; j < offerList.size(); j++) {
					String offerType = offerList.getData(j).getString("OFFER_TYPE");
					IDataset bizInfoList =getBizInfoList(goodsIdStrs[i],offerType, userId,goodsIdStrs[i]);
					AllbizInfoList.addAll(bizInfoList);
				}				
			}			
		}
		
		

		// 由于花卡套卡省产品编码和套餐省产品编码不同，并且集团要求
		// b)   实现号卡和套餐统一通过套餐编码查询及反馈。即花卡青春版号卡产品（一级能开编码9990289000001600001）通过花卡青春版基础套餐编码9990200200000410001作为入参中的商品编码进行查询及反馈。
		// 如果传的套餐的产品编码，且订购关系为1，则需要再查询套卡的订购关系
		// PARAM_CODE 套卡能开编码，PARA_CODE1 套餐能开编码
		IDataset productConfig = CommparaInfoQry.getCommByParaAttr("CSM", "6041", "ZZZZ");
		boolean confgTag = false; // 输入的goodsId是否为花卡套卡（或者套餐）一级能开（或者省侧）产品编码
		if (IDataUtil.isNotEmpty(productConfig)) {
			// PARAM_CODE 套卡能开编码，PARA_CODE1 套餐能开编码
			for (int i = 0; i < productConfig.size(); i++) {
				String paramCode = productConfig.getData(i).getString("PARAM_CODE");
				String paraCode1 = productConfig.getData(i).getString("PARA_CODE1");
				for (int j = 0; j < goodsIdStrs.length; j++) {
					if (StringUtils.equals(goodsIdStrs[j], paraCode1) ) {
						confgTag = true;
						break;
					}
				}

				if (confgTag) {
					//      套餐名称            一级能开产品ID     产品编码
					// 移动花卡-宝藏版（号卡） 9990289000001600001 99914621
					// 移动花卡-宝藏版（套餐） 9990200200000410001 99914622
					IDataset crmProducts=getCrmProductsInfo(paramCode);//根据全网编码查出省内编码
					String proId = crmProducts.first().getString("PRODUCT_ID");
					IDataset HktkProdInfoList = UserProductInfoQry.getUserProductByUserIdProductId(userId, proId); // 查询用户是否订购 移动花卡-宝藏版（号卡）
					/**
					 * 针对套卡用户特殊处理：
					 * 入参一个，为套餐商品编码时：返回查询结果为套餐商品编码，订购结果为0，已订购
					 * 入参一个，为套卡商品编码时：返回查询结果为真实查询结果，正常返回即可
					 * 入参两个，为套餐加套卡商品编码时：返回结果均为已订购。
					 */
					if (IDataUtil.isNotEmpty(HktkProdInfoList)) {
						IData HktkProdInfo = HktkProdInfoList.getData(0);
						String orderTime = changeTimePattern(HktkProdInfo.getString("START_DATE"));
						String validDate = changeTimePattern(HktkProdInfo.getString("START_DATE"));
						String expireDate = changeTimePattern(HktkProdInfo.getString("END_DATE"));
						IDataset crmProducts1=getCrmProductsInfo(paraCode1);//查找套餐对应的省产品编码
						String goodsName = UpcCall.qryOfferNameByOfferTypeOfferCode(crmProducts1.first().getString("PRODUCT_ID"), BofConst.ELEMENT_TYPE_CODE_PRODUCT);
						if (IDataUtil.isNotEmpty(AllbizInfoList)) {
							for (int k = 0; k < AllbizInfoList.size(); k++) {
								String goodsId = AllbizInfoList.getData(k).getString("GOODS_ID");
								if (StringUtils.equals(paraCode1, goodsId)) {
									AllbizInfoList.getData(k).put("IS_ORDER", "0");
									AllbizInfoList.getData(k).put("ORDER_TIME", orderTime);
									AllbizInfoList.getData(k).put("VALID_DATE", validDate);
									AllbizInfoList.getData(k).put("EXPIRE_DATE", expireDate);
									AllbizInfoList.getData(k).put("GOODS_NAME", goodsName);
								}
							}
						}
					}
				}
			}
		}
		if (IDataUtil.isNotEmpty(AllbizInfoList)&&!"0".equals(acctTag)) {//用户为已订购未激活时VALID_DATE返回29991231235959
			for (int k = 0; k < AllbizInfoList.size(); k++) {
				IData allbizInfoListData= AllbizInfoList.getData(k);
				if(allbizInfoListData.getString("VALID_DATE")!=null){
					AllbizInfoList.getData(k).put("VALID_DATE", "29991231235959");
				}
			}
		}
		data.put("BIZ_INFO_LIST", AllbizInfoList);
		return data;
	}
	
	
	/**
	 * 获取定向流量视频app列表
	 * @param elementId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private static String  getServiceIdList(String elementId, String userId) throws Exception{
		 String serviceIdList="";
		IDataset comparaInfos=CommparaInfoQry.getCommparaByCodeCode1("CSM", "2017",elementId,"IS_VIDEO_PKG");
		 if(IDataUtil.isNotEmpty(comparaInfos)){
			 
			 if(StringUtils.isNotBlank(comparaInfos.getData(0).getString("PARA_CODE20",""))){//固定APP的，以参数表为准
	             serviceIdList=comparaInfos.getData(0).getString("PARA_CODE20","");
	         }else{//自选
	         	 IData attrdata=new DataMap();
	         	 attrdata.put("USER_ID", userId);
	         	 attrdata.put("ELEMENT_ID", elementId);
	         	 attrdata.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
	         	 attrdata.put("DATE", SysDateMgr.getSysTime());
	              IDataset attrInfo=getUserAttrInfos(attrdata);
	              if(IDataUtil.isNotEmpty(attrInfo)){
	             	 for(int a=0;a<attrInfo.size();a++){
	             		 String appId=attrInfo.getData(a).getString("ATTR_VALUE","");
	                    	 if(!appId.isEmpty()&&!"-1".equals(appId)){//剔除特殊值
	                    		 serviceIdList = serviceIdList+appId+";";
	                    	 }
	             	 }
	             	 if(StringUtils.isNotBlank(serviceIdList)){
	             		 serviceIdList=serviceIdList.substring(0,serviceIdList.length()-1);
	             	 }
	              }
	         }
		 }
		return serviceIdList;
	}
	
	 public static IDataset getBizInfoList(String goodsId,String elementTypeCode,String userId,String id) throws Exception {
		 IDataset userOrderInfo = new DatasetList();
		 String goodsName="";
		 String serviceIdList = "";
		 IDataset bizInfoList= new DatasetList();
		 if("P".equals(elementTypeCode)){
			userOrderInfo =UserProductInfoQry.getUserProductByUserIdProductId(userId,id);
			goodsName=UpcCall.qryOfferNameByOfferTypeOfferCode(id, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
		}else if("D".equals(elementTypeCode)){
			userOrderInfo = UserDiscntInfoQry.getAllDiscntByUser(userId,id);
			goodsName=UpcCall.qryOfferNameByOfferTypeOfferCode(id, BofConst.ELEMENT_TYPE_CODE_DISCNT);
			 if(IDataUtil.isNotEmpty(userOrderInfo)){    
				 //当商品为定向流量商品时返回
				 serviceIdList= getServiceIdList(id,userId);	                		
			 }
		}else if("S".equals(elementTypeCode)){
			userOrderInfo = UserSvcInfoQry.getSvcUserId(userId,id);
			goodsName=UpcCall.qryOfferNameByOfferTypeOfferCode(id, BofConst.ELEMENT_TYPE_CODE_SVC);
		}
		 if (IDataUtil.isNotEmpty(userOrderInfo)) {
				for(int i=0;i<userOrderInfo.size();i++){
					IData returnOrderInfo = new DataMap();
					returnOrderInfo.put("GOODS_ID", goodsId);
					returnOrderInfo.put("GOODS_NAME", goodsName);
					returnOrderInfo.put("IS_ORDER", "0");// 0：是1：否
					if (StringUtils.isNotBlank(serviceIdList)) {
						returnOrderInfo.put("SERVICE_ID_LIST", serviceIdList);	
					}
					returnOrderInfo.put("ORDER_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userOrderInfo.getData(i).getString("START_DATE")));
					returnOrderInfo.put("VALID_DATE", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userOrderInfo.getData(i).getString("START_DATE")));
					returnOrderInfo.put("EXPIRE_DATE", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userOrderInfo.getData(i).getString("END_DATE")));
					bizInfoList.add(returnOrderInfo);
				}
			}else{
				IData returnOrderInfo = new DataMap();
				returnOrderInfo.put("GOODS_ID", goodsId);
				returnOrderInfo.put("GOODS_NAME", goodsName);
				returnOrderInfo.put("IS_ORDER", "1");// 0：是1：否
				bizInfoList.add(returnOrderInfo);
			}
		 return bizInfoList;
	 }
	 public static IDataset getBizInfoList2(String goodsId,String elementTypeCode,String userId,String id,String ctrmProductType) throws Exception {
		 IDataset userOrderInfo = new DatasetList();
		 String goodsName="";
		 String serviceIdList = "";
		 IDataset bizInfoList= new DatasetList();
		 if("P".equals(elementTypeCode)){
			if("2".equals(ctrmProductType)){
		        IData param = new DataMap();
		        param.put("USER_ID", userId);
		        param.put("PACKAGE_ID", id);
				userOrderInfo =UserSaleActiveInfoQry.getUserSaleActiveByUserIdPackageId(param);
				goodsName=UpcCall.qryOfferNameByOfferTypeOfferCode(id, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
			}else{
				userOrderInfo =UserProductInfoQry.getUserProductByUserIdProductId(userId,id);
				goodsName=UpcCall.qryOfferNameByOfferTypeOfferCode(id, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			}
		}else if("D".equals(elementTypeCode)){
			userOrderInfo = UserDiscntInfoQry.getAllDiscntByUser(userId,id);
			goodsName=UpcCall.qryOfferNameByOfferTypeOfferCode(id, BofConst.ELEMENT_TYPE_CODE_DISCNT);
			 if(IDataUtil.isNotEmpty(userOrderInfo)){    
				 //当商品为定向流量商品时返回
				 serviceIdList= getServiceIdList(id,userId);	                		
			 }
		}else if("S".equals(elementTypeCode)){
			userOrderInfo = UserSvcInfoQry.getSvcUserId(userId,id);
			goodsName=UpcCall.qryOfferNameByOfferTypeOfferCode(id, BofConst.ELEMENT_TYPE_CODE_SVC);
		}
		 if (IDataUtil.isNotEmpty(userOrderInfo)) {
				for(int i=0;i<userOrderInfo.size();i++){
					IData returnOrderInfo = new DataMap();
					returnOrderInfo.put("GOODS_ID", goodsId);
					returnOrderInfo.put("GOODS_NAME", goodsName);
					returnOrderInfo.put("IS_ORDER", "0");// 0：是1：否
					if (StringUtils.isNotBlank(serviceIdList)) {
						returnOrderInfo.put("SERVICE_ID_LIST", serviceIdList);	
					}
					returnOrderInfo.put("ORDER_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userOrderInfo.getData(i).getString("START_DATE")));
					returnOrderInfo.put("VALID_DATE", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userOrderInfo.getData(i).getString("START_DATE")));
					returnOrderInfo.put("EXPIRE_DATE", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userOrderInfo.getData(i).getString("END_DATE")));
					bizInfoList.add(returnOrderInfo);
				}
			}else{
				IData returnOrderInfo = new DataMap();
				returnOrderInfo.put("GOODS_ID", goodsId);
				returnOrderInfo.put("GOODS_NAME", goodsName);
				returnOrderInfo.put("IS_ORDER", "1");// 0：是1：否
				bizInfoList.add(returnOrderInfo);
			}
		 return bizInfoList;
	 }
	/**
	* 根据产品id找出crm测的信息
	* @param pd
	* @param data
	* @return
	* @throws Exception
	*/
	public static IDataset getCrmProductsInfo(String qwProductId) throws Exception {
	 IData  ctrmId=new  DataMap();
	 ctrmId.put("CTRM_PRODUCT_ID", qwProductId);
	 IDataset result = Dao.qryByCodeParser("TD_B_CTRM_RELATION","SEL_RSRV_BY_PRODUCT_ID", ctrmId, Route.CONN_CRM_CEN);
	 return result;
	}
	
	//查询
	public static IDataset getUserAttrInfos(IData info) throws Exception
	{	 
	 String routeId=info.getString("EPARCHY_CODE"); 	 
	return Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_BY_USER_ELEMENTID", info,routeId);
	
	}
	
	/**
	* @Function: getMobileUserInfo
	* @Description: 用户信息查询
	* @param: @param data
	* @param: @return
	* @param: @throws Exception
	* @return：IDataset
	* @throws：
	* @version: v1.0.0
	* @author: 
	* @date: 
	*/
	public IData getMobileUserInfo(IData data) throws Exception {
		String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IData result = new DataMap();
		result.put("BIZ_CODE", "0000");
		result.put("BIZ_DESC", "查询用户信息成功");
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		IData userData = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userData)) {
			result.put("BIZ_CODE", "2043");
			result.put("BIZ_DESC", "用户信息不存在！");
	     return result;
		}
		IData userInfo = new DataMap();
		String userId = userData.getString("USER_ID");
		QueryInfoBean queryInfoBean = BeanManager.createBean(QueryInfoBean.class);
		data.put("X_GETMODE", 1);// 1-输入用户标识
		data.put("USER_ID", userId);
		IDataset dataset = queryInfoBean.getUserCustAcct(data);
		
		String userName = dataset.getData(0).getString("CUST_NAME", "");// 用户姓名
		String userStatus = dataset.getData(0).getString("USER_STATE_CODESET","");// 用户状态	
		String userBegin = dataset.getData(0).getString("IN_DATE", "");// 入网时间		
		String simCardNo = dataset.getData(0).getString("SIM_CARD_NO", "");// SIM
		String userCity = userData.getString("EPARCHY_CODE");
		userStatus = getUserStateParam(userStatus);
		userBegin = userBegin.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		 // 客户名称限制32位
	    char ch[] = userName.toCharArray();
	    int j = 0;
	    for (int i = 0; i < ch.length; i++)
	    {
	        if (ch[i] > 255)
	        {
	            j = j + 2;
	        }
	        else
	        {
	            j = j + 1;
	        }
	        if (j > 32)
	        {
	            j = i;
	            break;
	        }
	    }
	    if (j > ch.length)
	        j = ch.length;
	    userName = userName.substring(0, j);
		// 中高端标识
		IDataset highCust = CustVipInfoQry.queryHighCustByUserId(userId, "*");
		String highLevelId = "";
		if (IDataUtil.isEmpty(highCust)) {//0：是 1：否
			highLevelId = "1";
		} else {
			highLevelId = "0";
		}
	
		String provInfo = StaticInfoQry.qryProvCode(getVisit().getProvinceCode());
	
		userInfo.put("USER_NAME", blurCustomerNameNewRule(userName));// 此处需要模糊化，以后进行配置
		userInfo.put("USER_STATUS", userStatus);
		userInfo.put("USER_BEGIN", userBegin);
		userInfo.put("STAR_CLASS", highLevelId);
		userInfo.put("CITY_CODE", userCity);
		userInfo.put("PROV_CODE", provInfo);
		userInfo.put("USER_UNIQUE", userId);
		userInfo.put("SIMCARD_NO", simCardNo);
		IDataset vipInfo = CustVipInfoQry.qryVipInfoByUserId(userId);
		if (IDataUtil.isEmpty(vipInfo)) {
			// 没有大客户信息
			userInfo.put("USER_ID", "00");// 普通客户
		} else {
			userInfo.put("USER_ID", "01");// VIP客户
			String vipTypeCode = vipInfo.getData(0).getString("VIP_TYPE_CODE","");// VIP等级
			if (!"1".equals(vipTypeCode) && !"2".equals(vipTypeCode) && !"3".equals(vipTypeCode)) {
				// 除1、2、3外其他归为普通大客户
				userInfo.put("USER_ID", "00");// 普通客户
			} 
		}
	
		 IData queryData = UcaInfoQry.qryCustomerInfoByCustId(userData.getString("CUST_ID"));
	     if(queryData == null)
	         CSAppException.apperr(CrmUserException.CRM_USER_397);
	    userInfo.put("REAL_NAME_INFO", "2".equals(queryData.getString("IS_REAL_NAME")) ? "2" : "1");//2:已登记，1:未登记
		userInfo.putAll(buildUserCreditInfo(userInfo));
//		IDataset userInfoList = new DatasetList();
//		userInfoList.add(userInfo);
		result.put("USER_INFO_LIST", userInfo);
		return result;
	}
	
	
	public static IData buildUserCreditInfo(IData input) throws Exception
	{
	 IData userInfo = new DataMap();
	 //2.添加用户信誉度信息
	 IData queryData = AcctCall.getUserCreditInfos("0", input.getString("USER_UNIQUE", input.getString("USER_ID"))).getData(0);       
	 IData transMap = new DataMap();
	 transMap.put("-1", "13");//未评级客户   
	 transMap.put("0", "12");//0星级客户   
	 transMap.put("1", "11");//1星客户    
	 transMap.put("2", "10");//2星客户    
	 transMap.put("3", "09");//3星客户    
	 transMap.put("4", "08");//4星客户    
	 transMap.put("5", "07");//5星普通客户  
	 transMap.put("6", "06");// 5星金客户   
	 transMap.put("7", "05");// 5星钻客户         
	 userInfo.put("STAR_LEVEL", transMap.getString(queryData.getString("CREDIT_CLASS")));           
	 String starScore = queryData.getString("STAR_SCORE", "0");
	 if ("".equals(starScore) || starScore == null)
	 {
	     starScore = "0";
	 }
	 userInfo.put("STAR_SCORE", starScore);
	 if (!"0".equals(userInfo.getString("STAR_LEVEL")))
	 {
	     String starTime = queryData.getString("STAR_TIME");
	     userInfo.put("STAR_TIME", starTime);
	 }
	 return userInfo;
	}
	
	/**
	 * 获取用户状态编码
	 */
	private String getUserStateParam(String param) {
		String result = "";
		if ("0".equals(param))
			result = "00";
		else if ("1".equals(param))
			result = "02";
		else if ("2".equals(param))
			result = "02";
		else if ("3".equals(param))
			result = "02";
		else if ("4".equals(param))
			result = "02";
		else if ("5".equals(param))
			result = "02";
		else if ("6".equals(param))
			result = "04";
		else if ("7".equals(param))
			result = "02";
		else if ("8".equals(param))
			result = "03";
		else if ("9".equals(param))
			result = "03";
		else if ("A".equals(param))
			result = "01";
		else if ("B".equals(param))
			result = "01";
		else if ("C".equals(param))
			result = "02";
		else if ("D".equals(param))
			result = "02";
		else if ("E".equals(param))
			result = "04";
		else if ("F".equals(param))
			result = "03";
		else if ("G".equals(param))
			result = "01";
		else if ("H".equals(param))
			result = "03";
		else if ("I".equals(param))
			result = "02";
		else if ("J".equals(param))
			result = "02";
		else if ("K".equals(param))
			result = "02";
		else if ("L".equals(param))
			result = "02";
		else if ("M".equals(param))
			result = "02";
		else if ("N".equals(param))
			result = "00";
		else if ("O".equals(param))
			result = "02";
		else if ("Q".equals(param))
			result = "02";
		else
			result = "00";
		return result;
	}
	
	/**
	 * 
	 * @param name
	 * @return 保留姓名最后一位,其它使用x模糊化
	 */
	public String blurCustomerNameNewRule(String name) {
		// 返回模糊后名字
		StringBuilder retName = new StringBuilder();
		if (name == null || "".equals(name)) {
			return "";
		}
		// x模糊
		for (int i = 0; i < name.length() - 1; i++) {
			retName.append("x");
		}
		retName.append(name.substring(name.length() - 1, name.length()));
		return retName.toString();
	}
	
	
	/**
	* 和家庭成员查询
	* 查询用户所在的和家庭业务中的家庭成员信息，主号和副号均可发起查询，返回信息中包含查询用户的记录
	* @param input
	* @return
	* @throws Exception
	*/
	public IData queryFamilyMembinfo(IData input) throws Exception {
		// 入参校验
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		String userId = userInfo.getString("USER_ID");
		IData data = new DataMap();
		IDataset menberInfoList = new DatasetList();
		data.put("BIZ_CODE", "0000");
		data.put("BIZ_DESC", "和家庭成员查询成功");
		data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		if(IDataUtil.isEmpty(userInfo)){
			data.put("BIZ_CODE", "2043");
			data.put("BIZ_DESC", "用户信息不存在！");
	     return data;
		}
		IDataset uuList = RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(userId, "45");//查询所有有效UU表关系
	 if (IDataUtil.isEmpty(uuList))
	 {
	 	data.put("BIZ_CODE", "2099");
			data.put("BIZ_DESC", "没有查询到和家庭信息");
	     return data;
	 }
	 IDataset uuAllList = new DatasetList();
	 for (int k = 0; k < uuList.size(); k++)
	 {
	     IData queryMember = uuList.getData(k);
	     String userIdA = queryMember.getString("USER_ID_A");
	     uuAllList = RelaUUInfoQry.queryFamilyMemList(userIdA, "45");//根据虚拟用户查询所有有效成员
	     if (IDataUtil.isEmpty(uuAllList))
	     {
	     	data.put("BIZ_CODE", "2099");
				data.put("BIZ_DESC", "没有查询到和家庭信息");
	         return data;
	     }
	     for (int i = 0; i < uuAllList.size(); i++)
	     {         	
	     	IData member = uuAllList.getData(i);
	     	String serialNumberB = member.getString("SERIAL_NUMBER_B");
	         String roleCodeB =member.getString("ROLE_CODE_B");//主卡的ROLECODEB是1 副卡是2
	         String beginTime = member.getString("START_DATE");
	         beginTime = beginTime.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
	         String shortCode = member.getString("SHORT_CODE");
	         IData userInfoB = UcaInfoQry.qryUserInfoBySn(serialNumberB);
	         String custid = userInfoB.getString("CUST_ID","");
	         String custname ="";
	         IData custinfo = UcaInfoQry.qryCustomerInfoByCustId(custid);
	 		if(IDataUtil.isNotEmpty(custinfo)) {
	 			custname= custinfo.getString("CUST_NAME","");
	 		}
	 		String userCity = userInfoB.getString("EPARCHY_CODE");
	 		String provInfo = StaticInfoQry.qryProvCode(getVisit().getProvinceCode());
	 		IData returnMemberInfo = new DataMap();
	 		returnMemberInfo.put("SERIAL_NUMBER", serialNumberB);//和家庭成员号码
	 		returnMemberInfo.put("MAIN_CARD_FLAG", roleCodeB);// 1 主号2副号
	 		returnMemberInfo.put("PROV_CODE", provInfo);//省份编码
	 		returnMemberInfo.put("CITY_CODE", userCity);//地市编码
	 		returnMemberInfo.put("BGN_TIME", beginTime);//业务开始时间,此时间是加入成员组的时间YYYYMMDDHH24MISS
	 		if (!"".equals(shortCode) && null != shortCode) {
				returnMemberInfo.put("SHORT_NUMBER", shortCode);// 家庭成员短号码
			}
	 		returnMemberInfo.put("USER_NAME", blurCustomerNameNewRule(custname));//要求不返回姓，只返回名字的最后一个字。如 “王小二”返回 “xx二”（小写字母x）
	 		menberInfoList.add(returnMemberInfo);
	     }
	    
	 }
	 data.put("MEMBER_INFO_LIST", menberInfoList);
		return data;
	}

	/**
	 * 11.7.75	定向流量产品订购关系查询接口（CIP00112）
	 * @param input
	 * @return
	 * @throws Exception
	 * @author zhaohj3
	 */
	public IDataset queryDireFlowProdOrdRela(IData input) throws Exception {
		IDataset results = new DatasetList();
		IData result = new DataMap();
		try {
			String serviceType = IDataUtil.chkParam(input, "SERVICE_TYPE");
			String serviceNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
			String serviceId = IDataUtil.chkParam(input, "SERVICE_ID");

			IDataset bizInfoList = new DatasetList();

			String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(serviceNumber);

			IData userInfo = UcaInfoQry.qryUserInfoBySn(serviceNumber, routeId);

			if (IDataUtil.isEmpty(userInfo)) {
				result.put("bizCode", "2999");
				result.put("bizDesc", "该服务号码[" + serviceNumber + "]用户信息不存在");
				results.add(result);
				return results;
			}

			String userId = userInfo.getString("USER_ID");

			IDataset videoPkgCommparas = CommparaInfoQry.getcommparaByAttrCode1Code20("CSM", "2017", "IS_VIDEO_PKG", serviceId, "ZZZZ");
			videoPkgCommparas = DataHelper.distinct(videoPkgCommparas, "PARAM_CODE", ""); // 去重
			if (IDataUtil.isNotEmpty(videoPkgCommparas)) {
				for (int i = 0; i < videoPkgCommparas.size(); i++) {
					IData videoPkgCommpara = videoPkgCommparas.getData(i);
					String discntCode = videoPkgCommpara.getString("PARAM_CODE");
					IDataset userDiscntInfos = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userId, discntCode, routeId);
					if (IDataUtil.isNotEmpty(userDiscntInfos)) {
						IData bizInfo = new DataMap();
						bizInfo.put("bossId", discntCode); // 省侧boss编码：省侧boss编码，当isOrder=0时必填。
						String discntName = UpcCall.qryOfferNameByOfferTypeOfferCode(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);
						bizInfo.put("bossName", discntName); // 省侧产品名称：省侧产品名称，当isOrder=0时必填。

						IData qryData = new DataMap();
						qryData.put("ELEMENT_ID", discntCode);
						qryData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
						qryData.put("EPARCHY_CODE", "ZZZZ");
						IDataset relationInfos = QueryListInfo.queryRelationInfoByElementIdType(qryData);

						if (IDataUtil.isNotEmpty(relationInfos)) {
							bizInfo.put("goodsId", relationInfos.getData(0).getString("CTRM_PRODUCT_ID")); // 商品编码：能开商品编码(当isOrder=0时必填。若无对应能开编码则为-1)
							bizInfo.put("goodsName", discntName); // 商品名称：能开商品名称(当isOrder=0时必填。若无对应能开编码则为-1)
						} else {
							bizInfo.put("goodsId", "-1"); // 商品编码：能开商品编码(当isOrder=0时必填。若无对应能开编码则为-1)
							bizInfo.put("goodsName", "-1"); // 商品名称：能开商品名称(当isOrder=0时必填。若无对应能开编码则为-1)
						}

						IDataset serviceIdList = CommparaInfoQry.getCommparaInfoByCode("CSM", "2017", discntCode, "IS_VIDEO_PKG", "ZZZZ");
						serviceIdList = DataHelper.distinct(serviceIdList, "PARA_CODE20", ""); // 去重
						String serviceIds = serviceId;
						if (IDataUtil.isNotEmpty(serviceIdList)) {
							for (int j = 0; j < serviceIdList.size(); j++) {
								String paraCode20 = serviceIdList.getData(j).getString("PARA_CODE20");
								if (!StringUtils.equals(serviceId, paraCode20)) {
									serviceIds = serviceIds + ";" + paraCode20;
								}
							}
						}
						bizInfo.put("serviceIdList", serviceIds); // APP服务列表：bizCode=0000时，必定包含请求中的serviceId； 多个serviceId用英文分号分隔，如serviceId1; serviceId2;serviceId3
						bizInfo.put("isOrder", "0"); // 是否存在订购关系：0：是； 1：否
						bizInfo.put("orderTime", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userDiscntInfos.getData(0).getString("UPDATE_TIME"))); // 订购时间：YYYYMMDDHH24MISS，当isOrder=0时必填。
						bizInfo.put("validDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userDiscntInfos.getData(0).getString("START_DATE"))); // 生效时间：YYYYMMDDHH24MISS，当isOrder=0时必填。
						bizInfo.put("expireDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userDiscntInfos.getData(0).getString("END_DATE"))); // 失效时间YYYYMMDDHH24MISS
						bizInfoList.add(bizInfo);
					}
				}
			}

			IDataset userAttrInfos = UserAttrInfoQry.getUserAttrByUserIdInsetTypeAttrValue(userId, BofConst.ELEMENT_TYPE_CODE_DISCNT, "APP_SERVICE_ID_%", serviceId);

			if (IDataUtil.isNotEmpty(userAttrInfos)) {
				for (int i = 0; i < userAttrInfos.size(); i++) {
					IData userAttrInfo = userAttrInfos.getData(i);
					String relaInstId = userAttrInfo.getString("RELA_INST_ID");
					IDataset userDiscntInfos = UserDiscntInfoQry.quyUserDiscntByUserIdAndInstId(userId, relaInstId);
					if (IDataUtil.isNotEmpty(userDiscntInfos)) {
						String discntCode = userDiscntInfos.getData(0).getString("DISCNT_CODE");
						String discntName = UpcCall.qryOfferNameByOfferTypeOfferCode(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);

						IData bizInfo = new DataMap();
						bizInfo.put("bossId", discntCode); // 省侧boss编码：省侧boss编码，当isOrder=0时必填。
						bizInfo.put("bossName", discntName); // 省侧产品名称：省侧产品名称，当isOrder=0时必填。

						IData qryData = new DataMap();
						qryData.put("ELEMENT_ID", discntCode);
						qryData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
						IDataset relationInfos = QueryListInfo.queryRelationInfoByElementIdType(qryData);

						if (IDataUtil.isNotEmpty(relationInfos)) {
							bizInfo.put("goodsId", relationInfos.getData(0).getString("CTRM_PRODUCT_ID")); // 商品编码：能开商品编码(当isOrder=0时必填。若无对应能开编码则为-1)
							bizInfo.put("goodsName", discntName); // 商品名称：能开商品名称(当isOrder=0时必填。若无对应能开编码则为-1)
						} else {
							bizInfo.put("goodsId", "-1"); // 商品编码：能开商品编码(当isOrder=0时必填。若无对应能开编码则为-1)
							bizInfo.put("goodsName", "-1"); // 商品名称：能开商品名称(当isOrder=0时必填。若无对应能开编码则为-1)
						}

						IDataset serviceIdList = UserAttrInfoQry.getUserAttrByRelaInstIdLikeAttrCode(userId, BofConst.ELEMENT_TYPE_CODE_DISCNT, relaInstId, "APP_SERVICE_ID_%");
						serviceIdList = DataHelper.distinct(serviceIdList, "ATTR_VALUE", ""); // 去重
						String serviceIds = serviceId;
						if (IDataUtil.isNotEmpty(serviceIdList)) {
							for (int j = 0; j < serviceIdList.size(); j++) {
								String attrValue = serviceIdList.getData(j).getString("ATTR_VALUE");
								if (!StringUtils.equals(serviceId, attrValue)) {
									serviceIds = serviceIds + ";" + attrValue;
								}
							}
						}
						bizInfo.put("serviceIdList", serviceIds); // APP服务列表：bizCode=0000时，必定包含请求中的serviceId； 多个serviceId用英文分号分隔，如serviceId1; serviceId2;serviceId3
						bizInfo.put("isOrder", "0"); // 是否存在订购关系：0：是； 1：否
						bizInfo.put("orderTime", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userDiscntInfos.getData(0).getString("UPDATE_TIME"))); // 订购时间：YYYYMMDDHH24MISS，当isOrder=0时必填。
						bizInfo.put("validDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userDiscntInfos.getData(0).getString("START_DATE"))); // 生效时间：YYYYMMDDHH24MISS，当isOrder=0时必填。
						bizInfo.put("expireDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, userDiscntInfos.getData(0).getString("END_DATE"))); // 失效时间YYYYMMDDHH24MISS
						bizInfoList.add(bizInfo);
					}
				}
			}

			if (IDataUtil.isEmpty(bizInfoList)) {
				if (IDataUtil.isEmpty(videoPkgCommparas)) {
					result.put("bizCode", "4007");
					result.put("bizDesc", "目前不支持所选视频APP，请重新选择");
					result.put("oprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
					results.add(result);
					return results;
				}
				IData bizInfo = new DataMap();
				bizInfo.put("isOrder", "1");
				bizInfo.put("serviceIdList", serviceId);
				bizInfoList.add(bizInfo);
			} else {
				DataHelper.sort(bizInfoList, "orderTime", IDataset.TYPE_STRING); // 按订购时间排序
			}

			result.put("bizInfoList", bizInfoList);
			result.put("bizCode", "0000");
			result.put("bizDesc", "查询成功");
			result.put("oprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		} catch (Exception e) {
			result.put("bizCode", "2998");
			result.put("bizDesc", e.getMessage().length() > 255 ? e.getMessage().substring(0, 255) : e.getMessage());
		}
		results.add(result);
		return results;
	}

	/**
	 * 和家固话入网资格校验接口(CIP00115)
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkComeInNetForFixPhone(IData input) throws Exception {
		String serviceType = IDataUtil.chkParam(input, "SERVICE_TYPE"); // 标识类型，01：手机号码，02：固话号码，03：宽带帐号，04：vip卡号，05：集团编码；本期只有01：手机号码
		String serviceNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER"); // 标识号码
		String custName = input.getString("CUSTOMER_NAME"); // 客户姓名
		String psptTypeCode = IDataUtil.chkParam(input, "ID_CARD_TYPE");
		String psptId = IDataUtil.chkParam(input, "ID_CARD_NUM");

//		应答编码
//		bizCode：bizDesc
//		2004：用户已单向停机
//		2005：用户欠费停机
//		2006：用户预销户
//		2007：用户销户
//		2009：用户状态非法，指用户处于非单停、停机、预销、销户的其它状态
//		2020：用户存在滞纳金
//		2053：用户主动申请停机
//		2060：号码与证件不一致
//		3A11：证件类型错误
//		3000：黑名单客户
//		3003：存在欠费号码
//		3007：该手机在归属地市无宽带
//		其他错误类型参见《中国移动网状网系统接口规范-二级返回码分册》
		IData ret = new DataMap();
		ret.put("RSP_CODE", "1"); // 校验是否通过，1-成功；2-失败
		IDataset rspInfo = new DatasetList();
		try {
			if ("01".equals(serviceType)) {
				IData userStateInfo = getUserState(input); // 用户状态查询
				if ("0000".equals(userStateInfo.getString("BIZ_CODE"))) {
					String userStauts = userStateInfo.getString("USERSTAUTS");
					if ("00".equals(userStauts)) {
						IDataset custInfoList = getCustInfoByPspt(custName, psptTypeCode, psptId);
						if (IDataUtil.isEmpty(custInfoList)) {
							ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
							IData bizInfo = new DataMap();
							bizInfo.put("BIZ_CODE", "2043");
							bizInfo.put("BIZ_DESC", "业务号码与客户姓名、证件号码信息不匹配！");
							rspInfo.add(bizInfo);
						}

						IDataset custList = getCustInfoByPspt(psptTypeCode, psptId); // 该证件下的用户资料
						if (IDataUtil.isNotEmpty(custList)) {
							boolean codebool = checkPostCard(psptTypeCode);
							// 检查证件类型是否合法
							if (!codebool) {
								ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
								IData bizInfo = new DataMap();
								bizInfo.put("BIZ_CODE", "3A11");
								bizInfo.put("BIZ_DESC", "证件类型不正确");
								rspInfo.add(bizInfo);
							}
							// 检查黑名单
							boolean checkBlackCust = UCustBlackInfoQry.isBlackCust(custList.getData(0).getString("PSPT_TYPE_CODE"), psptId);
							if (checkBlackCust) {
								ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
								IData bizInfo = new DataMap();
								bizInfo.put("BIZ_CODE", "3000");
								bizInfo.put("BIZ_DESC", "该用户有黑名单信息");
								rspInfo.add(bizInfo);
							}

							// 存在欠费号码
							// 根据客户标记获取用户是否有欠费判断
							if (custList != null && custList.size() > 0) {
								IData oweFeeData = getPhonenOweFeeUserById(custList);
								if (!oweFeeData.isEmpty()) {
									ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
									IData bizInfo = new DataMap();
									bizInfo.put("BIZ_CODE", "3003");
									bizInfo.put("BIZ_DESC", "该客户有号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元，不能再次使用该证件办理开户业务！");
									rspInfo.add(bizInfo);
								}
							}
						} else {
							ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
							IData bizInfo = new DataMap();
							bizInfo.put("BIZ_CODE", "2043");
							bizInfo.put("BIZ_DESC", "用户身份信息错误：证件号码不正确");
							rspInfo.add(bizInfo);
						}
					} else if ("01".equals(userStauts)) {
						//单向停机
						ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
						IData bizInfo = new DataMap();
						bizInfo.put("BIZ_CODE", "2004"); // 返回码，rspCode=2时必填
						bizInfo.put("BIZ_DESC", "用户已单向停机"); // 错误信息描述
						rspInfo.add(bizInfo);
					} else if ("02".equals(userStauts)) {
						//停机
						ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
						IData bizInfo = new DataMap();
						bizInfo.put("BIZ_CODE", "2005"); // 返回码，rspCode=2时必填
						bizInfo.put("BIZ_DESC", "用户已停机"); // 错误信息描述
						rspInfo.add(bizInfo);
					} else if ("03".equals(userStauts)) {
						//预销户
						ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
						IData bizInfo = new DataMap();
						bizInfo.put("BIZ_CODE", "2006"); // 返回码，rspCode=2时必填
						bizInfo.put("BIZ_DESC", "用户预销户"); // 错误信息描述
						rspInfo.add(bizInfo);
					} else if ("04".equals(userStauts)) {
						//销户
						ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
						IData bizInfo = new DataMap();
						bizInfo.put("BIZ_CODE", "2007"); // 返回码，rspCode=2时必填
						bizInfo.put("BIZ_DESC", "用户销户"); // 错误信息描述
						rspInfo.add(bizInfo);
					} else if ("99".equals(userStauts)) {
						//此号码不存在
						ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
						IData bizInfo = new DataMap();
						bizInfo.put("BIZ_CODE", "2009"); // 返回码，rspCode=2时必填
						bizInfo.put("BIZ_DESC", "此号码不存在"); // 错误信息描述
						rspInfo.add(bizInfo);
					}

					String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(serviceNumber);
					IData widenetUserInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serviceNumber);
					String widenetEparhyCode = widenetUserInfo.getString("EPARCHY_CODE");
					if (!StringUtils.equals(routeId, widenetEparhyCode)) {
						ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
						IData bizInfo = new DataMap();
						bizInfo.put("BIZ_CODE", "3007"); // 返回码，rspCode=2时必填
						bizInfo.put("BIZ_DESC", "该手机在归属地市无宽带"); // 错误信息描述
						rspInfo.add(bizInfo);
					}
				} else {
					ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
					IData bizInfo = new DataMap();
					bizInfo.put("BIZ_CODE", userStateInfo.getString("BIZ_CODE"));
					bizInfo.put("BIZ_DESC", userStateInfo.getString("BIZ_DESC"));
					rspInfo.add(bizInfo);
				}
			} else {
				ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
				IData bizInfo = new DataMap();
				bizInfo.put("BIZ_CODE", "2998"); // 返回码，rspCode=2时必填
				bizInfo.put("BIZ_DESC", "目前不支持标识类型为：[" + serviceType + "]的和家固话入网资格校验校验"); // 错误信息描述
				rspInfo.add(bizInfo);
			}

			if ("1".equals(ret.getString("RSP_CODE"))) { // 校验是否通过，1-成功；2-失败
				IData bizInfo = new DataMap();
				bizInfo.put("BIZ_CODE", "0000"); // 返回码，rspCode=2时必填
				bizInfo.put("BIZ_DESC", "成功"); // 错误信息描述
				rspInfo.add(bizInfo);
			}
		} catch (Exception e) {
			ret.put("RSP_CODE", "2"); // 校验是否通过，1-成功；2-失败
			IData bizInfo = new DataMap();
			bizInfo.put("BIZ_CODE", "2998"); // 返回码，rspCode=2时必填
			bizInfo.put("BIZ_DESC", e.getMessage()); // 错误信息描述
			rspInfo.add(bizInfo);
		}
		ret.put("RSP_INFO", rspInfo); // 失败原因，rspCode=2时必填

		return ret;
	}

	/**
	 * 查询已办理和家固话信息（CIP00116）
	 * @param input
	 * @return
	 * @throws Exception
	 * @zhaohj3
	 * @date 2019-4-21 23:48:56
	 */
	public IData queryHFixPhoneInfo (IData input) throws Exception {
		String serviceNoType = IDataUtil.chkParam(input, "SERVICE_NO_TYPE"); // 业务号码类型，01：手机号码 02：固话号码
		String serviceNo = IDataUtil.chkParam(input, "SERIAL_NUMBER"); // 业务号码，业务号码类型=01时填11位手机号码，业务号码类型=02时填写7位或8位固话号码
		String areaCode = input.getString("AREA_CODE"); // 固话区号，业务号码类型=02时必填，填写行政区号，如北京010
		String goodsId = input.getString("GOODS_ID"); // 产品编码，能开商品编码

		if("02".equals(serviceNoType)){
			serviceNo = areaCode+serviceNo;
		}
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serviceNo);
		if (IDataUtil.isEmpty(userInfo)) {
			String bizCode = "2998";
			String bizDesc = "根据号码[" + serviceNo + "]未查询到有效用户信息";
			return errorRerurn(bizCode, bizDesc);
		}

		// 和家固话资费公共参数配置
		// PARA_CODE1：资费编码
		IDataset commpara = CommparaInfoQry.getCommpara("CSM", "7359", "HJGH_PRODUCT_CONFIG", "ZZZZ");
		if (IDataUtil.isEmpty(commpara)) {
			String bizCode = "2998";
			String bizDesc = "省侧和家固话资费公共参数配置为空";
			return errorRerurn(bizCode, bizDesc);
		}

		String offerCode = "";
		if (StringUtils.isNotBlank(goodsId)) { // 查询能开商品编码对应BOSS产品信息
			IData param = new DataMap();
			param.put("CTRM_PRODUCT_ID", goodsId);
			param.put("EPARCHY_CODE", "0898");
			IDataset ctrmRelations = QueryListInfo.queryListInfoForRelation(param);
			if (IDataUtil.isEmpty(ctrmRelations)) {
				String bizCode = "2998";
				String bizDesc = "根据能开商品编码[" + goodsId + "]未查询到BOSS产品映射关系";
				return errorRerurn(bizCode, bizDesc);
			}

			offerCode = ctrmRelations.getData(0).getString("ELEMENT_ID"); // TODO 取优惠编码
			if (StringUtils.isBlank(offerCode)) {
				String bizCode = "2998";
				String bizDesc = "根据能开商品编码[" + goodsId + "]查询到的对应的BOSS元素编码为空";
				return errorRerurn(bizCode, bizDesc);
			}
		}

		String serviceNumber = "";
		List<String> bossServieFtNoList = new ArrayList<String>();
		if ("01".equals(serviceNoType)) { // 01：手机号码
			serviceNumber = serviceNo; // 手机号码
			// 根据手机号码USER_ID查询手机号码UU关系
			IDataset relationSN = RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(userInfo.getString("USER_ID"), "MS"); // 手机号码ROLE_CODE_B = 1
			if (IDataUtil.isNotEmpty(relationSN)) {
				for (int i = 0; i < relationSN.size(); i++) {
					String userIdA = relationSN.getData(i).getString("USER_ID_A");
					// 根据USER_ID_A查询固话UU关系
					IDataset relationIMS = RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "2"); // 固话号码ROLE_CODE_B = 2
					for (int j = 0; j < relationIMS.size(); j++) {
						bossServieFtNoList.add(relationIMS.getData(i).getString("SERIAL_NUMBER_B"));// 查询手机号码关联的固话号码
					}
				}
			}
		} else if ("02".equals(serviceNoType)) { // 02：固话号码
			IDataset relationSN = new DatasetList();
			// 根据固话号码USER_ID查询固话UU关系
			IDataset relationIMS = RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(userInfo.getString("USER_ID"),  "MS"); // 固话号码ROLE_CODE_B = 2
			if (IDataUtil.isNotEmpty(relationIMS)) {
				String userIdA = relationIMS.getData(0).getString("USER_ID_A");
				// 根据USER_ID_A查询手机UU关系
				relationSN = RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "1"); // 手机号码ROLE_CODE_B = 1
			}
			if (IDataUtil.isEmpty(relationSN)) {
				String bizCode = "2998";
				String bizDesc = "根据号码[" + serviceNo + "]未查询到业务账号";
				return errorRerurn(bizCode, bizDesc);
			}
			serviceNumber = relationSN.getData(0).getString("SERIAL_NUMBER_B");
			bossServieFtNoList.add(serviceNo);
		} else {
			String bizCode = "2998";
			String bizDesc = "业务号码类型[" + serviceNoType + "]错误";
			return errorRerurn(bizCode, bizDesc);
		}

		IDataset results = new DatasetList();
		for (int k = 0; k < bossServieFtNoList.size(); k++) { // 和家固话信息
			String bossServieFtNo = bossServieFtNoList.get(k);
			IData imsUserInfo = UcaInfoQry.qryUserInfoBySn(bossServieFtNo); // 查询固话号码用户信息
			if (IDataUtil.isEmpty(imsUserInfo)) {
				continue;
			}
			String removeTag = imsUserInfo.getString("REMOVE_TAG");
			String status;
			if ("0".equals(removeTag)) {
				status = "00"; // 00：正常（和家固话可正常使用）
			} else if ("2".equals(removeTag)) {
				status = "02"; // 01：异常（除正常和销户外的都是异常状态）
			} else {
				status = "01"; // 02：销户（和家固话不可使用,且已解除关系）
			}
			String openTime = Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, imsUserInfo.getString("OPEN_DATE"));

			IDataset discntInfos = UserDiscntInfoQry.getAllValidDiscntByUserId(imsUserInfo.getString("USER_ID"));
			if (IDataUtil.isNotEmpty(discntInfos)) {
				for (int j = 0; j < discntInfos.size(); j++) {
					String discntCode = discntInfos.getData(j).getString("DISCNT_CODE");
					for (int i = 0; i < commpara.size(); i++) {
						String configProductCode = commpara.getData(i).getString("PARA_CODE1");
						String configProductName = commpara.getData(i).getString("PARA_CODE2");
						String configDiscntCode = commpara.getData(i).getString("PARA_CODE4");
						if (StringUtils.equals(configDiscntCode, discntCode)) { // 用户订购了配置的和家固话资费
							if (StringUtils.isBlank(goodsId) || StringUtils.equals(offerCode, discntCode)) { // 如果查询条件中传了能开商品编码，则结果只展示需要查询能开商品编码订购信息
								String orderTime = Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, discntInfos.getData(0).getString("UPDATE_TIME"));
								String validTime = Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, discntInfos.getData(0).getString("START_DATE"));
								String endTime = Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, discntInfos.getData(0).getString("END_DATE"));

								IData result = new DataMap();
								result.put("SERVICE_NUMBER", serviceNumber); // 必填，业务账号，用户输入的手机号码
								result.put("BOSS_SERVIE_FT_AREA_CODE", areaCode); // 非必填，省BOSS侧记录的固话区号，省BOSS侧记录的和家固话行政区号，如北京010
								result.put("BOSS_SERVIE_FT_NO", bossServieFtNo); // 必填，省BOSS侧记录的固话号码，省BOSS侧记录的固话7位或8位号码
								result.put("STATUS", status); // 必填，固话状态，00：正常（和家固话可正常使用），01：异常（除正常和销户外的都是异常状态）,02：销户（和家固话不可使用,且已解除关系）
								result.put("OPEN_TIME", openTime); // 必填，固话开通时间，和家固话号码的开通时间
								result.put("GOODS_ID", goodsId); // 必填，套餐编码，能开产品编码，无能开编码时返回-1
								result.put("GOODS_NAME", configProductName); // 必填，套餐名称，能开产品名称，无能开编码时返回-1
								result.put("PRODUCT_ID", configProductCode); // 必填，省侧套餐编码，省侧产品编码
								result.put("PRODUCT_NAME", configProductName); // 必填，省侧套餐名称，省侧产品名称
								result.put("ORDER_TIME", orderTime); // 必填，套餐订购时间，套餐订购时间，即订单下发时间
								result.put("VALID_TIME", validTime); // 必填，套餐生效时间，套餐生效的时间
								result.put("END_TIME", endTime); // 必填，套餐失效时间，套餐失效的时间
								results.add(result);
							}
						}
					}
				}
			}
		}

		if (IDataUtil.isEmpty(results)) {
			String bizCode = "0016";
			String bizDesc = "查询成功，用户无对应的查询数据";
			IData result = errorRerurn(bizCode, bizDesc);
			result.put("RESULT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			result.put("RESULT_TYPE", "2");
			return result;
		}

		IData ret = new DataMap();
		ret.put("BIZ_CODE", "0000"); // 必填，消息返回码，0000代表查询成功且有数据， 0016代表查询成功，用户无对应的查询数据，其他错误码见《中国移动网状网系统接口规范-二级返回码分册》
		ret.put("BIZ_DESC", "成功"); // 非必填，错误信息描述，非0000必须返回
		ret.put("RESULT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS()); // 非必填，结果对应时间戳，此时间是由省BOSS返回给平台，代表查询返回内容的时间点。YYYYMMDDHH24MISS
		ret.put("RESULT_TYPE", "1"); // 非必填，结果类型，1-有办理记录；2-无办理记录
		ret.put("RESULT", results); // 非必填，和家固话信息，查询结果详情，当bizCode=0000必填

		return ret;
	}

	private IData errorRerurn(String bizCode, String bizDesc) throws Exception {
		IData result = new DataMap();
		result.put("BIZ_CODE", bizCode);
		result.put("BIZ_DESC", bizDesc);
		return result;
	}

	/**
	 * 11.7.110	特定主套餐订购关系查询（CIP00136）
	 *
	 * 查询用户是否存在指定主套餐的订购关系、订购类型、激活时间、生效时间、失效时间等信息。
	 * 如同一主套餐存在多条订购关系，按订购时间顺序返回多条。
	 * 如不存在订购关系，也需返回一条记录，“isOrder（是否存在订购关系）”返回“1（否）”。
	 * 注意：该接口返回当前生效和将要生效的订购关系，不需要返回历史订购关系。
	 * @param param
	 * @return
	 * @throws Exception
	 * @author zhaohj3
	 */
	public IData querySpecMainProductOrder(IData param) throws Exception {
		/**
		 * 要求：
		 * a)   可通过该接口入参中商品编码字段查询到用户是否存在花卡青春版的订购关系及订购来源、订购类型等信息。
		 * b)   实现号卡和套餐统一通过套餐编码查询及反馈。即花卡青春版号卡产品（一级能开编码9990289000001600001）通过花卡青春版基础套餐编码9990200200000410001作为入参中的商品编码进行查询及反馈。
		 *
		 * 多个一级能开产品ID对应同一个省产品ID
		 * 产品名称                 一级能开产品ID        全网资费编码
		 * 花卡青春版            9990289000001600001  999912111710019001
		 * 花卡青春版基础套餐     9990200200000410001 999912111710019001
		 * 入参校验
		 */
		String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
		String goodsType = IDataUtil.chkParam(param, "GOODS_TYPE");// 编码类型,1：省编码2：一级能开编码
		String goodsId = IDataUtil.chkParam(param, "GOODS_ID");// 当编码类型是1时，填写省公司编码。当编码类型是2时，填写一级能开编码。
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		IData data = new DataMap();
		data.put("BIZ_CODE", "0000");
		data.put("BIZ_DESC", "特定主套餐订购关系查询成功");
		data.put("RESULT_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		if (IDataUtil.isEmpty(userInfo)) {
			data.put("BIZ_CODE", "2043");
			data.put("BIZ_DESC", "用户信息不存在！");
			return data;
		}

		String userId = userInfo.getString("USER_ID");
		String offerCode = "";
		String offerType = "";
		if ("2".equals(goodsType)) { // 2：一级能开编码
			IDataset offerList = getCrmProductsInfo(goodsId);// 根据全网编码查出省内编码
			if (IDataUtil.isEmpty(offerList)) {
				data.put("BIZ_CODE", "4029");
				data.put("BIZ_DESC", "商品或产品映射关系不存在！");
				return data;
			}

            for (int i = 0; i < offerList.size(); i++) {
                IData offerInfo = offerList.getData(i);
                String elementTypeCode = offerInfo.getString("ELEMENT_TYPE_CODE");
                if ("P".equals(elementTypeCode)) {
                    offerCode = offerInfo.getString("PRODUCT_ID");
                    offerType = offerInfo.getString("ELEMENT_TYPE_CODE");
                }
            }

            if (StringUtils.isBlank(offerCode)) {
                data.put("BIZ_CODE", "4029");
                data.put("BIZ_DESC", "商品或产品映射关系不存在！");
                return data;
            }
        } else if ("1".equals(goodsType)) { // 1：省编码
			if (!isNumeric(goodsId)) {
				data.put("BIZ_CODE", "4157");
				data.put("BIZ_DESC", "商品编码格式错误,请输入数字！");
				return data;
			}
			offerCode = goodsId;
			offerType = "P";
			IData offerInfoP = UpcCall.queryOfferByOfferId(offerType, offerCode);
			if (IDataUtil.isEmpty(offerInfoP)) {
				data.put("BIZ_CODE", "4024");
				data.put("BIZ_DESC", "产品编号非法！");
				return data;
			}
		} else {
			data.put("BIZ_CODE", "2999");
			data.put("BIZ_DESC", "不支持编码类型[" + goodsType + "]的查询！");
			return data;
		}

		String goodsName = UpcCall.qryOfferNameByOfferTypeOfferCode(offerCode, offerType);
		IDataset userOrderInfo = UserProductInfoQry.qryUserMainProdInfoByUserIdProductId(userId, offerCode); // 查询当前和下月生效的产品

		// 由于花卡套卡省产品编码和套餐省产品编码不同，并且集团要求
		// b)   实现号卡和套餐统一通过套餐编码查询及反馈。即花卡青春版号卡产品（一级能开编码9990289000001600001）通过花卡青春版基础套餐编码9990200200000410001作为入参中的商品编码进行查询及反馈。
		// 如果传的套餐的产品编码，且订购关系为1，则需要再查询套卡的订购关系
		String RealGoodsId = goodsId; // 真正查询订购关系的一级能开产品编码，套餐编码对应的花卡套卡一级能开产品编码
		// PARAM_CODE 套卡能开编码，PARA_CODE1 套餐能开编码
		IDataset productConfig = CommparaInfoQry.getCommByParaAttr("CSM", "6041", "ZZZZ");
		if (IDataUtil.isEmpty(userOrderInfo)) {
			boolean confgTag = false; // 输入的goodsId是否为花卡套餐一级能开产品编码
			if (IDataUtil.isNotEmpty(productConfig)) {
				// PARAM_CODE 套卡能开编码，PARA_CODE1 套餐能开编码
				for (int i = 0; i < productConfig.size(); i++) {
					String paraCode1 = productConfig.getData(i).getString("PARA_CODE1");
					if (StringUtils.equals(goodsId, paraCode1)) {
						confgTag = true;
						RealGoodsId = productConfig.getData(i).getString("PARAM_CODE");
						break;
					}
				}
			}
			if (confgTag) {
				IDataset offerList = getCrmProductsInfo(RealGoodsId);// 根据全网编码查出省内编码
				if (IDataUtil.isNotEmpty(offerList)) {
					IData offerInfo = offerList.getData(0);
					offerCode = offerInfo.getString("PRODUCT_ID"); // 替换成套卡省产品编码，供后续查询订购关系
				}
				userOrderInfo = UserProductInfoQry.qryUserMainProdInfoByUserIdProductId(userId, offerCode); // 查询当前和下月生效的产品
			}
		}


		IDataset bizInfoList = new DatasetList();
		if (IDataUtil.isNotEmpty(userOrderInfo)) {
			String startDate = userOrderInfo.getData(0).getString("START_DATE");
			String endDate = userOrderInfo.getData(0).getString("END_DATE");
			String prodInstId = userOrderInfo.getData(0).getString("INST_ID");
			IData returnOrderInfo = new DataMap();
			returnOrderInfo.put("GOODS_ID", goodsId);
			returnOrderInfo.put("GOODS_NAME", goodsName);
			returnOrderInfo.put("IS_ORDER", "0");// 是否存在订购关系 0：是1：否
			returnOrderInfo.put("ORDER_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, startDate));
			returnOrderInfo.put("VALID_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, startDate));
			returnOrderInfo.put("EXPIRE_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, endDate));
			returnOrderInfo.put("ACTIVE_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, startDate));

			IDataset tradeProductInfoList = TradeProductInfoQry.getTradeProductByUserIdProductIdInstId(userId, offerCode, prodInstId);
			String tradeId = "";
			if (IDataUtil.isNotEmpty(tradeProductInfoList)) {
				tradeId = tradeProductInfoList.getData(0).getString("TRADE_ID");
			}

			String orderType = "2"; // 订购类型 1：新入网 2：转套餐 当isOrder为0时必填。
			String orderOrigin = "03"; // 订单来源 01：一级能力开放平台 02：省公司 03：其他平台 04：一级电渠 当isOrder为0时必填。
			for (int i = 0; i < productConfig.size(); i++) {
				// PARAM_CODE 套卡能开编码，PARA_CODE1 套餐能开编码
				String paramCode = productConfig.getData(i).getString("PARAM_CODE");
				String paraCode1 = productConfig.getData(i).getString("PARA_CODE1");
				if (StringUtils.equals(goodsId, paramCode)) {
					orderType = "1";
					orderOrigin = "04"; // 新入网时为04
					break;
				} else if (StringUtils.equals(goodsId, paraCode1)) {
					orderType = "2";
					orderOrigin = "01";// 转套餐时为01
					break;
				}
			}
			returnOrderInfo.put("ORDER_TYPE", orderType);
			returnOrderInfo.put("ORDER_ORIGIN", orderOrigin);
			String orderId = "-1"; // 一级能开订单号 当orderOrigin=01时必填（举例：统一返回主订单号，如SC05280T19042300330302，若省内存储的是子订单号（SC05280T19042300330302-01）去掉末尾“-01”后进行返回）
			if (StringUtils.equals(orderType, "1")) { // 订购类型 1：新入网 2：转套餐 当isOrder为0时必填。
				IDataset activateInfos = SyncActivateTimeQry.qrySyncActivateTimeInfoBySnProd(serialNumber, offerCode);
				if (IDataUtil.isNotEmpty(activateInfos)) {
					String activeTime = activateInfos.getData(0).getString("ACTIVATE_TIME"); // 激活时间 YYYYMMDDHH24MISS，当isOrder为0时必填。指号码的激活时间
					returnOrderInfo.put("ACTIVE_TIME", activeTime);
					if (StringUtils.equals(orderOrigin, "01")) { // 订单来源 01：一级能力开放平台
						orderId = activateInfos.getData(0).getString("RSRV_STR1");
						int end = orderId.indexOf("-") != -1 ? orderId.indexOf("-") : orderId.length();
						orderId = StringUtils.substring(orderId, 0, end); // 一级能开订单编码，示例：SC01204T18101000000010
					}
				}
			} else if (StringUtils.equals(orderType, "2")) { // 订购类型 1：新入网 2：转套餐 当isOrder为0时必填。
				if (StringUtils.equals(orderOrigin, "01")) {
					if (StringUtils.isNotBlank(tradeId)) {
						IDataset ctrmOrderProdcutInfoList = QueryListInfo.qryCtrmOrderProductInfoByTradeId(tradeId);
						if (IDataUtil.isNotEmpty(ctrmOrderProdcutInfoList)) {
							orderId = ctrmOrderProdcutInfoList.getData(0).getString("TID");
						}
					}
				}
			}
			if (StringUtils.equals(orderOrigin, "01")) {
				returnOrderInfo.put("ORDER_ID", orderId);
			}

			bizInfoList.add(returnOrderInfo);
		} else {
			IData returnOrderInfo = new DataMap();
			returnOrderInfo.put("GOODS_ID", goodsId);
			returnOrderInfo.put("GOODS_NAME", goodsName);
			returnOrderInfo.put("IS_ORDER", "1");// 0：是1：否
			bizInfoList.add(returnOrderInfo);
		}

		data.put("BIZ_INFO_LIST", bizInfoList);
		return data;
	}
	/**
	 * 一、二级能力平台接口 - 主套餐查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryMainProduct(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();

		input.put("IN_MODE_CODE", getVisit().getInModeCode());
		input.put("KIND_ID", "abilityPlat");
		input.put("IDTYPE", input.getString("SERVICE_TYPE"));
		input.put("IDVALUE", input.getString("SERIAL_NUMBER"));
		input.put("IDENT_CODE", "abilityPlat");
		input.put("BIZ_TYPE_CODE", "abilityPlat");
		input.put("TRADE_STAFF_ID", getVisit().getStaffId());
		input.put("TRADE_DEPART_ID", getVisit().getDepartId());
		input.put("TRADE_CITY_CODE", getVisit().getCityCode());
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		input.put("NO_CHECK_ABILITYPLAT", "1");
		IDataset resultList = new DatasetList();
		try {
			resultList = CSAppCall.call("SS.QueryInfoSVC.queryMainProductIBoss", input);
		} catch (Exception e) {
			String errorInfo = e.getMessage();
//			if (errorInfo.length() > 2000) {
//				errorInfo = e.getMessage().substring(0, 2000);
//			}
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", errorInfo);
			return result;
		}
		if (IDataUtil.isEmpty(resultList)) {
			result.put("BIZ_CODE", "0016");
			result.put("BIZ_DESC", "用户无对应的查询数据!");
			return result;
		} else {
			IData mainPackageInfo=new DataMap();
			IData temp = new DataMap();
			temp = resultList.getData(0);
			result.put("BIZ_CODE", "0000");
			mainPackageInfo.put("USER_BRAND", temp.getString("BRAND_CODE", "00"));
			mainPackageInfo.put("BRAND_NAME", temp.getString("BRAND_NAME",""));
			mainPackageInfo.put("CUR_PLAN_ID", temp.getString("CUR_PRODUCT_ID"));
			mainPackageInfo.put("CUR_PLAN_NAME", temp.getString("CUR_PRODUCT_NAME"));
			mainPackageInfo.put("START_TIME", temp.getString("START_TIME",""));
			mainPackageInfo.put("END_TIME", temp.getString("END_TIME", "20991231235959"));
			/*if (StringUtils.isNotEmpty(temp.getString("NEXT_PRODUCT_ID", ""))) {
				result.put("NEXT_PLAN_ID", temp.getString("NEXT_PRODUCT_ID"));
			}
			if (StringUtils.isNotEmpty(temp.getString("NEXT_PRODUCT_NAME", ""))) {
				result.put("NEXT_PLAN_NAME", temp.getString("NEXT_PRODUCT_NAME"));
			}*/
			//原来接口有问题，重新查询下一周期套餐
			IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
			if("2".equals(userInfo.getString("ACCT_TAG"))){
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "用户未激活!");
				return result;
			}
			IDataset userProducts = UserProductInfoQry.queryMainProductNow(userInfo.getString("USER_ID"));
			mainPackageInfo.put("END_TIME", userProducts.getData(0).getString("END_DATE", "20991231235959").replaceAll("-", "").replaceAll(":", "").replaceAll(" ", ""));
			mainPackageInfo.put("BRAND_NAME", InterfaceUtil.getBrandName(temp.getString("BRAND_CODE", "00")));
			IDataset userProduct = UserProductInfoQry.queryUserMainProduct(userInfo.getString("USER_ID"));

			//REQ202001130025_关于一级能力开放平台主套餐查询接口的改造通知
			String prodPrice = "0";
			prodPrice=String.valueOf(getAcctProPrice(userProduct.getData(0).getString("PRODUCT_ID"))*100);
			mainPackageInfo.put("PLAN_PRICE", prodPrice);
			//REQ202001130025_关于一级能力开放平台主套餐查询接口的改造通知
			if(!mainPackageInfo.getString("CUR_PLAN_ID").equals(userProduct.getData(0).getString("PRODUCT_ID"))){
				IData product = UProductInfoQry.qryProductByPK(userProduct.getData(0).getString("PRODUCT_ID"));
				mainPackageInfo.put("NEXT_PLAN_ID", product.getString("PRODUCT_ID"));
				mainPackageInfo.put("NEXT_PLAN_NAME", product.getString("PRODUCT_NAME"));
			}
			result.put("MAIN_PACKAGE_INFO", mainPackageInfo);
			return result;//COLLECTION_CHNN
		}
	}
	/**
	 * 一、二级能力平台接口 - 统一查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData getOrderSvc(IData input) throws Exception {
		input=new DataMap(input.toString());
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		IDataset resultList = new DatasetList();

		input.put("IDTYPE", input.getString("SERVICE_TYPE", ""));
		input.put("IDVALUE", input.getString("SERIAL_NUMBER", ""));
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

		try {
			resultList = CSAppCall.call("SS.QueryProductInfoIntfSVC.getOrderSvc", input);
		} catch (Exception e) {
			String errorInfo = e.getMessage();
//			if (errorInfo.length() > 2000) {
//				errorInfo = e.getMessage().substring(0, 2000);
//			}
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", errorInfo);
			result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			return result;
		}
		if (IDataUtil.isEmpty(resultList)) {
			result.put("BIZ_CODE", "0016");
			result.put("BIZ_DESC", "用户无对应的查询数据");
			return result;
		} else {
			IData temp = resultList.getData(0);
			result.put("BIZ_CODE", "0000");
			result.put("OPR_TIME", temp.getString("OPR_TIME"));
			if(!temp.containsKey("SUB")){
				result.put("BIZ_CODE", "0016");
				result.put("BIZ_DESC", "用户无对应的查询数据");
				return result;
			}

			//根据新接口规范重新整理数据格式
			IData tempData = new DataMap();
			IDataset prodInfo = new DatasetList();
			IDataset uniQryRecList = new DatasetList();
			prodInfo = temp.getDataset("SUB");

			IData temp1 = new DataMap();
			IDataset prodInfoList = new DatasetList();

			for (int i = 0; i < prodInfo.size(); i++) {
				tempData = new DataMap();
				tempData.put("PRODUCT_TYPE", temp.getDataset("PRODUCT_TYPE").get(i).toString());
				int tmpSize = prodInfo.getData(i).getDataset("BUNESS_TYPE").size();
				prodInfoList = new DatasetList();
				for (int j = 0; j < tmpSize; j++) {
					temp1 = new DataMap();
					temp1.put("BUNESS_TYPE", prodInfo.getData(i).getDataset("BUNESS_TYPE").get(j).toString());
					if ("01".equals(temp1.getString("BUNESS_TYPE"))) {
						temp1.put("BUNESS_CODE", prodInfo.getData(i).getDataset("BUNESS_CODE").get(j).toString());
					}
					if ("02".equals(temp1.getString("BUNESS_TYPE"))) {
						temp1.put("SP_ID", prodInfo.getData(i).getDataset("SP_ID").get(j).toString());
						temp1.put("BIZ_CODE", prodInfo.getData(i).getDataset("BIZ_CODE").get(j).toString());
					}
					temp1.put("BUNESS_NAME", prodInfo.getData(i).getDataset("BUNESS_NAME").get(j).toString());
					if ("02".equals(tempData.getString("PRODUCT_TYPE"))) {
						temp1.put("BUNESS_FREE", prodInfo.getData(i).getDataset("BUNESS_FREE").get(j).toString());
						temp1.put("FEE_TYPE", prodInfo.getData(i).getDataset("FEE_TYPE").get(j).toString());
					}
					if ("01".equals(tempData.getString("PRODUCT_TYPE")) || "04".equals(tempData.getString("PRODUCT_TYPE"))) {
						temp1.put("ORDERING_TIME", prodInfo.getData(i).getDataset("ORDERING_TIME").get(j).toString());
					}
					temp1.put("START_TIME", prodInfo.getData(i).getDataset("START_TIME").get(j).toString());
					temp1.put("DEAD_TIME", prodInfo.getData(i).getDataset("DEAD_TIME").get(j).toString());
					prodInfoList.add(temp1);
				}
				tempData.put("PROD_INFO", prodInfoList);
				uniQryRecList.add(tempData);
			}

			result.put("UNI_QRY_REC_LIST", uniQryRecList);
		}
		return result;
	}
	/**
	 * 一、二级能力平台接口 - 统一退订
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData closeAndOrderTrade(IData input) throws Exception {
		input=new DataMap(input.toString());
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		IDataset resultList = new DatasetList();

		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		if (IDataUtil.isEmpty(input.getDataset("PRODUNCT_INFO", new DatasetList()))) {
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", "用户操作产品信息PRODUNCT_INFO为空!");
			return result;
		}
		IDataset produnctInfoList = input.getDataset("PRODUNCT_INFO");
		IData produnctInfo = produnctInfoList.getData(0);
		input.put("NO_AUTH","1");
		input.put("BIZ_TYPE_CODE", "99");//新规范未传值,默认为99
		input.put("IDTYPE", input.getString("SERVICE_TYPE", ""));
		input.put("IDVALUE", input.getString("SERIAL_NUMBER", ""));
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", ""));
		input.put("PRODUNCT_INFO", produnctInfo);
		if(!"01".equals(produnctInfo.getString("OPR_CODE", ""))){
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", "操作代码OPR_CODE只允许为01-业务退订!");
			return result;
		}else {
			input.put("OPR_CODE", "02");//被调用接口中,02为业务退订
		}
		input.put("PRODUCT_TYPE", produnctInfo.getString("PRODUCT_TYPE", ""));
		input.put("PRODUCT_ID", produnctInfo.getString("PRODUCT_ID", ""));
		if ("02".equals(produnctInfo.getString("PRODUCT_TYPE", ""))) {
			input.put("SP_ID", produnctInfo.getString("SP_ID", ""));
			input.put("BIZ_CODE", produnctInfo.getString("BIZ_CODE", ""));
			input.put("OPR_NUMB", this.getOprNumFromBoss());//新规范未传值,默认为: 省domain + 省编码 + YYYYMMDDHH24MMSS + 流水号
		}
		try {
			resultList = CSAppCall.call("SS.ChangeProductIBossSVC.openAndCloseSvc", input);
			if (IDataUtil.isEmpty(resultList)) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "接口返回信息为空!");
				return result;
			} else if (IDataUtil.isEmpty(resultList.getData(0))) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "接口返回信息为空!");
				return result;
			} else {
				result.put("BIZ_CODE", "0000");
				return result;
			}
		} catch (Exception e) {
			String errorInfo = e.getMessage();
//			if (errorInfo.length() > 2000) {
//				errorInfo = e.getMessage().substring(0, 2000);
//			}
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", errorInfo);
			return result;
		}
	}
	/**
	 * 一、二级能力平台接口 - 用户综合信息查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryAllInfo(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		String sn = input.getString("SERIAL_NUMBER");
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

		// 取用户USER_ID
		IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", sn);

		if (IDataUtil.isEmpty(userInfos)) {
			result.put("BIZ_CODE", "0016");
			result.put("BIZ_DESC", "无正常用户信息!");
			return result;
		}
		String tmpUserId = userInfos.getData(0).getString("USER_ID");

		// 根据USER_ID获得数据
		IData output = queryZHInfo(sn, tmpUserId);

		result.put("BIZ_CODE", "0000");
		result.put("BIZ_DESC", "success");
		result.put("USER_INFO", output);

		return result;
	}
	/**
	 * 根据USER_ID获取综合资料
	 *
	 * @param sn
	 * @param userId
	 * @return IData
	 */
	private IData queryZHInfo(String sn, String userId) throws Exception {
		IData result = new DataMap();
		result.put("SERVICE_NUMBER", sn);
		result.put("USER_ID", userId);

		// 取用户资料
		IDataset userinfos = UserInfoQry.getUserInfoByUserId(userId);

		if (userinfos == null || userinfos.size() < 1) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "尊敬的用户，您输入的号码有误，请检查！");
			return null;
		}

		IData userinfo = (IData) (userinfos.get(0));
		String openDate = userinfo.getString("OPEN_DATE");
		openDate = openDate.replace(" ", "");
		openDate = openDate.replace("-", "");
		openDate = openDate.replace(":", "");
		result.put("USER_BEGIN", openDate);

		String regionId = userinfo.getString("EPARCHY_CODE");
		String regionName1 = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", regionId);
		if (StringUtils.isNotEmpty(regionName1)) {
			result.put("REGION_NAME", regionName1);//号码归属地
		} else {
			result.put("REGION_NAME", "AREA表未配置");//号码归属地
		}
		//yuyz
		IDataset carRegData = UserNpInfoQry.qryUserNpInfosByUserId(userId);//查询用户携转信息
		if (IDataUtil.isNotEmpty(carRegData)) {
			if (IDataUtil.isNotEmpty(carRegData.getData(0))) {
				result.put("CARRY_REGION", "携出地信息为空");//号码携出地
			} else {
				result.put("CARRY_REGION", "本用户没有携转信息");
			}
		} else {
			result.put("CARRY_REGION", "本用户没有携转信息");
		}
		result.put("CARRY_OPERATOR", "中国移动");//携出运营商，暂时写死
		//end
		String osState = userinfo.getString("USER_STATE_CODESET");
		String state = "";
		if ("0".equals(osState)){
			state = "01";//开机
		}else{
			state = "02";//已停机
		}
		result.put("OS_STATE", state);
		result.put("USER_STATUS", getUserStateParam(osState));

		String stateName = "服务状态参数表无配置";
		IDataset userSvcInfos = UserSvcInfoQry.getMainSvcUserId(userId);
		if (IDataUtil.isNotEmpty(userSvcInfos)) {
			stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode(userSvcInfos.getData(0).getString("SERVICE_ID"), osState);
		}

		result.put("OS_STATE_DESC", stateName);

		boolean is4GUser = ("x".equals(userinfo.getString("RSRV_TAG3"))) ? true : false;
		if (is4GUser) {
			result.put("NETWORK_TYPE", "0");
			result.put("VOLTE_FLAG", "0");
		} else {
			result.put("NETWORK_TYPE", "1");
			result.put("VOLTE_FLAG", "1");

		}
		return result;
	}
	/**
	 * 一、二级能力平台接口 - 客户资料查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryCustomerInfo(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		String sn = input.getString("SERIAL_NUMBER");
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

		// 取用户USER_ID
		IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", sn);

		if (IDataUtil.isEmpty(userInfos)) {
			result.put("BIZ_CODE", "0016");
			result.put("BIZ_DESC", "无正常用户信息!");
			return result;
		}

		String tmpcustId = userInfos.getData(0).getString("CUST_ID");
		String userId = userInfos.getData(0).getString("USER_ID");
		IData output = qryCustomerInfo(userId, tmpcustId);

		result.put("BIZ_CODE", "0000");
		result.put("CUSTOMER_INFO", output);
		return result;
	}

	private IData qryCustomerInfo(String userId,String custId) throws Exception {
		logger.debug("qryCustomerInfo-"+userId+"|"+custId);
		IData result = new DataMap();
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		// 个人客户、集团客户
		// 取客户资料
		if (uca.getCustPerson() == null && uca.getCustGroup() == null)
		{
			// 客户资料不存在;客户详细信息不存在
			return null;
		}
		result.put("CUSTOMER_NAME", fuzzyName(uca.getCustomer().getCustName()));
		// 个人客户
		if ("0".equals(uca.getCustomer().getCustType())){
			result.put("EMAIL",null==uca.getCustPerson().getEmail()?"":uca.getCustPerson().getEmail());
			result.put("USER_ADD", null==uca.getCustPerson().getPostAddress()?"":uca.getCustPerson().getPostAddress());//邮寄地址
			result.put("ZIP_CODE", null==uca.getCustPerson().getPostCode()?"":uca.getCustPerson().getPostCode());//邮政编码
			result.put("USER_NUM",  null==uca.getCustPerson().getPhone()?"":uca.getCustPerson().getPhone());//联系电话
			result.put("PORTABLE_NAME", null==uca.getCustPerson().getHomeAddress()?"":uca.getCustPerson().getHomeAddress());//客户归属地
		}else{
			result.put("EMAIL",null==uca.getCustGroup().getEmail()?"":uca.getCustGroup().getEmail());
			result.put("USER_ADD", "");//邮寄地址
			result.put("ZIP_CODE", null==uca.getCustGroup().getPostCode()?"":uca.getCustGroup().getPostCode());//邮政编码
			result.put("USER_NUM",  "");//联系电话
			result.put("PORTABLE_NAME","");//客户归属地
		}
		IData queryData = AcctCall.getUserCreditInfos("0",userId).getData(0);
		logger.debug("qryCustomerInfo-queryData"+queryData);
		if(IDataUtil.isNotEmpty(queryData)){
			IData transMap = new DataMap();
			transMap.put("-1", "13");//未评级客户
			transMap.put("0", "12");//0星级客户
			transMap.put("1", "11");//1星客户
			transMap.put("2", "10");//2星客户
			transMap.put("3", "09");//3星客户
			transMap.put("4", "08");//4星客户
			transMap.put("5", "07");//5星普通客户
			transMap.put("6", "06");// 5星金客户
			transMap.put("7", "05");// 5星钻客户
			result.put("STAR_LEVEL", transMap.getString(queryData.getString("CREDIT_CLASS")));
		}
		VipTradeData vips = uca.getVip();
		if (vips != null)
		{
			String CustManagerId=vips.getCustManagerId();// 客户经理编码
			IData custMgrdata = UStaffInfoQry.qryCustManagerInfoByCustManagerId(CustManagerId);
			if(IDataUtil.isNotEmpty(custMgrdata)){
				result.put("MANAGER_NAME", custMgrdata.getString("CUST_MANAGER_NAME",""));//个人客户经理姓名
				result.put("MANAGER_PHONE", custMgrdata.getString("SERIAL_NUMBER",""));//个人客户经理移动电话
			}
		}
		if(null==uca.getCustGroup()){//个人客户
			IDataset idsRelation = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(userId, "20", uca.getUser().getEparchyCode());
			if (IDataUtil.isNotEmpty(idsRelation))
			{
				String grpUserId = idsRelation.getData(0).getString("USER_ID_A");
				IData tmp = new DataMap();
				tmp.put("USER_ID", grpUserId);
				UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(tmp);
				logger.debug("qryCustomerInfo-grpUcaData"+grpUcaData);
				if ("8000".equals(grpUcaData.getProductId()))//加入了集团VPMN
				{
					result.put("GROUP_ID", grpUcaData.getCustGroup().getGroupId());//归属VPMN集团编号
					result.put("SHORT_NUM", idsRelation.getData(0).getString("SHORT_CODE"));//VPMN集团短号
				}
				String custManageId=grpUcaData.getCustGroup().getCustManagerId();
				IData custMgrdata = UStaffInfoQry.qryCustManagerInfoByCustManagerId(custManageId);
				if(IDataUtil.isNotEmpty(custMgrdata)){
					result.put("GRP_MANAGER_NAME", custMgrdata.getString("CUST_MANAGER_NAME",""));//集团客户经理姓名
					result.put("GRP_MANAGER_PHONE", custMgrdata.getString("SERIAL_NUMBER",""));//集团客户经理移动电话
				}
			}
		}else{//j集团客户
			String custManageId=uca.getCustGroup().getCustManagerId();
			IData custMgrdata = UStaffInfoQry.qryCustManagerInfoByCustManagerId(custManageId);
			if(IDataUtil.isNotEmpty(custMgrdata)){
				result.put("GRP_MANAGER_NAME", custMgrdata.getString("CUST_MANAGER_NAME",""));//集团客户经理姓名
				result.put("GRP_MANAGER_PHONE", custMgrdata.getString("SERIAL_NUMBER",""));//集团客户经理移动电话
			}
			if(StringUtils.isNotEmpty(uca.getCustGroup().getVpmnGroupId())){
				result.put("GROUP_ID", uca.getCustGroup().getVpmnGroupId());//集团客户经理移动电话
			}
			if(StringUtils.isNotEmpty(uca.getCustGroup().getVpmnNum())){
				result.put("SHORT_NUM", uca.getCustGroup().getVpmnNum());//集团客户经理移动电话
			}
		}
		String openDate=uca.getUser().getOpenDate();
		if(StringUtils.isNotBlank(openDate)){
			String netAge=SysDateMgr.daysBetween(SysDateMgr.decodeTimestamp(openDate, "yyyy-MM-dd"),SysDateMgr.decodeTimestamp(SysDateMgr.getSysDate(), "yyyy-MM-dd"))+"";
			result.put("NET_AGE", netAge);//网龄
		}
		String portableId = uca.getUser().getEparchyCode();
		String areaName = UAreaInfoQry.getAreaNameByAreaCode(portableId);
		if (StringUtils.isNotEmpty(areaName)) {
			result.put("PORTABLE_NAME", areaName);//号码归属地
		} else {
			result.put("PORTABLE_NAME", "AREA表未配置");//客户归属地
		}
		logger.debug("qryCustomerInfo-result"+result);
		return result;
	}
	/**
	 * 客户姓名处理
	 *
	 * @param inName
	 * @return
	 * @throws Exception
	 */
	private String fuzzyName(String inName) throws Exception {
		String outName = "";
		if ("".equals(inName)) {
			return outName;
		}
		inName=inName.trim();
		for (int i = 0; i < inName.length() - 1; i++) {
			outName += "x";
		}
		outName += inName.substring(inName.length() - 1);
		return outName;
	}
	/**
	 * 一、二级能力平台接口 - 用户等级信息查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryUserLevel(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		String sn = input.getString("SERIAL_NUMBER");
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

		// 取用户USER_ID
		IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", sn);

		if (IDataUtil.isEmpty(userInfos)) {
			result.put("BIZ_CODE", "0016");
			result.put("BIZ_DESC", "无正常用户信息!");
			return result;
		}
		String tmpUserId = userInfos.getData(0).getString("USER_ID");

		// 根据USER_ID获得数据
		IData output = queryLevelInfo(sn, tmpUserId);

		result.put("BIZ_CODE", "0000");
		result.put("LEVEL_INFO", output);

		return result;
	}

	private IData queryLevelInfo(String sn, String userId) throws Exception {
		IData result = new DataMap();
		UcaData ucaData = UcaDataFactory.getNormalUca(sn);
		UserTradeData userInfo = ucaData.getUser();
		VipTradeData vipInfo = ucaData.getVip();
		if (null == vipInfo) {
			result.put("VIP_LEVEL", "99");// VIP等级
			result.put("VIP_NUMBER", "-1");// VIP卡号
			result.put("VIP_DATE", "19000101");
			result.put("PLAN_NUMBER", "0");
			return result;
		} else {
			String vip_card_no = vipInfo.getVipCardNo();
			String identity_exp_date = vipInfo.getIdentityExpDate();
			String vipTypeCode = vipInfo.getVipTypeCode();// VIP类型编码
			String vipClassId = vipInfo.getVipClassId();// VIP级别标识
			String vipLevel = "99";// 99:代表Z无卡
			if ("1".equals(vipClassId)) {
				vipLevel = "03";// 贵宾卡
			} else if ("2".equals(vipClassId)) {
				vipLevel = "02";// 银卡
			} else if ("3".equals(vipClassId)) {
				vipLevel = "01";// 金卡
			} else if ("4".equals(vipClassId)) {
				vipLevel = "00";// 钻石卡
			}
			String vipEndDate = "";
			if (StringUtils.isNotEmpty(identity_exp_date) && identity_exp_date.length() >= 8) {
				vipEndDate = identity_exp_date.replace("-", "").substring(0, 8);
			}

			// 客户可使用的免费次数
			int totalFreeCount = 0;
			if ("5".equals(vipTypeCode) || ("0".equals(vipTypeCode) || "2".equals(vipTypeCode))) {
				if ("4".equals(vipClassId)) { // 钻卡，最多免费次数默认12
					totalFreeCount = 12;
				} else if ("3".equals(vipClassId)) {// 金卡，最多免费次数默认6
					totalFreeCount = 6;
				} else if ("2".equals(vipClassId)) {// 银卡，最多免费次数默认3
					totalFreeCount = 3;
				} else {
					totalFreeCount = 0;
				}
			}

			IDataset freeCountInfos = UserOtherInfoQry.queryUserOtherInfos(userInfo.getUserId(), "AREM", "AREM");
			// 已使用的免费服务次数
			IData freeCountInfo = new DataMap();
			if (IDataUtil.isNotEmpty(freeCountInfos)) {
				freeCountInfo = freeCountInfos.getData(0);
			}
			// 已用免费次数
			String ifeeCount = freeCountInfo.getString("RSRV_STR1", "0");
			int feeCount = Integer.parseInt(ifeeCount);
			// 免费次数
			int planNumber = (totalFreeCount - feeCount);
			if (planNumber < 0) {
				planNumber = 0;
			}
			result.put("VIP_LEVEL", vipLevel);// VIP等级
			result.put("VIP_NUMBER", vip_card_no);// VIP卡号
			result.put("VIP_DATE", vipEndDate);// VIP有效期
			result.put("PLAN_NUMBER", planNumber);// 机场VIP免费次数
		}
		return result;
	}
	/**
	 * 一、二级能力平台接口 - 客户星级评分详情查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData getStarScoreInfo(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		IDataset resultList = new DatasetList();

		input.put("IDTYPE", input.getString("SERVICE_TYPE"));
		input.put("IDVALUE", input.getString("SERIAL_NUMBER"));
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		input.put("OPR_NUMB", "abilityPlat");
		input.put("BIZ_TYPE_CODE", "abilityPlat");

		try {
			resultList = CSAppCall.call("SS.QueryInfosSVC.starScoreInfoQurey", input);
			if (IDataUtil.isEmpty(resultList)) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "接口返回信息为空!");
				return result;
			} else if (IDataUtil.isEmpty(resultList.getData(0))) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "接口返回信息为空!");
				return result;
			} else if (!"0000".equals(resultList.getData(0).getString("X_RESULTCODE"))) {
				result.put("BIZ_CODE", resultList.getData(0).getString("X_RESULTCODE"));
				result.put("BIZ_DESC", resultList.getData(0).getString("X_RESULTINFO"));
				return result;
			} else {
				IData temp = resultList.getData(0);
				result.put("BIZ_CODE", "0000");
				result.put("OPR_TIME", temp.getString("OPR_TIME"));

				//根据新接口规范重新整理数据格式
				IData starScoreInfo = temp.getDataset("STARSCOREINFO").getData(0);
				String dedScore = starScoreInfo.getString("DED_SCORE");
				IDataset addScoreInfo = starScoreInfo.getDataset("ADDSCOREINFO");
				IData dedScoreData = new DataMap();
				dedScoreData.put("DED_SCORE", dedScore);
				addScoreInfo.add(dedScoreData);
				starScoreInfo.put("ADDSCOREINFO", addScoreInfo);
				result.put("STARSCOREINFO", starScoreInfo);
				return result;
			}
		} catch (Exception e) {
			String errorInfo = e.getMessage();
//			if (errorInfo.length() > 2000) {
//				errorInfo = e.getMessage().substring(0, 2000);
//			}
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", errorInfo);
			return result;
		}
	}
	/**
	 * 一、二级能力平台接口 - 主套餐变更内容查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryChangeProductInfo(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "GOODS_ID");

		IData result = new DataMap();
		IDataset resultList = new DatasetList();
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		input.put("IDTYPE", input.getString("SERVICE_TYPE"));
		input.put("IDVALUE", input.getString("SERIAL_NUMBER"));
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

		IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", input.getString("SERIAL_NUMBER"));
		if (IDataUtil.isEmpty(userInfos)||"2".equals(userInfos.getData(0).getString("ACCT_TAG"))) {
			result.put("BIZ_CODE", "0016");
			result.put("BIZ_DESC", "用户无对应的查询数据!");
			return result;
		}

		IDataset rsProducts = AbilityPlatCheckRelativeQry.getCtrmProduct(input.getString("GOODS_ID"));
		if (IDataUtil.isEmpty(rsProducts)) {
			result.put("BIZ_CODE", "2999");
			result.put("BIZ_DESC", "商品的映射关系没有配置好. 请联系管理员");
			return result;
		}else {
			input.put("NEW_PRODUCT_ID", rsProducts.getData(0).getString("PRODUCT_ID"));
		}

		input.put("IN_MODE_CODE", getVisit().getInModeCode());
		input.put("KIND_ID", "abilityPlat");
		input.put("IDENT_CODE", "abilityPlat");
		input.put("BIZ_TYPE_CODE", "abilityPlat");
		input.put("TRADE_STAFF_ID", getVisit().getStaffId());
		input.put("TRADE_DEPART_ID", getVisit().getDepartId());
		input.put("TRADE_CITY_CODE", getVisit().getCityCode());
		input.put("NO_CHECK_ABILITYPLAT", "1");
		try {
			resultList = CSAppCall.call("SS.QueryInfoSVC.queryChangeProductInfoIBoss", input);
			if (IDataUtil.isEmpty(resultList)) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "用户无对应的查询数据");
				return result;
			} else if (IDataUtil.isEmpty(resultList.getData(0))) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "用户无对应的查询数据");
				return result;
			} else {
				result.put("BIZ_CODE", "0000");
				result.putAll(resultList.getData(0));
			}
		} catch (Exception e) {
			String errorInfo = e.getMessage();
//			if (errorInfo.length() > 2000) {
//				errorInfo = e.getMessage().substring(0, 2000);
//			}
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", errorInfo);
			return result;
		}
		return result;
	}
	/**
	 * 一、二级能力平台接口 - 4G套餐多终端共享成员查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData qryShareMealMember(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		String serialNumber=IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		//获得主卡用户信息
		IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
		String userID = userInfos.getData(0).getString("USER_ID");

		//查询多终端共享成员信息
		/*IDataset shareMemberList = ShareInfoQry.queryMember(userID);
		if (IDataUtil.isEmpty(shareMemberList)) {
			result.put("BIZ_CODE", "0016");
			result.put("BIZ_DESC", "用户无对应的查询数据");
			result.put("IS_SHARE", "1");
			return result;
		}*/
		IDataset userDatas=ShareInfoQry.queryMemberRelaAB(userID);
		if (IDataUtil.isEmpty(userDatas)) {
			result.put("BIZ_CODE", "0016");
			result.put("BIZ_DESC", "用户无对应的查询数据");
			result.put("IS_SHARE", "1");
			return result;
		}
		String shareId=userDatas.getData(0).getString("SHARE_ID");
		IDataset shareMemberList = queryShareMembers(shareId);
		result.put("BIZ_CODE", "0000");
		result.put("IS_SHARE", ShareInfoQry.queryDiscnt(userID).isEmpty() ? "1" : "0");
		result.put("MEMBER_LIST", transData(shareMemberList));

		return result;
	}
	private IDataset queryShareMembers(String shareId) throws Exception{
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT * FROM TF_F_USER_SHARE_RELA T WHERE T.SHARE_ID=:SHARE_ID AND T.END_DATE>SYSDATE AND T.END_DATE>T.START_DATE");
		IData param=new DataMap();
		param.put("SHARE_ID", shareId);
		return Dao.qryBySql(sql, param);
	}
	/**
	 * 数据格式转换
	 *
	 * @param srcList
	 * @return
	 * @throws Exception
	 */
	private IDataset transData(IDataset srcList) throws Exception {

		IDataset result = new DatasetList();
		if (IDataUtil.isNotEmpty(srcList)) {
			IData memberItem;
			for (int i = 0; i < srcList.size(); i++) {
				memberItem = new DataMap();
				memberItem.put("SHARE_ID_TYPE", "01");
				memberItem.put("SHARE_ID", srcList.getData(i).getString("SERIAL_NUMBER"));
				memberItem.put("MAIN_CARD_FLAG", "01".equals(srcList.getData(i).getString("ROLE_CODE"))?"1":"2");
				memberItem.put("EFFECT_TIME", SysDateMgr.decodeTimestamp(srcList.getData(i).getString("START_DATE"), SysDateMgr.PATTERN_STAND_SHORT));
				memberItem.put("EXPIRE_TIME", SysDateMgr.decodeTimestamp(srcList.getData(i).getString("END_DATE"), SysDateMgr.PATTERN_STAND_SHORT));
				result.add(memberItem);
			}
		}
		return result;
	}
	/**
	 * 一、二级能力平台接口 - 4G套餐多终端共享成员管理
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData chgShareMealMemberShip(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "OPR_CODE");
		IDataUtil.chkParam(input, "SHARE_SERVICE_TYPE");
		IDataUtil.chkParam(input, "SHARE_SERVICE_NUMBER");

		//参数转换
		input.put("TYPE", "01".equals(input.getString("OPR_CODE")) ? "ADD" : "DEL");
		input.put("SERIAL_NUMBER_A", input.getString("SHARE_SERVICE_NUMBER"));

		ShareMealBean bean = (ShareMealBean) BeanManager.createBean(ShareMealBean.class);
		IData result = new DataMap();
		try {
			result = bean.manageShareMember(input);
		} catch (Exception e) {
			//1.将接口数据转换为IBOSS需要的数据 由于这个类没有继承OrderService所以无法用filter的方式实现.
			String[] errorMessage = e.getMessage().split("●");
			if(errorMessage[0].contains("主卡当前有4个副卡共享成员,不能再新增共享成员！")){
				result.put("BIZ_CODE", "3006");
				result.put("BIZ_DESC", errorMessage[0]);
				return result;
			}else if(errorMessage[0].contains("该用户已经添加到别的共享，不可以多次添加共享！")){
				result.put("BIZ_CODE", "2000");
				result.put("BIZ_DESC", errorMessage[0]);
				return result;
			}else if(errorMessage[0].contains("此成员关系到本账期末结束，不需要再次取消！")){
				result.put("BIZ_CODE", "2001");
				result.put("BIZ_DESC", errorMessage[0]);
				return result;
			}else{
				//暂不处理
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", errorMessage[0]);
				return result;
			}
		}
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		result.put("BIZ_CODE", "0000");
		result.put("BIZ_DESC", "SUCCESS");
		return result;
	}
	/**
	 * 一、二级能力平台接口 - 服务密码凭证申请
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData certificateRequest(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "REQ_OPR_NUMB");
		IDataUtil.chkParam(input, "CHANNEL_ID");
		IDataUtil.chkParam(input, "PASSWORD");
		input.put("OPR_NUMB", input.getString("REQ_OPR_NUMB"));
		String identCodeType = IDataUtil.chkParam(input, "IDENT_CODE_TYPE");
		IData result = new DataMap();
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		
		if (!"03".equals(input.getString("IDENT_CODE_TYPE"))) {
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", "用户身份凭证类型IDENT_CODE_TYPE只允许为03!");
			return result;
		}

		IData info = new DataMap();

		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		input.put("PWD_TYPE", "01");
		IData userInfo = CustServiceHelper.checkUserInfo(input);

		String userId = userInfo.getString("USER_ID");

		input.put("USER_ID", userId);
		input.put("EPARCHY_CODE", input.getString("TRADE_EPARCHY_CODE", ""));
		input.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID", ""));
		input.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID", ""));
		input.put("UPDATE_TIME", SysDateMgr.getSysTime());

		input.put("IDENT_CODE_LEVEL", input.getString("IDENT_CODE_TYPE"));
		
		//调一级BOSS加密机解密密码
		IData inParam = new DataMap();
	    IDataset reqParams = new DatasetList();
	    IData reqParam = new DataMap();
	    reqParam.put("PARAM_NAME", "PASSWORD");
    	reqParam.put("PARAM_VALUE", input.getString("PASSWORD"));
    	reqParams.add(reqParam);
    	inParam.put("REQ_PARAM", reqParams);
    	inParam.put("KIND_ID", "BIP9A011_T9001012_0_0");// 接口标识
    	IDataset results = IBossCall.callHttpIBOSS9("IBOSS", inParam);
    	if(IDataUtil.isEmpty(results))
	    {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP9A011_T9001012_0_0！");
	    }
    	 IData tmpData = results.getData(0);
 	  	if(IDataUtil.isEmpty(tmpData)){
 	  		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口解密失败.KIND_ID=BIP9A011_T9001012_0_0！");
 	  	}
 	  	if(!"0".equals(tmpData.getString("X_RESULTCODE"))){
 	  		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口解密失败.KIND_ID=BIP9A011_T9001012_0_0！");
 	  	}
 	  	IDataset rspParams = tmpData.getDataset("RSP_PARAM");
 	  	if(IDataUtil.isNotEmpty(rspParams)){
	  		for(Iterator<Object> iterator = rspParams.iterator(); iterator.hasNext();){
	            IData rspParam = (IData) iterator.next();
	            if(StringUtils.isNotEmpty(rspParam.getString("PARAM_NAME")) && StringUtils.isNotEmpty(rspParam.getString("PARAM_VALUE"))){
	                if("PASSWORD".equals(rspParam.getString("PARAM_NAME"))){
	                	input.put("PASSWORD", rspParam.getString("PARAM_VALUE"));
	                }
	            }
	        }
	  	} 	    
 	  	input.put("CHECK_TYPE", input.getString("CHECK_TYPE","ABILITY_PLAT"));

		//01 自助，02.坐席
		String value = StaticUtil.getStaticValue("CUSTSERVICE_CHANNEL_ID", input.getString("CHANNEL_ID"));

		if ("01".equals(value) && !"1CSVCVSCK200001".equals(input.getString("CHANNEL_ID"))) {
			//校验客户是否已经绑定了手机号码
			IData bindingRelation = CustServiceHelper.checkBindingRelation(input);

			input.put("USER_ID", userId);
			IData retinfo = CustServiceHelper.checkServicePassword(input);

			if ("-1".equals(retinfo.getString("X_RESULTCODE"))) {
				int errorNum = bindingRelation.getInt("ERROR_NUMB", 0);
				errorNum = errorNum + 1;
				input.put("ERROR_NUMB", errorNum);
				CustServiceHelper.updateIdentInfo(input);
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", retinfo.getString("X_RESULTINFO"));
				return result;
			}
		} else {
			input.put("USER_ID", userId);
			IData retinfo = CustServiceHelper.checkServicePassword(input);
			if ("-1".equals(retinfo.getString("X_RESULTCODE"))) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", retinfo.getString("X_RESULTINFO"));
				return result;
			}
		}

		CustServiceHelper.buildIdentInfo(input);

		//凭证信息入库
		createCertificateInfo(input);

		IData authReturn = new DataMap();
		authReturn.put("SERVICE_NUMBER", input.getString("SERIAL_NUMBER"));
		authReturn.put("REQ_OPR_NUMB", input.getString("REQ_OPR_NUMB"));
		authReturn.put("IDENT_CODE", input.getString("IDENT_CODE"));//用户身份凭证
		authReturn.put("EFFECTIVE_TIME", "300");

		result.put("BIZ_CODE", "0000");
		result.put("AUTH_RETURN", authReturn);
		return result;
	}

	/**
	 * 凭证申请
	 *
	 * @param data
	 * @throws Exception
	 */
	public void createCertificateInfo(IData data) throws Exception {
		data.put("PARTITION_ID", SysDateMgr.getCurMonth());
		if (!Dao.insert("TF_F_USER_CERTIFICATE", data)) {
			CSAppException.apperr(CrmUserException.CRM_USER_2999);
		}
	}
	/**
	 * 一、二级能力平台接口 - 信用分查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData getUserCreditScore(IData input)throws Exception{
		IDataUtil.chkParam(input, "SERVICE_TYPE");
		String serialNumber =  IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		result.put("OPR_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo)) {
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", "用户资料异常！");
			return result;
		}

		IData inputData = new DataMap();
		inputData.put("ID_TYPE", "01");
		inputData.put("ID_VALUE",serialNumber);
		inputData.put("OPR_NUMB", this.getOprNumFromBoss());
		inputData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		inputData.put("BIZ_VERSION", "1.0.0");
		inputData.put("KIND_ID", "creditPoQuery_query_0_0");

		inputData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
		IDataset creditInfos = IBossCall.dealInvokeUrl("creditPoQuery_query_0_0",
				"IBOSSR", inputData);

		if (IDataUtil.isNotEmpty(creditInfos)) {
			if (!"0000".equals(creditInfos.getData(0).getString("X_RSPCODE",""))) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "调IBOSS查询信用分异常！");
				return result;
			} else {
				result.put("BIZ_CODE", "0000");
				result.put("CREDIT_SCORE", creditInfos.getData(0).getString("CREDIT", ""));
				return result;
			}
		} else {
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", "调IBOSS查询信用分异常！");
			return result;
		}
	}
	public String getOprNumFromBoss() throws Exception {
		StringBuilder operSeq = new StringBuilder();
		operSeq.append("COP");//省domain
		operSeq.append(this.getHomeProvCode());//省编码
		operSeq.append(SeqMgr.getPreSmsSendId()); // 取系统时间，YYYYMMDDHH24MMSS+流水号,流水号不足8位补0
		return operSeq.toString();
	}
	public String getHomeProvCode() throws Exception {
		return "290";
	}
	/**
	 * 一、二级能力平台接口 - 家庭网业务办理
	 * OPRCODE:01-增加副卡；02-删除副卡;03-组网;04-退网;05-变更短号;06-增加营销ID;07-变更家庭网主资费
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData familyNetOperate(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String oprCode = IDataUtil.chkParam(input, "OPR_CODE");

		IData result = new DataMap();
		IDataset resultList = new DatasetList();
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		oprCode=transOprCode(oprCode);//将新规范中oprcode转换成老接口中的要求
		input.put("OPR_CODE", oprCode);
		if ("-01-02-03-04-07-".indexOf(oprCode) >= 0) {
			IDataUtil.chkParam(input, "PRODUCT_ID_TYPE");
			IDataUtil.chkParam(input, "GOODS_ID");
			input.put("PRODUCT_ID", input.getString("GOODS_ID"));
			if ("1".equals(input.getString("PRODUCT_ID_TYPE"))) {
				IDataset rsProducts = AbilityPlatCheckRelativeQry.getCtrmProduct(input.getString("GOODS_ID"));
				if (IDataUtil.isEmpty(rsProducts)) {
					result.put("BIZ_CODE", "2999");
					result.put("BIZ_DESC", "商品的映射关系没有配置好. 请联系管理员");
					return result;
				} else {
					input.put("PRODUCT_ID", rsProducts.getData(0).getString("PRODUCT_ID"));
				}
			}
		}

		if ("-01-03-04-05-".indexOf(oprCode) >= 0) {
			input.put("MEMBER_PHONE_NO", input.getString("MEMBER_PHONE_NO"));
		}

		if ("05".equals(oprCode)) {
			input.put("MEMBER_SHORT_NO_OLD", input.getString("MEMBER_SHORT_NO_OLD"));
		}

		if ("-01-03-05-".indexOf(oprCode) >= 0) {
			input.put("MEMBER_SHORT_NO_NEW", input.getString("MEMBER_SHORT_NO_NEW"));
		}
		input.put("IDENT_CODE", "NOCHECK");
		input.put("BIZ_TYPE", "NOCHECK");
		try {
			resultList = CSAppCall.call("SS.FamilyOperPreSVC.Operate", input);
			if (IDataUtil.isEmpty(resultList)) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "接口返回信息为空!");
				return result;
			} else if (IDataUtil.isEmpty(resultList.getData(0))) {
				result.put("BIZ_CODE", "2998");
				result.put("BIZ_DESC", "接口返回信息为空!");
				return result;
			} else if (!"0000".equals(resultList.getData(0).getString("X_RESULTCODE"))) {
				result.put("BIZ_CODE", resultList.getData(0).getString("X_RESULTCODE"));
				result.put("BIZ_DESC", resultList.getData(0).getString("X_RESULTINFO"));
				return result;
			} else {
				result.put("BIZ_CODE", "0000");
				return result;
			}
		} catch (Exception e) {
			String errorInfo = e.getMessage();
//			if (errorInfo.length() > 2000) {
//				errorInfo = e.getMessage().substring(0, 2000);
//			}
			result.put("BIZ_CODE", "2998");
			result.put("BIZ_DESC", errorInfo);
			return result;
		}
	}
	/**
	 * 转换家庭网管理接口中的操作类型
	 * @param oprCode
	 * @return
	 * @throws Exception
	 */
	private static String transOprCode(String oprCode)throws Exception{
		if("01".equals(oprCode)){
			oprCode="03";
		}else if("02".equals(oprCode)){
			oprCode="04";
		}else if("03".equals(oprCode)){
			oprCode="01";
		}else if("04".equals(oprCode)){
			oprCode="02";
		}
		return oprCode;
	}
	/** CIP00139 全球通标签查询
     * @param input
     * @return
     * @throws Exception
     */
    public IData qryGsmTag(IData inParam) throws Exception {
        IDataUtil.chkParam(inParam, "SERVICE_TYPE");
        String serialNum = IDataUtil.chkParam(inParam, "SERIAL_NUMBER");
        IData result=new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "成功！");
        result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNum);
    	if(IDataUtil.isEmpty(userInfo))
    	{
    		result.put("BIZ_CODE", "0016");
    		result.put("BIZ_DESC", "用户无对应的查询数据");
    		return result;
    	}
    	//检查黑名单
    	IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
    	if(IDataUtil.isNotEmpty(custInfo)){
    		boolean checkBlackCust = UCustBlackInfoQry.isBlackCust(custInfo.getString("PSPT_TYPE_CODE"), custInfo.getString("PSPT_ID"));
    		if (checkBlackCust) {
    			result.put("BIZ_CODE", "3000");
        		result.put("BIZ_DESC", "黑名单用户无法查询");
        		return result;
    		}
    	}
        
        IData param = new DataMap();
        /*param.put("SERIAL_NUMBER", serialNum);
        IDataset roamTag = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GSM_BY_SN", param);*/
        //通过userid进行查询，优化全球通标签查询接口问题 
        param.put("USER_ID", userInfo.getString("USER_ID"));
        IDataset roamTag = Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BY_USER_ID", param);
        IData gBrandLevelInfo = new DataMap();
    	if(IDataUtil.isNotEmpty(roamTag))
    	{
    		String startDate = roamTag.getData(0).getString("START_DATE");
    		String inDate = roamTag.getData(0).getString("IN_DATE");
    		gBrandLevelInfo.put("GBRAND_LEVEL", "0"+roamTag.getData(0).getString("USER_CLASS"));
            gBrandLevelInfo.put("START_DATE", SysDateMgr.getDateForYYYYMMDD(startDate));
            if(startDate.equals(inDate)){
            	gBrandLevelInfo.put("GUSER_SOURCE", "02");
            }else{
            	gBrandLevelInfo.put("GUSER_SOURCE", "01");
            }
            
            gBrandLevelInfo.put("ACTIVE_TIME", inDate.split("\\.")[0].replaceAll("-", "")
            		.replaceAll(":", "")
            		.replaceAll(" ", ""));
            gBrandLevelInfo.put("ACTIVE_CHANNEL", "02");
            
    	}
    	else
    	{
            gBrandLevelInfo.put("GBRAND_LEVEL", "07");
            gBrandLevelInfo.put("START_DATE", "19000101");
            gBrandLevelInfo.put("GUSER_SOURCE", "00");
    	}
    	 result.put("GBRAND_LEVEL_INFO", gBrandLevelInfo);
        return result;
    }
    /** CIP00135 IMSI一致性校验
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkImsiIsSame(IData inParam) throws Exception {
    	String imsi = IDataUtil.chkParam(inParam, "IMSI_NUMBER");
    	String serialNum = IDataUtil.chkParam(inParam, "SERIAL_NUMBER");
    	IData result=new DataMap();
    	result.put("BIZ_CODE", "0000");
    	result.put("BIZ_DESC", "一致！");
    	
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNum);
    	if(IDataUtil.isEmpty(userInfo))
    	{
    		result.put("BIZ_CODE", "0016");
    		result.put("BIZ_DESC", "用户无对应的查询数据");
    		return result;
    	}
    	IDataset resInfos = UserResInfoQry.queryUserSimInfo(userInfo.getString("USER_ID"),"1");
    	if(IDataUtil.isEmpty(resInfos)){
    		result.put("BIZ_CODE", "2998");
        	result.put("BIZ_DESC", "不一致！");
    	}else{
    		String localImsi = resInfos.getData(0).getString("IMSI");
    		if(!imsi.equals(localImsi)){
    			result.put("BIZ_CODE", "2998");
            	result.put("BIZ_DESC", "不一致！");
    		}
    	}
    	return result;
    }
    
	public IDataset insertSycnNumberStatus(IData input) throws Exception{
        String startDate = SysDateMgr.addDays(SysDateMgr.getSysDate(), -1).replaceAll("-", "")+"000000";
		String endDate = SysDateMgr.addDays(SysDateMgr.getSysDate(), -1).replaceAll("-", "")+"235959";
		IDataset paramTime = CommparaInfoQry.getCommparaInfoByCode3A("CSM", "5555", "SYCN_NUMBER_TIME", "1", CSBizBean.getTradeEparchyCode());
		if(DataUtils.isNotEmpty(paramTime))
		{
			String paramCode = paramTime.getData(0).getString("PARA_CODE1");
			if(!"".equals(paramCode) && !paramCode.contains("YYYYMMDD")){
				String[] split = paramCode.split(",");
				startDate = split[0]+"000000";
				endDate = split[1]+"235959";
			}
		}
		IDataset responseInfos = new DatasetList();
		IData queryData = new DataMap();
        queryData.put("START_DATE", TimeUtil.format(TimeUtil.YYYYMMDDHHMMSS, startDate));
        queryData.put("END_DATE", TimeUtil.format(TimeUtil.YYYYMMDDHHMMSS, endDate));
		//查询开户数据是否存在 和固话、OAO
		IDataset OAOOrderInfos = Dao.qryByCode("TF_B_CTRM_GERLSUBORDER", "SEL_SUBORDER_BY_OPRTYPE", queryData, Route.CONN_CRM_CEN);
        if(OAOOrderInfos != null && OAOOrderInfos.size() > 0){
        	for(int i = 0;i < OAOOrderInfos.size();i++){
        		IData response = new DataMap();
        		IData orderInfo = OAOOrderInfos.getData(i);
        		try {
					IData insertData = new DataMap();
					IData param = new DataMap();
					param.put("SERIAL_NUMBER", orderInfo.getString("SERVICENO"));
					IDataset users = Dao.qryByCode("TF_F_USER", "SEL_STATE_BY_SN", param);
					String removeTag = "0";
					if(users != null && users.size() >0){
						removeTag = users.getData(0).getString("REMOVE_TAG");
					}
					String type = orderInfo.getString("NUMBER_OPRTYPE");
					insertData.put("ID", orderInfo.getString("ORDER_ID"));
					if("06".equals(type)){
						insertData.put("SERIAL_NUMBER", orderInfo.getString("A.RSRV_STR3||RSRV_STR4"));
						insertData.put("IS_MOBILE", "0");//1-移动号码，0-固定号码
					}else{
						insertData.put("SERIAL_NUMBER", orderInfo.getString("SERVICENO"));
						insertData.put("IS_MOBILE", "1");//1-移动号码，0-固定号码
					}
					insertData.put("ORDER_SOURCE", "01");//01：一级能力开放平台 02：省公司 03：其他平台 04：一级电渠
					insertData.put("ORDER_ID",orderInfo.getString("ORDER_ID"));
					String state = orderInfo.getString("STATE");
					String startTime = orderInfo.getString("CREATE_TIME");
					insertData.put("NUMBER_STAUTS_TIME", TimeUtil.format("yyyyMMddHHmmss", startTime));
					if((StringUtils.equals("SC", state) ||StringUtils.equals("AC", state)) && StringUtils.equals("0", removeTag)){
						insertData.put("NUMBER_STAUTS", "001");
					}else if((StringUtils.equals("SC", state) ||StringUtils.equals("AC", state)) && (StringUtils.equals("2", removeTag) || StringUtils.equals("4", removeTag))){
						insertData.put("NUMBER_STAUTS", "005");
					}else if(StringUtils.equals("CA", state)){
						insertData.put("NUMBER_STAUTS", "004");
					}else{
						continue;
					}
					Dao.insert("SYN_SNO_ACTIVE_STATUS",insertData,Route.CONN_CRM_CEN);
				} catch (Exception e) {
					response.put("SERIAL_NUMBER", orderInfo.getString("SERVICENO"));
					response.put("STATUS", "FAIL");
					response.put("INFO", e.getMessage());
					responseInfos.add(response);
				}
        	}
        }
        queryData.put("PRE_TYPE", "BestUseMobile");
        queryData.put("ACCEPT_STATE", "9");
        IDataset SJOrderInfos = Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_PRETYPEDATE", queryData, Route.CONN_CRM_CEN);
        if(SJOrderInfos != null && SJOrderInfos.size() > 0){
        	for(int i = 0;i < SJOrderInfos.size();i++){
        		IData response = new DataMap();
        		IData insertData = new DataMap();
        		IData orderInfo = SJOrderInfos.getData(i);
        		try {
        			IData param = new DataMap();
        			param.put("SERIAL_NUMBER", orderInfo.getString("SERIAL_NUMBER"));
        			IDataset users = Dao.qryByCode("TF_F_USER", "SEL_STATE_BY_SN", param);
        			String removeTag = "0";
        			if(users != null && users.size() >0){
        				removeTag = users.getData(0).getString("REMOVE_TAG");
        			}
        			
        			insertData.put("SERIAL_NUMBER", orderInfo.getString("SERIAL_NUMBER"));
        			insertData.put("IS_MOBILE", "1");//1-移动号码，0-固定号码
        			insertData.put("ORDER_SOURCE", "01");//01：一级能力开放平台 02：省公司 03：其他平台 04：一级电渠
        			insertData.put("ORDER_ID",orderInfo.getString("ORDER_ID"));
        			String startTime = orderInfo.getString("START_DATE");
        			insertData.put("NUMBER_STAUTS_TIME", TimeUtil.format("yyyyMMddHHmmss", startTime));
        			if(StringUtils.equals("0", removeTag)){
        				insertData.put("NUMBER_STAUTS", "001");
        			}else if(StringUtils.equals("2", removeTag) || StringUtils.equals("4", removeTag)){
        				insertData.put("NUMBER_STAUTS", "005");
        			}else{
        				continue;
        			}
        			Dao.insert("SYN_SNO_ACTIVE_STATUS",insertData,Route.CONN_CRM_CEN);
        		} catch (Exception e) {
        			response.put("SERIAL_NUMBER", orderInfo.getString("SERVICENO"));
        			response.put("STATUS", "FAIL");
        			response.put("INFO", e.getMessage());
        			responseInfos.add(response);
        		}
        	}
        }
		return responseInfos;
    }
	/**
	 * 号卡状态同步--OAO订单状态为FA
	 * 
	 */
	public IDataset sycnNumberStatusByFA(IData input) throws Exception{
        String startDate = SysDateMgr.addDays(SysDateMgr.getSysDate(), -1).replaceAll("-", "")+"000000";
		String endDate = SysDateMgr.addDays(SysDateMgr.getSysDate(), -1).replaceAll("-", "")+"235959";
		IDataset responseInfos = new DatasetList();
		//AEE传员工工号
		String staffID = input.getString("STAFF_ID");
		String beginTime=input.getString("BEGIN_TIME");
		String endTime=input.getString("END_TIME");
		
		IData queryData = new DataMap();
		
		if(StringUtils.isNotEmpty(beginTime)){
			queryData.put("START_DATE", beginTime);
		}else{
			queryData.put("START_DATE", TimeUtil.format(TimeUtil.YYYYMMDDHHMMSS, startDate));
		}
		
		if(StringUtils.isNotEmpty(endTime)){
			queryData.put("END_DATE", endTime);
		}else {
			queryData.put("END_DATE", TimeUtil.format(TimeUtil.YYYYMMDDHHMMSS, endDate));
		}
        
		IDataset OAOOrderInfos = Dao.qryByCode("TF_B_CTRM_GERLSUBORDER", "SEL_SUBORDER_BY_OPRTYPE_FA", queryData, Route.CONN_CRM_CEN);
        	for(int i = 0;i < OAOOrderInfos.size();i++){
        		IData response = new DataMap();
        		IData orderInfo = OAOOrderInfos.getData(i);
        		try {
					IData insertData = new DataMap();
					String serialNumber = orderInfo.getString("SERVICENO");
					//解除号卡预占
		            ResCall.releaseRes("2", serialNumber, "0", staffID, "1");
					insertData.put("ID", orderInfo.getString("ORDER_ID"));
					insertData.put("SERIAL_NUMBER", serialNumber);
					insertData.put("IS_MOBILE", "1");//1-移动号码，0-固定号码
					insertData.put("ORDER_SOURCE", "01");//01：一级能力开放平台 02：省公司 03：其他平台 04：一级电渠
					insertData.put("ORDER_ID",orderInfo.getString("ORDER_ID"));
					insertData.put("RSRV_STR1", "1");
					String startTime = orderInfo.getString("CREATE_TIME");
					insertData.put("NUMBER_STAUTS_TIME", TimeUtil.format("yyyyMMddHHmmss", startTime));
					insertData.put("NUMBER_STAUTS", "004");
					Dao.insert("SYN_SNO_ACTIVE_STATUS",insertData,Route.CONN_CRM_CEN);
				} catch (Exception e) {
					response.put("SERIAL_NUMBER", orderInfo.getString("SERVICENO"));
					response.put("STATUS", "FAIL");
					response.put("INFO", e.getMessage());
					responseInfos.add(response);
				}
        	}
        
       
		return responseInfos;
    }
	/**
	 * CIP00140 携号转出资格校验接口
	 * @param param  SERIAL_NUMBER
     * @return
     * @throws Exception
     * X_RESULTCODE
	 * 3059:号段属于卫星移动，不支持携号转网。3061:号段属于移动通信转售，不支持携号转网。3062:号段属于物联网，不支持携号转网。
	 * 3015:号码为单位所有，需过户至个人名下后办理携号转网。2031:申请携号转网的号码未在携出方办理真实身份信息登记。
	 * 2009:申请携号转网的号码处于XXXX（如停机/挂失）的非正常使用状态。2067:当前有X元费用尚未缴清影响携号转网办理。
	 * 2065:号码可能有未出账的国际漫游费用影响携号转网办理。2064:有在网协议XXX影响携号转网办理。
	 * 3060:您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于X年X月X日后再查询。
	 * 3064: 携号转网将影响您已办理的XXX，请在申请携号转网前做好相关业务变更。(备注：各省可将因关联业务未取消导致不能转出的原因通过此字段返回，如固移融合和合账。)
	 * */
	public IData checkNpOutMessage(IData param) throws Exception
	{
		IData result = new DataMap();
		result.put("BIZ_CODE", "2");
		
		String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");

		param.put("ABILITY_CHECK", "true");
		IDataset resultInfoLists = CSAppCall.call("SS.QueryNpMessageSVC.queryNpOutMessage", param);
		IData results = resultInfoLists.getData(0);
		IDataset resultInfo = results.getDataset("RESULTINFO");
		IDataset limitInfoList = results.getDataset("LIMITINFOLIST");

		if(DataUtils.isEmpty(resultInfo))
		{
			result.put("BIZ_CODE", "1");
		}
		else
		{
			result.put("RESULT_INFO", resultInfo);
			if(DataUtils.isNotEmpty(limitInfoList)){
				result.put("LIMIT_INFO_LIST", limitInfoList);
			}
		}
		
		return result;
	}

	/**
	 * CIP00142 号码局向查询接口
	 * @param input
	 * @return
	 * 4005	手机号码非法（不存在)
	 * 4025	手机号码归属省（市）不对
	 * 4800	号码归属组织不对（值为空）
	 * 4801	号码归属局向不对（值为空）	 *
	 *
	 * @throws Exception
	 */
	public IData selSNInfo(IData input) throws Exception {
		String msisdn = input.getString("msisdn","");
		String provCode = input.getString("provCode","");
		String cityCode = input.getString("cityCode","");

		IData rtnData = new DataMap();
		String bizCode = "0000";
		String bizDesc = "";
		String msisdnStatus = "";
		String orgName = "";
		String hlrCode = "";

        IDataset snUsedInfos = ResCall.getMphoneCodeInfoByResNo(msisdn,"1");//已用表数据
        if (IDataUtil.isEmpty(snUsedInfos) || snUsedInfos == null){
            //已用表查询不到，查询未用表 start
            IDataset snInfos = ResCall.getMphoneCodeInfoByResNo(msisdn,"0");//未用表数据
            if (IDataUtil.isEmpty(snInfos) || snInfos == null){
                bizCode = "4005";
                bizDesc = "手机号码非法（不存在)";
            }else{
                bizCode = "0000";
                bizDesc = "";
                IData snInfo = snInfos.getData(0);
                String mgmtCounty = snInfo.getString("MGMT_COUNTY","");
                if(!"HNHK".equals(mgmtCounty)){
                    bizCode = "4025";
                    bizDesc = "手机号码归属省（市）不对";
                }
                if ("".equals(mgmtCounty)){
                    bizCode = "4800";
                    bizDesc = "号码归属组织不对（值为空）";
                }
                String hlrSeg = snInfo.getString("HLR_SEG","");
                if ("".equals(hlrSeg)){
                    bizCode = "4801";
                    bizDesc = "号码归属局向不对（值为空）";
                }
                msisdnStatus = "1".equals(snInfo.getString("RES_STATE","")) ? "0" : "1";//状态：0--正常，1--异常
                provCode = "898";
                cityCode = "898";
                orgName = snInfo.getString("MGMT_COUNTY","");
                hlrCode = snInfo.getString("HLR_SEG","");
            }
            //已用表查询不到，查询未用表 end
        }else{
            //已用表号码构建状态数据 start
            bizCode = "0000";
            bizDesc = "";
            IData snUsedInfo = snUsedInfos.getData(0);
            String mgmtCounty = snUsedInfo.getString("MGMT_COUNTY","");
            if(!"HNHK".equals(mgmtCounty)){
                bizCode = "4025";
                bizDesc = "手机号码归属省（市）不对";
            }
            if ("".equals(mgmtCounty)){
                bizCode = "4800";
                bizDesc = "号码归属组织不对（值为空）";
            }
            String hlrSeg = snUsedInfo.getString("HLR_SEG","");
            if ("".equals(hlrSeg)){
                bizCode = "4801";
                bizDesc = "号码归属局向不对（值为空）";
            }
            msisdnStatus = "1";//状态：0--正常，1--异常，已用表默认状态异常
            provCode = "898";
            cityCode = "898";
            orgName = snUsedInfo.getString("MGMT_COUNTY","");
            hlrCode = snUsedInfo.getString("HLR_SEG","");
            //已用表号码构建状态数据 end
        }

		if ("0000".equals(bizCode)){
			IData hlrInfo = new DataMap();
			hlrInfo.put("msisdn",msisdn);
			hlrInfo.put("msisdnStatus",msisdnStatus);
			hlrInfo.put("provCode",provCode);
			hlrInfo.put("cityCode",cityCode);
			hlrInfo.put("orgName",orgName);
			hlrInfo.put("hlrCode",hlrCode);

			rtnData.put("hlrInfo",hlrInfo);
		}

		rtnData.put("bizCode",bizCode);
		rtnData.put("bizDesc",bizDesc);

		return rtnData;
	}

    /**
     * CIP00143 ICCID局向查询接口
     * @param input
     * @return
     * 4802	ICCID不存在
     * 4803	ICCID归属省（市）不对
     * 4805	ICCID归属组织不对（值为空）
     * 4804	ICCID归属局向不对（值为空）	 *
     *
     * @throws Exception
     */
	public IData selICCIDInfo(IData input) throws Exception {
		String iccid = input.getString("iccid","");
		String provCode = input.getString("provCode","");
		String cityCode = input.getString("cityCode","");

		IData rtnData = new DataMap();
		String bizCode = "0000";
		String bizDesc = "";
		String iccidStatus = "";
		String orgName = "";
		String hlrCode = "";
        IDataset simCardUsedInfos = ResCall.getSimCardInfo("0",iccid,"","1");//已用表数据
        if(IDataUtil.isEmpty(simCardUsedInfos) || simCardUsedInfos == null){
            //未用表数据查询 start
            IDataset simCardInfos = ResCall.getSimCardInfo("0",iccid,"","0");//未用表数据
            if (IDataUtil.isEmpty(simCardInfos) || simCardInfos == null){
                bizCode = "4802";
                bizDesc = "ICCID不存在";
            }else{
                bizCode = "0000";
                bizDesc = "";
                IData simCardInfo = simCardInfos.getData(0);
                String mgmtCounty = simCardInfo.getString("MGMT_COUNTY","");
                if(!"HNHK".equals(mgmtCounty)){
                    bizCode = "4803";
                    bizDesc = "ICCID归属省（市）不对";
                }
                if ("".equals(mgmtCounty)){
                    bizCode = "4805";
                    bizDesc = "ICCID归属组织不对（值为空）";
                }
                String hlrSeg = simCardInfo.getString("HLR_SEG","");
                if ("".equals(hlrSeg)){
                    bizCode = "4804";
                    bizDesc = "ICCID归属局向不对（值为空）";
                }
                iccidStatus = "1".equals(simCardInfo.getString("RES_STATE","")) ? "0" : "1";//状态：0--正常，1--异常
                provCode = "898";
                cityCode = "898";
                orgName = simCardInfo.getString("MGMT_COUNTY","");
                hlrCode = simCardInfo.getString("HLR_SEG","");
            }
            //未用表数据查询 end
        }else{
            bizCode = "0000";
            bizDesc = "";
            IData simCardUsedInfo = simCardUsedInfos.getData(0);
            String mgmtCounty = simCardUsedInfo.getString("MGMT_COUNTY","");
            if(!"HNHK".equals(mgmtCounty)){
                bizCode = "4803";
                bizDesc = "ICCID归属省（市）不对";
            }
            if ("".equals(mgmtCounty)){
                bizCode = "4805";
                bizDesc = "ICCID归属组织不对（值为空）";
            }
            String hlrSeg = simCardUsedInfo.getString("HLR_SEG","");
            if ("".equals(hlrSeg)){
                bizCode = "4804";
                bizDesc = "ICCID归属局向不对（值为空）";
            }
            iccidStatus = "1";//状态：0--正常，1--异常，已用表默认状态异常
            provCode = "898";
            cityCode = "898";
            orgName = simCardUsedInfo.getString("MGMT_COUNTY","");
            hlrCode = simCardUsedInfo.getString("HLR_SEG","");
        }

		if ("0000".equals(bizCode)){
			IData hlrInfo = new DataMap();
			hlrInfo.put("iccid",iccid);
			hlrInfo.put("iccidStatus",iccidStatus);
			hlrInfo.put("provCode",provCode);
			hlrInfo.put("cityCode",cityCode);
			hlrInfo.put("orgName",orgName);
			hlrInfo.put("hlrCode",hlrCode);

			rtnData.put("hlrInfo",hlrInfo);
		}

		rtnData.put("bizCode",bizCode);
		rtnData.put("bizDesc",bizDesc);

		return rtnData;
	}
	
		/**
	 * 权益状态通知接口 （CIP00144）
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData NotificationOfInterestStatus(IData input) throws Exception
	{
		IData resultData = new DataMap();
		resultData.put("bizCode", "0000");
		resultData.put("bizDesc", "Success");
		try {
			String poOrderNumber = IDataUtil.chkParam(input, "poOrderNumber");//能开订单号
			String bizType = IDataUtil.chkParam(input, "bizType");//指权益所关联业务的类型
			String goodsId = IDataUtil.chkParam(input, "goodsId");//当bizType=1或2时，指个人或家庭套餐/年包的能开商品编码；当bizType=3或4时，同权益商品编码serviceCode
			String orderId = input.getString("orderId", "");//goodsId对应的能开订单号。若订单是通过能开的，传能开订单号，若订单不走能开，不传该节点。
			String customerPhone = IDataUtil.chkParam(input, "customerPhone");//11位手机号码
			String serviceCode = IDataUtil.chkParam(input, "serviceCode");//指权益年包能开商品编码
			String serviceStatus = IDataUtil.chkParam(input, "serviceStatus");//1-用户权益领取成功；2-用户权益领取失败
			String receiveTime = "";//格式为：YYYYMMDDHH24MISS。用于上传用户实际领取权益的时间
			String effTime = "";//格式为：YYYYMMDDHH24MISS。用于上传权益的生效时间
			String expTime = "";//格式为：YYYYMMDDHH24MISS。用于上传权益失效时间。若无生效时间，不需要传该节点

			if("1".equals(serviceStatus))
			{

				HashMap<String, Object> param=new HashMap();
				String dealCond=input.getString("serviceInfo","");
				if(!"".equals(dealCond)){
					JSONObject jasonObject = JSONObject.fromObject(dealCond);
					Set<String> keySet = jasonObject.keySet();
					for (String key : keySet) {
						Object obj = jasonObject.get(key);
						param.put(key, obj);
					}
				}
				IData mapTypes=new DataMap(param);
				receiveTime = mapTypes.getString("receiveTime","");
				effTime = mapTypes.getString("effTime","");
				expTime = mapTypes.getString("expTime","");
			}

	

			IData param = new DataMap();
			param.put("PID",SeqMgr.getCtrmProId());
			param.put("POORDERNUMBER",poOrderNumber);
			param.put("BIZTYPE",bizType);
			param.put("GOODSID",goodsId);
			param.put("ORDERID",orderId);
			param.put("CUSTOMERPHONE",customerPhone);
			param.put("SERVICECODE",serviceCode);
			param.put("SERVICESTATUS",serviceStatus);
			param.put("RECEIVETIME",receiveTime);
			param.put("EFFTIME",effTime);
			param.put("EXPTIME",expTime);
			param.put("UPDATE_STAFF_ID",input.getString("TRADE_STAFF_ID",""));
			param.put("UPDATE_DEPART_ID",input.getString("TRADE_DEPART_ID",""));
			Dao.executeUpdateByCodeCode("TF_B_CTRM_ORDER_PRODUCT", "INSERT_CTRM_ORDER_PRODUCTINTEREST", param,Route.CONN_CRM_CEN);

            /*
             * 集团权益配合改造--权益状态通知 【CIP00144】
             * 权益接口： HAIN_UNHT_QYasyncWholeNotice
             * add by zhengkai5
             * */
			if("3".equals(bizType) || "4".equals(bizType))
			{
                BusinessAbilityCall.callBusinessCenterCommon("HAIN_UNHT_QYasyncWholeNotice",input);
            }


		}catch(Exception e){
			resultData.put("bizCode", "2043");
			resultData.put("bizDesc", "查询失败："+e.getMessage());
		}

		return resultData;
	}
	
	
	
	/**
	 * 订单状态反馈（CIP00081 ）
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData feedbackOrderStatus(IData input) throws Exception{
		logger.debug("=================AbilityPlatBean.feedbackOrderStatus  input"+input);
		IData resultData = new DataMap();
		resultData.put("bizCode", "0000");
		resultData.put("bizDesc", "Success");
		try {
			String orderId = IDataUtil.chkParam(input, "orderId");//订单号
			String subOrderId = IDataUtil.chkParam(input, "subOrderId");//子订单号
			String status = IDataUtil.chkParam(input, "status");//订单状态
			String statusDesc = input.getString("statusDesc","");//订单状态描述
			String dealState = "";//处理状态
			
			String serialNumber = "";//手机号码
			String discntCode = "";//优惠编码
			if("SC".equals(status)){//订购成功
				dealState = "1";
			}else{//订购失败
				dealState = "2";
			}

			//根据订单号查询权益记录
			IData param = new DataMap();
			param.put("ORDER_ID", orderId);
			param.put("SUB_ORDER_ID",subOrderId);
			IDataset dataInfos = Dao.qryByCode("TF_F_USER_RIGHTS_INTERESTS", "SEL_ALL_BY_ORDER_ID", param);
			logger.debug("=================AbilityPlatBean.feedbackOrderStatus  dataInfos"+dataInfos);
			if(IDataUtil.isNotEmpty(dataInfos)){
				serialNumber = dataInfos.getData(0).getString("SERIAL_NUMBER", "");
				discntCode = dataInfos.getData(0).getString("DISCNT_CODE", "");
				
				//更新权限表状态
				param.put("DEAL_STATE",dealState);
				param.put("RESULT_INFO",statusDesc);
				Dao.executeUpdateByCodeCode("TF_F_USER_RIGHTS_INTERESTS", "UPD_BY_ORDER_ID", param);

				if("SC".equals(status)){

					//调用产品变更接口
					IData discntParam = new DataMap();
					discntParam.put("SERIAL_NUMBER", serialNumber);
					discntParam.put("ELEMENT_ID", discntCode);
					discntParam.put("ELEMENT_TYPE_CODE", "D");
					discntParam.put("MODIFY_TAG", "0");
					discntParam.put("BOOKING_TAG", "0");// 立即生效
					discntParam.put("NO_TRADE_LIMIT", "TRUE");// 无台账限制
					discntParam.put("SKIP_RULE", "TRUE");// 跳过规则
					discntParam.put("IS_NEED_SMS", false);//不发送短信
					CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", discntParam);
				}
			}


            /*
             * 集团权益配合改造--省（接收）订单状态反馈  【CIP00081】
             * 权益接口： HAIN_UNHT_QYasyncHandleResult
             * add by zhengkai5
             * */
            BusinessAbilityCall.callBusinessCenterCommon("HAIN_UNHT_QYasyncHandleResult",input);



        }catch(Exception e){
			resultData.put("bizCode", "2998");
			resultData.put("bizDesc", "状态等信息更新失败");
			e.printStackTrace();
		}

		return resultData;
	}
	

	/**
	 * add by sundz
	 * @param param
	 * @return
	 * @throws Exception
	 */
	//开始
	public int  getAcctProPrice(String  offerCode) throws Exception {
		//主套餐
		BigDecimal newPrice = new BigDecimal("0");
		if (!StringUtils.isEmpty(offerCode)) {
			IDataset oldProductElements = UpcCall.queryOfferComRelOfferByOfferIdRelOfferType("P", offerCode, "D", "0898");
			//产品的构成是否为空
			if (IDataUtil.isNotEmpty(oldProductElements)) {
				IData params = new DataMap();
				params.put(Route.ROUTE_EPARCHY_CODE, "0898");
				params.put("PRODUCT_ID", offerCode);
				IDataset oldProductAmts= CSAppCall.call("SS.CancelWholeNetCreditPurchasesSVC.getProductAmt", params);
				if(IDataUtil.isNotEmpty(oldProductAmts)){
					String newAmt = oldProductAmts.first().getString("AMT");
					newPrice = newPrice.add(new BigDecimal(newAmt));
				}
			} else {
				//类似4G自选套餐
				//必选优惠
				IDataset offerList = null;
				//产品构成为空就查必选组优惠
				IDataset groupList = UpcCall.queryOfferGroups(offerCode);
				for (Object temp : groupList) {
					IData data1 = (IData) temp;
					String selectFlag = data1.getString("SELECT_FLAG");
					//如果是必选组
					if ("0".equals(selectFlag)) {
						offerList = UpcCall.queryGroupComRelOfferByGroupId(data1.getString("GROUP_ID"), "");
						//套餐价格
								for (Object temp3 : offerList) {
									IData data3 = (IData) temp3;
									String discntCode = data3.getString("OFFER_CODE");
									if (offerCode.equals(discntCode)) {
										IData map = new DataMap();
										map.put("PRODUCT_OFFERING_ID", discntCode);
										IDataset returnList = AcctCall.productOfferingConfig(map);
										if (IDataUtil.isNotEmpty(returnList)) {
											for (int j = 0; j < returnList.size(); j++) {
												String type = returnList.getData(j).getString("type");
												if ("Z".equals(type)) {
													String discntAmt = returnList.getData(j).getString("busiprice", "0");
													newPrice = newPrice.add(new BigDecimal(discntAmt));
												}
											}
								}
							}
							}
						}
					}
			}

		}
		return newPrice.intValue();
	}
	//结束



}
