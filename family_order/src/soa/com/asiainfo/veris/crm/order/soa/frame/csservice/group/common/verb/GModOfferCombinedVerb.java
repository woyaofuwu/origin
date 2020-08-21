package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;
import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferDelData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferModData;

public class GModOfferCombinedVerb extends GVerb
{
    private GOfferCombinedModData offerData;

    public GModOfferCombinedVerb() throws Exception
    {
    	super();
    }

    public GModOfferCombinedVerb(GOfferCombinedModData offerData) throws Exception
    {
    	super();
        this.offerData = offerData;
    }

    public IData run(GroupBaseReqData reqData) throws Exception
    {
    	
    	IData offerEntity = null;
    	
        if (offerData == null)
        {
            return null;
        }
        
        GOfferModData offerModData = offerData.getSelfOfferModData();
        if (offerModData == null)
        {
        	return null;
        }

        GModOfferVerb bv = new GModOfferVerb(offerModData);
        
        offerEntity = bv.run(reqData);
        
        if (offerEntity == null)
        {
            // 应该要报错才对， 说明前面解析的串有问题
            return null;
        }
        
        String consOfferInsId = offerEntity.getString("OFFER_INS_ID");
        String consOfferId = offerEntity.getString("OFFER_ID");
        String consOfferType = offerEntity.getString("OFFER_TYPE");
        
        // 分解组合销售品下的新增原子销售品
        List<GOfferAddData> atomAddOffers = offerData.getSubAtomAddOffers();
        if (atomAddOffers != null && atomAddOffers.size() > 0)
        {
            for (GOfferAddData atomAddOffer : atomAddOffers)
            {

                atomAddOffer.addOfferRel(consOfferId, consOfferType, consOfferInsId, "0");// 需要给atomOffer的销售品绑上上层销售品的ID
                GAddOfferVerb abv = new GAddOfferVerb(atomAddOffer);
                
                abv.run(reqData);
            }
        }

        // 分解组合销售品下的退订原子销售品
        List<GOfferDelData> atomDelOffers = offerData.getSubAtomDelOffers();
        if (atomDelOffers != null && atomDelOffers.size() > 0)
        {
            for (GOfferDelData atomDelOffer : atomDelOffers)
            {
                GDelOfferVerb abv = new GDelOfferVerb(atomDelOffer);
                // abv.setSubOffer(true);// 设置子商品操作
                abv.run(reqData);
            }
        }
        
        // 分解组合销售品下的修改原子销售品
        List<GOfferModData> atomModOffers = offerData.getSubAtomModOffers();
        if (atomModOffers != null && atomModOffers.size() > 0)
        {
            for (GOfferModData atomModOffer : atomModOffers)
            {
                GModOfferVerb abv = new GModOfferVerb(atomModOffer);
                abv.run(reqData);
            }
        }

        // 分解组合销售品下的组合销售品
        List<GOfferCombinedAddData> combinedAddOffers = offerData.getSubCombinedAddOffers();
        if (combinedAddOffers != null && combinedAddOffers.size() > 0)
        {
            for (GOfferCombinedAddData combinedAddOffer : combinedAddOffers)
            {
                combinedAddOffer.addOfferRel(consOfferId, consOfferType, consOfferInsId, "0");// 需要给consOffer的销售品绑上上层销售品的ID
                GAddOfferCombinedVerb cv = new GAddOfferCombinedVerb(combinedAddOffer);
                
                cv.run(reqData);
            }
        }

        // 分解组合销售品下的组合销售品
        List<GOfferCombinedModData> combinedModOffers = offerData.getSubCombinedModOffers();
        if (combinedModOffers != null && combinedModOffers.size() > 0)
        {
            for (GOfferCombinedModData combinedModOffer : combinedModOffers)
            {
                GModOfferCombinedVerb cv = new GModOfferCombinedVerb(combinedModOffer);
                
                cv.run(reqData);
            }
        }
        return offerEntity;
    }

    public GOfferCombinedModData getOfferData()
    {
        return offerData;
    }
}
