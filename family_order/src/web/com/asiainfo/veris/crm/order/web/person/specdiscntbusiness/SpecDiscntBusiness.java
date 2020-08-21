
package com.asiainfo.veris.crm.order.web.person.specdiscntbusiness;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: SpecDiscntBusiness.java
 * @Description: 特殊优惠办理及取消View
 * @version: v1.0.0
 * @author: maoke
 * @date: May 28, 2014 10:57:20 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 28, 2014 maoke v1.0.0 修改原因
 */
public abstract class SpecDiscntBusiness extends PersonBasePage
{
    /**
     * @Description: 获取用户优惠列表
     * @param cycle
     * @throws Exception
     * @author: maoke
     * @date: May 26, 2014 3:26:28 PM
     */
    public void getUserSpecDiscntList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));

        IData discntData = CSViewCall.call(this, "SS.SpecDiscntBusinessSVC.getUserSpecDiscntList", data).getData(0);

        setChooseDiscnt(discntData.getDataset("CHOOSE_DISCNT"));

        setDiscnt(discntData.getDataset("USER_DISCNT"));

        setAjax(discntData);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();

        IDataset result = CSViewCall.call(this, "SS.SpecDiscntBusinessRegSVC.tradeReg", data);

        this.setAjax(result);
    }

    public abstract void setChooseDiscnt(IDataset chooseType);

    public abstract void setDiscnt(IDataset discntDataset);
}
