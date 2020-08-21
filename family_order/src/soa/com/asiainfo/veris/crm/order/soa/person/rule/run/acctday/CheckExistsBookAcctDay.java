
package com.asiainfo.veris.crm.order.soa.person.rule.run.acctday;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * 校验是否存在预约账期
 * 
 * @author liutt
 * @date 2014-05-08
 */
public class CheckExistsBookAcctDay extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * 【普通付费关系变更】中使用
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 查询号码时校验
        {
            // databus 中含有ACCT_DAY、FIRST_DATE、如果存有与预期账期的话还有 NEXT_ACCT_DAY、NEXT_FIRST_DATE
            String acctDay = databus.getString("ACCT_DAY");
            String nextAcctDay = databus.getString("NEXT_ACCT_DAY");// 有值就代表有预约账期
            if (StringUtils.isNotBlank(acctDay) && StringUtils.isNotBlank(nextAcctDay) && !StringUtils.equals(acctDay, nextAcctDay))// 存在预约账期且新老账期日不一致
            {
                return true;
            }
        }
        return false;
    }

}
