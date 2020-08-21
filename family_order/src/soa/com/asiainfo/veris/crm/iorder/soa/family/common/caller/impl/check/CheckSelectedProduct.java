package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.check;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

/**
 * 必选产品校验
 * 
 * @author liujian
 *
 */
public class CheckSelectedProduct implements ICheck {

	public boolean check(IData param) throws Exception {

		String newFamilyProducrId = param.getString("FAMILY_PRODUCT_ID", "");
		String roleCode = param.getString(KeyConstants.ROLE_CODE);
		if (!checkRoleForceProduct(newFamilyProducrId, roleCode)) {
			return false;
		}

		IDataset selectedElements = param.getDataset("SELECTED_ELEMENTS");
		if (IDataUtil.isEmpty(selectedElements)) {
			FamilyCallerUtil.addErrorInfo(param, "CheckSelectedProduct", "check-901", "必须选择一个产品！");
			return true;
		}

		for (int i = 0, size = selectedElements.size(); i < size; i++) {
			if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_PRODUCT,
					selectedElements.getData(i).getString("ELEMENT_TYPE_CODE", ""))) {
				return false;
			}
		}
		FamilyCallerUtil.addErrorInfo(param, "CheckSelectedOffer", "check-902", "必须选择一个产品！");
		return true;
	}

	private boolean checkRoleForceProduct(String fusionProductId, String roleCode) throws Exception {
		
//		IDataset fusionRolesElements = UpcCallIntf.getDiscntInfo(fusionProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT,
//				BofConst.ELEMENT_TYPE_CODE_PRODUCT, roleCode, "");
//		if (IDataUtil.isEmpty(fusionRolesElements))
//			return false;
//
//		for (int i = 0, size = fusionRolesElements.size(); i < size; i++) {
//			IData elmentData = fusionRolesElements.getData(i);
//			if (StringUtils.equals(UpcConst.SELECT_FLAG_MUST_CHOOSE, elmentData.getString("SELECT_FLAG"))) {
//				return true;
//			}
//		}
		return false;
	}

}
