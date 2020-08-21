
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

import java.util.ArrayList;
import java.util.List;

public class FamilyRole
{
    // 角色编码
    private String roleCode;

    // 角色类型
    private String roleType;

    // 成员实例ID
    private String roleInstId;

    // 家庭代付标记
    private boolean isFamilyPay;

    // 家庭共享标记
    private boolean isFamilyShare;

    // 路由地州
    private String eparchyCode;

    // 归属手机号码
    private String memberMainSn;

    // 修改标记
    private String modifyTag;

    // 商品集合
    private List<ProductModuleData> roleOfferDatas = new ArrayList<ProductModuleData>();

    // 融合配置营销活动集合
    private List<ProductModuleData> roleSaleActiveDatas = new ArrayList<ProductModuleData>();

    public String getRoleCode()
    {
        return roleCode;
    }

    public void setRoleCode(String roleCode)
    {
        this.roleCode = roleCode;
    }

    public String getRoleType()
    {
        return roleType;
    }

    public void setRoleType(String roleType)
    {
        this.roleType = roleType;
    }

    public String getRoleInstId()
    {
        return roleInstId;
    }

    public void setRoleInstId(String roleInstId)
    {
        this.roleInstId = roleInstId;
    }

    public boolean isFamilyPay()
    {
        return isFamilyPay;
    }

    public void setFamilyPay(boolean familyPay)
    {
        isFamilyPay = familyPay;
    }

    public boolean isFamilyShare()
    {
        return isFamilyShare;
    }

    public void setFamilyShare(boolean familyShare)
    {
        isFamilyShare = familyShare;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public List<ProductModuleData> getRoleOfferDatas()
    {
        return roleOfferDatas;
    }

    public void setRoleOfferDatas(List<ProductModuleData> roleOfferDatas)
    {
        this.roleOfferDatas = roleOfferDatas;
    }

    public void addRoleOfferData(ProductModuleData roleOfferData)
    {
        this.roleOfferDatas.add(roleOfferData);
    }

    public List<ProductModuleData> getRoleSaleActiveDatas()
    {
        return roleSaleActiveDatas;
    }

    public void setRoleSaleActiveDatas(List<ProductModuleData> roleSaleActiveDatas)
    {
        this.roleSaleActiveDatas = roleSaleActiveDatas;
    }

    public void addRoleSaleActiveData(ProductModuleData roleSaleActiveData)
    {
        this.roleSaleActiveDatas.add(roleSaleActiveData);
    }

    public String getMemberMainSn()
    {
        return memberMainSn;
    }

    public void setMemberMainSn(String memberMainSn)
    {
        this.memberMainSn = memberMainSn;
    }
}
