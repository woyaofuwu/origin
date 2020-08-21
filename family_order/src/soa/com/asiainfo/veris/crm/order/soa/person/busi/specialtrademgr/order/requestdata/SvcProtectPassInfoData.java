
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: MemberData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2013-7-12 下午2:10:03 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-7-12 zhuyu v1.0.0 修改原因
 */
public class SvcProtectPassInfoData
{
    private String start_date;

    private String inst_id;

    private String end_date;

    public final String getEnd_date()
    {
        return end_date;
    }

    public final String getInst_id()
    {
        return inst_id;
    }

    public final String getStart_date()
    {
        return start_date;
    }

    public final void setEnd_date(String endDate)
    {
        end_date = endDate;
    }

    public final void setInst_id(String instId)
    {
        inst_id = instId;
    }

    public final void setStart_date(String startDate)
    {
        start_date = startDate;
    }

}
