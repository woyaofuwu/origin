
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PoTradePlusInfoQry
{

    /*
     * @description 根据订单编号和产品参数编号查询工单参数表数据
     * @author xunyl
     * @date 2014-04-15
     */
    public static IDataset qryPoTradePlus(String productOrderNum, String productSpecCharacterNum) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCTORDERNUMBER", productOrderNum);
        inparam.put("PRODUCTSPECCHARACTERNUMBER", productSpecCharacterNum);

        return Dao.qryByCode("TF_B_POTRADEPLUS", "SEL_BY_ORDERNUM_CHARACTERNUM", inparam,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据产品订单号查询TF_B_POTRADEPLUS表订单信息
     * 
     * @author ft
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryPoTradePlusInfoByProductOrderNo(String productOrderNo, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCTORDERNUMBER", productOrderNo);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT T.PRODUCTORDERNUMBER, ");
        sql.append("T.PRODUCTSPECCHARACTERNUMBER, ");
        sql.append("T.CHARACTERVALUE, ");
        sql.append("T.NAME, ");
        sql.append("DECODE(T.ACTION, 0, '删除', 1, '增加') AS ACTION ");
        sql.append("FROM TF_B_POTRADEPLUS T ");
        sql.append("WHERE T.PRODUCTORDERNUMBER = :PRODUCTORDERNUMBER ");

        return Dao.qryBySql(sql, inparam, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

}
