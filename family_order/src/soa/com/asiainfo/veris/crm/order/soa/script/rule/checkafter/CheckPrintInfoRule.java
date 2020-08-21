
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 查询用户保险打印信息出错！【TradeCheckAfter】
 * @author: xiaocl
 */

/*
 * select to_char(a.trade_id)
 * trade_id,a.rsrv_value_code,a.rsrv_value,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5
 * ,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,a.modify_tag,to_char(a.start_date,'yyyy-mm-dd
 * hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date from tf_b_trade_other a,tf_bh_trade b
 * where to_number(a.rsrv_str7)=b.trade_id and a.rsrv_str7=:TRADE_ID and a.rsrv_value_code=:RSRV_VALUE_CODE and
 * b.trade_type_code='330' and a.rsrv_value_code in ('801','803','804','805','807','808','810','811','812','813')
 */

public class CheckPrintInfoRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckPrintInfoRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckPrintInfoRule() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strRsrvStr1 = databus.getString("RSRV_STR1");// VRSRV_VALUE_CODE
        String strRsrvStr2 = databus.getString("RSRV_STR2");// VTRADE_ID

        /*
         * 外部查询相关信息获取区域
         */
        IData map = new DataMap();
        map.put("RSRV_VALUE_CODE", strRsrvStr1);
        map.put("TRADE_ID", strRsrvStr2);
        IDataset otherPrint = null;
        if (IDataUtil.isNotEmpty(otherPrint) && otherPrint.size() > 1)
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckPrintInfoRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
