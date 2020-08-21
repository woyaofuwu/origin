
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class MemberReqData extends GroupBaseReqData
{
    private UcaData grpUca;

    private String firstTimeNextAcct = "";

    private boolean diversifyBooking = false; // 分散账期预约

    private boolean isOutNet = false; // 是否网外号码

    protected String baseMebProductId = ""; // 成员主产品

    public String getBaseMebProductId()
    {
        return baseMebProductId;
    }

    public String getFirstTimeNextAcct()
    {
        return firstTimeNextAcct;
    }

    public final UcaData getGrpUca()
    {
        return grpUca;
    }

    public boolean isDiversifyBooking()
    {
        return diversifyBooking;
    }

    public boolean isOutNet()
    {
        return isOutNet;
    }

    public void setBaseMebProductId(String baseMebProductId)
    {
        this.baseMebProductId = baseMebProductId;
    }

    public void setDiversifyBooking(boolean diversifyBooking)
    {
        this.diversifyBooking = diversifyBooking;
    }

    public void setFirstTimeNextAcct(String firstTimeNextAcct)
    {
        this.firstTimeNextAcct = firstTimeNextAcct;
    }

    public final void setGrpUca(UcaData uca)
    {
        grpUca = uca;
    }

    public void setOutNet(boolean isOutNet)
    {
        this.isOutNet = isOutNet;
    }
}
