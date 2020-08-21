
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangePushemailMemElement extends ChangeMemElement
{

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        // 海南
        data.put("RSRV_STR1", reqData.getGrpUca().getUser().getUserId());
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        data.put("RSRV_STR6", reqData.getUca().getCustPerson().getCustName());

    }
}
