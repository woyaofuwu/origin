
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 请选择优惠！
 * @author: xiaocl
 */

/*
 * SELECT count(1) recordcount FROM tf_b_trade_discnt a WHERE trade_id=TO_NUMBER(:TRADE_ID) AND
 * accept_month=TO_NUMBER(:ACCEPT_MONTH) AND exists(select 1 from td_b_prod_discnt_member b,td_b_product c where
 * b.discnt_code=a.discnt_code and b.product_id =c.product_id and c.brand_code=:BRAND_CODE) AND modify_tag=:MODIFY_TAG
 */
public class ExistsMemberDiscnt extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsMemberDiscnt.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsMemberDiscnt() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");// 0
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        String strBrandCode = databus.getString("BRAND_CODE");
        IData map = new DataMap();
        map.put("BRAND_CODE", strBrandCode);
        IDataset listProdDiscntMember = Dao.qryByCode("TD_B_PROD_DISCNT_MEMBER", "SEL_PRODUCTDISCNTMEMBER", map);
        if (IDataUtil.isEmpty(listProdDiscntMember))
        {
            return false;
        }
        for (int iListProdDiscntMember = 0, iSize = listProdDiscntMember.size(); iListProdDiscntMember < iSize; iListProdDiscntMember++)
        {
            for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
            {
                IData tradeDiscnt = (IData) iter.next();
                if (listProdDiscntMember.getData(iListProdDiscntMember).getString("DISCNT_CODE").equals(tradeDiscnt.getString("DISCNT_CODE")) && strModifyTag.equals(tradeDiscnt.getString("MODIFY_TAG")))
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
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsMemberDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }
}
