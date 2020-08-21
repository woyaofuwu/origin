
package com.asiainfo.veris.crm.order.soa.script.rule.post;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradePost extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradePost.class);

    /**
     * 判断用户该笔业务中的邮寄信息是否包涵某ＸＸＸ邮寄方式
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradePost() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strPostContent = ruleParam.getString(databus, "POST_CONTENT");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradePost = databus.getDataset("TF_B_TRADE_POST");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradePost.iterator(); iter.hasNext();)
        {
            IData tradePost = (IData) iter.next();

            if (("*".equals(strModifyTag) || strModifyTag.equals(tradePost.getString("MODIFY_TAG"))) && tradePost.getString("POST_CONTENT").indexOf(strPostContent) > -1)
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradePost() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
