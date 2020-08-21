
package com.asiainfo.veris.crm.order.soa.frame.bre.script;

import java.io.Serializable;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;

public interface IBREScript extends Serializable
{
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception;
}
