
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.bdyj;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.PlatReload;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 病毒预警必选开通手机邮箱服务
 * 
 * @author xiekl 提交后校验
 */
public class MustOpenMail139Rule implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);

        List<PlatSvcTradeData> relaPstds = new ArrayList<PlatSvcTradeData>();
        relaPstds.addAll(uca.getUserPlatSvcByServiceId(PlatReload.mail139Free));
        relaPstds.addAll(uca.getUserPlatSvcByServiceId(PlatReload.mail139Vip));
        relaPstds.addAll(uca.getUserPlatSvcByServiceId(PlatReload.mail139Standard));
        relaPstds.addAll(uca.getUserPlatSvcByServiceId(PlatReload.mail139Vip));

        if (relaPstds == null || relaPstds.isEmpty())
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_908.toString(), PlatException.CRM_PLAT_908.getValue());

            return false;
        }
        else
        {
            for (int i = 0; i < relaPstds.size(); i++)
            {
                PlatSvcTradeData mailPstd = relaPstds.get(i);
                if (!BofConst.MODIFY_TAG_DEL.equals(mailPstd.getModifyTag()))
                {
                    return true;
                }
            }
        }

        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_908.toString(), PlatException.CRM_PLAT_908.getValue());

        return false;
    }

}
