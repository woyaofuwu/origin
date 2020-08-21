
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.sync;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCreditInfoQry;

public final class TradeSyncCredit
{
    private final static Logger logger = Logger.getLogger(TradeSyncCredit.class);

    /**
     * 信誉度同步表数据准备
     * 
     * @param ywToCreditDataset
     * @param tradeId
     * @param syncId
     * @throws Exception
     */
    private static IDataset getTradeCredit(IData tradeMains) throws Exception
    {

        String tradeId = tradeMains.getString("TRADE_ID");
        String syncId = tradeMains.getString("SYNC_SEQUENCE");
        String cancelTag = tradeMains.getString("CANCEL_TAG");

        String trade_type_code = tradeMains.getString("TRADE_TYPE_CODE");
        String eparchy_code = tradeMains.getString("EPARCHY_CODE");

        // 获取信誉度子台账
        IDataset tradeCredits = TradeCreditInfoQry.getTradeCreditByPK(tradeId, BofConst.MODIFY_TAG_ADD);

        if (IDataUtil.isEmpty(tradeCredits))
        {
            return tradeCredits;
        }

        IDataset ywToCreditDataset = new DatasetList();

        for (int i = 0; i < tradeCredits.size(); i++)
        {
            IData tradeCredit = tradeCredits.getData(i);

            String user_id = tradeCredit.getString("USER_ID");
            String credit_mode = tradeCredit.getString("CREDIT_MODE");
            String rsrvStr1 = tradeCredit.getString("RSRV_STR1");
            String rsrvStr2 = tradeCredit.getString("RSRV_STR2");
            String rsrvStr3 = tradeCredit.getString("RSRV_STR3");
            IData ywToCreditData = new DataMap();

            long creditValue = tradeCredit.getLong("CREDIT_VALUE", 0L);

            if (StringUtils.equals("2", cancelTag))// 返销时取反
            {
                creditValue = -1 * creditValue;
            }

            if (StringUtils.equals("addCredit", credit_mode))// 增加信用度
            {
                rsrvStr1 = String.valueOf(creditValue);
                rsrvStr2 = tradeCredit.getString("START_DATE");
                rsrvStr3 = tradeCredit.getString("END_DATE");
            }

            ywToCreditData.put("USER_ID", user_id);
            ywToCreditData.put("TRADE_TYPE_CODE", trade_type_code);
            ywToCreditData.put("SYNC_SEQUENCE", syncId);
            ywToCreditData.put("TRADE_ID", tradeId);
            ywToCreditData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            ywToCreditData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            ywToCreditData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            ywToCreditData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            ywToCreditData.put("CREDIT_VALUE", creditValue);
            ywToCreditData.put("EPARCHY_CODE", eparchy_code);
            ywToCreditData.put("SYNC_DAY", String.valueOf(Integer.valueOf(syncId.substring(6, 8))));
            ywToCreditData.put("RSRV_STR1", rsrvStr1);
            ywToCreditData.put("RSRV_STR2", rsrvStr2);
            ywToCreditData.put("RSRV_STR3", rsrvStr3);
            ywToCreditData.put("RSRV_STR4", tradeCredit.getString("RSRV_STR4"));
            ywToCreditData.put("RSRV_STR5", tradeCredit.getString("RSRV_STR5"));

            ywToCreditDataset.add(ywToCreditData);
        }

        return ywToCreditDataset;
    }

    /**
     * 插信誉度同步表
     * 
     * @param ywToCreditDataset
     * @throws Exception
     */
    private static void insYwToCredit(IDataset ywToCreditDataset) throws Exception
    {
        Dao.insert("TI_O_YWTOCREDIT", ywToCreditDataset);
    }

    public static void syncTradeCredit(IData mainTrade) throws Exception
    {
        String intfId = mainTrade.getString("INTF_ID", "");

        if (StringUtils.isNotBlank(intfId) && intfId.indexOf("TF_B_TRADE_CREDIT,") == -1)
        {
            return;
        }

        // 信誉度同步表数据准备
        IDataset ywToCreditDataset = getTradeCredit(mainTrade);

        // 插信誉度同步表
        if (IDataUtil.isNotEmpty(ywToCreditDataset))
        {
            insYwToCredit(ywToCreditDataset);
        }
    }
}
