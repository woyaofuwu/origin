
package com.asiainfo.veris.crm.order.web.person.simcardmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @author fanfb sim重个人化
 */
public abstract class ReuseCard extends PersonBasePage
{

    public abstract IData getUserInfo();

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        // 用户资料
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        setCustInfo(custInfo);
        setUserInfo(userInfo);
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.getUserResource", userInfo);// data ) ;
        // 校验是否是吉祥号码
        IDataset isJxNo = CSViewCall.call(this, "SS.ChangeCardSVC.isJXNumber", userInfo);// data ) ;
        setOldCard(output.getData(0));
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
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

        // data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.ChangeCardSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCustInfo(IData editInfo);

    public abstract void setNewCard(IData a);

    public abstract void setOldCard(IData a);

    public abstract void setUserInfo(IData userInfo);

    /**
     * 校验新sim信息
     */
    public void verifySimCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER", ""));
        IDataset output = CSViewCall.call(this, "SS.SimCardCheckSVC.verifySimCard", data);
        setNewCard(output.getData(0));
        setAjax(output.getData(0));
    }

    /**
     * 检查是否是4G卡
     * 
     * @param cycle
     * @throws Exception
     */
    public void verifySimcardUSIM(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData input = new DataMap();
        input.put("SIM_CARD_NO", data.getString("SIM_CARD_NO"));
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.verifySimcardUSIM", input);
        setAjax(output);
    }
}
