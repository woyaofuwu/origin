
package com.asiainfo.veris.crm.order.soa.script.rule.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 未完工校验【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckBeforeTradeLimit extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckBeforeTradeLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckBeforeTradeLimit() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        String strLimitTag = ruleParam.getString(databus, "LIMIT_TAG");
        String strLimitAttr = ruleParam.getString(databus, "LIMIT_ATTR");
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strUserId = databus.getString("USER_ID");
        String strBrandCode = databus.getString("BRAND_CODE");
        StringBuilder strError = new StringBuilder();
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strTradeId = databus.getString("TRADE_ID");
        String strStaffId = databus.getString("TRADE_STAFF_ID");
        IDataset listTradeTypeInfo = databus.getDataset("TD_S_TRADETYPE");
        // 取出跟当前办理的业务是否存在相限制类型的业务 且为未完工
        IDataset listExistsLimitTradeType = TradeInfoQry.getNoTradeByTradeId(strTradeTypeCode, strUserId, strBrandCode, strLimitAttr, strLimitTag, strEparchyCode, strTradeId);

        if (IDataUtil.isNotEmpty(listExistsLimitTradeType))
        {// 存在工单限制
            String strTradeTypeCodeLimit = listExistsLimitTradeType.getData(0).getString("LIMIT_TRADE_TYPE_CODE");
            String strTradeType = UTradeTypeInfoQry.getTradeType(strTradeTypeCodeLimit, strEparchyCode).getString("TRADE_TYPE");

            IDataset listTradeBookByUserId = TradeInfoQry.getTradeBookByUserIdTradeType(strUserId, strTradeTypeCodeLimit);

            if (IDataUtil.isNotEmpty(listTradeBookByUserId) && listTradeBookByUserId.size() > 0)
            {// 存在预约工单的操作
                IDataset listGroupTradeType = AttrBizInfoQry.getTradeTypeCode1(strTradeTypeCode, "业务类型", "TradeTypeCode");
                IDataset listComparaInfo = BreQryForCommparaOrTag.getCommpara("CSM", 164, strTradeTypeCode, "ZZZZ");
                if ("110".equals(listTradeBookByUserId.first().getString("TRADE_TYPE_CODE")) && listGroupTradeType != null && listGroupTradeType.size() > 0 && (listComparaInfo == null || listComparaInfo.size() == 0))// //预约产品变更之后办理不在164配置中的集团业务
                {

                }
                if ((strTradeTypeCode.equals("110") || strTradeTypeCode.equals("240") && strTradeTypeCode.equals(listTradeBookByUserId.first().getString("TRADE_TYPE_CODE"))))
                {

                }
                else
                {
                    if ("110".equals(listTradeBookByUserId.first().getString("TRADE_TYPE_CODE")))
                    {

                        strError.append("业务受理前条件判断-该用户当前有预约的产品变更工单，如要办理此业务，请先取消预约的产品变更业务！");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6080, strError.toString());
                    }
                    else
                    {
                        strError.append("业务受理前条件判断-用户有预约的限制业务【").append(strTradeType).append("】！");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6080, strError.toString());
                    }
                }

            }
            else
            {
                {
                    if (databus.getString("X_CHOICE_TAG", "").equals("0"))
                    {
                        strError.append("业务受理前条件判断-用户有未完工的限制业务【").append(strTradeType).append("】！");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751013, strError.toString());
                    }
                    else
                    {// 工单提交的时候也校验，防止工单重复提交
                        strError.append("业务受理前条件判断-用户有未完工的限制业务【").append(strTradeType).append("】！");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751013, strError.toString());
                    }
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckBeforeTradeLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
