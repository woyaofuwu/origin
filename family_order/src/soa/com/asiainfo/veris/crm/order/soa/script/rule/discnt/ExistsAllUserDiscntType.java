
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: GPRS服务用户不能完全取消GPRS优惠 老系统SQL规则翻译代码
 * @author: xiaocl
 */
/*
 * SELECT SUM(recordnum) recordcount FROM (SELECT COUNT(*) recordnum FROM tf_f_user_discnt a WHERE user_id =
 * TO_NUMBER(:USER_ID) AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) AND end_date >=
 * ADD_MONTHS(TRUNC(SYSDATE,'MM'),1) AND NOT EXISTS (SELECT 1 FROM tf_b_trade_discnt WHERE trade_id =
 * TO_NUMBER(:TRADE_ID) AND accept_month = TO_NUMBER(:ACCEPT_MONTH) AND a.user_id = TO_NUMBER(:USER_ID) AND modify_tag =
 * '1' AND discnt_code = a.discnt_code) AND EXISTS (SELECT 1 FROM td_b_dtype_discnt WHERE discnt_code = a.discnt_code
 * AND discnt_type_code = :DISCNT_TYPE_CODE) UNION ALL SELECT COUNT(*) recordnum FROM tf_b_trade_discnt b WHERE trade_id
 * = TO_NUMBER(:TRADE_ID) AND accept_month = TO_NUMBER(:ACCEPT_MONTH) AND b.user_id = TO_NUMBER(:USER_ID) AND b.end_date
 * >= ADD_MONTHS(TRUNC(SYSDATE,'MM'),1) AND modify_tag in('0', 'U') AND EXISTS (SELECT 1 FROM td_b_dtype_discnt WHERE
 * discnt_code = b.discnt_code AND discnt_type_code = :DISCNT_TYPE_CODE) UNION ALL SELECT COUNT(*) recordnum FROM
 * tf_f_user_discnt a WHERE user_id = TO_NUMBER(:USER_ID) AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) AND end_date
 * >= SYSDATE AND EXISTS (SELECT 1 FROM tf_b_trade_discnt WHERE trade_id = TO_NUMBER(:TRADE_ID) AND accept_month =
 * TO_NUMBER(:ACCEPT_MONTH) AND a.user_id = TO_NUMBER(:USER_ID) AND modify_tag = '2' AND end_date >=
 * ADD_MONTHS(TRUNC(SYSDATE,'MM'),1) AND discnt_code = a.discnt_code) AND EXISTS (SELECT 1 FROM td_b_dtype_discnt WHERE
 * discnt_code = a.discnt_code AND discnt_type_code = :DISCNT_TYPE_CODE)
 */
public class ExistsAllUserDiscntType extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsAllUserDiscntType.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsAllUserDiscntType() >>>>>>>>>>>>>>>>>>");

        if(CSBizBean.getVisit().getStaffId().indexOf("CREDIT") > -1)//信控过来不要做校验
        {
            return false;
        }
        /* 自定义区域 */
        boolean bResult = false;
        boolean bExistsOne = false; // 设置第一逻辑点
        boolean bExistsOneO = false; // 设置第一逻辑点
        boolean bExistsOne1 = false;
        boolean bExistsTwo = false; // 设置第二逻辑点
        boolean bExistsTwo0 = false; // 设置第二逻辑点
        boolean bExistsTwo1 = false; // 设置第二逻辑点
        boolean bExistsThree = false; // 设置第san逻辑点
        String strDiscntTpyeCode = ruleParam.getString(databus, "DISCNT_TYPE_CODE");

        // union 第一部分
        // A，如果用户存在有效的优惠资料惠，且结束时间大于下月，且台账没有这类优惠的删除类型的台账
        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");
        IData map = new DataMap();
        map.put("DISCNT_TYPE_CODE", strDiscntTpyeCode);
