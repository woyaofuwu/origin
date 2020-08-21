
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: F0彩铃服务与彩铃优惠不能同时开通！SQL翻译
 * @author: xiaocl
 */

/*
 * SELECT count(*) recordcount FROM tf_b_trade_discnt WHERE trade_id=TO_NUMBER(:TRADE_ID) AND
 * accept_month=TO_NUMBER(:ACCEPT_MONTH) AND discnt_code IN (SELECT discnt_code FROM td_b_dtype_discnt WHERE
 * discnt_type_code=:DISCNT_TYPE_CODE AND end_date > SYSDATE) AND (modify_tag =:MODIFY_TAG OR :MODIFY_TAG = '*')
 */
public class ExistsTradeAddTypeDiscnt extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsTradeAddTypeDiscnt.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeDiscnt() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strDiscntTypeCode = ruleParam.getString(databus, "DISCNT_TYPE_CODE");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        IDataset listDiscntType = UDiscntInfoQry.queryDiscntsByDiscntType(strDiscntTypeCode);
        		//Dao.qryByCode("TD_B_DTYPE_DISCNT", "SEL_DISTYPE_BY_TYPECODE", param, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(listDiscntType)) {
        	 return false;
		}
        if (IDataUtil.isNotEmpty(listDiscntType) && IDataUtil.isNotEmpty(listTradeDiscnt))
        {
            for (int iDt = 0, iSize = listDiscntType.size(); iDt < iSize; iDt++)
            {
                for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
                {
                    IData tradeDiscnt = (IData) iter.next();

                    if (listDiscntType.getData(iDt).getString("DISCNT_CODE").equals(tradeDiscnt.getString("DISCNT_CODE")) && ("*".equals(strModifyTag) || strModifyTag.equals(tradeDiscnt.getString("MODIFY_TAG"))))
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
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
