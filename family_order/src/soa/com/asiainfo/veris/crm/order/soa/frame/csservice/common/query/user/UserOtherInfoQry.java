
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctConsignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class UserOtherInfoQry
{

    /**
     * 用管中心通行证绑定passid
     * 
     * @param userId
     * @param rsrvValueCode
     * @param rsrvValue
     * @return
     * @throws Exception
     */
    public static int cancelPassIDInfoByUserId(String userId, String rsrvValueCode, String passId, String staffId, String departId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE", passId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        cond.put("UPDATE_STAFF_ID", staffId);
        cond.put("UPDATE_DEPART_ID", departId);
        cond.put("UPDATE_TIME", SysDateMgr.getSysTime());
        int result = Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "DEL_BY_USERID_RSRVVALUE", cond);

        return result;
    }

    public static IDataset checkLpzsLbyb(String userId, String rsrvValueCode, String rsrvStr2) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("RSRV_STR2", rsrvStr2);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_CHECK_LPZS_LBYB", param);

        return dataset;
    }
    
    public static IDataset queryUserAllOther(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_ESP_DATA", param);
        return dataset;
    }

    public static IDataset checkPassChange(String user_id, String rsrv_value_code) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", user_id);
        cond.put("RSRV_VALUE_CODE", rsrv_value_code);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_RSVCEND_BY_USERID", cond);
    }

    /**
     * @Function: exportVipInfos
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:03:01 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset exportVipInfos(String area_code, String start_date, String end_date, String rsrv_tag2, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("RSRV_TAG2", rsrv_tag2);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        param.put("AREA_CODE", area_code);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT /*+ index(U IDX_TF_F_USER_OTHER_VALUECODE)*/");
        parser.addSQL(" US.SERIAL_NUMBER SERIAL_NUMBER,");
        parser.addSQL(" U.RSRV_STR1 CUST_NAME,");
        parser.addSQL(" U.RSRV_STR2 DEPST_ID,");
        parser.addSQL(" U.RSRV_STR3 ADDR_ID,");
        parser.addSQL(" DECODE(U.RSRV_STR4,'0','营运交通工具意外伤害险','家庭财产火灾爆炸保险') XZ,");

        parser.addSQL(" U.RSRV_STR8 SXR_NAME,");
        parser.addSQL(" U.RSRV_STR9 SXR_DEPST_ID,");
        parser.addSQL(" U.RSRV_STR10 SXR_ADDR,");

        parser.addSQL(" U.UPDATE_STAFF_ID UPDATE_STAFF_ID,");
        parser.addSQL(" U.UPDATE_DEPART_ID UPDATE_DEPART_ID,");
        parser.addSQL(" TO_CHAR(U.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,");
        parser.addSQL(" U.RSRV_STR25 RSRV_STR25,");
        parser.addSQL(" U.RSRV_STR29 RSRV_STR29,");
        parser.addSQL(" U.RSRV_STR30 RSRV_STR30,");
        parser.addSQL(" TO_CHAR(U.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,");
        parser.addSQL(" TO_CHAR(U.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,");
        parser.addSQL(" TO_CHAR(U.RSRV_DATE10,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE10,");
        parser.addSQL(" U.RSRV_STR6 BX_ID,");
        parser.addSQL(" US.CITY_CODE CITY_CODE,");
        parser.addSQL(" DECODE(U.RSRV_TAG1,'1','是','否') RSRV_TAG1,");
        parser.addSQL(" DECODE(U.RSRV_TAG2,'1','是','否') RSRV_TAG2");
        parser.addSQL(" FROM  TF_F_USER US, TF_F_USER_OTHER U");
        parser.addSQL(" WHERE U.RSRV_VALUE_CODE = 'VIP_SAC_BX'");
        parser.addSQL(" AND U.USER_ID = US.USER_ID");
        parser.addSQL(" AND U.PARTITION_ID = US.PARTITION_ID");
        if ("0".equals(param.getString("RSRV_TAG2")))
        {
            parser.addSQL(" AND U.RSRV_TAG2 IS NULL");
        }
        else if ("1".equals(param.getString("RSRV_TAG2")))
        {
            parser.addSQL(" AND U.RSRV_TAG2 ='1'");
        }
        parser.addSQL(" AND U.START_DATE BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') AND");
        parser.addSQL(" TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')");

        parser.addSQL(" AND U.END_DATE > SYSDATE");

        parser.addSQL(" AND US.CITY_CODE IN");
        parser.addSQL(" (SELECT A.AREA_CODE");
        parser.addSQL(" FROM TD_M_AREA A");
        parser.addSQL(" START WITH A.AREA_CODE = :AREA_CODE");
        parser.addSQL(" CONNECT BY PRIOR A.AREA_CODE = A.PARENT_AREA_CODE)");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @methodName: getAllOtherInfoByUserIdValueCode
     * @Description: 根据USER_ID和RSRV_VALUE_CODE条件查询，不加时间限制
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-3-28 下午2:13:08
     */
    public static IDataset getAllOtherInfoByUserIdValueCode(String userId, String rsrvValueCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE_1", cond);
    }

    /**
     * @Function: getBDserialNumber
     * @Description: 取得账户绑定的手机号
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:04:10 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getBDserialNumber(String user_id, String rsrv_value_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT t.user_id ACCT_ID,t.RSRV_VALUE_CODE,t.RSRV_VALUE USER_ID,START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        parser.addSQL(" FROM tf_f_user_other t ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND t.RSRV_VALUE_CODE =:RSRV_VALUE_CODE ");
        parser.addSQL(" AND t.START_DATE<=SYSDATE ");
        parser.addSQL(" AND t.END_DATE>SYSDATE ");
        parser.addSQL(" AND t.RSRV_VALUE=:USER_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 获得USER_OTHER表中办理过影号的信息*****参数不合理。删除该方法
     * 
     * @return IDataset
     * @throws Exception
     */
    /*
     * public static IDataset getDeptuyUserOther(IData comData) throws Exception { String sql = comData.getString("sql",
     * ""); return Dao.qryByCode("TF_F_USER_OTHER", sql, comData); }
     */
    /**
     * 获得USER_OTHER表中办理过影号的信息
     * 
     * @param userId
     * @param openType
     * @return
     * @throws Exception
     */
    public static IDataset getDeptuyUserOther(String userId, String openType) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("OPEN_TYPE", openType);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRV_VALUE_CODE_RSRVSTR4", param);
    }

    public static IDataset getErrPasswdValue(String userId, String day) throws Exception
    {
        IData param = new DataMap();
        param.put("DAY", day);
        param.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_ERR_PASSWD_VAL", param);
        return dataset;
    }

    /**
     * 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE,EPARCHY_CODE查询用户信息
     */
    public static IDataset getForbidenInfo(String user_id, String service_mode, String process_tag) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);
        inparams.put("SERVICE_MODE", service_mode);
        inparams.put("PROCESS_TAG", process_tag);

        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_PK", inparams);
    }

    public static IDataset getInfoByInstId(String instId, String partitionId) throws Exception
    {
        IData param = new DataMap();
        param.put("INST_ID", instId);
        param.put("PARTITION_ID", partitionId);
        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_INST_ID", param);
    }

    /**
     * * 根据INST_ID查询OTHER表记录 与getInfoByInstId区别是加了时间判断
     * 
     * @param instId
     * @param partitionId
     * @return
     * @throws Exception
     */
    public static IDataset getInfoByInstId2(String instId, String partitionId) throws Exception
    {
        IData param = new DataMap();
        param.put("INST_ID", instId);
        param.put("PARTITION_ID", partitionId);
        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_INST_ID2", param);
    }

    /**
     * 判断是否用过
     */
    public static IDataset getInfoByRsrv3(String userId, String rsrvValueCode, String rsrv30) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("RSRV_VALUE_CODE", rsrvValueCode);
        inparam.put("RSRV_STR30", rsrv30);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID_RSRV3", inparam);
    }

    public static IDataset getInfoByTradeId(String userId, String tradeId, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_ID", tradeId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_COMM_BY_TRADE", param);
    }

    public static IDataset getInvoiceInfo(String invoiceNo, String serviceMode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", invoiceNo);
        cond.put("SERVICE_MODE", serviceMode);

        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_INVOICE_NO", cond);
    }

    /**
     * @Function: getLeasedLineSalePayInfo
     * @Description: 从集团库 获取集团营销预存信息 专线类
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:06:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getLeasedLineSalePayInfo(String cust_id, String eparchy_code) throws Exception
    {

        IData data = new DataMap();
        data.put("CUST_ID", cust_id);
        data.put("EPARCHY_CODE", eparchy_code);

        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT GROUP_ID, ");
        parser.addSQL("     USER_ID, ");
        parser.addSQL("     PRODUCT_NAME, ");
        parser.addSQL("     sum(PAY_FEE) PAY_FEE, ");
        parser.addSQL("     (sum(PAY_FEE) - sum(PAYED_FEE)) MAYPAY_FEE, ");
        parser.addSQL("     abs(sum(PAYED_FEE)) PAYED_FEE ");
        parser.addSQL("FROM (SELECT A.RSRV_STR1 GROUP_ID, ");
        parser.addSQL("             A.USER_ID, ");
        parser.addSQL("             A.RSRV_STR6 PRODUCT_NAME, ");
        parser.addSQL("             ROUND(A.RSRV_NUM1 / 100.00,2) PAY_FEE, ");
        parser.addSQL("             0 PAYED_FEE ");
        parser.addSQL("        FROM TF_F_USER_OTHER A, ");
        parser.addSQL("             (SELECT b.user_id,b.PARTITION_ID  ");
        parser.addSQL("                FROM td_s_commpara a, tf_f_user b, tf_f_user_product p ");
        parser.addSQL("                WHERE p.product_id = a.param_code ");
        parser.addSQL("                 and a.subsys_code = 'CGM' ");
        parser.addSQL("                 and a.param_attr = '1129' ");
        parser.addSQL("                 and b.USER_ID = p.USER_ID ");
        parser.addSQL("                 and b.PARTITION_ID = p.PARTITION_ID ");
        parser.addSQL("                 and b.EPARCHY_CODE = :EPARCHY_CODE ");
        parser.addSQL("                 and b.CUST_ID = :CUST_ID) S, ");
        parser.addSQL("             (SELECT PARA_CODE1, PARA_CODE2, PARAM_NAME ");
        parser.addSQL("                FROM td_s_commpara t ");
        parser.addSQL("               WHERE subsys_code = 'CGM' ");
        parser.addSQL("                 and param_attr = '1130' ");
        parser.addSQL("                 and EPARCHY_CODE = :EPARCHY_CODE) T ");
        parser.addSQL("       WHERE A.RSRV_VALUE_CODE = 'GRSF' ");
        parser.addSQL("         and A.USER_ID = S.USER_ID ");
        parser.addSQL("         and A.PARTITION_ID = S.PARTITION_ID  ");
        parser.addSQL("         and A.RSRV_DATE1 between to_date(T.PARA_CODE1, 'yyyy-mm-dd') and to_date(T.PARA_CODE2, 'yyyy-mm-dd') ");
        parser.addSQL("      union all ");
        parser.addSQL("      SELECT A.RSRV_STR1 GROUP_ID, ");
        parser.addSQL("             A.USER_ID, ");
        parser.addSQL("             A.RSRV_STR16 PRODUCT_NAME, ");
        parser.addSQL("             0 PAY_FEE, ");
        parser.addSQL("             ROUND(to_number(RSRV_STR17) / 100.00,2) PAYED_FEE ");
        parser.addSQL("        FROM TF_F_USER_OTHER A, ");
        parser.addSQL("             (SELECT b.user_id,b.PARTITION_ID  ");
        parser.addSQL("               FROM td_s_commpara a, tf_f_user b, tf_f_user_product p ");
        parser.addSQL("              WHERE p.product_id = a.param_code ");
        parser.addSQL("                 and b.USER_ID = p.USER_ID ");
        parser.addSQL("                 and b.PARTITION_ID = p.PARTITION_ID ");
        parser.addSQL("                 and a.subsys_code = 'CGM' ");
        parser.addSQL("                 and a.param_attr = '1129' ");
        parser.addSQL("                 and b.EPARCHY_CODE = :EPARCHY_CODE ");
        parser.addSQL("                 and b.CUST_ID = :CUST_ID) S, ");
        parser.addSQL("             (SELECT PARA_CODE1, PARA_CODE2, PARAM_NAME ");
        parser.addSQL("                FROM td_s_commpara t ");
        parser.addSQL("               WHERE subsys_code = 'CGM' ");
        parser.addSQL("                 and param_attr = '1130' ");
        parser.addSQL("                 and EPARCHY_CODE = :EPARCHY_CODE) T ");
        parser.addSQL("       WHERE A.RSRV_VALUE_CODE = 'PRES' ");
        parser.addSQL("         and A.USER_ID = S.USER_ID ");
        parser.addSQL("         and A.PARTITION_ID = S.PARTITION_ID ");
        parser.addSQL("         and A.Start_Date >= to_date(T.PARA_CODE1, 'yyyy-mm-dd')) FB ");
        parser.addSQL(" group by GROUP_ID, USER_ID, PRODUCT_NAME ");
        parser.addSQL(" order by PAYED_FEE ");

        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return dataset;
    }

    /**
     * 用管中心通行证绑定passid
     * 
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset getMaxStartDate(String userId, String rsrvValueCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        cond.put("UPDATE_TIME", SysDateMgr.getSysTime());
        IDataset result = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_MAXSTARTDATE2", cond);

        return result;
    }

    /**
     * @Function: getNLeasedLineSalePayInfo
     * @Description: 从集团库获取集团营销预存信息 非专线类
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:07:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getNLeasedLineSalePayInfo(String cust_id, String eparchy_code) throws Exception
    {

        IData data = new DataMap();
        data.put("CUST_ID", cust_id);
        data.put("EPARCHY_CODE", eparchy_code);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT GROUP_ID, ");
        parser.addSQL("   CUST_NAME, ");
        parser.addSQL("   USER_ID, ");
        parser.addSQL("   PRODUCT_NAME, ");
        parser.addSQL("   SUM(PAY_FEE) PAY_FEE, ");
        parser.addSQL("   SUM(PAY_FEE) - SUM(PAYED_FEE) MAYPAY_FEE, ");
        parser.addSQL("   ABS(SUM(PAYED_FEE)) PAYED_FEE ");
        parser.addSQL(" FROM (SELECT A.RSRV_STR1 GROUP_ID, ");
        parser.addSQL("           A.RSRV_STR4 CUST_NAME, ");
        parser.addSQL("           A.USER_ID, ");
        parser.addSQL("           A.RSRV_STR6 PRODUCT_NAME, ");
        parser.addSQL("           ROUND(A.RSRV_NUM1 / 100.00,2) PAY_FEE, ");
        parser.addSQL("           0 PAYED_FEE ");
        parser.addSQL("      FROM TF_F_USER_OTHER A, ");
        parser.addSQL("           (SELECT b.user_id,b.PARTITION_ID  ");
        parser.addSQL("              FROM tf_f_user b, tf_f_user_product p ");
        parser.addSQL("             WHERE NOT EXISTS (SELECT param_code ");
        parser.addSQL("                      FROM td_s_commpara c ");
        parser.addSQL("                     WHERE c.subsys_code = 'CGM' ");
        parser.addSQL("                       and c.param_attr = '1129' ");
        parser.addSQL("                       and p.product_id = c.param_code) ");
        parser.addSQL("               and b.USER_ID = p.USER_ID ");
        parser.addSQL("               and b.PARTITION_ID = p.PARTITION_ID ");
        parser.addSQL("               and b.EPARCHY_CODE = :EPARCHY_CODE ");
        parser.addSQL("               and b.CUST_ID = :CUST_ID) S, ");
        parser.addSQL("           (SELECT PARA_CODE1, PARA_CODE2, PARAM_NAME ");
        parser.addSQL("              FROM td_s_commpara t ");
        parser.addSQL("             WHERE subsys_code = 'CGM' ");
        parser.addSQL("               and param_attr = '1130' ");
        parser.addSQL("               and EPARCHY_CODE = :EPARCHY_CODE) T ");
        parser.addSQL("     WHERE A.RSRV_VALUE_CODE = 'GRSF' ");
        parser.addSQL("       and A.USER_ID = S.USER_ID ");
        parser.addSQL("       and A.PARTITION_ID = S.PARTITION_ID ");
        parser.addSQL("       and A.RSRV_DATE1 BETWEEN to_date(T.PARA_CODE1, 'yyyy-mm-dd') AND ");
        parser.addSQL("           to_date(T.PARA_CODE2, 'yyyy-mm-dd') ");
        parser.addSQL("    union all ");
        parser.addSQL("    SELECT A.RSRV_STR1 GROUP_ID, ");
        parser.addSQL("           A.RSRV_STR15 CUST_NAME, ");
        parser.addSQL("           A.USER_ID, ");
        parser.addSQL("           A.RSRV_STR16 PRODUCT_NAME, ");
        parser.addSQL("           0 PAY_FEE, ");
        parser.addSQL("           ROUND(to_number(RSRV_STR17) / 100.00,2) PAYED_FEE ");
        parser.addSQL("      FROM TF_F_USER_OTHER A, ");
        parser.addSQL("           (SELECT b.user_id,b.PARTITION_ID  ");
        parser.addSQL("              FROM tf_f_user b, tf_f_user_product p ");
        parser.addSQL("             WHERE not exists (select param_code ");
        parser.addSQL("                     FROM td_s_commpara c ");
        parser.addSQL("                     WHERE c.subsys_code = 'CGM' ");
        parser.addSQL("                       and c.param_attr = '1129' ");
        parser.addSQL("                       and p.product_id = c.param_code) ");
        parser.addSQL("               and b.USER_ID = p.USER_ID ");
        parser.addSQL("               and b.PARTITION_ID = p.PARTITION_ID ");
        parser.addSQL("               and b.EPARCHY_CODE = :EPARCHY_CODE ");
        parser.addSQL("               and b.CUST_ID = :CUST_ID) S, ");
        parser.addSQL("           (SELECT PARA_CODE1, PARA_CODE2, PARAM_NAME ");
        parser.addSQL("              FROM td_s_commpara t ");
        parser.addSQL("             WHERE subsys_code = 'CGM' ");
        parser.addSQL("               and param_attr = '1130' ");
        parser.addSQL("               and EPARCHY_CODE = :EPARCHY_CODE) T ");
        parser.addSQL("     WHERE A.RSRV_VALUE_CODE = 'PRES' ");
        parser.addSQL("       and A.USER_ID = S.USER_ID ");
        parser.addSQL("       and A.PARTITION_ID = S.PARTITION_ID ");
        parser.addSQL("       and A.Start_Date >= to_date(T.PARA_CODE1, 'yyyy-mm-dd')) FB ");
        parser.addSQL("GROUP BY GROUP_ID, CUST_NAME, USER_ID, PRODUCT_NAME ");
        parser.addSQL("ORDER BY PAYED_FEE ");

        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return dataset;
    }

    /**
     * 根据user_id/RSRV_VALUE_CODE 查询用户other信息，*****带表中所有列*****
     * 
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset getOtherInfoByCodeUserId(String userId, String rsrvValueCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID", cond);
        return dataset;
    }
    
    public static IDataset getOtherInfoByCodeUserIdTrade(String userId, String rsrvValueCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRVVALUECODE_ALL", cond);
        if(IDataUtil.isEmpty(dataset))
        {
        	dataset = Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_BY_USERID_RSRVVALUECODE", cond, Route.getJourDb(Route.CONN_CRM_CG));
        }
        return dataset;
    }

    public static IDataset getOtherInfoByIdPTag(String userId, String rsrvValueCode, String processTag) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("RSRV_VALUE_CODE", rsrvValueCode);
        inparam.put("USER_ID", userId);
        inparam.put("PROCESS_TAG", processTag);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_PROCESS_TAG", inparam);
    }

    /**
     * 查询用户证件是否已申请办理手机保障服务
     * 
     * @param RSRV_VALUE_CODE
     *            RSRV_STR1 RSRV_STR4
     * @return
     * @throws Exception
     */
    public static IDataset getOtherInfoByPspt(String rsrvValueCode, String rsrvStr1, String rsrvStr4) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("RSRV_STR1", rsrvStr1);
        param.put("RSRV_STR4", rsrvStr4);
        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_OTHERSTR2", param);
    }

    /**
     * @Description:根据用户ID与SERVICE_MODE获取TF_F_USER_OTHERSERV信息
     * @version: v1.0.0
     * @author: liutt2
     */
    public static IDataset getOtherServByUserIdServMode(String userId, String servMode) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("SERVICE_MODE", servMode);
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_USERID_ALL", inparam);

    }

    /**
     * @Function: getOutGrpInfos
     * @Description: 从集团库查询客户联系信息（根据CUST_ID）
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:09:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getOutGrpInfos(String group_id, String group_name, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("Group_name", group_name);
        param.put("Group_id", group_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT t.user_id OUT_GROUP_ID,t.RSRV_VALUE_CODE,t.RSRV_VALUE OUT_GROUP_NAME,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        parser.addSQL(" FROM tf_f_user_other t ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND t.RSRV_VALUE_CODE ='oGrp' ");
        parser.addSQL(" AND t.START_DATE<=SYSDATE ");
        parser.addSQL(" AND t.END_DATE>SYSDATE ");
        parser.addSQL(" AND t.RSRV_VALUE=:Group_name ");
        parser.addSQL(" AND t.user_id=to_number(:Group_id) ");
        parser.addSQL(" AND t.PARTITION_ID=mod(to_number(:Group_id),10000) ");
        parser.addSQL(" ORDER BY user_id DESC ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    public static IDataset getPlatsvcShl(String discntCode, String rsrvdate1, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("DISCNT_CODE", discntCode);
        inparam.put("RSRV_DATE1", rsrvdate1);
        return Dao.qryByCode("TF_F_USER_PLATSVC_SHL", "SEL_BY_DISANDDATE", inparam, pagination);
    }

    public static IDataset getSaleGooes(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_SALE_GOOES", param);

        return dataset;
    }

    /**
     * @Function: getSalePayInfo
     * @Description: 从集团库 获取集团营销预存信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:11:59 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getSalePayInfo(String cust_id, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchy_code);
        param.put("CUST_ID", cust_id);

        IDataset salePayInfo = Dao.qryByCode("TF_F_USER_OTHER", "SEL_SALEPAYINFO_BY_CUSTID", param, Route.CONN_CRM_CG);
        return salePayInfo;
    }

    /**
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     * @author wangww3
     */
    public static IDataset getTietbusyInfo(String userId, String rsrvValueCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_TIETBUSY_BY_NUMBER", cond);

        return dataset;
    }

    /** 查询用户的机场服务已使用免费次数 */
    // User_other表里面RSRV_VALUE_CODE为AREM的记录的rsrv_str1就是已使用服务数
    public static IDataset getUsedTimes(String user_id) throws Exception
    {

        StringBuilder sql = new StringBuilder();
        sql.append("select sum(nvl(rsrv_str1,0)) USEDTIMES from tf_f_user_other where user_id = '" + user_id + "' and partition_id = to_number(mod('" + user_id);
        sql.append("',10000)) and rsrv_value_code = 'AREM' and to_char(start_date,'yyyymmdd') > to_char(sysdate,'yyyy')");
        IDataset resultset = Dao.qryBySql(sql, new DataMap());
        return resultset;
    }

    /**
     * @Function: getUserMenBrand
     * @Description: 查询用户品牌信息（集团用户）
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:13:08 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserMenBrand(String user_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select t.*  ");
        parser.addSQL(" from TF_F_USER_OTHER t ");
        parser.addSQL(" where t.user_id =:USER_ID ");
        parser.addSQL(" and t.rsrv_value = 'GRP_PRS_BRAND' and t.end_date > sysdate ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset getUserOther(String userId, String rsrvValueCode) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("RSRV_VALUE_CODE", rsrvValueCode);
        inparam.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", inparam);
    }

    /**
     * 根据USER_ID、RSRV_VALUE_CODE和副号码查询TF_F_USER_OTHER数据(原X_GETMODE = 3) xiaoyx 参数不合理。删除该方法
     */
    /*
     * public static IDataset getUserOtherByUserRsrvValueCodeAndNum(IData params, Pagination page) throws Exception {
     * return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE_4", params, page); }
     */

    /**
     * 根据USER_ID查询影号主副卡资料
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserOtherAllByOneCardMulti(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_INS_DESTROY_OTHER", param);
    }

    public static IDataset getUserOtherByCrossRegservice(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", "KQFW");
        param.put("RSRV_VALUE", "0");
        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_CROSS_REGSERVICE", param);
    }

    /**
     * 根据instid/tradeId/userId/rsrvvaluecode查询信息
     * 
     * @param userId
     * @param instId
     * @param rsrvValueCode
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getUserOtherByInstAndTrade(String userId, String instId, String rsrvValueCode, String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_ID", instId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_INST_TRADE", param);
    }

    /**
     * 根据操作类型查询所有影号业务USER_OTHER信息
     * 
     * @param user_id
     * @param rsrv_value
     * @param open_type
     * @param rsrv_str4
     * @return
     * @throws Exception
     */
    public static IDataset getUserOtherByOpenTypeAll(String user_id, String rsrv_value, String open_type, String rsrv_str4) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE", rsrv_value);
        param.put("OPEN_TYPE", open_type);
        if (rsrv_str4 != null && !"".equals(rsrv_str4))
        {
            param.put("RSRV_STR4", rsrv_str4);
        }

        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_RSRV_VALUE_CODE_RSRVSTR4_BY_ALL", param);
    }

    public static IDataset getUserOtherByRsrvstr6(String userId, String rsrvStr6, String rsrvValueCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_STR6", rsrvStr6);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID_RSRV6", cond);
    }

    public static IDataset getUserOtherByRsrvstr7(String userId, String rsrvStr7, String rsrvValueCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_STR7", rsrvStr7);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID_RSRV7", cond);
    }

    public static IDataset getUserOtherByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", "KQFW");
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUE_CODE1", param);
    }

    /**
     * 根据user_id获取用户的其他资料
     * 
     * @param pd
     *            页面数据
     * @throws Exception
     */
    public static IDataset getUserOtherByUserID(String userId) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID_VALUECODE_1", inparam);
    }

    /**
     * @Function: getUserOtherByUseridRsrvcode
     * @Description: 根据当前时间sysdate，RSRV_VALUE_CODE,USER_ID查询tf_f_user_other表
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午6:22:12 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherByUseridRsrvcode(String user_id, String rsrv_value_code, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", param, pagination);
    }

    public static IDataset getUserOtherByUserIdStr1Str3(String userId, String rsrvValueCode, String rsrvStr1, String rsrvStr3) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("RSRV_STR1", rsrvStr1);
        param.put("RSRV_STR3", rsrvStr3);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR1STR3", param);
    }

    public static IDataset getUserOtherByUserIdStr1Str5(String userId, String rsrvValueCode, String rsrvStr1, String rsrvStr5) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("RSRV_STR1", rsrvStr1);
        param.put("RSRV_STR5", rsrvStr5);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR1STR5", param);
    }

    /**
     * 根据USER_ID、RSRV_VALUE_CODE、RSRV_VALUE查询TF_F_USER_OTHER数据
     * 
     * @param user_id
     * @param rsrv_value_code
     * @param rsrv_value
     * @return
     * @throws Exception
     */
    public static IDataset getUserOtherByUserRsrvValue(String user_id, String rsrv_value_code, String rsrv_value) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        param.put("RSRV_VALUE", rsrv_value);

        Long pValue = Long.parseLong(user_id) % 10000;
        String partition_id = pValue.toString();

        param.put("PARTITION_ID", partition_id);

        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_RSRV_VALUE_VALID", param);
    }

    /**
     * @Function: getUserOtherByUserRsrvValue
     * @Description: 根据USER_ID、RSRV_VALUE_CODE、RSRV_VALUE查询TF_F_USER_OTHER数据(原X_GETMODE = 10)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:16:37 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherByUserRsrvValue(String user_id, String rsrv_value_code, String rsrv_value, String rsrv_str4, String partition_id, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        param.put("RSRV_VALUE", rsrv_value);
        param.put("RSRV_STR4", rsrv_str4);
        param.put("PARTITION_ID", partition_id);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRV_VALUE_VALID", param, page);
    }

    /**
     * @Function: getUserOtherByUserRsrvValueCode
     * @Description:从集团库根据USER_ID、RSRV_VALUE_CODE查询TF_F_USER_OTHER数据(原X_GETMODE = 3)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午6:26:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherByUserRsrvValueCode(String user_id, String rsrv_value_code, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);

        IDataset ids = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", param);

        for (int i = 0, size = ids.size(); i < size; i++)
        {
            IData map = ids.getData(i);

            map.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(map.getString("RSRV_STR3")));
        }

        return ids;
    }

    /**
     * @Function: getUserOtherByUserRsrvValueCodeAndNum
     * @Description: 根据USER_ID、RSRV_VALUE_CODE和副号码查询TF_F_USER_OTHER数据(原X_GETMODE = 3) xiaoyx
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:18:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherByUserRsrvValueCodeAndNum(String user_id, String rsrv_value_code, String serial_number, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        param.put("SERIAL_NUMBER", serial_number);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE_4", param, page);
    }

    /**
     * @Function: getUserOtherByUserRsrvValueCodeByEc
     * @Description: 根据USER_ID、RSRV_VALUE_CODE查询TF_F_USER_OTHER数据(原X_GETMODE = 3)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午6:29:05 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherByUserRsrvValueCodeByEc(String user_id, String rsrv_value_code) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", param);
    }

    /**
     * @Function: getUserOtherByUserRsrvValueCodeByEc
     * @Description: 根据USER_ID、RSRV_VALUE_CODE查询TF_F_USER_OTHER数据(原X_GETMODE = 3)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午6:59:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherByUserRsrvValueCodeByEc(String user_id, String rsrv_value_code, String eparchyCode, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", param, page, eparchyCode);
    }

    /**
     * 根据USER_ID、RSRV_VALUE_CODE,INST_ID查询TF_F_USER_OTHER数据
     */
    public static IDataset getUserOtherByUserRsrvValueCodeRsrvstr2(IData params, String eparchyCode, Pagination page) throws Exception
    {
        // TODO code_code表里没有
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USRID_RECODE_RSVSTR2", params, page, eparchyCode);
    }

    /**
     * 根据userId/rsrvvaluecode查询信息
     * 
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserOtherCancel(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID_VALUECODE_CANCEl", param);
    }

    public static IDataset getUserOtherInfo(IData param) throws Exception
    {

        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_USER_OTHERINFO", param);
    }

    /**
     * 根据sql_ref,eparchy_code查询用户其他(TF_F_USER_OTHER)信息 参数不合理。删除该方法
     */
    public static IDataset getUserOtherInfo(IData inparams, Pagination page) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE_3", inparams, page);
    }

    /**
     * @param inparams
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IDataset getUserOtherInfo(String userId, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE_3", param);
    }

    /**
     * @Function: getUserOtherInfo
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:23:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherInfo(String user_id, String rsrv_value_code, String partition_id, String rsrv_value) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        param.put("PARTITION_ID", partition_id);
        param.put("RSRV_VALUE", rsrv_value);

        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRV_VALUE", param);

        if (dataset != null && dataset.size() > 0)
        {
            return dataset;
        }
        return null;
    }

    public static IDataset getUserOtherInfoByAll(String userId, String rsrvCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRVVALUECODE_ALL", param);

        return dataset;
    }

    public static IDataset getUserOtherInfoByAllUserId(IData inparams) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_ALL_BY_USERID", inparams);

        return dataset;
    }

    /**
     * 查询用户集团预存金额
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getUserOtherInfoByGroupExchange(IData inparams) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_GROUP_EXCHANGE", inparams, Route.CONN_CRM_CG);
        return dataset;
    }

    
    public static IDataset queryTransFee(String rsrvValueCode, String start_staff_id, String end_staff_id, String start_date, String end_date, String trans_type, String campus_id, Pagination pagination) throws Exception
    {
	    IData param = new DataMap();
	    param.put("RSRV_VALUE_CODE", rsrvValueCode);
	    param.put("START_STAFF_ID", start_staff_id);
	    param.put("END_STAFF_ID", end_staff_id);
	    param.put("START_DATE", start_date);
	    param.put("END_DATE", end_date);
	    param.put("TRANS_TYPE", trans_type);
	    param.put("CAMPUS_ID", campus_id);
	    return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_DATE_STAFFID_QRY", param, pagination);
     }
    
    /**
     * TODO SQL_REF为动态 根据sql_ref,eparchy_code查询用户其他(TF_F_USER_OTHER)信息 ims
     * 
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     * @author tianyt
     */
    public static IDataset getUserOtherInfoForGrp(IData inparams, Pagination page) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_USER_VALUECODE2", inparams, page);
    }


    /**
     * @Function: getUserOtherservByPK
     * @Description: 根据USER_ID、SERVICE_MODE和PROCESS_TAG查询TF_F_USER_OTHERSVC数据(原X_GETMODE = 0)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:25:38 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherservByPK(String user_id, String service_mode, String process_tag, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_MODE", service_mode);
        param.put("PROCESS_TAG", process_tag);

        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_PK", param, page);
    }

    /**
     * @Function: getUserOtherservByRSRVSTR1
     * @Description: 根据RSRV_STR1查询TF_F_USER_OTHERSVC数据(原DEAL_FLAG = 1)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:27:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherservByRSRVSTR1(String service_mode, String rsrv_str1, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("RSRV_STR1", rsrv_str1);
        param.put("SERVICE_MODE", service_mode);

        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_RSRV_STR1", param, page);
    }

    /**
     * @Function: getUserOtherservBySN
     * @Description: 根据SERIAL_NUMBER查询TF_F_USER_OTHERSVC数据(原DEAL_FLAG = 0)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:29:09 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherservBySN(String serial_number, String service_mode, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("SERVICE_MODE", service_mode);

        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_SERIAL_NUMBER", param, page);
    }

    /**
     * @Function: getUserOtherservBySNandRSRVSTR1
     * @Description: 根据SERIAL_NUMBER、RSRV_STR1查询TF_F_USER_OTHERSVC数据(原DEAL_FLAG = 2)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 下午2:30:25 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherservBySNandRSRVSTR1(String serial_number, String service_mode, String rsrv_str1, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("SERVICE_MODE", service_mode);
        param.put("RSRV_STR1", rsrv_str1);

        // CSAppEntity app = new CSAppEntity();
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_SN_RSRVSTR1", param, page);
    }

    /**
     * @Function: getUserOtherUserId
     * @Description:根据用户标识USER_ID查询用户其他信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:01:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserOtherUserId(String user_id, String rsrv_value_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID", param, pagination);
    }

    /**
     * 查询跨省用户信息（集团用户）
     * 
     * @author xiajj
     * @param params
     *            查询所需参数
     * @param pagination
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getUserOverProvinceInfo(IData params, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR1", params, pagination);
    }

    /**
     * @Function: getUserOverProvinceInfo
     * @Description: 查询跨省用户信息（集团用户）
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:02:25 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserOverProvinceInfo(String user_id, String rsrv_value_code, String rsrv_str1, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        param.put("RSRV_STR1", rsrv_str1);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR1", param, pagination);
    }
    public static IDataset getUserOthersInfoByUseridValuecodeSTR2(String user_id, String rsrv_value_code, String rsrv_str2) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        param.put("RSRV_STR2", rsrv_str2);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR3", param);
    }

    public static IDataset getUserOverProvinceInfoForCg(IData params) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR1", params, Route.CONN_CRM_CG);
    }

    /**
     * 查询发票重打业务
     * 
     * @param serialNumber
     * @param tradeEparchCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserRePrintTrade(String serialNumber, String tradeEparchCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_EPARCHY_CODE", tradeEparchCode);
        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SELT_REPRINTTRADEINFO", param);
    }

    public static IDataset getVipExchangeBySnUpdateTime(String userId, String serialNumber, String updateTime, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("UPDATE_TIME", updateTime);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_VIPEXCHANGE_BYPARAMS", inparam, pagination);
    }

    public static IDataset getVipInfo(IData params) throws Exception
    {

        params.put("RSRV_VALUE_CODE", "VIP_SAC_BX");

        IDataset userOtherSet = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID", params);

        return userOtherSet;
    }

    public static IDataset hasBDinfo(IData params) throws Exception
    {

        IDataset set = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", params);

        return set;
    }

    /**
     * 手机号与通行证号绑定
     * 
     * @param userId
     * @param rsrvValueCode
     * @param rsrvValue
     * @param oprNumb
     * @param remark
     * @return
     * @throws Exception
     */
    public static int insPassIDInfoByUserId(String userId, String rsrvValueCode, String rsrvValue, String oprNumb, String remark, String staffId, String departId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE", rsrvValueCode);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        cond.put("TRADE_ID", oprNumb);
        cond.put("START_DATE", SysDateMgr.getSysTime());
        cond.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        cond.put("REMARK", remark);
        cond.put("INST_ID", SeqMgr.getInstId());
        cond.put("UPDATE_STAFF_ID", staffId);
        cond.put("UPDATE_DEPART_ID", departId);
        cond.put("UPDATE_TIME", SysDateMgr.getSysTime());
        int result = Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "INS_USER_INFO", cond);

        return result;
    }

    public static int insUserOtherByInstId(String sysdate, String enddate, String rsrvValue, String userId, String rsrvValueCode, String staffId, String departId, String inModeCode, String instId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE", rsrvValue);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("RSRV_STR1", staffId); // 受理工号
        param.put("RSRV_STR2", departId);// 受理部门
        param.put("RSRV_STR3", sysdate);// 开始时间
        param.put("RSRV_STR4", enddate);// 结束时间
        param.put("RSRV_STR5", inModeCode);// 接入渠道
        param.put("START_DATE", sysdate);
        param.put("END_DATE", enddate);
        param.put("UPDATE_TIME", sysdate);
        param.put("UPDATE_STAFF_ID", staffId);
        param.put("UPDATE_DEPART_ID", departId);
        param.put("INST_ID", instId);

        return Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "INS_USEROTHER_BY_INSTID", param);
    }

    public static int insUserOtherForGGTH(String instId, String userId, String cardNo, String joinCause, String awardType, String awardClass, String eparchyCode, String serialNumber, String cardPasswd, String packageId, String awardClassName,
            String sysDate, String endDate, String staffId, String departId, String remark, String tag) throws Exception
    {

        IData param = new DataMap();
        param.put("INST_ID", instId);
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", "GGTH");
        param.put("RSRV_VALUE", cardNo);
        param.put("RSRV_STR1", joinCause);
        param.put("RSRV_STR2", awardType);
        param.put("RSRV_STR3", awardClass);
        param.put("RSRV_STR6", "0");
        param.put("RSRV_STR7", eparchyCode);
        param.put("RSRV_STR8", "EGGTH");
        param.put("RSRV_STR9", "0000");
        param.put("RSRV_STR10", serialNumber);
        param.put("RSRV_STR11", cardPasswd);
        param.put("RSRV_STR28", packageId);
        param.put("RSRV_STR23", "EGGTH");
        param.put("RSRV_STR17", awardClassName);
        param.put("RSRV_DATE1", sysDate);
        param.put("START_DATE", sysDate);
        param.put("END_DATE", endDate);
        param.put("STAFF_ID", staffId);
        param.put("DEPART_ID", departId);
        param.put("UPDATE_STAFF_ID", staffId);
        param.put("UPDATE_DEPART_ID", departId);
        param.put("UPDATE_TIME", sysDate);
        param.put("REMARK", remark);
        param.put("PROCESS_TAG", tag);

        return Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "INS_USER_OTHER_GGTH", param);
    }

    public static IDataset qryByStr1RmtCard(String userId, String rsrvValueCode, String rsrvStr1) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        cond.put("RSRV_STR1", rsrvStr1);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRVSTR1", cond);
    }

    /**
     * 集团绑定号码查询 根据 集团客户编码GROUP_ID 和 产品编码PRODUCT_ID 查询
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryECBindSnQry(IData data, Pagination pagination) throws Exception
    {

        String strSerialNumberB = data.getString("SERIAL_NUMBER_B", "");

        IData id = new DataMap();
        IDataset idsResultInfo = new DatasetList();

        String strBindUserId = "";
        String strBindActId = "";
        String strBindType = "";
        IData idBindAcctConsignData = null;

        // 查绑定号码
        if (!"".equals(strSerialNumberB))
        {
            // 查用户信息
            id.clear();
            id.put("SERIAL_NUMBER", strSerialNumberB);
            id.put("REMOVE_TAG", "0");
            id.put("NET_TYPE_CODE", "00");

            IDataset idsUserInfo = new DatasetList();

            // TODO idsUserInfo = UserInfoQry.getUserInfoBySn(id, CSBizBean.getTradeEparchyCode());

            if (idsUserInfo.size() == 0)// 无
            {
                return idsResultInfo;
            }

            IData idUser = idsUserInfo.getData(0);

            strBindUserId = idUser.getString("USER_ID", "");

            // 查询绑定用户信息
            // id.clear();
            // id.put("RSRV_VALUE_CODE", "A2SR");
            // id.put("RSRV_VALUE", strBindUserId);
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "不允许用sql_ref方式");

            IDataset idsBindUserInfo = getUserOtherInfo(id, null);

            if (idsBindUserInfo.size() == 0)// 无
            {
                return idsResultInfo;
            }

            IData idBindUser = idsBindUserInfo.getData(0);

            strBindActId = idBindUser.getString("USER_ID", "");

            // 绑定类型 1:个人转集团,null:绑定
            strBindType = idBindUser.getString("RSRV_STR2", "");

            if ("1".equals(strBindType))
            {
                strBindType = "个人转集团";
            }
            else if ("".equals(strBindType))
            {
                strBindType = "绑定号码";
            }
            else
            {
                strBindType = "";
            }
        }

        // 查绑定号码
        if (!"".equals(strSerialNumberB))
        {
            data.put("BIND_ACT_ID", strBindActId);
        }

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT ");
        parser.addSQL(" t.cust_id,t.GROUP_ID,t.CUST_NAME, ");
        parser.addSQL(" c.CUST_TYPE,c.CUST_STATE,c.PSPT_TYPE_CODE,c.PSPT_ID, ");
        parser.addSQL(" DECODE(C.CUST_STATE,'0','在网','1','潜在') CUST_STATE_NAME, ");
        parser.addSQL(" DECODE(C.CUST_TYPE,'0','个人客户','1','集团客户','2','家庭客户','3','团体客户' ) CUST_TYPE_NAME, ");
        parser.addSQL(" u.USER_ID,u.PRODUCT_ID,u.BRAND_CODE,u.SERIAL_NUMBER,u.IN_STAFF_ID,u.IN_DEPART_ID, ");
        parser.addSQL(" u.DEVELOP_STAFF_ID,u.DEVELOP_DEPART_ID, ");
        parser.addSQL(" TO_CHAR(u.IN_DATE,'YYYY-MM-DD HH:MM:SS') IN_DATE, TO_CHAR(u.OPEN_DATE,'YYYY-MM-DD HH:MM:SS') OPEN_DATE, ");
        parser.addSQL(" u.CITY_CODE,u.EPARCHY_CODE,u.USER_TYPE_CODE, u.USER_STATE_CODESET, ");
        parser.addSQL(" p.ACCT_ID, ");
        parser.addSQL(" a.PAY_NAME,a.PAY_MODE_CODE ");
        parser.addSQL(" from Tf_f_Cust_Group t, TF_F_CUSTOMER c, tf_f_user u, TF_A_PAYRELATION p, TF_F_ACCOUNT a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" AND t.GROUP_ID = :GROUP_ID ");
        parser.addSQL(" AND t.REMOVE_TAG ='0' ");
        parser.addSQL(" AND c.CUST_ID=t.CUST_ID ");
        parser.addSQL(" AND c.PARTITION_ID=MOD(t.CUST_ID,10000) ");
        parser.addSQL(" AND u.CUST_ID=t.CUST_ID ");
        parser.addSQL(" AND u.REMOVE_TAG='0' ");
        parser.addSQL(" AND u.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL(" AND u.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND p.user_id=u.USER_ID ");
        parser.addSQL(" AND p.partition_id=MOD(u.USER_ID,10000) ");
        parser.addSQL(" AND p.default_tag='1' ");
        parser.addSQL(" AND p.act_tag='1' ");
        parser.addSQL(" AND to_number(to_char(sysdate, 'yyyymmdd')) BETWEEN start_cycle_id AND end_cycle_id ");
        parser.addSQL(" AND a.ACCT_ID = :BIND_ACT_ID and a.PARTITION_ID = mod(:BIND_ACT_ID, 10000) ");
        parser.addSQL(" AND a.ACCT_ID=p.ACCT_ID ");
        parser.addSQL(" AND a.PARTITION_ID=MOD(p.ACCT_ID, 10000) ");

        idsResultInfo = Dao.qryByParse(parser, pagination);

        String strFlag = "";

        for (int i = 0; i < idsResultInfo.size(); i++)
        {
            IData idResult = idsResultInfo.getData(i);
            String strAcctId = "";

            // 查绑定号码
            if (!"".equals(strBindActId))
            {
                if ("".equals(strFlag))
                {
                    String strPayModeCode = idResult.getString("PAY_MODE_CODE");

                    // 托收，查托收表
                    if ("1".equals(strPayModeCode))
                    {
                        IDataset idsAcctConsignInfo = AcctConsignInfoQry.getConsignInfoByAcctId(strBindActId);

                        if (idsAcctConsignInfo.size() != 0)// 有
                        {
                            idBindAcctConsignData = idsAcctConsignInfo.getData(0);
                        }
                    }
                }

                strFlag = "selend";

                if (idBindAcctConsignData != null)
                {
                    idResult.put("BANK_ACCT_NO", idBindAcctConsignData.getString("BANK_ACCT_NO", ""));
                    idResult.put("SUPER_BANK_CODE", idBindAcctConsignData.getString("SUPER_BANK_CODE", ""));
                    idResult.put("BANK_CODE", idBindAcctConsignData.getString("BANK_CODE", ""));
                    idResult.put("CONTRACT_ID", idBindAcctConsignData.getString("CONTRACT_ID", ""));
                }
            }
            else
            {
                strAcctId = idResult.getString("ACCT_ID");

                String strUserId = idResult.getString("USER_ID");

                String strPayModeCode = idResult.getString("PAY_MODE_CODE");

                // 托收，查托收表
                if ("1".equals(strPayModeCode))
                {
                    IDataset idsAcctConsignInfo = AcctConsignInfoQry.getConsignInfoByAcctId(strAcctId);

                    if (idsAcctConsignInfo.size() != 0)// 有
                    {
                        IData idAcctConsignData = idsAcctConsignInfo.getData(0);

                        idResult.put("BANK_ACCT_NO", idAcctConsignData.getString("BANK_ACCT_NO", ""));
                        idResult.put("SUPER_BANK_CODE", idAcctConsignData.getString("SUPER_BANK_CODE", ""));
                        idResult.put("BANK_CODE", idAcctConsignData.getString("BANK_CODE", ""));
                        idResult.put("CONTRACT_ID", idAcctConsignData.getString("CONTRACT_ID", ""));
                    }

                }
            }

            // 查绑定号码
            if (!"".equals(strSerialNumberB))
            {
                idResult.put("SERIAL_NUMBER_B", strSerialNumberB);

                // 绑定类型
                idResult.put("BIND_TYPE", strBindType);
            }
            else
            {
                id.clear();
                id.put("USER_ID", strAcctId);
                id.put("RSRV_VALUE_CODE", "A2SR");

                IDataset idsOtherInfo = getUserOtherByUserRsrvValueCode(strAcctId, "A2SR", null);

                if (idsOtherInfo.size() != 0)// 有
                {
                    IData idOtherData = idsOtherInfo.getData(0);

                    strBindUserId = idOtherData.getString("RSRV_VALUE");

                    // 绑定类型 1:个人转集团,null:绑定
                    strBindType = idOtherData.getString("RSRV_STR2", "");

                    // 查询绑定用户信息
                    IDataset idsBindUserInfo = IDataUtil.idToIds(UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(strBindUserId));

                    if (idsBindUserInfo.size() != 0)// 有
                    {
                        IData idBindUserData = idsBindUserInfo.getData(0);

                        idResult.put("SERIAL_NUMBER_B", idBindUserData.getString("SERIAL_NUMBER", ""));

                        if ("1".equals(strBindType))
                        {
                            strBindType = "个人转集团";
                        }
                        else if ("".equals(strBindType))
                        {
                            strBindType = "绑定号码";
                        }
                        else
                        {
                            strBindType = "";
                        }

                        // 绑定类型
                        idResult.put("BIND_TYPE", strBindType);
                    }
                }
            }

        } // 结束循环

        return idsResultInfo;
    }

    /**
     * @Description:GPRS行业应用查询
     * @param param
     * @param queryTypeStr
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryGPRSInfo(IData param, String GPRSQueryTypeStr, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT TO_CHAR(A.USER_ID) USER_ID ,B.CUST_ID,B.CUST_NAME, B.GROUP_ID, ");
        parser.addSQL(" '' PAY_NUMBER ,");
        parser.addSQL(" A.SERIAL_NUMBER, A.RSRV_STR3 TERM_NUM, TO_NUMBER(A.RSRV_STR2)/100 MONTHFEE,");
        parser.addSQL("       TO_NUMBER(A.RSRV_STR4)/100 OVERFEE, A.RSRV_STR5 APNNO,A.RSRV_STR6 BDAPN,A.RSRV_STR7 ALXAPN, A.RSRV_STR8 SERVICE_NUMBER,A.RSRV_STR9 HWAPN,");
        parser.addSQL("		  A.PRODUCT_ID PRODUCT_ID, TO_CHAR(A.OPEN_DATE,'YYYY-MM-DD HH:MM:SS') OPEN_DATE ");
        parser.addSQL("FROM TF_F_USER A , TF_F_CUST_GROUP B ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("    AND A.CUST_ID = B.CUST_ID  ");
        parser.addSQL("    AND A.BRAND_CODE='APNG' ");
        parser.addSQL("    AND A.REMOVE_TAG='0' ");
        parser.addSQL("    AND B.GROUP_ID = :GROUP_ID ");// 根据集团客户编码查询(EC编码)
        parser.addSQL("    AND A.SERIAL_NUMBER = :SERIAL_NUMBER "); // 根据集团编码查询(服务号码)

        if (GPRSQueryTypeStr.equals("2")) // 根据成员用户服务号码查询，根据集团成员serial_number反查集团group_id,然后再根据group_id查询
        {
            parser.addSQL("AND EXISTS ");
            parser.addSQL(" (SELECT 1 ");
            parser.addSQL("    FROM TF_F_RELATION_UU uu ");
            parser.addSQL("   WHERE SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
            parser.addSQL("   AND uu.END_DATE > last_day(trunc(sysdate))+1-1/24/3600 ");
            parser.addSQL("   AND a.USER_ID=uu.USER_ID_A ");
            parser.addSQL("   AND a.PARTITION_ID=mod(uu.USER_ID_A,10000) ) ");
        }

        IDataset ids = Dao.qryByParse(parser, pagination);
        ;

        String strUserId = "";

        for (int i = 0; i < ids.size(); i++)
        {
            IData id = ids.getData(i);

            strUserId = id.getString("USER_ID");

            param.clear();
            param.put("USER_ID", strUserId);

            SQLParser parser1 = new SQLParser(param);

            parser1.addSQL(" SELECT bu.SERIAL_NUMBER PAY_NUMBER");
            parser1.addSQL(" FROM TF_A_PAYRELATION p, TF_F_USER_OTHER bo, tf_f_user bu ");
            parser1.addSQL(" WHERE p.user_id = :USER_ID ");
            parser1.addSQL(" AND p.partition_id = MOD(:USER_ID, 10000) ");
            parser1.addSQL(" AND p.default_tag = '1' ");
            parser1.addSQL(" AND p.act_tag = '1' ");
            parser1.addSQL(" AND to_number(to_char(SYSDATE, 'yyyymmdd')) BETWEEN start_cycle_id AND end_cycle_id ");
            parser1.addSQL(" AND bo.PARTITION_ID = MOD(p.ACCT_ID, 10000) ");
            parser1.addSQL(" AND bo.USER_ID = p.ACCT_ID ");
            parser1.addSQL(" AND bu.USER_ID = bo.RSRV_VALUE ");
            parser1.addSQL(" AND bu.PARTITION_ID = MOD(bo.RSRV_VALUE, 10000) ");
            parser1.addSQL(" AND bo.rsrv_value_code = 'A2SR' ");
            parser1.addSQL(" AND SYSDATE BETWEEN bo.start_date + 0 AND bo.end_date + 0 ");

            IDataset idsOther = Dao.qryByParse(parser1);

            if (idsOther != null && idsOther.size() > 0)
            {
                IData idOther = idsOther.getData(0);
                id.put("PAY_NUMBER", idOther.getString("PAY_NUMBER", ""));
            }
        }

        return ids;
    }

    /**
     * 查询集团ADC\MAS用户级别信息
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryGroupAdcMasLevelInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT USER_ID, ");
        parser.addSQL("        RSRV_STR1 AS GROUP_ID, "); // GROUP_ID 集团客户编码
        parser.addSQL("        RSRV_STR2 AS CUST_NAME, "); // 集团客户名称
        parser.addSQL("        RSRV_STR3 AS PRODUCT_ID, "); // 产品ID
        parser.addSQL("        RSRV_STR4 AS EC_SERIAL_NUMBER, "); // 服务号码
        parser.addSQL("        RSRV_STR5 AS BIZ_NAME, "); // 业务名称
        parser.addSQL("        RSRV_STR6 AS ACCESS_NUMBER, "); // 接入代码
        parser.addSQL("        RSRV_STR7 AS LEVEL1, "); // 级别
        parser.addSQL("        RSRV_STR8 AS BIZ_STATUS, "); // 业务状态
        parser.addSQL("        RSRV_STR20, "); // 被投诉次数
        parser.addSQL("        RSRV_STR21, "); // 平均收入
        parser.addSQL("        RSRV_STR22, "); // 每月发送流量
        parser.addSQL("        RSRV_STR23, "); // 连续3个月被举报数量
        parser.addSQL("        RSRV_STR24, "); // 连续3个月被举报率
        parser.addSQL("        RSRV_STR25 "); // 发送流速
        parser.addSQL("        RSRV_STR26 "); // 月发送量
        parser.addSQL("   FROM TF_F_USER_OTHER T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.RSRV_VALUE_CODE = 'LEVEL' ");
        parser.addSQL(" AND T.RSRV_STR1 = :GROUP_ID ");
        // parser.addSQL(" AND T.RSRV_STR2 LIKE :GROUP_ID ");
        // parser.addSQL(" AND T.RSRV_STR4 = :EC_SERIAL_NUMBER ");
        // parser.addSQL(" AND T.RSRV_STR6 = :ACCESS_NUMBER ");
        // parser.addSQL(" AND T.RSRV_STR7 = :LEVEL ");
        // parser.addSQL(" AND T.RSRV_STR8 = :BIZ_STATUS	");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 根据USER_ID、VALUE_CODE查询OTHER表有效资料
     * 
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     *             wangjx 2013-8-6
     */
    public static IDataset qryOtherInfoByIdCode(String userId, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_ID_CODE", param);
    }

    /**
     * 根据USER_ID、VALUE_CODE查询OTHER表有效资料【分页】
     * 
     * @param userId
     * @param rsrvValueCode
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryOtherInfoByIdCodePag(String userId, String rsrvValueCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_ID_CODE", param, pagination);
    }

    /**
     * 查询集团用户一年内的预存金额
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qrySumRsrvByUserId(IData inParam) throws Exception
    {
        SQLParser parser = new SQLParser(inParam);

        parser.addSQL("SELECT SUM(RSRV_NUM1) FEE");
        parser.addSQL("  FROM  TF_F_USER_OTHER t");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("  AND t.RSRV_VALUE_CODE = 'GRSF'");
        parser.addSQL("  AND t.START_DATE > SYSDATE - 365");
        parser.addSQL("  AND t.END_DATE > SYSDATE");
        parser.addSQL("  AND t.USER_ID = :USER_ID");
        parser.addSQL("  AND t.PARTITION_ID = mod(:USER_ID, 10000)");
        parser.addSQL("  GROUP BY t.USER_ID");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 根据USER_ID,RSRV_STR2,RSRV_VALUE_CODE,查询用户信息
     */
    public static IDataset qryUserGgthInfo(String userId, String award_type, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_STR2", award_type);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_GGTH_BY_USERID", param);
    }

    public static IDataset qryUserGgthInfo(String userId, String valueCardNo, String awardType, String joinCause, String awardClass) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", "GGTH");
        param.put("RSRV_VALUE", valueCardNo);
        param.put("RSRV_STR2", awardType);// 兑奖类型
        param.put("RSRV_STR1", joinCause);// 参与原因
        param.put("RSRV_STR3", awardClass);// 奖品档次
        // param.put("RSRV_STR13","");
        param.put("RSRV_STR23", "EGGTH");
        return Dao.qryByCode("TF_F_USER_OTHER", "SelUsrOtherByValueCode", param);
    }

    /**
     * @Function: qryUserOthInfoForGrp
     * @Description:查询集团用户 userother表信息 。
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午3:07:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IData qryUserOthInfoForGrp(String rsrv_value_code, String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_VALUE_CODE", rsrv_value_code);
        param.put("USER_ID", user_id);

        IDataset result = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USRID_RSRV_VALUE", param, Route.CONN_CRM_CG);

        if (result.size() > 0)
        {
            return result.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }

    /**
     * 作用：查询 userother表信息 。
     * 
     * @param params
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IData qryUsrOthInfo(IData params, String eparchyCode) throws Exception
    {

        // getVisit().setRouteEparchyCode( eparchyCode);
        IDataset result = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_USRID_USRIDA", params, eparchyCode);
        if (result.size() > 0)
        {
            return result.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }

    /**
     * 查询隐号信息
     * 
     * @param userIdB
     * @param relationTypeCode
     * @param roleCodeA
     * @param roleCodeB
     * @return
     * @throws Exception
     */
    public static IDataset queryAccessoroalBySn(String userIdB, String relationTypeCode, String roleCodeA, String roleCodeB) throws Exception
    {
        IData param = new DataMap();
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        param.put("USER_ID_B", userIdB);
        param.put("ROLE_CODE_A", roleCodeA);
        param.put("ROLE_CODE_B", roleCodeB);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_ACCESSOROAL_BY_SN", param);
    }

    /**
     * 生日畅打登记查询
     * 
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset queryBirthdayRegInfo(String userId, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BIRTHDAYREGINFO_BY_USERID", param);
    }

    public static IDataset queryBussPresents(String userId, String rsrvValueCode, String rsrvStr6) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_STR6", rsrvStr6);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);

        IDataset result = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_BUSSPRESENT1", cond);

        return result;
    }

    public static IDataset queryByOtherStr3(String userId, String rsrvValueCode, String rsrvStr1) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_STR1", rsrvStr1);
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR3", param);

        return dataset;
    }

    public static IDataset queryByOtherStr3(String userId, String rsrvValueCode, String rsrvStr1, String rsrvStr2) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_STR1", rsrvStr1);
        param.put("RSRV_STR2", rsrvStr2);
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR3", param);

        return dataset;
    }

    public static IDataset queryByOtherStr3(String userId, String rsrvValueCode, String rsrvValue, String rsrvStr2, String rsrvStr6) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_STR6", rsrvStr6);
        param.put("RSRV_STR2", rsrvStr2);
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("RSRV_VALUE", rsrvValue);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR3", param);

        return dataset;
    }

    public static IDataset queryByTradeId(String userId, String rsrvValueCode, String preTradeId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("TRADE_ID", preTradeId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);

        IDataset result = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_USERTRADEID", cond);

        return result;
    }

    public static IDataset queryByTradeId(String userId, String rsrvValueCode, String preTradeId, String preRsRv) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("TRADE_ID", preTradeId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        cond.put("RSRV_STR3", preRsRv);

        IDataset result = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_USERTRADEID", cond);

        return result;
    }

    public static IDataset queryByTradeIDRS(String userId, String rsrvValueCode, String preTradeId, String rsrvStr1, String rsrvStr2, String rsrvStr3) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        cond.put("TRADE_ID", preTradeId);
        cond.put("RSRV_STR1", rsrvStr1);
        cond.put("RSRV_STR2", rsrvStr2);
        cond.put("RSRV_STR3", rsrvStr3);

        IDataset result = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_USERTRADEIDRS", cond);

        return result;
    }

    public static IDataset queryGaveUserBussPresent(String userId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("RSRV_VALUE_CODE", "LPZS");
        cond.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        cond.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_GAVEPREBUSSPRESENT", cond);
    }

    public static IDataset queryGGCardByUserId(String userId, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_GGCARD_BY_USERID", param);
    }

    /**
     * @Function: queryGGCardByUserId
     * @Description: 用户的刮刮卡信息查询
     * @param: @param userId
     * @param: @param rsrvValueCode
     * @param: @param eparchyCode
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 3:14:52 PM Jul 30, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Jul 30, 2013 longtian3 v1.0.0 TODO:
     */
    public static IDataset queryGGCardByUserId(String userId, String rsrvValueCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("JOIN_CAUSE", "1019"); // 刮刮卡参与原因
        param.put("TRADE_TYPE", "1017"); // 刮刮卡兑奖类型
        param.put("AWARD_CLASS", "1037"); // 刮刮卡奖品等级
        param.put("PRESENT_NAME", "1018"); // 刮刮卡奖品名称
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_RSRV_GGCARD", param);
    }

    /**
     * 根据userId查询集团用户是否支持高级付费标记
     * 
     * @param userid
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpPayTagByUserID(String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", "GRPI");
        param.put("RSRV_STR1", "01");
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR1", param, pagination, Route.CONN_CRM_CG);
    }

    public static IDataset queryInfoByApproveId(IData param) throws Exception
    {

        IDataset infos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_SALEACTIVE_BY_APPROVEID", param);

        return infos;
    }

    public static IDataset queryInfoByExecId(IData param) throws Exception
    {

        IDataset infos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_SALEACTIVE_BY_EXECID", param);

        return infos;
    }

    public static IDataset queryInfoByImportId(IData param) throws Exception
    {

        IDataset infos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_SALEACTIVE_BY_IMPORTID", param);

        return infos;
    }

    public static IData queryLcuParamsByTradeId(IData param) throws Exception
    {

        IDataset infos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_SALEACTIVE_BY_TRADEID", param);

        return infos == null ? null : infos.getData(0);
    }

    /**
     * 查询用户在活动周期内是否已办理置换业务
     * 
     * @param params
     *            USER_ID RSRV_VALUE_CODE START_DATE END_DATE 查询所需参数
     * @return IDataset
     * @throws Exception
     */
    public static IDataset queryOtherByDate(String userId, String rsrvValueCode, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_VALUECODE_STARTDATE", param);
    }

    /**
     * 查询串号是否已申请用来做三免三优登记
     * 
     * @param pd
     * @param params
     *            查询所需参数
     * @return IDataset
     * @throws Exception
     *             comment：业务受理前规则校验中调用
     */
    public static IDataset queryOtherByImei(String rsrvValueCode, String rsrvValue) throws Exception
    {
        IData params = new DataMap();
        params.put("RSRV_VALUE_CODE", rsrvValueCode);
        params.put("RSRV_VALUE", rsrvValue);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRVVLAUE", params);
    }

    /**
     * 查询用户是否已申请办理手机保障服务
     * 
     * @param params
     *            USER_ID RSRV_VALUE_CODE 查询所需参数
     * @return IDataset
     * @throws Exception
     */
    public static IDataset queryOtherByUserId(String userId, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE_NEW2", param);
    }

    public static IDataset queryOtherByValueAllDb(String rsrvValueCode, String rsrvValue) throws Exception
    {
        IData params = new DataMap();
        params.put("RSRV_VALUE_CODE", rsrvValueCode);
        params.put("RSRV_VALUE", rsrvValue);
        return Dao.qryByCodeAllCrm("TF_F_USER_OTHER", "SEL_BY_RSRVVLAUE", params, true);
    }

    /**
     * 查询OTHER数据【SEL_BY_OTHERSTR3】
     * 
     * @param userId
     * @param rsrvValueCode
     * @param rsrvStr2
     * @return
     * @throws Exception
     */
    public static IDataset queryOtherStr3ByStr2(String userId, String rsrvValueCode, String rsrvStr2) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_STR2", rsrvStr2);
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_OTHERSTR3", param);

        return dataset;
    }

    public static IDataset queryPwdCheckErrInfos(String serialNumber, String startDate, String endDate, String rsrvValueCode, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PARA_CODE1", serialNumber);
        inparam.put("PARA_CODE2", startDate);
        inparam.put("PARA_CODE3", endDate);
        inparam.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_MMEE_BYSN", inparam, pagination);
    }

    /**
     * 定制查询
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySaleActiveInfos(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT /*+ index(U IDX_TF_F_USER_OTHER_VALUECODE)*/U.RSRV_STR5 TRADE_ID,U.RSRV_STR1 SERIAL_NUMBER_A,");
        parser.addSQL(" U.RSRV_STR3 SERIAL_NUMBER_B,");
        parser.addSQL(" P.PRODUCT_NAME PRODUCT_NAME,");
        parser.addSQL(" PK.PACKAGE_NAME PACKAGE_NAME,");
        parser.addSQL(" DECODE(U.RSRV_TAG1,");
        parser.addSQL("        '1',");
        parser.addSQL("        '审核通过',");
        parser.addSQL("        '2',");
        parser.addSQL("        '审核拒绝',");
        parser.addSQL("        '3',");
        parser.addSQL("        '转移成功',");
        parser.addSQL("        '未审核') IS_CHECKED,");
        parser.addSQL(" U.RSRV_STR4 IMPORT_STAFF_ID,");
        parser.addSQL(" TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') IMPORT_DATE,");
        parser.addSQL(" U.RSRV_STR6 CHECKED_STAFF_ID,");
        parser.addSQL(" U.RSRV_STR7 EXEC_STAFF_ID,");
        parser.addSQL(" TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') CHECKED_DATE,");
        parser.addSQL(" TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') EXEC_DATE,");
        parser.addSQL(" u.rsrv_str15 CHECKED_INFO,");
        parser.addSQL(" u.rsrv_str16 TRANSFER_INFO");
        parser.addSQL(" FROM TF_F_USER_OTHER U, TD_B_PRODUCT P, TD_B_PACKAGE PK");
        parser.addSQL(" WHERE U.RSRV_VALUE_CODE = 'YXHDQY'");
        parser.addSQL(" AND U.RSRV_NUM1 = P.PRODUCT_ID");
        parser.addSQL(" AND U.RSRV_NUM2 = PK.PACKAGE_ID");
        parser.addSQL(" AND 1=1");
        parser.addSQL(" AND u.rsrv_str1=:SERIAL_NUMBER_A");
        parser.addSQL(" AND U.RSRV_STR3=:SERIAL_NUMBER_B");
        parser.addSQL(" AND (U.RSRV_TAG1=:IS_CHECKED)");
        parser.addSQL(" AND U.RSRV_STR4 =:IMPORT_STAFF_ID");
        parser.addSQL(" AND u.rsrv_str6 =:CHECKED_STAFF_ID");
        parser.addSQL(" AND U.RSRV_NUM2 = TO_NUMBER(:PACKAGE_ID)");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset querySvcInfoByUserIdAndSvcId(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_STR9", serviceId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT * FROM TF_F_USER_OTHER WHERE USER_ID = :USER_ID AND PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("AND RSRV_STR9 = :RSRV_STR9 AND SYSDATE BETWEEN START_DATE AND END_DATE");

        return Dao.qryByParse(parser);
    }

    public static IDataset querySvcInfoByUserIdAndSvcIdPf(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_STR9", serviceId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT * FROM TF_F_USER_OTHER WHERE USER_ID = :USER_ID AND PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("AND RSRV_STR9 = :RSRV_STR9 AND SYSDATE < END_DATE AND START_DATE < END_DATE");

        return Dao.qryByParse(parser);
    }

    /**
     * 根据USER_ID,RSRV_VALUE_CODE 查询
     * 
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset querytietInfo(String userId, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_TIETBUSY_INFO", param);
    }

    /**
     * 根据userId查询所有有效的
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAllValidInfos(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_ALL_VALID_BY_USERID", param);

    }

    public static IDataset queryUserBookingMeeting(String userId, String confId, String operCode) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        inParam.put("RSRV_VALUE_CODE", "V2CP");
        inParam.put("RSRV_STR10", confId);
        inParam.put("RSRV_STR11", operCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_BOOKINGMEETING", inParam);
    }

    public static IDataset queryUserBussPresent(String userId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("RSRV_VALUE_CODE", "LPZS");
        cond.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        cond.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_PREBUSSPRESENT", cond);
    }

    public static IDataset queryUserInfoByRsrvValue(String userId, String rsrvVvalue, String rsrvStr4) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("RSRV_VALUE_CODE", rsrvVvalue);
        inparam.put("RSRV_STR4", rsrvStr4);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE_4", inparam);
    }

    /**
     * 根据发票号码获取发票信息
     * 
     * @param USER_ID
     * @param invoiceNo
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-17
     */
    public static IDataset queryUserInvoiceInfos(String USER_ID, String invoiceNo) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", USER_ID);
        cond.put("SERVICE_MODE", "FG");
        cond.put("SERIAL_NUMBER", invoiceNo.trim());
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_INVOICENO_USERID", cond);
    }

    /**
     * @author luoz
     * @date 2013-08-12
     * @description 根据rsrvStr30查询userother表。
     * @param userId
     * @param rsrvValueCode
     * @param rsrvStr30
     * @return
     * @throws Exception
     */
    public static IDataset queryUserOtherBtRsrvStr30(String userId, String rsrvValueCode, String rsrvStr30) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        inParam.put("RSRV_VALUE_CODE", rsrvValueCode);
        inParam.put("RSRV_STR30", rsrvStr30);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRV_STR30", inParam);
    }

    public static IDataset queryUserOtherByRsrvStr4(String userId, String rsrvValueCode, String rsrvStr4) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        cond.put("RSRV_STR4", rsrvStr4);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_RSRVSTR4", cond);
    }

    /**
     * 根据user_id, rsrv_value_code查询用户其它信息
     * 
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset queryUserOtherByUserValueCode(String userId, String rsrvValueCode) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        inParam.put("RSRV_VALUE_CODE", rsrvValueCode);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_VALUECODE_NEWLIKE", inParam);
    }

    /**
     * 查询用户是否已经同步过资料
     * 
     * @author zhuyu
     * @return brandCode
     * @throws Exception
     */
    public static IDataset queryUserOtherInfobyStr9orStr10(String rsrvValueCode, String rsrvValue, String rsrvStr9, String rsrvStr10) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("RSRV_VALUE", rsrvValue);
        param.put("RSRV_STR9", rsrvStr9);
        param.put("RSRV_STR10", rsrvStr10);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select partition_id,user_id,rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7 from TF_F_USER_OTHER a ");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and a.rsrv_value_code = :RSRV_VALUE_CODE");
        parser.addSQL(" and a.rsrv_value = :RSRV_VALUE");
        parser.addSQL(" and a.rsrv_str9 = :RSRV_STR9");
        parser.addSQL(" and a.rsrv_str10 = :RSRV_STR10");
        parser.addSQL(" and a.end_date>sysdate");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryUserOtherInfos(String USER_ID, String RSRV_VALUE_CODE, String RSRV_VALUE) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", USER_ID);
        cond.put("RSRV_VALUE_CODE", RSRV_VALUE_CODE);
        cond.put("RSRV_VALUE", RSRV_VALUE);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUE_CODE", cond);
    }

    /**
     * 定制查询
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryVipInfos(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT /*+ index(U IDX_TF_F_USER_OTHER_VALUECODE)*/");
        parser.addSQL(" US.SERIAL_NUMBER SERIAL_NUMBER,");
        parser.addSQL(" U.RSRV_STR1 CUST_NAME,");
        parser.addSQL(" U.RSRV_STR2 DEPST_ID,");
        parser.addSQL(" U.RSRV_STR3 ADDR_ID,");
        parser.addSQL(" DECODE(U.RSRV_STR4,'0','营运交通工具意外伤害险','家庭财产火灾爆炸保险') XZ,");

        parser.addSQL(" U.RSRV_STR8 SXR_NAME,");
        parser.addSQL(" U.RSRV_STR9 SXR_DEPST_ID,");
        parser.addSQL(" U.RSRV_STR10 SXR_ADDR,");
        parser.addSQL(" U.UPDATE_STAFF_ID UPDATE_STAFF_ID,");
        parser.addSQL(" U.UPDATE_DEPART_ID UPDATE_DEPART_ID,");
        parser.addSQL(" TO_CHAR(U.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,");
        parser.addSQL(" U.RSRV_STR25 RSRV_STR25,");
        parser.addSQL(" U.RSRV_STR29 RSRV_STR29,");
        parser.addSQL(" U.RSRV_STR30 RSRV_STR30,");
        parser.addSQL(" TO_CHAR(U.RSRV_DATE10,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE10,");
        parser.addSQL(" TO_CHAR(U.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,");
        parser.addSQL(" TO_CHAR(U.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,");

        parser.addSQL(" U.RSRV_STR6 BX_ID,");
        parser.addSQL(" US.CITY_CODE CITY_CODE,");
        parser.addSQL(" DECODE(U.RSRV_TAG1,'1','是','否') RSRV_TAG1,");
        parser.addSQL(" DECODE(U.RSRV_TAG2,'1','是','否') RSRV_TAG2");
        parser.addSQL(" FROM  TF_F_USER US, TF_F_USER_OTHER U");
        parser.addSQL(" WHERE U.RSRV_VALUE_CODE = 'VIP_SAC_BX'");
        parser.addSQL(" AND U.USER_ID = US.USER_ID");
        parser.addSQL(" AND U.PARTITION_ID = US.PARTITION_ID");
        if ("0".equals(param.getString("RSRV_TAG2")))
        {
            parser.addSQL(" AND U.RSRV_TAG2 IS NULL");
        }
        else if ("1".equals(param.getString("RSRV_TAG2")))
        {
            parser.addSQL(" AND U.RSRV_TAG2 ='1'");
        }
        parser.addSQL(" AND U.START_DATE BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') AND");
        parser.addSQL(" TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')");

        parser.addSQL(" AND U.END_DATE > SYSDATE");
        parser.addSQL(" AND US.CITY_CODE IN");
        parser.addSQL(" (SELECT A.AREA_CODE");
        parser.addSQL(" FROM TD_M_AREA A");
        parser.addSQL(" START WITH A.AREA_CODE = :AREA_CODE");
        parser.addSQL(" CONNECT BY PRIOR A.AREA_CODE = A.PARENT_AREA_CODE)");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @Function: queryVPMN
     * @Description: VPMN短号，包月费
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:03:37 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryVPMN(String SERIAL_NUMBER, Pagination pagination) throws Exception
    {

        IData data = new DataMap();

        data.clear();
        data.put("SERIAL_NUMBER", SERIAL_NUMBER);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT t.USER_ID_A, t.USER_ID_B, t.SERIAL_NUMBER_A ");
        parser.addSQL(" FROM tf_f_relation_uu t ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" AND serial_number_b = :SERIAL_NUMBER ");
        parser.addSQL(" AND t.RELATION_TYPE_CODE IN ('20') ");
        parser.addSQL(" And t.End_Date > last_day(trunc(sysdate))+1-1/24/3600 ");

        IData idsRet = new DataMap();

        IDataset idsUUInfo = Dao.qryByParse(parser);

        String strUserIdB = "";
        String strUserIdA = "";

        IDataset idsResult = new DatasetList();

        if (idsUUInfo == null || idsUUInfo.size() == 0)// 无
        {
            idsRet.clear();
            idsRet.put("SERIAL_NUMBER_A", "");
            idsRet.put("FEE_MON", "");
            idsRet.put("SHORT_CODE", "");
            idsRet.put("P_LIMIT", "");
            idsRet.put("PAYITEM_CODE", "");
            idsRet.put("CONTRACT_NO", "");

            idsResult.add(idsRet);

            return idsResult;
        }

        IData idUU = idsUUInfo.getData(0);

        strUserIdA = idUU.getString("USER_ID_A");
        strUserIdB = idUU.getString("USER_ID_B");

        idsRet.clear();
        idsRet.put("SERIAL_NUMBER_A", idUU.getString("SERIAL_NUMBER_A"));

        // 包月费
        parser = new SQLParser(data);

        data.clear();
        data.put("USER_ID_B", strUserIdB);

        parser.addSQL(" SELECT A.ATTR_VALUE / 100 FEE_MON_SHORT");
        parser.addSQL(" FROM tf_f_user_attr a ");
        parser.addSQL(" WHERE a.attr_code = 'FEE_MON_SHORT' ");
        parser.addSQL(" AND A.INST_TYPE = 'P' ");
        parser.addSQL(" AND A.USER_ID = :USER_ID_B ");
        parser.addSQL(" AND a.PARTITION_ID = MOD(:USER_ID_B, 10000) ");
        parser.addSQL(" AND SYSDATE BETWEEN a.START_DATE AND a.END_DATE ");

        IDataset idsFEE_MON_SHORT = Dao.qryByParse(parser);

        if (idsFEE_MON_SHORT == null || idsFEE_MON_SHORT.size() == 0)// 无
        {
            idsRet.put("FEE_MON", "");
        }
        else
        {
            idsRet.put("FEE_MON", idsFEE_MON_SHORT.getData(0).getString("FEE_MON_SHORT"));
        }

        // 短号
        parser = new SQLParser(data);

        data.clear();
        data.put("USER_ID_B", strUserIdB);

        parser.addSQL(" SELECT A.ATTR_VALUE SHORT_CODE ");
        parser.addSQL(" FROM tf_f_user_attr a ");
        parser.addSQL(" WHERE a.attr_code = 'SHORT_CODE' ");
        parser.addSQL(" AND A.INST_TYPE = 'P' ");
        parser.addSQL(" AND A.USER_ID = :USER_ID_B ");
        parser.addSQL(" AND a.PARTITION_ID = MOD(:USER_ID_B, 10000) ");
        parser.addSQL(" AND SYSDATE BETWEEN a.START_DATE AND a.END_DATE ");

        IDataset idsShort_code = Dao.qryByParse(parser);

        if (idsShort_code == null || idsShort_code.size() == 0)// 无
        {
            idsRet.put("SHORT_CODE", "");
        }
        else
        {
            idsRet.put("SHORT_CODE", idsShort_code.getData(0).getString("SHORT_CODE"));
        }

        // 短号
        parser = new SQLParser(data);

        data.clear();
        data.put("USER_ID_A", strUserIdA);
        data.put("USER_ID_B", strUserIdB);

        parser.addSQL(" SELECT p.LIMIT / 100 P_LIMIT , p.PAYITEM_CODE, act.CONTRACT_NO ");
        parser.addSQL(" FROM tf_a_payrelation p,TF_F_ACCOUNT act ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND p.User_Id = :USER_ID_B ");
        parser.addSQL(" AND p.PARTITION_ID = MOD(:USER_ID_B, 10000) ");
        parser.addSQL(" AND p.RSRV_STR1 = :USER_ID_A ");
        parser.addSQL(" AND p.default_tag = '0' ");
        parser.addSQL(" AND p.ACT_TAG = '1' ");
        parser.addSQL(" AND TO_NUMBER(TO_CHAR(SYSDATE, 'YYYYMMDD')) BETWEEN START_CYCLE_ID AND END_CYCLE_ID ");
        parser.addSQL(" AND act.ACCT_ID=p.ACCT_ID ");
        parser.addSQL(" AND act.PARTITION_ID=MOD(p.ACCT_ID,10000) ");

        IDataset idsPayRelat = Dao.qryByParse(parser);

        if (idsPayRelat == null || idsPayRelat.size() == 0)// 无
        {
            idsRet.put("P_LIMIT", "");
            idsRet.put("PAYITEM_CODE", "");
            idsRet.put("CONTRACT_NO", "");
        }
        else
        {
            idsRet.put("P_LIMIT", idsPayRelat.getData(0).getString("P_LIMIT"));
            idsRet.put("PAYITEM_CODE", idsPayRelat.getData(0).getString("PAYITEM_CODE"));
            idsRet.put("CONTRACT_NO", idsPayRelat.getData(0).getString("CONTRACT_NO"));
        }

        idsResult.add(idsRet);
        return idsResult;
    }

    /**
     * @Function: queryVPMNGroupMebInfos
     * @Description: VPMN成员业务历史查询
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:04:36 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryVPMNGroupMebInfos(String START_DATE, String END_DATE, String SERIAL_NUMBER, Pagination pagination) throws Exception
    {

        if (null != START_DATE && !START_DATE.trim().equals(""))
        {
            START_DATE += SysDateMgr.getFirstTime00000();
        }

        if (null != END_DATE && !END_DATE.trim().equals(""))
        {
            END_DATE += SysDateMgr.getEndTime235959();
        }

        IData data = new DataMap();
        data.put("START_DATE", START_DATE);
        data.put("END_DATE", END_DATE);
        data.put("SERIAL_NUMBER", SERIAL_NUMBER);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT a.CUST_ID_B, ");
        parser.addSQL(" a.SERIAL_NUMBER, ");
        parser.addSQL(" a.trade_id, ");
        parser.addSQL(" a.USER_ID, ");
        parser.addSQL(" a.cust_name, ");
        parser.addSQL(" DECODE(a.TRADE_TYPE_CODE, ");
        parser.addSQL(" 2964, ");
        parser.addSQL(" '集团成员新增', ");
        parser.addSQL(" 2974, ");
        parser.addSQL(" '集团成员新增', ");
        parser.addSQL(" 2965, ");
        parser.addSQL(" '成员产品变更', ");
        parser.addSQL(" 2975, ");
        parser.addSQL(" '成员产品变更', ");
        parser.addSQL(" 2967, ");
        parser.addSQL(" '集团成员注销', ");
        parser.addSQL(" 2977, ");
        parser.addSQL(" '集团成员注销') TRADE_TYPE, ");
        parser.addSQL(" g.GROUP_ID, ");
        parser.addSQL(" g.CUST_NAME group_name, ");
        parser.addSQL(" decode(c.scp_code, '00', '虚拟集团', '智能网集团') group_type, ");
        parser.addSQL(" DECODE(c.scp_code, ");
        parser.addSQL(" '00', ");
        parser.addSQL(" '虚拟SCP', ");
        parser.addSQL(" '01', ");
        parser.addSQL(" '华为SCP', ");
        parser.addSQL(" '02', ");
        parser.addSQL(" '东信SCP', ");
        parser.addSQL(" '无') scp_code, ");
        parser.addSQL(" (SELECT tr.ATTR_VALUE ");
        parser.addSQL(" FROM TF_B_TRADE_ATTR tr ");
        parser.addSQL(" WHERE tr.trade_id = a.trade_id ");
        parser.addSQL(" AND tr.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND tr.ATTR_CODE = 'SHORT_CODE' ");
        parser.addSQL(" AND ROWNUM < 2) short_code, ");
        parser.addSQL(" (SELECT a.CONTRACT_NO ");
        parser.addSQL(" FROM TF_B_TRADE_PAYRELATION p, TF_F_ACCOUNT a ");
        parser.addSQL(" WHERE p.trade_id = a.trade_id ");
        parser.addSQL(" AND p.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND p.RSRV_STR1 = a.USER_ID_B ");
        parser.addSQL(" AND a.ACCT_ID = p.ACCT_ID ");
        parser.addSQL(" AND a.PARTITION_ID = MOD(p.ACCT_ID, 10000) ");
        parser.addSQL(" AND ROWNUM < 2) rsrv_str10, ");
        parser.addSQL(" (SELECT tr.ATTR_VALUE / 100 ");
        parser.addSQL(" FROM TF_B_TRADE_ATTR tr ");
        parser.addSQL(" WHERE tr.trade_id = a.trade_id ");
        parser.addSQL(" AND tr.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND tr.ATTR_CODE = 'FEE_MON_SHORT' ");
        parser.addSQL(" AND ROWNUM < 2) rsrv_num1, ");
        parser.addSQL(" (SELECT p.PAYITEM_CODE ");
        parser.addSQL(" FROM TF_B_TRADE_PAYRELATION p ");
        parser.addSQL(" WHERE p.trade_id = a.trade_id ");
        parser.addSQL(" AND p.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND ROWNUM < 2) PAYITEM_CODE, ");
        parser.addSQL(" (SELECT p.LIMIT / 100 ");
        parser.addSQL(" FROM TF_B_TRADE_PAYRELATION p ");
        parser.addSQL(" WHERE p.trade_id = a.trade_id ");
        parser.addSQL(" AND p.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND ROWNUM < 2) MON_FEE_LIMIT, ");
        parser.addSQL(" a.TRADE_STAFF_ID, ");
        parser.addSQL(" TO_CHAR(a.accept_date, 'YYYY-MM-DD HH24:MI:SS') accept_date, ");
        parser.addSQL(" TO_CHAR(a.finish_date, 'YYYY-MM-DD HH24:MI:SS') finish_date, ");
        parser.addSQL(" TO_CHAR(a.exec_time, 'YYYY-MM-DD HH24:MI:SS') exec_time ");
        parser.addSQL(" FROM tf_b_trade A, tf_f_user_vpn c, tf_f_cust_group g ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND a.TRADE_TYPE_CODE IN (2964, 2965, 2967, 2974, 2975, 2977) ");
        parser.addSQL(" AND g.CUST_ID = a.CUST_ID_B ");
        parser.addSQL(" AND c.USER_ID = a.USER_ID_B ");
        parser.addSQL(" AND c.PARTITION_ID = MOD(a.USER_ID_B, 10000) ");
        parser.addSQL(" AND c.REMOVE_TAG = '0' ");
        parser.addSQL(" AND a.ACCEPT_DATE BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') AND TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL(" AND A.SERIAL_NUMBER = :SERIAL_NUMBER ");

        parser.addSQL("UNION ALL ");

        parser.addSQL(" SELECT a.CUST_ID_B, ");
        parser.addSQL(" a.SERIAL_NUMBER, ");
        parser.addSQL(" a.trade_id, ");
        parser.addSQL(" a.USER_ID, ");
        parser.addSQL(" a.cust_name, ");
        parser.addSQL(" DECODE(a.TRADE_TYPE_CODE, ");
        parser.addSQL(" 2964, ");
        parser.addSQL(" '集团成员新增', ");
        parser.addSQL(" 2974, ");
        parser.addSQL(" '集团成员新增', ");
        parser.addSQL(" 2965, ");
        parser.addSQL(" '成员产品变更', ");
        parser.addSQL(" 2975, ");
        parser.addSQL(" '成员产品变更', ");
        parser.addSQL(" 2967, ");
        parser.addSQL(" '集团成员注销', ");
        parser.addSQL(" 2977, ");
        parser.addSQL(" '集团成员注销') TRADE_TYPE, ");
        parser.addSQL(" g.GROUP_ID, ");
        parser.addSQL(" g.CUST_NAME group_name, ");
        parser.addSQL(" decode(c.scp_code, '00', '虚拟集团', '智能网集团') group_type, ");
        parser.addSQL(" DECODE(c.scp_code, ");
        parser.addSQL(" '00', ");
        parser.addSQL(" '虚拟SCP', ");
        parser.addSQL(" '01', ");
        parser.addSQL(" '华为SCP', ");
        parser.addSQL(" '02', ");
        parser.addSQL(" '东信SCP', ");
        parser.addSQL(" '无') scp_code, ");
        parser.addSQL(" (SELECT tr.ATTR_VALUE ");
        parser.addSQL(" FROM TF_B_TRADE_ATTR tr ");
        parser.addSQL(" WHERE tr.trade_id = a.trade_id ");
        parser.addSQL(" AND tr.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND tr.ATTR_CODE = 'SHORT_CODE' ");
        parser.addSQL(" AND ROWNUM < 2) short_code, ");
        parser.addSQL(" (SELECT a.CONTRACT_NO ");
        parser.addSQL(" FROM TF_B_TRADE_PAYRELATION p, TF_F_ACCOUNT a ");
        parser.addSQL(" WHERE p.trade_id = a.trade_id ");
        parser.addSQL(" AND p.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND p.RSRV_STR1 = a.USER_ID_B ");
        parser.addSQL(" AND a.ACCT_ID = p.ACCT_ID ");
        parser.addSQL(" AND a.PARTITION_ID = MOD(p.ACCT_ID, 10000) ");
        parser.addSQL(" AND ROWNUM < 2) rsrv_str10, ");
        parser.addSQL(" (SELECT tr.ATTR_VALUE / 100 ");
        parser.addSQL(" FROM TF_B_TRADE_ATTR tr ");
        parser.addSQL(" WHERE tr.trade_id = a.trade_id ");
        parser.addSQL(" AND tr.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND tr.ATTR_CODE = 'FEE_MON_SHORT' ");
        parser.addSQL(" AND ROWNUM < 2) rsrv_num1, ");
        parser.addSQL(" (SELECT p.PAYITEM_CODE ");
        parser.addSQL(" FROM TF_B_TRADE_PAYRELATION p ");
        parser.addSQL(" WHERE p.trade_id = a.trade_id ");
        parser.addSQL(" AND p.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND ROWNUM < 2) PAYITEM_CODE, ");
        parser.addSQL(" (SELECT p.LIMIT / 100 ");
        parser.addSQL(" FROM TF_B_TRADE_PAYRELATION p ");
        parser.addSQL(" WHERE p.trade_id = a.trade_id ");
        parser.addSQL(" AND p.ACCEPT_MONTH = a.ACCEPT_MONTH ");
        parser.addSQL(" AND ROWNUM < 2) MON_FEE_LIMIT, ");
        parser.addSQL(" a.TRADE_STAFF_ID, ");
        parser.addSQL(" TO_CHAR(a.accept_date, 'YYYY-MM-DD HH24:MI:SS') accept_date, ");
        parser.addSQL(" TO_CHAR(a.finish_date, 'YYYY-MM-DD HH24:MI:SS') finish_date, ");
        parser.addSQL(" TO_CHAR(a.exec_time, 'YYYY-MM-DD HH24:MI:SS') exec_time ");
        parser.addSQL(" FROM tf_bh_trade A, tf_f_user_vpn c, tf_f_cust_group g ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND a.TRADE_TYPE_CODE IN (2964, 2965, 2967, 2974, 2975, 2977) ");
        parser.addSQL(" AND g.CUST_ID = a.CUST_ID_B ");
        parser.addSQL(" AND c.USER_ID = a.USER_ID_B ");
        parser.addSQL(" AND c.PARTITION_ID = MOD(a.USER_ID_B, 10000) ");
        parser.addSQL(" AND c.REMOVE_TAG = '0' ");
        parser.addSQL(" AND a.ACCEPT_DATE BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') AND TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL(" AND A.SERIAL_NUMBER = :SERIAL_NUMBER ");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @Function: queryVPMNPayItemInfos
     * @Description: VPMN成员帐目
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:05:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryVPMNPayItemInfos(String PAYITEM_CODE, Pagination pagination) throws Exception
    {

        IDataset idsResult = new DatasetList();
        IData data = new DataMap();

        if ("".equals(PAYITEM_CODE))
        {
            return idsResult;
        }

        data.clear();
        data.put("ITEM_ID", PAYITEM_CODE);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT t1.NOTE_ITEM_CODE, t1.NOTE_ITEM ");
        parser.addSQL(" FROM td_b_noteitem T1 ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND t1.TEMPLET_CODE IN (50000000) ");
        parser.addSQL(" AND t1.PRINT_LEVEL = '1' ");
        parser.addSQL(" AND t1.NOTE_ITEM_CODE IN ");
        parser.addSQL(" (SELECT DISTINCT t.PARENT_ITEM_CODE ");
        parser.addSQL(" FROM td_b_noteitem T, TD_B_COMPITEM c ");
        parser.addSQL(" WHERE c.ITEM_ID = :ITEM_ID ");
        parser.addSQL(" AND t.TEMPLET_CODE IN (50000000) ");
        parser.addSQL(" AND t.PRINT_LEVEL = '2' ");
        parser.addSQL(" AND c.SUB_ITEM_ID = t.NOTE_ITEM_CODE) ");
        parser.addSQL(" ORDER BY t1.NOTE_ITEM_CODE ASC ");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据发票号码获取userID不是查询号码的发票信息
     * 
     * @param USER_ID
     * @param invoiceNo
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-17
     */
    public static IDataset queryWZUserInvoiceInfos(String USER_ID, String invoiceNo) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", USER_ID);
        cond.put("SERVICE_MODE", "FG");
        cond.put("SERIAL_NUMBER", invoiceNo.trim());
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_INVOICENO_NOUSERID", cond);
    }

    /**
     * 用管中心通行证绑定passid
     * 
     * @param userId
     * @param rsrvValueCode
     * @param rsrvValue
     * @return
     * @throws Exception
     */
    public static int updPassIDInfoByUserId(String userId, String rsrvValueCode, String passId, String staffId, String departId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE", passId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        cond.put("UPDATE_TIME", SysDateMgr.getSysTime());
        cond.put("UPDATE_STAFF_ID", staffId);
        cond.put("UPDATE_DEPART_ID", departId);
        int result = Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_USEROTHER_CUR_BY_PK", cond);

        return result;
    }

    public static int updUserOtherByInstId(String endDate, String updateTime, String staffId, String departId, String inModeCode, String userId, String rsrvValueCode, String startDate, String instId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("END_DATE", endDate);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("START_DATE", startDate);
        param.put("UPDATE_TIME", updateTime);
        param.put("UPDATE_STAFF_ID", staffId);
        param.put("UPDATE_DEPART_ID", departId);
        param.put("RSRV_STR6", inModeCode);// 修改渠道
        param.put("INST_ID", instId);

        return Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_USEROTHER_BY_INSTID", param);
    }

    public static IDataset vipBXInfo(IData params) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID", params);

        if (dataset != null && dataset.size() > 0)
        {
            return dataset;
        }
        return null;
    }
    public static IDataset queryOtherInfoByUserIdDate(String userId, String timePoint) throws Exception
    {
        IData cond = new DataMap();
        cond.put("TIME_POINT", timePoint);
        cond.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_UID_DATE", cond);
    }
