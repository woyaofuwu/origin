
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * 校验营销包是否可以通过短信验证码方式办理！
 * 
 * @author Mr.Z
 */
public class CheckActiveTradeBySms extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 3925485728584999453L;

    public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
    {

        return false;
    }

}
