
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata;

public class CreateUserRequestData extends BaseCreateUserRequestData
{
	

    private String simCardNo; // sim卡号

    // 资源其他信息

    private String Imsi;

    private String Ki;

    private String ResTypeCode;// 资源类型

    private String EmptyCardId;// 白卡id

    private String NewAgentSaleTag;// 该白卡是否为代理商空卡买断

    private String ResKindCode;// 卡类型

    private String SimFeeTag;

    private String SimCardSaleMoney;

    private String OPC;

    private String ResKindName;

    private String Flag4G;

    private String netChoose;// 网上选号

    private String m2mFlag; // 是否为物联网

    private String OpenType; // 开户类型

    private String saleMoney;// 代理商买断卡费
    
    private String resFlag4G; //资源接口过来字段判断2、3、4G卡
    
    private String occupyTypeCode;// 选号类型 1是网上选号，3是自助选号


    // ---------------------------邮寄信息---------------------------------//

	private String chrOpenPostInfo; // 是否支持邮寄标记

    private String postInfoPostTag; // 邮递标志

    private String postInfoPostCyc; // 邮寄周期

    private String postInfoPostAddress; // 邮递地址

    private String postInfoPostCode; // 邮递编码

    private String postInfoPostName; // 邮递名称

    private String postInfoFaxNbr; // 传真电话

    private String postInfoEmail; // Email地址

    private String postInfoPostTypeSet; // 邮寄方式

    private String postContent = ""; // 邮政投递内容拼串

    private String emailContent = ""; // EMAIL投递内容拼串

    private String MMScontent = "";// MMS投递内容拼串

    private String eid ;//eSIM设备eid

    private String primarySerialNumber;

    private String newImei;

    public String getNewImei() {
        return newImei;
    }

    public void setNewImei(String newImei) {
        this.newImei = newImei;
    }

    public String getPrimarySerialNumber() {
        return primarySerialNumber;
    }

    public void setPrimarySerialNumber(String primarySerialNumber) {
        this.primarySerialNumber = primarySerialNumber;
    }


    public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getChrOpenPostInfo()
    {
        return chrOpenPostInfo;
    }

    public String getEmailContent()
    {
        return emailContent;
    }

    public String getEmptyCardId()
    {
        return EmptyCardId;
    }

    public String getFlag4G()
    {
        return Flag4G;
    }

    public String getImsi()
    {
        return Imsi;
    }

    public String getKi()
    {
        return Ki;
    }

    public String getM2mFlag()
    {
        return m2mFlag;
    }

    public String getMMScontent()
    {
        return MMScontent;
    }

    public String getNetChoose()
    {
        return netChoose;
    }

    public String getNewAgentSaleTag()
    {
        return NewAgentSaleTag;
    }

    public String getOPC()
    {
        return OPC;
    }

    public String getOpenType()
    {
        return OpenType;
    }

    public String getpostContent()
    {
        return postContent;
    }

    public String getPostInfoEmail()
    {
        return postInfoEmail;
    }

    public String getPostInfoFaxNbr()
    {
        return postInfoFaxNbr;
    }

    public String getPostInfoPostAddress()
    {
        return postInfoPostAddress;
    }

    public String getPostInfoPostCode()
    {
        return postInfoPostCode;
    }

    public String getPostInfoPostCyc()
    {
        return postInfoPostCyc;
    }

    public String getPostInfoPostName()
    {
        return postInfoPostName;
    }

    public String getPostInfoPostTag()
    {
        return postInfoPostTag;
    }

    public String getPostInfoPostTypeSet()
    {
        return postInfoPostTypeSet;
    }

    public String getResKindCode()
    {
        return ResKindCode;
    }

    public String getResKindName()
    {
        return ResKindName;
    }

    public String getResTypeCode()
    {
        return ResTypeCode;
    }

    public String getSaleMoney()
    {
        return saleMoney;
    }

