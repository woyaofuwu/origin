package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sundryquery.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Created by zhaohj3 on 2019/2/26.
 */
public class SyncActivateTimeQry {
    public static IDataset qrySyncActivateTimeInfoBySnProd(String sn, String productId) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_B_SYNC_ACTIVATE_TIME", "SEL_BY_SN_PRODUCT_ID", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qrySyncActivateTimeInfoByTradeId(String tradeId) throws Exception {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_SYNC_ACTIVATE_TIME", "SEL_BY_TRADE_ID", param, Route.CONN_CRM_CEN);
    }
}
