
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.memberproductinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.web.BaseTempComponent;

public class MemberProductInfo extends BaseTempComponent
{

    private IData productInfo;

    /**
     * @return productInfo
     */
    public IData getProductInfo()
    {

        return productInfo;
    }

    /**
     * render component
     * 
     * @param writer
     * @param cycle
     */
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
    public void setProductInfo(IData productInfo)
    {

        this.productInfo = productInfo;
    }
}
