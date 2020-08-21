
package com.asiainfo.veris.crm.order.soa.script.rule.ctt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class IsCttSerialNumber extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsCttSerialNumber.class);

    /**
     * @author huangsl
     * @deprecated 判断是不是固话或者铁通宽带的号码
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsCttSerialNumber() >>>>>>>>>>>>>>>>>>");
        boolean bResult = false;

        String netTypeCode = databus.getString("NET_TYPE_CODE", "00");
        String serialNumber = databus.getString("SERIAL_NUMBER");
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            if (!"11".equals(netTypeCode) && !"00".equals(netTypeCode))
            {
                bResult = true;
                databus.put("SERIAL_NUMBER", serialNumber);
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, -1, "业务受理前条件判断：服务号码[" + serialNumber + "]不是固话号码或宽带账号，不能办理此业务!");
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsCttSerialNumber() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
