
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CustPersonInfoQry
{
    /**
     * 查询集团用户的网外信息
     * 
     * @param param
     * @throws Exception
     */
    public static IDataset getMemInfo(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.*,B.CUST_NAME,B.PSPT_TYPE_CODE,B.PSPT_ID,'' POST_CODE,'' ADDR FROM TF_F_USER A,TF_F_CUSTOMER B  ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND A.REMOVE_TAG='0' ");
        parser.addSQL(" AND B.REMOVE_TAG='0' ");
        parser.addSQL(" AND A.CUST_ID=B.CUST_ID ");
        parser.addSQL(" AND A.SERIAL_NUMBER=:SERIAL_NUMBER_A");
        parser.addSQL(" AND A.USER_ID=:USER_ID_MEM");
        parser.addSQL(" AND A.PARTITION_ID=MOD(to_number(:USER_ID_MEM), 10000)");
        parser.addSQL(" AND ROWNUM='1'");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset getOweFeeByPspt(IData inparam) throws Exception
    {

        IData param2 = new DataMap();
        IData res;
        IDataset midReses, midReses2, midReses3;
        IData midRes, midRes2, midRes3;
        IDataset result = new DatasetList();
        String eparchyCode = inparam.getString("EPARCHY_CODE");
        inparam.put("CUST_TYPE", "0");
        midReses = CustomerInfoQry.getCustInfoByPsptCustType(inparam, null);
        int custSum = midReses.size();

        int tag = 0;
        int getMode = 0;
        param2.put("ID_TYPE", "1");
        param2.put("START_ACYC_ID", "0");
        param2.put("END_ACYC_ID", "999");
        param2.put("INTEGRATE_ITEM_CODE", "0");
        param2.put("X_GETMODE", "3");
        for (int i = 0; i < custSum; i++)
        {
            midRes = (IData) midReses.get(i);
            inparam.put("CUST_ID", midRes.getString("CUST_ID"));
            midReses2 = UserInfoQry.getUserInfoByCustId(midRes.getString("CUST_ID"), null);
            int userSum = midReses2.size();
            int j = 0;
            while (true)
            {
                midRes2 = (IData) midReses2.get(j);
                param2.put("ID", midRes2.getString("USER_ID"));
                if (j < userSum && tag < 20 && getMode == 0)
                {
                    midReses3 = CSAppCall.call("QCS_CheckOweUser", param2);
                    midRes3 = (IData) midReses3.get(0);
                    if (midRes3.getDouble("RSRV_NUM1", 0) > 0)
                    {
                        getMode = 1;
                        tag++;
                        j++;
                        res = new DataMap();
                        res.put("RSRV_NUM1", midRes3.getString("RSRV_NUM1", "0"));
                        res.put("RSRV_NUM2", midRes3.getString("RSRV_NUM2", "0"));
                        res.put("RSRV_NUM3", midRes3.getString("RSRV_NUM3", "0"));
                        res.put("USER_ID", midRes2.getString("USER_ID"));
                        res.put("SERIAL_NUMBER", midRes2.getString("SERIAL_NUMBER"));
                        result.add(res);
                    }
                    else
                    {
                        tag++;
                        j++;
                    }
                }
                else if (j < userSum && (tag >= 20 || getMode == 1))
                {
                    tag++;
                    j++;
                }
                else
                {
                    i++;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 根据用户ID查询个人客户信息和主产品信息
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryCustAndProductInfoByUserId(String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_CUST_PERSON", "SEL_PRODUCT_BY_USER_ID", param, routeId);
    }
    
    /**
     * 根据CUST_ID查询用户使用人信息
     * @author Yanwu
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset qryCustPersonOtherByCustId(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        return Dao.qryByCode("TF_F_CUST_PERSON", "SEL_BY_CUSTID_PERSON_OTHER", param);
    }
    
    public static IDataset qryCustPersonOtherByCustRouteId(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        return Dao.qryByCode("TF_F_CUST_PERSON", "SEL_BY_CUSTID_PERSON_OTHER", param,Route.CONN_CRM_CG);
    }

    /**
     * 根据客户标识，客户类型查询个人客户资料
     * 
     * @author tangxy
     * @param pspt_type_code
     * @param pspt_id
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryPerCustInfoByCustIDCustType(String custId, String CustType) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("CUST_TYPE", CustType);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTID_PERSON", param);
    }

    /**
     * 根据客户名称，客户类型查询个人客户资料
     * 
     * @author tangxy
     * @param pspt_type_code
     * @param pspt_id
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryPerCustInfoByCustNameCustType(String custName, String CustType) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("CUST_TYPE", CustType);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTNAME_PERSON", param);
    }

    /**
     * 根据客户名称(CUST_NAME)获取客户资料
     */
    public static IDataset qryPerInfoByCustName(IData params, Pagination pagination) throws Exception
    {

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT partition_id,to_char(cust_id) cust_id,pspt_type_code,pspt_id,to_char(pspt_end_date,'yyyy-mm-dd hh24:mi:ss') pspt_end_date, ");
        sql.append("pspt_addr,cust_name,sex,to_char(birthday,'yyyy-mm-dd hh24:mi:ss') birthday,nationality_code,local_native_code,population, ");
        sql.append("language_code,folk_code,phone,post_code,post_address,fax_nbr,email,contact,contact_phone,home_address,work_name,work_depart, ");
        sql.append("job,job_type_code,educate_degree_code,religion_code,revenue_level_code,marriage,character_type_code,webuser_id,web_passwd,contact_type_code,community_id ");
        sql.append("FROM tf_f_cust_person ");
        sql.append("WHERE cust_name LIKE '%'||:CUST_NAME||'%' ");

        return Dao.qryBySql(sql, params, pagination);
    }

    /**
     * 根据客户标识PSPT_ID获取个人客户资料表
     */
    public static IDataset qryPerInfoByPsptId(IData params, Pagination pagination) throws Exception
    {

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT TO_CHAR(CUST_ID) CUST_ID, PSPT_TYPE_CODE, PSPT_ID, ");
        sql.append("TO_CHAR(PSPT_END_DATE, 'yyyy-mm-dd hh24:mi:ss') PSPT_END_DATE, PSPT_ADDR, ");
        sql.append("CUST_NAME, SEX, TO_CHAR(BIRTHDAY, 'yyyy-mm-dd hh24:mi:ss') BIRTHDAY, ");
        sql.append("NATIONALITY_CODE, LOCAL_NATIVE_CODE, POPULATION, LANGUAGE_CODE, FOLK_CODE, ");
        sql.append("PHONE, POST_CODE, FAX_NBR, EMAIL, CONTACT, CONTACT_PHONE, HOME_ADDRESS, ");
        sql.append("WORK_NAME, WORK_DEPART, JOB, JOB_TYPE_CODE, EDUCATE_DEGREE_CODE, ");
        sql.append("RELIGION_CODE, REVENUE_LEVEL_CODE, MARRIAGE, CHARACTER_TYPE_CODE, ");
        sql.append("WEBUSER_ID, WEB_PASSWD, CONTACT_TYPE_CODE, COMMUNITY_ID ,EPARCHY_CODE ,CITY_CODE ,POST_ADDRESS ");
        sql.append("FROM TF_F_CUST_PERSON ");
        sql.append("WHERE PSPT_TYPE_CODE = :PSPT_TYPE_CODE ");
        sql.append("AND PSPT_ID = :PSPT_ID ");

        return Dao.qryBySql(sql, params, pagination);
    }

    /**
     * 根据PSPT_TYPE_CODE,PSPT_ID查询个人客户资料
     * 
     * @author chenzm
     * @param pspt_type_code
     * @param pspt_id
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryPerInfoByPsptId(String pspt_type_code, String pspt_id) throws Exception
    {

        IData params = new DataMap();
        params.put("PSPT_TYPE_CODE", pspt_type_code);
        params.put("PSPT_ID", pspt_id);

        return qryPerInfoByPsptId(params, null);
    }
    
    public static IDataset getPerInfoByCustId(String custId) throws Exception{
      	 IData params = new DataMap();
           params.put("CUST_ID", custId);
           return Dao.qryByCode("TF_F_CUST_PERSON", "SEL_BY_PK", params);
       }
    
    /**
     * 根据PSPT_TYPE_CODE,PSPT_ID查询个人客户资料
     * 
     * @author liquan
     * @param pspt_type_code
     * @param pspt_id
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryPerInfoByPsptId_1( String pspt_id,String cust_name) throws Exception
    {
        IData params = new DataMap();
        params.put("PSPT_ID", pspt_id);
        params.put("PSPT_NAME", cust_name);

        return Dao.qryByCode("TF_F_CUST_PERSON", "SEL_BY_PSPT_4", params);
    } 
    
    /**
     * 根据PSPT_TYPE_CODE,PSPT_ID查询个人客户资料
     * 
     * @author liquan
     * @param pspt_type_code
     * @param pspt_id
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryPerInfoByPsptId_2( String pspt_id,String cust_name,String serial_number) throws Exception
    {
        IData params = new DataMap();
        params.put("PSPT_ID", pspt_id);
        params.put("PSPT_NAME", cust_name);
        params.put("SERIAL_NUMBER", serial_number);

        return Dao.qryByCode("TF_F_CUST_PERSON", "SEL_BY_PSPT_5", params);
    }    
    
    /**
     * TF_F_CUST_PERSON_OTHER 根据custid查询个人客户资料
     * 
     * @author liquan
     * @param pspt_type_code
     * @param pspt_id
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryPerInfoByPsptId_2( String custId ) throws Exception
    {
        IData params = new DataMap();
        params.put("CUST_ID", custId);
        return Dao.qryByCode("TF_F_CUST_PERSON", "SEL_BY_CUSTID_1", params);
    }  
    
    
    /**
     * 根据PSPT_TYPE_CODE,PSPT_ID查询个人客户资料
     * 
     * @author liquan
     * @param pspt_type_code
     * @param pspt_id
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryUserPsptByPsptIdName( String pspt_id,String cust_name) throws Exception
    {
        IData params = new DataMap();
        params.put("PSPT_ID", pspt_id);
        params.put("PSPT_NAME", cust_name);
        return Dao.qryByCode("TF_F_USER_PSPT", "SEL_BY_PSPTID", params);
    }  
    
}
