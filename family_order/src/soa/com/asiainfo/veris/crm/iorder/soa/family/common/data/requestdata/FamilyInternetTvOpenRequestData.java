package com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata;

import com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.requestdata.InternetTvOpenRequestData;

/**
 *
 * @author zhangxi
 *
 */
public class FamilyInternetTvOpenRequestData extends InternetTvOpenRequestData {

	private String familyMemberInstId;

	public String getFamilyMemberInstId() {
		return familyMemberInstId;
	}

	public void setFamilyMemberInstId(String familyMemberInstId) {
		this.familyMemberInstId = familyMemberInstId;
	}

}
