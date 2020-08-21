
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class TradeAttrInfoQry
{
    private static final Logger log = Logger.getLogger(TradeAttrInfoQry.class);

    /**
     * chenyi 删除老台帐信息 2014-3-4
     */
    public static void delByAttrCode(IData inparam) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "DEL_BY_TRADE_INST_CODE_VALUE", inparam, Route.getJourDb(Route.CONN_CRM_CG));
    }
    /**
     * 更新trade表信息
     * @param inparam
     * @throws Exception
     */
    public static void updateByTradeId(IData map) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_BY_TRADEID", map, Route.getJourDb(Route.CONN_CRM_CG));
    }
    /**
     * 获取trade表信息
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTrade(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE", "SEL_ALL_TRADE_BY_TRADEID", params);
    }


    /**
     * 根据tradeId和modifytag删除tf_b_trade_attr表数据
     * 
     * @param tradeId
     * @param modifyTag
     * @throws Exception
     */
    public static void deleteAttrByTradeIdModifyTag(String tradeId, String modifyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("MODIFY_TAG", modifyTag);

        StringBuilder sql = new StringBuilder();

        sql.append("DELETE tf_b_trade_attr t ");
        sql.append("where TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("AND MODIFY_TAG = :MODIFY_TAG ");

        Dao.executeUpdate(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 删除老ICB参数 chenyi 2014-3-4
     * 
     * @throws Exception
     */
    public static void delTradeAttrInfo(IData inparam) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "DEL_BY_TRADEID_ATTRCODE", inparam, Route.getJourDb());
    }

    /**
     * 根据tradeId查询所有的用户属性备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakAttrByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_ATTR_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * 根据业务流水号，实例类型，属性编码，用户标识查询有效业务台帐用户实例属性子表
     * 
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IDataset getAttrByTradeID(String trade_id, String modify_tag) throws Exception
    {

        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("MODIFY_TAG", modify_tag);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_TRADEID", param, Route.getJourDb());
    }

    /**
     * @Description: 查询资费ICB参数的台帐信息
     * @author liuxx3
     * @date 2014-06-21
     */
    public static IDataset getDiscntAttrByTradeId(String tradeId) throws Exception
    {

        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_DISPARAM_BY_TRADEID", param, Route.getJourDb());
    }

    /*
     * @description 根据属性编码、属性状态及属性组编号查询相关属性
     * @author xunyl
     * @date 2013-09-14
     */
    public static IDataset getTradeAttrByAttrCode(String attrCode, String modifyTag, String attrGroup, String tradeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("ATTR_CODE", attrCode);
        inparams.put("MODIFY_TAG", modifyTag);
        inparams.put("RSRV_STR4", attrGroup);

        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_ATTRCODE_MODIFYTAG", inparams, Route.getJourDb());
    }

    /**
     * 根据属性编码、属性状态及台账id
     * 
     * @param attrCode
     * @param modifyTag
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IData getTradeAttrByAttrCodeAndTradeIdAndModifyTag(String tradeId, String modifyTag, String attrCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("ATTR_CODE", attrCode);
        inparams.put("MODIFY_TAG", modifyTag);

        IDataset tradeAttrList = Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_ATTRCODE_MODIFYTAG_TRADEID", inparams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

        if (IDataUtil.isEmpty(tradeAttrList))
        {
            return new DataMap();
        }

        return tradeAttrList.getData(0);
    }

    /*
     * @description 根据属性编码、属性状态及属性组编号查询相关属性
     * @author xunyl
     * @date 2013-09-14
     */
    public static IDataset getTradeAttrByAttrCodeForGrp(String attrCode, String modifyTag, String attrGroup, String tradeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("ATTR_CODE", attrCode);
        inparams.put("MODIFY_TAG", modifyTag);
        inparams.put("RSRV_STR4", attrGroup);

        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_ATTRCODE_MODIFYTAG", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 获取属性台账
     * 
     * @param trade_id
     * @param attr_code
     * @param inst_id
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrByInstId(String trade_id, String attr_code, String inst_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("ATTR_CODE", attr_code);
        inparams.put("INST_ID", inst_id);

        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_TRADE_INST_ATTR_CODE", inparams, Route.getJourDb());
    }

    /**
     * 获取属性台账
     * 
     * @param trade_id
     * @param attr_code
     * @param inst_id
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrByInstType(String trade_id, String inst_type) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("INST_TYPE", inst_type);

        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_TRADE_INSTTYPE", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrByInstTypeUserId(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_TRADE_INSTTYPE_USERID", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 获取属性子台帐
     * 
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrByTradeId(String trade_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);

        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_ALL_BY_TRADE", inparams,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 根据attr_code,trade_id查询TRAD_ATTR信息
     * 
     * @param trade_id
     *            业务流水号
     * @param attr_code
     *            属性编码
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrByTradeIDandAttrCode(String trade_id, String attr_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("ATTR_CODE", attr_code);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_ATTR_CODE", param, pagination, Route.getJourDb());
    }

    // todo 无SQL
    /**
     * 根据user_id,inst_id查TF_B_TRADE_ATTR
     * 
     * @author shixb
     * @version 创建时间：2009-5-30 下午09:49:06
     */
    public static IDataset getTradeAttrByUserIdInstId(IData inparams) throws Exception
    {
        // TODO
        IDataset dataset = Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_USERID_INSTID", inparams, Route.getJourDb());
        return dataset;
    }

    /**
     * 获取属性台账 chenyi
     * 
     * @param trade_id
     * @param attr_code
     * @param inst_id
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrInfoByInstType(String trade_id, String inst_type) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("INST_TYPE", inst_type);

        SQLParser parser = new SQLParser(inparams);
        parser
                .addSQL("Select to_char(TRADE_ID) TRADE_ID, ACCEPT_MONTH, to_char(USER_ID) USER_ID, INST_TYPE, to_char(INST_ID) INST_ID, ATTR_CODE, ATTR_VALUE, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, MODIFY_TAG, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2||RSRV_STR5 as RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3");
        parser.addSQL(" FROM  TF_B_TRADE_ATTR ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" AND TRADE_ID= TO_NUMBER( :TRADE_ID) ");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR( :TRADE_ID,5,2))");
        parser.addSQL(" AND INST_TYPE = :INST_TYPE ");
        parser.addSQL(" AND END_DATE > SYSDATE");
        parser.addSQL(" AND ATTR_VALUE IS NOT NULL");
        parser.addSQL(" AND (RSRV_STR1 IS NULL OR RSRV_STR1 NOT IN('M'))");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * chenyi 查询有效的属性
     * 
     * @param trade_id
     * @param attr_code
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrInfoByTradeIDAttrCode(String trade_id, String attr_code, String modifyTag) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("ATTR_CODE", attr_code);
        inparams.put("MODIFY_TAG", modifyTag);
        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT to_char(TRADE_ID) TRADE_ID, ");
        sql.append("ACCEPT_MONTH, ");
        sql.append("to_char(USER_ID) USER_ID, ");
        sql.append("INST_TYPE, ");
        sql.append("to_char(INST_ID) INST_ID, ");
        sql.append("ATTR_CODE, ");
        sql.append("ATTR_VALUE, ");
        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("MODIFY_TAG, ");
        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK, ");
        sql.append("RSRV_NUM1, ");
        sql.append("RSRV_NUM2, ");
        sql.append("RSRV_NUM3, ");
        sql.append("to_char(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, ");
        sql.append("RSRV_STR2, ");
        sql.append("RSRV_STR3, ");
        sql.append("RSRV_STR4, ");
        sql.append("RSRV_STR5, ");
        sql.append("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("RSRV_TAG1, ");
        sql.append("RSRV_TAG2, ");
        sql.append("RSRV_TAG3 ");
        sql.append("FROM TF_B_TRADE_ATTR ");
        sql.append("WHERE trade_id = :TRADE_ID ");
        sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("AND ATTR_CODE = :ATTR_CODE ");
        sql.append("AND MODIFY_TAG =:MODIFY_TAG ");
        sql.append("AND END_DATE>SYSDATE ");
        return Dao.qryBySql(sql, inparams, Route.getJourDb());
    }

    /**
     * 根据业务流水号，实例类型，属性编码，用户标识查询有效业务台帐用户实例属性子表
     * 
     * @param trade_id
     * @param inst_type
     * @param attr_code
     * @param user_id
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByTradeID(IData param) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_TRADEID_INSTID", param, Route.getJourDb());
    }

    /**
     * 根据业务流水号，实例类型，实例标识，用户标识查询业务台帐用户实例属性子表
     * 
     * @param trade_id
     *            业务流水号
     * @param inst_type
     *            实例类型
     * @param inst_id
     *            实例标识
     * @param user_id
     *            用户标识
     * @param bboss_flag
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByTradeIDInstid(String trade_id, String inst_type, String inst_id, String user_id, String bboss_flag, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("INST_TYPE", inst_type);
        param.put("INST_ID", inst_id);
        param.put("USER_ID", user_id);
        IDataset dataset = new DatasetList();

        if ("1".equals(bboss_flag))// 读取注销台帐
        {
            dataset = Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_TRADEID_INSTID_ZXDATA", param, Route.getJourDb());
        }
        else
        {
            dataset = Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_TRADEID_INSTID", param, page, Route.getJourDb());
        }

        return dataset;
    }

    /**
     * 根据业务流水号，实例类型，属性编码，用户标识查询有效业务台帐用户实例属性子表
     * 
     * @author liuxx3
     * @date 2014 - 08 -09
     */
    public static IDataset getUserAttrByTradeIDNew(String tradeId, String userId, String instType, String RelaInstId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", userId);
        param.put("INST_TYPE", "P");
        param.put("RELA_INST_ID", RelaInstId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT ");
        parser.addSQL("to_char(USER_ID) USER_ID, ");
        parser.addSQL("INST_TYPE, ");
        parser.addSQL("to_char(INST_ID) INST_ID, ");
        parser.addSQL("ATTR_CODE, ");
        parser.addSQL("ATTR_VALUE, ");
        parser.addSQL("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("UPDATE_STAFF_ID, ");
        parser.addSQL("UPDATE_DEPART_ID, ");
        parser.addSQL("REMARK, ");
        parser.addSQL("RSRV_NUM1, ");
        parser.addSQL("RSRV_NUM2, ");
        parser.addSQL("RSRV_NUM3, ");
        parser.addSQL("to_char(RSRV_NUM4) RSRV_NUM4, ");
        parser.addSQL("to_char(RSRV_NUM5) RSRV_NUM5, ");
        parser.addSQL("RSRV_STR1, ");
        parser.addSQL("RSRV_STR2, ");
        parser.addSQL("RSRV_STR3, ");
        parser.addSQL("RSRV_STR4, ");
        parser.addSQL("RSRV_STR5, ");
        parser.addSQL("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL("RSRV_TAG1, ");
        parser.addSQL("RSRV_TAG2, ");
        parser.addSQL("RSRV_TAG3 ");
        parser.addSQL("FROM TF_B_TRADE_ATTR ");
        parser.addSQL("WHERE TRADE_ID = :TRADE_ID ");
        parser.addSQL("AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        parser.addSQL("AND INST_TYPE = :INST_TYPE ");
        parser.addSQL("AND RELA_INST_ID = :RELA_INST_ID ");
        parser.addSQL("AND USER_ID = :USER_ID ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查出台帐表里面的相关元素属性 chenyi 13-10-26
     * 
     * @param userId
     * @param inst_type
     * @param inst_id
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByUserIdInstid(String tradeId, String inst_type, String inst_id) throws Exception
    {

        IData idata = new DataMap();
        idata.put("TRADE_ID", tradeId);
        idata.put("INST_TYPE", inst_type);
        idata.put("RELA_INST_ID", inst_id);
        IDataset dataset = Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_ATTRINFO_BYTRAEID_INTYPE_RELAINSTID", idata, Route.getJourDb());
        return dataset;
    }

    /**
     * 根据业务流水号，实例类型，属性编码，用户标识查询有效业务台帐用户实例属性子表
     * 
     * @param trade_id
     * @param inst_type
     * @param attr_code
     * @param user_id
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductAttrValuebyTradeIdAndUserId(String trade_id, String inst_type, String attr_code, String user_id, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("INST_TYPE", inst_type);
        param.put("ATTR_CODE", attr_code);
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_P_TRADEID_TYPE_CODE", param, page, Route.getJourDb());
    }

    /**
     * 根据业务流水号，实例类型，属性编码，用户标识查询失效业务台帐用户实例属性子表
     * 
     * @param trade_id
     * @param inst_type
     * @param attr_code
     * @param user_id
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductAttrValuebyTradeIdAndUserId_ENDDATA(String trade_id, String inst_type, String attr_code, String user_id, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("INST_TYPE", inst_type);
        param.put("ATTR_CODE", attr_code);
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_P_TRADEID_TYPE_CODE_ZXDATA", param, page, Route.getJourDb());
    }

    /**
     * chenyi 2014-03-04 将数据插入attr表
     * 
     * @param param
     * @throws Exception
     */

    public static void insertAttrInfo(IData param) throws Exception
    {

        Dao.insert("TF_B_TRADE_ATTR", param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据属性编号和属性值查询属性台账表，判断该属性值之前是否被占用
     * 
     * @param attrCode
     * @param attrValue
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeAttrByAttrCodeAttrValue(String attrCode, String attrValue) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ATTR_CODE", attrCode);
        inparams.put("ATTR_VALUE", attrValue);

        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_ATTRCODE_ATTRVALUE", inparams, Route.getJourDb());
    }

    // todo
    /**
     * @Description: 根据TRADE_ID INSTTYPE,USER_ID查询出TF_B_TRADE_ATTR表的数据 查询出有效的属性
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeGrpAttrByTradeId(String trade_id, String userId, String inst_type, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("USER_ID", userId);
        param.put("INST_TYPE", inst_type);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" Select ");
        parser
                .addSQL(" to_char(TRADE_ID) TRADE_ID, ACCEPT_MONTH, to_char(USER_ID) USER_ID, INST_TYPE, to_char(INST_ID) INST_ID, ATTR_CODE, ATTR_VALUE || RSRV_STR4 as ATTR_VALUE, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, MODIFY_TAG, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2||RSRV_STR5 as RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
        parser.addSQL("  FROM   TF_B_TRADE_ATTR  ");
        parser.addSQL(" WHERE TRADE_ID= TO_NUMBER(:TRADE_ID) ");
        parser.addSQL("  AND   ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        parser.addSQL(" AND   INST_TYPE = :INST_TYPE ");
        parser.addSQL("  and   USER_ID=:USER_ID");
        parser.addSQL(" AND START_DATE<sysdate ");
        parser.addSQL(" AND END_DATE>sysdate ");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * @Description 更改属性表中的状态
     * @author chenyi
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */

    public static void updateBbossAttrState(IData param) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPD_MODFSTR1_BY_PK", param, Route.getJourDb());
    }

    /**
     * @Description 根据user表中更改属性表中的状态
     * @author chenyi
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public static void updateBbossAttrStateByUID(String rsrv_str1, String user_id, String attr_code, String modify_tag, String attr_value) throws Exception
    {

        IData param = new DataMap();
        param.put("RSRV_STR1", rsrv_str1);
        param.put("USER_ID", user_id);
        param.put("ATTR_CODE", attr_code);
        param.put("MODIFY_TAG", modify_tag);
        param.put("ATTR_VALUE", attr_value);

        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPDATE_STATE_BYUSERID", param, Route.getJourDb());
    }

    /**
     * @Description 更改属性表中属性组状态的状态
     * @author chenyi
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */

    public static void updateBbossGroupAttrState(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_ATTR");
        parser.addSQL(" set MODIFY_TAG = :MODIFY_TAG ");
        parser.addSQL("  ,end_date=Sysdate");
        parser.addSQL("  ,rsrv_str1= :RSRV_STR1");
        parser.addSQL("  ,IS_NEED_PF= :IS_NEED_PF");
        parser.addSQL("  ,update_time=Sysdate");
        parser.addSQL("  ,rsrv_str3= :ATTR_NAME");
        parser.addSQL("  ,rsrv_str5= :RSRV_STR5");
        parser.addSQL("  where 1=1");
        parser.addSQL(" and TRADE_ID = TO_NUMBER(:TRADE_ID)");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))");
        parser.addSQL(" AND INST_TYPE = :INST_TYPE");
        parser.addSQL(" AND ATTR_CODE = :ATTR_CODE");
        parser.addSQL(" AND ATTR_VALUE = :ATTR_VALUE");
        parser.addSQL(" AND  rsrv_str4= :RSRV_STR4");
        parser.addSQL(" AND MODIFY_TAG='0'");
        parser.addSQL(" AND END_DATE >SYSDATE");
        Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @description 更新产品参数表的回收标记(RSRV_STR1字段)
     * @author xunyl
     * @date 2014-08-29
     */
    public static void updateDelFlag(String tradeId, String relaInstId, String instType, String delFlag, String isNeedPf) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("RELA_INST_ID", relaInstId);
        inparams.put("INST_TYPE", instType);
        inparams.put("RSRV_STR1", delFlag);
        inparams.put("IS_NEED_PF", isNeedPf);
        StringBuilder sql = new StringBuilder(1000);

        sql.append("update TF_B_TRADE_ATTR b ");
        sql.append("set rsrv_str1  = :RSRV_STR1, ");
        sql.append("is_need_pf= :IS_NEED_PF ");
        sql.append("where b.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("and b.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("and b.inst_type = :INST_TYPE ");
        sql.append("and b.rela_inst_id = :RELA_INST_ID ");

        Dao.executeUpdate(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 更新属性开始时间
     * 
     * @author chenzm
     * @param trade_id
     * @param start_date
     * @throws Exception
     */
    public static void updateStartDate(String trade_id, String start_date) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("START_DATE", start_date);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPD_STARTDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * chenyi IS_NEND_PF是否发指令： 1或者是空： 发指令 0： 不发指令
     * 
     * @param trade_id
     * @throws Exception
     */
    public static void updIsSendPfTradeid(String trade_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPD_ISSENDPF_BY_PK", param, Route.getJourDb());
    }

    /**
     * chenyi 更新modify
     * 
     * @param trade_id
     * @throws Exception
     */
    public static void updModfyByTradeid(String trade_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPDATEMODEFYTAG_BY_PK", param, Route.getJourDb());
    }

    /**
     * chenyi
     * 
     * @param trade_id
     * @param attr_code
     * @param pagination
     * @return
     * @throws Exception
     */
    public static void updStr1ByTradeid(String trade_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPD_STR1_BY_TRADEID", param, Route.getJourDb());
    }

    // todo
    /**
     * 获取台帐优惠属性表
     * 
     * @param iData
     * @return
     */
    public IDataset getTradeAttrInfo(String tradeId, String instId, String userId, Pagination pagination) throws Exception
    {

        if (tradeId == null || "".equals(tradeId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103);
        }
        if (instId == null || "".equals(instId))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_73);
        }
        if (userId == null || "".equals(userId))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_582);
        }
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("INST_TYPE", "D");
        params.put("TRADE_ID", instId);
        params.put("USER_ID", userId);
        try
        {
            IDataset iDataset = Dao.qryByCodeParser("TF_B_TRADE_ATTR", "SEL_BY_ATTRID", params, pagination, Route.getJourDb(Route.CONN_CRM_CG));
            if (!IDataUtil.isNotEmpty(iDataset))
            {
                ((IData) iDataset.get(0)).put("X_RECORDNUM", iDataset.size());
            }
            return iDataset;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(TradeException.CRM_TRADE_35);
            return null;
        }
    }
    
    /**
     * @Description:通过资费ID查询资费属性信息
     * @author: chenmw3
     * @date: 2017-4-21 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrByDiscntCodeAttrValue(String userId, String discntCode,String attrValue) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", "D");
        param.put("DISCNT_CODE", discntCode);
        param.put("ATTR_VALUE", attrValue);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_DISCNTCODE_ATTRVALUE", param, Route.getJourDb());
    }
    
    /**
     * @Description:通过资费生效时间范围查询资费属性信息
     * @author: chenmw3
     * @date: 2017-4-21 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrByDiscntCodeAttrValueDateScope(String userId, String discntCode,String attrValue,String qryStartDate, String qryEndDate) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", "D");
        param.put("DISCNT_CODE", discntCode);
        param.put("ATTR_VALUE", attrValue);
        param.put("QRY_START_DATE", qryStartDate);
        param.put("QRY_END_DATE", qryEndDate);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_DISCNTCODE_ATTRVALUE_DATESCOPE", param, Route.getJourDb());
    }
    
    /**
     * @Description:通过资费当前时间查询资费属性信息
     * @author: chenmw3
     * @date: 2017-4-21 
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAttrByDiscntCodeAttrValueThisDate(String userId, String discntCode,String attrValue,String thisDate) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_TYPE", "D");
        param.put("DISCNT_CODE", discntCode);
        param.put("ATTR_VALUE", attrValue);
        param.put("THIS_DATE", thisDate);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_DISCNTCODE_ATTRVALUE_THISDATE", param, Route.getJourDb());
    }
    
    /**
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getDeskTopGrpDiscntAttrByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_TRADEID_FOR_DESKTOPGRP", param, Route.getJourDb());
    }
    public static IDataset getAttrByIdcTo7041(String tradeId) throws Exception
    {
    	 IData param = new DataMap();
         param.put("TRADE_ID", tradeId);
         return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_BY_TRADEID_FOR_IDC7041", param, Route.getJourDb());
    }
}
