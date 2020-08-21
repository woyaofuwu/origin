
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.mail139;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class Mail139PeActiveRule implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (!"SS.PlatRegSVC.PeActive139Mail".equals(CSBizBean.getVisit().getTransId()))
        {
            return true;
        }

        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);

        List<DiscntTradeData> discntList = uca.getUserDiscntByDiscntId("3302");
        if (discntList != null && !discntList.isEmpty())
        {
            DiscntTradeData discnt = discntList.get(0);
            // 如果优惠还没有生效，不能激活
            if (discnt.getStartDate().compareTo(SysDateMgr.getSysTime()) > 0)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "用户已经预约了139邮箱5元版优惠，未到开始时间不允许激活！");
                return false;
            }
        }

        return true;
    }

}
