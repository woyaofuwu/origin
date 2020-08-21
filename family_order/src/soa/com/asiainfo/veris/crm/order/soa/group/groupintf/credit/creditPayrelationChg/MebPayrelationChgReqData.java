
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit.creditPayrelationChg;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class MebPayrelationChgReqData extends MemberReqData
{
    private String DEAL_FLAG;
    private String tradeTypeCode;
    private String ACCT_ID;
    private String insertDate;      //信控发起工单crm接收的时间
    private String oweFeeCycle;     //欠费账期数
    private boolean IF_SMS = true; // 默认发完工短信;

    public void setDealFlag(String dealFlag)
    {
        this.DEAL_FLAG = dealFlag;
    }

    public String getDealFlag()
    {
        return DEAL_FLAG;
    }

    public void setIfsms(boolean ifSms)
    {
        this.IF_SMS = ifSms;
    }

    public boolean getIfsms()
    {
        return IF_SMS;
    }

    public void setAcctId(String acctId)
    {
        this.ACCT_ID = acctId;
    }

    public String getAcctId()
    {
        return ACCT_ID;
    }

    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }

    public String getInsertDate()
    {
        return insertDate;
    }

    public void setInsertDate(String insertDate)
    {
        this.insertDate = insertDate;
    }

    public String getOweFeeCycle()
    {
        return oweFeeCycle;
    }

    public void setOweFeeCycle(String oweFeeCycle)
    {
        this.oweFeeCycle = oweFeeCycle;
    }
    
}
