
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * 校验是否是宽带用户
 * 
 * @author likai3
 */
public class CheckBroadbandSerialNumber extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 查询号码时校验
        {
            String netTypeCode = databus.getString("NET_TYPE_CODE", "00");

            if (!StringUtils.equals("11", netTypeCode))
            {
                return true;
            }

        }
        return false;
    }
}
