
package com.asiainfo.veris.crm.order.soa.person.busi.changeuserinfo.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * 用户资料修改请求数据类
 * 
 * @author liutt
 */
public class ModifyUserInfoReqData extends BaseReqData
{

    private String userTypeCode;// 用户类型

    private String assurePsptTypeCode;// 担保人证件类型

    private String assurePsptId;// 担保人证件号码

    private String assureName;// 担保人姓名

    private String assureTypeCode;// 担保类型

    private String assureDate;// 担保期限

    public String getAssureDate()
    {
        return assureDate;
    }

    public String getAssureName()
    {
        return assureName;
    }

    public String getAssurePsptId()
    {
        return assurePsptId;
    }

    public String getAssurePsptTypeCode()
    {
        return assurePsptTypeCode;
    }

    public String getAssureTypeCode()
    {
        return assureTypeCode;
    }

    public String getUserTypeCode()
    {
        return userTypeCode;
    }

    public void setAssureDate(String assureDate)
    {
        this.assureDate = assureDate;
    }

    public void setAssureName(String assureName)
    {
        this.assureName = assureName;
    }

    public void setAssurePsptId(String assurePsptId)
    {
        this.assurePsptId = assurePsptId;
    }

    public void setAssurePsptTypeCode(String assurePsptTypeCode)
    {
        this.assurePsptTypeCode = assurePsptTypeCode;
    }

    public void setAssureTypeCode(String assureTypeCode)
    {
        this.assureTypeCode = assureTypeCode;
    }

    public void setUserTypeCode(String userTypeCode)
    {
        this.userTypeCode = userTypeCode;
    }

}
