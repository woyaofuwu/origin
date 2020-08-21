
package com.asiainfo.veris.crm.order.web.person.getsaleactivegift;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GetSaleActiveGiftException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class GetSaleActiveGift extends PersonBasePage
{
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        IDataset giftInfos = CSViewCall.call(this, "SS.GetSaleActiveGiftSVC.GetGiftInfos", userInfo);
        if (giftInfos == null || giftInfos.size() < 1)
        {
            CSViewException.apperr(GetSaleActiveGiftException.CRM_GETSALEACTIVEGIFT_1);
        }
        for (int i = 0, size = giftInfos.size(); i < size; i++)
            giftInfos.getData(i).put("PARA_CODE2", giftInfos.getData(i).getString("PARA_CODE2") + "," + giftInfos.getData(i).getString("RELATION_TRADE_ID"));
        setGiftInfos(giftInfos);
        setAjax(userInfo);
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
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.put("REMARK", getData().getString("REMARK"));
        param.putAll(data);

        IDataset dataset = CSViewCall.call(this, "SS.GetSaleActiveGiftRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setGiftInfos(IDataset giftInfos);
}
