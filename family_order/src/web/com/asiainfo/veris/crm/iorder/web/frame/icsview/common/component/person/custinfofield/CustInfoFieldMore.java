
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.person.custinfofield;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

public abstract class CustInfoFieldMore extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        setCustInfo(null);
        setAssureInfo(null);
        super.cleanupAfterRender(cycle);
    }

    public abstract IData getCustInfo();

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setAssureInfo(IData assureInfo);
}
