
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:此用户已做产品变更，请重新刷新VPMN界面后办理VPMN！！【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckRefreshByProductVpmnRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckRefreshByProductVpmnRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckRefreshByProductVpmnRule() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        /* 获取业务台账，用户资料信息 */
        IDataset listUser = databus.getDataset("TF_F_USER");
        String strProductId = databus.getString("PRODUCT_ID");
        if (IDataUtil.isNotEmpty(listUser))
        {
            for (Iterator iter = listUser.iterator(); iter.hasNext();)
            {
                IData userData = (IData) iter.next();
                if (userData.getString("REMOVE_TAG").equals("0") && !userData.getString("PRODUCT_ID").equals(strProductId))
                {
                    bResult = true;
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckRefreshByProductVpmnRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
