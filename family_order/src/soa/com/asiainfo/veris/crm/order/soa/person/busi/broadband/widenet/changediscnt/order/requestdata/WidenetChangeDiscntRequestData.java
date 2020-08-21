
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changediscnt.order.requestdata;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class WidenetChangeDiscntRequestData extends BaseReqData
{
    private IDataset spcUserDiscnts;

    private String spcDelDiscntFee;

    private String spcDelDiscntPaymentId;

    private String campusTag;

    public String getCampusTag()
    {
        return campusTag;
    }

    public String getSpcDelDiscntFee()
    {
        return spcDelDiscntFee;
    }

    public String getSpcDelDiscntPaymentId()
    {
        return spcDelDiscntPaymentId;
    }

    public IDataset getSpcUserDiscnts()
    {
        return spcUserDiscnts;
    }

    public void setCampusTag(String campusTag)
    {
        this.campusTag = campusTag;
    }

    public void setSpcDelDiscntFee(String spcDelDiscntFee)
    {
        this.spcDelDiscntFee = spcDelDiscntFee;
    }

    public void setSpcDelDiscntPaymentId(String spcDelDiscntPaymentId)
    {
        this.spcDelDiscntPaymentId = spcDelDiscntPaymentId;
    }

    public void setSpcUserDiscnts(IDataset spcUserDiscnts)
    {
        this.spcUserDiscnts = spcUserDiscnts;
    }
}
