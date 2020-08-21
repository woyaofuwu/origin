
package com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 亲情成员对象
 * 
 * @author Administrator
 */
public class MemberData
{

    private UcaData uca;// 成员三户资料

    private String modifyTag;// 操作标识

    private String instId;// 实例标识

    private String orderNo;// 顺序号

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public UcaData getUca()
    {
        return uca;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public void setUca(UcaData uca)
    {
        this.uca = uca;
    }
}
