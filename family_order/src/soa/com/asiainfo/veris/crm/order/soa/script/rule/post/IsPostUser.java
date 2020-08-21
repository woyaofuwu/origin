
package com.asiainfo.veris.crm.order.soa.script.rule.post;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class IsPostUser extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsPostUser.class);

    /**
     * 判断用户邮寄信息
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsPostUser() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strPostTag = ruleParam.getString(databus, "POST_TAG");

        /* 获取业务台账，用户资料信息 */
        IDataset listUserPostInfo = databus.getDataset("TF_F_POSTINFO");

        /* 开始逻辑规则校验 */
        int iCountUserPostInfo = listUserPostInfo.size();
        for (int idx = 0; idx < iCountUserPostInfo; idx++)
        {
            if (strPostTag.equals(listUserPostInfo.getData(0).getString("POST_TAG")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsPostUser() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
