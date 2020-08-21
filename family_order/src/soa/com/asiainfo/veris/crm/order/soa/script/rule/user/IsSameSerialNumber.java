
package com.asiainfo.veris.crm.order.soa.script.rule.user;

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
 * @Description: 无条件呼叫转移，不能互转到自己手机上面 *
 * @author: xiaocl
 */

/*
 * SELECT COUNT(*) recordcount FROM tf_b_trade_attr where trade_id = :TRADE_ID and accept_month =
 * to_number(substr(:TRADE_ID, 5, 2)) and inst_type = 'S' and rsrv_num1 = :SERVICE_ID and attr_code = 'V12V1' and
 * attr_value = :SERV_PARA1 AND modify_tag='0'
 */
public class IsSameSerialNumber extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(IsSameSerialNumber.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsSameSerialNumber() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        String strInstType = "S";
        String strRsrvnum1 = ruleParam.getString(databus, "SERVICE_ID");// 12
        String strAttrCode = "V12V1";
        String strAttrValue = databus.getString("SERIAL_NUMBER");
        String strModifyTag = "0";

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeAttr = databus.getDataset("TF_B_TRADE_ATTR");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeAttr.iterator(); iter.hasNext();)
        {
            IData tradeAttr = (IData) iter.next();
            if (tradeAttr.getString("INST_TYPE").equals(strInstType)
                    && (tradeAttr.getString("MODIFY_TAG").equals(strModifyTag) && strRsrvnum1.equals(tradeAttr.getString("RSRV_NUM1")) && strAttrCode.equals(tradeAttr.getString("ATTR_CODE")) && strAttrValue.equals(tradeAttr.getString("ATTR_VALUE"))))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsSameSerialNumber() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
