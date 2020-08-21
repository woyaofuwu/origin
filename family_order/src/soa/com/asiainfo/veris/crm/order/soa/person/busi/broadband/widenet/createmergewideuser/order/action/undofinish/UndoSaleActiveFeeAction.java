
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.undofinish;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

/**
 * 废弃，暂时不用，CancelSaleActiveAction 中处理营销活动返销
 * @author zyc
 *
 */
public class UndoSaleActiveFeeAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        //宽带营销活动tradeID
        String saleActiveTradeId = mainTrade.getString("RSRV_STR5");
        
        //魔百和营销活动tradeID
        String topSetBoxSaleActiveTradeId = mainTrade.getString("RSRV_STR6");
        
        //如果办理的宽带营销活动
        if (StringUtils.isNotBlank(saleActiveTradeId))
        {
            //预存费用
            String saleActiveFee = mainTrade.getString("RSRV_STR7");
            
            //如果宽带营销活动存在预存费用
            if (StringUtils.isNotBlank(saleActiveFee) && !StringUtils.equals("0", saleActiveFee))
            {
                //宽带营销活动费用撤销
                AcctCall.recvFeeCancel(saleActiveTradeId);
            }
        }
        
        //如果办理的魔百和营销活动
        if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId))
        {
            //预存费用
            String saleActiveFee = mainTrade.getString("RSRV_STR7");
            
            //如果魔百和营销活动存在预存费用
            if (StringUtils.isNotBlank(saleActiveFee) && !StringUtils.equals("0", saleActiveFee))
            {
                //魔百和营销活动费用撤销
                AcctCall.recvFeeCancel(topSetBoxSaleActiveTradeId);
            }    
            
        }
    }

}
