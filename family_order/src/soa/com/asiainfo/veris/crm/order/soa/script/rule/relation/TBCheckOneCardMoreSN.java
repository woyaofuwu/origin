
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
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
 * @Description: 一卡双号用户不能办理哪些业务【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckOneCardMoreSN extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckOneCardMoreSN.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckOneCardMoreSN() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            IData param = new DataMap();
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            if (strTradeTypeCode.equals("140") || strTradeTypeCode.equals("141") || strTradeTypeCode.equals("142") || strTradeTypeCode.equals("143") || strTradeTypeCode.equals("190") || strTradeTypeCode.equals("192"))
            {
                String strId = databus.getString("ID");
                String strEparchyCode = databus.getDataset("TF_F_USER").getData(0).getString("EPARCHY_CODE");

                param.clear();
                param.put("EPARCHY_CODE", strEparchyCode);
                param.put("TAG_CODE", "CS_VIEWCON_CHANGERES");
                param.put("SUBSYS_CODE", "CSM");
                param.put("USE_TAG", "0");
                IDataset listTag = Dao.qryByCode("TD_S_TAG", "SEL_ALL_BY_TAGCODE", param, Route.CONN_CRM_CEN);
                // boolean tag = Dao.qryByRecordCount("TD_S_TAG", "SEL_ALL_BY_TAGCODE", param, Route.CONN_CRM_CEN);
                String strTagNum = "1";
                int iMainCount = listTag.size();
                if (iMainCount > 0)
                {
                    strTagNum = listTag.getData(0).getString("TAG_NUMBER");
                }
                else if (iMainCount == 1)
                {
                    strTagNum = (String) listTag.first().getString("TAG_NUMBER");
                }
                param.clear();
                param.put("USER_ID_B", strId);
                param.put("RELATION_TYPE_CODE", "30");
                if (Dao.qryByRecordCount("TF_F_RELATION_UU", "SEL_USER_UUROL", param))
                {
                    if (strTagNum.equals("1") || strTradeTypeCode.equals("190") || strTradeTypeCode.equals("192"))
                    {
                        databus.put("X_CHECK_TAG", "1");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, "业务受理前条件判断:该用户是一卡双号，继续操作将取消一卡双号业务！<br/>是否要继续业务的办理？选择【是】继续办理业务，选择【否】终止办理业务。");
                    }
                    else if (!strTradeTypeCode.equals("190") && !strTradeTypeCode.equals("192"))
                    {
                        if (!strTradeTypeCode.equals("143"))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751030, "该用户是一卡双号集团关系用户，不允许在普通资源变更界面受理业务，请先取消一卡双号集团关系！");
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckOneCardMoreSN() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
