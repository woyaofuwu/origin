
package com.asiainfo.veris.crm.order.soa.script.rule.svc;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class ExistsServiceIdInTradeSvc extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsServiceIdInTradeSvc.class);

    /**
     * 判断台账是否操作了某些服务
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsServiceIdInTradeSvc() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strSvcId = ruleParam.getString(databus, "SERVICE_ID");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeSvc.iterator(); iter.hasNext();)
        {
            IData tradeSvc = (IData) iter.next();

            if (strSvcId.indexOf("|" + tradeSvc.getString("SERVICE_ID") + "|") > -1 && ("*".equals(strModifyTag) || strModifyTag.equals(tradeSvc.getString("MODIFY_TAG"))))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201643, "#业务后特殊限制表判断：无权限增加或删除短信服务！");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsServiceIdInTradeSvc() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
