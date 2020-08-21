
package com.asiainfo.veris.crm.order.soa.person.rule.run.acctday;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * 校验非自然月账期，提示用户是否需要继续办理
 * 
 * @author likai3
 * @date 2014-05-08
 */
public class CheckAcctDay extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String errorMsg = "";
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 查询号码时校验
        {
            // databus 中含有ACCT_DAY、FIRST_DATE、如果存有与预期账期的话还有 NEXT_ACCT_DAY、NEXT_FIRST_DATE
            String acctDay = databus.getString("ACCT_DAY");
            if (!StringUtils.equals("1", acctDay))
            {
                // errorMsg = "该用户是非自然月账期用户，办理该业务将变更为自然月用户！";
                // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, "-1", errorMsg);
                return true;
            }
        }
        return false;
    }

}
