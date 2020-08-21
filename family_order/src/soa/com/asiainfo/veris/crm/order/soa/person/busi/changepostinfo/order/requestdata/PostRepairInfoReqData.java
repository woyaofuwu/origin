
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * 邮寄资料补录请求数据类
 * 
 * @author liutt
 */
public class PostRepairInfoReqData extends BaseReqData
{
    private String repairReason = "";// 补寄原因编码

    private String repairMonth = "";// 补寄月份

    private String postContent = ""; // 邮政投递内容拼串

    private String emailContent = ""; // EMAIL投递内容拼串

    private String postEmail = ""; // Email地址

    private String postName = ""; // 邮寄名称

    private String postAddress = ""; // 邮寄地址

    private String postCode = ""; // 邮递邮编

    private String postFaxNbr = ""; // 传真号码

    public String getEmailContent()
    {
        return emailContent;
    }

    public String getPostAddress()
    {
        return postAddress;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getPostContent()
    {
        return postContent;
    }

    public String getPostEmail()
    {
        return postEmail;
    }

    public String getPostFaxNbr()
    {
        return postFaxNbr;
    }

    public String getPostName()
    {
        return postName;
    }

    public String getRepairMonth()
    {
        return repairMonth;
    }

    public String getRepairReason()
    {
        return repairReason;
    }

    public void setEmailContent(String emailContent)
    {
        this.emailContent = emailContent;
    }

    public void setPostAddress(String postAddress)
    {
        this.postAddress = postAddress;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setPostContent(String postContent)
    {
        this.postContent = postContent;
    }

    public void setPostEmail(String postEmail)
    {
        this.postEmail = postEmail;
    }

    public void setPostFaxNbr(String postFaxNbr)
    {
        this.postFaxNbr = postFaxNbr;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
    }

    public void setRepairMonth(String repairMonth)
    {
        this.repairMonth = repairMonth;
    }

    public void setRepairReason(String repairReason)
    {
        this.repairReason = repairReason;
    }

}
