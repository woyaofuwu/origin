/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-5-15 修改历史 Revision 2014-5-15 下午03:25:29
 */
public class NcardsOneAcctSaleReqData extends BaseReqData
{

    private String startCycId;

    private String serialNumberMain;

    private String serialNumberSecond;

    private String userIdMain;

    private String userIdSecond;

    public String getSerialNumberMain()
    {
        return serialNumberMain;
    }

    public String getSerialNumberSecond()
    {
        return serialNumberSecond;
    }

    public String getStartCycId()
    {
        return startCycId;
    }

    public String getUserIdMain()
    {
        return userIdMain;
    }

    public String getUserIdSecond()
    {
        return userIdSecond;
    }

    public void setSerialNumberMain(String serialNumberMain)
    {
        this.serialNumberMain = serialNumberMain;
    }

    public void setSerialNumberSecond(String serialNumberSecond)
    {
        this.serialNumberSecond = serialNumberSecond;
    }

    public void setStartCycId(String startCycId)
    {
        this.startCycId = startCycId;
    }

    public void setUserIdMain(String userIdMain)
    {
        this.userIdMain = userIdMain;
    }

    public void setUserIdSecond(String userIdSecond)
    {
        this.userIdSecond = userIdSecond;
    }

}
