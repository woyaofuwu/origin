
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 判断未返单用户不能办理业务【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckLimitByReOddState extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckLimitByReOddState.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckLimitByReOddState() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
        	
        	 IData reqData = databus.getData("REQDATA");// 请求的数据
        	 String strBatRealName=reqData.getString("BATCH_OPER_TYPE","");
        	 
        	 if("BATREALNAME".equals(strBatRealName)){//批量实名制业务不进行未返单用户效验限制		 
        		 return false;
        	 }
        	 
            String strIdType = databus.getString("ID_TYPE");
            if ("1".equals(strIdType))
            {
                IDataset results = databus.getDataset("TF_F_USER");
                if (IDataUtil.isNotEmpty(results))
                {
                    IData user = results.getData(0);
                    String strBrandCode = user.getString("BRAND_CODE");
                    IDataset listTradeTypeCode = databus.getDataset("TD_S_TRADETYPE");
                    IData dataTradeTypeCode = listTradeTypeCode.first();
                    if (IDataUtil.isEmpty(dataTradeTypeCode))
                        return false;
                    String strPeropenLimitTag = dataTradeTypeCode.getString("PREOPEN_LIMIT_TAG", "");
                    String strOpenMode = user.getString("OPEN_MODE");
                   
                 
                    if (!strBrandCode.equals("GS01") && !strBrandCode.equals("GS02") && !strBrandCode.equals("MOSP"))
                    {
                        if ("1".equals(strPeropenLimitTag) && "1".equals(strOpenMode))
                        {
                            bResult = true;
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751025, "业务受理前条件判断：该用户是预开未返单用户，不能办理此业务！");
                        }
                    }
                }

            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckLimitByReOddState() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
