
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * @CREATED 备卡激活返销接口
 */
public class UndoSimBakActAction implements ITradeFinishAction
{
    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String oldSimCardNo = "";
        String newSimCardNo = "";
        String trade_id = mainTrade.getString("TRADE_ID");
        if(StringUtils.isNotEmpty(trade_id)){
            IDataset tradeResInfos = TradeResInfoQry.queryAllTradeResByTradeId(trade_id);
            if(IDataUtil.isNotEmpty(tradeResInfos)){
                for(int i =0;i<tradeResInfos.size();i++){
                    IData resInfo = tradeResInfos.getData(i);
                    if(StringUtils.equals("1", resInfo.getString("MODIFY_TAG"))){
                        oldSimCardNo = resInfo.getString("RES_CODE");
                    }
                    if(StringUtils.equals("0", resInfo.getString("MODIFY_TAG"))){
                        newSimCardNo = resInfo.getString("RES_CODE");
                    }
                }
            }
            
        }
        if(StringUtils.isNotEmpty(oldSimCardNo) && StringUtils.isNotEmpty(newSimCardNo)){
            ResCall.backupCardReturnSale(oldSimCardNo, newSimCardNo);
        }

    }

}
