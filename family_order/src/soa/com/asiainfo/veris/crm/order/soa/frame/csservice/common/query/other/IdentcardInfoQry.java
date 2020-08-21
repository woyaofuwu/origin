
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 统一凭证
 * 
 * @author J2EE
 */
public class IdentcardInfoQry
{

    /**
     * 根据USER_ID、IDENT_CODE_TYPE查询用户有效凭证
     * 
     * @param userId
     * @param identCodeType
     * @return
     * @throws Exception
     */
    public static IData checkIdentInfoByIdent(String identCode, String bizCode, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("IDENT_CODE", identCode);
        param.put("BUSINESS_CODE", bizCode);
        param.put("SERIAL_NUMBER", serialNumber);

        IDataset results = Dao.qryByCodeParser("TF_B_IDENTCARD_MANAGE", "CHECK_IDENT", param);

        return (IDataUtil.isEmpty(results) ? null : results.getData(0));
    }

    /**
     * 根据USER_ID、IDENT_CODE_TYPE查询用户有效凭证
     * 
     * @param userId
     * @param identCodeType
     * @return
     * @throws Exception
     */
    public static IData checkIdentInfoByIdent(String identCode, String bizCode, String serialNumber, String identType, String identLevel) throws Exception
    {
        IData param = new DataMap();
        param.put("IDENT_CODE", identCode);
        param.put("BUSINESS_CODE", bizCode);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("IDENT_CODE_LEVEL", identLevel);
        param.put("IDENT_CODE_TYPE", identType);

        IDataset results = Dao.qryByCodeParser("TF_B_IDENTCARD_MANAGE", "CHECK_IDENT", param);

        return (IDataUtil.isEmpty(results) ? null : results.getData(0));
    }

    /**
     * 根据IDENT_CODE获取统一凭证信息
     * 
     * @param identCode
     * @return
     * @throws Exception
     */
    public static IData qryIdentInfoByPK(String identCode) throws Exception
    {
        IData param = new DataMap();
        param.put("IDENT_CODE", identCode);

        // StringBuilder sql = new StringBuilder(1000);
        // sql.append("select IDENT_CODE,USER_ID,CUST_ID,USER_TYPE,IDENT_CODE_LEVEL,IDENT_CODE_TYPE,HOME_PROVINCE,START_DATE,END_DATE, ");
        // sql.append(" SERIAL_NUMBER,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
        // sql.append(" from TF_B_IDENTCARD_MANAGE  ");
        // sql.append(" where Ident_code = :IDENT_CODE");
        // sql.append(" and end_date > sysdate");

        IDataset results = Dao.qryByCode("TF_B_IDENTCARD_MANAGE", "SEL_BY_IDENT", param);

        return (IDataUtil.isEmpty(results) ? null : results.getData(0));
    }

    /**
     * 根据USER_ID、SERIAL_NUMBER查询用户有效凭证
     * 
     * @param userId
     * @param identCodeType
     * @return
     * @throws Exception
     */
    public static IData getUserIdentInfo(String identCode, String serialNumber) throws Exception
    {
    	 IData param = new DataMap();
         param.put("IDENT_CODE", identCode);
         param.put("SERIAL_NUMBER", serialNumber);

         IDataset results = Dao.qryByCode("TF_B_IDENTCARD_MANAGE", "SEL_BY_IDENT", param);

         return (IDataUtil.isEmpty(results) ? null : results.getData(0));
    }
    /**
     * 根据USER_ID、IDENT_CODE_TYPE查询用户有效凭证
     * 
     * @param userId
     * @param identCodeType
     * @return
     * @throws Exception
     * 2015-04-28 LXM
     */
    public static IData qryIdentInfoByUserId(String userId, String identCodeType ,String strPwdType) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("IDENT_CODE_TYPE", identCodeType);
        param.put("PWD_TYPE", strPwdType);
        IDataset results = Dao.qryByCode("TF_B_IDENTCARD_MANAGE", "SEL_BY_USERID", param);

        return (IDataUtil.isEmpty(results) ? null : results.getData(0));
    }
}
