
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userreservecombo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUserReserveComboQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;

public class QueryUserReserveComboBean extends CSBizBean
{
    public IDataset queryUserReserveDiscnt(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = Route.getJourDb();// BizRoute.getRouteId();
        String tradeId = data.getString("TRADE_ID", "");
        IDataset dataSet = QueryUserReserveComboQry.queryUserReserveDiscnt(tradeId, routeEparchyCode, page);
        return dataSet;
    }

    public IDataset queryUserReserveProduct(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = Route.getJourDb();// BizRoute.getRouteId();
        String tradeId = data.getString("TRADE_ID", "");
        IDataset dataSet = QueryUserReserveComboQry.queryUserReserveProduct(tradeId, routeEparchyCode, page);
        return dataSet;
    }

    public IDataset queryUserReserveService(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = Route.getJourDb();// BizRoute.getRouteId();
        String tradeId = data.getString("TRADE_ID", "");
        IDataset dataSet = QueryUserReserveComboQry.queryUserReserveService(tradeId, routeEparchyCode, page);
        return dataSet;
    }

    /**
     * 功能：用户预约产品查询 作者：GongGuang
     */
    public IDataset queryUserReserveTrade(IData data, Pagination page) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        // IDataset userNextProducts = UserProductInfoQry.queryNextProductBySn(serialNumber, "0");
        // if (IDataUtil.isEmpty(userNextProducts))
        // {
        // CSAppException.apperr(CrmUserException.CRM_USER_1201);
        // }
        // String instId = userNextProducts.getData(0).getString("INST_ID");
        // String userId =
        // userNextProducts.getData(0).getString("USER_ID");//UcaDataFactory.getNormalUca(serialNumber).getUserId();
        String userId = UcaDataFactory.getNormalUca(serialNumber).getUserId();
        IDataset trades = TradeHistoryInfoQry.queryBookedProductTrade(userId, "110");
        return trades;
    }
}
