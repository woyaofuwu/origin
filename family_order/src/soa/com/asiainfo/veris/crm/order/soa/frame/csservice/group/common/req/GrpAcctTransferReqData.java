
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.req;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class GrpAcctTransferReqData extends GroupReqData
{
    private IData MODITAG;

    private String newAcctId;

    private String newPayTag; // 新付费方式

    private String newUserName; // 用户群名称

    private String oldAcctId;

    private String oldPayTag; // 老付费方式

    private String oldUserName; // 用户群名称

    private String RELATION_TYPE_CODE;

    private String USER_ID;

    public IData getMODITAG()
    {
        return MODITAG;
    }

    public String getNewAcctId()
    {
        return newAcctId;
    }

    public String getNewPayTag()
    {
        return newPayTag;
    }

    public String getNewUserName()
    {
        return newUserName;
    }

    public String getOldAcctId()
    {
        return oldAcctId;
    }

    public String getOldPayTag()
    {
        return oldPayTag;
    }

    public String getOldUserName()
    {
        return oldUserName;
    }

    public String getRELATION_TYPE_CODE()
    {
        return RELATION_TYPE_CODE;
    }

    public String getUSER_ID()
    {
        return USER_ID;
    }

    public void setMODITAG(IData mODITAG)
    {
        MODITAG = mODITAG;
    }

    public void setNewAcctId(String newAcctId)
    {
        this.newAcctId = newAcctId;
    }

    public void setNewPayTag(String newPayTag)
    {
        this.newPayTag = newPayTag;
    }

    public void setNewUserName(String newUserName)
    {
        this.newUserName = newUserName;
    }

    public void setOldAcctId(String oldAcctId)
    {
        this.oldAcctId = oldAcctId;
    }

    public void setOldPayTag(String oldPayTag)
    {
        this.oldPayTag = oldPayTag;
    }

    public void setOldUserName(String oldUserName)
    {
        this.oldUserName = oldUserName;
    }

    public void setRELATION_TYPE_CODE(String rELATION_TYPE_CODE)
    {
        RELATION_TYPE_CODE = rELATION_TYPE_CODE;
    }

    public void setUSER_ID(String uSER_ID)
    {
        USER_ID = uSER_ID;
    }

}
