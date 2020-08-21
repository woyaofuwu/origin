
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;


/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: GPRS优惠和4700优惠互斥！
 * @author: xiaocl
 */

/*
 * Select count(*) recordcount From Tf_b_Trade_Discnt a Where trade_id=TO_NUMBER(:TRADE_ID) AND
 * accept_month=TO_NUMBER(:ACCEPT_MONTH) And a.Modify_Tag = '0' And Sysdate Between a.start_date And a.end_date And
 * Exists (Select 1 From td_b_element_limit b,tf_f_user_discnt c Where c.partition_id=Mod(TO_NUMBER(:USER_ID),10000) And
 * c.user_id=TO_NUMBER(:USER_ID) And b.element_type_code_a='D' And b.element_id_a=a.discnt_code ----- And
 * b.element_id_b=c.discnt_code And b.element_type_code_b='D' And c.discnt_code=4700 And Sysdate Between b.start_date
 * And b.end_date And Sysdate Between c.start_date And c.end_date)
 */
public class ExistsTradeAdd4700LimitDiscnt extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsTradeAdd4700LimitDiscnt.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeAdd4700LimitDiscnt() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        boolean bExistsOne = false; // 设置第一关联逻辑节点
        boolean bExistsTwo = false; // 设置第二关联逻辑节点

        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");
        String strElementTypeCodeA = ruleParam.getString(databus, "ELEMENT_TYPE_CODE_A");
        String strElementTypeCodeB = ruleParam.getString(databus, "ELEMENT_TYPE_CODE_B");

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        IDataset listTradeDiscnt = databus.getDataset("TF_F_USER_DISCNT");
        IDataset listUserDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

        /*
         * 外部查询相关信息获取区域
         */
        IData map = new DataMap();
        map.put("ELEMENET_TYPE_CODE_A", strElementTypeCodeA);// element_type_code_a
        map.put("ELEMENET_ID_B", strDiscntCode);// element_id_b
        map.put("ELEMENET_TYPE_CODE_B", strElementTypeCodeB);
//        IDataset listElementLimit = Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_ELEMENTLIMIT_RULE", map);// /td_b_element_limit
        IDataset listElementLimit=UpcCall.queryRelByElementAElementB(strDiscntCode, strDiscntCode, strElementTypeCodeA, strElementTypeCodeB);

        // 第一逻辑单元
        for (Iterator iterUserDiscnt = listUserDiscnt.iterator(); iterUserDiscnt.hasNext();)
        {
            IData userDiscnt = (IData) iterUserDiscnt.next();
            bExistsTwo=UpcCallIntf.hasSpecificOfferRelThisTwoOffer(userDiscnt.getString("DISCNT_CODE"), "D", strDiscntCode, strElementTypeCodeB, "0");
          /*  for (int iListElementLimit = 0; iListElementLimit < listElementLimit.size(); iListElementLimit++)
            {
                if (listElementLimit.getData(iListElementLimit).getString("ELEMENET_ID_B").equals(userDiscnt.getString("DISCNT_CODE")) && userDiscnt.getString("DISCNT_CODE").equals(strDiscntCode))
                {
                    bExistsOne = true;
                    break;
                }
                else
                {
                    bExistsOne = false;
                    continue;
                }
            }*/
            if (bExistsOne)
            {
                break;
            }
        }
        // 第二逻辑单元
        for (Iterator iterTradeDiscnt = listUserDiscnt.iterator(); iterTradeDiscnt.hasNext();)
        {
            IData userDiscnt = (IData) iterTradeDiscnt.next();
            bExistsTwo=UpcCallIntf.hasSpecificOfferRelThisTwoOffer(userDiscnt.getString("DISCNT_CODE"), "D", strDiscntCode, strElementTypeCodeB, "0");
            /*for (int iListElementLimit = 0; iListElementLimit < listElementLimit.size(); iListElementLimit++)
            {
                if (listElementLimit.getData(iListElementLimit).getString("ELEMENT_ID").equals(userDiscnt.getString("DISCNT_CODE")))
                {
                    bExistsTwo = true;
                    break;
                }
            }*/
            if (bExistsTwo)
            {
                break;
            }
        }

        if (bExistsOne && bExistsTwo)
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeAdd4700LimitDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
