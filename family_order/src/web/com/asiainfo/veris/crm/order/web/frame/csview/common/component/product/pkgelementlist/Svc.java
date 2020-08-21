
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.pkgelementlist;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class Svc extends CSBizTempComponent
{
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        this.setSvc(null);
        this.setSvcList(null);
    }

    public abstract IData getSvc();

    public abstract IDataset getSvcList();

    @Override
    public void renderComponent(StringBuilder arg0, IMarkupWriter arg1, IRequestCycle arg2) throws Exception
    {

    }

    public abstract void setSvc(IData svc);

    public abstract void setSvcList(IDataset svcList);

}