//        IDataset listDiscntType = Dao.qryByCode("TD_B_DTYPE_DISCNT", "SEL_DISTYPE_BY_TYPECODE", map, Route.CONN_CRM_CEN);
        IDataset listDiscntType= UDiscntInfoQry.queryDiscntsByDiscntType(strDiscntTpyeCode);
        if (IDataUtil.isEmpty(listDiscntType))
        {
            return false;
        }

        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        for (int iListUserDiscnt = 0, iASize = listUserDiscnt.size(); iListUserDiscnt < iASize; iListUserDiscnt++)
        {
            for (int iListDiscntType = 0, iBSize = listDiscntType.size(); iListDiscntType < iBSize; iListDiscntType++)
            {
                if (listUserDiscnt.getData(iListUserDiscnt).getString("DISCNT_CODE").equals(listDiscntType.getData(iListDiscntType).getString("DISCNT_CODE")))
                {
                    bExistsOneO = true;
                    break;
                }
            }
            for (int iListTradeDiscnt = 0, iCSize = listTradeDiscnt.size(); iListTradeDiscnt < iCSize; iListTradeDiscnt++)
            {

                if (!(listTradeDiscnt.getData(iListTradeDiscnt).getString("DISCNT_CODE").equals(listUserDiscnt.getData(iListUserDiscnt).getString("DISCNT_CODE"))
                		&& listTradeDiscnt.getData(iListTradeDiscnt).getString("MODIFY_TAG").equals("1")))
                {
                    bExistsOne1 = true;
                    continue;
                }
                else
                {
                	bExistsOne1 = false;
                    break;
                }
            }
            if (bExistsOneO && !bExistsOne1)
            {
                break;
            }
        }
        if (bExistsOneO && bExistsOne1)
        {
            bExistsOne = true;
        }
        // B，如果用户存在有效的优惠资料惠，且结束时间大于本月，且台账没有这类优惠的删除类型的台账
        // union 第三部分
        for (int iListUserDiscnt = 0, iDSize = listUserDiscnt.size(); iListUserDiscnt < iDSize; iListUserDiscnt++)
        {
            for (int iListDiscntType = 0, iESize = listDiscntType.size(); iListDiscntType < iESize; iListDiscntType++)
            {
                if (listUserDiscnt.getData(iListUserDiscnt).getString("DISCNT_CODE").equals(listDiscntType.getData(iListDiscntType).getString("DISCNT_CODE")))
                {
                    bExistsTwo0 = true;
                    break;
                }
            }
            for (int iListTradeDiscnt = 0, iFSize = listTradeDiscnt.size(); iListTradeDiscnt < iFSize; iListTradeDiscnt++)
            {
                IData listTrade = listTradeDiscnt.getData(iListTradeDiscnt);
                if (listTrade.getString("DISCNT_CODE").equals(listDiscntType.getData(iListUserDiscnt).getString("DISCNT_CODE")) && listTrade.getString("MODIFY_TAG").equals("2"))
                {
                    bExistsTwo1 = true;
                    break;
                }
            }
            if (bExistsTwo0 && bExistsTwo1)
            {
                bExistsTwo = true;
                break;
            }
        }
        // union 第二部分
        for (int iListTradeDiscnt = 0, iGSize = listTradeDiscnt.size(); iListTradeDiscnt < iGSize; iListTradeDiscnt++)
        {
            IData tradediscnt = listTradeDiscnt.getData(iListTradeDiscnt);
            String strdiscnt = tradediscnt.getString("DISCNT_CODE");
            String strmodify = tradediscnt.getString("MODIFY_TAG");

            for (int iListDiscntType = 0, iISize = listDiscntType.size(); iListDiscntType < iISize; iListDiscntType++)
            {
                if (strdiscnt.equals(listDiscntType.getData(iListDiscntType).getString("DISCNT_CODE")) && ("0".equals(strmodify) || "U".equals(strmodify)))
                {
                    bExistsThree = true;
                    break;
                }
            }
            if (bExistsThree)
                break;
        }
        if (bExistsOne || bExistsTwo || bExistsThree)
        {
            bResult = true;
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsAllUserDiscntType() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }
}
