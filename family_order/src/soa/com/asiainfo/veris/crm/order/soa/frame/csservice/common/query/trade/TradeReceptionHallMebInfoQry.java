package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeReceptionHallMebInfoQry {

    /**
     * @Description:根据用户的user_id 找商品关系台帐编码(带route)
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchMebInfoByUserIdOfferIdRouteId(String userId, String productOfferId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_OFFER_ID", productOfferId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select b.* ");
        parser.addSQL("from tf_b_trade_ecrecep_meb a, tf_b_trade b ");
        parser.addSQL("where a.trade_id = b.trade_id ");
        parser.addSQL("and a.accept_month = b.accept_month ");
        parser.addSQL("and b.USER_ID = :USER_ID ");
        parser.addSQL("and a.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");


        return Dao.qryByParse(parser, Route.getJourDb(routeId));
    }
    
    /**
     * @Description:根据用户的user_id 找商品关系台帐编码
     * @author shixb修改
     * @date
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getMerchpMebTradeInfo(String serialNumber, String productOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/ b.*");
        parser.addSQL("  from tf_b_trade_ecrecep_meb  a  ,tf_b_trade b");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and b.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" and a.PRODUCT_OFFER_ID =:PRODUCT_OFFER_ID");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, pagination,Route.getJourDb(BizRoute.getRouteId()));
    }

}
