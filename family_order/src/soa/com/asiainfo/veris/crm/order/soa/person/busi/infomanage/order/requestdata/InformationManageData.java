
package com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.requestdata;

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
public class InformationManageData
{
    public String enableTag;// 提示标志

    public String noticeContent;// 提示内容

    public String remark;

    public String tradeId;// 业务流水号

    public String startDate;

    public String tag;// 增加、删除标识

    public final String getEnableTag()
    {
        return enableTag;
    }

    public final String getNoticeContent()
    {
        return noticeContent;
    }

    public final String getRemark()
    {
        return remark;
    }

    public final String getStartDate()
    {
        return startDate;
    }

    public final String getTag()
    {
        return tag;
    }

    public final String getTradeId()
    {
        return tradeId;
    }

    public final void setEnableTag(String enableTag)
    {
        this.enableTag = enableTag;
    }

    public final void setNoticeContent(String noticeContent)
    {
        this.noticeContent = noticeContent;
    }

    public final void setRemark(String remark)
    {
        this.remark = remark;
    }

    public final void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public final void setTag(String tag)
    {
        this.tag = tag;
    }

    public final void setTradeId(String tradeId)
    {
        this.tradeId = tradeId;
    }

}
