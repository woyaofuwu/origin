package com.asiainfo.veris.crm.order.soa.person.busi.plat.mobilepayment;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AutoPayException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class AutoPayContractIntfBean extends CSBizBean
{

	protected static Logger logger = Logger.getLogger(AutoPayContractIntfBean.class);

	/**
	 * 判断在用户在BOSS侧是否存在通过其他缴费渠道（例如银行、集团账户代缴）办理的代缴话费签约记录
	 * 
	 * @param pd
	 * @param param
	 *            user_id ，DEFAULT_TAG
	 * @return true 不存在 false 存在
	 * @throws Exception
	 */

	public static boolean accountCondition(IData param) throws Exception
	{
		param.put("DEFAULT_TAG", "1");

		IData payRelation = UcaInfoQry.qryLastPayRelaByUserId(param.getString("USER_ID"));

		if (IDataUtil.isNotEmpty(payRelation))
		{
			// 获取账户信息
			IData acctInfos = UcaInfoQry.qryAcctInfoByAcctId(payRelation.getString("ACCT_ID"));
			if (acctInfos != null && acctInfos.size() > 0)
			{
				if (acctInfos.getString("PAY_MODE_CODE").equals("0"))
					return true;
			} else
			{
				return false;
			}
		}
		return false;
	}

	/**
	 * 手机支付代扣签约
	 * 
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public static IData autoExpendContract(IData param) throws Exception
	{
		/*
		 * 走iboss接口即为 后付费 否贼为预预付费 ATTR_VALUE 内容为 ”a|b|ccc|dddd“ 其中 “ | ” 为分隔符
		 * a：付费类型 ，内容 0 或 1 ; 0后付费 ， 1 预付费。 b：触发类型 ，内容 0 或 1; 0：按时间触发 1：按限额触发。
		 * ccc：触发时机（方式） ， 两种情况 i ：按时间触发 日期 内容为 1-28； ii：按金额触发 金额 内容 为 1-999。
		 * dddd： 自动交费额度 ， 内容 1-9999。
		 */
		if (param.get("IN_MODE_CODE") != null && "6".equals(param.get("IN_MODE_CODE")))
		{
			if (getPayTypeQry(param).equals("1"))
			{
				// common.error("03","非后付费,预付费用户请通过其他渠道办理签约");
				CSAppException.apperr(AutoPayException.CRM_AUTO_1);
			}
			param.put("KIND_ID", "BIP2B083_T2040027_1_0");
			param.put("ATTR_STR1", "171717_PARAM");
			param.put("ATTR_STR2", "0|1|000|0000"); // 后付费账务自动处理 只需给定 a 为 0
													// 后付费才走平台 走1级boss必然后付费
		} else
		{
			param.put("ATTR_STR1", "171717_PARAM");
			param.put("ATTR_STR2", param.getString("ATTR_VALUE"));
		}
		param.put("REMOVE_TAG", "0");
		param.put("NET_TYPE_CODE", "00");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_497);
		}
		param.put("USER_ID", userInfo.getString("USER_ID"));
		param.put("ELEMENT_ID", "171717");
		param.put("ELEMENT_TYPE_CODE", "S");
		param.put("MODIFY_TAG", "0");
		param.put("PAYOPR", "01");
		// 云南产品变更特殊处理
		getTradeData(param);
		childCheckBeforeTrade(param.getString("SERIAL_NUMBER"), param.getString("USER_ID"), param.getString("PAYOPR"), 0);
		childCheckBeforeTrade(param.getString("SERIAL_NUMBER"), param.getString("USER_ID"), param.getString("PAYOPR"), 1);

		IDataset dataset = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
		return dataset.getData(0);

	}

	/**
	 * 手机支付签约变更
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IData changeAutoContractInfo(IData param) throws Exception
	{

		if (param.getString("IN_MODE_CODE") != null && "6".equals(param.getString("IN_MODE_CODE")))
		{
			CSAppException.apperr(PlatException.CRM_PLAT_74, "手机支付签约变更只支持营业厅和10086网厅");
		}

		String attrValue = IDataUtil.chkParam(param, "ATTR_VALUE");
		String serialNumber = param.getString("SERIAL_NUMBER", param.getString("IDVALUE"));

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (userInfo == null || userInfo.isEmpty())
		{
			CSAppException.apperr(CrmUserException.CRM_USER_497);
		}
		String userId = userInfo.getString("USER_ID");

		// 校验入参
		verifyAutoContractVariable(attrValue);

		IData callParam = new DataMap();
		callParam.put("SERIAL_NUMBER", serialNumber);
		callParam.put("USER_ID", userId);
		callParam.put("ELEMENT_ID", "171717");
		callParam.put("ELEMENT_TYPE_CODE", "S");
		callParam.put("MODIFY_TAG", "2");
		callParam.put("PAYOPR", "02");
		callParam.put("BOOKING_TAG", "0");
		callParam.put("ATTR_STR1", param.getString("ATTR_CODE"));
		callParam.put("ATTR_STR2", attrValue);

		childCheckBeforeTrade(serialNumber, userId, "02", 0);
		childCheckBeforeTrade(serialNumber, userId, "03", 1);

		IDataset dataset = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", callParam);
		return dataset.getData(0);

	}

	/* comment:CSIntf.onSubmitBaseTradeCheck里面调用 */
	public static void childCheckBeforeTrade(String serialNumber, String userId, String payOpr, int tag) throws Exception
	{
		// tag ： 0--业务受理时子类业务规则校验 1--业务提交时子类业务规则校验
		if (tag == 0)
		{
			IData iParam = new DataMap();
			iParam.put("IDVALUE", serialNumber);

			IDataset userPlatSvcList = UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, "99166951");

			if (IDataUtil.isEmpty(userPlatSvcList))
			{
				CSAppException.apperr(PlatException.CRM_PLAT_1000_8, "该用户没有开通手机支付业务！");

			} else
			{
				IData userPlatSvc = userPlatSvcList.getData(0);
				if (!"A".equals(userPlatSvc.getString("BIZ_STATE_CODE")))
				{
					CSAppException.apperr(PlatException.CRM_PLAT_1000_8, "该用户没有开通手机支付业务,或手机支付业务已被暂停！");
				}
			}

			// 判断 主号码
			iParam.put("USER_ID", userId);
			if (!RelaUUInfoQry.getRoleIdbOneCN(iParam.getString("USER_ID_B", ""), iParam.getString("ROLE_CODE_B", "")).equals("1"))
			{
				CSAppException.apperr(PlatException.CRM_PLAT_1000_9);
			}

		}
		if (tag == 1)
		{
			IData iParam = new DataMap();
			iParam.put("USER_ID", userId);
			iParam.put("SERVICE_ID", "171717");
			String opr = payOpr;
			if ("01".equals(opr))
			{
				iParam.put("SERVICE_ID", "171717");
				if (UserSvcInfoQry.getAutoPayContractState(iParam.getString("USER_ID"), iParam.getString("SERVICE_ID")))
				{
					CSAppException.apperr(PlatException.CRM_PLAT_1000_10);
				}
			}
			// 校验是否开通了手机代扣签约
			if (opr.equals("02") || opr.equals("03"))
			{
				if (!UserSvcInfoQry.getAutoPayContractState(iParam.getString("USER_ID"), iParam.getString("SERVICE_ID")))
					CSAppException.apperr(PlatException.CRM_PLAT_1000_11);

			}

			// 海南代码已注释掉这段
			// // 判断在用户在BOSS侧是否存在通过其他缴费渠道
			// if (!accountCondition(iParam))
			// {
			// CSAppException.apperr(PlatException.CRM_PLAT_1000_12);
			// }

		}

	}

	/**
	 * 将 服务属性拆串
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static IData detachStr(String value) throws Exception
	{
		IData data = new DataMap();
		try
		{
			String[] param = value.split("\\|");
			data.put("PAY_TYPE", StringUtils.equals("0", param[1])/*
																 * "0".equals(param
																 * [1])
																 */? "按时间触发" : "按金额触发");
			data.put("PAY_TIME", param[2]);
			data.put("PAYBNUM", param[3]);
		} catch (ArrayIndexOutOfBoundsException e)
		{
			// common.error("参数内容错误，请检查参数内容");
			CSAppException.apperr(ParamException.CRM_PARAM_180);
		}
		return data;
	}

	/**
	 * 付费类型查询
	 * 
	 * @param pd
	 * @param param
	 * @return 0 后付费 1 预付费
	 * @throws Exception
	 */
	public static IData getPayTypeQry(IData param) throws Exception
	{
		IData result = new DataMap();
		if (getVisit().getProvinceCode().equals("HAIN"))
		{
			IDataset list = UserInfoQry.existUserVIPREDNODEAL4AUTOPAYBySN(param);
			if (list != null && list.size() > 0)
			{

				result.put("PREPAY_TAG", "0");
			} else
			{
				result.put("PREPAY_TAG", "1");
			}

		} else
		{
			param.put("REMOVE_TAG", "0");
			IDataset userInfo = UserInfoQry.getUsersBySn(param.getString("SERIAL_NUMBER"));
			if (userInfo.size() > 0)
			{
				IData user = (IData) userInfo.get(0);
				String prepay_tag = user.getString("PREPAY_TAG");
				if ("0".equals(prepay_tag))
					result.put("PREPAY_TAG", "0");
				else
					result.put("PREPAY_TAG", "1");

			} else
			{
				result.put("X_RESULTCODE", "9");
				result.put("X_RESULTINFO", "手机号对应的用户不存在");
				result.put("X_RSPTYPE", "2");// add by ouyk
				result.put("X_RSPCODE", "2998");// add by ouyk
			}
		}
		return result;
	}

	/**
	 * 重写 getTradeData 设置初始化和 签约业务的校验
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */

	protected static void getTradeData(IData data) throws Exception
	{

		// in_mode_code=6是一级boss传过来的接入码
		if (data.getString("IN_MODE_CODE") != null && "6".equals(data.getString("IN_MODE_CODE")))
		{
			if (data.getString("PAYOPR") != null && "02".equals(data.getString("PAYOPR")))
			{
				CSAppException.apperr(PlatException.CRM_PLAT_1000_1);
			}

			if (data.getString("KIND_ID") == null)
			{
				CSAppException.apperr(PlatException.CRM_PLAT_1000_2, "KIND_ID");
			}

			String tradeTypeCode = StaticUtil.getStaticValue("CSINFT_BIZ_TRADETYPECODE", data.getString("KIND_ID"));
			if (StringUtils.isBlank(tradeTypeCode))
			{
				CSAppException.apperr(PlatException.CRM_PLAT_1000_3);
			}

			data.put("TRADE_TYPE_CODE", tradeTypeCode);
		} else
		{
			if (data.getString("PAYOPR") != null && !"03".equals(data.getString("PAYOPR")))
			{
				// 校验参数是为空
				if (data.getString("ATTR_CODE") == null || "".equals(data.getString("ATTR_CODE")) || data.getString("ATTR_VALUE") == null || "".equals(data.getString("ATTR_VALUE")))
				{
					CSAppException.apperr(PlatException.CRM_PLAT_1000_4);
				}
				verifyAutoContractVariable(data.getString("ATTR_VALUE"));
			}
		}
	}

	/**
	 * 手机支付解约
	 * 
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IData removeAutoContract(IData param) throws Exception
	{
		String serialNumber = param.getString("IDVALUE", param.getString("SERIAL_NUMBER"));

		if (param.getString("IN_MODE_CODE") != null && "6".equals(param.getString("IN_MODE_CODE")))
		{
			param.put("KIND_ID", "BIP2B083_T2040028_1_0");
		}

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (userInfo == null || userInfo.isEmpty())
		{
			CSAppException.apperr(CrmUserException.CRM_USER_497);
		}
		String userId = userInfo.getString("USER_ID");

		param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_ID", userId);
		param.put("ELEMENT_ID", "171717");
		param.put("ELEMENT_TYPE_CODE", "S");
		param.put("MODIFY_TAG", "1");
		param.put("PAYOPR", "03");
		param.put("BOOKING_TAG", "0");
		getTradeData(param);

		childCheckBeforeTrade(serialNumber, userId, "03", 0);
		childCheckBeforeTrade(serialNumber, userId, "03", 1);

		IDataset dataset = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
		return dataset.getData(0);
	}

	/**
	 * 服务属性拆串校验
	 * 
	 * @param value
	 * @throws Exception
	 */
	public static void verifyAutoContractVariable(String value) throws Exception
	{
		String[] param = value.split("\\|");

		try
		{
			String payType = param[1];
			int payValue = Integer.parseInt(param[2]);
			int payQuota = Integer.parseInt(param[3]);

			if (payType != null && !"".equals(payType) && "0".equals(payType))
			{
				if (0 >= payValue || payValue > 28)
				{
					CSAppException.apperr(PlatException.CRM_PLAT_1000_5);
				}
			}
			if (payType != null && !"".equals(payType) && "1".equals(payType))
			{
				if (0 >= payValue || payValue > 999)
				{
					CSAppException.apperr(PlatException.CRM_PLAT_1000_5);
				}
			}
			if (0 >= payQuota || payQuota > 9999)
				CSAppException.apperr(PlatException.CRM_PLAT_1000_6);
		} catch (ArrayIndexOutOfBoundsException e)
		{
			CSAppException.apperr(PlatException.CRM_PLAT_1000_7);
		} catch (NumberFormatException e)
		{
			CSAppException.apperr(PlatException.CRM_PLAT_74, "触发额度和缴费额度必须为整数");
		}
	}
}
