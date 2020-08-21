
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.widenetmove;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WidenetMove extends PersonBasePage
{

    public void checkSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WidenetMoveSVC.checkSerialNumber", data);
        IDataset acctInfos = dataset.getData(0).getDataset("ALL_ACCT");
        setAllAcct(acctInfos);
        setAjax(dataset);
        setNewWideInfo(dataset.getData(0));
    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        if (IDataUtil.isEmpty(dataset))
        {
            CSViewException.apperr(WidenetException.CRM_WIDENET_4);
        }
        IData wideInfo = dataset.getData(0);
        wideInfo.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        wideInfo.put("WIDE_TYPE", data.getString("WIDE_TYPE", ""));
        setWideInfo(wideInfo);
    }

    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        if ("ADSL".equals(data.getString("WIDE_TYPE")) || "TTADSL".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "622");
        }
        else if ("XIAN".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "623");
        }
        else if ("GPON".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "606");
        }
        param.put("WIDE_TYPE", data.getString("WIDE_TYPE"));
        setWideInfo(param);
    }

    /**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {

            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IDataset dataset = CSViewCall.call(this, "SS.WidenetMoveRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setAllAcct(IDataset datas);

    public abstract void setNewWideInfo(IData userWideInfo);

    public abstract void setWideInfo(IData wideInfo);

}
