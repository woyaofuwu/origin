
package com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata;

import com.ailk.common.data.IData;

/**
 * @author Administrator
 */
public class TradeTypeData
{
    private String auditTag;

    private String backTag;

    private String bpmCode;

    private String cancelSpTag;

    private String creditJudgeTag;

    private String creditLimitDay;

    private String creditLimitHour;

    private String creditLimitTag;

    private String creditTradeDetailTag;

    private String creditTradeTag;

    private String creditYwTag;

    private String endDate;

    private String eparchyCode;

    private String extendTag;

    private String identityCheckTag;

    private String infoTagSet;

    private String intfTagSet;

    private String judgeOweTag;

    private String netTypeCode;

    private String pfErrLcu;

    private String preopenLimitTag;

    private String priority;

    private String prtTradeffTag;

    private String prtTraderrTag;

    private String recompRentTag;

    private String remark;

    private String resReleaseTag;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rtnDepositTag;

    private String rtnForegiftTag;

    private String startDate;

    private String tagSet;

    private String tradeAttr;

    private String tradeType;

    private String tradeTypeCode;

    public TradeTypeData(IData data)
    {
        this.auditTag = data.getString("AUDIT_TAG");
        this.backTag = data.getString("BACK_TAG");
        this.bpmCode = data.getString("BPM_CODE");
        this.cancelSpTag = data.getString("CANCEL_SP_TAG");
        this.creditJudgeTag = data.getString("CREDIT_JUDGE_TAG");
        this.creditLimitDay = data.getString("CREDIT_LIMIT_DAY");
        this.creditLimitHour = data.getString("CREDIT_LIMIT_HOUR");
        this.creditLimitTag = data.getString("CREDIT_LIMIT_TAG");
        this.creditTradeDetailTag = data.getString("CREDIT_TRADE_DETAIL_TAG");
        this.creditTradeTag = data.getString("CREDIT_TRADE_TAG");
        this.creditYwTag = data.getString("CREDIT_YW_TAG");
        this.endDate = data.getString("END_DATE");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.extendTag = data.getString("EXTEND_TAG");
        this.identityCheckTag = data.getString("IDENTITY_CHECK_TAG");
        this.infoTagSet = data.getString("INFO_TAG_SET");
        this.intfTagSet = data.getString("INTF_TAG_SET");
        this.judgeOweTag = data.getString("JUDGE_OWE_TAG");
        this.netTypeCode = data.getString("NET_TYPE_CODE");
        this.pfErrLcu = data.getString("PF_ERR_LCU");
        this.preopenLimitTag = data.getString("PREOPEN_LIMIT_TAG");
        this.priority = data.getString("PRIORITY");
        this.prtTradeffTag = data.getString("PRT_TRADEFF_TAG");
        this.prtTraderrTag = data.getString("PRT_TRADERR_TAG");
        this.recompRentTag = data.getString("RECOMP_RENT_TAG");
        this.remark = data.getString("REMARK");
        this.resReleaseTag = data.getString("RES_RELEASE_TAG");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rtnDepositTag = data.getString("RTN_DEPOSIT_TAG");
        this.rtnForegiftTag = data.getString("RTN_FOREGIFT_TAG");
        this.startDate = data.getString("START_DATE");
        this.tagSet = data.getString("TAG_SET");
        this.tradeAttr = data.getString("TRADE_ATTR");
        this.tradeType = data.getString("TRADE_TYPE");
        this.tradeTypeCode = data.getString("TRADE_TYPE_CODE");
    }

    public String getAuditTag()
    {
        return auditTag;
    }

    public String getBackTag()
    {
        return backTag;
    }

    public String getBpmCode()
    {
        return bpmCode;
    }

    public String getCancelSpTag()
    {
        return cancelSpTag;
    }

    public String getCreditJudgeTag()
    {
        return creditJudgeTag;
    }

    public String getCreditLimitDay()
    {
        return creditLimitDay;
    }

    public String getCreditLimitHour()
    {
        return creditLimitHour;
    }

    public String getCreditLimitTag()
    {
        return creditLimitTag;
    }

    public String getCreditTradeDetailTag()
    {
        return creditTradeDetailTag;
    }

    public String getCreditTradeTag()
    {
        return creditTradeTag;
    }

