
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ModifyPostInfoReqData extends BaseReqData
{
    private String postContent = ""; // 邮政投递内容拼串

    private String postType = ""; // 邮寄方式拼串

    private String emailContent = ""; // EMAIL投递内容拼串

    private String MMScontent = "";// MMS投递内容拼串

    private String postTag = ""; // 邮寄标志

    private String postEmail = ""; // Email地址

    private String postName = ""; // 邮寄名称

    private String postAddress = ""; // 邮寄地址

    private String postCyc = ""; // 邮递周期

    private String postCode = ""; // 邮递邮编

    private String postFaxNbr = ""; // 传真号码

    private String endDate = ""; // 接口参数 结束时间

    private String startDate = ""; // 接口参数 开始时间

    public String getEmailContent()
    {
        return emailContent;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getMMScontent()
    {
        return MMScontent;
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

    public String getPostCyc()
    {
        return postCyc;
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

    public String getPostTag()
    {
        return postTag;
    }

    public String getPostType()
    {
        return postType;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setEmailContent(String emailContent)
    {
        this.emailContent = emailContent;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setMMScontent(String mMScontent)
    {
        MMScontent = mMScontent;
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

    public void setPostCyc(String postCyc)
    {
        this.postCyc = postCyc;
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

    public void setPostTag(String postTag)
    {
        this.postTag = postTag;
    }

    public void setPostType(String postType)
    {
        this.postType = postType;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

}
