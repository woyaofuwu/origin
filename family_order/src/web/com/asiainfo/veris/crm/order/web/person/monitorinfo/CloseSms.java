
package com.asiainfo.veris.crm.order.web.person.monitorinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CloseSms extends PersonBasePage
{
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        IDataset tipInfo = CSViewCall.call(this, "SS.CloseSmsSVC.loadChildInfo", userInfo);
        if (IDataUtil.isNotEmpty(tipInfo))
        {
            setTipInfo("注意：该用户已经" + tipInfo.getData(0).getString("STATE_CLOSED_NAME") + "!");
            setAjax("TIP_CODE", "1");
        }
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.CloseSmsTradeSVC.tradeReg", data);
        this.setAjax(result);
    }

    public abstract void setInfo(IData info);

    public abstract void setTipInfo(String tipInfo);
}
