
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.groupproductstree;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class GroupProductsTreePopup extends CSBizTempComponent
{

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
    }

    public abstract String getMethod();

    public abstract String getParentTypeCode();

    public abstract IData getTreeParams();

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/product/groupproductstree/GroupProductsTree.js");
        try
        {

            IData params = new DataMap();
            params.put("PARENT_TYPE_CODE", this.getParentTypeCode());
            params.put("METHOD_NAME", this.getMethod());
            IData treeparams = getTreeParams();
            if (treeparams != null)
                params.putAll(treeparams);
            setTreeParams(params);

        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        super.renderComponent(writer, cycle);
    }

    public abstract void setTreeParams(IData treeparams);
}
