
package com.asiainfo.veris.crm.order.soa.script.rule.customer;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务受理前条件判断：非实名制的客户办理报停业务时业务限制！【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitTradeByNoRealName extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitTradeByNoRealName.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeByAcctTagAndRealName() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            IData param = new DataMap();
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            String strUserId = databus.getString("USER_ID");
            String batchId = databus.getString("BATCH_ID");
            // 批量报停报开不执行此规则
            if (("131".equals(strTradeTypeCode) || "133".equals(strTradeTypeCode)) && StringUtils.isEmpty(batchId))
            {
                param.put("USER_ID", strUserId);
                IDataset userInfo2 = Dao.qryByCode("TF_F_USER", "SEL_USERCUST_ID", param);
                if (userInfo2 != null && userInfo2.size() > 0)
                {
                    String isrealname = userInfo2.getData(0).getString("IS_REAL_NAME", "");
                    if (!"1".equals(isrealname))
                    {
                        StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("该号码为非实名用户，请登记实名制后再进行办理!");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 755016, strError.toString());

                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeByNoRealName() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
