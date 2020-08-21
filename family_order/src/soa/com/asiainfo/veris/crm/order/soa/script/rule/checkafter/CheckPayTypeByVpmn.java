
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:该集团已有约定消费购机用户，不能办理付费方式变更!【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckPayTypeByVpmn extends BreBase implements IBREScript

{

    private static Logger logger = Logger.getLogger(CheckPayTypeByVpmn.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckPayTypeByVpmn() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        boolean bResultOne = false;

        /* 参数获取区域 */
        String strProcessTag = ruleParam.getString(databus, "PROCESS_TAG");
        String strAdvancePay = ruleParam.getString(databus, "ADVANCE_PAY");

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strPayModeCode = databus.getString("PAY_MODE_CODE");
        String strBankCode = databus.getString("BAND_CODE");
        String strBankAcctNo = databus.getString("BANK_ACCT_NO");
        String strUserId = databus.getString("USER_ID");

        String strBrandCode = databus.getString("BRAND_CODE");
        if (!strBrandCode.equals("VPMN"))
        {
            return bResult;
        }
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        IDataset listUserSaleActive = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        IDataset listUserAccount = databus.getDataset("TF_F_ACCOUNT");
        /*
         * 外部查询相关信息获取区域
         */
        IData map = new DataMap();
        map.put("USER_ID", strUserId);
        map.put("PROCESS_TAG", "0");
        map.put("EPARCHY_CODE", strEparchyCode);
        map.put("RELATION_TYPE_CODE", "20");
        if (Dao.qryByRecordCount("TD_B_CPARAM", "SEL_BY_USERIDA_NOT_MODFIY_ACCT", map))
        {
            StringBuilder strb = new StringBuilder("业务登记后条件判断:判断购机用户类型异常!");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201012, strb.toString());
            return false;
        }
        /*
         * 分部判断区域
         */
        // 第一逻辑单元
        for (Iterator iter = listUserSaleActive.iterator(); iter.hasNext();)
        {
            IData userSaleActive = (IData) iter.next();
            if (strProcessTag.equals(userSaleActive.getString("PROCESS_TAG")) && strAdvancePay.equals(userSaleActive.getString("ADVANCE_PAY")))
            {
                bResultOne = true;
                break;
            }
        }

        if (bResultOne)
        {
            for (Iterator iterListAccount = listUserAccount.iterator(); iterListAccount.hasNext();)
            {
                IData userAccount = (IData) iterListAccount.next();
                if (!userAccount.getString("PAY_MODE_CODE").equals(strPayModeCode) || !userAccount.getString("BANK_CODE").equals(strBankCode) || !userAccount.getString("BANK_ACCT_NO").equals(strBankAcctNo))
                {
                    bResult = true;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckPayTypeByVpmn() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
