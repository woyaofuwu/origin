
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata;

import com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata.BaseFamilyBusiReqData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 家庭成员管理请求对象
 * @Auther: zhenggang
 * @Date: 2020/7/31 10:19
 * @version: V1.0
 */
public class FamilyMemberManageReqData extends BaseFamilyBusiReqData
{
    // 新增对象集合
    private List<FamilyRole> newRoles = new ArrayList<FamilyRole>();

    // 删除对象集合
    private List<FamilyRole> delRoles = new ArrayList<FamilyRole>();

    public List<FamilyRole> getNewRoles()
    {
        return newRoles;
    }

    public void setNewRoles(List<FamilyRole> newRoles)
    {
        this.newRoles = newRoles;
    }

    public void addNewRoles(FamilyRole newRole)
    {
        this.newRoles.add(newRole);
    }

    public List<FamilyRole> getDelRoles()
    {
        return delRoles;
    }

    public void setDelRoles(List<FamilyRole> delRoles)
    {
        this.delRoles = delRoles;
    }

    public void addDelRoles(FamilyRole delRole)
    {
        this.delRoles.add(delRole);
    }
}
