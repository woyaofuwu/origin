
package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ValueCardReqData extends BaseReqData
{
    /** 卡信息 */
    private List<ValueCardInfoReqData> cardlist;

    /** 业务类型 */
    private String tradeTypeCode;

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
    private String radio;

    /** 客户名称 */
    private String custName;// 发票打印需要打印自定义名称

    /** 手机号码 */
    private String custSerialNumber;// 发票打印需要打印自定义名称
    
    /** 客户号码*/
    private String customerNumber;//有价卡赠送  赠送信息中的客户号码
    
    /**客户姓名*/
    private String customerName;
    
    /**对应的集团名称*/
    private String  groupName;
    
    /**赠送人姓名*/
    private String giveName;
    

    public String getAuditStaffId()
    {
        return auditStaffId;
    }

    public int getCardCount()
    {
        return cardCount;
    }

    public List<ValueCardInfoReqData> getCardlist()
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

    public void setAuditStaffId(String auditStaffId)
    {
        this.auditStaffId = auditStaffId;
    }

    public void setCardCount(int cardCount)
    {
        this.cardCount = cardCount;
    }

    public void setCardlist(List<ValueCardInfoReqData> cardlist)
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

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGiveName() {
		return giveName;
	}

	public void setGiveName(String giveName) {
		this.giveName = giveName;
	}
}
