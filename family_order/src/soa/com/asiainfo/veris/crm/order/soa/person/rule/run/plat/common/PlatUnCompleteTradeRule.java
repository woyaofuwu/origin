
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.common;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 平台服务未完工工单判断
 * 
 * @author Administrator
 */
public class PlatUnCompleteTradeRule extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);

        if ("64".equals(psd.getOprSource()))
        {
            return true;
        }

        List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(psd.getElementId());
        if (pstds != null && pstds.size() > 0)
        {
            PlatSvcTradeData userPlatSvc = pstds.get(0);
            if (StringUtils.isNotEmpty(userPlatSvc.getOperCode()))
            {
                // 如果operCode有值表示老的资料是从台帐取的，因为资料表没有oper_code
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0998.toString(), PlatException.CRM_PLAT_0998.getValue());
                return false;
            }
        }
        return true;
    }

}
