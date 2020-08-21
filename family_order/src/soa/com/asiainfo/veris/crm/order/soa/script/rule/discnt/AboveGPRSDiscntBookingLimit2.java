
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTimeUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 每个月申请的数据流量包不能超过20次
 * @author: xiaocl
 */

/*
 * select 1 as RSRV_STR1 from dual where (select count(1) from ( select a.user_id, a.discnt_code from tf_f_user_discnt
 * a, td_b_dtype_discnt b Where a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000) and a.user_id = :USER_ID and
 * a.discnt_code = b.discnt_code and b.discnt_type_code = :DISCNT_TYPE_CODE and sysdate between a.start_date and
 * a.end_date union all select c.user_id, c.discnt_code from tf_b_trade_discnt c, td_b_dtype_discnt b Where
 * c.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) and c.trade_id = :TRADE_ID and c.user_id = :USER_ID and
 * c.modify_tag = '0' and c.discnt_code = b.discnt_code and b.discnt_type_code = :DISCNT_TYPE_CODE and sysdate <
 * c.end_date) ) > :RSRV_NUM1 and (select count(1) from tf_b_trade_discnt c, td_b_dtype_discnt b Where c.accept_month =
 * TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) and c.trade_id = :TRADE_ID and c.user_id = :USER_ID and c.modify_tag = '0' and
 * c.discnt_code = b.discnt_code and b.discnt_type_code = :DISCNT_TYPE_CODE and sysdate < c.end_date) > 0
 */
public class AboveGPRSDiscntBookingLimit2 extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(AboveGPRSDiscntBookingLimit2.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 AboveGPRSDiscntBookingLimit2() >>>>>>>>>>>>>>>>>>");

        if(CSBizBean.getVisit().getStaffId().indexOf("CREDIT") > -1)//信控过来不要做校验
        {
            return false;
        }
        
        boolean bResult = false;
        String strDiscntTpyeCode = ruleParam.getString(databus, "DISCNT_TYPE_CODE");// 8
        int iRsrvNum = ruleParam.getInt(databus, "RSRV_NUM1");// 20
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");
        if (IDataUtil.isEmpty(listTradeDiscnt) || IDataUtil.isEmpty(listUserDiscnt))
        {
            return false;
        }
        IDataset listDtypeDiscnt =new DatasetList();
        IDataset listDtypes= UDiscntInfoQry.queryDiscntsByDiscntType(strDiscntTpyeCode);
        IDataset commparaDataset = CommparaInfoQry.getCommpara("CSM", "2048", "", CSBizBean.getTradeEparchyCode());      
        for(int i=0;i<listDtypes.size();i++){
        	IData discnt=listDtypes.getData(i);       	
        	for(int j=0; j<commparaDataset.size();j++){
        		   IData commpara=commparaDataset.getData(j);
        		   if(discnt.getString("DISCNT_CODE").equals(commpara.getString("PARA_CODE1"))){
        			   listDtypeDiscnt.add(discnt);
        		    	}	
        		}
        	}
        
        if (IDataUtil.isEmpty(listDtypeDiscnt))
        {
            return false;
        }

        int iCount0 = 0;
        for (int i = 0, iASize = listUserDiscnt.size(); i < iASize; i++)
        {
            IData UserDiscnt = listUserDiscnt.getData(i);
            for (int iListDtype = 0, iBSize = listDtypeDiscnt.size(); iListDtype < iBSize; iListDtype++)
            {
                IData tradeDtypeDiscnt = listDtypeDiscnt.getData(iListDtype);
                if (UserDiscnt.getString("DISCNT_CODE").equals(tradeDtypeDiscnt.getString("DISCNT_CODE")) && BreTimeUtil.getCurDate(databus).compareTo(UserDiscnt.getString("END_DATE")) < 0)
                {
                    iCount0 = iCount0 + 1;

                }
            }
        }
        // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201119","[iCount0]"+iCount0);
        int iCount1 = 0;
        for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
        {
            IData tradeDiscnt = (IData) iter.next();
            for (int iListDtype = 0, iBSize = listDtypeDiscnt.size(); iListDtype < iBSize; iListDtype++)
            {
                IData tradeDtypeDiscnt = listDtypeDiscnt.getData(iListDtype);
                if (tradeDiscnt.getString("DISCNT_CODE").equals(tradeDtypeDiscnt.getString("DISCNT_CODE")) && tradeDiscnt.getString("MODIFY_TAG").equals("0") && BreTimeUtil.getCurDate(databus).compareTo(tradeDiscnt.getString("END_DATE")) < 0)
                {
                    iCount1 = iCount1 + 1;

                }
            }
        }
        if ((iCount1 + iCount0) > iRsrvNum && iCount1 > 0)
            bResult = true;
        // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201119","[bResult]"+bResult);
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 AboveGPRSDiscntBookingLimit2() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
