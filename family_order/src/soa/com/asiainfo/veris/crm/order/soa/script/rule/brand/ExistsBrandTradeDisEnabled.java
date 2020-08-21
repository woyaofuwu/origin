
package com.asiainfo.veris.crm.order.soa.script.rule.brand;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 该用户品牌不能强制停机
 * @author: xiaocl
 */
/*
 * SELECT count(1) recordcount FROM tf_f_user a WHERE user_id=to_number(:USER_ID) AND
 * partition_id=mod(to_number(:USER_ID),10000) AND exists (select 1 from td_s_commpara where subsys_code='CSM' and
 * param_attr=131 and param_code=a.brand_code and para_code1=:TRADE_TYPE_CODE and (eparchy_code=:TRADE_EPARCHY_CODE or
 * eparchy_code='ZZZZ') and sysdate between start_date and end_date)
 */
public class ExistsBrandTradeDisEnabled extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsBrandTradeDisEnabled.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsAllUserSvc() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strTradeEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        IDataset ComparaInfo = BreQryForCommparaOrTag.getCommparaCode("CSM", 131, strTradeTypeCode, strTradeEparchyCode);
        if (IDataUtil.isEmpty(ComparaInfo))
        {
            return false;
        }
        IDataset listUser = databus.getDataset("TF_F_USER");
        for (int iListUser = 0, iASize = listUser.size(); iListUser < iASize; iListUser++)
        {
            for (int iComparaInfo = 0, iBSize = ComparaInfo.size(); iComparaInfo < iBSize; iComparaInfo++)
            {
                if (listUser.getData(iListUser).getString("BRAND_CODE").equals(ComparaInfo.getData(iComparaInfo).getString("PARAM_CODE")))
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

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsBrandTradeDisEnabled() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
