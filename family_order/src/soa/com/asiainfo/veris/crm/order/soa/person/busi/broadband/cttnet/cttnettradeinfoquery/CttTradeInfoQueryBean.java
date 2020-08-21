
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnettradeinfoquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class CttTradeInfoQueryBean extends CSBizBean
{
    public IDataset getTradeInfo(IData param, Pagination pagination) throws Exception
    {
        IData input = new DataMap();
        input.put("TRADE_EPARCHY_CODE", param.getString(Route.ROUTE_EPARCHY_CODE));
        input.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        input.put("START_DATE", param.getString("START_DATE"));
        input.put("FINISH_DATE", param.getString("END_DATE"));
        input.put("TRADE_DEPART_ID", param.getString("DEPART_ID"));
        input.put("TRADE_STAFF_ID", param.getString("TRADE_STAFF_ID"));
        input.put("TRADE_ID", param.getString("TRADE_ID"));

        return TradeInfoQry.queryTradeInfo(input, pagination);
    }
}
