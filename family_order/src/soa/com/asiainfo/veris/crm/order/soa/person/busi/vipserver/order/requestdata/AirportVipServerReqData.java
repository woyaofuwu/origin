
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class AirportVipServerReqData extends BaseReqData
{
    private String serviceType; // 服务类型

    private String airDromeId; // 机场ID

    private String airDromeName; // 机场名称

    private String planLine; // 航班编号

    private String followNuber; // 随行人数

    private String serviceContent; // 服务内容

    private String handingChange; // 手续费

    private String serviceChange; // 服务费

    private String indivInfo; // 个性化信息

    private String feeBack; // 客户反馈

    private String innovation; // 服务创新

    private String advices; // 服务建议

    private String others; // 其它

    private int totalFreeCount; // 客户可使用的免费次数

    private int freeCount; // 已使用的免费服务次数

    private String oldStartDate; // 旧记录开始时间

    private int thisRsrvStr1; // 本次业务完成后已使用的免费次数

    private String thisRsrvStr2; // 本次消耗的免费次数

    private int restCount; // 剩余免费次数

    private String consumeScore; // 消耗积分

    private String valueChange; // // 代扣金额

    private String score; // 原积分

    private String vipId; // vipid

    private String vipTypeCode; //

    private String vipClassId; //

    private String vipNo;

    public String getAdvices()
    {
        return advices;
    }

    public String getAirDromeId()
    {
        return airDromeId;
    }

    public String getAirDromeName()
    {
        return airDromeName;
    }

    public String getConsumeScore()
    {
        return consumeScore;
    }

    public String getFeeBack()
    {
        return feeBack;
    }

    public String getFollowNuber()
    {
        return followNuber;
    }

    public int getFreeCount()
    {
        return freeCount;
    }

    public String getHandingChange()
    {
        return handingChange;
    }

    public String getIndivInfo()
    {
        return indivInfo;
    }

    public String getInnovation()
    {
        return innovation;
    }

    public String getOldStartDate()
    {
        return oldStartDate;
    }

    public String getOthers()
    {
        return others;
    }

    public String getPlanLine()
    {
        return planLine;
    }

    public int getRestCount()
    {
        return restCount;
    }

    public String getScore()
    {
        return score;
    }

    public String getServiceChange()
    {
        return serviceChange;
    }

    public String getServiceContent()
    {
        return serviceContent;
    }

    public String getServiceType()
    {
        return serviceType;
    }

    public int getThisRsrvStr1()
    {
        return thisRsrvStr1;
    }

    public String getThisRsrvStr2()
    {
        return thisRsrvStr2;
    }

    public int getTotalFreeCount()
    {
        return totalFreeCount;
    }

    public String getValueChange()
    {
        return valueChange;
    }

    public String getVipClassId()
    {
        return vipClassId;
    }

    public String getVipId()
    {
        return vipId;
    }

    public String getVipNo()
    {
        return vipNo;
    }

    public String getVipTypeCode()
    {
        return vipTypeCode;
    }

    public void setAdvices(String advices)
    {
        this.advices = advices;
    }

    public void setAirDromeId(String airDromeId)
    {
        this.airDromeId = airDromeId;
    }

    public void setAirDromeName(String airDromeName)
    {
        this.airDromeName = airDromeName;
    }

    public void setConsumeScore(String consumeScore)
    {
        this.consumeScore = consumeScore;
    }

    public void setFeeBack(String feeBack)
    {
        this.feeBack = feeBack;
    }

    public void setFollowNuber(String followNuber)
    {
        this.followNuber = followNuber;
    }

    public void setFreeCount(int freeCount)
    {
        this.freeCount = freeCount;
    }

    public void setHandingChange(String handingChange)
    {
        this.handingChange = handingChange;
    }

    public void setIndivInfo(String indivInfo)
    {
        this.indivInfo = indivInfo;
    }

    public void setInnovation(String innovation)
    {
        this.innovation = innovation;
    }

    public void setOldStartDate(String oldStartDate)
    {
        this.oldStartDate = oldStartDate;
    }

    public void setOthers(String others)
    {
        this.others = others;
    }

    public void setPlanLine(String planLine)
    {
        this.planLine = planLine;
    }

    public void setRestCount(int restCount)
    {
        this.restCount = restCount;
    }

    public void setScore(String score)
    {
        this.score = score;
    }

    public void setServiceChange(String serviceChange)
    {
        this.serviceChange = serviceChange;
    }

    public void setServiceContent(String serviceContent)
    {
        this.serviceContent = serviceContent;
    }

    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    public void setThisRsrvStr1(int thisRsrvStr1)
    {
        this.thisRsrvStr1 = thisRsrvStr1;
    }

    public void setThisRsrvStr2(String thisRsrvStr2)
    {
        this.thisRsrvStr2 = thisRsrvStr2;
    }

    public void setTotalFreeCount(int totalFreeCount)
    {
        this.totalFreeCount = totalFreeCount;
    }

    public void setValueChange(String valueChange)
    {
        this.valueChange = valueChange;
    }

    public void setVipClassId(String vipClassId)
    {
        this.vipClassId = vipClassId;
    }

    public void setVipId(String vipId)
    {
        this.vipId = vipId;
    }

    public void setVipNo(String vipNo)
    {
        this.vipNo = vipNo;
    }

    public void setVipTypeCode(String vipTypeCode)
    {
        this.vipTypeCode = vipTypeCode;
    }

}
