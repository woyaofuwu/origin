
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetchangemodifyacct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @Description: 铁通宽带产品变更
 * @version: v1.0.0
 */
public abstract class CttBroadBandModifyAcct extends PersonBasePage
{

    public void checkWidenetAcctId(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        String eparchyCode = getVisit().getStaffEparchyCode();
        if (StringUtils.isBlank(eparchyCode))
        {
            eparchyCode = "0898";
        }
        pagedata.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset dsResult = CSViewCall.call(this, "SS.CttBroadBandModifyAcctSVC.checkWidenetAcctId", pagedata);

    }

    /**
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));
        IData custInfo = new DataMap(pagedata.getString("CUST_INFO"));

        String userId = userInfo.getString("USER_ID", "");
        
        String productId = userInfo.getString("PRODUCT_ID");
        String brandCode = userInfo.getString("BRAND_CODE");
        
        if (StringUtils.isNotBlank(productId))
        {
            userInfo.put("PRODUCT_NAME", userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, productId)));
        }
        
        if (StringUtils.isNotBlank(brandCode))
        {
            userInfo.put("BRAND_NAME", UpcViewCall.getBrandNameByBrandCode(this, brandCode));
        }
        setUserInfo(userInfo);
        setCustInfo(custInfo);

        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("SERIAL_NUMBER", pagedata.getString("AUTH_SERIAL_NUMBER"));
        String eparchyCode = getVisit().getStaffEparchyCode();
        if (StringUtils.isBlank(eparchyCode))
        {
            eparchyCode = "0898";
        }
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset dsResult = CSViewCall.call(this, "SS.CttBroadBandModifyAcctSVC.getOldBroadBandInfo", data);

        IData commonInfo = dsResult.size() > 0 ? (IData) dsResult.get(0) : null;
        commonInfo.put("USER_ID", userId);
        setInfo(commonInfo);

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
        String eparchyCode = getVisit().getStaffEparchyCode();
        if (StringUtils.isBlank(eparchyCode))
        {
            eparchyCode = "0898";
        }
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset result = CSViewCall.call(this, "SS.CttBroadBandModifyAcctRegSVC.tradeReg", data);
        this.setAjax(result);
    }

    // 三户信息
    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setUserInfo(IData userInfo);

}
