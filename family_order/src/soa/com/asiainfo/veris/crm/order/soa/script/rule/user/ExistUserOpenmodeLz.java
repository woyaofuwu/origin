
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistUserOpenmodeLz extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistUserOpenmodeLz.class);

    /**
     * 判断用户开户方式
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistUserOpenmodeLz() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listUser = databus.getDataset("TF_F_USER");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listUser.iterator(); iter.hasNext();)
        {
            IData user = (IData) iter.next();

            if ("5".equals(user.getString("OPEN_MODE")) || "6".equals(user.getString("OPEN_MODE")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistUserOpenmodeLz() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
