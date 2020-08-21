
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetsendbacktime;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @Description: 铁通宽带产品变更
 * @version: v1.0.0
 */
public abstract class CttBroadBandSendBackTime extends PersonBasePage
{

    /**
     * 查询优惠补退历史
     * 
     * @param cycle
     * @throws Exception
     */
    public void getSendBackHistory(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String eparchyCode = getVisit().getStaffEparchyCode();
        if (StringUtils.isBlank(eparchyCode))
        {
            eparchyCode = "0898";
        }
        IDataset sendBackInfo = CSViewCall.call(this, "SS.CttBroadBandSendBackTimeSVC.getSendBackHistory", data);
        this.setSendBackInfo(sendBackInfo);
    }

    /**
     * 初始化业务
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));
        IData custInfo = new DataMap(pagedata.getString("CUST_INFO"));
        pagedata.put("USER_ID", userInfo.getString("USER_ID"));

        IDataset userInfos = CSViewCall.call(this, "SS.CttBroadBandSendBackTimeSVC.qryBroadBandUser", pagedata);
        if (IDataUtil.isEmpty(userInfos))
        {
            CSViewException.apperr(BroadBandException.CRM_BROADBAND_4);
        }
        else
        {
            String productId = userInfo.getString("PRODUCT_ID");
            String brandCode = userInfo.getString("BRAND_CODE");
            
            if (StringUtils.isNotBlank(productId))
            {
                userInfo.put("PRODUCT_NAME", userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", productId)));
            }
            
            if (StringUtils.isNotBlank(brandCode))
            {
                userInfo.put("BRAND_NAME", UpcViewCall.getBrandNameByBrandCode(this, brandCode));
            }
            
            this.setUserInfo(userInfo);
            this.setCustInfo(custInfo);
            setDiscntInfos(userInfos.getData(0).getDataset("DISCNT_INFO"));

            setSendBackTimeInfo(new DataMap(userInfos.getData(0).getString("SEND_BACK_DATA")));
            setInfo(new DataMap(userInfos.getData(0).getString("addrInfo")));
        }
    }

    /**
     * 业务受理
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        String sendbackdays = data.getString("SEND_BACK_DAYS");
        String eparchyCode = getVisit().getStaffEparchyCode();
        if (StringUtils.isBlank(eparchyCode))
        {
            eparchyCode = "0898";
        }
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset dataset = CSViewCall.call(this, "SS.CttBroadBandSendBackTimeSVC.tradeReg", data);
        setAjax(dataset);
    }

    // 三户信息
    public abstract void setCustInfo(IData custInfo);

    // public abstract void setInfos(IDataset infos);
    // public abstract void setDiscntInfo(IData discntInfo);
    public abstract void setDiscntInfos(IDataset discntInfos);

    public abstract void setInfo(IData info);

    public abstract void setSendBackInfo(IDataset sendBackInfo);

    public abstract void setSendBackTimeInfo(IData sendBackTimeInfo);

    public abstract void setUserInfo(IData userInfo);
}
