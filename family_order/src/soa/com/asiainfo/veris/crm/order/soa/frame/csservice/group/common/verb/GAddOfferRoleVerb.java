package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferRoleAddData;

public class GAddOfferRoleVerb extends GVerb
{

    private GOfferRoleAddData roleOffer;

    public GAddOfferRoleVerb() throws Exception
    {
        super();
    }

    public GAddOfferRoleVerb(GOfferRoleAddData roleOffer) throws Exception
    {
    	super();
        this.roleOffer = roleOffer;
    }

    public IData run(BizData bizData) throws Exception
    {
    	return null;
    }

}
