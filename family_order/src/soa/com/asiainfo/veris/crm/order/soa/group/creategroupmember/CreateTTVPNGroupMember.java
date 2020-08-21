
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateTTVPNGroupMember extends CreateGroupMember
{

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        String net_typecode = UProductInfoQry.getNetTypeCodeByProductId(reqData.getGrpUca().getProductId());

        if (StringUtils.isEmpty(net_typecode))
        {
            net_typecode = "00";
        }
        data.put("NET_TYPE_CODE", net_typecode);// 铁通专线网别13
    }
}
