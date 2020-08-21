
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:特定号段才能办理TD无线座机（铁通）套餐! 【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckTdByProductRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckTdByProductRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckTdByProductRule() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;

        /* 参数获取区域 */
        String strProductRule = ruleParam.getString(databus, "PRODICT_ID");

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strProductId = databus.getString("PRODUCT_ID");
        /*
         * 外部查询相关信息获取区域
         */
        String strSn = databus.getString("SERIAL_NUMBER");
        IData map = new DataMap();
        map.put("PHONECODE_S", strSn);
        IDataset listDefProductPhoneSn = Dao.qryByCode("TD_B_DEFPRODUCT_PHONE", "SEL_BY_PHONE", map);
        /*
         * 分部判断区域
         */
        // 第一逻辑单元
        if (listDefProductPhoneSn.isEmpty())
        {
            if (strProductRule.equals(strProductId))
            {
                bResult = true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckTdByProductRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
