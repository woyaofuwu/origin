
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.ScoreFactory;

public class UndoGoodsAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset scoreDs = TradeScoreInfoQry.queryTradeScoreJoinExchangeRule(tradeId);

        if (IDataUtil.isEmpty(scoreDs))
            return;
        int size = scoreDs.size();
        for (int i = 0; i < size; i++)
        {
            IData scoreData = scoreDs.getData(i);
            if ((ScoreFactory.EXCHANGE_TYPE_REWARD.equals(scoreData.getString("RSRV_STR1"))) && (StringUtils.isNotBlank(scoreData.getString("RES_ID"))))
            {
                try
                {
                    ResCall.occupyReleaseGoods(scoreData.getString("RES_ID"), scoreData.getString("ACTION_COUNT"), tradeId);
                }
                catch (Exception e)
                {
                    // 调用资源更新接口出错！
                    CSAppException.apperr(CrmUserException.CRM_USER_867);
                }
            }
        }

    }

}
