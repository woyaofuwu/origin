
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoDefProductPhoneQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 特定号段才能办理TD无线座机（铁通）套餐 9432 【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckDefProductPhone extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckDefProductPhone.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckDefProductPhone() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;

        String strProductId = databus.getString("PRODUCT_ID");
        /*
         * 外部查询相关信息获取区域
         */
        String strSn = databus.getString("SERIAL_NUMBER");
        IDataset listDefProductPhoneSn = PoDefProductPhoneQry.getDefProductPhoneInfos(strSn);
        if (IDataUtil.isEmpty(listDefProductPhoneSn))
        {
            if ("10009432".equals(strProductId))
            {
                bResult = true;
            }
        }
        /*
         * 分部判断区域
         */
        // 第一逻辑单元
        if (IDataUtil.isNotEmpty(listDefProductPhoneSn))
        {
            IData productPhonsn = listDefProductPhoneSn.getData(0);
            if (!"".equals(productPhonsn.getString("PRODUCT_ID")) && !strProductId.equals(productPhonsn.getString("PRODUCT_ID")))
            {
                bResult = true;
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckDefProductPhone() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
