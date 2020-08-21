
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.Qry360UserViewException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserCommUtil;

public class Qry360InfoDAO extends CSBizBean
{
    /**
     * 根据权限返回SIM,没有权限的后六位显示为"******"
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getSimbyPurview(IData data, String context, Pagination pagination) throws Exception
    {
        if (context == null || context.length() == 0)
            return "";
        String staffId = CSBizBean.getVisit().getStaffId();
        String rightCode = "CSM_SIM_SHOWALL";

        if (StaffPrivUtil.isFuncDataPriv(staffId, rightCode))
        {
            return context;
        }
        // data.put("data_code", rightCode);
        // SQLParser parser = new SQLParser(data);
        // parser.addSQL(" select 1 from td_m_dataright ");
        // parser.addSQL(" where data_code= :data_code");
        // IDataset ds = UserCommUtil.qryByParse(parser, pagination,Route.CONN_SYS);
        // if (ds.size() == 0)
        // { // 没有定义，不控制权限
        // return context;
        // }
        // data.put("staffId", staffId);
        // data.put("rightCode", rightCode);
        // SQLParser sqlParser = new SQLParser(data);
        // sqlParser.addSQL("select data_code,right_class,oper_special ");
        // sqlParser.addSQL(" from tf_m_staffdataright where data_type<'2' and right_attr='0' ");
        // sqlParser.addSQL(" and right_tag='1' and (rsvalue1 is null or rsvalue1<>'1') ");
        // sqlParser.addSQL(" and staff_id= :staffId and data_code= :rightCode ");
        // sqlParser.addSQL(" union ");
        // sqlParser.addSQL(" select data_code,right_class,oper_special ");
        // sqlParser.addSQL(" from tf_m_roledataright where role_code in(");
        // sqlParser.addSQL(" select data_code from tf_m_staffdataright ");
        // sqlParser.addSQL(" where data_type<'2' and right_attr='1' and right_tag='1' ");
        // sqlParser.addSQL(" and staff_id= :staffId) and (rsvalue1 is null or rsvalue1<>'1') and data_code= :rightCode ");
        // sqlParser.addSQL(" minus");
        // sqlParser.addSQL(" select data_code,right_class,oper_special ");
        // sqlParser.addSQL(" from tf_m_staffdataright where data_type<'2' and right_attr='0' ");
        // sqlParser.addSQL(" and right_tag='0' and (rsvalue1 is null or rsvalue1<>'1') ");
        // sqlParser.addSQL(" and staff_id= :staffId and data_code= :rightCode ");
        // ds = UserCommUtil.qryByParse(sqlParser, pagination);
        // if (ds.size() > 0)
        // return context;
        int clen = context.length();
        return context.substring(0, clen > 6 ? clen - 6 : 0) + "******";
    }

    public static IDataset getUserWidenetInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_USER_WIDENET_INFO", param);
    }

    /**
     * 查询用户新接触信息
     *
     * @throws Exception
     */
    public static IDataset qryNewContactMgr(IData data, Pagination pagination) throws Exception
    {

        String sYear = "";
        String sTouth = "";
        String eYear = "";
        String eTouth = "";
        if (!data.getString("START_DATE", "").equals(""))
        {
            sYear = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("START_DATE"), "yyyy-MM-dd"), "yyyy");
            sTouth = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("START_DATE"), "yyyy-MM-dd"), "MMdd");
        }
        if (!data.getString("END_DATE", "").equals(""))
        {
            eYear = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("END_DATE"), "yyyy-MM-dd"), "yyyy");
            eTouth = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("END_DATE"), "yyyy-MM-dd"), "MMdd");
        }
        data.put("TOUCH_DAY_S", sTouth);
        data.put("TOUCH_DAY_E", eTouth);
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        if ("".equals(serialNumber))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select  a.touch_id,A.TOUCH_DAY,A.TOUCH_TIME,A.SN from TL_B_CUST_TOUCH a");
        parser.addSQL(" where 1=1   ");
        parser.addSQL(" and a.SN = :SERIAL_NUMBER ");
        if (sYear.equals(eYear))
        {
            parser.addSQL(" and TOUCH_DAY >=  :TOUCH_DAY_S ");
            parser.addSQL(" and TOUCH_DAY <   :TOUCH_DAY_E");
        }
        IDataset outlist = UserCommUtil.qryByParse(parser, pagination, Route.CONN_LOG);
        return outlist;
    }

    /**
     * 查询用户新接触信息
     *
     * @throws Exception
     */
    public static IDataset qryNewContactTrac(String serialNumber, String touchDay, String touchid, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("TOUCH_DAY", touchDay);
        data.put("TOUCH_ID", touchid);
        if ("".equals(serialNumber))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select a.TOUCH_ID , A.TOUCH_DAY,A.TOUCH_TIME,A.SN,A.SVC_NAME,A.IN_MODE_CODE,A.STAFF_ID,A.DEPART_ID , A.REMARK from TL_B_CUST_TOUCH_TRACE A ");
        parser.addSQL(" where 1=1   ");
        parser.addSQL(" and a.TOUCH_DAY = :TOUCH_DAY ");
        parser.addSQL(" and a.SN = :SERIAL_NUMBER ");
        parser.addSQL(" and a.TOUCH_ID = :TOUCH_ID ");
        parser.addSQL("  order by  a.TOUCH_TIME desc ");
        IDataset outlist = Dao.qryByParse(parser, pagination, Route.CONN_LOG);
        return outlist;
    }

    public static IDataset qryNpUserInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT USER_ID,NP_SERVICE_TYPE,SERIAL_NUMBER,PORT_OUT_NETID,PORT_IN_NETID, ");
        parser.addSQL("HOME_NETID,B_NP_CARD_TYPE,A_NP_CARD_TYPE,NP_TAG,APPLY_DATE,NP_DESTROY_TIME, ");
        parser.addSQL("PORT_IN_DATE,PORT_OUT_DATE,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5 ");
        parser.addSQL("FROM TF_F_USER_NP WHERE 1=1 ");
        parser.addSQL("AND USER_ID = :USER_ID ");
        parser.addSQL("AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        return Dao.qryByParse(parser);
    }

   public static IDataset qryTHCustomerContactInfo(IData param, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(param);
        SQLParser parser4Ncc = new SQLParser(param);
        IData sumData = new DataMap();
        String sqlTime = null;
        if(param.getString("l","").length() > 6){
        	sqlTime = param.getString("l","").substring(0,6);
        }else{
        	Date date = new Date();
        	SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
        	sqlTime = fmt.format(date);
        }

        parser.addSQL(" select t.call_serial_no,  ");
        parser.addSQL("       t.start_date,  ");
        parser.addSQL("       t.staff_id,  ");
        parser.addSQL("       t.serial_no,  ");
        parser.addSQL("       t.call_serial_main,  ");
        parser.addSQL("       t.call_serial_pass,  ");
        parser.addSQL("       decode(t.call_mode,'0','普通呼入','1','主动外呼','2','主动外呼','3','预测外呼') call_mode ");
        parser.addSQL("  from tf_bh_staff_callinfo t where t.serial_no= :SERIAL_NUMBER ");
        parser.addSQL(" order by t.start_date desc ");
        //IDataset result = Dao.qryByParse(parser, Route.CONN_CC);

        parser4Ncc.addSQL(" select t.CALL_ID CALL_SERIAL_NO,  ");
        parser4Ncc.addSQL("       t.START_TIME START_DATE,  ");
        parser4Ncc.addSQL("       t1.CODE STAFF_ID,  ");

        parser4Ncc.addSQL("       t.SERVICE_NO SERIAL_NO,  ");
        parser4Ncc.addSQL("       t.CALLER_NO CALL_SERIAL_MAIN,  ");
        parser4Ncc.addSQL("       t.CALLED_NO CALL_SERIAL_PASS,  ");
        parser4Ncc.addSQL("       t2.CODE_NAME CALL_MODE  ");
        parser4Ncc.addSQL("  from ct_tele_"+sqlTime+" t,SEC_OPERATOR t1,BS_STATIC_DATA t2  ");
        parser4Ncc.addSQL("  where t.SERVICE_NO = :SERIAL_NUMBER ");
        parser4Ncc.addSQL("  and t2.code_type = 'CS_CT_TELE@CALL_TYPE' ");
        parser4Ncc.addSQL("  and t2.code_value = t.call_type ");
        parser4Ncc.addSQL("  and t.op_id = t1.operator_id ");
        parser4Ncc.addSQL(" order by t.START_TIME desc ");
        IDataset result4Ncc = Dao.qryByParse(parser4Ncc, "ncc");

        //result4Ncc.addAll(result);
        DataHelper.sort(result4Ncc, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        return result4Ncc;
    }

    public static IDataset qryTHRelaTradeInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        SQLParser parser4Ncc = new SQLParser(param);
        IData sumData = new DataMap();

        parser.addSQL(" select a.accept_phone_code, ");
        parser.addSQL("       a.accept_time, ");
        parser.addSQL("       a.content, ");
        parser.addSQL("       c.data_name trade_status, ");
        parser.addSQL("       a.workform_id, ");
        parser.addSQL("       b.remark , ");
        parser.addSQL("       a.accept_staff_id, ");
        parser.addSQL("       a.cust_name, ");
        parser.addSQL("       d.data_name, ");
        parser.addSQL("       e.data_name urgencydegre ");
        parser.addSQL("  from tf_f_workform a, ");
        parser.addSQL("       td_b_tradetype b, ");
        parser.addSQL("       (select * from td_s_static_param where type_id = 'WORKFORM_STATE') c, ");
        parser.addSQL("       (select * from td_s_static_param where type_id = 'TD_B_CUST_LEVLE') d, ");
        parser.addSQL("       (select * from td_s_static_param  where type_id= 'TD_B_URGENCY_DEGREE') e     ");
        parser.addSQL(" where a.trade_type_code = b.trade_type_code(+) ");
        parser.addSQL("   and a.workform_state = c.data_id(+) ");
        parser.addSQL("   and a.cust_class = d.data_id(+) ");
        parser.addSQL("   and a.deal_class_code = e.data_id(+)");
        parser.addSQL("   and a.call_phone_code = :SERIAL_NUMBER");
        parser.addSQL("   order by a.accept_time desc ");
        //IDataset result = Dao.qryByParse(parser, Route.CONN_CC);

        parser4Ncc.addSQL(" select * ");
        parser4Ncc.addSQL("  from (SELECT A.SUBS_NUMBER ACCEPT_PHONE_CODE, ");
        parser4Ncc.addSQL("               A.ACCEPT_TIME,  ");
        parser4Ncc.addSQL("               A.SERVICE_CONTENT CONTENT, ");
        parser4Ncc.addSQL("       		  decode(A.PROCESS_STATE,'1','删除','2','归档','3','初始化','4','活动','5','暂归档','7','销单') TRADE_STATUS, ");
        parser4Ncc.addSQL("       		  A.SHOW_SERIAL_NO WORKFORM_ID, ");
        parser4Ncc.addSQL("       	 	  B.FULL_NAME REMARK, ");
        parser4Ncc.addSQL("       		  C.CODE ACCEPT_STAFF_ID, ");
        parser4Ncc.addSQL("       		  A.SUBS_NAME CUST_NAME，");
        parser4Ncc.addSQL("       		  A.SUBS_LEVEL DATA_NAME, ");
        parser4Ncc.addSQL("         	  decode(A.URGENT_ID,'1','非常紧急','2','紧急','3','一般','4','不紧急') URGENCYDEGRE, ");
        parser4Ncc.addSQL("       		  A.SUBS_NUMBER, ");
        parser4Ncc.addSQL("       		  A.SERIAL_ID ");
        parser4Ncc.addSQL("   		FROM SR_PROBLEM_PROCESS A, SR_PROBLEM_WORK_ITEM M, SR_SERVICE_REQUEST_TYPE B, SEC_OPERATOR C");
        parser4Ncc.addSQL(" 	WHERE 1 = 1 ");
        parser4Ncc.addSQL(" 		AND A.LAST_WORK_ITEM_IDS = M.WORK_ITEM_ID ");
        parser4Ncc.addSQL(" 		AND A.SR_TYPE_ID = B.SR_TYPE_ID ");
        parser4Ncc.addSQL(" 		AND A.ACCEPT_STAFF_NO = C.OPERATOR_ID) ");
        parser4Ncc.addSQL(" WHERE 1 = 1 ");
        parser4Ncc.addSQL(" 	AND SUBS_NUMBER = :SERIAL_NUMBER ");
        parser4Ncc.addSQL(" 	order by accept_time desc ");
        IDataset result4Ncc = Dao.qryByParse(parser4Ncc, "ncc");

        //result4Ncc.addAll(result);
        DataHelper.sort(result4Ncc, "ACCEPT_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        return result4Ncc;
    }

    /**
     * 根据USER_ID查询优惠信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserDiscntInfo(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT T.REMARK, T.PARTITION_ID, T.USER_ID, T.USER_ID_A, T.DISCNT_CODE, ");
        parser.addSQL("T.SPEC_TAG, T.RELATION_TYPE_CODE, ");
        parser.addSQL("TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL("TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, T.REMARK ");
        parser.addSQL("FROM TF_F_USER_DISCNT T ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND T.USER_ID = :USER_ID ");
        parser.addSQL("AND T.PARTITION_ID = MOD(TO_NUMBER(:VUSER_ID), 10000) ");
        parser.addSQL("AND SYSDATE <= T.END_DATE ");
        parser.addSQL("AND T.START_DATE <= T.END_DATE ");
        parser.addSQL("ORDER BY T.START_DATE DESC ");

        IDataset ids = UserCommUtil.qryByParse(parser, pagination);

        for (int i = 0, size = ids.size(); i < size; i++)
        {
            IData map = ids.getData(i);

            map.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(map.getString("DISCNT_CODE")));
            map.put("DISCNT_EXPLAIN", UDiscntInfoQry.getDiscntExplainByDiscntCode(map.getString("DISCNT_CODE")));
        }

        return ids;
    }

    /**
     * 根据USER_ID查询优惠信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserDiscntInfoAll(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT T.REMARK, T.PARTITION_ID, T.USER_ID, T.USER_ID_A, T.DISCNT_CODE, ");
        parser.addSQL("T.SPEC_TAG, T.RELATION_TYPE_CODE, ");
        parser.addSQL("TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL("TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, T.REMARK ");
        parser.addSQL("FROM TF_F_USER_DISCNT T ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND T.USER_ID = :USER_ID ");
        parser.addSQL("AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("ORDER BY T.START_DATE DESC ");

        IDataset ids = UserCommUtil.qryByParse(parser, pagination);

        for (int i = 0, size = ids.size(); i < size; i++)
        {
            IData map = ids.getData(i);

            map.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(map.getString("DISCNT_CODE")));
            map.put("DISCNT_EXPLAIN", UDiscntInfoQry.getDiscntExplainByDiscntCode(map.getString("DISCNT_CODE")));
        }

        return ids;
    }

    /**
     * 根据USER_ID查询权益信息 by huangmx5
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserWelfareInfoAll(IData data, Pagination pagination) throws Exception
    {
    	String user_id = data.getString("USER_ID", "");
    	// 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("REL_OFFER_TYPE", "Q");
        IDataset ids = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_ALL_BY_USER_ID_AND_REL_OFFER_TYPE", param);
    	
        for (int i = 0, size = ids.size(); i < size; i++)
        {
            IData map = ids.getData(i);
            //权益包名称
            map.put("OFFER_NAME_Q", UDiscntInfoQry.getWelfareNameByOfferCodeAndOfferType(map.getString("REL_OFFER_CODE")));
            if("S".equals(map.getString("OFFER_TYPE"))){
            	//服务权益名称
            	map.put("OFFER_NAME", USvcInfoQry.getSvcNameBySvcId(map.getString("OFFER_CODE")));
            }
            if("D".equals(map.getString("OFFER_TYPE"))){
            	//优惠权益名称
            	map.put("OFFER_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(map.getString("OFFER_CODE")));
            }
            if("Z".equals(map.getString("OFFER_TYPE"))){
            	//平台服务权益名称
            	IDataset offerDataList = UpcCall.querySpComprehensiveInfoByServiceId(map.getString("OFFER_CODE",""));
            	map.put("OFFER_NAME", offerDataList.getData(0).getString("OFFER_NAME",""));
            }
        }

        return ids;
    }
    /**
     * 根据USER_ID查询权益信息 by huangmx5
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserWelfareInfo(IData data, Pagination pagination) throws Exception
    {
    	String user_id = data.getString("USER_ID", "");
    	// 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("REL_OFFER_TYPE", "Q");
        IDataset ids = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_USER_ID_AND_REL_OFFER_TYPE", param);
    	
        for (int i = 0, size = ids.size(); i < size; i++)
        {
            IData map = ids.getData(i);
            //权益包名称
            map.put("OFFER_NAME_Q", UDiscntInfoQry.getWelfareNameByOfferCodeAndOfferType(map.getString("REL_OFFER_CODE")));
            if("S".equals(map.getString("OFFER_TYPE"))){
            	//服务权益名称
            	map.put("OFFER_NAME", USvcInfoQry.getSvcNameBySvcId(map.getString("OFFER_CODE")));
            }
            if("D".equals(map.getString("OFFER_TYPE"))){
            	//优惠权益名称
            	map.put("OFFER_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(map.getString("OFFER_CODE")));
            }
            if("Z".equals(map.getString("OFFER_TYPE"))){
            	//平台服务权益名称
            	IDataset offerDataList = UpcCall.querySpComprehensiveInfoByServiceId(map.getString("OFFER_CODE",""));
            	map.put("OFFER_NAME", offerDataList.getData(0).getString("OFFER_NAME",""));
            }
        }

        return ids;
    }
    

    /**
     * 根据USER_ID查询服务信息
     * liquan
     * @param data
     * @param
     * @return
     * @throws Exception
     */
    public IDataset qryUserServiceInfo(IData data) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }


        SQLParser parser = new SQLParser(data);
        parser.addSQL("select T.SERVICE_ID , ");
        parser.addSQL(" TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE  ");
        parser.addSQL("  from tf_f_user_svc t where 1=1   ");
        parser.addSQL("  AND t.USER_ID = :USER_ID  ");
        parser.addSQL("  AND t.partition_id=MOD(:USER_ID, 10000)   AND SYSDATE <= T.END_DATE  AND T.START_DATE <= T.END_DATE ");
        parser.addSQL("  order by t.service_id,t.start_date desc  ");

        IDataset outlist = UserCommUtil.qryByParse(parser);

        for (int i = 0, size = outlist.size(); i < size; i++)
        {
            IData map = outlist.getData(i);

            map.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(map.getString("SERVICE_ID")));
        }
        return outlist;
    }

    /**
     * 根据USER_ID查询优惠信息
     *
     * @param data
     * @param liquan
     * @return
     * @throws Exception
     */
    public static IDataset qryUserDiscntInfo(IData data) throws Exception
    {
        String serialNumber = data.getString("USER_ID", "");

        // 如果没有传入SERIAL_NUMBER，则直接返回
        if ("".equals(serialNumber))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT  T.DISCNT_CODE,  ");
        parser.addSQL(" TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE  ");
        parser.addSQL(" FROM TF_F_USER_DISCNT T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND SYSDATE <= T.END_DATE ");
        parser.addSQL(" AND T.START_DATE <= T.END_DATE ");
        parser.addSQL(" ORDER BY T.START_DATE DESC ");

        IDataset ids = UserCommUtil.qryByParse(parser);

        for (int i = 0, size = ids.size(); i < size; i++)
        {
            IData map = ids.getData(i);

            map.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(map.getString("DISCNT_CODE")));
        }

        return ids;
    }

    /**
     * 根据USER_ID查询用户基本信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserInfo(IData data, Pagination pagination) throws Exception
    {
        QryUserInfoDao dao = new QryUserInfoDao();
        int x_getmode = data.getInt("X_GETMODE", 99);
        // x_getmode 获取方式：
        // 0-服务号码（取正常）、
        // 1-用户标识、
        // 2-客户标识、
        // 3-服务号码（取所有）、
        // 4-服务号码（取所有非正常用户）、
        // 5-服务号码（取最后销户用户）
        IData qryData = new DataMap();
        IDataset reout = new DatasetList();
        String sn = data.getString("SERIAL_NUMBER", "");
        String uid = data.getString("USER_ID", "");
        String cid = data.getString("CUST_ID", "");
        if (("".equals(sn)) && ("".equals(uid)) && ("".equals(cid)))
        {
            CSAppException.apperr(Qry360UserViewException.CRM_UserView_2);
        }

        switch (x_getmode)
        {
            case 0:// 输入服务号码取正常用户
            {
                qryData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
                qryData.put("REMOVE_TAG", "0");
                reout = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER", "")));
                break;
            }
            case 1:// 输入用户标识
            {
                IData reoutData = UcaInfoQry.qryUserInfoByUserId(data.getString("USER_ID", ""));
                //如果查询不到则去h表中查找销户转H表中的数据 hui
                if(IDataUtil.isEmpty(reoutData))
                {
                	IDataset reoutDatas = dao.qryUserInfoFromHis(data.getString("USER_ID", ""));
                	if(IDataUtil.isNotEmpty(reoutDatas))
                	{
                		reoutData = reoutDatas.getData(0);
                	}
                }
                reout.add(reoutData);
                break;
            }
            case 2:// 输入客户标识取正常用户
            {
                qryData.put("CUST_ID", data.getString("CUST_ID", ""));
                qryData.put("REMOVE_TAG", "0");
                reout = dao.qryUserInfoByCid(qryData, pagination);
                break;
            }
            case 3:// 输入服务号码取所有用户
            {
                qryData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
                reout = dao.qryUserInfoBySN(qryData, pagination);
                break;
            }
            case 4:// 输入服务号码取所有非正常用户
            {
                qryData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
                reout = dao.qryDestroyUserInfoBySN(qryData, pagination);
                break;
            }
            case 5:// 输入服务号码取最后销户用户
            {
                qryData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
                reout = dao.qryMaxDestroyUserInfoBySN(qryData, pagination);
                break;
            }
            case 6:// 输入服务号码取预销户用户资料
            {
                qryData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
                qryData.put("REMOVE_TAG", "1");
                reout = dao.qryUserInfoBySN(qryData, pagination);
                break;
            }
            case 7:// 输入服务号码取同帐户所有号码
            {
                // 根据号码和remove_tag查询用户表
                IData out = null;
                // dao.queryByPK("TF_F_USER", new String[] {
                // "SERIAL_NUMBER", "REMOVE_TAG" },
                // new String[] { data.getString("SERIAL_NUMBER", ""), "0" });
                if (out == null)
                {
                    CSAppException.apperr(Qry360UserViewException.CRM_UserView_3);
                }
                // 根据user_id查询付费关系资料
                IData out2 = dao.qryPayRelationByUid(out, pagination);
                if (out2 == null)
                {
                    CSAppException.apperr(Qry360UserViewException.CRM_UserView_4);
                }

                // 根据acct_ID查询帐户下面的付费关系
                IData in2 = new DataMap();
                in2.put("ACCT_ID", out2.getString("ACCT_ID"));
                in2.put("DEFAULT_TAG", "1");
                in2.put("ACT_TAG", "1");
                IDataset outlist = dao.qryPayRelationByAid(in2, pagination);
                for (int i = 0; i < outlist.size(); i++)
                {
                    qryData.put("USER_ID", ((IData) outlist.get(i)).getString("USER_ID", ""));
                    qryData.put("PARTITION_ID", StrUtil.getPartition4ById((((IData) outlist.get(i)).getString("USER_ID", ""))));
                    IData o = UcaInfoQry.qryUserMainProdInfoByUserId(qryData.getString("USER_ID"));
                    if (IDataUtil.isEmpty(o))
                    {
                        continue;
                    }
                    else
                    {
                        IData outX = o;
                        reout.add(outX);
                    }
                }

            }
            default:
            {
                CSAppException.apperr(Qry360UserViewException.CRM_UserView_5);
            }
        }

        if (IDataUtil.isEmpty(reout))
        {
            CSAppException.apperr(Qry360UserViewException.CRM_UserView_6);
        }

        for (int x = 0; x < reout.size(); x++)
        {
            IData params = reout.getData(x);
            dao.getUserSuperInfo(data, pagination, params, -1);
            // 翻译客户经理字段。因该表只在中心库
            if (!"".equals(params.getString("CUST_MANAGER_ID", "")))
            {
                // String custManagerName = StaticUtil.getStaticValueDataSource(getVisit(),
                // Route.CONN_CRM_CEN,"TF_F_CUST_MANAGER_STAFF","CUST_MANAGER_ID", "CUST_MANAGER_NAME",
                // params.getString("CUST_MANAGER_ID",""));
                IData custManager = UStaffInfoQry.qryCustManagerInfoByCustManagerId(params.getString("CUST_MANAGER_ID"));
                if (IDataUtil.isEmpty(custManager))
                {
                    params.put("CUST_MANAGER_NAME", custManager.getString("CUST_MANAGER_NAME"));
                }
            }
        }
        return reout;
    }
    //modify by lijun17
    public static IDataset qryUserSaleActiveInfo(IData param) throws Exception
    {
    	//SEL_BY_SALEACTIVE_VALID_HAIN
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_VALID_QYSALEACTIVE_NOW", param);
    }
    //add by huangsl modify by lijun17
    public static IDataset qrySaleActiveInfoHis(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ALL_SALEACTIVEFOR360_HIS_NOW", param);
    }
    public static IDataset qrySaleActiveInfoVaild(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ALL_SALEACTIVEFOR360_VAILD", param);
    }
    //modify by lijun17
    public static IDataset qrySaleActiveInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ALL_SALEACTIVEFOR360_NOW", param);
    }

    public static IDataset qryUserSaleActiveInfoAll(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_SALEACTIVE_ALL_HAIN", param);
    }

    public static IDataset queryMemberAll(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_ALL_MEMBER", param);
    }

    public static IDataset queryProductAndPackage(IData param) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PRODUCT_PACKAGE", param);
    }

    public static IDataset queryShareDiscnt(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_SHARE", "SEL_BY_CUST", param);
    }

    public static IDataset queryThServerInfos(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        SQLParser parser4Ncc = new SQLParser(param);
        IData sumData = new DataMap();

        parser.addSQL(" select a.call_serial_main, ");
        parser.addSQL("       a.start_date, ");
        parser.addSQL("       c.remark, ");
        parser.addSQL("       d.content, ");
        parser.addSQL("       e.data_name, ");
        parser.addSQL("       d.workform_id ");
        parser.addSQL("  from tf_bh_staff_callinfo a, ");
        parser.addSQL("       tf_b_serv_request_info b, ");
        parser.addSQL("       td_b_tradetype c, ");
        parser.addSQL("       tf_f_workform d, ");
        parser.addSQL("       (select * from td_s_static_param where type_id = 'WORKFORM_STATE') e ");
        parser.addSQL(" where a.call_serial_no = b.call_serial_no(+) ");
        parser.addSQL("   and b.request_typecode_sub = c.trade_type_code(+) ");
        parser.addSQL("   and b.workform_id = d.workform_id(+) ");
        parser.addSQL("   and d.workform_state = e.data_id(+) ");
        parser.addSQL("   and c.remark is not null ");
        parser.addSQL("   and a.call_serial_main = :SERIAL_NUMBER");
        parser.addSQL("   order by a.start_date desc ");
        //IDataset result = Dao.qryByParse(parser, Route.CONN_CC);

        parser4Ncc.addSQL(" select  M.SUBS_NUMBER CALL_SERIAL_MAIN, ");
        parser4Ncc.addSQL("         M.ACCEPT_TIME START_DATE, ");
        parser4Ncc.addSQL("         B.FULL_NAME REMARK, ");
        parser4Ncc.addSQL("         M.SERVICE_CONTENT CONTENT, ");
        parser4Ncc.addSQL("         decode(M.REQUEST_STATE,'1','已保存','2','活动中','3','已关闭','4','已删除') DATA_NAME, ");
        parser4Ncc.addSQL("         M.SHOW_SERIAL_NO WORKFORM_ID ");
        parser4Ncc.addSQL("   from SR_SERVICE_REQUEST M, SR_SERVICE_REQUEST_TYPE B   ");
        parser4Ncc.addSQL("  where 1 = 1 ");
        parser4Ncc.addSQL("    and M.SR_TYPE_ID = B.SR_TYPE_ID ");
        parser4Ncc.addSQL("    and M.REQUEST_STATE != '1' ");
        parser4Ncc.addSQL("    and M.SR_WF_TYPE in ('01', '06', '07')  ");
        parser4Ncc.addSQL("    and M.WORKFORM_TYPE_CODE in ('01', '13', '14') ");
        parser4Ncc.addSQL("    and M.SUBS_NUMBER = :SERIAL_NUMBER ");
        parser4Ncc.addSQL("  order by M.ACCEPT_TIME desc  ");
        IDataset result4Ncc = Dao.qryByParse(parser4Ncc, "ncc");

        //result4Ncc.addAll(result);
        DataHelper.sort(result4Ncc, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        return result4Ncc;
    }

    /**
     * 根据用户ID、客户ID、业务类型编码、起始时间和结束时间查询业务历史信息（tf_f_bh_trade）
     *
     * @param param
     * @param pagination
     * @return
     */
    public static IDataset queryTradeHistoryInfo(IData data, Pagination pagination) throws Exception
    {
        // 将accetp_month 加入到查询条件
        String sYear = "";
        String sMonth = "";
        String eYear = "";
        String eMonth = "";
        if (!data.getString("START_DATE", "").equals(""))
        {
            sYear = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("START_DATE"), "yyyy-MM-dd"), "yyyy");
            sMonth = SysDateMgr.getTheMonth(data.getString("START_DATE"));
        }
        if (!data.getString("END_DATE", "").equals(""))
        {
            eYear = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("END_DATE"), "yyyy-MM-dd"), "yyyy");
            eMonth = SysDateMgr.getTheMonth(data.getString("END_DATE"));
        }

        String user_id = data.getString("USER_ID", "").trim();
        String trade_id = data.getString("TRADE_ID", "").trim();
        String qry_trade_table = data.getString("B_AND_BH", "");// 该字段用来标志查询历史信息时
        // 1同时也查trade表的非预约的数据
        if ("".equals(user_id) && "".equals(trade_id))
        {
            // common.error("用户编码参数[USER_ID]及[TRADE_ID]不能都为空");
            return null;
        }

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select  trade_id,in_mode_code,accept_month||'月' accept_month,order_id,priority,trade_type_code,");
        parser.addSQL(" decode(subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
        parser.addSQL(" subscribe_state,");
        parser.addSQL(" cancel_tag,");
        parser.addSQL(" decode(fee_state,'0','未收费','1','已收费','') fee_state,");
        parser.addSQL(" next_deal_tag,");
        parser.addSQL(" serial_number,brand_code,product_id,oper_fee,foregift,fee_time,exec_time,finish_date, ");
        parser.addSQL(" eparchy_code, trim(city_code) city_code,trim(trade_staff_id) trade_staff_id, trim(trade_depart_id) trade_depart_id, trade_eparchy_code, trim(trade_city_code) trade_city_code, remark, ");
        // add by yijb 云南的漏洞，部分信息没有查询展示
        parser.addSQL(" INVOICE_NO,	ADVANCE_PAY , FEE_STAFF_ID, ACCT_ID ,");
        parser.addSQL(" accept_date,cust_name,user_id,cust_id, substr(process_tag_set, 9,1) process_tag_set ");
        parser.addSQL(" from tf_bh_trade a  ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.user_id=:USER_ID");
        parser.addSQL(" and a.trade_id=:TRADE_ID");
        parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
        parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
        parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 ");
        if (sYear.equals(eYear) && !sYear.equals("") && !eYear.equals(""))
        {
            parser.addSQL(" and a.accept_month >=  " + sMonth + " ");
            parser.addSQL(" and a.accept_month <=  " + eMonth + " ");
        }

        // add by caiy 2009-05-25
        // 增加查询需要屏蔽的trade_type_code
        boolean b5 = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_USER_LIST_QRY");
        boolean b6 = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_USER_CUSTORMER_QRY");
        // if (!(getVisit().hasPriv("SYS_USER_LIST_QRY") || getVisit().hasPriv("SYS_USER_CUSTORMER_QRY")))
        if (!(b5 || b6))
        {
            parser.addSQL(" and a.trade_type_code not in ('1170','2101') ");// 1170
            // 清单查询
            // 2101
            // 客户资料查询
        }
        // else if (!(getVisit().hasPriv("SYS_USER_LIST_QRY")))
        else if (!b5)
        {
            parser.addSQL(" and a.trade_type_code  <>'1170'");
        }
        // else if (!(getVisit().hasPriv("SYS_USER_CUSTORMER_QRY")))
        else if (!b6)
        {
            parser.addSQL(" and a.trade_type_code  <>'2101'");
        }
        /*
         * parser.addSQL(" union all ");
         * parser.addSQL(" select  trade_id,in_mode_code,accept_month||'月' accept_month,order_id,priority,trade_type_code,"
         * );parser.addSQL(
         * " decode(subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,"
         * ); parser.addSQL(" subscribe_state,"); parser.addSQL(" cancel_tag,");
         * parser.addSQL(" decode(fee_state,'0','未收费','1','已收费','') fee_state,"); parser.addSQL(" next_deal_tag,");
         * parser.addSQL(" serial_number,brand_code,product_id,oper_fee,foregift,fee_time,exec_time,finish_date, ");
         * parser.addSQL(
         * " eparchy_code, trim(city_code) city_code ,trim(trade_staff_id) trade_staff_id, trim(trade_depart_id) trade_depart_id, trade_eparchy_code, trim(trade_city_code) trade_city_code, remark, "
         * ); // add by yijb 云南的漏洞，部分信息没有查询展示 parser.addSQL(" INVOICE_NO,	ADVANCE_PAY , FEE_STAFF_ID, ACCT_ID ,");
         * parser.addSQL(" accept_date,cust_name,user_id,cust_id, substr(process_tag_set, 9,1) process_tag_set ");
         * parser.addSQL(" from tf_bh_trade_second a  "); parser.addSQL(" where 1=1 ");
         * parser.addSQL(" and a.user_id=:USER_ID"); parser.addSQL(" and a.trade_id=:TRADE_ID");
         * parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
         * parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
         * parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 "); if (sYear.equals(eYear) &&
         * !sYear.equals("") && !eYear.equals("")) { parser.addSQL(" and a.accept_month >=  " + sMonth + " ");
         * parser.addSQL(" and a.accept_month <=  " + eMonth + " "); } // add by caiy 2009-05-25 //
         * 增加查询需要屏蔽的trade_type_code // if (!(getVisit().hasPriv("SYS_USER_LIST_QRY") ||
         * getVisit().hasPriv("SYS_USER_CUSTORMER_QRY"))) boolean b1 =
         * StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_USER_LIST_QRY"); boolean b2 =
         * StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_USER_CUSTORMER_QRY"); boolean b3 =
         * StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_USER_LIST_QRY"); boolean b4 =
         * StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_USER_CUSTORMER_QRY"); if (!(b1 || b2)) {
         * parser.addSQL(" and a.trade_type_code not in ('1170','2101') ");// 1170 // 清单查询 // 2101 // 客户资料查询 } // else
         * if (!(getVisit().hasPriv("SYS_USER_LIST_QRY"))) else if (!b3) {
         * parser.addSQL(" and a.trade_type_code  <>'1170'"); } // else if
         * (!(getVisit().hasPriv("SYS_USER_CUSTORMER_QRY"))) else if (!b4) {
         * parser.addSQL(" and a.trade_type_code  <>'2101'"); } if(!"1".equals(qry_trade_table)) {
         * parser.addSQL(" order by a.ACCEPT_DATE desc "); }
         */

        if ("1".equals(qry_trade_table))
        {// 该字段用来标志查询历史信息时 1同时也查trade表的非预约的数据
            parser.addSQL(" union ");
            parser.addSQL(" select  trade_id,in_mode_code,accept_month||'月' accept_month,order_id,priority,trade_type_code,");
            parser.addSQL(" decode(subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
            parser.addSQL(" subscribe_state,");
            parser.addSQL(" cancel_tag,");
            parser.addSQL(" decode(fee_state,'0','未收费','1','已收费','') fee_state,");
            parser.addSQL(" next_deal_tag,");
            parser.addSQL(" serial_number,brand_code,product_id,oper_fee,foregift,fee_time,exec_time,finish_date, ");
            parser.addSQL(" eparchy_code, trim(city_code)  city_code,trim(trade_staff_id) trade_staff_id, trim(trade_depart_id) trade_depart_id, trade_eparchy_code, trim(trade_city_code) trade_city_code, remark, ");
            // add by yijb 云南的漏洞，部分信息没有查询展示
            parser.addSQL(" INVOICE_NO,	ADVANCE_PAY , FEE_STAFF_ID, ACCT_ID ,");
            parser.addSQL(" accept_date,cust_name,user_id,cust_id, substr(process_tag_set, 9,1) process_tag_set ");
            parser.addSQL(" from tf_b_trade a ");
            parser.addSQL(" where 1=1 ");
            parser.addSQL(" and a.exec_time < sysdate ");// 非预约的数据
            parser.addSQL(" and a.user_id=:USER_ID");
            parser.addSQL(" and a.trade_id=:TRADE_ID");
            parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
            if (sYear.equals(eYear) && !sYear.equals("") && !eYear.equals(""))
            {
                parser.addSQL(" and a.accept_month >=  " + sMonth + " ");
                parser.addSQL(" and a.accept_month <=  " + eMonth + " ");
            }
            parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
            parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 ");
            parser.addSQL(" and a.trade_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_TRADENOQUERY' ) ");

        }
        parser.addSQL(" order by ACCEPT_DATE desc ");
        IDataset results = Dao.qryByParse(parser, pagination, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        if (results == null || results.isEmpty())
        {
            return null;
        }
        else
        {
            IData result_info = null;
            for (int i = 0; i < results.size(); i++)
            {
                result_info = results.getData(i);
                String trade_type_code = result_info.getString("TRADE_TYPE_CODE", "");
                if (!"".equals(trade_type_code) && "110".equals(trade_type_code))
                {
                    String process_tag_set = result_info.getString("PROCESS_TAG_SET", "");
                    result_info.put("TAG_110", "1");
                    if ("1".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务变更");
                    }
                    else if ("2".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "优惠变更");
                    }
                    else if ("3".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务和优惠变更");
                    }
                    else if ("4".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "产品变更");
                    }
                    else
                    {
                        result_info.put("TAG_110", "2");
                    }
                }
                else
                {
                    result_info.put("TAG_110", "2");
                }
            }
        }
        return results;
    }

    /**
     * 根据用户ID、客户ID、业务类型编码、起始时间和结束时间查询业务历史信息（tf_f_bh_trade）Cg库
     *
     * @param param
     * @param pagination
     * @return
     */
    public static IDataset queryTradeHistoryInfoCg(IData data, Pagination pagination) throws Exception
    {

        String user_id = data.getString("USER_ID", "").trim();
        String trade_id = data.getString("TRADE_ID", "").trim();
        String qry_trade_table = data.getString("B_AND_BH", "");// 该字段用来标志查询历史信息时
        // 1同时也查trade表的非预约的数据
        if ("".equals(user_id) && "".equals(trade_id))
        {
            // common.error("用户编码参数[USER_ID]及[TRADE_ID]不能都为空");
            return null;
        }

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select  trade_id,in_mode_code,accept_month||'月' accept_month,order_id,priority,trade_type_code,");
        parser.addSQL(" decode(subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
        parser.addSQL(" subscribe_state,");
        parser.addSQL(" cancel_tag,");
        parser.addSQL(" decode(fee_state,'0','未收费','1','已收费','') fee_state,");
        parser.addSQL(" next_deal_tag,");
        parser.addSQL(" serial_number,brand_code,product_id,oper_fee,foregift,fee_time,exec_time,finish_date, ");
        parser.addSQL(" eparchy_code, trim(city_code)  city_code ,trim(trade_staff_id) trade_staff_id, trim(trade_depart_id) trade_depart_id, trade_eparchy_code, trim(trade_city_code) trade_city_code, remark, ");
        // add by yijb 云南的漏洞，部分信息没有查询展示
        parser.addSQL(" INVOICE_NO,	ADVANCE_PAY , FEE_STAFF_ID, ACCT_ID ,");
        parser.addSQL(" accept_date,cust_name,user_id,cust_id, substr(process_tag_set, 9,1) process_tag_set ");
        parser.addSQL(" from tf_bh_trade a  ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.user_id=:USER_ID");
        parser.addSQL(" and a.trade_id=:TRADE_ID");
        parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
        parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
        parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 ");
        // add by caiy 2009-05-25
        boolean b5 = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_USER_LIST_QRY");
        boolean b6 = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_USER_CUSTORMER_QRY");
        // 增加查询需要屏蔽的trade_type_code
        // if (!(getVisit().hasPriv("SYS_USER_LIST_QRY") || getVisit().hasPriv("SYS_USER_CUSTORMER_QRY")))
        if (!(b5 || b6))
        {
            parser.addSQL(" and a.trade_type_code not in ('1170','2101') ");// 1170
            // 清单查询
            // 2101
            // 客户资料查询
        }
        // else if (!(getVisit().hasPriv("SYS_USER_LIST_QRY")))
        else if (!b5)
        {
            parser.addSQL(" and a.trade_type_code  <>'1170'");
        }
        // else if (!(getVisit().hasPriv("SYS_USER_CUSTORMER_QRY")))
        else if (!b6)
        {
            parser.addSQL(" and a.trade_type_code  <>'2101'");
        }
        /*
         * parser.addSQL(" union all ");
         * parser.addSQL(" select  trade_id,in_mode_code,accept_month||'月' accept_month,order_id,priority,trade_type_code,"
         * );parser.addSQL(
         * " decode(subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,"
         * ); parser.addSQL(" subscribe_state,"); parser.addSQL(" cancel_tag,");
         * parser.addSQL(" decode(fee_state,'0','未收费','1','已收费','') fee_state,"); parser.addSQL(" next_deal_tag,");
         * parser.addSQL(" serial_number,brand_code,product_id,oper_fee,foregift,fee_time,exec_time,finish_date, ");
         * parser.addSQL(
         * " eparchy_code, trim(city_code) city_code ,trim(trade_staff_id) trade_staff_id, trim(trade_depart_id) trade_depart_id, trade_eparchy_code, trim(trade_city_code) trade_city_code, remark, "
         * ); // add by yijb 云南的漏洞，部分信息没有查询展示 parser.addSQL(" INVOICE_NO,	ADVANCE_PAY , FEE_STAFF_ID, ACCT_ID ,");
         * parser.addSQL(" accept_date,cust_name,user_id,cust_id, substr(process_tag_set, 9,1) process_tag_set ");
         * parser.addSQL(" from tf_bh_trade_second a  "); parser.addSQL(" where 1=1 ");
         * parser.addSQL(" and a.user_id=:USER_ID"); parser.addSQL(" and a.trade_id=:TRADE_ID");
         * parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
         * parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
         * parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 "); // add by caiy 2009-05-25 //
         * 增加查询需要屏蔽的trade_type_code // if (!(getVisit().hasPriv("SYS_USER_LIST_QRY") ||
         * getVisit().hasPriv("SYS_USER_CUSTORMER_QRY"))) if (!(b5 || b6)) {
         * parser.addSQL(" and a.trade_type_code not in ('1170','2101') ");// 1170 // 清单查询 // 2101 // 客户资料查询 } // else
         * if (!(getVisit().hasPriv("SYS_USER_LIST_QRY"))) else if (!b5) {
         * parser.addSQL(" and a.trade_type_code  <>'1170'"); } // else if
         * (!(getVisit().hasPriv("SYS_USER_CUSTORMER_QRY"))) else if (!b6) {
         * parser.addSQL(" and a.trade_type_code  <>'2101'"); } if(!"1".equals(qry_trade_table)) {
         * parser.addSQL(" order by a.ACCEPT_DATE desc "); }
         */

        if ("1".equals(qry_trade_table))
        {// 该字段用来标志查询历史信息时 1同时也查trade表的非预约的数据
            parser.addSQL(" union ");
            parser.addSQL(" select  trade_id,in_mode_code,accept_month||'月' accept_month,order_id,priority,trade_type_code,");
            parser.addSQL(" decode(subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
            parser.addSQL(" subscribe_state,");
            parser.addSQL(" cancel_tag,");
            parser.addSQL(" decode(fee_state,'0','未收费','1','已收费','') fee_state,");
            parser.addSQL(" next_deal_tag,");
            parser.addSQL(" serial_number,brand_code,product_id,oper_fee,foregift,fee_time,exec_time,finish_date, ");
            parser.addSQL(" eparchy_code, trim(city_code)  city_code,trim(trade_staff_id) trade_staff_id, trim(trade_depart_id) trade_depart_id, trade_eparchy_code, trim(trade_city_code) trade_city_code, remark, ");
            // add by yijb 云南的漏洞，部分信息没有查询展示
            parser.addSQL(" INVOICE_NO,	ADVANCE_PAY , FEE_STAFF_ID, ACCT_ID ,");
            parser.addSQL(" accept_date,cust_name,user_id,cust_id, substr(process_tag_set, 9,1) process_tag_set ");
            parser.addSQL(" from tf_b_trade a ");
            parser.addSQL(" where 1=1 ");
            parser.addSQL(" and a.exec_time < sysdate ");// 非预约的数据
            parser.addSQL(" and a.user_id=:USER_ID");
            parser.addSQL(" and a.trade_id=:TRADE_ID");
            parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
            parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
            parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 ");
            parser.addSQL(" and a.trade_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_TRADENOQUERY' ) ");
            // parser.addSQL(" order by a.ACCEPT_DATE desc ");

        }
        parser.addSQL("order by accept_date desc");
        IDataset results = UserCommUtil.qryByParse(parser, pagination, Route.getJourDb(BizRoute.getTradeEparchyCode()));//Route.CONN_CRM_CG
        if (results == null || results.isEmpty())
        {
            return null;
        }
        else
        {
            IData result_info = null;
            for (int i = 0; i < results.size(); i++)
            {
                result_info = results.getData(i);
                String trade_type_code = result_info.getString("TRADE_TYPE_CODE", "");
                if (!"".equals(trade_type_code) && "110".equals(trade_type_code))
                {
                    String process_tag_set = result_info.getString("PROCESS_TAG_SET", "");
                    result_info.put("TAG_110", "1");
                    if ("1".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务变更");
                    }
                    else if ("2".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "优惠变更");
                    }
                    else if ("3".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务和优惠变更");
                    }
                    else if ("4".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "产品变更");
                    }
                    else
                    {
                        result_info.put("TAG_110", "2");
                    }
                }
                else
                {
                    result_info.put("TAG_110", "2");
                }
            }
        }
        return results;
    }

    public static IDataset queryTradeHistoryInfoNew(IData data, Pagination pagination) throws Exception
    {
        // 将accetp_month 加入到查询条件
        String sYear = "";
        String sMonth = "";
        String eYear = "";
        String eMonth = "";
        if (!data.getString("START_DATE", "").equals(""))
        {
            sYear = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("START_DATE"), "yyyy-MM-dd"), "yyyy");
            sMonth = SysDateMgr.getTheMonth(data.getString("START_DATE"));
        }
        if (!data.getString("END_DATE", "").equals(""))
        {
            eYear = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("END_DATE"), "yyyy-MM-dd"), "yyyy");
            eMonth = SysDateMgr.getTheMonth(data.getString("END_DATE"));
        }

        String user_id = data.getString("USER_ID", "").trim();
        String trade_id = data.getString("TRADE_ID", "").trim();
        String qry_trade_table = data.getString("B_AND_BH", "");// 该字段用来标志查询历史信息时
        // 1同时也查trade表的非预约的数据
        if ("".equals(user_id) && "".equals(trade_id))
        {
            // common.error("用户编码参数[USER_ID]及[TRADE_ID]不能都为空");
            return null;
        }

        SQLParser parser = new SQLParser(data);
		parser.addSQL(" select /*+ index(a IDX_TF_BH_TRADE_USERID)*/a.trade_id,a.in_mode_code,a.accept_month||'月' accept_month,a.order_id,a.priority,a.trade_type_code,");
		parser.addSQL(" decode(a.subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
		parser.addSQL(" a.subscribe_state,");
		parser.addSQL(" a.cancel_tag,");
		parser.addSQL(" decode(a.fee_state,'0','未收费','1','已收费','') fee_state,");
		parser.addSQL(" a.next_deal_tag,");
		parser.addSQL(" a.serial_number,a.brand_code,a.product_id,a.oper_fee,a.foregift,a.fee_time,a.exec_time,a.finish_date, ");
		parser.addSQL(" a.eparchy_code, a.city_code,a.trade_staff_id, a.trade_depart_id, a.trade_eparchy_code, a.trade_city_code, a.remark, ");
		// add by yijb 云南的漏洞，部分信息没有查询展示
		parser.addSQL(" a.INVOICE_NO,	a.ADVANCE_PAY , a.FEE_STAFF_ID, a.ACCT_ID ,");
		parser.addSQL(" a.accept_date,a.cust_name,a.user_id,a.cust_id, substr(a.process_tag_set, 9,1) process_tag_set");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR4,'') RSRV_STR4, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR5,'') RSRV_STR5, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR6,'') RSRV_STR6, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR7,'') RSRV_STR7, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR8,'') RSRV_STR8, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR9,'') RSRV_STR9 ");
