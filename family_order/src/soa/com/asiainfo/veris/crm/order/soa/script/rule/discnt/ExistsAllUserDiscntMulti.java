
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 全球通1+1亲情回馈”用户需80元包月费以上的优惠 老系统SQL规则翻译代码
 * @author: xiaocl
 */
/*
 * SELECT SUM(recordnum) recordcount FROM (SELECT COUNT(*) recordnum FROM tf_f_user_discnt a WHERE user_id =
 * TO_NUMBER(:USER_ID) AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) AND end_date >= sysdate AND NOT EXISTS (SELECT
 * 1 FROM tf_b_trade_discnt WHERE trade_id = TO_NUMBER(:TRADE_ID) AND accept_month = TO_NUMBER(:ACCEPT_MONTH) AND
 * a.user_id = TO_NUMBER(:USER_ID) AND modify_tag = '1' AND discnt_code = a.discnt_code) AND EXISTS(SELECT 1 FROM
 * td_s_commpara WHERE subsys_code='CSM' AND param_attr=2001 AND param_code=:PARAM_CODE AND
 * para_code1=to_char(a.discnt_code) AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ') AND SYSDATE BETWEEN
 * start_date AND end_date) UNION ALL SELECT COUNT(*) recordnum FROM tf_b_trade_discnt b WHERE trade_id =
 * TO_NUMBER(:TRADE_ID) AND accept_month = TO_NUMBER(:ACCEPT_MONTH) AND b.user_id = TO_NUMBER(:USER_ID) AND modify_tag =
 * '0' AND EXISTS(SELECT 1 FROM td_s_commpara WHERE subsys_code='CSM' AND param_attr=2001 AND param_code=:PARAM_CODE AND
 * para_code1=to_char(b.discnt_code) AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ') AND SYSDATE BETWEEN
 * start_date AND end_date) )
 */
public class ExistsAllUserDiscntMulti extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsAllUserDiscntMulti.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsAllUserDiscntMulti() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;

        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");// PRCHSMD 购机某一类型部分依赖优惠
        IDataset ComparaInfo = CommparaInfoQry.getCommparaCode1("CSM", "2001", strParamCode, strEparchyCode);
        if (IDataUtil.isEmpty(ComparaInfo))
        {
            return false;
        }

        IDataset userDiscntList = databus.getDataset("TF_F_USER_DISCNT_AFTER");
        for (int i = 0, size = userDiscntList.size(); i < size; i++)
        {
            IData userDiscnt = userDiscntList.getData(i);
            String modifyTag = userDiscnt.getString("MODIFY_TAG");
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                for (int iComparaInfo = 0, iBSize = ComparaInfo.size(); iComparaInfo < iBSize; iComparaInfo++)
                {
                    if (userDiscnt.getString("DISCNT_CODE").equals(ComparaInfo.getData(iComparaInfo).getString("PARA_CODE1")))
                    {
                        bResult = true;
                        break;
                    }
                }
            }
            if (bResult)
            {
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsAllUserDiscntMulti() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
