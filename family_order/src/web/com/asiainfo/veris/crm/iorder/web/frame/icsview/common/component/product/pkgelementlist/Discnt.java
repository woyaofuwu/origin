
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.product.pkgelementlist;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class Discnt extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        this.setDiscnt(null);
        this.setDiscntList(null);
    }

    public abstract IData getDiscnt();

    public abstract IDataset getDiscntList();

    @Override
    public void renderComponent(StringBuilder arg0, IMarkupWriter arg1, IRequestCycle arg2) throws Exception
    {

    }

    public abstract void setDiscnt(IData discnt);

    public abstract void setDiscntList(IDataset discntList);

}
