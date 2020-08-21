
package com.asiainfo.veris.crm.order.web.person.modifysaleactiveimei;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ModifySaleActiveIMEI extends PersonBasePage
{
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        IDataset goodsInfos = CSViewCall.call(this, "SS.ModifySaleActiveIMEI.GetUserSaleActiveGoodsInfos", userInfo);

        setGoodsInfos(goodsInfos);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.put("REMARK", getData().getString("REMARK"));
        param.putAll(data);

        IDataset dataset = CSViewCall.call(this, "SS.ModifySaleActiveIMEIRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setGoodsInfos(IDataset goodsInfos);
}
