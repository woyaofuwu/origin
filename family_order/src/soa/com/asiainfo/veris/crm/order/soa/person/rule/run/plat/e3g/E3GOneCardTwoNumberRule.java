
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.e3g;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * 一卡双号的副号不能办理E3g
 * 
 * @author bobo
 */
public class E3GOneCardTwoNumberRule extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);

        IDataset relations = RelaUUInfoQry.getUUInfoByUserIdAB("", uca.getUserId(), "30", "");

        if (relations != null && relations.size() > 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0982_1.toString(), PlatException.CRM_PLAT_0982_1.getValue());
            return false;
        }
        else
        {
            return true;
        }

    }

}
