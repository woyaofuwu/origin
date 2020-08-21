/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-5-20 修改历史 Revision 2014-5-20 上午09:44:22
 */
public class NcardsOneAcctCancelReqData extends BaseReqData
{

    private String userIdSecond;

    private String custIdSecond;

    private String userIdA;

    private String serialNumberA;

    private String curCycleId;

    private String serialNumberSecond;

    public String getCurCycleId()
    {
        return curCycleId;
    }

    public String getCustIdSecond()
    {
        return custIdSecond;
    }

    public String getSerialNumberA()
    {
        return serialNumberA;
    }

    public String getSerialNumberSecond()
    {
        return serialNumberSecond;
    }

    public String getUserIdA()
    {
        return userIdA;
    }

    public String getUserIdSecond()
    {
        return userIdSecond;
    }

    public void setCurCycleId(String curCycleId)
    {
        this.curCycleId = curCycleId;
    }

    public void setCustIdSecond(String custIdSecond)
    {
        this.custIdSecond = custIdSecond;
    }

    public void setSerialNumberA(String serialNumberA)
    {
        this.serialNumberA = serialNumberA;
    }

    public void setSerialNumberSecond(String serialNumberSecond)
    {
        this.serialNumberSecond = serialNumberSecond;
    }

    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

    public void setUserIdSecond(String userIdSecond)
    {
        this.userIdSecond = userIdSecond;
    }

}
