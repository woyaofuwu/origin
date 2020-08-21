package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.check.submit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

public class CheckAcceptSubmit implements ICheck {

	@Override
	public boolean check(IData checkInparam) throws Exception {
		String fmyProductId = checkInparam.getString("FAMILY_PRODUCT_ID");
		return checkForceElement(checkInparam, fmyProductId);
	}

	public boolean checkForceElement(IData checkInparam, String fmyProductId) throws Exception {
		IData forceOfferData = buildUpcForceOfferData(fmyProductId);
		if (IDataUtil.isEmpty(forceOfferData))
			return false;

		return checkRoleForceElements(checkInparam, forceOfferData, checkInparam);
	}

	private boolean checkRoleForceElements(IData param, IData forceOfferData, IData checkInparam) throws Exception {
		String roleCode = param.getString("ROLE_CODE");
		IDataset selOffers = FamilyCallerUtil.getDataset(param, "OFFERS");
		IDataset subRoles = FamilyCallerUtil.getDataset(param, "SUB_ROLES");
		boolean result = checkForceOffer(selOffers, roleCode, checkInparam, forceOfferData);
		if (IDataUtil.isNotEmpty(subRoles)) {
			for (int i = 0; i < subRoles.size(); i++) {
				IData role = subRoles.getData(i);
				boolean flag = checkRoleForceElements(role, forceOfferData, checkInparam);
				if (!result && flag) {
					result = true;
				}
			}
		}
		return result;
	}

	private IData buildUpcForceOfferData(String fusionProductId) throws Exception {
		IDataset productComInfos = UpcCallIntf.qryOfferElesByProduct(fusionProductId,
				BofConst.ELEMENT_TYPE_CODE_PRODUCT, CSBizBean.getUserEparchyCode());
		if (IDataUtil.isEmpty(productComInfos))
			return null;
		IData offerdata = new DataMap();
		for (int i = 0; i < productComInfos.size(); i++) {
			IData proData = productComInfos.getData(i);
			if (!StringUtils.equals("0", proData.getString("SELECT_FLAG"))) {
				productComInfos.remove(i);
				i--;
				continue;
			}
			if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_PRODUCT, proData.getString("OFFER_TYPE"))) {// 产品不在这里校验必选
				productComInfos.remove(i);
				i--;
				continue;
			}
			String roleCode = proData.getString("ROLE_CODE", "0");
			if (offerdata.containsKey(roleCode)) {
				offerdata.getDataset(roleCode).add(proData);
			} else {
				IDataset tmpSet = new DatasetList();
				tmpSet.add(proData);
				offerdata.put(roleCode, tmpSet);
			}
		}
		return offerdata;
	}

	public boolean checkForceOffer(IDataset selOffers, String roleCode, IData checkInparam, IData roleForceOfferData)
			throws Exception {

		IDataset roleForceElementSet = roleForceOfferData.getDataset(roleCode, null);
		if (IDataUtil.isEmpty(roleForceElementSet))
			return false;

//		// 用户选择
		IData selOfferdata = buildSelectOfferData(selOffers);
		if (IDataUtil.isEmpty(selOfferdata)) {
			String strError = FamilyRolesEnum.getRoleName(roleCode) + "没有新增任何商品，请重新选择！";
			FamilyCallerUtil.addErrorInfo(checkInparam, "CheckAcceptSubmit", "check-1101", strError);
			return true;
		}

		for (int i = 0, size = roleForceElementSet.size(); i < size; i++) {
			IData forceData = roleForceElementSet.getData(i);
			String eleStr = "【" + forceData.getString("OFFER_CODE") + "】" + forceData.getString("OFFER_NAME");
			if (!selOfferdata.containsKey(eleStr)) {
				String strError = FamilyRolesEnum.getRoleName(roleCode) + "必选商品【" + eleStr + "】不能为空，请重新选择！";
				FamilyCallerUtil.addErrorInfo(checkInparam, "CheckAcceptSubmit", "check-1102", strError);
				return true;
			}
		}
		return false;
	}

	private IData buildSelectOfferData(IDataset selOffers) throws Exception {
		if (IDataUtil.isEmpty(selOffers))
			return null;
		IData selOfferdata = new DataMap();
		for (int i = 0, size = selOffers.size(); i < size; i++) {
			IData offerData = selOffers.getData(i);
			if (!StringUtils.equals("0", offerData.getString("MODIFY_TAG")))
				continue;
			selOfferdata.put(offerData.getString("ELEMENT_ID") + "-" + offerData.getString("ELEMENT_TYPE_CODE"),
					offerData.getString("ELEMENT_NAME"));
		}
		return selOfferdata;
	}
}
