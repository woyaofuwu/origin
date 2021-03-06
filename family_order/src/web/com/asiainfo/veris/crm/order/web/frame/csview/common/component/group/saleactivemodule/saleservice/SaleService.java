
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.saleactivemodule.saleservice;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SaleService extends CSBizTempComponent
{
    protected void cleanupAfterRender(IRequestCycle cycle)
    {

        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setInfos(null);
        }
    }

    public abstract String getBasicDate();

    public abstract String getCampnType();

    public abstract String getEparchyCode();

    public abstract String getPackageId();

    public abstract String getProductId();

    public abstract String getSerialNumber();

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
        data.put("BASIC_DATE", this.getBasicDate());
        data.put("CAMPN_TYPE", this.getCampnType());
        IDataset infos = CSViewCall.call(this, "CS.SaleActiveQuerySVC.querySaleSvcsByPackageId", data);

        if (infos == null)
        {
            infos = new DatasetList();
        }
        setInfos(infos);
    }

    public abstract void setInfos(IDataset infos);
}
