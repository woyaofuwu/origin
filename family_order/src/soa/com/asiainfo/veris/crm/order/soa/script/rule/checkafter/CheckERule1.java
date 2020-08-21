
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 该随e行号码已经与其他手机绑定,不能再次办理
 * @author: xiaocl
 */
public class CheckERule1 extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckERule1.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckERule1() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strUserId = databus.getDataset("TF_B_TRADE").first().getString("RSRV_STR6");
        String strTradeId = databus.getString("TRADE_ID");
        String strBrandCode = databus.getString("BRAND_CODE");
        if (!strBrandCode.equals("G001"))
        {
            StringBuilder strb = new StringBuilder("业务登记后条件判断:");
            strb.append("全球通用户才可以办理随E行捆绑业务!");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201047", strb.toString());
        }

        IData map = new DataMap();
        map.put("USER_ID", strUserId);
        map.put("TRADE_ID", strTradeId);
        if (Dao.qryByRecordCount("TD_S_CPARAM", "SEL_BY_LIMIT_G005", map,Route.getJourDb()))
        {
            StringBuilder strError = new StringBuilder("业务登记后条件判断:").append("该随e行号码已经与其他手机绑定,不能再次办理!");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201051", strError.toString());
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckERule1() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
