
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 统一订购和退订查询
 * 
 * @author zhouwu
 * @date 2014-07-29 21:24:56
 */
public class UnionOrderAndCancelQry extends CSBizBean
{

    /**
     * 统一退订查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryUnionCancelInfos(IData data, Pagination pagination) throws Exception
    {
    	//TODO huanghua 22 此处默认数据源为cen1，本机没有该表，暂时屏蔽---测试环境查询正常
        return Dao.qryByCode("SMS", "SEL_UNION_SESSION", data, pagination);
    }

    /**
     * 统一订购查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryUnionOrderInfos(IData data, Pagination pagination) throws Exception
    {
    	//TODO huanghua 15 此处默认数据源为cen1，改为crm1
        return Dao.qryByCode("SMS", "SEL_UNION_ORDER_LOG", data, pagination, Route.getCrmDefaultDb());
    }

    /**
     * 统一退订业务详细信息查询
     * 
     * @param sessionId
     * @param orderId
     * @return
     * @throws Exception
     */
    public static IDataset showCancelDetailInfo(String sessionId, String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("SESSION_ID", sessionId);
        param.put("ORDER_ID", orderId);

        return Dao.qryByCode("SMS", "SEL_UNION_SESSION_DLOG", param);
    }

    /**
     * 统一订购详细信息查询
     * 
     * @param sessionId
     * @param orderId
     * @return
     * @throws Exception
     */
    public static IDataset showOrderDetailInfo(String sessionId, String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("SESSION_ID", sessionId);
        param.put("ORDER_ID", orderId);

        return Dao.qryByCode("SMS", "SEL_UNION_ORDER_DLOG", param);
    }

    /**
     * 统一退订会话详细信息查询
     * 
     * @param sessionId
     * @return
     * @throws Exception
     */
    public static IDataset showSessionDetailInfo(String sessionId) throws Exception
    {
        IData param = new DataMap();
        param.put("SESSION_ID", sessionId);

        return Dao.qryByCode("SMS", "SEL_UNION_SESSION_LOG", param);
    }

}
