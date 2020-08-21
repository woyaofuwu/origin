
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;

public class OperCodeTrans
{

    public static void operCodeTrans(IData param)
    {
        String opercode = param.getString("OPER_CODE");
        if ("01".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_ORDER);
        }
        else if ("02".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
        }
        else if ("11".equals(opercode))
        {
            // WLAN的套餐退订不做操作码转换
            if (!PlatConstants.PLAT_WLAN.equals(param.getString("BIZ_TYPE_CODE")) && !PlatConstants.PLAT_WLAN_EDU.equals(param.getString("BIZ_TYPE_CODE")) && !"WL".equals(param.getString("BIZ_TYPE_CODE")))
            {
                param.put("OPER_CODE", PlatConstants.OPER_ORDER);
            }
        }
        else if ("14".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_PAUSE);
        }
        else if ("15".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_RESTORE);
        }
        else if ("18".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_SERVICE_OPEN);
        }
        else if ("19".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_SERVICE_CLOSE);
        }
        else if ("18".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_SERVICE_OPEN);
        }
        else if ("22".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_ORDER_PLAY);
        }
        else if ("16".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_ADD_MONEY);
        }
        else if ("12".equals(opercode))
        {
            // WLAN的套餐变更不做操作码转换
            if (!PlatConstants.PLAT_WLAN.equals(param.getString("BIZ_TYPE_CODE")) && !PlatConstants.PLAT_WLAN_EDU.equals(param.getString("BIZ_TYPE_CODE")) && !"WL".equals(param.getString("BIZ_TYPE_CODE")))
            {
                param.put("OPER_CODE", PlatConstants.OPER_BOOKING);
            }
        }
        else if ("13".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_CANCEL_BOOKING);
        }
        else if ("33".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_LOSE);
        }
        else if ("34".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_UNLOSE);
        }
        else if ("6".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_ORDER_TC);
        }
        else if ("7".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_CANCEL_TC);
        }
        else if ("09".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_RESET);
        }
        else if ("23".equals(opercode))
        {
            param.put("OPER_CODE", PlatConstants.OPER_CHANGE_TC);
        }
        else if ("21".equals(opercode))
        {
            // 游戏平台充值
            param.put("OPER_CODE", PlatConstants.OPER_ADD_MONEY);
        }
        else if ("25".equals(opercode))
        {
            // 游戏平台点播
            param.put("OPER_CODE", PlatConstants.OPER_ORDER_PLAY);
        }
    }
}
