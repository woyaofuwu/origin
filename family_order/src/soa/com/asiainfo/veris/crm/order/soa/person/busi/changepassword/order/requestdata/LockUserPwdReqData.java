
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * 用户密码锁定请求数据
 * 
 * @author liutt
 */
public class LockUserPwdReqData extends BaseReqData
{
    private String oldTradeTypeCode; // 从哪笔业务进来的

    private String checkMode;// 验证方式 0 =本人证件号码 1=用户密码

    private String netTypeCode;

    private String inModeCode;// 渠道

    private String errorNum;// 密码错误次数

    public String getCheckMode()
    {
        return checkMode;
    }

    public String getErrorNum()
    {
        return errorNum;
    }

    public String getInModeCode()
    {
        return inModeCode;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public String getOldTradeTypeCode()
    {
        return oldTradeTypeCode;
    }

    public void setCheckMode(String checkMode)
    {
        this.checkMode = checkMode;
    }

    public void setErrorNum(String errorNum)
    {
        this.errorNum = errorNum;
    }

    public void setInModeCode(String inModeCode)
    {
        this.inModeCode = inModeCode;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setOldTradeTypeCode(String oldTradeTypeCode)
    {
        this.oldTradeTypeCode = oldTradeTypeCode;
    }

}
