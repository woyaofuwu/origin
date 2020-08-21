package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporder.TpOrderCreate;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

import java.util.List;
import java.util.Map;

/**
 * 记录甩单工单跟立即销户的关系
 */
public class DestroyUserTpRelAction implements ITradeAction {
    @Override
    public void executeAction(BusiTradeData btd) throws Exception {
        TpOrderCreate tpOrderCreate = new TpOrderCreate();
        //获取立即销户的OrderId
        String orderId = btd.getRD().getOrderId();

        //获取甩单工单
         Map<String,List> tradeDatasMap = btd.getTradeDatasMap();
         List orderIds = tradeDatasMap.get("TP_ORDER_IDS");
         if(DataUtils.isNotEmpty(orderIds)){
             for (int i = 0 ;i < orderIds.size();i++){
                 String tpOrderId = (String) orderIds.get(i);
                 tpOrderCreate.insertOrderRel(orderId,TpConsts.OrderABType.orderId,tpOrderId,TpConsts.OrderABType.tpOrder,TpConsts.relType.TPORDER_WITH_TRADE);
             }
         }
    }
}
