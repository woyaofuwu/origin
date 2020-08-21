
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImeiInfoQry;

public class UserImeiFinishAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        IDataset tradeSaleGoodsList = TradeSaleGoodsInfoQry.getTradeSaleGoods(mainTrade.getString("TRADE_ID"), BofConst.MODIFY_TAG_DEL);

        if (IDataUtil.isEmpty(tradeSaleGoodsList))
            return;

        IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));

        if (tradeSaleActive.getData(0).getString("RSRV_TAG3", "").equals("S"))
            return;

        for (int index = 0, size = tradeSaleGoodsList.size(); index < size; index++)
        {
            IData tradeSaleGoodsData = tradeSaleGoodsList.getData(index);
            String resTypeCode = tradeSaleGoodsData.getString("RES_TYPE_CODE");

            if (!"4".equals(resTypeCode) || !(tradeSaleGoodsData.getString("RES_CODE", "").length() > 2))
                continue;

            IData cond = new DataMap();

            cond.put("USER_ID", tradeSaleGoodsData.getString("USER_ID"));
            cond.put("IMEI", tradeSaleGoodsData.getString("RES_CODE"));

            Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "DEL_USERIMEI_BY_ENDDATE", cond);

            IDataset userImeis = UserImeiInfoQry.getUserImeiInfoByUserId(tradeSaleGoodsData.getString("USER_ID"));

            if (IDataUtil.isEmpty(userImeis))
                continue;

            IData userImei = userImeis.getData(0);

            cond.put("USER_ID", tradeSaleGoodsData.getString("USER_ID"));
            cond.put("IMEI", userImei.getString("IMEI"));
            cond.put("START_DATE", userImei.getString("START_DATE"));
            cond.put("END_DATE", SysDateMgr.getSysTime());

            Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_USERIMEI_BY_IMEI_STARTDATE", cond);
        }
    }

}
