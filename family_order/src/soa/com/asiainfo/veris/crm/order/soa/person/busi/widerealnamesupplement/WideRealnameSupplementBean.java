package com.asiainfo.veris.crm.order.soa.person.busi.widerealnamesupplement;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

public class WideRealnameSupplementBean extends CSBizBean
{
	protected static Logger log = Logger.getLogger(WideRealnameSupplementBean.class);
	
	/**
	 * 营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public IData verifyIdCardName(IData param) throws Exception
 {
		String psptId = param.getString("CERT_ID", "");// 证件号码
		String psptType = param.getString("CERT_TYPE", "").trim();// 证件类型
		String psptName = param.getString("CERT_NAME", "").trim();// 证件姓名
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0");
		
		String resultMsg = "";
		boolean breakflag = false;
//		System.out.println("CreatePersonUserBeanxxxxxxxxxxxxxxxxxverifyIdCardName2273 "+param);
	 
			
			/**
			 * 
			 *
0	本地身份证
1	外地身份证
2	户口本
3	军人身份证
A	护照
D	单位证明
E	营业执照
G	事业单位法人证书
H	港澳居民回乡证
I	台湾居民回乡证
L	社会团体法人登记证书
M	组织机构代码证
N	台湾居民来往大陆通行证
O	港澳居民来往内地通行证
P	外国人永久居留身份证
			 * 
			 * 
			 */
			
			int num = -1;//是否进行一证多名校验

			
			IDataset re = CommparaInfoQry.getCommByParaAttr("CSM", "3451", "ZZZZ");
//			System.out.println("CreatePersonUserBeanxxxxxxxxxxxxxxxxxverifyIdCardName2302 "+ re);
			
			if (IDataUtil.isNotEmpty(re)) {
				for (int i = 0; i < re.size(); i++) {
					String paramCode = re.getData(i).getString("PARAM_CODE", "").trim();
					if (paramCode.equals(psptType)) {
						//System.out.println(.println("CreatePersonUserBeanxxxxxxxxxxxxxxxxxverifyIdCardName2306 "+re.getData(i).getString("PARA_CODE2", "").trim());
						num = Integer.parseInt(re.getData(i).getString("PARA_CODE2", "-1").trim());
						break;
					}
				}
			}
//			System.out.println("CreatePersonUserBeanxxxxxxxxxxxxxxxxxverifyIdCardName2312 "+ num);
			
			if (num != -1) {

				String custId = null;
				if (param.getString("SERIAL_NUMBER") != null && param.getString("SERIAL_NUMBER").length() > 0) {
					IData userInfo = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER", "").trim());
					if (userInfo != null && userInfo.size() > 0) {
						custId = userInfo.getString("CUST_ID", "");
					}
				}
				
				IDataset ds = CustomerInfoQry.getCustInfoByPsptCustType1(psptType, psptId, psptName, custId);
//				System.out.println("CreatePersonUserBeanxxxxxxxxxxxxxxxxxverifyIdCardName2326 " + ds);
//				System.out.println("CreatePersonUserBeanxxxxxxxxxxxxxxxxxverifyIdCardName2327 " + ds.size());

				if (!ds.isEmpty() && ds.size() > 0) {
					resultMsg= "【本地校验】同一个证件号码不能对应不同的名称。";
					result.put("X_RESULTCODE", "1");
					result.put("X_RESULTMSG",resultMsg);
					breakflag = true;
				}
//				System.out.println("CreatePersonUserBeanxxxxxxxxxxxxxxxxxverifyIdCardName2331 " + result);								
				
			}
			 
			
			if(breakflag){
				return result;
			}
		
