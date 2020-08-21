
package com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class GRPValueCardReqData extends BaseReqData
{
    /** 卡信息 */
    private List<GRPValueCardInfoReqData> cardlist;

    /** 业务类型 */
    private String tradeTypeCode;
    
    private String groupID;
    
    private String groupuserID;

    /** 需要缴纳的费用 = totalFee */
    private Float shouldPayFee = new Float(0);

    /** 总面值 */
    private Float deviceTotalFee = new Float(0); //

    /** 费用总费用 */
    private String totalFee;

    /** 发票、免填单卡段 */
    private StringBuilder cardSegment;//

    /** 拼发票打印内容的卡张数 */
    private int cardCount;

    /** 发票打印自定义名称的标志 */
    private String invoiceTag;

    /** 活动名称 */
    private String prosecutionWay;

    private String auditStaffId;

    private String giveMonth;// for VPMNgive

    /** 折扣标记 */
    private String radio; //a：正常销售；b：打折销售

    /** 客户名称 */
    private String custName;// 发票打印需要打印自定义名称

    /** 手机号码 */
    private String custSerialNumber;// 发票打印需要打印自定义名称
    

    /** 打折销售 折扣率 */
    private String disCount;
    /** 打折销售 销售价格 */
    private String salePrice;

    public String getAuditStaffId()
    {
        return auditStaffId;
    }

    public int getCardCount()
    {
        return cardCount;
    }

    public List<GRPValueCardInfoReqData> getCardlist()
    {
        return cardlist;
    }

    public StringBuilder getCardSegment()
    {
        return cardSegment;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getCustSerialNumber()
    {
        return custSerialNumber;
    }

    public Float getDeviceTotalFee()
    {
        return deviceTotalFee;
    }

    public String getGiveMonth()
    {
        return giveMonth;
    }

    public String getInvoiceTag()
    {
        return invoiceTag;
    }

    public String getProsecutionWay()
    {
        return prosecutionWay;
    }

    public String getRadio()
    {
        return radio;
    }
    
    public String getSaleprice()
    {
        return salePrice;
    }
    
    public String getDiscount()
    {
        return disCount;
    }


    public Float getShouldPayFee()
    {
        return shouldPayFee;
    }

    public String getTotalFee()
    {
        return totalFee;
    }

    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }
    
    public String getGroupID()
    {
        return groupID;
    }
    
    public String getGroupUserID()
    {
        return groupuserID;
    }

    public void setAuditStaffId(String auditStaffId)
    {
        this.auditStaffId = auditStaffId;
    }

    public void setCardCount(int cardCount)
    {
        this.cardCount = cardCount;
    }

    public void setCardlist(List<GRPValueCardInfoReqData> cardlist)
    {
        this.cardlist = cardlist;
    }

    public void setCardSegment(StringBuilder cardSegment)
    {
        this.cardSegment = cardSegment;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setCustSerialNumber(String custSerialNumber)
    {
        this.custSerialNumber = custSerialNumber;
    }

    public void setDeviceTotalFee(Float deviceTotalFee)
    {
        this.deviceTotalFee = deviceTotalFee;
    }

    public void setGiveMonth(String giveMonth)
    {
        this.giveMonth = giveMonth;
    }

    public void setInvoiceTag(String invoiceTag)
    {
        this.invoiceTag = invoiceTag;
    }

    public void setProsecutionWay(String prosecutionWay)
    {
        this.prosecutionWay = prosecutionWay;
    }

    public void setRadio(String radio)
    {
        this.radio = radio;
    }
    
    public void setSaleprice(String salePrice)
    {
    	this.salePrice = salePrice;
    }
    
    public void setDiscount(String disCount)
    {
    	this.disCount = disCount;
    }
    

    public void setShouldPayFee(Float shouldPayFee)
    {
        this.shouldPayFee = shouldPayFee;
    }

    public void setTotalFee(String totalFee)
    {
        this.totalFee = totalFee;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }
    
    public void setGroupID(String groupID)
    {
        this.groupID = groupID;
    }
    
    public void setGroupUsrID(String groupuserID)
    {
        this.groupuserID = groupuserID;
    }

}
