
package com.asiainfo.veris.crm.order.soa.script.rule.svc;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 携号服务限制规则，携入号码不能办理某些服务
 * @author: xiaocl
 */

/*
 * SELECT COUNT(1) recordcount FROM tf_b_trade_svc a WHERE trade_id=TO_NUMBER(:TRADE_ID) AND
 * accept_month=TO_NUMBER(:ACCEPT_MONTH) AND a.MODIFY_TAG = '0' AND EXISTS (SELECT 1 FROM td_b_element_limit_np WHERE
 * element_type_code='S' AND limit_tag='0' AND element_id=to_char(a.service_id) AND (eparchy_code = :EPARCHY_CODE OR
 * eparchy_code='ZZZZ') AND SYSDATE BETWEEN start_date AND end_date)
 */

public class SEL_NP_SVC_LIMIT_A extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(SEL_NP_SVC_LIMIT_A.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SEL_NP_SVC_LIMIT_A() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
        if (IDataUtil.isEmpty(listTradeSvc))
        {
            return false;
        }
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        IData map = new DataMap();
        map.put("ELEMENT_TYPE_CODE", "S");
        map.put("LIMIT_TAG", "0");
        map.put("EPARCHY_CODE", strEparchyCode);
//        IDataset listElementLimitNp = Dao.qryByCode("TD_B_ELEMENT_LIMIT_NP", "SEL_ELEMENT_LIMIT_NP", map, Route.CONN_CRM_CEN);
        IDataset elementLimitNps = UpcCall.qryOfferLimitNpByOfferId(null, null, "0");
        IDataset listElementLimitNp = new DatasetList();
        if (IDataUtil.isEmpty(elementLimitNps))
        {
            return false;
        }
        else{//取OFFER_TYPE = 'S' 的
        	for(Object obj : elementLimitNps){
        		IData elementLimitNp = (IData) obj;
        		if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementLimitNp.getString("OFFER_TYPE",""))){
        			listElementLimitNp.add(elementLimitNp);
        		}
        	}
        }
        
        if(IDataUtil.isEmpty(listElementLimitNp)){
        	return false;
        }
        for (int iListTradeSvc = 0, iSize = listTradeSvc.size(); iListTradeSvc < iSize; iListTradeSvc++)
        {
            for (int iListElementLimitNp = 0, iiSize = listElementLimitNp.size(); iListElementLimitNp < iiSize; iListElementLimitNp++)
            {
                if (listTradeSvc.getData(iListTradeSvc).getString("SERVICE_ID").equals(listElementLimitNp.getData(iListElementLimitNp).getString("ELEMENT_ID")))
                {
                    bResult = true;
                    break;
                }
            }
            if (bResult)
            {
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SEL_NP_SVC_LIMIT_A() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
