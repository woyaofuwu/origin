
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.fetion;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 飞信大包月规则 飞信大包月业务在业务延续期内不能退订
 * 
 * @author xiekl
 */
public class FetionBigMonthlyRule extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        PlatOfficeData officeData = psd.getOfficeData();

        // 大包月不允许退订
        if (PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode()) && "901508FXHY03_901508FXHY06_901508FXHY12".indexOf(officeData.getSpCode() + officeData.getBizCode()) > -1)
        {

            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_2998.toString(), PlatException.CRM_PLAT_2998.getValue());

            return false;
        }
        return true;
    }

}
