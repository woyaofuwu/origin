
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;

public class SaleActiveQry
{

    public static IDataset getAccountOpenUserState(String userId, String serialNumber) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.USER_ID, A.BIZ_STATE_CODE  FROM TF_F_USER_PLATSVC A, TD_B_PLATSVC B WHERE A.SERVICE_ID = B.SERVICE_ID " + "AND A.partition_id =  to_number(MOD(:USER_ID,10000))  AND A.user_id = :USER_ID   AND B.sp_code='698000' "
                + "AND B.biz_code='00000001'  AND A.biz_state_code <> 'E'  AND SYSDATE BETWEEN A.START_DATE AND B.END_DATE");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        String routeId = RouteInfoQry.getEparchyCodeBySn(serialNumber);
        IDataset res = Dao.qryBySql(sql, param, routeId);
        return res;
    }

    public static IDataset getEndOffSet(String packageId) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select max(END_OFFSET) end_offset from td_b_package_element where package_id = :PACKAGE_ID and element_type_code = 'A' ");
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        IDataset res = Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
        return res;
    }

    /**
     * 获取6位随机码
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static String getRandomCode() throws Exception
    {
        SQLParser parser = new SQLParser(new DataMap());
        parser.addSQL(" SELECT LPAD(TRUNC(DBMS_RANDOM.VALUE*1000000),6,0) RANDOM_CODE FROM DUAL ");
        IDataset set = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        return ((IData) set.get(0)).getString("RANDOM_CODE");
    }

    public static IDataset qrySaleActiveBySn(String sn) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_SERIAL_NUMBER", data, Route.CONN_CRM_CG);
    }

    public static IDataset queryContractTerminal(String productId, String modelCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("MODEL_CODE", modelCode);
        IDataset res = Dao.qryByCode("TD_B_PRODUCT", "SEL_TERMINALLEVEL_BYMODEL", param, page, Route.CONN_CRM_CEN);
        return res;
    }

    public static IDataset queryGgCardInfo(String userId, String rsrvValueCode, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("RSRV_VALUE_CODE", rsrvValueCode);
        data.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_GGCARD", data);
    }

    public static IDataset queryGiftHis(String userId, String rsrvValueCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("RSRV_VALUE_CODE", rsrvValueCode);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_BUSSPRESENT", data, page);
    }

    /*
     * 查询机型是否已配置合约
     */
    public static IDataset queryIsExistsModelCode(String productId, String modelCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("MODEL_CODE", modelCode);
        IDataset res = Dao.qryByCode("TD_B_SALE_GOODS", "SEL_ISHAVE_GOODS", param, Route.CONN_CRM_CEN);
        return res;
    }

    /*
     * 查询合约价对应可办理档次
     */
    public static IDataset queryModelCodeLevel(String productId, String price) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PRICE", price);
        IDataset res = Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_LEVELS_BY_PRICE", param, Route.CONN_CRM_CEN);
        return res;
    }

    /*
     * 查询合约产品终端信息
     */
    public static IDataset queryModelInfoByProductId(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select distinct c.goods_id GOODS_ID, e.model_code MODEL_CODE, e.model_desc MODEL_DESC   from td_b_product_package a, td_b_package  b, td_b_sale_goods  c, td_b_package_element d, td_s_res_model  e  "
                + " where  a.package_id = b.package_id  and b.package_id = d.package_id    and d.element_id = c.goods_id    and d.element_type_code = 'G'  and c.res_id = e.model_code  and a.end_date>sysdate "
                + " and b.end_date>sysdate  and c.end_date>sysdate  and d.end_date>sysdate");
        parser.addSQL(" and product_id = :PRODUCT_ID  ");
        IDataset res = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        // Dao.qryByCode("TD_B_PRODUCT", "SEL_PRODUCT_PACKAGE", param, Route.CONN_CRM_CEN);
        return res;
    }

    public static IDataset queryPriceLevelInfo(String elementId) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_ID", elementId);
        SQLParser parser = new SQLParser(param);
        parser
                .addSQL(" SELECT A.PACKAGE_ID PACKAGE_ID, A.PACKAGE_NAME PACKAGE_NAME,  A.FEE / 100 PRAYFEE,  B.FEE / 100 BUYFEE   FROM  "
                        + " (SELECT B.PACKAGE_ID,    B.PACKAGE_NAME,    D.ELEMENT_TYPE_CODE,    SUM(FEE) FEE     FROM TD_B_PACKAGE    B,    TD_B_PACKAGE_ELEMENT  C,    TD_B_PRODUCT_TRADEFEE D  "
                        + "  WHERE C.ELEMENT_TYPE_CODE = 'G'      AND C.ELEMENT_ID = :ELEMENT_ID      AND B.PACKAGE_ID = C.PACKAGE_ID      AND D.PACKAGE_ID = C.PACKAGE_ID      AND (D.ELEMENT_TYPE_CODE = 'A' OR D.ELEMENT_ID = C.ELEMENT_ID)  "
                        + "    AND D.ELEMENT_TYPE_CODE = 'A'      AND B.END_DATE > SYSDATE      AND C.END_DATE > SYSDATE      AND D.END_DATE > SYSDATE    GROUP BY B.PACKAGE_ID, B.PACKAGE_NAME, D.ELEMENT_TYPE_CODE) A,  "
                        + "( SELECT B.PACKAGE_ID,    B.PACKAGE_NAME,    D.ELEMENT_TYPE_CODE,    SUM(FEE) FEE     FROM TD_B_PACKAGE    B,    TD_B_PACKAGE_ELEMENT  C,    TD_B_PRODUCT_TRADEFEE D    "
                        + " WHERE C.ELEMENT_TYPE_CODE = 'G'      AND C.ELEMENT_ID = :ELEMENT_ID      AND B.PACKAGE_ID = C.PACKAGE_ID      AND D.PACKAGE_ID = C.PACKAGE_ID      AND (D.ELEMENT_TYPE_CODE = 'A' OR D.ELEMENT_ID = C.ELEMENT_ID)    "
                        + "   AND D.ELEMENT_TYPE_CODE = 'G'      AND B.END_DATE > SYSDATE      AND C.END_DATE > SYSDATE      AND D.END_DATE > SYSDATE    GROUP BY B.PACKAGE_ID, B.PACKAGE_NAME, D.ELEMENT_TYPE_CODE) B  WHERE A.PACKAGE_ID = B.PACKAGE_ID order by A.FEE");
        IDataset res = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        // Dao.qryByCode("TD_B_PRODUCT", "SEL_PRODUCT_TRADEFEE", param, Route.CONN_CRM_CEN);
        return res;
    }

    public static IDataset queryProductInfo(String productId, String productName, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PRODUCT_NAME", productName);
        param.put("EMPARCHYCODE", eparchyCode);
        IDataset res = Dao.qryByCode("TD_B_PRODUCT", "SEL_PRODUCT_RELEASE", param, Route.CONN_CRM_CEN);
        return res;
    }

    public static IDataset querySalePhoneSchool(String examId, String eparchyCode, String stuName, String sendsmsTag, String startTime, String endTime, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("EXAM_ID", examId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("STU_NAME", stuName);
        data.put("START_TIME", startTime);
        data.put("END_TIME", endTime);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT * FROM TF_F_STUDENT_SALEPHONE");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND EXAM_ID = :EXAM_ID");
        parser.addSQL(" AND EPARCHY_CODE = :EPARCHY_CODE");
        parser.addSQL(" AND STU_NAME LIKE '%'||:STU_NAME||'%'");
        if ("0".equals(sendsmsTag))
        {
            parser.addSQL(" AND SENDSMS_TAG = 0");
        }
        else if ("1".equals(sendsmsTag))
        {
            parser.addSQL(" AND SENDSMS_TAG > 0");
        }

        parser.addSQL(" AND CREATE_TIME >= to_date(:START_TIME,'yyyy-MM-dd') ");
        parser.addSQL(" AND CREATE_TIME <= to_date(:END_TIME,'yyyy-MM-dd')+1 ");
        IDataset results = Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
        return results;
    }

    public static IData querySingleExamID(String examId) throws Exception
    {
        IData res = new DataMap();
        res = Dao.qryByPK("TF_F_STUDENT_SALEPHONE", new String[]
        { "EXAM_ID" }, new String[]
        { examId }, Route.CONN_CRM_CEN);
        return res;
    }

    public static IData querySinglePsptID(String psptId) throws Exception
    {
        IData res = new DataMap();
        res = Dao.qryByPK("TF_F_STUDENT_SALEPHONE", new String[]
        { "PSPT_ID" }, new String[]
        { psptId }, Route.CONN_CRM_CEN);
        return res;
    }

    public static IData querySingleStuMobile(String mobilePhone) throws Exception
    {
        IData res = new DataMap();
        res = Dao.qryByPK("TF_F_STUDENT_SALEPHONE", new String[]
        { "STU_MOBILPHONE" }, new String[]
        { mobilePhone }, Route.CONN_CRM_CEN);
        return res;
    }

    public static IDataset queryTerminalImportDetail(String importId, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("IMPORT_ID", importId);
        return Dao.qryByCode("TF_F_CMS_IMPORT_DATA", "SEL_IMPORTDETAIL", data, page, Route.CONN_CRM_CEN);
    }

    /**
     * 第二次校验验证码
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset secondCheck(String stuMobilPhone, String rsrvStr3, String rsrvStr4) throws Exception
    {
        IData param = new DataMap();
        param.put("STU_MOBILPHONE", stuMobilPhone);
        param.put("RSRV_STR3", rsrvStr3);
        param.put("RSRV_STR4", rsrvStr4);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT 1 FROM TF_F_STUDENT_SALEPHONE");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND STU_MOBILPHONE = :STU_MOBILPHONE");
        parser.addSQL(" AND RSRV_STR3 = :RSRV_STR3");
        parser.addSQL(" AND RSRV_DATE2 >= sysdate");
        // parser.addSQL(" AND RSRV_STR4 = :RSRV_STR4 ");
        IDataset results = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        return results;
    }

    public static int updateByTradeId(String rsrvTag1, String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_TAG1", rsrvTag1);
        param.put("TRADE_ID", tradeId);
        int num = Dao.executeUpdateByCodeCode("TF_B_TRADE_SALE_ACTIVE", "UPDATE_BY_TRADE", param);
        return num;
    }

    public static int updateSendCheck(String stuMobilPhone, String remark, String rsrvStr3, String rsrvDate2, String rsrvStr4) throws Exception
    {
        IData param = new DataMap();
        param.put("STU_MOBILPHONE", stuMobilPhone);
        param.put("REMARK", remark);
        param.put("RSRV_STR3", rsrvStr3);
        param.put("RSRV_DATE2", rsrvDate2);// 失效时间
        param.put("RSRV_STR4", rsrvStr4);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_F_STUDENT_SALEPHONE set");
        parser.addSQL("  REMARK = :REMARK ");
        parser.addSQL(" ,RSRV_STR3 = :RSRV_STR3 ");
        parser.addSQL(" ,RSRV_DATE2 = to_date( :RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss')");
        parser.addSQL(" WHERE STU_MOBILPHONE= :STU_MOBILPHONE ");
        // parser.addSQL(" AND RSRV_STR4= :RSRV_STR4 ");
        return Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * REQ201504080009优惠活动礼品配置界面缺失修复
     * 查询礼品配置信息
     * */
    public static IDataset querySaleActiveExt(IData inparam) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", inparam.getString("PRODUCT_ID",""));
        data.put("PACKAGE_ID", inparam.getString("PACKAGE_ID",""));
        data.put("GOODS_ID", inparam.getString("GOODS_ID",""));
        data.put("CITY_CODE", inparam.getString("CITY_CODE",""));
        return Dao.qryByCode("TD_B_SALE_GOODS_EXT", "SEL_RESID_BY_SALEACTIVE", data, Route.CONN_CRM_CEN);
    }
}
