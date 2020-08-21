package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.check.addmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyMemberChaInfoQry;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * 家庭添加存量魔百和规则校验
 * @author zhangxi
 *
 */
public class CheckAddOldTvMember implements ICheck {

	@Override
	public boolean check(IData checkInparam) throws Exception {

		String roleCode = checkInparam.getString(KeyConstants.ROLE_CODE);
		String roleType = checkInparam.getString(KeyConstants.ROLE_TYPE);
		String serialNumber = checkInparam.getString(KeyConstants.SERIAL_NUMBER);
		String resNo = checkInparam.getString(KeyConstants.RES_NO);

		if (!StringUtils.equals(FamilyRolesEnum.MBH.getRoleCode(), roleCode)|| FamilyConstants.TYPE_NEW.equals(roleType)) {
			return false;
		}

		if (StringUtils.isEmpty(resNo)) {
			FamilyCallerUtil.addErrorInfo(checkInparam, "CheckAddMember", "check-001","该用户" + serialNumber + "添加存量魔百和时，资源编码不能为空！");
		}

		// 成员号码资料查询
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(userInfo)) {
			FamilyCallerUtil.addErrorInfo(checkInparam, "CheckAddMember", "check-002", "获取用户" + serialNumber + "资料失败");
		}

		// 成员号码未完工工单校验
		IDataset tradeInfos = TradeInfoQry.getTradeInfoBySn(serialNumber);
		if (IDataUtil.isNotEmpty(tradeInfos)) {
			String tradeTypeCode = tradeInfos.getData(0).getString(KeyConstants.TRADE_TYPE_CODE);
			IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(tradeTypeCode, CSBizBean.getTradeEparchyCode());
			String tradeType = tradeTypeInfo.getString(KeyConstants.TRADE_TYPE);
			FamilyCallerUtil.addErrorInfo(checkInparam, "CheckAddMember", "check-003", "该用户有" + tradeType + "未完工工单");
		}

		String userId = userInfo.getString(KeyConstants.USER_ID);

		// 是否已经是家庭用户
		IDataset userMembersChas = FamilyMemberChaInfoQry.queryNowValidByMemberUserIdAndChaCode(userId,FamilyConstants.FamilyMemCha.FAMILY_TOPSETBOX.getValue());
		if (IDataUtil.isNotEmpty(userMembersChas)) {
			for (Object obj : userMembersChas) {
				IData userMembersCha = (DataMap) obj;
				if (resNo.equals(userMembersCha.get(KeyConstants.CHA_VALUE))) {
					FamilyCallerUtil.addErrorInfo(checkInparam, "CheckAddMember", "check-004","该用户:" + serialNumber + "下资源编码:" + resNo + "已经是家庭成员，不能再加入家庭");
				}
			}
		}

		return false;
	}

}
