
package com.asiainfo.veris.crm.order.web.person.score.getgiftofstudyimbursement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class GetGiftOfStudyImbursement extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void getCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.GetGiftOfStudyImbursementSVC.getCommInfo", data);

        setInfos(dataset.getData(0).getDataset("GIFTINFO"));
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.GetGiftOfStudyImbursementRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
