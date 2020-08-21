
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.saleactivemodule.salediscnt;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SaleDiscnt extends CSBizTempComponent
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

    public abstract String getUserId();

    /** 查询优惠参数 */
    private void queryAttrItems(IDataset infos) throws Exception
    {
        String eparchyCode = getTradeEparchyCode();
        for (int i = 0; i < infos.size(); i++)
        {
            IData info = infos.getData(i);
            String discnt_code = info.getString("DISCNT_CODE");
            IData param = new DataMap();
            param.put("ID_TYPE", "D");
            param.put("ID", discnt_code);
            param.put("EPARCHY_CODE", eparchyCode);
            param.put("ATTR_OBJ", "0");
            IDataset itemas = CSViewCall.call(this, "CS.AttrItemInfoQrySVC.getAttrItemAByIDTO", param);

            info.put("HAS_ATTR", itemas.size() > 0 ? "true" : "false");
            info.put("ATTR_ITEMAS", itemas);
        }
    }

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        if (cycle.isRewinding())
            return;
        IData param = getPage().getData();
        String packageId = getPackageId();
        String eparchyCode = getEparchyCode();
        String acctStr = param.getString("ACCTDAY");
        IData UseracctDay = new DataMap(acctStr);
        param.putAll(UseracctDay);
        param.put("PACKAGE_ID", packageId);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset infos = CSViewCall.call(this, "SS.SaleActiveSVC.queryDiscntsByPkgIdEparchy", param);

        // 查询优惠参数
        queryAttrItems(infos);
        setInfos(infos);
    }

    public abstract void setInfos(IDataset infos);
}
