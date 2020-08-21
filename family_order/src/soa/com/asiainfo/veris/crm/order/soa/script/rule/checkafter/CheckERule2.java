
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
 * @Description: 该随e行号码已经与其他手机绑定,不能再次办理
 * @author: xiaocl
 */
public class CheckERule2 extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckERule2.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckERule2() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String strRsrvStr6 = databus.getString("RSRV_STR6", "");
        /* 获取规则配置信息 */
        String strShortCode = ruleParam.getString(databus, "SHORT_CODE");
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        if ("".equals(strRelationTypeCode) || "".equals(strShortCode) || "".equals(strRsrvStr6))
            return false;
        /* 获取业务台账，用户资料信息 */
        IDataset listUserRelationUu = databus.getDataset("TF_F_RELATION_UU");
        if (IDataUtil.isEmpty(listUserRelationUu))
            return false;
        /* 开始逻辑规则校验 */
        for (Iterator iter = listUserRelationUu.iterator(); iter.hasNext();)
        {
            IData userRelationUu = (IData) iter.next();
            if (strRelationTypeCode.equals(userRelationUu.getString("RELATION_TYPE_CODE")) && strShortCode.equals(userRelationUu.getString("SHORT_CODE")) && strRsrvStr6.equals(userRelationUu.getString("USER_ID_B")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckERule2() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
