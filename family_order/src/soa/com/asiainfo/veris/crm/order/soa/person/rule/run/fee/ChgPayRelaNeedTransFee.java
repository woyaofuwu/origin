
package com.asiainfo.veris.crm.order.soa.person.rule.run.fee;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;

/**
 * 普通付费关系变更是否需要转移费用
 * 
 * @author Administrator
 */
public class ChgPayRelaNeedTransFee extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * 独立账户，且账户中存在预存费用
     */
    public boolean run(IData databus, BreRuleParam dataParam) throws Exception
    {
        // String xChoiceTag = databus.getString("X_CHOICE_TAG");
        // if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 提交时校验
        // {

        String acctId = databus.getString("ACCT_ID");
        String isAcctOnlyFlag = PayRelaInfoQry.isAcctOnly(acctId, "1", "1");// 检查是否独立账户
        if (StringUtils.equals("true", isAcctOnlyFlag))// 独立账户
        {
            String userId = databus.getString("USER_ID");
            IData feeData = AcctCall.getOweFeeByUserId(userId);// 查询是否存在预存费用
            long balance = feeData.getLong("ACCT_BALANCE", 0);
            if (balance > 0)
            {
                return true;// 本业务会将用户预存话费转入新帐户中，是否继续？选择[确定]继续办理本业务，[取消]退出本业务？
            }

        }
        // }
        return false;
    }

}
