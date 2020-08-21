
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

public class TBCheckBlackUserRule extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckBlackUserRule.class);

    /**
     * Copyright: Copyright 2014 Asiainfo-Linkage
     * 
     * @ClassName: TBCheckBlackUserRule.java
     * @Description: 黑白名单判断规则 询问类规则【TradeCheckBefore】
     * @version: v1.0.0
     * @author: xiaocl
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckBeforeTradeLimit() >>>>>>>>>>>>>>>>>>");
        // 自定义区域
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        IData userInfo = databus.getDataset("TF_F_USER").getData(0);

        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
        String strTagSet = "";
        int iCustCount = 0;
        int iCheckBlackUser = 1;
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strEparchyCode = databus.getString("EPARCHY_CODE");

        if (IDataUtil.isNotEmpty(custInfo))
        {
            iCustCount = 1;
        }

        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            if (!strTradeTypeCode.equals("-1"))
            {
                IDataset listTradeType = null;
                IData param = new DataMap();
                /*
                 * param.clear(); param.put("EPARCHY_CODE", strEparchyCode); param.put("TRADE_TYPE_CODE",
                 */
                // strTagSet = listTradeType.getData(0).getString("TRADE_TYPE_CODE");
                strTagSet = databus.getDataset("TD_S_TRADETYPE").getData(0).getString("TRADE_TYPE_CODE", "");
                if (strTagSet.substring(1, 2).equals("0"))
                {
                    if (databus.getString("SERIAL_NUMBER").substring(0, 2).equals("99"))
                    {
                        iCheckBlackUser = 0;
                    }
                    if (iCustCount > 0 && iCheckBlackUser != 0)
                    {
                        param.clear();
                        param.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE"));
                        param.put("PSPT_ID", custInfo.getString("PSPT_ID"));
                        if (Dao.qryByRecordCount("TD_O_BLACKUSER", "SEL_BY_PK", param))
                        {
                            if (strTagSet.substring(1, 2).equals("1"))
                            {
                                StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("黑名单客户，建议终止业务的办理！<br/>是否要继续业务的办理？选择【是】继续办理业务，选择【否】终止办理业务。");
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, 751031, strError.toString());
                            }
                            else if (strTagSet.substring(1, 2).equals("2"))
                            {
                                StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("黑名单客户不能办理当前业务！");
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751014, strError.toString());
                            }
                        }
                    }
                }

            }

            if (logger.isDebugEnabled())
                logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckBlackUserRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        }
        return bResult;
    }
}
