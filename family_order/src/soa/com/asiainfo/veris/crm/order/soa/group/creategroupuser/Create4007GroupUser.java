
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class Create4007GroupUser extends CreateGroupUser
{

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String netTypeCode = UProductInfoQry.getNetTypeCodeByProductId(reqData.getUca().getProductId());// getStaticValue)

        if (StringUtils.isEmpty(netTypeCode))
        {
            netTypeCode = "00";
        }

        reqData.setNetTypeCode(netTypeCode);
    }

    @Override
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();

        IData tradeData = bizData.getTrade();

        tradeData.put("NET_TYPE_CODE", reqData.getNetTypeCode());
    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("NET_TYPE_CODE", reqData.getNetTypeCode());
    }

    @Override
    protected void setTradefeeDefer(IData map) throws Exception
    {
        super.setTradefeeDefer(map);

        map.put("FEE_MODE", "0");
        map.put("ACT_TAG", "1");
        map.put("FEE_TYPE_CODE", "9720");
        map.put("DEFER_CYCLE_ID", "-1");
        map.put("DEFER_ITEM_CODE", "18248");
    }

}
