
package com.asiainfo.veris.crm.order.soa.person.busi.twobusiarrearagered;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TwoBusiArrearageRedQry extends CSBizBean
{


    public static int insertInfo(IData data) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_TWO_BUSINESS_ARREARAGE_RED","INSERT", data);
    }

    /**
     *
     *Unable to delete directory XXX @return
     * @throws Exception
     */
    public static IDataset queryRedInfoByGroupIdAndTradeTypeCode(IData input,Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(input);
        parser.addSQL(" SELECT T.RED_ID,T.GROUP_ID,T.GROUP_NAME,DECODE(T.REMOVE_TAG,'0','正常','1','停用') REMOVE_TAG ,T.OFFER_CODE,T.OFFER_NAME,T.INSERT_STAFF_ID,T.UPDATE_STAFF_ID,T.REMARK,T.RSRV_STR1,T.RSRV_STR2,T.RSRV_STR3,T.RSRV_STR4,T.RSRV_STR5,TO_CHAR(INSERT_DATE,'yyyy-mm-dd hh24:mi:ss') INSERT_DATE,TO_CHAR(UPDATE_DATE,'yyyy-mm-dd hh24:mi:ss') UPDATE_DATE ");
        parser.addSQL(	" FROM UOP_CRM1.TF_TWO_BUSINESS_ARREARAGE_RED T ");
        parser.addSQL("WHERE 1=1" );
        if(StringUtils.isNotEmpty(input.getString("REMOVE_TAG"))){//删除标记
            if (input.getString("REMOVE_TAG").equals("0")){//基础业务
                parser.addSQL(" and REMOVE_TAG = '0'");

            }
        }
        if(StringUtils.isNotEmpty(input.getString("GROUP_ID"))&&StringUtils.isNotEmpty(input.getString("GROUP_NAME"))){
            parser.addSQL(" and ( t.GROUP_ID =:GROUP_ID or t.GROUP_NAME =:GROUP_NAME ) ");

        }else if (StringUtils.isNotEmpty(input.getString("GROUP_ID"))){
            parser.addSQL(" and  t.GROUP_ID =:GROUP_ID ");

        }

        if(StringUtils.isNotEmpty(input.getString("OFFER_CODE"))){
            parser.addSQL(" AND OFFER_CODE = :OFFER_CODE");
        }
        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));

//        return Dao.qryBySql(parser, input, Route.getJourDb(Route.CONN_CRM_CG));

//        return Dao.qryByCode("TF_TWO_BUSINESS_ARREARAGE_RED", "SEL_BY_GROUPID_TRADETYPECODE", input);

    }
    public static int updateRedInfoTradeAttr(IData param) throws Exception
    {

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE UOP_CRM1.TF_TWO_BUSINESS_ARREARAGE_RED T SET REMOVE_TAG=1, UPDATE_STAFF_ID = :UPDATE_STAFF_ID , UPDATE_DATE=to_date(:UPDATE_DATE,'yyyy-mm-dd hh24:mi:ss') WHERE 1=1 ");
//        sql.append(" AND ( T.red_id=:RED_IDS  ) ");
        sql.append(" AND T.red_id IN(" + param.getString("RED_IDS") + ")");

        int updateResulte= Dao.executeUpdate(sql, param);

//        int updateResulte=Dao.executeUpdateByCodeCode("TF_TWO_BUSINESS_ARREARAGE_RED","UPDATE", param);;

        return updateResulte;
    }

    /**
     * 查询提供
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset queryRedInfoByGroupIdAndTradeTypeCode(IData input) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT T.* ");
        sql.append(	" FROM UOP_CRM1.TF_TWO_BUSINESS_ARREARAGE_RED T ");
        sql.append("WHERE 1=1" );
        if(StringUtils.isNotEmpty(input.getString("REMOVE_TAG"))){//删除标记
            if (input.getString("REMOVE_TAG").equals("0")){//基础业务
                sql.append(" and REMOVE_TAG = '0'");

            }
        }
        if(StringUtils.isNotEmpty(input.getString("GROUP_ID"))){
            sql.append(" and t.GROUP_ID =:GROUP_ID");
        }
        if(StringUtils.isNotEmpty(input.getString("GROUP_NAME"))){
            sql.append(" or t.GROUP_NAME =:GROUP_NAME");
        }
        if(StringUtils.isNotEmpty(input.getString("OFFER_CODE"))){
            sql.append(" AND OFFER_CODE = :OFFER_CODE");
        }
        return Dao.qryBySql(sql, input, Route.getJourDb(Route.CONN_CRM_CG));

//        return Dao.qryByCode("TF_TWO_BUSINESS_ARREARAGE_RED", "SEL_BY_GROUPID_TRADETYPECODE", input);

    }







}
