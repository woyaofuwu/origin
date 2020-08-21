
package com.asiainfo.veris.crm.order.web.person.plat.mobilepayment;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.person.plat.PlatOrder;

public abstract class AccountOpen extends PlatOrder
{

    /**
     * 业务规则校验
     * 
     * @param data
     * @throws Exception
     */
    public void checkBeforeTrade(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        data.put("SERVICE_ID", "99166951");
        IDataset userPlatSvcList = CSViewCall.call(this, "CS.UserPlatSvcInfoQrySVC.queryPlatSvcByUserIdAndServiceId", data);
        if (userPlatSvcList != null && !userPlatSvcList.isEmpty())
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "该用户已开通手机支付业务，不能重复办理！");
        }

        super.checkBeforeTrade(cycle);
    }
}
