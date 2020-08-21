package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferMultiRolesAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferMultiRolesModData;

public class GOffersResolver
{

    public BizData run(BizData bizData, GroupBaseReqData reqData, IDataset offerOrderList) throws Exception
    {

        if (IDataUtil.isEmpty(offerOrderList))
            return bizData;

        for (int i = 0, row = offerOrderList.size(); i < row; i++)
        {
            IData offerOrderInfo = offerOrderList.getData(i);
            String operCode = offerOrderInfo.getString("OPER_CODE");
            if (TRADE_MODIFY_TAG.Add.getValue().equals(operCode))
            {
                // 多角色销售品
                IDataset secondLevRolesOffers = offerOrderInfo.getDataset("ROLE_OFFERS");
                // 组合销售品
                IDataset secondLevConOffers = offerOrderInfo.getDataset("SUBOFFERS");

                if (IDataUtil.isNotEmpty(secondLevRolesOffers))
                {
                    // 多角色销售品
                    GOfferMultiRolesAddData mutieOffer = GOfferMultiRolesAddData.getInstance(offerOrderInfo);
                    GAddOfferMutilRolesVerb mutilRolesVerb = new GAddOfferMutilRolesVerb(mutieOffer);
                    mutilRolesVerb.run(bizData);
                }
                else if (IDataUtil.isNotEmpty(secondLevConOffers))
                {
                    // 组合销售品
                    GOfferCombinedAddData conOffer = GOfferCombinedAddData.getInstance(offerOrderInfo);
                    GAddOfferCombinedVerb combineVerb = new GAddOfferCombinedVerb(conOffer);
                    combineVerb.run(reqData);
                }
                else
                {
                    // 原子销售品
                    GOfferAddData singOffer = GOfferAddData.getInstance(offerOrderInfo);
                    GAddOfferVerb singOfferVerb = new GAddOfferVerb(singOffer);
                    singOfferVerb.run(reqData);

                }
            }
            if (TRADE_MODIFY_TAG.MODI.getValue().equals(operCode))
            {
                // 多角色销售品
                IDataset secondLevRolesOffers = offerOrderInfo.getDataset("ROLE_OFFERS");
                // 组合销售品
                IDataset secondLevConOffers = offerOrderInfo.getDataset("SUBOFFERS");

                if (IDataUtil.isNotEmpty(secondLevRolesOffers))
                {
                    // 多角色销售品
                    GOfferMultiRolesModData mutieOffer = GOfferMultiRolesModData.getInstance(offerOrderInfo);

                }
                else if (IDataUtil.isNotEmpty(secondLevConOffers))
                {
                    // 组合销售品
                    GOfferCombinedModData conOffer = GOfferCombinedModData.getInstance(offerOrderInfo);
                    GModOfferCombinedVerb combineVerb = new GModOfferCombinedVerb(conOffer);
                    combineVerb.run(reqData);
                }
                else
                {
                    // 原子销售品
                    GOfferModData singOffer = GOfferModData.getInstance(offerOrderInfo);
                    GModOfferVerb singOfferVerb = new GModOfferVerb(singOffer);
                    singOfferVerb.run(reqData);
                }
            }
        }

        return bizData;
    }
}