//		parser.addSQL(" from tf_bh_trade a , tf_b_trade_score b");
		parser.addSQL(" from tf_bh_trade a ");

        parser.addSQL(" where 1=1 ");
	//	parser.addSQL(" and a.trade_id=b.trade_id(+)");
		parser.addSQL(" and a.user_id=to_number(:USER_ID)");
		parser.addSQL(" and a.trade_id=:TRADE_ID");
		parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
		parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
		parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 ");
        if (sYear.equals(eYear) && !sYear.equals("") && !eYear.equals(""))
        {
            parser.addSQL(" and a.accept_month >=  " + sMonth + " ");
            parser.addSQL(" and a.accept_month <=  " + eMonth + " ");
        }
//modify by lijun17 		parser.addSQL(" and a.trade_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_TRADENOQUERY' ) ");
        IDataset staticInfo = StaticInfoQry.getStaticValueByTypeId("USER360VIEW_TRADENOQUERY");
        String user360ViewTradeNoQuery = "";
        if(IDataUtil.isNotEmpty(staticInfo)){
        	for(int i = 0 ; i < staticInfo.size() ; i++){
        		String dataId = staticInfo.getData(i).getString("DATA_ID", "");
        		if(StringUtils.isNotBlank(dataId)){
        			if(i == staticInfo.size()-1){
        				user360ViewTradeNoQuery = user360ViewTradeNoQuery + "'" + dataId + "'";
        			}else{
        				user360ViewTradeNoQuery = user360ViewTradeNoQuery + "'" + dataId + "',";
        			}
        		}
        	}
        }
        parser.addSQL(" and a.trade_type_code not in ("+user360ViewTradeNoQuery+") ");

		parser.addSQL(" union ");
		parser.addSQL(" select /*+ index(a IDX_TF_BH_TRADE_USERID)*/a.trade_id,a.in_mode_code,a.accept_month||'月' accept_month,a.order_id,a.priority,a.trade_type_code,");
		parser.addSQL(" decode(a.subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
		parser.addSQL(" a.subscribe_state,");
		parser.addSQL(" a.cancel_tag,");
		parser.addSQL(" decode(a.fee_state,'0','未收费','1','已收费','') fee_state,");
		parser.addSQL(" a.next_deal_tag,");
		parser.addSQL(" a.serial_number,a.brand_code,a.product_id,a.oper_fee,a.foregift,a.fee_time,a.exec_time,a.finish_date, ");
		parser.addSQL(" a.eparchy_code, a.city_code,a.trade_staff_id, a.trade_depart_id, a.trade_eparchy_code, a.trade_city_code, a.remark, ");
		parser.addSQL(" a.INVOICE_NO,	a.ADVANCE_PAY , a.FEE_STAFF_ID, a.ACCT_ID ,");
		parser.addSQL(" a.accept_date,a.cust_name,a.user_id,a.cust_id, substr(a.process_tag_set, 9,1) process_tag_set");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR4,'') RSRV_STR4, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR5,'') RSRV_STR5, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR6,'') RSRV_STR6, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR7,'') RSRV_STR7, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR8,'') RSRV_STR8, ");