    public String getSimCardNo()
    {
        return simCardNo;
    }

    public String getSimCardSaleMoney()
    {
        return SimCardSaleMoney;
    }

    public String getSimFeeTag()
    {
        return SimFeeTag;
    }

    public void setChrOpenPostInfo(String chrOpenPostInfo)
    {
        this.chrOpenPostInfo = chrOpenPostInfo;
    }

    public void setemailContent(String emailContent)
    {
        this.emailContent = emailContent;
    }

    public void setEmptyCardId(String EmptyCardId)
    {
        this.EmptyCardId = EmptyCardId;
    }

    public void setFlag4G(String Flag4G)
    {
        this.Flag4G = Flag4G;
    }

    public void setImsi(String Imsi)
    {
        this.Imsi = Imsi;
    }

    public void setKi(String Ki)
    {
        this.Ki = Ki;
    }

    public void setM2mFlag(String m2mFlag)
    {
        this.m2mFlag = m2mFlag;
    }

    public void setMMScontent(String MMScontent)
    {
        this.MMScontent = MMScontent;
    }

    public void setNetChoose(String netChoose)
    {
        this.netChoose = netChoose;
    }

    public void setNewAgentSaleTag(String NewAgentSaleTag)
    {
        this.NewAgentSaleTag = NewAgentSaleTag;
    }

    public void setOPC(String OPC)
    {
        this.OPC = OPC;
    }

    public void setOpenType(String OpenType)
    {
        this.OpenType = OpenType;
    }

    public void setpostContent(String postContent)
    {
        this.postContent = postContent;
    }

    public void setPostInfoEmail(String postInfoEmail)
    {
        this.postInfoEmail = postInfoEmail;
    }

    public void setPostInfoFaxNbr(String postInfoFaxNbr)
    {
        this.postInfoFaxNbr = postInfoFaxNbr;
    }

    public void setPostInfoPostAddress(String postInfoPostAddress)
    {
        this.postInfoPostAddress = postInfoPostAddress;
    }

    public void setPostInfoPostCode(String postInfoPostCode)
    {
        this.postInfoPostCode = postInfoPostCode;
    }

    public void setPostInfoPostCyc(String postInfoPostCyc)
    {
        this.postInfoPostCyc = postInfoPostCyc;
    }

    public void setPostInfoPostName(String postInfoPostName)
    {
        this.postInfoPostName = postInfoPostName;
    }

    public void setPostInfoPostTag(String postInfoPostTag)
    {
        this.postInfoPostTag = postInfoPostTag;
    }

    public void setPostInfoPostTypeSet(String postInfoPostTypeSet)
    {
        this.postInfoPostTypeSet = postInfoPostTypeSet;
    }

    public void setResKindCode(String ResKindCode)
    {
        this.ResKindCode = ResKindCode;
    }

    public void setResKindName(String ResKindName)
    {
        this.ResKindName = ResKindName;
    }

    public void setResTypeCode(String ResTypeCode)
    {
        this.ResTypeCode = ResTypeCode;
    }

    public void setSaleMoney(String saleMoney)
    {
        this.saleMoney = saleMoney;
    }

    public void setSimCardNo(String simCardNo)
    {
        this.simCardNo = simCardNo;
    }

    public void setSimCardSaleMoney(String SimCardSaleMoney)
    {
        this.SimCardSaleMoney = SimCardSaleMoney;
    }

    public void setSimFeeTag(String SimFeeTag)
    {
        this.SimFeeTag = SimFeeTag;
    }

	public String getResFlag4G() {
		return resFlag4G;
	}

	public void setResFlag4G(String resFlag4G) {
		this.resFlag4G = resFlag4G;
	}

	
	public String getOccupyTypeCode() {
		return occupyTypeCode;
	}

	public void setOccupyTypeCode(String occupyTypeCode) {
		this.occupyTypeCode = occupyTypeCode;
	}

}
