
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleGoodsSVC extends CSBizService
{
    private static final long serialVersionUID = -6584796247379107501L;

    public IData checkResInfo(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_NO", input.getString("RES_NO"));
        param.put("BUY_TYPE", SaleActiveConst.TERMINAL_BUY_TYPE_GIFT);
        param.put("SALE_STAFF_ID", input.getString("SALE_STAFF_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, input.getString(Route.ROUTE_EPARCHY_CODE));
        IDataset terminalInfos = CSAppCall.call("CS.TerminalQuerySVC.getEnableTerminalByResNo", input);
        return terminalInfos.getData(0);
    }
}
