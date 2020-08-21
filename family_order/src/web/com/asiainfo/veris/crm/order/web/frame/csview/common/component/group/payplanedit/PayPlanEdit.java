
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplanedit;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class PayPlanEdit extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {

        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setPayPlans(null);
            setPayPlanList(null);
        }
    }

    public abstract String getProductId();

    public abstract String getUserId();

    /**
     * render component
     * 
     * @param writer
     * @param cycle
     * @throws Exception
     */
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        if (cycle.isRewinding())
            return;

        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/payplan/PayPlanEdit.js");
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/businesstip/businesstip.js");

        if (StringUtils.isBlank(getProductId()))
            return;

        IData resultData = PayPlanEditView.renderPayPlanEditInfo(this, getProductId(), getUserId());
        if (IDataUtil.isEmpty(resultData))
            return;

        IDataset payPanSrc = resultData.getDataset("PAYPLAN_SRC");
        IDataset payPanList = resultData.getDataset("PAYPLAN_LIST");
        setPayPlans(payPanSrc);
        setPayPlanList(payPanList);

    }

    public abstract void setPayPlanList(IDataset payPlanList);

    public abstract void setPayPlans(IDataset payPlans);

    public abstract void setUserId(String userId);
}
