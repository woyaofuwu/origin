
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata;

public class PostInfo
{
    // ---------------------------邮寄信息---------------------------------//
    private String postTag; // 邮递标志

    private String postCyc; // 邮寄周期

    private String postinfoAddress; // 邮递地址

    private String postinfoCode; // 邮递编码

    private String postName; // 邮递名称

    private String postFaxNbr; // 传真电话

    private String postEmail; // Email地址

    private String postTypeset; // 邮寄方式

    private String postContent; //

    public final String getPostContent()
    {
        return postContent;
    }

    public final String getPostCyc()
    {
        return postCyc;
    }

    public final String getPostEmail()
    {
        return postEmail;
    }

    public final String getPostFaxNbr()
    {
        return postFaxNbr;
    }

    public final String getPostinfoAddress()
    {
        return postinfoAddress;
    }

    public final String getPostinfoCode()
    {
        return postinfoCode;
    }

    public final String getPostName()
    {
        return postName;
    }

    public final String getPostTag()
    {
        return postTag;
    }

    public final String getPostTypeset()
    {
        return postTypeset;
    }

    public final void setPostContent(String postContent)
    {
        this.postContent = postContent;
    }

    public final void setPostCyc(String postCyc)
    {
        this.postCyc = postCyc;
    }

    public final void setPostEmail(String postEmail)
    {
        this.postEmail = postEmail;
    }

    public final void setPostFaxNbr(String postFaxNbr)
    {
        this.postFaxNbr = postFaxNbr;
    }

    public final void setPostinfoAddress(String postinfoAddress)
    {
        this.postinfoAddress = postinfoAddress;
    }

    public final void setPostinfoCode(String postinfoCode)
    {
        this.postinfoCode = postinfoCode;
    }

    public final void setPostName(String postName)
    {
        this.postName = postName;
    }

    public final void setPostTag(String postTag)
    {
        this.postTag = postTag;
    }

    public final void setPostTypeset(String postTypeset)
    {
        this.postTypeset = postTypeset;
    }

}
