package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.offer;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class OfferDetailSVC extends CSBizService
{
    public IDataset getOfferDetail(IData param) throws Exception
    {
        String offerCode = param.getString("OFFER_CODE");
        String offerType = param.getString("OFFER_TYPE");
        String eparchyCode = param.getString("ROUTE_CODE");
        
        IDataset results = UpcCall.queryOfferDetailsByOfferId(offerType, offerCode, eparchyCode);
        
        return results;
    }
}
