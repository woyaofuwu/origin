
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UItemAInfoQrySVC extends CSBizService
{
	private static final Logger log = Logger.getLogger(UItemAInfoQrySVC.class);

    public IDataset qryOfferChaSpecsByIdAndIdType(IData iData) throws Exception
    { 
        return UItemAInfoQry.qryOfferChaSpecsByIdAndIdType(iData.getString("OFFER_TYPE"), iData.getString("OFFER_ID"),null);
    }
}
