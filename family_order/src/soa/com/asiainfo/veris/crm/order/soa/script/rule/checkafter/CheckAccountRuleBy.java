
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:天赐良机，欢乐无限]托收购机用户只能修改帐户名称!"【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckAccountRuleBy extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckAccountRuleBy.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckAccountRuleBy() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        boolean bResultOne = false;

        /* 参数获取区域 */
        String strProcessTag = ruleParam.getString(databus, "PROCESS_TAG");
        String strAdvancePay = ruleParam.getString(databus, "ADVANCE_PAY");
        String strPurchashMode = ruleParam.getString(databus, "PRODUCT_ID");
        String strRsrvStr5 = ruleParam.getString(databus, "RSRV_STR5");

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strPayModeCode = databus.getDataset("TF_B_TRADE_ACCOUNT").first().getString("PAY_MODE_CODE", "");
        String strBankCode = databus.getDataset("TF_B_TRADE_ACCOUNT").first().getString("BANK_CODE", "");
        String strBankAcctNo = databus.getDataset("TF_B_TRADE_ACCOUNT").first().getString("BANK_ACCT_NO", "");
        IDataset listUserSaleActive = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        IDataset listUserAccount = databus.getDataset("TF_F_ACCOUNT");

        /*
         * 分部判断区域
         */
        // 第一逻辑单元

        for (Iterator iter = listUserSaleActive.iterator(); iter.hasNext();)
        {
            IData userSaleActive = (IData) iter.next();
            if (userSaleActive.getString("PRODUCT_ID").equals(strPurchashMode) && userSaleActive.getString("PROCESS_TAG").equals(strProcessTag) && userSaleActive.getString("ADVANCE_PAY").equals(strAdvancePay)
                    && userSaleActive.getString("RSRV_STR5").equals(strRsrvStr5))
            {
                bResultOne = true;
            }
        }
        if (bResultOne)
        {
            for (Iterator iter = listUserAccount.iterator(); iter.hasNext();)
            {
                IData userAccount = (IData) iter.next();
                if (!userAccount.getString("PAY_MODE_CODE").equals(strPayModeCode) || !userAccount.getString("BANK_CODE").equals(strBankCode) || !userAccount.getString("BANK_ACCT_NO").equals(strBankAcctNo))
                {
                    bResult = true;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckAccountRuleBy() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
