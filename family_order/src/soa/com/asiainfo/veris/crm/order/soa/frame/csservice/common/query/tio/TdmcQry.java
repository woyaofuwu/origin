
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tio;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: TdmcQry.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2013-9-17 下午7:59:32 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-9-17 lijm3 v1.0.0 修改原因
 */
public class TdmcQry extends CSBizBean
{

    public static IDataset getTdmcsBySelByAll(String msisdn, String content, String start_date, String end_date, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MSISDN", msisdn);
        param.put("CONTENT", content);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);

        return Dao.qryByCode("TI_O_TDMC", "SEL_BY_ALL", param, pagination);
    }

    public static IDataset getTdmcsBySelByAll(String msisdn, String content, String start_date, String end_date, String tradeEparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MSISDN", msisdn);
        param.put("CONTENT", content);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);

        return Dao.qryByCode("TI_O_TDMC", "SEL_BY_ALL", param, pagination, tradeEparchyCode);
    }

    public static IDataset getTdmcsBySelByMsisdn(String msisdn, String content, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MSISDN", msisdn);
        param.put("CONTENT", content);

        return Dao.qryByCode("TI_O_TDMC", "SEL_BY_MSISDN", param, pagination);
    }

    public static IDataset getTdmcsBySelByMsisdn(String msisdn, String content, String tradeEparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MSISDN", msisdn);
        param.put("CONTENT", content);

        return Dao.qryByCode("TI_O_TDMC", "SEL_BY_MSISDN", param, pagination, tradeEparchyCode);
    }

}
