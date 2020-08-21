
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.person.ucaview.custinfoview;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

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
