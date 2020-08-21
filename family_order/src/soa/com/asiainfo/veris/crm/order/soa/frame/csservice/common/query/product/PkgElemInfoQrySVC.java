
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

/**
 * @author Administrator
 */
public class PkgElemInfoQrySVC extends CSBizService
{
    public IDataset queryElementTypeByPkgId(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        String packageId = input.getString("PACKAGE_ID");
//        return PkgElemInfoQry.queryPackageTypeCodes(packageId);
        IDataset infos = UpcCall.qryOfferFromSaleActiveByOfferId(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        IData info = infos.getData(0);
        
        IDataset offerComRelList = info.getDataset("OFFER_COM_REL_LIST");
        if(IDataUtil.isNotEmpty(offerComRelList))
        {
            for(int i=0; i<offerComRelList.size(); i++)
            {
                IData offerComRelInfo = offerComRelList.getData(i);
                String offerType = offerComRelInfo.getString("OFFER_TYPE");
                
                IData result = new DataMap();
                result.put("ELEMENT_TYPE_CODE", offerType);
                
                if(!IDataUtil.dataSetContainsKeyAndValue(results, "ELEMENT_TYPE_CODE", offerType))
                {
                    results.add(result);
                }
            }
        }
        
        IDataset offerJoinRelList = info.getDataset("OFFER_JOIN_REL_LIST");
        if(IDataUtil.isNotEmpty(offerJoinRelList))
        {
            for(int i=0; i<offerJoinRelList.size(); i++)
            {
                IData offerJoinRelInfo = offerJoinRelList.getData(i);
                String offerType = offerJoinRelInfo.getString("OFFER_TYPE");
                
                IData result = new DataMap();
                result.put("ELEMENT_TYPE_CODE", offerType);
                
                if(!IDataUtil.dataSetContainsKeyAndValue(results, "ELEMENT_TYPE_CODE", offerType))
                {
                    results.add(result);
                }
            }
        }
        
        IDataset offerGifts = UpcCall.qryOfferGiftsByOfferId(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        if(IDataUtil.isNotEmpty(offerGifts))
        {
            IData result = new DataMap();
            result.put("ELEMENT_TYPE_CODE", "A");
            results.add(result);
        }
        
        if(info.containsKey("OFFER_GROUP_REL_LIST") && IDataUtil.isNotEmpty(info.getDataset("OFFER_GROUP_REL_LIST")))
        {
            IDataset offerGroupRelList = info.getDataset("OFFER_GROUP_REL_LIST");
            if(IDataUtil.isNotEmpty(offerGroupRelList))
            {
                for(int i=0; i<offerGroupRelList.size(); i++)
                {
                    IData offerGroupRelInfo = offerGroupRelList.getData(i);
                    String groupId = offerGroupRelInfo.getString("GROUP_ID");
                    
                    if(groupId.indexOf(packageId) >= 0)
                    {
                        continue;
                    }else
                    {
                        IData result = new DataMap();
                        result.put("ELEMENT_TYPE_CODE", "K");
                        results.add(result);
                        break;
                    }
                }
            }
            
        }
        
        return results;
    }
    
    public IDataset queryElementByPkgId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        return PkgElemInfoQry.getPackageElementByPackageId(packageId);
    }
}
