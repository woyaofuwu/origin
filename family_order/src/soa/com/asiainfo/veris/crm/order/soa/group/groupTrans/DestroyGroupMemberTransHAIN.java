
package com.asiainfo.veris.crm.order.soa.group.groupTrans;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMemberTrans;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.util.GroupProductUtil;

public class DestroyGroupMemberTransHAIN extends DestroyGroupMemberTrans
{
    @Override
    public void checkRequestData(IData iData) throws Exception
    {
        super.checkRequestData(iData);

        String productId = iData.getString("PRODUCT_ID");

        GroupProductUtil.checkProductCanDo(iData.getString("OPER_TYPE"), productId);
    }

}