			if (psptType.equals("0") || psptType.equals("1") || psptType.equals("3") || psptType.equals("A")) {// 本地外地户口护照军人
				IDataset ds = CustomerInfoQry.getCustInfoByPsptCustType2(psptType, psptId, psptName);
				if (!ds.isEmpty() && ds.size() >= 5) {// 上限是5个
					result.put("X_RESULTCODE", "1");
				}
		} else {

			String custId = null;
			if (param.getString("SERIAL_NUMBER") != null && param.getString("SERIAL_NUMBER").length() > 0) {
				IData userInfo = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER", "").trim());
				if (userInfo != null && userInfo.size() > 0) {
					custId = userInfo.getString("CUST_ID", "");
				}
			}
			IDataset ds = CustomerInfoQry.getCustInfoByPsptCustType1(psptType, psptId, psptName, custId);
			if (!ds.isEmpty()) {
				resultMsg = "同一个证件号码不能对应不同的名称。";
				result.put("X_RESULTCODE", "1");
				result.put("X_RESULTMSG", resultMsg);
			}

		}
			return result;
	}
	
	
	/*
	 * 全网一证5号校验
	 */
	public IDataset checkGlobalMorePsptId(IData input) throws Exception
	{
		IDataset ajaxDataset = new DatasetList();
		IData ajaxData = new DataMap();
		ajaxData.put("MSG", "OK");
		ajaxData.put("CODE", "0");
		String custName = input.getString("CUST_NAME", "").trim();
		String psptId = input.getString("PSPT_ID", "").trim();
		String psptTypeCode = input.getString("PSPT_TYPE_CODE", "");
		String brandCode = input.getString("BRAND_CODE", "");
		
		// 根据证件类型查找全网开户限制数
		IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "2552", psptTypeCode, "ZZZZ");
		if (openLimitResult.isEmpty())
		{// 如果本地配置没有该业务类型的限制数量配置，则直接返回
			ajaxDataset.add(ajaxData);
			return ajaxDataset;
		}
		
		if (!"".equals(custName) && !"".equals(psptId) && !"".equals(psptTypeCode))
		{
			
			// 如果省内校验通过,进行全网一证五号校验
			// 调用全网证件号码查验接口
			IData param = new DataMap();
			param.put("CUSTOMER_NAME", custName);
			param.put("IDCARD_TYPE", psptTypeCode);
			param.put("IDCARD_NUM", psptId);
	
			// 调用全网证件号码查验接口
			NationalOpenLimitBean bean = BeanManager.createBean(NationalOpenLimitBean.class);
			IDataset callResult = new DatasetList();
			try
			{
				//callResult = bean.idCheck(param);
				IData resultData=new DataMap();
				resultData.put("X_RESULTCODE", "0");
				resultData.put("TOTAL", "0");
				callResult.add(resultData);
			} catch (Exception e)
			{
				ajaxData.put("MSG", "校验【全网一证多号】出现异常，请联系系统管理员！" + custName + "|" + psptTypeCode + "|" + psptId);
				ajaxData.put("CODE", "1");
				ajaxDataset.add(ajaxData);
				return ajaxDataset;
			}
	
			String acctTag = "";
	
			if (IDataUtil.isNotEmpty(callResult))
			{
				if ("0".equals(callResult.getData(0).getString("X_RESULTCODE")))
				{
					int openNum = callResult.getData(0).getInt("TOTAL", 0);
					if (openNum >= 0)
					{
	
						if (IDataUtil.isNotEmpty(openLimitResult))
						{
							int openLimitNum = openLimitResult.getData(0).getInt("PARA_CODE1", 0);
							int localopenLimitNum = openLimitResult.getData(0).getInt("PARA_CODE2", 0);
							String localSwitch = openLimitResult.getData(0).getString("PARA_CODE4", "");
							String localProduct = openLimitResult.getData(0).getString("PARA_CODE3", "");
							
							
							int rCount = UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);
							int rLimit = UserInfoQry.getRealNameUserLimitByPspt(custName, psptId);
							if (rCount >= rLimit)
							{
								ajaxData.put("MSG", "【本省一证多号】校验: 证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
								if (acctTag.equals("0"))
								{// 如果tf_f_user的acct_tag=0，正常状态，仅提示不限制，其他状态值，需要限制
									ajaxData.put("CODE", "2");
								} else
								{
									ajaxData.put("CODE", "1");
								}
							
							}
							if (openNum >= openLimitNum)
							{
								ajaxData.put("MSG", "【全网一证多号】校验: 证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + openLimitNum + "个】，请更换其它证件！");
								if (acctTag.equals("0"))
								{// 如果tf_f_user的acct_tag=0，正常状态，仅提示不限制，其他状态值，需要限制
									ajaxData.put("CODE", "2");
								} else
								{
									ajaxData.put("CODE", "1");
								}
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
										ajaxData.put("MSG", "【全网一证多号】校验: 证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + openLimitNum + "个】，请更换其它证件！");
										if (acctTag.equals("0"))
										{// 如果tf_f_user的acct_tag=0，正常状态，仅提示不限制，其他状态值，需要限制
											ajaxData.put("CODE", "2");
										} else
										{
											ajaxData.put("CODE", "1");
										}
									}
								}
							}
							
							if(StringUtils.isNotBlank(localSwitch) && StringUtils.isNotBlank(brandCode) && localProduct.contains(brandCode))
							{
								
								int monthCount = UserInfoQry.getRealNameUserCountByPspt3(custName, psptId, "00");	// 获取使用人证件号码已实名制开户的数量
								if (monthCount >= localopenLimitNum)
								{
									ajaxData.put("MSG", "【本省一证多号】校验: 证件号码【" + psptId + "】当月入网的数量已达到最大值【" + localopenLimitNum + "个】，请下月再办理！");
									ajaxData.put("CODE", "1");
								}
							}
							
						}
					}
				} else
				{
					if ("2998".equals(callResult.getData(0).getString("X_RESULTCODE")))
					{
						/**
						 * REQ201709250007_全网一证多名返回优化
						 * <br/>
						 * 二级错误
						 * <br/>
						 * 调用集团一证五号平台，若返回码为：23039  提示：同一证件号码下存在多个用户姓名，不限制用户办理业务
						 * @author zhuoyingzhi
						 * @date 20171017
						 */
						String x_rspcode =callResult.getData(0).getString("X_RSPCODE", "").trim();
						if ("ns1:23039".equals(x_rspcode)
							||"b:23039".equals(x_rspcode)
							|| x_rspcode.indexOf("23039")!=-1
							) {
							ajaxData.put("MSG", "同一证件号码下存在多个用户姓名，不限制用户办理业务");
							ajaxData.put("CODE", "3");
						} else {
							ajaxData.put(
									"MSG",
									"【全网一证多号】校验: "
											+ callResult.getData(0).getString(
													"X_RESULTINFO"));
							if (acctTag.equals("0")) {// 如果tf_f_user的acct_tag=0，正常状态，仅提示不限制，其他状态值，需要限制
								ajaxData.put("CODE", "2");
							} else {
								ajaxData.put("CODE", "1");
							}
						}
					}else
					{
						// 调用接口出现异常
						ajaxData.put("MSG", "校验【全网一证多号】出现异常，请联系系统管理员！");
						ajaxData.put("CODE", "1");
					}
				}
			}
			ajaxDataset.add(ajaxData);
		}
		
		return ajaxDataset;
	}
	
	/*
	 * 实名制认证营业执照
	 */
	public IData verifyEnterpriseCard(IData param) throws Exception
	{

		IData result = new DataMap();

		IDataset staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_ENTERPRISE");
		String isVerify = "";
		if (IDataUtil.isNotEmpty(staticInfo))
		{
			isVerify = staticInfo.getData(0).getString("PARA_CODE1", "").trim();// 是否进行验证,
																				// 返回1为验证,
																				// 0不验证
		}

		if (!isVerify.equals("1"))
		{
			result.put("X_RESULTCODE", "0");
			log.error("CreatePersonUserBean.verifyEnterpriseCard 2044");
			return result;
		}
		

		String psptId = param.getString("regitNo", "");
		String psptName = param.getString("enterpriseName", "");
		String legalperson = param.getString("legalperson", "");
		String termstartdate = param.getString("termstartdate", "");
		String termenddate = param.getString("termenddate", "");
		String startdate = param.getString("startdate", "");

		if (psptId.equals(""))
		{
			CSAppException.apperr(CustException.CRM_CUST_100);
		}
		if (psptName.equals(""))
		{
			CSAppException.apperr(CustException.CRM_CUST_78);
		}
		if (legalperson.equals(""))
		{
			CSAppException.apperr(CustException.CRM_CUST_1111);
		}
		if (termstartdate.equals(""))
		{
			CSAppException.apperr(CustException.CRM_CUST_1112);
		}
		if (termenddate.equals(""))
		{
			CSAppException.apperr(CustException.CRM_CUST_1113);
		}
		if (startdate.equals(""))
		{
			CSAppException.apperr(CustException.CRM_CUST_1114);
		}

		termstartdate = termstartdate.replace("年", "-").replace("月", "-").replace("日", "");
		termenddate = termenddate.replace("年", "-").replace("月", "-").replace("日", "");
		startdate = startdate.replace("年", "-").replace("月", "-").replace("日", "");

		// 海南请求源：898 用户名：&YRjGt 密码：&YOiBkfpy 秘钥：BDSUB
		// 从参数配置表中获取登录1085平台的用户名，密码。如果没配置，登录信息就为写死的默认信息。
		String loginUser = "g!ZIQ+";
		String loginPass = "aOG$GQq~A";
		IDataset results = CommparaInfoQry.getCommNetInfo("CSM", "1085", "LOGIN_INFO_HAINAN");

		if (IDataUtil.isNotEmpty(results))
		{
			loginUser = results.getData(0).getString("PARA_CODE1");
			loginPass = results.getData(0).getString("PARA_CODE2");
		}
		
		IData ibossParam = new DataMap();
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		String seqRealId = SeqMgr.getRealId();
		StringBuffer tranId = new StringBuffer().append("898").append(date).append(seqRealId);

		ibossParam.put("KIND_ID", "ENTERPRISEINFOCOMPARE_10085_0_0");
		ibossParam.put("PROV_CODE", "898");
		ibossParam.put("PRO_CODE", "898");
		ibossParam.put("TRANSACTION_ID", tranId.toString());
		ibossParam.put("USER_NAME", loginUser);
		ibossParam.put("PASSWORD", loginPass);
		ibossParam.put("ENTERPRISE_NAME", psptName);
		ibossParam.put("REGIT_NO", psptId);
		ibossParam.put("LEGAL_PERSON", legalperson);
		ibossParam.put("START_DATE", startdate);
		ibossParam.put("TERM_START_DATE", termstartdate);
		ibossParam.put("TERM_END_DATE", termenddate);
		ibossParam.put("para_code1", "1");
		IData resultIboss = null;

		try
		{
			IDataset dataset = IBossCall.callHttpIBOSS4("IBOSS", ibossParam);
			resultIboss = (dataset == null || dataset.isEmpty()) ? null : dataset.getData(0);
		} catch (Exception e)
		{
			log.error(e);
			String errStr = e.getMessage();
			CSAppException.apperr(CrmCommException.CRM_COMM_103, errStr);
		}
		if (resultIboss != null && resultIboss.size() > 0)
		{
			String return_message = "";
			if ("0000".equals(resultIboss.getString("RETURN_CODE", "")))
			{// 请求成功

				String compare_result = resultIboss.getString("COMPARE_RESULT", "").trim();// 证件
				String state = resultIboss.getString("STATE", "").trim();// 状态
				String enterprise_name_result = resultIboss.getString("ENTERPRISE_NAME_RESULT", "").trim();// 企业名称
				String legal_person_result = resultIboss.getString("LEGAL_PERSON_RESULT", "").trim();// 法人
				String start_date_result = resultIboss.getString("START_DATE_RESULT", "").trim();// 成立时间
				String term_start_date_result = resultIboss.getString("TERM_START_DATE_RESULT", "").trim();// 营业开始时间
				String term_end_date_result = resultIboss.getString("TERM_END_DATE_RESULT", "").trim();// 营业结束时间
				String isblack = resultIboss.getString("ISBLACK", "").trim();// 黑名单

				if (compare_result.equals("1"))
				{// 1：存在 2：不存在
					boolean isPass = true;
					if (!state.equals("0"))
					{// 0在营业
						isPass = false;
						if (state.equals("1"))
						{
							return_message = "证件状态：已注销;";
						}
						if (state.equals("2"))
						{
							return_message = "证件状态：不在有效期;";
						}
						if (state.equals("3"))
						{
							return_message = "证件状态：吊销;";
						}
						if (state.equals("4"))
						{
							return_message = "证件状态：注销;";
						}
						if (state.equals("5"))
						{
							return_message = "证件状态：迁出;";
						}
						if (state.equals("6"))
						{
							return_message = "证件状态：停业;";
						}
						if (state.equals("7"))
						{
							return_message = "证件状态：其他;";
						}
					}

					if (!enterprise_name_result.equals("1"))
					{ // 1 一致 2 不一致
						isPass = false;
						return_message += "客户姓名不一致;";
					}

					if (isPass)
					{
						result.put("X_RESULTCODE", "0");
						return_message +=",营业执照核查一致";
					} else
					{
						result.put("X_RESULTCODE", "1");
						return_message +=",营业执照核查不一致";
					}
				} else
				{
					return_message = "证件号码不存在!";
					result.put("X_RESULTCODE", "1");
				}
			} else
			{
				result.put("X_RESULTCODE", "1");
				return_message = resultIboss.getString("RETURN_MESSAGE", "营业执照认证失败").trim();
			}
			result.put("X_RESULTINFO", return_message);
			/**
			 * REQ201706130001_关于录入联网核验情况的需求
			 * @author zhuoyingzhi
			 * @date 20170921
			 */
			 insertCheckEnterpriseCardInfoLog(param, return_message, "核查完成");
		} else
		{
			result.put("X_RESULTCODE", "1");
			result.put("X_RESULTINFO", "营业执照认证失败");
			// CSAppException.apperr(CrmCommException.CRM_COMM_103, "营业执照认证失败");
			/**
			 * REQ201706130001_关于录入联网核验情况的需求
			 * @author zhuoyingzhi
			 * @date 20170921
			 */
			 insertCheckEnterpriseCardInfoLog(param,"营业执照认证失败", "核查完成");
		}
		return result;
	}
	
	/**
	    * REQ201706130001_关于录入联网核验情况的需求
	    * <br/>
	    * 营业执照记录日志
	    * @author zhuoyingzhi
	    * @date 20170920
	    * @param param
	    */
	   public void  insertCheckEnterpriseCardInfoLog(IData param,String checkInfo,String checkTag){
		   try {
			   IData paramInfo=new DataMap();
			   //核验手机号码
			   paramInfo.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER",""));
			   //办理业务类型
			   paramInfo.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE",""));
			   //办理渠道
			   paramInfo.put("IN_MODE_CODE", getVisit().getInModeCode());
			   //核验时间
			   paramInfo.put("CHECK_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			   //核验证件类型
			   paramInfo.put("CHECK_TYPE", "E");
			   
			   String cardNo=param.getString("regitNo","");
			   //核验证件号码
			   paramInfo.put("CHECK_PSPT_ID", cardNo);
			   //核验客户名称
			   paramInfo.put("CHECK_CUST_NAME", param.getString("enterpriseName", "").replaceAll("", ""));
			   
			   
			   //核验结果
			   paramInfo.put("CHECK_INFO", checkInfo); 
			   //是否完成核验
			   paramInfo.put("CHECK_TAG", checkTag);
			   //交易
			   paramInfo.put("CHECK_KIND", "ENTERPRISEINFOCOMPARE_10085_0_0");
			   //工号
			   paramInfo.put("IN_STAFF_ID", getVisit().getStaffId());
			   
			   //
			   paramInfo.put("RSRV_STR1", "营业执照真实性核验");
			   
			   Dao.insert("TL_B_CRM_CHECK_INFO", paramInfo);
			} catch (Exception e) {
				log.debug("---insertCheckEnterpriseCardInfoLog----"+e);
			}
	   }
	   
	   public IData verifyOrgCard(IData param) throws Exception
		{

			IData result = new DataMap();

			IDataset staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_ORG");
			String isVerify = "";
			if (IDataUtil.isNotEmpty(staticInfo))
			{
				isVerify = staticInfo.getData(0).getString("PARA_CODE1", "").trim();// 是否进行验证,
																					// 返回1为验证,
																					// 0不验证
			}
			

			if (!isVerify.equals("1"))
			{
				result.put("X_RESULTCODE", "0");
				log.error("CreatePersonUserBean.verifyOrgCard 2092");
				return result;
			}
			
			String psptId = param.getString("orgCode", "");
			String psptName = param.getString("orgName", "");
			String orgtype = param.getString("orgtype", "");
			String effectiveDate = param.getString("effectiveDate", "");
			String expirationDate = param.getString("expirationDate", "");

			if (psptId.equals(""))
			{
				CSAppException.apperr(CustException.CRM_CUST_100);
			}
			if (psptName.equals(""))
			{
				CSAppException.apperr(CustException.CRM_CUST_78);
			}
			if (orgtype.equals(""))
			{
				CSAppException.apperr(CustException.CRM_CUST_1115);
			}
			if (effectiveDate.equals(""))
			{
				CSAppException.apperr(CustException.CRM_CUST_1116);
			}
			if (expirationDate.equals(""))
			{
				CSAppException.apperr(CustException.CRM_CUST_1117);
			}

			effectiveDate = effectiveDate.replace("年", "-").replace("月", "-").replace("日", "");
			expirationDate = expirationDate.replace("年", "-").replace("月", "-").replace("日", "");

			// 海南请求源：898 用户名：&YRjGt 密码：&YOiBkfpy 秘钥：BDSUB
			// 从参数配置表中获取登录1085平台的用户名，密码。如果没配置，登录信息就为写死的默认信息。
			String loginUser = "g!ZIQ+";
			String loginPass = "aOG$GQq~A";
			IDataset results = CommparaInfoQry.getCommNetInfo("CSM", "1085", "LOGIN_INFO_HAINAN");

			if (IDataUtil.isNotEmpty(results))
			{
				loginUser = results.getData(0).getString("PARA_CODE1");
				loginPass = results.getData(0).getString("PARA_CODE2");
			}

			IData ibossParam = new DataMap();
			String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			String seqRealId = SeqMgr.getRealId();
			StringBuffer tranId = new StringBuffer().append("898").append(date).append(seqRealId);

			ibossParam.put("KIND_ID", "ORGINFOCOMPARE_10085_0_0");
			ibossParam.put("PROV_CODE", "898");
			ibossParam.put("PRO_CODE", "898");
			ibossParam.put("TRANSACTION_ID", tranId.toString());
			ibossParam.put("USER_NAME", loginUser);
			ibossParam.put("PASSWORD", loginPass);
			ibossParam.put("ORG_NAME", psptName);
			ibossParam.put("ORG_CODE", psptId);
			ibossParam.put("ORG_TYPE", orgtype);
			ibossParam.put("EFFECTIVE_DATE", effectiveDate);
			ibossParam.put("EXPIRATION_DATE", expirationDate);
			ibossParam.put("para_code1", "1");

			IData resultIboss = null;

			try
			{
				IDataset dataset = IBossCall.callHttpIBOSS4("IBOSS", ibossParam);
				resultIboss = (dataset == null || dataset.isEmpty()) ? null : dataset.getData(0);
			} catch (Exception e)
			{
				log.error(e);
				String errStr = e.getMessage();
				CSAppException.apperr(CrmCommException.CRM_COMM_103, errStr);
			}
			if (resultIboss != null && resultIboss.size() > 0)
			{
				String return_message = "";

				if ("0000".equals(resultIboss.getString("RETURN_CODE", "")))
				{// 请求成功

					String compare_result = resultIboss.getString("COMPARE_RESULT", "").trim();// 证件
//					String state = resultIboss.getString("STATE", "").trim();// 状态
					String org_name_result = resultIboss.getString("ORG_NAME_RESULT", "").trim();// 机构名称
					String org_type_result = resultIboss.getString("ORG_TYPE_RESULT", "").trim();// 机构类型
					String effective_date_result = resultIboss.getString("EFFECTIVE_DATE_RESULT", "").trim();// 生效日期
					String expiration_date_result = resultIboss.getString("EXPIRATION_DATE_RESULT", "").trim();// 失效日期
					String isblack = resultIboss.getString("ISBLACK", "").trim();// 黑名单

					if (compare_result.equals("1"))
					{// 1：存在 2：不存在
						boolean isPass = true;
						/*if (state.equals("2"))
						{// 1生效 2失效
							isPass = false;
							return_message += "该证件已失效;";
						}*/
						if (!org_name_result.equals("1"))
						{ // 1 一致 2 不一致
							isPass = false;
							return_message += "客户姓名不一致;";
						}

						if (isPass)
						{
							result.put("X_RESULTCODE", "0");
						} else
						{
							result.put("X_RESULTCODE", "1");
						}
					} else
					{
						return_message = "证件号码不存在!";
						result.put("X_RESULTCODE", "1");
					}
				} else
				{
					result.put("X_RESULTCODE", "1");
					return_message = resultIboss.getString("RETURN_MESSAGE", "组织机构代码证认证失败");
				}
				result.put("X_RESULTINFO", return_message);
			} else
			{
				result.put("X_RESULTCODE", "1");
				result.put("X_RESULTINFO", "组织机构代码证认证失败");
				// CSAppException.apperr(CrmCommException.CRM_COMM_103,
				// "组织机构代码认证失败");
			}
			return result;
		}   
	   
	   
	   /**
		 * 获取军人身份证类型
		 * 
		 * @param cycle
		 * @throws Exception
		 */
		public IData psptTypeCodePriv(IData param) throws Exception
		{

			IData result = new DataMap();
			result.put("X_RESULTCODE", "1");

			boolean hasPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_SOLDIERIDCARD");
			log.error("CreatePersonUserBean.psptTypeCodePriv 1833  " + hasPriv);
			if (hasPriv)
			{// 有权限
				result.put("X_RESULTCODE", "0");
				result.put("PSPT_TYPE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_PASSPORTTYPE", new String[]
				{ "EPARCHY_CODE" }, "PSPT_TYPE", new String[]
				{ "0892" }));
				result.put("PSPT_TYPE_CODE", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_PASSPORTTYPE", new String[]
				{ "EPARCHY_CODE" }, "PSPT_TYPE_CODE", new String[]
				{ "0892" }));
			}
			return result;
		}
	   
	   
	   
	   
	   
	   /**
		 * 实名验证用户身份证信息
		 * 
		 * @param cycle
		 * @throws Exception
		 */
		public IData verifyIdCard(IData param) throws Exception
		{

			IData result = new DataMap();
			String psptId = param.getString("CERT_ID", "");// 证件号码
			String psptName = param.getString("CERT_NAME", "").replaceAll("", "");// 姓名,中文获取，个别环境后面会带特殊字符(非空格)。如"雷金石"
			String onePsptIdMoreNameMessage = "该证件号码在系统中登记了多个名字，请客户核实确认。";
			// 默认实名认证二代身份证信息，如果认证平台无法正常工作时，可通过此开关设置是否进行认证
			log.error("CreatePersonUserBean.verifyIdCard 1820");
			IDataset staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_IDCARD");
			log.error("CreatePersonUserBean.verifyIdCard 1822");
			boolean isVerify = IDataUtil.isNotEmpty(staticInfo) && staticInfo.getData(0).getBoolean("PARA_CODE1");// 是否进行验证,
			// 返回true为验证,
			// false不验证
			log.error("CreatePersonUserBean.verifyIdCard 1824  " + isVerify);
			if (!isVerify)
			{
				result.put("X_RESULTCODE", "0");
				log.error("CreatePersonUserBean.verifyIdCard 1827");
				IDataset ds = queryOnePsptIdMoreName(psptId, psptName, param.getString("SERIAL_NUMBER", "").trim());
				if (ds != null && ds.size() > 0)
				{
					if(isPwlwOper(param.getString("SERIAL_NUMBER", "").trim(), param.getString("BUISUSERTYPE", "").trim())){
						result.put("X_RESULTCODE", "0");//取消物联网本省一证多号判断
					}else{
						result.put("X_RESULTCODE", "2");// 存在一证多号的记录
					}
						result.put("X_RESULTINFO", onePsptIdMoreNameMessage);
				}
				return result;
			}
			// 是否有不需要网上实名认证直接提交二代身份证识别仪采集信息权限,如果有权限，可跳过平台认证免认证
			boolean hasPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_IDCARDWITHOUTVERIFY");// 是否免认证
																												// 返回true为免认证
																												// ,
																												// false则需要认证
			log.error("CreatePersonUserBean.verifyIdCard 1833  " + hasPriv);
			if (hasPriv)
			{
				result.put("X_RESULTCODE", "0");
				log.error("CreatePersonUserBean.verifyIdCard 1691");
				IDataset ds = queryOnePsptIdMoreName(psptId, psptName, param.getString("SERIAL_NUMBER", "").trim());
				if (ds != null && ds.size() > 0)
				{
					if(isPwlwOper(param.getString("SERIAL_NUMBER", "").trim(), param.getString("BUISUSERTYPE", "").trim())){
						result.put("X_RESULTCODE", "0");//取消物联网本省一证多号判断
					}else{
					result.put("X_RESULTCODE", "2");// 存在一证多号的记录
					}
					result.put("X_RESULTINFO", onePsptIdMoreNameMessage);
				}
				return result;
			}

			String psptTypeCode = param.getString("CERT_TYPE", "").trim();
			if (psptTypeCode.length() > 0 && psptTypeCode.equals("3"))
			{// 军人身份证只校验一证多名，不进行校验在线公司校验
				result.put("X_RESULTCODE", "0");
				log.error("CreatePersonUserBean.verifyIdCard 1702");
				IDataset ds = queryOnePsptIdMoreName(psptId, psptName, param.getString("SERIAL_NUMBER", "").trim());
				if (ds != null && ds.size() > 0)
				{
					if(isPwlwOper(param.getString("SERIAL_NUMBER", "").trim(), param.getString("BUISUSERTYPE", "").trim())){
						result.put("X_RESULTCODE", "0");//取消物联网本省一证多号判断
					}else{
					result.put("X_RESULTCODE", "2");// 存在一证多号的记录
					}
					result.put("X_RESULTINFO", onePsptIdMoreNameMessage);
				}
				return result;
			}
			log.error("CreatePersonUserBean.verifyIdCard 1957");
			
			String psptAddr = param.getString("CERT_ADDR", "").replaceAll("", "");// 地址
			String psptSex = param.getString("CERT_SEX", "").replaceAll("", "");// 性别
			// psptName = new String(psptName.getBytes(),"UTF-8");
			// 证件号码不能为空
			if (psptId.equals(""))
			{
				CSAppException.apperr(CustException.CRM_CUST_100);
			}
			// 客户姓名不能为空
			if (psptName.equals(""))
			{
				CSAppException.apperr(CustException.CRM_CUST_78);
			}
			// 证件地址不能为空
			// if(psptAddr.equals("")){
			// CSAppException.apperr(CustException.CRM_CUST_1007);
			// }
			String gender = "";
			if ("男".equals(psptSex))
			{
				gender = "1";
			} else if ("女".equals(psptSex))
			{
				gender = "0";
			} else
			{
				// CSAppException.apperr(CustException.CRM_CUST_17);
				gender = psptSex;
			}

			// 海南请求源：898 用户名：&YRjGt 密码：&YOiBkfpy 秘钥：BDSUB
			// 从参数配置表中获取登录1085平台的用户名，密码。如果没配置，登录信息就为写死的默认信息。
			String loginUser = "g!ZIQ+";
			String loginPass = "aOG$GQq~A";
			IDataset results = CommparaInfoQry.getCommNetInfo("CSM", "1085", "LOGIN_INFO_HAINAN");

			if (IDataUtil.isNotEmpty(results))
			{
				loginUser = results.getData(0).getString("PARA_CODE1");
				loginPass = results.getData(0).getString("PARA_CODE2");
			}
			IData ibossParam = new DataMap();
			String mphone = "";
			String channel = "";
			IData inparams = new DataMap();
			inparams.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
			IDataset staffInfoset = StaffInfoQry.getStaffInfo(inparams);
			if (staffInfoset != null && staffInfoset.size() > 0)
			{
				mphone = staffInfoset.getData(0).getString("SERIAL_NUMBER");
				channel = staffInfoset.getData(0).getString("DEPART_ID");
			}
			String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			String seqRealId = SeqMgr.getRealId();
			StringBuffer tranId = new StringBuffer().append("898").append(date).append(seqRealId);
			ibossParam.put("KIND_ID", "REALITYVERIFY_10085_0_0");

			ibossParam.put("PROV_CODE", "898");
			ibossParam.put("TRANSACTION_ID", tranId.toString());//
			ibossParam.put("USER_NAME", loginUser);
			ibossParam.put("PASSWORD", loginPass);
			ibossParam.put("BILL_ID", param.getString("SERIAL_NUMBER", "00000000000"));
			ibossParam.put("CHANNEL_ID", channel);
			ibossParam.put("BUSI_TYPE", param.getString("BUSI_TYPE", "7"));
			ibossParam.put("CUST_NAME", psptName);
			ibossParam.put("CUST_CERT_NO", psptId);
			ibossParam.put("CUST_CERT_ADDR", psptAddr);
			ibossParam.put("OPER_CODE", CSBizBean.getVisit().getStaffId());
			ibossParam.put("GENDER", gender);
			ibossParam.put("NATION", param.getString("CERT_NATIONAL"));
			ibossParam.put("BIRTHDAY", param.getString("CERT_BIRTHDAY"));
			ibossParam.put("ISSUING_AUTHORITY", param.getString("CERT_DEPART"));
			ibossParam.put("CERT_VALIDDATE", param.getString("CERT_VALIDDATE"));
			ibossParam.put("CERT_EXPDATE", param.getString("CERT_EXPDATE"));
			ibossParam.put("OPER_TEL", mphone);
			ibossParam.put("SOURCE_TYPE", "1");// 开户对应的身份证信息来源类型 :二代证读卡器
			ibossParam.put("para_code1", "1");
			IData resultIboss = null;

			try
			{
				log.error("CreatePersonUserBean.verifyIdCard 2040"+ibossParam);
				IDataset dataset = IBossCall.callHttpIBOSS4("IBOSS", ibossParam);
				resultIboss = (dataset == null || dataset.isEmpty()) ? null : dataset.getData(0);
			} catch (Exception e)
			{
				log.error(e);
				String errStr = e.getMessage();
				CSAppException.apperr(CrmCommException.CRM_COMM_103, errStr);
			}
			if (resultIboss != null && resultIboss.size() > 0)
			{
				String return_message = resultIboss.getString("RETURN_MESSAGE", "");
				if ("0000".equals(resultIboss.getString("RETURN_CODE", "")))
				{
					if (resultIboss.getString("VERIFY_RESULT", "").equals("0"))
					{
						IDataset ds = queryOnePsptIdMoreName(psptId, psptName, param.getString("SERIAL_NUMBER", "").trim());
						if (ds == null || ds.size() == 0)
						{
							result.put("X_RESULTCODE", "0");
							return_message="核验成功";
						} else
						{
							result.put("X_RESULTCODE", "2");// 存在一证多号的记录
							return_message = onePsptIdMoreNameMessage;
						}
					} else
					{
						result.put("X_RESULTCODE", "1");
						return_message = "该证件在公安部系统校验不通过，建议用户到当地派出所核对自己的证件信息。若是军人请用军官证或军人身份证开户。";
					}

					result.put("X_RESULTINFO", return_message);
					
					/**
					 * REQ201706130001_关于录入联网核验情况的需求
					 * @author zhuoyingzhi
					 * @date 20170920
					 */
					insertCheckEFormInfoLog(param, return_message, "核验完成");

				} else
				{
					result.put("X_RESULTCODE", "1");
					result.put("X_RESULTINFO", "身份证实名认证失败");
					// CSAppException.apperr(CrmCommException.CRM_COMM_103,
					// "身份证实名认证失败");
					/**
					 * REQ201706130001_关于录入联网核验情况的需求
					 * @author zhuoyingzhi
					 * @date 20170920
					 */
					insertCheckEFormInfoLog(param, "身份证实名认证失败", "核验未完成");				
				}
			}
			return result;
		}
	
		private IDataset queryOnePsptIdMoreName(String psptId, String psptName, String serial_number) throws Exception
		{
			IDataset ds = null;
			if (serial_number != null && serial_number.length() > 0)
			{// 如果有手机号码
				ds = CustPersonInfoQry.qryPerInfoByPsptId_2(psptId, psptName, serial_number);// 从tf_f_cust_person表查开户人信息
			} else
			{
				ds = CustPersonInfoQry.qryPerInfoByPsptId_1(psptId, psptName);// 从tf_f_cust_person表查开户人信息
			}

			if (ds == null || ds.size() == 0)
			{
				ds = CustPersonInfoQry.qryUserPsptByPsptIdName(psptId, psptName);// 从表TF_F_USER_PSPT查询使用人、经办人、责任人信息
			}
			return ds;
		}
		
		 /**
		    * REQ201706130001_关于录入联网核验情况的需求
		    * <br/>
		    * 扫描按钮记录日志
		    * @author zhuoyingzhi
		    * @date 20170920
		    * @param param
		    */
		   public void  insertCheckEFormInfoLog(IData param,String checkInfo,String checkTag){
			   try {
				   IData paramInfo=new DataMap();
				   //核验手机号码
				   paramInfo.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER",""));
				   //办理业务类型
				   paramInfo.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE",""));
				   //办理渠道
				   paramInfo.put("IN_MODE_CODE", getVisit().getInModeCode());
				   //核验时间
				   paramInfo.put("CHECK_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				   //核验证件类型
				   paramInfo.put("CHECK_TYPE", param.getString("CERT_TYPE",""));
				   
				   String cardNo=param.getString("CERT_ID","");
				   //核验证件号码
				   paramInfo.put("CHECK_PSPT_ID", cardNo);
				   //核验客户名称
				   paramInfo.put("CHECK_CUST_NAME", param.getString("CERT_NAME", "").replaceAll("", ""));
				   
				   
				   //核验结果
				   paramInfo.put("CHECK_INFO", checkInfo); 
				   //是否完成核验
				   paramInfo.put("CHECK_TAG", checkTag);
				   //交易
				   paramInfo.put("CHECK_KIND", "REALITYVERIFY_10085_0_0");
				   //工号
				   paramInfo.put("IN_STAFF_ID", getVisit().getStaffId());
				   
				   //
				   paramInfo.put("RSRV_STR1", "身份证真实性核验");
				   
				   Dao.insert("TL_B_CRM_CHECK_INFO", paramInfo);
				} catch (Exception e) {
					log.debug("---insertCheckEFormInfoLog----"+e);
				}
		   } 
		   
		   public boolean isPwlwOper(String serialnumber, String buisusertype) throws Exception {
				boolean returnflag = false;
					
					if ((buisusertype.trim().length() > 0 && buisusertype.trim().equals("PWLW"))) {// 因批量物联网开户界面操作时，还没有手机号码，所以特别加了该表示区分
						returnflag = true;
			}
					if (serialnumber.length() > 0) {// 通过手机号码校验是否是物联网号码
						IData userProductInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialnumber);
						if (userProductInfo != null) {
							String brandCode = userProductInfo.getString("BRAND_CODE", "").trim();
							if (brandCode.equals("PWLW")) {
								returnflag = true;
							}
						} else {
							IDataset mphoneds = ResCall.getMphonecodeInfo(serialnumber, "0");// 0为查空闲 , 1为查已用																					
							if (mphoneds != null && mphoneds.size() > 0) {
								String res_sku_id = mphoneds.first().getString("RES_SKU_ID", "").trim();
								if (res_sku_id.equals("01001") || res_sku_id.equals("01002") || res_sku_id.equals("01003") || res_sku_id.equals("01004") || res_sku_id.equals("01005")
										|| res_sku_id.equals("01006") || res_sku_id.equals("01007") || res_sku_id.equals("01008")) {
									returnflag = true;
								}
							}
						}
					}
				return returnflag;
			}
		
}