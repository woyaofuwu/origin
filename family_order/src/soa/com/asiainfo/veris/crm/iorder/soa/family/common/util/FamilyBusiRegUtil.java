
package com.asiainfo.veris.crm.iorder.soa.family.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata.BaseFamilyBusiReqData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public class FamilyBusiRegUtil {
	/**
	 * @description 单个新增成员API
	 * @param input
	 * @param btd
	 * @throws Exception
	 */
	public static void callAddRoles(IData input, BusiTradeData btd) throws Exception {
		if (input.containsKey("SUB_ROLES")) {
			IDataset subRoles = new DatasetList(input.getString("SUB_ROLES", "[]"));

			if (IDataUtil.isNotEmpty(subRoles)) {
				BaseFamilyBusiReqData reqData = (BaseFamilyBusiReqData) btd.getRD();
				IData param = buildCommonParams(reqData);
				for (Object obj : subRoles) {
					IData subRole = (DataMap) obj;
					subRole.putAll(param);
					subRole.put(Route.ROUTE_EPARCHY_CODE, subRole.getString(KeyConstants.EPARCHY_CODE));
					CSAppCall.call("SS.FamilyMemberAddSingleRegSVC.tradeReg", subRole);
				}
			}
		}
	}

	/**
	 * @description 单个删除成员API
	 * @param input
	 * @param btd
	 * @throws Exception
	 */
	public static void callDelRoles(IData input, BusiTradeData btd) throws Exception {
		if (input.containsKey("DEL_ROLES")) {
			IDataset subRoles = new DatasetList(input.getString("DEL_ROLES", "[]"));

			if (IDataUtil.isNotEmpty(subRoles)) {
				BaseFamilyBusiReqData reqData = (BaseFamilyBusiReqData) btd.getRD();
				IData param = buildCommonParams(reqData);
				for (Object obj : subRoles) {
					IData subRole = (DataMap) obj;
					subRole.putAll(param);
					subRole.put(Route.ROUTE_EPARCHY_CODE, subRole.getString(KeyConstants.EPARCHY_CODE));
					CSAppCall.call("SS.FamilyMemberDelSingleRegSVC.tradeReg", subRole);
				}
			}
		}
	}

	public static IData buildCommonParams(BaseFamilyBusiReqData reqData) throws Exception {
		IData subRole = new DataMap();
		String familySn = reqData.getFamilySn();
		String familyUserId = reqData.getFamilyUserId();
		String fmyProductId = reqData.getFmyProductId();
		String fmyAcctId = reqData.getFmyAcctId();
		String managerSn = reqData.getManagerSn();
		subRole.put("FAMILY_SERIAL_NUMBER", familySn);
		subRole.put("FAMILY_USER_ID", familyUserId);
		subRole.put("FAMILY_PRODUCT_ID", fmyProductId);
		subRole.put("FAMILY_ACCT_ID", fmyAcctId);
		subRole.put("MANAGER_SN", managerSn);
		subRole.put("ORDER_TYPE_CODE", reqData.getOrderTypeCode());
		subRole.put("TOP_TRADE_ID", reqData.getTradeId());
		subRole.put("TOP_EPARCHY_CODE", reqData.getUca().getUserEparchyCode());
		subRole.put("BUSI_TYPE", reqData.getOrderTypeCode());
		return subRole;
	}

	public static void createLimitTrade(BaseFamilyBusiReqData req) throws Exception {
		if (StringUtils.isEmpty(req.getMiddleTradeId())) {
			insertTradeLimit(req.getTradeId(), req.getTopTradeId(), req.getTopEparchyCode());
		} else {
			if (StringUtils.isNotEmpty(req.getTopTradeId())) {
				insertTradeLimit(req.getMiddleTradeId(), req.getTopTradeId(), req.getTopEparchyCode());
			}
			insertTradeLimit(req.getTradeId(), req.getMiddleTradeId(), req.getMiddleEparchyCode());
		}
	}

	public static void insertTradeLimit(String tradeId, String limitTradeId, String limitEparchyCode) throws Exception {
		if (StringUtils.isEmpty(tradeId) || StringUtils.isEmpty(limitTradeId)
				|| StringUtils.isEmpty(limitEparchyCode)) {
			CSAppException.apperr(FamilyException.CRM_FAMILY_1);
		}
		IData param = new DataMap();
		param.put(KeyConstants.TRADE_ID, tradeId);
		param.put(KeyConstants.LIMIT_TRADE_ID, limitTradeId);
		param.put(KeyConstants.ACCEPT_MONTH, StrUtil.getAcceptMonthById(limitTradeId));
		param.put(KeyConstants.LIMIT_TYPE, "0");
		param.put(KeyConstants.ROUTE_ID, limitEparchyCode);
		param.put(KeyConstants.STATE, FamilyConstants.TRADE_LIMIT_STATE);
		Dao.insert("TF_B_TRADE_LIMIT", param, Route.getJourDb(Route.CONN_CRM_CG));
	}
}
