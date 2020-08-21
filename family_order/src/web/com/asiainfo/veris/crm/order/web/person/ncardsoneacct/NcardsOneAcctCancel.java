/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.ncardsoneacct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-5-14 修改历史 Revision 2014-5-14 下午08:00:35
 */
public abstract class NcardsOneAcctCancel extends PersonBasePage
{
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        IDataset results = CSViewCall.call(this, "SS.NcardsOneAcctSVC.loadCancelChildInfo", userInfo);

        this.setCustInfoView2(results.getData(0).getData("SECOND_CUST_INFO"));
        IData userInfo2 = results.getData(0).getData("SECOND_USER_INFO");
        
        //TODO huanghua 翻译产品名称与品牌名称以及用户状态
        userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", userInfo.getString("PRODUCT_ID","")));
        userInfo.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, userInfo.getString("BRAND_CODE","")));
        userInfo.put("STATE_NAME",UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this, "0", "S", userInfo.getString("USER_STATE_CODESET", "")));
        userInfo2.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", userInfo2.getString("PRODUCT_ID","")));
        userInfo2.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, userInfo2.getString("BRAND_CODE","")));
        userInfo2.put("STATE_NAME",UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this, "0", "S", userInfo.getString("USER_STATE_CODESET", "")));
        this.setUserInfoView2(userInfo2);
        this.setCustInfoView(custInfo);
        this.setUserInfoView(userInfo);
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

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

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        this.setAjax(CSViewCall.call(this, "SS.NcardsOneAcctCancelRegSVC.tradeReg", data));
    }

    public abstract void setCustInfoView2(IData cond);

    public abstract void setUserInfoView2(IData cond);

}
