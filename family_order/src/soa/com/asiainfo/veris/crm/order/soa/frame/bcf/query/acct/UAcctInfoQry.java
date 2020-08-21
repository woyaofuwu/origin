
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.acct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPayModeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;

public final class UAcctInfoQry
{

    /**
     * 根据CUST_ID查询账户信息,不带路由
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset qryAcctInfoByCustId(String custId) throws Exception
    {
        return qryAcctInfoByCustId(custId, null);
    }

    /**
     * 根据CUST_ID查询账户信息,带路由
     * 
     * @param custId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryAcctInfoByCustId(String custId, String routeId) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", custId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(ACCT_ID) ACCT_ID, TO_CHAR(ACCT_ID) TMP_ACCT_ID, ");
        sql.append("TO_CHAR(CUST_ID) CUST_ID, PAY_NAME, PAY_MODE_CODE, ");
        sql.append("ACCT_PASSWD, NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, ");
        sql.append("BANK_CODE, BANK_ACCT_NO, ");
        sql.append("TO_CHAR(SCORE_VALUE) SCORE_VALUE, CREDIT_CLASS_ID, ");
        sql.append("TO_CHAR(BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE, ");
        sql.append("TO_CHAR(CREDIT_VALUE) CREDIT_VALUE, ");
        sql.append("TO_CHAR(DEBUTY_USER_ID) DEBUTY_USER_ID, DEBUTY_CODE, ");
        sql.append("CONTRACT_NO, DEPOSIT_PRIOR_RULE_ID, ");
        sql.append("ITEM_PRIOR_RULE_ID, ");
        sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, ");
        sql.append("REMOVE_TAG, ");
        sql.append("TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, ");
        sql.append("RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
        sql.append("RSRV_STR10 ");
        sql.append("FROM TF_F_ACCOUNT ");
        sql.append("WHERE CUST_ID = TO_NUMBER(:CUST_ID) ");
        sql.append("AND REMOVE_TAG = '0' ");
        sql.append("ORDER BY ACCT_ID desc ");

        IDataset accountList = Dao.qryBySql(sql, data, routeId);

        if (IDataUtil.isEmpty(accountList))
        {
            return null;
        }

        for (int row = 0, size = accountList.size(); row < size; row++)
        {
            IData account = accountList.getData(row);

            account.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(account.getString("EPARCHY_CODE")));

            String payModeCode = account.getString("PAY_MODE_CODE");

            account.put("PAY_MODE_NAME", UPayModeInfoQry.getPayModeNameByPayModeCode(payModeCode));

            if (StringUtils.isNotBlank(payModeCode))
            {
                account.put("BANK", UBankInfoQry.getBankNameByBankCode(account.getString("BANK_CODE")));
            }
        }

        return accountList;
    }

    /**
     * 根据CUST_ID查询账户信息,路由到CG库
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset qryAcctInfoByCustIdForGrp(String custId) throws Exception
    {

        return qryAcctInfoByCustId(custId, Route.CONN_CRM_CG);
    }

    /**
     * 根据GROUP_ID查询账户信息
     * 
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset qryAcctInfoByGrpId(IData inparams, Pagination page) throws Exception
    {
        String groupId = inparams.getString("GROUP_ID");

        IData dataGroupCustInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(dataGroupCustInfo))
        {
            return null;
        }

        String custId = dataGroupCustInfo.getString("CUST_ID");

        return qryAcctInfoByCustIdForGrp(custId);
    }

}
