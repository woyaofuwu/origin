package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzy;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class GroupInfoQueryDAO
{

    public static IDataset existOldShortNum(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT COUNT(1) RECORDCOUNT ");
        parser.addSQL(" FROM TF_F_RELATION_UU  uu ");
        parser.addSQL(" WHERE user_id_a = :USER_ID_A ");
        parser.addSQL("       AND user_id_b = :USER_ID_B ");
        parser.addSQL("       AND sysdate>=start_date AND sysdate<=end_date ");
        parser.addSQL("       AND short_code= :SHORT_CODE");
        parser.addSQL("       AND relation_type_code='20' ");

        return Dao.qryByParse(parser);
    }

    /**
     * 功能描述：获取查询账单周期参数；
     * 
     * @author zhangbaoshi 功能
     * @输入参数： ACCEPT_DATE 查询账单的日期 格式 mmmmdd GROUP_ID 集团编码 格式 number
     * @输出参数：符合条件的集团账单信息(产品订购关系)；
     */
    public static IDataset getAcceptDateInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT ACYC_ID, BCYC_ID, TO_CHAR(ACYC_START_TIME,'YYYY-MM-DD')ACYC_START_TIME, TO_CHAR(ACYC_END_TIME - 1, 'YYYY-MM-DD') ACYC_END_TIME ,USE_TAG ");
        parser.addSQL(" FROM TD_A_ACYCPARA WHERE BCYC_ID<=TO_NUMBER(TO_CHAR (ADD_MONTHS (SYSDATE,TO_NUMBER('-1') ),'YYYYMM')) ");
        parser.addSQL(" AND  BCYC_ID>= TO_NUMBER(TO_CHAR(ADD_MONTHS(SYSDATE,TO_NUMBER('-12')),'YYYYMM')) ORDER BY ACYC_ID ");
        return Dao.qryByParse(parser);
    }

    public static IData getGrpInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select p.group_mgr_cust_name,p.cust_name ");
        parser.addSQL(" from tf_f_cust_group p ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and p.group_id=:GROUP_ID ");

        IDataset ids = Dao.qryByParse(parser);
        return ids.size() > 0 ? ids.getData(0) : new DataMap();
    }

    public static IData getGrpInfoByUserId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select p.group_id,p.group_mgr_cust_name,p.cust_name ");
        parser.addSQL(" from tf_f_user u, tf_f_cust_group p ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and u.cust_id = p.cust_id ");
        parser.addSQL(" and u.remove_tag = '0' ");
        parser.addSQL(" and u.user_id = :USER_ID_A ");
        IDataset ids = Dao.qryByParse(parser);
        return ids.size() > 0 ? ids.getData(0) : new DataMap();
    }

    public static IDataset getGrpProductList(IData param, Pagination pagination) throws Exception
    {
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT p.product_id, p.product_name, p.brand_code,p.product_id||'|'||p.product_name showname ");
        parser.addSQL(" from td_b_ptype_product t,TD_B_PRODUCT p,TD_S_PRODUCT_TYPE pt ");
        parser.addSQL(" where pt.PARENT_PTYPE_CODE='1000' ");
        parser.addSQL(" AND t.product_id=p.product_id ");
        parser.addSQL(" AND t.product_type_code=pt.product_type_code ");
        parser.addSQL(" AND SYSDATE BETWEEN t.start_date AND T.end_date ");
        parser.addSQL(" AND SYSDATE BETWEEN pt.start_date AND pt.end_date ");
        parser.addSQL(" AND product_obj_type = '1' ");
        parser.addSQL(" AND release_tag = '1' ");
        parser.addSQL(" AND EXISTS (SELECT 1 FROM td_b_product_release ");
        parser.addSQL(" WHERE (release_eparchy_code = :TRADE_EPARCHY_CODE OR release_eparchy_code = 'ZZZZ') ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL(" AND p.product_id = product_id ) ");
        parser.addSQL(" ORDER BY product_id ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset getRelaTypCode(IData dt) throws Exception
    {
        SQLParser parser = new SQLParser(dt);

        parser.addSQL(" SELECT relation_type_code  ");
        parser.addSQL(" FROM TD_B_PRODUCT_COMP ");
        parser.addSQL(" WHERE product_id = :PRODUCT_ID ");

        return Dao.qryByParse(parser);
    }

    public static IDataset getRoleCodeInfo(IData dt) throws Exception
    {
        SQLParser parser = new SQLParser(dt);

        parser.addSQL(" SELECT data_id, data_name  ");
        parser.addSQL(" FROM TD_S_STATIC ");
        parser.addSQL(" WHERE type_id = :TYPE_ID ");

        return Dao.qryByParse(parser);
    }

    public static IDataset getSerNumByVpnNoAndShortCode(IData dt) throws Exception
    {
        SQLParser parser = new SQLParser(dt);

        parser.addSQL(" SELECT m.serial_number serial_number ");
        parser.addSQL(" FROM tf_f_user_vpn_meb m ");
        parser.addSQL(" WHERE m.remove_tag='0'  ");
        parser.addSQL("        AND m.short_code = :SHORT_CODE  ");
        parser.addSQL("        AND m.user_id_a in ( ");
        parser.addSQL("            SELECT user_id FROM tf_f_user u  ");
        parser.addSQL("            WHERE u.serial_number= :VPN_NO  ");
        parser.addSQL("                            )  ");

        return Dao.qryByParse(parser);
    }

    /**
     * 功能描述：根据集团编码查询集团客户编码及集团服务代码查询集团订购关系详细资料信息
     * 
     * @author zhangbaoshi 功能
     * @输入参数： EC_USER_ID EC用户编码 BIZ_CODE 集团服务代码
     * @输出参数：符合条件的成员订购详细信息；
     */
    public static IDataset getUserGroupMbmpSubInfos(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT partition_id,to_char(user_id) user_id,to_char(serial_number) serial_number,biz_code,biz_name,");
        parser.addSQL(" to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,");
        parser.addSQL(" to_char(ec_user_id) ec_user_id,to_char(ec_serial_number) ec_serial_number,rsrv_num1,rsrv_num2,rsrv_num3,");
        parser.addSQL(" to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,");
        parser.addSQL(" to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,");
        parser.addSQL(" to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,remark,update_staff_id,update_depart_id,");
        parser.addSQL(" to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time ");
        parser.addSQL(" from tf_f_user_grp_meb_platsvc WHERE ec_user_id=TO_NUMBER(:EC_USER_ID) AND biz_code= :BIZ_CODE AND end_date > SYSDATE ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset getUsrMem(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  u.serial_number,  u.product_id,  to_char(uu.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date  ");
        parser.addSQL("   FROM tf_f_user u, tf_f_relation_uu uu ");
        parser.addSQL("  WHERE 1 = 1  ");
        parser.addSQL("    AND u.user_id = uu.user_id_b ");
        parser.addSQL("    AND u.partition_id = mod(u.user_id,10000) ");
        parser.addSQL("    AND sysdate between uu.start_date and uu.end_date ");
        parser.addSQL("    AND u.remove_tag = '0' ");
        parser.addSQL("    AND uu.user_id_a = :USER_ID_A ");
        IDataset resultSet =  Dao.qryByParse(parser);
        
        if(IDataUtil.isNotEmpty(resultSet))
        {
            for(int i = 0, isize = resultSet.size(); i < isize; i++)
            {
                IData result = resultSet.getData(i);
                result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(result.getString("PRODUCT_ID")));
            }
        }
        
        return resultSet;
    }

    public static IDataset getUsrMemCount(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  count(u.user_id) TOTAL_NUM  ");
        parser.addSQL("   FROM tf_f_user u, tf_f_relation_uu uu,tf_f_user_product up ");
        parser.addSQL("  WHERE 1 = 1  ");
        parser.addSQL("    AND u.user_id = uu.user_id_b ");
        parser.addSQL("    AND up.product_id = :PRODUCT_ID ");
        parser.addSQL("    AND u.partition_id = mod(u.user_id,10000) ");
        parser.addSQL("    AND sysdate between uu.start_date and uu.end_date ");
        parser.addSQL("    AND u.remove_tag = '0' ");
        parser.addSQL("    AND uu.user_id_a = :USER_ID_A ");
        parser.addSQL("    AND u.user_id = up.user_id ");
        return Dao.qryByParse(parser);
    }

    public static IDataset getUsrSerA(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT distinct uu.serial_number_a serial_number_a,uu.user_id_a user_id_a ");
        parser.addSQL(" FROM tf_f_relation_uu uu ");
        parser.addSQL(" WHERE   uu.relation_type_code='20' ");
        parser.addSQL("       AND uu.serial_number_b=:SERIAL_NUMBER_B AND uu.end_date>sysdate  ");
        parser.addSQL("       AND uu.user_id_a in(  ");
        parser.addSQL("       SELECT u.user_id FROM tf_f_user u ");
        parser.addSQL("       WHERE u.remove_tag='0' AND u.product_id='8000' ");
        parser.addSQL("       ) ");

        return Dao.qryByParse(parser);
    }

    public static IDataset getVpnInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT v.vpn_no vpn_no,v.vpn_name vpn_name ");
        parser.addSQL(" FROM tf_f_user_vpn v ");
        parser.addSQL(" WHERE v.user_id= :USER_ID  ");
        parser.addSQL("        AND v.remove_tag='0'  ");

        return Dao.qryByParse(parser);
    }

    public static IDataset hasTradeTypeCode(IData dt) throws Exception
    {
        SQLParser parser = new SQLParser(dt);

        parser.addSQL(" SELECT count(1) a ");
        parser.addSQL(" FROM   TD_S_TRADETYPE ");
        parser.addSQL(" WHERE  trade_type_code = :TRADE_TYPE_CODE ");
        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }

    public static IDataset intfVpnCheckRight(IData dt) throws Exception
    {
        SQLParser parser = new SQLParser(dt);

        parser.addSQL(" SELECT r.staff_id, r.right_code, r.user_product_code ");
        parser.addSQL(" FROM tf_m_staff_grp_right r ");
        parser.addSQL(" WHERE (  ");
        parser.addSQL("        r.staff_id=:STAFF_ID  ");
        parser.addSQL("        AND r.user_product_code=:USER_PRODUCT_CODE ");
        parser.addSQL("        AND r.right_code=:RIGHT_CODE  ");
        parser.addSQL("        AND r.end_date>sysdate ");
        parser.addSQL("        )  ");

        return Dao.qryByParse(parser);
    }

    public static IDataset intfVpnNeedCheckRight(IData dt) throws Exception
    {
        SQLParser parser = new SQLParser(dt);

        parser.addSQL(" SELECT r.staff_id, r.right_code, r.user_product_code ");
        parser.addSQL(" FROM tf_m_staff_grp_right r ");
        parser.addSQL(" WHERE   ");
        parser.addSQL("        r.user_product_code=:USER_PRODUCT_CODE ");
        parser.addSQL("        AND r.right_code=:RIGHT_CODE ");
        parser.addSQL("        AND r.end_date>sysdate ");

        return Dao.qryByParse(parser);
    }

    public static IDataset qryADCGroupInfoBySn(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT C.GROUP_ID, C.CUST_NAME, U.REMOVE_TAG, TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MM:SS') IN_DATE, ");
        parser.addSQL("	      GP.SERIAL_NUMBER, GP.BIZ_TYPE_CODE, GP.BIZ_NAME, GP.SERV_CODE ");
        parser.addSQL("  FROM TF_F_USER             U, ");
        parser.addSQL("       TF_F_USER_PRODUCT     UP, ");
        parser.addSQL("       TF_F_USER_GRP_PLATSVC GP, ");
        parser.addSQL("       TF_F_CUST_GROUP       C, ");
        parser.addSQL("       TF_F_USER_BLACKWHITE  BW ");
        parser.addSQL(" WHERE C.REMOVE_TAG = '0' ");
        parser.addSQL("   AND C.CUST_ID = U.CUST_ID ");
        parser.addSQL("   AND (SYSDATE BETWEEN GP.START_DATE AND GP.END_DATE) ");
        parser.addSQL("   AND GP.PARTITION_ID = UP.PARTITION_ID ");
        parser.addSQL("   AND GP.USER_ID = BW.EC_USER_ID ");
        parser.addSQL("   AND U.REMOVE_TAG = '0' ");
        parser.addSQL("   AND U.USER_ID = BW.EC_USER_ID ");
        parser.addSQL("   AND U.PARTITION_ID = UP.PARTITION_ID ");
        parser.addSQL("   AND UP.BRAND_CODE = 'ADCG' ");
        parser.addSQL("   AND UP.MAIN_TAG = '1' ");
        parser.addSQL("   AND UP.USER_ID = BW.EC_USER_ID ");
        parser.addSQL("   AND UP.PARTITION_ID = MOD(BW.EC_USER_ID, 10000) ");
        parser.addSQL("   AND (SYSDATE BETWEEN BW.START_DATE AND BW.END_DATE) ");
        parser.addSQL("   AND BW.SERIAL_NUMBER = :SERIAL_NUMBER ");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryADCMembersbyuser(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT C.GROUP_ID, C.CUST_NAME, GP.SERIAL_NUMBER, GP.SERV_CODE, GP.BIZ_NAME, GP.BIZ_TYPE_CODE, ");
        parser.addSQL("       GMP.SERIAL_NUMBER SERIAL_NUMBER_B, TO_CHAR(GMP.START_DATE, 'YYYY-MM-DD HH24:MM:SS') START_DATE ");
        parser.addSQL("  FROM TF_F_CUST_GROUP           C, ");
        parser.addSQL("       TF_F_USER_GRP_MEB_PLATSVC GMP, ");
        parser.addSQL("       TF_F_USER_GRP_PLATSVC     GP, ");
        parser.addSQL("       TF_F_USER_PRODUCT         UP ");
        parser.addSQL(" WHERE C.REMOVE_TAG = '0' ");
        parser.addSQL("   AND C.GROUP_ID = GP.GROUP_ID ");
        parser.addSQL("   AND GMP.END_DATE > SYSDATE ");
        parser.addSQL("   AND GMP.EC_USER_ID = UP.USER_ID ");
        parser.addSQL("   AND GP.END_DATE > SYSDATE ");
        parser.addSQL("   AND GP.USER_ID = UP.USER_ID ");
        parser.addSQL("   AND GP.PARTITION_ID = UP.PARTITION_ID ");
        parser.addSQL("   AND UP.BRAND_CODE = 'ADCG' ");
        parser.addSQL("   AND UP.MAIN_TAG = '1' ");
        parser.addSQL("   AND UP.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("   AND UP.USER_ID = TO_NUMBER(:USER_ID) ");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryADCPersonalOrderInfo(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select DISTINCT b.serial_number,b.biz_code,b.SERV_CODE,b.biz_name, a.group_id,b.remark, ");
        parser.addSQL("b.start_date,b.end_date,b.ec_serial_number, a.biz_type_code,");
        parser.addSQL("decode (a.biz_status,'A','正常','N','暂停','S','内部测试','T','测试待审','R','试商用','E','终止') biz_status, ");
        parser.addSQL("decode (a.BIZ_ATTR,'0','定购','1','白名单','2','黑名单') BIZ_ATTR ");
        parser.addSQL("  from tf_f_user_grp_platsvc a ,tf_f_user_grp_meb_platsvc b ");
        parser.addSQL(" where a.user_id=b.EC_USER_ID ");
        parser.addSQL("   and a.SERV_CODE=b.SERV_CODE ");
        parser.addSQL("   and b.serial_number= :SERIAL_NUMBER ");
        parser.addSQL("   and a.SERV_CODE = :SERV_CODE ");
        parser.addSQL("   and a.serial_number = :EC_SERIAL_NUMBER ");
        parser.addSQL("   and b.end_date > sysdate and a.end_date > sysdate  ");
        return Dao.qryByParse(parser, pagination);
    }
    
    public static IDataset qryCenpyOrderInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT C.CITY_CODE,                                                 ");
        parser.addSQL("       D.GROUP_ID,                                                  ");
        parser.addSQL("       D.CUST_NAME,                                                 ");
        parser.addSQL("       C.SERIAL_NUMBER as EC_SERIAL_NUMBER,                                             ");
        parser.addSQL("       D.CUST_MANAGER_ID,                                           ");
        parser.addSQL("       A.SERIAL_NUMBER,                                             ");
        parser.addSQL("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,   ");
        parser.addSQL("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,       ");
        parser.addSQL("       DECODE(A.OPER_TYPE,                                          ");
        parser.addSQL("              '3',                                                  ");
        parser.addSQL("              '指定用户全量统付',                                   ");
        parser.addSQL("              '4',                                                  ");
        parser.addSQL("              '指定用户定额统付',                                   ");
        parser.addSQL("              '5',                                                  ");
        parser.addSQL("              '指定用户限量统付',                                   ");
        parser.addSQL("              A.OPER_TYPE) OPER_TYPE,                               ");
        parser.addSQL("       A.ELEMENT_ID  ELEMENT_ID,                            ");
        parser.addSQL("       F.ATTR_NAME DISCNT_NAME,                                     ");
        parser.addSQL("       DECODE(A.OPER_TYPE, '5', TO_CHAR(A.LIMIT_FEE), ' ') LIMIT_FEE,");
        parser.addSQL("       A.INST_ID");
        parser.addSQL("  FROM TF_F_USER_MEB_CENPAY A,                                      ");
        parser.addSQL("       TF_F_USER_GRP_MERCHP B,                                      ");
        parser.addSQL("       TF_F_USER            C,                                      ");
        parser.addSQL("       TF_F_CUST_GROUP      D,                                      ");
        parser.addSQL("       TF_F_USER_PRODUCT    E,                                      ");
        parser.addSQL("       TD_B_ATTR_BIZ        F                                       ");
        parser.addSQL(" WHERE A.PRODUCT_OFFER_ID = B.PRODUCT_OFFER_ID                      ");
        parser.addSQL("   AND C.USER_ID = B.USER_ID                                        ");
        parser.addSQL("   AND C.PARTITION_ID = B.PARTITION_ID                              ");
        parser.addSQL("   AND E.USER_ID = C.USER_ID                                        ");
        parser.addSQL("   AND E.PARTITION_ID = C.PARTITION_ID                              ");
        parser.addSQL("   AND D.GROUP_ID = B.GROUP_ID                                      ");
        parser.addSQL("   AND F.ID = E.PRODUCT_ID                                          ");
        parser.addSQL("   AND F.ID_TYPE = 'D'                                              ");
        parser.addSQL("   AND F.ATTR_CODE = TO_CHAR(A.ELEMENT_ID)                          ");
        parser.addSQL("   AND F.attr_obj = 'FluxPay'                                       ");
        parser.addSQL("   AND A.OPER_TYPE=:CENPY_TYPE                               ");
        parser.addSQL("   AND A.SERIAL_NUMBER=:SERIAL_NUMBER                             ");
        parser.addSQL("   AND C.SERIAL_NUMBER=:EC_SERIAL_NUMBER                             ");
        parser.addSQL("   AND A.START_DATE>=TO_DATE(:START_TIME,'YYYY-MM-DD') AND A.START_DATE<=TO_DATE(:END_TIME,'YYYY-MM-DD')                               ");
        parser.addSQL("UNION ALL                                                           ");
        parser.addSQL("SELECT C.CITY_CODE,                                                 ");
        parser.addSQL("       D.GROUP_ID,                                                  ");
        parser.addSQL("       D.CUST_NAME,                                                 ");
        parser.addSQL("       C.SERIAL_NUMBER as EC_SERIAL_NUMBER,                                             ");
        parser.addSQL("       D.CUST_MANAGER_ID,                                           ");
        parser.addSQL("       A.SERIAL_NUMBER,                                             ");
        parser.addSQL("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,   ");
        parser.addSQL("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,       ");
        parser.addSQL("       DECODE(A.OPER_TYPE,                                          ");
        parser.addSQL("              '3',                                                  ");
        parser.addSQL("              '指定用户全量统付',                                   ");
        parser.addSQL("              '4',                                                  ");
        parser.addSQL("              '指定用户定额统付',                                   ");
        parser.addSQL("              '5',                                                  ");
        parser.addSQL("              '指定用户限量统付',                                   ");
        parser.addSQL("              A.OPER_TYPE) OPER_TYPE,                               ");
        parser.addSQL("       A.ELEMENT_ID  ELEMENT_ID,                            ");
        parser.addSQL("       '' DISCNT_NAME,                                     ");
        parser.addSQL("       DECODE(A.OPER_TYPE, '5', TO_CHAR(A.LIMIT_FEE), ' ') LIMIT_FEE,");
        parser.addSQL("       A.INST_ID");
        parser.addSQL("  FROM TF_F_USER_MEB_CENPAY A,                                      ");
        parser.addSQL("       TF_F_USER_GRP_MERCHP B,                                      ");
        parser.addSQL("       TF_F_USER            C,                                      ");
        parser.addSQL("       TF_F_CUST_GROUP      D,                                      ");
        parser.addSQL("       TF_F_USER_PRODUCT    E                                      ");
        parser.addSQL(" WHERE A.PRODUCT_OFFER_ID = B.PRODUCT_OFFER_ID                      ");
        parser.addSQL("   AND C.USER_ID = B.USER_ID                                        ");
        parser.addSQL("   AND C.PARTITION_ID = B.PARTITION_ID                              ");
        parser.addSQL("   AND E.USER_ID = C.USER_ID                                        ");
        parser.addSQL("   AND E.PARTITION_ID =C.PARTITION_ID                              ");
        parser.addSQL("   AND D.GROUP_ID = B.GROUP_ID                                      ");
        parser.addSQL("   AND A.ELEMENT_ID IS NULL                          ");
        parser.addSQL("   AND A.OPER_TYPE=:CENPY_TYPE                               ");
        parser.addSQL("   AND A.SERIAL_NUMBER=:SERIAL_NUMBER                             ");
        parser.addSQL("   AND C.SERIAL_NUMBER=:EC_SERIAL_NUMBER                             ");
        parser.addSQL("   AND A.START_DATE>=TO_DATE(:START_TIME,'YYYY-MM-DD') AND A.START_DATE<=TO_DATE(:END_TIME,'YYYY-MM-DD')                               ");
        parser.addSQL("UNION ALL                                                           ");
        parser.addSQL("SELECT C.CITY_CODE,                                                 ");
        parser.addSQL("       D.GROUP_ID,                                                  ");
        parser.addSQL("       D.CUST_NAME,                                                 ");
        parser.addSQL("       C.SERIAL_NUMBER as EC_SERIAL_NUMBER,                                             ");
        parser.addSQL("       D.CUST_MANAGER_ID,                                           ");
        parser.addSQL("       A.SERIAL_NUMBER,                                             ");
        parser.addSQL("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,   ");
        parser.addSQL("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,       ");
        parser.addSQL("       DECODE(A.PRODUCT_OFFER_ID,                                   ");
        parser.addSQL("              '7342',                                               ");
        parser.addSQL("              '指定用户全量统付',                                   ");
        parser.addSQL("              '7343',                                               ");
        parser.addSQL("              '指定用户定额统付',                                   ");
        parser.addSQL("              '7344',                                               ");
        parser.addSQL("              '指定用户限量统付',                                   ");
        parser.addSQL("              A.PRODUCT_OFFER_ID) OPER_TYPE,                        ");
        parser.addSQL("       A.ELEMENT_ID ELEMENT_ID,                                      ");
        parser.addSQL("       A.RSRV_STR2 DISCNT_NAME,                                     ");
        parser.addSQL("       TO_CHAR(A.LIMIT_FEE) LIMIT_FEE,                               ");
        parser.addSQL("       A.INST_ID");
        parser.addSQL("  FROM TF_F_USER_MEB_CENPAY A,                                      ");
        parser.addSQL("       TF_F_USER_PRODUCT    B,                                      ");
        parser.addSQL("       TF_F_USER            C,                                      ");
        parser.addSQL("       TF_F_CUST_GROUP      D                                       ");
        parser.addSQL(" WHERE A.MP_GROUP_CUST_CODE = C.USER_ID                    ");
        parser.addSQL("   AND C.USER_ID = B.USER_ID                                        ");
        parser.addSQL("   AND C.PARTITION_ID = B.PARTITION_ID                              ");
        parser.addSQL("   AND D.CUST_ID = C.CUST_ID                                        ");
        parser.addSQL("   AND A.PRODUCT_OFFER_ID IN ('7342', '7343', '7344')");
        parser.addSQL("   AND a.serial_number=:SERIAL_NUMBER");
        parser.addSQL("   AND c.serial_number=:EC_SERIAL_NUMBER");
        parser.addSQL("   AND A.OPER_TYPE=:CENPY_TYPE");
        parser.addSQL("   AND A.START_DATE>=TO_DATE(:START_TIME,'YYYY-MM-DD') AND A.START_DATE<=TO_DATE(:END_TIME,'YYYY-MM-DD')");
        

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryAllGroupENetInfo(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT DISTINCT g.group_id,g.cust_name,b.serial_number_b serial_number,b.start_date,b.end_date ");
        parser.addSQL("FROM  tf_f_user a, tf_f_relation_uu b,tf_f_cust_group g,tf_f_user_svc s ");
        parser.addSQL("WHERE b.relation_type_code IN ('28','A1') ");
        parser.addSQL("      AND b.start_date >= to_date(:VSTART_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("      AND b.start_date <= to_date(:VEND_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("      AND b.start_date <= sysdate and b.end_date > sysdate ");
        parser.addSQL("      AND a.cust_id = g.cust_id ");
        parser.addSQL("      AND g.group_id = :GROUP_ID ");
        parser.addSQL("      AND a.user_id = b.user_id_a ");
        parser.addSQL("      AND b.serial_number_b = :SERIAL_NUMBER  ");
        parser.addSQL("      AND a.partition_id=mod(b.user_id_a,10000)   ");
        parser.addSQL("      AND s.user_id = b.user_id_b ");
        parser.addSQL("      AND s.partition_id=mod(b.user_id_b,10000) ");
        parser.addSQL("      and s.service_id IN ('1160','1161') ");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryBBossktWorkSx(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT t.rsrv_str12,t.rsrv_str13,t.rsrv_str14");
        parser.addSQL(" FROM tf_b_trade_other t ");
        parser.addSQL(" WHERE t.rsrv_value_code='BOSG'");
        parser.addSQL(" AND t.trade_id=:TRADE_ID");
        parser.addSQL(" AND t.start_date>to_date(:START_DATE,'yyyy-mm-dd')");
        parser.addSQL(" AND t.start_date<to_date(:END_DATE,'yyyy-mm-dd')");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryBBossMerchpMeb(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select m.status, m.user_id, m.serial_number,  m.ec_user_id,  m.ec_serial_number,  m.product_offer_id");
        parser.addSQL(" from tf_f_user_grp_merch_meb m ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and m.serial_number = :SERIAL_NUMBER");
        parser.addSQL(" and m.ec_user_id = :EC_USER_ID");
        parser.addSQL(" and user_id = TO_NUMBER(:USER_ID)");
        parser.addSQL(" and partition_id = MOD(TO_NUMBER(:USER_ID), 10000)");
        parser.addSQL(" and  sysdate between m.start_date and m.end_date");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryDisInstId(IData dt) throws Exception
    {
        SQLParser parser = new SQLParser(dt);

        parser.addSQL(" SELECT d.inst_id ");
        parser.addSQL(" FROM   tf_f_user_discnt d,tf_f_user u ");
        parser.addSQL(" WHERE  d.user_id=u.user_id ");
        parser.addSQL("        AND d.user_id = :USER_ID AND d.relation_type_code = :RELATION_TYPE_CODE ");
        parser.addSQL("        AND d.end_date>sysdate AND u.remove_tag='0' ");

        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }

    public static IDataset qryECBizByGrpID(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select distinct c.biz_name,to_char(a.open_date, 'yyyy-mm-dd hh24:mi:ss') open_date ");
        parser.addSQL(" from tf_f_user a, tf_f_cust_group b, tf_f_user_grp_platsvc c ");
        parser.addSQL(" where 1 = 1  ");
        parser.addSQL(" and a.cust_id = b.cust_id  ");
        parser.addSQL(" and a.user_id = c.user_id  ");
        parser.addSQL(" and b.group_id = c.group_id ");
        parser.addSQL(" and b.group_id = :GROUP_ID ");
        parser.addSQL(" and a.remove_tag = '0' ");
        parser.addSQL(" and c.biz_status = 'A' ");
        parser.addSQL(" and c.end_date >SYSDATE ");

        return Dao.qryByParse(parser);

    }

    public static IDataset qryGroupENetInfo(IData param, Pagination pagination) throws Exception
    {
        String destoryFindFlag = param.getString("destroyFlag");
        String hasDestoryedStr = "";

        if ("DESTORYED".equals(destoryFindFlag))
        {
            hasDestoryedStr = "      AND not exists( ";
        }
        if ("NOT_DESTORYED".equals(destoryFindFlag))
        {
            hasDestoryedStr = "      AND exists( ";
        }

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT g.group_id,g.cust_name,b.serial_number_b serial_number,b.start_date,b.end_date ");
        parser.addSQL("FROM  tf_f_user a, tf_f_relation_uu b,tf_f_cust_group g ");
        parser.addSQL("WHERE b.relation_type_code IN ('28','A1') ");
        parser.addSQL("      AND b.start_date >= to_date(:VSTART_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("      AND b.start_date <= to_date(:VEND_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("      AND b.start_date <= sysdate and b.end_date > sysdate  ");
        parser.addSQL("      AND a.cust_id = g.cust_id ");
        parser.addSQL("      AND g.group_id = :GROUP_ID ");
        parser.addSQL("      AND a.user_id = b.user_id_a ");
        parser.addSQL("      AND b.serial_number_b = :SERIAL_NUMBER  ");
        parser.addSQL("      AND a.partition_id=mod(b.user_id_a,10000)   ");

        parser.addSQL(hasDestoryedStr);

        parser.addSQL("        SELECT count(1) ");
        parser.addSQL("        FROM TF_F_RELATION_UU uu,TF_F_USER_SVC v ");
        parser.addSQL("        WHERE v.user_id = uu.user_id_b ");
        parser.addSQL("        AND uu.relation_type_code IN ('28','A1') ");
        parser.addSQL("        AND v.partition_id=mod(b.user_id_b,10000) ");
        parser.addSQL("        AND v.service_id IN ('1160','1161') ");
        parser.addSQL("        AND v.start_date <= sysdate and v.end_date > sysdate  ");
        parser.addSQL("      )  ");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryGroupLBSByProductId(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select a.serial_number_a,d.cust_name group_name,a.serial_number_b, ");
        parser.addSQL("         e.cust_name, a.start_date, a.end_date ");
        parser.addSQL("  from tf_f_relation_uu a,tf_f_user b,tf_f_user c,tf_f_cust_group d,tf_f_customer e ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL("   and a.relation_type_code = '29' ");
        parser.addSQL("   and a.start_date < = sysdate ");
        parser.addSQL("   and a.end_date > sysdate ");
        parser.addSQL("   and a.user_id_a = b.user_id ");
        parser.addSQL("   and d.cust_id = b.cust_id ");
        parser.addSQL("   and c.user_id = a.user_id_b ");
        parser.addSQL("   and c.partition_id = mod(a.user_id_b,10000) ");
        parser.addSQL("   and e.cust_id = c.cust_id ");
        parser.addSQL("   and e.partition_id = mod(c.cust_id,10000) ");
        parser.addSQL("   AND b.serial_number = :SERIAL_NUMBER_B ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryGroupLBSBySN(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select a.serial_number_a,d.cust_name group_name,a.serial_number_b, ");
        parser.addSQL("         e.cust_name, a.start_date, a.end_date ");
        parser.addSQL("  from tf_f_relation_uu a,tf_f_user b,tf_f_user c,tf_f_cust_group d,tf_f_customer e ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL("   and a.relation_type_code = '29' ");
        parser.addSQL("   and a.start_date < = sysdate ");
        parser.addSQL("   and a.end_date > sysdate ");
        parser.addSQL("   and a.user_id_a = b.user_id ");
        parser.addSQL("   and b.partition_id = mod(a.user_id_a,10000) ");
        parser.addSQL("   and d.cust_id = b.cust_id ");
        parser.addSQL("   and c.user_id = a.user_id_b ");
        parser.addSQL("   and c.partition_id = mod(a.user_id_b,10000) ");
        parser.addSQL("   and e.cust_id = c.cust_id ");
        parser.addSQL("   and e.partition_id = mod(c.cust_id,10000) ");
        parser.addSQL("   AND a.serial_number_b = :SERIAL_NUMBER ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryGroupLBSInfo(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select a.serial_number_a,d.cust_name group_name,a.serial_number_b, ");
        parser.addSQL("         e.cust_name, a.start_date, a.end_date ");
        parser.addSQL("  from tf_f_relation_uu a,tf_f_user b,tf_f_user c,tf_f_cust_group d,tf_f_customer e ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL("   and a.relation_type_code = '29' ");
        parser.addSQL("   and a.start_date < = sysdate ");
        parser.addSQL("   and a.end_date > sysdate ");
        parser.addSQL("   and a.user_id_a = b.user_id ");
        parser.addSQL("   and d.cust_id = b.cust_id ");
        parser.addSQL("   and c.user_id = a.user_id_b ");
        parser.addSQL("   and e.cust_id = c.cust_id ");
        parser.addSQL("   and e.partition_id = mod(c.cust_id,10000) ");
        parser.addSQL("   and e.partition_id = mod(c.cust_id,10000) ");
        parser.addSQL("   AND b.serial_number = :SERIAL_NUMBER_B  ");
        parser.addSQL("   AND c.serial_number = :SERIAL_NUMBER  ");
        parser.addSQL("   and a.partition_id = mod(c.user_id,10000)  ");
        parser.addSQL("   AND B.REMOVE_TAG = '0' AND c.remove_tag ='0'  ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryGroupNetworkInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT U.USER_ID, U.SERIAL_NUMBER, U.CITY_CODE, UP.PRODUCT_ID, C.GROUP_ID, C.CUST_NAME, '移动公司' AGENT ");
        parser.addSQL("  FROM TF_F_USER_PRODUCT UP, TF_F_USER U, TF_F_CUST_GROUP C ");
        parser.addSQL(" WHERE C.CUST_ID = U.CUST_ID ");
        parser.addSQL("   AND U.USER_ID = UP.USER_ID ");
        parser.addSQL("   AND U.PARTITION_ID = UP.PARTITION_ID ");
        parser.addSQL("   AND UP.PRODUCT_ID IN (7010, 7011, 7012, 7013, 7014, 7015) ");
        parser.addSQL("   AND UP.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL("   AND C.GROUP_ID = :GROUP_ID ");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryGroupPRBTByProductId(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        String state = param.getString("STATE");

        parser.addSQL("SELECT C.CUST_NAME GROUP_NAME, C.GROUP_ID, C.CUST_MANAGER_ID, UU.SERIAL_NUMBER_A, UU.SERIAL_NUMBER_B, ");
        parser.addSQL("       TO_CHAR(UU.START_DATE, 'YYYY-MM-DD HH24:MM:SS') START_DATE, ");
        parser.addSQL("       TO_CHAR(UU.END_DATE, 'YYYY-MM-DD HH24:MM:SS') END_DATE, ");
        parser.addSQL("       DECODE(PAY.PLAN_TYPE_CODE, 'G', '是', 'P', '否', '否') PLAN_TYPE_CODE, ");
        parser.addSQL("       U.CITY_CODE ");
        parser.addSQL("  FROM TF_F_CUST_GROUP   C, ");
        parser.addSQL("       TF_F_USER_PAYPLAN PAY, ");
        parser.addSQL("       TF_F_USER         U, ");
        parser.addSQL("       TF_F_RELATION_UU  UU ");
        parser.addSQL(" WHERE C.CUST_ID = U.CUST_ID ");
        parser.addSQL("   AND PAY.USER_ID_A = UU.USER_ID_A ");
        parser.addSQL("   AND PAY.USER_ID = UU.USER_ID_B ");
        parser.addSQL("   AND PAY.PARTITION_ID = UU.PARTITION_ID ");

        if ("0".equals(state))
        {
            parser.addSQL(" AND PAY.END_DATE > SYSDATE ");
        }
        else
        {
            parser.addSQL(" AND PAY.END_DATE = (SELECT MAX(END_DATE) ");
            parser.addSQL("                       FROM TF_F_USER_PAYPLAN TEMP ");
            parser.addSQL("                      WHERE TEMP.USER_ID = UU.USER_ID_B ");
            parser.addSQL("                        AND TEMP.PARTITION_ID = UU.PARTITION_ID ");
            parser.addSQL("                        AND TEMP.USER_ID_A = UU.USER_ID_A) ");
        }

        parser.addSQL("   AND U.USER_ID = UU.USER_ID_A ");
        parser.addSQL("   AND U.PARTITION_ID = MOD(UU.USER_ID_A, 10000) ");

        if ("0".equals(state))
        {
            parser.addSQL("   AND UU.END_DATE >= SYSDATE ");
        }
        else
        {
            parser.addSQL("   AND UU.END_DATE < SYSDATE ");
        }

        parser.addSQL("   AND UU.RELATION_TYPE_CODE = '26' ");
        parser.addSQL("   AND UU.USER_ID_A = TO_NUMBER(:USER_ID_A) ");

        IDataset dataset = Dao.qryByParse(parser, pagination);

        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, row = dataset.size(); i < row; i++)
            {
                IData data = dataset.getData(i);
                String custMangerId = data.getString("CUST_MANAGER_ID");
                String manageName = UStaffInfoQry.getCustManageNameByCustManagerId(custMangerId);
                data.put("CUST_MANAGER_NAME", manageName);
            }
        }
        return dataset;
    }

    public static IDataset qryGroupPRBTBySN(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        String serialNumberB = param.getString("SERIAL_NUMBER_B");

        IData userData = UcaInfoQry.qryUserInfoBySn(serialNumberB);

        if (IDataUtil.isEmpty(userData))
        {
            return new DatasetList();
        }

        param.put("USER_ID_B", userData.getString("USER_ID"));

        String state = param.getString("STATE");

        parser.addSQL("SELECT C.CUST_NAME GROUP_NAME, C.GROUP_ID, C.CUST_MANAGER_ID, UU.SERIAL_NUMBER_A, UU.SERIAL_NUMBER_B, ");
        parser.addSQL("       TO_CHAR(UU.START_DATE, 'YYYY-MM-DD HH24:MM:SS') START_DATE, ");
        parser.addSQL("       TO_CHAR(UU.END_DATE, 'YYYY-MM-DD HH24:MM:SS') END_DATE, ");
        parser.addSQL("       DECODE(PAY.PLAN_TYPE_CODE, 'G', '是', 'P', '否', '否') PLAN_TYPE_CODE, ");
        parser.addSQL("       U.CITY_CODE ");
        parser.addSQL("  FROM TF_F_CUST_GROUP   C, ");
        parser.addSQL("       TF_F_USER_PAYPLAN PAY, ");
        parser.addSQL("       TF_F_USER         U, ");
        parser.addSQL("       TF_F_RELATION_UU  UU ");
        parser.addSQL(" WHERE C.CUST_ID = U.CUST_ID ");
        parser.addSQL("   AND PAY.USER_ID_A = UU.USER_ID_A ");
        parser.addSQL("   AND PAY.USER_ID = UU.USER_ID_B ");
        parser.addSQL("   AND PAY.PARTITION_ID = UU.PARTITION_ID ");

        if ("0".equals(state))
        {
            parser.addSQL(" AND PAY.END_DATE > SYSDATE ");
        }
        else
        {
            parser.addSQL(" AND PAY.END_DATE = (SELECT MAX(END_DATE) ");
            parser.addSQL("                       FROM TF_F_USER_PAYPLAN TEMP ");
            parser.addSQL("                      WHERE TEMP.USER_ID = UU.USER_ID_B ");
            parser.addSQL("                        AND TEMP.PARTITION_ID = UU.PARTITION_ID ");
            parser.addSQL("                        AND TEMP.USER_ID_A = UU.USER_ID_A) ");
        }

        parser.addSQL("   AND U.USER_ID = UU.USER_ID_A ");
        parser.addSQL("   AND U.PARTITION_ID = MOD(UU.USER_ID_A, 10000) ");

        if ("0".equals(state))
        {
            parser.addSQL("   AND UU.END_DATE >= SYSDATE ");
        }
        else
        {
            parser.addSQL("   AND UU.END_DATE < SYSDATE ");
        }

        parser.addSQL("   AND UU.RELATION_TYPE_CODE = '26' ");
        parser.addSQL("   AND UU.USER_ID_B = TO_NUMBER(:USER_ID_B) ");
        parser.addSQL("   AND UU.PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");

        IDataset dataset = Dao.qryByParse(parser);

        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, row = dataset.size(); i < row; i++)
            {
                IData data = dataset.getData(i);
                String custMangerId = data.getString("CUST_MANAGER_ID");
                String manageName = UStaffInfoQry.getCustManageNameByCustManagerId(custMangerId);
                data.put("CUST_MANAGER_NAME", manageName);
            }
        }
        return dataset;
    }

    public static IDataset qryGroupPRBTInfo(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT a.serial_number_a, c.cust_name group_name,  ");
        parser.addSQL(" a.serial_number_b, a.start_date,a.end_date,decode(d.plan_type_code,'G','是','P','否','否') plan_type_code,u.city_code,e.group_id,e.cust_manager_id ");
        parser.addSQL(" FROM tf_f_relation_uu a,tf_f_user u,tf_f_customer c,tf_f_user_payplan d,tf_f_cust_group e  ");
        parser.addSQL(" where a.relation_type_code = '26' ");
        parser.addSQL("  and u.user_id = a.user_id_a ");
        parser.addSQL("  and u.partition_id = mod(a.user_id_a,10000) ");
        parser.addSQL("  and c.cust_id = u.cust_id ");
        parser.addSQL("  and u.cust_id = e.cust_id ");
        parser.addSQL("  and d.user_id = a.user_id_b ");
        parser.addSQL("  and d.user_id_a = a.user_id_a ");
        parser.addSQL("  and d.partition_id=mod(a.user_id_b,10000) ");
        parser.addSQL("  AND a.serial_number_b = :SERIAL_NUMBER_B ");
        parser.addSQL("  AND a.user_id_a = :USER_ID_A ");
        if (StringUtils.equals("0", param.getString("STATE")))
        {
            parser.addSQL("  AND a.end_date >= sysdate ");
            parser.addSQL("  AND d.end_date > sysdate ");
        }
        else
        {
            parser.addSQL("  AND a.end_date < sysdate ");
            parser.addSQL(" AND d.end_date = (select max(end_date) from tf_f_user_payplan g where  g.user_id = a.user_id_b  and g.partition_id = mod(a.user_id_b, 10000)  and g.user_id_a = a.user_id_a ) ");
        }
        IDataset dataset = Dao.qryByParse(parser, pagination);
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = dataset.getData(i);
                String custMangerId = data.getString("CUST_MANAGER_ID");
                String manageName = UStaffInfoQry.getCustManageNameByCustManagerId(custMangerId);
                data.put("CUST_MANAGER_NAME", manageName);
            }
        }
        return dataset;
    }

    public static IDataset qryGrpBizHistory(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT distinct ");
        parser.addSQL(" t.serial_number, t.cust_name,t.trade_type_code,t.accept_date,");
        parser.addSQL(" t.trade_staff_id,t.trade_depart_id,t.trade_city_code,");
        parser.addSQL(" t.trade_eparchy_code,t.term_ip,t.finish_date,t.remark, ");
        parser.addSQL(" b.trade_type,c.staff_name,d.depart_name,e.area_name");
        parser.addSQL(" FROM tf_bh_trade t,TD_S_TRADETYPE b, TD_M_STAFF c,TD_M_DEPART d,TD_M_AREA e ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND t.trade_type_code=b.trade_type_code ");
        parser.addSQL(" AND t.trade_staff_id=c.staff_id ");
        parser.addSQL(" AND t.trade_depart_id=d.depart_id ");
        parser.addSQL(" AND t.trade_city_code=e.area_code ");
        parser.addSQL(" AND t.serial_number =:SERIAL_NUMBER ");
        parser.addSQL(" AND brand_code =:BRAND_CODE ");
        parser.addSQL(" AND trade_staff_id =:TRADE_STAFF_ID ");
        parser.addSQL(" AND accept_date + 0 between  TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND ");
        parser.addSQL(" TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL(" ORDER BY accept_date ");

        return Dao.qryByParse(parser, pagination);
    }

    public static String qryGrpInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select b.cust_name,b.cust_id ");
        parser.addSQL(" from tf_f_customer b,tf_f_user c  ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL("     and c.cust_id=b.cust_id ");
        parser.addSQL("     and c.remove_tag='0' ");
        parser.addSQL("     and c.user_id=:USER_ID_A ");

        IDataset ids = Dao.qryByParse(parser);
        IData dt = ids.size() > 0 ? ids.getData(0) : new DataMap();
        return dt.size() > 0 ? dt.getString("CUST_NAME") : "";
    }

    public static IDataset qryGrPInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT p.group_id group_id, u.user_id user_id ");
        parser.addSQL(" FROM   tf_f_user u ,tf_f_cust_group p ");
        parser.addSQL(" WHERE  u.cust_id=p.cust_id ");
        parser.addSQL("        AND  u.serial_number = :SERIAL_NUMBER ");
        parser.addSQL("        AND  u.product_id = :PRODUCT_ID ");
        parser.addSQL("        AND  u.net_type_code='00' ");
        parser.addSQL("        AND  u.remove_tag='0' ");
        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }

    public static IDataset qryGrpRela1(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT uu.user_id_a,uu.serial_number_a,uu.user_id_b,uu.serial_number_b,uu.short_code,uu.start_date,uu.end_date,uu.relation_type_code,e.relation_type_name,d.cust_id cust_id_b,d.cust_name");
        parser.addSQL(" FROM tf_f_relation_uu uu,td_s_relation e,tf_f_user u,tf_f_customer d");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND uu.serial_number_a = :SERIAL_NUMBER");
        parser.addSQL(" AND uu.user_id_a = :USER_ID");
        parser.addSQL(" AND uu.end_date > last_day(trunc(sysdate)) + 1 - 1 / 24 / 3600 ");
        // parser.addSQL(" AND uu.end_date > sysdate ");
        parser.addSQL(" AND uu.relation_type_code = e.relation_type_code");
        parser.addSQL(" AND u.remove_tag = '0'");
        parser.addSQL(" AND u.user_id = uu.user_id_b");
        parser.addSQL(" AND u.cust_id = d.cust_id");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryGrpSpecialPayInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT DISTINCT d.group_id GROUP_ID,d.cust_name CUST_NAME,e.acct_id ACCT_ID,e.pay_name PAY_NAME,e.bank_code BANK_CODE,  ");
        parser.addSQL(" e.bank_acct_no BANK_ACCT_NO,b.serial_number SERIAL_NUMBER,a.payitem_code PAYITEM_CODE,a.limit/100 para_code9, a.start_cycle_id START_CYCLE_ID,  ");
        parser.addSQL(" a.end_cycle_id para_code11,to_char(a.update_time,'yyyy-MM-dd hh24:mi:ss') para_code12,a.update_staff_id para_code13,e.pay_mode_code PAY_MODE_CODE,b.city_code city_code   ");
        parser.addSQL(" from tf_a_payrelation a ,tf_f_user b,tf_f_user_product c,tf_f_cust_group d,tf_f_account e ");
        parser.addSQL(" WHERE a.partition_id =  b.partition_id AND a.user_id = b.user_id  AND b.remove_tag='0' AND a.default_tag='0' ");
        parser.addSQL(" AND a.act_tag='1' ");
        if (param.getString("STATE").equals("1"))
            parser.addSQL(" AND to_number(to_char(SYSDATE,'yyyymmdd')) BETWEEN a.start_cycle_id AND a.end_cycle_id   ");
        parser.addSQL(" AND a.acct_id = e.acct_id AND e.partition_id = MOD(a.acct_id,10000)  AND b.partition_id = c.partition_id ");
        parser.addSQL(" AND b.user_id = c.user_id  AND d.cust_id= e.cust_id AND e.remove_tag='0' AND d.remove_tag='0'   ");
        parser.addSQL(" AND c.end_date>SYSDATE ");
        //取消查询集团产品限制
        //parser.addSQL(" AND c.product_id in (SELECT p.product_id from td_b_product p WHERE p.product_mode='12' AND p.end_date>SYSDATE UNION SELECT p.product_id FROM td_b_product p WHERE p.product_id IN ('10009410','10009411') AND p.end_date>SYSDATE )");
        parser.addSQL(" AND b.serial_number = :SERIAL_NUMBER ");
        parser.addSQL(" AND d.group_id = :GROUP_ID ");

        IDataset dataset = Dao.qryByParse(parser, pagination);
        if(IDataUtil.isNotEmpty(dataset)){
        	for(int i=0;i<dataset.size();i++){
        		IData data = dataset.getData(i);
        		String payitemcode = data.getString("PAYITEM_CODE");
        		String itemname = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ITEM", "ITEM_NAME", "ITEM_ID", payitemcode);
        		if(StringUtils.isEmpty(itemname)){
        			itemname = "新付费帐目,未命名";
        		}
        		data.put("ITEM_NAME", itemname);
        	}
        }
        return dataset;
    }

    public static IDataset qryIPLaterPayInfoA(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT B.SERIAL_NUMBER_A, D.CUST_NAME, B.SERIAL_NUMBER_B, A.USER_STATE_CODESET, ");
        parser.addSQL("A.OPEN_DATE, D.REMARK ");
        parser.addSQL("FROM TF_F_USER A, TF_F_RELATION_UU B, TF_F_CUSTOMER D ");
        parser.addSQL("WHERE A.REMOVE_TAG = '0' ");
        parser.addSQL("AND A.USER_ID = B.USER_ID_B ");
        parser.addSQL("AND A.PARTITION_ID = B.PARTITION_ID ");
        parser.addSQL("AND B.USER_ID_A = (SELECT USER_ID ");
        parser.addSQL("FROM TF_F_USER ");
        parser.addSQL("WHERE SERIAL_NUMBER = :SERIAL_NUMBER_A ");
        parser.addSQL("AND REMOVE_TAG = '0') ");
        parser.addSQL("AND D.CUST_ID = A.CUST_ID ");
        parser.addSQL("AND D.PARTITION_ID = MOD(A.CUST_ID, 10000) ");
        parser.addSQL("AND B.RELATION_TYPE_CODE = '51' ");

        IDataset dataset = Dao.qryByParse(parser, pagination);
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0;i<dataset.size();i++)
        	{
        		IData data = dataset.getData(i);
        		String userStateCodeset = data.getString("USER_STATE_CODESET");
        		String stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode("75", userStateCodeset);
        		if(StringUtils.isBlank(stateName)||StringUtils.isEmpty(stateName))
        			stateName = "";
        		data.put("STATE_NAME", stateName);
        	}
        }
        return dataset;
    }

    public static IDataset qryIPLaterPayInfoB(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT B.SERIAL_NUMBER_A, D.CUST_NAME, B.SERIAL_NUMBER_B, A.USER_STATE_CODESET, ");
        parser.addSQL("A.OPEN_DATE, D.REMARK ");
        parser.addSQL("FROM TF_F_USER A, TF_F_RELATION_UU B, TF_F_CUSTOMER D ");
        parser.addSQL("WHERE A.REMOVE_TAG = '0' ");
        parser.addSQL("AND A.USER_ID = B.USER_ID_B ");
        parser.addSQL("AND A.PARTITION_ID = B.PARTITION_ID ");
        parser.addSQL("AND B.USER_ID_A = ");
        parser.addSQL("(SELECT USER_ID_A ");
        parser.addSQL("FROM TF_F_RELATION_UU ");
        parser.addSQL("WHERE SERIAL_NUMBER_B = :SERIAL_NUMBER ");
        parser.addSQL("AND RELATION_TYPE_CODE = '51' ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE) ");
        parser.addSQL("AND D.CUST_ID = A.CUST_ID ");
        parser.addSQL("AND D.PARTITION_ID = MOD(A.CUST_ID, 10000) ");
        parser.addSQL("AND B.RELATION_TYPE_CODE = '51' ");

        IDataset dataset = Dao.qryByParse(parser, pagination);
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0;i<dataset.size();i++)
        	{
        		IData data = dataset.getData(i);
        		String userStateCodeset = data.getString("USER_STATE_CODESET");
        		String stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode("75", userStateCodeset);
        		if(StringUtils.isBlank(stateName)||StringUtils.isEmpty(stateName))
        			stateName = "";
        		data.put("STATE_NAME", stateName);
        	}
        }
        return dataset;
    }

    public static IDataset qryMemDisName(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT discnt_name ");
        parser.addSQL(" FROM   td_b_discnt ");
        parser.addSQL(" WHERE  discnt_code = :DISCNT_CODE ");

        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }

    public static IDataset qryMerchpInfos(IData param, Pagination pagination, String routeId) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT a.* ");
        parser.addSQL("  FROM  TF_F_USER_GRP_MERCHP  a, td_f_poproduct t ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("   AND t.pospecnumber=a.MERCH_SPEC_CODE ");
        parser.addSQL("   AND t.PRODUCTSPECNUMBER=a.PRODUCT_SPEC_CODE ");
        parser.addSQL("   AND a.group_id = :GROUP_ID ");
        parser.addSQL("   AND a.USER_ID = :USER_ID ");
        parser.addSQL("   AND a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE ");
        parser.addSQL("   AND a.PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE  ");
        parser.addSQL("   AND a.status = :STATUS  ");
        parser.addSQL("   AND a.end_date > sysdate ");

        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset qryMobilePhoneCodeInfo(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT a.group_id,a.cust_name, a.cust_manager_id,b.product_id,c.serial_number_b ");
        parser.addSQL("  FROM tf_F_cust_group a, Tf_f_Cust_Product b,Tf_f_Relation_Uu c ");
        parser.addSQL(" WHERE a.cust_id = b.cust_id ");
        parser.addSQL("   AND b.product_id = '6100' ");
        parser.addSQL("   AND b.rsrv_str5=c.user_id_a ");
        parser.addSQL("   AND c.role_code_b = '2' ");
        parser.addSQL("   AND a.group_id = :GROUP_ID     ");
        parser.addSQL("   AND a.cust_name like '%'||:CUST_NAME||'%' ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryParentVpmnQuery(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT to_char(a.user_id) user_id,vpn_no,vpn_name,to_char(user_id_a) user_id_a, ");
        parser.addSQL("group_area,scp_code,vpmn_type,province,sub_state,func_tlags,feeindex, ");
        parser.addSQL("inter_feeindex,out_feeindex,outgrp_feeindex,subgrp_feeindex,notdiscnt_feeindex, ");
        parser.addSQL("pre_ip_no,pre_ip_disc,othor_ip_disc,trans_no,max_close_num,max_num_close, ");
        parser.addSQL("person_maxclose,max_outgrp_num,max_outgrp_max,max_inner_num,max_outnum, ");
        parser.addSQL("max_users,max_linkman_num,max_telphonist_num,max_limit_users, ");
        parser.addSQL("to_char(pkg_start_date,'yyyy-mm-dd hh24:mi:ss') pkg_start_date,pkg_type, ");
        parser.addSQL("discount,to_char(limit_fee) limit_fee,zone_max,zonefree_num, ");
        parser.addSQL("to_char(zone_fee) zone_fee,mt_maxnum,aip_id,work_type_code,vpn_scare_code, ");
        parser.addSQL("vpn_time_code,vpn_user_code,vpn_bundle_code,manager_no,call_net_type, ");
        parser.addSQL("call_area_type,over_fee_tag,limfee_type_code,sinword_type_code,move_tag, ");
        parser.addSQL("trans_tag,lock_tag,cust_manager,link_man,to_char(month_fee_limit) month_fee_limit, ");
        parser.addSQL("short_code_len,call_roam_type,bycall_roam_type,payitem_code, ");
        parser.addSQL("to_char(item_fee) item_fee,to_char(b.open_date,'yyyy-mm-dd hh24:mi:ss') open_date, ");
        parser.addSQL("to_char(a.remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date, ");
        parser.addSQL("a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_num4,a.rsrv_num5, ");
        parser.addSQL("a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5, ");
        parser.addSQL("a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,a.rsrv_tag1,a.rsrv_tag2,a.rsrv_tag3 ");
        parser.addSQL("FROM tf_f_user_vpn a , tf_f_user b ");
        parser.addSQL("WHERE b.user_id=a.user_id(+) ");
        parser.addSQL("AND b.partition_id=Mod(b.user_id,10000) ");
        parser.addSQL("AND b.remove_tag='0' ");
        parser.addSQL("AND b.user_id=to_number(:USER_ID) ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryPhoneAndKlfInfo(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select a.product_id,e.product_name,a.serial_number,'利客发' para_code4,b.update_time ");
        parser.addSQL("FROM tf_f_user a, tf_a_payrelation b, td_b_product e ");
        parser.addSQL("WHERE a.user_id=b.user_id ");
        parser.addSQL("AND a.remove_tag='0' ");
        parser.addSQL("AND a.brand_code = 'DX01' ");
        parser.addSQL("AND a.product_id = 7024 ");
        parser.addSQL("AND b.start_acyc_id <= :VPARA_CODE2 ");
        parser.addSQL("AND b.end_acyc_id >= :VPARA_CODE2 ");
        parser.addSQL("AND a.serial_number <> :SERIAL_NUMBER ");
        parser.addSQL("AND a.product_id = e.product_id ");
        parser.addSQL("AND b.acct_id IN ( ");
        parser.addSQL("SELECT d.acct_id FROM tf_f_user c, tf_a_payrelation d ");
        parser.addSQL("WHERE c.user_id=d.user_id ");
        parser.addSQL("AND c.remove_tag='0' ");
        parser.addSQL("AND c.serial_number = :SERIAL_NUMBER ");
        parser.addSQL("AND d.start_acyc_id <= :VPARA_CODE2 ");
        parser.addSQL("AND d.end_acyc_id >= :VPARA_CODE2 ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryProductInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        if ("1".equals(param.get("IS_Flag")))
        {
            parser.addSQL("SELECT U.SERIAL_NUMBER,U.USER_ID, TO_CHAR(U.IN_DATE, 'YYYY-MM-DD HH24:MM:SS') IN_DATE, ");
            parser.addSQL("       TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MM:SS') OPEN_DATE, ");
            parser.addSQL("       UP.PRODUCT_ID, UP.BRAND_CODE, GP.SERV_CODE, GP.BIZ_NAME, GP.SERVICE_ID");
            parser.addSQL("  FROM TF_F_USER                 U, ");
            parser.addSQL("       TF_F_USER_PRODUCT         UP, ");
            parser.addSQL("		  TF_F_USER_GRP_PLATSVC     GP ");
            parser.addSQL("	WHERE U.REMOVE_TAG = '0' ");
            parser.addSQL("	  AND U.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND UP.END_DATE > SYSDATE ");
            parser.addSQL("	  AND UP.BRAND_CODE IN ('ADCG', 'MASG') ");
            parser.addSQL("	  AND UP.MAIN_TAG = '1' ");
            parser.addSQL("	  AND UP.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND UP.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND GP.SERV_CODE = :SERV_CODE ");
            parser.addSQL("	  AND GP.BIZ_ATTR = :BIZ_ATTR ");
            parser.addSQL("	  AND GP.SERIAL_NUMBER = :SERIAL_NUMBER ");
            parser.addSQL("	  AND GP.GROUP_ID = :GROUP_ID ");
            parser.addSQL("	ORDER BY U.OPEN_DATE ");
        }
        else
        {
            parser.addSQL("SELECT U.SERIAL_NUMBER,U.USER_ID, TO_CHAR(U.IN_DATE, 'YYYY-MM-DD HH24:MM:SS') IN_DATE, ");
            parser.addSQL("       TO_CHAR(U.OPEN_DATE, 'YYYY-MM-DD HH24:MM:SS') OPEN_DATE, ");
            parser.addSQL("       UP.PRODUCT_ID, UP.BRAND_CODE, GP.SERV_CODE, GP.BIZ_NAME, GP.SERVICE_ID");
            parser.addSQL("  FROM TF_F_USER                 U, ");
            parser.addSQL("       TF_F_USER_PRODUCT         UP, ");
            parser.addSQL("		  TF_F_USER_GRP_PLATSVC     GP ");
            parser.addSQL("	WHERE U.REMOVE_TAG = '0' ");
            parser.addSQL("	  AND U.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND UP.END_DATE > SYSDATE ");
            parser.addSQL("	  AND UP.BRAND_CODE IN ('ADCG', 'MASG') ");
            parser.addSQL("	  AND UP.MAIN_TAG = '1' ");
            parser.addSQL("	  AND UP.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND UP.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND (SYSDATE BETWEEN GP.START_DATE AND GP.END_DATE) ");
            parser.addSQL("	  AND GP.SERV_CODE = :SERV_CODE ");
            parser.addSQL("	  AND GP.BIZ_ATTR = :BIZ_ATTR ");
            parser.addSQL("	  AND GP.SERIAL_NUMBER = :SERIAL_NUMBER ");
            parser.addSQL("	  AND GP.GROUP_ID = :GROUP_ID ");
            parser.addSQL("	ORDER BY U.OPEN_DATE ");
        }

        IDataset dataset = Dao.qryByParse(parser, pagination);
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = dataset.getData(i);
                // 积分值SCORE_VALUE
                String score = ""; // 用户总可用积分
                try
                {
                    IDataset scoreInfos = AcctCall.queryUserScoreInfo(data.getString("SERIAL_NUMBER"));

                    if (IDataUtil.isNotEmpty(scoreInfos))
                    {
                        score = scoreInfos.getData(0).getString("SUM_SCORE");
                    }
                }
                catch (Exception e)
                {
                    String err = "调账务接口出错";
                }
                data.put("SCORE_VALUE", score); // 积分值
                // 信用等级CREDIT_CLASS 信用度CREDIT_VALUE
                String creditClass = "";
                String creditValue = "";

                try
                {
                    IDataset creditInfos = AcctCall.getUserCreditInfos("0", data.getString("USER_ID"));

                    if (IDataUtil.isNotEmpty(creditInfos))
                    {
                        creditClass = creditInfos.getData(0).getString("CREDIT_CLASS");
                        creditValue = creditInfos.getData(0).getString("CREDIT_VALUE");
                    }
                }
                catch (Exception e)
                {
                    String err = "调信控接口出错";
                }
                data.put("CREDIT_CLASS", creditClass); // 信用等级
                data.put("CREDIT_VALUE", creditValue); // 信用度
                
                String product_name = UProductInfoQry.getProductNameByProductId(data.getString("PRODUCT_ID"));
                data.put("PRODUCT_NAME", product_name);
            }
        }
        return dataset;
    }

    public static IDataset qryProductMebInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        if ("1".equals(param.get("IS_Flag")))
        {
            parser.addSQL("SELECT GMP.SERIAL_NUMBER AS MEB_SERIAL_NUMBER, GMP.SERV_CODE, GMP.BIZ_NAME, GP.SERIAL_NUMBER AS SERIAL_NUMBER, ");
            parser.addSQL("       GP.BIZ_TYPE_CODE, GP.ACCESS_MODE, ");
            parser.addSQL("       (CASE WHEN GP.BIZ_STATE_CODE = 'A' THEN '正常商用' WHEN GP.BIZ_STATE_CODE = 'N' THEN '暂停' ELSE GP.BIZ_STATE_CODE  END) BIZ_STATE_CODE, ");
            parser.addSQL("       GP.PRICE, GP.BILLING_TYPE, GP.BIZ_PRI, GP.BIZ_STATUS, GP.BIZ_ATTR, GP.SERVICE_ID ");
            parser.addSQL("  FROM TF_F_USER_GRP_MEB_PLATSVC GMP, ");
            parser.addSQL("       TF_F_USER                 U, ");
            parser.addSQL("       TF_F_USER_PRODUCT         UP, ");
            parser.addSQL("		  TF_F_USER_GRP_PLATSVC     GP ");
            parser.addSQL(" WHERE GMP.EC_USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.REMOVE_TAG = '0' ");
            parser.addSQL("	  AND U.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND UP.END_DATE > SYSDATE ");
            parser.addSQL("	  AND UP.BRAND_CODE IN ('ADCG', 'MASG') ");
            parser.addSQL("	  AND UP.MAIN_TAG = '1' ");
            parser.addSQL("	  AND UP.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND UP.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND (SYSDATE BETWEEN GP.START_DATE AND GP.END_DATE) ");
            parser.addSQL("	  AND GP.SERV_CODE = :SERV_CODE ");
            parser.addSQL("	  AND GP.BIZ_ATTR = :BIZ_ATTR ");
            parser.addSQL("	  AND GP.SERIAL_NUMBER = :SERIAL_NUMBER ");
            parser.addSQL("	  AND GP.GROUP_ID = :GROUP_ID ");

        }
        else
        {
            parser.addSQL("SELECT GMP.SERIAL_NUMBER AS MEB_SERIAL_NUMBER, GMP.SERV_CODE, GMP.BIZ_NAME, GP.SERIAL_NUMBER AS SERIAL_NUMBER, ");
            parser.addSQL("       GP.BIZ_TYPE_CODE, GP.ACCESS_MODE, ");
            parser.addSQL("       (CASE WHEN GP.BIZ_STATE_CODE = 'A' THEN '正常商用' WHEN GP.BIZ_STATE_CODE = 'N' THEN '暂停' ELSE GP.BIZ_STATE_CODE  END) BIZ_STATE_CODE, ");
            parser.addSQL("       GP.PRICE, GP.BILLING_TYPE, GP.BIZ_PRI, GP.BIZ_STATUS, GP.BIZ_ATTR, GP.SERVICE_ID ");
            parser.addSQL("  FROM TF_F_USER_GRP_MEB_PLATSVC GMP, ");
            parser.addSQL("       TF_F_USER                 U, ");
            parser.addSQL("       TF_F_USER_PRODUCT         UP, ");
            parser.addSQL("		  TF_F_USER_GRP_PLATSVC     GP ");
            parser.addSQL("	WHERE (SYSDATE BETWEEN GMP.START_DATE AND GMP.END_DATE) ");
            parser.addSQL("   AND GMP.EC_USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.REMOVE_TAG = '0' ");
            parser.addSQL("	  AND U.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND UP.END_DATE > SYSDATE ");
            parser.addSQL("	  AND UP.BRAND_CODE IN ('ADCG', 'MASG') ");
            parser.addSQL("	  AND UP.MAIN_TAG = '1' ");
            parser.addSQL("	  AND UP.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND UP.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND (SYSDATE BETWEEN GP.START_DATE AND GP.END_DATE) ");
            parser.addSQL("	  AND GP.SERV_CODE = :SERV_CODE ");
            parser.addSQL("	  AND GP.BIZ_ATTR = :BIZ_ATTR ");
            parser.addSQL("	  AND GP.SERIAL_NUMBER = :SERIAL_NUMBER ");
            parser.addSQL("	  AND GP.GROUP_ID = :GROUP_ID ");
        }

        IDataset ds = Dao.qryByParse(parser, pagination);
        if (IDataUtil.isNotEmpty(ds))
        {
            for (int i = 0; i < ds.size(); i++)
            {
                IData data = ds.getData(i);
                data.put("BIZ_PRI", StaticUtil.getStaticValue("PLAT_GRP_BIZ_PRI", data.getString("BIZ_PRI","").trim())); // j2ee发现前面有空格的数据
                //翻译业务状态和业务属性
                String serviceId = data.getString("SERVICE_ID");
                String bizStatus = data.getString("BIZ_STATUS");
                String bizAttr   = data.getString("BIZ_ATTR");
                IData  transLate  = UItemBInfoQry.qryChaSpecialVal(serviceId, "S", "TD_M_BIZSTATUS", bizStatus);
                data.put("BIZ_STATE", transLate.getString("TEXT"));
                transLate.clear();
                transLate = UItemBInfoQry.qryChaSpecialVal(serviceId, "S", "TD_M_BIZBLACKWHITE", bizAttr);
                data.put("BLACK_WHITE", transLate.getString("TEXT"));

            }
        }
        return ds;
    }

    public static IDataset qryProductMebInfoExport(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        if ("0".equals(param.get("IS_Flag")))
        {
            parser.addSQL("SELECT GMP.SERIAL_NUMBER AS MEB_SERIAL_NUMBER, GMP.SERV_CODE, GMP.BIZ_NAME, GP.SERIAL_NUMBER AS SERIAL_NUMBER, ");
            parser.addSQL("       GP.BIZ_TYPE_CODE, GP.ACCESS_MODE, ");
            parser.addSQL("       (CASE WHEN GP.BIZ_STATE_CODE = 'A' THEN '正常商用' WHEN GP.BIZ_STATE_CODE = 'N' THEN '暂停' ELSE GP.BIZ_STATE_CODE  END) BIZ_STATE_CODE, ");
            parser.addSQL("       GP.PRICE, GP.BILLING_TYPE, GP.BIZ_PRI, GP.BIZ_STATUS, GP.BIZ_ATTR, GP.SERVICE_ID ");
            parser.addSQL("  FROM TF_F_USER_GRP_MEB_PLATSVC GMP, ");
            parser.addSQL("       TF_F_USER                 U, ");
            parser.addSQL("       TF_F_USER_PRODUCT         UP, ");
            parser.addSQL("		  TF_F_USER_GRP_PLATSVC     GP ");
            parser.addSQL("	WHERE SYSDATE > GMP.END_DATE ");
            parser.addSQL("   AND GMP.EC_USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.REMOVE_TAG = '0' ");
            parser.addSQL("	  AND U.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND UP.END_DATE > SYSDATE ");
            parser.addSQL("	  AND UP.BRAND_CODE IN ('ADCG', 'MASG') ");
            parser.addSQL("	  AND UP.MAIN_TAG = '1' ");
            parser.addSQL("	  AND UP.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND UP.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND (SYSDATE BETWEEN GP.START_DATE AND GP.END_DATE) ");
            parser.addSQL("	  AND GP.SERV_CODE = :SERV_CODE ");
            parser.addSQL("	  AND GP.BIZ_ATTR = :BIZ_ATTR ");
            parser.addSQL("	  AND GP.SERIAL_NUMBER = :SERIAL_NUMBER ");
            parser.addSQL("	  AND GP.GROUP_ID = :GROUP_ID ");
        }
        else
        {
            parser.addSQL("SELECT GMP.SERIAL_NUMBER AS MEB_SERIAL_NUMBER, GMP.SERV_CODE, GMP.BIZ_NAME, GP.SERIAL_NUMBER AS SERIAL_NUMBER, ");
            parser.addSQL("       GP.BIZ_TYPE_CODE, GP.ACCESS_MODE, ");
            parser.addSQL("       (CASE WHEN GP.BIZ_STATE_CODE = 'A' THEN '正常商用' WHEN GP.BIZ_STATE_CODE = 'N' THEN '暂停' ELSE GP.BIZ_STATE_CODE  END) BIZ_STATE_CODE, ");
            parser.addSQL("       GP.PRICE, GP.BILLING_TYPE, GP.BIZ_PRI, GP.BIZ_STATUS, GP.BIZ_ATTR, GP.SERVICE_ID ");
            parser.addSQL("  FROM TF_F_USER_GRP_MEB_PLATSVC GMP, ");
            parser.addSQL("       TF_F_USER                 U, ");
            parser.addSQL("       TF_F_USER_PRODUCT         UP, ");
            parser.addSQL("		  TF_F_USER_GRP_PLATSVC     GP ");
            parser.addSQL("	WHERE (SYSDATE BETWEEN GMP.START_DATE AND GMP.END_DATE) ");
            parser.addSQL("   AND GMP.EC_USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.REMOVE_TAG = '0' ");
            parser.addSQL("	  AND U.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND U.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND UP.END_DATE > SYSDATE ");
            parser.addSQL("	  AND UP.BRAND_CODE IN ('ADCG', 'MASG') ");
            parser.addSQL("	  AND UP.MAIN_TAG = '1' ");
            parser.addSQL("	  AND UP.USER_ID = GP.USER_ID ");
            parser.addSQL("	  AND UP.PARTITION_ID = GP.PARTITION_ID ");
            parser.addSQL("	  AND (SYSDATE BETWEEN GP.START_DATE AND GP.END_DATE) ");
            parser.addSQL("	  AND GP.SERV_CODE = :SERV_CODE ");
            parser.addSQL("	  AND GP.BIZ_ATTR = :BIZ_ATTR ");
            parser.addSQL("	  AND GP.SERIAL_NUMBER = :SERIAL_NUMBER ");
            parser.addSQL("	  AND GP.GROUP_ID = :GROUP_ID ");
        }

        IDataset ds = Dao.qryByParse(parser, pagination, Route.getCrmDefaultDb());
        if (IDataUtil.isNotEmpty(ds))
        {
            for (int i = 0; i < ds.size(); i++)
            {
                IData data = ds.getData(i);
                String bizStatus = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_ITEMB", new String[]
                { "ID", "ATTR_FIELD_CODE", "ATTR_CODE" }, "ATTR_FIELD_NAME", new String[]
                { data.getString("SERVICE_ID"), data.getString("BIZ_STATUS"), "TD_M_BIZSTATUS" });
                String bizAttr = data.getString("BIZ_ATTR") + "(" + StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_ITEMB", new String[]
                { "ID", "ATTR_FIELD_CODE", "ATTR_CODE" }, "ATTR_FIELD_NAME", new String[]
                { data.getString("SERVICE_ID"), data.getString("BIZ_ATTR"), "TD_M_BIZBLACKWHITE" }) + ")";

                data.put("BIZ_TYPE_CODE", StaticUtil.getStaticValue("GRP_PLAT_BIZ_TYPE_CODE", data.getString("BIZ_TYPE_CODE")));
                data.put("ACCESS_MODE", StaticUtil.getStaticValue("GRP_PLAT_ACCESS_MODE", data.getString("ACCESS_MODE")));
                data.put("PRICE", String.valueOf(data.getDouble("PRICE") / 100));
                data.put("BILLING_TYPE", StaticUtil.getStaticValue("GRP_PLAT_QADC_BILLINGTYPE", data.getString("BILLING_TYPE")));
                data.put("BIZ_PRI", StaticUtil.getStaticValue("PLAT_GRP_BIZ_PRI", data.getString("BIZ_PRI").trim()));// j2ee发现前面有空格的数据
                data.put("BIZ_STATUS", bizStatus);
                data.put("BIZ_ATTR", bizAttr);

            }
        }
        return ds;
    }

    public static IDataset qrySerialNum(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT u.serial_number,u.brand_code,u.eparchy_code FROM tf_f_user u ");
        parser.addSQL(" where u.remove_tag='0' ");
        parser.addSQL(" and u.user_id=:USER_ID  ");
        parser.addSQL(" and u.product_id=:PRODUCT_ID ");
        return Dao.qryByParse(parser);
    }

    public static IDataset qryShortCodeByVpmn(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT d.short_code,decode(e.SHORT_CODE,d.short_code,'已用', '未用') STATE                             ");
        parser.addSQL("  FROM (select *from td_b_vshortcode                                                             ");
        parser.addSQL("  where 1 = 1                                         ");
        parser.addSQL("  and (contain_4= :contain_4 OR :contain_4 IS NULL)                                               ");
        parser.addSQL("  and (contain_7= :contain_7 OR :contain_7 IS NULL)                                                ");
        parser.addSQL("   and (SHORT_CODE LIKE '6'||:SHORT_LENGTH ||'%' OR :SHORT_LENGTH IS NULL)");
        parser.addSQL("  and (length = :SHORT_TYPE OR :SHORT_TYPE IS NULL)                                                     ");
        parser.addSQL("   )d                                                     ");
        parser.addSQL("  left join (select a.* from TF_F_RELATION_UU A , TF_F_USER B, TF_F_USER_VPN C                   ");
        parser.addSQL(" where 1 = 1                                                                                     ");
        parser.addSQL("   AND A.SERIAL_NUMBER_A = B.SERIAL_NUMBER                                                       ");
        parser.addSQL("   AND A.USER_ID_A = B.USER_ID                                                                   ");
        parser.addSQL("   AND B.SERIAL_NUMBER = C.VPN_NO                                                                ");
        parser.addSQL("   AND A.RELATION_TYPE_CODE = '20'                                                               ");
        parser.addSQL("   AND A.END_DATE > SYSDATE                                                                      ");
        parser.addSQL("   AND B.REMOVE_TAG = '0'                                                                        ");
        parser.addSQL("   AND (B.SERIAL_NUMBER = :SERIAL_NUMBER_A OR :SERIAL_NUMBER_A IS NULL)) e ");
        parser.addSQL("   on d.short_code=e.short_code");
        // parser.addSQL("   order by e.short_code                                                  ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryShortCodeByVpmn11(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT d.short_code,decode(e.SHORT_CODE,d.short_code,'已用', '未用') STATE                             ");
        parser.addSQL("  FROM (select *from td_b_vshortcode                                                             ");
        parser.addSQL("  where 1 = 1                                         ");
        parser.addSQL("  and (contain_4= :contain_4 OR :contain_4 IS NULL)                                               ");
        parser.addSQL("  and (contain_7= :contain_7 OR :contain_7 IS NULL)                                                ");
        parser.addSQL("   and (SHORT_CODE LIKE '6'||:SHORT_LENGTH ||'%' OR :SHORT_LENGTH IS NULL)");
        parser.addSQL("  and (length = :SHORT_TYPE OR :SHORT_TYPE IS NULL)                                                     ");
        parser.addSQL("   )d                                                     ");
        parser.addSQL("  left join (select a.* from TF_F_RELATION_UU A , TF_F_USER B                  ");
        parser.addSQL(" where 1 = 1                                                                                     ");
        parser.addSQL("   AND A.SERIAL_NUMBER_A = B.SERIAL_NUMBER                                                       ");
        parser.addSQL("   AND A.USER_ID_A = B.USER_ID                                                                   ");
        // parser.addSQL("   AND B.SERIAL_NUMBER = C.VPN_NO                                                                ");
        parser.addSQL("   AND exists(select 1 from TF_F_USER_VPN C where B.SERIAL_NUMBER = C.VPN_NO)                                                               ");
        parser.addSQL("   AND A.RELATION_TYPE_CODE = '20'                                                               ");
        parser.addSQL("   AND A.END_DATE > SYSDATE                                                                      ");
        parser.addSQL("   AND B.REMOVE_TAG = '0'                                                                        ");
        parser.addSQL("   AND (B.SERIAL_NUMBER = :SERIAL_NUMBER_A OR :SERIAL_NUMBER_A IS NULL)) e ");
        parser.addSQL("   on d.short_code=e.short_code");
        // parser.addSQL("   order by e.short_code                                                  ");
        SQLParser parser1 = new SQLParser(param);
        parser1.addParser(parser);
        return Dao.qryByParse(parser, pagination);

    }

    public static IDataset qryShortCodeByVpmn4M(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT d.short_code,decode(e.SHORT_CODE,d.short_code,'已用', '未用') STATE                             ");
        parser.addSQL("  FROM (select *from td_b_vshortcode                                                             ");
        parser.addSQL("  where 1 = 1                                         ");
        parser.addSQL("  and (contain_4= :contain_4 OR :contain_4 IS NULL)                                               ");
        parser.addSQL("  and (contain_7= :contain_7 OR :contain_7 IS NULL)                                                ");
        parser.addSQL("   and (SHORT_CODE LIKE '6'||:SHORT_LENGTH ||'%' OR :SHORT_LENGTH IS NULL)");
        parser.addSQL("  and (length = :SHORT_TYPE OR :SHORT_TYPE IS NULL)                                                     ");
        parser.addSQL("   )d                                                     ");
        parser.addSQL("  left join (select a.* from TF_F_RELATION_UU A , TF_F_USER B                  ");
        parser.addSQL(" where 1 = 1                                                                                     ");
        parser.addSQL("   AND A.SERIAL_NUMBER_A = B.SERIAL_NUMBER                                                       ");
        parser.addSQL("   AND A.USER_ID_A = B.USER_ID                                                                   ");
        // parser.addSQL("   AND B.SERIAL_NUMBER = C.VPN_NO                                                                ");
        parser.addSQL("   AND exists(select 1 from TF_F_USER_VPN C where B.SERIAL_NUMBER = C.VPN_NO)                                                               ");
        parser.addSQL("   AND A.RELATION_TYPE_CODE = '20'                                                               ");
        parser.addSQL("   AND A.END_DATE > SYSDATE                                                                      ");
        parser.addSQL("   AND B.REMOVE_TAG = '0'                                                                        ");
        // parser.addSQL("   AND (B.SERIAL_NUMBER in (select SERIAL_NUMBER_B from TF_F_RELATION_UU where SERIAL_NUMBER_A= :MserialNumberA OR :MserialNumberA IS NULL))) e ");
        parser.addSQL("   AND (B.SERIAL_NUMBER in (" + param.getString("SERIAL_NUMBER_A") + "))) e ");
        parser.addSQL("   on d.short_code=e.short_code");
        parser.addSQL("   order by e.short_code                                                     ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryShortCodeunused(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.SERIAL_NUMBER_A SERIAL_NUMBER_A,C.VPN_NAME VPN_NAME,A.SHORT_CODE SHORT_CODE,DECODE(A.RELATION_TYPE_CODE, '20', '已用', '未用') STATE ");
        parser.addSQL("  FROM TF_F_RELATION_UU A, TF_F_USER B, TF_F_USER_VPN C ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL("   AND A.SERIAL_NUMBER_A = B.SERIAL_NUMBER ");
        parser.addSQL("   AND A.USER_ID_A = B.USER_ID ");
        parser.addSQL("   AND B.SERIAL_NUMBER = C.VPN_NO ");
        parser.addSQL("   AND A.RELATION_TYPE_CODE = '20' ");
        parser.addSQL("   AND A.END_DATE > SYSDATE ");
        parser.addSQL("   AND B.REMOVE_TAG = '0' ");
        parser.addSQL("   AND B.SERIAL_NUMBER = :SERIAL_NUMBER_A ");
        parser.addSQL("   AND LENGTH(A.SHORT_CODE) = :SHORT_TYPE ");
        parser.addSQL("   AND A.SHORT_CODE LIKE '%'||:SHORT_LENGTH ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qrySubPrdInfo(IData param, Pagination pagination, String routeId) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT p.user_id, p.product_id, p.user_id_a, p.modify_tag, p.product_mode, t.trade_id ");
        parser.addSQL("  FROM tf_b_trade_product p , tf_b_trade t ,tf_f_cust_group g  ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("   AND t.cust_id = g.cust_id ");
        parser.addSQL("   AND t.RSRV_STR10 = '1' "); // 预受理已响应BBOSS下发的标志
        parser.addSQL("   AND g.group_id = :GROUP_ID ");
        parser.addSQL("   AND g.remove_tag = '0' ");
        parser.addSQL("   AND p.trade_id = t.trade_id ");
        // 只查询子产品信息
        parser.addSQL("   AND p.user_id_a = ( SELECT p2.user_id FROM tf_b_trade_product p2 WHERE p2.trade_id = t.trade_id AND p2.product_id = :PRODUCUT_ID  )");

        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset qrySubProductInfos(IData param, String routeId) throws Exception
    {
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT t.* FROM td_b_product_comp_rela t ");
        sql.append(" WHERE  t.product_id_a = :PRODUCT_ID AND t.relation_type_code = '1' "); // relation_type_code =
        // '1'包产品关系
        return null;// Dao.qryBySql(sql, param, routeId); // j2ee move

    }

    public static IDataset qryUserDiscntInfoQuery(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a, ");
        parser.addSQL("discnt_code,spec_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date, ");
        parser.addSQL("to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date  ");
        parser.addSQL("FROM tf_f_user_discnt ");
        parser.addSQL("WHERE user_id = to_number(:USER_ID) ");
        parser.addSQL("AND partition_id = MOD(to_number(:USER_ID),10000) ");
        parser.addSQL("AND end_date > SYSDATE ");
        parser.addSQL("ORDER BY start_date ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryUserInfoBySerialnumber(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select a.user_id,a.product_id,a.brand_code ");
        parser.addSQL("FROM tf_f_user a ");
        parser.addSQL("WHERE a.serial_number = :SERIAL_NUMBER ");
        parser.addSQL("AND a.remove_tag = '0' ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryUserMerchInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT m.* ");
        parser.addSQL("  FROM TF_F_USER_GRP_MERCH m , tf_f_user u ,tf_f_cust_group g  ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("   AND u.cust_id = g.cust_id ");
        parser.addSQL("   AND u.remove_tag = '0' ");
        parser.addSQL("   AND m.user_id = u.user_id ");
        parser.addSQL("   AND m.status = 'A' ");
        parser.addSQL("   AND g.group_id = :GROUP_ID ");
        parser.addSQL("   AND g.remove_tag = '0' ");
        parser.addSQL("   AND u.product_id = :PRODUCT_ID ");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryUserOrderRelationInfoA(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT gp.group_id,gp.cust_name,g.user_id,g.serial_number,g.biz_type_code,g.biz_code,g.biz_name, ");
        parser.addSQL("       g.access_mode,g.access_number,g.biz_state_code,g.price,g.billing_type, ");
        parser.addSQL("       g.biz_pri,g.biz_status,g.biz_attr,g.usage_desc,g.intro_url, ");
        parser.addSQL("       g.start_date start_date,g.end_date,g.first_date,g.remark ");
        parser.addSQL("  FROM tf_f_user_grp_platsvc g,tf_f_user t,tf_f_cust_group gp ");
        parser.addSQL(" WHERE g.user_id=t.user_id and t.partition_id=mod(g.user_id,10000)");
        parser.addSQL("   and t.remove_tag='0' and gp.remove_tag='0' and gp.group_id = g.group_id and t.cust_id = gp.cust_id ");
        parser.addSQL("   and t.serial_number = :SERIAL_NUMBER ");
        // parser.addSQL("   AND g.biz_state_code= :BIZ_STATE_CODE ");
        // parser.addSQL("   and g.end_date > sysdate ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryUserOrderRelationInfoB(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT gp.group_id,gp.cust_name,a.user_id,a.serial_number,biz_type_code,a.biz_code,a.biz_name,access_mode, ");
        parser.addSQL("       access_number,biz_state_code,price,billing_type,biz_pri,biz_status, ");
        parser.addSQL("       biz_attr,usage_desc,intro_url,a.start_date,a.end_date,a.first_date,a.remark ");
        parser.addSQL("  FROM tf_f_user_grp_platsvc a, tf_f_user_grp_meb_platsvc b,tf_f_user t ,tf_f_cust_group gp,tf_f_user gu ");
        parser.addSQL(" WHERE b.user_id = t.user_id AND t.partition_id = mod(b.user_id,10000) ");
        parser.addSQL("   AND a.user_id = b.ec_user_id and a.user_id = gu.user_id and a.partition_id = gu.partition_id and gu.brand_code='ADCG' and gu.remove_tag='0'");
        parser.addSQL("   and t.serial_number = :SERIAL_NUMBER ");
        parser.addSQL("   and t.remove_tag = '0' and gp.remove_tag ='0' and gp.group_id = a.group_id and b.end_date>sysdate ");
        // parser.addSQL("   AND a.biz_code = b.biz_code ");
        // parser.addSQL("   AND b.end_date > SYSDATE ");
        // parser.addSQL("   AND a.end_date > SYSDATE ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryUserPrdByGrpId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT u.partition_id,u.user_id,p.group_id,u.serial_number,p.audit_state,to_char(u.open_date,'yyyy-mm-dd hh24:mi:ss') uopen_date, ");
        parser.addSQL("        p.cust_name,u.open_date,t.product_id,t.product_name,t.start_date,t.end_date ");
        parser.addSQL(" FROM   tf_f_user u,tf_f_cust_group p,td_b_product t ");
        parser.addSQL(" WHERE  u.cust_id=p.cust_id ");
        parser.addSQL("        AND u.product_id=t.product_id ");
        parser.addSQL("        AND u.open_date < t.end_date ");
        parser.addSQL("        AND t.start_date+0<sysdate AND t.end_date+0>=sysdate ");
        parser.addSQL("        AND p.group_id= :GROUP_ID");
        parser.addSQL("        AND u.remove_tag='0' ");

        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }

    public static String qryUsrIdBySer(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select c.user_id ");
        parser.addSQL(" from tf_f_user c,tf_f_relation_uu uu  ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL("     and c.user_id=uu.user_id_a ");
        parser.addSQL("     and uu.relation_type_code in ('21','25') ");
        parser.addSQL("     and c.serial_number=:SERIAL_NUMBER ");
        parser.addSQL("     and c.remove_tag='0' ");
        parser.addSQL("     and c.user_state_codeset='0' ");

        IDataset ids = Dao.qryByParse(parser);
        IData dt = ids.size() > 0 ? ids.getData(0) : new DataMap();
        return dt.size() > 0 ? dt.getString("USER_ID") : "";
    }

    public static IDataset qryUUnVPN(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT uu.serial_number_b                                      ");
        parser.addSQL(" FROM tf_f_user_vpn vpn,tf_f_relation_uu uu                     ");
        parser.addSQL(" WHERE  vpn.USER_ID=uu.user_id_b                                ");
        parser.addSQL(" AND uu.user_id_a = to_number( :USER_ID_A )               ");
        parser.addSQL(" and vpn.PARTITION_ID=uu.partition_id                           ");
        parser.addSQL(" AND uu.relation_type_code = '40'                               ");
        parser.addSQL(" AND uu.end_date>SYSDATE                                        ");
        parser.addSQL(" and vpn.Remove_Tag <> '1'                                     ");
        return Dao.qryByParse(parser, pagination);
    }

    /*
     * by wenjb 2010-1-21
     */
    public static IDataset qryVpmnByName(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT b.user_id ");
        parser.addSQL(" FROM tf_f_user_vpn b ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("       AND b.vpn_name like '%'||:VPN_NAME||'%'  ");
        parser.addSQL("       AND b.remove_tag='0'  ");
        parser.addSQL("       AND b.partition_id=mod(b.user_id,10000)  ");
        return Dao.qryByParse(parser);
    }

    /*
     * by wenjb 2010-1-21
     */
    public static IDataset qryVpmnBySer(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        // if("0".equals(param.getString("QueryModeVPMN"))) {
        // // selStr=" AND b.vpn_name like '%'||:VPN_NAME||'%' ";
        // selStr=" AND b.vpn_name =:VPN_NAME ";
        // }else if("1".equals(param.getString("QueryModeVPMN"))){
        // selStr=" AND a.serial_number=:SERIAL_NUMBER ";
        // }else{
        // selStr=" AND a.serial_number='' ";
        // }
        parser.addSQL("SELECT c.user_id_a,c.serial_number_a, ");
        parser.addSQL("       b.vpn_name , a.brand_code ,a.city_code ,  ");
        parser.addSQL("       TO_CHAR(a.open_date,'yyyy-mm-dd hh24:mi:ss') open_date, ");
        parser.addSQL("       TO_CHAR(a.destroy_time,'yyyy-mm-dd hh24:mi:ss') destroy_time, ");
        parser.addSQL("       SUM(CASE WHEN c.end_date < sysdate THEN 0 ELSE 1 END) cnt_normal,");
        parser.addSQL("       SUM(CASE WHEN c.end_date < sysdate THEN 1 ELSE 0 END) cnt_destory");
        parser.addSQL(" FROM tf_f_user_vpn b,tf_f_user a,tf_f_relation_uu c ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("       AND a.serial_number=:SERIAL_NUMBER  ");
        parser.addSQL("       AND a.user_id=:USER_ID  ");
        parser.addSQL("       AND b.user_id=a.user_id AND b.vpmn_type in ('0','2','3') ");
        parser.addSQL("       AND a.user_id=c.user_id_a AND c.relation_type_code in ('21','25') ");
        parser.addSQL("       AND a.remove_tag='0'  ");
        parser.addSQL("       AND c.partition_id=mod(c.user_id_b,10000)  ");
        parser.addSQL("       AND c.start_date<c.end_date  ");
        parser.addSQL(" GROUP BY c.user_id_a,c.serial_number_a,b.vpn_name,a.brand_code, ");
        parser.addSQL("          a.city_code,a.open_date,a.destroy_time ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryVpmnInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT distinct(a.user_id),c.vpn_no,c.vpn_name,b.cust_name,");
        parser.addSQL(" a.serial_number, d.short_code, to_char(d.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,");
        parser.addSQL(" to_char(d.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,'' PRODFLAG, d.role_code_b ");
        parser.addSQL(" FROM tf_f_user a, tf_f_customer b, TF_F_USER_VPN c, tf_f_relation_uu d ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND a.user_id = d.user_id_b ");
        parser.addSQL(" AND c.user_id = d.user_id_a ");
        parser.addSQL(" AND a.cust_id = b.cust_id ");
        parser.addSQL(" AND c.partition_id = mod(d.user_id_a,10000) ");
        parser.addSQL(" AND a.partition_id=mod(d.user_id_b,10000) ");
        parser.addSQL(" AND b.partition_id=mod(a.cust_id,10000) ");
        parser.addSQL(" AND D.start_date<D.end_date ");
        parser.addSQL(" AND D.relation_type_code = '20' ");
        parser.addSQL(" AND d.user_id_a=:USER_ID ");
        parser.addSQL(" AND c.vpn_no=:VPN_NO");
        parser.addSQL(" AND (( '0' = :REMOVE_TAG and  d.end_date>sysdate ) or ('1' = :REMOVE_TAG and  d.end_date<sysdate )) ");

        IDataset dataset = Dao.qryByParse(parser, pagination);
        if (IDataUtil.isNotEmpty(dataset))
        {
        	//判断是否敏感信息集团
        	boolean isDataFuzzy = false;
        	IDataset extInfos = qryGrpSecretInfo(param);
        	if(extInfos!=null && extInfos.size()>0){
        		IData extInfo = extInfos.getData(0);
        		String secretFlag = extInfo.getString("SECRETGRP", "");
        		//是敏感集团则要判断登录用户是否集团的分管经理,不是则要做模糊化处理
        		if("1".equals(secretFlag)){
        			String custManagerId = extInfo.getString("CUST_MANAGER_ID");
        			if(!CSBizBean.getVisit().getStaffId().equals(custManagerId)){
        				isDataFuzzy = true;
        			}
        		}
        	}
        	
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = dataset.getData(i);
                String relaTypeCode = "20";
                String roleCodeB = data.getString("ROLE_CODE_B");
                String roleName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_RELATION_ROLE", new String[]
                { "RELATION_TYPE_CODE", "ROLE_CODE_B" }, "ROLE_B", new String[]
                { relaTypeCode, roleCodeB });
                data.put("ROLE_B", roleName);
                //VPMN名称,用户名称做模糊化处理
                if(isDataFuzzy){
                	data.put("VPN_NAME", DataFuzzy.fuzzyAll(data.getString("VPN_NAME")));	//VPMN名称
                	data.put("CUST_NAME", DataFuzzy.fuzzyAll(data.getString("CUST_NAME")));	//用户名称
                }
            }
        }
        return dataset;
    }

    public static IDataset qryVpmnInfoExport(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT distinct(a.user_id),c.vpn_no,c.vpn_name,b.cust_name,");
        parser.addSQL(" a.serial_number, d.short_code, to_char(d.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,");
        parser.addSQL(" to_char(d.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,'' PRODFLAG, d.role_code_b ");
        parser.addSQL(" FROM tf_f_user a, tf_f_customer b, TF_F_USER_VPN c, tf_f_relation_uu d ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND a.user_id = d.user_id_b  ");
        parser.addSQL(" AND c.user_id = d.user_id_a ");
        parser.addSQL(" AND a.cust_id = b.cust_id ");
        parser.addSQL(" AND c.partition_id = mod(d.user_id_a,10000) ");
        parser.addSQL(" AND a.partition_id=mod(d.user_id_b,10000) ");
        parser.addSQL(" AND b.partition_id=mod(a.cust_id,10000) ");
        parser.addSQL(" AND D.start_date<D.end_date ");
        parser.addSQL(" AND D.relation_type_code = '20' ");
        parser.addSQL(" AND d.user_id_a=:USER_ID ");
        parser.addSQL(" AND c.vpn_no=:VPN_NO");
        // parser.addSQL(" AND (( '0' = :REMOVE_TAG and d.end_date>sysdate ) or ('1' = :REMOVE_TAG and d.end_date<sysdate )) ");
        parser.addSQL(" AND (( '0' = :REMOVE_TAG and a.remove_tag=:REMOVE_TAG and d.end_date>sysdate ) or ('1' = :REMOVE_TAG and a.remove_tag=:REMOVE_TAG and d.end_date<sysdate )) ");

        IDataset dataset = Dao.qryByParse(parser, pagination, Route.getCrmDefaultDb());
        if (IDataUtil.isNotEmpty(dataset))
        {
        	//判断是否敏感信息集团
        	boolean isDataFuzzy = false;
        	IDataset extInfos = qryGrpSecretInfo(param);
        	if(extInfos!=null && extInfos.size()>0){
        		IData extInfo = extInfos.getData(0);
        		String secretFlag = extInfo.getString("SECRETGRP", "");
        		//是敏感集团则要判断登录用户是否集团的分管经理,不是则要做模糊化处理
        		if("1".equals(secretFlag)){
        			String custManagerId = extInfo.getString("CUST_MANAGER_ID");
        			if(!CSBizBean.getVisit().getStaffId().equals(custManagerId)){
        				isDataFuzzy = true;
        			}
        		}
        	}
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = dataset.getData(i);
                String relaTypeCode = "20";
                String roleCodeB = data.getString("ROLE_CODE_B");
                String roleName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_RELATION_ROLE", new String[]
                { "RELATION_TYPE_CODE", "ROLE_CODE_B" }, "ROLE_B", new String[]
                { relaTypeCode, roleCodeB });
                data.put("ROLE_B", roleName);
                //VPMN名称,用户名称做模糊化处理
                if(isDataFuzzy){
                	data.put("VPN_NAME", DataFuzzy.fuzzyAll(data.getString("VPN_NAME")));	//VPMN名称
                	data.put("CUST_NAME", DataFuzzy.fuzzyAll(data.getString("CUST_NAME")));	//用户名称
                }
            }
        }
        return dataset;
    }

    public static IDataset qryVpmnInfoByMemSer(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT a.user_id_a ,b.serial_number,c.cust_name ,b.brand_code ");
        parser.addSQL(" FROM tf_f_relation_uu a,tf_f_user b,tf_f_customer c ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL("  AND a.relation_type_code in ('21') ");
        parser.addSQL(" AND b.serial_number=:SERIAL_NUMBER ");
        parser.addSQL("  AND b.cust_id=c.cust_id ");
        parser.addSQL("  AND b.remove_tag='0' ");
        parser.addSQL("  AND a.user_id_b=b.user_id ");
        parser.addSQL(" AND a.partition_id=mod(b.user_id,10000) ");
        parser.addSQL(" AND c.partition_id=mod(b.cust_id,10000) ");
        parser.addSQL(" AND a.end_date > last_day(trunc(sysdate)) + 1 - 1 / 24 / 3600 ");
        // parser.addSQL(" AND a.end_date>sysdate ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryVpmnInfoRelation(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT a.user_id_a");
        parser.addSQL(" FROM tf_f_relation_uu a,tf_f_user b ");
        parser.addSQL(" WHERE remove_tag='0'");
        parser.addSQL("  AND a.user_id_b=b.user_id ");
        parser.addSQL("  AND a.partition_id=mod(b.user_id,10000) ");
        parser.addSQL("  AND b.partition_id=mod(b.user_id,10000) ");
        parser.addSQL("  AND a.start_date<a.end_date ");
        parser.addSQL(" AND a.relation_type_code = '20'");
        // parser.addSQL(" AND b.serial_number=:SERIAL_NUMBER  ");
        parser.addSQL(" AND b.user_id=:MEB_USER_ID  ");
        parser.addSQL(" AND a.end_date > sysdate  ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryVpmnInfoRelationByRouteId(IData param, Pagination pagination, String routeId) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT a.user_id_a");
        parser.addSQL(" FROM tf_f_relation_uu a,tf_f_user b ");
        parser.addSQL(" WHERE remove_tag='0'");
        parser.addSQL("  AND a.user_id_b=b.user_id ");
        parser.addSQL("  AND a.partition_id=mod(b.user_id,10000) ");
        parser.addSQL("  AND b.partition_id=mod(b.user_id,10000) ");
        parser.addSQL("  AND a.start_date<a.end_date ");
        parser.addSQL(" AND a.relation_type_code = '20'");
        // parser.addSQL(" AND b.serial_number=:SERIAL_NUMBER  ");
        parser.addSQL(" AND b.user_id=:MEB_USER_ID  ");
        parser.addSQL(" AND a.end_date > sysdate  ");
        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset qryVpmnSaleActive(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT SERIAL_NUMBER_B,RSRV_STR1,RSRV_STR2,RSRV_DATE2,");
        parser.addSQL("     DECODE(GIVE_TAG, '1', '已赠送', '0', '未赠送') GIVE_TAG,GIVE_DATE,UPDATE_STAFF_ID,UPDATE_DEPART_ID");
        parser.addSQL("  FROM TF_F_VPMNACTIVE_RELATION ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL("   AND USER_ID_A = :USER_ID_A ");
        parser.addSQL("   AND ACTIVE_TYPE = :ACTIVE_TYPE ");
        parser.addSQL("   AND END_DATE > SYSDATE ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryVPMNScpInfo(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT v.vpn_no vpn_no,v.scp_code scp_code");
        parser.addSQL(" FROM   tf_f_user_vpn v ");
        parser.addSQL(" WHERE  v.user_id = TO_NUMBER(:USER_ID)");

        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }

    public static IDataset qryVpnMemShrtCod(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT b.short_code short_code ");
        parser.addSQL(" FROM tf_f_user_vpn v,tf_f_user_vpn_meb b ");
        parser.addSQL(" WHERE v.user_id = b.user_id_a  ");
        parser.addSQL("       AND v.vpn_no= :VPN_NO ");
        parser.addSQL("       AND b.serial_number = :SERIAL_NUMBER_B ");
        parser.addSQL("       AND b.remove_tag='0' ");

        return Dao.qryByParse(parser);

    }

    public static IDataset queryByUserid(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select a.attr_lable ,b.attr_value ");
        parser.addSQL(" from td_b_attr_itema  a ,tf_f_user_attr b  ");
        parser.addSQL(" where a.attr_code=b.attr_code ");
        parser.addSQL(" and b.user_id=:USER_ID ");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 集团专线产品查询
     */
    public static IDataset queryGroupSpecialLineProductCodeList(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM td_b_product p  ");
        parser.addSQL(" WHERE 1=1  ");
        parser.addSQL(" AND p.brand_code='OG01'  ");
        parser.addSQL(" AND p.product_name LIKE '%专线' ");
        parser.addSQL(" AND p.release_tag='1' ");
        parser.addSQL(" ORDER BY product_id ");
        return Dao.qryByParse(parser);
    }

    /**
     * 功能描述：根据条件查询集团订购关系的基本信息；
     * 
     * @author zhangbaoshi 功能
     * @输入参数： GROUP_ID 集团编码 PRODUCT_ID 集团产品编码 BIZ_CODE 集团服务代码 RB_LIST 集团订购关系类型
     * @输出参数：符合条件的集团订购关系信息
     */
    public static IDataset querySpecialLineParamInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT                                                              ");
        parser.addSQL(" '0' X_TAG,                                                          ");
        parser.addSQL(" o.RSRV_VALUE OPER_TAG,                                              ");
        parser.addSQL(" o.RSRV_STR1 NET_LINE_CODE,                                          ");
        parser.addSQL(" o.RSRV_STR2 NET_LINE,                                               ");
        parser.addSQL(" o.RSRV_STR3 PRICE_LINE,                                             ");
        parser.addSQL(" to_char(o.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,           ");
        parser.addSQL(" to_char(o.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,               ");
        parser.addSQL(" o.RSRV_STR4 REMARK,                                                 ");
        parser.addSQL(" o.RSRV_STR5 OLD_OPER_TAG,                                           ");
        parser.addSQL(" o.INST_ID INST_ID,                                                  ");
        parser.addSQL(" p.product_name                                                      ");
        parser.addSQL(" FROM tf_f_user_other o,tf_f_cust_group g,tf_f_user u,td_b_product p ");
        parser.addSQL(" WHERE u.cust_id=g.cust_id                                           ");
        parser.addSQL(" AND u.user_id=o.user_id                                             ");
        parser.addSQL(" AND u.product_id=p.product_id                                       ");
        parser.addSQL(" AND g.group_id=:GROUP_ID                                            ");
        parser.addSQL(" AND u.product_id=:PRODUCT_ID                                        ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset serANeedCheckRight(IData dt) throws Exception
    {
        SQLParser parser = new SQLParser(dt);

        parser.addSQL(" SELECT param_attr,param_code,param_name,para_code1,para_code2 ");
        parser.addSQL(" FROM td_s_commpara ");
        parser.addSQL(" WHERE (  ");
        parser.addSQL("        subsys_code='CSM'  ");
        parser.addSQL("        AND param_attr=:PARAM_ATTR ");
        parser.addSQL("        AND para_code1=:VPN_NO  ");
        parser.addSQL("        AND sysdate BETWEEN start_date AND end_date ");
        parser.addSQL("                            )  ");
        parser.addSQL("        AND (");
        parser.addSQL("             eparchy_code=:EPARCHY_CODE ");
        parser.addSQL("             OR eparchy_code='ZZZZ' ");
        parser.addSQL("             ) ");

        return Dao.qryByParse(parser);
    }

    public static IDataset SuperqryShortCodeByVpmn(IData param, Pagination pagination, int common, int mother) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT d.short_code,decode(e.SHORT_CODE,d.short_code,'已用', '未用') STATE                             ");
        parser.addSQL("  FROM (select *from td_b_vshortcode                                                             ");
        parser.addSQL("  where 1 = 1                                         ");
        parser.addSQL("  and (contain_4= :contain_4 OR :contain_4 IS NULL)                                               ");
        parser.addSQL("  and (contain_7= :contain_7 OR :contain_7 IS NULL)                                                ");
        parser.addSQL("   and (SHORT_CODE LIKE '6'||:SHORT_LENGTH ||'%' OR :SHORT_LENGTH IS NULL)");
        parser.addSQL("  and (length = :SHORT_TYPE OR :SHORT_TYPE IS NULL)                                                     ");
        parser.addSQL("   )d                                                     ");
        parser.addSQL("  left join (select a.* from TF_F_RELATION_UU A , TF_F_USER B                  ");
        parser.addSQL(" where 1 = 1                                                                                     ");
        parser.addSQL("   AND A.SERIAL_NUMBER_A = B.SERIAL_NUMBER                                                       ");
        parser.addSQL("   AND A.USER_ID_A = B.USER_ID                                                                   ");
        // parser.addSQL("   AND B.SERIAL_NUMBER = C.VPN_NO                                                                ");
        parser.addSQL("   AND exists(select 1 from TF_F_USER_VPN C where B.SERIAL_NUMBER = C.VPN_NO)                                                               ");
        parser.addSQL("   AND A.RELATION_TYPE_CODE = '20'                                                               ");
        parser.addSQL("   AND A.END_DATE > SYSDATE                                                                      ");
        parser.addSQL("   AND B.REMOVE_TAG = '0'                                                                        ");
        parser.addSQL("   AND (B.SERIAL_NUMBER = :SERIAL_NUMBER_A OR :SERIAL_NUMBER_A IS NULL)) e ");
        parser.addSQL("   on d.short_code=e.short_code");
        // parser.addSQL("   order by e.short_code                                                  ");

        SQLParser parser1 = new SQLParser(param);
        parser1.addParser(parser);
        return Dao.qryByParse(parser, pagination);

    }

    public static IDataset getUserInfo(IData inparam) throws Exception
    {
        return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCTID", inparam);
    }

    public static IDataset getUserGrpPackageByUserId(IData inparam) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_USERID_NOW", inparam);
    }

    public static IDataset getUserRealtionUU(IData inparam) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_PK", inparam);
    }

    public static IDataset getPackageElement(IData inparam) throws Exception
    {
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PK", inparam);
    }
    
    /**
     * 查询敏感集团标识信息
     * @param param
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-5-8
     */
    public static IDataset qryGrpSecretInfo(IData param) throws Exception
    {
    	SQLParser parser = new SQLParser(param);
    	parser.addSQL("SELECT (SELECT /*+index(ext IDX_TF_F_CUST_GROUP_EXTEND_VT)*/EXT.RSRV_NUM1");
    	parser.addSQL("    	          FROM TF_F_CUST_GROUP_EXTEND EXT");
    	parser.addSQL("    	         WHERE EXT.EXTEND_VALUE = B.CUST_ID");
    	parser.addSQL("    	           AND EXT.EXTEND_TAG = 'SECRETGRP' AND ROWNUM=1) SECRETGRP,");
    	parser.addSQL("    	       B.*");
    	parser.addSQL("    	  FROM TF_F_USER A, TF_F_CUST_GROUP B");
    	parser.addSQL("    	 WHERE A.CUST_ID = B.CUST_ID");
    	parser.addSQL("    	   AND A.USER_ID = :USER_ID");
    	parser.addSQL("    	   AND A.REMOVE_TAG = '0'");
    	parser.addSQL("    	   AND B.REMOVE_TAG = '0'");
    	return Dao.qryByParse(parser, Route.getCrmDefaultDb());
    }

	public static IDataset qryGrpVpmnSerialNumber(IData inparam) throws Exception {
		SQLParser parser = new SQLParser(inparam);
    	parser.addSQL("SELECT u1.SERIAL_NUMBER,c1.CUST_NAME FROM tf_f_user u1,Tf_f_Cust_Group c1");
    	parser.addSQL("    	 WHERE 1=1");
    	parser.addSQL("    	   and u1.cust_id = c1.cust_id");
    	parser.addSQL("    	   and u1.user_id in (");
    	parser.addSQL("    	   SELECT p.user_id FROM tf_f_user_product p");
    	parser.addSQL("    	   where 1=1");
    	parser.addSQL("    	   and p.product_id='8000'");
    	parser.addSQL("    	   and p.user_id in (");
    	parser.addSQL("    	   select u2.user_id from Tf_f_Cust_Group c2 left join tf_f_user u2 on c2.cust_id=u2.cust_id");
    	parser.addSQL("    	   where 1=1");
    	parser.addSQL("    	   and c2.group_id= :GROUP_ID");
    	parser.addSQL("    	   ))");
    	return Dao.qryByParse(parser, Route.getCrmDefaultDb());
	}
}
