
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.saleactivemodule;

import org.apache.log4j.Logger;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
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

        String msg = "";
        boolean flag = true;
        IData inData = getPage().getData();
        inData.put("EVENT_TYPE", "CHKPKG");
        // p_csm_CheckForGrpSaleActive存储过程校验
        IDataset rtDataset = CSViewCall.call(this, "SS.SaleActiveSVC.CheckSaleElement", inData);
        if (IDataUtil.isNotEmpty(rtDataset))
        {
            IData result = rtDataset.getData(0);
            if (!"0".equals(result.getString("v_resultcode", "0")))
            {
                flag = false;
                msg = result.getString("v_resultinfo", "");
            }
        }

        IData data = new DataMap();
        data.put("FLAG", flag);
        data.put("ERROR_MESSAGE", msg);
        getPage().setAjax(data);
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
            setCampnType(null);
            setHasSaleCredit(false);
            setHasSaleDeposit(false);
            setHasSaleDiscnt(false);
            setHasSaleGoods(false);
            setHasSalePlatsvc(false);
            setHasSaleScore(false);
            setHasSaleService(false);
            setUserId(null);
            setSerialNumber(null);
            setEparchyCodeCompId(null);
            setInfo(null);
            setAcctDay(null);
        }
    }

    public abstract String getCampnType();

    public abstract String getEparchyCodeCompId();

    public abstract String getGoodsId();

    public abstract String getPackageId();

    public abstract String getProductId();

    public abstract String getSerialNumber();

    private void readerComponentAction(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData param = getPage().getData();
        String packageId = getPage().getParameter("PACKAGE_ID", getPackageId());
        String productId = getPage().getParameter("PRODUCT_ID", getProductId());
        String goodsId = getPage().getParameter("GOODS_ID", getGoodsId());
        String userId = getPage().getParameter("USER_ID", "");
        String serialNumber = getPage().getParameter("SERIAL_NUMBER", "");
        String eparchyCode = getPage().getParameter("EPARCHY_CODE", "");

        if (StringUtils.isBlank(packageId))
            return;
        String acctStr = param.getString("ACCTDAY");
        IData UseracctDay = new DataMap(acctStr);
        String acctDay = UseracctDay.getString("ACCT_DAY", "");
        String campnType = getPage().getParameter("CAMPN_TYPE", "");

        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("PRODUCT_ID", productId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("CAMPN_TYPE", campnType);
        IData info = CSViewCall.call(this, "CS.SaleActiveQuerySVC.querySaleActiveInfoByPackageId", data).getData(0);

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

        setPackageId(info.getString("PACKAGE_ID"));
        setProductId(info.getString("PRODUCT_ID"));
        setCampnType(info.getString("CAMPN_TYPE"));
        setGoodsId(goodsId);
        setUserId(userId);
        setAcctDay(acctDay);
        setEparchyCode(eparchyCode);
        setSerialNumber(serialNumber);

        setEparchyCodeCompId(getPage().getParameter("SALEACTIVEMODULE_EPARCHY_CODE_COMPID", getEparchyCodeCompId()));
        addScriptContent(writer, "saleactiveModule.spAutoAddCheckedElems();");

        String needAcct = info.getString("RSRV_STR5");
        String needAcctRsrv6 = info.getString("RSRV_STR6");
        if (StringUtils.isNotBlank(needAcct) && "acct".equals(needAcct))
        {
            info.put("NEED_ACCT", "1");
            IDataset acctInfos = null;
            
            if(StringUtils.isNotBlank(needAcct) && "acct".equals(needAcctRsrv6)){
            	
            	data.clear();
	            data.put("USER_ID", userId);
	            data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            	acctInfos = CSViewCall.call(this, "CS.PayRelaInfoQrySVC.queryAllPayrelationByProductUserId", data);
            	
            } else {
            	
	            // 查询账户信息
	            data.clear();
	            data.put("USER_ID", userId);
	            IData userinfo = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserInfoByUserIdForGrp", data).getData(0);
	            data.clear();
	            data.put("CUST_ID", userinfo.getString("CUST_ID"));
	            acctInfos= CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctInfoByCustIDForGrp", data);
	            
            }
            
            
            if (IDataUtil.isNotEmpty(acctInfos))
            {
                for (int a = 0, acctSize = acctInfos.size(); a < acctSize; a++)
                {
                    IData acctInfo = acctInfos.getData(a);
                    String showAcct = acctInfo.getString("ACCT_ID") + "|" + acctInfo.getString("PAY_NAME") + "|" + acctInfo.getString("PAY_MODE_NAME") + "|" + acctInfo.getString("EPARCHY_NAME");
                    acctInfo.put("SHOW_ACCT", showAcct);
                }
            }
            setAcctInfos(acctInfos);
        }
        setInfo(info);
    }

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        addResourceFile(writer, "scripts/csserv/component/group/saleactivemodule/saleactivemodule.js");
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

    public abstract void setAcctDay(String acctDay);

    public abstract void setAcctInfos(IDataset acctInfos);

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

    public abstract void setPackageId(String packageId);

    public abstract void setProductId(String productId);

    public abstract void setSerialNumber(String serialNumber);

    public abstract void setUserId(String userId);

    private void updVeriCideOk(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        this.setRenderContent(false);

        IData cond = getPage().getData();

        cond.put(Route.ROUTE_EPARCHY_CODE, cond.getString("EPARCHY_CODE", ""));

        IDataset resultDataSet = CSViewCall.call(this, "CS.SaleActiveSmsSVC.updVeriCodeOk", cond);

        getPage().setAjax(resultDataSet.getData(0));
    }

}
