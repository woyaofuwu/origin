
package com.asiainfo.veris.crm.order.soa.person.busi.usergrpdiscntspecdeal.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: UserDiscntSpecDealReqData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: maoke
 * @date: May 27, 2014 8:01:11 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 27, 2014 maoke v1.0.0 修改原因
 */
public class UserGrpDiscntSpecDealReqData extends BaseReqData
{
    private String discntCode;

    private String instId;

    private String startDate;

    private String oldStartDate;

    private String endDate;

    private String oldEndDate;
    
    private String discntRemark;

    public String getDiscntCode()
    {
        return discntCode;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getOldEndDate()
    {
        return oldEndDate;
    }

    public String getOldStartDate()
    {
        return oldStartDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setDiscntCode(String discntCode)
    {
        this.discntCode = discntCode;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setOldEndDate(String oldEndDate)
    {
        this.oldEndDate = oldEndDate;
    }

    public void setOldStartDate(String oldStartDate)
    {
        this.oldStartDate = oldStartDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getDiscntRemark()
    {
        return discntRemark;
    }

    public void setDiscntRemark(String discntRemark)
    {
        this.discntRemark = discntRemark;
    }
}
