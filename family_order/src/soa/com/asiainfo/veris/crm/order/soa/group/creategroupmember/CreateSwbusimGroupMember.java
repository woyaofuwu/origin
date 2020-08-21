
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateSwbusimGroupMember extends CreateGroupMember
{
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER
        data.put("RSRV_STR4", reqData.getUca().getProductId());
        data.put("RSRV_STR8", reqData.getUca().getCustomer().getCustName());
        data.put("RSRV_STR9", reqData.getUca().getAcctId());

    }
}
