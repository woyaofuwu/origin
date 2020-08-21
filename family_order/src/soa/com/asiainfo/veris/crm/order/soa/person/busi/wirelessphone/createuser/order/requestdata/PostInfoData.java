/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.requestdata;

import com.ailk.common.data.IData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PostInfoData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-9 上午10:19:43 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
 */

public class PostInfoData
{

    private String chrOpenPostInfo; // 是否支持邮寄标记

    private String postInfoPostTag; // 邮递标志

    private String postInfoPostCyc; // 邮寄周期

    private String postInfoPostAddress; // 邮递地址

    private String postInfoPostCode; // 邮递编码

    private String postInfoPostName; // 邮递名称

    private String postInfoFaxNbr; // 传真电话

    private String postInfoEmail; // Email地址

    private String postInfoPostTypeSet; // 邮寄方式

    private String postInfoPostTypeContent; // 邮证投递内容

    public PostInfoData()
    {

    }

    public PostInfoData(IData param)
    {
        setChrOpenPostInfo(param.getString("CHR_OPENPOSTINFO"));
        setPostInfoPostTag(param.getString("POST_TAG", ""));
        setPostInfoPostTypeSet(param.getString("POST_TYPESET", ""));
        setPostInfoPostTypeContent(param.getString("POSTTYPE_CONTENT", ""));
        setPostInfoPostCyc(param.getString("POST_CYC", ""));
        setPostInfoPostAddress(param.getString("POST_ADDRESS", ""));
        setPostInfoPostCode(param.getString("POST_CODE", ""));
        setPostInfoPostName(param.getString("POST_NAME", ""));
        setPostInfoFaxNbr(param.getString("FAX_NBR", ""));
        setPostInfoEmail(param.getString("EMAIL", ""));
    }

    public String getChrOpenPostInfo()
    {
        return chrOpenPostInfo;
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

    public String getPostInfoPostTypeContent()
    {
        return postInfoPostTypeContent;
    }

    public String getPostInfoPostTypeSet()
    {
        return postInfoPostTypeSet;
    }

    public void setChrOpenPostInfo(String chrOpenPostInfo)
    {
        this.chrOpenPostInfo = chrOpenPostInfo;
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

    public void setPostInfoPostTypeContent(String postInfoPostTypeContent)
    {
        this.postInfoPostTypeContent = postInfoPostTypeContent;
    }

    public void setPostInfoPostTypeSet(String postInfoPostTypeSet)
    {
        this.postInfoPostTypeSet = postInfoPostTypeSet;
    }

}
