
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.saleactive.saleactivemodule.saleservice;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SaleService extends CSBizTempComponent
{
    public abstract String getBasicDate();

    public abstract String getCampnType();

    public abstract String getEparchyCode();

    public abstract String getPackageId();

    public abstract String getProductId();

    public abstract String getSerialNumber();

    public abstract void setInfos(IDataset infos);
    
    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
    	super.cleanupAfterRender(cycle);
    	if (!cycle.isRewinding())
    		setInfos(null);
    }
    
    @Override
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        if (cycle.isRewinding())
            return;

        IData data = new DataMap();
        data.put("PACKAGE_ID", getPackageId());
        data.put("PRODUCT_ID", getProductId());
        data.put("EPARCHY_CODE", getEparchyCode());
        data.put("SERIAL_NUMBER", getSerialNumber());
        data.put("BASIC_DATE", getBasicDate());
        data.put("CAMPN_TYPE", getCampnType());
        IDataset infos = CSViewCall.call(this, "CS.SaleActiveQuerySVC.querySaleSvcsByPackageId", data);
        if (IDataUtil.isNotEmpty(infos))
            setInfos(infos);
    }
}
