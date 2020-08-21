/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-4-15 修改历史 Revision 2014-4-15 下午03:07:59
 */
public class BaseChlCommFeeSubRequestData extends BaseReqData
{

    private String custType;

    private String newTrade;

    private String phoneSub;

    public String getCustType()
    {
        return custType;
    }

    public String getNewTrade()
    {
        return newTrade;
    }

    public String getPhoneSub()
    {
        return phoneSub;
    }

    public void setCustType(String custType)
    {
        this.custType = custType;
    }

    public void setNewTrade(String newTrade)
    {
        this.newTrade = newTrade;
    }

    public void setPhoneSub(String phoneSub)
    {
        this.phoneSub = phoneSub;
    }
}
