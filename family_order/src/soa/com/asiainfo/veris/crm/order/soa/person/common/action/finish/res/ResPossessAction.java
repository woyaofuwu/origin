
package com.asiainfo.veris.crm.order.soa.person.common.action.finish.res;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;

public class ResPossessAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {

        // 资源占用
        String strTradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String serailNumber = mainTrade.getString("SERIAL_NUMBER");
        String productId = mainTrade.getString("PRODUCT_ID");
        String brandCode = mainTrade.getString("BRAND_CODE");
        String advanceFee = mainTrade.getString("ADVANCE_PAY", "0");
        String rsrvStr7 = mainTrade.getString("RSRV_STR7", "0");
        // --->和多号业务需求的改造:
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String agentId = "";
        
        String brand_code = mainTrade.getString("BRAND_CODE");
        
        // 处理代理商开户取对应代理商问题 sunxin
        IDataset userTradeInfos = TradeUserInfoQry.getTradeUserByTradeId(strTradeId);
        if (IDataUtil.isNotEmpty(userTradeInfos))
        {
            agentId = userTradeInfos.getData(0).getString("DEVELOP_DEPART_ID");
        }

        // 影号完工不占用资源
        String strOrderId = mainTrade.getString("ORDER_ID");
        IData orderInfo = UOrderInfoQry.qryOrderByOrderId(strOrderId, mainTrade.getString("EPARCHY_CODE"));
        if ("375".equals(orderInfo.getString("ORDER_TYPE_CODE")))// 影号业务受理ORDER_TYPE_CODE = 375
        {
            return;
        }
        // 获取业务台帐资源子表
        IDataset tradeResInfos = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(strTradeId, BofConst.MODIFY_TAG_ADD);

        if (IDataUtil.isEmpty(tradeResInfos))
        {
            return;
        }

        String imsi = "";
        String sim_card_no = "";
        for (int j = 0; j < tradeResInfos.size(); j++)
        {
            if ("1".equals(tradeResInfos.getData(j).getString("RES_TYPE_CODE")))
            {
                imsi = tradeResInfos.getData(j).getString("IMSI");
                sim_card_no = tradeResInfos.getData(j).getString("RES_CODE");
                break;
            }
        }

        for (int i = 0; i < tradeResInfos.size(); i++)
        {
            String strResTypeCode = tradeResInfos.getData(i).getString("RES_TYPE_CODE");
            String strResCode = tradeResInfos.getData(i).getString("RES_CODE");
            String rsrvStr5 = tradeResInfos.getData(i).getString("RSRV_STR5");
            
            
            String x_check_tag = "0";
            //3822:无线固话批量预开买断 add by yangxd3
            if ("500".equals(orderInfo.getString("ORDER_TYPE_CODE")) || "700".equals(orderInfo.getString("ORDER_TYPE_CODE")) || "3822".equals(orderInfo.getString("ORDER_TYPE_CODE")))
                x_check_tag = "1";

            // 号码占用
            if ("01".equals(rsrvStr5) && "0".equals(strResTypeCode))
            {
                ResCall.resPossessForIOTMphone("0", "0", sim_card_no, imsi, strResCode, strResTypeCode, productId);
            }
            // --->和多号业务需求的改造:
            /**
             * REQ201711060015关于BOSS侧进行和多号业务受理支撑系统改造的通知
             * @author zhuoyingzhi
             * @date 20180105
             */
            else if ("MOSP".equals(brand_code) && "0".equals(strResTypeCode)){
            	String msisdn_type =tradeResInfos.getData(i).getString("RSRV_STR2");
            	//ResCall.resPossessForMphone("0", "0", sim_card_no, imsi, strResCode, strResTypeCode, productId,msisdn_type);
            	ResCall.resPossessForMphone(sim_card_no, imsi, strResCode, productId, strTradeId, brandCode, x_check_tag,msisdn_type);
            }
            else if ("0".equals(strResTypeCode) && !"01".equals(rsrvStr5))
            {
                ResCall.resPossessForMphone(sim_card_no, imsi, strResCode, productId, strTradeId, brandCode, x_check_tag);
            }
            else if ("01".equals(rsrvStr5) && "1".equals(strResTypeCode))
            {
                ResCall.resPossessForIOTSim("0", "0", serailNumber, strResCode, strResTypeCode,x_check_tag, strTradeId, userId);
            }

            else if ("1".equals(strResTypeCode) && !"01".equals(rsrvStr5))
            {
                ResCall.resPossessForSimAgent("0", serailNumber, strResCode, x_check_tag, strTradeId, userId, advanceFee, rsrvStr7, productId, tradeTypeCode, agentId);
            }

            else if ("L".equals(strResTypeCode))
            {
                ResCall.resPossessForCmnet(strResTypeCode, strResCode);
            }
        }
    }

}
