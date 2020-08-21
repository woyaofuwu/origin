package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class USaleScoreInfoQry
{
    public static IData queryById(String scoreDeductId) throws Exception
    {
        IData offerInfo = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_SCORE, scoreDeductId);
        if(IDataUtil.isNotEmpty(offerInfo))
        {
            IData cha = UpcCall.qryOfferComChaTempChaByCond(scoreDeductId, BofConst.ELEMENT_TYPE_CODE_SCORE);
            offerInfo.putAll(cha);
            offerInfo.put("SCORE_DEDUCT_ID", scoreDeductId);
        }
        
        return offerInfo;
    }
}
