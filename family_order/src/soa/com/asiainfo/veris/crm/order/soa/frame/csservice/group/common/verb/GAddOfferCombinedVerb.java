package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedAddData;

public class GAddOfferCombinedVerb extends GVerb
{

    private GOfferCombinedAddData offerData;

    public GAddOfferCombinedVerb() throws Exception
    {
        super();
    }

    public GAddOfferCombinedVerb(GOfferCombinedAddData offerData) throws Exception
    {
        super();
        this.offerData = offerData;
    }

    public IData run(GroupBaseReqData reqData) throws Exception
    {
        if (offerData == null)
            return null;

        // 自身的原子销售品
        GOfferAddData selfOfferAddData = offerData.getSelfOfferData();
        GAddOfferVerb selfOfferAddVerb = new GAddOfferVerb(selfOfferAddData);
        IData offerEntity = selfOfferAddVerb.run(reqData);

        String consOfferInsId = offerEntity.getString("OFFER_INS_ID");
        String consOfferId = offerEntity.getString("OFFER_ID");
        String consOfferType = offerEntity.getString("OFFER_TYPE");

        // 分解组合销售品下的原子销售品
        List<GOfferAddData> atomOffers = offerData.getSubAtomOffers();
        if (atomOffers != null && atomOffers.size() > 0)
        {
            for (GOfferAddData atomOffer : atomOffers)
            {
                if (StringUtils.isBlank(atomOffer.getValidDate()) || StringUtils.isBlank(atomOffer.getExpireDate()))
                {
                    atomOffer.setValidDate(offerEntity.getString("VALID_DATE"));
                    atomOffer.setExpireDate(offerEntity.getString("EXPIRE_DATE"));
                }
                
                atomOffer.addOfferRel(consOfferId, consOfferType, consOfferInsId, "0");// 需要给atomOffer的销售品绑上上层销售品的ID
                GAddOfferVerb abv = new GAddOfferVerb(atomOffer);

                abv.run(reqData);
            }
        }

        // 分解组合销售品下的组合销售品
        List<GOfferCombinedAddData> combinedOffers = offerData.getSubCombinedOffers();
        if (combinedOffers != null && combinedOffers.size() > 0)
        {
            for (GOfferCombinedAddData combinedOffer : combinedOffers)
            {
                combinedOffer.addOfferRel(consOfferId, consOfferType, consOfferInsId, "0");// 需要给consOffer的销售品绑上上层销售品的ID
                GAddOfferCombinedVerb cv = new GAddOfferCombinedVerb(combinedOffer);

                cv.run(reqData);
            }
        }

        return offerEntity;

    }

    public GOfferCombinedAddData getOfferData()
    {
        return offerData;
    }

    public void setOfferData(GOfferCombinedAddData offerData)
    {
        this.offerData = offerData;
    }
}
