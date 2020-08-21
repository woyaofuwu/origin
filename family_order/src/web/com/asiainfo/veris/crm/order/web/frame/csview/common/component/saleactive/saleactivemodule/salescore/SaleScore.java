
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.saleactive.saleactivemodule.salescore;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SaleScore extends CSBizTempComponent
{
    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
            setInfos(null);
    }

    public abstract String getEparchyCode();

    public abstract String getPackageId();

    public abstract String getProductId();

    public abstract String getSerialNumber();

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        if (cycle.isRewinding())
            return;

        String packageId = getPackageId();
        String productId = getProductId();
        String eparchyCode = getEparchyCode();
        String serialNumber = getSerialNumber();

        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("PRODUCT_ID", productId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("SERIAL_NUMBER", serialNumber);

        IDataset infos = CSViewCall.call(this, "CS.SaleActiveQuerySVC.querySaleScoresByPackageId", data);
        if (IDataUtil.isEmpty(infos))
            return;
        setUserScore(infos.getData(0).getString("USER_SCORE"));
        infos.getData(0).put("DEPOSIT_VALUE_DISPLAY", getPage().formatDecimal("0.00", String.valueOf(infos.getData(0).getDouble("DEPOSIT_VALUE") / 100)));

        setInfos(infos);
    }

    public abstract void setInfos(IDataset infos);

    public abstract void setUserScore(String userScore);
}
