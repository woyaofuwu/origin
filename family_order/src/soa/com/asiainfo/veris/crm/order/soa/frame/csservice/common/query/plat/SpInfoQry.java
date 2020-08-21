
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class SpInfoQry
{
    public static IDataset qryMaxMonthSpBizList(String state, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("STATE", state);
        param.put("EPARCHY_CODE", eparchyCode);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT MAX(AVAIL_MONTH) AVAIL_MONTH FROM TD_B_SP_BIZ_LIST ");
        parser.addSQL("WHERE STATE = :STATE ");
        parser.addSQL("AND TRUNC(SYSDATE) BETWEEN TRUNC(START_DATE) AND TRUNC(END_DATE) ");
        parser.addSQL("AND (RSRV_STR1 = :EPARCHY_CODE OR RSRV_STR1 = 'ZZZZ') ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBizServiceInfosByBillTypeSpBizCode(String SERVICE_CODE, String SP_CODE) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SP_CODE", SP_CODE);
        cond.put("SERVICE_CODE", SERVICE_CODE);
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_BIZSPCODE_BILLTYPE_BIZTYPECODE", cond);
    }

    public static IDataset queryBizServiceInfosBySpBizCode(String SERVICE_CODE, String SP_CODE) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SP_CODE", SP_CODE);
        cond.put("SERVICE_CODE", SERVICE_CODE);
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_BIZSPCODE", cond);
    }

    /**
     * 根据SP_CODE，查询sp供应商信息
     */

    public static IDataset queryCommpara1704(IData cond, Pagination pagination) throws Exception
    {

        cond.put("PARAM_ATTR", "1704");
        cond.put("SUBSYS_CODE", "CSM");
        cond.put("EPARCHY_CODE", Route.getCrmDefaultDb());

        StringBuilder sql = new StringBuilder(
                "SELECT subsys_code,param_attr,param_code,param_name,para_code1,para_code2,para_code3,para_code4,para_code5,para_code6,para_code7,para_code8,para_code9,para_code12,para_code13,para_code14,para_code15,para_code16,para_code17,para_code18,para_code19,para_code20,para_code21,para_code22,para_code23,para_code24,para_code25,to_char(para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time "
                        + "FROM td_s_commpara "
                        + " WHERE subsys_code=:SUBSYS_CODE "
                        + "AND param_attr=:PARAM_ATTR "
                        + "AND param_code=:PARAM_CODE "
                        + "AND (eparchy_code=:EPARCHY_CODE Or eparchy_code ='ZZZZ') "
                        + "And Sysdate Between start_date And end_date ");
        return Dao.qryBySql(sql, cond, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryCommparaNeedSqlref(IData cond) throws Exception
    {

        cond.put("PARAM_ATTR", "1704");
        cond.put("SUBSYS_CODE", "CSM");
        cond.put("EPARCHY_CODE", "0731");

        StringBuilder sql = new StringBuilder(
                "SELECT subsys_code,param_attr,param_code,param_name,para_code1,para_code2,para_code3,para_code4,para_code5,para_code6,para_code7,para_code8,para_code9,para_code12,para_code13,para_code14,para_code15,para_code16,para_code17,para_code18,para_code19,para_code20,para_code21,para_code22,para_code23,para_code24,para_code25,to_char(para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time "
                        + "FROM td_s_commpara "
                        + " WHERE subsys_code=:SUBSYS_CODE "
                        + "AND param_attr=:PARAM_ATTR "
                        + "AND param_code=:PARAM_CODE "
                        + "AND (eparchy_code=:EPARCHY_CODE Or eparchy_code ='ZZZZ') "
                        + "And Sysdate Between start_date And end_date ");
        return Dao.qryBySql(sql, cond);

        // return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAMCODE", cond,Route.CONN_CRM_CEN);

    }

    /**
     * 根据企业代码和结算月份查询Sp结算费用
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryImportData(IData inparam) throws Exception
    {
        String sql = "select SP_CODE,BCYC_ID,COMPANY_NAME,TRADE_TYPE,STREAM_NO,TRIM(TO_CHAR(FEE/100,'99999990.99')) SP_FEE,"
                + "TRIM(TO_CHAR(BALANCE/100,'99999990.99')) SP_BALANCE,MONTH from TF_A_SETTLE_PAYDETAIL A where A.BCYC_ID=TO_NUMBER(:BCYC_ID) and A.SP_CODE=:SP_CODE and A.DATA_TYPE='0' and A.DATA_SOURCE='1'";
        return Dao.qryBySql(new StringBuilder(sql), inparam);
    }

    public static IDataset queryServiceInfoBySp(String biz_type_code, String sp_code) throws Exception
    {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT BIZ_CODE SERVICE_ID, ('(' || BIZ_CODE || ')' || SERVICE_NAME) SERVICE_NAME" + " FROM TD_B_PLATSVC A" + " WHERE A.BIZ_STATE_CODE = 'A' AND A.SP_CODE = '" + sp_code + "' AND A.BIZ_TYPE_CODE='" + biz_type_code);
        sql.append(" ' AND SYSDATE BETWEEN START_DATE AND END_DATE " + " AND (A.SP_CODE <> '925555' OR A.BIZ_CODE = '+MAILMF' OR A.BIZ_CODE = '+MAILBZ' OR A.BIZ_CODE = '+MAILVIP')" + " ORDER BY A.BIZ_CODE");

        return Dao.qryBySql(sql, new DataMap());
        // return new DatasetList();
    }

    public static IDataset querySpBizInfo(String SP_CODE, String BIZ_CODE) throws Exception
    {
        /*IData cond = new DataMap();
        cond.put("SP_CODE", SP_CODE);
        cond.put("BIZ_CODE", BIZ_CODE);*/
        IDataset upcDatas = UpcCall.querySpServiceAndProdByCond(SP_CODE, BIZ_CODE, null, null);
        if(ArrayUtil.isNotEmpty(upcDatas))
        {
        	for(int i = 0 ; i < upcDatas.size() ; i ++)
        	{
        		IData upcData = upcDatas.getData(i);
        		if(!StringUtils.equals("A", upcData.getString("SP_STATUS")))
        		{
        			upcDatas.remove(i);
        			i--;
        		}
        	}
        }
        return upcDatas;
        //return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_PK", cond);
    }

    /**
     * 根据结算月份和企业代码查询SP企业是否存在往月欠费
     * 
     * @param pd
     * @param dataset
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IData querySpFeeByBcycId(IData inparam) throws Exception
    {

        String sql = "SELECT SP_ID,SP_CODE,COMPANY_NAME,TRADE_TYPE,STREAM_NO,FEE,BALANCE,DATA_SOURCE,DATA_TYPE,MONTH,IN_TIME,BCYC_ID "
                + " FROM TF_A_SETTLE_PAYDETAIL WHERE SP_CODE=:SP_CODE AND BCYC_ID<TO_NUMBER(:MONTH) AND DATA_SOURCE='1' AND DATA_TYPE='0' and BALANCE<0 and FEE<=0";

        IDataset result = Dao.qryBySql(new StringBuilder(sql), inparam);

        Float bal = 0.0f;
        Float fee = 0.0f;
        if (result == null || result.size() < 1)
        {
            return null;
        }
        for (int i = 0; i < result.size(); i++)
        {
            IData data = result.getData(i);
            bal = Float.parseFloat(data.getString("BALANCE").trim());
            fee = Float.parseFloat(data.getString("FEE").trim());
            if (bal < 0 && fee <= 0)
            {
                return data;
            }
        }

        return null;

    }

    /**
     * 根据结算月份和费用类型、数据类型查询SP结算费用信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset querySpFeeByTradeType(IData inparam, Pagination pagination) throws Exception
    {

        String sql = "SELECT SP_ID,SP_CODE,COMPANY_NAME,TRADE_TYPE,STREAM_NO,TRIM(TO_CHAR(FEE/100,'9999999990.99')) SP_FEE,TRIM(TO_CHAR(BALANCE/100,'9999999990.99')) SP_BALANCE,DATA_SOURCE,DATA_TYPE,MONTH,IN_TIME,BCYC_ID,OPER_TIME,OPER_STAFF_ID,RSRV_STR6 "
                + " FROM TF_A_SETTLE_PAYDETAIL WHERE BCYC_ID=TO_NUMBER(:BCYC_ID)  " + " AND DATA_SOURCE='1' AND DATA_TYPE=:DATA_TYPE " + " AND (TRADE_TYPE=:TRADE_TYPE OR :TRADE_TYPE IS NULL) ";

        return Dao.qryBySql(new StringBuilder(sql), inparam, pagination);
    }

    // 导出用，指定eparchy_code
    public static IDataset querySpFeeByTradeType(IData inparam, String eparchy_code) throws Exception
    {

        String sql = "SELECT SP_ID,SP_CODE,COMPANY_NAME,TRADE_TYPE,STREAM_NO,TRIM(TO_CHAR(FEE/100,'9999999990.99')) SP_FEE,TRIM(TO_CHAR(BALANCE/100,'9999999990.99')) SP_BALANCE,DATA_SOURCE,DATA_TYPE,MONTH,IN_TIME,BCYC_ID,OPER_TIME,OPER_STAFF_ID,RSRV_STR6 "
                + " FROM TF_A_SETTLE_PAYDETAIL WHERE BCYC_ID=TO_NUMBER(:BCYC_ID)  " + " AND DATA_SOURCE='1' AND DATA_TYPE=:DATA_TYPE " + " AND (TRADE_TYPE=:TRADE_TYPE OR :TRADE_TYPE IS NULL) ";

        return Dao.qryBySql(new StringBuilder(sql), inparam, null, eparchy_code);
    }

    /**
     * code_code被缓存，只能拼sql
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySPInfo(IData param, Pagination pagination) throws Exception
    {
        String sql = "SELECT SP_ID,SP_CODE,SP_NAME,SP_NAME_EN,SP_SHORT_NAME,SP_TYPE,SP_ATTR,SP_STATUS,SP_DESC,SERV_CODE,PLAT_CODE,IN_PROVINCE,CON_PROVINCE,CS_TEL,CS_URL,CONTACT,OPR_SOURCE,FIRST_DATE,FILE_NAME,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5 "
                + " FROM TD_M_SP_INFO WHERE 1 = 1 "
                + " AND (SP_CODE = :SP_CODE OR :SP_CODE IS NULL) "
                + " AND (PLAT_CODE = :PLAT_CODE OR :PLAT_CODE IS NULL) "
                + " AND (SP_NAME LIKE '%' || :SP_NAME || '%' OR :SP_NAME IS NULL) "
                + " AND (SP_TYPE = :SP_TYPE OR :SP_TYPE IS NULL) "
                + " AND (SP_ATTR = :SP_ATTR OR  :SP_ATTR IS NULL) "
                + " AND (SP_STATUS = :SP_STATUS OR :SP_STATUS IS NULL) "
                + " AND (UPDATE_STAFF_ID = :UPDATE_STAFF_ID OR :UPDATE_STAFF_ID IS NULL) " + " AND START_DATE <= TO_DATE(:START_DATE,'YYYY-MM-DD') " + " AND END_DATE >= TO_DATE(:END_DATE,'YYYY-MM-DD') ";

        return Dao.qryBySql(new StringBuilder(sql), param, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset querySpInfosBySpcodeSpstatus(String SP_CODE) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SP_CODE", SP_CODE);
        return Dao.qryByCode("TD_M_SP_INFO", "SEL_BY_SPCODE_SPSTATUS", cond);
    }

    /**
     * 按时间查询SP结算费用
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySpSettleByDate(IData inparam, Pagination pagination) throws Exception
    {
        String sql = "select a.SP_CODE,a.COMPANY_NAME,a.TRADE_TYPE,TRIM(TO_CHAR(b.FEE/100,'9999999990.99')) SP_FEE,TRIM(TO_CHAR(a.BALANCE/100,'9999999990.99')) SP_BALANCE,a.BCYC_ID "
                + " From (select sp_code,company_name,trade_type,bcyc_id,sum(fee) fee,sum(balance) balance From  TF_A_SETTLE_PAYDETAIL where data_source='1' and data_type='0' "
                + " group by sp_code,company_name,trade_type,bcyc_id )a, (select sp_code,bcyc_id,sum(fee) fee,sum(balance) balance From  TF_A_SETTLE_PAYDETAIL " + " where data_source='1' and data_type='1' group by sp_code,bcyc_id) b "
                + " where a.sp_code=b.sp_code(+) " + " and a.bcyc_id=b.bcyc_id(+) " + " and a.bcyc_id>=:START_DATE " + " and a.bcyc_id<=:END_DATE " + " and (a.company_name like :COMPANY_NAME or :COMPANY_NAME is null) "
                + " and (a.sp_code=:SP_CODE or :SP_CODE is null) " + " and (a.trade_type=:TRADE_TYPE or :TRADE_TYPE is null) "
                + " and ((b.fee is null and :DATA_TYPE='0' and a.fee<=0) or (b.fee is not null and :DATA_TYPE='1') or :DATA_TYPE is null) " + " order by a.bcyc_id,a.BALANCE ";
        return Dao.qryBySql(new StringBuilder(sql), inparam, pagination);
    }

    /**
     * 导出用
     * 
     * @param inparam
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset querySpSettleByDate(IData inparam, String eparchy_code) throws Exception
    {
        String sql = "select a.SP_CODE,a.COMPANY_NAME,a.TRADE_TYPE,TRIM(TO_CHAR(b.FEE/100,'9999999990.99')) SP_FEE,TRIM(TO_CHAR(a.BALANCE/100,'9999999990.99')) SP_BALANCE,a.BCYC_ID "
                + " From (select sp_code,company_name,trade_type,bcyc_id,sum(fee) fee,sum(balance) balance From  TF_A_SETTLE_PAYDETAIL where data_source='1' and data_type='0' "
                + " group by sp_code,company_name,trade_type,bcyc_id )a, (select sp_code,bcyc_id,sum(fee) fee,sum(balance) balance From  TF_A_SETTLE_PAYDETAIL " + " where data_source='1' and data_type='1' group by sp_code,bcyc_id) b "
                + " where a.sp_code=b.sp_code(+) " + " and a.bcyc_id=b.bcyc_id(+) " + " and a.bcyc_id>=:START_DATE " + " and a.bcyc_id<=:END_DATE " + " and (a.company_name like :COMPANY_NAME or :COMPANY_NAME is null) "
                + " and (a.sp_code=:SP_CODE or :SP_CODE is null) " + " and (a.trade_type=:TRADE_TYPE or :TRADE_TYPE is null) "
                + " and ((b.fee is null and :DATA_TYPE='0' and a.fee<=0) or (b.fee is not null and :DATA_TYPE='1') or :DATA_TYPE is null) " + " order by a.bcyc_id,a.BALANCE ";

        return Dao.qryBySql(new StringBuilder(sql), inparam, null, eparchy_code);
    }

    /**
     * 根据付款月份查询SP结算付款费用信息
     * 
     * @param pd
     * @param inParam
     * @return IDataset
     * @throws Exception
     */
    public static IDataset querySpSettleByMonth(IData inparam, Pagination pagination) throws Exception
    {

        String sql = "SELECT SP_ID,SP_CODE,COMPANY_NAME,TRADE_TYPE,TRIM(TO_CHAR(STREAM_NO,FEE/100,'9999999990.99')) SP_FEE,TRIM(TO_CHAR(BALANCE/100,'9999999990.99')) SP_BALANCE,DATA_SOURCE,DATA_TYPE,MONTH,IN_TIME,BCYC_ID,OPER_TIME,OPER_STAFF_ID,RSRV_STR6 "
                + " FROM TF_A_SETTLE_PAYDETAIL WHERE MONTH=TO_NUMBER(:MONTH) " + " AND DATA_SOURCE='1' AND DATA_TYPE=:DATA_TYPE " + " AND (TRADE_TYPE = :TRADE_TYPE OR :TRADE_TYPE IS NULL) ";
        return Dao.qryBySql(new StringBuilder(sql), inparam, pagination);
    }

    public static IDataset querySpSettleByMonth(IData inparam, String eparchy_code) throws Exception
    {

        String sql = "SELECT SP_ID,SP_CODE,COMPANY_NAME,TRADE_TYPE,STREAM_NO,TRIM(TO_CHAR(FEE/100,'9999999990.99')) SP_FEE,TRIM(TO_CHAR(BALANCE/100,'9999999990.99')) SP_BALANCE,DATA_SOURCE,DATA_TYPE,MONTH,IN_TIME,BCYC_ID,OPER_TIME,OPER_STAFF_ID,RSRV_STR6 "
                + " FROM TF_A_SETTLE_PAYDETAIL WHERE MONTH=TO_NUMBER(:MONTH) " + " AND DATA_SOURCE='1' AND DATA_TYPE=:DATA_TYPE " + " AND (TRADE_TYPE = :TRADE_TYPE OR :TRADE_TYPE IS NULL) ";
        return Dao.qryBySql(new StringBuilder(sql), inparam, null, eparchy_code);
    }
    /**
	 * 查询服务局数据
	 */
	public static IDataset getPlatSVCBySPInfo(IData inParam) throws Exception{
		IDataset resultList = Dao.qryByCodeParser("TD_B_PLATSVC","SEL_BY_SP_BIZ_INFO",inParam,Route.CONN_CRM_CEN);
		return resultList;
	}
}