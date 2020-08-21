package com.asiainfo.veris.crm.iorder.pub.family.consts;

public enum FamilyRolesEnum {
	FAMILY("0", "家庭"),
	PHONE("1", "手机"),
	WIDENET("2", "宽带"),
	IMS("3", "固话"),
	MBH("4", "魔百和");

	private final String roleCode;
	private final String roleName;

	private FamilyRolesEnum(String roleCode, String roleName) {
		this.roleCode = roleCode;
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return this.roleCode;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public String getValue() {
		return this.roleCode;
	}

	public String toString() {
		return "tab_name=" + roleCode + ",sql_ref=" + roleName;
	}

	public static String getRoleName(String roleCode) {
		FamilyRolesEnum[] roleNames = FamilyRolesEnum.values();
		for (int i = 0; i < roleNames.length; i++) {
			if (roleNames[i].roleCode.equals(roleCode)) {
				return roleNames[i].roleName;
			}
		}
		return null;
	}
}
