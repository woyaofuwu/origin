package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class GOfferMultiRolesAddData extends OfferData
{

    private GOfferCombinedAddData offerData;

    private List<GOfferRoleAddData> roleOffers;
    
    private String validDate;
    
    private String expireDate;

    public void setSelfConsOfferData(GOfferCombinedAddData offerData)
    {
        this.offerData = offerData;
    }

    public GOfferCombinedAddData getSelfConsOfferData()
    {
        return offerData;
    }

    public void addSubRoleOfferData(GOfferRoleAddData roleOffer)
    {
        if (roleOffer == null)
            return;
        if (roleOffers == null)
            roleOffers = new ArrayList<GOfferRoleAddData>();
        roleOffers.add(roleOffer);
    }

    public List<GOfferRoleAddData> getSubRoleOffers()
    {
        return roleOffers;
    }

    public void addOfferRel(String offerRelId, String offerType, String offerRelInsId, String relType)
    {
        offerData.addOfferRel(offerRelId, offerType, offerRelInsId, relType);
    }

    public static GOfferMultiRolesAddData getInstance(IData offerData) throws Exception
    {
        GOfferMultiRolesAddData offer = new GOfferMultiRolesAddData();
        GOfferCombinedAddData selfOfferData = GOfferCombinedAddData.getInstance(offerData);
        offer.setSelfConsOfferData(selfOfferData);

        IDataset roleOffers = offerData.getDataset("ROLE_OFFERS");
        if (IDataUtil.isNotEmpty(roleOffers))
        {
            for (Object roleOffer : roleOffers)
            {
                offer.addSubRoleOfferData(GOfferRoleAddData.getInstance((IData)roleOffer));
            }
        }

        return offer;

    }

    public String getValidDate()
    {
        return validDate;
    }

    public void setValidDate(String validDate)
    {
        this.validDate = validDate;
    }

    public String getExpireDate()
    {
        return expireDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }
}
