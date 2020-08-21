
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class PasswordErrLogQry
{
    public static IDataset getProblemManageInfo(IData input, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_J2EETEST_PROBLEM", "SEL_ALL_PROBLEM", input, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryPasswordErrLogBySerialNumber(String serialNumber, Pagination pagination) throws Exception
    {

        if ("".equals(serialNumber))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_707);
        }
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select t.log_id, t.serial_number, t.login_passwd,  t.in_mode_code  in_mode, to_char(t.log_time, 'yyyy-mm-dd hh24:mi:ss') log_time, t.code_type_code ");
        parser.addSQL(" from   tf_b_passwd_errorlog t where 1=1");
        parser.addSQL(" and t.serial_number = :SERIAL_NUMBER ");

        parser.addSQL(" and log_time > trunc(sysdate)");
        IDataset result = Dao.qryByParse(parser, pagination);
        return result;
    }

    /**
     * 查询当天错误密码次数
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryPasswordErrLogCount(String userId, String passwdTypeCode, String inModeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PASSWD_TYPE_CODE", passwdTypeCode);
        param.put("IN_MODE_CODE", inModeCode);
        return Dao.qryByCode("TF_F_USER_PASSWD_ERROR", "SEL_PASSWD_ERROR_COUNT", param);
    }
}
