
package com.asiainfo.veris.crm.order.web.person.score.scoredonate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ScoreDonate extends PersonBasePage
{

    private void getCommInfo(IData allInfo) throws Exception
    {
        String brandCode = allInfo.getData("USER_INFO").getString("BRAND_CODE", "");
        IData inparam = new DataMap();
        inparam.put("USER_ID", allInfo.getData("USER_INFO").getString("USER_ID"));
        inparam.put("OPEN_MODE", allInfo.getData("USER_INFO").getString("OPEN_MODE"));
        inparam.put("USER_STATE_CODESET", allInfo.getData("USER_INFO").getString("USER_STATE_CODESET"));
        inparam.put("SERIAL_NUMBER", allInfo.getData("USER_INFO").getString("SERIAL_NUMBER", ""));
        inparam.put("BRAND_CODE", brandCode);

        IData param2 = new DataMap();
        param2.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param2.put("BRAND_CODE", brandCode);
        IData result2 = CSViewCall.callone(this, "CS.BrandInfoQrySVC.queryBrandByBrandCode", param2);

        IDataset data = CSViewCall.call(this, "SS.ScoreDonateSVC.getCommInfo", inparam);
        if (IDataUtil.isNotEmpty(data)) {
            data.getData(0).getData("COMMINFO").put("BRAND_CODE", brandCode);
            data.getData(0).getData("COMMINFO").put("BRAND_NAME", result2.getString("BRAND"));
            setCommInfo(data.getData(0).getData("COMMINFO"));
        }
    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData dataset = new DataMap();

        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        setUserInfo(userInfo);

        dataset.put("USER_INFO", userInfo);

        // 获取子业务资料
        getCommInfo(dataset);
    }

    /**
     * 业务提交
     *
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("REMARK", data.getString("comminfo_REMARK"));
        param.put("DONATE_SCORE", data.getString("comminfo_DONATE_SCORE"));
        param.put("TRANSFER_POINT", data.getString("comminfo_DONATE_SCORE"));
        param.put("NEWSCORE", data.getString("NEWSCORE"));
        param.put("SCORE", data.getString("INIT_USER_SCORE"));
        param.put("OBJECT_SERIAL_NUMBER", data.getString("objinfo_OBJ_SERIAL_NUMBER"));
        param.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        param.put("OBJ_USER_ID", data.getString("objinfo_OBJ_USER_ID"));
        param.put("CHECK_MODE", data.getString("CHECK_MODE"));

        IDataset dataset = CSViewCall.call(this, "SS.ScoreDonateRegSVC.TradeReg", param);
        setAjax(dataset);
    }

    public void queryObjCustInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String serialNumber = data.getString("objinfo_OBJ_SERIAL_NUMBER");
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        inparam.put("OPEN_MODE", data.getString("OPEN_MODE"));
        inparam.put("USER_STATE_CODESET", data.getString("USER_STATE_CODESET"));

        IData userInfo = new DataMap();
        userInfo.put("SERIAL_NUMBER", serialNumber);
        IDataset userInfoDataset = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySn", userInfo);

        if (IDataUtil.isEmpty(userInfoDataset))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_1);
        }

        IData userInfoData = userInfoDataset.getData(0);

        String userId = userInfoData.getString("USER_ID");
        String brandCode = userInfoData.getString("BRAND_CODE");

        inparam.put("USER_ID", userId);

        String custId = userInfoDataset.getData(0).getString("CUST_ID");

        IData custInfo = new DataMap();
        custInfo.put("CUST_ID", custId);
        custInfo.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset custInfoDataset = CSViewCall.call(this, "CS.CustomerInfoQrySVC.getCustInfoByCId", custInfo);

        if (IDataUtil.isEmpty(custInfoDataset))
        {
            CSViewException.apperr(CustException.CRM_CUST_111);
        }

        IData param2 = new DataMap();
        param2.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param2.put("BRAND_CODE", brandCode);
        IData result2 = CSViewCall.callone(this, "CS.BrandInfoQrySVC.queryBrandByBrandCode", param2);

        IDataset objCommInfo = CSViewCall.call(this, "SS.ScoreDonateSVC.getCommInfo", inparam);
        if (IDataUtil.isNotEmpty(objCommInfo)) {
            objCommInfo.getData(0).getData("COMMINFO").put("BRAND_CODE", brandCode);
            objCommInfo.getData(0).getData("COMMINFO").put("BRAND_NAME", result2.getString("BRAND"));
            setObjCommInfo(objCommInfo.getData(0).getData("COMMINFO"));
        }

        setObjUserInfo(userInfoDataset.getData(0));
        setObjCustInfo(custInfoDataset.getData(0));
    }

    public abstract void setCommInfo(IData commInfo);

    public abstract void setObjCommInfo(IData objCommInfo);

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setObjCustInfo(IData objCustInfo);

    public abstract void setObjUserInfo(IData objUserInfo);

    public abstract void setUserInfo(IData userInfo);
}
