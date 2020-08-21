
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * 用户密码解锁请求数据
 * 
 * @author liutt
 */
public class UnLockUserPwdReqData extends BaseReqData
{
    private String unLockType; // 解锁方式

    private String psptTypeCode; // 证件类型

    private String psptId;// 证件号码

    private String message;// 随机码

    public String getMessage()
    {
        return message;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getPsptTypeCode()
    {
        return psptTypeCode;
    }

    public String getUnLockType()
    {
        return unLockType;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setPsptTypeCode(String psptTypeCode)
    {
        this.psptTypeCode = psptTypeCode;
    }

    public void setUnLockType(String unLockType)
    {
        this.unLockType = unLockType;
    }
}
