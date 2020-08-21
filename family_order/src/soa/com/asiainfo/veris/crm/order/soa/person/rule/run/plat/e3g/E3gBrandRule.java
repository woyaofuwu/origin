
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.e3g;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class E3gBrandRule extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        if ("G005".equals(uca.getBrandCode()))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0982_1.toString(), PlatException.CRM_PLAT_0982_1.getValue());
            return false;
        }

        // 只有 动感地带 神州行 全球通能办理
        if ("G001-G002-G010".indexOf(uca.getBrandCode()) < 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0982_1.toString(), PlatException.CRM_PLAT_0982_1.getValue());
            return false;
        }

        return true;
    }

}
