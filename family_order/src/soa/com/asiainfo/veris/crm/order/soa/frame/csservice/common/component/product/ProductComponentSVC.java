
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.product;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ProductComponentSVC extends CSBizService
{
    public IDataset transmit(IData data) throws Exception
    {
        String acctDay = data.getString("ACCT_DAY");
        String firstDate = data.getString("FIRST_DATE");
        String nextAcctDay = data.getString("NEXT_ACCT_DAY");
        String nextFirstDate = data.getString("NEXT_FIRST_DATE");

        if (StringUtils.isNotBlank(acctDay))
        {
            AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDate, nextAcctDay, nextFirstDate);
            AcctTimeEnvManager.setAcctTimeEnv(env);
        }
        data.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        return CSAppCall.call(data.getString("SVC_NAME"), data);
    }
}
