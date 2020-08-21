package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferMultiRolesAddData;

public class GAddOfferMutilRolesVerb extends GVerb
{

    private GOfferMultiRolesAddData offerData;

    public GAddOfferMutilRolesVerb() throws Exception
    {
        super();
    }

    public GAddOfferMutilRolesVerb(GOfferMultiRolesAddData offerData) throws Exception
    {
        this.offerData = offerData;
    }

    public IData run(BizData bizData) throws Exception
    {
    	return null;
    }
}
