/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-7-14 修改历史 Revision 2014-7-14 下午09:36:12
 */
public class SyncPayPromptReqData extends BaseReqData
{
    private String modifyTag;

    private String warningFee;

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getWarningFee()
    {
        return warningFee;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setWarningFee(String warningFee)
    {
        this.warningFee = warningFee;
    }

}
