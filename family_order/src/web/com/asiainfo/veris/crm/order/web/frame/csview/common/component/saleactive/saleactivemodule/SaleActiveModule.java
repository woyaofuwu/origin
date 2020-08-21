
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.saleactive.saleactivemodule;

import org.apache.log4j.Logger;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SaleActiveModule extends CSBizTempComponent
{
    private static final Logger logger = Logger.getLogger(SaleActiveModule.class);

    private void checkBindSerialNumber(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        this.setRenderContent(false);

        IData cond = getPage().getData();

        cond.put(Route.ROUTE_EPARCHY_CODE, cond.getString("EPARCHY_CODE", ""));

        IDataset resultDataSet = CSViewCall.call(this, "CS.SaleActiveCheckSVC.checkByPackage", cond);

        getPage().setAjax(resultDataSet.getData(0));

    }

    private void checkByPackage(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        this.setRenderContent(false);

        IData cond = getPage().getData();

        cond.put(Route.ROUTE_EPARCHY_CODE, cond.getString("EPARCHY_CODE", ""));

        IDataset ruleResultDataSet = CSViewCall.call(this, "CS.SaleActiveCheckSVC.checkByPackage", cond);
        IData ruleResultData=ruleResultDataSet.getData(0);
        
        /**REQ201510260003 光猫申领提示优化【2015下岛优化】
         * chenxy3 20151027 
         * */
        IData commData  = CSViewCall.call(this, "SS.FTTHModermApplySVC.checkProdInCommpara", cond).first();
        String inTag=commData.getString("IN_TAG","");
        if("1".equals(inTag)){ 
	        	IData resultData  = CSViewCall.call(this, "SS.FTTHModermApplySVC.checkIfFtthNewUser", cond).first();
	        	ruleResultData.putAll(resultData);//IS_FTTH_USER 
        }
        getPage().setAjax(ruleResultData);
    }

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setPackageId(null);
            setProductId(null);
            setEparchyCode(null);
            setGoodsId(null);
            setNewImei(null);
            setCampnType(null);
            setHasSaleCredit(false);
            setHasSaleDeposit(false);
            setHasSaleDiscnt(false);
            setHasSaleGoods(false);
            setHasSalePlatsvc(false);
            setHasSaleScore(false);
            setHasSaleService(false);
            setAssureList(null);
            setSerialNumber(null);
            setEparchyCodeCompId(null);
            setInfo(null);
        }
    }

    public abstract String getCampnType();

    public abstract String getEparchyCodeCompId();

    public abstract String getGoodsId();

    public abstract String getNewImei();

    public abstract String getPackageId();

    public abstract String getProductId();

    private void readerComponentAction(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        String packageId = getPage().getParameter("PACKAGE_ID", getPackageId());
        String productId = getPage().getParameter("PRODUCT_ID", getProductId());
        String goodsId = getPage().getParameter("GOODS_ID", getGoodsId());
        String newImei = getPage().getParameter("NEW_IMEI", getNewImei());
        String serialNumber = getPage().getParameter("SERIAL_NUMBER", "");
        String eparchyCode = getPage().getParameter("EPARCHY_CODE", "");
        String campnType = getPage().getParameter("CAMPN_TYPE", "");
		/**
                 * REQ201604180021 网上营业厅合约终端销售价格调整需求
                 * chenxy3 20160428 
                 */
        String orderId = getPage().getParameter("NET_ORDER_ID", "");
        if (StringUtils.isBlank(packageId))
            return;

        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("PRODUCT_ID", productId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("CAMPN_TYPE", campnType);
        IData info = CSViewCall.call(this, "CS.SaleActiveQuerySVC.querySaleActiveInfoByPackageId", data).getData(0);
        setInfo(info);

        data.clear();
        data.put("PACKAGE_ID", packageId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset elementTypes = CSViewCall.call(this, "CS.PkgElemInfoQrySVC.queryElementTypeByPkgId", data);

        IData temp = new DataMap();
        int size = elementTypes.size();
        for (int i = 0; i < size; i++)
        {
            IData elem = elementTypes.getData(i);
            String elemType = elem.getString("ELEMENT_TYPE_CODE", "");
            temp.put(elemType, elemType);
        }
        setHasSaleCombine(temp.containsKey("K")); // K: 组合包
        setHasSaleService(temp.containsKey("S")); // S: 服务
        setHasSalePlatsvc(temp.containsKey("Z")); // Z: 平台服务
        setHasSaleDiscnt(temp.containsKey("D")); // D: 优惠
        setHasSaleDeposit(temp.containsKey("A")); // A: 预存
        setHasSaleGoods(temp.containsKey("G")); // G: 实物
        setHasSaleScore(temp.containsKey("J")); // J: 积分
        setHasSaleCredit(temp.containsKey("C")); // C: 信用度
        
        
        setNetOrderId(orderId);
        
        setPackageId(info.getString("PACKAGE_ID"));
        setProductId(info.getString("PRODUCT_ID"));
        setCampnType(info.getString("CAMPN_TYPE"));
        setNewImei(newImei);
        setGoodsId(goodsId);
        setSerialNumber(serialNumber);
        setEparchyCode(eparchyCode);
        setBasicElemDate(info.getString("BOOK_DATE"));
        setEparchyCodeCompId(getPage().getParameter("SALEACTIVEMODULE_EPARCHY_CODE_COMPID", getEparchyCodeCompId()));
        addScriptContent(writer, "saleactiveModule.spAutoAddCheckedElems();");
    }

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        addResourceFile(writer, "scripts/csserv/component/saleactive/saleactivemodule/saleactivemodule.js");
        String specTag = getPage().getParameter("SPEC_TAG", "");
        if (StringUtils.isBlank(specTag))
        {
            readerComponentAction(informalParametersBuilder, writer, cycle);
        }
        else if (specTag.equals("checkByPackage"))
        {
            checkByPackage(informalParametersBuilder, writer, cycle);
        }
        else if (specTag.equals("SEND_VERI_CODE_SMS"))
        {
            sendVeriCodeSms(informalParametersBuilder, writer, cycle);
        }
        else if (specTag.equals("UPD_VERI_CODE_OK"))
        {
            updVeriCideOk(informalParametersBuilder, writer, cycle);
        }
        else if (specTag.equals("CHECK_BIND_SN"))
        {
            checkBindSerialNumber(informalParametersBuilder, writer, cycle);
        }

    }

    private void sendVeriCodeSms(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        this.setRenderContent(false);

        IData cond = getPage().getData();

        cond.put(Route.ROUTE_EPARCHY_CODE, cond.getString("EPARCHY_CODE", ""));

        IDataset resultDataSet = CSViewCall.call(this, "CS.SaleActiveSmsSVC.sendVeriCodeSms", cond);

        getPage().setAjax(resultDataSet.getData(0));
    }

    public abstract void setAssureList(IDataset assureList);

    public abstract void setBasicElemDate(String date);

    public abstract void setCampnType(String campnType);

    public abstract void setEparchyCode(String eparchyCode);

    public abstract void setEparchyCodeCompId(String eparchyCodeCompId);

    public abstract void setGoodsId(String goodsId);

    public abstract void setHasSaleCombine(boolean hasSaleCombine);

    public abstract void setHasSaleCredit(boolean hasCredit);

    public abstract void setHasSaleDeposit(boolean hasDeposit);

    public abstract void setHasSaleDiscnt(boolean hasDiscnt);

    public abstract void setHasSaleGoods(boolean hasGoods);

    public abstract void setHasSalePlatsvc(boolean hasPlatsvc);

    public abstract void setHasSaleScore(boolean hasScore);

    public abstract void setHasSaleService(boolean hasService);

    public abstract void setInfo(IData info);

    public abstract void setNewImei(String newImei);

    public abstract void setPackageId(String packageId);

    public abstract void setProductId(String productId);

    public abstract void setSerialNumber(String serialNumber);
    /**
                 * REQ201604180021 网上营业厅合约终端销售价格调整需求
                 * chenxy3 20160428 
                 */
    public abstract void setNetOrderId(String netOrderId);

    private void updVeriCideOk(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        this.setRenderContent(false);

        IData cond = getPage().getData();

        cond.put(Route.ROUTE_EPARCHY_CODE, cond.getString("EPARCHY_CODE", ""));

        IDataset resultDataSet = CSViewCall.call(this, "CS.SaleActiveSmsSVC.updVeriCodeOk", cond);

        getPage().setAjax(resultDataSet);
    }

}
