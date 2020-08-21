
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 产品变更未完工工单特殊判断
 * @author: xiaocl
 */
public class CheckNotFinishTradeForProdChange extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckNotFinishTradeForProdChange.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckNotFinishTradeForProdChange() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strLimitTag = ruleParam.getString(databus, "LIMIT_TAG");
        String strLimitAttr = ruleParam.getString(databus, "LIMIT_ATTR");
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strUserId = databus.getString("USER_ID");
        String strBrandCode = databus.getString("BRAND_CODE");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strTradeId = databus.getString("TRADE_ID");
        // 取出跟当前办理的业务是否存在相限制类型的业务 且为未完工
        IDataset listExistsLimitTradeType = TradeInfoQry.getNoTradeByTradeId(strTradeTypeCode, strUserId, strBrandCode, strLimitAttr, strLimitTag, strEparchyCode, strTradeId);

        if (IDataUtil.isNotEmpty(listExistsLimitTradeType))
        {
            String strTradeTypeCodeLimit = listExistsLimitTradeType.getData(0).getString("LIMIT_TRADE_TYPE_CODE");
            if (strTradeTypeCodeLimit.equals("110") || strTradeTypeCodeLimit.equals("120") || strTradeTypeCodeLimit.equals("150"))
            {
                IDataset listGroupTradeType = AttrBizInfoQry.getTradeTypeCode1(strTradeTypeCode, "业务类型", "TradeTypeCode");
                IDataset listComparaInfo = BreQryForCommparaOrTag.getCommpara("CSM", 164, strTradeTypeCode, "ZZZZ");
                if ((strTradeTypeCode.equals("110") || strTradeTypeCode.equals("240") || strTradeTypeCode.equals("120") || strTradeTypeCode.equals("150") || listGroupTradeType.size() > 0) && listComparaInfo.size() == 0)
                {
                }
                else
                {
                    IDataset listTradeBookByUserId = TradeInfoQry.getTradeBookByUserIdTradeType(strUserId, strTradeTypeCodeLimit);
                    if (IDataUtil.isNotEmpty(listTradeBookByUserId) && listTradeBookByUserId.size() > 0)
                    {
                        bResult = true;
                    }
                }

            }
            else
            {
                bResult = true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckNotFinishTradeForProdChange() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
