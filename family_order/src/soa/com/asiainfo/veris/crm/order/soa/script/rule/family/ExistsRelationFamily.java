
package com.asiainfo.veris.crm.order.soa.script.rule.family;

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
 * @Description: 改用户存在家庭成员，不能删除家庭网功能套餐费(不能销户)，请先注销家庭成员！
 * @author: xiaocl
 */
/*
 * SELECT count(1) recordcount FROM tf_b_trade_discnt a, tf_f_user_discnt b, tf_f_relation_uu c WHERE a.trade_id =
 * TO_NUMBER(:TRADE_ID) AND a.accept_month = TO_NUMBER(:ACCEPT_MONTH) AND a.discnt_code = :DISCNT_CODE AND (a.modify_tag
 * = :MODIFY_TAG OR :MODIFY_TAG = '*') AND a.user_id = b.user_id AND b.partition_id = mod(a.user_id, 10000) AND
 * a.discnt_code = :DISCNT_CODE AND b.relation_type_code = :RELATION_TYPE_CODE AND b.user_id_a = c.user_id_a AND
 * (c.role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*') AND b.end_date > sysdate AND c.end_date > add_months(sysdate, 1)
 */
public class ExistsRelationFamily extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsRelationFamily.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsRelationFamily() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        boolean bExistsOne = false; // 设置第一逻辑点
        boolean bExistsTwo = false; // 设置第二逻辑点

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strRoleCode = ruleParam.getString(databus, "ROLE_CODE_B");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");
        IDataset listUserRelation = databus.getDataset("TF_F_RELATION_UU");

        // 如果没有存在删除家庭套餐优惠的台账，且用户资料存在有效的家庭套餐优惠，直接返回。无须继续校验。
        for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
        {
            IData tradeDiscnt = (IData) iter.next();
            for (Iterator iterUserDiscnt = listUserDiscnt.iterator(); iterUserDiscnt.hasNext();)
            {
                IData userDiscnt = (IData) iterUserDiscnt.next();
                if (tradeDiscnt.getString("USER_ID").equals(userDiscnt.getString("USER_ID")) && tradeDiscnt.getString("DISCNT_CODE").equals(strDiscntCode)
                        && (tradeDiscnt.getString("MODIFY_TAG").equals("*") || tradeDiscnt.getString("MODIFY_TAG").equals(strModifyTag)) && userDiscnt.getString("RELATION_TYPE_CODE", "").equals(strRelationTypeCode))
                {
                    // 判断此用户是否属于亲情用户
                    for (Iterator iterUserRelation = listUserRelation.iterator(); iterUserRelation.hasNext();)
                    {
                        IData userRelation = (IData) iterUserRelation.next();
                        if ((userDiscnt.getString("USER_ID").equals(userRelation.getString("USER_ID_A")) && (userRelation.getString("ROLE_CODE_B").equals(strRoleCode) || userRelation.getString("ROLE_CODE_B").equals("*"))))
                        {
                            bExistsTwo = true;
                            break;
                        }
                    }
                    if (bExistsTwo)
                    {
                        bExistsOne = true;
                        break;
                    }
                }
            }
            if (bExistsOne)
            {
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsRelationFamily() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
