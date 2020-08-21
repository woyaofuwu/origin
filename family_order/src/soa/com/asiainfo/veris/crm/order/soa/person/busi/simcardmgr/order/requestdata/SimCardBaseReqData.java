
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata;

/**
 * sim卡基本信息
 * 
 * @author fanfb
 */
public class SimCardBaseReqData
{

    private String simCardNo;

    private String imsi;

    private String resTypeCode;

    private String ki;

    private String emptyCardId;

    private String writeCardFlag;

    private String agentSaleTag;// 该白卡是否为代理商空卡买断

    private String resKindCode;//

    private String opc;

    private String simCardSaleMoney;

    private String simEmptyTypeCode;

    private String simCardPasswd;// 密文

    private String simCardPasswdKey;// 密钥

    private String feeTag;

    private String flag4G;

    private String saleMoney;
    
    private String resFlag4G; //资源测返回的分2/3G,4G标识

    public String getAgentSaleTag()
    {
        return agentSaleTag;
    }

    public String getEmptyCardId()
    {
        return emptyCardId;
    }

    public String getFeeTag()
    {
        return feeTag;
    }

    public String getFlag4G()
    {
        return flag4G;
    }

    public String getImsi()
    {
        return imsi;
    }

    public String getKi()
    {
        return ki;
    }

    public String getOpc()
    {
        return opc;
    }

    public String getResKindCode()
    {
        return resKindCode;
    }

    public String getResTypeCode()
    {
        return resTypeCode;
    }

    public String getSaleMoney()
    {
        return saleMoney;
    }

    public String getSimCardNo()
    {
        return simCardNo;
    }

    public String getSimCardPasswd()
    {
        return simCardPasswd;
    }

    public String getSimCardPasswdKey()
    {
        return simCardPasswdKey;
    }

    public String getSimCardSaleMoney()
    {
        return simCardSaleMoney;
    }

    public String getSimEmptyTypeCode()
    {
        return simEmptyTypeCode;
    }

    public String getWriteCardFlag()
    {
        return writeCardFlag;
    }

    public void setAgentSaleTag(String agentSaleTag)
    {
        this.agentSaleTag = agentSaleTag;
    }

    public void setEmptyCardId(String emptyCardId)
    {
        this.emptyCardId = emptyCardId;
    }

    public void setFeeTag(String feeTag)
    {
        this.feeTag = feeTag;
    }

    public void setFlag4G(String flag4g)
    {
        this.flag4G = flag4g;
    }

    public void setImsi(String imsi)
    {
        this.imsi = imsi;
    }

    public void setKi(String ki)
    {
        this.ki = ki;
    }

    public void setOpc(String opc)
    {
        this.opc = opc;
    }

    public void setResKindCode(String resKindCode)
    {
        this.resKindCode = resKindCode;
    }

    public void setResTypeCode(String resTypeCode)
    {
        this.resTypeCode = resTypeCode;
    }

    public void setSaleMoney(String saleMoney)
    {
        this.saleMoney = saleMoney;
    }

    public void setSimCardNo(String simCardNo)
    {
        this.simCardNo = simCardNo;
    }

    public void setSimCardPasswd(String simCardPasswd)
    {
        this.simCardPasswd = simCardPasswd;
    }

    public void setSimCardPasswdKey(String simCardPasswdKey)
    {
        this.simCardPasswdKey = simCardPasswdKey;
    }

    public void setSimCardSaleMoney(String simCardSaleMoney)
    {
        this.simCardSaleMoney = simCardSaleMoney;
    }

    public void setSimEmptyTypeCode(String simEmptyTypeCode)
    {
        this.simEmptyTypeCode = simEmptyTypeCode;
    }

    public void setWriteCardFlag(String writeCardFlag)
    {
        this.writeCardFlag = writeCardFlag;
    }

	public String getResFlag4G() {
		return resFlag4G;
	}

	public void setResFlag4G(String resFlag4G) {
		this.resFlag4G = resFlag4G;
	}

}
