package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade; 

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl.ChnlInfoQry;
import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.DepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util.Base64Util;

public class CreatePersonUserBean extends CSBizBean
{
	protected static Logger log = Logger.getLogger(CreatePersonUserBean.class);

	/**
	 * 判断号码是否可以二次开户
	 * 
	 * @param pd
	 * @param inData
	 * @return
	 * @throws Exception
	 */
	public static IData canReopenMPCode(IData data) throws Exception
	{
		IData inData = new DataMap();
		inData.put("X_GETMODE", "0");
		inData.put("RES_TRADE_CODE", "ICheckReOpenRes");
		inData.put("RES_CODE", data.getString(""));
		inData.put("NET_TYPE_CODE", data.getString(""));
		inData.put("RES_TYPE_CODE", "0");
		// 需要调用资源接口查询 sunxin
		/*
		 * IDataset resultList = TuxedoHelper.callTuxSvc(pd,
		 * "QCS_CanReopenMPCode", inData, true); return resultList.size() > 0 ?
		 * resultList.getData(0) : new DataMap();
		 */
		return new DataMap();
	}

	/**
	 * 根据号码查询是否有未完工工单 后续使用规则，先暂不处理 sunxin
	 * 
	 * @param PageData
	 *            ,TradeData
	 */
	/**
	 * 检查SIM卡是否是4GUSIM卡
	 * 
	 * @author zhangxiaobao
	 * @date 2013-10-20
	 * @param pd
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkUser4GUsimCard(String simTypeCode) throws Exception
	{
		IData qryParam = new DataMap();
		qryParam.put("PARA_ATTR", "8510");
		qryParam.put("PARA_CODE1", "TD_LTE_SIM_CARDTYPE_CODE");
		qryParam.put("PARA_CODE2", simTypeCode);
		qryParam.put("EPARCHY_CODE", "");
		IDataset dataset = null;//
		return dataset;
	}

	/**
	 * 删除预存费用
	 * 
	 * @author chenzm
	 * @param feeList
	 * @return
	 * @throws Exception
	 */
	public static void delDepositFee(IDataset feeList) throws Exception
	{
		String feeMode;
		String feeTypeCode;
		int fee;
		for (int i = 0; i < feeList.size(); i++)
		{
			feeMode = feeList.getData(i).getString("FEE_MODE");
			feeTypeCode = feeList.getData(i).getString("FEE_TYPE_CODE");
			if ("2".equals(feeMode) && !existFeeType(feeTypeCode))
			{
				fee = Integer.parseInt(feeList.getData(i).getString("FEE"));
				if (fee != 0)
				{
					feeList.remove(i);
					i--;
				}
			}
		}
	}

	public static IDataset distinct(List<IDataset> sets)
	{
		IDataset datas = new DatasetList();
		Map<String, IData> map = new HashMap<String, IData>();
		for (IDataset set : sets)
		{
			for (int i = 0; i < set.size(); i++)
			{
				IData data = set.getData(i);
				map.put(data.getString("PRODUCT_TYPE_CODE"), data);
			}
		}
		for (String key : map.keySet())
		{
			datas.add(map.get(key));
		}
		return datas;
	}

	/**
	 * 过滤不删除得预存费用
	 * 
	 * @author chenzm
	 * @param feeList
	 * @return
	 * @throws Exception
	 */
	private static boolean existFeeType(String feeTypeCode) throws Exception
	{
		IDataset resultList = CommparaInfoQry.getCommparaAllCol("CSM", "1811", feeTypeCode, CSBizBean.getTradeEparchyCode());
		return IDataUtil.isNotEmpty(resultList);
	}

