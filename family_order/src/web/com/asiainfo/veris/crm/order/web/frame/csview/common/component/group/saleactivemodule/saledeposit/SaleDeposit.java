
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.saleactivemodule.saledeposit;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SaleDeposit extends CSBizTempComponent
{
    private void checkDepositGiftUser(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        this.setRenderContent(false);
        String userId = getPage().getParameter("USER_ID", "");
        String giftSerialNumber = getPage().getParameter("GIFT_SERIAL_NUMBER", "");
        String eparchyCode = getPage().getParameter("EPARCHY_CODE", "");
        IData param = new DataMap();

        param.clear();
        param.put("USER_ID", userId);
        param.put("GIFT_SERIAL_NUMBER", giftSerialNumber);
        param.put("PRODUCT_ID", getPage().getParameter("PRODUCT_ID", ""));
        param.put("PACKAGE_ID", getPage().getParameter("PACKAGE_ID", ""));
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("CAMPN_TYPE", getPage().getParameter("CAMPN_TYPE"));
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset result = CSViewCall.call(this, "CS.SaleDepositSVC.checkDepositGiftUser", param);

        getPage().setAjax(result);
    }

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
            setInfos(null);
    }

    public abstract String getBasicDate();

    public abstract String getCampnType();

    public abstract String getEparchyCode();

    public abstract String getPackageId();

    public abstract String getProductId();

    public abstract String getSerialNumber();

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        String specTag = getPage().getParameter("SPEC_TAG", "");

        if (StringUtils.isBlank(specTag))
        {
            renderComponentAction(informalParametersBuilder, writer, cycle);
        }
        else if (specTag.equals("checkDepositGiftUser"))
        {
            checkDepositGiftUser(informalParametersBuilder, writer, cycle);
        }
    }

    private void renderComponentAction(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData param = getPage().getData();
        String packageId = getPage().getParameter("PACKAGE_ID", getPackageId());
        String productId = getPage().getParameter("PRODUCT_ID", getProductId());
        String eparchyCode = getEparchyCode();
        String serialNumber = getSerialNumber();

        if (StringUtils.isBlank(packageId) || StringUtils.isBlank(productId))
            return;

        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("PRODUCT_ID", productId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("CAMPN_TYPE", getCampnType());
        data.put("BASIC_DATE", this.getBasicDate());
        IDataset infos = CSViewCall.call(this, "CS.SaleActiveQuerySVC.querySaleDepositsByPackageId", data);

        if (infos == null)
        {
            infos = new DatasetList();
        }

        int size = infos.size();
        for (int i = 0; i < size; i++)
        {
            String rsrvstr4 = infos.getData(i).getString("RSRV_STR4", "");
            if (rsrvstr4.length() == 0)
            {
                String depositType = infos.getData(i).getString("DEPOSIT_TYPE", "0");
                if (depositType.equalsIgnoreCase("1"))
                {
                    String drsrvstr4 = "单赠送" + infos.getData(i).getInt("FEE", 0) / 100 + "元";
                    infos.getData(i).put("RSRV_STR4", drsrvstr4);
                }
                else
                {
                    String drsrvstr4 = "固定预存" + infos.getData(i).getInt("FEE", 0) / 100 + "元";
                    infos.getData(i).put("RSRV_STR4", drsrvstr4);
                }
            }
            else
            {
                rsrvstr4 = rsrvstr4 + infos.getData(i).getInt("FEE", 0) / 100 + "元";
                infos.getData(i).put("RSRV_STR4", rsrvstr4);
            }

            infos.getData(i).put("DISPLAY_FEE", infos.getData(i).getInt("FEE", 0) / 100);
            infos.getData(i).put("DISPLAY_DEPOSIT_HIGH_VALUE", infos.getData(i).getInt("DEPOSIT_HIGH_VALUE", 0) / 100);
            infos.getData(i).put("DISPLAY_DEPOSIT_LOW_VALUE", infos.getData(i).getInt("DEPOSIT_LOW_VALUE", 0) / 100);
        }

        setInfos(infos);
    }

    public abstract void setInfos(IDataset infos);
}