//		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR9,'') RSRV_STR9 ");
//		parser.addSQL(" from tf_bh_trade a , tf_b_trade_score b");
		parser.addSQL(" from tf_bh_trade a ");

        parser.addSQL(" where 1=1 ");
//		parser.addSQL(" and a.trade_id=b.trade_id(+)");

		parser.addSQL(" and a.user_id=to_number(:USER_ID)");//新增

//modify by lijun17 		parser.addSQL(" and a.serial_number='KD_' || (SELECT b.serial_number FROM tf_f_user b WHERE 1=1 AND b.user_id=:USER_ID)");


//		IData userInfo = UcaInfoQry.qryUserInfoByUserId(user_id);
//		String serialNumber = "";
//		if(IDataUtil.isNotEmpty(userInfo)){
//			serialNumber = userInfo.getString("SERIAL_NUMBER","");
//		}
//		parser.addSQL(" and a.serial_number='KD_' || "+serialNumber);


		parser.addSQL(" and a.trade_id=:TRADE_ID");
		parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
		parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
		parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 ");
        if (sYear.equals(eYear) && !sYear.equals("") && !eYear.equals(""))
        {
            parser.addSQL(" and a.accept_month >=  " + sMonth + " ");
            parser.addSQL(" and a.accept_month <=  " + eMonth + " ");
        }
