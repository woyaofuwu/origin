
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizIntercept;

public class GroupBaseIntercept extends CSBizIntercept
{
    @Override
    public boolean after(String svcName, IData head, IData indata, IDataset idataset) throws Exception
    {
        super.after(svcName, head, indata, idataset);
        return true;
    }

    @Override
    public boolean before(String svcName, IData head, IData indata) throws Exception
    {
        super.before(svcName, head, indata);
        return true;
    }

}
