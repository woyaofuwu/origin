
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:您已办理 ["+objUserPurchaseNotModfiyAcct.GetString("PRODUCT_NAME")+"]
 *               购机,只能修改帐户名称"【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckAccountRuleBy1 extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckAccountRuleBy1.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckAccountRuleBy1() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        boolean bResultOne = false;
        /* 参数获取区域 */
        String strProcessTag = ruleParam.getString(databus, "PROCESS_TAG");
        String strAdvancePay = ruleParam.getString(databus, "ADVANCE_PAY");
        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strPayModeCode = databus.getDataset("TF_B_TRADE_ACCOUNT").first().getString("PAY_MODE_CODE", "");
        String strBankCode = databus.getDataset("TF_B_TRADE_ACCOUNT").first().getString("BANK_CODE", "");
        String strBankAcctNo = databus.getDataset("TF_B_TRADE_ACCOUNT").first().getString("BANK_ACCT_NO", "");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        IDataset listUserSaleActive = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        IDataset listUserAccount = databus.getDataset("TF_F_ACCOUNT");

        IDataset list9987 = BreQryForCommparaOrTag.getCommpara("CSM", 9987, strEparchyCode);
        if (IDataUtil.isEmpty(list9987))
        {
            return false;
        }
        /*
         * 分部判断区域
         */
        // 第一逻辑单元
        String strProductId="";
        for (Iterator iter = listUserSaleActive.iterator(); iter.hasNext();)
        {
            IData userSaleActive = (IData) iter.next();
            strProductId = userSaleActive.getString("PRODUCT_ID") ;
            for (int iList9987 = 0, iSize = list9987.size(); iList9987 < iSize; iList9987++)
            {
            	IData datalist9987 = list9987.getData(iList9987);
                if (strProcessTag.equals(userSaleActive.getString("PROCESS_TAG")) 
                		&& strAdvancePay.equals(userSaleActive.getString("ADVANCE_PAY"))
                		&& datalist9987.getString("PARAM_CODE","").equals(strProductId))
                {
                    bResultOne = true;
                    break;
                }
            }
        }

        if (bResultOne)
        {
            for (Iterator iterListAccount = listUserAccount.iterator(); iterListAccount.hasNext();)
            {
                IData userAccount = (IData) iterListAccount.next();
                
                if (!userAccount.getString("PAY_MODE_CODE").equals(strPayModeCode) 
                		|| !StringUtils.equals(userAccount.getString("BANK_CODE",""),strBankCode) 
                		|| !StringUtils.equals(userAccount.getString("BANK_ACCT_NO",""),strBankAcctNo))
                {
                	StringBuilder strError = new StringBuilder("业务登记后条件判断:").append("您已办理").append(BreQueryHelp.getNameByCode("PRODUCT_ID", strProductId)).append("购机，只能修改账户名称！");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201158", strError.toString());
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckAccountRuleBy1() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
