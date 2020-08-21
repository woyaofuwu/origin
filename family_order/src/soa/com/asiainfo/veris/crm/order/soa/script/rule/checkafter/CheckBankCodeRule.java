
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

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

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:农行代缴必须选择农业银行!【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckBankCodeRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckBankCodeRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckBankCodeRule() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strPayModeCode = databus.getDataset("TF_B_TRADE_ACCOUNT").first().getString("PAY_MODE_CODE", "");

        /*
         * 分部判断区域
         */
        // 第一逻辑单元
        if (StringUtils.equals("8", strPayModeCode))
        {
            IDataset acctConsigns = databus.getDataset("TF_B_TRADE_ACCT_CONSIGN");
            if (IDataUtil.isNotEmpty(acctConsigns))
            {
                String strSuperBankCode = "";
                for (int i = 0, size = acctConsigns.size(); i < size; i++)
                {
                    IData consign = acctConsigns.getData(i);
                    if (StringUtils.equals(consign.getString("MODIFY_TAG"), "0"))
                    {
                        strSuperBankCode = consign.getString("SUPER_BANK_CODE", "");
                        break;
                    }
                }
                if (!StringUtils.equals("03", strSuperBankCode))
                {
                    StringBuilder strb = new StringBuilder("业务登记后条件判断:农行代缴必须选择农业银行!");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201010, strb.toString());
                }
            }

        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckBankCodeRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
