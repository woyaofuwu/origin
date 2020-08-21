
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone.order.requestdata;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class FamilyFixPhoneReqData extends BaseReqData
{

    private IData pageInfo;

    public IData getPageInfo()
    {
        return pageInfo;
    }

    public void setPageInfo(IData pageInfo)
    {
        this.pageInfo = pageInfo;
    }
}
