
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossOrderDeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class BBossOrderDeal
{

    /*
     * @description bboss瞬时报文处理
     * @author zhangcheng
     * @date 2013-08-15
     */
    public static IDataset dealBbossRspMessage(IData map) throws Exception
    {
        String tradeId = map.getString("TRADE_ID");

        // 1- 处理返回结果
        IData result = new DataMap();

        if (map.containsKey("BBSS_ODRRSP_POOFFERID")) // 表示商品订购关系
        {
            String poOfferid = dealString(map.getString("BBSS_ODRRSP_POOFFERID")); // BBOSS商品订购关系
            String poOdnum = dealString(map.getString("BBSS_ODRRSP_POODRNUM")); // BBOSS订单号

            // 根据商品订单号修改
            IData merchParam = new DataMap();
            merchParam.put("TRADE_ID", tradeId);
            merchParam.put("MERCH_OFFER_ID", poOfferid);
            merchParam.put("MERCH_ORDER_ID", poOdnum);

            // 根据流水号更新商品订购关系 和BBOSS订单号
            Dao.save("TF_B_TRADE_GRP_MERCH", merchParam, new String[]{ "TRADE_ID" },Route.getJourDb());
        }

        if (map.containsKey("BBSS_ODRRSP_PO_PID"))
        {
            //更新merchp表记录
            String poPid = dealString(map.getString("BBSS_ODRRSP_PO_PID")); // BBOSS产品订购关系
            String poPnum = dealString(map.getString("BBSS_ODRRSP_PO_PNUM")); // BBOSS产品订单号
            IData merchpParam = new DataMap();
            merchpParam.put("TRADE_ID", tradeId);
            merchpParam.put("PRODUCT_OFFER_ID", poPid);
            merchpParam.put("PRODUCT_ORDER_ID", poPnum);
            // 根据流水号更新商品订购关系
            Dao.save("TF_B_TRADE_GRP_MERCHP", merchpParam, new String[]{ "TRADE_ID" },Route.getJourDb());
            
            //更新other表记录
            IDataset tradeGrpMerchpInfoList = TradeGrpMerchpInfoQry.qryGrpMerchpByTradeId(tradeId,null);
            if(IDataUtil.isNotEmpty(tradeGrpMerchpInfoList)){
                IData tradeGrpMerchInfo = tradeGrpMerchpInfoList.getData(0);
                String merchSpecCode = tradeGrpMerchInfo.getString("MERCH_SPEC_CODE");
                IData isToBilling = StaticInfoQry.getStaticInfoByTypeIdDataId("BBOSS_OTHERTOBILLING", merchSpecCode);
                if (IDataUtil.isNotEmpty(isToBilling))
                {
                    String rsrvValueCode = isToBilling.getString("PDATA_ID");                   
                    IData otherParam = new DataMap();
                    otherParam.put("TRADE_ID", tradeId);
                    otherParam.put("RSRV_VALUE_CODE", rsrvValueCode);
                    otherParam.put("RSRV_VALUE", poPid);
                    otherParam.put("RSRV_STR1", poPid);                                        
                    Dao.save("TF_B_TRADE_OTHER", otherParam, new String[]{ "TRADE_ID", "RSRV_VALUE_CODE"},Route.getJourDb());
                }
            }           
        }        
        
        // BBOSS成员叠加包订购订单号
        if (StringUtils.isNotEmpty(map.getString("MEMBER_ORDER_NUMBER")))
        {
            String memberOrderNumber = dealString(map.getString("MEMBER_ORDER_NUMBER"));
            IData merchpParam = new DataMap();
            merchpParam.put("TRADE_ID", tradeId);
            merchpParam.put("MEMBERORDERNUMBER", memberOrderNumber);
            // 根据流水号更新成员订购关系
            Dao.save("TF_B_TRADE_GRP_MERCH_MB_DIS", merchpParam, new String[]{ "TRADE_ID" },Route.getJourDb());
        }

        // 更新订单状态
        // 订单状态在TradePf.backPf里一起更新了
        // IData paramTrade = new DataMap();
        // paramTrade.put("TRADE_ID", tradeId);
        // paramTrade.put("SUBSCRIBE_STATE", "W");
        // Dao.save("TF_B_TRADE", paramTrade, new String[]
        // { "TRADE_ID" });

        // 回收管理节点数据
        // 清空回收TF_B_TRADE_DISCNT（删除str3=M的数据）
        Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "DEL_DISTNCT_BYTRADEID", map,Route.getJourDb());

        // 清空回收TF_B_TRADE_ATTR（删除str1=M的数据）
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "DEL_TRADEATTR_BYTRADEID", map,Route.getJourDb());

        // 清空回收TF_B_TRADE_GRP_MERCHP_DISCNT（删除str3=M的数据）
        Dao.executeUpdateByCodeCode("TF_B_TRADE_GRP_MERCHP_DISCNT", "DEL_BY_TRADE_DISCNT_STR1", map,Route.getJourDb());

        result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result.put("RSP_CODE", "00");
        result.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);

        return new DatasetList();
    }

    /*
     * @description bboss截取字符串
     * @author zhangcheng
     * @date 2013-08-15
     */
    public static String dealString(String s)
    {
        if (s.startsWith("[\"") && s.endsWith("\"]"))
        {
            s = s.substring(2, s.length() - 2);
        }

        return s;
    }

}
