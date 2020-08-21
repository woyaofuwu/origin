
package com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata;

import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyRole;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @Description 家庭业务基础请求对象
 * @Auther: zhenggang
 * @Date: 2020/7/30 16:27
 * @version: V1.0
 */
public class BaseFamilyBusiReqData extends BaseReqData
{
    private String topTradeId;

    private String topEparchyCode;

    private String middleTradeId;

    private String middleEparchyCode;

    // 家庭主商品
    private String fmyProductId;

    // 家庭账户ID
    private String fmyAcctId;

    // 成员主商品
    private String memProductId;

    // 家庭手机号
    private String familySn;

    // 家庭用户ID
    private String familyUserId;

    // 管理员号码
    private String managerSn;

    // 针对单次成员增加或删除的处理
    private FamilyRole familyRole;

    private String familyEffectiveDate;

    // 家庭成员关系实例ID(这里要提前生成)
    private String familyMemberInstId;

    public String getTopTradeId()
    {
        return topTradeId;
    }

    public void setTopTradeId(String topTradeId)
    {
        this.topTradeId = topTradeId;
    }

    public String getTopEparchyCode()
    {
        return topEparchyCode;
    }

    public void setTopEparchyCode(String topEparchyCode)
    {
        this.topEparchyCode = topEparchyCode;
    }

    public String getMiddleTradeId()
    {
        return middleTradeId;
    }

    public void setMiddleTradeId(String middleTradeId)
    {
        this.middleTradeId = middleTradeId;
    }

    public String getMiddleEparchyCode()
    {
        return middleEparchyCode;
    }

    public void setMiddleEparchyCode(String middleEparchyCode)
    {
        this.middleEparchyCode = middleEparchyCode;
    }

    public String getFmyProductId()
    {
        return fmyProductId;
    }

    public void setFmyProductId(String fmyProductId)
    {
        this.fmyProductId = fmyProductId;
    }

    public String getMemProductId()
    {
        return memProductId;
    }

    public void setMemProductId(String memProductId)
    {
        this.memProductId = memProductId;
    }

    public String getFamilySn()
    {
        return familySn;
    }

    public void setFamilySn(String familySn)
    {
        this.familySn = familySn;
    }

    public String getFamilyUserId()
    {
        return familyUserId;
    }

    public void setFamilyUserId(String familyUserId)
    {
        this.familyUserId = familyUserId;
    }

    public String getManagerSn()
    {
        return managerSn;
    }

    public void setManagerSn(String managerSn)
    {
        this.managerSn = managerSn;
    }

    public FamilyRole getFamilyRole()
    {
        return familyRole;
    }

    public void setFamilyRole(FamilyRole familyRole)
    {
        this.familyRole = familyRole;
    }

    public String getFamilyEffectiveDate()
    {
        return familyEffectiveDate;
    }

    public void setFamilyEffectiveDate(String familyEffectiveDate)
    {
        this.familyEffectiveDate = familyEffectiveDate;
    }

    public String getFmyAcctId()
    {
        return fmyAcctId;
    }

    public void setFmyAcctId(String fmyAcctId)
    {
        this.fmyAcctId = fmyAcctId;
    }

    public String getFamilyMemberInstId()
    {
        return familyMemberInstId;
    }

    public void setFamilyMemberInstId(String familyMemberInstId)
    {
        this.familyMemberInstId = familyMemberInstId;
    }
}
