
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.IntegralPlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScorePlanInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.requestdata.IntegralPlanAddReqData;

public class IntegralPlanAddTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        createTradeIntegralPlan(bd);

    }

    public void createTradeIntegralPlan(BusiTradeData bd) throws Exception
    {

        IntegralPlanAddReqData reqData = (IntegralPlanAddReqData) bd.getRD();
        UcaData uca = reqData.getUca();

        IDataset acctinfos = ScoreAcctInfoQry.queryScoreAcctInfoByUserId(reqData.getUca().getUserId(), "10A", getUserEparchyCode());

        IDataset planinfo = ScorePlanInfoQry.queryScorePlanInfoByUserId(reqData.getUca().getUserId(), "10A");

        if (IDataUtil.isEmpty(acctinfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户积分账户资料无数据！");
        }
        else
        {
            IData scoreAcct = acctinfos.getData(0);

            IntegralPlanTradeData integralPlanTDnew = new IntegralPlanTradeData();

            integralPlanTDnew.setIntegralPlanInstId(SeqMgr.getInstId(getUserEparchyCode()));
            integralPlanTDnew.setIntegralAcctId(scoreAcct.getString("INTEGRAL_ACCT_ID"));
            integralPlanTDnew.setIntegralPlanId(reqData.getIntegralPlanId());
            integralPlanTDnew.setUserId(uca.getUserId());
            integralPlanTDnew.setAcctId(uca.getAcctId());
            integralPlanTDnew.setCustId(uca.getCustId());
            integralPlanTDnew.setStartDate(reqData.getStartDate());
            integralPlanTDnew.setEndDate(reqData.getEndDate() + SysDateMgr.END_DATE);
            integralPlanTDnew.setModifyTag(BofConst.MODIFY_TAG_ADD);
            integralPlanTDnew.setStatus("10A");
            integralPlanTDnew.setRemark("积分计划订购");
            bd.add(uca.getSerialNumber(), integralPlanTDnew);

            if (IDataUtil.isNotEmpty(planinfo))
            {
                IntegralPlanTradeData integralPlanTDold = new IntegralPlanTradeData(planinfo.getData(0));
                integralPlanTDold.setModifyTag(BofConst.MODIFY_TAG_UPD);
                integralPlanTDold.setEndDate(SysDateMgr.addDays(reqData.getStartDate(), -1) + SysDateMgr.END_DATE);
                integralPlanTDold.setStatus("10E");

                bd.add(uca.getSerialNumber(), integralPlanTDold);
            }
        }

    }

}
