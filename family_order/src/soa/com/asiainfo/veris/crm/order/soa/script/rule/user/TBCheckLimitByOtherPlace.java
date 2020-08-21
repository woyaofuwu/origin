
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 判断用户是否可以异地受理业务【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckLimitByOtherPlace extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckLimitByOtherPlace.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckLimitByOtherPlace() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        int iCount;
        String strTag = "";
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            /* 获取规则配置信息 */
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            String strExtendTag = databus.getDataset("TD_S_TRADETYPE").getData(0).getString("EXTEND_TAG");
            String strEparchyCode = databus.getString("EPARCHY_CODE");
            if (strExtendTag.equals("0"))
            {
                String strIdType = databus.getString("ID_TYPE");
                // 判断客户现在是否在办理异地业务
                if (strIdType.equals("0"))
                {
                    IDataset results = databus.getDataset("TF_F_CUSTOMER");
                    if (IDataUtil.isNotEmpty(results))
                    {
                        IData customer = results.getData(0);
                        String strEparchyCodeCust = customer.getString("EPARCHY_CODE");
                        if (!strEparchyCodeCust.equals(strEparchyCode))
                        {
                            StringBuilder strb = new StringBuilder("业务受理前条件判断：业务受理前条件判断：客户不能异地办理该业务！");
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751020, strb.toString());
                        }
                    }

                }
                // 判断用户现在是否在办理异地业务
                else if (strIdType.equals("1"))
                {
                    IDataset results = databus.getDataset("TF_F_USER");
                    if (IDataUtil.isNotEmpty(results))
                    {
                        IData user = results.getData(0);
                        String strEparchyCodeUser = user.getString("EPARCHY_CODE");
                        if (!strEparchyCodeUser.equals(strEparchyCode))
                        {
                            // 增加参数控制在某些业务中可以进行异地办理业务
                            IData param = new DataMap();
                            param.put("SUBSYS_CODE", "CSM");
                            param.put("PARAM_ATTR", 1214);
                            param.put("PARAM_CODE", strTradeTypeCode);
                            param.put("EPARCHY_CODE", "ZZZZ");
                            IDataset commparaList = Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param);
                            iCount = commparaList.size();
                            if (iCount > 0)
                            {
                                strTag = ((String) commparaList.get(0, "PARA_CODE1")).trim();
                            }
                            if (strTag.equals("1"))
                            {
                                if (databus.getString("SERIAL_NUMBER").substring(0, 2).equals("99"))
                                {
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751021, "业务受理前条件判断：用户不能异地办理该业务！");
                                }
                            }
                        }
                    }

                }
            }

            if (logger.isDebugEnabled())
                logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckLimitByOtherPlace() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        }
        return bResult;
    }

}