//modify by lijun17		parser.addSQL(" and a.trade_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_TRADENOQUERY' ) ");
        parser.addSQL(" and a.trade_type_code not in ("+user360ViewTradeNoQuery+") ");

		if("1".equals(qry_trade_table)){//该字段用来标志查询历史信息时 1同时也查trade表的非预约的数据
			parser.addSQL(" union ");
			parser.addSQL(" select /*+ index(a IDX_TF_B_TRADE_USERID)*/a.trade_id,a.in_mode_code,a.accept_month||'月' accept_month,a.order_id,a.priority,a.trade_type_code,");
			parser.addSQL(" decode(a.subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
			parser.addSQL(" a.subscribe_state,");
			parser.addSQL(" a.cancel_tag,");
			parser.addSQL(" decode(a.fee_state,'0','未收费','1','已收费','') fee_state,");
			parser.addSQL(" a.next_deal_tag,");
			parser.addSQL(" a.serial_number,a.brand_code,a.product_id,a.oper_fee,a.foregift,a.fee_time,a.exec_time,a.finish_date, ");
			parser.addSQL(" a.eparchy_code, a.city_code,a.trade_staff_id, a.trade_depart_id, a.trade_eparchy_code, a.trade_city_code, a.remark, ");
			parser.addSQL(" a.INVOICE_NO,	a.ADVANCE_PAY , a.FEE_STAFF_ID, a.ACCT_ID ,");
			parser.addSQL(" a.accept_date,a.cust_name,a.user_id,a.cust_id, substr(a.process_tag_set, 9,1) process_tag_set ");
//			parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR4,'') RSRV_STR4, ");
//			parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR5,'') RSRV_STR5, ");
//			parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR6,'') RSRV_STR6, ");
//			parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR7,'') RSRV_STR7, ");
//			parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR8,'') RSRV_STR8, ");
//			parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR9,'') RSRV_STR9 ");
//			parser.addSQL(" from tf_b_trade a  , tf_b_trade_score b");
			parser.addSQL(" from tf_b_trade a ");

			parser.addSQL(" where 1=1 ");
//			parser.addSQL(" and a.trade_id=b.trade_id(+)");
			parser.addSQL(" and a.exec_time < sysdate ");//非预约的数据
			parser.addSQL(" and a.user_id=to_number(:USER_ID)");
			parser.addSQL(" and a.trade_id=:TRADE_ID");
			parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
			parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
			parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 ");
//modify by lijun17				parser.addSQL(" and a.trade_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_TRADENOQUERY' ) ");
			parser.addSQL(" and a.trade_type_code not in ("+user360ViewTradeNoQuery+") ");
		}
        parser.addSQL(" order by accept_date desc");
        IDataset results = Dao.qryByParse(parser, pagination, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        if (results == null || results.isEmpty())
        {
            // return null;
        }
        else
        {
            IData result_info = null;
            for (int i = 0; i < results.size(); i++)
            {
                result_info = results.getData(i);
                String trade_type_code = result_info.getString("TRADE_TYPE_CODE", "");
                if (!"".equals(trade_type_code) && "110".equals(trade_type_code))
                {
                    String process_tag_set = result_info.getString("PROCESS_TAG_SET", "");
                    result_info.put("TAG_110", "1");
                    if ("1".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务变更");
                    }
                    else if ("2".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "优惠变更");
                    }
                    else if ("3".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务和优惠变更");
                    }
                    else if ("4".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "产品变更");
                    }
                    else
                    {
                        result_info.put("TAG_110", "2");
                    }
                }
                else
                {
                    result_info.put("TAG_110", "2");
                }
            }
        }
        return results;
    }

    /**
     * 根据用户ID、客户ID、业务类型编码、起始时间和结束时间查询业务台帐信息（tf_f_b_trade）
     *
     * @param param
     * @param pagination
     * @return
     */
    public static IDataset queryTradeInfo(IData data, Pagination pagination) throws Exception
    {
        // 将accetp_month 加入到查询条件
        String sYear = "";
        String sMonth = "";
        String eYear = "";
        String eMonth = "";
        if (!data.getString("START_DATE", "").equals(""))
        {
            sYear = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("START_DATE"), "yyyy-MM-dd"), "yyyy");
            sMonth = SysDateMgr.getTheMonth(data.getString("START_DATE"));
        }
        if (!data.getString("END_DATE", "").equals(""))
        {
            eYear = SysDateMgr.date2String(SysDateMgr.string2Date(data.getString("END_DATE"), "yyyy-MM-dd"), "yyyy");
            eMonth = SysDateMgr.getTheMonth(data.getString("END_DATE"));
        }

        String user_id = data.getString("USER_ID", "").trim();
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select trade_id,in_mode_code,accept_month||'月' accept_month,order_id,priority,trade_type_code,");
        parser.addSQL(" decode(subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
        parser.addSQL(" subscribe_state,");
        parser.addSQL(" cancel_tag,");
        parser.addSQL(" eparchy_code,");
        parser.addSQL(" decode(fee_state,'0','未收费','1','已收费','') fee_state,");
        parser.addSQL(" next_deal_tag,");
        parser.addSQL(" serial_number,brand_code,product_id,oper_fee,foregift,fee_time,exec_time,finish_date, ");
        parser.addSQL(" trim(city_code) city_code,trim(trade_staff_id) trade_staff_id, trim(trade_depart_id) trade_depart_id, trade_eparchy_code, trim(trade_city_code) trade_city_code, remark, ");
        // add by yijb 云南的漏洞，部分信息没有查询展示
        parser.addSQL(" INVOICE_NO,	ADVANCE_PAY , FEE_STAFF_ID, ACCT_ID ,");
        parser.addSQL(" accept_date,cust_name,user_id,cust_id, substr(process_tag_set, 9,1) process_tag_set ");
        parser.addSQL(" from tf_b_trade  ");
        parser.addSQL(" where user_id=:USER_ID");
        parser.addSQL(" and trade_id=:TRADE_ID");
        parser.addSQL(" and trade_type_code=:TRADE_TYPE_CODE");
        parser.addSQL(" and exec_time > sysdate ");
        if (sYear.equals(eYear) && !sYear.equals("") && !eYear.equals(""))
        {
            parser.addSQL(" and accept_month >=  " + sMonth + " ");
            parser.addSQL(" and accept_month <=  " + eMonth + " ");
        }

		parser.addSQL(" union ");
		parser.addSQL(" select trade_id,in_mode_code,accept_month||'月' accept_month,order_id,priority,trade_type_code,");
		parser.addSQL(" decode(subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
		parser.addSQL(" subscribe_state,");
		parser.addSQL(" cancel_tag,");
		parser.addSQL(" eparchy_code,");
		parser.addSQL(" decode(fee_state,'0','未收费','1','已收费','') fee_state,");
		parser.addSQL(" next_deal_tag,");
		parser.addSQL(" serial_number,brand_code,product_id,oper_fee,foregift,fee_time,exec_time,finish_date, ");
		parser.addSQL(" city_code,trade_staff_id, trade_depart_id, trade_eparchy_code, trade_city_code, remark, ");
		parser.addSQL(" INVOICE_NO,	ADVANCE_PAY , FEE_STAFF_ID, ACCT_ID ,");
		parser.addSQL(" accept_date,cust_name,user_id,cust_id, substr(process_tag_set, 9,1) process_tag_set ");
		parser.addSQL(" from tf_b_trade  ");
//modify by lijun17		parser.addSQL(" where serial_number='KD_' || (SELECT b.serial_number FROM tf_f_user b WHERE 1=1 AND b.user_id=:USER_ID)");
		IData userInfo = UcaInfoQry.qryUserInfoByUserId(user_id);
		String serialNumber = "";
		if(IDataUtil.isNotEmpty(userInfo)){
			serialNumber = userInfo.getString("SERIAL_NUMBER","");
		}
		parser.addSQL(" where serial_number='KD_' || "+serialNumber);
        parser.addSQL(" and trade_id=:TRADE_ID");
		parser.addSQL(" and trade_type_code=:TRADE_TYPE_CODE");
        if (sYear.equals(eYear) && !sYear.equals("") && !eYear.equals(""))
        {
            parser.addSQL(" and accept_month >=  " + sMonth + " ");
            parser.addSQL(" and accept_month <=  " + eMonth + " ");
        }

        parser.addSQL(" order by accept_date desc ");

        IDataset results = UserCommUtil.qryByParse(parser, pagination, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        if (results == null || results.isEmpty())
        {
        }
        else
        {
            IData result_info = null;
            for (int i = 0; i < results.size(); i++)
            {
                result_info = results.getData(i);
                String trade_type_code = result_info.getString("TRADE_TYPE_CODE", "");
                if (!"".equals(trade_type_code) && "110".equals(trade_type_code))
                {
                    String process_tag_set = result_info.getString("PROCESS_TAG_SET", "");
                    result_info.put("TAG_110", "1");
                    if ("1".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务变更");
                    }
                    else if ("2".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "优惠变更");
                    }
                    else if ("3".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务和优惠变更");
                    }
                    else if ("4".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "其他(含产品变更)");
                    }
                    else
                    {
                        result_info.put("TAG_110", "2");
                    }
                }
                else
                {
                    result_info.put("TAG_110", "2");
                }
            }
        }

        return results;
    }

    /**
     * 根据用户ID、客户ID、业务类型编码、起始时间和结束时间查询业务台帐信息（tf_f_b_trae） Cg库
     *
     * @param param
     * @param pagination
     * @return
     */
    public static IDataset queryTradeInfoCg(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "").trim();
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select trade_id,in_mode_code,accept_month||'月' accept_month,order_id,priority,trade_type_code,");
        parser.addSQL(" decode(subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
        parser.addSQL(" subscribe_state,");
        parser.addSQL(" cancel_tag,");
        parser.addSQL(" decode(fee_state,'0','未收费','1','已收费','') fee_state,");
        parser.addSQL(" next_deal_tag,");
        parser.addSQL(" serial_number,brand_code,product_id,oper_fee,foregift,fee_time,exec_time,finish_date, ");
        parser.addSQL(" trim(city_code) city_code,trim(trade_staff_id) trade_staff_id, trim(trade_depart_id) trade_depart_id, trade_eparchy_code, trim(trade_city_code) trade_city_code, remark, ");
        // add by yijb 云南的漏洞，部分信息没有查询展示
        parser.addSQL(" INVOICE_NO,	ADVANCE_PAY , FEE_STAFF_ID, ACCT_ID ,");
        parser.addSQL(" accept_date,cust_name,user_id,cust_id, substr(process_tag_set, 9,1) process_tag_set ");
        parser.addSQL(" from tf_b_trade  ");
        parser.addSQL(" where user_id=:USER_ID");
        parser.addSQL(" and trade_type_code=:TRADE_TYPE_CODE");
        parser.addSQL(" and exec_time > sysdate ");
        parser.addSQL(" order by accept_date desc ");

        IDataset results = UserCommUtil.qryByParse(parser, pagination, Route.getJourDb(BizRoute.getTradeEparchyCode()));//Route.CONN_CRM_CG
        if (results == null || results.isEmpty())
        {
            return null;
        }
        else
        {
            IData result_info = null;
            for (int i = 0; i < results.size(); i++)
            {
                result_info = results.getData(i);
                String trade_type_code = result_info.getString("TRADE_TYPE_CODE", "");
                if (!"".equals(trade_type_code) && "110".equals(trade_type_code))
                {
                    String process_tag_set = result_info.getString("PROCESS_TAG_SET", "");
                    result_info.put("TAG_110", "1");
                    if ("1".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务变更");
                    }
                    else if ("2".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "优惠变更");
                    }
                    else if ("3".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务和优惠变更");
                    }
                    else if ("4".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "其他(含产品变更)");
                    }
                    else
                    {
                        result_info.put("TAG_110", "2");
                    }
                }
                else
                {
                    result_info.put("TAG_110", "2");
                }
            }
        }

        return results;
    }

    public static IDataset queryWideNetUserDiscnt(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
//        parser.addSQL(" select t.discnt_code,d.discnt_name, ");
//        parser.addSQL(" t.partition_id,t.user_id,t.user_id_a,t.discnt_code,t.spec_tag,t.relation_type_code,to_char(t.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,");
//        parser.addSQL(" to_char(t.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,t.remark ");
//        parser.addSQL(" from tf_f_user_discnt t, td_b_discnt d where 1=1 ");
//        parser.addSQL("  AND t.USER_ID = :USER_ID  ");
//        parser.addSQL(" AND t.PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
//        parser.addSQL(" AND t.DISCNT_CODE = d.DISCNT_CODE ");
        parser.addSQL(" select t.discnt_code, ");
        parser.addSQL(" t.partition_id,t.user_id,t.user_id_a,t.discnt_code,t.spec_tag,t.relation_type_code,to_char(t.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,");
        parser.addSQL(" to_char(t.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,t.remark ");
        parser.addSQL(" from tf_f_user_discnt t where 1=1 ");
        parser.addSQL("  AND t.USER_ID = :USER_ID  ");
        parser.addSQL(" AND t.PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
        IDataset outDataList =  Dao.qryByParse(parser);
        if(IDataUtil.isNotEmpty(outDataList)){
        	for(int i = 0 ; i < outDataList.size() ; i++){
        		IData map = outDataList.getData(i);
        		map.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(map.getString("DISCNT_CODE","")));
        	}
        }
        return outDataList;
    }

    public static IDataset queryWideNetUserProductInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
