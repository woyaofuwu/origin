
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ChangePhonePreRegisterRequestData extends BaseReqData
{

    private String oldSerialNum = "";// 输入的原号码

    private String newSerialNum = "";// 输入的新号码

    private String psptId = "";// 证件号码

    private String psptTypeCode = "";// 证件类型

    private String newProvince = "";// 新省份编码

    private String oldProvince = "";// 旧号码归属省份编码

    private String newEparchy = ""; // 新号码归属地州

    private String oldEparchy = ""; // 旧号码归属地州

    private String moveInfo; // platInfo对象

    private String synTag = ""; // 

    private String whHandle = ""; // 

    public String getWhHandle()
    {
        return whHandle;
    }

    public void setWhHandle(String whHandle)
    {
        this.whHandle = whHandle;
    }

    private String opr_code = ""; // 

    private String rsrvstr1 = ""; // 

    private String rsrvstr2 = ""; // 

    public String getMoveInfo()
    {
        return moveInfo;
    }

    public String getNewEparchy()
    {
        return newEparchy;
    }

    public String getNewProvince()
    {
        return newProvince;
    }

    public String getNewSerialNum()
    {
        return newSerialNum;
    }

    public String getOldEparchy()
    {
        return oldEparchy;
    }

    public String getOldProvince()
    {
        return oldProvince;
    }

    public String getOldSerialNum()
    {
        return oldSerialNum;
    }

    public String getOpr_code()
    {
        return opr_code;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getPsptTypeCode()
    {
        return psptTypeCode;
    }

    public String getRsrvstr1()
    {
        return rsrvstr1;
    }

    public String getRsrvstr2()
    {
        return rsrvstr2;
    }

    public String getSynTag()
    {
        return synTag;
    }

    public void setMoveInfo(String moveInfo)
    {
        this.moveInfo = moveInfo;
    }

    public void setNewEparchy(String newEparchy)
    {
        this.newEparchy = newEparchy;
    }

    public void setNewProvince(String newProvince)
    {
        this.newProvince = newProvince;
    }

    public void setNewSerialNum(String newSerialNum)
    {
        this.newSerialNum = newSerialNum;
    }

    public void setOldEparchy(String oldEparchy)
    {
        this.oldEparchy = oldEparchy;
    }

    public void setOldProvince(String oldProvince)
    {
        this.oldProvince = oldProvince;
    }

    public void setOldSerialNum(String oldSerialNum)
    {
        this.oldSerialNum = oldSerialNum;
    }

    public void setOpr_code(String opr_code)
    {
        this.opr_code = opr_code;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setPsptTypeCode(String psptTypeCode)
    {
        this.psptTypeCode = psptTypeCode;
    }

    public void setRsrvstr1(String rsrvstr1)
    {
        this.rsrvstr1 = rsrvstr1;
    }

    public void setRsrvstr2(String rsrvstr2)
    {
        this.rsrvstr2 = rsrvstr2;
    }

    public void setSynTag(String synTag)
    {
        this.synTag = synTag;
    }

}
