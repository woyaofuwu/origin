
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.ucaview.custinfoview;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class CustInfoView extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        setCustInfoView(null);

        super.cleanupAfterRender(cycle);
    }

    public abstract IData getCustInfoView();

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        super.renderComponent(stringBuilder, writer, cycle);
    }

    public abstract void setCustInfoView(IData custInfoView);

}
