package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class CreateOcsGroupUserPageDataTrans  extends CreateGroupUserPageDataTrans{

    protected IDataset transformOfferList(IDataset offerinfo) throws Exception{
        
          IDataset result = super.transformOfferList(offerinfo);
        IDataset paramAttr = new DatasetList();
        IDataset offersInfo = offerinfo.first().getDataset("SUBOFFERS");
        for (Object offer : offersInfo)
        {
            IData offerInfos = (IData)offer;
            IDataset offerChaInfo =  offerInfos.getDataset("OFFER_CHA_SPECS");  
            for (Object offers : result)
            {
                IData offerInf = (IData)offers;
                String elementId = offerInf.getString("ELEMENT_ID");
                if(IDataUtil.isNotEmpty(offerChaInfo)){
                    for (Object offerCha : offerChaInfo)
                    {
                        IData offerChaInfos = (IData)offerCha;
                            if(elementId.equals(offerInfos.getString("OFFER_CODE"))){
                                    IData offerChas = new DataMap();
                                    offerChas.put("ATTR_VALUE", offerChaInfos.getString("ATTR_VALUE"));
                                    offerChas.put("ATTR_CODE", offerChaInfos.getString("ATTR_CODE"));
                                    paramAttr.add(offerChas);
                                    offerInf.put("ATTR_PARAM", paramAttr);
                            }
                        
                    }
            }
                
            }
        }
        return result;
        
    }

}
