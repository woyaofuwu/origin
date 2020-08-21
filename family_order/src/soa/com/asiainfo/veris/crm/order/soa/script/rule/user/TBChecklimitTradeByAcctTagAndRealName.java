
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

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务受理前条件判断：待激活用户业务限制!【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitTradeByAcctTagAndRealName extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitTradeByAcctTagAndRealName.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeByAcctTagAndRealName() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            IData param = new DataMap();
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            String strUserId = databus.getString("USER_ID");
            if (("131".equals(strTradeTypeCode) || "132".equals(strTradeTypeCode) /* || "240".equals(strTradeTypeCode) */
                    || "110".equals(strTradeTypeCode) /* || "252".equals(strTradeTypeCode) */
                    || "142".equals(strTradeTypeCode) /* || "255".equals(strTradeTypeCode) */
                    || "143".equals(strTradeTypeCode) || "192".equals(strTradeTypeCode) || "144".equals(strTradeTypeCode) || "3804".equals(strTradeTypeCode)))
            {
                param.put("USER_ID", strUserId);
                IDataset userInfo2 = Dao.qryByCode("TF_F_USER", "SEL_USERCUST_ID", param);

                if (IDataUtil.isNotEmpty(userInfo2))
                {
                    IData userInfo2Data = userInfo2.first();
                    String isrealname = userInfo2Data.getString("IS_REAL_NAME");
                    String rsrv_num1 = userInfo2Data.getString("RSRV_NUM1");
                    String acct_tag = userInfo2Data.getString("ACCT_TAG");
                    if ((!"1".equals(isrealname)) && "1".equals(rsrv_num1) && "2".equals(acct_tag))
                    {
                        if ((!StringUtils.equals("0", xChoiceTag)) && StringUtils.equals("192", strTradeTypeCode))
                        {
                            // 对于待激活非实名制用户，有一个接口可以直接立即销户，跳过此场景，只限制从前台发起的立即销户
                        }
                        else
                        {
                            StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("未激活用户不能办理！");
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751039, strError.toString());
                        }
                    }
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeByAcctTagAndRealName() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
