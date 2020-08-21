
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata;

/**
 * @author think
 */
public class ProtectionInfoRequestData extends BaseProtectPassInfoRequestData
{
    private String inst_id;

    private String svc_inst_id;

    private String start_date;

    private String attr_value;

    private String flag;

    public final String getAttr_value()
    {
        return attr_value;
    }

    public final String getFlag()
    {
        return flag;
    }

    public final String getInst_id()
    {
        return inst_id;
    }

    public final String getStart_date()
    {
        return start_date;
    }

    public final String getSvc_inst_id()
    {
        return svc_inst_id;
    }

    public final void setAttr_value(String attrValue)
    {
        attr_value = attrValue;
    }

    public final void setFlag(String flag)
    {
        this.flag = flag;
    }

    public final void setInst_id(String instId)
    {
        inst_id = instId;
    }

    public final void setStart_date(String startDate)
    {
        start_date = startDate;
    }

    public final void setSvc_inst_id(String svcInstId)
    {
        svc_inst_id = svcInstId;
    }

}
