
package com.asiainfo.veris.crm.order.web.person.changephone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangePhoneCancel extends PersonBasePage
{

    /**
     * 取消业务查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData custInfo = new DataMap(data.getString("CUST_INFO", "{}"));
        IData userInfo = new DataMap(data.getString("USER_INFO", "{}"));
        setUserInfo(userInfo);
        setCustInfo(custInfo);
        IDataset changePhoneInfo = CSViewCall.call(this, "SS.ChangePhoneCancelSVC.queryChangePhoneInfo", userInfo);
        if ("-1".equals(changePhoneInfo.getData(0).getString("STATUS", "-1")))
        {
            CSViewException.apperr(ChangePhoneException.CRM_CHANGEPHONE_21, userInfo.get("SERIAL_NUMBER"));
        }
        setChangePhoneInfo(changePhoneInfo.getData(0));
        setAjax(changePhoneInfo);
    }

    /**
     * 取消业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData input = new DataMap();
        // 填充接口数据
        input.put("OLD_SN", data.get("RELA_SERIAL_NUMBER"));
        input.put("NEW_SN", data.get("SERIAL_NUMBER"));
        input.put("CHANNEL", "1");
        input.put("OPER_CODE", "2");
        input.put("ACTIVED_TIME", SysDateMgr.getSysDate());
        input.put("HAND_CHARGE", "");
        input.put("RESERVE", "");
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        // 其他附加参数
        input.put("TRADE_CITY_CODE", getVisit().getCityCode());
        input.put("TRADE_STAFF_ID", getVisit().getStaffId());
        input.put("TRADE_DEPART_ID", getVisit().getDepartId());
        input.put("IN_MODE_CODE", "1");
        IDataset dataset = CSViewCall.call(this, "SS.ChangePhoneCancelSVC.changePhoneCancel", input);
        setAjax(dataset);
    }

    public abstract void setChangePhoneInfo(IData changePhoneInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);

}
