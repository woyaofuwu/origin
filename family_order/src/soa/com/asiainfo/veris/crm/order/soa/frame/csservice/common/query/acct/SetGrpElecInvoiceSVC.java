package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct;

import java.util.HashMap;
import java.util.Map;

import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class SetGrpElecInvoiceSVC extends CSBizService
{

	public static Logger logger = Logger.getLogger(SetGrpElecInvoiceSVC.class);

	private static final long serialVersionUID = 1L;
	public static final String MON_ELEC_INVOICE = "0"; // 月结电子发票

	public static final String CASH_ELEC_INVOICE = "1"; // 现金电子发票

	public static final String BUSINESS_ELEC_INVOICE = "2"; // 营业业务电子发票

	// public static final String NOTE_ELEC_INVOICE = "3"; //纸质发票

	/**
	 * 查询用户信息并展示
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryUserInfo(IData input) throws Exception
	{

		return SetGrpElecInvoiceBean.queryUserInfo(input);
	}

	/**
	 * 根据userId查询电子发票设置信息
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset qryEPostInfoByUserID(IData input) throws Exception
	{

		return SetGrpElecInvoiceBean.qryEPostInfoByUserID(input);

	}

	// /**
	// * 根据custId查询电子发票设置信息
	// *
	// * @param input
	// * @return
	// * @throws Exception
	// */
	// public IDataset qryByCustID(IData input) throws Exception {
	//		
	// return SetGrpElecInvoiceBean.qryByCustID(input);
	//		
	// }
	/**
	 * 校验手机号
	 * 
	 * @param input
	 * @throws Exception
	 */
	public void checkPostNumber(IData input) throws Exception
	{

		String PostNumber = input.getString("SERIAL_NUMBER");
		if (StringUtils.isNotEmpty(PostNumber))
		{
			IDataset ret = UserInfoQry.checkUserIsMoblie(PostNumber);
			if (IDataUtil.isEmpty(ret))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该推送号码为非移动用户！");
			}
			IDataset users = UserInfoQry.checkUserIsCancel(PostNumber);
			if (IDataUtil.isNotEmpty(users))
			{
				String removeTag = users.getData(0).getString("REMOVE_TAG");
				if (!removeTag.equals("0"))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码对应的用户已销户！");
				}
			} else
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码对应的用户资料不存在！");
			}

		}

	}

	public IDataset onSubmitBaseTrade(IData data) throws Exception
	{

		String operation = data.getString("OPRATION");
		if ("ByCustId".equals(operation))
		{
			data.put("ACCT_TYPE", "2");
			data.put("CUST_TAG", data.getString("CUST_ID"));
			IData inparam = new DataMap();
			String custId = data.getString("CUST_ID");
			inparam.put("CUST_ID", custId);
			IDataset UserInfos = SetGrpElecInvoiceBean.queryUserInfo(inparam);

			for (int i = 0; i < UserInfos.size(); i++)// 遍历用户
			{
				String modifyTag = UserInfos.getData(i).getString("MODIFY_TAG");
				if ("0".equals(modifyTag))
				{
					data.put("NEW_FLAG", "0");
				} else
				{
					data.put("NEW_FLAG", "1");
				}
				data.put("USER_ID", UserInfos.getData(i).getString("USER_ID"));
				setInfoOperation(data);
			}
		} else
		{
			data.put("ACCT_TYPE", "2");
			data.put("CUST_TAG", data.getString("USER_ID"));
			data.put("NEW_FLAG", data.getString("NEW_FLAG"));
			setInfoOperation(data);
		}
		IDataset results = new DatasetList();
		IData result = new DataMap();
		result.put("RESULT_CODE", "0000");
		results.add(result);
		return results;
	}

	public void setInfoOperation(IData input) throws Exception
	{
		String userId = input.getString("USER_ID");
		String eparchyCode = input.getString("EPARCHY_CODE");
		String newFlag = input.getString("NEW_FLAG");
		IDataset AcctInfo = SetGrpElecInvoiceBean.qryAcctInfoByUserId(userId);

		if (IDataUtil.isEmpty(AcctInfo)) {
			IData payRelaInfo = UcaInfoQry.qryLastPayRelaByUserId(userId);
			AcctInfo = IDataUtil.idToIds(payRelaInfo);
		}
		if (IDataUtil.isNotEmpty(AcctInfo))
		{
			String acctId = AcctInfo.getData(0).getString("ACCT_ID");
			input.put("ACCT_ID", acctId);
			// 获取页面数据
			String acctType = input.getString("ACCT_TYPE");
			String custTag = input.getString("CUST_TAG");
			String postDate = input.getString("postinfo_POST_DATE_MON", "");
			String postTypeMon = input.getString("POST_TYPE_MON");// 月结
			String postTypeCash = input.getString("POST_TYPE_CASH");// 现金
			String postTypeBusi = input.getString("POST_TYPE_BUSI");// 营业

			// 查询电子发票信息表
			IDataset results = SetGrpElecInvoiceBean.QueryGrpEpostinfoByUserId(input);
			if (IDataUtil.isEmpty(results))
			{// 如果 tf_f_grpacct_epostinfo里无数据，则为新增
				if (MON_ELEC_INVOICE.equals(postTypeMon))
				{// 月结新增
					input.put("POST_TAG", "0");
					input.put("POST_DATE_A", postDate);
					input.put("TYPE", "0");
					addMonElecInvoice(input);
				}
				if (CASH_ELEC_INVOICE.equals(postTypeCash))
				{// 现金新增
					input.put("POST_TAG", "1");
					input.put("TYPE", "1");
					addMonElecInvoice(input);
				}
				if (BUSINESS_ELEC_INVOICE.equals(postTypeBusi))
				{// 营业新增
					input.put("POST_TAG", "2");
					input.put("TYPE", "2");
					addMonElecInvoice(input);
				}

			} else
			{// 否则表里有数据 ，分四种情况
				for (int i = 0; i < results.size(); i++)
				{
					// 获取表里数据
					IData forminfo = results.getData(i);
					forminfo.put("ACCT_TYPE", acctType);// 客户类型
					forminfo.put("CUST_TAG", custTag);// 客户标识
					forminfo.put("EPARCHY_CODE", eparchyCode);
					forminfo.put("ACCT_ID", acctId);
					forminfo.put("NEW_FLAG", newFlag);
					String postType_T = results.getData(i).getString("POST_TAG");
					String pushWay_T = results.getData(i).getString("POST_CHANNEL");
					String postDate_T = results.getData(i).getString("POST_DATE");
					String emailnumber_T = results.getData(i).getString("POST_ADR");
					input.put("EMAIL_T", emailnumber_T);
					input.put("PUSH_WAY", pushWay_T);
					input.put("postDate_T", postDate_T);
					if (MON_ELEC_INVOICE.equals(postType_T))
					{// 表里有月结
						if (MON_ELEC_INVOICE.equals(postTypeMon))
						{// 提交时有月结
							input.put("POST_TAG", "0");
							input.put("POST_DATE_U", postDate);
							input.put("TYPE", "0");
							updateMonElecInvoice(input);// 修改月结
						} else
						{// 提交时无月结
							forminfo.put("POST_TAG", "0");
							forminfo.put("TYPE", "0");
							deleteMonElecInvoice(forminfo);// 删除月结
						}
					}
					if (CASH_ELEC_INVOICE.equals(postType_T))
					{// 或表里有现金
						if (CASH_ELEC_INVOICE.equals(postTypeCash))
						{// 提交时有现金
							input.put("POST_TAG", "1");
							input.put("TYPE", "1");
							input.put("POST_DATE_U", null);
							updateMonElecInvoice(input);// 修改现金
						} else
						{// 提交时无现金
							forminfo.put("POST_TAG", "1");
							forminfo.put("TYPE", "1");
							deleteMonElecInvoice(forminfo);// 删除现金
						}
					}

					if (BUSINESS_ELEC_INVOICE.equals(postType_T))
					{// 表里有营业
						if (StringUtils.isEmpty(postTypeBusi))
						{// 提交时无营业
							forminfo.put("POST_TAG", "2");
							forminfo.put("TYPE", "2");
							deleteMonElecInvoice(forminfo);// 营业删除
						} else
						{
							input.put("POST_TAG", "2");
							input.put("TYPE", "2");
							input.put("POST_DATE_U", null);
							updateMonElecInvoice(input);// 营业修改
						}
					}
					boolean insertMon = false;
					boolean insertCash = false;
					boolean insertBusi = false;
					IData inparam = new DataMap();
					inparam.put("USER_ID", userId);
					inparam.put("POST_TAG", "0");// 月结
					if (IDataUtil.isEmpty(SetGrpElecInvoiceBean.qryEPostInfoByTag(inparam)))
					{ // 表里无月结
						insertMon = true;
					}
					inparam.put("POST_TAG", "1");// 现金
					if (IDataUtil.isEmpty(SetGrpElecInvoiceBean.qryEPostInfoByTag(inparam)))
					{ // 表里无现金
						insertCash = true;
					}
					inparam.put("POST_TAG", "2");// 营业
					if (IDataUtil.isEmpty(SetGrpElecInvoiceBean.qryEPostInfoByTag(inparam)))
					{ // 表里无营业
						insertBusi = true;
					}
					if (insertMon && (MON_ELEC_INVOICE.equals(postTypeMon)))
					{// 表里无月结,提交时有月结

						input.put("POST_TAG", "0");
						input.put("POST_DATE_A", postDate);
						input.put("TYPE", "0");
						addMonElecInvoice(input); // 月结新增
					}
					if (insertCash && CASH_ELEC_INVOICE.equals(postTypeCash))
					{// 表里无现金,提交时有现金
						input.put("POST_TAG", "1");
						input.put("TYPE", "1");
						addMonElecInvoice(input);// 现金新增
					}
					if (insertBusi && StringUtils.isNotEmpty(postTypeBusi))
					{// 表里无营业，提交时有

						input.put("POST_TAG", "2");
						input.put("TYPE", "2");
						addMonElecInvoice(input); // 营业新增
					}
				}

			}
		}

	}

	// 电子发票新增
	public static void addMonElecInvoice(IData input) throws Exception
	{
		// 获取页面数据
		String newFlag = input.getString("NEW_FLAG");
		String acctType = input.getString("ACCT_TYPE", "");
		String custTag = input.getString("CUST_TAG", "");
		String custId = input.getString("CUST_ID", "");
		String userId = input.getString("USER_ID", "");
		String acctId = input.getString("ACCT_ID", "");
		String postTag = input.getString("POST_TAG", "");
		String type = input.getString("TYPE", "");
		String pushWay = input.getString("POST_CHANNEL", "");
		String pushWay_T = "";
		String phonenumber = input.getString("postinfo_RECEIVE_NUMBER", "");
		String emailnumber = input.getString("postinfo_POST_ADR", "");
		String emailnumber_T = "";
		String postDate = input.getString("POST_DATE_A", "");
		String postDate_T = "";
		String flag = "0";
		String eparchycode = input.getString("EPARCHY_CODE", "");
		IData Tableinfo = new DataMap();// 存放表数据
		Tableinfo.put("CUST_ID", input.getString("CUST_ID"));
		Tableinfo.put("USER_ID", input.getString("USER_ID"));
		Tableinfo.put("POST_TAG", postTag);
		Tableinfo.put("POST_DATE", postDate);
		Tableinfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
		// String partId =
		// userId.length()>4?userId.substring(userId.length()-4,userId.length()):"";
		String partId = StrUtil.getPartition4ById(userId);
		Tableinfo.put("PARTITION_ID", partId);
		if ("02".equals(pushWay))
		{// 如推送方式为02，则为手机
			pushWay = "0";
			Tableinfo.put("POST_CHANNEL", pushWay);
			Tableinfo.put("RECEIVE_NUMBER", phonenumber);
		} else if ("12".equals(pushWay))
		{
			pushWay = "1";
			Tableinfo.put("POST_CHANNEL", pushWay);
			Tableinfo.put("POST_ADR", emailnumber);

			/*
			 * if(!"".equals(emailnumber)) {
			 * if(!"139.com".equals(emailnumber.substring
			 * (emailnumber.indexOf("@")+1))){
			 * CSAppException.apperr(CrmCommException.CRM_COMM_103,"请填写139邮箱！");
			 * }
			 * 
			 * }
			 */

		} else if ("02,12".equals(pushWay))
		{
			pushWay = "2";
			Tableinfo.put("POST_CHANNEL", pushWay);
			Tableinfo.put("RECEIVE_NUMBER", phonenumber);
			Tableinfo.put("POST_ADR", emailnumber);

			/*
			 * if (!"".equals(emailnumber)) { if
			 * (!"139.com".equals(emailnumber.substring(emailnumber
			 * .indexOf("@") + 1))) {
			 * CSAppException.apperr(CrmCommException.CRM_COMM_103,
			 * "请填写139邮箱！"); }
			 * 
			 * }
			 */
		}
		// 传数据给IBOSS
		IDataset IBossResults = sendDataToIBOSS(custTag, emailnumber_T, emailnumber, postDate, postDate_T, eparchycode);
		if (IDataUtil.isNotEmpty(IBossResults))
		{// 调了IBOSS接口的情况
			if ("0000".equals(IBossResults.getData(0).getString("RETURN_CODE")))
			{// 返回成功
				IDataset AcctResults = sendDataToAcct(custId, acctId, userId, acctType, flag, type, postDate, pushWay, phonenumber, emailnumber, eparchycode, newFlag);
				if (IDataUtil.isNotEmpty(AcctResults) && "0000".equals(AcctResults.getData(0).getString("RESULT_CODE")))
				{
					Dao.insert("tf_f_grpacct_epostinfo", Tableinfo, Route.CONN_CRM_CG);
				} else if (IDataUtil.isEmpty(AcctResults))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "帐务TAM_ELECNOTE_SETINFO_SYNC接口返回为空！");
				} else
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, AcctResults.getData(0).getString("RESULT_INFO"));
				}
			} else
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, IBossResults.getData(0).getString("RETURN_MESSAGE"));
			}

		} else
		{// 没调IBOSS接口的情况
			// 传数据给账管
			IDataset AcctResults = sendDataToAcct(custId, acctId, userId, acctType, flag, type, postDate, pushWay, phonenumber, emailnumber, eparchycode, newFlag);
			if (IDataUtil.isNotEmpty(AcctResults) && "0000".equals(AcctResults.getData(0).getString("RESULT_CODE")))
			{
				Dao.insert("tf_f_grpacct_epostinfo", Tableinfo, Route.CONN_CRM_CG);
			} else if (IDataUtil.isEmpty(AcctResults))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "帐务TAM_ELECNOTE_SETINFO_SYNC接口返回为空！");
			} else
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, AcctResults.getData(0).getString("RESULT_INFO"));
			}
		}
	}

	// 电子发票修改
	public static void updateMonElecInvoice(IData input) throws Exception
	{
		// 获取页面数据
		String newFlag = input.getString("NEW_FLAG");
		String flag = "1";
		String eparchycode = input.getString("EPARCHY_CODE", "");
		String custTag = input.getString("CUST_TAG", "");
		String acctType = input.getString("ACCT_TYPE", "");
		String custId = input.getString("CUST_ID", "");
		String acctId = input.getString("ACCT_ID", "");
		String userId = input.getString("USER_ID", "");
		String postTag = input.getString("POST_TAG", "");
		String type = input.getString("TYPE", "");
		String pushWay = input.getString("POST_CHANNEL", "");
		String pushWay_T = input.getString("PUSH_WAY", "");
		if ("02".equals(pushWay))
		{
			pushWay = "0";
		} else if ("12".equals(pushWay))
		{
			pushWay = "1";
		} else if ("02,12".equals(pushWay))
		{
			pushWay = "2";
		}
		String phonenumber = input.getString("postinfo_RECEIVE_NUMBER", "");
		String emailnumber = input.getString("postinfo_POST_ADR", "");
		/*
		 * if (!"".equals(emailnumber)) { if
		 * (!"139.com".equals(emailnumber.substring(emailnumber .indexOf("@") +
		 * 1))) { CSAppException.apperr(CrmCommException.CRM_COMM_103,
		 * "请填写139邮箱！"); }
		 * 
		 * }
		 */
		String emailnumber_T = input.getString("EMAIL_T", "");
		String postDate = input.getString("POST_DATE_U", "");
		String postDate_T = input.getString("postDate_T", "");
		IData Tableinfo = new DataMap();// 存放表数据
		// 传数据到表里
		Tableinfo.put("CUST_ID", input.getString("CUST_ID"));
		Tableinfo.put("USER_ID", input.getString("USER_ID"));
		Tableinfo.put("POST_TAG", postTag);
		Tableinfo.put("POST_DATE", postDate);
		Tableinfo.put("POST_CHANNEL", pushWay);
		Tableinfo.put("RECEIVE_NUMBER", phonenumber);
		Tableinfo.put("POST_ADR", emailnumber);
		Tableinfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
		// String partId =
		// userId.length()>4?userId.substring(userId.length()-4,userId.length()):"";
		String partId = StrUtil.getPartition4ById(userId);
		Tableinfo.put("PARTITION_ID", partId);
		// 传数据给IBOSS
		IDataset IBossResults = sendDataToIBOSS(custTag, emailnumber_T, emailnumber, postDate, postDate_T, eparchycode);
		if (IDataUtil.isNotEmpty(IBossResults))
		{// 调了IBOSS接口的情况
			if ("0000".equals(IBossResults.getData(0).getString("RETURN_CODE")))
			{
				// 传数据给账管
				IDataset AcctResults = sendDataToAcct(custId, acctId, userId, acctType, flag, type, postDate, pushWay, phonenumber, emailnumber, eparchycode, newFlag);
				if (IDataUtil.isNotEmpty(AcctResults) && "0000".equals(AcctResults.getData(0).getString("RESULT_CODE")))
				{
					Dao.update("tf_f_grpacct_epostinfo", Tableinfo, new String[]
					{ "USER_ID", "POST_TAG" }, Route.CONN_CRM_CG);
				} else if (IDataUtil.isEmpty(AcctResults))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "帐务TAM_ELECNOTE_SETINFO_SYNC接口返回为空！");
				} else
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, AcctResults.getData(0).getString("RESULT_INFO"));
				}
			} else
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, IBossResults.getData(0).getString("RETURN_MESSAGE"));
			}
		} else
		{// 没调IBOSS接口的情况
			// 传数据给账管
			IDataset AcctResults = sendDataToAcct(custId, acctId, userId, acctType, flag, type, postDate, pushWay, phonenumber, emailnumber, eparchycode, newFlag);
			if (IDataUtil.isNotEmpty(AcctResults) && "0000".equals(AcctResults.getData(0).getString("RESULT_CODE")))
			{
				Dao.update("tf_f_grpacct_epostinfo", Tableinfo, new String[]
				{ "USER_ID", "POST_TAG" }, Route.CONN_CRM_CG);
			} else if (IDataUtil.isEmpty(AcctResults))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "帐务TAM_ELECNOTE_SETINFO_SYNC接口返回为空！");
			} else
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, AcctResults.getData(0).getString("RESULT_INFO"));
			}
		}
	}

	// 电子发票删除
	public static void deleteMonElecInvoice(IData forminfo) throws Exception
	{
		String newFlag = forminfo.getString("NEW_FLAG");
		String flag = "2";
		String eparchycode = forminfo.getString("EPARCHY_CODE", "");
		String acctType = forminfo.getString("ACCT_TYPE", "");
		String custId = forminfo.getString("CUST_ID", "");
		String acctId = forminfo.getString("ACCT_ID", "");
		String userId = forminfo.getString("USER_ID", "");
		String custTag = forminfo.getString("CUST_TAG", "");
		String postTag_T = forminfo.getString("POST_TAG", "");
		String type = forminfo.getString("TYPE", "");
		String postDate_T = forminfo.getString("POST_DATE", "");
		String postDate = "";
		String pushWay = "";
		String pushWay_T = forminfo.getString("POST_CHANNEL", "");
		String phoneNumber_T = forminfo.getString("RECEIVE_NUMBER", "");
		String emailNumber_T = forminfo.getString("POST_ADR", "");
		String emailNumber = "";
		IData Tableinfo = new DataMap();// 存放表数据
		Tableinfo.put("USER_ID", forminfo.getString("USER_ID", ""));
		Tableinfo.put("POST_TAG", postTag_T);
		// 传数据给IBOSS
		IDataset IBossResults = sendDataToIBOSS(custTag, emailNumber, emailNumber_T, postDate, postDate_T, eparchycode);
		if (IDataUtil.isNotEmpty(IBossResults))
		{// 调了IBOSS接口的情况
			if ("0000".equals(IBossResults.getData(0).getString("RETURN_CODE")))
			{
				// 传数据给账管
				IDataset AcctResults = sendDataToAcct(custId, acctId, userId, acctType, flag, type, postDate_T, pushWay_T, phoneNumber_T, emailNumber_T, eparchycode, newFlag);
				if (IDataUtil.isNotEmpty(AcctResults) && "0000".equals(AcctResults.getData(0).getString("RESULT_CODE")))
				{
					Dao.delete("tf_f_grpacct_epostinfo", Tableinfo, new String[]
					{ "USER_ID", "POST_TAG" }, Route.CONN_CRM_CG);
				} else if (IDataUtil.isEmpty(AcctResults))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "帐务TAM_ELECNOTE_SETINFO_SYNC接口返回为空！");
				} else
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, AcctResults.getData(0).getString("RESULT_INFO"));
				}
			} else
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, IBossResults.getData(0).getString("RETURN_MESSAGE"));
			}

		} else
		{
			// 传数据给账管
			IDataset AcctResults = sendDataToAcct(custId, acctId, userId, acctType, flag, type, postDate_T, pushWay_T, phoneNumber_T, emailNumber_T, eparchycode, newFlag);
			if (IDataUtil.isNotEmpty(AcctResults) && "0000".equals(AcctResults.getData(0).getString("RESULT_CODE")))
			{
				Dao.delete("tf_f_grpacct_epostinfo", Tableinfo, new String[]
				{ "USER_ID", "POST_TAG" }, Route.CONN_CRM_CG);
			} else if (IDataUtil.isEmpty(AcctResults))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "帐务TAM_ELECNOTE_SETINFO_SYNC接口返回为空！");
			} else
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, AcctResults.getData(0).getString("RESULT_INFO"));
			}

		}
	}

	/**
	 * 传数据到账管
	 * 
	 * ACCT_TYPE 账户类型 TYPE 业务类型 PUSH_CHANNEL 推送渠道 SMS_NUMBER 短厅推送号码 EMAIL_NUMBER
	 * 邮箱推送号码 PUSH_DATE 定制月结推送时间 FLAG 标识
	 * 
	 * @return
	 */
	public static IDataset sendDataToAcct(String custId, String acctId, String userId, String acctType, String flag, String type, String postDate, String pushWay, String phonenumber, String emailnumber, String eparchycode, String newFlag) throws Exception
	{
		IData Acctinfo = new DataMap();// 存放账管数据
		Acctinfo.put("ACCT_TYPE", "2");
		Acctinfo.put("CUST_ID", custId);
		Acctinfo.put("ACCT_ID", acctId);
		Acctinfo.put("USER_ID", userId);
		Acctinfo.put("FLAG", flag);
		Acctinfo.put("TYPE", type);
		Acctinfo.put("PUSH_CHANNEL", pushWay);
		Acctinfo.put("SMS_NUMBER", phonenumber);
		Acctinfo.put("EMAIL_NUMBER", emailnumber);
		Acctinfo.put("PUSH_DATE", postDate);
		Acctinfo.put("EPARCHY_CODE", eparchycode);
		// Acctinfo.put("PARTITION_ID", userId.substring(12));
		Acctinfo.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
		Acctinfo.put("NEW_FLAG", newFlag);
		IDataset AcctResults = CSAppCall.call("TAM_ELECNOTE_SETINFO_SYNC", Acctinfo);
		return AcctResults;
	}

	/**
	 * 传数据到 IBOSS KHBS 客户标识 KHLX 客户类型 KHYXDZ 邮箱地址 KHGSD 客户归属地 KHFQQD 请求发起渠道
	 * KHQQSJ 请求发起时间 TSZQ 推送周期 TSRQ 推送日期 JKLX 接口类型 SFBS 省份标识 YWLX 业务类型 YWLSH
	 * 业务流水号
	 **/
	public static IDataset sendDataToIBOSS(String custTag, String postEmail_T, String postEmail, String postDate, String postDate_T, String epachyCode) throws Exception
	{
		IDataset results = new DatasetList();
		Map<String, Object> customerInfo = new HashMap<String, Object>();
		customerInfo.put("KHBS", custTag);
		customerInfo.put("KHLX", "2");
		customerInfo.put("KHGSD", epachyCode);
		customerInfo.put("KHFQQD", "1");// 自有营业厅

		Map<String, Object> invoiceInfo = new HashMap<String, Object>();
		invoiceInfo.put("TSZQ", "1");
		invoiceInfo.put("SFBS", "898");// getVisit().getProvinceCode()
		invoiceInfo.put("YWLX", "1");

		// 全部屏蔽掉调用IBSOSS接口
		Map<String, Map<String, Object>> requestJsonMap = new HashMap<String, Map<String, Object>>();
		IData ibossData = new DataMap();
		JSONObject jSONObject = null;
		String contentJson = null;
		ibossData.put("KIND_ID", "EPostMail_BOSS_0_0");
		customerInfo.put("KHQQSJ", System.currentTimeMillis());
		invoiceInfo.put("YWLSH", "DZFP_" + invoiceInfo.get("SFBS") + "_*_" + customerInfo.get("KHBS") + "_*_" + customerInfo.get("KHQQSJ"));
		if (StringUtils.isNotEmpty(postEmail_T))
		{// 之前有邮箱
			if (StringUtils.isNotEmpty(postEmail) && !postEmail_T.equals(postEmail))
			{// 现在有且不同（修改邮箱）

				customerInfo.put("KHYXDZ", postEmail);
				invoiceInfo.put("JKLX", "1");
				requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
				requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
				jSONObject = JSONObject.fromObject(requestJsonMap);
				contentJson = jSONObject.toString();
				ibossData.put("CONTENT_XML", contentJson);
				try
				{
					results = null;// IBossCall.callHttpKIBOSS("IBOSS",
									// ibossData);
				} catch (Exception e)
				{
					logger.error("139邮箱设置失败:" + e.getMessage());
				}

			} else if (StringUtils.isEmpty(postEmail))
			{// 现在无邮箱（取消邮箱）
				invoiceInfo.put("JKLX", "2");
				requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
				requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
				jSONObject = JSONObject.fromObject(requestJsonMap);
				contentJson = jSONObject.toString();
				ibossData.put("CONTENT_XML", contentJson);
				try
				{
					results = null;// IBossCall.callHttpKIBOSS("IBOSS",
									// ibossData);
				} catch (Exception e)
				{
					logger.error("139邮箱设置失败:" + e.getMessage());
				}
			}
		} else if (StringUtils.isEmpty(postEmail_T) && StringUtils.isNotEmpty(postEmail))
		{// 之前没有邮箱现在有（新增邮箱）
			customerInfo.put("KHYXDZ", postEmail);
			invoiceInfo.put("JKLX", "1");
			requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
			requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
			jSONObject = JSONObject.fromObject(requestJsonMap);
			contentJson = jSONObject.toString();
			ibossData.put("CONTENT_XML", contentJson);
			try
			{
				results = null;// IBossCall.callHttpKIBOSS("IBOSS", ibossData);
			} catch (Exception e)
			{
				logger.error("139邮箱设置失败:" + e.getMessage());
			}
		}
		customerInfo.put("KHQQSJ", System.currentTimeMillis());
		invoiceInfo.put("YWLSH", "DZFP_" + invoiceInfo.get("SFBS") + "_*_" + customerInfo.get("KHBS") + "_*_" + customerInfo.get("KHQQSJ"));
		if (StringUtils.isEmpty(postDate_T))
		{// 之前没有定期推送
			if (StringUtils.isNotEmpty(postDate) && StringUtils.isNotEmpty(postEmail))
			{// 现在有，且配置了常用邮箱
				invoiceInfo.put("TSRQ", postDate);
				invoiceInfo.put("JKLX", "3");
				requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
				requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
				jSONObject = JSONObject.fromObject(requestJsonMap);
				contentJson = jSONObject.toString();
				ibossData.put("CONTENT_XML", contentJson);
				try
				{
					results = null;// IBossCall.callHttpKIBOSS("IBOSS",
									// ibossData);
				} catch (Exception e)
				{
					logger.error("139邮箱设置失败:" + e.getMessage());
				}
			}
		} else
		{// 之前有定期推送
			if (StringUtils.isNotEmpty(postEmail_T))
			{// 之前有邮箱
				if (StringUtils.isEmpty(postEmail))
				{// 现在无
					invoiceInfo.put("JKLX", "4");
					requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
					requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
					jSONObject = JSONObject.fromObject(requestJsonMap);
					contentJson = jSONObject.toString();
					ibossData.put("CONTENT_XML", contentJson);
					try
					{
						results = null;// IBossCall.callHttpKIBOSS("IBOSS",
										// ibossData);
					} catch (Exception e)
					{
						logger.error("139邮箱设置失败:" + e.getMessage());
					}
				} else if (StringUtils.isNotEmpty(postEmail) && !postDate_T.equals(postDate))
				{// 现在有邮箱，但推送日期变化
					invoiceInfo.put("TSRQ", postDate);
					invoiceInfo.put("JKLX", "3");
					requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
					requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
					jSONObject = JSONObject.fromObject(requestJsonMap);
					contentJson = jSONObject.toString();
					ibossData.put("CONTENT_XML", contentJson);
					try
					{
						results = null;// IBossCall.callHttpKIBOSS("IBOSS",
										// ibossData);
					} catch (Exception e)
					{
						logger.error("139邮箱设置失败:" + e.getMessage());
					}
				} else if (StringUtils.isEmpty(postDate))
				{// 取消定期推送
					invoiceInfo.put("JKLX", "4");
					requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
					requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
					jSONObject = JSONObject.fromObject(requestJsonMap);
					contentJson = jSONObject.toString();
					ibossData.put("CONTENT_XML", contentJson);
					try
					{
						results = null;// IBossCall.callHttpKIBOSS("IBOSS",
										// ibossData);
					} catch (Exception e)
					{
						logger.error("139邮箱设置失败:" + e.getMessage());
					}
				}

			} else
			{// 之前没有有邮箱
				if (StringUtils.isNotEmpty(postEmail))
				{// 现在有（配置定期推送，须配置过邮箱，之前判断过，传过1接口，故这里只传3接口）
					invoiceInfo.put("TSRQ", postDate);
					invoiceInfo.put("JKLX", "3");
					requestJsonMap.put("DZFPYJTSPZ_KHXX", customerInfo);
					requestJsonMap.put("DZFPYJTSPZ_PZXX", invoiceInfo);
					jSONObject = JSONObject.fromObject(requestJsonMap);
					contentJson = jSONObject.toString();
					ibossData.put("CONTENT_XML", contentJson);
					try
					{
						results = null;// IBossCall.callHttpKIBOSS("IBOSS",
										// ibossData);
					} catch (Exception e)
					{
						logger.error("139邮箱设置失败:" + e.getMessage());
					}
				}
			}
		}

		return results;
	}

	// 获取集团电子发票设置
	public static IDataset getERecptGrpConf(IData input) throws Exception
	{

		return SetGrpElecInvoiceBean.qryEPostInfoByCustID(input);

	}

}
