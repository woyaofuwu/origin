
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * @Description: 宽带用户服务状态不是【开通】状态，不能办理此业务!
 * @version: v1.0.0
 * @author: likai3
 */
public class CheckWidenetUserStateCodeset extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// auth组建调用的时候校验
        {
            if (!StringUtils.equals("0", databus.getString("USER_STATE_CODESET")))
            {
                return true;
            }
        }
        return false;
    }
}
