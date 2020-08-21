package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IData;

public class GOfferMultiRolesModData
{

    private GOfferCombinedModData offerModData;

    private List<GOfferRoleModData> roleModOffers;

    public GOfferCombinedModData getOfferModData()
    {
        return offerModData;
    }

    public void setOfferModData(GOfferCombinedModData offerModData)
    {
        this.offerModData = offerModData;
    }

    public static GOfferMultiRolesModData getInstance(IData offerData) throws Exception
    {
        GOfferMultiRolesModData offer = new GOfferMultiRolesModData();

        GOfferCombinedModData selfOfferData = GOfferCombinedModData.getInstance(offerData);
        offer.setOfferModData(selfOfferData);

        IDataset roleOffers = offerData.getDataset("ROLE_OFFERS");
        if (IDataUtil.isNotEmpty(roleOffers))
        {
            for (Object roleOffer : roleOffers)
            {
            	offer.addModSubRoleOfferData(GOfferRoleModData.getInstance((IData)roleOffer));
            }
        }

        return offer;

    }

    public List<GOfferRoleModData> getRoleModOffers()
    {
        return roleModOffers;
    }

    public void setRoleModOffers(List<GOfferRoleModData> roleModOffers)
    {
        this.roleModOffers = roleModOffers;
    }

    public void addModSubRoleOfferData(GOfferRoleModData roleOffer)
    {
        if (roleOffer == null)
            return;
        if (roleModOffers == null)
            roleModOffers = new ArrayList<GOfferRoleModData>();
        roleModOffers.add(roleOffer);
    }
}
