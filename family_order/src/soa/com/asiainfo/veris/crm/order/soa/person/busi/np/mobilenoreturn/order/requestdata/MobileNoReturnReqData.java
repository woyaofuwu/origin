
package com.asiainfo.veris.crm.order.soa.person.busi.np.mobilenoreturn.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class MobileNoReturnReqData extends BaseReqData
{

    public String resCode;

    public String tagCode;

    public String userNpTag;// 是否需要修资料包括。资源 4需要其它不需要

    public String newUserTagSet;

    public final String getNewUserTagSet()
    {
        return newUserTagSet;
    }

    public final String getResCode()
    {
        return resCode;
    }

    public final String getTagCode()
    {
        return tagCode;
    }

    public final String getUserNpTag()
    {
        return userNpTag;
    }

    public final void setNewUserTagSet(String newUserTagSet)
    {
        this.newUserTagSet = newUserTagSet;
    }

    public final void setResCode(String resCode)
    {
        this.resCode = resCode;
    }

    public final void setTagCode(String tagCode)
    {
        this.tagCode = tagCode;
    }

    public final void setUserNpTag(String userNpTag)
    {
        this.userNpTag = userNpTag;
    }

}
