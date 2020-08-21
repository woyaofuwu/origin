
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.mcld;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class McldYearRule extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        if (PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode()))
        {
            String bizCode = psd.getOfficeData().getBizCode();
            if (bizCode.indexOf("BN") > 0)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0999_8.toString(), PlatException.CRM_PLAT_0999_8.getValue());
                return false;
            }
        }

        return true;
    }

}
