
package com.asiainfo.veris.crm.order.soa.person.common.action.finish.res;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.NoPhoneTradeUtil;

public class UndoResPossessStateAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        // 资源返销
        String strTradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        // 获取业务台帐资源子表
        IDataset tradeResInfos = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(strTradeId, BofConst.MODIFY_TAG_ADD);

        if (tradeResInfos.isEmpty())
        {
            return;
        }
        DataHelper.sort(tradeResInfos, "RES_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        for (int i = 0; i < tradeResInfos.size(); i++)
        {
            String strResTypeCode = tradeResInfos.getData(i).getString("RES_TYPE_CODE");
            String strResCode = tradeResInfos.getData(i).getString("RES_CODE");
            String rsrvStr5 = tradeResInfos.getData(i).getString("RSRV_STR5");
            // 号码占用
            if ("01".equals(rsrvStr5) && "0".equals(strResTypeCode))
            {
                ResCall.undoResPossessForIOTMphone(strResCode);
            }
            else if (!"01".equals(rsrvStr5) && "0".equals(strResTypeCode))
            {
                //无手机宽带开户没有sim卡，需要调用专门的资源接口去返销号码  modify_by_duhj 20200513               
                boolean isNophoneOpenWide = NoPhoneTradeUtil.checkNophoneWideCreate(strTradeId);
                if(isNophoneOpenWide){
                    ResCall.undoResPossessForNoPhone(strResCode);
                }else {
                    ResCall.undoResPossessForMphone(strResCode);
                }
                               
            }
            else if ("01".equals(rsrvStr5) && "1".equals(strResTypeCode))
            {
                ResCall.undoResPossessForIOTSim(strResCode);
            }
            else if (!"01".equals(rsrvStr5) && "1".equals(strResTypeCode))
            {
                ResCall.undoResPossessForSim(strResCode,tradeTypeCode);
            }
        }

    }

}
