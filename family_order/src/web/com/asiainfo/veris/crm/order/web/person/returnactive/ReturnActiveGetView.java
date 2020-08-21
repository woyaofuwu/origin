
package com.asiainfo.veris.crm.order.web.person.returnactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReturnActiveGetView extends PersonBasePage
{

    public void checkCardCodeRes(IRequestCycle cycle) throws Exception
    {

    }

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("RSRV_VALUE_CODE", "RETURNACTIVE");
        IDataset userOtherList = CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherUserId", data);
        IData info = new DataMap();
        if (IDataUtil.isEmpty(userOtherList))
        {
            info.put("HAVE_NUM", "0");
        }
        else
        {
            info.put("HAVE_NUM", userOtherList.getData(0).getString("RSRV_STR1", "0"));
        }

        setAjax(info);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.ReturnActiveGetRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setInfo(IData info);
}
