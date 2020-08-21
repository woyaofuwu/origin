
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @author xiaozb
 */
public class DestroyUserNowReqData extends BaseReqData
{

    private String removeReasonCode = "";// 销户原因编码

    private String activeTag = "";  // 未激活买断卡销户，要传标记位给资源

    public String getRemoveReasonCode()
    {
        return removeReasonCode;
    }

    public void setRemoveReasonCode(String removeReasonCode)
    {
        this.removeReasonCode = removeReasonCode;
    }

    public String getActiveTag()
    {
        return activeTag;
    }

    public void setActiveTag(String activeTag)
    {
        this.activeTag = activeTag;
    }
}