    public String getCreditYwTag()
    {
        return creditYwTag;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getExtendTag()
    {
        return extendTag;
    }

    public String getIdentityCheckTag()
    {
        return identityCheckTag;
    }

    public String getInfoTagSet()
    {
        return infoTagSet;
    }

    public String getIntfTagSet()
    {
        return intfTagSet;
    }

    public String getJudgeOweTag()
    {
        return judgeOweTag;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public String getPfErrLcu()
    {
        return pfErrLcu;
    }

    public String getPreopenLimitTag()
    {
        return preopenLimitTag;
    }

    public String getPriority()
    {
        return priority;
    }

    public String getPrtTradeffTag()
    {
        return prtTradeffTag;
    }

    public String getPrtTraderrTag()
    {
        return prtTraderrTag;
    }

    public String getRecompRentTag()
    {
        return recompRentTag;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getResReleaseTag()
    {
        return resReleaseTag;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRtnDepositTag()
    {
        return rtnDepositTag;
    }

    public String getRtnForegiftTag()
    {
        return rtnForegiftTag;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTagSet()
    {
        return tagSet;
    }

    public String getTradeAttr()
    {
        return tradeAttr;
    }

    public String getTradeType()
    {
        return tradeType;
    }

    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }

    public void setAuditTag(String auditTag)
    {
        this.auditTag = auditTag;
    }

    public void setBackTag(String backTag)
    {
        this.backTag = backTag;
    }

    public void setBpmCode(String bpmCode)
    {
        this.bpmCode = bpmCode;
    }

    public void setCancelSpTag(String cancelSpTag)
    {
        this.cancelSpTag = cancelSpTag;
    }

    public void setCreditJudgeTag(String creditJudgeTag)
    {
        this.creditJudgeTag = creditJudgeTag;
    }

    public void setCreditLimitDay(String creditLimitDay)
    {
        this.creditLimitDay = creditLimitDay;
    }

    public void setCreditLimitHour(String creditLimitHour)
    {
        this.creditLimitHour = creditLimitHour;
    }

    public void setCreditLimitTag(String creditLimitTag)
    {
        this.creditLimitTag = creditLimitTag;
    }

    public void setCreditTradeDetailTag(String creditTradeDetailTag)
    {
        this.creditTradeDetailTag = creditTradeDetailTag;
    }

    public void setCreditTradeTag(String creditTradeTag)
    {
        this.creditTradeTag = creditTradeTag;
    }

    public void setCreditYwTag(String creditYwTag)
    {
        this.creditYwTag = creditYwTag;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setExtendTag(String extendTag)
    {
        this.extendTag = extendTag;
    }

    public void setIdentityCheckTag(String identityCheckTag)
    {
        this.identityCheckTag = identityCheckTag;
    }

    public void setInfoTagSet(String infoTagSet)
    {
        this.infoTagSet = infoTagSet;
    }

    public void setIntfTagSet(String intfTagSet)
    {
        this.intfTagSet = intfTagSet;
    }

    public void setJudgeOweTag(String judgeOweTag)
    {
        this.judgeOweTag = judgeOweTag;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setPfErrLcu(String pfErrLcu)
    {
        this.pfErrLcu = pfErrLcu;
    }

    public void setPreopenLimitTag(String preopenLimitTag)
    {
        this.preopenLimitTag = preopenLimitTag;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public void setPrtTradeffTag(String prtTradeffTag)
    {
        this.prtTradeffTag = prtTradeffTag;
    }

    public void setPrtTraderrTag(String prtTraderrTag)
    {
        this.prtTraderrTag = prtTraderrTag;
    }

    public void setRecompRentTag(String recompRentTag)
    {
        this.recompRentTag = recompRentTag;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setResReleaseTag(String resReleaseTag)
    {
        this.resReleaseTag = resReleaseTag;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setRtnDepositTag(String rtnDepositTag)
    {
        this.rtnDepositTag = rtnDepositTag;
    }

    public void setRtnForegiftTag(String rtnForegiftTag)
    {
        this.rtnForegiftTag = rtnForegiftTag;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setTagSet(String tagSet)
    {
        this.tagSet = tagSet;
    }

    public void setTradeAttr(String tradeAttr)
    {
        this.tradeAttr = tradeAttr;
    }

    public void setTradeType(String tradeType)
    {
        this.tradeType = tradeType;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }
}
