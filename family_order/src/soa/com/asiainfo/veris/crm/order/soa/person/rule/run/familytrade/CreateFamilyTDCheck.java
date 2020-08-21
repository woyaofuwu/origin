
package com.asiainfo.veris.crm.order.soa.person.rule.run.familytrade;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * @author zhouwu
 * @description 亲亲网无线固话校验：TD二代无线固话号码不能办理亲亲网业务
 * @version 1.0
 * @data 2014-05-27 15:29:27
 */
public class CreateFamilyTDCheck extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        // 查询号码时校验
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            String netTypeCode = databus.getString("NET_TYPE_CODE");
            if (StringUtils.isNotBlank(netTypeCode) && "18".equals(netTypeCode))
            {
                return true;
            }
        }
        return false;
    }

}
