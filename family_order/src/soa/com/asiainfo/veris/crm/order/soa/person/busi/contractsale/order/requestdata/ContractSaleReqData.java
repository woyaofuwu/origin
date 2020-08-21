
package com.asiainfo.veris.crm.order.soa.person.busi.contractsale.order.requestdata;

import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

/**
 * @author Administrator
 */
public class ContractSaleReqData extends SaleActiveReqData
{

    private String countryImei;// 客户手机串号

    private String countryAccountNumber;// 存折(卡)号

    private String countryBankName;// 农信社名称

    private String assureType;// 担保类型

    private String assureName;// 担保人姓名

    private String assurePsptId;// 担保人证件号码

    private String assureContact;// 担保人联系方式

    private String assureContactor;// 联系人

    private String otherNumber;// 异网手机号码

    private String mainNumber;// 主号码

    private String developNo;// 刮刮卡号

    private String ggCheckTag;// 刮刮卡校验是否成功--0成功

    private String bankCode;// 信用卡银行

    private String payTypeCode;// 分期期数

    private String stuMobilePhone;// 校园营销学生手机号码

    private String contractConsumeDetailDesc;// 合约保底明细描述

    private String discount;// 折扣

    public String getAssureContact()
    {
        return assureContact;
    }

    public String getAssureContactor()
    {
        return assureContactor;
    }

    public String getAssureName()
    {
        return assureName;
    }

    public String getAssurePsptId()
    {
        return assurePsptId;
    }

    public String getAssureType()
    {
        return assureType;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    /***
     * * @return Returns the contractConsumeDetailDesc.
     */
    public String getContractConsumeDetailDesc()
    {
        return contractConsumeDetailDesc;
    }

    public String getCountryAccountNumber()
    {
        return countryAccountNumber;
    }

    public String getCountryBankName()
    {
        return countryBankName;
    }

    public String getCountryImei()
    {
        return countryImei;
    }

    public String getDevelopNo()
    {
        return developNo;
    }

    /***
     * * @return Returns the discount.
     */
    public String getDiscount()
    {
        return discount;
    }

    public String getGgCheckTag()
    {
        return ggCheckTag;
    }

    public String getMainNumber()
    {
        return mainNumber;
    }

    public String getOtherNumber()
    {
        return otherNumber;
    }

    public String getPayTypeCode()
    {
        return payTypeCode;
    }

    /***
     * * @return Returns the stuMobilePhone.
     */
    public String getStuMobilePhone()
    {
        return stuMobilePhone;
    }

    public void setAssureContact(String assureContact)
    {
        this.assureContact = assureContact;
    }

    public void setAssureContactor(String assureContactor)
    {
        this.assureContactor = assureContactor;
    }

    public void setAssureName(String assureName)
    {
        this.assureName = assureName;
    }

    public void setAssurePsptId(String assurePsptId)
    {
        this.assurePsptId = assurePsptId;
    }

    public void setAssureType(String assureType)
    {
        this.assureType = assureType;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    /***
     * @param contractConsumeDetailDesc
     *            The contractConsumeDetailDesc to set.
     */
    public void setContractConsumeDetailDesc(String contractConsumeDetailDesc)
    {
        this.contractConsumeDetailDesc = contractConsumeDetailDesc;
    }

    public void setCountryAccountNumber(String countryAccountNumber)
    {
        this.countryAccountNumber = countryAccountNumber;
    }

    public void setCountryBankName(String countryBankName)
    {
        this.countryBankName = countryBankName;
    }

    public void setCountryImei(String countryImei)
    {
        this.countryImei = countryImei;
    }

    public void setDevelopNo(String developNo)
    {
        this.developNo = developNo;
    }

    /***
     * @param discount
     *            The discount to set.
     */
    public void setDiscount(String discount)
    {
        this.discount = discount;
    }

    public void setGgCheckTag(String ggCheckTag)
    {
        this.ggCheckTag = ggCheckTag;
    }

    public void setMainNumber(String mainNumber)
    {
        this.mainNumber = mainNumber;
    }

    public void setOtherNumber(String otherNumber)
    {
        this.otherNumber = otherNumber;
    }

    public void setPayTypeCode(String payTypeCode)
    {
        this.payTypeCode = payTypeCode;
    }

    /***
     * @param stuMobilePhone
     *            The stuMobilePhone to set.
     */
    public void setStuMobilePhone(String stuMobilePhone)
    {
        this.stuMobilePhone = stuMobilePhone;
    }
}
