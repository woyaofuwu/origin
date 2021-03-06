
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupunifiedbill;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class CreateGroupUnifiedBillReqData extends MemberReqData
{
    private String memRoleB = "1";

    private boolean immeffect;// 是否立即生效

    private String immediate;// 预约

    // private String planTypeCode;//付费方式：P-个人付费；G-集团付费(默认)；C-定制；T-统付 PLAN_TYPE_CODE
    // private String planModeCode;//付费模式代码：1-完全统付；2-限定金额；3-限定账目项；4-同时限定金额及账目项

    /**
     * 1,字段 planTypeCode,//付费方式：P-个人付费；G-集团付费(默认)；C-定制；T-统付 PLAN_TYPE_CODE planModeCode
     * //付费模式代码：1-完全统付；2-限定金额；3-限定账目项；4-同时限定金额及账目项 START_CYCLE_ID,END_CYCLE_ID CHECK_ALL:综合账目全选,1为全选 NOTE_ITEMS:
     * ITEM_CODE:
     */
    private IData PAY_INFO; // 付费关系

    private IData MEM_PRDUCT_INFOS;

    // 其他
    private String imsPassword = "";// IMS_PASSWORD

    private String shortCode = "";// 短号

    public String getImmediate()
    {
        return immediate;
    }

    public boolean getImmeffect()
    {
        return immeffect;
    }

    public String getImsPassword()
    {
        return imsPassword;
    }

    public IData getMEM_PRDUCT_INFOS()
    {
        return MEM_PRDUCT_INFOS;
    }

    public String getMemRoleB()
    {
        return memRoleB;
    }

    public IData getPAY_INFO()
    {
        return PAY_INFO;
    }

    public String getShortCode()
    {
        return shortCode;
    }

    public void setImmediate(String immediate)
    {
        this.immediate = immediate;
    }

    public void setImmeffect(boolean immeffect)
    {
        this.immeffect = immeffect;
    }

    public void setImsPassword(String imsPassword)
    {
        this.imsPassword = imsPassword;
    }

    public void setMEM_PRDUCT_INFOS(IData mem_prduct_infos)
    {
        MEM_PRDUCT_INFOS = mem_prduct_infos;
    }

    public void setMemRoleB(String memRoleB)
    {
        this.memRoleB = memRoleB;
    }

    public void setPAY_INFO(IData pay_info)
    {
        PAY_INFO = pay_info;
    }

    public void setShortCode(String shortCode)
    {
        this.shortCode = shortCode;
    }
}
