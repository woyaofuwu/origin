
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:办理活动[%s]发现存在未完工的预约主产品变更工单，业务不能继续！【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckSaleActiveByBookTradeRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckSaleActiveByBookTradeRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckSaleActiveByBookTradeRule() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        /* 获取规则配置信息 */
        String strTradeTypeCodeCompara = ruleParam.getString(databus, "TRADE_TYPE_CODE");
        String strProcessTag = ruleParam.getString(databus, "PROCESS_TAG");
        /* 获取业务台账，用户资料信息 */
        IDataset listTrade = databus.getDataset("TF_B_TRADE");
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strProductId = databus.getString("RSRV_STR1");
        String strUserId = databus.getString("USER_ID");

        /*
         * Select a.param_code,param_name,para_code1 From Td_s_Commpara a Where a.Subsys_Code = 'CSM' And a.Param_Attr =
         * 151 And a.Param_Code = :TRADE_TYPE_CODE And a.Para_Code1 = :PRODUCT_ID And Sysdate Between a.Start_Date And
         * a.End_Date And Exists (Select 1 From Tf_b_Trade b Where b.User_Id = TO_NUMBER(:USER_ID) And b.Trade_Type_Code
         * = 110 And b.Exec_Time > Sysdate And Substr(b.Process_Tag_Set, 9, 1) = '4')
         */
        /*
         * IData map = new DataMap(); map.put("SUBSYS_CODE", "CSM"); map.put("PARAM_ATTR", "151"); map.put("PARAM_CODE",
         * strTradeTypeCode); map.put("PARA_CODE1", strProductId);
         */
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        IDataset listCompara = BreQryForCommparaOrTag.getCommpara("CSM", 151, strTradeTypeCode, strProductId, strEparchyCode);
        for (Iterator iter = listTrade.iterator(); iter.hasNext();)
        {
            IData dataTrade = (IData) iter.next();
            if (dataTrade.getString("USER_ID").equals(strUserId) && dataTrade.getString("TRADE_TYPE_CODE").equals(strTradeTypeCodeCompara) && dataTrade.getString("PROCESS_TAG").equals(strProcessTag))
            {
                for (int iListCompara = 0, iSize = listCompara.size(); iListCompara < iSize; iListCompara++)
                {
                    bResult = true;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckSaleActiveByBookTradeRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
