
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.createOutNetNumber;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class CreateOutNetNumberReqData extends MemberReqData
{

    protected boolean isAddAcct = false; // 是否新增账户

    private IDataset productParamInfo; // 产品参数信息

    public IDataset getProductParamInfo()
    {
        return productParamInfo;
    }

    public boolean isAddAcct()
    {
        return isAddAcct;
    }

    public void setAddAcct(boolean isAddAcct)
    {
        this.isAddAcct = isAddAcct;
    }

    public void setProductParamInfo(IDataset productParamInfo)
    {
        this.productParamInfo = productParamInfo;
    }
}
