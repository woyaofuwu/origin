
package com.asiainfo.veris.crm.order.soa.script.rule.user;

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
 * @Description: 业务受理前条件判断：本月宽带开户用户的业务限制！用户在本月已办理宽带开户，开户当月不能办理手机号码主动销号、携转出网、宽带销户、宽带报停【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitTradeByWidenetIng extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(TBChecklimitTradeByWidenetIng.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeByWidenetIng() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            String strSerialNumber = databus.getString("SERIAL_NUMBER");
            if ("192".equals(strTradeTypeCode) || "605".equals(strTradeTypeCode) || "603".equals(strTradeTypeCode) || "617".equals(strTradeTypeCode) || "618".equals(strTradeTypeCode) || "624".equals(strTradeTypeCode)
                    || "625".equals(strTradeTypeCode) || "635".equals(strTradeTypeCode))
            {
                IData params = new DataMap();
                // 主动销号
                if ("192".equals(strTradeTypeCode))
                {
                    params.put("SERIAL_NUMBER", "KD_" + strSerialNumber);
                }
                else
                {
                    params.put("SERIAL_NUMBER", strSerialNumber);
                }
                IDataset dt = Dao.qryByCode("TF_B_TRADE_WIDENETHN", "SEL_WIDEUSER_BY_SN_KD", params);
                if (dt != null && dt.size() > 0)
                {
                    StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("用户在本月已办理宽带开户，开户当月不能办理手机号码主动销号、携转出网、宽带销户、宽带报停！");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751031, strError.toString());
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeByWidenetIng() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
