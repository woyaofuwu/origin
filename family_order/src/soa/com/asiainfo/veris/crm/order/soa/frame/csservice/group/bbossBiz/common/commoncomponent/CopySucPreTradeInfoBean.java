
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class CopySucPreTradeInfoBean
{

    /**
     * copy业务预受理归档后的台账数据，isSpec表示是否需要特殊处理merch和merchp表
     * 
     * @param oldTradeId
     * @param isSpec
     * @throws Exception
     * @author fanti3
     */
    public static String copyTradeInfo(String oldTradeId, String intfId, boolean isSpec, boolean isRestore) throws Exception
    {

        String newTradeId = SeqMgr.getTradeId();

        // 1- 循环备份子台账表数据
        String[] tableNameList = intfId.split(",");

        for (int i = 0; i < tableNameList.length; ++i)
        {

            // 获取表名
            String tableName = tableNameList[i];

            // 1-1 主台账数据和订单数据不做备份
            if ("TF_B_TRADE".equals(tableName) || "TF_B_ORDER".equals(tableName))
            {

                continue;
            }

            // 1-2 根据老台账编号查询老台账信息
            IDataset tradeInfoList = TradeInfoQry.queryTradeInfoByTradeIdAndTableName(oldTradeId, tableName);
            // 取不到数据则不做处理
            if (IDataUtil.isEmpty(tradeInfoList))
            {

                continue;
            }

            // 1-3 入各子台账表的备份数据
            for (int index = 0; index < tradeInfoList.size(); ++index)
            {

                // 1-3-1 修改主台账的台账编号和更新时间
                IData tradeInfo = tradeInfoList.getData(index);
                tradeInfo.put("TRADE_ID", newTradeId);
                tradeInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());

                // 1-3-2 台账表特殊处理
                specDealTradeInfo(tableName, tradeInfo, isSpec);

                // 1-3-3 台账表特殊处理恢复
                restoreSpecTradeInfo(tableName, tradeInfo, isRestore);

                // 1-3-4 执行Insert操作
                Dao.insert(tableName, tradeInfo, Route.getJourDb(Route.CONN_CRM_CG));
            }
        }

        return newTradeId;
    }

    /**
     * 恢复merch和merchp表记录
     * 
     * @author ft
     * @param tableName
     * @param tradeInfo
     * @param isRestore
     */
    private static void restoreSpecTradeInfo(String tableName, IData tradeInfo, boolean isRestore)
    {

        // 恢复备份订单标识，转为受理的订单标识
        if (isRestore)
        {
            tradeInfo.put("REMARK", "");
        }

        // 恢复merch和merchp表集团订单、订购关系信息，以免根据集团信息查询时查出两条记录
        if ("TF_B_TRADE_GRP_MERCH".equals(tableName) && isRestore)
        {

            tradeInfo.put("MERCH_ORDER_ID", tradeInfo.getString("MERCH_ORDER_ID").substring(1, // BBOSS
                    // 集团客户商品订单处理失败通知订单备份,
                    // 特殊处理恢复
                    tradeInfo.getString("MERCH_ORDER_ID").length() - 1));
            tradeInfo.put("MERCH_OFFER_ID", tradeInfo.getString("MERCH_OFFER_ID").substring(1, tradeInfo.getString("MERCH_OFFER_ID").length() - 1));

        }
        else if ("TF_B_TRADE_GRP_MERCHP".equals(tableName) && isRestore)
        {

            tradeInfo.put("PRODUCT_ORDER_ID", tradeInfo.getString("PRODUCT_ORDER_ID").substring(1, // BBOSS
                    // 集团客户商品订单处理失败通知订单备份,
                    // 特殊处理恢复
                    tradeInfo.getString("PRODUCT_ORDER_ID").length() - 1));
            tradeInfo.put("PRODUCT_OFFER_ID", tradeInfo.getString("PRODUCT_OFFER_ID").substring(1, tradeInfo.getString("PRODUCT_OFFER_ID").length() - 1));
        }
    }

    /**
     * 针对merch表和merchp表做特殊处理
     * 
     * @author ft
     * @param tableName
     * @param tradeInfo
     * @param isSpec
     */
    private static void specDealTradeInfo(String tableName, IData tradeInfo, boolean isSpec)
    {

        // 备份订单标识
        if (isSpec)
        {
            tradeInfo.put("REMARK", "集团客户商品预受理成功订单备份");
        }

        // 处理merch和merchp表集团订单、订购关系信息，以免根据集团信息查询时查出两条记录
        if ("TF_B_TRADE_GRP_MERCH".equals(tableName) && isSpec)
        {

            tradeInfo.put("MERCH_ORDER_ID", "F" + tradeInfo.getString("MERCH_ORDER_ID")); // BBOSS 集团客户商品订单处理失败通知订单备份
            tradeInfo.put("MERCH_OFFER_ID", "F" + tradeInfo.getString("MERCH_OFFER_ID")); // BBOSS 集团客户商品订单处理失败通知订单备份

        }
        else if ("TF_B_TRADE_GRP_MERCHP".equals(tableName) && isSpec)
        {

            tradeInfo.put("PRODUCT_ORDER_ID", "F" + tradeInfo.getString("PRODUCT_ORDER_ID")); // BBOSS
            // 集团客户商品订单处理失败通知订单备份
            tradeInfo.put("PRODUCT_OFFER_ID", "F" + tradeInfo.getString("PRODUCT_OFFER_ID")); // BBOSS
            // 集团客户商品订单处理失败通知订单备份
        }
    }

    //集客大厅
    public static String copyJKDTTradeInfo(String oldTradeId, String intfId, boolean isSpec, boolean isRestore) throws Exception
    {

        String newTradeId = SeqMgr.getTradeId();

        // 1- 循环备份子台账表数据
        String[] tableNameList = intfId.split(",");

        for (int i = 0; i < tableNameList.length; ++i)
        {

            // 获取表名
            String tableName = tableNameList[i];

            // 1-1 主台账数据和订单数据不做备份
            if ("TF_B_TRADE".equals(tableName) || "TF_B_ORDER".equals(tableName))
            {

                continue;
            }

            // 1-2 根据老台账编号查询老台账信息
            IDataset tradeInfoList = TradeInfoQry.queryTradeInfoByTradeIdAndTableName(oldTradeId, tableName);
            // 取不到数据则不做处理
            if (IDataUtil.isEmpty(tradeInfoList))
            {

                continue;
            }

            // 1-3 入各子台账表的备份数据
            for (int index = 0; index < tradeInfoList.size(); ++index)
            {

                // 1-3-1 修改主台账的台账编号和更新时间
                IData tradeInfo = tradeInfoList.getData(index);
                tradeInfo.put("TRADE_ID", newTradeId);
                tradeInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());

                // 1-3-2 台账表特殊处理
                specJKDTDealTradeInfo(tableName, tradeInfo, isSpec);

                // 1-3-3 台账表特殊处理恢复
                restoreJKDTSpecTradeInfo(tableName, tradeInfo, isRestore);

                // 1-3-4 执行Insert操作
                Dao.insert(tableName, tradeInfo, Route.getJourDb(Route.CONN_CRM_CG));
            }
        }

        return newTradeId;
    }

    private static void specJKDTDealTradeInfo(String tableName, IData tradeInfo, boolean isSpec)
    {

        // 备份订单标识
        if (isSpec)
        {
            tradeInfo.put("REMARK", "集团客户商品预受理成功订单备份");
        }

        // 处理merch和merchp表集团订单、订购关系信息，以免根据集团信息查询时查出两条记录
        if ("TF_B_TRADE_ECRECEP_OFFER".equals(tableName) && isSpec)
        {

            tradeInfo.put("MERCH_ORDER_ID", "F" + tradeInfo.getString("MERCH_ORDER_ID")); // BBOSS 集团客户商品订单处理失败通知订单备份
            tradeInfo.put("MERCH_OFFER_ID", "F" + tradeInfo.getString("MERCH_OFFER_ID")); // BBOSS 集团客户商品订单处理失败通知订单备份

        }
        else if ("TF_B_TRADE_ECRECEP_PRODUCT".equals(tableName) && isSpec)
        {

            tradeInfo.put("PRODUCT_ORDER_ID", "F" + tradeInfo.getString("PRODUCT_ORDER_ID")); // BBOSS
            // 集团客户商品订单处理失败通知订单备份
            tradeInfo.put("PRODUCT_OFFER_ID", "F" + tradeInfo.getString("PRODUCT_OFFER_ID")); // BBOSS
            // 集团客户商品订单处理失败通知订单备份
        }
    }
    
    //集客大厅
    private static void restoreJKDTSpecTradeInfo(String tableName, IData tradeInfo, boolean isRestore)
    {

        // 恢复备份订单标识，转为受理的订单标识
        if (isRestore)
        {
            tradeInfo.put("REMARK", "");
        }

        // 恢复merch和merchp表集团订单、订购关系信息，以免根据集团信息查询时查出两条记录
        if ("TF_B_TRADE_ECRECEP_OFFER".equals(tableName) && isRestore)
        {

            tradeInfo.put("MERCH_ORDER_ID", tradeInfo.getString("MERCH_ORDER_ID").substring(1, // BBOSS
                    // 集团客户商品订单处理失败通知订单备份,
                    // 特殊处理恢复
                    tradeInfo.getString("MERCH_ORDER_ID").length() - 1));
            tradeInfo.put("MERCH_OFFER_ID", tradeInfo.getString("MERCH_OFFER_ID").substring(1, tradeInfo.getString("MERCH_OFFER_ID").length() - 1));

        }
        else if ("TF_B_TRADE_ECRECEP_PRODUCT".equals(tableName) && isRestore)
        {

            tradeInfo.put("PRODUCT_ORDER_ID", tradeInfo.getString("PRODUCT_ORDER_ID").substring(1, // BBOSS
                    // 集团客户商品订单处理失败通知订单备份,
                    // 特殊处理恢复
                    tradeInfo.getString("PRODUCT_ORDER_ID").length() - 1));
            tradeInfo.put("PRODUCT_OFFER_ID", tradeInfo.getString("PRODUCT_OFFER_ID").substring(1, tradeInfo.getString("PRODUCT_OFFER_ID").length() - 1));
        }
    }

}
