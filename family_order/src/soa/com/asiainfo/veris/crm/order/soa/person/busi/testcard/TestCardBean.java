
package com.asiainfo.veris.crm.order.soa.person.busi.testcard;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TestCardBean extends CSBizBean
{

    public void updateCustName(IData input) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TF_F_CUSTOMER C ");
        sql.append(" SET C.CUST_NAME =:CUST_NAME,C.UPDATE_STAFF_ID=:UPDATE_STAFF_ID,C.UPDATE_DEPART_ID=:UPDATE_DEPART_ID,C.UPDATE_TIME=SYSDATE ");
        sql.append(" WHERE C.CUST_ID = :CUST_ID ");
        Dao.executeUpdate(sql, input);
    }

    public void updateTestCardEndDate(IData input) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TF_F_OWN_SERIALNUMBER_MANAGE M ");
        sql.append(" SET M.END_DATE = TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),M.UPDATE_STAFF_ID=:UPDATE_STAFF_ID,M.UPDATE_DEPART_ID=:UPDATE_DEPART_ID,M.UPDATE_TIME=SYSDATE ");
        sql.append(" WHERE M.SERIAL_NUMBER = :SERIAL_NUMBER ");
        Dao.executeUpdate(sql, input);
    }

}
