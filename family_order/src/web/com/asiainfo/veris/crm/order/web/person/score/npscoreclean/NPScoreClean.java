
package com.asiainfo.veris.crm.order.web.person.score.npscoreclean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class NPScoreClean extends PersonBasePage
{
    private void getCommInfo(IData allInfo) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", allInfo.getData("USER_INFO").getString("USER_ID"));
        inparam.put("SERIAL_NUMBER", allInfo.getData("USER_INFO").getString("SERIAL_NUMBER", ""));
        IDataset data = CSViewCall.call(this, "SS.NPScoreCleanSVC.getCommInfo", inparam);

        setCommInfo(data.getData(0).getData("COMMINFO"));

        log.debug("------------------ScoreDonate.getCommInfo->data = " + data);
    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData dataset = new DataMap();

        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        setUserInfo(userInfo);

        dataset.put("USER_INFO", userInfo);

        // 获取子业务资料
        getCommInfo(dataset);
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
        param.put("REMARK", data.getString("comminfo_REMARK"));
        param.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));

        IDataset dataset = CSViewCall.call(this, "SS.NPScoreCleanRegSVC.TradeReg", param);
        setAjax(dataset);
    }


    public abstract void setCommInfo(IData commInfo);
    
    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);
}
