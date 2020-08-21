
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;

public class AcctConsignInfoQry
{
    /**
     * 根据账户标识 ACCT_ID 获取账户托收信息
     * 
     * @author chenzm
     * @param acct_id
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getConsignInfoByAcctId(String acctId) throws Exception
    {
        return getConsignInfoByAcctId(acctId, null);
    }

    public static IDataset getConsignInfoByAcctId(String acctId, String routId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT TO_CHAR(INST_ID) INST_ID, PARTITION_ID, ");
        sql.append("TO_CHAR(ACCT_ID) ACCT_ID, PAY_MODE_CODE, EPARCHY_CODE, ");
        sql.append("CITY_CODE, SUPER_BANK_CODE, BANK_CODE, BANK_ACCT_NO, ");
        sql.append("BANK_ACCT_NAME, CONTRACT_ID, CONTRACT_NAME, CONTACT, ");
        sql.append("CONTACT_PHONE, POST_ADDRESS, POST_CODE, CONSIGN_MODE, ");
        sql.append("PRIORITY, PAYMENT_ID, PAY_FEE_MODE_CODE, ");
        sql.append("ASSISTANT_TAG, ");
        sql.append("TO_CHAR(ACCT_BALANCE_ID) ACCT_BALANCE_ID, ACT_TAG, ");
        sql.append("START_CYCLE_ID, END_CYCLE_ID, UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, ");
        sql.append("RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
        sql.append("RSRV_STR10 ");
        sql.append("FROM TF_F_ACCOUNT_CONSIGN T ");
        sql.append("WHERE T.ACCT_ID = TO_NUMBER(:ACCT_ID) ");
        sql.append("AND T.PARTITION_ID = MOD(:ACCT_ID, 10000) ");
        sql.append("AND TO_NUMBER(TO_CHAR(SYSDATE, 'YYYYMM')) BETWEEN T.START_CYCLE_ID AND T.END_CYCLE_ID ");

        IDataset ids = Dao.qryBySql(sql, param, routId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        map.put("SUPER_BANK", UBankInfoQry.getSuperBankNameBySuperBankCode(map.getString("SUPER_BANK_CODE")));
        map.put("BANK", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK", "BANK_CODE", "BANK", map.getString("BANK_CODE")));
        return ids;
    }

    /**
     * @param acctId
     * @param cycleId
     * @return
     * @throws Exception
     */
    public static IDataset getConsignInfoByAcctIdAndCycle(String acctId, String cycleId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("CYCLE_ID", cycleId);
        return Dao.qryByCode("TF_F_ACCOUNT_CONSIGN", "SEL_BY_ACCT_ID", param);
    }

    // 方法入参请不要使用 IData 、map类型，需要指定具体的参数，如有使用请修改
    /*
     * public static IDataset getDelConsignInfoByAcctId(IData param) throws Exception { return
     * Dao.qryByCode("TF_F_ACCOUNT_CONSIGN", "SEL_DEL_BY_ACCTID", param); } public static IDataset
     * getDelConsignInfoByAcctIdAndCycle(IData param) throws Exception { return Dao.qryByCode("TF_F_ACCOUNT_CONSIGN",
     * "SEL_UPD_DEL_ACCT_ID_ACYC", param); }
     */

    public static IDataset getConsignInfoByAcctIdForGrp(String acctId) throws Exception
    {

        return getConsignInfoByAcctId(acctId, Route.CONN_CRM_CG);
    }

}
