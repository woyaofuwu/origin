
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.relaproductinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.web.BaseTempComponent;

public class RelaProductInfo extends BaseTempComponent
{

    private IData productInfo;

    private IData compProductInfo;

    /**
     * @return compProductInfo
     */
    public IData getCompProductInfo()
    {

        return compProductInfo;
    }

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
     * @param compProductInfo
     *            要设置的 compProductInfo
     */
    public void setCompProductInfo(IData compProductInfo)
    {

        this.compProductInfo = compProductInfo;
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
