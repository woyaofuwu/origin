
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferDelData;

public class GDelOfferCombinedVerb extends GVerb
{
    private GOfferDelData offerData;

    public GDelOfferCombinedVerb() throws Exception
    {
        super();
    }

    public GDelOfferCombinedVerb(GOfferDelData offerData) throws Exception
    {
    	super();
        this.offerData = offerData;
    }

    public IData run(BizData bizData) throws Exception
    {
    	return null;
    }
//    public BaseEntity run(EntityContainer ec) throws Exception
//    {
//        if (offerData == null)
//        {
//            CSBizException.bizerr(OrderException.CRM_ORDER_374);
//        }
//        GOfferDelData selfOfferDelData = offerData.getSelfOfferDelData();
//        if (selfOfferDelData == null)
//        {
//            CSBizException.bizerr(OrderException.CRM_ORDER_375);
//        }
//
//        GDelOfferVerb ov = new GDelOfferVerb(sca, selfOfferDelData);
//        VerbRunner.run(ov, ec);
//
//        String consOfferInsId = selfOfferDelData.getOfferInsId();
//        // 分解组合销售品下的新增原子销售品
//        List<GOfferAddData> atomAddOffers = offerData.getSubAtomAddOffers();
//        if (IDataUtil.isNotEmpty(atomAddOffers))
//        {
//            for (GOfferAddData atomAddOffer : atomAddOffers)
//            {
//
//                atomAddOffer.addOfferRel(consOfferInsId, EcConstants.OFFER_COM_REL_ROLE_ID_SUBOFFER);// 需要给atomOffer的销售品绑上上层销售品的ID
//                GAddOfferVerb abv = new GAddOfferVerb(sca, atomAddOffer);
//                VerbRunner.run(abv, ec);
//            }
//        }
//
//        // 分解组合销售品下的退订原子销售品
//        List<GOfferDelData> atomDelOffers = offerData.getSubAtomDelOffers();
//        if (IDataUtil.isNotEmpty(atomDelOffers))
//        {
//            for (GOfferDelData atomDelOffer : atomDelOffers)
//            {
//                GDelOfferVerb abv = new GDelOfferVerb(sca, atomDelOffer);
//                VerbRunner.run(abv, ec);
//            }
//        }
//
//        // 分解组合销售品下的变更原子销售品
//        List<GOfferModData> atomModOffers = offerData.getSubAtomModOffers();
//        if (IDataUtil.isNotEmpty(atomModOffers))
//        {
//            for (GOfferModData atomModOffer : atomModOffers)
//            {
//                if (IDataUtil.isNotEmpty(atomModOffer.getAddProds()))
//                {
//                    atomModOffer.addOfferRel(consOfferInsId, EcConstants.OFFER_COM_REL_ROLE_ID_SUBOFFER);// 里面有新增prods,需要给atomOffer的销售品绑上上层销售品的ID,
//                }
//                GModOfferVerb abv = new GModOfferVerb(sca, atomModOffer);
//                VerbRunner.run(abv, ec);
//            }
//        }
//
//        return null;
//    }
    
}
