package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.check.addmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

/**
 * 家庭添加新开魔百时规则校验
 * @author zhangxi
 *
 */
public class CheckAddNewTvMember implements ICheck {

	@Override
	public boolean check(IData checkInparam) throws Exception {

		String roleCode = checkInparam.getString(KeyConstants.ROLE_CODE);
		String roleType = checkInparam.getString(KeyConstants.ROLE_TYPE);
		String serialNumber = checkInparam.getString(KeyConstants.SERIAL_NUMBER);

		if (!StringUtils.equals(FamilyRolesEnum.MBH.getRoleCode(), roleCode)|| FamilyConstants.TYPE_OLD.equals(roleType)) {
			return false;
		}

		// 成员手机号码资料查询
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(userInfo))
        {
            FamilyCallerUtil.addErrorInfo(checkInparam, "CheckAddMember", "check-001", "该用户" + serialNumber + "资料不存在");
        }

        // 规则检验
        IData param = new DataMap();
        IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString(KeyConstants.CUST_ID));
        if (IDataUtil.isEmpty(customerInfo))
        {
            FamilyCallerUtil.addErrorInfo(checkInparam, "CheckAddMember", "check-002", "该用户" + serialNumber + "查询不到有效的客户信息");
        }

        param.putAll(userInfo);
        param.put(KeyConstants.IS_REAL_NAME, customerInfo.getString(KeyConstants.IS_REAL_NAME));
        param.put(KeyConstants.TRADE_TYPE_CODE, FamilyConstants.ROLE_TRADE_TYPE.MBH_OPEN);
        param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        param.put(KeyConstants.X_CHOICE_TAG, "0");
        IDataset infos = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade", param);
        FamilyCallerUtil.addBreerr(checkInparam, infos);

		return false;
	}

}
