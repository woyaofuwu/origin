
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyTTVPNGroupMember extends DestroyGroupMember
{

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();

        String net_typecode = UProductInfoQry.getNetTypeCodeByProductId(reqData.getGrpUca().getProductId());

        if (StringUtils.isEmpty(net_typecode))
        {
            net_typecode = "00";
        }
        tradeData.put("NET_TYPE_CODE", net_typecode);
    }
}
