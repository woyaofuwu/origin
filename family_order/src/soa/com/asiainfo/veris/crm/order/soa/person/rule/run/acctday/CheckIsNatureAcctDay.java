
package com.asiainfo.veris.crm.order.soa.person.rule.run.acctday;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * 校验是否是自然结账日
 * 
 * @author chenzm
 * @date 2014-05-23
 */
public class CheckIsNatureAcctDay extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 分散用户判断
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 查询号码时校验
        {
            String acctDay = databus.getString("ACCT_DAY");

            if (StringUtils.isNotBlank(acctDay) && !"1".equals(acctDay))// 分散用户
            {
                return true;
            }
        }
        return false;
    }

}
