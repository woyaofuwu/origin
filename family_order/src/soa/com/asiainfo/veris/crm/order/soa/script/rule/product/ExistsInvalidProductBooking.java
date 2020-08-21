
package com.asiainfo.veris.crm.order.soa.script.rule.product;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 非预约产品，不能办理预约产品变更
 * @author: xiaocl
 */
/*
 * SELECT count(1) recordcount FROM tf_b_trade a WHERE a.trade_id=TO_NUMBER(:TRADE_ID) AND
 * a.accept_month=TO_NUMBER(:ACCEPT_MONTH) AND SUBSTR(a.process_tag_set,19,1) = '1' AND NOT EXISTS (SELECT 1 FROM
 * td_s_commpara b WHERE b.subsys_code = 'CSM' AND b.param_attr = 243 AND b.param_code = '1' AND TRIM(b.para_code1) =
 * a.product_id AND SYSDATE < b.end_date AND b.eparchy_code = :EPARCHY_CODE)
 */
public class ExistsInvalidProductBooking extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsInvalidProductBooking.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsInvalidProductBooking() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        IDataset listTrade = databus.getDataset("TF_B_TRADE");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        IDataset listPreDroduct = BreQryForCommparaOrTag.getCommparaCode1("CSM", 243, "1", strEparchyCode);
        for (Iterator iter = listTrade.iterator(); iter.hasNext();)
        {
            IData trade = (IData) iter.next();
            for (int iListPreDroduct = 0, iSzie = listPreDroduct.size(); iListPreDroduct < iSzie; iListPreDroduct++)
            {
                if (!listPreDroduct.getData(iListPreDroduct).getString("PARA_CODE1").equals(trade.getString("PRODUCT_ID")) && trade.getString("PROCESS_TAG_SET").substring(18, 19).equals("1"))
                {
                    bResult = true;// 非预约产品条件
                    continue;
                }
                else
                {
                    bResult = false;
                    break;
                }
                    
            }
            if (bResult)
            {
                break;
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsInvalidProductBooking() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
