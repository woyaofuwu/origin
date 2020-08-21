
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.requestdata.ChangeTrunkIdReqData;

public class ChangeTrunkIdTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ChangeTrunkIdReqData changeTrunkIdRD = (ChangeTrunkIdReqData) btd.getRD();
        btd.getMainTradeData().setRsrvStr1(changeTrunkIdRD.getTrunkId());// RSRV_STR1 记录新trunkid
        btd.getMainTradeData().setRsrvStr2(changeTrunkIdRD.getSwitchId());// RSRV_STR2记录老switchid
        btd.getMainTradeData().setRsrvStr3(changeTrunkIdRD.getOldTrunkId());// RSRV_STR1 记录老的trunkid
        btd.getMainTradeData().setNetTypeCode("12");
        String user_id = btd.getRD().getUca().getUserId();
        String trunckIdString = ((ChangeTrunkIdReqData) btd.getRD()).getTrunkId();
        String[] trunckIds = trunckIdString.split(",");
        for (int i = 0; i < trunckIds.length; i++)
        {
            String trunckId = trunckIds[i];
            StringBuilder sql = new StringBuilder();
            IData params = new DataMap();
            params.put("USER_ID", user_id);
            params.put("TRUNK_ID", trunckId);
            sql.append(" UPDATE TF_F_USER_TRUNK ");
            sql.append(" SET TRUNK_ID = :TRUNK_ID ");
            sql.append(" WHERE 1=1 ");
            sql.append(" and user_id = to_number(:USER_ID) ");
            sql.append(" AND START_DATE<=SYSDATE ");
            sql.append(" AND END_DATE>SYSDATE ");
            IData data = new DataMap();
            Dao.executeUpdate(sql, params);
        }

    }
}
