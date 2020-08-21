
package com.asiainfo.veris.crm.order.soa.script.rule.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 限制类逻辑
 * @author: xiaocl
 */
public class CheckNetTradeLimit extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckNetTradeLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        IDataset listUser = databus.getDataset("TF_F_USER");
        if (IDataUtil.isEmpty(listUser))
        {
            return false;
        }
        String strNetTypeCode = listUser.first().getString("NET_TYPE_CODE");
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", strTradeTypeCode);
        param.put("LIMIT_TAG", "1");
        IDataset tradeLimits = Dao.qryByCode("TD_S_NET_TRADE_LIMIT", "SEL_NETTRADE_LIMIT_1", param, Route.CONN_CRM_CEN);
        // 以业务类型为主体，判断哪些网别能办理该业务
        if (IDataUtil.isNotEmpty(tradeLimits))
        {
            boolean canFlag = false; // 默认当前用户不能办理
            for (int i = 0; i < tradeLimits.size(); i++)
            {
                IData tradeLimit = tradeLimits.getData(i);
                String netTypeCode = tradeLimit.getString("NET_TYPE_CODE");
                if (StringUtils.equals(strNetTypeCode, netTypeCode))
                {
                    canFlag = true; // 如果配置配置该网别能办理该业务，设为true
                    break;
                }
            }
            if (!canFlag)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751016, "网别判断，用户不能办理该业务!");
            }
        }

        // 以业务类型为主体，判断哪些网别不能办理该业务
        param.put("LIMIT_TAG", "0");
        param.put("NET_TYPE_CODE", strNetTypeCode);
        tradeLimits = Dao.qryByCode("TD_S_NET_TRADE_LIMIT", "SEL_NETTRADE_LIMIT", param, Route.CONN_CRM_CEN);
        if (IDataUtil.isNotEmpty(tradeLimits))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751016, "网别判断，用户不能办理该业务!");
        }

        return false;
    }
}
