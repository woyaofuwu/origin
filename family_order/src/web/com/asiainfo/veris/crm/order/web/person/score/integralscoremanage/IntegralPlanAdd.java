
package com.asiainfo.veris.crm.order.web.person.score.integralscoremanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class IntegralPlanAdd extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData userinfo = new DataMap(data.getString("USER_INFO"));

        // 校验用户积分账户信息
        CSViewCall.callone(this, "SS.ScoreAcctSVC.getAcctInfo", userinfo);

        IData planInfo = CSViewCall.callone(this, "SS.ScoreAcctSVC.getIntegralPlan", userinfo);

        setAcctInfo(planInfo);

        // 获取子业务资料

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

        IDataset dataset = CSViewCall.call(this, "SS.IntegralPlanAddRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setAcctInfo(IData custInfo);

}
