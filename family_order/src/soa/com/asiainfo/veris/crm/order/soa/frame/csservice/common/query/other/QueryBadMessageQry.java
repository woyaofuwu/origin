
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 不良短信查询
 * 
 * @author steven
 */
public class QueryBadMessageQry extends CSBizBean
{

    public static IDataset queryBadMessage(String msISDN, String content, String startDate, String endDate, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("MSISDN", msISDN);
        params.put("CONTENT", content);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
        return Dao.qryByCode("TI_O_TDMC", "SEL_BAD_MESSAGE_NEW", params, pagination, routeEparchyCode);

    }

}
