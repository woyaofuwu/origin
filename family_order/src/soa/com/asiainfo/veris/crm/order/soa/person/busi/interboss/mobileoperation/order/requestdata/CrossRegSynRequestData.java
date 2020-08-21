
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation.order.requestdata;

/**
 * @author think
 */
public class CrossRegSynRequestData extends BaseCrossRegSynRequestData
{

    public String scoreChanged;// 积分变动值

    public String yearId;// 修改积分用 参数

    public String rsrvStr2;// 积分类型 0－全球通积分；1－动感地带

    public String vipTypeCode;// 固定为个人大客户类型

    public String vipClassId;// 大客户级别

    public String mobilenum;// 跨区号码

    public final String getMobilenum()
    {
        return mobilenum;
    }

    public final String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public final String getScoreChanged()
    {
        return scoreChanged;
    }

    public final String getVipClassId()
    {
        return vipClassId;
    }

    public final String getVipTypeCode()
    {
        return vipTypeCode;
    }

    public final String getYearId()
    {
        return yearId;
    }

    public final void setMobilenum(String mobilenum)
    {
        this.mobilenum = mobilenum;
    }

    public final void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public final void setScoreChanged(String scoreChanged)
    {
        this.scoreChanged = scoreChanged;
    }

    public final void setVipClassId(String vipClassId)
    {
        this.vipClassId = vipClassId;
    }

    public final void setVipTypeCode(String vipTypeCode)
    {
        this.vipTypeCode = vipTypeCode;
    }

    public final void setYearId(String yearId)
    {
        this.yearId = yearId;
    }

}
