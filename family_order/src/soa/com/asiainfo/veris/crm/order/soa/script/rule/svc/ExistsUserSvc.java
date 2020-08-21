
package com.asiainfo.veris.crm.order.soa.script.rule.svc;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsUserSvc extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsUserSvc.class);

    /**
     * 判断用户是否有某个服务
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsUserSvc() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strSvcId = ruleParam.getString(databus, "SERVICE_ID");

        /* 获取业务台账，用户资料信息 */
        IDataset listUserSvc = databus.getDataset("TF_F_USER_SVC");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listUserSvc.iterator(); iter.hasNext();)
        {
            IData userSvc = (IData) iter.next();

            if (strSvcId.equals(userSvc.getString("SERVICE_ID")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsUserSvc() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
