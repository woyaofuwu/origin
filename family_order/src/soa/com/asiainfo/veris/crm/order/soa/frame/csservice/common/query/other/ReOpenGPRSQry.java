
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ReOpenGPRSQry extends CSBizBean
{

    public static IDataset getReasonForStopGPRS(String tradeTypeCode, String userId, String acceptDate, String cancelTag) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_TYPE_CODE", tradeTypeCode);
        params.put("USER_ID", userId);
        params.put("ACCEPT_DATE", acceptDate);
        params.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCode("TF_BH_TRADE", "SEL_RSRV_BY_USERID", params,Route.getJourDb(BizRoute.getRouteId()));

    }

}
