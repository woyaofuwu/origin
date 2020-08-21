
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.changediscnt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WidenetChangeDiscnt extends PersonBasePage
{
    /**
     * @param clcle
     * @throws Exception
     * @author chenzm
     */
    public void checkDiscnt(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WidenetChangeDiscntSVC.checkDiscnt", data);
        setAjax(dataset);
    }

    /**
     * @param clcle
     * @throws Exception
     * @author chenzm
     */
    public void getSpecDiscnt(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WidenetChangeDiscntSVC.getSpecDiscnt", data);
        setAjax(dataset);
    }

    /**
     * @param clcle
     * @throws Exception
     * @author chenzm
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WidenetChangeDiscntSVC.loadChildInfo", data);

        IDataset userDiscntList = dataset.getData(0).getDataset("USER_DISCNT_LIST");
        IDataset canDiscntList = dataset.getData(0).getDataset("CAN_DISCNT_LIST");
        IDataset deferMonthsList = dataset.getData(0).getDataset("DEFER_MONTHS_LIST");
        IDataset discntParam = dataset.getData(0).getDataset("DISCNT_PARAM_LIST");
        // TODO 资费权限过滤
        setUserDiscntList(userDiscntList);
        setCanDiscntList(canDiscntList);
        setDeferMonthsList(deferMonthsList);
        setAjax(discntParam);
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
        param.put("CAMPUS_TAG", data.getString("TAG"));
        setInfo(param);
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
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.WidenetChangeDiscntRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    public abstract void setCanDiscntList(IDataset datas);

    public abstract void setDeferMonthsList(IDataset datas);

    public abstract void setDiscntParam(IData discntParam);

    public abstract void setInfo(IData info);

    public abstract void setUserDiscntList(IDataset datas);

}
