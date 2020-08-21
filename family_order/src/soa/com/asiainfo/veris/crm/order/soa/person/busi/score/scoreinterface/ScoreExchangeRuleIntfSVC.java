
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.RuleException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;

public class ScoreExchangeRuleIntfSVC extends CSBizService
{
    public IData queryScoreExchagneRuleByRuleId(IData inparam) throws Exception
    {
        IDataset ruleDatas = ExchangeRuleInfoQry.queryExchRuleByRuleId(inparam.getString("RULE_ID"));
        if (IDataUtil.isEmpty(ruleDatas))
        {
            CSAppException.apperr(RuleException.CRM_RULE_36);
        }
        return ruleDatas.getData(0);
    }

    public IDataset queryScoreExchangeRules(IData inparam) throws Exception
    {
        IData userInfo = UcaInfoQry.qryUserInfoBySn(inparam.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");
        // 获取客户可用积分余额
        String score = "";
        IDataset scoreInfo = AcctCall.queryUserScore(userId);
        if (IDataUtil.isNotEmpty(scoreInfo))
        {
            score = scoreInfo.getData(0).getString("SUM_SCORE");
        }
        else
        {
            // 获取用户积分无数据!
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
        String brandCode = productInfo.getString("BRAND_CODE");
        IDataset ruleInfos = ExchangeRuleInfoQry.queryExRuleByScore(score, userInfo.getString("EPARCHY_CODE"), brandCode);
        return ruleInfos;
    }
}
