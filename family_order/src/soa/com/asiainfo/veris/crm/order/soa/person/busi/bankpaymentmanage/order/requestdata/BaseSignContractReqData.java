/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-6-20 修改历史 Revision 2014-6-20 下午04:12:53
 */
public class BaseSignContractReqData extends BaseReqData
{
    private String bankId = ""; // 银行名称

    private String bankAcctid = ""; // 银行卡号

    private String bankAcctType = ""; // 银行卡类型

    private String operInfo = ""; // 操作方式

    private String prepayTag = ""; // 付费方式

    private String payType = ""; // 缴费方式

    private String rechThreshold = ""; // 充值阀值

    private String rechAmount = ""; // 充值额度

    private String chnlType = ""; //

    private String subNumber = "";

    private String signId;

    private String cancelDataStr;// 解约副号码数据串

    private String operFlowId;
    
    private String preTradeId;//预约tradeId

    
    public String getPreTradeId()
    {
        return preTradeId;
    }

    public void setPreTradeId(String preTradeId)
    {
        this.preTradeId = preTradeId;
    }

    public String getBankAcctid()
    {
        return bankAcctid;
    }

    public String getBankAcctType()
    {
        return bankAcctType;
    }

    public String getBankId()
    {
        return bankId;
    }

    public String getCancelDataStr()
    {
        return cancelDataStr;
    }

    public String getChnlType()
    {
        return chnlType;
    }

    public String getOperFlowId()
    {
        return operFlowId;
    }

    public String getOperInfo()
    {
        return operInfo;
    }

    public String getPayType()
    {
        return payType;
    }

    public String getPrepayTag()
    {
        return prepayTag;
    }

    public String getRechAmount()
    {
        return rechAmount;
    }

    public String getRechThreshold()
    {
        return rechThreshold;
    }

    public String getSignId()
    {
        return signId;
    }

    public String getSubNumber()
    {
        return subNumber;
    }

    public void setBankAcctid(String bankAcctid)
    {
        this.bankAcctid = bankAcctid;
    }

    public void setBankAcctType(String bankAcctType)
    {
        this.bankAcctType = bankAcctType;
    }

    public void setBankId(String bankId)
    {
        this.bankId = bankId;
    }

    public void setCancelDataStr(String cancelDataStr)
    {
        this.cancelDataStr = cancelDataStr;
    }

    public void setChnlType(String chnlType)
    {
        this.chnlType = chnlType;
    }

    public void setOperFlowId(String operFlowId)
    {
        this.operFlowId = operFlowId;
    }

    public void setOperInfo(String operInfo)
    {
        this.operInfo = operInfo;
    }

    public void setPayType(String payType)
    {
        this.payType = payType;
    }

    public void setPrepayTag(String prepayTag)
    {
        this.prepayTag = prepayTag;
    }

    public void setRechAmount(String rechAmount)
    {
        this.rechAmount = rechAmount;
    }

    public void setRechThreshold(String rechThreshold)
    {
        this.rechThreshold = rechThreshold;
    }

    public void setSignId(String signId)
    {
        this.signId = signId;
    }

    public void setSubNumber(String subNumber)
    {
        this.subNumber = subNumber;
    }

}
