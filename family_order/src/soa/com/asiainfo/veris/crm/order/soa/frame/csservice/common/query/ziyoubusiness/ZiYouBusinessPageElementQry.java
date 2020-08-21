
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ziyoubusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ZiYouBusinessPageElementQry
{
    public static IDataset getFbPageElements(String tradeTypeCode, String lv) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("LV", lv);
        return Dao.qryByCode("TD_B_DYNAMIC_PAGE", "SEL_BY_TRADETYPECODE_LV", param, Route.CONN_CRM_CEN);
    }

}
