
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.farm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * 用户未订购农信通百事易，不能定制内容服务
 * 
 * @author xiekl
 */
public class CustomizeFarmRule implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        String xTransCode = "SS.PlatSyncRinpRegSVC.tradeReg";
        if (xTransCode.equals(CSBizBean.getVisit().getXTransCode()))
        {
            UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);

            IDataset farmPlatSvcList = new DatasetList();
            farmPlatSvcList.add(uca.getUserPlatSvcByServiceId("40036001"));
            farmPlatSvcList.add(uca.getUserPlatSvcByServiceId("99954155"));

            if (farmPlatSvcList == null || farmPlatSvcList.isEmpty())
            {
                // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,
                // PlatException.CRM_PLAT_2998_10.toString(), PlatException.CRM_PLAT_2998_10.getValue());
                return false;
            }
        }

        return true;
    }

}
