
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupuserinfos;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IDataset;
import com.ailk.web.BaseTempComponent;

public class GroupUserInfos extends BaseTempComponent
{

    private IDataset useInfos;

    /**
     * @return productInfo
     */
    public IDataset getUseInfos()
    {

        return useInfos;
    }

    /**
     * render component
     * 
     * @param writer
     * @param cycle
     */
    @Override
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;

        super.renderComponent(writer, cycle);
    }

    /**
     * @param productInfo
     *            要设置的 productInfo
     */
    public void setUseInfos(IDataset useInfos)
    {

        this.useInfos = useInfos;
    }

}
