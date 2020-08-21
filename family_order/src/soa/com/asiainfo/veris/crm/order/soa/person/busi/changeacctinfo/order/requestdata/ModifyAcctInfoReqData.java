
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ModifyAcctInfoReqData extends BaseReqData
{

    private String payModeCode;// 帐户类别

    private String payName;// 帐户名称

    private String bankCode;// 银行名称

    private String bankAcctNo;// 银行帐号

    private String rsrvStr6;// 银行协议号

    private String contractNo;// 银行帐号

    private String superbankCode;// 上级银行

    private String postCode; // 邮政编码

    private String postAddress;// 邮政地址
    
    private String rsrvStr5;// 欠费周期
    
    private String mainTradeRemark;
   
    public String getmainTradeRemark() {
        return mainTradeRemark;
    }
    
    public void setmainTradeRemark(String mainTradeRemark) {
        this.mainTradeRemark = mainTradeRemark;
    }
    
    public String getRsrvStr5() {
		return rsrvStr5;
	}

	public void setRsrvStr5(String rsrvStr5) {
		this.rsrvStr5 = rsrvStr5;
	}

	public String getBankAcctNo()
    {
        return bankAcctNo;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    public String getContractNo()
    {
        return contractNo;
    }

    public String getPayModeCode()
    {
        return payModeCode;
    }

    public String getPayName()
    {
        return payName;
    }

    public String getPostAddress()
    {
        return postAddress;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getSuperbankCode()
    {
        return superbankCode;
    }

    public void setBankAcctNo(String bankAcctNo)
    {
        this.bankAcctNo = bankAcctNo;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public void setContractNo(String contractNo)
    {
        this.contractNo = contractNo;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setPayName(String payName)
    {
        this.payName = payName;
    }

    public void setPostAddress(String postAddress)
    {
        this.postAddress = postAddress;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setSuperbankCode(String superbankCode)
    {
        this.superbankCode = superbankCode;
    }

}
