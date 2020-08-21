
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.score;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScorePlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScorePlanInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeScorePlanAction.java
 * @Description: 产品变更积分计划变更
 * @version: v1.0.0
 * @author: maoke
 * @date: Jul 23, 2014 5:22:19 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jul 23, 2014 maoke v1.0.0 修改原因
 */
public class ChangeScorePlanAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        String userId = uca.getUserId();

        List<ProductTradeData> productTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        if (productTradeDatas != null && productTradeDatas.size() > 0)// 有主产品变更
        {
            // 查询前品牌积分计划
            IDataset ids = ScorePlanInfoQry.queryScorePlanInfoByUserId(userId, "10A");

            if (IDataUtil.isNotEmpty(ids))
            {
                for (int i = 0; i < ids.size(); i++)
                {
                    // 终止前品牌积分计划
                    ScorePlanTradeData endScorePlanTD = new ScorePlanTradeData(ids.getData(i));

                    endScorePlanTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    endScorePlanTD.setEndDate(btd.getRD().getAcceptTime());
                    endScorePlanTD.setStatus("10E");

                    btd.add(uca.getSerialNumber(), endScorePlanTD);
                }

            }

            // 查询用户积分账户
            IDataset scoreAcctInfo = ScoreAcctInfoQry.queryScoreAcctInfoByUserId(userId, "10A", uca.getUser().getEparchyCode());
            if (IDataUtil.isNotEmpty(scoreAcctInfo))
            {
                // TF_B_TRADE_INTEGRALPLAN 默认订购一个积分计划
                ScorePlanTradeData scorePlanTradeData = new ScorePlanTradeData();

                scorePlanTradeData.setUserId(userId);
                scorePlanTradeData.setStartDate(btd.getRD().getAcceptTime());
                scorePlanTradeData.setEndDate(SysDateMgr.getTheLastTime());
                scorePlanTradeData.setStatus("10A");
                scorePlanTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                scorePlanTradeData.setIntegralAcctId(scoreAcctInfo.getData(0).getString("INTEGRAL_ACCT_ID"));
                scorePlanTradeData.setIntegralPlanId("-1");// 积分计划标识 -1表示默认
                scorePlanTradeData.setIntegralPlanInstId(SeqMgr.getInstId());

                btd.add(uca.getSerialNumber(), scorePlanTradeData);
            }
        }
    }
}
