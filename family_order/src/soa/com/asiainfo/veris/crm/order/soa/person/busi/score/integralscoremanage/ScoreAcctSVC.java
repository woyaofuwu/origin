
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScorePlanInfoQry;

public class ScoreAcctSVC extends CSBizService
{

    public IData getAcctInfo(IData input) throws Exception
    {

        IDataset acctinfo = ScoreAcctInfoQry.queryScoreAcctInfoByUserId(input.getString("USER_ID"), "10A", getUserEparchyCode());

        if (IDataUtil.isEmpty(acctinfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户积分账户资料无数据！");
        }

        return acctinfo.getData(0);
    }

    public IData getIntegralPlan(IData input) throws Exception
    {

        String userId = input.getString("USER_ID");

        IDataset planinfo = ScorePlanInfoQry.queryScorePlanInfoByUserId(userId, "10A");

        IData integralPlan = new DataMap();
        if (IDataUtil.isNotEmpty(planinfo))
        {
            integralPlan = planinfo.getData(0);
        }
        return integralPlan;
    }

}
