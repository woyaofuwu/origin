
package com.asiainfo.veris.crm.order.soa.script.rule.right;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsNoRightPurchaseDiscnt extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsNoRightPurchaseDiscnt.class);

    /**
     * 判断员工是否有办理的营销包权限
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsNoRightPurchaseDiscnt() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strPackageId = databus.getString("RSRV_STR2");
        String orderTypeCode = databus.getString("ORDER_TYPE_CODE");

        /* 开始逻辑规则校验 */
        if ("240".equals(databus.getString("TRADE_TYPE_CODE")) && !"210".equals(orderTypeCode))
        {
            bResult = StaffPrivUtil.isPkgPriv(databus.getString("TRADE_STAFF_ID"), strPackageId);
        }
        else
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsNoRightPurchaseDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        bResult = !bResult;

        return bResult;
    }

}