	/**
	 * 根据品牌过滤产品类型
	 * 
	 * @param productTypeList
	 * @param strBrandCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset filterProductTypeListByBrandCode(IDataset productTypeList, String strBrandCode) throws Exception
	{
		// 通过strBrandCode查询所有对应的产品类型
		IData param = new DataMap();
		param.put("BRAND_CODE", strBrandCode);// 目前只支持单个产品的过滤
		IDataset dataset = null;
		String strProductTypes = "";
		for (int i = 0; i < dataset.size(); i++)
		{
			strProductTypes = strProductTypes + "," + dataset.getData(i).getString("PRODUCT_TYPE_CODE");
		}
		IData productTypeData = null;
		for (int i = 0; i < productTypeList.size(); i++)
		{
			productTypeData = productTypeList.getData(i);
			// brandCode = productTypeData.getString("PRODUCT_TYPE_CODE");

		}
		return productTypeList;
	}

	/**
	 * 根据产品类型默认标记过滤产品类型
	 * 
	 * @param oldList
	 * @param strDefaultTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset filterProductTypeListByDefaultTag(IDataset productTypeList, String strDefaultTag) throws Exception
	{
		IData productTypeData = null;
		String defaultTag = "";// 默认标记
		String parentTypeCode = "";// 父结点
		for (int i = 0; i < productTypeList.size(); i++)
		{
			productTypeData = productTypeList.getData(i);
			defaultTag = productTypeData.getString("DEFAULT_TAG");
			if (strDefaultTag.indexOf(defaultTag) < 0)
			{
				productTypeList.remove(i);
				i--;
			}
		}
		return productTypeList;
	}

	/**
	 * 根据证件类型和证件号码获取客户资料
	 * 
	 * @author chenzm
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset getCustInfoByPspt(IData data) throws Exception
	{
		IData custPersonList = null;
		IData custData = null;
		IDataset custList = new DatasetList();// 先给初始值，便于后面判断
		custList = CustomerInfoQry.getCustInfoByPsptCustType("0", data.getString("PSPT_TYPE_CODE"), data.getString("PSPT_ID"));
		// 获取客户核心资料+客户个人资料
		/*
		 * 办理家庭网业务添加虚拟客户核心资料，不会添加客户资料，屏蔽 if (IDataUtil.isNotEmpty(custList)) {
		 * for (int i = 0; i < custList.size(); i++) { custData =
		 * custList.getData(i); String custId = custData.getString("CUST_ID");
		 * custPersonList = UcaInfoQry.qryPerInfoByCustId(custId); if
		 * (IDataUtil.isNotEmpty(custPersonList)) {
		 * custData.putAll(custPersonList);//
		 * 根据客户标识查个人资料，只有一条记录，直接取getData(0)即可。 } else {
		 * CSAppException.apperr(CustException.CRM_CUST_64,
		 * data.getString("PSPT_ID"), custData.getString("CUST_ID")); } } }
		 */
		return custList;
	}

	/**
	 * 根据号段和号码类型获取默认产品
	 * 
	 * @author chenzm
	 * @param serialNumber
	 * @param codeTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IData getDefaultProduct(String serialNumber, String codeTypeCode) throws Exception
	{

		IDataset defalutProductInfos = ProductInfoQry.getDefaultProductByPhone(serialNumber, codeTypeCode, CSBizBean.getUserEparchyCode());
		if (IDataUtil.isEmpty(defalutProductInfos))
		{
			defalutProductInfos = ProductInfoQry.getDefaultProductByResType(serialNumber, codeTypeCode, CSBizBean.getUserEparchyCode());
		}

		return IDataUtil.isNotEmpty(defalutProductInfos) ? defalutProductInfos.getData(0) : new DataMap();
	}

	/**
	 * 处理sql绑定多个值，in方式
	 * 
	 * @param strOldBindValue
	 * @return
	 * @throws Exception
	 */
	public static String getMulteBindValue(String strOldBindValue) throws Exception
	{

		if (strOldBindValue.startsWith(","))
			strOldBindValue = strOldBindValue.substring(1);// 去掉首字符为","号
		if (strOldBindValue.endsWith(","))
			strOldBindValue = strOldBindValue.substring(0, strOldBindValue.length() - 1);// 去掉尾字符为","号
		// 不存在多个值
		if (strOldBindValue.indexOf(",") == -1)
		{
			return "";
		}
		return strOldBindValue;// 转换后参数
	}

	/**
	 * 获取发票类型列表
	 * 
	 * @author chenzm
	 * @param strEparchyCode
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getNoteItemList(String strEparchyCode) throws Exception
	{

		IData inParams = new DataMap();
		inParams.put("PARA_CODE1", strEparchyCode);
		IDataset resultList = null;

		return resultList != null && resultList.size() > 0 ? resultList : new DatasetList();
	}

	/**
	 * 根据用户标识获取欠费信息
	 * 
	 * @@author chenzm
	 * @param custList
	 * @param allUser
	 * @return IData
	 * @throws Exception
	 */
	public static IData getOweFeeAllUserById(IDataset custList, boolean allUser) throws Exception
	{

		IData oweFeeData = new DataMap();
		IData custData = null;
		IData userData = null;
		IDataset userList = null;
		IData owefeeData = null;
		double dFee = 0;// 往月欠费
		int iOnlineNum = 0;// 当前证件下在网用户数
		boolean isExistsOweFeeFlag = false;// 存在欠费用户标记
		String oweFeeSerialNumber = "";// 欠费号码
		for (int i = 0; i < custList.size(); i++)
		{
			custData = custList.getData(i);
			String cust_id = custData.getString("CUST_ID");
			if (allUser)
			{

				userList = UserInfoQry.getAllUserInfoByCustId(cust_id);
			} else
			{
				userList = UserInfoQry.getAllNormalUserInfoByCustId(cust_id);
			}

			if (IDataUtil.isNotEmpty(userList))
			{
				iOnlineNum += userList.size();// 统计在网用户数
				// 未找到欠费用户时，才查欠费信息，找到一条则不查询，提示第一条欠费信息即可
				if (!isExistsOweFeeFlag)
				{
					// 根据用户标识查询欠费信息
					for (int j = 0; j < userList.size(); j++)
					{
						userData = userList.getData(j);
						// 老系统就判的写死的时间，不太合理
						if (CSBizBean.getUserEparchyCode().equals("0736") && userData.getString("OPEN_DATE").compareTo(SysDateMgr.addYears(SysDateMgr.getSysDate(), -5)) < 0)
							continue;
						String userID = userData.getString("USER_ID");
						owefeeData = AcctCall.getOweFeeByUserId(userID);
						dFee = owefeeData.getDouble("LAST_OWE_FEE");
						if (dFee > 0)
						{// 找到有往月欠费用户则退出循环，提示欠费信息
							isExistsOweFeeFlag = true;
							oweFeeSerialNumber = userData.getString("SERIAL_NUMBER");
							break;
						}
					}
				}
			}
		}
		// 存在欠费用户时，返回欠费号码，欠费金额，在网用户数
		if (isExistsOweFeeFlag)
		{

			String strFee = String.valueOf(((float) dFee) / 100);
			oweFeeData.put("OWE_FEE_SERIAL_NUMBER", oweFeeSerialNumber);
			oweFeeData.put("OWE_FEE", strFee);
			oweFeeData.put("ONLINE_NUM", iOnlineNum);
			oweFeeData.put("IS_EXISTS_OWE_FEE_FLAG", true);
		}
		return oweFeeData;
	}

	/**
	 * 获取父子二级产品类型列表
	 * 
	 * @param productTypeList
	 * @return
	 * @throws Exception
	 */
	public static IData getParentChildProductTypeList(IDataset productTypeList) throws Exception
	{
		IData productTypeData = null;
		IData returnData = new DataMap();
		String parentPtypeCode = "";
		IDataset parentProductTypeList = new DatasetList();
		IDataset childProductTypeList = new DatasetList();
		String childParentTypeCode = "";
		for (int i = 0; i < productTypeList.size(); i++)
		{
			productTypeData = productTypeList.getData(i);
			parentPtypeCode = productTypeData.getString("PARENT_PTYPE_CODE");
			if ("0000".equals(parentPtypeCode))
			{
				parentProductTypeList.add(productTypeData);
			} else
			{
				childProductTypeList.add(productTypeData);
			}
		}
		returnData.put("PARENT_PRODUCT_TYPE_LIST", parentProductTypeList);
		returnData.put("CHILD_PRODUCT_TYPE_LIST", childProductTypeList);
		return returnData;
	}

	/**
	 * 根据默认标记获取产品类型
	 * 
	 * @param strDefaultTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset getProductTypeCodeByDefaultTag(String strDefaultTag) throws Exception
	{

		IDataset typeset = null;
		if (strDefaultTag.indexOf(",") == -1)
		{// 单参数
			return ProductTypeInfoQry.getProductTypeByDefaultTag(strDefaultTag);
		} else
		{
			strDefaultTag = getMulteBindValue(strDefaultTag);
			String[] strBindValueArry = StringUtils.split(strDefaultTag, ',');
			List<IDataset> sets = new ArrayList<IDataset>();
			for (int i = 0; i < strBindValueArry.length; i++)
			{
				typeset = ProductTypeInfoQry.getProductTypeByDefaultTag(strBindValueArry[i]);
				sets.add(typeset);
			}
			return distinct(sets);
		}
	}

	/**
	 * 根据产品标识获取产品类型
	 * 
	 * @param strProductId
	 * @param strEparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getProductTypeCodeByProductId(String strProductId, String strEparchyCode) throws Exception
	{

		IData inData = new DataMap();
		inData.put("PRODUCT_ID", strProductId);// 个人产品标识
		inData.put("EPARCHY_CODE", strEparchyCode);// 用户路由
		if (strProductId.indexOf(",") == -1)
		{// 单参数
			return ProductTypeInfoQry.getProductTypeByProductID(strProductId, strEparchyCode);
		} else
		{
			// 现没有绑定多个产品情况
			String strOldBindValue = getMulteBindValue(strProductId);
			return ProductTypeInfoQry.getProductTypeByProductID(strOldBindValue, strEparchyCode);
		}
	}

	/**
	 * 输入新开户号码后的校验，获取开户信息
	 * 
	 * @author chenzm
	 * @param data
	 * @return
	 * @throws Exception
	 */

	/**
	 * 检查证件是否为黑名单
	 * 
	 * @author chenzm
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData IsBlackUser(IData data) throws Exception
	{

		boolean isBlackFlag = false;
		IData returnData = new DataMap();
		String pspt_type_code = data.getString("PSPT_TYPE_CODE");
		String pspt_id = data.getString("PSPT_ID");
		IDataset resultList = UCustBlackInfoQry.qryBlackCustInfo(pspt_type_code, pspt_id);
		if (resultList != null && resultList.size() > 0)
		{// 是黑名单时再根据黑名单级别处理
			IData blackData = resultList.getData(0);
			if ("6".equals(blackData.getString("BLACK_USER_CLASS_CODE")))
			{// 实名保护资料，不做黑名单处理
				isBlackFlag = false;
			} else
			{
				isBlackFlag = true;
			}
			// 黑名单时提示继续或中断
			if (isBlackFlag)
			{
				returnData.put("IS_BLACK_USER", isBlackFlag);// 黑名单标记
				returnData.put("MOB_PHONECODE", blackData.getString("MOB_PHONECODE"));// 黑名单号码
				returnData.put("JOIN_CAUSE", blackData.getString("JOIN_CAUSE"));// 黑名单原因
			}
		}
		return returnData;
	}

	/**
	 * 弹出信息提示
	 * 
	 * @param obj
	 * @throws Exception
	 */
	public static void redirectToMsgForCallSvc(Object obj) throws Exception
	{
		String xResultCode = "";
		String xResultInfo = "";
		IData xResultData = null;
		if (obj instanceof IDataset)
		{
			xResultData = ((IDataset) obj).getData(0);
		} else if (obj instanceof IData)
		{
			xResultData = ((IData) obj);
		}
		xResultCode = xResultData.getString("X_RESULTCODE", "0");
		xResultInfo = xResultData.getString("X_RESULTINFO", "");
		if (!"0".equals(xResultCode))
		{
			CSAppException.apperr(BizException.CRM_BIZ_5, xResultInfo);
		}
	}

	/**
	 * 校验刮刮卡资源
	 * 
	 * @author chenzm
	 * @param data
	 * @throws Exception
	 */
	public IDataset checkGGCard(IData data) throws Exception
	{

		String res_no_s = data.getString("DEVELOP_NO");
		String res_no_e = data.getString("DEVELOP_NO");
		return ResCall.checkVirtualCard(res_no_s, res_no_e, getVisit().getDepartId(), "3");

	}

	/**
	 * 校验证件下停机，黑名单，欠费销户用户等
	 * 
	 * @author chenzm
	 * @param data
	 * @throws Exception
	 */
	public IData checkPsptId(IData data) throws Exception
	{
		IData ajaxReturnData = new DataMap();
		// 黑名单
		IData blackData = IsBlackUser(data);
		if (!blackData.isEmpty())
		{
			if (blackData.getBoolean("IS_BLACK_USER", false))
			{
				String strBlackHintMsg = "该证件为黑名单资料！黑名单号码：" + blackData.getString("MOB_PHONECODE") + "。加入黑名单原因：" + blackData.getString("JOIN_CAUSE") + "。";
				if (!"1".equals(data.getString("CHR_BLACKCHECKMODE")))
				{

					// 提示是否继续
					ajaxReturnData.put("IS_BLACK_USER", true);
					ajaxReturnData.put("BLACK_USER_MSG", strBlackHintMsg);
				} else
				{
					CSAppException.apperr(BizException.CRM_BIZ_5, strBlackHintMsg);
				}
			}
		}

		// 提示是否选择同客户,正确的业务逻辑应该是提示黑名单后中断，点击继续办理时才调用下面的方法，考虑将下面的方法抽成方法，实现java中断。后续再完善。
		// 获取客户资料,并校验证件号码下有无欠费用户
		IDataset custList = getCustInfoByPspt(data);

		// 根据客户标记获取用户是否有欠费判断
		if (IDataUtil.isNotEmpty(custList))
		{
			// 根据客户标记获取用户欠费信息
			if (data.getBoolean("CHR_CHECKOWEFEEBYPSPT", false))
			{// 根据证件号码判断欠费(巧妙的实现方式是根据用户标记)
				// HXYD-YZ-REQ-20100420-008常德分公司要求一证多号判断含消号用户欠费需求 销户的号码也要判往月欠费
				IData oweFeeData = getOweFeeAllUserById(custList, data.getBoolean("CHR_CHECKOWEFEEBYPSPT_ALLUSER", false));
				if (IDataUtil.isEmpty(oweFeeData))
				{
					if (oweFeeData.getBoolean("IS_EXISTS_OWE_FEE_FLAG", false))
					{
						String strOnlineHintMsg = "当前用户证件下共有在网用户【" + oweFeeData.getString("ONLINE_NUM") + "】个！";
						if (data.getBoolean("CHR_CHECKOWEFEEBYPSPT_ALLUSER", false))
						{
							strOnlineHintMsg = "当前用户证件下共有历史入网记录【" + oweFeeData.getString("ONLINE_NUM") + "】个！";
						}

						if (data.getBoolean("OPEN_LIMIT_TAG", false))
						{// 存在开户限制时，中断业务操作
							String strOweFeeExitHintMsg = strOnlineHintMsg + "\n其中号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元，不能再次使用该证件办理开户业务！";

							if (CSBizBean.getUserEparchyCode().equals("0736"))
								strOweFeeExitHintMsg = strOnlineHintMsg + "\n其中2008年1月1日后入网的号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元，不能再次使用该证件办理开户业务！";

							CSAppException.apperr(BizException.CRM_BIZ_5, strOweFeeExitHintMsg);
						} else
						{
							// 前台判断存在欠费用户时，提示是否继续，点击否中断，点击是则调用后续客户开户数限制:JudgeOpenLimit
							String strOweFeeConfirmHintMsg = strOnlineHintMsg + "\n其中号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元。";
							if (CSBizBean.getUserEparchyCode().equals("0736"))
								strOweFeeConfirmHintMsg = strOnlineHintMsg + "\n其中2008年1月1日后入网的号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元。";

							ajaxReturnData.put("IS_EXISTS_OWE_FEE_FLAG", true);
							ajaxReturnData.put("OWE_CONFIRM_HINT_MSG", strOweFeeConfirmHintMsg);// 欠费信息提示
						}
					}
				}
			}
		}
		return ajaxReturnData;
	}

	/**
	 * 二次开户登录员业务区必须和号码归属业务一致
	 * 
	 * @author chenzm
	 * @param userInfo
	 * @throws Exception
	 */
	public void checkReOpenCityCode(IData userInfo) throws Exception
	{

		IData tagInfo = getTagInfo(CSBizBean.getUserEparchyCode(), "CS_AGAINCITYCHECK", "0");
		if ("1".equals(tagInfo.getString("TAG_CHAR", "")))
		{
			if (!getVisit().getCityCode().equals(userInfo.getString("CITY_CODE", getVisit().getCityCode())))
			{
				CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.SAME_TO_CITY_CODE_IN_RE_OPEN);
			}
		}
	}

	/**
	 * 检查密码的设置是否符合要求
	 * 
	 * @author chenzm
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData checkSimplePassword(IData data) throws Exception
	{

		String routeEparchyCode = BizRoute.getRouteId();
		String configEparchyCode = TagInfoQry.getSysTagInfo("CSM_PASSWORD_LIMIT_SWITCH", "TAG_INFO", "NO_DATA", routeEparchyCode);
		IData rtnData = new DataMap();
		rtnData.put("CHECK_PASS", "TRUE");
		rtnData.put("CHECK_MESSAGE", "密码设置符合要求，验证通过");

		if (routeEparchyCode.equals(configEparchyCode))
		{

			String serialNumber = data.getString("SERIAL_NUMBER");
			String psptType = data.getString("PSPT_TYPE_CODE");
			String psptId = data.getString("PSPT_ID");
			String birthday = data.getString("BIRTHDAY");
			String newPassword = data.getString("USER_PASSWD");

			String simplePwd1 = serialNumber.substring(0, 3) + serialNumber.substring(serialNumber.length() - 3, serialNumber.length());
			if (newPassword != null && !"".equals(newPassword))
			{

				// 密码不能为手机号码前三位和后三位
				if (simplePwd1.equals(newPassword))
				{
					rtnData.put("CHECK_PASS", "FALSE");
					rtnData.put("CHECK_MESSAGE", "密码不能为手机号码前三位和后三位！");
					return rtnData;
				}

				// AAABBB类号码（A和B不要求连续）
				IData param = new DataMap();
				for (int i = 0; i < newPassword.length(); i++)
				{
					char charI = newPassword.charAt(i);

					if (param.isEmpty())
					{
						param.put(String.valueOf(charI), charI);

					} else
					{
						if (!param.containsKey(charI))
						{
							param.put(String.valueOf(charI), charI);
						}
					}
				}
				if (param.size() <= 2)
				{
					rtnData.put("CHECK_PASS", "FALSE");
					rtnData.put("CHECK_MESSAGE", "密码不能为AAABBB类号码（A和B不要求连续）");
					return rtnData;
				}

				// 不允许用户使用自己的生日作为密码。
				String[] birthdayArray = birthday.split("-");
				String birthdayMatch = "";
				for (int i = 0; i < birthdayArray.length; i++)
				{
					if (birthdayArray[i].length() == 1)
					{
						birthdayMatch = birthdayMatch + "0" + birthdayArray[i];
					} else
					{
						birthdayMatch = birthdayMatch + birthdayArray[i];
					}
				}
				birthdayMatch = birthdayMatch.substring(2);
				if (birthdayMatch.equals(newPassword))
				{
					rtnData.put("CHECK_PASS", "FALSE");
					rtnData.put("CHECK_MESSAGE", "不允许使用自己的生日作为密码");
					return rtnData;
				}

				// 如用户使用身份证上户，不允许用户采用身份证中连续的6位数字。
				if ("0".equals(psptType) || "1".equals(psptType) || "2".equals(psptType))
				{
					/*
					 * if (KMP.kmpMatch(psptId, newPassword)) {
					 * rtnData.put("CHECK_PASS", "FALSE");
					 * rtnData.put("CHECK_MESSAGE", "密码不允许采用身份证中连续的6位数字。");
					 * return rtnData; }
					 */
				}

			} else
			{
				rtnData.put("CHECK_PASS", "FALSE");
				rtnData.put("CHECK_MESSAGE", "密码不能为空！");
				return rtnData;
			}
		}
		return rtnData;
	}

	/**
	 * 资源sim卡校验
	 * 
	 * @author chenzm
	 * @param data
	 * @throws Exception
	 */
	public IData checkSimResource(IData data) throws Exception
	{

		IData returnData = new DataMap();
		String serialNumber = data.getString("SERIAL_NUMBER", "");
		String simCardNo = data.getString("SIM_CARD_NO", "");
		String oldSimCardNo = data.getString("OLD_SIM_CARD_NO", "");
		IDataset checkSimDatas = new DatasetList();
		// 非第一次时并新老资源不一样时，先释放上一次资源 add by yinhq 2009-09-25
		if (!StringUtils.isBlank(oldSimCardNo) && !oldSimCardNo.equals(simCardNo))
		{
			/*
			 * IData resOccupyData =
			 * IResOccupyRelease(pd,oldSimCardNo,"1","ReleaseResTempoccupySingle"
			 * ); redirectToMsgForCallSvc(resOccupyData);
			 */// 同手机一样，需要资源提供接口 sunxin
		}
		// 产品限制SIM卡类型，EXISTS_SINGLE_PRODUCT依赖于号段绑定产品 没有找到对应值，不知道是否还需要 sunxin
		/*
		 * String forceProduct = pd.getParameter("EXISTS_SINGLE_PRODUCT", "");
		 * if(!"".equals(forceProduct)){ checkSimForProduct(pd, forceProduct,
		 * simCardNo); }
		 */
		// SIM卡校验
		if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")))
		{
			// checkSimDatas = ResCall.checkResourceForIOTSim("0", "0",
			// serialNumber, simCardNo, "1",
			// data.getString("CHECK_DEPART_ID"), "", "0");
		} else
		{
			// checkSimDatas = ResCall.checkResourceForSim("0", "0",
			// serialNumber, simCardNo, "1",
			// data.getString("CHECK_DEPART_ID"), "", "0");
		}

		/*
		 * IData checkSimData = checkSimDatas.getData(0);
		 * returnData.put("FEE_TAG", false);
		 * if("0".equals(checkSimData.getString
		 * ("FEE_TAG",""))||"2".equals(checkSimData.getString("FEE_TAG","")))
		 * returnData.put("FEE_TAG", true);
		 * returnData.put("SIM_FEE_TAG",checkSimData.getString("FEE_TAG",""));
		 * //add by wenhj HNYD-REQ-20110402-010
		 * returnData.put("SIM_CARD_SALE_MONEY",
		 * ""+checkSimData.getInt("SALE_MONEY", 0)); //add by wenhj
		 * HNYD-REQ-20110402-010 returnData.put("CHECK_RESULT_CODE", "1");//
		 * SIM校验成功，且服务号码成功！ IDataUtil.chkParam(checkSimData, "IMSI");
		 * IDataUtil.chkParam(checkSimData, "KI"); // 取出卡资源部分数据用于登记台帐
		 * returnData.put("RES_KIND_CODE",
		 * checkSimData.getString("RES_KIND_CODE", ""));// 卡类型名称
		 * returnData.put("RES_KIND_NAME",
		 * checkSimData.getString("RES_KIND_NAME", ""));// 卡类型编码
		 * returnData.put("IMSI", checkSimData.getString("IMSI", ""));
		 * returnData.put("KI", checkSimData.getString("KI", "")); String
		 * strResKindCode = checkSimData.getString("CODE_TYPE_CODE", "0");// 卡类型
		 * String strCapacityTypeCode = checkSimData.getString("NET_TYPE_CODE",
		 * "1");// 卡容量 String strNewAgentSaleTag =
		 * checkSimData.getString("RSRV_STR3", "");// //该白卡是否为代理商空卡买断。 String
		 * strResTypeCode="1"; // 如果是写卡写的白卡则将白卡的类型放进rsrv_str8
		 * if(!"".equals(checkSimData.getString("EMPTY_CARD_ID", "")) &&
		 * !"U".equals(strResKindCode) && !"X".equals(strResKindCode)) {
		 * //如果SIM卡表中EMPTY_CARD_ID字段不为空，标明该卡由白卡写成，到白卡表中取卡类型 IData
		 * newEmptyCardInfo = new
		 * DataMap();//getResInfo(pd,checkSimData.getString("EMPTY_CARD_ID",
		 * ""),"6", 0,"IGetEmptyCardInfo");资源接口 sunxin String newSimCardType =
		 * newEmptyCardInfo.getString("SIM_TYPE_CODE"); String
		 * newCapacityTypeCode =
		 * newEmptyCardInfo.getString("CAPACITY_TYPE_CODE"); String str1 =
		 * StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_RESKIND",new
		 * java.lang.String[]{"RES_TYPE_CODE","RES_KIND_CODE"},"KIND_NAME",new
		 * java.lang.String[]{"6",newSimCardType}); String str2 =
		 * StaticUtil.getStaticValue
		 * (CSBizBean.getVisit(),"TD_S_SIMCAPACITY","CAPACITY_TYPE_CODE"
		 * ,"CAPACITY_TYPE" ,newCapacityTypeCode); String str = str1 + str2 ;
		 * returnData.put("MAIN_RSRV_STR8",str);// 预留字段用于登记主台帐:MAIN_XXX
		 * //为获取设备价格准备白卡属性参数 strResTypeCode="6"; strResKindCode = newSimCardType
		 * ; strCapacityTypeCode = strCapacityTypeCode; strNewAgentSaleTag =
		 * newEmptyCardInfo.getString("RSRV_STR2", "");// //该白卡是否为代理商空卡买断。 }
		 * else { returnData.put( "MAIN_RSRV_STR8",
		 * checkSimData.getString("RSRV_STR4", "") + " " +
		 * checkSimData.getString("RSRV_STR1", ""));// 预留字段用于登记主台帐:MAIN_XXX }
		 * returnData.put("NEW_AGENT_SALE_TAG", strNewAgentSaleTag); // xiekl
		 * 物联网网修改 物联网 机器卡类型1001 1002需要写入OPC值 if ("Z".equals(strResKindCode) ||
		 * "1".equals(data.getString("M2M_FLAG"))) {// 是否为USIM卡,3G,将OPC记录在attr表
		 * String uSimOpc = checkSimData.getString("OPC", ""); if
		 * (!StringUtils.isBlank(uSimOpc)) { returnData.put("OPC_CODE",
		 * "OPC_VALUE"); returnData.put("OPC_VALUE", uSimOpc); } } // add by
		 * zhangxiaobao for lte begin String simTypeCode =
		 * checkSimData.getString("SIM_TYPE_CODE", ""); String uSimOpc =
		 * checkSimData.getString("OPC", ""); IDataset uimInfo =
		 * checkUser4GUsimCard(simTypeCode); returnData.put("FLAG_4G", ""); if
		 * (null !=uimInfo && uimInfo.size()>0 &&null !=uSimOpc &&
		 * !"".equals(uSimOpc)) { returnData.put("NOTICE_CONTENT",
		 * uimInfo.getData(0).getString("PARA_VALUE8", ""));
		 * returnData.put("NOTICE_FLAG","1"); returnData.put("FLAG_4G", "1"); }
		 * returnData.put("EMPTY_CARD_ID",
		 * checkSimData.getString("EMPTY_CARD_ID", "")); // add by zhangxiaobao
		 * for lte end // 获取设备价格 String strProductId = "-1"; String strClassId =
		 * "Z"; String authTag = data.getString("AUTH_FOR_SALE_ACTIVE_TAG",
		 * "false"); if(!"true".equals(authTag)){ IData dataFee =
		 * this.getDevicePrice(data,strResTypeCode ,strResKindCode,
		 * strCapacityTypeCode,strProductId, strClassId); if
		 * (IDataUtil.isNotEmpty(dataFee)) { returnData.put("FEE_MODE", "0");
		 * returnData.put("FEE_TYPE_CODE", dataFee.getString("FEEITEM_CODE"));
		 * returnData.put("FEE", dataFee.getString("DEVICE_PRICE")); } } //
		 * 获取卡号费用 湖南获取卡费时strCapacityTypeCode=0 //IData feeData =
		 * DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), "1",
		 * strProductId, "10", "0", strResTypeCode); // 增加省内异地开户预存特殊处理 sunxin
		 * boolean bProvOpenDefltAdvancePay = "1".equals(td.getString(
		 * "CS_NUM_PROVOPENADVANCEPAY", "")) ? true : false; int
		 * iProvOpenDefltAdvancePay = 0; IData feeData = null; if
		 * (bProvOpenDefltAdvancePay) { feeData = getTagInfo(pd, td,
		 * "CS_NUM_PROVOPENADVANCEPAY", "0"); iProvOpenDefltAdvancePay =
		 * feeData.getInt("TAG_NUMBER", 0); } if
		 * (pd.getContext().getPrivMap().containsValue(
		 * "csCreateProvUserRemoteTrade") && bProvOpenDefltAdvancePay) {
		 * FeeListMgr.delAllFeeList(pd, td, "2", "*", "");// 删除预存费用
		 * FeeListMgr.addFeeList(pd, td, "2", "29", iProvOpenDefltAdvancePay +
		 * ""); } // 增加省内异地开户卡费特殊处理 boolean bProvOpenDefltOperFee =
		 * "1".equals(td.getString( "CS_NUM_PROVOPENOPERFEE", "")) ? true :
		 * false; int iProvOpenDefltOperFee = 0; if (bProvOpenDefltOperFee) {
		 * feeData = getTagInfo(pd, td, "CS_NUM_PROVOPENOPERFEE", "0");
		 * iProvOpenDefltOperFee = feeData.getInt("TAG_NUMBER", 0); } if
		 * (pd.getContext().getPrivMap().containsValue(
		 * "csCreateProvUserRemoteTrade") && bProvOpenDefltOperFee) {
		 * FeeListMgr.delAllFeeList(pd, td, "0", "*", "");// 删除营业费用 FeeListMgr
		 * .addFeeList(pd, td, "0", "10", iProvOpenDefltOperFee + ""); }
		 */
		// 测试 sunxin
		returnData.put("CHECK_RESULT_CODE", "1");// SIM校验成功，且服务号码成功！
		returnData.put("IMSI", "11111111111111");
		returnData.put("KI", "11111111111");
		return returnData;
	}

	/**
	 * 处理通常及特殊情况下产品信息
	 * 
	 * @param pd
	 * @param td
	 * @return
	 * @throws Exception
	 */
	public IData createProductInfo(IData data) throws Exception
	{

		IData returnData = new DataMap();
		boolean specDealTag = false;// 不同处理标志
		String strBindSingleProduct = data.getString("EXISTS_SINGLE_PRODUCT", "");
		if (data.getString("B_REOPEN_TAG").equals("1"))// 如果是二次开户，则肯定绑定的是单个产品，获取绑定的单个产品id
		{
			IData pData = getMainProduct(data.getString("USER_ID"));
			if (!pData.isEmpty())
			{
				strBindSingleProduct = pData.getString("PRODUCT_ID");
			}
		}
		String strBindDefaultTag = data.getString("EXISTS_DEFAULT_TAG", "");
		String strBindBrand = data.getString("EXISTS_BIND_BRAND", "");// 考虑前面传过来的！条件(反向条件)
		String strBindMulteProduct = data.getString("EXISTS_MULTE_PRODUCT", "");// 绑定多个产品
		String strBindDiscntCode = data.getString("EXISTS_SINGLE_DISCNT", "");// 绑定优惠，针对密码卡，优惠存在时，单个产品绑定一定存在
		// strBindMulteProduct="71004500,71004501";
		// 绑定产品集时，显示产品目录，同时将产品集对应的结果集传给"产品目录"做为入参，只显示这些产品
		String strBindMulteProductBrandCode = "";// 多产品对应的brand_code
		IData multeProductData = null;
		/*
		 * 暂时先不管，sunxin if (!StringUtils.isBlank(strBindMulteProduct)) {
		 * IDataset moreProductList = getProductInfo(strBindMulteProduct,
		 * CSBizBean.getVisit()); returnData.put("MORE_PRODUCT_LIST",
		 * moreProductList);// 给产品目录传值，只显示此列表 }
		 */

		// 判断标志位:只要有一个不为空，作特殊处理
		if (!StringUtils.isBlank(strBindSingleProduct) || !StringUtils.isBlank(strBindDefaultTag))
		{
			specDealTag = true;// 单产品
		}
		IDataset productTypeList = null;
		// 产品类型:不存在绑定产品:无特殊处理
		if (!specDealTag)
		{
			ProductTypeInfoQry.getProductsType("0000", null);
		} else
		{
			if (!StringUtils.isBlank(strBindSingleProduct))
			{
				productTypeList = getProductTypeCodeByProductId(strBindSingleProduct, BizRoute.getRouteId());

				// 号码绑定单个产品时，不显示产品目录，直接将此产品下的必选包下的必选择默认元素显示
				if (productTypeList.size() > 0)
				{
					IData productInfo = UProductInfoQry.qryProductByPK(strBindSingleProduct);
					/*
					 * 测试先屏蔽，sunxin 老代码有此处理 IDataset forceElements =
					 * getForcePackageElementsByProductId( pd, td,
					 * strBindSingleProduct, strBindDiscntCode);
					 * returnData.put("FORCE_ELEMENTS", forceElements);
					 */
					returnData.put("PRODUCT_NAME", productInfo.getString("PRODUCT_NAME"));
					returnData.put("PRODUCT_ID", strBindSingleProduct);
					returnData.put("BRAND_CODE", productInfo.getString("BRAND_CODE"));
				}
			} else
			{
				IData inData = new DataMap();
				inData.put("PARENT_PTYPE_CODE", "0000");// 个人产品类型
				// productTypeList = getAllProductCatalog(inData);
				IData productTypeData = getParentChildProductTypeList(productTypeList);
				IDataset parentProductTypeList = productTypeData.getDataset("PARENT_PRODUCT_TYPE_LIST");
				String strParentTypeCode = "";
				// 绑定默认标记
				if (!StringUtils.isBlank(strBindDefaultTag))
				{
					filterProductTypeListByDefaultTag(parentProductTypeList, strBindDefaultTag);
				}
				// 绑定 品牌
				if (!StringUtils.isBlank(strBindBrand))
				{
					filterProductTypeListByBrandCode(parentProductTypeList, strBindBrand);
				}
				// 绑定 多个产品
				// if (!isNull(strBindMulteProduct)) {
				// filterProductTypeListByMutleProduct(parentProductTypeList,
				// strBindMulteProductBrandCode);
				// }
				productTypeList = parentProductTypeList;
			}
		}
		returnData.put("PRODUCT_TYPE_LIST", productTypeList);
		/*
		 * 先屏蔽 sunxin ceshi IDataset productTypeListNew = new DatasetList();
		 * IData productTypeData = new DataMap(); String productTypeCode = "";
		 * // 非物联网开户，去物联网产品 if
		 * (!PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")) &&
		 * IDataUtil.isNotEmpty(productTypeList)) { for (int i = 0; i <
		 * productTypeList.size(); i++) { productTypeData =
		 * productTypeList.getData(i); productTypeCode =
		 * productTypeData.getString("PRODUCT_TYPE_CODE"); if
		 * (!"0800".equals(productTypeCode)) {
		 * productTypeListNew.add(productTypeData); } } } else if
		 * (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")) &&
		 * IDataUtil.isNotEmpty(productTypeList)) { // 物联网开户 for (int i = 0; i <
		 * productTypeList.size(); i++) { productTypeData =
		 * productTypeList.getData(i); productTypeCode =
		 * productTypeData.getString("PRODUCT_TYPE_CODE"); if
		 * ("0800".equals(productTypeCode)) {
		 * productTypeListNew.add(productTypeData); break; } } }
		 * returnData.put("PRODUCT_TYPE_LIST", productTypeListNew);
		 */
		return returnData;
	}

	public String getAlertNum() throws Exception
	{

		IData tmp = getTagInfo(CSBizBean.getUserEparchyCode(), "USER_OPEN_NUM_ALERT", "0");
		String alertnum = tmp.getString("TAG_NUMBER", "0");
		return alertnum;
	}

	/**
	 * 获取设备价格:一般为SIM卡费
	 * 
	 * @param pd
	 * @param td
	 * @param strResKindCode
	 * @param strCapacityTypeCode
	 * @param strProductId
	 * @param strClassId
	 * @throws Exception
	 */
	public IData getDevicePrice(IData data, String strResTypeCode, String strResKindCode, String strCapacityTypeCode, String strProductId, String strClassId) throws Exception
	{
		// 检查是否根据号码段 免卡费
		IDataset sectnoSimFeeList = null;
		IData sectnoSimData = null;
		if ("1".equals(data.getString("SPEC_SN_SECTNO_SIM_FEE", "")))
		{// 1：是根据号段免卡费
			IData inparam = new DataMap();
			inparam.put("SUBSYS_CODE", "CSM");
			inparam.put("PARAM_ATTR", "101");
			inparam.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

			if (sectnoSimFeeList != null && sectnoSimFeeList.size() > 0)
			{
				sectnoSimData = sectnoSimFeeList.getData(0);
			}
			if (sectnoSimData != null && !sectnoSimData.isEmpty())
			{
				if (data.getString("SERIAL_NUMBER").indexOf(sectnoSimData.getString("PARA_CODE2")) != -1)
				{
					return new DataMap();// 找到符合号段的记录，无卡费
				}
			}
		}
		// 检查品牌是否不收卡费
		String noCardFeeBrand = data.getString("NO_CARD_FEE_BRAND", "");
		String brandCode = data.getString("BRAND_CODE", "");// 需要后续处理，sunxin
		if (!"".equals(noCardFeeBrand))
		{// 不为空，则根据规则品牌免卡费
			if (!"".equals(brandCode))
			{
				if (noCardFeeBrand.indexOf("|" + brandCode + "|") != -1)
				{
					return new DataMap();// 找到符合号段的记录，无卡费
				}
			}
		}
		IData inParam = new DataMap();
		inParam.put("RULE_BIZ_TYPE_CODE", "TradeFeeMgr");
		inParam.put("RULE_BIZ_KIND_CODE", "TradeFeeMgr");
		inParam.put("TRADE_TYPE_CODE", "10");
		inParam.put("RES_TYPE_CODE", strResTypeCode);
		inParam.put("RES_KIND_CODE", strResKindCode);
		inParam.put("CAPACITY_TYPE_CODE", strCapacityTypeCode);
		inParam.put("PRODUCT_ID", strProductId);
		inParam.put("CLASS_ID", strClassId);
		inParam.put("EPARCHY_CODE", "");
		// 配置卡费
		IDataset devicePrices = null;
		return devicePrices == null || devicePrices.size() == 0 ? null : devicePrices.getData(0);

	}

	/**
	 * 获取是否分库标志
	 * 
	 * @author chenzm
	 * @return int
	 * @throws Exception
	 */
	public int getDiffDataBase() throws Exception
	{
		int iResult = -1;

		IDataset dataset = CommparaInfoQry.getOnlyByAttr("CSM", "1013", CSBizBean.getUserEparchyCode());

		if (!IDataUtil.isEmpty(dataset))
		{
			IData resultData = dataset.getData(0);

			// 有异地业务，需在省中心记录台帐资料
			if ("1".equals(resultData.getString("PARAM_CODE")))
			{
				iResult = 0;
			}
			// 有异地业务，无需在省中心记录台帐资料
			else if ("2".equals(resultData.getString("PARAM_CODE")))
			{
				iResult = 1;
			} else
			{
				iResult = -1;
			}
		}

		return iResult;
	}

	/**
	 * 获取用户绑定产品信息
	 * 
	 * @author chenzm
	 * @param strUserId
	 * @throws Exception
	 */
	public IData getMainProduct(String strUserId) throws Exception
	{
		IData data = UcaInfoQry.qryMainProdInfoByUserId(strUserId);
		return data;
	}

	/**
	 * 根据铁通号码获取移动号码资源
	 * 
	 * @author chenzm
	 * @param data
	 * @throws Exception
	 */
	public IData getMobilePhoneByTieTongNumber(IData data) throws Exception
	{
		IDataset mobilePhoneList = ResCall.getMobilePhoneByTieTongNumber(data.getString("TIETONG_NUMBER"));
		boolean hasMphoneFlag = false;
		IData mphoneData = new DataMap();
		if (mobilePhoneList != null && mobilePhoneList.size() > 0)
		{
			String mphoneNumber = mobilePhoneList.getData(0).getString("PARA_VALUE1", "");
			if (!StringUtils.isBlank(mphoneNumber))
			{
				hasMphoneFlag = true;
				mphoneData.put("HAS_MPHONE_FLAG", "true");
				mphoneData.put("SERIAL_NUMBER", mphoneNumber);
			}
		}
		// 没有找到对应的移动号码
		if (!hasMphoneFlag)
		{
			mphoneData.put("HAS_MPHONE_FLAG", "false");
			mphoneData.put("SERIAL_NUMBER", "");
			mphoneData.put("HINT_MESSAGE", "该铁通号码【" + data.getString("TIETONG_NUMBER") + "】无对应的移动号码资料！");
		}
		return mphoneData;
	}

	/**
	 * 获取号码归属
	 * 
	 * @author chenzm
	 * @param data
	 * @param intDiffData
	 * @throws Exception
	 */
	public IData getMphoneAddress(IData data, int intDiffData) throws Exception
	{
		boolean bSameEparchyCode = true;
		boolean bDiffTrade = false;
		String strRouteEparchyCode = "";
		String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
		// 根据号码获取路由的方法，需要公用出来，最好放专门的路由类，或放基类里，直接调用
		String strEparchyCode = RouteInfoQry.getEparchyCodeBySn(data.getString("SERIAL_NUMBER"));
		if (StringUtils.isBlank(strEparchyCode))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_475, data.getString("SERIAL_NUMBER"));
		}

		// 异地业务
		if (!strEparchyCode.equals(tradeEparchyCode))
		{
			bSameEparchyCode = false;
			strRouteEparchyCode = strEparchyCode;

			if (intDiffData == 0)
			{
				bDiffTrade = true; // 省中心记台帐
			} else
			{
				bDiffTrade = false; // 省中心不记台帐
			}
		} else
		{
			strRouteEparchyCode = tradeEparchyCode;
			bDiffTrade = false; // 非异地
		}

		IData temp = new DataMap();
		temp.put("B_SAME_EPARCHY_CODE", bSameEparchyCode ? "0" : "1"); // 0:同地市
																		// ;1:不同地市
		temp.put("B_DIFF_TRADE", bDiffTrade ? "0" : "1"); // 是否登记省中心台帐:0:登记;1:不登记

		return temp;
	}

	/**
	 * 省内异地开户根据所选地州和号码查询开户号码
	 * 
	 * @author chenzm
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset getProductFeeInfo(IData data) throws Exception
	{

		String product_id = data.getString("PRODUCT_ID");
		String brand_code = data.getString("BRAND_CODE");
		String eparchy_code = CSBizBean.getUserEparchyCode();
		// IDataset dataset = ProductFeeInfoQry.getProductFeeInfo("10",
		// product_id, "-1", "-1", brand_code, "3", eparchy_code);
		IDataset dataset = UpcCall.qryDynamicPrice(product_id, brand_code, "-1", null, "10", null, null, null);
		return dataset;

	}

	/**
	 * 获取产品类型 iMode 0-普通开户(神州行除外) 1-神州行开户 2-所有
	 * 
	 * @author chenzm
	 * @param iMode
	 * @return
	 * @throws Exception
	 */
	public String getProductTypeByFilter(int iMode) throws Exception
	{
		String filterDefaultTag = "";

		if (iMode == 0 || iMode == 1)
		{
			if (iMode == 0)
			{
				filterDefaultTag = "1,2";// DEFAULT_TAG
			} else if (iMode == 1)
			{
				filterDefaultTag = "6";// DEFAULT_TAG
			}
		}

		return filterDefaultTag;
	}

	/**
	 * 证件下欠费销户数检查
	 * 
	 * @author chenzm
	 * @param data
	 * @return int
	 * @throws Exception
	 */
	public int getQfXhCnt(IData data) throws Exception
	{

		int num = 0;
		String pspt_type_code = data.getString("PSPT_TYPE_CODE");
		String pspt_id = data.getString("PSPT_ID");
		String eparchy_code = CSBizBean.getUserEparchyCode();
		// 已欠费销户用户数
		IDataset restult = UserInfoQry.getQfxhUserInfoByPspt(pspt_type_code, pspt_id, eparchy_code);

		if (restult != null && restult.size() > 0)
		{
			num = restult.getData(0).getInt("QFXH_NUM", 0);
		}
		return num;
	}

	/**
	 * 查询td_s_tag表参数
	 * 
	 * @param strEparchyCode
	 * @param tagCode
	 * @param userTag
	 * @return
	 * @throws Exception
	 */
	public IData getTagInfo(String strEparchyCode, String tagCode, String userTag) throws Exception
	{
		IDataset tagList = new DatasetList();
		tagList = TagInfoQry.getTagInfo(strEparchyCode, tagCode, userTag, null);
		return IDataUtil.isEmpty(tagList) ? new DataMap() : tagList.getData(0);
	}

	/**
	 * 获取证件下开户的号码资料
	 * 
	 * @author chenzm
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset getUserInfoByPspt(IData data) throws Exception
	{
		IDataset userList = new DatasetList();
		IData userData = new DataMap();
		IData owefeeData = new DataMap();
		String psptTypeCode = data.getString("PSPT_TYPE_CODE");
		String psptId = data.getString("PSPT_ID");
		IDataset userList0 = UserInfoQry.getAllUserInfoByPsptId(psptTypeCode, psptId, "0", CSBizBean.getUserEparchyCode());
		data.put("REMOVE_TAG", "3");
		IDataset userList3 = UserInfoQry.getAllUserInfoByPsptId(psptTypeCode, psptId, "3", CSBizBean.getUserEparchyCode());
		data.put("REMOVE_TAG", "4");
		IDataset userList4 = UserInfoQry.getAllUserInfoByPsptId(psptTypeCode, psptId, "4", CSBizBean.getUserEparchyCode());
		userList.addAll(userList0);
		userList.addAll(userList3);
		userList.addAll(userList4);
		if (IDataUtil.isNotEmpty(userList))
		{
			while (userList.size() > 10)
			{
				userList.remove(userList.size() - 1);
			}
			for (int i = 0; i < userList.size(); i++)
			{
				userData = userList.getData(i);
				String userID = userData.getString("USER_ID");
				owefeeData = AcctCall.getOweFeeByUserId(userID);
				String all_new_balance = owefeeData.getString("ACCT_BALANCE", "0.00");
				userData.put("LEFT_FEE", (Integer.parseInt(all_new_balance) / 100) + "元");
			}
		}
		return userList;
	}

	/**
	 * 界面初始化方法
	 * 
	 * @author chenzm
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData InitPara(IData data) throws Exception
	{
		IData returnData = new DataMap();

		IData rDualInfo;
		String strTagInfo = "";
		String strTagChar = "";
		String strTagNumber = "";
		String strOpenType = data.getString("OPEN_TYPE", ""); // 是否代理商开户，通过地址栏参数
		String strEparchyCode = CSBizBean.getTradeEparchyCode();

		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_CANPREOPEN", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("CAN_PRE_OPEN", strTagChar);// 标识页面"预约开户"checkbox可用否

		// 获取是否允许合户
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_ISSAMEACCT", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("IS_SAME_ACCT", strTagChar);

		// 获取黑名单提示方式标记
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_BLACKCHECKMODE", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("BLACK_CHECK_MODE", strTagChar);

		// 获取开户是否打印票据(0:不打印，1:根据前台选择打印，默认不打印)
		boolean printEnabledTag = true;// 普通用户默认为打印，处理代理商开户打印标记
		rDualInfo = getTagInfo(strEparchyCode, "CS_ALL_CREATEUSERPRINT", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		strTagInfo = rDualInfo.getString("TAG_INFO", "");
		strTagNumber = rDualInfo.getString("TAG_NUMBER", "0");

		/*
		 * if (strCurRightCode.equals(strTagInfo)) {//
		 * 控制不同地市是否需要打印，一般strTagInfo为菜单编码RIGHT_CODE if ("1".equals(strTagChar))
		 * {// 0:不打印，1:打印 printEnabledTag = "1".equals(strTagNumber) ? true :
		 * false; } else { printEnabledTag = false; } }
		 * dualInfo.put("CHK_PRINT_ENABLE", printEnabledTag);//
		 * 业务登记时，根据此标记是否提示打印(是否显示打印按钮) 先屏蔽 sunxin
		 */

		// 获取特殊号段免卡费标记
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_SPECSNSECTNOSIMFEE", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("SPEC_SN_SECTNO_SIM_FEE", strTagChar);

		// 获取开户免卡费(不收卡费)的品牌信息
		rDualInfo = getTagInfo(strEparchyCode, "CS_INF_NOCARDFEEBRAND", "0");
		strTagInfo = rDualInfo.getString("TAG_INFO", "");
		returnData.put("NO_CARD_FEE_BRAND", strTagInfo);

		// 返单开户已有客户资料是否copy客户资料
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_RETURNOPENCOPYCUST", "0");
		strTagInfo = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("RETURN_OPEN_COPY_CUST", strTagInfo);

		// 获取默认预存款存折标记
		rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_OPENPREPAYDEPOSIT", "0");
		strTagNumber = rDualInfo.getString("TAG_NUMBER", "0");
		returnData.put("PRE_PAY_DEPOSIT", strTagNumber);

		// 获取省内跨区开户默认预存款标记
		rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PROVOPENADVANCEPAY", "0");
		strTagInfo = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("PROV_OPEN_ADVANCE_PAY", strTagInfo);// 预存款标记

		// 获取省内跨区开户默认卡费标记
		rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PROVOPENOPERFEE", "0");
		strTagInfo = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("PROV_OPEN_OPERFEE", strTagInfo);

		// 获取是否支持购座机入网
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_OPENBUYDESKDEVICE", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("OPEN_BUY_DESKDEVICE", strTagChar);
		// 获取默认开户用户数
		rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_OPENLIMITCOUNT", "0");
		strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
		returnData.put("OPEN_LIMIT_COUNT", strTagNumber);
		// 获取智能网在前台是否允许收费标志
		rDualInfo = getTagInfo(strEparchyCode, "CS_CSM_GSFEETAG", "0");

		strTagInfo = rDualInfo.getString("TAG_INFO", "");
		returnData.put("GS_FEE_TAG", strTagInfo);
		// 获取开户确认时是否判断黑名单停机标志
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_JUDGEBLACKSTOP", "0");

		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("JUDGE_BLACK_STOP", strTagChar);
		// 获取本省代码
		rDualInfo = getTagInfo(strEparchyCode, "PUB_INF_PROVINCE", "0");

		strTagInfo = rDualInfo.getString("TAG_INFO", "");
		returnData.put("PROVINCE", strTagInfo);
		// 获取是否显示用户提示信息的标记
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_SHOWHINTINFO", "0");

		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("SHOW_HINT_INFO", strTagChar);
		// 获取默认密码的使用方式
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPWDMODE", "0");

		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("DEFAULT_PWD_MODE", strTagChar);
		// 获取默认密码
		rDualInfo = getTagInfo(strEparchyCode, "CS_INF_DEFAULTPWD", "0");

		strTagInfo = rDualInfo.getString("TAG_INFO", "");
		returnData.put("DEFAULT_PWD", strTagInfo);
		// 获取密码长度
		rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PASSWORDLENGTH", "0");

		strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
		returnData.put("DEFAULT_PWD_LENGTH", strTagNumber);
		// 获取业务办理身份验证顺序
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_IDCHKDEALDISMODE", "0");

		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("ID_CHKDEALDIS_MODE", strTagChar);
		// 获取是否使用密码键盘标记
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_USEPASSWDKEYBOARD", "0");

		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("USE_PASSWD_KEYBOARD", strTagChar);
		// 获取业务区句柄
		rDualInfo = getTagInfo(strEparchyCode, "CS_INF_CITYCODEHANDLE", "0");
		// td_m_area30
		strTagInfo = rDualInfo.getString("TAG_INFO", "");
		returnData.put("CITY_CODE_HANDLE", strTagInfo);

		// 获取押金的名称
		rDualInfo = getTagInfo(strEparchyCode, "CS_INF_FOREGIFTNAME", "0");

		strTagInfo = rDualInfo.getString("TAG_INFO", "");
		returnData.put("FOREGIFT_NAME", strTagInfo);
		// 获取服务参数值的显示方式
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_SERVPARAMODE", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("SERV_PARAM_MODE", strTagChar);
		// 获取是否提示重新打印标志
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_ISAFRESHPRINT", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("IS_AFRESH_PRINT", strTagChar);
		// 获取提示信息蓝框控制标志
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_HINTINFOCONTROL", "0");

		strTagInfo = rDualInfo.getString("TAG_INFO", "");
		returnData.put("HINTINFO_CONTROL", strTagInfo);
		// 获取员工是否有费用减免权限SYS002
		// 默认用户类型
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTUSERTYPE", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("DEFAULT_USER_TYPE", strTagChar);
		// 默认证件类型
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPSPTTYPE", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("DEFAULT_PSPT_TYPE", strTagChar);
		// 默认帐户类型
		rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPAYMODE", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "");
		returnData.put("DEFAULT_PAY_MODE", strTagChar);
		returnData.put("OPEN_TYPE", strOpenType);

		// returnData.put("REUSE_TYPE",
		// "".equals(strReuseType)?td.getString("REUSE_TYPE",""):strReuseType);
		// 先屏蔽 sunxin
		// 代理商开户是否只使用操作员部门(登录员工部门)进行资料校验：0,不使用登录员工部门,1,仅使用操作员工部门
		rDualInfo = getTagInfo(strEparchyCode, "CS_RESCHECK_BYDEPART", "0");
		strTagChar = rDualInfo.getString("TAG_CHAR", "0");
		returnData.put("RES_CHECK_BY_DEPART", strTagChar);

		// initSelectParam 暂时不处理，sunxin

		return returnData;
	}

	/**
	 * 查询某个工号的登陆时间限制
	 * 
	 * @author chenzm
	 * @param data
	 * @throws Exception
	 */
	public void IsCanOperate(IData data) throws Exception
	{
		// 查询配置，如果不做限制，则直接返回
		IDataset dsCommpara = CommparaInfoQry.getCommparaAllCol("CSM", "3270", "10", getVisit().getStaffEparchyCode());
		if (IDataUtil.isEmpty(dsCommpara) || !"1".equals(dsCommpara.getData(0).getString("PARA_CODE1")))
		{
			return;
		}
		String errorcontent = dsCommpara.getData(0).getString("PARA_CODE23");

		// 根据员工查询登录时间
		IData inData = new DataMap();
		inData.put("STAFF_ID", getVisit().getStaffId());
		IDataset resultList = StaffInfoQry.queryDateTimeByStaffId(getVisit().getStaffId());
		if (resultList != null && resultList.size() > 0)
		{
			String sysdate = SysDateMgr.getSysDate();
			String flag = "0";
			for (int i = 0; i < resultList.size(); i++)
			{
				if (!(sysdate.compareTo(resultList.getData(i).getString("ALLOW_STARTTIME")) < 0) && !(resultList.getData(i).getString("ALLOW_ENDTIME").compareTo(sysdate) < 0))
				{
					flag = "1";
					break;

				}
			}

			if ("0".equals(flag))
			{
				CSAppException.apperr(BizException.CRM_BIZ_5, errorcontent);
			}
		}
	}

	/**
	 * 证件下最大用户开户数检查
	 * 
	 * @author chenzm
	 * @param data
	 * @return int
	 * @throws Exception
	 */
	public int JudgeOpenLimit(IData data) throws Exception
	{
		int openNum = 0;
		String pspt_type_code = data.getString("PSPT_TYPE_CODE");
		String pspt_id = data.getString("PSPT_ID");
		// 已开户用户数
		IDataset openList = UserInfoQry.getUserInfoByPsptEx(pspt_type_code, pspt_id, CSBizBean.getUserEparchyCode());

		if (openList != null && openList.size() > 0)
		{
			openNum = openList.getData(0).getInt("OPEN_NUM", 0);
		}

		// 某些证件类型不限制最多只允许办理5个移动号码 add by chenzm
		IData rDualInfo = getTagInfo(CSBizBean.getUserEparchyCode(), "CS_NUM_OPENLIMITCOUNT", "0");
		String tagInfo = rDualInfo.getString("TAG_INFO", "");
		if (tagInfo.indexOf(pspt_type_code) > -1)
		{
			return openNum;
		}
		// 是否证件开户限制
		int openLimitNum = data.getInt("OPEN_LIMIT_COUNT", 0);
		if (openLimitNum > 0)
		{
			if (openList != null && openList.size() > 0)
			{
				openNum = openList.getData(0).getInt("OPEN_NUM", 0);
			}
			// 比较
			if (openNum >= openLimitNum)
			{
				CSAppException.apperr(CustException.CRM_CUST_59, openLimitNum);
			}
		}
		return openNum;
	}

	/**
	 * 二次开户中获取除三户外的其他信息
	 * 
	 * @author chenzm
	 * @param userInfo
	 * @throws Exception
	 */
	public IData loadOtherInfo(IData userInfo) throws Exception
	{

		IData inData = new DataMap();
		String user_id = userInfo.getString("USER_ID");
		IDataset dataset = UserResInfoQry.queryUserResByUserIdResType(user_id, "1");
		if (dataset.isEmpty())
			CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.NO_FOUND_DATA_IN_RES_TABLE);
		String simCardNo = dataset.getData(0).getString("RES_CODE");
		inData.put("SIM_CARD_NO", simCardNo);
		return inData;
	}

	/**
	 * 开户营销费用重算
	 * 
	 * @author chenzm
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset mputeFee(IData data) throws Exception
	{
		IDataset feelist = new DatasetList(data.getString("FEE_LIST"));
		IDataset feeDatas = new DatasetList();
		IDataset resultFee = new DatasetList();
		// 开户费用
		IDataset openUserFee = new DatasetList();
		// 营销活动费用
		IDataset saleactiveFee = new DatasetList();
		for (int i = 0; i < feelist.size(); i++)
		{
			IData temp = feelist.getData(i);
			if ("10".equals(temp.getString("TRADE_TYPE_CODE", "")))
			{
				openUserFee.add(temp);
			} else
			{
				saleactiveFee.add(temp);
			}
		}
		IDataset comparamInfos = CommparaInfoQry.getCommparaInfoBy5("CSM", "2001", "SALEACTIVEFILTER4CREATEUSER", data.getString("SALE_PRODUCT_ID"), CSBizBean.getTradeEparchyCode(), null);
		if (IDataUtil.isEmpty(comparamInfos))
		{

		} else
		{
			String getFeeType = comparamInfos.getData(0).getString("PARA_CODE2");
			// 费用重叠
			if ("0".equals(getFeeType))
			{

			}
			// 只取营销活动费用
			else if ("1".equals(getFeeType))
			{
				// 计算后的费用

				resultFee.addAll(saleactiveFee);
				delDepositFee(openUserFee);
				resultFee.addAll(openUserFee);

			}// 预存就高
			else if ("2".equals(getFeeType))
			{
				int iOpenFee = 0;
				int iSaleFee = 0;
				int fee = 0;
				IData temp = new DataMap();
				for (int i = 0; i < openUserFee.size(); i++)
				{
					temp = openUserFee.getData(i);
					if ("2".equals(temp.getString("FEE_MODE")) && !existFeeType(temp.getString("FEE_TYPE_CODE")))
					{
						fee = Integer.parseInt(temp.getString("FEE"));
						iOpenFee = iOpenFee + fee;
					}
				}
				fee = 0;
				for (int i = 0; i < saleactiveFee.size(); i++)
				{
					temp = saleactiveFee.getData(i);
					if ("2".equals(temp.getString("FEE_MODE")))
					{
						fee = Integer.parseInt(temp.getString("FEE"));
						iSaleFee = iSaleFee + fee;
					}
				}
				// 预存就高，营销活动的钱还是保留，开户预存减去营销活动的钱
				if (iOpenFee > iSaleFee)
				{
					for (int i = 0; i < openUserFee.size(); i++)
					{
						temp = openUserFee.getData(i);
						if ("2".equals(temp.getString("FEE_MODE")))
						{
							fee = Integer.parseInt(temp.getString("FEE"));
							if (fee > iSaleFee)
							{
								temp.put("OLDFEE", String.valueOf(fee - iSaleFee));
								temp.put("FEE", String.valueOf(fee - iSaleFee));
								break;
							}
						}
					}
				} else
				{
					delDepositFee(openUserFee);
				}

				resultFee.addAll(saleactiveFee);
				resultFee.addAll(openUserFee);
			}
		}
		for (int i = 0; i < resultFee.size(); i++)
		{
			IData feeConfig = resultFee.getData(i);
			IData feeData = new DataMap();
			feeData.put("TRADE_TYPE_CODE", feeConfig.getString("TRADE_TYPE_CODE"));
			feeData.put("MODE", feeConfig.getString("FEE_MODE"));
			feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
			feeData.put("FEE", feeConfig.getString("OLDFEE"));
			if (StringUtils.isNotBlank(feeConfig.getString("DISCNT_GIFT_ID")))
			{
				feeData.put("ELEMENT_ID", feeConfig.getString("DISCNT_GIFT_ID"));
			}
			feeDatas.add(feeData);
		}
		return feeDatas;
	}

	/**
	 * 获取全量结果：产品类型
	 * 
	 * @param pd
	 * @param inData
	 * @return
	 * @throws Exception
	 */

	/**
	 * 开户代理商下的村级服务站选择
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset queryAgent(IData data) throws Exception
	{
		String strEpachyCode = CSBizBean.getTradeEparchyCode();
		String strCityCode = CSBizBean.getVisit().getCityCode();
		String strDepartId = CSBizBean.getVisit().getDepartId();
		String strParaCode = data.getString("DEPART_ID", "");
		IDataset result = new DatasetList();
		if (!StringUtils.isBlank(strParaCode))
		{
			if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS_CRM_AgentCreateUser"))
			{
				result = DepartInfoQry.getAgentInfoByDepartId(strEpachyCode, strCityCode, strDepartId, strParaCode);
			} else
			{
				result = DepartInfoQry.getAgentInfoByNotManagerId(strEpachyCode, strCityCode, strParaCode);
			}
		}
		return result;
	}

	/**
	 * 开户根据所选身份证和来源查询开户号码
	 * 
	 * @author chenzm
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset queryIDlePhone(IData data) throws Exception
	{

		String psptId = data.getString("PSPT_ID", "");// 证件号码
		String netchooseType = data.getString("NETCHOOSE_TYPE", "");// 选号来源
		// IDataset dataset = TuxedoHelper.callTuxSvc(pd, "TRM_IResOccupyUse",
		// param); 调用资源接口，需要修改 孙鑫
		IDataset dataset = ResCall.queryIDlePhone(psptId, netchooseType, "", "");
		return dataset;

	}

	/**
	 * @Description: 查询返乡标记
	 * @author: dingyang
	 * @date 2013-12-4 下午02:18:47
	 */
	public IDataset queryReturnTaginfo(IData data) throws Exception
	{
		data.put("TAG_CODE", "CS_CHR_RETURNTAG");
		IDataset result = TagInfoQry.getSysTagInfo2(data);
		if (result.size() > 0)
		{
			IDataset r = BreQryForCommparaOrTag.getCommpara("CSM", 2456, data.getString("PRODUCT_ID"), data.getString("EPARCHY_CODE"));
			return r;
		}
		return null;
	}

	/**
	 * 获取产品信息用于产品目录选择(多产品时)
	 * 
	 * @param pd
	 * @param strBindProducts
	 * @param strEparchyCode
	 * @return
	 * @throws Exception
	 */
	/**
	 * 开户-身份证判定是否黑名单用户 REQ201510090022 关于新建黑名单库的需求 chenxy3 20151022
	 */
	public IDataset checkPsptidBlackListInfo(IData input) throws Exception
	{
		IDataset callSet = null;
		String psptId = input.getString("PSPT_ID", "");
		callSet = AcctCall.qryBlackListByPsptId(input);// 调账务接口查黑名单

		return callSet;
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
		log.error("CreatePersonUserBean.verifyIdCard 1830");
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
        NationalOpenLimitBean nobean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
		IDataset checkResults = nobean.checkPspt(psptTypeCode);
		
		//Q-港澳台居民居住证-16 CARD_PASS_NO 通行证号码 证件类型为16时， 必填      营业界面没有此值
		if ((psptTypeCode.equals("O") || psptTypeCode.equals("N") || psptTypeCode.equals("P")  || psptTypeCode.equals("Q")
				|| psptTypeCode.equals("W")) && IDataUtil.isNotEmpty(checkResults)){

			String stafftag = isReality(ibossParam);
			//开关关闭或者有免查验功能
			if("1".equals(stafftag)) {
				result.put("X_RESULTCODE", "0");
				return result;
			}

			ibossParam.put("KIND_ID", "OTHER_CERT_REALITY_VERIFY_10085_0_0");
			ibossParam.put("SOURCE_CODE", "898");
			ibossParam.put("TRANSACTION_ID", tranId.toString());
			ibossParam.put("USER_NAME", loginUser);
			ibossParam.put("PASSWORD", loginPass);
			ibossParam.put("CERT_TYPE", checkResults.getData(0).getString("PARA_CODE1"));
			ibossParam.put("CUST_CERT_NO", psptId);
			ibossParam.put("CARD_PASS_NO", "");
			ibossParam.put("CUST_NAME", psptName);
			ibossParam.put("ADDRESS", "");
			ibossParam.put("ISSUING_AUTHORITY", param.getString("CERT_DEPART"));
			ibossParam.put("CERT_VALIDDATE", param.getString("CERT_VALIDDATE"));
			ibossParam.put("CERT_EXPDATE", param.getString("CERT_EXPDATE"));
			ibossParam.put("BIRTHDAY", param.getString("CERT_BIRTHDAY"));
			ibossParam.put("NATIONALITY", param.getString("CERT_NATIONAL","CHN"));
			ibossParam.put("SEX", "");
			ibossParam.put("VERIFY_TYPE", "1");
			ibossParam.put("BILL_ID", param.getString("SERIAL_NUMBER", "00000000000"));
			ibossParam.put("BUSI_TYPE", param.getString("BUSI_TYPE", "2"));
			ibossParam.put("NODE_CODE", "01200001089801");
			ibossParam.put("para_code1", "1");
		}else{
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
		}
		//REQ201908060023 关于联网核验时需要反馈请求网点的渠道编码的通知
		ibossParam.put("NODE_CODE", qryNodeCode());

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
					result.put("SEQ",resultIboss.getString("SEQ"));
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

	//其他证件人像查验开关权限
	public String isReality(IData param) throws Exception
	{
		String tag = "1";
		// 海南人像查验比对功能开关配置
		IDataset comptag = CommparaInfoQry.getCommparaInfos("CSM", "2119", "OTHERPIC_COMP_TAG");
		if (null != comptag && IDataUtil.isNotEmpty(comptag))
		{// 人像查验比对开关获取
			if ("0".equals(comptag.getData(0).getString("PARA_CODE1")))
			{// 开关打开
				if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "OTHERNOTREA_STFPRV"))
				{// 有免查验比对权限
					tag = "1";
				} else
				{
					tag = "0";
				}
			} else if ("1".equals(comptag.getData(0).getString("PARA_CODE1")))
			{// 开关关闭
				if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "OTHERREA_STFPRV"))
				{// 需要查验比对
					tag = "0";
				} else
				{
					tag = "1";
				}
			}
		}
		return tag;
	}

	//其他证件人像比对开关权限
	public String isCompara(IData param) throws Exception
	{
		String tag = "1";
		// 海南人像查验比对功能开关配置
		IDataset comptag = CommparaInfoQry.getCommparaInfos("CSM", "2119", "OTHERPIC_COMP_TAG");
		if (null != comptag && IDataUtil.isNotEmpty(comptag))
		{// 人像查验比对开关获取
			if ("0".equals(comptag.getData(0).getString("PARA_CODE2")))
			{// 开关打开
				if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "OTHERNOTCOM_STFPRV"))
				{// 有免查验比对权限
					tag = "1";
				} else
				{
					tag = "0";
				}
			} else if ("1".equals(comptag.getData(0).getString("PARA_CODE2")))
			{// 开关关闭
				if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "OTHERCOM_STFPRV"))
				{// 需要查验比对
					tag = "0";
				} else
				{
					tag = "1";
				}
			}
		}
		return tag;
	}

	/**
     * 获取经办人一个自然月证件号的数量
     * @param input
     * @return
     * @throws Exception
     */
	public IDataset AgentIdCardMonth(IData param) throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) AGENT_NUM from tf_f_user_pspt t where t.user_type='1' ");
		sql.append("and t.pspt_id= :PSPT_ID ");
		sql.append("and to_char(t.insert_date,'yyyymm')=to_char(sysdate,'yyyymm') ");
		
		return Dao.qryBySql(sql, param);
	}
	
	/**
     * 获取一年内经办人证件号的数量
     * @param input
     * @return
     * @throws Exception
     */
	public IDataset AgentIdCardYear(IData param) throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) AGENT_NUM from tf_f_user_pspt t where t.user_type='1' ");
		sql.append("and t.pspt_id= :PSPT_ID ");
		sql.append("and to_char(t.insert_date,'yyyy')=to_char(sysdate,'yyyy') ");
		
		return Dao.qryBySql(sql, param);
	}
	
	
	/**
	 * 人像信息比对
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author dengyi
	 */
	public IData cmpPicInfo(IData param) throws Exception
	{

		IData result = new DataMap();
		String psptId = param.getString("CERT_ID", "");// 证件号码
		String psptName = param.getString("CERT_NAME", "").replaceAll("", "");// 姓名
		String psptTypeCode = param.getString("CERT_TYPE", "").trim(); // 证件类型
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		String seqRealId = SeqMgr.getRealId();
		StringBuffer tranId = new StringBuffer().append("898").append(date).append(seqRealId);
		String loginUser = "g!ZIQ+";
		String loginPass = "aOG$GQq~A";
		IDataset results = CommparaInfoQry.getCommNetInfo("CSM", "1085", "LOGIN_INFO_HAINAN");

		if (IDataUtil.isNotEmpty(results))
		{
			loginUser = results.getData(0).getString("PARA_CODE1");
			loginPass = results.getData(0).getString("PARA_CODE2");
		}
		// 人像比对接口入参
		IData resultIboss = null;
		IData inParam = new DataMap();
		boolean tag = false;

		IData stafftag = isCmpPic(inParam);
		if ("0".equals(stafftag.getString("CMPTAG", "1")))
		{// 员工要求进行人像比对
			tag = true;
		}
		log.debug("=====================tag_1:"+tag);
		if (tag)
		{
			tag = false;// 重置比对标签
			// tag为联网比对标志，标志满足客户或经办人证件类型为身份证、军人身份证或户口本为true,否则为false不进行比对
			if (psptTypeCode.length() > 0)
			{
				IDataset cretType = CommparaInfoQry.getCommParas("CSM", "2016", "PIC_COMP_CERTTYPE", psptTypeCode, "");
				if (null != cretType && IDataUtil.isNotEmpty(cretType))
				{// 证件类型符合要求，则进行人像比对
					tag = true;
				}
			}
			log.debug("=====================tag_2:"+tag);
			if (tag)
			{
				String tradeTypeCode = param.getString("BLACK_TRADE_TYPE", "");
				//Q-港澳台居民居住证-16 CARD_PASS_NO 通行证号码 证件类型为16时， 必填      营业界面没有此值
				if ((psptTypeCode.equals("O") || psptTypeCode.equals("N") || psptTypeCode.equals("P")  || psptTypeCode.equals("Q")
						|| psptTypeCode.equals("W"))) {

					String ftag = isCompara(inParam);
					//开关关闭或者有免查验功能
					if ("1".equals(ftag)) {
						result.put("X_RESULTCODE", "0");
						return result;
					}

					NationalOpenLimitBean nobean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
					IDataset checkResults = nobean.checkPspt(psptTypeCode);

					inParam.put("KIND_ID", "OTHER_CERT_COMPARE_SERVICE_10085_0_0");
					inParam.put("SOURCE_CODE", "898");
					inParam.put("TRANSACTION_ID", tranId.toString());
					inParam.put("USER_NAME", loginUser);
					inParam.put("PASSWORD", loginPass);
					inParam.put("CERT_TYPE", checkResults.getData(0).getString("PARA_CODE1"));
					inParam.put("CUST_CERT_NO", psptId);
					inParam.put("CARD_PASS_NO", "");
					inParam.put("CUST_NAME", psptName);
					inParam.put("ADDRESS", "");
					inParam.put("ISSUING_AUTHORITY", param.getString("CERT_DEPART"));
					inParam.put("CERT_VALIDDATE", param.getString("CERT_VALIDDATE"));
					inParam.put("CERT_EXPDATE", param.getString("CERT_EXPDATE"));
					inParam.put("BIRTHDAY", param.getString("CERT_BIRTHDAY"));
					inParam.put("NATIONALITY", param.getString("CERT_NATIONAL","CHN"));
					inParam.put("SEX", "");
					inParam.put("VERIFY_TYPE", "1");
					inParam.put("BILL_ID", param.getString("SERIAL_NUMBER", "00000000000"));
					inParam.put("NODE_CODE", "01200001089801");
				}else if("3".equals(psptTypeCode)) {// 军人身份证类型需要调用的接口：CUST_PIC_COMPARE_SERVICE_FOR_JS_10085_0_0
					inParam.put("KIND_ID", "CUST_PIC_COMPARE_SERVICE_FOR_JS_10085_0_0");
					inParam.put("SOURCE_CODE", "898");
					inParam.put("TRANSACTION_ID", tranId.toString());
					inParam.put("USER_NAME", loginUser);
					inParam.put("PASS_WORD", loginPass);
					inParam.put("CUST_CERT_NO", psptId);
					inParam.put("CUST_NAME", psptName);
					inParam.put("BILL_ID", param.getString("SERIAL_NUMBER", "00000000000"));
					inParam.put("IS_HANDLE_CARD", "0");
				}else {
					// 对本地身份证、外地身份证、2种证件类型进行联网比对
					inParam.put("KIND_ID", "CUST_PIC_COMPARE_SERVICE_10085_0_0");
					inParam.put("TRANSACTION_ID", tranId.toString());
					inParam.put("USER_NAME", loginUser);
					inParam.put("PASS_WORD", loginPass);
					inParam.put("CUST_CERT_NO", psptId);
					inParam.put("CUST_NAME", psptName);
					inParam.put("BILL_ID", param.getString("SERIAL_NUMBER", "00000000000"));
					inParam.put("IS_HANDLE_CARD", "0");

				}
				inParam.put("para_code1", "1");
				if ("100".equals(tradeTypeCode))
				{// 过户
					inParam.put("BUSI_TYPE", "3");
				} else
				{// 新客户入网
					inParam.put("BUSI_TYPE", "1");
				}
				String PIC_NAME_R = URLDecoder.decode(param.getString("PIC_STREAM", ""), "utf-8");
				String PIC_NAME_T = URLDecoder.decode(param.getString("FRONTBASE64", ""), "utf-8");
				inParam.put("PIC_NAME_R", PIC_NAME_R);
				inParam.put("PIC_NAME_T", PIC_NAME_T);
				/**
				 * REQ201710230007_人像比对日志日志记录优化
				 * @author zhuoyingzhi
				 * @date 20180413
				 */
				//手机号码
				inParam.put("ROUTEVALUE", inParam.getString("BILL_ID",""));

				//REQ201908060023 关于联网核验时需要反馈请求网点的渠道编码的通知
				inParam.put("NODE_CODE", qryNodeCode());
				
				try
				{
					if (log.isDebugEnabled())
					{
						log.error("----IBOSS-param-huanghua----:" + inParam);
					}
					IDataset dataset = IBossCall.callHttpIBOSS4("IBOSS", inParam);
					log.debug("=====================dataset:"+dataset);
					resultIboss = (dataset == null || dataset.isEmpty()) ? null : dataset.getData(0);
				} catch (Exception e)
				{
					log.error(e);
					String errStr = e.getMessage();
					CSAppException.apperr(CrmCommException.CRM_COMM_103, errStr);
				}
			}
			log.debug("=====================resultIboss:"+resultIboss);
			if (resultIboss != null && resultIboss.size() > 0)
			{
				String return_message = resultIboss.getString("RETURN_MESSAGE", "");
				if ("0000".equals(resultIboss.getString("RETURN_CODE", "")))
				{
					if (resultIboss.getString("VERIFY_RESULT", "").equals("0"))
					{
						result.put("SEQ",resultIboss.getString("SEQ"));
						result.put("X_RESULTCODE", "0");
						return_message="人像比对成功";
					} else
					{
						result.put("X_RESULTCODE", "1");
						return_message = "人像比对失败,摄像结果与身份证照片不符，请重新摄像";
					}
					result.put("X_RESULTINFO", return_message);
					/**
					 * REQ201706130001_关于录入联网核验情况的需求
					 * @author zhuoyingzhi
					 * @date 20170919
					 */
					insertCheckInfoLog(param, return_message, "核查完成");
				} else
				{
					result.put("X_RESULTCODE", "1");
					result.put("X_RESULTINFO", "人像比对失败");
					
					/**
					 * REQ201706130001_关于录入联网核验情况的需求
					 * @author zhuoyingzhi
					 * @date 20170919
					 */
					insertCheckInfoLog(param, "调用在线公司接口返回异常", "核查未成");
				}
			}
		}
		return result;
	}

	/**
	 * 人像信息比对员工信息
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author dengyi
	 */
	public IData isCmpPic(IData param) throws Exception
	{
		IData result = new DataMap();
		String tag = "1";
		// 海南人像比对功能开关配置
		IDataset comptag = CommparaInfoQry.getCommparaInfos("CSM", "2016", "PIC_COMP_TAG");
		if (null != comptag && IDataUtil.isNotEmpty(comptag))
		{// 人像比对开关获取
			if ("0".equals(comptag.getData(0).getString("PARA_CODE1")))
			{// 开关打开
				if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "NOTCMP_STFPRV"))
				{// 有免比对权限
					tag = "1";
				} else
				{
					tag = "0";
				}
			} else if ("1".equals(comptag.getData(0).getString("PARA_CODE1")))
			{// 开关关闭
				if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "CMP_STFPRV"))
				{// 需要比对
					tag = "0";
				} else
				{// 有免比对
					tag = "1";
				}
			}
		}
		result.put("CMPTAG", tag);
		return result;
	}

	/**
	 *  工号是否具有某功能权限
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 * @author chenchunni
	 */
	public IData isFuncDataPriv(IData param) throws Exception
	{
		IData result = new DataMap();
		// 定义是否可以使用某类型证件的权限  1：没有权限
		result.put("X_RESULTCODE", "1");
		// 获取员工号、以及权限编码
		String staffID = param.getString("STAFF_ID","");
		String rightCode = param.getString("RIGHT_CODE","");

		boolean hasPriv = StaffPrivUtil.isFuncDataPriv(staffID, rightCode);
		if (hasPriv) {// 有权限
			result.put("X_RESULTCODE", "0");
		}
		return result;
	}

	/* 
	 * 无手机宽带开户-判断是否有线上预约工单
	 * REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
	 * zhangxing3
	 */
	public static IDataset queryUserTradeBook(IData iData) throws Exception
	{

		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" select * from tf_b_trade_book a where a.pspt_id = :PSPT_ID ");
		dctparser.addSQL("    and a.book_status='0' and a.book_end_date > sysdate and a.rsrv_tag1='0' ");
		IDataset resultset = Dao.qryByParse(dctparser,Route.CONN_CRM_CEN);
		return resultset;
	}
	
    /**
     * 跨区补卡是否免人像比对和身份证可手动输入权限
     * @param input
     * @return
     * @throws Exception
     * @author  
     */
    public IData kqbkDataRight(IData param) throws Exception
    {
        IData result = new DataMap();
        String tag = "1";
        if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "OverlandCardTest"))
        {// 有免人像比对和身份证可手动输入权限
            tag = "1";
        } else
        {
            tag = "0";
        }        
        result.put("TAG", tag);
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
		if (isPwlwOper(param.getString("SERIAL_NUMBER", "").trim(), param.getString("BUISUSERTYPE", "").trim())) {
			if (psptType.equals("0") || psptType.equals("1") || psptType.equals("2") || psptType.equals("3")) {
				IDataset staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_IDCARD");
				log.error("CreatePersonUserBean.verifyIdCard2331");
				boolean isVerify = IDataUtil.isNotEmpty(staticInfo) && staticInfo.getData(0).getBoolean("PARA_CODE1");
				log.error("CreatePersonUserBean.verifyIdCard2335  " + isVerify);
				if (isVerify) {
					return result;
				}
			}
			if (psptType.equals("E") ) {				
				IDataset staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_ENTERPRISE");				
			    String isVerify = staticInfo.getData(0).getString("PARA_CODE1", "").trim();																
				{
					return result;
				}				
			}
			if (psptType.equals("M")) {
				IDataset staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_ORG");
				String isVerify = staticInfo.getData(0).getString("PARA_CODE1", "").trim();				
				{
					return result;
				}
			}
		}
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
	 * REQ201608230012 关于2016年下半年吉祥号码优化需求（三） chenxy3 20161009 查询销户的号码是否超过一年
	 * */
	public static IDataset checkIfNoOverOneYear(IData input) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		return Dao.qryByCode("TD_S_CPARAM", "IS_DETROY_OVER_ONE_YEAR", param);
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
		
		// REQ201802120028 单位证件开户等界面校验功能优化需求
		if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_SPECIALCARDWITHOUTVERIFY")) {
			result.put("X_RESULTCODE", "0");
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
					/*
					 * if(!legal_person_result.equals("1")){ //1 一致 2 不一致 isPass
					 * = false; return_message += "法人名称不一致;"; }
					 * if(!start_date_result.equals("1")){ //1 一致 2 不一致 isPass =
					 * false; return_message += "成立日期不一致;"; }
					 * if(!term_start_date_result.equals("1")){ //1 一致 2 不一致
					 * isPass = false; return_message += "营业开始日期不一致;"; }
					 * if(!term_end_date_result.equals("1")){ //1 一致 2 不一致
					 * isPass = false; return_message += "营业结束日期不一致;"; }
					 * if(isblack.equals("0")){ // 0 是黑名单 1不是黑名单 isPass = false;
					 * return_message += "该证件属于黑名单;"; }
					 */

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
		
		// REQ201802120028 单位证件开户等界面校验功能优化需求
		if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_SPECIALCARDWITHOUTVERIFY")) {
			result.put("X_RESULTCODE", "0");
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
//				String state = resultIboss.getString("STATE", "").trim();// 状态
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
					/*
					 * if(!org_type_result.equals("1")){ //1 一致 2 不一致 isPass =
					 * false; return_message += "机构类型不一致;"; }
					 * if(!effective_date_result.equals("1")){ //1 一致 2 不一致
					 * isPass = false; return_message += "有效日期不一致;"; }
					 * if(!expiration_date_result.equals("1")){ //1 一致 2 不一致
					 * isPass = false; return_message += "失效日期不一致;"; }
					 * if(isblack.equals("0")){ // 0 是黑名单 1不是黑名单 isPass = false;
					 * return_message += "该证件属于黑名单;"; }
					 */

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
		String serialNumber = input.getString("SERIAL_NUMBER", "").trim();// 客户资料变更是需要传，其他不用
		String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "").trim();// 客户资料变更是需要传，其他不用
		String brandCode = input.getString("BRAND_CODE", "");

		
		//HNHN、HNSJ 2个业务区手机号码，不校验全国一证5号
        if (serialNumber.length() > 0 && tradeTypeCode.length() > 0) {
            if (tradeTypeCode.equals("60") || tradeTypeCode.equals("100")) {//客户资料变更、过户
                IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
                if (userInfo != null) {
                    String cityCode = userInfo.getString("CITY_CODE", "").trim();
                    //System.out.println(.println("CreatePersonUserBean.javaxxxxxxxxxxxxxx2678 " + cityCode);
                    if (cityCode.equals("HNHN") || cityCode.equals("HNSJ")) {//这2个业务区手机号码，不校验全国一证5号
                        ajaxDataset.add(ajaxData);
                        return ajaxDataset;
                    }
                }
            }
        }		

		// 根据证件类型查找全网开户限制数
		IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "2552", psptTypeCode, "ZZZZ");
		if (openLimitResult.isEmpty())
		{// 如果本地配置没有该业务类型的限制数量配置，则直接返回
			ajaxDataset.add(ajaxData);
			return ajaxDataset;
		}

		if (!"".equals(custName) && !"".equals(psptId) && !"".equals(psptTypeCode))
		{

			/**
			 * 关于尽快落实“实名制开户拍照留存用户照片及限制全国一证5号需求”的通知需求
			 * 
			 * @author zhaohj3
			 */
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
				callResult = bean.idCheck(param);
				ajaxData.put("SEQ",callResult.getData(0).getString("SEQ"));
			} catch (Exception e)
			{
				ajaxData.put("MSG", "校验【全网一证多号】出现异常，请联系系统管理员！" + custName + "|" + psptTypeCode + "|" + psptId);
				ajaxData.put("CODE", "1");
				ajaxDataset.add(ajaxData);
				return ajaxDataset;
			}

			String acctTag = "";
			if (serialNumber.length() > 0 && tradeTypeCode.equals("60"))
			{// 客户资料变更
				IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
				acctTag = userInfo.getString("ACCT_TAG", "").trim();
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
							ajaxData.put("MSG", "开户人有不良信息，不满足开户条件，禁止开户");
							if (acctTag.equals("0"))
							{// 如果tf_f_user的acct_tag=0，正常状态，仅提示不限制，其他状态值，需要限制
								ajaxData.put("CODE", "2");
							} else
							{
								ajaxData.put("CODE", "1");
							}
						}
						if (IDataUtil.isNotEmpty(openLimitResult))
						{
							int openLimitNum = openLimitResult.getData(0).getInt("PARA_CODE1", 0);
							int localopenLimitNum = openLimitResult.getData(0).getInt("PARA_CODE2", 0);
							String localSwitch = openLimitResult.getData(0).getString("PARA_CODE4", "");
							String localProduct = openLimitResult.getData(0).getString("PARA_CODE3", "");
							
							
							int rCount = UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);
			                //add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
							// 判断一证五号个数以登记该证件为户主和使用人合并计算           	 	 	 
							rCount += UserInfoQry.getRealNameUserCountByUsePspt(custName, psptId, null);
							//add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
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
								if(isPwlwOper(serialNumber, "")){
									ajaxData.put("CODE", "0");//物联网号码不进行本省一证多号校验
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
									if(isPwlwOper(serialNumber, "")){
										ajaxData.put("CODE", "0");//物联网号码不进行本省一证多号校验
									}
								}
							}
							
							//单个证件入网
					        IDataset openMonthResult = CommparaInfoQry.getCommparaAllCol("CSM", "2662", psptTypeCode, "ZZZZ");
							if(IDataUtil.isNotEmpty(openMonthResult))
							{
								int params = UserInfoQry.getRealNameUserCountByDay(custName,psptId);
								if(params>0){
									ajaxData.put("MSG2", "您的证件号码【"+psptId+"】在近七天曾办理过开户，存在被不法分子冒用进行欺诈违法活动的风险，请您核实本次开户是否本人使用？");
									ajaxData.put("CODE2", "0");
								
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

	/**
	 * REQ201706130001_关于录入联网核验情况的需求
	 * <br/>
	 * 记录人像比对效验信息
	 * @author zhuoyingzhi
	 * @date 20170920
	 * @param param
	 * @param checkInfo
	 * @param checkTag
	 */
   public void  insertCheckInfoLog(IData param,String checkInfo,String checkTag){
	   try {
		   IData paramInfo=new DataMap();
		   //核验手机号码
		   paramInfo.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER",""));
		   //办理业务类型
		   paramInfo.put("TRADE_TYPE_CODE", param.getString("BLACK_TRADE_TYPE",""));
		   //办理渠道
		   paramInfo.put("IN_MODE_CODE", getVisit().getInModeCode());
		   //核验时间
		   paramInfo.put("CHECK_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		   //核验证件类型
		   paramInfo.put("CHECK_TYPE", param.getString("CERT_TYPE",""));
		   //核验证件号码
		   paramInfo.put("CHECK_PSPT_ID", param.getString("CERT_ID",""));
		   //核验客户名称
		   paramInfo.put("CHECK_CUST_NAME", param.getString("CERT_NAME",""));
		   
		   //核验结果
		   paramInfo.put("CHECK_INFO", checkInfo);
		   //是否完成核验
		   paramInfo.put("CHECK_TAG", checkTag);
		   //交易
		   paramInfo.put("CHECK_KIND", "CUST_PIC_COMPARE_SERVICE_10085_0_0");
		   //工号
		   paramInfo.put("IN_STAFF_ID", getVisit().getStaffId());
		   
		   //
		   paramInfo.put("RSRV_STR1", "人像比对");
		   
		   Dao.insert("TL_B_CRM_CHECK_INFO", paramInfo);
		} catch (Exception e) {
			log.debug("---insertCheckInfoLog----"+e);
		}
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

/**
 * @description 校验业务类型的开户号段限制
 * @param @param data
 * @return void
 * @author tanzheng
 * @date 2019年4月17日
 * @param data
 * @throws Exception 
 */
public void checkNumberLimit(IData data) throws Exception {
	long serialNumer = data.getLong("SERIAL_NUMBER");
	String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
	IDataset commparas = CommparaInfoQry.getCommpara("CSM","2828",tradeTypeCode,"0898");
	
	String testNum = "";
	String suffixNum = (serialNumer+"").substring(7);
	boolean allow = false;
	if(IDataUtil.isNotEmpty(commparas)){
		for(Object commData : commparas){
			IData tempData = (IData)commData;
			if(serialNumer > tempData.getLong("PARA_CODE1") 
					&& serialNumer < tempData.getLong("PARA_CODE2")){
				allow = true ;
				//将是否允许吉祥号开户传回去
				data.put("ALLOW_BEAUTY", tempData.getString("PARA_CODE3"));
				testNum = tempData.getString("PARA_CODE4");
				break;
			}
			
		}
	}
	if(!allow){
		CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.IS_NOT_FOR_OPENUSER_NUMBER);
	}
	if(StringUtils.isNotBlank(testNum) && testNum.contains(suffixNum)){
		CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.TEST_NUM_NOT_ALLOW_OPEN);
	}
	
} 
   
   /**
    * REQ201904260020新增物联网批量开户界面权限控制需求
    * 免人像比对权限判断
    * @author mengqx
    * @date 20190515
    * @param clcle
    * @throws Exception
    */
	public IData isBatCmpPic(IData param) throws Exception
	{
		IData result = new DataMap();
		String tag = "1";
		if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "BAT_NOTCMP_STFPRV"))
		{// 有免比对权限
			tag = "1";
		} else
		{
			tag = "0";
		}
		result.put("CMPTAG", tag);
		return result;
	}
	
	/**
     * REQ201909040010在和家固话实名认证环节增加校验客户的固话开户实名信息与手机号码实名信息—BOSS侧
     * 家庭IMS固话开户(新),在界面进行人像对比后，所获取的证件号码和姓名，要与界面输入的手机号码对应的证件号码和姓名进行对比，信息一致才能提交办理
     * mengqx 20190912
     */
    public IData checkIMSPhoneCustInfo(IData input) throws Exception
    {
    	IData result = new DataMap();

		IDataset commpara = CommparaInfoQry.getCommparaByCode1("CSM", "925", "1", "0898");// 拦截提示增加配置功能，有问题时改配置即可关闭，不再进行校验，也不再拦截提示。
		if (IDataUtil.isEmpty(commpara))
		{
			result.put("CODE", "0");
			result.put("MSG", "校验通过！");
			return result;
		}
   	 
    	String custName = input.getString("CUST_NAME", "").trim();
		String psptId = input.getString("PSPT_ID", "").trim();
		String serialNumber = input.getString("SERIAL_NUMBER", "").trim();
		
		log.debug("----CreatePersonUserBean-----mqx----begin");

    	result.put("CODE", "-1");
    	result.put("MSG", "开户失败，代付手机号码与和家固话号码的实名信息不一致!");
    	
    	IDataset custInfos = CustomerInfoQry.getNormalCustInfoBySN(serialNumber);
    	if(IDataUtil.isNotEmpty(custInfos)){
    		IData custInfo = custInfos.getData(0);
    		String custInfoPsptId = custInfo.getString("PSPT_ID");
    		String custInfoName = custInfo.getString("CUST_NAME");
    		
    		log.debug("----CreatePersonUserBean-----mqx------psptId="+psptId+",custInfoPsptId="+custInfoPsptId+",custName="+custName+",custInfoName="+custInfoName);
    		
    		if(psptId.equals(custInfoPsptId) && custName.equals(custInfoName)){
    			result.put("CODE", "0");
    			result.put("MSG", "校验通过！");
    		}
    	}
    			
    	
   	 	return result;
    }


	/**
	 * REQ201810190032 	和家固话开户界面增加实名制校验—BOSS侧  by mqx 20190108
	 * 和家固话单位开户权限判断
	 *
	 */
	public IData verifyOrganizationPriv(IData param) throws Exception
	{
		IData result = new DataMap();
		String code = "0";
		String psptTypeCode = param.getString("PSPT_TYPE_CODE");
		String staffId = getVisit().getStaffId();

		log.debug("----CreatePersonUserBean-----mqx----verifyOrganizationPriv----psptTypeCode="+psptTypeCode);

		if(!StaffPrivUtil.isFuncDataPriv(staffId, "OP_ORGANIZATION_PRIV")&&!StringUtils.isBlank(psptTypeCode)){
			IDataset organizationPsptTypes = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "2019",
					"VERIFY_ORGANIZATION_PSPT_TYPE_CODE", psptTypeCode, getVisit().getStaffEparchyCode());
			if(IDataUtil.isNotEmpty(organizationPsptTypes)){
				code = "1";//和家固话单位开户权限校验不通过
			}
		}

		result.put("CODE", code);
		return result;
	}

	/**
	 * REQ201810190032 	和家固话开户界面增加实名制校验—BOSS侧  by mqx 20190108
	 * 和家固话代办权限判断
	 *
	 */
	public IData verifyIMSOpAgentPriv(IData param) throws Exception
	{
		IData result = new DataMap();
		String code = "0";
		String staffId = getVisit().getStaffId();

		log.debug("----CreatePersonUserBean-----mqx----verifyIMSOpAgentPriv---="+!StaffPrivUtil.isFuncDataPriv(staffId, "IMS_OP_AGENT_PRIV"));

		if(!StaffPrivUtil.isFuncDataPriv(staffId, "IMS_OP_AGENT_PRIV")){
            IDataset imsAgentSwitch = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "2019",
                    "IMS_OP_AGENT_PRIV_SWITCH", "1", getVisit().getStaffEparchyCode());
            if(IDataUtil.isNotEmpty(imsAgentSwitch)){//开关为打开
                code = "1";//和家固话代办权限校验不通过
            }
		}

		result.put("CODE", code);
		return result;
	}

	/**
	 * REQ201908060023 关于联网核验时需要反馈请求网点的渠道编码的通知
	 * 企业类型（电信企业，请求方固定填01）_企业标识（请求方固定填2000）_渠道类型（请求方根据实际情况传参）
	 * _归属地市区号（请求方根据实际情况传参）_网点类型（请求方根据实际情况传参）_网点标识（请求方根据实际情况传参）
	 * @return
	 */
	private String qryNodeCode() throws Exception{
		String nodeCode="01_2000_01_0898";
		IDataset chnlInfos = ChnlInfoQry.getGlobalChlId(getVisit().getDepartId());
		String chnlKindId="";
		String globalChnlId="";
		if(DataUtils.isNotEmpty(chnlInfos)){
			chnlKindId=chnlInfos.first().getString("CHNL_KIND_ID","");
			globalChnlId=chnlInfos.first().getString("GLOBAL_CHNL_ID","");
		}
		if("100".equals(chnlKindId)){
			nodeCode+="_01";
		}else{
			nodeCode+="_02";
		}
		if(StringUtils.isNotBlank(globalChnlId)){
            nodeCode=nodeCode+"_"+globalChnlId;
        }else{
            nodeCode=nodeCode+"_0000000000000000";
        }

		return nodeCode;
	}

	/**
	 * REQ201911290007_【携号转网】关于发布CSMS和SOA间接口协议上身份证件传递要求的通知
	 * 修改携转界面使用的客户组件，过滤掉不符合规范证件
	 * @author mengqx
	 * @date 20200323
	 */
	public IData queryNpPsptTypeList(IData input) throws Exception{
		IData rtnData=new DataMap();

		IDataset allPsptTypeList =  StaticUtil.getStaticList("TD_S_PASSPORTTYPE2");

		IDataset psptTypeList = new DatasetList();

		//获得配置参数中前台可展示的证件类型
		IDataset paras = CommparaInfoQry.getCommparaAllCol("CSM", "40", "NP_PSPT_TYPE_LIST", "0898");

		if (IDataUtil.isNotEmpty(paras))
		{
			String psptIds = paras.getData(0).getString("PARA_CODE1");

			if (StringUtils.isNotBlank(psptIds))
			{
				for (int i = 0; i < allPsptTypeList.size(); i++)
				{
					if (psptIds.indexOf(allPsptTypeList.getData(i).getString("DATA_ID")) > -1)
					{
						psptTypeList.add(allPsptTypeList.getData(i));
					}
				}
			}
			else
			{
				psptTypeList = allPsptTypeList;
			}
		}
		else
		{
			psptTypeList = allPsptTypeList;
		}

		rtnData.put("PSPT_TYPE_LIST", psptTypeList);

		return rtnData;
	}
}
