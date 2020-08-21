
package com.asiainfo.veris.crm.order.soa.group.groupTrans;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeGroupMemberTrans;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.parse.ElementInfoParse;

public class ChangeGroupMemberTransHAIN extends ChangeGroupMemberTrans
{
    // 重载基类
    public void checkRequestData(IData iData) throws Exception
    {
        super.checkRequestData(iData);
    }

    // 重写基类
    protected void parseElement(IData iData) throws Exception
    {
        ElementInfoParse.parseElmentInfoChsMeb(iData);

    }

}
