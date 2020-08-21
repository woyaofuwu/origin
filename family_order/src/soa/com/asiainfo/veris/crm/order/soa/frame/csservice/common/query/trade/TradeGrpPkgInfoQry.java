
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeGrpPkgInfoQry
{

    /**
     * 查询集团定制的包中Trade相关优惠元素
     * 
     * @param trade_id
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getGrpCustomizeDiscntByTradeId(String trade_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        // TODO param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_ID", trade_id);
        return Dao.qryByCode("TF_B_TRADE_GRP_PACKAGE", "SEL_DISCNT_BY_TRADEID", param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团定制的包中Trade相关服务元素
     * 
     * @param trade_id
     * @param user_id
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getGrpCustomizeServByTradeId(String trade_id, String user_id, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        // TODO param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_ID", trade_id);
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_B_TRADE_GRP_PACKAGE", "SEL_SERV_BY_TRADEID", param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团定制的包中相关TRADEID服务元素
     * 
     * @param trade_id
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getGrpCustomizeSpByTradeId(String trade_id, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        // TODO param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_ID", trade_id);
        return Dao.qryByCode("TF_B_TRADE_GRP_PACKAGE", "SEL_SP_BY_TRADEID", param, pagination, Route.CONN_CRM_CG);
    }
}
