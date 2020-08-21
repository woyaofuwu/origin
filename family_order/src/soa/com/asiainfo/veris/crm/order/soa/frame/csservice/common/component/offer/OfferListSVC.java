package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.offer;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class OfferListSVC extends CSBizService
{
    public IDataset getOfferList(IData param) throws Exception
    {
        String mainOfferId = param.getString("MAIN_OFFER_ID");
        String offerLineId = param.getString("OFFER_LINE_ID");
        String categoryId = param.getString("CATEGORY_ID");
        
        IDataset results = UpcCall.queryOfferByMutiCateId(categoryId, offerLineId);
        if (IDataUtil.isNotEmpty(results))
        {
            for (int i = results.size()-1; i >= 0; i--)
            {
                if (IDataUtil.isEmpty(results.getData(i).getDataset("OFFER_LIST")))
                {
                    results.remove(i);
                }
                else
                {
                    IDataset offerList = results.getData(i).getDataset("OFFER_LIST");
                    for (int j = 0, size = offerList.size(); j < size; j++)
                    {
                        offerList.getData(j).put("REL_OFFER_ID", mainOfferId);
                        offerList.getData(j).put("REL_OFFER_TYPE", "P");
                    }
                }
            }
        }
        
        return results;
    }
}
