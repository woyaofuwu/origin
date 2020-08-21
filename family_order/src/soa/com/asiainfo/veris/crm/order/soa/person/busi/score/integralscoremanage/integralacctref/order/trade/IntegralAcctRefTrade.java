
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.integralacctref.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.integralacctref.order.requestdata.IntegralAcctRefRequestData;

public class IntegralAcctRefTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        IntegralAcctRefRequestData reqData = (IntegralAcctRefRequestData) btd.getRD();
        String refAcctId = reqData.getRefAcctId();
        String userId = reqData.getUca().getUserId();
        IDataset integralAcctInfo = ScoreAcctInfoQry.queryScoreAcctInfoByUserId(userId, "10A", Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(integralAcctInfo))
        {
            CSAppException.apperr(ScoreException.CRM_SCORE_4);
        }

        // 失效积分账户
        saveByPk(refAcctId);

        // 主台账
        MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(SysDateMgr.getSysTime());
        mainList.setRsrvStr2(refAcctId);
        mainList.setRsrvStr3(integralAcctInfo.getData(0).getString("INTEGRAL_ACCT_ID"));
    }

    public void saveByPk(String refAcctId) throws Exception
    {
        IDataset refIntegralAcctInfo = ScoreAcctInfoQry.queryScoreAcctInfoByIntegralAcctId(refAcctId);
        if (IDataUtil.isEmpty(refIntegralAcctInfo))
        {
            CSAppException.apperr(ScoreException.CRM_SCORE_4);
        }
        int size = refIntegralAcctInfo.size();
        for (int i = 0; i < size; i++)
        {
            IData param = new DataMap();
            param.put("END_DATE", SysDateMgr.getSysTime());
            param.put("STATUS", "10E");
            param.put("INTEGRAL_ACCT_ID", refAcctId);
            Dao.executeUpdateByCodeCode("TF_F_INTEGRAL_ACCT", "DEL_FINISH", param,Route.CONN_CRM_CEN);
        }
    }
}
