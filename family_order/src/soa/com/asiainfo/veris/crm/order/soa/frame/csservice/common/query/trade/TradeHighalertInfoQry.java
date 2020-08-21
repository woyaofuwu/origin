
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: TradeHighalertInfoQry.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2013-10-17 下午8:56:04 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-10-17 lijm3 v1.0.0 修改原因
 */
public class TradeHighalertInfoQry extends CSBizService
{

    public static IDataset getHighalertTradeByCreatDate(String start_date, String end_date, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        return Dao.qryByCodeParser("TF_F_HIGHALERT_TRADE", "SEL_ALL_BY_CREAT_DATE", param, page, Route.CONN_CRM_CEN);
    }

}
