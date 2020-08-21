package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
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
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class TradeOtherInfoQry extends CSBizBean {
    /**
     * chenyi 2014-3-4 删除合同附件
     * 
     * @param param
     * @throws Exception
     */
    public static void delRsrvValueCode(IData param) throws Exception {

        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "DEL_BY_TRADEID_RSRVVALUECODE", param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据tradeId查询所有的账户信息备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakOtherByTradeId(String tradeId) throws Exception {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_OTHER_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * 查询GRP_PLATSVC
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xiajj
     */
    public static IDataset getGrpOtherByTrade(String trade_id, String eparchyCode, Pagination pagination) throws Exception {

        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        IDataset idata = Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_BY_TRADE_ID", param, pagination, Route.getJourDb(eparchyCode));
        return idata;
    }

    /**
     * 获取其它子台帐
     * 
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IDataset getTradeOtherByTradeId(String trade_id) throws Exception {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);

        return Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_BY_TRADE_ID", inparams, Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据tradeId查询other表信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeOtherByTradeId(String tradeId, String rsrvValue) throws Exception {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("RSRV_VALUE", rsrvValue);
        return Dao.qryByCodeParser("TF_B_TRADE_OTHER", "SEL_BY_TRDRVALUE", params, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getTradeOtherByTradeId(String trade_id, String rsrv_value_code, String user_id) throws Exception {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("RSRV_VALUE_CODE", rsrv_value_code);
        inparams.put("USER_ID", user_id);

        return Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_BY_TRADEID_RSRVVLLUE_USERID", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据tradeId查询所有的账户信息备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeOtherByTradeIdAndRsrvCode(String tradeId, String rsrvValueCode) throws Exception {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("RSRV_VALUE_CODE", rsrvValueCode);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("Select to_char(TRADE_ID) TRADE_ID, ");
        sql.append("ACCEPT_MONTH, ");
        sql.append("to_char(USER_ID) USER_ID, ");
        sql.append("RSRV_VALUE_CODE, ");
        sql.append("RSRV_VALUE, ");
        sql.append("RSRV_NUM1, ");
        sql.append("RSRV_NUM2, ");
        sql.append("RSRV_NUM3, ");
        sql.append("to_char(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("to_char(RSRV_NUM6) RSRV_NUM6, ");
        sql.append("to_char(RSRV_NUM7) RSRV_NUM7, ");
        sql.append("to_char(RSRV_NUM8) RSRV_NUM8, ");
        sql.append("to_char(RSRV_NUM9) RSRV_NUM9, ");
        sql.append("to_char(RSRV_NUM10) RSRV_NUM10, ");
        sql.append("to_char(RSRV_NUM11) RSRV_NUM11, ");
        sql.append("to_char(RSRV_NUM12) RSRV_NUM12, ");
        sql.append("to_char(RSRV_NUM13) RSRV_NUM13, ");
        sql.append("to_char(RSRV_NUM14) RSRV_NUM14, ");
        sql.append("to_char(RSRV_NUM15) RSRV_NUM15, ");
        sql.append("to_char(RSRV_NUM16) RSRV_NUM16, ");
        sql.append("to_char(RSRV_NUM17) RSRV_NUM17, ");
        sql.append("to_char(RSRV_NUM18) RSRV_NUM18, ");
        sql.append("to_char(RSRV_NUM19) RSRV_NUM19, ");
        sql.append("to_char(RSRV_NUM20) RSRV_NUM20, ");
        sql.append("RSRV_STR1, ");
        sql.append("RSRV_STR2, ");
        sql.append("RSRV_STR3, ");
        sql.append("RSRV_STR4, ");
        sql.append("RSRV_STR5, ");
        sql.append("RSRV_STR6, ");
        sql.append("RSRV_STR7, ");
        sql.append("RSRV_STR8, ");
        sql.append("RSRV_STR9, ");
        sql.append("RSRV_STR10, ");
        sql.append("RSRV_STR11, ");
        sql.append("RSRV_STR12, ");
        sql.append("RSRV_STR13, ");
        sql.append("RSRV_STR14, ");
        sql.append("RSRV_STR15, ");
        sql.append("RSRV_STR16, ");
        sql.append("RSRV_STR17, ");
        sql.append("RSRV_STR18, ");
        sql.append("RSRV_STR19, ");
        sql.append("RSRV_STR20, ");
        sql.append("RSRV_STR21, ");
        sql.append("RSRV_STR22, ");
        sql.append("RSRV_STR23, ");
        sql.append("RSRV_STR24, ");
        sql.append("RSRV_STR25, ");
        sql.append("RSRV_STR26, ");
        sql.append("RSRV_STR27, ");
        sql.append("RSRV_STR28, ");
        sql.append("RSRV_STR29, ");
        sql.append("RSRV_STR30, ");
        sql.append("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("to_char(RSRV_DATE4, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE4, ");
        sql.append("to_char(RSRV_DATE5, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5, ");
        sql.append("to_char(RSRV_DATE6, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE6, ");
        sql.append("to_char(RSRV_DATE7, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE7, ");
        sql.append("to_char(RSRV_DATE8, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE8, ");
        sql.append("to_char(RSRV_DATE9, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE9, ");
        sql.append("to_char(RSRV_DATE10, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE10, ");
        sql.append("RSRV_TAG1, ");
        sql.append("RSRV_TAG2, ");
        sql.append("RSRV_TAG3, ");
        sql.append("RSRV_TAG4, ");
        sql.append("RSRV_TAG5, ");
        sql.append("RSRV_TAG6, ");
        sql.append("RSRV_TAG7, ");
        sql.append("RSRV_TAG8, ");
        sql.append("RSRV_TAG9, ");
        sql.append("RSRV_TAG10, ");
        sql.append("PROCESS_TAG, ");
        sql.append("STAFF_ID, ");
        sql.append("DEPART_ID, ");
        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("MODIFY_TAG, ");
        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK ");
        sql.append("FROM tf_b_trade_other ");
        sql.append("WHERE trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("AND rsrv_value_code = :RSRV_VALUE_CODE ");

        return Dao.qryBySql(sql, params, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据tradeId查询tradeOther表记录
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(String tradeId, String rsrvValueCode) throws Exception {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("RSRV_VALUE_CODE", rsrvValueCode);
        return Dao.qryByCodeParser("TF_B_TRADE_OTHER", "SEL_BY_TRADEID_RSRVVLLUE", params, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * 根据TRADE_ID、RSRV_VALUE_CODE查询所有OTHER台账数据
     * 
     * @param tradeId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset getTradeOtherByTradeIdRsrvCode(String tradeId, String rsrvValueCode) throws Exception {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("RSRV_VALUE_CODE", rsrvValueCode);
        return Dao.qryByCodeParser("TF_B_TRADE_OTHER", "SEL_BY_TRADEID_RSRVVLLUE", params, Route.getJourDb(getTradeEparchyCode()));
    }

    /**
     * 合同信息插Other表 chenyi 2013-3-4
     * 
     * @param inparam
     * @throws Exception
     */
    public static void inserOtherInfo(IData inparam) throws Exception {

        Dao.insert("TF_B_TRADE_OTHER", inparam, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 插入TF_B_TRADE_OTHER表 下发的业务管理流程信息
     * 
     * @author weixb3
     */
    public static void insertManagerTrade(String trade_id, String merchp_product_id, String merch_product_id, String pr_mn_operate_code, String product_offer_id, IDataset manage_id, IDataset manage_value, String ibsysid) throws Exception {

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("TRADE_ID", trade_id);
        data.put("MERCHP_PRODUCT_ID", merchp_product_id);
        data.put("MERCH_PRODUCT_ID", merch_product_id);
        data.put("PR_MN_OPERATE_CODE", "BBOSS_" + pr_mn_operate_code);
        data.put("PRODUCT_OFFER_ID", product_offer_id);
        data.put("END_DATE", SysDateMgr.END_DATE_FOREVER);

        if (!"".equals(trade_id) && manage_id.size() > 0) {
            // BbossTradeQueryDAO dao = new BbossTradeQueryDAO(pd,"cg");
            String inst_id = SeqMgr.getInstId();
            data.put("INST_ID", inst_id);

            SQLParser str = new SQLParser(data);
            for (int i = 0; i < manage_id.size(); i++) {
                if (i == 0) {
                    str.addSQL("  insert into TF_B_TRADE_OTHER(INST_ID,RSRV_TAG1,Trade_Id,ACCEPT_MONTH,USER_ID,RSRV_VALUE_CODE,RSRV_VALUE,RSRV_STR1,START_DATE,END_DATE,MODIFY_TAG,RSRV_STR21,RSRV_STR22,RSRV_DATE1");

                    if (StringUtils.isNotEmpty(ibsysid)) {
                        str.addSQL(" ,RSRV_STR20");
                    }
                }

                str.addSQL(" ,RSRV_NUM");
                str.addSQL(i + 1 + "");
                // modify by hudie@管理流程信息字段扩展
                str.addSQL(" ,RSRV_STR");
                str.addSQL(i + 1 + 22 + "");
                // modify by hudie@管理流程信息字段扩展
                if (i == manage_id.size() - 1) {
                    str.addSQL(" )");
                }
            }
            for (int i = 0; i < manage_id.size(); i++) {
                if (i == 0) {
                    str.addSQL("  select  :INST_ID,'0',A.TRADE_ID,A.ACCEPT_MONTH,A.USER_ID,");
                    str.addSQL("  :PR_MN_OPERATE_CODE");
                    str.addSQL(" , '管理流程信息'");// RSRV_VALUE必须有值，否则同步计费账务报错
                    str.addSQL(" , 'BBOSS_MANAGE'");// RSRV_VALUE必须有值，否则同步计费账务报错
                    str.addSQL("  ,sysdate,to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') ,'0'");
                    str.addSQL("  ,:MERCH_PRODUCT_ID, :MERCHP_PRODUCT_ID, sysdate");

                    if (StringUtils.isNotEmpty(ibsysid)) {
                        str.addSQL(" ,:IBSYSID");
                    }

                }

                str.addSQL(",'" + manage_id.get(i) + "'");
                str.addSQL(",'" + manage_value.get(i) + "'");

                if (i == manage_id.size() - 1) {
                    str.addSQL("  from tf_b_trade_grp_merchp a");
                    str.addSQL("  where a.TRADE_ID=TO_NUMBER('" + trade_id + "') ");
                    str.addSQL("  and a.accept_month = TO_NUMBER(SUBSTR('" + trade_id + "',5,2)) ");
                    str.addSQL("  and a.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID");
                }
            }
            Dao.executeUpdate(str, Route.getJourDb(Route.CONN_CRM_CG));
        }
    }

    /**
     * @description 合同信息、联系人信息等入表
     * @author xunyl
     * @date 2013-10-25
     */
    public static int insTradeOther(IData inparams) throws Exception {
        inparams.put("INST_ID", SeqMgr.getInstId());
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "INS_TRADE_OTHER", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description:BBOSS工单开通
     * @author jch
     * @date 2009-8-10
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossktWork(String START_DATE, String END_DATE, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("START_DATE", START_DATE);
        param.put("END_DATE", END_DATE);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  t.trade_id, t.rsrv_str1,t.rsrv_str2,t.rsrv_value,case when t.rsrv_str3='1' then '正常下达' when t.rsrv_str3='2' then '撤销工单' end rsrv_str3 ,t.rsrv_str4,t.rsrv_str5,t.rsrv_str6,t.rsrv_str7,t.rsrv_str8,t.rsrv_str9");
        parser.addSQL(" ,case when t.rsrv_str10='0' then '不需要' when t.rsrv_str10='1' then '需要' end rsrv_str10 ");
        parser.addSQL(" ,case when t.rsrv_str11='1' then '业务开通' when t.rsrv_str11='2' then '业务取消' when t.rsrv_str11='6' then '变更成员' end rsrv_str11 ");
        parser.addSQL(" FROM  tf_b_trade_other  t");
        parser.addSQL(" WHERE t.rsrv_value_code='BOSG'");
        parser.addSQL(" AND t.start_date>to_date(:START_DATE,'yyyy-mm-dd')");
        parser.addSQL(" AND t.start_date<to_date(:END_DATE,'yyyy-mm-dd')");
        return Dao.qryByParse(parser, pagination, Route.getJourDb());
    }

    /**
     * @Description:BBOSS工单开通查属性
     * @author jch
     * @date 2009-8-10
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossktWorkSx(String TRADE_ID, String START_DATE, String END_DATE, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("TRADE_ID", TRADE_ID);
        param.put("START_DATE", START_DATE);
        param.put("END_DATE", END_DATE);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT t.rsrv_str12,t.rsrv_str13,t.rsrv_str14");
        parser.addSQL(" FROM tf_b_trade_other t ");
        parser.addSQL(" WHERE t.rsrv_value_code='BOSG'");
        parser.addSQL(" AND t.trade_id=:TRADE_ID");
        parser.addSQL(" AND t.start_date>to_date(:START_DATE,'yyyy-mm-dd')");
        parser.addSQL(" AND t.start_date<to_date(:END_DATE,'yyyy-mm-dd')");
        return Dao.qryByParse(parser, pagination, Route.getJourDb());
    }

    public static IDataset qryTradeOtherByRsrvVlaue(IData inparam) throws Exception {
        return Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_TRD_OTR_BY_RSRVVLAUE", inparam, Route.getJourDb());
    }

    /**
     * 根据trade_id找礼品赠送信息
     * 
     * @param pd
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeOtherInfo(IData inparam) throws Exception {
        SQLParser parser = new SQLParser(inparam);
        parser.addSQL(" select rsrv_value_code,rsrv_value,rsrv_str1" + ",rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6," + "rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,staff_id," + "depart_id,start_date,end_date,modify_tag,remark "
                + "from tf_b_trade_other where 1=1 ");
        parser.addSQL(" and TRADE_ID = :TRADE_ID  ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    // todo
    /**
     * @Description 查询预受理登记在TF_B_TRADE_OTHER中未操作的状态
     * @author jch
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public static IDataset queryBbossManageDetailInfo(String cond_GROUP_ID, String cond_OPERATE_FLAG, String cond_POSPECNUMBER, String cond_START_DATE, String cond_END_DATE, String TRADE_ID, String cond_PRODUCTSPECNUMBER, Pagination page)
            throws Exception {
        IData param = new DataMap();
        param.put("GROUP_ID", cond_GROUP_ID);
        param.put("OPERATE_FLAG", cond_OPERATE_FLAG);
        param.put("POSPECNUMBER", cond_POSPECNUMBER);
        param.put("START_DATE", cond_START_DATE);
        param.put("END_DATE", cond_END_DATE);
        param.put("TRADE_ID", TRADE_ID);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select distinct t.trade_id,t.user_id,trade.cust_id,t.RSRV_VALUE_CODE,t.RSRV_NUM1,t.RSRV_STR11,t.RSRV_STR21,t.RSRV_STR22,t.RSRV_STR23,t.RSRV_TAG1,t.RSRV_DATE1,t.RSRV_DATE2,t.START_DATE,t.END_DATE,t.MODIFY_TAG,t.UPDATE_TIME,t.UPDATE_STAFF_ID,t.UPDATE_DEPART_ID,t.remark, ");
        parser.addSQL("  trade.order_id ,merch.MERCH_ORDER_ID,merchp.PRODUCT_ORDER_ID ");
        parser.addSQL(" from tf_b_trade_other t,tf_b_trade trade,tf_b_trade_grp_merchp merchp,tf_b_trade_grp_merch merch");
        parser.addSQL(" where T.RSRV_STR1='BBOSS_MANAGE'");
        parser.addSQL(" and t.rsrv_num10 IS NULL");
        parser.addSQL(" and t.trade_id=:TRADE_ID");
        parser.addSQL(" and t.accept_month=TO_NUMBER(SUBSTR(t.trade_id,5,2))");
        parser.addSQL(" and t.modify_tag='0'");
        parser.addSQL(" and t.trade_id=merchp.trade_id and t.accept_month=trade.accept_month ");
        parser.addSQL(" and merchp.trade_id=trade.trade_id");
        parser.addSQL(" and MERCH.TRADE_ID  IN( SELECT TRADE_ID  FROM TF_B_TRADE  WHERE  ORDER_ID=TRADE.ORDER_ID) ");

        if ("0".equals(param.getString("OPERATE_FLAG")))// 0未操作
        {
            parser.addSQL(" and t.RSRV_TAG1 in('0','2')");
        } else {
            parser.addSQL(" and t.RSRV_TAG1=:OPERATE_FLAG ");
        }

        if (!"".equals(param.getString("GROUP_ID"))) {
            parser.addSQL("  and merchp.GROUP_ID = :GROUP_ID");
        }

        if (!"".equals(param.getString("POSPECNUMBER", ""))) {
            param.put("RSRV_STR21", GrpCommonBean.merchToProduct(cond_POSPECNUMBER, 0, cond_PRODUCTSPECNUMBER));// 商品订单编码转换
            param.put("RSRV_STR22", GrpCommonBean.merchToProduct(cond_PRODUCTSPECNUMBER, 2, null));// 产品订单编码转换
            parser.addSQL("  and RSRV_STR21=:RSRV_STR21 ");
            parser.addSQL("  and RSRV_STR22=:RSRV_STR22 ");
        }
        if (!"".equals(param.getString("START_DATE"))) // BBOSS下发时间
        {

            parser.addSQL(" and to_date(to_char(T.RSRV_DATE1,'yyyy-MM-dd'),'yyyy-MM-dd')>=to_date(:START_DATE,'yyyy-MM-dd')");

        }
        if (!"".equals(param.getString("END_DATE"))) {
            parser.addSQL(" and to_date(to_char(T.RSRV_DATE1,'yyyy-MM-dd'),'yyyy-MM-dd')<=to_date(:END_DATE,'yyyy-MM-dd')");
        }

        return Dao.qryByParse(parser, page, Route.getJourDb());

    }

    // todo
    /**
     * @Description 查询预受理登记在TF_B_TRADE_OTHER中未操作的状态
     * @author jch
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public static IDataset queryBbossManageInfo(IData param) throws Exception {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select t.trade_id,t.user_id,t.RSRV_VALUE_CODE,t.RSRV_NUM1,t.RSRV_STR11,t.RSRV_STR21,t.RSRV_STR22,t.RSRV_TAG1,t.RSRV_DATE1,t.RSRV_DATE2,t.START_DATE,t.END_DATE,t.MODIFY_TAG,t.UPDATE_TIME,t.UPDATE_STAFF_ID,t.UPDATE_DEPART_ID,t.remark ");
        parser.addSQL(" ,compar.param_name,compar.para_code1 ");
        parser.addSQL(" from tf_b_trade_other t,td_s_commpara compar ");

        if (!"".equals(param.getString("cond_GROUP_ID"))) {
            parser.addSQL(" ,tf_b_trade_grp_merch merch");
        }

        parser.addSQL(" where RSRV_VALUE_CODE='BBOSS_'||compar.param_code ");
        parser.addSQL(" and compar.param_attr='3523' ");
        parser.addSQL(" and t.trade_id=:TRADE_ID");
        parser.addSQL(" and t.accept_month=TO_NUMBER(SUBSTR(t.trade_id,5,2))");

        if ("0".equals(param.getString("cond_OPERATE_FLAG")))// 0未操作
        {

            parser.addSQL(" and t.RSRV_TAG1 in('0','2')");
        } else
            parser.addSQL(" and t.RSRV_TAG1=:cond_OPERATE_FLAG ");

        if (!"".equals(param.getString("cond_GROUP_ID"))) {
            parser.addSQL("  and merch.trade_id=t.trade_id ");
            parser.addSQL(" and merch.group_id=:cond_GROUP_ID");
            parser.addSQL(" and merch.accept_month=TO_NUMBER(SUBSTR(merch.trade_id,5,2))");
        }

        if (!"".equals(param.getString("cond_POSPECNUMBER", ""))) {

            param.put("RSRV_STR21", AttrBizInfoQry.getAttrValueBy1BAttrCodeObj(param.getString("cond_POSPECNUMBER"), "PRO"));
            param.put("RSRV_STR22", AttrBizInfoQry.getAttrValueBy1BAttrCodeObj(param.getString("cond_PRODUCTSPECNUMBER"), "PRO"));
            parser.addSQL("  and RSRV_STR21=:RSRV_STR21 ");
            parser.addSQL("  and RSRV_STR22=:RSRV_STR22 ");

        }
        if (!"".equals(param.getString("cond_START_DATE"))) // BBOSS下发时间
        {

            parser.addSQL(" and t.RSRV_DATE1>=to_date(:cond_START_DATE,'yyyy-MM-dd')");

        }
        if (!"".equals(param.getString("cond_END_DATE"))) {
            parser.addSQL(" and t.RSRV_DATE1<=to_date(:cond_END_DATE,'yyyy-MM-dd')");
        }

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);

    }

    // todo
    public static IDataset queryBbossManageInfo4M2M(String GROUP_ID, String PRODUCT_ID) throws Exception {
        IData param = new DataMap();
        param.put("GROUP_ID", GROUP_ID);
        param.put("PRODUCT_ID", PRODUCT_ID);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select merch.inst_id,merch.group_id,t.trade_id,t.user_id,t.RSRV_VALUE_CODE, ");
        parser.addSQL("         t.RSRV_NUM1,t.RSRV_STR1,t.RSRV_STR21,t.RSRV_STR22,t.RSRV_TAG1, ");
        parser.addSQL("         TO_CHAR(t.RSRV_DATE1, 'yyyy-MM-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("         TO_CHAR(t.RSRV_DATE2, 'yyyy-MM-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("         TO_CHAR(t.START_DATE, 'yyyy-MM-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL("         TO_CHAR(t.END_DATE, 'yyyy-MM-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL("         TO_CHAR(t.UPDATE_TIME, 'yyyy-MM-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("         t.MODIFY_TAG,t.UPDATE_STAFF_ID,t.UPDATE_DEPART_ID,t.remark, ");
        parser.addSQL("         compar.param_name,compar.para_code1 ");
        parser.addSQL("    from tf_b_trade_other     t, ");
        parser.addSQL("         td_s_commpara        compar, ");
        parser.addSQL("         tf_b_trade_grp_merch merch ");
        parser.addSQL("   where RSRV_VALUE_CODE = 'BBOSS_' || compar.param_code ");
        parser.addSQL("     and compar.param_attr = '3523' ");
        parser.addSQL("      and t.accept_month=TO_NUMBER(SUBSTR(t.trade_id,5,2)) ");
        parser.addSQL("      and t.RSRV_TAG1 ='0' ");
        parser.addSQL("      and merch.trade_id=t.trade_id  ");
        parser.addSQL("      and merch.group_id= :GROUP_ID ");
        parser.addSQL("      and merch.accept_month=t.accept_month ");
        parser.addSQL("      and RSRV_STR21= :PRODUCT_ID ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 查询esop bboss管理节点信息
     * 
     * @param ibsysid
     * @return
     * @throws Exception
     */
    public static IDataset queryBbossManageInfoByEsop(String ibsysid) throws Exception {
        IDataset retIds = new DatasetList();
        /* IData params = new DataMap(); params.put("IBSYSID", ibsysid); return Dao.qryByCodeParser("TF_B_TRADE_OTHER", "SEL_OTHERINFO_BYESOP", params, Route.getJourDb(Route.CONN_CRM_CG)); */
        // ------------先查询订单库-----------
        IData params = new DataMap();
        params.put("IBSYSID", ibsysid);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT DISTINCT T.TRADE_ID,");
        parser.addSQL("                T.USER_ID,");
        parser.addSQL("                TRADE.CUST_ID,");
        parser.addSQL("                T.RSRV_VALUE_CODE,");
        parser.addSQL("                T.RSRV_NUM1,");
        parser.addSQL("                T.RSRV_STR11,");
        parser.addSQL("                T.RSRV_STR21,");
        parser.addSQL("                T.RSRV_STR22,");
        parser.addSQL("                T.RSRV_STR23,");
        parser.addSQL("                T.RSRV_TAG1,");
        parser.addSQL("                T.RSRV_DATE1,");
        parser.addSQL("                T.RSRV_DATE2,");
        parser.addSQL("                T.START_DATE,");
        parser.addSQL("                T.END_DATE,");
        parser.addSQL("                T.MODIFY_TAG,");
        parser.addSQL("                T.UPDATE_TIME,");
        parser.addSQL("                T.UPDATE_STAFF_ID,");
        parser.addSQL("                T.UPDATE_DEPART_ID,");
        parser.addSQL("                T.REMARK,");
        parser.addSQL("                TRADE.ORDER_ID,");
        parser.addSQL("                MERCH.MERCH_ORDER_ID,");
        parser.addSQL("                MERCHP.PRODUCT_ORDER_ID");
        parser.addSQL("  FROM TF_B_TRADE_OTHER      T,");
        parser.addSQL("       TF_B_TRADE            TRADE,");
        parser.addSQL("       TF_B_TRADE_GRP_MERCHP MERCHP,");
        parser.addSQL("       TF_B_TRADE_GRP_MERCH  MERCH");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL("   AND T.RSRV_STR1 = 'BBOSS_MANAGE'");
        parser.addSQL("   AND T.RSRV_NUM10 IS NULL");
        parser.addSQL("   AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(T.TRADE_ID, 5, 2))");
        parser.addSQL("   AND T.MODIFY_TAG = '0'");
        parser.addSQL("   AND T.TRADE_ID = MERCHP.TRADE_ID");
        parser.addSQL("   AND T.ACCEPT_MONTH = TRADE.ACCEPT_MONTH");
        parser.addSQL("   AND MERCHP.TRADE_ID = TRADE.TRADE_ID");
        parser.addSQL("   AND MERCH.TRADE_ID IN");
        parser.addSQL("       (SELECT TRADE_ID FROM TF_B_TRADE WHERE ORDER_ID = TRADE.ORDER_ID)");
        parser.addSQL("   AND T.RSRV_STR20 = :IBSYSID");
        IDataset ids = Dao.qryByParse(parser, Route.getJourDb());
        // ---------再查询资料库--------------------
        if (IDataUtil.isNotEmpty(ids)) {
            // -------台账与资料表做关联过滤数据--------------
            for (int i = 0; i < ids.size(); i++) {
                IData eachTrade = ids.getData(i);
                String custId = eachTrade.getString("CUST_ID", "");
                IData ida = UcaInfoQry.qryGrpInfoByCustId(custId);
                if (IDataUtil.isNotEmpty(ida)) {
                    eachTrade.put("CUST_NAME", ida.getString("CUST_NAME", ""));
                    retIds.add(eachTrade);
                }
            }

        }

        return retIds;
    }

    // todo
    /**
     * @Description 根据TRADEID查询预受理登记在TF_B_TRADE_OTHER中的数据
     * @author jch
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */

    public static IDataset queryBbossManageInfobyTradeIdUserId(String TRADE_ID, String USER_ID, String RSRV_VALUE_CODE, String MODIFY_TAG) throws Exception {
        IData param = new DataMap();
        param.put("TRADE_ID", TRADE_ID);
        param.put("USER_ID", USER_ID);
        param.put("RSRV_VALUE_CODE", RSRV_VALUE_CODE);
        param.put("MODIFY_TAG", MODIFY_TAG);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select TRADE_ID,USER_ID,RSRV_VALUE_CODE,RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_NUM6 ");
        parser.addSQL(" ,RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11,RSRV_NUM12,RSRV_NUM13,RSRV_NUM14,RSRV_NUM15,RSRV_NUM16");
        parser.addSQL(" ,RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6");
        parser.addSQL(" ,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16");
        parser.addSQL(" ,RSRV_STR17,RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24,RSRV_STR25,RSRV_STR26");
        parser.addSQL(" ,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30,RSRV_TAG1");
        parser.addSQL("  from tf_b_trade_other ");
        parser.addSQL("  where TRADE_ID= :TRADE_ID");
        parser.addSQL(" and USER_ID=:USER_ID");
        parser.addSQL(" and MODIFY_TAG=:MODIFY_TAG");

        if (!"".equals(param.getString("RSRV_VALUE_CODE", "")))
            parser.addSQL(" AND RSRV_VALUE_CODE=:RSRV_VALUE_CODE ");

        parser.addSQL(" order by rsrv_date1  desc "); // 按时间排序，取最新的一条

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    // todo
    /**
     * 查询IDC预受理资料子表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryNeedStartPreidcTradeC(String SERIAL_NUMBER, String CUST_NAME, String START_DATE, String END_DATE, String BUIS_NAME, Pagination pagination) throws Exception {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", SERIAL_NUMBER);
        data.put("CUST_NAME", CUST_NAME);
        data.put("START_DATE", START_DATE);
        data.put("END_DATE", END_DATE);
        data.put("BUIS_NAME", BUIS_NAME);
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT trade_id, accept_month, subscribe_type, subscribe_state,  cust_name, cust_type, " + "cust_addr, group_name, post_address, eparchy_code, city_code, prin_name, prin_phone, cust_id, post_code_post,post_code_grp, "
                + "prin_email, buis_name, buis_phone, buis_email, tech_name, tech_phone, tech_email, serial_number, accept_date, " + "finish_date, exp_date, remark ");
        parser.addSQL(" FROM tf_b_pretrade_idc a ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND a.SERIAL_NUMBER=:SERIAL_NUMBER");
        parser.addSQL(" AND a.CUST_NAME like '%' || :CUST_NAME || '%'");
        parser.addSQL(" AND a.ACCEPT_DATE >= to_date(:START_DATE, 'YYYY-MM-DD ') ");
        parser.addSQL(" AND a.ACCEPT_DATE <= to_date(:END_DATE, 'YYYY-MM-DD ') + 1 ");
        parser.addSQL(" AND a.BUIS_NAME like '%' || :BUIS_NAME || '%'");
        IDataset resultset = Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0; i < resultset.size(); i++) {
            IData result = resultset.getData(i);
            result.put("SUBSCRIBE_NAME", StaticUtil.getStaticValue("IDC_APPLY_ORDER_STATE", result.getString("SUBSCRIBE_STATE")));
        }
        return resultset;
    }

    // todo
    /**
     * @Description: 查询出TF_B_POTRADE_STATE 表中的 信息
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryPoTradeState(String SYNC_SEQUENCE) throws Exception {
        IData param = new DataMap();
        param.put("SYNC_SEQUENCE", SYNC_SEQUENCE);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TF_B_POTRADE_STATE ");
        parser.addSQL("  WHERE SYNC_SEQUENCE=:SYNC_SEQUENCE  ");

        /* parser.addSQL(" SELECT * "); parser.addSQL(" FROM TF_B_PRODUCTTRADE "); parser.addSQL(" WHERE SYNC_SEQUENCE=:SYNC_SEQUENCE "); if(StringUtils.isNotEmpty(param.getString("ORDER_NUMBER"
         * ))||StringUtils.isNotBlank(param.getString("ORDER_NUMBER"))) { parser.addSQL(" AND ORDER_NUMBER=:ORDER_NUMBER "); } */

        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));

    }

    // todo
    /**
     * @Description: 根据 SYNC_SEQUENCE and INFO_TAG,查询出 TF_B_POTRADE_STATE_ATTR 表的信息INFO_TAG P 产品 O商品 INFO_TYPE CUST_MANAGER客户经理 RespPerson 当前状态负责人信息
     * @author jch
     * @date
     * @param INTFO_TYPE
     *            ==null||"" 不带INFO_TYPE条件 ORDERING_ID BBOSS产品订单号 为null或者 "",不带条件
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryPoTradeStateAttr(String SYNC_SEQUENCE, String INFO_TYPE, String INFO_TAG, String ORDERING_ID) throws Exception {
        IData param = new DataMap();
        param.put("SYNC_SEQUENCE", SYNC_SEQUENCE);
        param.put("INFO_TYPE", INFO_TYPE);
        param.put("INFO_TAG", INFO_TAG);
        param.put("ORDERING_ID", ORDERING_ID);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  ");
        parser.addSQL(" * ");
        parser.addSQL(" FROM TF_B_POTRADE_STATE_ATTR ");
        parser.addSQL("  WHERE SYNC_SEQUENCE=:SYNC_SEQUENCE ");
        parser.addSQL(" AND INFO_TAG=:INFO_TAG ");

        if (StringUtils.isNotEmpty(param.getString("INFO_TYPE")) || StringUtils.isNotBlank(param.getString("INFO_TYPE"))) {
            parser.addSQL(" AND INFO_TYPE=:INFO_TYPE ");
        }
        if (StringUtils.isNotEmpty(param.getString("ORDERING_ID")) || StringUtils.isNotBlank(param.getString("ORDERING_ID"))) {
            parser.addSQL(" AND ORDERING_ID=:ORDERING_ID ");
        }
        parser.addSQL(" order by ordering_id,sub_info_type ");

        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * 查询IDC参数记录
     * 
     * @author Chengwei
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryPreidcIdcParams(String trade_id, String subscribe_type) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", trade_id);
        data.put("SUBSCRIBE_TYPE", subscribe_type);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT trade_id, accept_month, subscribe_type, param_type, param_name, param_value, modify_tag, " + "rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5");
        parser.addSQL(" FROM tf_b_pretrade_param_idc a ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND a.TRADE_ID = :TRADE_ID");
        parser.addSQL(" AND a.SUBSCRIBE_TYPE = :SUBSCRIBE_TYPE");
        IDataset dataset = Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
        return dataset;
    }

    /**
     * @Description: 根据条件参数查询出工单状态信息
     * @author jch
     * @date
     * @param param
     *            条件参数的map
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryProvCprt(String EC_CODE, String MERCH_ORDER_ID, String MERCH_SPEC_CODE, String IF_PROVCPRT, String IF_ANS, String SYNC_STATE, String START_DATE, String END_DATE, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("EC_CODE", EC_CODE);
        param.put("MERCH_ORDER_ID", MERCH_ORDER_ID);
        param.put("MERCH_SPEC_CODE", MERCH_SPEC_CODE);
        param.put("IF_PROVCPRT", IF_PROVCPRT);
        param.put("IF_ANS", IF_ANS);
        param.put("SYNC_STATE", SYNC_STATE);
        param.put("START_DATE", START_DATE);
        param.put("END_DATE", END_DATE);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select ");
        parser.addSQL(" t.sync_sequence,t.po_order_id,t.ec_serial_number,t.merch_spec_code,t.sync_state,t.syn_time,t.send,t.receive,t.if_provcprt,t.provcprt_type");
        parser.addSQL(" ,t.if_ans,t.trade_id,t.remark,t.EC_NAME ");
        parser.addSQL(" from TF_B_POTRADE_STATE t ");
        parser.addSQL(" where 1=1 ");

        // 判断全网客户编码
        if (StringUtils.isNotEmpty(param.getString("EC_CODE")) && StringUtils.isNotBlank(param.getString("EC_CODE"))) {
            parser.addSQL(" and t.ec_serial_number =:EC_CODE ");
        }

        // 判断全网商品订单号
        if (StringUtils.isNotEmpty(param.getString("MERCH_ORDER_ID")) && StringUtils.isNotBlank(param.getString("MERCH_ORDER_ID"))) {
            parser.addSQL(" and t.po_order_id =:MERCH_ORDER_ID ");
        }
        // 判断全网商品ID
        if (StringUtils.isNotEmpty(param.getString("MERCH_SPEC_CODE")) && StringUtils.isNotBlank(param.getString("MERCH_SPEC_CODE"))) {
            parser.addSQL(" and t.merch_spec_code =:MERCH_SPEC_CODE ");
        }

        // 判断是否需要反馈
        if (StringUtils.isNotEmpty(param.getString("IF_PROVCPRT")) && StringUtils.isNotBlank(param.getString("IF_PROVCPRT"))) {
            parser.addSQL(" and t.IF_PROVCPRT=:IF_PROVCPRT");
        }

        // 判断是否已反馈
        if (StringUtils.isNotEmpty(param.getString("IF_ANS")) && StringUtils.isNotBlank(param.getString("IF_ANS"))) {
            parser.addSQL(" and t.IF_ANS=:IF_ANS");
        }

        // 判断同步状态
        if (StringUtils.isNotEmpty(param.getString("SYNC_STATE")) && StringUtils.isNotBlank(param.getString("SYNC_STATE"))) {
            parser.addSQL(" and t.SYNC_STATE=:SYNC_STATE");
        }

        if (StringUtils.isNotEmpty(param.getString("START_DATE")) && StringUtils.isNotBlank(param.getString("START_DATE"))) {
            param.put("START_DATE", SysDateMgr.getDateForYYYYMMDD(param.getString("START_DATE")) + "000000");
            param.put("END_DATE", SysDateMgr.getDateForYYYYMMDD(param.getString("END_DATE")) + "235959");

            parser.addSQL(" and t.syn_time>=:START_DATE ");
            parser.addSQL(" and t.syn_time<=:END_DATE");
        }

        IDataset ProvCprts = Dao.qryByParse(parser, pagination, Route.getJourDb(BizRoute.getRouteId()));
        for (int i = 0; i < ProvCprts.size(); i++) {
            IDataset poinfos = UpcCall.queryPoByPospecNumber(ProvCprts.getData(i).getString("MERCH_SPEC_CODE", ""));
            if (!poinfos.isEmpty()) {
                ProvCprts.getData(i).put("POSPECNAME", poinfos.getData(0).getString("POSPECNAME", ""));
            }
        }
        return ProvCprts;
    }

    /**
     * 查询IDC预受理资料单条详细情况
     * 
     * @author Chengwei
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryStartPreidcByTrade(String TRADE_ID) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", TRADE_ID);
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT trade_id, accept_month, subscribe_type, subscribe_state, cust_name, cust_type, " + "cust_addr, group_name, post_address, eparchy_code, city_code, prin_name, prin_phone,cust_id, post_code_post,post_code_grp, "
                + "prin_email, buis_name, buis_phone, buis_email, tech_name, tech_phone, tech_email, serial_number, accept_date, " + "finish_date, exp_date, remark ");
        parser.addSQL(" FROM tf_b_pretrade_idc a ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND a.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND ROWNUM < 2 ");
        IDataset dataset = Dao.qryByParse(parser, null, Route.CONN_CRM_CG);

        return dataset;
    }

    // todo
    /**
     * 作用：查询工单订购的子业务数
     * 
     * @author
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset querySubscribeTypeByTradeId(String TRADE_ID) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", TRADE_ID);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT DISTINCT(SUBSCRIBE_TYPE) SUBSCRIBE_TYPE ");
        parser.addSQL(" FROM  TF_B_PRETRADE_PARAM_IDC  T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.TRADE_ID=:TRADE_ID");
        IDataset dataset = Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
        return dataset;
    }

    // todo
    /**
     * @Description: 根据TradeId 和 Rsrv_Value_Code 查询出TF_B_TRADE_OTHRE数据
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeOtherByTradeIdAndRsrvValueCode(String TRADE_ID, String RSRV_VALUE_CODE) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", TRADE_ID);
        data.put("RSRV_VALUE_CODE", RSRV_VALUE_CODE);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT *  ");
        parser.addSQL(" FROM TF_B_TRADE_OTHER ");
        parser.addSQL("  WHERE TRADE_ID=:TRADE_ID  ");
        parser.addSQL(" and ACCEPT_MONTH=TO_NUMBER(SUBSTR(:VTRADE_ID,5,2))");
        parser.addSQL(" and RSRV_VALUE_CODE=:RSRV_VALUE_CODE");
        parser.addSQL(" and START_DATE < sysdate ");
        parser.addSQL(" and END_DATE> sysdate ");

        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryUserDataLineByUserIdAndProductNo(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));
        data.put("SHEET_TYPE", inparam.getString("SHEET_TYPE"));
        data.put("PRODUCT_NO", inparam.getString("PRODUCT_NO"));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT  *  ");
        parser.addSQL(" FROM TF_F_USER_DATALINE  T ");
        parser.addSQL(" WHERE 1 = 1 AND T.USER_ID = :USER_ID  ");
        parser.addSQL(" AND T.SHEET_TYPE = :SHEET_TYPE  ");
        parser.addSQL(" AND T.PRODUCT_NO = :PRODUCT_NO  ");
        parser.addSQL(" AND T.END_DATE > sysdate ");
        parser.addSQL(" ORDER BY INST_ID ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    public static IDataset queryUserDataLineByUserIdAndProductNoForDatalineKJ(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));
        data.put("SHEET_TYPE", inparam.getString("SHEET_TYPE"));
        data.put("PRODUCT_NO", inparam.getString("PRODUCT_NO"));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT  *  ");
        parser.addSQL(" FROM TF_F_USER_DATALINE  T ");
        parser.addSQL(" WHERE 1 = 1 AND T.USER_ID = :USER_ID  ");
        parser.addSQL(" AND T.SHEET_TYPE = :SHEET_TYPE  ");
        parser.addSQL(" AND T.PRODUCT_NO = :PRODUCT_NO  ");
        // parser.addSQL(" AND T.END_DATE < sysdate ");
        parser.addSQL(" ORDER BY INST_ID ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    public static IDataset queryUserDataLineByUserId(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT  *  ");
        parser.addSQL(" FROM TF_F_USER_DATALINE  T ");
        parser.addSQL(" WHERE 1 = 1 AND T.USER_ID = :USER_ID  ");
        parser.addSQL(" AND (T.RSRV_STR3 = '0' OR T.RSRV_STR3 IS NULL)"); // RSRV_STR3 专线停开机标识 0开机 1停机
        parser.addSQL(" AND T.END_DATE > sysdate ");
        parser.addSQL(" ORDER BY INST_ID ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }
    
    public static IDataset queryAllUserDataLineByProductNo(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));
        data.put("SHEET_TYPE", inparam.getString("SHEET_TYPE"));
        data.put("PRODUCT_NO", inparam.getString("PRODUCT_NO"));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT  *  ");
        parser.addSQL(" FROM TF_F_USER_DATALINE  T ");
        parser.addSQL(" WHERE 1 = 1  ");
        parser.addSQL(" AND T.USER_ID = :USER_ID  ");
        parser.addSQL(" AND T.SHEET_TYPE = :SHEET_TYPE  ");
        parser.addSQL(" AND T.PRODUCT_NO = :PRODUCT_NO  ");
        parser.addSQL(" ORDER BY T.END_DATE DESC ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    public static IDataset queryUserOtherInfoByUserId(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));
        data.put("RSRV_VALUE_CODE", inparam.getString("RSRV_VALUE_CODE"));
        data.put("RSRV_STR7", inparam.getString("USER_ID_A", ""));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT /*+ INDEX(T,IDX_TF_F_USER_OTHER_UID) */ PARTITION_ID,USER_ID," + "RSRV_VALUE_CODE,RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_NUM6,"
                + "RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11,RSRV_NUM12,RSRV_NUM13,RSRV_NUM14," + "RSRV_NUM15,RSRV_NUM16,RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1,RSRV_STR2,"
                + "RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10," + "RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,"
                + "RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24," + "RSRV_STR25,RSRV_STR26,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30,RSRV_DATE1,"
                + "RSRV_DATE2,RSRV_DATE3,RSRV_DATE4,RSRV_DATE5,RSRV_DATE6,RSRV_DATE7,RSRV_DATE8," + "RSRV_DATE9,RSRV_DATE10,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,RSRV_TAG5,"
                + "RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID," + "DEPART_ID,TRADE_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,INST_ID ");
        parser.addSQL(" FROM TF_F_USER_OTHER T");
        parser.addSQL(" WHERE 1 = 1  ");
        parser.addSQL(" AND T.USER_ID = TO_NUMBER(:USER_ID)  ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)  ");
        parser.addSQL(" AND T.RSRV_VALUE_CODE =:RSRV_VALUE_CODE  ");
        if (!"".equals(data.getString("RSRV_STR7"))) {
            parser.addSQL(" AND T.RSRV_STR7 =:RSRV_STR7  ");
        }
        parser.addSQL(" AND T.END_DATE > sysdate ");
        parser.addSQL(" ORDER BY RSRV_VALUE ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    public static IDataset queryUserOtherInfoByUserIdForDatalineKJ(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));
        data.put("RSRV_VALUE_CODE", inparam.getString("RSRV_VALUE_CODE"));
        data.put("RSRV_STR7", inparam.getString("USER_ID_A", ""));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT /*+ INDEX(T,IDX_TF_F_USER_OTHER_UID) */ PARTITION_ID,USER_ID," + "RSRV_VALUE_CODE,RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_NUM6,"
                + "RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11,RSRV_NUM12,RSRV_NUM13,RSRV_NUM14," + "RSRV_NUM15,RSRV_NUM16,RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1,RSRV_STR2,"
                + "RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10," + "RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,"
                + "RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24," + "RSRV_STR25,RSRV_STR26,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30,RSRV_DATE1,"
                + "RSRV_DATE2,RSRV_DATE3,RSRV_DATE4,RSRV_DATE5,RSRV_DATE6,RSRV_DATE7,RSRV_DATE8," + "RSRV_DATE9,RSRV_DATE10,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,RSRV_TAG5,"
                + "RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID," + "DEPART_ID,TRADE_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,INST_ID ");
        parser.addSQL(" FROM TF_F_USER_OTHER T");
        parser.addSQL(" WHERE 1 = 1  ");
        parser.addSQL(" AND T.USER_ID = TO_NUMBER(:USER_ID)  ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)  ");
        parser.addSQL(" AND T.RSRV_VALUE_CODE =:RSRV_VALUE_CODE  ");
        if (!"".equals(data.getString("RSRV_STR7"))) {
            parser.addSQL(" AND T.RSRV_STR7 =:USER_ID_A  ");
        }
        parser.addSQL(" AND T.END_DATE < sysdate ");
        parser.addSQL(" ORDER BY RSRV_VALUE ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    public static IDataset queryUserOtherInfoByUserIdForVOIPDatalineKJ(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));
        data.put("RSRV_VALUE_CODE", inparam.getString("RSRV_VALUE_CODE"));
        data.put("USER_ID_A", inparam.getString("PRODUCTNO", ""));
        data.put("RSRV_STR13", inparam.getString("RSRV_STR13", ""));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT /*+ INDEX(T,IDX_TF_F_USER_OTHER_UID) */ PARTITION_ID,USER_ID," + "RSRV_VALUE_CODE,RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_NUM6,"
                + "RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11,RSRV_NUM12,RSRV_NUM13,RSRV_NUM14," + "RSRV_NUM15,RSRV_NUM16,RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1,RSRV_STR2,"
                + "RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10," + "RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,"
                + "RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24," + "RSRV_STR25,RSRV_STR26,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30,RSRV_DATE1,"
                + "RSRV_DATE2,RSRV_DATE3,RSRV_DATE4,RSRV_DATE5,RSRV_DATE6,RSRV_DATE7,RSRV_DATE8," + "RSRV_DATE9,RSRV_DATE10,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,RSRV_TAG5,"
                + "RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID," + "DEPART_ID,TRADE_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,INST_ID ");
        parser.addSQL(" FROM TF_F_USER_OTHER T");
        parser.addSQL(" WHERE 1 = 1  ");
        parser.addSQL(" AND T.USER_ID = TO_NUMBER(:USER_ID)  ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)  ");
        parser.addSQL(" AND T.RSRV_VALUE_CODE =:RSRV_VALUE_CODE  ");
        parser.addSQL(" AND T.RSRV_STR9 =:USER_ID_A  ");
        parser.addSQL(" AND T.RSRV_STR13 =:RSRV_STR13  ");
        // parser.addSQL(" AND T.END_DATE < sysdate ");
        parser.addSQL(" ORDER BY RSRV_VALUE ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    public static IDataset queryUserVOIPOtherInfoByUserId(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));
        data.put("RSRV_VALUE_CODE", inparam.getString("RSRV_VALUE_CODE"));
        data.put("RSRV_STR7", inparam.getString("USER_ID_A", ""));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT /*+ INDEX(T,IDX_TF_F_USER_OTHER_UID) */ PARTITION_ID,USER_ID," + "RSRV_VALUE_CODE,RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_NUM6,"
                + "RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11,RSRV_NUM12,RSRV_NUM13,RSRV_NUM14," + "RSRV_NUM15,RSRV_NUM16,RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1,RSRV_STR2,"
                + "RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10," + "RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,"
                + "RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24," + "RSRV_STR25,RSRV_STR26,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30,RSRV_DATE1,"
                + "RSRV_DATE2,RSRV_DATE3,RSRV_DATE4,RSRV_DATE5,RSRV_DATE6,RSRV_DATE7,RSRV_DATE8," + "RSRV_DATE9,RSRV_DATE10,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,RSRV_TAG5,"
                + "RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID," + "DEPART_ID,TRADE_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,INST_ID ");
        parser.addSQL(" FROM TF_F_USER_OTHER T");
        parser.addSQL(" WHERE 1 = 1  ");
        parser.addSQL(" AND T.USER_ID = TO_NUMBER(:USER_ID)  ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)  ");
        parser.addSQL(" AND T.RSRV_VALUE_CODE =:RSRV_VALUE_CODE  ");
        parser.addSQL(" AND T.RSRV_STR7 =:RSRV_STR7  ");
        parser.addSQL(" AND T.END_DATE > sysdate ");
        parser.addSQL(" ORDER BY RSRV_VALUE ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    /**
     * @Description 更改TF_B_TRADE_OTHER的标志位
     * @author jch
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */

    public static void udpateBbossOtherFlag(IData param) throws Exception {

        param.put("RSRV_TAG1", "1");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPDATE_RSRV_TAG1_BY_PK", param, Route.getJourDb());

    }

    /**
     * 更新other表状态 防止以前管理节点信息发服开 2014-1-7
     * 
     * @param trade_id
     * @param rsrv_value_code
     * @param user_id
     * @return
     * @throws Exception
     */
    public static void updateManageInfo(String trade_id, String manageinfo, String isneedPf) throws Exception {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("RSRV_STR1", manageinfo);
        inparams.put("IS_NEED_PF", isneedPf);

        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPDATE_MANAGEINFO_BY_PK", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 更新other表状态 chenyi
     * 
     * @param trade_id
     * @param rsrv_value_code
     * @param user_id
     * @return
     * @throws Exception
     */
    public static void updateModifyDate(String trade_id, String manageNode, String isneedPf) throws Exception {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("MANAGENODE", manageNode);
        inparams.put("IS_NEED_PF", isneedPf);

        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UP_MDF_DATE_BYRADEID", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据TRADE_ID查询专线资料台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryDatalineByTradeId(String tradeId) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT *  ");
        parser.addSQL(" FROM TF_B_TRADE_USER_DATALINE ");
        parser.addSQL(" WHERE 1 = 1 AND TRADE_ID =:TRADE_ID  ");
        parser.addSQL(" AND START_DATE < sysdate ");
        parser.addSQL(" AND END_DATE> sysdate ");

        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询OTHER表信息
     * 
     * @param tradeId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset queryUserOtherByTradeId(String tradeId, String rsrvValueCode) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("RSRV_VALUE_CODE", rsrvValueCode);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT *  ");
        parser.addSQL(" FROM TF_B_TRADE_OTHER ");
        parser.addSQL(" WHERE 1 = 1 AND TRADE_ID =:TRADE_ID  ");
        parser.addSQL(" AND RSRV_VALUE_CODE = :RSRV_VALUE_CODE ");
        parser.addSQL(" AND START_DATE < sysdate ");
        parser.addSQL(" AND END_DATE> sysdate ");

        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询派发信息
     * 
     * @param tradeId
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset queryDatalineAttr(String tradeId, String attrCode) throws Exception {

        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("ATTR_CODE", attrCode);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT *  ");
        parser.addSQL(" FROM TF_B_TRADE_DATALINE_ATTR ");
        parser.addSQL(" WHERE 1 = 1 AND TRADE_ID =:TRADE_ID  ");
        parser.addSQL(" AND ATTR_CODE =:ATTR_CODE  ");
        parser.addSQL(" AND START_DATE < sysdate ");
        parser.addSQL(" AND END_DATE> sysdate ");

        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * 查询派发信息
     * 
     * @param tradeId
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset queryDatalineAttrTrade(String attrvalue, String attrCode) throws Exception {

        IData data = new DataMap();
        data.put("ATTR_VALUE", attrvalue);
        data.put("ATTR_CODE", attrCode);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT *  ");
        parser.addSQL(" FROM TF_B_TRADE_DATALINE_ATTR ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND ATTR_CODE =:ATTR_CODE  ");
        parser.addSQL(" AND ATTR_VALUE =:ATTR_VALUE  ");
        parser.addSQL(" AND START_DATE < sysdate ");
        parser.addSQL(" AND END_DATE > sysdate ");

        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static void updateDatalineTrade(String tradeId, IData userDataline) throws Exception {
        Dao.update("TF_B_TRADE_USER_DATALINE", userDataline, new String[] { "TRADE_ID" }, new String[] { tradeId }, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static void updateUserOther(String tradeId, IData userOther) throws Exception {
        Dao.update("TF_B_TRADE_OTHER", userOther, new String[] { "TRADE_ID" }, new String[] { tradeId }, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static void updateUserDatalineAttr(String tradeId, String attrCode, IData userOther) throws Exception {
        Dao.update("TF_B_TRADE_DATALINE_ATTR", userOther, new String[] { "TRADE_ID", "ATTR_CODE" }, new String[] { tradeId, attrCode }, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static void updateBBossUploadForInNeedPf(String tradeId, String rsrvValueCode, String isNeedPf) throws Exception {

        IData param = new DataMap();
        param.put("IS_NEED_PF", isNeedPf);
        param.put("TRADE_ID", tradeId);
        param.put("RSRV_VALUE_CODE", rsrvValueCode);

        StringBuilder sql = new StringBuilder(100);

        sql.append(" UPDATE TF_B_TRADE_OTHER T ");
        sql.append("   SET T.IS_NEED_PF = :IS_NEED_PF ");
        sql.append(" WHERE T.RSRV_VALUE_CODE = :RSRV_VALUE_CODE ");
        sql.append("   AND T.TRADE_ID = :TRADE_ID ");

        Dao.executeUpdate(sql, param, Route.getJourDb());
    }

    /**
     * 查询OTHER表信息
     * 
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeOtherBySIMM(String userId) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_BY_USR_SIMM", data, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 获取TF_F_USER_OTHER表数据
     * 
     * @param tradeId
     * @param userId
     * @param productNo
     * @return
     * @throws Exception
     */
    public static IDataset queryUserOtherInfosByTradeId(String tradeId, String userId, String productNo) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("USER_ID", userId);
        data.put("RSRV_STR7", productNo);
        IDataset dataset = new DatasetList();
        IDataset userOtherInfos = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_USERID_RSRV7", data);
        if (IDataUtil.isNotEmpty(userOtherInfos)) {
            for (int i = 0; i < userOtherInfos.size(); i++) {
                IData other = userOtherInfos.getData(i);
                String ins_id = other.getString("INST_ID");
                data.put("INST_ID", ins_id);
                IDataset tradeotherInfos = Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_OTHERINFO_BY_TRADEID_USERID", data, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(tradeotherInfos)) {
                    dataset.add(other);
                }

            }
        }
        return dataset;
    }

    /**
     * 获取TF_F_USER_OTHER表数据
     * 
     * @param tradeId
     * @param userId
     * @param rsrvCode
     * @return
     * @throws Exception
     */
    public static IDataset queryUserOtherInfosByIdRsrvCode(String tradeId, String userId, String rsrvCode) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("USER_ID", userId);
        data.put("RSRV_VALUE_CODE", rsrvCode);
        IDataset result = new DatasetList();
        IDataset userOtherInfos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHERINFO_BY_USERID_RSRVCODE", data);
        if (IDataUtil.isNotEmpty(userOtherInfos)) {
            for (int j = 0; j < userOtherInfos.size(); j++) {
                IData userOtherInfo = userOtherInfos.getData(j);
                if (IDataUtil.isNotEmpty(userOtherInfo)) {
                    String instId = userOtherInfo.getString("INST_ID", "");
                    IData param = new DataMap();
                    param.put("TRADE_ID", tradeId);
                    param.put("USER_ID", userId);
                    param.put("INST_ID", instId);
                    IDataset tradeOtherInfos = Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_OTHERINFO_BY_TRADEID_USERID", param);
                    if (IDataUtil.isNotEmpty(tradeOtherInfos)) {
                        result.add(userOtherInfo);
                    }
                }
            }
        }
        return result;
        // return Dao.qryByCode("TF_F_USER_OTHER",
        // "SEL_OTHERINFO_BY_TRADEID_RSRVCODE", data);
    }

    /**
     * 获取TF_F_USER_DATALINE数据
     * 
     * @param tradeId
     * @param userId
     * @param productNo
     * @return
     * @throws Exception
     */
    public static IDataset queryUserOtherDataLineByTradeId(String tradeId, String userId, String productNo) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("USER_ID", userId);
        data.put("PRODUCT_NO", productNo);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHERDATALINE_BY_TRADEID", data);
    }

    public static IDataset queryUserOtherInfoByUserIdProductNo(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.get("USER_ID"));
        data.put("RSRV_VALUE_CODE", inparam.get("RSRV_VALUE_CODE"));
        data.put("RSRV_STR7", inparam.get("PRODUCT_NO"));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT /*+ INDEX(T,IDX_TF_F_USER_OTHER_UID) */ PARTITION_ID,USER_ID," + "RSRV_VALUE_CODE,RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_NUM6,"
                + "RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11,RSRV_NUM12,RSRV_NUM13,RSRV_NUM14," + "RSRV_NUM15,RSRV_NUM16,RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1,RSRV_STR2,"
                + "RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10," + "RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,"
                + "RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24," + "RSRV_STR25,RSRV_STR26,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30,RSRV_DATE1,"
                + "RSRV_DATE2,RSRV_DATE3,RSRV_DATE4,RSRV_DATE5,RSRV_DATE6,RSRV_DATE7,RSRV_DATE8," + "RSRV_DATE9,RSRV_DATE10,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,RSRV_TAG5,"
                + "RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID," + "DEPART_ID,TRADE_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,INST_ID ");
        parser.addSQL(" FROM TF_F_USER_OTHER T");
        parser.addSQL(" WHERE 1 = 1  ");
        parser.addSQL(" AND T.USER_ID = TO_NUMBER(:USER_ID)  ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)  ");
        parser.addSQL(" AND T.RSRV_VALUE_CODE =:RSRV_VALUE_CODE  ");
        parser.addSQL(" AND T.RSRV_STR7 =:RSRV_STR7  ");
        parser.addSQL(" AND T.END_DATE > sysdate ");
        parser.addSQL(" ORDER BY RSRV_VALUE ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    /**
     * 判断是否含有魔百和开户在途，（即判断是否宽带融合开户含魔百和开户在途，未完工 或 魔百和预开户在途，未完工）
     */
    public static IDataset getTradeInfosByTradeTypeAndOther(String sn) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_F_USER_OTHER", "QRY_TOPSETBOX_TRADE", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset queryUserDataLineByProductNo(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("SHEET_TYPE", inparam.getString("SHEET_TYPE"));
        data.put("PRODUCT_NO", inparam.getString("PRODUCT_NO"));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT  *  ");
        parser.addSQL(" FROM TF_F_USER_DATALINE  T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.SHEET_TYPE = :SHEET_TYPE  ");
        parser.addSQL(" AND T.PRODUCT_NO = :PRODUCT_NO  ");
        parser.addSQL(" AND T.END_DATE > sysdate ");
        parser.addSQL(" ORDER BY INST_ID ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    public static IDataset queryUserDataLineByUserIdRsrv3(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT  *  ");
        parser.addSQL(" FROM TF_F_USER_DATALINE  T ");
        parser.addSQL(" WHERE 1 = 1 AND T.USER_ID = :USER_ID  ");
        parser.addSQL(" AND T.RSRV_STR3 = '1'"); // RSRV_STR3 专线停开机标识 0开机 1停机
        parser.addSQL(" AND T.END_DATE > sysdate ");
        parser.addSQL(" ORDER BY INST_ID ");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }
    
    public static IDataset queryUserOtherByUserIdStop(IData inparam) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", inparam.getString("USER_ID"));
        data.put("PRODUCT_NO", inparam.getString("PRODUCT_NO"));
        
        StringBuilder sql = new StringBuilder(200);
        
        sql.append(" SELECT  *  FROM TF_F_USER_DATALINE  T ");
        sql.append(" WHERE 1 = 1 AND T.USER_ID = :USER_ID ");
        sql.append(" AND T.PRODUCT_NO =:PRODUCT_NO ");
        sql.append(" ORDER BY T.END_DATE DESC ");
        
        return Dao.qryBySql(sql, data, Route.CONN_CRM_CG);
    }

    /**
     * 根据INST_ID、RSRV_VALUE_CODE查询所有OTHER台账数据
     *
     * @param instId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset getTradeOtherByInstIdRsrvCode(String instId, String rsrvValueCode) throws Exception {
        IData params = new DataMap();
        params.put("INST_ID", instId);
        params.put("RSRV_VALUE_CODE", rsrvValueCode);
        return Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_BY_INSTID_RSRVVLLUE", params, Route.getJourDb(getTradeEparchyCode()));
    }

    /**
     * BUG20200420112956 机场停车权益重复使用记录
     * @param userId
     * @param rsrvStr4
     * @return
     * @throws Exception
     */
    public static IDataset getTradeOtherByRsrvStr4(String userId, String rsrvStr4) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("RSRV_STR4", rsrvStr4);
        return Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_BY_USERID_RSRVSTR4", params, Route.getJourDb(getTradeEparchyCode()));
    }

	  
    /**
     * 企业专线满意度短信下发，获取目标号码
     * @param userId
     * @param rsrvValueCode
     * @return
     * @throws Exception
     */
    public static IDataset queryTargetMsisdn(String trade_id) throws Exception {
        IData data = new DataMap();
        data.put("TRADE_ID", trade_id);
        return Dao.qryByCode("TF_B_EOP_ATTR", "SEL_MSISDN_BY_TRADEID", data, Route.getJourDb(Route.CONN_CRM_CG));
    }
   
}
