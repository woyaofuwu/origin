/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.ivalues.IPageData;

public class RoleOperData implements IPageData
{

    private String roleId = "";

    private String roleName = "";

    private String itemType = "角色";

    private String netTypeCode = "";

    private String isGroupRole = "";

    private String isMainRole = "";

    private String minNumber;

    private String maxNumber;

    private IPageData prodOperData;

    private List<PackageOperData> packOperList = new ArrayList<PackageOperData>();

    public String getIsGroupRole()
    {
        return isGroupRole;
    }

    public String getIsMainRole()
    {
        return isMainRole;
    }

    public String getItemType()
    {
        return this.itemType;
    }

    public String getMaxNumber()
    {
        return maxNumber;
    }

    public String getMinNumber()
    {
        return minNumber;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public List<PackageOperData> getPackOperList()
    {
        return packOperList;
    }

    public IPageData getParentPageData()
    {
        return prodOperData;
    }

    public String getRoleId()
    {
        return roleId;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setIsGroupRole(String isGroupRole)
    {
        this.isGroupRole = isGroupRole;
    }

    public void setIsMainRole(String isMainRole)
    {
        this.isMainRole = isMainRole;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public void setMaxNumber(String maxNumber)
    {
        this.maxNumber = maxNumber;
    }

    public void setMinNumber(String minNumber)
    {
        this.minNumber = minNumber;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setPackOperList(List<PackageOperData> packOperList)
    {
        this.packOperList = packOperList;
    }

    public void setParentPageData(IPageData parent)
    {
        prodOperData = parent;
    }

    public void setRoleId(String roleId)
    {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

}
