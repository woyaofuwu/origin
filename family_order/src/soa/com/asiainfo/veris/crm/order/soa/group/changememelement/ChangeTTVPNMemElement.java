
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeTTVPNMemElement extends ChangeMemElement
{

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();

        String net_typecode = UProductInfoQry.getNetTypeCodeByProductId(reqData.getGrpUca().getProductId());
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        if (StringUtils.isEmpty(net_typecode))
        {
            net_typecode = "00";
        }
        tradeData.put("NET_TYPE_CODE", net_typecode);

        tradeData.put("RSRV_STR1", reqData.getGrpUca().getUserId());
        tradeData.put("RSRV_STR2", relationTypeCode);
        tradeData.put("RSRV_STR3", reqData.getUca().getSerialNumber());
        tradeData.put("RSRV_STR6", reqData.getGrpUca().getCustGroup().getCustName());
    }
}
