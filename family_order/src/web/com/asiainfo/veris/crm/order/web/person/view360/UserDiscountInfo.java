
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserDiscountInfo extends PersonBasePage
{

    /**
     * 客户资料综合查询 - 优惠信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        // 获取页面信息
        IData params = getData();
        IData condition = new DataMap();

        String sn = params.getString("SERIAL_NUMBER", "");
        condition.put("SEND_NUMBER", sn);
        condition.put("SERIAL_NUMBER", sn);
        /*
         * if (!sn.equals("") && sn.length() > 0) { condition.put("SN", sn); IDataset rtnDataset = CSViewCall.call(this,
         * "SS.GetUser360ViewSVC.getUserDiscntAndProduct", condition); condition.put("MESSAGE_CONTENT",
         * rtnDataset.get(0, "MESSAGE_CONTENT")); condition.put("IS_SHOW", rtnDataset.get(0, "IS_SHOW")); }
         */
        condition.put("SIM_CHECK", params.getString("SIM_CHECK", ""));
        condition.put("SIM_NUMBER", params.getString("SIM_NUMBER", ""));
        condition.put("NORMAL_USER_CHECK", params.getString("NORMAL_USER_CHECK", ""));
        condition.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        setCondition(condition);

        if ("1".equals(data.getString("SelectTag", "0")))
        {
            IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserDiscntInfo", data);
            setInfos(output);
        }
        else
        {
            IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserDiscntInfo", data);
            setInfos(output);
        }
    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
