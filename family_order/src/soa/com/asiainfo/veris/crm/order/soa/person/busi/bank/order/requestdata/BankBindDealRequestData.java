
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @author think
 */
public class BankBindDealRequestData extends BaseReqData
{
    public String bankName;

    public String bankCardNo;

    public String passWord;

    public final String getBankName()
    {
        return bankName;
    }
    
    public final void setBankName(String bankName)
    {
        this.bankName = bankName;
    }
    
    public final String getBankCardNo()
    {
        return bankCardNo;
    }

    public final void setBankCardNo(String bankCardNo)
    {
        this.bankCardNo = bankCardNo;
    }
    
    public final String getPassWord()
    {
        return passWord;
    }

    public final void setPassWord(String passWord)
    {
        this.passWord = passWord;
    }


}
