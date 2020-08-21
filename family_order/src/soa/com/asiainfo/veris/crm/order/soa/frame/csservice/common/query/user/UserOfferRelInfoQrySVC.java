package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserOfferRelInfoQrySVC extends CSBizService{

    private static final long serialVersionUID = 1L;

    public IDataset qryUserOfferRelInfosByRelOfferInstId(IData input) throws Exception
    {
        String rel_offer_ins_id = input.getString("REL_OFFER_INS_ID");
        IDataset dataset = UserOfferRelInfoQry.qryUserOfferRelInfosByRelOfferInstId(rel_offer_ins_id);

        return dataset;
    }
}
