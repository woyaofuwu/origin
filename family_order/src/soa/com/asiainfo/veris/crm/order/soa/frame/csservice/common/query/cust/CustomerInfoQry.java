
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust; 

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.statement.Parameter;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class CustomerInfoQry
{
    public static int[] executeBatch(StringBuilder sql, Parameter[] params, String routeId) throws Exception
    {
        return Dao.executeBatch(sql, params, routeId);
    }

    /**
     * 输入服务号码取所有非正常用户 ADD BY HH
     */
    public static IDataset getABNormalCustInfoBySn(String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_ALL_BY_SN_ABNORMAL", params);
    }

    /**
     * 根据挂账集团名称，获取挂账集团客户信息
     * 
     * @author fengsl
     * @date 2013-02-26
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getAccountUniteByCustName(IData inparam, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(inparam);
        parser.addSQL("SELECT a.CUST_ID,a.CUST_NAME,b.USER_ID,b.SERIAL_NUMBER,b.RSRV_STR1,b.OPEN_DATE  ");
        parser.addSQL(" FROM TF_F_CUSTOMER a, TF_F_USER b WHERE 1=1");
        parser.addSQL(" AND a.CUST_NAME LIKE '%' || :CUST_NAME || '%'");
        parser.addSQL(" AND a.CUST_ID = b.CUST_ID");
        parser.addSQL(" AND a.partition_id = MOD(to_number(b.cust_id),10000)");
        parser.addSQL(" AND b.PRODUCT_ID = '7000'");
        parser.addSQL(" AND a.REMOVE_TAG = '0'");
        parser.addSQL(" AND b.REMOVE_TAG = '0'");
        parser.addSQL(" AND rownum < 101");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);

    }

    /**
     * 根据挂账集团名称，获取挂账集团客户信息
     * 
     * @author fuzn
     * @date 2013-05-21
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getAccountUniteByCustPersonName(IData inparam, Pagination pagination, String routeID) throws Exception
    {
        SQLParser parser = new SQLParser(inparam);

        parser.addSQL("SELECT c.CUST_ID, c.CUST_NAME, u.USER_ID, u.SERIAL_NUMBER, u.RSRV_STR1, u.OPEN_DATE");
        parser.addSQL("  FROM tf_f_customer c, tf_f_user u, tf_f_user_product up");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL("   AND c.cust_name = :CUST_NAME");
        parser.addSQL("   AND c.cust_id = u.cust_id");
        parser.addSQL("   AND c.partition_id = MOD(to_number(u.cust_id),10000)");
        parser.addSQL("   AND u.user_id = up.user_id");
        parser.addSQL("   AND u.partition_id = mod(to_number(up.user_id), 10000)");
        parser.addSQL("   AND up.product_id = '7000'");
        parser.addSQL("   AND c.remove_tag = '0'");
        parser.addSQL("   AND u.remove_tag = '0'");
        parser.addSQL("   AND (SYSDATE between up.start_date and up.end_date)");
        parser.addSQL("   AND up.main_tag = '1'");

        return Dao.qryByParse(parser, pagination, routeID);

    }

    /**
     * 根据SERIAL_NUMBER查客户信息
     * 
     * @author shixb
     * @version 创建时间：2009-5-14 下午04:08:25
     */
    public static IDataset getCustAndUserInfoBySn(IData params, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUST_USER_BY_SN", params, pagination);
    }

    public static IDataset getCustByCustidAndPsptid(String custId, String psptId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("IDCARDNUM", psptId);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTID_PSPTID", param);
    }

    public static IDataset getCustByCustidAndPsptid2(String custId, String idcardNum) throws Exception
    {

        IData param = new DataMap();
        param.put("CUST_ID", CSBizBean.getVisit().getStaffId());
        param.put("IDCARDNUM", idcardNum);

        // 1、查询客户经理名下的所有用户
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM TF_F_CUSTOMER C  WHERE  C.CUST_ID=to_number(:CUST_ID) AND C.PSPT_ID=:IDCARDNUM  ");

        return Dao.qryByParse(parser);
    }

    public static IDataset getCustByCustidAndPsptidAndType(String custId, String psptId, String type) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("IDCARDNUM", psptId);
        param.put("IDCARDTYPE", type);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTID_TYPE_PSPTID", param);
    }

    public static IDataset getCustByCustidAndPsptidAndType2(String custId, String psptId, String type) throws Exception
    {

        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("IDCARDNUM", psptId);
        param.put("IDCARDTYPE", type);

        // 1、查询客户经理名下的所有用户
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM TF_F_CUSTOMER C  WHERE  C.CUST_ID=:CUST_ID AND C.PSPT_TYPE_CODE=:IDCARDTYPE AND C.PSPT_ID=:IDCARDNUM  ");

        return Dao.qryByParse(parser);
    }

    /**
     * 根据服务号码获取客户资料及个人客户资料表信息 ADD BY HH
     */
    public static IDataset getCustInfoByAllSn(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_ALL_BY_SN", params, pagination);
    }

    /**
     * 根据CUST_ID获取客户资料及个人客户资料表信息 ADD BY HH
     */
    public static IDataset getCustInfoByCustId(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTID_PERSON", params, pagination);
    }

    /**
     * 根据CUST_ID查询客户信息
     */
    public static IDataset getCustInfoByCustIdOnly(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTID1", inparams);
    }

    /**
     * 根据CUST_NAME获取客户资料及个人客户资料表信息 ADD BY HH
     */
    public static IDataset getCustInfoByCustName(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTNAME_PERSON", params, pagination);
    }

    /**
     * 根据证件号码和证件类型获取客户信息
     */
    public static IDataset getCustInfoByPspt(String psptTypeCode, String psptId) throws Exception
    {
        IData param = new DataMap();
        param.put("PSPT_TYPE_CODE", psptTypeCode);
        param.put("PSPT_ID", psptId);

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT_PSPTTYPE", param);
    }
    
   /**
    * 根据证件号码和证件类型获取客户custId
    * @param psptId
    * @return
    * @throws Exception
    */
    public static IDataset getCustIdByPspt(String psptId) throws Exception
    {
        IData param = new DataMap();
        param.put("PSPT_ID", psptId);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUSTID_BY_PSPT", param);
    }

    /**
     * 根据CUST_TYPE,PSPT_TYPE_CODE,PSPT_ID获取客户资料表
     */
    public static IDataset getCustInfoByPsptCustType(IData params, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT", params, pagination);
    }

    /**
     * 根据CUST_TYPE,PSPT_TYPE_CODE,PSPT_ID获取客户资料表
     * 
     * @author chenzm
     * @param cust_type
     *            //客户类型
     * @param pspt_type_code
     *            //证件类型
     * @param pspt_id
     *            //证件号码
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getCustInfoByPsptCustType(String cust_type, String pspt_type_code, String pspt_id) throws Exception
    {
        IData params = new DataMap();
        params.put("CUST_TYPE", cust_type);
        params.put("PSPT_TYPE_CODE", pspt_type_code);
        params.put("PSPT_ID", pspt_id);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT", params);
    }
    
    /**
     * 根据PSPT_TYPE_CODE,PSPT_ID获取客户资料表
     * 
     * @author liquan
     * @param cust_type
     *            //客户类型
     * @param pspt_type_code
     *            //证件类型
     * @param pspt_id
     *            //证件号码
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getCustInfoByPsptCustType1(String pspt_type_code, String pspt_id, String pspt_name,String cust_id) throws Exception
    {   
        String sqlref  = "";
        IData params = new DataMap();
        params.put("PSPT_TYPE_CODE", pspt_type_code);
        params.put("PSPT_ID", pspt_id);
        params.put("PSPT_NAME", pspt_name);
        if(cust_id!=null&&cust_id.trim().length()>0){
            params.put("CUST_ID", cust_id);
            sqlref = "SEL_BY_PSPT_1";
        }else {
            sqlref = "SEL_BY_PSPT_2";
        }
        return Dao.qryByCode("TF_F_CUSTOMER", sqlref, params);
    }
    public static IDataset getCustInfoByPsptCustType2(String pspt_type_code, String pspt_id, String pspt_name) throws Exception
    {
        String sqlref  = "SEL_BY_PSPT_3";
        IData params = new DataMap();
        params.put("PSPT_TYPE_CODE", pspt_type_code);
        params.put("PSPT_ID", pspt_id);
        params.put("PSPT_NAME", pspt_name);
        return Dao.qryByCode("TF_F_CUSTOMER", sqlref, params);
    }
    //REQ201906040031 一证五号校验规则优化，去掉身份证类型的条件来查询
    public static IDataset getCustInfoByPsptCustType3(String pspt_type_code,String pspt_id, String pspt_name) throws Exception
    {
        String sqlref  = "SEL_BY_PSPT_3";
        IData params = new DataMap();
        params.put("PSPT_TYPE_CODE", pspt_type_code);
        params.put("PSPT_ID", pspt_id);
        params.put("PSPT_NAME", pspt_name);
        return Dao.qryByCodeParser("TF_F_CUSTOMER", sqlref, params);
    }
    /**
     * 根据PSPT_TYPE_CODE,PSPT_ID获取客户资料表
     * 
     * @author liquan
     * @param cust_type
     *            //客户类型
     * @param pspt_type_code
     *            //证件类型
     * @param pspt_id
     *            //证件号码
     * @return IDataset
     * @throws Exception
     *//*
    public static IDataset getCustInfoByPsptCustType2(String pspt_type_code, String pspt_id, String pspt_name,String cust_id) throws Exception
    {
        String sqlref  = "";
        IData params = new DataMap();
        params.put("PSPT_TYPE_CODE", pspt_type_code);
        params.put("PSPT_ID", pspt_id);
        params.put("PSPT_NAME", pspt_name);
        if(cust_id!=null&&cust_id.trim().length()>0){
            params.put("CUST_ID", cust_id);
            sqlref = "SEL_BY_PSPT_1";
        }else {
            sqlref = "SEL_BY_PSPT_2";
        }
        return Dao.qryByCode("TF_F_CUST_PERSON", sqlref, params);
    }*/

    /**
     * 根据CUST_TYPE,PSPT_ID获取客户资料表
     */
    public static IDataset getCustInfoByPsptCustTypeForSpecial(IData params, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT_TYPE0OR1", params, pagination);
    }

    public static IDataset getCustInfoByPsptName(IData params, Pagination pagination) throws Exception
    {

        params.put("CUST_TYPE", "0");
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT_CUSTNAME", params, pagination);
    }

    public static IDataset getCustInfoByPsptName(String cust_name, String cust_type, String pspt_type_code, String pspt_id) throws Exception
    {
        IData params = new DataMap();

        params.put("CUST_NAME", cust_name);
        params.put("CUST_TYPE", cust_type);
        params.put("PSPT_TYPE_CODE", pspt_type_code);
        params.put("PSPT_ID", pspt_id);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT_CUSTNAME", params);
    }

    /**
     * 根据SERIAL_NUMBER查客户信息
     * 
     * @author shixb
     * @version 创建时间：2009-5-14 下午04:08:25
     */
    public static IDataset getCustInfoBySn(IData params, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_SERIAL_NUMBER", params, pagination);
    }

    /**
     * 根据SN查有效的客户资料
     */
    public static IDataset getCustInfoBySnValid(IData params, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_SN_VALID", params, pagination);
    }

    public static IDataset getCustomerAndPersonInfoByPspt(String custType, String psptTypeCode, String psptId) throws Exception
    {
        IData params = new DataMap();
        params.put("CUST_TYPE", custType);
        params.put("PSPT_TYPE_CODE", psptTypeCode);
        params.put("PSPT_ID", psptId);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT_PERSON", params);
    }

    /**
     * /**
     * 
     * @Description：根据SERIAL_NUMBER查正常客户信息
     * @author wusf
     * @date 2009-11-22
     * @param params
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getCustomerBySn(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_SN", params, pagination);
    }

    public static IDataset getCustomerInfoByUserId(String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_USERID", params);
    }

    /**
     * 获取客户资料
     * 
     * @param params
     * @return
     * @throws Exception
     *             wangjx 2013-3-28
     */
    public static IDataset getCustUserInfoByPspt(IData params) throws Exception
    {

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT_PERSON_WIDENETUSER", params);
    }

    /**
     * 根据CUST_TYPE,PSPT_TYPE_CODE,PSPT_ID获取客户资料、个人客户资料信息、宽带用户信息 2010-4-6
     * 
     * @author xj
     */
    public static IDataset getCustUserInfoByPspt(IData params, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT_PERSON_WIDENETUSER", params, pagination);
    }

    /**
     * 输入服务号码取正常用户信息 ADD BY HH
     */
    public static IDataset getNormalCustInfoBySn(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_ALL_BY_SN_NORMAL", params, pagination);
    }

    public static IDataset getNormalCustInfoBySN(String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_ALL_BY_SN_NORMAL", params);
    }

    public static IDataset getNormalCustInfoByUserId(String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUSTINFO_BY_USERID", params);
    }

    public static IDataset getNPUFitUser(String userIdA, String userIdB, String userIdC, String userIdD) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID_A", userIdA);
        params.put("USER_ID_B", userIdB);
        params.put("USER_ID_C", userIdC);
        params.put("USER_ID_D", userIdD);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_USERID_NPUSER_INFO", params);
    }

    public static IDataset getNPUNotFitUser(String serialNumberA, String serialNumberB, String serialNumberC, String serialNumberD, String serialNumberE) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER_A", serialNumberA);
        params.put("SERIAL_NUMBER_B", serialNumberB);
        params.put("SERIAL_NUMBER_C", serialNumberC);
        params.put("SERIAL_NUMBER_D", serialNumberD);
        params.put("SERIAL_NUMBER_E", serialNumberE);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_USERID_NPUSER_NOTFIT", params);
    }

    /**
     * 根据CUST_TYPE,PSPT_TYPE_CODE,PSPT_ID获取客户资料及个人客户资料表信息
     */
    public static IDataset getPersonCustInfoByPsptCustType(String psptId, String psptTypeCode, String custType, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("CUST_TYPE", custType);
        params.put("PSPT_TYPE_CODE", psptTypeCode);
        params.put("PSPT_ID", psptId);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT_PERSON", params, pagination);
    }

    /**
     * 根据SERIAL_NUMBER查客户信息
     * 
     * @author liaoyi
     * @version 创建时间：2009-12-05 上午10:08:25
     */
    public static IDataset getVIPNCustAndUserInfoBySn(IData params, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUST_USER_BY_SN_VIPN", params, pagination);
    }

    /**
     * 查询vpn信息（小栏框）
     * 
     * @param user_id
     * @return
     * @throws Exception
     */
    public static IDataset getVPNInfoByUserId(String user_id) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_USERID_VPN", params);
    }

    /**
     * 根据USER_ID查询客户信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IData qryCustInfo(String custId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("CUST_ID", custId);

        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (custInfo == null)
        {
            return null;
        }

        IData personInfo = UcaInfoQry.qryPerInfoByCustId(custId);
        if (personInfo != null && !personInfo.isEmpty())
        {
            custInfo.putAll(personInfo);
        }

        return custInfo;
    }

    public static IDataset queryCustInfoByCustTypeCustName(String custType, String custName) throws Exception
    {
        IData params = new DataMap();
        params.put("CUST_TYPE", custType);
        params.put("CUST_NAME", custName);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTNAME_GROUP", params);
    }

    public static IDataset queryCustInfoBySN(String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_SERIAL_NUMBER_2", params);
    }    
    public static IDataset queryGroupCustInfoBySN(String serialNumber) throws Exception {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_SERIAL_NUMBER_GROUP", params);
    }

    public static IDataset queryCustomerInfo(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT p.pspt_type,t.pspt_id FROM tf_f_customer t, td_s_passporttype p WHERE t.cust_id=:CUST_ID ");
        parser.addSQL(" AND t.pspt_type_code=p.pspt_type_code AND t.eparchy_code=p.eparchy_code ");
        parser.addSQL(" AND ROWNUM<2 ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    public static IDataset queryCustUserInfoBySN(String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_ALL_BY_SN", params);
    } 
    /**
     * REQ201506020023 证件办理业务触发完善客户信息
     * 捞取TF_B_AUTO_UPD_PSPTID未处理的数据进行调用资料变更服务处理.
     * */
    public static IDataset queryAutoUpdPsptid(IData inputData) throws Exception
    {
    	SQLParser parser = new SQLParser(inputData); 
        parser.addSQL(" select t.*  from TF_B_AUTO_UPD_PSPTID t where t.auto_tag='0' AND T.CUST_NAME<>'undefined' and t.pspt_addr<>'undefined' and t.serial_number like '1%' and rownum<120"); 
        return Dao.qryByParse(parser);
    } 
    public static IDataset querySamePsIdAndNameCust(String custId)throws Exception{
    	IData params = new DataMap();
        params.put("CUST_ID", custId);
        return Dao.qryByCode("TF_F_CUSTOMER", "QRY_CUST_BY_SAME_PSPT_AND_NAME", params);
    } 
    
    /**
	 * REQ201604290001 关于调整黑名单系统功能的需求-20160405
	 * chenxy3 20160606
	 * */
    public static IDataset getCustInfoByCustidAndPsptid(String custId, String psptId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("PSPT_ID", psptId);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUST_BY_CUSTID_PSPTID", param);
    }
    
    public static IDataset getCustInfoPsptBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUSTOMER_BY_PSPT", param);
    }
    
    public static IDataset getUserPsptByUserid(String strUserID, String strUserType) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", strUserID);
        param.put("USER_TYPE", strUserType);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_USERPSPT_BY_USERID", param);
    }
    
    public static IDataset getPsptByUserid(String strUserID) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", strUserID);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_PSPT_BY_USERID", param);
    }
    
    public static IDataset getUserPsptByPsptID(String strCustName, String strPsptID) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_NAME", strCustName);
        param.put("PSPT_ID", strPsptID);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_USERPSPT_BY_PSPTID", param);
    }
    
    public static IDataset getCustomerByCustID(String strCustID) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", strCustID);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUSTOMER_BY_CUSTID", param);
    }
    
    public static IDataset getCustPersonByCustID(String strCustID) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", strCustID);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUSTPERSON_BY_CUSTID", param);
    }
    
    public static IDataset getNormalCustInfoByUserIdPT(String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUSTINFO_BY_USERIDPT", params);
    }
	/**
	 * 
	 * @param inparam
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpCustInfoByCustId(IData inparam, Pagination pagination) throws Exception
	{
		SQLParser parser = new SQLParser(inparam);
		parser.addSQL("SELECT b.CUST_ID");
		parser.addSQL(" FROM TF_F_CUST_GROUP b WHERE 1=1");
		parser.addSQL(" AND b.CUST_ID = :CUST_ID");
		parser.addSQL(" AND b.REMOVE_TAG = '0'");
		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);

	}
	 /**
		 * 根据客户标识号获取个人客户资料
		 * 
		 * @param param
		 * @return IDataset
		 * @throws Exception
		 */
		public static IDataset getCustInfoByCustIdPk(String cust_id) throws Exception
		{
			IData param = new DataMap();
			param.put("CUST_ID", cust_id);
			return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PK", param);
		}
		
		//REQ201907300019 关于企业宽带实名制补录信息开发需求
		public static IDataset getCustInfoAddr(String cust_id) throws Exception
		{
			IData param = new DataMap();
	        param.put("CUST_ID", cust_id);
			
			SQLParser parser = new SQLParser(param);
			parser.addSQL("SELECT b.CUST_ID");
			parser.addSQL(" FROM TF_F_CUSTOMER_ADDR b WHERE 1=1");
			parser.addSQL(" AND b.CUST_ID = :CUST_ID");
			parser.addSQL(" AND b.REMOVE_TAG = '0'");
			return Dao.qryByParse(parser);
		}
		
}
