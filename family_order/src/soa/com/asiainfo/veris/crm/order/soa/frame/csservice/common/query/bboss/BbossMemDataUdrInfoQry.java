
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BbossMemDataUdrInfoQry
{

    /**
     * 更加产品订单号查询TI_B_BBOSSMEMBERDATA_UDR表信息
     * 
     * @param productOrderNo
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossMemUdrByProductOrderNo(String productOrderNo, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCTORDERNUMBER", productOrderNo);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT T.IBSYSID, T.MEMBERNUMBER, T.ORDERNUMBER ");
        sql.append("FROM TI_B_BBOSSMEMBERDATA_UDR T ");
        sql.append("WHERE T.ORDERNUMBER = :PRODUCTORDERNUMBER ");
        sql.append("ORDER BY UPDATE_TIME DESC ");

        return Dao.qryBySql(sql, param, page);
    }
}
