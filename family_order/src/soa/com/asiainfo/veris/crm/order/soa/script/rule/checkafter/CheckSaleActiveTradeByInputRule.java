
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
 * @Description: 业务登记后条件判断:业务登记后条件判断:手机串号为-1，传值有误，请重新校验手机串号或换台电脑重新办理该业务！【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckSaleActiveTradeByInputRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckSaleActiveTradeByInputRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckSaleActiveTradeByInputRule() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        boolean bExistsOne = false; // 设置第一逻辑点

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeSaleActive = databus.getDataset("TF_B_TRADE_SALE_ACTIVE");
        IDataset listTradeSaleGoods = databus.getDataset("TF_B_TRADE_SALE_GOODS");
        for (Iterator iter = listTradeSaleActive.iterator(); iter.hasNext();)
        {
            IData TradeSaleActive = (IData) iter.next();
            if (TradeSaleActive.getString("MODIFY_TAG").equals(strModifyTag))
            {
                bExistsOne = true;
            }
        }

        if (bExistsOne)
        {
            for (Iterator iter = listTradeSaleActive.iterator(); iter.hasNext();)
            {
                {
                    IData TradeSaleActive = (IData) iter.next();
                    String campnType = TradeSaleActive.getString("CAMPN_TYPE");
                    int iCntTradeSaleGoods = listTradeSaleGoods.size();
                    String strResCode = listTradeSaleGoods.getData(0).getString("RES_CODE");
                    if (campnType.equals("") || campnType.length() == 0)
                    {

                    }
                    if (iCntTradeSaleGoods > 0 && (campnType == "YX03" || campnType == "YX07" || campnType == "YX08" || campnType == "YX09") && strResCode == "-1")
                    {
                        bResult = true;
                    }
                }
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckSaleActiveTradeByInputRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
