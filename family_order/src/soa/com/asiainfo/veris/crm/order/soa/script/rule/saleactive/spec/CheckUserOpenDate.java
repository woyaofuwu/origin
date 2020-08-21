
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 判断入网时间的规则，入网小于多少天 td_bre_parameter 传入 DAYS 365 目前都是365天 ---------------------------------------------如下特殊产品走该规则
 * product_id = 69900803，69900800，69900804
 * 
 * @author Mr.Z
 */
public class CheckUserOpenDate extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -6297900872898350305L;

    private static Logger logger = Logger.getLogger(CheckUserOpenDate.class);

    public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckUserOpenDate() >>>>>>>>>>>>>>>>>>");
        }

        String openDate = databus.getString("OPEN_DATE");
        String days = ruleparam.getString(databus, "DAYS");
        String sysDate = SysDateMgr.getSysTime();

        String tempDate = SysDateMgr.addDays(openDate, Integer.parseInt(days));

        if (tempDate.compareTo(sysDate) > 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20047, "用户在网未满" + days + "天，不能继续办理！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckUserOpenDate() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
