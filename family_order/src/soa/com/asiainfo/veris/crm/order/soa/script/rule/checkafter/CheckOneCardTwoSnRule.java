
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 新用户是一卡双号副卡，不能办理该购机业务!【TradeCheckAfter】
 * @author: xiaocl
 */

/*
 * SELECT count(1) recordcount FROM tf_f_relation_uu WHERE partition_id = MOD(TO_NUMBER(:USER_ID_B),10000) AND user_id_b
 * = TO_NUMBER(:USER_ID_B) AND relation_type_code = :RELATION_TYPE_CODE AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B
 * = '*') AND end_date+0 > sysdate
 */
public class CheckOneCardTwoSnRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckOneCardTwoSnRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckOneCardTwoSnRule() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String strRsrvStr1 = databus.getString("RSRV_STR1", "");
        String strRsrvStr1Para = ruleParam.getString(databus, "RSRV_STR1");
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strRoleCodeB = ruleParam.getString(databus, "ROLE_CODE_B");
        IDataset listUserRelationUu = databus.getDataset("TF_F_RELATION_UU");
        if (strRsrvStr1.equals(strRsrvStr1Para))
        {
            for (Iterator iter = listUserRelationUu.iterator(); iter.hasNext();)
            {
                IData userRelationUu = (IData) iter.next();
                if (userRelationUu.getString("ROLE_COE_B").equals(strRoleCodeB) && userRelationUu.getString("RELATION_TYPE_CODE").equals(strRelationTypeCode))
                {
                    bResult = true;
                    break;
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckOneCardTwoSnRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }

}
