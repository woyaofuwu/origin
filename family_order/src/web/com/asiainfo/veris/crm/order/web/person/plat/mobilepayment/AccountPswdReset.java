
package com.asiainfo.veris.crm.order.web.person.plat.mobilepayment;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AccountPswdReset extends PersonBasePage
{

    /**
     * 业务规则校验
     * 
     * @param data
     * @throws Exception
     */
    @Override
    public void checkBeforeTrade(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        data.put("SERVICE_ID", "99166951");
        IDataset userPlatSvcList = CSViewCall.call(this, "CS.UserPlatSvcInfoQrySVC.queryPlatSvcByUserIdAndServiceId", data);
        if (userPlatSvcList == null || userPlatSvcList.isEmpty())
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "该用户没有开通手机支付业务！");
        }

        super.checkBeforeTrade(cycle);
    }

    public void getCustInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        IData allData = new DataMap();
        allData.putAll(custInfo);
        allData.putAll(userInfo);
        setCustInfo(allData);
    }

    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData data);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    /**
     * 提交时触发的方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitTrade(IRequestCycle cycle) throws Exception
    {

        IData data = getData("AUTH", true);
        IDataset result = CSViewCall.call(this, "SS.IBossMobilePaySVC.resetPassword", data);
        setInfos(result);

        setCondition(getData("cond", true));

        // --TODO 打印需要配置
        this.setAjax("ALERT_INFO", "业务信息已发送至业务平台,稍候用户将收到密码重置短信,密码重置成功！");// 传给页面提示

    }

}
