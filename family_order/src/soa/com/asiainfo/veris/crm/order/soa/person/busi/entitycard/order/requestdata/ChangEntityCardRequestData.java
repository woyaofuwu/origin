/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-5-29 修改历史 Revision 2014-5-29 下午05:23:29
 */
public class ChangEntityCardRequestData extends BaseReqData
{
    private String oldCardNo;

    private String newCardNo;

    public String getNewCardNo()
    {
        return newCardNo;
    }

    public String getOldCardNo()
    {
        return oldCardNo;
    }

    public void setNewCardNo(String newCardNo)
    {
        this.newCardNo = newCardNo;
    }

    public void setOldCardNo(String oldCardNo)
    {
        this.oldCardNo = oldCardNo;
    }

}
