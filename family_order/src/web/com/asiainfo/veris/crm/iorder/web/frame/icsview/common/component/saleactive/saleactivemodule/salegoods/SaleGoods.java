
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.saleactive.saleactivemodule.salegoods;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SaleGoods extends CSBizTempComponent
{
    public abstract String getCampnType();

    public abstract String getEparchyCode();

    public abstract String getGoodsId();

    public abstract String getNewImei();

    public abstract String getPackageId();

    public abstract String getProductId();

    public abstract String getSerialNumber();

    public abstract String getTerminalDetailInfo();
    
    public abstract String getNetOrderId(); // REQ201604180021 chenxy3
    
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setNewImei(String imei);

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding()) {
            setInfos(null);
            setNewImei(null);
        }
    }
    
    @Override
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        String specTag = getPage().getParameter("SPEC_TAG", "");
        if (StringUtils.isBlank(specTag)) {
            renderComponentAction();
        } else if ("checkResInfo".equals(specTag)) {
            checkResInfo();
        }
    }

    private void renderComponentAction() throws Exception
    {
        String packageId = getPage().getParameter("PACKAGE_ID", getPackageId());
        String productId = getPage().getParameter("PRODUCT_ID", getProductId());
        String goodsId = getPage().getParameter("GOODS_ID", getGoodsId());
        String newImei = getPage().getParameter("NEW_IMEI", getNewImei());
        String eparchyCode = getEparchyCode();
        String serialNumber = getSerialNumber();

        if (StringUtils.isBlank(packageId))
            return;

        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("PACKAGE_ID", packageId);
        data.put("GOODS_ID", goodsId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("CAMPN_TYPE", getCampnType());
        data.put("NEW_IMEI", newImei);
        data.put("NET_ORDER_ID", getPage().getParameter("NET_ORDER_ID", "")); // REQ201604180021   chenxy3
        System.out.println(getPage().getParameter("SMS_CODE", "")+"=========wangcode");
        data.put("SMS_CODE", getPage().getParameter("SMS_CODE", ""));//101774--wangsc10--20190311
        IDataset infos = CSViewCall.call(this, "CS.SaleActiveQuerySVC.querySaleGoodsByPackageId", data);

        if (IDataUtil.isNotEmpty(infos)) {
            dataProcess(infos);
        }
        if (StringUtils.isNotBlank(newImei)) {
            setNewImei(newImei);
        }

        setInfos(infos);
        setInfo(infos.getData(0));
    }
    
    private void checkResInfo() throws Exception
    {
        this.setRenderContent(false);

        String productId = getPage().getParameter("PRODUCT_ID", "");
        String resTypeCode = getPage().getParameter("RES_TYPE_CODE", "");
        String resId = getPage().getParameter("RES_ID", "");
        String resNo = getPage().getParameter("RES_NO", "");
        String othernetTag = getPage().getParameter("OTHERNET_TAG", "");
        String otherNumber = getPage().getParameter("OTHER_NUMBER", "");
        String eparchyCode = getPage().getParameter("EPARCHY_CODE", "");
        String packageId = getPage().getParameter("PACKAGE_ID", "");
        String resCheck = getPage().getParameter("RES_CHECK", "0");
        String saleStaffId = getPage().getParameter("SALE_STAFF_ID");

        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("PRODUCT_ID", productId);
        data.put("RES_TYPE_CODE", resTypeCode);
        data.put("RES_ID", resId);
        data.put("RES_NO", resNo);
        data.put("OTHERNET_TAG", othernetTag);
        data.put("OTHER_NUMBER", otherNumber);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("CAMPN_TYPE", getCampnType());
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        data.put("RES_CHECK", resCheck);
        data.put("SALE_STAFF_ID", saleStaffId);

        IDataset infos = CSViewCall.call(this, "CS.SaleGoodsSVC.checkResInfo", data);

        getPage().setAjax(infos);
    }
    
    private void dataProcess(IDataset datas) throws Exception
    {
        for (int i = 0; i < datas.size(); i++) {
            String goods_value = datas.getData(i).getString("GOODS_VALUE");
            String goods_value_display = FeeUtils.Fen2Yuan(goods_value);
            datas.getData(i).put("GOODS_VALUE_DISPLAY", goods_value_display);
        }
    }
}
