
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class UserIdentInfoQry
{
	public static IDataset getseqString() throws Exception
	{
		IData param = new DataMap();
	    SQLParser parser = new SQLParser(param);
	    parser.addSQL(" SELECT seq_user_ident_code.nextval OUTSTR FROM dual  ");
	    return Dao.qryByParse(parser);
	}
	 
    public static IDataset checkIdent(String identCode, String businessCode, String identCodeType, String identCodeLevel, String serialNumber) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("IDENT_CODE", identCode);
        inparams.put("BUSINESS_CODE", businessCode);
        inparams.put("IDENT_CODE_TYPE", identCodeType);
        inparams.put("IDENT_CODE_LEVEL", identCodeLevel);
        inparams.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCodeParser("TF_B_IDENTCARD_MANAGE", "CHECK_IDENT", inparams, getEparchyCodeBySn(serialNumber));
    }
    
    public static String getEparchyCodeBySn(String serialNumber) throws Exception {
		String eparchyCode = Route.CONN_CRM_CEN;
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		SQLParser sqlParser = new SQLParser(param);
		sqlParser.addSQL(" select * from res_numseg_hlr where :SERIAL_NUMBER between start_num and end_num and res_state='0' ");
		IDataset switchDatset = Dao.qryByParse(sqlParser, Route.CONN_RES);
	    if (null != switchDatset && switchDatset.size() > 0) {
	    	eparchyCode = switchDatset.getData(0).getString("MGMT_DISTRICT");	
	    } else {
	    	CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码归属TD_M_MOFFICE表无数据");
	    }
	    
	    return eparchyCode;
	}

    public static IDataset queryIdentCode(String userId, String identCode, String contactId) throws Exception
    {
        IData params = new DataMap();
        params.put("CONTACT_ID", contactId);
        params.put("IDENT_CODE", identCode);
        params.put("USER_ID", userId);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT PARTITION_ID,USER_ID,SERIAL_NUMBER,");
        parser.addSQL(" to_char(REGIST_TIME,'yyyy-mm-dd hh24:mi:ss') REGIST_TIME,");
        parser.addSQL(" to_char(IDENT_UNEFFT,'yyyy-mm-dd hh24:mi:ss') IDENT_UNEFFT,");
        parser.addSQL(" CONTACT_ID,UPDATE_TIME,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,");
        parser.addSQL(" RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,");
        parser.addSQL(" RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,");
        parser.addSQL(" IDENT_CODE FROM TF_F_USER_IDENT_CODE WHERE ");
        parser.addSQL(" CONTACT_ID=:CONTACT_ID");
        parser.addSQL(" AND IDENT_CODE=:IDENT_CODE");
        parser.addSQL(" AND USER_ID=:USER_ID");
        parser.addSQL(" AND PARTITION_ID=MOD(:USER_ID, 10000)");
        parser.addSQL(" AND sysdate between regist_time and ident_unefft");
        parser.addSQL(" ORDER BY IDENT_UNEFFT DESC");

        IDataset resultList = Dao.qryByParse(parser);

        return resultList;
    }

    public static IDataset queryIdentInfoByCode(String identCode, String serialNumber) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("IDENT_CODE", identCode);
        inparams.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_B_IDENTCARD_MANAGE", "SEL_BY_IDENT", inparams);
    }

    public static int updIdent2DisableByCode(String identCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("IDENT_CODE", identCode);

        return Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "UPD_DISABLE_BY_IDENT", inparams);
    }

    public static int updIdentByCode(String identCode, String effTime) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("IDENT_CODE", identCode);
        inparams.put("EFFECTIVE_TIME", effTime);

        return Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "UPD_BY_IDENT", inparams);
    }
    
    /**
     * 凭证查询校验
     * @param identCode
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset searchIdentCode(String identCode, String serialNumber)
			throws Exception {
		IData param = new DataMap();
		param.put("IDENT_CODE", identCode);
        param.put("SERIAL_NUMBER", serialNumber);

		return Dao.qryByCode("TF_F_USER_ACCOUNT", "SEL_IDENTCODE_BY_SERIALNUMBER", param);
	}

	/**
	 * 根据USER_ID，MICRO_ACCOUNT，SERIAL_NUMBER 查询用户账号表
	 * @param userId
	 * @param microAccount
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryMicroAccount(String userId,
			String microAccount, String serialNumber) throws Exception {
		IData param = new DataMap();
		param.put("MICRO_ACCOUNT", microAccount);
		param.put("USER_ID", userId);
		param.put("SERIAL_NUMBER", serialNumber);
		
		return Dao.qryByCode("TF_F_USER_ACCOUNT", "SEL_USERACCOUNT_BY_SERIALNUMBER_USERID", param);
	}
	
	/**
	 * 根据USER_ID，SERIAL_NUMBER 查询用户账号表
	 * @param userId
	 * @param microAccount
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryAccountBySerialNumber(String userId,
			String serialNumber) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERIAL_NUMBER", serialNumber);
		
		return Dao.qryByCode("TF_F_USER_ACCOUNT", "SEL_USERACCOUNT_BY_SERIALNUMBER", param);
	}

	/**
	 * 根据USER_ID，PASSWORD，SERIAL_NUMBER查询用户账号日志表
	 * @param userId
	 * @param passwd
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPWD(String userId, String passwd,
			String serialNumber) throws Exception {
		IData param = new DataMap();
		param.put("PASSWORD", passwd);
		param.put("USER_ID", userId);
		param.put("SERIAL_NUMBER", serialNumber);

		return Dao.qryByCode("TF_B_PWD_LOG", "SEL_BY_USERID_PASSWORD", param);
	}

	/**
	 * 根据手机号码查询用户账号日志表
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPWDLog(IData param) throws Exception {
		return Dao.qryByCode("TF_B_PWD_LOG", "SEL_BY_SERIALNUMBER", param);
	}

	/**
	 * 根据手机号码更新用户账号日志表
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static int updatePWDLog(IData param) throws Exception {
		return Dao.executeUpdateByCodeCode("TF_B_PWD_LOG",
				"UPD_BY_SERIALNUMBER", param);
	}

	/**
	 * 更新错误次数
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static int updateErrorNum(IData param) throws Exception {
		return Dao.executeUpdateByCodeCode("TF_F_USER_ACCOUNT",
				"UPD_ERRORNUM_BY_ACCOUNT", param);
	}
	
	/**
	 * chenxy3
	 * 2015-08-10
	 * 使用无account条件的语句
	 */
	public static int updateErrorNum2(IData param) throws Exception {
		return Dao.executeUpdateByCodeCode("TF_F_USER_ACCOUNT",
				"UPD_ERRORNUM_BY_ACCOUNT2", param);
	}
	
	/**
	 * chenxy3
	 * 20160322
	 * 一般凭证下来，终止原有有效凭证
	 */
	public static int updateErrorNum3(IData param) throws Exception {
		return Dao.executeUpdateByCodeCode("TF_F_USER_ACCOUNT",
				"UPD_ERRORNUM_BY_ACCOUNT3", param);
	}

}
