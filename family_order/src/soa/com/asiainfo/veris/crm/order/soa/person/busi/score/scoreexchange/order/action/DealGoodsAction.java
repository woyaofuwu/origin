
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.action;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.score.ScoreFactory;

public class DealGoodsAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<ScoreTradeData> scoreTDList = btd.getTradeDatas(TradeTableEnum.TRADE_SCORE);
        BaseReqData req = btd.getRD();
        String tradeId = btd.getTradeId();
        ;
        String preType = req.getPreType();
        String isConFirm = req.getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
        {
            // 如果是预受理且不是二次回复确认受理，则不处理
            return;
        }

        for (ScoreTradeData scoreTD : scoreTDList)
        {
            if ((ScoreFactory.EXCHANGE_TYPE_REWARD.equals(scoreTD.getRsrvStr1())) && (StringUtils.isNotBlank(scoreTD.getResId())))
            {
                try
                {
                    ResCall.occupyGoods(CSBizBean.getTradeEparchyCode(), scoreTD.getResId(), scoreTD.getActionCount(), CSBizBean.getVisit().getDepartId(), tradeId);
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