//        parser.addSQL(" select a.product_id,b.product_name,a.brand_code ");
//        parser.addSQL(" from tf_f_user_product a, td_b_product b where 1=1 ");
//        parser.addSQL("  AND a.USER_ID = :USER_ID  ");
//        parser.addSQL(" AND a.PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
//        parser.addSQL(" AND a.product_id = b.product_id ");
//        parser.addSQL(" AND a.end_date > sysdate ");
        parser.addSQL(" select a.product_id,a.brand_code ");
        parser.addSQL(" from tf_f_user_product a where 1=1 ");
        parser.addSQL("  AND a.USER_ID = :USER_ID  ");
        parser.addSQL(" AND a.PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
        parser.addSQL(" AND a.end_date > sysdate ");

        IDataset outDataList = Dao.qryByParse(parser);
        if(IDataUtil.isNotEmpty(outDataList)){
        	for(int i = 0 ; i < outDataList.size() ; i++){
        		IData map = outDataList.getData(i);
        		map.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(map.getString("PRODUCT_ID","")));
        	}
        }
        return outDataList;
    }

    public static IDataset queryWideUserInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_WIDENETHN", "SEL_WIDEUSER_BY_SN", param);
    }

    /**
     * 查询支付平台业务信息
     *
     * @throws Exception
     */
    public IDataset getBankbandInfos(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT to_char(user_band_id) user_band_id, ");
        parser.addSQL("       to_char(user_id) user_id, ");
        parser.addSQL("       bank_card_no, ");
        parser.addSQL("       update_staff_id, ");
        parser.addSQL("       update_depart_id, ");
        parser.addSQL("       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time, ");
        parser.addSQL("       to_char(start_time, 'yyyy-mm-dd hh24:mi:ss') start_time, ");
        parser.addSQL("       to_char(end_time, 'yyyy-mm-dd hh24:mi:ss') end_time, ");
        parser.addSQL("       reck_tag ");
        parser.addSQL("  FROM tf_f_bankband ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("   AND user_id = :USER_ID ");
        parser.addSQL("   AND sysdate BETWEEN start_time AND end_time ");

        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
        return outlist;
    }

    /**
     * 查询个人代扣账户信息
     *
     * @throws Exception
     */
    public IDataset getDeductInfos(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT to_char(register_id) register_id, ");
        parser.addSQL("       eparchy_code, ");
        parser.addSQL("       channel_id, ");
        parser.addSQL("       to_char(user_id) user_id, ");
        parser.addSQL("       to_char(acct_id) acct_id, ");
        parser.addSQL("       bank_account_no, ");
        parser.addSQL("       bank_usrp_id, ");
        parser.addSQL("       pay_name, ");
        parser.addSQL("       pay_address, ");
        parser.addSQL("       pay_post_code, ");
        parser.addSQL("       destroy_tag, ");
        parser.addSQL("       inmode_code, ");
        parser.addSQL("       deduct_type_code, ");
        parser.addSQL("       to_char(deduct_money) deduct_money, ");
        parser.addSQL("       to_char(deduct_step) deduct_step, ");
        parser.addSQL("       to_char(open_time, 'yyyy-mm-dd hh24:mi:ss') open_time, ");
        parser.addSQL("       open_city_code, ");
        parser.addSQL("       open_depart_id, ");
        parser.addSQL("       open_staff_id, ");
        parser.addSQL("       to_char(destroy_time, 'yyyy-mm-dd hh24:mi:ss') destroy_time, ");
        parser.addSQL("       destroy_city_code, ");
        parser.addSQL("       destroy_depart_id, ");
        parser.addSQL("       destroy_staff_id, ");
        parser.addSQL("       remark ");
        parser.addSQL("  FROM tf_f_user_deduct ");
        parser.addSQL(" WHERE user_id = :USER_ID ");
        parser.addSQL("  AND destroy_tag = :DESTROY_TAG ");

        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
        return outlist;
    }

    /**
     * 查询保证金信息
     *
     * @throws Exception
     */
    public IDataset getForegiftInfo(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        // parser.addSQL("select * from tf_f_user_foregift ");
        parser.addSQL("select foregift_code, money from tf_f_user_foregift ");
        parser.addSQL(" where user_id = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
        parser.addSQL(" and money>0 ");
        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
        return outlist;
    }

    /**
     * 查询用户接触信息
     *
     * @throws Exception
     */
    public IDataset qryContactMgr(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select ACCEPT_MONTH,CUST_CONTACT_ID,CUST_ID,CUST_NAME,USER_ID,");
        parser.addSQL(" SERIAL_NUMBER,PRODUCT_ID,EPARCHY_CODE,CITY_CODE,  CONTACT_MODE,");
        parser.addSQL(" IN_MODE_CODE,IN_MEDIA_CODE,CHANNEL_ID,SUB_CHANNEL_ID,START_TIME,");
        parser.addSQL(" FINISH_TIME,CONTACT_STATE,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,");
        parser.addSQL(" RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,REMARK ");
        parser.addSQL(" from tf_b_cust_contact ");
        parser.addSQL(" where 1=1 and start_time >add_months(sysdate,-1) ");
        parser.addSQL(" and CUST_ID = :CUST_ID ");
        parser.addSQL(" and USER_ID = :USER_ID ");
        parser.addSQL(" and START_TIME >= to_date(:START_DATE,'YYYY-MM-DD') ");
        parser.addSQL(" and START_TIME < to_date(:END_DATE,'YYYY-MM-DD')+1 ");
        parser.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH ");
        parser.addSQL(" and CUST_CONTACT_ID = :CUST_CONTACT_ID ");
        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
        return outlist;
    }

    /**
     * 根据手机号码查询投诉信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryCustComplaintInfo(IData data, Pagination pagination) throws Exception
    {

        IData param = new DataMap();

        param.put("ACCEPT_PHONE_CODE", data.getString("SERIAL_NUMBER"));
        param.put("START_DATE", data.getString("START_DATE"));
        param.put("END_DATE", data.getString("END_DATE"));
        // 调接口
        // IDataset dataset = CTSCall.queryFinishedWorkform(data.getString("SERIAL_NUMBER"),
        // data.getString("START_DATE"), data.getString("END_DATE"));
        // IDataset list = (DatasetList)HttpHelper.callHttpSvc(pd, "CRM_Query_FinishedWorkform", yjmap);
        // IDataset dataset = (DatasetList)CSAppCall.callHttp("CRM_Query_FinishedWorkform", param); //正式上线需要放开这条语句
        // only for test start
        IDataset dataset = new DatasetList();
        IData ces = new DataMap();
        ces.put("WORKFORM_ID", "3109121924012512");
        ces.put("TRADE_TYPE_CODE", "110");
        ces.put("TRADE_TYPE_REMARK", "产品变更");
        ces.put("ACCEPT_TIME", "2013-07-25");
        ces.put("FINISH_TIME", "2013-07-25");
        ces.put("CONTENT", "CONTENT");
        ces.put("TOTAL_TIME", "5");
        ces.put("CUST_NAME", "测试号码");
        ces.put("ACCEPT_PHONE_CODE", "");
        ces.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        ces.put("CUST_CLASS", "CUST_CLASS");
        ces.put("BRAND_CODE", "CG01");
        ces.put("IS_RESOLVE", "IS_RESOLVE");
        ces.put("RETURN_FEE", "1000");
        ces.put("SATISFY_DEGREE_CODE", "01");
        ces.put("FINISH_STAFF_ID", "01");
        ces.put("FINISH_DEPART_ID", "01");
        dataset.add(ces);
        // only for test end.
        return dataset;

    }

    /**
     * 根据USER_ID查询邮寄信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryPostInfo(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select * from tf_f_postinfo t where t.id_type=1 ");
        parser.addSQL(" and t.id = :USER_ID   ");
        parser.addSQL(" AND t.PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
        parser.addSQL(" and t.start_date <= sysdate  ");
        parser.addSQL(" and t.end_date >= sysdate  ");

        return UserCommUtil.qryByParse(parser, pagination);
    }

    /**
     * 根据USER_ID查询产品信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryProductInfo(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);
        // 要求产品名称必需和营收系统对应起来，营收取表td_b_product_release产品的发布日期、td_b_product是产品的起至日期,
//        parser.addSQL(" SELECT  t.start_date, t.end_date ,t.product_id,t.remark,p.product_name,p.product_explain, ");
//        parser.addSQL(" p.brand_code ");
//        parser.addSQL(" FROM tf_f_user_product t, td_b_product p ");
//        parser.addSQL(" WHERE 1=1 ");
//        parser.addSQL(" AND t.user_id=:USER_ID ");
//        parser.addSQL(" AND t.partition_id =MOD(:USER_ID,10000) ");
//        parser.addSQL(" AND t.end_date > SYSDATE ");
//        parser.addSQL(" AND t.end_date > t.start_date ");
//        parser.addSQL(" AND p.product_id=t.product_id ");
//        parser.addSQL("  order by  t.start_date desc  ");
        parser.addSQL(" SELECT t.start_date,t.end_date,t.product_id,t.remark,t.main_tag ");
        parser.addSQL(" FROM tf_f_user_product t ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND t.user_id=:USER_ID ");
        parser.addSQL(" AND t.partition_id =MOD(:USER_ID,10000) ");
        parser.addSQL(" AND t.end_date > SYSDATE ");
        parser.addSQL(" AND t.end_date > t.start_date ");
        parser.addSQL("  order by  t.start_date desc  ");
        IDataset out = UserCommUtil.qryByParse(parser, pagination);
        return out;
    }

    /**
     * 根据USER_ID查询所有三产品信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryProductInfoAll(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);
        // 要求产品名称必需和营收系统对应起来，营收取表td_b_product_release产品的发布日期、td_b_product是产品的起至日期,
//        parser.addSQL(" SELECT  t.start_date, t.end_date ,t.product_id,t.remark,p.product_name,p.product_explain, ");
//        parser.addSQL(" p.brand_code ");
//        parser.addSQL(" FROM tf_f_user_product t, td_b_product p ");
//        parser.addSQL(" WHERE 1=1 ");
//        parser.addSQL(" AND t.user_id=:USER_ID ");
//        parser.addSQL(" AND t.partition_id =MOD(:USER_ID,10000) ");
//        parser.addSQL(" AND p.product_id=t.product_id ");
//        parser.addSQL("  order by  t.start_date desc  ");
        parser.addSQL(" SELECT t.start_date,t.end_date,t.product_id,t.remark,t.main_tag ");
        parser.addSQL(" FROM tf_f_user_product t");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND t.user_id=:USER_ID ");
        parser.addSQL(" AND t.partition_id =MOD(:USER_ID,10000) ");
        parser.addSQL("  order by  t.start_date desc  ");
        IDataset out = UserCommUtil.qryByParse(parser, pagination);
        return out;
    }

    /**
     * 根据tf_f_relation_uu中的USER_ID_B获取用户关系数据 用户关系分为用户与用户的关系（比如亲情号码）和用户与集团的关系（用户使用了集团的产品） 用户与用户的关系（关联person、uu、user、vip表）
     * 用户与集团的关系（关联group、uu、user表）
     *
     * @param param
     *            用户ID
     * @return IDataset 用户关系数据
     */
    public IDataset qryRelationInfo(IData data, Pagination pagination) throws Exception
    {
        String userId = data.getString("USER_ID", "");
        if (StringUtils.isBlank(userId))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select a.relation_type_code, a.user_id_a, a.serial_number_a, a.role_code_a, a.user_id_b, a.serial_number_b,'TD_S_RELATION_ROLE_1_'||a.relation_type_code rcode, ");
        parser.addSQL(" a.role_code_b, a.orderno, a.short_code, a.start_date, ");
        parser.addSQL(" a.end_date from Tf_f_Relation_Uu a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.user_id_b = :USER_ID ");
        parser.addSQL(" and a.end_date >= sysdate");
        parser.addSQL(" and a.relation_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_RELATIONQUERY' ) ");
        parser.addSQL(" Union ");
        parser.addSQL(" select a.relation_type_code, a.user_id_a, a.serial_number_a, a.role_code_a, a.user_id_b, a.serial_number_b,'TD_S_RELATION_ROLE_1_'||a.relation_type_code rcode, ");
        parser.addSQL(" a.role_code_b, a.orderno, a.short_code, a.start_date, ");
        parser.addSQL(" a.end_date from Tf_f_Relation_bb a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.user_id_b = :USER_ID ");
        parser.addSQL(" and a.end_date >= sysdate");
        parser.addSQL(" and a.relation_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_RELATIONQUERY' ) ");
        /*
         * parser.addSQL(" select a.relation_type_code, a.user_id_a, a.serial_number_a, a.role_code_a, a.user_id_b, a.serial_number_b,'TD_S_RELATION_ROLE_1_'||a.relation_type_code rcode, "
         * ); parser.addSQL(" a.role_code_b, a.orderno, a.short_code, a.start_date, ");parser.addSQL(
         * " a.end_date, c.cust_name,d.cust_manager_id from Tf_f_Relation_Uu a,tf_F_user b ,tf_F_cust_person c, tf_f_cust_vip d "
         * );parser.addSQL(
         * " where 1=1 and d.remove_tag(+) = '0'  and a.user_id_b = :USER_ID and a.end_date >= sysdate and a.user_id_a = b.user_id and b.cust_id = c.cust_id and d.user_id(+) = b.user_id"
         * );parser.addSQL(
         * " and a.relation_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_RELATIONQUERY' ) "
         * ); parser.addSQL(" Union ");parser.addSQL(
         * " select a.relation_type_code, a.user_id_a, a.serial_number_a, a.role_code_a, a.user_id_b, a.serial_number_b,'TD_S_RELATION_ROLE_1_'||a.relation_type_code rcode, "
         * ); parser.addSQL(" a.role_code_b, a.orderno, a.short_code, a.start_date, ");parser.addSQL(
         * " a.end_date, c.cust_name,c.cust_manager_id from Tf_f_Relation_bb a,tf_F_user b ,tf_f_cust_group c  ");
         * parser.addSQL(" where 1=1 "); parser.addSQL(" and c.remove_tag='0' ");
         * parser.addSQL(" and a.user_id_b = :USER_ID ");
         * parser.addSQL(" and a.end_date >= sysdate and a.user_id_a = b.user_id and b.cust_id = c.cust_id ");
         * parser.addSQL
         * (" and a.relation_type_code not in (select data_id from td_s_static where type_id='USER360VIEW_RELATIONQUERY' ) "
         * );
         */
        IDataset out = UserCommUtil.qryByParse(parser, pagination);
        return out;
    }

    /**
     * 根据USER_ID查询资源信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryResourceInfo(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select * from tf_f_user_res t ");
        parser.addSQL(" where t.USER_ID = :USER_ID   ");
        parser.addSQL(" AND t.PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
        parser.addSQL(" AND SYSDATE BETWEEN t.start_date AND t.end_date ");
        return UserCommUtil.qryByParse(parser, pagination);
    }

    /**
     * 根据USER_ID查询资源全部信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryResourceInfoAll(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("select * from tf_f_user_res t where t.USER_ID = :USER_ID ");
        parser.addSQL(" AND t.PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
        return UserCommUtil.qryByParse(parser, pagination);
    }

    /**
     * 根据USER_ID 查询积分信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryScoreInfo(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select  t.* from tf_f_user_score t where 1=1 ");
        parser.addSQL(" and t.USER_ID = :USER_ID  ");
        parser.addSQL(" and PARTITION_ID = mod(:USER_ID,10000 )");

        return UserCommUtil.qryByParse(parser, pagination);
    }

    /**
     * 根据USER_ID查询服务信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserServiceInfo(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);
//modify by lijun17         parser.addSQL("select t.*,t.REMARK||' '||F_CSM_GETSVCATTR(T.USER_ID,T.SERVICE_ID) REMARK1 from tf_f_user_svc t where 1=1 ");
//        parser.addSQL("  AND t.USER_ID = :USER_ID  ");
//        parser.addSQL("  AND t.partition_id=MOD(:USER_ID, 10000)  ");
        parser.addSQL("select t.*,t.REMARK REMARK1 from tf_f_user_svc t where 1=1 ");
        parser.addSQL("  AND t.USER_ID = :USER_ID  ");
        parser.addSQL("  AND t.partition_id=MOD(:USER_ID, 10000)  ");

        // 2009-6-6 add by caiy
        // 和客户王卫平确认，不管个人服务和集团服务，都要展示
        // 2009-5-25 add by caiy
        // 因为个人服务和集团服务合在一起了，所以增加下面的判断条件
        // parser.addSQL(" AND t.user_id_a = -1 ");

        // parser.addSQL(" AND SYSDATE BETWEEN t.start_date AND t.end_date ");
        // modify by caiy
        // 不选所有记录，则要把预约生效的也要显示出来。
        // 根据结束时间大于当前时间来判断。
        parser.addSQL(" AND t.end_date > SYSDATE ");
        parser.addSQL(" AND t.end_date > t.start_date ");
        parser.addSQL("  order by t.service_id ,t.start_date desc  ");

        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
        //翻译服务名称     将方法F_CSM_GETSVCATTR 转化成Java代码
        if(IDataUtil.isNotEmpty(outlist)){
        	for(int i = 0 ; i < outlist.size() ; i++){
        		String remark1 = "";
        		IData outData = outlist.getData(i);
        		String serviceId = outData.getString("SERVICE_ID");
        		outData.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(serviceId));
        		IDataset userSvcAttrInfos = this.qryUserServiceAttrInfoByUserId(outData.getString("USER_ID"), serviceId, pagination);
        		IDataset userAttrItemaInfos = UpcCall.queryChaSpecByfieldNameAndvalueAndOfferId("S", serviceId, null, null);
        		if(IDataUtil.isNotEmpty(userSvcAttrInfos) && IDataUtil.isNotEmpty(userAttrItemaInfos) && userAttrItemaInfos.size() > 0){
        			for(int j = 0 ; j < userSvcAttrInfos.size() ; j++){
        				String attrCode = userSvcAttrInfos.getData(j).getString("ATTR_CODE","");
        				String attrValue = userSvcAttrInfos.getData(j).getString("ATTR_VALUE","");
        				IDataset attrFieldBInfos = UpcCall.queryChaSpecByfieldNameAndvalueAndOfferId("S", serviceId, attrCode, attrValue);
        				if(IDataUtil.isNotEmpty(attrFieldBInfos)){
        					String attrFieldName = attrFieldBInfos.getData(0).getString("TEXT","");
            				if(StringUtils.isNotBlank(attrFieldName)){
            					if(StringUtils.isNotBlank(remark1)){
                					remark1 = remark1 + "|" + attrFieldName;
                				}else{
                					remark1 = attrFieldName;
                				}
            				}
        				}
        			}
        		}
        		outData.put("REMARK1", outData.getString("REMARK1","") + " " + remark1);
        	}
        }

        return outlist;
    }

    /**
     * 根据USER_ID查询服务信息
     *
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserServiceInfoAll(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        // TODO select * 待处理
        SQLParser parser = new SQLParser(data);
//modify by lijun17        parser.addSQL("select t.*,t.REMARK||' '||F_CSM_GETSVCATTR(T.USER_ID,T.SERVICE_ID) REMARK1 from tf_f_user_svc t where 1=1 ");
//        parser.addSQL("  AND t.USER_ID = :USER_ID  ");
//        parser.addSQL("  AND t.partition_id=MOD(:USER_ID, 10000)  ");
//        parser.addSQL("  order by t.service_id,t.start_date desc  ");
        parser.addSQL("select t.*,t.REMARK REMARK1 from tf_f_user_svc t where 1=1 ");
        parser.addSQL("  AND t.USER_ID = :USER_ID  ");
        parser.addSQL("  AND t.partition_id=MOD(:USER_ID, 10000)  ");
        parser.addSQL("  order by t.service_id,t.start_date desc  ");

        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
        //翻译服务名称 将方法F_CSM_GETSVCATTR 转化成Java代码
        if(IDataUtil.isNotEmpty(outlist)){
        	for(int i = 0 ; i < outlist.size() ; i++){
        		String remark1 = "";
        		IData outData = outlist.getData(i);
        		String serviceId = outData.getString("SERVICE_ID");
        		outData.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(serviceId));
        		IDataset userSvcAttrInfos = this.qryUserServiceAttrInfoByUserId(outData.getString("USER_ID"), serviceId, pagination);
        		IDataset userAttrItemaInfos = UpcCall.queryChaSpecByfieldNameAndvalueAndOfferId("S", serviceId, null, null);
        		if(IDataUtil.isNotEmpty(userSvcAttrInfos) && IDataUtil.isNotEmpty(userAttrItemaInfos) && userAttrItemaInfos.size() > 0){
        			for(int j = 0 ; j < userSvcAttrInfos.size() ; j++){
        				String attrCode = userSvcAttrInfos.getData(j).getString("ATTR_CODE","");
        				String attrValue = userSvcAttrInfos.getData(j).getString("ATTR_VALUE","");
        				IDataset attrFieldBInfos = UpcCall.queryChaSpecByfieldNameAndvalueAndOfferId("S", serviceId, attrCode, attrValue);
        				if(IDataUtil.isNotEmpty(attrFieldBInfos)){
        					String attrFieldName = attrFieldBInfos.getData(0).getString("TEXT","");
            				if(StringUtils.isNotBlank(attrFieldName)){
            					if(StringUtils.isNotBlank(remark1)){
                					remark1 = remark1 + "|" + attrFieldName;
                				}else{
                					remark1 = attrFieldName;
                				}
            				}
        				}
        			}
        		}
        		outData.put("REMARK1", outData.getString("REMARK1","") + " " + remark1);
        	}
        }
        return outlist;
    }

    public IDataset qryUserServiceAttrInfoByUserId(String userId, String elementId, Pagination pagination) throws Exception
    {
    	IData data = new DataMap();
    	data.put("USER_ID", userId);
    	data.put("ELEMENT_ID", elementId);
    	SQLParser parser = new SQLParser(data);
        parser.addSQL("select T.ATTR_CODE,T.ATTR_VALUE from tf_f_user_attr t ");
        parser.addSQL("  where t.USER_ID = :USER_ID  ");
        parser.addSQL("  AND t.element_id= :ELEMENT_ID  ");
        parser.addSQL("  and t.inst_type='S'  ");
        parser.addSQL("  and sysdate between t.start_date and t.end_date ");
        return UserCommUtil.qryByParse(parser, pagination);
    }


    /**
     * 查询业务历史信息--页面业务类型展示 Cg库
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryTradeTypeCode(IData data) throws Exception
    {
        String eparchy_code = data.getString("EPARCHY_CODE", "");
        if ("".equals(eparchy_code))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select TRADE_TYPE_CODE, TRADE_TYPE_CODE|| ' || '||TRADE_TYPE TRADE_TYPE from td_s_tradetype a ");
        parser.addSQL(" Where 1 = 1 ");
        parser.addSQL(" AND EPARCHY_CODE = :EPARCHY_CODE ");
        parser.addSQL(" ORDER BY TRADE_TYPE_CODE ");
        return UserCommUtil.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 查询业务历史信息--页面业务类型展示 Cg库
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryTradeTypeCodeCg(IData data) throws Exception
    {
        String eparchy_code = data.getString("EPARCHY_CODE", "");
        if ("".equals(eparchy_code))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select TRADE_TYPE_CODE, TRADE_TYPE_CODE|| ' || '||TRADE_TYPE TRADE_TYPE from td_s_tradetype a ");
        parser.addSQL(" Where 1 = 1 ");
        parser.addSQL(" AND EPARCHY_CODE = :EPARCHY_CODE ");
        parser.addSQL(" ORDER BY TRADE_TYPE_CODE ");
        return UserCommUtil.qryByParse(parser, Route.CONN_CRM_CG);
    }


    public IDataset qryUserResInfo(IData data) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select * from tf_f_user_res t ");
        parser.addSQL(" where t.USER_ID = :USER_ID   ");
        parser.addSQL(" AND t.PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
        parser.addSQL(" AND SYSDATE BETWEEN t.start_date AND t.end_date ");
        return UserCommUtil.qryByParse(parser);
    }

	/*
	 * 查询客户名称和客户经理
	 */
	public IDataset getCustManagerId(IData in) throws Exception
	{
		SQLParser parser = new SQLParser(in);
		parser.addSQL(" select v.cust_name, v.cust_manager_id ");
		parser.addSQL(" from  tf_f_user  u ,tf_f_cust_vip v ");
		parser.addSQL(" where u.user_id = :USER_ID ");
		parser.addSQL(" and u.user_id = v.user_id ");
		parser.addSQL(" and v.remove_tag = '0' ");
		parser.addSQL(" Union ");
		parser.addSQL(" select g.cust_name, g.cust_manager_id ");
		parser.addSQL(" from  tf_f_user  u ,tf_f_cust_group g ");
		parser.addSQL(" where u.user_id = :USER_ID ");
		parser.addSQL(" and u.cust_id = g.cust_id ");
		parser.addSQL(" and g.remove_tag = '0' ");
		return UserCommUtil.qryByParse(parser);
	}

	/*
	 * 查询客户名称
	 */
	public IDataset getCustName(IData in) throws Exception
	{
		SQLParser parser = new SQLParser(in);
		parser.addSQL(" select c.cust_name ");
		parser.addSQL(" from  tf_f_user  u ,tf_f_customer c ");
		parser.addSQL(" where u.user_id = :USER_ID ");
		parser.addSQL(" and u.cust_id =c.cust_id ");
		parser.addSQL(" and c.remove_tag ='0' ");
		return UserCommUtil.qryByParse(parser);
	}

	/**
	 * 查询条件信息作为在点击标签页查询的条件
	 * @param param
	 * @return
	 * @throws Exception
	 */
    public static IDataset qryUserCityInfo(IData param) throws Exception
    {
		SQLParser parser = new SQLParser(param);
    	parser.addSQL(" select city_code from tf_f_user_city where 1 = 1 ");
    	parser.addSQL(" and user_id = :USER_ID ");
    	parser.addSQL(" AND partition_id =MOD(:USER_ID,10000) ");
    	parser.addSQL(" AND end_date > SYSDATE ");
    	return UserCommUtil.qryByParse(parser);
    }

	/**
	 * 查询电话经理信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
    public static IDataset qryTelManager(IData param) throws Exception
    {
		SQLParser parser = new SQLParser(param);
    	parser.addSQL(" select vip_id,user_id,cust_id,usecust_id,serial_number,eparchy_code,cust_manager_id from tf_f_cust_vip_selfdef where remove_tag = '0' ");
    	parser.addSQL(" and serial_number = :SERIAL_NUMBER ");
    	return UserCommUtil.qryByParse(parser);
    }
    public static IDataset queryWideNetUserDiscntIVR(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BYS_WIDENETIVR", param);
    }

    public static IDataset queryWideNetUserProductInfoIVR(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BYS_WIDENETIVR", param);
    }

    public static IDataset queryFTTHDepositInfoIVR(IData param) throws Exception
    {
    	return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BYS_WIDENETIVR", param);
    }

    public static IDataset qryUserSaleActiveInfoIVR(IData param) throws Exception
    {
    	return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BYS_WIDENETIVR", param);
    }

    public IDataset loadNavButtonConfig(IData param) throws Exception {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT t.TITLE_POSITION, t.BUTTON_TEXT, t.BUTTON_IMG, ");
        parser.addSQL("        t.BUTTON_ORDER, t.PAGE_TITLE, t.PAGE_NAME, ");
        parser.addSQL("        t.PAGE_LISTENER, t.PAGE_PARAMS, t.PAGE_SUBSYS_CODE, ");
        //添加查询REMARK字段，add by zhangxi 20200728
        parser.addSQL("        t.RIGHT_CODE,t.REMARK ");
        parser.addSQL("   FROM TD_S_NAVBUTTONCONFIG t ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("    AND t.FRAME_NAME = :FRAME_NAME ");
        parser.addSQL("    AND t.NAVTAB_NAME = :NAVTAB_NAME ");
        parser.addSQL("    AND t.TITLE_POSITION < :TITLE_COUNT ");
        parser.addSQL("    AND t.WIRELESS_PHONE_PAGE = :TD_USER ");
        parser.addSQL("    AND t.NO_PHONE_KD_PAGE = :KD_USER ");
        parser.addSQL("    AND t.STATE = '1' ");
        parser.addSQL("    ORDER BY t.TITLE_POSITION ASC ");
        return UserCommUtil.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
