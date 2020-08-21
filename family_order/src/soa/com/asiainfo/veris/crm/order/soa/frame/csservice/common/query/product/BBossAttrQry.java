
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BBossAttrQry
{

    /**
     * 根据attrcode查询TD_S_BBOSS_ATTR参数表的配置信息
     * 
     * @author ft
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrByAttrCode(String attrCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ATTR_CODE", attrCode);

        StringBuilder sql = new StringBuilder();

        sql.append("select t.PRODUCT_ID, ");
        sql.append("t.ATTR_CODE, ");
        sql.append("t.ATTR_NAME, ");
        sql.append("t.DEFAULT_VALUE, ");
        sql.append("t.BIZ_TYPE, ");
        sql.append("t.PRIORITY, ");
        sql.append("t.EDIT_TYPE, ");
        sql.append("t.FORMAT, ");
        sql.append("t.VISIABLE, ");
        sql.append("t.MANDATORY, ");
        sql.append("t.READONLY, ");
        sql.append("t.ONINIT_JS, ");
        sql.append("t.ONCHANGE_JS, ");
        sql.append("t.ONSUBMIT_JS, ");
        sql.append("t.UPDATE_TIME, ");
        sql.append("t.UPDATE_STAFF_ID, ");
        sql.append("t.EPARCHY_CODE, ");
        sql.append("t.SHOW_INDEX, ");
        sql.append("t.REMARK, ");
        sql.append("t.GROUPATTR, ");
        sql.append("t.RSRV_STR1, ");
        sql.append("t.RSRV_STR2, ");
        sql.append("t.RSRV_STR3, ");
        sql.append("t.RSRV_STR4, ");
        sql.append("t.RSRV_STR5, ");
        sql.append("t.RSRV_STR6, ");
        sql.append("t.RSRV_STR7, ");
        sql.append("t.RSRV_STR8, ");
        sql.append("t.RSRV_STR9, ");
        sql.append("t.RSRV_STR10, ");
        sql.append("t.FRONT_PART, ");
        sql.append("t.AFTER_PART ");
        sql.append("from TD_S_BBOSS_ATTR t ");
        sql.append("Where t.attr_code = :ATTR_CODE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据属性组编号查询属性组包含的所有属性，并且按照显示顺序INDEX由小到大输出
     * 
     * @param groupAttr
     * @param bizType
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrByGroupAttrBizType(String groupAttr, String bizType) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUPATTR", groupAttr);
        param.put("BIZ_TYPE", bizType);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select t.PRODUCT_ID, ");
        parser.addSQL("t.ATTR_CODE, ");
        parser.addSQL("t.ATTR_NAME, ");
        parser.addSQL("t.DEFAULT_VALUE, ");
        parser.addSQL("t.BIZ_TYPE, ");
        parser.addSQL("t.PRIORITY, ");
        parser.addSQL("t.EDIT_TYPE, ");
        parser.addSQL("t.FORMAT, ");
        parser.addSQL("t.VISIABLE, ");
        parser.addSQL("t.MANDATORY, ");
        parser.addSQL("t.READONLY, ");
        parser.addSQL("t.ONINIT_JS, ");
        parser.addSQL("t.ONCHANGE_JS, ");
        parser.addSQL("t.ONSUBMIT_JS, ");
        parser.addSQL("t.UPDATE_TIME, ");
        parser.addSQL("t.UPDATE_STAFF_ID, ");
        parser.addSQL("t.EPARCHY_CODE, ");
        parser.addSQL("t.SHOW_INDEX, ");
        parser.addSQL("t.REMARK, ");
        parser.addSQL("t.GROUPATTR, ");
        parser.addSQL("t.RSRV_STR1, ");
        parser.addSQL("t.RSRV_STR2, ");
        parser.addSQL("t.RSRV_STR3, ");
        parser.addSQL("t.RSRV_STR4, ");
        parser.addSQL("t.RSRV_STR5, ");
        parser.addSQL("t.RSRV_STR6, ");
        parser.addSQL("t.RSRV_STR7, ");
        parser.addSQL("t.RSRV_STR8, ");
        parser.addSQL("t.RSRV_STR9, ");
        parser.addSQL("t.RSRV_STR10 ");
        parser.addSQL("from TD_S_BBOSS_ATTR t ");
        parser.addSQL("Where GROUPATTR = :GROUPATTR ");
        parser.addSQL("and BIZ_TYPE = :BIZ_TYPE ");
        parser.addSQL("order by SHOW_INDEX ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品编码和业务类型查询bbossattr表配置信息 2014-7-24 code_code翻译
     * 
     * @author ft
     * @param productSpecNumber
     * @param bizType
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrByPospecBiztype(String productSpecNumber, String bizType) throws Exception
    {

        IData param = new DataMap();
        param.put("PRODUCTSPECNUMBER", productSpecNumber);
        param.put("BIZ_TYPE", bizType);

        StringBuilder sql = new StringBuilder();

        sql.append("select t.PRODUCT_ID, ");
        sql.append("t.ATTR_CODE, ");
        sql.append("t.ATTR_NAME, ");
        sql.append("t.DEFAULT_VALUE, ");
        sql.append("t.BIZ_TYPE, ");
        sql.append("t.PRIORITY, ");
        sql.append("t.EDIT_TYPE, ");
        sql.append("t.FORMAT, ");
        sql.append("t.VISIABLE, ");
        sql.append("t.MANDATORY, ");
        sql.append("t.READONLY, ");
        sql.append("t.ONINIT_JS, ");
        sql.append("t.ONCHANGE_JS, ");
        sql.append("t.ONSUBMIT_JS, ");
        sql.append("t.UPDATE_TIME, ");
        sql.append("t.UPDATE_STAFF_ID, ");
        sql.append("t.EPARCHY_CODE, ");
        sql.append("t.SHOW_INDEX, ");
        sql.append("t.REMARK, ");
        sql.append("t.GROUPATTR, ");
        sql.append("t.RSRV_STR1, ");
        sql.append("t.RSRV_STR2, ");
        sql.append("t.RSRV_STR3, ");
        sql.append("t.RSRV_STR4, ");
        sql.append("t.RSRV_STR5, ");
        sql.append("t.RSRV_STR6, ");
        sql.append("t.RSRV_STR7, ");
        sql.append("t.RSRV_STR8, ");
        sql.append("t.RSRV_STR9, ");
        sql.append("t.RSRV_STR10, ");
        sql.append("t.front_part, ");
        sql.append("t.after_part ");
        sql.append("from TD_S_BBOSS_ATTR t ");
        sql.append("Where PRODUCT_ID = :PRODUCTSPECNUMBER ");
        sql.append("and BIZ_TYPE = :BIZ_TYPE ");
        sql.append("order by SHOW_INDEX ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品编码、操作类型、业务类型查询bbossattr配置表信息 2014-7-24 code_code转SQL
     * 
     * @author ft
     * @param productId
     * @param operType
     * @param bizType
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrByPospecOpertypeBiztype(String productId, String operType, String bizType) throws Exception
    {

        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("OPERTYPE", operType);
        param.put("BIZ_TYPE", bizType);

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT t.product_id, ");
        sql.append("t.attr_code, ");
        sql.append("t.attr_name, ");
        sql.append("t.default_value, ");
        sql.append("t.biz_type, ");
        sql.append("t.priority, ");
        sql.append("t.edit_type, ");
        sql.append("t.format, ");
        sql.append("t.visiable, ");
        sql.append("t.mandatory, ");
        sql.append("t.readonly, ");
        sql.append("t.oninit_js, ");
        sql.append("t.onchange_js, ");
        sql.append("t.onsubmit_js, ");
        sql.append("t.update_time, ");
        sql.append("t.update_staff_id, ");
        sql.append("t.eparchy_code, ");
        sql.append("t.show_index, ");
        sql.append("t.remark, ");
        sql.append("t.groupattr, ");
        sql.append("t.rsrv_str1, ");
        sql.append("t.rsrv_str2, ");
        sql.append("t.rsrv_str3, ");
        sql.append("t.rsrv_str4, ");
        sql.append("t.rsrv_str5, ");
        sql.append("t.rsrv_str6, ");
        sql.append("t.rsrv_str7, ");
        sql.append("t.rsrv_str8, ");
        sql.append("t.rsrv_str9, ");
        sql.append("t.rsrv_str10 ");
        sql.append("FROM td_s_bboss_attr t ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND (t.visiable = :OPERTYPE or t.visiable like :OPERTYPE || ',%' or ");
        sql.append("t.visiable like '%,' || :OPERTYPE or ");
        sql.append("t.visiable like '%,' || :OPERTYPE || ',%') ");
        sql.append("AND product_id = :PRODUCT_ID ");
        sql.append("AND biz_type = :BIZ_TYPE ");
        sql.append("ORDER BY show_index ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    }
    
    /**
     * @Title: qryBBossAttrMandatoryByOperCodeAndAttrCode 查询该属性(ATTR_CODE)在该操作(OPER_CODE)时是否必填
     * @param attrCode
     * @param operCode
     * @return
     * @throws Exception
     * @return IDataset
     * @author chenkh
     * @time 2014年12月26日
     */
    public static IDataset qryBBossAttrMandatoryByOperCodeAndAttrCode(String attrCode, String operCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ATTR_CODE", attrCode);
        inparam.put("OPER_CODE", operCode);
        SQLParser parser = new SQLParser(inparam);
        parser.addSQL("SELECT T.PRODUCT_ID, ");
        parser.addSQL("T.ATTR_CODE, ");
        parser.addSQL("T.ATTR_NAME, ");
        parser.addSQL("T.DEFAULT_VALUE, ");
        parser.addSQL("T.BIZ_TYPE, ");
        parser.addSQL("T.PRIORITY, ");
        parser.addSQL("T.EDIT_TYPE, ");
        parser.addSQL("T.FORMAT, ");
        parser.addSQL("T.VISIABLE, ");
        parser.addSQL("T.MANDATORY, ");
        parser.addSQL("T.READONLY, ");
        parser.addSQL("T.ONINIT_JS, ");
        parser.addSQL("T.ONCHANGE_JS, ");
        parser.addSQL("T.ONSUBMIT_JS, ");
        parser.addSQL("T.UPDATE_TIME, ");
        parser.addSQL("T.UPDATE_STAFF_ID, ");
        parser.addSQL("T.EPARCHY_CODE, ");
        parser.addSQL("T.SHOW_INDEX, ");
        parser.addSQL("T.REMARK, ");
        parser.addSQL("T.GROUPATTR, ");
        parser.addSQL("T.RSRV_STR1, ");
        parser.addSQL("T.RSRV_STR2, ");
        parser.addSQL("T.RSRV_STR3, ");
        parser.addSQL("T.RSRV_STR4, ");
        parser.addSQL("T.RSRV_STR5, ");
        parser.addSQL("T.RSRV_STR6, ");
        parser.addSQL("T.RSRV_STR7, ");
        parser.addSQL("T.RSRV_STR8, ");
        parser.addSQL("T.RSRV_STR9, ");
        parser.addSQL("T.RSRV_STR10 ");
        parser.addSQL("FROM TD_S_BBOSS_ATTR T ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND T.ATTR_CODE = :ATTR_CODE ");
        parser.addSQL("AND (T.MANDATORY LIKE :OPER_CODE || ',%' OR ");
        parser.addSQL("T.MANDATORY LIKE '%,' || :OPER_CODE OR ");
        parser.addSQL("T.MANDATORY LIKE '%,' || :OPER_CODE || ',%') ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    
    /**
     * 根据departCode查询代理商记录是否存在
     * 
     * @param attrCode
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public static IDataset qryBBossDepartCode(String departCode) throws Exception
    {
        IData param = new DataMap();
        param.put("DEPART_CODE", departCode);
        StringBuilder sql = new StringBuilder();
        sql.append("select * from TD_M_DEPART ");
        sql.append("Where DEPART_CODE = :DEPART_CODE ");

        return Dao.qryBySql(sql, param, Route.CONN_SYS);
    }
}
