
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class GetModifyTrade
{

    /**
     * 更新trade表数据
     * 
     * @param tradeId
     * @param orderId
     * @param sysDate
     * @param merchInfo
     * @param merchPInfos
     * @throws Exception
     */
    public IDataset modifyTrade(IData data) throws Exception
    {

        IDataset ids = new DatasetList();
        IData dt = new DataMap();
        dt.put("X_RESULTCODE", "0"); // 正常状态
        dt.put("X_RESULTINFO", "ok"); // 正常状态描述
        ids.add(dt);
        String tradeId = data.getString("TRADE_ID", "");

        String subscribeId = data.getString("SUBSCRIBE_ID", "");
        if ("".equals(subscribeId))
        {
            tradeId = data.getString("TRADE_ID", "");
        }

        if (!"".equals(subscribeId) && "".equals(tradeId))
        {
            return ids;
            // TradeInfoQry tradedao = new TradeInfoQry();
            //
            // IDataset datas = tradedao.getTradeBysubscribeIdForEsop(subscribeId);
            //
            // if (IDataUtil.isNotEmpty(datas))
            // {
            // tradeId = datas.getData(0).getString("TRADE_ID", "");
            // }
        }

        IDataset tradeDatas = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId);
        String orderId = "";
        if (null != tradeDatas)
        {
            IData tradeData = tradeDatas.getData(0);
            orderId = tradeData.getString("ORDER_ID", "");
        }
        else
        {
            dt.put("X_RESULTCODE", "01"); // 异常状态
            dt.put("X_RESULTINFO", "对应的tradeId：[" + tradeId + "]查询不到对应台帐信息"); // 正常状态描述
            return ids;
        }

        StringBuilder buf = new StringBuilder();
        // getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);

        if (tradeId != null)
        {
            // 台账优惠表
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_DISCNT T ");
            buf.append(" SET T.START_DATE= sysDate");
            buf.append(" WHERE T.START_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_DISCNT T ");
            buf.append(" SET T.END_DATE= sysdate");
            buf.append(" WHERE T.END_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='1' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            // 业务台帐服务
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_SVC T ");
            buf.append(" SET T.START_DATE= sysdate");
            buf.append(" WHERE T.START_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_SVC T ");
            buf.append(" SET T.END_DATE= sysdate");
            buf.append(" WHERE T.END_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='1' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            // 业务台帐服务
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_PAYRELATION T ");
            buf.append(" SET T.start_cycle_id=to_char(sysdate,'YYYYMMDD')");
            buf.append(" WHERE T.start_cycle_id < to_char(sysdate,'YYYYMMDD')");
            buf.append(" AND T.MODIFY_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_PAYRELATION T ");
            buf.append(" SET T.end_cycle_id=to_char(trunc(last_day(SYSDATE)),'YYYYMMDD')");
            buf.append(" WHERE T.end_cycle_id < to_char(sysdate,'YYYYMMDD')");
            buf.append(" AND T.MODIFY_TAG='1' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            // 业务台帐服务状态
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
            buf.append(" SET T.START_DATE= sysdate");
            buf.append(" WHERE T.START_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
            buf.append(" SET T.END_DATE= sysdate");
            buf.append(" WHERE T.END_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='1' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            // 台账参数表
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_ATTR T ");
            buf.append(" SET T.START_DATE= sysdate");
            buf.append(" WHERE T.START_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_ATTR T ");
            buf.append(" SET T.END_DATE= sysdate");
            buf.append(" WHERE T.END_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='1' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            // 台账产品表
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
            buf.append(" SET T.START_DATE= sysdate");
            buf.append(" WHERE T.START_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
            buf.append(" SET T.END_DATE= sysdate");
            buf.append(" WHERE T.END_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='1' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            // 台账关系表
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_RELATION T ");
            buf.append(" SET T.START_DATE= sysdate");
            buf.append(" WHERE T.START_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_RELATION T ");
            buf.append(" SET T.END_DATE= sysdate");
            buf.append(" WHERE T.END_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='1' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            // 台账用户表
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_USER T ");
            buf.append(" SET T.OPEN_DATE= sysdate");
            buf.append(" WHERE T.OPEN_DATE < sysdate");
            buf.append(" AND T.MODIFY_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE_USER T ");
            buf.append(" SET T.DESTROY_TIME= sysdate");
            buf.append(" WHERE T.DESTROY_TIME < sysdate");
            buf.append(" AND T.MODIFY_TAG='1' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR('" + tradeId + "',5,2))");
            Dao.executeUpdate(buf);

        }
        if (orderId != null)
        {
            // 订单表
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_ORDER T ");
            buf.append(" SET T.EXEC_TIME= sysdate");
            buf.append(" WHERE T.ORDER_ID=TO_NUMBER('" + orderId + "')");
            Dao.executeUpdate(buf);

        }

        if (tradeId != null)
        {
            // 台账主表
            buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE T ");
            buf.append(" SET T.EXEC_TIME= sysdate");
            buf.append(" WHERE T.TRADE_ID=TO_NUMBER('" + tradeId + "') ");
            Dao.executeUpdate(buf);
        }

        return ids;
    }
}
