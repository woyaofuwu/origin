
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class ChkVpmnShortCodeRule extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkVpmnShortCodeRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkVpmnShortCodeRule() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        IData data = new DataMap();
        data.put("SHORT_CODE", databus.getString("SHORT_CODE"));
        data.put("USER_ID_A", databus.getString("USER_ID"));
        data.put("EPARCHY_CODE", databus.getString("MEB_EPARCHY_CODE", Route.getCrmDefaultDb()));
        IData reData = VpnUnit.shortCodeValidateVpn(data);
        if (IDataUtil.isNotEmpty(reData))
        {
            if ("false".equals(reData.getString("RESULT")))
            {
                String errMessage = reData.getString("ERROR_MESSAGE");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, errMessage);
                bResult = false;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChkVpmnShortCodeRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
