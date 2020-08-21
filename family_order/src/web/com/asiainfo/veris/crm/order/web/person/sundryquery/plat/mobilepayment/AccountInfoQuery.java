
package com.asiainfo.veris.crm.order.web.person.sundryquery.plat.mobilepayment;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AccountInfoQuery extends PersonBasePage
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

    public void init(IRequestCycle cycle) throws Exception
    {
        IData inits = getData("cond", true);
        this.setCondition(inits);
    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset result = CSViewCall.call(this, "SS.AccountInfoQuerySVC.queryAccountInfo", data);
        setInfos(result);
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

        IData data = getData("cond", true);
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        IDataset result = CSViewCall.call(this, "SS.AccountInfoQuerySVC.changeRealName", data);

        if (result != null && !result.isEmpty())
        {
            setAjax("ALERT_INFO", "实名信息更新成功！");
        }
        else
        {
            // common.error("操作失败！<br>" + result.getString("X_RSPDESC"));
            String message = "操作失败，返回错误信息为：";
            if (result != null && !result.isEmpty())
            {
                message += result.getData(0).getString("X_RSPDESC", "");
            }
            else
            {
                message += "接口返回null";
            }
            CSViewException.apperr(PlatException.CRM_PLAT_74, message);
        }
    }

}
