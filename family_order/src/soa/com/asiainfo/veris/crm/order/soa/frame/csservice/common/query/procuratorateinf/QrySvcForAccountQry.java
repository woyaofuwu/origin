
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.procuratorateinf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QrySvcForAccountQry
{

    /**
     * 下面说明的的sql的拆分为下面两个查询. 数据量不大 按照条件查询服务 (账务管理接口查询)
     * 
     * @param data
     * @return
     * @throws Exception
     *             //查正常的 订购关系 and c.biz_state_code in ('A','N','L')"
     */
    public static IDataset selOrderRelationIng(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TF_F_USER_PLATSVC_TRACE", "SEL_USERPLATSVC_INGCOND", data);
        // return result.size()>0?result.getData(0):new DataMap();
        return result;
    }

    /**
     * 查询指定
     * 
     * @param data
     * @return
     * @throws Exception
     *             //查退订和预退订的 parser.addSQL(" and c.biz_state_code in ('E','P')");
     */
    public static IDataset selOrderRelationPre(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TF_F_USER_PLATSVC_TRACE", "SEL_USERPLATSVC_PRECOND", data);
        return result;
    }

    public static IDataset selServiceAttr(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TF_F_USER_ATTR", "SEL_ATTR_BY_SERVICEID", data);
        return result;
    }

    /***
     * 对以上几个查询方法 的说明 : SQLParser parser = newSQLParser(param);
     * parser.addSQL(" select t1.*,t2.sp_code, t2.biz_code,nvl(t2.count_sp,0) count_sp from ( "); parser.addSQL(
     * " select c.*,z.biz_name,p.service_name,m.sp_name from td_b_platsvc p,td_m_sp_biz z,td_m_sp_info m,tf_f_user_platsvc c "
     * ); parser.addSQL(" where c.partition_id = mod(:USER_ID,10000) "); parser.addSQL(" and c.user_id = :USER_ID ");
     * parser.addSQL(" and c.service_id = p.service_id "); parser.addSQL(" and c.sp_code = z.sp_code ");
     * parser.addSQL(" and c.biz_code = z.biz_code "); parser.addSQL(" and c.sp_code = m.sp_code ");
     * parser.addSQL(" and c.biz_type_code = :BIZ_TYPE_CODE "); parser.addSQL(" and c.sp_code = :SP_CODE ");
     * parser.addSQL(" and c.biz_code = :BIZ_CODE "); //REQ201307230013
     * parser.addSQL(" and to_char(c.start_date,'yyyymmdd') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'),'yyyymmdd')");
     * parser.addSQL(" and to_char(c.end_date,'yyyymmdd') >= to_char(to_date(:END_DATE, 'yyyy-mm-dd'),'yyyymmdd')");
     * parser.addSQL(" and c.oper_code = :OPER_CODE "); parser.addSQL(" and c.biz_code <> 'FXSJHYDG' ");
     * if("1".equals(param.getString("BIZ_STATE_CODE"))){ //查正常的
     * parser.addSQL(" and c.biz_state_code in ('A','N','L')"); } elseif("2".equals(param.getString("BIZ_STATE_CODE"))){
     * //查退订和预退订的 parser.addSQL(" and c.biz_state_code in ('E','P')"); } //modify by maikw20130718
     * parser.addSQL(" order by c.start_date desc )t1, ");
     * parser.addSQL(" ( select a.sp_code, a.biz_code, count(*)  count_sp  from tf_f_user_platsvc a ");
     * parser.addSQL(" where a.partition_id = mod(:USER_ID,10000) "); parser.addSQL(" and  a.user_id = :USER_ID ");
     * parser.addSQL(" and  a.oper_code = '06' "); parser.addSQL(" and  a.biz_state_code in ('A', 'E') ");
     * parser.addSQL(" and  to_number(to_char(sysdate,'yyyymm')) = to_number(to_char(a.start_date, 'yyyymm'))  group by a.sp_code, a.biz_code )t2  "
     * ); parser.addSQL(" where t1.sp_code = t2.sp_code(+) "); parser.addSQL(" and t1.biz_code = t2.biz_code(+) ");
     * returnthis.queryList(parser); 变换成 查询 a: SELECT A.PARTITION_ID, TO_CHAR(A.USER_ID) USER_ID, A.PRODUCT_ID,
     * A.PACKAGE_ID, A.SERVICE_ID, A.BIZ_STATE_CODE, A.OPER_CODE, TO_CHAR(A.FIRST_DATE, 'yyyy-mm-dd hh24:mi:ss')
     * FIRST_DATE, TO_CHAR(A.FIRST_DATE_MON, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE_MON, A.GIFT_SERIAL_NUMBER,
     * A.GIFT_USER_ID, A.START_DATE, A.END_DATE, TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
     * A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, A.RSRV_NUM1, A.RSRV_NUM2, A.RSRV_NUM3, TO_CHAR(A.RSRV_NUM4)
     * RSRV_NUM4, TO_CHAR(A.RSRV_NUM5) RSRV_NUM5, A.RSRV_STR1, A.RSRV_STR2, A.RSRV_STR3, A.RSRV_STR4, A.RSRV_STR5,
     * A.RSRV_STR6, A.RSRV_STR7, A.RSRV_STR8, A.RSRV_STR9, A.RSRV_STR10, TO_CHAR(A.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss')
     * RSRV_DATE1, TO_CHAR(A.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, TO_CHAR(A.RSRV_DATE3, 'yyyy-mm-dd
     * hh24:mi:ss') RSRV_DATE3, B.SP_CODE, B.BIZ_CODE, B.BIZ_TYPE_CODE FROM TF_F_USER_PLATSVC_TRACE A, TD_B_PLATSVC B
     * WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) AND A.USER_ID = TO_NUMBER(:USER_ID) AND A.SERVICE_ID =
     * B.SERVICE_ID AND B.SP_CODE = :SP_CODE AND B.BIZ_CODE = :BIZ_CODE AND B.BIZ_TYPE_CODE = :BIZ_TYPE_CODE AND
     * TO_CHAR(A.START_DATE, 'yyyymmdd') <= TO_CHAR(TO_DATE(:END_DATE, 'yyyy-mm-dd'), 'yyyymmdd') AND
     * TO_CHAR(A.END_DATE, 'yyyymmdd') >= TO_CHAR(TO_DATE(:END_DATE, 'yyyy-mm-dd'), 'yyyymmdd') AND A.OPER_CODE =
     * :OPER_CODE AND B.BIZ_CODE <> 'FXSJHYDG' AND B.BIZ_STATE_CODE IN ('E', 'P') ORDER BY A.START_DATE DESC 查询sql B:
     * select count(*) count_sp, m.sp_code, m.biz_code from (SELECT A.PARTITION_ID, TO_CHAR(A.USER_ID) USER_ID,
     * A.PRODUCT_ID, A.PACKAGE_ID, A.SERVICE_ID, A.BIZ_STATE_CODE, A.OPER_CODE, TO_CHAR(A.FIRST_DATE, 'yyyy-mm-dd
     * hh24:mi:ss') FIRST_DATE, TO_CHAR(A.FIRST_DATE_MON, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE_MON, A.GIFT_SERIAL_NUMBER,
     * A.GIFT_USER_ID, A.START_DATE, A.END_DATE, TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
     * A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, A.RSRV_NUM1, A.RSRV_NUM2, A.RSRV_NUM3, TO_CHAR(A.RSRV_NUM4)
     * RSRV_NUM4, TO_CHAR(A.RSRV_NUM5) RSRV_NUM5, A.RSRV_STR1, A.RSRV_STR2, A.RSRV_STR3, A.RSRV_STR4, A.RSRV_STR5,
     * A.RSRV_STR6, A.RSRV_STR7, A.RSRV_STR8, A.RSRV_STR9, A.RSRV_STR10, TO_CHAR(A.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss')
     * RSRV_DATE1, TO_CHAR(A.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, TO_CHAR(A.RSRV_DATE3, 'yyyy-mm-dd
     * hh24:mi:ss') RSRV_DATE3, B.SP_CODE, B.BIZ_CODE, B.BIZ_TYPE_CODE FROM TF_F_USER_PLATSVC_TRACE A, TD_B_PLATSVC B
     * WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) AND A.USER_ID = TO_NUMBER(:USER_ID) AND A.SERVICE_ID =
     * B.SERVICE_ID and A.oper_code = '06' and B.biz_state_code in ('A', 'E') and to_number(to_char(sysdate,'yyyymm')) =
     * to_number(to_char(m.start_date, 'yyyymm') ) m group by m.sp_code, m.biz_code 合并后 select t1.*,t2.sp_code SPCODE,
     * t2.biz_code BIZCODE, nvl(t2.count_sp,0) count_sp from ( SELECT A.PARTITION_ID, TO_CHAR(A.USER_ID) USER_ID,
     * A.PRODUCT_ID, A.PACKAGE_ID, A.SERVICE_ID, A.BIZ_STATE_CODE, A.OPER_CODE, TO_CHAR(A.FIRST_DATE, 'yyyy-mm-dd
     * hh24:mi:ss') FIRST_DATE, TO_CHAR(A.FIRST_DATE_MON, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE_MON, A.GIFT_SERIAL_NUMBER,
     * A.GIFT_USER_ID, A.START_DATE, A.END_DATE, TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
     * A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, A.RSRV_NUM1, A.RSRV_NUM2, A.RSRV_NUM3, TO_CHAR(A.RSRV_NUM4)
     * RSRV_NUM4, TO_CHAR(A.RSRV_NUM5) RSRV_NUM5, A.RSRV_STR1, A.RSRV_STR2, A.RSRV_STR3, A.RSRV_STR4, A.RSRV_STR5,
     * A.RSRV_STR6, A.RSRV_STR7, A.RSRV_STR8, A.RSRV_STR9, A.RSRV_STR10, TO_CHAR(A.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss')
     * RSRV_DATE1, TO_CHAR(A.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, TO_CHAR(A.RSRV_DATE3, 'yyyy-mm-dd
     * hh24:mi:ss') RSRV_DATE3, B.SP_CODE, B.BIZ_CODE, B.BIZ_TYPE_CODE FROM TF_F_USER_PLATSVC_TRACE A, TD_B_PLATSVC B
     * WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) AND A.USER_ID = TO_NUMBER(:USER_ID) AND A.SERVICE_ID =
     * B.SERVICE_ID AND B.SP_CODE = :SP_CODE AND B.BIZ_CODE = :BIZ_CODE AND B.BIZ_TYPE_CODE = :BIZ_TYPE_CODE AND
     * TO_CHAR(A.START_DATE, 'yyyymmdd') <= TO_CHAR(TO_DATE(:END_DATE, 'yyyy-mm-dd'), 'yyyymmdd') AND
     * TO_CHAR(A.END_DATE, 'yyyymmdd') >= TO_CHAR(TO_DATE(:END_DATE, 'yyyy-mm-dd'), 'yyyymmdd') AND A.OPER_CODE =
     * :OPER_CODE AND B.BIZ_CODE <> 'FXSJHYDG' AND B.BIZ_STATE_CODE IN ('E', 'P') ORDER BY A.START_DATE DESC ) t1,
     * (select count(*) count_sp, m.sp_code, m.biz_code from (SELECT A.PARTITION_ID, TO_CHAR(A.USER_ID) USER_ID,
     * A.PRODUCT_ID, A.PACKAGE_ID, A.SERVICE_ID, A.BIZ_STATE_CODE, A.OPER_CODE, TO_CHAR(A.FIRST_DATE, 'yyyy-mm-dd
     * hh24:mi:ss') FIRST_DATE, TO_CHAR(A.FIRST_DATE_MON, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE_MON, A.GIFT_SERIAL_NUMBER,
     * A.GIFT_USER_ID, A.START_DATE, A.END_DATE, TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
     * A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, A.RSRV_NUM1, A.RSRV_NUM2, A.RSRV_NUM3, TO_CHAR(A.RSRV_NUM4)
     * RSRV_NUM4, TO_CHAR(A.RSRV_NUM5) RSRV_NUM5, A.RSRV_STR1, A.RSRV_STR2, A.RSRV_STR3, A.RSRV_STR4, A.RSRV_STR5,
     * A.RSRV_STR6, A.RSRV_STR7, A.RSRV_STR8, A.RSRV_STR9, A.RSRV_STR10, TO_CHAR(A.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss')
     * RSRV_DATE1, TO_CHAR(A.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, TO_CHAR(A.RSRV_DATE3, 'yyyy-mm-dd
     * hh24:mi:ss') RSRV_DATE3, B.SP_CODE, B.BIZ_CODE, B.BIZ_TYPE_CODE FROM TF_F_USER_PLATSVC_TRACE A, TD_B_PLATSVC B
     * WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) AND A.USER_ID = TO_NUMBER(:USER_ID) AND A.SERVICE_ID =
     * B.SERVICE_ID and A.oper_code = '06' and B.biz_state_code in ('A', 'E') and to_number(to_char(sysdate,'yyyymm')) =
     * to_number(to_char(m.start_date, 'yyyymm') ) m group by m.sp_code, m.biz_code) t2 where t1.sp_code = t2.sp_code(+)
     * and t1.biz_code = t2.biz_code(+)
     */
}
