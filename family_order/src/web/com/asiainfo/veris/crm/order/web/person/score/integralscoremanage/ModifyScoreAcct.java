
package com.asiainfo.veris.crm.order.web.person.score.integralscoremanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ModifyScoreAcct extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData userinfo = new DataMap(data.getString("USER_INFO"));

        IData acctInfo = CSViewCall.callone(this, "SS.ScoreAcctSVC.getAcctInfo", userinfo);

        setAcctInfo(acctInfo);

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

        IDataset dataset = CSViewCall.call(this, "SS.ModifyScoreAcctRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setAcctInfo(IData custInfo);

}
