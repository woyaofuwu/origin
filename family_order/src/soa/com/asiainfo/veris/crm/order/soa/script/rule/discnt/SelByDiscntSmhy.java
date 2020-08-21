
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class SelByDiscntSmhy extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(SelByDiscntSmhy.class);

    /**
     * 判断用户是否有XXXX优惠
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SelByDiscntSmhy() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listCommpara = BreQryForCommparaOrTag.getCommparaPara3("CSM", 3218, databus.getString("EPARCHY_CODE"));

        /* 开始逻辑规则校验 */
        if (listCommpara.size() > 0)
        {
            IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");

            for (Iterator iter = listUserDiscnt.iterator(); iter.hasNext();)
            {
                IData userdiscnt = (IData) iter.next();

                for (Iterator iterator = listCommpara.iterator(); iterator.hasNext();)
                {
                    IData commpara = (IData) iterator.next();

                    if (userdiscnt.getString("DISCNT_CODE").equals(commpara.getString("PARA_CODE3")))
                    {
                        bResult = true;
                        break;
                    }
                }

                if (bResult)
                {
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SelByDiscntSmhy() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
