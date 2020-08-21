
package com.asiainfo.veris.crm.iorder.soa.group.param.minorec.elecagreement;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.priv.StaffPrivUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class AgreementInfoBean extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 初始化字符串长度
     */
    private static final int LENTH = 100;

    private static final int SUBSTR_LENTH = 2;


    public static IDataset queryAgreementDefInfo(IData input) throws Exception {

        StringBuilder strSql = new StringBuilder();
        strSql.append(" SELECT T.PRODUCT_ID, T.AGREEMENT_DEF_ID,T.CONTRACT_CODE, T.REMARK, T.VALID_TAG, T.ORDER_INDEX, T.MIN_NUM, T.MAX_NUM, ");
        strSql.append(" A.AGREEMENT_NAME, A.AGREEMENT_DEF, A.AGREEMENT_TYPE, A.REMARK ");
        strSql.append(" FROM TD_B_ELEC_AGREEMENT_PRODUCT T, TD_B_ELEC_AGREEMENT_DEF A");
        strSql.append(" WHERE T.AGREEMENT_DEF_ID = A.AGREEMENT_DEF_ID ");

        if(StringUtils.isNotEmpty(input.getString("PRODUCT_ID",""))){
            strSql.append(" AND T.PRODUCT_ID = :PRODUCT_ID ");
        }
        if(StringUtils.isNotEmpty(input.getString("CONTRACT_CODE",""))){
            strSql.append(" AND T.CONTRACT_CODE = :CONTRACT_CODE ");
        }
        if(StringUtils.isNotEmpty(input.getString("AGREEMENT_TYPE",""))){
            strSql.append(" AND A.AGREEMENT_TYPE = :AGREEMENT_TYPE ");
        }
        strSql.append(" AND ( T.BUSI_TYPE = :BUSI_TYPE OR T.BUSI_TYPE = 'COMMON' ) ");
        strSql.append(" AND A.VALID_TAG = '0' ");
        strSql.append(" ORDER BY T.ORDER_INDEX ");

        IData map = new DataMap();

        if(StringUtils.isNotEmpty(input.getString("PRODUCT_ID",""))){
            map.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
        }
        if(StringUtils.isNotEmpty(input.getString("CONTRACT_CODE",""))){
            map.put("CONTRACT_CODE", input.getString("CONTRACT_CODE"));
        }
        if(StringUtils.isNotEmpty(input.getString("AGREEMENT_TYPE",""))){
            map.put("AGREEMENT_TYPE", input.getString("AGREEMENT_TYPE"));
        }
        map.put("BUSI_TYPE", input.getString("BUSI_TYPE","COMMON"));

        /*SQLParser parser = new SQLParser(map);

        parser.addSQL(strSql.toString());*/

        return Dao.qryBySql(strSql,map, Route.CONN_CRM_CEN);
    }

    public static IDataset preemptAgreementId(IData input) throws Exception {

        IData param = new DataMap();

        param.put("EPARCHY_CODE", input.getString("EPARCHY_CODE"));
        param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));

        StringBuilder sf = new StringBuilder();
        sf.append(" SELECT AGREEMENT_ID,RES_STATE,EPARCHY_CODE,EPARCHY_CODE,UPDATE_TIME,UPDATE_STAFF_ID,PRODUCT_ID,BRAND_CODE,UPDATE_DEPART_ID ");
        sf.append(" FROM TF_F_ELECTRONIC_AGRE_RES ");
        sf.append(" WHERE 1=1 ");
        sf.append(" AND ").append("EPARCHY_CODE").append(" = :EPARCHY_CODE ");
        sf.append(" AND (").append("PRODUCT_ID").append(" = :PRODUCT_ID OR ").append("PRODUCT_ID").append(" = '-1' ) ");
        sf.append(" AND ").append("RES_STATE = 'N' AND ROWNUM = 1 ");
        sf.append(" ORDER BY ").append("PRODUCT_ID").append(" DESC , ").append("AGREEMENT_ID").append(" ASC ");

        IDataset results = Dao.qryBySql(sf,input, Route.CONN_CRM_CG);
        return results;
    }
    public static void updadteAgreementId(IData input) throws Exception {
        Dao.update("TF_F_ELECTRONIC_AGRE_RES", input);
    }

    public static IDataset queryElecAgreement(IData input, Pagination pagination) throws Exception {
        input.put("AREA_CODE", input.getString("cond_AREA_CODE"));
        input.put("CUST_MANAGER_ID", input.getString("cond_CUST_MANAGER_ID"));
        input.put("CUST_MANAGER_NAME", input.getString("cond_CUST_MANAGER_NAME"));
        input.put("DEPART_ID", input.getString("cond_DEPART_ID"));
        SQLParser parser = new SQLParser(input);
        parser.addSQL(" SELECT DISTINCT A.AGREEMENT_ID ,A.CONTRACT_CODE, A.PRODUCT_ID, R.ARCHIVES_NAME ,R.ARCHIVES_TYPE,G.CUST_NAME, ");
        parser.addSQL(" A.ARCHIVES_ID, A.PDF_FILE,R.START_DATE, R.END_DATE, R.ARCHIVES_ATTACH,R.ARCHIVES_STATE ");
        parser.addSQL(" ,( CASE R.ARCHIVES_STATE WHEN '0' THEN '待审核' WHEN '1' THEN '审核通过' WHEN '2' THEN '审核不通过' WHEN '3' THEN '归档' WHEN '4' THEN '失效'  ");
        parser.addSQL(" ELSE '其他' END ) AS ARCHIVES_STATE_NAME ");
        parser.addSQL(" FROM TF_F_ELECTRONIC_ARCHIVES_REL T, TF_F_ELECTRONIC_AGREEMENT A, TF_F_ELECTRONIC_ARCHIVES R ,TF_F_CUST_GROUP G ");
        parser.addSQL(" WHERE T.ARCHIVES_ID = A.ARCHIVES_ID AND R.ARCHIVES_ID = A.ARCHIVES_ID AND T.CUST_ID = G.CUST_ID AND T.VALID_TAG = '0' ");
        parser.addSQL(" AND G.GROUP_ID = :GROUP_ID ");
        parser.addSQL(" AND A.AGREEMENT_ID = :AGREEMENT_ID ");
        parser.addSQL(" AND T.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL(" AND R.ARCHIVES_STATE = :ARCHIVES_STATE ");
        //获取客户经理查询条件SQL
        String grant_sql = getManagerGrantSql(input, " g.CUST_MANAGER_ID ","2");
        //获取当前员工是否有CMG_ALL_VIEW权限
        boolean priv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(),"CMG_ALL_VIEW");
        if(priv) {
            String all_grant_sql = grant_sql;
            all_grant_sql = all_grant_sql.replaceFirst("and", "");
            String area_code = input.getString("AREA_CODE", "");
            if(!"".equals(area_code) && !"0898".equals(area_code) && !"HAIN".equals(area_code)) {
                if("".equals(all_grant_sql)) {
                    parser.addSQL(" and ((rsrv_num3 is null or rsrv_num3!='1') or (g.city_code = :AREA_CODE and g.rsrv_num3 = '1'))");
                } else {
                    //铁通集团客户被限定在当前客户经理所在的业务区
                    parser.addSQL(" and (" + all_grant_sql + " or (g.city_code = :AREA_CODE and g.rsrv_num3 = '1')) ");
                }
            }
        } else {
            //没有权限只可以查询移动集团客户合同
            parser.addSQL(grant_sql);
        }
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
    
    public static IDataset queryAgreementInfosByCIDAndPID(IData input) throws Exception {

        SQLParser parser = new SQLParser(input);
        parser.addSQL(" SELECT A.AGREEMENT_ID || R.ARCHIVES_NAME AS ARCHIVES_NAME, A.AGREEMENT_ID ,R.ARCHIVES_NAME AS AGREEMENT_NAME ,R.ARCHIVES_TYPE, A.ARCHIVES_ID, A.PDF_FILE, R.START_DATE, R.END_DATE, R.ARCHIVES_ATTACH ");
        parser.addSQL(" FROM TF_F_ELECTRONIC_ARCHIVES_REL T, TF_F_ELECTRONIC_AGREEMENT A, TF_F_ELECTRONIC_ARCHIVES R ");
        parser.addSQL(" WHERE T.ARCHIVES_ID = A.ARCHIVES_ID AND R.ARCHIVES_ID = A.ARCHIVES_ID AND SYSDATE BETWEEN R.START_DATE AND R.END_DATE ");
        parser.addSQL(" AND T.CUST_ID = :CUST_ID ");
        parser.addSQL(" AND A.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL(" AND T.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL(" AND T.VALID_TAG = '0' ");
        parser.addSQL(" AND R.ARCHIVES_STATE != '4' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    public static IDataset queryElectronicArchives(IData input) throws Exception {
        StringBuilder sf = new StringBuilder();
        sf.append(" SELECT * ");
        sf.append(" FROM TF_F_ELECTRONIC_ARCHIVES ");
        sf.append(" WHERE ");
        sf.append(" ARCHIVES_STATE <> '4' ");
        sf.append(" AND ARCHIVES_ID = :ARCHIVES_ID ");
        return Dao.qryBySql(sf,input, Route.CONN_CRM_CG);
    }

    public static IDataset queryElectronicAgreAttach(IData input) throws Exception {
        StringBuilder sf = new StringBuilder();
        sf.append(" SELECT ARCHIVES_ID,AGREEMENT_ID,PDF_FILE,PRODUCT_ID ");
        sf.append(" FROM TF_F_ELECTRONIC_AGRE_ATTACH ");
        sf.append(" WHERE ");
        if(StringUtils.isNotEmpty(input.getString("ARCHIVES_ID"))){
            sf.append(" ARCHIVES_ID = :ARCHIVES_ID ");
        }
        if(StringUtils.isNotEmpty(input.getString("AGREEMENT_ID"))){
            sf.append(" AGREEMENT_ID = :AGREEMENT_ID ");
        }
        return Dao.qryBySql(sf,input, Route.CONN_CRM_CG);
    }

    public static void updateElecAgreementAttachInfo(IData input) throws Exception {
        StringBuilder sf = new StringBuilder();
        sf.append(" UPDATE TF_F_ELECTRONIC_ARCHIVES SET ARCHIVES_ATTACH = :ARCHIVES_ATTACH ,STATE_DESC = :STATE_DESC ");
        sf.append(" WHERE ");
        sf.append(" ARCHIVES_ID = :ARCHIVES_ID ");
        Dao.executeUpdate(sf, input,Route.CONN_CRM_CG);
    }

    public static IDataset queryElectronicAgreement(IData input) throws Exception {
        StringBuilder sf = new StringBuilder();
        sf.append(" SELECT A.ARCHIVES_ID,A.AGREEMENT_ID,A.A_NAME,A.A_ADDRESS,A.A_HEADER,A.A_CONTACT_PHONE,A.A_BANK,A.A_BANK_ACCT_NO,A.A_SIGN_MAN,A.A_SIGN_DATE,A.B_NAME,A.B_ADDRESS,A.B_HEADER,A.B_CONTACT_PHONE,A.B_BANK,A.B_BANK_ACCT_NO,A.B_SIGN_DATE,A.B_SIGN_MAN,A.PDF_FILE,A.PRODUCT_ID,A.CONTRACT_CODE ");
        sf.append(" ,T.ARCHIVES_STATE,T.ARCHIVES_NAME,T.START_DATE,T.END_DATE,R.PRODUCT_ID AS PRODUCTS ");
        sf.append(" FROM TF_F_ELECTRONIC_AGREEMENT A ,TF_F_ELECTRONIC_ARCHIVES T ,TF_F_ELECTRONIC_AGREEMENT_REL R ");
        sf.append(" WHERE 1 = 1 ");

        if(StringUtils.isNotEmpty(input.getString("ARCHIVES_ID"))){
            sf.append(" AND A.ARCHIVES_ID = :ARCHIVES_ID ");
        }
        if(StringUtils.isNotEmpty(input.getString("AGREEMENT_ID"))){
            sf.append(" AND A.AGREEMENT_ID = :AGREEMENT_ID ");
        }
        sf.append(" AND A.ARCHIVES_ID = T.ARCHIVES_ID ");
        sf.append(" AND A.AGREEMENT_ID = R.AGREEMENT_ID ");
        /*SQLParser parser = new SQLParser(input);
        parser.addSQL(sf.toString());*/

        return Dao.qryBySql(sf,input, Route.CONN_CRM_CG);
    }

    public static IDataset queryDetailAttrElectronicInfo(IData input) throws Exception {
        StringBuilder sf = new StringBuilder();
        sf.append(" SELECT ARCHIVES_ID,ATTR_CODE,ATTR_VALUE,ATTR_GROUP,GROUP_INDEX,REMARK,START_DATE,END_DATE ");
        sf.append(" FROM TF_F_ELECTRONIC_ARCHIVES_ATTR ");
        sf.append(" WHERE ");
        sf.append(" ARCHIVES_ID = :ARCHIVES_ID ");
        sf.append(" AND SYSDATE < END_DATE ");

        return Dao.qryBySql(sf,input, Route.CONN_CRM_CG);
    }

    public static IDataset queryElecAgrePdfInfo(IData input) throws Exception {
        StringBuilder strSql = new StringBuilder();
        strSql.append(" SELECT A.AGRE_PDF_ID,T.CONTRACT_CODE,B.SUB_ARCHIVE_TYPE_CODE,B.ARCHIVE_TYPE_CODE,C.AGREEMENT_NAME ");
        strSql.append(" FROM TD_B_ELEC_AGRE_PDF_DEF_REL A,TD_B_ELEC_ARCHIVE_TYPE_SUB B,TD_B_ELEC_AGREEMENT_DEF C, TD_B_ELEC_AGREEMENT_PRODUCT T");
        strSql.append(" WHERE ");
        strSql.append(" A.AGREEMENT_DEF_ID=C.AGREEMENT_DEF_ID AND A.AGREEMENT_DEF_ID=T.AGREEMENT_DEF_ID AND C.AGREEMENT_TYPE = B.SUB_ARCHIVE_TYPE_CODE AND A.AGREEMENT_DEF_ID =:AGREEMENT_DEF_ID AND T.PRODUCT_ID = :PRODUCT_ID ");

        /*SQLParser parser = new SQLParser(input);
        parser.addSQL(strSql.toString());*/

        return Dao.qryBySql(strSql,input, Route.CONN_CRM_CEN);
    }

    public static void updateElectronicInfo(IData input) throws Exception{
        
        Dao.save("TF_F_ELECTRONIC_ARCHIVES", input, Route.CONN_CRM_CG);
    }

    public static void updateElectronicAgreement(IData input) throws Exception
    {
        Dao.save("TF_F_ELECTRONIC_AGREEMENT", input, Route.CONN_CRM_CG);
    }

    public static void updateElectronicAgreAttach(IData input) throws Exception
    {
        Dao.save("TF_F_ELECTRONIC_AGRE_ATTACH", input, Route.CONN_CRM_CG);
    }

    public static void updateElectronicAttrEnd(IData input) throws Exception{
        SQLParser sql = new SQLParser(input);
        sql.addSQL(" UPDATE TF_F_ELECTRONIC_ARCHIVES_ATTR SET END_DATE=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')");
        sql.addSQL(" WHERE ARCHIVES_ID=:ARCHIVES_ID ");
        sql.addSQL(" AND ATTR_CODE=:ATTR_CODE ");
        sql.addSQL(" AND  ATTR_GROUP=:ATTR_GROUP ");
        sql.addSQL(" AND  GROUP_INDEX=:GROUP_INDEX ");
        sql.addSQL(" AND START_DATE <= SYSDATE ");
        sql.addSQL(" AND END_DATE > SYSDATE ");
        Dao.executeUpdate(sql, Route.CONN_CRM_CG);
    }
    public static void updateElectronicRelEnd(IData input) throws Exception{
        SQLParser sql = new SQLParser(input);
        sql.addSQL(" UPDATE TF_F_ELECTRONIC_ARCHIVES_REL SET VALID_TAG = :VALID_TAG ");
        sql.addSQL(" WHERE ARCHIVES_ID=:ARCHIVES_ID ");
        sql.addSQL(" AND VALID_TAG= '0' ");
        Dao.executeUpdate(sql, Route.CONN_CRM_CG);
    }

    public static void insertElectronicAttr(IDataset input) throws Exception{
        Dao.insert("TF_F_ELECTRONIC_ARCHIVES_ATTR", input,  Route.CONN_CRM_CG);
    }
    public static void insertElectronicAgreement(IData input) throws Exception{
        Dao.insert("TF_F_ELECTRONIC_AGREEMENT", input,  Route.CONN_CRM_CG);
    }
    public static void insertElectronicAttach(IData input) throws Exception{
        Dao.insert("TF_F_ELECTRONIC_AGRE_ATTACH", input,  Route.CONN_CRM_CG);
    }
    public static void insertElectronicRel(IData input) throws Exception{
        Dao.insert("TF_F_ELECTRONIC_ARCHIVES_REL", input,  Route.CONN_CRM_CG);
    }
    public static void insertElectronicArchives(IData input) throws Exception{
        Dao.insert("TF_F_ELECTRONIC_ARCHIVES", input,  Route.CONN_CRM_CG);
    }


    public static void updateAgreementEffTime(IData input) throws Exception {
        SQLParser sql = new SQLParser(input);
        sql.addSQL(" UPDATE TF_F_ELECTRONIC_ARCHIVES SET START_DATE=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),END_DATE=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS') ");
        sql.addSQL(" WHERE ARCHIVES_ID = :ARCHIVES_ID ");
        Dao.executeUpdate(sql, Route.CONN_CRM_CG);
    }

    public static IDataset qryPdfReleByTemplateId(IData input) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT A.PDF_ELE_CODE, A.PDF_ELE_TYPE ");
        sql.append(" FROM TD_B_ELEC_AGRE_PDF_ELE A, TD_B_ELEC_AGRE_PDF_DEF_REL B ");
        sql.append("  WHERE A.AGRE_PDF_ID = B.AGRE_PDF_ID ");
        sql.append(" AND B.AGREEMENT_DEF_ID = :AGREEMENT_DEF_ID ");

        return Dao.qryBySql(sql, input, Route.CONN_CRM_CEN);
    }

    public static IDataset qryElecAgreementRel(IData input) throws Exception {
        SQLParser parser = new SQLParser(input);
        parser.addSQL(" SELECT * FROM TF_F_ELECTRONIC_AGREEMENT_REL T ");
        parser.addSQL(" WHERE T.AGREEMENT_ID = :AGREEMENT_ID ");
        parser.addSQL(" AND T.VALID_TAG = :VALID_TAG ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    public static void updadteElecAgreementRel(IData input) throws Exception {

        /*SQLParser sql = new SQLParser(input);
        sql.addSQL(" UPDATE TF_F_ELECTRONIC_AGREEMENT_REL T SET T.PRODUCT_ID = :PRODUCT_ID, ");
        sql.addSQL(" T.UPDATE_TIME = :UPDATE_TIME, ");
        sql.addSQL(" T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
        sql.addSQL(" T.UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
        sql.addSQL(" WHERE T.REL_ID = :REL_ID ");
        Dao.executeUpdate(sql, Route.CONN_CRM_CG);*/
        Dao.save("TF_F_ELECTRONIC_AGREEMENT_REL", input, Route.CONN_CRM_CG);
    }

    public static void insertElecAgreementRel(IData input) throws Exception {

        Dao.insert("TF_F_ELECTRONIC_AGREEMENT_REL", input,  Route.CONN_CRM_CG);
    }
    
    public static IDataset queryAgArchivesByAgreementId(IData input) throws Exception {
        SQLParser sql = new SQLParser(input);
        sql.addSQL(" SELECT T.ARCHIVES_ID,T.AGREEMENT_ID,T.A_NAME,T.A_SIGN_DATE,T.B_NAME, ");
        sql.addSQL("  T.B_SIGN_DATE,A.ARCHIVES_NAME,A.ARCHIVES_TYPE,A.SUB_ARCHIVES_TYPE,A.START_DATE,A.END_DATE ");
        sql.addSQL("    FROM TF_F_ELECTRONIC_AGREEMENT T, TF_F_ELECTRONIC_ARCHIVES A ");
        sql.addSQL("     WHERE T.ARCHIVES_ID = A.ARCHIVES_ID ");
        sql.addSQL("     AND T.AGREEMENT_ID = :AGREEMENT_ID ");
        return Dao.qryByParse(sql, Route.CONN_CRM_CG);
    }

    public static IData queryArchivesState(IData input) throws Exception{
        SQLParser sql = new SQLParser(input);
        sql.addSQL(" SELECT * ");
        sql.addSQL(" FROM TF_F_ELECTRONIC_ARCHIVES ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND ARCHIVES_STATE = :ARCHIVES_STATE ");
        sql.addSQL(" AND ARCHIVES_ID = :ARCHIVES_ID ");
        IDataset results = Dao.qryByParse(sql, Route.CONN_CRM_CG);
        if(DataUtils.isNotEmpty(results)){
            return results.first();
        }else{
            return new DataMap();
        }
    }
    
    public static int updateArchivesState(IData input) throws Exception {

        StringBuilder sql = new StringBuilder(LENTH);
        sql.append(" UPDATE TF_F_ELECTRONIC_ARCHIVES T SET T.ARCHIVES_STATE = :ARCHIVES_STATE WHERE T.ARCHIVES_ID = :ARCHIVES_ID ");
        return Dao.executeUpdate(sql,input,Route.CONN_CRM_CG);
    }
    
    public static int updateAgreemnetRelState(IData input) throws Exception{

        StringBuilder sql = new StringBuilder(LENTH);
        sql.append(" UPDATE TF_F_ELECTRONIC_AGREEMENT_REL T SET T.VALID_TAG = :VALID_TAG,T.RSRV_STR1=:IBSYSID WHERE T.AGREEMENT_ID = :AGREEMENT_ID ");
        return Dao.executeUpdate(sql,input,Route.CONN_CRM_CG);
    }

    public static int[] insertLog(IDataset dataset) throws Exception{
        return Dao.insert("TF_F_ELECTRONIC_AGREEMENT_LOG",dataset,Route.CONN_CRM_CG);
    }

    public static IData getAgreementId() throws Exception{
        String seq = SeqMgr.getAgreementId();
        String systime = SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD);

        //截取位数
        String cityCode = getVisit().getCityCode();
        if(StringUtils.isBlank(cityCode)||cityCode.length()<SUBSTR_LENTH){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"员工地市编码【"+cityCode+"】无效！");
        }

        IData data = new DataMap();
        data.put("AGREEMENT_ID",systime+cityCode.substring(cityCode.length()-SUBSTR_LENTH)+seq);
        return data;

    }

    public static int updateArchivesRel(IData param, BizVisit visit) throws Exception{
        int none = -1;
        int sucess = 1;
        IData input = new DataMap();
        input.put("AGREEMENT_ID",param.getString("CONTRACT_ID"));
        input.put("PRODUCT_ID",param.getString("PRODUCT_ID"));
        input.put("CUST_ID",param.getString("CUST_ID"));
        input.put("USER_ID",param.getString("USER_ID"));
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT B.* FROM TF_F_ELECTRONIC_AGREEMENT A, TF_F_ELECTRONIC_ARCHIVES_REL B ");
        sql.append(" WHERE A.ARCHIVES_ID = B.ARCHIVES_ID ");
        sql.append(" AND B.VALID_TAG = '0' ");
        sql.append(" AND A.AGREEMENT_ID = :AGREEMENT_ID ");
        sql.append(" AND B.PRODUCT_ID = :PRODUCT_ID ");
        sql.append(" AND B.CUST_ID = :CUST_ID ");
        sql.append(" AND B.USER_ID = :USER_ID ");

        IDataset dataset = Dao.qryBySql(sql,input,Route.CONN_CRM_CG);

        //已存在关联关系，无需处理
        if(DataUtils.isNotEmpty(dataset)){
            return none;
        }

        input.put("USER_ID","-1");
        IDataset defualtDataset = Dao.qryBySql(sql,input,Route.CONN_CRM_CG);

        String sysStart_date = SysDateMgr.getSysDate();
        //存在-1项则更新
        if(DataUtils.isNotEmpty(defualtDataset)){
            IData data = defualtDataset.first();
            data.put("USER_ID",param.getString("USER_ID"));
            data.put("UPDATE_TIME",sysStart_date);
            data.put("UPDATE_STAFF_ID",visit.getStaffId());
            data.put("UPDATE_DEPART_ID",visit.getDepartId());
            return Dao.save("TF_F_ELECTRONIC_ARCHIVES_REL",data,Route.CONN_CRM_CG) ? sucess : none;
        }else{//不存在-1项则插入一条
            IData data1 = new DataMap();
            data1.put("AGREEMENT_ID",param.getString("CONTRACT_ID"));
            StringBuilder agSql = new StringBuilder();
            agSql.append(" SELECT * FROM TF_F_ELECTRONIC_AGREEMENT t WHERE t.AGREEMENT_ID= :AGREEMENT_ID ");
            IDataset agInfos = Dao.qryBySql(agSql,data1,Route.CONN_CRM_CG);
            if(DataUtils.isNotEmpty(agInfos)){
                IData rel = new DataMap();
                rel.put("REL_ID", SeqMgr.getArchivesId());
                rel.put("CUST_ID", param.getString("CUST_ID"));
                rel.put("USER_ID", param.getString("USER_ID"));// 1客户,
                rel.put("ARCHIVES_ID", agInfos.first().getString("ARCHIVES_ID"));
                rel.put("CREATE_TIME", sysStart_date);
                rel.put("CREATE_STAFF_ID", visit.getStaffId());
                rel.put("CREATE_DEPART_ID", visit.getDepartId());
                rel.put("UPDATE_TIME", sysStart_date);
                rel.put("UPDATE_STAFF_ID", visit.getStaffId());
                rel.put("UPDATE_DEPART_ID", visit.getDepartId());
                rel.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
                rel.put("VALID_TAG", "0");
                Dao.insert("TF_F_ELECTRONIC_ARCHIVES_REL",rel,Route.CONN_CRM_CG);
                return sucess;
            }

        }
        return none;
    }

    public static void updateIbsysid(String agreementId,String ibsysid) throws Exception{
        IData param = new DataMap();
        param.put("AGREEMENT_ID",agreementId);
        param.put("IBSYSID",ibsysid);
        param.put("VALID_TAG","0");
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TF_F_ELECTRONIC_AGREEMENT_REL A SET A.RSRV_STR1 = :IBSYSID WHERE A.AGREEMENT_ID = :AGREEMENT_ID AND A.VALID_TAG = :VALID_TAG ");
        Dao.executeUpdate(sql,param,Route.CONN_CRM_CG);
    }

    public static IDataset getAgreementUser(IData param) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM TF_F_USER T WHERE T.CONTRACT_ID = :AGREEMENT_ID AND T.REMOVE_TAG = '0' ");
        return Dao.qryBySql(sql,param,Route.CONN_CRM_CG);
    }

    public static IDataset getAgreementProductInfos(IData input) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM TF_F_ELECTRONIC_ARCHIVES_REL T WHERE T.ARCHIVES_ID = :ARCHIVES_ID AND T.VALID_TAG = '0' ");
        return Dao.qryBySql(sql,input,Route.CONN_CRM_CG);
    }
    
	public static IDataset elecAgreementSumReportForm(IData input, Pagination pagination) throws Exception {
		StringBuilder sqlStr = new StringBuilder();
		sqlStr.append( " SELECT DISTINCT A.AGREEMENT_ID ,A.CONTRACT_CODE, T.PRODUCT_ID, R.ARCHIVES_NAME ,R.ARCHIVES_TYPE,G.CUST_NAME, A.ARCHIVES_ID, A.PDF_FILE,R.START_DATE, R.END_DATE, R.ARCHIVES_ATTACH,R.ARCHIVES_STATE ");
		sqlStr.append( " ,( CASE R.ARCHIVES_STATE WHEN '0' THEN '待审核' WHEN '1' THEN '审核通过' WHEN '2' THEN '审核不通过' WHEN '3' THEN '归档' WHEN '4' THEN '失效' ELSE '其他' END ) AS ARCHIVES_STATE_NAME ");
		sqlStr.append( " , A.A_NAME, A.A_ADDRESS, A.A_HEADER, A.A_CONTACT_PHONE, A.A_BANK, A.A_BANK_ACCT_NO, A.A_SIGN_MAN, A.A_SIGN_DATE ");
		sqlStr.append( " , A.B_NAME, A.B_ADDRESS, A.B_HEADER, A.B_CONTACT_PHONE, A.B_BANK, A.B_BANK_ACCT_NO, A.B_SIGN_DATE, A.B_SIGN_MAN ");
		sqlStr.append( " , R.CREATE_TIME, R.CREATE_STAFF_ID, R.UPDATE_TIME, R.UPDATE_STAFF_ID ");
		
		sqlStr.append( " FROM TF_F_ELECTRONIC_ARCHIVES_REL T, TF_F_ELECTRONIC_AGREEMENT A, TF_F_ELECTRONIC_ARCHIVES R ,TF_F_CUST_GROUP G ");
		sqlStr.append( " WHERE T.ARCHIVES_ID = A.ARCHIVES_ID AND R.ARCHIVES_ID = A.ARCHIVES_ID AND T.CUST_ID = G.CUST_ID AND T.VALID_TAG = '0' ");
		
		sqlStr.append( " AND to_char(R.START_DATE, 'yyyy-MM-dd HH24:mi:ss') >= to_char(to_date(:START_DATE, 'yyyy-MM-dd HH24:mi:ss'), 'yyyy-MM-dd HH24:mi:ss') ");
		sqlStr.append( " AND to_char(R.END_DATE, 'yyyy-MM-dd HH24:mi:ss') <= to_char(to_date(:END_DATE, 'yyyy-MM-dd HH24:mi:ss'), 'yyyy-MM-dd HH24:mi:ss') ");

		return Dao.qryBySql(sqlStr, input, pagination, Route.CONN_CRM_CG);

	}
	
	public static String getManagerGrantSql(IData param, String bindfield, String type) throws Exception
    {
        StringBuffer privsql = new StringBuffer();
        String area_code = param.getString("AREA_CODE", "");
        String depart_id = param.getString("DEPART_ID", "");
        String manager_id = param.getString("CUST_MANAGER_ID", "");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "ORG_LISTALL"))
        {
            param.put("MGL_CUST_MANAGER_ID", getVisit().getStaffId());
        }
        String fetch_type = "";
        if (!"".equals(manager_id))
        {
            fetch_type = "C";
        }
        else if (!"".equals(depart_id))
        {
            fetch_type = "D";
        }
        else if (area_code.equals(getVisit().getProvinceCode()))
            fetch_type = "N";
        else
        {
            fetch_type = "A";
        }

        if ("C".equals(fetch_type))
        {
            privsql.append(getManagerSqlByGrant(getVisit(), param, manager_id, bindfield, type));
        }
        if ("D".equals(fetch_type))
        {
            privsql.append(getDepartSqlByGrant(getVisit(), param, depart_id, bindfield, type));
        }
        if ("A".equals(fetch_type))
        {
            privsql.append(getAreaSqlByGrant(getVisit(), param, area_code, bindfield, type));
        }

        return privsql.toString();
    }
    
    protected static String getAreaSqlByGrant(BizVisit pd, IData param, String area_code, String bindfield, String staff_type) throws Exception
    {
        String[] areas = area_code.split(",");
        if (areas.length == 1)
        {
            param.put("_AREA_FRAME_1", StaticUtil.getStaticValue(pd, "TD_M_AREA", "AREA_CODE", "AREA_FRAME", areas[0]));
            if ("1".equals(staff_type))
            {
                return " and exists (select comarea.AREA_CODE from TD_M_AREA comarea, TD_M_DEPART comdept, TD_M_STAFF comstaff where comarea.AREA_CODE = comdept.AREA_CODE and comdept.DEPART_ID = comstaff.DEPART_ID and comstaff.STAFF_ID = "
                        + bindfield + " and comarea.AREA_FRAME like :_AREA_FRAME_1 || '%')";
            }
            if ("2".equals(staff_type))
            {
                return " and exists (select cms.CUST_MANAGER_ID from TF_F_CUST_MANAGER_STAFF cms where cms.CUST_MANAGER_ID = " + bindfield + " and cms.AREA_FRAME like :_AREA_FRAME_1 || '%')";
            }
        }

        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < areas.length; ++i)
        {
            param.put("_AREA_FRAME_" + (i + 1), StaticUtil.getStaticValue(pd, "TD_M_AREA", "AREA_CODE", "AREA_FRAME", areas[i]));
            if (i > 0)
                sql.append(" or ");
            if ("1".equals(staff_type))
            {
                sql.append(" exists (select comarea.AREA_CODE from TD_M_AREA comarea, TD_M_DEPART comdept, TD_M_STAFF comstaff where comarea.AREA_CODE = comdept.AREA_CODE and comdept.DEPART_ID = comstaff.DEPART_ID and comstaff.STAFF_ID = "
                        + bindfield + " and comarea.AREA_FRAME like :_AREA_FRAME_" + (i + 1) + " || '%')");
            }
            if ("2".equals(staff_type))
            {
                sql.append(" exists (select cms.CUST_MANAGER_ID from TF_F_CUST_MANAGER_STAFF cms where cms.CUST_MANAGER_ID = " + bindfield + " and cms.AERA_FRAME like :_AREA_FRAME_" + (i + 1) + " || '%')");
            }
        }

        return " and (" + sql + ")";
    }

    protected static String getManagerSqlByGrant(BizVisit pd, IData param, String manager_id, String bindfield, String staff_type) throws Exception
    {
        String[] managers = manager_id.split(",");
        if (managers.length == 1)
            return " and " + bindfield + " = :CUST_MANAGER_ID";

        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < managers.length; ++i)
        {
            param.put("_CUST_MANAGER_ID_" + (i + 1), managers[i]);
            sql.append(":_CUST_MANAGER_ID_" + (i + 1));
            if (i == managers.length - 1)
                continue;
            sql.append(",");
        }

        return " and " + bindfield + " in (" + sql + ")";
    }
    
    protected static String getDepartSqlByGrant(BizVisit v, IData param, String depart_id, String bindfield, String staff_type) throws Exception
    {
        String[] departs = depart_id.split(",");
        if (departs.length == 1)
        {
            param.put("_DEPART_FRAME_1", StaticUtil.getStaticValue(v, "TD_M_DEPART", "DEPART_ID", "DEPART_FRAME", departs[0]));
            if ("1".equals(staff_type))
            {
                return " and exists (select comdept.DEPART_ID from TD_M_DEPART comdept, TD_M_STAFF comstaff where comdept.DEPART_ID = comstaff.DEPART_ID and comstaff.STAFF_ID = " + bindfield
                        + " and comdept.DEPART_FRAME like :_DEPART_FRAME_1 || '%')";
            }
            if ("2".equals(staff_type))
            {
                return " and exists (select cms.CUST_MANAGER_ID from TF_F_CUST_MANAGER_STAFF cms where cms.CUST_MANAGER_ID = " + bindfield + " and cms.DEPART_FRAME like :_DEPART_FRAME_1 || '%')";
            }
        }

        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < departs.length; ++i)
        {
            param.put("_DEPART_FRAME_" + (i + 1), StaticUtil.getStaticValue(v, "TD_M_DEPART", "DEPART_ID", "DEPART_FRAME", departs[i]));
            if (i > 0)
                sql.append(" or ");
            if ("1".equals(staff_type))
            {
                sql.append(" exists (select comdept.DEPART_ID from TD_M_DEPART comdept, TD_M_STAFF comstaff where comdept.DEPART_ID = comstaff.DEPART_ID and comstaff.STAFF_ID = " + bindfield + " and comdept.DEPART_FRAME like :_DEPART_FRAME_"
                        + (i + 1) + " || '%')");
            }
            if ("2".equals(staff_type))
            {
                sql.append(" exists (select cms.CUST_MANAGER_ID from TF_F_CUST_MANAGER_STAFF cms where cms.CUST_MANAGER_ID = " + bindfield + " and cms.DEPART_FRAME like :_DEPART_FRAME_" + (i + 1) + " || '%')");
            }
        }

        return " and (" + sql + ")";
    }
}