/**
     * 获取租赁光猫信息
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset getModemRentByCodeUserId(String userId, String rsrvValueCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_MODEM_RENT_BY_USERID", cond);
        return dataset;
    }
    
    /**
     * 获取租赁和赠送光猫信息
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset getModemRentByCodeUserId2(String userId, String rsrvValueCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RSRV_VALUE_CODE", rsrvValueCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_MODEM_RENT_BY_USERID2", cond);
        return dataset;
    }
    

    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
     * chenxy3 2016-08-26 
     * */
    public static IDataset queryUserOtherByRsrv4(IData input) throws Exception{
        IData data = new DataMap();
        data.put("USER_ID", input.getString("USER_ID",""));
        data.put("RSRV_VALUE", input.getString("RSRV_VALUE",""));
        data.put("ORDER_ID", input.getString("ORDER_ID","")); 
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_VALUE_CODE_RSRV4", data);
    }
    
    
    public static IDataset getUserOtherByUserIdStr5(String userId, String rsrvValueCode, String rsrvStr5) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);
        param.put("RSRV_STR5", rsrvStr5);

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR5", param);
    }
	
	/**
     * 查询用户的和商务标识
     * @param rsrvValueCode
     * @param rsrvValue
     * @param rsrvStr9
     * @param rsrvStr10
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public static IDataset queryUserOtherInfoForPg(String userId, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT PARTITION_ID,");
        parser.addSQL("       USER_ID,");
        parser.addSQL("       RSRV_VALUE_CODE,");
        parser.addSQL("       RSRV_VALUE,");
        parser.addSQL("       RSRV_NUM1,");
        parser.addSQL("       RSRV_NUM2,");
        parser.addSQL("       RSRV_NUM3,");
        parser.addSQL("       RSRV_NUM4,");
        parser.addSQL("       RSRV_NUM5,");
        parser.addSQL("       RSRV_STR1,");
        parser.addSQL("       RSRV_STR2,");
        parser.addSQL("       RSRV_STR3,");
        parser.addSQL("       RSRV_STR4,");
        parser.addSQL("       RSRV_STR5,");
        parser.addSQL("       PROCESS_TAG,");
        parser.addSQL("       STAFF_ID,");
        parser.addSQL("       DEPART_ID,");
        parser.addSQL("       TRADE_ID,");
        parser.addSQL("       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
        parser.addSQL("       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,");
        parser.addSQL("       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
        parser.addSQL("       UPDATE_STAFF_ID,");
        parser.addSQL("       UPDATE_DEPART_ID,");
        parser.addSQL("       REMARK,");
        parser.addSQL("       INST_ID");
        parser.addSQL("  FROM TF_F_USER_OTHER A");
        parser.addSQL(" WHERE A.USER_ID = :USER_ID");
        parser.addSQL("   AND A.RSRV_VALUE_CODE = :RSRV_VALUE_CODE");
        parser.addSQL("   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE");

        return Dao.qryByParse(parser);
    }
    
   
    
	/**
     * 海洋通 报停 报开修改状态
     * @param userid
     * @throws Exception
     * add by xuzh5 2018-7-5 4:58:10
     */
    public static void updateOtherStatus(String userid,String status) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userid);
        data.put("RSRV_STR5", status);
        SQLParser parser = new SQLParser(data);
        parser.addSQL("UPDATE  TF_F_USER_OTHER A SET A.RSRV_STR5=:RSRV_STR5 ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND A.USER_ID=:USER_ID   ");
        parser.addSQL(" AND A.RSRV_VALUE_CODE='HYT' ");
        Dao.executeUpdates(parser);
    }
	
	/**
     * 查询“集团客户信息系统”产品信息
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-6-20
     */
    public static IDataset queryUserOtherInfoForGcci(String userId, String rsrvValueCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT PARTITION_ID,");
        parser.addSQL("       USER_ID,");
        parser.addSQL("       RSRV_VALUE_CODE,");
        parser.addSQL("       RSRV_VALUE,");
        parser.addSQL("       RSRV_NUM1,");
        parser.addSQL("       RSRV_NUM2,");
        parser.addSQL("       RSRV_NUM3,");
        parser.addSQL("       RSRV_NUM4,");
        parser.addSQL("       RSRV_NUM5,");
        parser.addSQL("       RSRV_STR1,");
        parser.addSQL("       RSRV_STR2,");
        parser.addSQL("       RSRV_STR3,");
        parser.addSQL("       RSRV_STR4,");
        parser.addSQL("       RSRV_STR5,");
        parser.addSQL("       PROCESS_TAG,");
        parser.addSQL("       STAFF_ID,");
        parser.addSQL("       DEPART_ID,");
        parser.addSQL("       TRADE_ID,");
        parser.addSQL("       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
        parser.addSQL("       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,");
        parser.addSQL("       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
        parser.addSQL("       UPDATE_STAFF_ID,");
        parser.addSQL("       UPDATE_DEPART_ID,");
        parser.addSQL("       REMARK,");
        parser.addSQL("       INST_ID");
        parser.addSQL("  FROM TF_F_USER_OTHER A");
        parser.addSQL(" WHERE A.USER_ID = :USER_ID");
        parser.addSQL("   AND A.RSRV_VALUE_CODE = :RSRV_VALUE_CODE");
        parser.addSQL("   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE");

        return Dao.qryByParse(parser);
    }

    /**
     * REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
     * 根据调用第三方的接口的返回值修改other表数据
     * @param data
     * @throws Exception
     * @author chenzg
     * @date 2018-6-25
     */
    public static void updIdcOrderDataInfo(IData param) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE TF_B_TRADE_OTHER A ");
        sql.append(" SET A.REMARK = :REMARK");
        sql.append(" 	,A.RSRV_STR10 = :RSRV_STR10");
        sql.append(" WHERE A.TRADE_ID = :TRADE_ID");
        sql.append(" AND A.USER_ID = :USER_ID");
        sql.append(" AND A.RSRV_VALUE_CODE = :RSRV_VALUE_CODE");
        sql.append(" AND A.INST_ID = :INST_ID");
        
        Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
	
	 //查下是否高价值小区用户
    public static Boolean IsHightDevice(IData params) throws Exception{
		
    	 Boolean flag = false;
		 
		 StringBuilder sql = new StringBuilder(300);
		 //写死默认值ACTIVITY_CODE为10001，代表一个营销活动
		 //params.put("ACTIVITY_CODE", "10001");
		 //0表示有效的
		 params.put("REMOVE_TAG", "0");
		 sql.append("select * from  TF_F_GARDEN_DEVICE_INFO where  DEVICE_CODE =:DEVICE_ID AND REMOVE_TAG =:REMOVE_TAG"); 
	     IDataset infos =Dao.qryBySql(sql, params);
		 if(infos != null&&infos.size()>0)
		 {
			 flag=true;
		 }
	   
		return flag;
	}
	
    /**
     * REQ201810090021智能组网终端出库接口
	 * @param param
	 * @throws Exception
	 * @author yanghb6
     * @date 2018-11-20
     */
    public static int insertMergeWideTerm(IData param)throws Exception{
    	return Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "INS_MERGE_WIDE_TERM", param);
    }
	
	public static IDataset getAllNoPhoneUserOther(String rsrv_value_code) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_VALUE_CODE", rsrv_value_code);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  t.RSRV_STR1, t.RSRV_STR2, t.RSRV_STR28, t.user_id, t.RSRV_VALUE_CODE, t.RSRV_VALUE , t.START_DATE, to_char(t.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        parser.addSQL(" FROM tf_f_user_other t ,tf_f_user u");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND t.RSRV_VALUE_CODE =:RSRV_VALUE_CODE ");
        parser.addSQL(" AND t.USER_ID =u.USER_ID ");
        parser.addSQL(" AND u.RSRV_TAG1 = 'N' ");
        parser.addSQL(" AND t.START_DATE<=SYSDATE ");
        parser.addSQL(" AND t.END_DATE>SYSDATE ");
        return Dao.qryByParse(parser);
    }
	
	 
    public static void insertOnemsg(IData dataset) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "INS_USER_INFO", dataset);
    }

    public static IDataset queryLineTradeAttr(String productNo) throws Exception {
        IDataset datalineAttrs = new DatasetList();
        IDataset datalineTrades = TradeOtherInfoQry.queryDatalineAttrTrade(productNo, "PRODUCTNO");
        if(datalineTrades != null && datalineTrades.size() > 0) {
            datalineAttrs = TradeOtherInfoQry.queryDatalineAttr(datalineTrades.getData(0).getString("TRADE_ID", ""), null);
        }
        return datalineAttrs;
    }

   /**
     * 根据USER_ID,RSRV_VALUE_CODE.RSRV_VALUE,RSRV_STR1,RSRV_STR2查询TF_F_USER_OTHER生效的记录
     *  RSRV_VALUE 可为null,RSRV_STR1可为null,RSRV_STR2可为null, null表示查所有
     *  sysdate between start_date and end_date
     *  REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param userId
     * @param rsrvCode
     * @param rightType
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserOtherInfoVaild(String userId, String rsrvCode,String rsrvValue,String rightType,String discntCode) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvCode);
        param.put("RSRV_VALUE", rsrvValue);
        param.put("RSRV_STR1", rightType);
        param.put("RSRV_STR2", discntCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_BENEFIT", param);
        return dataset;
    }
    /**
     * 根据USER_ID,RSRV_VALUE_CODE.RSRV_VALUE,RSRV_STR1,RSRV_STR2查询TF_F_USER_OTHER生效的记录
     *  RSRV_VALUE 可为null,RSRV_STR1可为null,RSRV_STR2可为null, null表示查所有
     *  sysdate between start_date and end_date
     *  REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param userId
     * @param rsrvCode
     * @param rightType
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserOtherInfoByBenefit(String userId, String rsrvCode,String rsrvValue,String rightType,String discntCode) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvCode);
        param.put("RSRV_VALUE", rsrvValue);
        param.put("RSRV_STR1", rightType);
        param.put("RSRV_STR2", discntCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_BENEFIT", param);
        DatasetList result = new DatasetList();
        if(IDataUtil.isNotEmpty(dataset)){
            for (int i = 0; i < dataset.size(); i++) {
                //数据未处理前,RSRV_DATE1为空,截止时间END_DATE
                //数据处理后,RSRV_DATE1必不为空,且为截止时间,END_DATE为当年年底
                String endDate = dataset.getData(i).getString("RSRV_DATE1",dataset.getData(i).getString("END_DATE"));
                if(StringUtils.isNotBlank(endDate)){
                    if(SysDateMgr.compareTo(SysDateMgr.getSysTime(),endDate)<=0){
                        result.add( dataset.getData(i));
                    }
                }
            }
        }
        return result;
    }
    /**
     * 根据USER_ID,RSRV_VALUE_CODE,RSRV_STR1,RSRV_STR2查询TF_F_USER_OTHER当年的变更的记录(弃用)
     *  END_DATE<SYSDATE
     *  EXTRACT(YEAR FROM END_DATE) = EXTRACT(YEAR FROM SYSDATE)
     *  REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param userId
     * @param rsrvCode
     * @param rightType
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserOtherInfoHisByBenefit(String userId, String rsrvCode,String rightType,String discntCode) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvCode);
        param.put("RSRV_STR1", rightType);
        param.put("RSRV_STR2", discntCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_HIS_BY_BENEFIT", param);
        return dataset;
    }

    /**
     * 根据REL_ID找对应USER_ID
     * RSRV_STR1可为null,RSRV_STR2可为null,
     * sysdate between start_date and end_date
     * REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param rsrvCode
     * @param rightType
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserIdByRelID( String rsrvCode,String rsrvValue,String rightType,String discntCode) throws Exception{
        IData param = new DataMap();
        param.put("RSRV_VALUE_CODE", rsrvCode);
        param.put("RSRV_VALUE", rsrvValue);
        param.put("RSRV_STR1", rightType);
        param.put("RSRV_STR2", discntCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USERID_BY_RELID", param);
        DatasetList result = new DatasetList();
        if(IDataUtil.isNotEmpty(dataset)){
            for (int i = 0; i < dataset.size(); i++) {
                //数据未处理前,RSRV_DATE1为空,截止时间END_DATE
                //数据处理后,RSRV_DATE1必不为空,且为截止时间,END_DATE为当年年底
                String endDate = dataset.getData(i).getString("RSRV_DATE1",dataset.getData(i).getString("END_DATE"));
                if(StringUtils.isNotBlank(endDate)){
                    if(SysDateMgr.compareTo(SysDateMgr.getSysTime(),endDate)<=0){
                        result.add( dataset.getData(i));
                    }
                }
            }
        }
        return result;
    }
    /**
     * 根据user_id查询当年的权益使用记录
     * RSRV_VALUE 可为null,RSRV_STR1可为null,RSRV_STR2可为null,RSRV_STR3可为null   null标识查所有
     *   AND EXTRACT(YEAR FROM START_DATE) = EXTRACT(YEAR FROM SYSDATE)
     *   AND (:START_DATE is null or START_DATE >= :START_DATE)
     *  REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param userId
     * @param rightType
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getRightUseRecordByUserId( String userId,String rightType,String discntCode,String relId,String startDate) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", PersonConst.BENEFIT_RIGHT_USE_RECORD);
        param.put("RSRV_STR1", rightType);
        param.put("RSRV_STR2", discntCode);
        param.put("RSRV_STR3", relId);
        param.put("START_DATE", startDate);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_RIGHT_USE_RECORD", param);
        return dataset;
    }

    /**
     * 根据user_id,start_date,end_date查询用户的权益使用记录
     * RSRV_VALUE 可为null,RSRV_STR1可为null,RSRV_STR2可为null,RSRV_STR3可为null   null标识查所有
     *   AND START_DATE benween :START_DATE and :END_DATE
     *  REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param userId
     * @param rightType
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getRightUseRecordByUserIdAndDate( String userId,String rightType,String discntCode,String relId,String startDate,String endDate) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", PersonConst.BENEFIT_RIGHT_USE_RECORD);
        param.put("RSRV_STR1", rightType);
        param.put("RSRV_STR2", discntCode);
        param.put("RSRV_STR3", relId);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_RIGHT_USE_RECORD_BY_DATE", param);
        return dataset;
    }


    /**
     * 根据USER_ID,RSRV_VALUE_CODE.RSRV_VALUE,RSRV_STR1,RSRV_STR2查询TF_F_USER_OTHER当年最后一条记录(不管是否生效)
     *  RSRV_VALUE 可为null,RSRV_STR1可为null,RSRV_STR2可为null, null表示查所有
     *  AND EXTRACT(YEAR FROM START_DATE) = EXTRACT(YEAR FROM SYSDATE)
     *  order by START_DATE desc
     *  REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param userId
     * @param rsrvCode
     * @param rightType
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getLastCuryear(String userId, String rsrvCode,String rsrvValue,String rightType,String discntCode) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", rsrvCode);
        param.put("RSRV_VALUE", rsrvValue);
        param.put("RSRV_STR1", rightType);
        param.put("RSRV_STR2", discntCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_LAST_CURYEAR", param);
        return dataset;
    }

    /**
     * 根据inst_id,user_id 查询user_other记录
     * REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param instId
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getByInstIdAndUserId(String instId, String userId) throws Exception{
        IData param = new DataMap();
        param.put("INST_ID", instId);
        param.put("USER_ID", userId);
        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_INSTID_AND_USERID", param);
    }


    /**
     * 根据RSRV_STR4查询user_other记录
     * REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getByTingJDTradeID(String tradeId) throws Exception{
        IData param = new DataMap();
        param.put("RSRV_VALUE_CODE", PersonConst.BENEFIT_RIGHT_USE_RECORD);
        param.put("RSRV_STR4", tradeId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_TINGJD_TRADEID", param);
        return dataset;
    }

    public static IDataset getMoveAreaTagBySourceTagElementID(String userId, String serialNum, String sourceTag, String elementId) throws Exception{
        IData param = new DataMap();
        param.put("RSRV_VALUE_CODE", "MOVE_AREA_TAG");
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE", serialNum);
        param.put("RSRV_STR1", sourceTag);
        param.put("RSRV_STR2", elementId);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_MOVE_AREA_TAG", param);
        return dataset;
    }

    public static IDataset queryUserMoveAreaTag(IData inData) throws Exception{
        inData.put("RSRV_VALUE_CODE", "MOVE_AREA_TAG");
        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_MOVE_AREA_TAG_BY_USER_ID", inData);
    }

    /**
     * 根据RSRV_STR4查询user_other记录
     * REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset querySnByRelId(String relid, String rsrvCode,String rightType,String discntCode ) throws Exception{
        IData param = new DataMap();
        param.put("RSRV_VALUE", relid);
        param.put("RSRV_VALUE_CODE", rsrvCode);
        param.put("RSRV_STR1", rightType);
        param.put("RSRV_STR2", discntCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_SN_BY_REID", param);
        return dataset;
    }
}
