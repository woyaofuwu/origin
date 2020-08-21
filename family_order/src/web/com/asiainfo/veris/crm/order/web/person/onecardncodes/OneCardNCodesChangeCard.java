
package com.asiainfo.veris.crm.order.web.person.onecardncodes;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OneCardNCodesChangeCard extends PersonBasePage
{

    /**
     * 校验sim
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkNewMSim(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put("SERIAL_NUMBER_A", getData().getString("AUTH_SERIAL_NUMBER"));
        data.put("SERIAL_NUMBER_B", getData().getString("f_serial_number"));
        data.put("SIM_CARD_NO", getData().getString("commInfo_NEWRES_CODE"));

        IDataset output = CSViewCall.call(this, "SS.OneCardNCodesChangeCardSVC.CheckNewSIM", data);

        setResInfo(output.getData(0));
        setAjax(output.getData(0));
    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));

        // 用户资料
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        IDataset output = CSViewCall.call(this, "SS.OneCardNCodesChangeCardSVC.changeCard", userInfo);

        //TODO huanghua 翻译产品名称与品牌名称
        IData oUserInfo = output.getData(0).getData("OUSERINFO");
        userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", userInfo.getString("PRODUCT_ID","")));
        userInfo.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, userInfo.getString("BRAND_CODE","")));
        userInfo.put("STATE_NAME",UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this, "0", "S", userInfo.getString("USER_STATE_CODESET","")));
        oUserInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", oUserInfo.getString("PRODUCT_ID","")));
        oUserInfo.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, oUserInfo.getString("BRAND_CODE","")));
        oUserInfo.put("STATE_NAME",UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this, "0", "S", oUserInfo.getString("USER_STATE_CODESET","")));
        
        setCustInfo(custInfo);
        setUserInfo(userInfo);
        setResInfo(output.getData(0).getData("USERRESINFO"));
        setOuser_info(output.getData(0).getData("OUSERINFO"));
        setFresinfo(output.getData(0).getData("OUSERRESINFO"));
        setOcust_info(output.getData(0).getData("OCUSTINFO"));
        setAjax(output.getData(0));
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

        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        data.put("OTHERSN", getData().getString("f_serial_number"));
        data.put("SIM_CARD_NO_O", getData().getString("commInfo_SIMNO"));
        data.put("SIM_CARD_NO_M", getData().getString("commInfo_NEWRES_CODE"));

        IDataset dataset = CSViewCall.call(this, "SS.OneCardNCodesChangeCardRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCondition(IData cond);

    public abstract void setCustInfo(IData data);

    public abstract void setEditInfo(IData data);

    public abstract void setFresinfo(IData data);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset datas);

    public abstract void setOcust_info(IData data);

    public abstract void setOuser_info(IData data);

    public abstract void setResInfo(IData data);

    public abstract void setUserInfo(IData data);
}
