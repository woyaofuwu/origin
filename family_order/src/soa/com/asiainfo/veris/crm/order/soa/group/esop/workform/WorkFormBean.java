package com.asiainfo.veris.crm.order.soa.group.esop.workform;

import java.util.Iterator;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class WorkFormBean {
    public static IDataset getExpWorkForms(IData map, Pagination pagination) throws Exception {

        IData params = new DataMap();
        params.put("BUSIFORM_ID", map.getString("BUSIFORM_ID"));
        params.put("BI_SN", map.getString("BI_SN"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.BUSIFORM_ID, ");
        parser.addSQL("       TO_CHAR(C.CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        parser.addSQL("       TO_CHAR(B.UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL("       T.LOG_INFO, ");
        parser.addSQL("       T.LOG_INFO1, ");
        parser.addSQL("       T.LOG_INFO2, ");
        parser.addSQL("       T.LOG_INFO3, ");
        parser.addSQL("       T.LOG_INFO4, ");
        parser.addSQL("       T.BUSIFORM_NODE_ID, ");
        parser.addSQL("       A.NODE_ID, ");
        parser.addSQL("       C.STATE, ");
        parser.addSQL("       C.BI_SN, ");
        parser.addSQL("       C.BPM_TEMPLET_ID, ");
        parser.addSQL("       A.EPARCHY_CODE AREA_ID ");
        parser.addSQL("  FROM TF_B_EWE_NODE A, ");
        parser.addSQL("       TF_B_EWE_STEP B, ");
        parser.addSQL("      (SELECT T.LOG_INFO,T.LOG_INFO1,T.LOG_INFO2,T.LOG_INFO3,T.LOG_INFO4,T.BUSIFORM_ID,T.BUSIFORM_NODE_ID,T.STEP_ID, ");
        parser.addSQL("      ROW_NUMBER() OVER(PARTITION BY T.BUSIFORM_ID ORDER BY T.UPDATE_DATE DESC) G ");
        parser.addSQL("      FROM TF_B_EWE_ERROR_LOG T ) T, ");
        parser.addSQL("       TF_B_EWE C ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL("   AND T.BUSIFORM_NODE_ID = B.BUSIFORM_NODE_ID ");
        parser.addSQL("   AND A.BUSIFORM_NODE_ID = T.BUSIFORM_NODE_ID ");
        parser.addSQL("   AND C.BUSIFORM_ID = T.BUSIFORM_ID ");
        parser.addSQL("   AND B.STATE=A.STATE ");
        parser.addSQL("   AND T.G<=1 ");
        parser.addSQL("   AND B.STATE='M' ");
        parser.addSQL("   AND (T.BUSIFORM_ID =:BUSIFORM_ID or :BUSIFORM_ID is null )  ");
        parser.addSQL("   AND (C.BI_SN =:BI_SN or :BI_SN is null )  ");
        parser.addSQL(" ORDER BY B.UPDATE_DATE DESC ");

        IDataset expInfos = new DatasetList();
        // 我的工单跳转则 不走分页
        if ("TRUE".equals(map.getString("FLAG"))) {
            expInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        } else {
            expInfos = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        }
        if (IDataUtil.isNotEmpty(expInfos)) {
            for (Object object : expInfos) {
                IData expInfo = (IData) object;
                IData params2 = new DataMap();
                params2.put("NODE_ID", expInfo.getString("NODE_ID"));
                params2.put("BPM_TEMPLET_ID", expInfo.getString("BPM_TEMPLET_ID"));
                SQLParser parser2 = new SQLParser(params2);
                parser2.addSQL(" SELECT  ");
                parser2.addSQL(" D.NODE_NAME ");
                parser2.addSQL("  FROM TD_B_EWE_NODE_TEMPLET D ");
                parser2.addSQL(" WHERE 1 = 1 ");
                parser2.addSQL(" AND D.NODE_ID = :NODE_ID ");
                parser2.addSQL(" AND D.BPM_TEMPLET_ID = :BPM_TEMPLET_ID ");
                IDataset nedeTempletInfo = Dao.qryByParse(parser2, Route.CONN_CRM_CEN);
                if (IDataUtil.isNotEmpty(nedeTempletInfo)) {
                    expInfo.put("NODE_NAME", nedeTempletInfo.getData(0).getString("NODE_NAME"));
                }

                IData params3 = new DataMap();
                params3.put("BI_SN", expInfo.getString("BI_SN"));
                params3.put("BUSIFORM_ID", expInfo.getString("BUSIFORM_ID"));
                SQLParser parser3 = new SQLParser(params3);
                parser3.addSQL(" SELECT  ");
                parser3.addSQL(" E.PRODUCT_NO ");
                parser3.addSQL("  FROM TF_B_EWE_RELE D, ");
                parser3.addSQL("  TF_B_EOP_EOMS_STATE E ");
                parser3.addSQL(" WHERE 1 = 1 ");
                parser3.addSQL(" AND E.IBSYSID = :BI_SN ");
                parser3.addSQL(" AND D.SUB_BUSIFORM_ID = :BUSIFORM_ID ");
                parser3.addSQL(" AND E.RECORD_NUM = D.RELE_VALUE ");
                IDataset productInfos = Dao.qryByParse(parser3, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(productInfos)) {
                    expInfo.put("PRODUCT_NO", productInfos.getData(0).getString("PRODUCT_NO"));

                }
                // 拼错误信息

                expInfo.put("LOG_INFO_A", expInfo.getString("LOG_INFO") + expInfo.getString("LOG_INFO1") + expInfo.getString("LOG_INFO2") + expInfo.getString("LOG_INFO3") + expInfo.getString("LOG_INFO4"));
            }
        }

        return expInfos;

    }

    public static IDataset getRenewWorkSheet(String ibsysId) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", ibsysId);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT A.NODE_ID, ");
        parser.addSQL(" A.PRODUCT_ID, ");
        parser.addSQL(" A.SHEETTYPE, ");
        parser.addSQL(" A.GROUP_SEQ, ");
        parser.addSQL(" B.IBSYSID, ");
        parser.addSQL(" B.BUSI_STATE, ");
        parser.addSQL(" B.PRODUCT_NO, ");
        parser.addSQL(" B.SERIALNO, ");
        parser.addSQL(" B.TRADE_ID, ");
        parser.addSQL(" B.DEAL_TYPE, ");
        parser.addSQL(" TO_CHAR(B.CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss') CREATE_DATE, ");
        parser.addSQL(" B.RECORD_NUM, ");
        parser.addSQL(" C.GROUP_ID, ");
        parser.addSQL(" C.RSRV_STR4 ");
        parser.addSQL(" FROM (SELECT T.NODE_ID,T.PRODUCT_ID, ");
        parser.addSQL("  T.SHEETTYPE,T.RECORD_NUM,T.IBSYSID,T.GROUP_SEQ, ROW_NUMBER() OVER(PARTITION BY T.RECORD_NUM ORDER BY T.INSERT_TIME DESC ) G ");
        parser.addSQL("  FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID ) A, ");
        parser.addSQL("      TF_B_EOP_EOMS_STATE B, ");
        parser.addSQL("      TF_B_EOP_SUBSCRIBE C ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND A.IBSYSID = B.IBSYSID ");
        parser.addSQL(" AND A.IBSYSID = C.IBSYSID ");
        parser.addSQL(" AND A.RECORD_NUM = B.RECORD_NUM ");
        parser.addSQL(" AND A.G <= 1 ");
        IDataset eomsInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(eomsInfos)) {
            for (Object object : eomsInfos) {
                IData eomsInfo = (IData) object;
                if (!"P".equals(eomsInfo.getString("BUSI_STATE"))) {
                    continue;
                }
                IData params1 = new DataMap();
                params1.put("IBSYSID", ibsysId);
                params1.put("ATTR_CODE", "opDesc");
                params1.put("RECORD_NUM", eomsInfo.getString("RECORD_NUM"));

                SQLParser parser1 = new SQLParser(params1);

                parser1.addSQL(" SELECT A.ATTR_VALUE,A.ATTR_CODE ");
                parser1.addSQL(" FROM TF_B_EOP_ATTR A, ");
                parser1.addSQL("  (SELECT T.NODE_ID,T.PRODUCT_ID, ");
                parser1.addSQL("  T.SHEETTYPE,T.RECORD_NUM,T.IBSYSID,T.GROUP_SEQ, ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC ) G ");
                parser1.addSQL("  FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM=:RECORD_NUM AND T.OPER_TYPE = 'withdrawWorkSheet' ) B ");
                parser1.addSQL(" WHERE 1 = 1 ");
                parser1.addSQL(" AND A.IBSYSID = B.IBSYSID ");
                parser1.addSQL(" AND A.GROUP_SEQ = B.GROUP_SEQ ");
                parser1.addSQL(" AND A.RECORD_NUM = B.RECORD_NUM ");
                parser1.addSQL(" AND A.ATTR_CODE = 'opDesc' ");
                parser1.addSQL(" AND B.G <= 1");

                IDataset attrInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(attrInfos)) {
                    eomsInfo.put("ATTR_VALUE", attrInfos.getData(0).getString("ATTR_VALUE"));
                }
            }

        }
        return eomsInfos;

    }

    public static IDataset queryEweConfigInfo(String busiState, String configName) throws Exception {

        IData params = new DataMap();
        if ("-1".equals(busiState)) {
            busiState = "A";
        }
        params.put("PARAMVALUE", busiState);
        params.put("CONFIGNAME", configName);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.CONFIGNAME, ");
        parser.addSQL(" C.CONFIGDESC, ");
        parser.addSQL(" C.PARAMNAME, ");
        parser.addSQL(" C.VALUESEQ, ");
        parser.addSQL(" C.PARAMVALUE, ");
        parser.addSQL(" C.VALUEDESC, ");
        parser.addSQL(" C.REMARK, ");
        parser.addSQL(" C.VALID_TAG, ");
        parser.addSQL(" C.UPDATE_DATE ");
        parser.addSQL(" FROM TD_B_EWE_CONFIG C ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.PARAMVALUE = :PARAMVALUE ");
        parser.addSQL(" AND C.CONFIGNAME = :CONFIGNAME ");
        parser.addSQL(" AND C.VALID_TAG = '0' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

    }

    public static IDataset getWorkfromProductAttr(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT ");
        parser.addSQL(" C.RECORD_NUM,C.BUSI_STATE ");
        parser.addSQL(" FROM TF_B_EOP_EOMS_STATE C ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID = :IBSYSID ");

        IDataset eomsStateInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(eomsStateInfos)) {
            for (Object object : eomsStateInfos) {
                IData eomsStateInfo = (IData) object;
                IData params1 = new DataMap();
                params1.put("IBSYSID", param.getString("IBSYSID"));
                params1.put("RECORD_NUM", eomsStateInfo.getString("RECORD_NUM"));

                // 获取最新的GROUP_SEQ
                SQLParser parser3 = new SQLParser(params1);
                parser3.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID  FROM ");
                parser3.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID, ");
                parser3.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
                parser3.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'renewWorkSheet') R ");
                parser3.addSQL(" WHERE 1=1 ");
                parser3.addSQL(" AND R.G<=1 ");
                IDataset eomsInfos = Dao.qryByParse(parser3, Route.getJourDb(Route.CONN_CRM_CG));
                String groupSeq = "";
                if (IDataUtil.isNotEmpty(eomsInfos)) {
                    groupSeq = eomsInfos.getData(0).getString("GROUP_SEQ");
                } else {
                    SQLParser parser4 = new SQLParser(params1);
                    parser4.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID  FROM ");
                    parser4.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID, ");
                    parser4.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
                    parser4.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'newWorkSheet') R ");
                    parser4.addSQL(" WHERE 1=1 ");
                    parser4.addSQL(" AND R.G<=1 ");
                    IDataset eomsInfosss = Dao.qryByParse(parser4, Route.getJourDb(Route.CONN_CRM_CG));
                    if (IDataUtil.isNotEmpty(eomsInfosss)) {
                        groupSeq = eomsInfosss.getData(0).getString("GROUP_SEQ");
                    }
                }

                // 获取地址
                IData params2 = new DataMap();
                params2.put("IBSYSID", param.getString("IBSYSID"));
                params2.put("RECORD_NUM", eomsStateInfo.getString("RECORD_NUM"));
                params2.put("GROUP_SEQ", groupSeq);

                SQLParser parser2 = new SQLParser(params2);
                parser2.addSQL(" SELECT C.ATTR_CODE,  ");
                parser2.addSQL("        C.ATTR_VALUE, ");
                parser2.addSQL("        D.PRODUCT_NAME, ");
                parser2.addSQL("        D.PRODUCT_ID ");
                parser2.addSQL("  FROM TF_B_EOP_ATTR C, ");
                parser2.addSQL("       TF_B_EOP_PRODUCT D, ");
                parser2.addSQL("       TF_B_EWE E ");
                parser2.addSQL("  WHERE 1=1 ");
                parser2.addSQL("  AND C.IBSYSID = :IBSYSID ");
                parser2.addSQL("  AND C.GROUP_SEQ = :GROUP_SEQ ");
                parser2.addSQL("  AND C.RECORD_NUM =:RECORD_NUM ");
                parser2.addSQL("  AND E.BI_SN = C.IBSYSID ");
                parser2.addSQL("  AND D.IBSYSID = C.IBSYSID ");
                parser2.addSQL("  AND E.BUSI_CODE = D.PRODUCT_ID ");
                parser2.addSQL("  AND E.TEMPLET_TYPE = '0' ");
                IDataset datalineAttrinfos = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
                for (Object object2 : datalineAttrinfos) {
                    IData attrInfo = (IData) object2;
                    String productNmae = attrInfo.getString("PRODUCT_NAME", "");
                    String productId = attrInfo.getString("PRODUCT_ID", "");
                    eomsStateInfo.put("PRODUCTNAME", productNmae);
                    eomsStateInfo.put("PRODUCT_ID", productId);
                    eomsStateInfo.put(attrInfo.getString("ATTR_CODE"), attrInfo.getString("ATTR_VALUE"));
                }
                eomsStateInfo.put("IBSYSID", param.getString("IBSYSID"));

                if ("C".equals(eomsStateInfo.getString("BUSI_STATE"))) {
                    eomsStateInfo.put("BUSI_STATE", "已撤单");
                } else {
                    eomsStateInfo.put("BUSI_STATE", "已开通");
                }
            }
        }
        return eomsStateInfos;
    }

    public static IDataset getWorkfromEoms(String ibsysId) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", ibsysId);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT DISTINCT C.NODE_ID, ");
        parser.addSQL(" C.IBSYSID, ");
        parser.addSQL(" C.PRODUCT_ID, ");
        parser.addSQL(" C.SERIALNO, ");
        parser.addSQL(" C.SHEETTYPE ");
        parser.addSQL(" FROM TF_B_EOP_EOMS C ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID = :IBSYSID ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset querydataLineInfoList(IData param) throws Exception {
        IData params = new DataMap();
        params.put("RECORD_NUM", param.getString("RECORD_NUM"));
        params.put("NODE_ID", param.getString("NODE_ID"));
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
        params.put("GROUP_SEQ", param.getString("GROUP_SEQ"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, ");
        parser.addSQL(" T.GROUP_SEQ, ");
        parser.addSQL(" T.SEQ, ");
        parser.addSQL(" T.ATTR_CODE, ");
        parser.addSQL(" T.ATTR_NAME, ");
        parser.addSQL(" T.ATTR_VALUE, ");
        parser.addSQL(" T.UPDATE_TIME, ");
        parser.addSQL(" T.RECORD_NUM, ");
        parser.addSQL(" T.ACCEPT_MONTH, ");
        parser.addSQL(" D.ACCEPT_STAFF_ID ");
        parser.addSQL(" FROM TF_B_EOP_ATTR T, TF_B_EWE D ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.IBSYSID =D.BI_SN ");
        parser.addSQL(" AND T.RECORD_NUM =:RECORD_NUM ");
        parser.addSQL(" AND T.NODE_ID =:NODE_ID ");
        parser.addSQL(" AND T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.SUB_IBSYSID =:SUB_IBSYSID ");
        parser.addSQL(" AND T.GROUP_SEQ =:GROUP_SEQ ");
        parser.addSQL(" AND D.TEMPLET_TYPE = '0' ");
        IDataset attrInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if ("0".equals(param.getString("RECORD_NUM")) && IDataUtil.isNotEmpty(attrInfos)) {
            IData params1 = new DataMap();
            params1.put("IBSYSID", param.getString("IBSYSID"));
            params1.put("RECORD_NUM", param.getString("RECORD_NUM_A", ""));
            SQLParser parser1 = new SQLParser(params1);
            parser1.addSQL(" SELECT  T.RECORD_NUM,T.SUB_IBSYSID,T.GROUP_SEQ,T.PRODUCT_ID ");
            parser1.addSQL(" FROM TF_B_EOP_EOMS T ");
            parser1.addSQL(" WHERE 1=1 ");
            parser1.addSQL(" AND T.IBSYSID =:IBSYSID ");
            if ("34".equals(param.getString("DEAL_TYPE"))) {
                parser1.addSQL(" AND T.RECORD_NUM =:RECORD_NUM ");
            }
            parser1.addSQL(" AND T.OPER_TYPE ='withdrawWorkSheet' ");
            parser1.addSQL(" ORDER BY T.INSERT_TIME DESC ");
            IDataset eomsinfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
            if (IDataUtil.isNotEmpty(eomsinfos)) {
                IData params2 = new DataMap();
                if ("34".equals(param.getString("DEAL_TYPE"))) {
                    params2.put("RECORD_NUM", param.getString("RECORD_NUM_A", ""));
                } else {
                    params2.put("RECORD_NUM", eomsinfos.getData(0).getString("RECORD_NUM"));
                }
                params2.put("SUB_IBSYSID", eomsinfos.getData(0).getString("SUB_IBSYSID"));
                params2.put("GROUP_SEQ", eomsinfos.getData(0).getString("GROUP_SEQ"));
                SQLParser parse2 = new SQLParser(params2);
                parse2.addSQL(" SELECT ");
                parse2.addSQL(" T.ATTR_CODE, ");
                parse2.addSQL(" T.ATTR_VALUE ");
                parse2.addSQL(" FROM TF_B_EOP_ATTR T ");
                parse2.addSQL(" WHERE 1=1 ");
                parse2.addSQL(" AND T.RECORD_NUM =:RECORD_NUM ");
                parse2.addSQL(" AND T.SUB_IBSYSID =:SUB_IBSYSID ");
                parse2.addSQL(" AND T.GROUP_SEQ =:GROUP_SEQ ");
                IDataset eomsAttrInfos = Dao.qryByParse(parse2, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(eomsAttrInfos)) {
                    for (Object object : attrInfos) {
                        IData attrInfo = (IData) object;
                        for (Object object2 : eomsAttrInfos) {
                            IData eomsAttrInfo = (IData) object2;
                            if ("serialNo".equals(eomsAttrInfo.getString("ATTR_CODE"))) {
                                attrInfo.put("SERIALNO", eomsAttrInfo.getString("ATTR_VALUE"));
                            }
                            if ("opType".equals(eomsAttrInfo.getString("ATTR_CODE"))) {
                                attrInfo.put("OP_TYPE", eomsAttrInfo.getString("ATTR_VALUE"));
                            }
                            if ("opDesc".equals(eomsAttrInfo.getString("ATTR_CODE"))) {
                                attrInfo.put("OP_DESC", eomsAttrInfo.getString("ATTR_VALUE"));
                            }

                        }
                    }
                }

            }
        }
        return attrInfos;
    }

    public static IDataset qryDatalineTradeAttrInfos(String userId) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT  TO_CHAR(T.TRADE_ID) TRADE_ID, ");
        parser.addSQL(" T.ACCEPT_MONTH, ");
        parser.addSQL(" TO_CHAR(T.USER_ID) USER_ID, ");
        parser.addSQL(" T.INST_TYPE, ");
        parser.addSQL(" T.SHEET_TYPE, ");
        parser.addSQL(" T.PRODUCT_NO, ");
        parser.addSQL(" T.INST_ID, ");
        parser.addSQL(" T.ATTR_CODE, ");
        parser.addSQL(" T.ATTR_VALUE, ");
        parser.addSQL(" TO_CHAR(T.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        parser.addSQL(" TO_CHAR(T.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        parser.addSQL(" T.MODIFY_TAG, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        parser.addSQL(" T.UPDATE_STAFF_ID, ");
        parser.addSQL(" T.UPDATE_DEPART_ID, ");
        parser.addSQL(" T.REMARK ");
        parser.addSQL(" FROM TF_B_TRADE_DATALINE_ATTR T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.USER_ID = TO_NUMBER(:USER_ID) ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryAllWorkFormInfos(String busiFromId, Pagination pagination) throws Exception {

        IData params = new DataMap();
        params.put("BUSIFORM_ID", busiFromId);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.BUSIFORM_ID, ");
        parser.addSQL("       TO_CHAR(C.CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        parser.addSQL("       TO_CHAR(A.UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL("       D.LOG_INFO, ");
        parser.addSQL("       D.LOG_INFO1, ");
        parser.addSQL("       D.LOG_INFO2, ");
        parser.addSQL("       D.LOG_INFO3, ");
        parser.addSQL("       D.LOG_INFO4, ");
        parser.addSQL("       A.EPARCHY_CODE AREA_ID ");
        parser.addSQL("  FROM TF_B_EWE_NODE A, ");
        parser.addSQL("       TF_B_EWE_STEP  B, ");
        parser.addSQL("       TF_B_EWE C, ");
        parser.addSQL("       TF_B_EWE_ERROR_LOG D ");
        parser.addSQL(" WHERE B.BUSIFORM_ID = A.BUSIFORM_ID ");
        parser.addSQL("   AND B.BUSIFORM_ID = C.BUSIFORM_ID ");
        parser.addSQL("   AND B.BUSIFORM_ID = D.BUSIFORM_ID ");
        parser.addSQL("   AND A.STATE='9' ");
        parser.addSQL("   AND B.STATE='M' ");
        parser.addSQL("   AND (A.BUSIFORM_ID =:BUSIFORM_ID or :BUSIFORM_ID is null ) ");

        return null;// Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryStmtDirect(String paramName) throws Exception {

        IData params = new DataMap();
        params.put("PARAMNAME", paramName);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.SYSCODE, ");
        parser.addSQL("       C.CONFIGNAME, ");
        parser.addSQL("       C.CONFIGDESC, ");
        parser.addSQL("       C.PARAMNAME, ");
        parser.addSQL("       C.VALUESEQ, ");
        parser.addSQL("       C.PARAMVALUE, ");
        parser.addSQL("       C.VALUEDESC, ");
        parser.addSQL("       C.REMARK, ");
        parser.addSQL("       C.VALID_TAG  ");
        parser.addSQL("  FROM TD_B_EWE_CONFIG C ");
        parser.addSQL(" WHERE 1=1 ");
        if (StringUtils.isBlank(paramName)) {
            parser.addSQL("  AND C.CONFIGNAME = 'BUSINESS_STATUS' ");
        } else {
            parser.addSQL("  AND C.CONFIGNAME = 'BUSINESS_STATUS' ");
            parser.addSQL("  AND C.PARAMNAME = :PARAMNAME ");
        }

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

    }

    public static IDataset queryEopSubscribe(String configName) throws Exception {

        IData params = new DataMap();
        params.put("BPM_TEMPLET_ID", configName);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL("       C.ACCEPT_MONTH, ");
        parser.addSQL("       C.ORDER_ID, ");
        parser.addSQL("       C.BPM_TEMPLET_ID, ");
        parser.addSQL("       C.IN_MODE_CODE, ");
        parser.addSQL("       C.DEAL_STATE, ");
        parser.addSQL("       C.DEAL_RESULT, ");
        parser.addSQL("       C.BUSI_TYPE, ");
        parser.addSQL("       C.BUSI_CODE,  ");
        parser.addSQL("       C.GROUP_ID, ");
        parser.addSQL("       C.CUST_NAME, ");
        parser.addSQL("       C.ACCEPT_TIME, ");
        parser.addSQL("       C.EPARCHY_CODE,  ");
        parser.addSQL("       C.FLOW_EXPECT_TIME, ");
        parser.addSQL("       C.FLOW_LEVEL, ");
        parser.addSQL("       C.FLOW_DESC, ");
        parser.addSQL("       C.REMARK ");
        parser.addSQL("  FROM TF_B_EOP_SUBSCRIBE C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND C.BPM_TEMPLET_ID = :BPM_TEMPLET_ID ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static IDataset queryNodetemplet(String configName) throws Exception {

        IData params = new DataMap();
        params.put("BPM_TEMPLET_ID", configName);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.BPM_TEMPLET_ID, ");
        parser.addSQL("       C.NODE_ID, ");
        parser.addSQL("       C.NODE_NAME, ");
        parser.addSQL("       C.NODE_DESC, ");
        parser.addSQL("       C.NODE_POS, ");
        parser.addSQL("       C.NODE_TYPE, ");
        parser.addSQL("       C.REMARK, ");
        parser.addSQL("       C.VALID_TAG, ");
        parser.addSQL("       C.PAGELOAD_EXPRESS,  ");
        parser.addSQL("       C.ACCEPT_DEPART_ID, ");
        parser.addSQL("       C.UPDATE_DEPART_ID, ");
        parser.addSQL("       C.UPDATE_STAFF_ID, ");
        parser.addSQL("       C.ACCEPT_STAFF_ID,  ");
        parser.addSQL("       C.CREATE_DATE, ");
        parser.addSQL("       C.UPDATE_DATE ");
        parser.addSQL("  FROM TD_B_EWE_NODE_TEMPLET C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND C.BPM_TEMPLET_ID = :BPM_TEMPLET_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

    }

    public static IDataset queryeopNode(String ibsysId, String nodeId) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", ibsysId);
        params.put("NODE_ID", nodeId);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.SUB_IBSYSID, ");
        parser.addSQL("       C.ACCEPT_MONTH, ");
        parser.addSQL("       C.IBSYSID, ");
        parser.addSQL("       C.NODE_ID, ");
        parser.addSQL("       C.PRODUCT_ID, ");
        parser.addSQL("       C.ORDER_ID, ");
        parser.addSQL("       C.DEAL_STATE, ");
        parser.addSQL("       C.DEAL_OPTION, ");
        parser.addSQL("       C.DEAL_SRC,  ");
        parser.addSQL("       C.DEAL_DESC, ");
        parser.addSQL("       C.DEAL_TIME, ");
        parser.addSQL("       C.TRANSFER_PARAM, ");
        parser.addSQL("       C.ROLE_ID,  ");
        parser.addSQL("       C.WORKFORM_TYPE, ");
        parser.addSQL("       C.INSERT_TIME, ");
        parser.addSQL("       C.CITY_CODE, ");
        parser.addSQL("       C.EPARCHY_CODE, ");
        parser.addSQL("       C.DEPART_ID,  ");
        parser.addSQL("       C.DEPART_NAME, ");
        parser.addSQL("       C.STAFF_ID, ");
        parser.addSQL("       C.STAFF_NAME, ");
        parser.addSQL("       C.STAFF_PHONE,  ");
        parser.addSQL("       C.REMARK, ");
        parser.addSQL("       C.RSRV_STR1, ");
        parser.addSQL("       C.RSRV_STR2, ");
        parser.addSQL("       C.RSRV_STR3, ");
        parser.addSQL("       C.RSRV_STR4,  ");
        parser.addSQL("       C.RSRV_STR5, ");
        parser.addSQL("       C.RSRV_STR6, ");
        parser.addSQL("       C.RSRV_STR7 ");
        parser.addSQL("  FROM TF_B_EOP_NODE C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND C.IBSYSID = :IBSYSID ");
        parser.addSQL("  AND C.NODE_ID = :NODE_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

    }

    public static IDataset queryArchiveWay(String groupId, String idsysId, String productId) throws Exception {

        IData params = new DataMap();
        params.put("GROUP_ID", groupId);
        params.put("IBSYSID", idsysId);
        params.put("PRODUCT_ID", productId);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.IBSYSID, ");
        parser.addSQL("       T.BUSI_CODE, ");
        parser.addSQL("       T.ACCEPT_MONTH, ");
        parser.addSQL("       T.GROUP_ID, ");
        parser.addSQL("       T.RSRV_STR4, ");
        parser.addSQL("       T.RSRV_STR3, ");
        parser.addSQL("       A.BUSIFORM_ID, ");
        parser.addSQL("       B.NODE_ID, ");
        parser.addSQL("       B.ACCEPT_STAFF_ID,  ");
        parser.addSQL("       TO_CHAR(B.UPDATE_DATE,'yyyy-mm-dd hh24:mi:ss') UPDATE_DATE, ");
        parser.addSQL("       C.PRODUCT_NO, ");
        parser.addSQL("       C.SERIALNO, ");
        parser.addSQL("       C.RECORD_NUM,  ");
        parser.addSQL("       C.BUSI_STATE ");
        parser.addSQL("  FROM TF_B_EOP_SUBSCRIBE T, ");
        parser.addSQL("   TF_B_EWE A, ");
        parser.addSQL("   TF_B_EWE_NODE B, ");
        parser.addSQL("   TF_B_EOP_EOMS_STATE C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND T.IBSYSID = A.BI_SN ");
        parser.addSQL("  AND A.BUSIFORM_ID = B.BUSIFORM_ID ");
        parser.addSQL("  AND C.IBSYSID = T.IBSYSID ");
        parser.addSQL("  AND T.GROUP_ID = :GROUP_ID ");
        parser.addSQL("  AND T.BUSI_CODE = :PRODUCT_ID ");
        parser.addSQL("  AND T.BPM_TEMPLET_ID = 'EDIRECTLINEOPENPBOSS' ");
        parser.addSQL("  AND B.UPDATE_DATE = (SELECT MAX(UPDATE_DATE) FROM TF_B_EWE_NODE D  WHERE D.BUSIFORM_ID= A.BUSIFORM_ID) ");
        parser.addSQL("  AND (A.IBSYSID =:IBSYSID or :IBSYSID is null ) ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static IDataset queryNodeTempletInfo(String nodeId) throws Exception {

        IData params = new DataMap();
        params.put("NODE_ID", nodeId);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.BPM_TEMPLET_ID, ");
        parser.addSQL("       C.NODE_ID, ");
        parser.addSQL("       C.NODE_NAME, ");
        parser.addSQL("       C.NODE_DESC, ");
        parser.addSQL("       C.NODE_POS, ");
        parser.addSQL("       C.NODE_TYPE, ");
        parser.addSQL("       C.REMARK, ");
        parser.addSQL("       C.VALID_TAG, ");
        parser.addSQL("       C.PAGELOAD_EXPRESS,  ");
        parser.addSQL("       C.ACCEPT_DEPART_ID, ");
        parser.addSQL("       C.UPDATE_DEPART_ID, ");
        parser.addSQL("       C.UPDATE_STAFF_ID, ");
        parser.addSQL("       C.ACCEPT_STAFF_ID  ");
        parser.addSQL("  FROM TD_B_EWE_NODE_TEMPLET C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND C.NODE_ID = :NODE_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

    }

    public static IDataset qryArchiveWay(String ibsysid) throws Exception {
        IData params = new DataMap();
        params.put("MAIN_IBSYSID", ibsysid);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL("       C.ACCEPT_MONTH, ");
        parser.addSQL("       C.MAIN_IBSYSID, ");
        parser.addSQL("       C.ATTR_CODE, ");
        parser.addSQL("       C.ATTR_VALUE ATTR_NEW_VALUE, ");
        parser.addSQL("       C.ATTR_MAIN_VALUE ATTR_OLD_VALUE, ");
        parser.addSQL("       C.STAFF_ID, ");
        parser.addSQL("       C.CITY_CODE, ");
        parser.addSQL("       C.DEPART_ID,  ");
        parser.addSQL("       C.CITY_CODE_A, ");
        parser.addSQL("       C.EPARCHY_CODE, ");
        parser.addSQL("  TO_CHAR(C.VALID_DATE, 'yyyy-mm-dd hh24:mi:ss') VALID_DATE, ");
        parser.addSQL("  TO_CHAR(C.ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE ");
        parser.addSQL("  FROM TF_B_EOP_MODI_TRACE C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND C.MAIN_IBSYSID = :MAIN_IBSYSID ");
        parser.addSQL("  AND C.ATTR_CODE = 'ARCHIVE_WAY' ");
        parser.addSQL("  AND C.ATTR_TYPE = 'P' ");
        parser.addSQL("  AND C.VALID_DATE = (SELECT MAX(VALID_DATE) FROM TF_B_EOP_MODI_TRACE D  WHERE D.MAIN_IBSYSID= :MAIN_IBSYSID) ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static IDataset queryClearList(IData map, Pagination pagination) throws Exception {
        String subTypeOpen = map.getString("SUB_TYPE_OPEN");
        if (StringUtils.isNotEmpty(subTypeOpen)) {
            if ("1".equals(subTypeOpen)) {
                subTypeOpen = "开通单";
            } else if ("2".equals(subTypeOpen)) {
                subTypeOpen = "变更开通单";
            }
        }
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("START_DATE", map.getString("ACCEPT_START"));
        params.put("END_DATE", map.getString("ACCEPT_END"));
        params.put("SUB_TYPE", subTypeOpen);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT * FROM (  SELECT C.IBSYSID,C.BIZ_RANGE,C.PRODUCT_NAME, ");
        parser.addSQL("       C.PRODUCT_NO,C.NODE_ID1,C.NODE_ID1_DEALTIME, ");
        parser.addSQL("       C.TITLE,C.NODE_ID2,C.NODE_ID2_DEALTIME, ");
        parser.addSQL("       C.GROUP_ID,C.NODE_ID3,C.NODE_ID3_DEALTIME, ");
        parser.addSQL("       C.CUST_NAME,C.NODE_ID4,C.NODE_ID4_DEALTIME, ");
        parser.addSQL("       C.CITY_CODE,C.NODE_ID5,C.NODE_ID5_DEALTIME, ");
        parser.addSQL("       C.CUST_MANAGER_NAME,C.NODE_ID6,C.NODE_ID6_DEALTIME, ");
        parser.addSQL("       C.BUSINESS_TYPE,C.NODE_ID7,C.NODE_ID7_DEALTIME, ");
        parser.addSQL("       C.PORT_A_CUST,C.NODE_ID8,C.NODE_ID8_DEALTIME,  ");
        parser.addSQL("       C.PORT_Z_CUST,C.NODE_ID9,C.NODE_ID9_DEALTIME, ");
        parser.addSQL("       C.BUILDING_SECTION,C.NODE_ID10,C.NODE_ID10_DEALTIME, ");
        parser.addSQL("       C.SUB_TYPE,C.NODE_ID11,C.NODE_ID11_DEALTIME, ");
        parser.addSQL("       C.NODE_ID12,C.NODE_ID12_DEALTIME, ");
        parser.addSQL("       C.NODE_ID13,C.NODE_ID13_DEALTIME, ");
        parser.addSQL("       C.NODE_ID14,C.NODE_ID14_DEALTIME, ");
        parser.addSQL(" TO_CHAR(C.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID1_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID1_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID2_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID2_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID3_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID3_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID4_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID4_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID5_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID5_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID6_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID6_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID7_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID7_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID8_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID8_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID9_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID9_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID10_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID10_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID11_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID11_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID12_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID12_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID13_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID13_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID14_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID14_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL(" C.RSRV_STR1,C.RSRV_STR2,C.RSRV_STR3,C.RSRV_STR4,C.RSRV_STR5,'' FINISH_DATE, ");
        parser.addSQL(" C.RSRV_TAG1,C.RSRV_TAG2,C.RSRV_TAG3,W.ATTR_VALUE,W.RECORD_NUM,Q.STATE,Q.ACCEPT_STAFF_ID  ");
        parser.addSQL(" FROM TF_B_EOP_LINEREPORT_LIST C,TF_B_EOP_ATTR W,TF_B_EWE Q,TF_B_EOP_EOMS_STATE H ");
        parser.addSQL("  WHERE W.ATTR_CODE='BIZSECURITYLV' ");
        parser.addSQL("  AND W.NODE_ID='apply'  ");
        parser.addSQL("  AND W.SUB_IBSYSID = (SELECT MAX(T.SUB_IBSYSID) FROM TF_B_EOP_ATTR T WHERE T.IBSYSID = C.IBSYSID ");
        parser.addSQL("  AND T.NODE_ID='apply' AND T.ATTR_CODE='BIZSECURITYLV') ");
        parser.addSQL("  AND W.RECORD_NUM <> '0' ");
        parser.addSQL("  AND C.IBSYSID= Q.BI_SN ");
        parser.addSQL("  AND Q.TEMPLET_TYPE= '0' ");
        parser.addSQL("  AND W.IBSYSID=C.IBSYSID ");
        parser.addSQL("  AND C.IBSYSID = H.IBSYSID ");
        parser.addSQL("  AND C.PRODUCT_NO = H.PRODUCT_NO ");
        parser.addSQL("  AND W.RECORD_NUM = H.RECORD_NUM ");
        parser.addSQL("  AND (C.IBSYSID =:IBSYSID or :IBSYSID is null) ");
        if (StringUtils.isNotBlank(subTypeOpen)) {
            parser.addSQL(" AND C.SUB_TYPE =:SUB_TYPE ");
        } else {
            parser.addSQL(" AND C.SUB_TYPE IN ('开通单','变更开通单') ");
        }
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') >= to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT C.IBSYSID,C.BIZ_RANGE,C.PRODUCT_NAME, ");
        parser.addSQL("       C.PRODUCT_NO,C.NODE_ID1,C.NODE_ID1_DEALTIME, ");
        parser.addSQL("       C.TITLE,C.NODE_ID2,C.NODE_ID2_DEALTIME, ");
        parser.addSQL("       C.GROUP_ID,C.NODE_ID3,C.NODE_ID3_DEALTIME, ");
        parser.addSQL("       C.CUST_NAME,C.NODE_ID4,C.NODE_ID4_DEALTIME, ");
        parser.addSQL("       C.CITY_CODE,C.NODE_ID5,C.NODE_ID5_DEALTIME, ");
        parser.addSQL("       C.CUST_MANAGER_NAME,C.NODE_ID6,C.NODE_ID6_DEALTIME, ");
        parser.addSQL("       C.BUSINESS_TYPE,C.NODE_ID7,C.NODE_ID7_DEALTIME, ");
        parser.addSQL("       C.PORT_A_CUST,C.NODE_ID8,C.NODE_ID8_DEALTIME,  ");
        parser.addSQL("       C.PORT_Z_CUST,C.NODE_ID9,C.NODE_ID9_DEALTIME, ");
        parser.addSQL("       C.BUILDING_SECTION,C.NODE_ID10,C.NODE_ID10_DEALTIME, ");
        parser.addSQL("       C.SUB_TYPE,C.NODE_ID11,C.NODE_ID11_DEALTIME, ");
        parser.addSQL("       C.NODE_ID12,C.NODE_ID12_DEALTIME, ");
        parser.addSQL("       C.NODE_ID13,C.NODE_ID13_DEALTIME, ");
        parser.addSQL("       C.NODE_ID14,C.NODE_ID14_DEALTIME, ");
        parser.addSQL(" TO_CHAR(C.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID1_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID1_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID2_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID2_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID3_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID3_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID4_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID4_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID5_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID5_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID6_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID6_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID7_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID7_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID8_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID8_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID9_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID9_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID10_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID10_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID11_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID11_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID12_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID12_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID13_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID13_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID14_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID14_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL(" C.RSRV_STR1,C.RSRV_STR2,C.RSRV_STR3,C.RSRV_STR4,C.RSRV_STR5, ");
        parser.addSQL(" TO_CHAR(Q.FINISH_DATE,'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
        parser.addSQL(" C.RSRV_TAG1,C.RSRV_TAG2,C.RSRV_TAG3,W.ATTR_VALUE,W.RECORD_NUM,Q.STATE,Q.ACCEPT_STAFF_ID  ");
        parser.addSQL(" FROM TF_B_EOP_LINEREPORT_LIST C,TF_BH_EOP_ATTR W,TF_BH_EWE Q,TF_BH_EOP_EOMS_STATE H ");
        parser.addSQL("  WHERE W.ATTR_CODE='BIZSECURITYLV' ");
        parser.addSQL("  AND W.NODE_ID='apply'  ");
        parser.addSQL("  AND W.SUB_IBSYSID = (SELECT MAX(T.SUB_IBSYSID) FROM TF_BH_EOP_ATTR T WHERE T.IBSYSID = C.IBSYSID ");
        parser.addSQL("  AND T.NODE_ID='apply' AND T.ATTR_CODE='BIZSECURITYLV') ");
        parser.addSQL("  AND W.RECORD_NUM <> '0'  ");
        parser.addSQL("  AND W.IBSYSID=C.IBSYSID ");
        parser.addSQL("  AND C.IBSYSID= Q.BI_SN ");
        parser.addSQL("  AND Q.TEMPLET_TYPE= '0' ");
        parser.addSQL("  AND C.IBSYSID = H.IBSYSID ");
        parser.addSQL("  AND C.PRODUCT_NO = H.PRODUCT_NO ");
        parser.addSQL("  AND W.RECORD_NUM = H.RECORD_NUM ");
        parser.addSQL(" AND (C.IBSYSID =:IBSYSID or :IBSYSID is null) ");
        if (StringUtils.isNotBlank(subTypeOpen)) {
            parser.addSQL(" AND C.SUB_TYPE =:SUB_TYPE ");
        } else {
            parser.addSQL(" AND C.SUB_TYPE IN ('开通单','变更开通单') ");
        }
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') >= to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" ) Z Order by Z.IBSYSID ");

        IDataset attrlineInfos = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(attrlineInfos)) {
            for (Object attrIf : attrlineInfos) {
                IData attlineInfo = (IData) attrIf;
                String ibsysId = attlineInfo.getString("IBSYSID");
                String recordNum = attlineInfo.getString("RECORD_NUM");
                String state = attlineInfo.getString("STATE", "");
                String acceptStaffId = attlineInfo.getString("ACCEPT_STAFF_ID", "");

                String dealStaffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", acceptStaffId);
                attlineInfo.put("ACCEPT_STAFF_NAME", dealStaffName);

                if ("4".equals(state)) {
                    attlineInfo.put("STATE_TYPE", "已撤单");
                } else if ("9".equals(state)) {
                    attlineInfo.put("STATE_TYPE", "已完成");
                } else {
                    attlineInfo.put("STATE_TYPE", "未完成");
                }
                String groupId = attlineInfo.getString("GROUP_ID", "");
                IData data = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                if (IDataUtil.isNotEmpty(data)) {
                    String servLevel = data.getString("SERV_LEVEL", "");
                    if (StringUtils.isBlank(servLevel)) {
                        servLevel = "4";
                    }
                    String servicelevel = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[] { "CUSTGROUP_SERVLEVEL", servLevel });
                    attlineInfo.put("SERVICELEVEL", servicelevel);
                }

                IData params1 = new DataMap();
                params1.put("IBSYSID", ibsysId);
                params1.put("RECORD_NUM", recordNum);

                SQLParser parser2 = new SQLParser(params1);
                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.ATTR_CODE ");
                parser2.addSQL(" FROM TF_B_EOP_ATTR C ");
                parser2.addSQL(" WHERE C.ATTR_CODE = 'PRODUCTNO' ");
                parser2.addSQL(" AND C.NODE_ID = 'apply'  ");
                parser2.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
                parser2.addSQL(" AND C.IBSYSID = :IBSYSID ");
                parser2.addSQL(" UNION ALL ");
                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.ATTR_CODE ");
                parser2.addSQL(" FROM TF_BH_EOP_ATTR C ");
                parser2.addSQL(" WHERE C.ATTR_CODE = 'PRODUCTNO' ");
                parser2.addSQL(" AND C.NODE_ID = 'apply'  ");
                parser2.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
                parser2.addSQL(" AND C.IBSYSID = :IBSYSID ");

                IDataset attrData2 = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(attrData2)) {
                    String productNo = attrData2.first().getString("ATTR_VALUE");
                    attlineInfo.put("PRODUCTNO", productNo);
                }
            }

        }

        return attrlineInfos;

    }

    public static IDataset querySurveyList(IData map, Pagination pagination) throws Exception {
        String subTypeOpen = map.getString("SUB_TYPE_OPEN");
        if (StringUtils.isNotEmpty(subTypeOpen)) {
            if ("1".equals(subTypeOpen)) {
                subTypeOpen = "开通勘察单";
            } else if ("2".equals(subTypeOpen)) {
                subTypeOpen = "变更勘察单";
            }
        }
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("START_DATE", map.getString("ACCEPT_START"));
        params.put("END_DATE", map.getString("ACCEPT_END"));
        params.put("SUB_TYPE", subTypeOpen);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT * FROM (  SELECT C.IBSYSID,C.BIZ_RANGE,C.PRODUCT_NAME, ");
        parser.addSQL("       C.PRODUCT_NO,C.NODE_ID1,C.NODE_ID1_DEALTIME, ");
        parser.addSQL("       C.TITLE,C.NODE_ID2,C.NODE_ID2_DEALTIME, ");
        parser.addSQL("       C.GROUP_ID,C.NODE_ID3,C.NODE_ID3_DEALTIME, ");
        parser.addSQL("       C.CUST_NAME,C.NODE_ID4,C.NODE_ID4_DEALTIME, ");
        parser.addSQL("       C.CITY_CODE,C.NODE_ID5,C.NODE_ID5_DEALTIME, ");
        parser.addSQL("       C.CUST_MANAGER_NAME,C.NODE_ID6,C.NODE_ID6_DEALTIME, ");
        parser.addSQL("       C.BUSINESS_TYPE,C.NODE_ID7,C.NODE_ID7_DEALTIME, ");
        parser.addSQL("       C.PORT_A_CUST,C.NODE_ID8,C.NODE_ID8_DEALTIME,  ");
        parser.addSQL("       C.PORT_Z_CUST,C.NODE_ID9,C.NODE_ID9_DEALTIME, ");
        parser.addSQL("       C.BUILDING_SECTION,C.NODE_ID10,C.NODE_ID10_DEALTIME, ");
        parser.addSQL("       C.SUB_TYPE,C.NODE_ID11,C.NODE_ID11_DEALTIME, ");
        parser.addSQL("       C.NODE_ID12,C.NODE_ID12_DEALTIME, ");
        parser.addSQL("       C.NODE_ID13,C.NODE_ID13_DEALTIME, ");
        parser.addSQL("       C.NODE_ID14,C.NODE_ID14_DEALTIME, ");
        parser.addSQL(" TO_CHAR(C.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID1_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID1_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID2_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID2_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID3_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID3_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID4_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID4_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID5_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID5_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID6_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID6_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID7_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID7_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID8_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID8_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID9_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID9_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID10_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID10_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID11_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID11_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID12_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID12_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID13_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID13_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID14_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID14_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL(" C.RSRV_STR1,C.RSRV_STR2,C.RSRV_STR3,C.RSRV_STR4,C.RSRV_STR5,'' FINISH_DATE, ");
        parser.addSQL(" C.RSRV_TAG1,C.RSRV_TAG2,C.RSRV_TAG3,W.ATTR_VALUE,W.RECORD_NUM,Q.STATE,Q.ACCEPT_STAFF_ID  ");
        parser.addSQL(" FROM TF_B_EOP_LINEREPORT_LIST C,TF_B_EOP_ATTR W,TF_B_EWE Q ");
        parser.addSQL("  WHERE W.ATTR_CODE='BIZSECURITYLV' ");
        parser.addSQL("  AND W.NODE_ID='apply'  ");
        parser.addSQL("  AND W.SUB_IBSYSID = (SELECT MAX(T.SUB_IBSYSID) FROM TF_B_EOP_ATTR T WHERE T.IBSYSID = C.IBSYSID ");
        parser.addSQL("  AND T.NODE_ID='apply' AND T.ATTR_CODE='BIZSECURITYLV') ");
        parser.addSQL("  AND W.RECORD_NUM <> '0'  ");
        parser.addSQL("  AND C.IBSYSID= Q.BI_SN ");
        parser.addSQL("  AND Q.TEMPLET_TYPE= '0' ");
        parser.addSQL("  AND W.IBSYSID=C.IBSYSID ");
        parser.addSQL("  AND (C.IBSYSID =:IBSYSID or :IBSYSID is null) ");
        if (StringUtils.isNotBlank(subTypeOpen)) {
            parser.addSQL(" AND C.SUB_TYPE =:SUB_TYPE ");
        } else {
            parser.addSQL(" AND C.SUB_TYPE IN ('开通勘察单','变更勘察单') ");
        }
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') >= to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT C.IBSYSID,C.BIZ_RANGE,C.PRODUCT_NAME, ");
        parser.addSQL("       C.PRODUCT_NO,C.NODE_ID1,C.NODE_ID1_DEALTIME, ");
        parser.addSQL("       C.TITLE,C.NODE_ID2,C.NODE_ID2_DEALTIME, ");
        parser.addSQL("       C.GROUP_ID,C.NODE_ID3,C.NODE_ID3_DEALTIME, ");
        parser.addSQL("       C.CUST_NAME,C.NODE_ID4,C.NODE_ID4_DEALTIME, ");
        parser.addSQL("       C.CITY_CODE,C.NODE_ID5,C.NODE_ID5_DEALTIME, ");
        parser.addSQL("       C.CUST_MANAGER_NAME,C.NODE_ID6,C.NODE_ID6_DEALTIME, ");
        parser.addSQL("       C.BUSINESS_TYPE,C.NODE_ID7,C.NODE_ID7_DEALTIME, ");
        parser.addSQL("       C.PORT_A_CUST,C.NODE_ID8,C.NODE_ID8_DEALTIME,  ");
        parser.addSQL("       C.PORT_Z_CUST,C.NODE_ID9,C.NODE_ID9_DEALTIME, ");
        parser.addSQL("       C.BUILDING_SECTION,C.NODE_ID10,C.NODE_ID10_DEALTIME, ");
        parser.addSQL("       C.SUB_TYPE,C.NODE_ID11,C.NODE_ID11_DEALTIME, ");
        parser.addSQL("       C.NODE_ID12,C.NODE_ID12_DEALTIME, ");
        parser.addSQL("       C.NODE_ID13,C.NODE_ID13_DEALTIME, ");
        parser.addSQL("       C.NODE_ID14,C.NODE_ID14_DEALTIME, ");
        parser.addSQL(" TO_CHAR(C.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID1_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID1_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID2_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID2_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID3_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID3_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID4_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID4_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID5_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID5_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID6_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID6_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID7_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID7_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID8_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID8_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID9_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID9_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID10_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID10_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID11_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID11_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID12_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID12_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID13_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID13_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.NODE_ID14_ARRIVALTIME,'yyyy-mm-dd hh24:mi:ss') NODE_ID14_ARRIVALTIME, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" TO_CHAR(C.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL(" C.RSRV_STR1,C.RSRV_STR2,C.RSRV_STR3,C.RSRV_STR4,C.RSRV_STR5, ");
        parser.addSQL(" TO_CHAR(Q.FINISH_DATE,'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
        parser.addSQL(" C.RSRV_TAG1,C.RSRV_TAG2,C.RSRV_TAG3,W.ATTR_VALUE,W.RECORD_NUM,Q.STATE,Q.ACCEPT_STAFF_ID  ");
        parser.addSQL(" FROM TF_B_EOP_LINEREPORT_LIST C,TF_BH_EOP_ATTR W,TF_BH_EWE Q ");
        parser.addSQL("  WHERE W.ATTR_CODE='BIZSECURITYLV' ");
        parser.addSQL("  AND W.NODE_ID='apply'  ");
        parser.addSQL("  AND W.SUB_IBSYSID = (SELECT MAX(T.SUB_IBSYSID) FROM TF_BH_EOP_ATTR T WHERE T.IBSYSID = C.IBSYSID ");
        parser.addSQL("  AND T.NODE_ID='apply' AND T.ATTR_CODE='BIZSECURITYLV') ");
        parser.addSQL("  AND W.RECORD_NUM <> '0'  ");
        parser.addSQL("  AND W.IBSYSID=C.IBSYSID ");
        parser.addSQL("  AND C.IBSYSID= Q.BI_SN ");
        parser.addSQL("  AND Q.TEMPLET_TYPE= '0' ");
        parser.addSQL(" AND (C.IBSYSID =:IBSYSID or :IBSYSID is null) ");
        if (StringUtils.isNotBlank(subTypeOpen)) {
            parser.addSQL(" AND C.SUB_TYPE =:SUB_TYPE ");
        } else {
            parser.addSQL(" AND C.SUB_TYPE IN ('开通勘察单','变更勘察单') ");
        }
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') >= to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" ) Z Order by Z.IBSYSID ");

        IDataset attrlineInfos = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(attrlineInfos)) {
            for (Object attrIf : attrlineInfos) {
                IData attlineInfo = (IData) attrIf;
                String ibsysId = attlineInfo.getString("IBSYSID");
                String recordNum = attlineInfo.getString("RECORD_NUM");
                String state = attlineInfo.getString("STATE", "");
                String acceptStaffId = attlineInfo.getString("ACCEPT_STAFF_ID", "");
                String dealStaffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", acceptStaffId);
                attlineInfo.put("ACCEPT_STAFF_NAME", dealStaffName);
                if ("4".equals(state)) {
                    attlineInfo.put("STATE_TYPE", "已撤单");
                } else if ("9".equals(state)) {
                    attlineInfo.put("STATE_TYPE", "已完成");
                } else {
                    attlineInfo.put("STATE_TYPE", "未完成");
                }

                String groupId = attlineInfo.getString("GROUP_ID", "");
                IData data = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                if (IDataUtil.isNotEmpty(data)) {
                    String servLevel = data.getString("SERV_LEVEL", "");
                    if (StringUtils.isBlank(servLevel)) {
                        servLevel = "4";
                    }
                    String servicelevel = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[] { "CUSTGROUP_SERVLEVEL", servLevel });
                    attlineInfo.put("SERVICELEVEL", servicelevel);
                }
                IData params1 = new DataMap();
                params1.put("IBSYSID", ibsysId);
                params1.put("RECORD_NUM", recordNum);

                SQLParser parser2 = new SQLParser(params1);
                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.ATTR_CODE ");
                parser2.addSQL(" FROM TF_B_EOP_ATTR C ");
                parser2.addSQL(" WHERE C.ATTR_CODE = 'PRODUCTNO' ");
                parser2.addSQL(" AND C.NODE_ID = 'apply'  ");
                parser2.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
                parser2.addSQL(" AND C.IBSYSID = :IBSYSID ");
                parser2.addSQL(" UNION ALL ");
                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.ATTR_CODE ");
                parser2.addSQL(" FROM TF_BH_EOP_ATTR C ");
                parser2.addSQL(" WHERE C.ATTR_CODE = 'PRODUCTNO' ");
                parser2.addSQL(" AND C.NODE_ID = 'apply'  ");
                parser2.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
                parser2.addSQL(" AND C.IBSYSID = :IBSYSID ");

                IDataset attrData2 = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(attrData2)) {
                    String productNo = attrData2.first().getString("ATTR_VALUE");
                    attlineInfo.put("PRODUCTNO", productNo);
                }

            }

        }

        return attrlineInfos;

    }

    public static IDataset getWorkformNewAttrList400Busi(String ibsysId, String busiType, String cityCode, Pagination pagination) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", ibsysId);
        params.put("BPM_TEMPLET_ID", busiType);
        params.put("CITY_CODE", cityCode);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL(" C.ACCEPT_MONTH, ");
        parser.addSQL(" C.ORDER_ID, ");
        parser.addSQL(" C.BPM_TEMPLET_ID, ");
        parser.addSQL(" C.IN_MODE_CODE, ");
        parser.addSQL(" C.DEAL_STATE, ");
        parser.addSQL(" C.DEAL_RESULT, ");
        parser.addSQL(" C.BUSI_TYPE, ");
        parser.addSQL(" C.BUSI_CODE, ");
        parser.addSQL(" C.GROUP_ID, ");
        parser.addSQL(" C.CUST_NAME, ");
        parser.addSQL("  TO_CHAR(C.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL(" C.EPARCHY_CODE, ");
        parser.addSQL("  TO_CHAR(C.FLOW_EXPECT_TIME, 'yyyy-mm-dd hh24:mi:ss') FLOW_EXPECT_TIME, ");
        parser.addSQL(" C.FLOW_LEVEL, ");
        parser.addSQL(" C.FLOW_DESC, ");
        parser.addSQL(" C.REMARK, ");
        parser.addSQL(" C.RSRV_STR1, ");
        parser.addSQL(" C.RSRV_STR2, ");
        parser.addSQL(" C.RSRV_STR3, ");
        parser.addSQL(" C.RSRV_STR4, ");
        parser.addSQL(" C.RSRV_STR5, ");
        parser.addSQL(" C.RSRV_STR6, ");
        parser.addSQL(" C.RSRV_STR7, ");
        parser.addSQL(" D.SUB_IBSYSID ");
        parser.addSQL(" FROM TF_Bh_EOP_SUBSCRIBE C, ");
        parser.addSQL("      TF_Bh_EOP_NODE D, ");
        parser.addSQL("      TF_Bh_EOP_OTHER E ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.BPM_TEMPLET_ID = :BPM_TEMPLET_ID ");
        parser.addSQL(" AND C.IBSYSID = E.IBSYSID ");
        parser.addSQL(" AND E.NODE_ID = 'infoReview' ");
        parser.addSQL(" AND E.ATTR_CODE = 'AUDIT_RESULT' ");
        parser.addSQL(" AND E.ATTR_VALUE = '1' ");
        parser.addSQL(" AND E.SUB_IBSYSID = D.SUB_IBSYSID ");
        parser.addSQL(" AND (:IBSYSID is null or C.IBSYSID=:IBSYSID) ");
        parser.addSQL(" AND (:CITY_CODE is null or D.CITY_CODE=:CITY_CODE) ");
        parser.addSQL(" ORDER BY C.IBSYSID DESC ");

        IDataset newAttrList = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));

        if (IDataUtil.isNotEmpty(newAttrList)) {
            for (Object object : newAttrList) {
                IData newAttrListInfo = (IData) object;
                String ibsysid1 = newAttrListInfo.getString("IBSYSID");
                IData params1 = new DataMap();
                params1.put("IBSYSID", ibsysid1);

                SQLParser parser1 = new SQLParser(params1);
                parser1.addSQL(" SELECT C.IBSYSID, ");
                parser1.addSQL(" E.SUB_IBSYSID ");
                parser1.addSQL(" FROM TF_BH_EOP_SUBSCRIBE C, ");
                parser1.addSQL("      TF_BH_EOP_OTHER E ");
                parser1.addSQL(" WHERE 1 = 1 ");
                parser1.addSQL(" AND C.IBSYSID like :IBSYSID || '_%' ");
                parser1.addSQL(" AND E.IBSYSID = C.IBSYSID ");
                parser1.addSQL(" AND ( (E.NODE_ID='infoReview' and E.ATTR_CODE='AUDIT_RESULT' and E.ATTR_VALUE='1') or ");
                parser1.addSQL(" (E.NODE_ID='infoApprove' and E.ATTR_CODE='AUDIT_RESULT' and E.ATTR_VALUE='1' and C.BPM_TEMPLET_ID='TIMERREVIEWFOURMANAGE') or ");
                parser1.addSQL(" (E.NODE_ID='infoPrvApprove' and E.ATTR_CODE='AUDIT_RESULT' and E.ATTR_VALUE='1')) ");
                parser1.addSQL(" ORDER BY C.IBSYSID DESC ");
                IDataset reviewWorkformInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(reviewWorkformInfos)) {
                    IData reviewWorkformInfo = reviewWorkformInfos.getData(0);
                    newAttrListInfo.putAll(reviewWorkformInfo);

                }

                IData params2 = new DataMap();
                params2.put("IBSYSID", ibsysid1);
                SQLParser parser2 = new SQLParser(params2);

                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_CODE, ");
                parser2.addSQL(" C.NODE_ID, ");
                parser2.addSQL(" C.ATTR_NAME, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.ATTR_TYPE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.PARENT_ATTR_CODE, ");
                parser2.addSQL(" C.ACCEPT_MONTH ");
                parser2.addSQL(" FROM TF_BH_EOP_ATTR C ");
                parser2.addSQL(" WHERE 1 = 1 ");
                parser2.addSQL(" AND C.IBSYSID =:IBSYSID ");
                parser2.addSQL(" AND C.NODE_ID = 'infoReview' ");
                IDataset eopAttrInfos = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(eopAttrInfos)) {
                    for (Object object2 : eopAttrInfos) {
                        IData eopAttrInfo = (IData) object2;
                        String attrCode = eopAttrInfo.getString("ATTR_CODE");
                        String attrValue = eopAttrInfo.getString("ATTR_VALUE");
                        newAttrListInfo.put(attrCode, attrValue);
                    }
                }

            }

        }

        return newAttrList;

    }

    public static IDataset queryBusiWorkformReviewDetailInfos(String ibsysid, String subIbsysid) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", ibsysid);
        params.put("SUB_IBSYSID", subIbsysid);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL("    E.SUB_IBSYSID ");
        parser.addSQL(" FROM TF_BH_EOP_SUBSCRIBE C, ");
        parser.addSQL("      TF_BH_EOP_OTHER E, ");
        parser.addSQL(" (SELECT T.SUB_IBSYSID, ");
        parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.SUB_IBSYSID DESC) G ");
        parser.addSQL(" FROM TF_BH_EOP_OTHER T ) R ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID like :IBSYSID || '%' ");
        parser.addSQL(" AND E.IBSYSID = C.IBSYSID ");
        parser.addSQL(" AND R.SUB_IBSYSID = E.SUB_IBSYSID ");
        parser.addSQL(" AND R.G <=1 ");
        parser.addSQL(" AND ( (E.NODE_ID='infoReview' and E.ATTR_CODE='AUDIT_RESULT' and E.ATTR_VALUE='1') or ");
        parser.addSQL(" (E.NODE_ID='infoApprove' and E.ATTR_CODE='AUDIT_RESULT' and E.ATTR_VALUE='1' and C.BPM_TEMPLET_ID='TIMERREVIEWFOURMANAGE') or ");
        parser.addSQL(" (E.NODE_ID='infoPrvApprove' and E.ATTR_CODE='AUDIT_RESULT' and E.ATTR_VALUE='1')) ");
        parser.addSQL(" ORDER BY C.IBSYSID DESC ");
        IDataset reviewWorkformInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(reviewWorkformInfos)) {
            for (Object object : reviewWorkformInfos) {
                IData reviewWorkformInfo = (IData) object;
                String ibsysId1 = reviewWorkformInfo.getString("IBSYSID");
                String subIbsysId1 = reviewWorkformInfo.getString("SUB_IBSYSID");

                IData params2 = new DataMap();
                params2.put("IBSYSID", ibsysId1);
                params2.put("SUB_IBSYSID", subIbsysId1);
                SQLParser parser2 = new SQLParser(params2);

                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_CODE, ");
                parser2.addSQL(" C.NODE_ID, ");
                parser2.addSQL(" C.ATTR_NAME, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.ATTR_TYPE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.PARENT_ATTR_CODE, ");
                parser2.addSQL(" C.ACCEPT_MONTH ");
                parser2.addSQL(" FROM TF_BH_EOP_ATTR C ");
                parser2.addSQL(" WHERE 1 = 1 ");
                parser2.addSQL(" AND C.IBSYSID =:IBSYSID ");
                parser2.addSQL(" AND C.SUB_IBSYSID =:SUB_IBSYSID ");
                IDataset eopAttrInfos = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(eopAttrInfos)) {
                    for (Object object2 : eopAttrInfos) {
                        IData eopAttrInfo = (IData) object2;
                        String attrCode = eopAttrInfo.getString("ATTR_CODE");
                        String attrValue = eopAttrInfo.getString("ATTR_VALUE");
                        reviewWorkformInfo.put(attrCode, attrValue);
                    }
                }

            }

        }
        return reviewWorkformInfos;

    }

    public static IDataset queryWorkformDetail(IData map, Pagination pagination) throws Exception {
        IData params = new DataMap();
        params.put("STAFF_ID", map.getString("cond_TRADE_STAFF_ID"));
        params.put("ATTR_VALUE", map.getString("cond_PRODUCT_NO"));
        params.put("BPM_TEMPLET_ID", map.getString("cond_TPTEMPLET_ID"));
        params.put("PRODUCT_ID", map.getString("cond_PRODUCT_ID"));
        params.put("START_DATE", map.getString("COMPLAINT_START_DATE"));
        params.put("END_DATE", map.getString("COMPLAINT_END_DATE"));
        params.put("GROUP_ID", map.getString("cond_CUST_ID"));
        params.put("TITLE", map.getString("cond_TITLE"));
        params.put("IBSYSID", map.getString("cond_SUBSCRIBE_ID"));
        params.put("CITY_CODE", map.getString("cond_CITY_CODE"));
        params.put("STATE", map.getString("cond_BUSI_STATE"));
        // 根据客服姓名查询客户STAFF_ID,如果输入了客户姓名查询
        if (StringUtils.isBlank(map.getString("cond_TRADE_STAFF_ID")) && StringUtils.isNotBlank(map.getString("cond_TRADE_STAFF_NAME"))) {
            String staffName = map.getString("cond_TRADE_STAFF_NAME");
            String staffId = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_NAME", "STAFF_ID", staffName);
            if (StringUtils.isBlank(staffId)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据客户经理姓名【" + staffName + "】未获取到任何信息，请确认该客户经理是否存在！");
            } else {
                params.put("STAFF_ID", staffId);
            }
        }
        // 稽核状态
        params.put("OTHER_ATTR_VALUE", map.getString("cond_DEAL_TAG"));
        IDataset myworkformList = new DatasetList();

        if ("0".equals(map.getString("cond_BUSI_STATE"))) {
            SQLParser parser = new SQLParser(params);
            parser.addSQL(" SELECT C.IBSYSID,C.ACCEPT_MONTH,C.ORDER_ID,C.BPM_TEMPLET_ID,C.IN_MODE_CODE, ");
            parser.addSQL("        C.DEAL_STATE,C.DEAL_RESULT,C.BUSI_TYPE,C.BUSI_CODE,C.GROUP_ID, ");
            parser.addSQL("        C.EPARCHY_CODE,C.FLOW_LEVEL,C.FLOW_DESC,C.REMARK,C.RSRV_STR1, ");
            parser.addSQL("        C.RSRV_STR2,C.RSRV_STR3,C.RSRV_STR4,C.RSRV_STR5,C.RSRV_STR6,C.RSRV_STR7, ");
            parser.addSQL("        C.CUST_NAME,E.ACCEPT_STAFF_ID,E.STATE,E.CITY_CODE,F.PRODUCT_NAME, ");
            parser.addSQL("        F.PRODUCT_ID,T.BUSIFORM_ID,T.NODE_ID,'false' IS_FINISH,T.DEAL_STAFF_ID, ");
            parser.addSQL("        T.UPDATE_STAFF_ID,F.SERIAL_NUMBER, ");
            parser.addSQL("        TO_CHAR(C.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
            parser.addSQL("  TO_CHAR(C.FLOW_EXPECT_TIME, 'yyyy-mm-dd hh24:mi:ss') FLOW_EXPECT_TIME ");
            if (StringUtils.isNotBlank(map.getString("cond_DEAL_TAG"))) {
                parser.addSQL("    ,U.ATTR_CODE,U.ATTR_VALUE ");
            }
            parser.addSQL("  FROM TF_B_EOP_SUBSCRIBE C,   ");
            parser.addSQL("  (SELECT T.BUSIFORM_ID,T.NODE_ID,T.UPDATE_DATE,T.DEAL_STAFF_ID,T.UPDATE_STAFF_ID, ");
            parser.addSQL("  ROW_NUMBER() OVER(PARTITION BY T.BUSIFORM_ID ORDER BY T.UPDATE_DATE DESC) G ");
            parser.addSQL("  FROM TF_B_EWE_NODE T) T, ");
            parser.addSQL("  TF_B_EWE E,  ");
            parser.addSQL("  TF_B_EOP_PRODUCT F ");
            if (StringUtils.isNotBlank(map.getString("cond_PRODUCT_NO"))) {
                parser.addSQL("    ,( SELECT T.ATTR_CODE,T.ATTR_VALUE,T.IBSYSID, ");
                parser.addSQL("      ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.IBSYSID DESC) Q ");
                parser.addSQL("     FROM TF_B_EOP_ATTR T WHERE  T.ATTR_CODE='PRODUCTNO' AND T.ATTR_VALUE=:ATTR_VALUE ) R ");
            }
            if (StringUtils.isNotBlank(map.getString("cond_DEAL_TAG"))) {
                parser.addSQL("    ,( SELECT T.IBSYSID,T.ATTR_CODE,T.ATTR_VALUE, ");
                parser.addSQL("      ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.UPDATE_TIME DESC) Y ");
                parser.addSQL("     FROM TF_B_EOP_OTHER T  WHERE  T.ATTR_CODE='AUDITESOPINFO' AND T.ATTR_VALUE=:OTHER_ATTR_VALUE) U ");
            }
            parser.addSQL("  WHERE E.BUSIFORM_ID = T.BUSIFORM_ID  ");
            parser.addSQL("  AND C.IBSYSID = E.BI_SN ");
            parser.addSQL("  AND F.RECORD_NUM = '0' ");
            parser.addSQL("  AND C.IBSYSID = F.IBSYSID ");
            parser.addSQL("  AND (:STAFF_ID is null or E.ACCEPT_STAFF_ID=:STAFF_ID) ");
            parser.addSQL("  AND E.TEMPLET_TYPE = '0' ");
            parser.addSQL("  AND T.G <= 1 ");
            parser.addSQL("  AND (:IBSYSID is null or C.IBSYSID=:IBSYSID) ");
            parser.addSQL("  AND (:START_DATE is null or  E.CREATE_DATE >= to_date(:START_DATE, 'yyyy-MM-dd HH24:mi:ss')) ");
            parser.addSQL("  AND (:END_DATE is null or  E.CREATE_DATE <= to_date(:END_DATE, 'yyyy-MM-dd HH24:mi:ss')) ");
            parser.addSQL("  AND (:PRODUCT_ID is null or F.PRODUCT_ID=:PRODUCT_ID) ");
            parser.addSQL("  AND (:GROUP_ID is null or C.GROUP_ID=:GROUP_ID) ");
            parser.addSQL("  AND (:BPM_TEMPLET_ID is null or E.BPM_TEMPLET_ID=:BPM_TEMPLET_ID) ");
            parser.addSQL("  AND (:CITY_CODE is null or E.CITY_CODE=:CITY_CODE) ");
            parser.addSQL("  AND (:STATE is null or E.STATE=:STATE) ");
            parser.addSQL("  AND (:TITLE is null or C.RSRV_STR4 like '%'|| :TITLE ||'%') ");
            if (StringUtils.isNotBlank(map.getString("cond_PRODUCT_NO"))) {
                parser.addSQL("  AND R.IBSYSID = C.IBSYSID ");
                parser.addSQL("  AND R.Q<=1 ");
            }
            if (StringUtils.isNotBlank(map.getString("cond_DEAL_TAG"))) {
                parser.addSQL("  AND U.IBSYSID = C.IBSYSID ");
                parser.addSQL("  AND U.Y<=1 ");
            }
            parser.addSQL("  ORDER BY C.ACCEPT_TIME DESC ");
            myworkformList = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));

        } else {
            SQLParser parser = new SQLParser(params);
            parser.addSQL(" SELECT C.IBSYSID,C.ACCEPT_MONTH,C.ORDER_ID,C.BPM_TEMPLET_ID,C.IN_MODE_CODE, ");
            parser.addSQL("        C.DEAL_STATE,C.DEAL_RESULT,C.BUSI_TYPE,C.BUSI_CODE,C.GROUP_ID, ");
            parser.addSQL("        C.EPARCHY_CODE,C.FLOW_LEVEL,C.FLOW_DESC,C.REMARK,C.RSRV_STR1, ");
            parser.addSQL("        C.RSRV_STR2,C.RSRV_STR3,C.RSRV_STR4,C.RSRV_STR5,C.RSRV_STR6,C.RSRV_STR7, ");
            parser.addSQL("        C.CUST_NAME,E.ACCEPT_STAFF_ID,E.STATE,E.CITY_CODE,F.PRODUCT_NAME, ");
            parser.addSQL("        F.PRODUCT_ID,T.BUSIFORM_ID,T.NODE_ID,'true' IS_FINISH,T.DEAL_STAFF_ID, ");
            parser.addSQL("        T.UPDATE_STAFF_ID,F.SERIAL_NUMBER, ");
            parser.addSQL("        TO_CHAR(C.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
            parser.addSQL("  TO_CHAR(C.FLOW_EXPECT_TIME, 'yyyy-mm-dd hh24:mi:ss') FLOW_EXPECT_TIME ");
            if (StringUtils.isNotBlank(map.getString("cond_DEAL_TAG"))) {
                parser.addSQL("    ,U.ATTR_CODE,U.ATTR_VALUE ");
            }
            parser.addSQL("  FROM TF_BH_EOP_SUBSCRIBE C,   ");
            parser.addSQL("  (SELECT T.BUSIFORM_ID,T.NODE_ID,T.UPDATE_DATE,T.DEAL_STAFF_ID,T.UPDATE_STAFF_ID, ");
            parser.addSQL("  ROW_NUMBER() OVER(PARTITION BY T.BUSIFORM_ID ORDER BY T.UPDATE_DATE DESC) G ");
            parser.addSQL("  FROM TF_BH_EWE_NODE T) T, ");
            parser.addSQL("  TF_BH_EWE E,  ");
            parser.addSQL("  TF_BH_EOP_PRODUCT F ");
            if (StringUtils.isNotBlank(map.getString("cond_PRODUCT_NO"))) {
                parser.addSQL("    ,( SELECT T.ATTR_CODE,T.ATTR_VALUE,T.IBSYSID, ");
                parser.addSQL("      ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.IBSYSID DESC) Q ");
                parser.addSQL("     FROM TF_BH_EOP_ATTR T WHERE  T.ATTR_CODE='PRODUCTNO' AND T.ATTR_VALUE=:ATTR_VALUE ) R ");
            }
            if (StringUtils.isNotBlank(map.getString("cond_DEAL_TAG"))) {
                parser.addSQL("    ,( SELECT T.IBSYSID,T.ATTR_CODE,T.ATTR_VALUE, ");
                parser.addSQL("      ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.UPDATE_TIME DESC) Y ");
                parser.addSQL("     FROM TF_BH_EOP_OTHER T  WHERE  T.ATTR_CODE='AUDITESOPINFO' AND T.ATTR_VALUE=:OTHER_ATTR_VALUE) U ");
            }
            parser.addSQL("  WHERE E.BUSIFORM_ID = T.BUSIFORM_ID  ");
            parser.addSQL("  AND C.IBSYSID = E.BI_SN ");
            parser.addSQL("  AND F.RECORD_NUM = '0' ");
            parser.addSQL("  AND C.IBSYSID = F.IBSYSID ");
            parser.addSQL("  AND (:STAFF_ID is null or E.ACCEPT_STAFF_ID=:STAFF_ID) ");
            parser.addSQL("  AND E.TEMPLET_TYPE = '0' ");
            parser.addSQL("  AND T.G <= 1 ");
            parser.addSQL("  AND (:IBSYSID is null or C.IBSYSID=:IBSYSID) ");
            parser.addSQL("  AND (:START_DATE is null or  E.CREATE_DATE >= to_date(:START_DATE, 'yyyy-MM-dd HH24:mi:ss')) ");
            parser.addSQL("  AND (:END_DATE is null or  E.CREATE_DATE <= to_date(:END_DATE, 'yyyy-MM-dd HH24:mi:ss')) ");
            parser.addSQL("  AND (:PRODUCT_ID is null or F.PRODUCT_ID=:PRODUCT_ID) ");
            parser.addSQL("  AND (:GROUP_ID is null or C.GROUP_ID=:GROUP_ID) ");
            parser.addSQL("  AND (:BPM_TEMPLET_ID is null or E.BPM_TEMPLET_ID=:BPM_TEMPLET_ID) ");
            parser.addSQL("  AND (:CITY_CODE is null or E.CITY_CODE=:CITY_CODE) ");
            parser.addSQL("  AND (:STATE is null or E.STATE=:STATE) ");
            parser.addSQL("  AND (:TITLE is null or C.RSRV_STR4 like '%'|| :TITLE ||'%') ");
            if (StringUtils.isNotBlank(map.getString("cond_PRODUCT_NO"))) {
                parser.addSQL("  AND R.IBSYSID = C.IBSYSID ");
                parser.addSQL("  AND R.Q<=1 ");
            }
            if (StringUtils.isNotBlank(map.getString("cond_DEAL_TAG"))) {
                parser.addSQL("  AND U.IBSYSID = C.IBSYSID ");
                parser.addSQL("  AND U.Y<=1 ");
            }
            parser.addSQL("  ORDER BY C.ACCEPT_TIME DESC ");
            myworkformList = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        }
        getyMyworkform(myworkformList);// 拼加参数

        return myworkformList;

    }

    public static IDataset getyMyworkform(IDataset myworkformList) throws Exception {
        if (IDataUtil.isNotEmpty(myworkformList)) {
            for (Object object : myworkformList) {
                IData myworkformInfo = (IData) object;
                String bpmTempletId = myworkformInfo.getString("BPM_TEMPLET_ID");
                String nodeId = myworkformInfo.getString("NODE_ID");
                String isFinish = myworkformInfo.getString("IS_FINISH");
                String staffId = myworkformInfo.getString("ACCEPT_STAFF_ID");
                String dealStaffId = myworkformInfo.getString("DEAL_STAFF_ID");
                String updateStaffId = myworkformInfo.getString("UPDATE_STAFF_ID");
                String state = myworkformInfo.getString("STATE");
                String otherIbsysId = myworkformInfo.getString("IBSYSID");
                // 获取流程和节点名
                IData params2 = new DataMap();
                params2.put("BPM_TEMPLET_ID", bpmTempletId);
                params2.put("NODE_ID", nodeId);
                SQLParser parser2 = new SQLParser(params2);
                parser2.addSQL(" SELECT C.TEMPLET_NAME, ");
                parser2.addSQL(" D.NODE_NAME ");
                parser2.addSQL(" FROM TD_B_EWE_FLOW_TEMPLET C, ");
                parser2.addSQL("      TD_B_EWE_NODE_TEMPLET D ");
                parser2.addSQL(" WHERE 1 = 1 ");
                parser2.addSQL(" AND C.BPM_TEMPLET_ID = D.BPM_TEMPLET_ID ");
                parser2.addSQL(" AND C.BPM_TEMPLET_ID = :BPM_TEMPLET_ID ");
                parser2.addSQL(" AND D.NODE_ID = :NODE_ID ");

                IDataset nedeTempletInfo = Dao.qryByParse(parser2, Route.CONN_CRM_CEN);
                if (IDataUtil.isNotEmpty(nedeTempletInfo)) {
                    myworkformInfo.put("NODE_NAME", nedeTempletInfo.getData(0).getString("NODE_NAME"));
                    myworkformInfo.put("TEMPLET_NAME", nedeTempletInfo.getData(0).getString("TEMPLET_NAME"));

                }
                // 获取员工姓名
                String dealStaffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", dealStaffId);
                String staffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
                String updatestaffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", updateStaffId);

                if (StringUtils.isNotBlank(staffName)) {
                    myworkformInfo.put("ACCEPT_STAFF_NAME", staffName);
                }
                if (StringUtils.isNotBlank(dealStaffName)) {
                    myworkformInfo.put("DEAL_STAFF_NAME", dealStaffName);
                }
                // 自动节点未入当前处理人，取系统更新工号
                if (StringUtils.isBlank(dealStaffId)) {
                    myworkformInfo.put("DEAL_STAFF_NAME", updatestaffName);
                    myworkformInfo.put("DEAL_STAFF_ID", updateStaffId);
                }
                // 获取稽核状态,已稽核的会直接进历史表
                IData params3 = new DataMap();
                params3.put("IBSYSID", otherIbsysId);
                SQLParser parser3 = new SQLParser(params3);
                parser3.addSQL(" SELECT ATTR_VALUE FROM ");
                parser3.addSQL(" ( SELECT T.IBSYSID,T.ATTR_CODE,T.ATTR_VALUE, ");
                parser3.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.UPDATE_TIME DESC) G  ");
                parser3.addSQL(" FROM TF_BH_EOP_OTHER T WHERE T.IBSYSID =:IBSYSID AND T.ATTR_CODE ='AUDITESOPINFO') R WHERE R.G<=1");
                IDataset otherInfos = Dao.qryByParse(parser3, Route.getJourDb(Route.CONN_CRM_CG));
                String otherAttrValue = "";
                if (IDataUtil.isEmpty(otherInfos)) {
                    otherAttrValue = "0";
                } else {
                    otherAttrValue = otherInfos.getData(0).getString("ATTR_VALUE");
                }
                // 转换稽核状态
                if ("true".equals(isFinish)
                        && ("DIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId)
                                || "EVIOPDIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId))) {
                    myworkformInfo.put("TEMPLET_STATE", "已完成");
                    myworkformInfo.put("CREATE_STATE", "已开通");
                    if ("0".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "未稽核");
                    } else if ("1".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "已稽核");
                    } else if ("2".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "一次整改");
                    } else if ("3".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "一次整改稽核");
                    } else if ("4".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "二次整改");
                    } else if ("5".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "二次整改稽核");
                    }
                } else if ("false".equals(isFinish)
                        && ("DIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId)
                                || "EVIOPDIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId))) {
                    myworkformInfo.put("TEMPLET_STATE", "未完成");
                    myworkformInfo.put("CREATE_STATE", "未开通");
                    if ("0".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "未稽核");
                    } else if ("1".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "已稽核");
                    } else if ("2".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "一次整改");
                    } else if ("3".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "一次整改稽核");
                    } else if ("4".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "二次整改");
                    } else if ("5".equals(otherAttrValue)) {
                        myworkformInfo.put("AUDIT_STATE", "二次整改稽核");
                    }
                } else if ("false".equals(isFinish)
                        && !("DIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId)
                                || "EVIOPDIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId))) {
                    myworkformInfo.put("TEMPLET_STATE", "未完成");
                    myworkformInfo.put("AUDIT_STATE", "无需稽核");
                    myworkformInfo.put("CREATE_STATE", "未开通");
                } else if ("true".equals(isFinish)
                        && !("DIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId)
                                || "EVIOPDIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId))) {
                    myworkformInfo.put("TEMPLET_STATE", "已完成");
                    myworkformInfo.put("AUDIT_STATE", "无需稽核");
                    myworkformInfo.put("CREATE_STATE", "已开通");
                }
                String datalineRemove = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "DATELINE_REMOVE", bpmTempletId });

                // 必须是专线流程并且登陆工号才能撤单权限
                String bizstaffId = CSBizBean.getVisit().getStaffId();
                if (StringUtils.isNotBlank(datalineRemove) && staffId.equals(bizstaffId) && "false".equals(isFinish)) {
                    myworkformInfo.put("DATELINE_REMOVE", "撤单");
                    myworkformInfo.put("DATELINE_REMOVE_A", "");
                    if ("4".equals(state)) {
                        myworkformInfo.put("PORT_A_CUST", "已撤单");
                        myworkformInfo.put("DATELINE_REMOVE", "");
                        myworkformInfo.put("DATELINE_REMOVE_A", "0");
                    } else {
                        myworkformInfo.put("PORT_A_CUST", "未撤单");
                    }
                } else {
                    if ("4".equals(state)) {
                        myworkformInfo.put("PORT_A_CUST", "已撤单");
                    } else if ("G".equals(state)) {
                        myworkformInfo.put("PORT_A_CUST", "割接已废弃");
                    } else {
                        myworkformInfo.put("PORT_A_CUST", "未撤单");
                    }
                    myworkformInfo.put("DATELINE_REMOVE", "");
                    myworkformInfo.put("DATELINE_REMOVE_A", "0");
                }
                // 综合资管流程图
                String zhzgUrl = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "SEL_BY_URL", "ZHZG_MAP" });
                IDataset emosInfos = new DatasetList();
                if (StringUtils.isNotBlank(zhzgUrl) && "false".equals(isFinish)) {
                    SQLParser parser4 = new SQLParser(params3);
                    parser4.addSQL(" SELECT SERIALNO FROM ");
                    parser4.addSQL(" ( SELECT T.SERIALNO, ");
                    parser4.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.SERIALNO ORDER BY T.UPDATE_TIME DESC) G  ");
                    parser4.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID) R WHERE R.G<=1 ");
                    emosInfos = Dao.qryByParse(parser4, Route.getJourDb(Route.CONN_CRM_CG));
                } else if (StringUtils.isNotBlank(zhzgUrl) && "true".equals(isFinish)) {
                    SQLParser parser8 = new SQLParser(params3);
                    parser8.addSQL(" SELECT SERIALNO FROM ");
                    parser8.addSQL(" ( SELECT T.SERIALNO, ");
                    parser8.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.SERIALNO ORDER BY T.UPDATE_TIME DESC) G  ");
                    parser8.addSQL(" FROM TF_BH_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID) R WHERE R.G<=1 ");
                    emosInfos = Dao.qryByParse(parser8, Route.getJourDb(Route.CONN_CRM_CG));
                }
                if (IDataUtil.isNotEmpty(emosInfos)) {
                    myworkformInfo.put("ESOP_ID", emosInfos.getData(0).getString("SERIALNO"));
                    myworkformInfo.put("ESOP_ID_A", "查看资管流程状态");
                    myworkformInfo.put("ZHZG_URL", zhzgUrl + emosInfos.getData(0).getString("SERIALNO"));
                } else {
                    myworkformInfo.put("ESOP_ID", "0");
                    myworkformInfo.put("ESOP_ID_A", "");
                }
                // 查询已关联的开通单
                SQLParser parser5 = new SQLParser(params3);
                parser5.addSQL(" SELECT  G.BI_SN ");
                parser5.addSQL(" FROM TF_B_EOP_SUBSCRIBE F, TF_B_EWE G ");
                parser5.addSQL(" WHERE F.IBSYSID = G.BI_SN ");
                parser5.addSQL(" AND F.RSRV_STR5 =:IBSYSID ");
                parser5.addSQL(" AND G.TEMPLET_TYPE='0' ");
                parser5.addSQL(" UNION ALL ");
                parser5.addSQL(" SELECT G.BI_SN ");
                parser5.addSQL(" FROM TF_BH_EOP_SUBSCRIBE F, TF_BH_EWE G ");
                parser5.addSQL(" WHERE F.IBSYSID = G.BI_SN ");
                parser5.addSQL(" AND F.RSRV_STR5 =:IBSYSID ");
                parser5.addSQL(" AND G.TEMPLET_TYPE='0' ");
                IDataset ktibsysidInfos = Dao.qryByParse(parser5, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(ktibsysidInfos)) {
                    myworkformInfo.put("KTIBSYSID", ktibsysidInfos.getData(0).getString("BI_SN"));
                }

                // 查询是否已发综资，展示专线详情查看
                SQLParser parser6 = new SQLParser(params3);
                parser6.addSQL(" SELECT C.RECORD_NUM, ");
                parser6.addSQL("        C.PRODUCT_NO, ");
                parser6.addSQL("        C.IBSYSID, ");
                parser6.addSQL("        C.BUSI_STATE ");
                parser6.addSQL("  FROM TF_B_EOP_EOMS_STATE C ");
                parser6.addSQL("  WHERE C.IBSYSID = :IBSYSID  ");
                parser6.addSQL("  UNION ALL ");
                parser6.addSQL(" SELECT C.RECORD_NUM, ");
                parser6.addSQL("        C.PRODUCT_NO, ");
                parser6.addSQL("        C.IBSYSID, ");
                parser6.addSQL("        C.BUSI_STATE ");
                parser6.addSQL("  FROM TF_BH_EOP_EOMS_STATE C ");
                parser6.addSQL("  WHERE C.IBSYSID = :IBSYSID ");

                IDataset recordNumInfos = Dao.qryByParse(parser6, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(recordNumInfos)) {
                    myworkformInfo.put("RECORDNUM_INFOS", "1");
                    myworkformInfo.put("RECORDNUM_INFOS_A", "查看");
                    myworkformInfo.put("DEAL_STAFF_NAME", "");
                    myworkformInfo.put("DEAL_STAFF_ID", "");
                } else {
                    myworkformInfo.put("RECORDNUM_INFOS", "0");
                    myworkformInfo.put("RECORDNUM_INFOS_A", "");
                }
                // 工单是否存在异常,只展示未完工的异常信息
                if ("false".equals(isFinish)) {
                    IData params7 = new DataMap();
                    params7.put("IBSYSID", otherIbsysId);
                    params7.put("STATE", "M");

                    SQLParser parser7 = new SQLParser(params7);
                    parser7.addSQL(" SELECT C.BUSIFORM_ID, ");
                    parser7.addSQL("        C.BUSIFORM_NODE_ID, ");
                    parser7.addSQL("        C.STATE ");
                    parser7.addSQL("  FROM TF_B_EWE_STEP C, ");
                    parser7.addSQL("       TF_B_EWE E ");
                    parser7.addSQL("  WHERE E.BI_SN = :IBSYSID ");
                    parser7.addSQL("  AND E.BUSIFORM_ID = C.BUSIFORM_ID ");
                    parser7.addSQL("  AND C.STATE = :STATE ");
                    IDataset stepInfos = Dao.qryByParse(parser7, Route.getJourDb(Route.CONN_CRM_CG));
                    if (IDataUtil.isNotEmpty(stepInfos)) {
                        myworkformInfo.put("STEP_INFOS", "1");
                        myworkformInfo.put("STEP_INFOS_A", "查看异常信息");
                    } else {
                        myworkformInfo.put("STEP_INFOS", "0");
                        myworkformInfo.put("STEP_INFOS_A", "");
                    }
                }
            }
        }
        return myworkformList;

    }

    public static IDataset queryDatelineAttr(IData map) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.RECORD_NUM, ");
        parser.addSQL("        C.PRODUCT_NO, ");
        parser.addSQL("        C.IBSYSID, ");
        parser.addSQL("        C.BUSI_STATE ");
        parser.addSQL("  FROM TF_B_EOP_EOMS_STATE C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND C.IBSYSID = :IBSYSID ");
        parser.addSQL("  UNION ALL ");
        parser.addSQL(" SELECT C.RECORD_NUM, ");
        parser.addSQL("        C.PRODUCT_NO, ");
        parser.addSQL("        C.IBSYSID, ");
        parser.addSQL("        C.BUSI_STATE ");
        parser.addSQL("  FROM TF_BH_EOP_EOMS_STATE C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND C.IBSYSID = :IBSYSID ");

        IDataset recordNumInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

        int datelineSum = recordNumInfos.size();// 专线总数
        int datelineCancel = 0;// 撤单数
        int datalineHang = 0;// 挂起数
        int datalinecheckin = 0;// 完工数

        for (Object object : recordNumInfos) {
            IData recordNuminfo = (IData) object;
            String recordNum = recordNuminfo.getString("RECORD_NUM");
            String busiType = recordNuminfo.getString("BUSI_STATE");
            // 取专线条数
            if ("-1".equals(busiType)) {
                busiType = "A";
            }
            if ("9".equals(busiType)) {
                datalinecheckin++;
            }
            if ("Q".equals(busiType) || "F".equals(busiType)|| "W".equals(busiType)) {
                datalineHang++;
            }
            if ("C".equals(busiType)) {
                datelineCancel++;
            }

            String busiTypeDesc = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMVALUE" }, "VALUEDESC", new String[] { "EOMS_BUSI_STATE", busiType });
            recordNuminfo.put("BUSI_STATE", busiTypeDesc);

            // 获取最新的GROUP_SEQ
            IData params3 = new DataMap();
            params3.put("IBSYSID", map.getString("IBSYSID"));
            params3.put("RECORD_NUM", recordNum);

            // 获取最新的GROUP_SEQ
            SQLParser parser3 = new SQLParser(params3);
            parser3.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID  FROM ");
            parser3.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID, ");
            parser3.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
            parser3.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'renewWorkSheet') R ");
            parser3.addSQL(" WHERE 1=1 ");
            parser3.addSQL(" AND R.G<=1 ");
            parser3.addSQL("  UNION ALL ");
            parser3.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID  FROM ");
            parser3.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID, ");
            parser3.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
            parser3.addSQL(" FROM TF_BH_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'renewWorkSheet') R ");
            parser3.addSQL(" WHERE 1=1 ");
            parser3.addSQL(" AND R.G<=1 ");
            IDataset eomsInfos = Dao.qryByParse(parser3, Route.getJourDb(Route.CONN_CRM_CG));
            String groupSeq = "";
            if (IDataUtil.isNotEmpty(eomsInfos)) {
                groupSeq = eomsInfos.getData(0).getString("GROUP_SEQ");
            } else {
                SQLParser parser5 = new SQLParser(params3);
                parser5.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID  FROM ");
                parser5.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID, ");
                parser5.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
                parser5.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'newWorkSheet') R ");
                parser5.addSQL(" WHERE 1=1 ");
                parser5.addSQL(" AND R.G<=1 ");
                parser5.addSQL("  UNION ALL ");
                parser5.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID  FROM ");
                parser5.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID, ");
                parser5.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
                parser5.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'newWorkSheet') R ");
                parser5.addSQL(" WHERE 1=1 ");
                parser5.addSQL(" AND R.G<=1 ");
                IDataset eomsInfosss = Dao.qryByParse(parser5, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(eomsInfosss)) {
                    groupSeq = eomsInfosss.getData(0).getString("GROUP_SEQ");
                }
            }

            // 获取资管撤单失败原因
            SQLParser parser4 = new SQLParser(params3);

            parser4.addSQL(" SELECT E.ATTR_CODE,E.ATTR_VALUE FROM ");
            parser4.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM, ");
            parser4.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
            parser4.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM AND T.OPER_TYPE ='agreeCancelWorkSheet') R, ");
            parser4.addSQL(" TF_B_EOP_ATTR E ");
            parser4.addSQL(" WHERE E.IBSYSID = R.IBSYSID ");
            parser4.addSQL(" AND R.GROUP_SEQ =E.GROUP_SEQ ");
            parser4.addSQL(" AND R.RECORD_NUM =E.RECORD_NUM ");
            parser4.addSQL(" AND R.G<=1 ");
            parser4.addSQL("  UNION ALL ");
            parser4.addSQL(" SELECT E.ATTR_CODE,E.ATTR_VALUE FROM ");
            parser4.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM, ");
            parser4.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
            parser4.addSQL(" FROM TF_BH_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM AND T.OPER_TYPE ='agreeCancelWorkSheet') R, ");
            parser4.addSQL(" TF_BH_EOP_ATTR E ");
            parser4.addSQL(" WHERE E.IBSYSID = R.IBSYSID ");
            parser4.addSQL(" AND R.GROUP_SEQ =E.GROUP_SEQ ");
            parser4.addSQL(" AND R.RECORD_NUM =E.RECORD_NUM ");
            parser4.addSQL(" AND R.G<=1 ");

            IDataset eomsAttrInfos = Dao.qryByParse(parser4, Route.getJourDb(Route.CONN_CRM_CG));
            if (IDataUtil.isNotEmpty(eomsAttrInfos)) {
                for (Object object2 : eomsAttrInfos) {
                    IData eomsAttrInfo = (IData) object2;
                    if ("agreeContent".equals(eomsAttrInfo.getString("ATTR_CODE"))) {
                        if (StringUtils.isBlank(eomsAttrInfo.getString("ATTR_VALUE"))) {
                            recordNuminfo.put("AGREE_CONTENT", "");
                        } else {
                            recordNuminfo.put("AGREE_CONTENT", eomsAttrInfo.getString("ATTR_VALUE"));
                        }
                    }
                    if ("agreeResult".equals(eomsAttrInfo.getString("ATTR_CODE"))) {
                        if ("0".equals(eomsAttrInfo.getString("ATTR_VALUE"))) {
                            recordNuminfo.put("DEAL_TYPE", "撤单失败");
                        } else if ("1".equals(eomsAttrInfo.getString("ATTR_VALUE"))) {
                            recordNuminfo.put("DEAL_TYPE", "已撤单");
                        } else if ("2".equals(eomsAttrInfo.getString("ATTR_VALUE"))) {
                            recordNuminfo.put("DEAL_TYPE", "撤单中");
                        }
                    }

                }
            } else if ("C".equals(busiType)) {
                recordNuminfo.put("DEAL_TYPE", "已撤单");
            } else {
                recordNuminfo.put("DEAL_TYPE", "未发起撤单");

            }

            // 获取地址
            IData params2 = new DataMap();
            params2.put("IBSYSID", map.getString("IBSYSID"));
            params2.put("RECORD_NUM", recordNum);
            if (IDataUtil.isNotEmpty(eomsInfos)) {
                params2.put("GROUP_SEQ", groupSeq);
            }

            SQLParser parser2 = new SQLParser(params2);
            parser2.addSQL(" SELECT C.ATTR_CODE,  ");
            parser2.addSQL("        C.ATTR_VALUE, ");
            parser2.addSQL("        D.PRODUCT_NAME ");
            parser2.addSQL("  FROM TF_B_EOP_ATTR C, ");
            parser2.addSQL("       TF_B_EOP_PRODUCT D, ");
            parser2.addSQL("       TF_B_EWE E ");
            parser2.addSQL("  WHERE 1=1 ");
            parser2.addSQL("  AND C.IBSYSID = :IBSYSID ");
            parser2.addSQL("  AND C.GROUP_SEQ = :GROUP_SEQ ");
            parser2.addSQL("  AND C.RECORD_NUM  in ('0',:RECORD_NUM) ");
            parser2.addSQL("  AND E.BI_SN = C.IBSYSID ");
            parser2.addSQL("  AND D.IBSYSID = C.IBSYSID ");
            parser2.addSQL("  AND E.BUSI_CODE = D.PRODUCT_ID ");
            parser2.addSQL("  AND E.TEMPLET_TYPE = '0' ");
            parser2.addSQL("  UNION ALL ");
            parser2.addSQL(" SELECT C.ATTR_CODE,  ");
            parser2.addSQL("        C.ATTR_VALUE, ");
            parser2.addSQL("        D.PRODUCT_NAME ");
            parser2.addSQL("  FROM TF_BH_EOP_ATTR C, ");
            parser2.addSQL("       TF_BH_EOP_PRODUCT D, ");
            parser2.addSQL("       TF_BH_EWE E ");
            parser2.addSQL("  WHERE 1=1 ");
            parser2.addSQL("  AND C.IBSYSID = :IBSYSID ");
            parser2.addSQL("  AND C.GROUP_SEQ = :GROUP_SEQ ");
            parser2.addSQL("  AND C.RECORD_NUM  in ('0',:RECORD_NUM) ");
            parser2.addSQL("  AND E.BI_SN = C.IBSYSID ");
            parser2.addSQL("  AND D.IBSYSID = C.IBSYSID ");
            parser2.addSQL("  AND E.BUSI_CODE = D.PRODUCT_ID ");
            parser2.addSQL("  AND E.TEMPLET_TYPE = '0' ");
            IDataset datalineAttrinfos = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
            String provincea = "";
            String provincez = "";
            String citya = "";
            String cityz = "";
            String areaa = "";
            String areaz = "";
            String countya = "";
            String countyz = "";
            String villagea = "";
            String villagez = "";

            for (Object object2 : datalineAttrinfos) {
                IData datalineAttrinfo = (IData) object2;
                String productNmae = datalineAttrinfo.getString("PRODUCT_NAME", "");
                recordNuminfo.put("PRODUCTNAME", productNmae);
                if ("TRADEID".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("PRODUCT_NO", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("CHANGEMODE".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("CHANGEMODE", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("BIZRANGE".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("BIZRANGE", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("PROVINCEA".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    provincea = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("CITYA".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    citya = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("AREAA".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    areaa = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("COUNTYA".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    countya = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("VILLAGEA".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    villagea = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("PROVINCEZ".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    provincez = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("CITYZ".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    cityz = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("AREAZ".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    areaz = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("COUNTYZ".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    countyz = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("VILLAGEZ".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    villagez = datalineAttrinfo.getString("ATTR_VALUE");
                }
                if ("BANDWIDTH".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("BANDWIDTH", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("TRADENAME".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("TRADENAME", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("BIZSECURITYLV".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("BIZSECURITYLV", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("PORTACUSTOM".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("PORTACUSTOM", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("PORTACONTACT".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("PORTACONTACT", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("PORTACONTACTPHONE".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("PORTACONTACTPHONE", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("PORTZCUSTOM".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("PORTZCUSTOM", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("PORTZCONTACT".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("PORTZCONTACT", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("PORTZCONTACTPHONE".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("PORTZCONTACTPHONE", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("TRANSFERMODE".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("TRANSFERMODE", datalineAttrinfo.getString("ATTR_VALUE"));
                }
                if ("ROUTEMODE".equals(datalineAttrinfo.getString("ATTR_CODE"))) {
                    recordNuminfo.put("ROUTEMODE", datalineAttrinfo.getString("ATTR_VALUE"));
                }

            }

            if (StringUtils.isNotBlank(provincea) && StringUtils.isNotBlank(citya) && StringUtils.isNotBlank(areaa) && StringUtils.isNotBlank(countya) && StringUtils.isNotBlank(villagea)) {
                recordNuminfo.put("ADDERA", provincea + citya + areaa + countya + villagea);

            }
            if (StringUtils.isNotBlank(provincez) && StringUtils.isNotBlank(cityz) && StringUtils.isNotBlank(areaz) && StringUtils.isNotBlank(countyz) && StringUtils.isNotBlank(villagez)) {
                recordNuminfo.put("ADDERZ", provincez + cityz + areaz + countyz + villagez);
            }

            // 专线详情获取撤单原因
            SQLParser parser8 = new SQLParser(params3);
            parser8.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID  FROM ");
            parser8.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID, ");
            parser8.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
            parser8.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'cancelWorkSheet') R ");
            parser8.addSQL(" WHERE 1=1 ");
            parser8.addSQL(" AND R.G<=1 ");
            parser8.addSQL("  UNION ALL ");
            parser8.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID  FROM ");
            parser8.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID, ");
            parser8.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
            parser8.addSQL(" FROM TF_Bh_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'cancelWorkSheet') R ");
            parser8.addSQL(" WHERE 1=1 ");
            parser8.addSQL(" AND R.G<=1 ");
            IDataset eomsInfosss = Dao.qryByParse(parser8, Route.getJourDb(Route.CONN_CRM_CG));
            if (IDataUtil.isNotEmpty(eomsInfosss)) {
                groupSeq = eomsInfosss.getData(0).getString("GROUP_SEQ");
                IData params6 = new DataMap();
                params6.put("IBSYSID", map.getString("IBSYSID"));
                params6.put("GROUP_SEQ", groupSeq);
                params6.put("RECORD_NUM", eomsInfosss.getData(0).getString("RECORD_NUM"));

                SQLParser parser9 = new SQLParser(params6);
                parser9.addSQL(" SELECT C.ATTR_CODE,  ");
                parser9.addSQL("        C.ATTR_VALUE ");
                parser9.addSQL("  FROM TF_BH_EOP_ATTR C ");
                parser9.addSQL("  WHERE 1=1 ");
                parser9.addSQL("  AND C.IBSYSID = :IBSYSID ");
                parser9.addSQL("  AND C.GROUP_SEQ = :GROUP_SEQ ");
                parser9.addSQL("  AND C.RECORD_NUM = :RECORD_NUM ");
                parser9.addSQL("  UNION ALL ");
                parser9.addSQL(" SELECT C.ATTR_CODE,  ");
                parser9.addSQL("        C.ATTR_VALUE ");
                parser9.addSQL("  FROM TF_B_EOP_ATTR C ");
                parser9.addSQL("  WHERE 1=1 ");
                parser9.addSQL("  AND C.IBSYSID = :IBSYSID ");
                parser9.addSQL("  AND C.GROUP_SEQ = :GROUP_SEQ ");
                parser9.addSQL("  AND C.RECORD_NUM = :RECORD_NUM ");

                IDataset attrListInfos = Dao.qryByParse(parser9, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(attrListInfos)) {
                    for (Object object3 : attrListInfos) {
                        IData attrData = (IData) object3;
                        if ("DEAL_RESULT".equals(attrData.getString("ATTR_CODE"))) {
                            recordNuminfo.put("DEAL_RESULT", attrData.getString("ATTR_VALUE"));
                            break;
                        }
                    }
                }
            }
        }

        IData datalineSumInfo = new DataMap();
        datalineSumInfo.put("DATELINE_SUM", datelineSum);// 专线总数
        datalineSumInfo.put("DATELINE_CANCEL", datelineCancel);// 撤单数
        datalineSumInfo.put("DATALINE_HANG", datalineHang);// 挂起数
        datalineSumInfo.put("DATALINE_CHECKIN", datalinecheckin);// 完工数
        recordNumInfos.add(datalineSumInfo);
        return recordNumInfos;

    }

    public static IDataset viewHisStaff(IData map) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT * FROM ( ");
        parser.addSQL(" SELECT C.ACCEPT_MONTH, ");
        parser.addSQL("  C.BUSIFORM_ID, ");
        parser.addSQL("  C.NODE_ID, ");
        parser.addSQL("  C.REMARK, ");
        parser.addSQL("  C.DEAL_STAFF_ID, ");
        parser.addSQL("  TO_CHAR(C.UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss') UPDATE_DATE, ");
        parser.addSQL("  TO_CHAR(C.FLOW_REAL_TIME, 'yyyy-mm-dd hh24:mi:ss') FLOW_REAL_TIME, ");
        parser.addSQL("  TO_CHAR(C.CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss') CREATE_DATE ");
        parser.addSQL("  FROM TF_B_EWE_NODE_TRA C, ");
        parser.addSQL("       TF_B_EWE E ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND E.BI_SN = :IBSYSID ");
        parser.addSQL("  AND E.BUSIFORM_ID = C.BUSIFORM_ID ");
        parser.addSQL("  AND E.TEMPLET_TYPE = '0' ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT C.ACCEPT_MONTH, ");
        parser.addSQL("  C.BUSIFORM_ID, ");
        parser.addSQL("  C.NODE_ID, ");
        parser.addSQL("  C.REMARK, ");
        parser.addSQL("  C.DEAL_STAFF_ID, ");
        parser.addSQL("  TO_CHAR(C.UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss') UPDATE_DATE, ");
        parser.addSQL("  TO_CHAR(C.FLOW_REAL_TIME, 'yyyy-mm-dd hh24:mi:ss') FLOW_REAL_TIME, ");
        parser.addSQL("  TO_CHAR(C.CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss') CREATE_DATE ");
        parser.addSQL("  FROM TF_BH_EWE_NODE C, ");
        parser.addSQL("       TF_BH_EWE E ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND E.BI_SN = :IBSYSID ");
        parser.addSQL("  AND E.BUSIFORM_ID = C.BUSIFORM_ID ");
        parser.addSQL("  AND E.TEMPLET_TYPE = '0' ");
        parser.addSQL("  ) Z ORDER BY Z.UPDATE_DATE ASC  ");

        IDataset ndoetarInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        IData maps = new DataMap();
        if (IDataUtil.isNotEmpty(ndoetarInfos)) {
            for (Object object : ndoetarInfos) {
                IData nodetarInfo = (IData) object;
                String dealStaffId = nodetarInfo.getString("DEAL_STAFF_ID");
                String staffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", dealStaffId);
                nodetarInfo.put("STAFF_NAME", staffName);

                IData params2 = new DataMap();
                params2.put("NODE_ID", nodetarInfo.getString("NODE_ID"));
                SQLParser parser2 = new SQLParser(params2);
                parser2.addSQL(" SELECT D.NODE_NAME ");
                parser2.addSQL("  FROM TD_B_EWE_NODE_TEMPLET D ");
                parser2.addSQL(" WHERE 1 = 1 ");
                parser2.addSQL(" AND D.NODE_ID = :NODE_ID ");

                IDataset nedeTempletInfo = Dao.qryByParse(parser2, Route.CONN_CRM_CEN);
                if (IDataUtil.isNotEmpty(nedeTempletInfo)) {
                    if ("End".equals(nedeTempletInfo.getData(0).getString("NODE_NAME"))) {
                        nodetarInfo.put("NODE_NAME", "结束");
                    } else {
                        nodetarInfo.put("NODE_NAME", nedeTempletInfo.getData(0).getString("NODE_NAME"));
                    }
                }
            }

        }
        return ndoetarInfos;
    }

    public static IData queryProductAndBusiType(IData map) throws Exception {

        IData params = new DataMap();

        IData offerCha = new DataMap();
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.BPM_TEMPLET_ID, ");
        parser.addSQL("  C.TEMPLET_NAME, ");
        parser.addSQL("  C.VALID_TAG ");
        parser.addSQL("  FROM TD_B_EWE_FLOW_TEMPLET C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND C.VALID_TAG='0' ");
        parser.addSQL("  AND C.TEMPLET_TYPE='0' ");
        parser.addSQL("  AND C.UPDATE_STAFF_ID='XINLIUC' ");
        parser.addSQL("  ORDER BY NLSSORT(C.TEMPLET_NAME,'NLS_SORT=SCHINESE_PINYIN_M') ");

        IDataset busiTypeInfos = Dao.qryByParse(parser, Route.CONN_CRM_CEN);

        SQLParser parser1 = new SQLParser(params);
        parser1.addSQL(" SELECT DISTINCT  ");
        parser1.addSQL("  D.PROD_SPEC_ID ");
        parser1.addSQL("  FROM TD_B_EOP_BUSI_FLOW_RELE C, ");
        parser1.addSQL("       TD_B_EOP_PROD_SPEC D ");
        parser1.addSQL("  WHERE 1=1 ");
        parser1.addSQL("  AND D.PROD_SPEC_TYPE = 'P' ");
        parser1.addSQL("  AND C.BUSI_SPEC_ID = D.BUSI_SPEC_ID ");
        parser1.addSQL("  AND C.END_TIME > SYSDATE ");
        parser1.addSQL("  ORDER BY NLSSORT(D.PROD_SPEC_ID,'NLS_SORT=SCHINESE_PINYIN_M') ");
        IDataset productInfos = Dao.qryByParse(parser1, Route.CONN_CRM_CEN);

        IDataset productlist = new DatasetList();
        if (IDataUtil.isNotEmpty(productInfos)) {
            IData param = new DataMap();
            for (Object object : productInfos) {
                IData data = new DataMap();
                IData productInfo = (IData) object;
                String prodSpecId = productInfo.getString("PROD_SPEC_ID");
                if ("7000".equals(prodSpecId) || "701101".equals(prodSpecId) || prodSpecId.indexOf("VP") != -1 || "99832".equals(prodSpecId) || "99833".equals(prodSpecId) || "99834".equals(prodSpecId) || "99835".equals(prodSpecId)) {
                    continue;
                }
                IData input = new DataMap();
                input.put("OFFER_CODE", prodSpecId);
                input.put("OFFER_TYPE", "P");
                // g根据offerCode,获取产品名称
                ServiceResponse response = BizServiceFactory.call("UPC.Out.OfferQueryFSV.queryOfferByOfferId", input, null);
                IData out = response.getBody();
                if (IDataUtil.isNotEmpty(out)) {
                    IDataset ouparams = out.getDataset("OUTDATA");
                    if (IDataUtil.isNotEmpty(ouparams)) {
                        data.put("DATA_NAME", ouparams.getData(0).getString("OFFER_NAME"));
                        data.put("DATA_ID", ouparams.getData(0).getString("OFFER_CODE"));
                        productlist.add(data);
                    }
                }
            }
            param.put("DATA_VAL", productlist);
            offerCha.put("PRODUCT_INFO", param);
        }

        IDataset busiTypelist = new DatasetList();
        if (IDataUtil.isNotEmpty(busiTypeInfos)) {
            IData param = new DataMap();
            for (Object object : busiTypeInfos) {
                IData data = new DataMap();
                IData busiTypeInfo = (IData) object;
                data.put("DATA_NAME", busiTypeInfo.getString("TEMPLET_NAME"));
                data.put("DATA_ID", busiTypeInfo.getString("BPM_TEMPLET_ID"));
                busiTypelist.add(data);
            }
            param.put("DATA_VAL", busiTypelist);
            offerCha.put("BUSI_TYPE_INFO", param);
        }

        return offerCha;

    }

    public static IDataset queryHistoryMistakeInfo(IData map, Pagination pagination) throws Exception {

        IData params = new DataMap();
        params.put("STAFF_ID", map.getString("cond_TRADE_STAFF_ID"));
        params.put("BPM_TEMPLET_ID", map.getString("cond_TPTEMPLET_ID"));
        params.put("PRODUCT_ID", map.getString("cond_PRODUCT_ID"));
        params.put("START_DATE", map.getString("COMPLAINT_START_DATE"));
        params.put("END_DATE", map.getString("COMPLAINT_END_DATE"));
        params.put("IBSYSID", map.getString("cond_SUBSCRIBE_ID"));
        params.put("GROUP_ID", map.getString("cond_CUST_ID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.BUSIFORM_ID,");
        parser.addSQL("  D.LOG_ID, ");
        parser.addSQL("  D.LOG_INFO, ");
        parser.addSQL("  TO_CHAR(D.UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss') UPDATE_DATE, ");
        parser.addSQL("  E.DEAL_STAFF_ID, ");
        parser.addSQL("  E.NODE_ID, ");
        parser.addSQL("  R.PRODUCT_ID, ");
        parser.addSQL("  C.GROUP_ID, ");
        parser.addSQL("  R.PRODUCT_NAME, ");
        parser.addSQL("  R.IBSYSID ");
        parser.addSQL("  FROM TF_B_EWE T, ");
        parser.addSQL("       TF_B_EWE_ERROR_LOG D, ");
        parser.addSQL("       TF_B_EWE_NODE_TRA E, ");
        parser.addSQL("       TF_B_EOP_PRODUCT R, ");
        parser.addSQL("       TF_B_EOP_SUBSCRIBE C ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND T.BUSIFORM_ID = E.BUSIFORM_ID ");
        parser.addSQL("  AND E.BUSIFORM_NODE_ID = D.BUSIFORM_NODE_ID ");
        parser.addSQL("  AND T.BI_SN = R.IBSYSID ");
        parser.addSQL("  AND T.BI_SN = C.IBSYSID ");
        parser.addSQL("  AND T.ACCEPT_STAFF_ID = :STAFF_ID ");
        parser.addSQL(" AND (:IBSYSID is null or T.BI_SN=:IBSYSID) ");
        parser.addSQL(" AND (:GROUP_ID is null or C.GROUP_ID=:GROUP_ID) ");
        parser.addSQL(" AND (:START_DATE is null or  D.UPDATE_DATE >= to_date(:START_DATE, 'yyyy-MM-dd HH24:mi:ss')) ");
        parser.addSQL(" AND (:END_DATE is null or  D.UPDATE_DATE <= to_date(:END_DATE, 'yyyy-MM-dd HH24:mi:ss')) ");
        parser.addSQL(" AND (:PRODUCT_ID is null or R.PRODUCT_ID=:PRODUCT_ID) ");
        parser.addSQL(" AND (:BPM_TEMPLET_ID is null or T.BPM_TEMPLET_ID=:BPM_TEMPLET_ID) ");
        parser.addSQL(" ORDER BY D.UPDATE_DATE DESC ");

        IDataset historyMistakeInfos = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));

        if (IDataUtil.isNotEmpty(historyMistakeInfos)) {
            for (Object object : historyMistakeInfos) {
                IData myworkformInfo = (IData) object;
                String nodeId = myworkformInfo.getString("NODE_ID");
                String dealStaffId = myworkformInfo.getString("DEAL_STAFF_ID");
                IData params2 = new DataMap();
                params2.put("NODE_ID", nodeId);
                SQLParser parser2 = new SQLParser(params2);
                parser2.addSQL(" SELECT D.NODE_NAME ");
                parser2.addSQL("  FROM TD_B_EWE_NODE_TEMPLET D ");
                parser2.addSQL(" WHERE 1 = 1 ");
                parser2.addSQL(" AND D.NODE_ID = :NODE_ID ");

                IDataset nedeTempletInfo = Dao.qryByParse(parser2, Route.CONN_CRM_CEN);
                if (IDataUtil.isNotEmpty(nedeTempletInfo)) {
                    myworkformInfo.put("NODE_NAME", nedeTempletInfo.getData(0).getString("NODE_NAME"));

                }
                String dealStaffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", dealStaffId);
                if (StringUtils.isNotBlank(dealStaffName)) {
                    myworkformInfo.put("DEAL_STAFF_NAME", dealStaffName);
                }
            }

        }

        return historyMistakeInfos;

    }

    public static IDataset queryMarktingWorkFormList(IData map, Pagination pagination) throws Exception {

        IData params = new DataMap();
        if ("true".equals(map.getString("cond_FLAG"))) {
            params.put("RESPONSIBILITY_ID", map.getString("STAFF_ID"));
        } else if ("false".equals(map.getString("cond_FLAG"))) {
            String StaffId = map.getString("STAFF_ID");
            String cityId = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "CITY_CODE", StaffId);
            if (!"HNSJ".equals(cityId)) {
                params.put("CITY_ID", cityId);
            }
        }
        params.put("IBSYSID_TAPMARKETING", map.getString("cond_IBSYSID_TAPMARKETING"));
        params.put("IBSYSID_EXCITATION", map.getString("cond_IBSYSID_EXCITATION"));
        params.put("RESULT_CODE", map.getString("cond_RESULT_CODE"));
        params.put("CUST_ID", map.getString("cond_CUST_ID"));
        params.put("PRODUCT_NO", map.getString("cond_PRODUCT_NO"));
        params.put("ACCEPT_DATE", map.getString("cond_ACCEPT_DATE"));
        params.put("LEADER_MONTH", map.getString("cond_LEADER_MONTH"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT IBSYSID, IBSYSID_TAPMARKETING, ");
        parser.addSQL("  IBSYSID_EXCITATION, PROVINCEA, CITY_ID, ");
        parser.addSQL("  CITYA, FRIENDBUSINESS_NAME, CUST_ID, ");
        parser.addSQL("  CUST_NAME, RESPONSIBILITY_ID, ");
        parser.addSQL("  RESPONSIBILITY_NAME, RESPONSIBILITY_PHONE, ");
        parser.addSQL("  LINENAME, LINETYPE, BANDWIDTH,LEADER_OPINION, ");
        parser.addSQL("  PRODUCT_NO, PRODUCT_NUMBER, CONTRACT_AGE, ");
        parser.addSQL("  MONTHLYFEE_TAP,MONTHLYFEE_EXCITATION, LINEPRICE_TAP, ");
        parser.addSQL("  LINEPRICE_EXCITATION, RESULT_CODE, AUDIT_OPINION, ");
        parser.addSQL("  to_char(EXCITATION_DATE,'yyyy-MM-dd HH24:mi:ss') EXCITATION_DATE, ");
        parser.addSQL("  to_char(AUDIT_DATE,'yyyy-MM-dd HH24:mi:ss') AUDIT_DATE, ");
        parser.addSQL("  to_char(LEADER_DATE,'yyyy-MM-dd HH24:mi:ss') LEADER_DATE, ");
        parser.addSQL("  to_char(ACCEPT_DATE,'yyyy-MM-dd HH24:mi:ss') ACCEPT_DATE, ");
        parser.addSQL("  to_char(UPDATE_DATE,'yyyy-MM-dd HH24:mi:ss') UPDATE_DATE, ");
        parser.addSQL("  RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        parser.addSQL("  to_char(RSRV_DATE1,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("  to_char(RSRV_DATE1,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("  to_char(RSRV_DATE1,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE3 ");
        parser.addSQL("  FROM TF_B_EOP_TAPMARKETING ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND (IBSYSID =:IBSYSID or :IBSYSID is null)");
        parser.addSQL("  AND (IBSYSID_TAPMARKETING =:IBSYSID_TAPMARKETING or :IBSYSID_TAPMARKETING is null) ");
        parser.addSQL("  AND (IBSYSID_EXCITATION =:IBSYSID_EXCITATION or :IBSYSID_EXCITATION is null) ");
        parser.addSQL("  AND (RESULT_CODE=:RESULT_CODE or :RESULT_CODE is null)");
        parser.addSQL("  AND (RESPONSIBILITY_ID=:RESPONSIBILITY_ID or :RESPONSIBILITY_ID is null) ");
        parser.addSQL("  AND (CITY_ID=:CITY_ID or :CITY_ID is null) ");
        parser.addSQL("  AND (CUST_ID=:CUST_ID or :CUST_ID is null) ");
        parser.addSQL("  AND (PRODUCT_NO=:PRODUCT_NO or :PRODUCT_NO is null) ");
        parser.addSQL("  AND (ACCEPT_DATE>=to_date(:ACCEPT_DATE,'yyyy-MM-dd HH24:mi:ss') or :ACCEPT_DATE is null) ");
        parser.addSQL("  AND (to_char(LEADER_DATE,'yyyy-MM')=:LEADER_MONTH or :LEADER_MONTH is null) ");

        IDataset MarktingWorkFormList = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        return MarktingWorkFormList;

    }

    public static IDataset queryattrByRecordNum(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("ATTR_CODE", map.getString("ATTR_CODE"));
        params.put("ATTR_VALUE", map.getString("ATTR_VALUE"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT DISTINCT C.NODE_ID, ");
        parser.addSQL(" C.IBSYSID, ");
        parser.addSQL(" C.ACCEPT_MONTH, ");
        parser.addSQL(" C.RECORD_NUM, ");
        parser.addSQL(" D.SUB_IBSYSID, ");
        parser.addSQL(" C.ATTR_CODE, ");
        parser.addSQL(" C.ATTR_VALUE ");
        parser.addSQL(" FROM TF_B_EOP_ATTR C, ");
        parser.addSQL("  TF_B_EOP_EOMS D ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID = :IBSYSID ");
        parser.addSQL(" AND C.ATTR_CODE = :ATTR_CODE ");
        parser.addSQL(" AND C.ATTR_VALUE = :ATTR_VALUE ");
        parser.addSQL(" AND C.SUB_IBSYSID = D.SUB_IBSYSID ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryByAttach(IData map) throws Exception {
        IData params = new DataMap();
        params.put("RECORD_NUM", map.getString("RECORD_NUM"));
        params.put("SUB_IBSYSID", map.getString("SUB_IBSYSID"));
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL(" C.SEQ, ");
        parser.addSQL(" C.GROUP_SEQ, ");
        parser.addSQL(" C.SUB_IBSYSID, ");
        parser.addSQL(" C.NODE_ID, ");
        parser.addSQL(" C.DISPLAY_NAME, ");
        parser.addSQL(" C.ATTACH_NAME, ");
        parser.addSQL(" C.ATTACH_LENGTH, ");
        parser.addSQL(" C.ATTACH_URL, ");
        parser.addSQL(" C.ATTACH_LOCAL_PATH, ");
        parser.addSQL(" C.ATTACH_CITY_CODE, ");
        parser.addSQL(" C.ATTACH_EPARCHY_CODE, ");
        parser.addSQL(" C.ATTACH_DEPART_ID, ");
        parser.addSQL(" C.ATTACH_DEPART_NAME, ");
        parser.addSQL(" C.ATTACH_STAFF_ID, ");
        parser.addSQL(" C.ATTACH_STAFF_NAME, ");
        parser.addSQL(" C.ATTACH_STAFF_PHONE, ");
        parser.addSQL(" C.FILE_ID, ");
        parser.addSQL(" TO_CHAR(C.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" C.REMARK, ");
        parser.addSQL(" TO_CHAR(C.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" C.ACCEPT_MONTH, ");
        parser.addSQL(" C.VALID_TAG, ");
        parser.addSQL(" C.ATTACH_TYPE ");
        parser.addSQL(" FROM TF_B_EOP_ATTACH C ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID = :IBSYSID ");
        parser.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
        parser.addSQL(" AND C.SUB_IBSYSID = :SUB_IBSYSID ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryReportSurvey(IData map, Pagination pagination) throws Exception {

        String subTypeOpen = map.getString("SUB_TYPE_OPEN");
        if (StringUtils.isNotEmpty(subTypeOpen)) {
            if ("1".equals(subTypeOpen)) {
                subTypeOpen = "开通勘察单";
            } else if ("2".equals(subTypeOpen)) {
                subTypeOpen = "变更勘察单";
            }
        }
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("START_DATE", map.getString("ACCEPT_START"));
        params.put("END_DATE", map.getString("ACCEPT_END"));
        params.put("SUB_TYPE", subTypeOpen);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT*FROM ( SELECT C.IBSYSID,C.SUB_TYPE,C.BIZ_RANGE, ");
        parser.addSQL("  C.PRODUCT_NO,C.PRODUCT_NAME,C.APPROVAL_TIME, ");
        parser.addSQL("  C.TITLE,C.BOSS_TIME,C.ARRIVAL_TIME, ");
        parser.addSQL("  C.GROUP_ID,C.REPLY_TIME,C.ARCHIVE_TIME, ");
        parser.addSQL("  C.CUST_NAME,C.CONFIRM_TIME,REPORTED_TIME, ");
        parser.addSQL("  C.CITY_CODE,C.END_TIME,C.RSRV_STR1, ");
        parser.addSQL("  C.CUST_MANAGER_NAME,C.RSRV_STR2,C.RSRV_STR3, ");
        parser.addSQL("  C.BUSINESS_TYPE,C.RSRV_STR4,C.RSRV_STR5, ");
        parser.addSQL("  C.BUILDING_SECTION,RSRV_TAG1, ");
        parser.addSQL("       TO_CHAR(C.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL("       TO_CHAR(C.CONTRACT_TIME, 'yyyy-mm-dd hh24:mi:ss') CONTRACT_TIME, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,'' FINISH_DATE, ");
        parser.addSQL(" C.RSRV_TAG2,C.RSRV_TAG3,W.ATTR_VALUE,W.RECORD_NUM,Q.STATE,Q.ACCEPT_STAFF_ID  ");
        parser.addSQL(" FROM TF_B_EOP_LINEREPORT C,TF_B_EOP_ATTR W,TF_B_EWE Q ");
        parser.addSQL("  WHERE W.ATTR_CODE='BIZSECURITYLV' ");
        parser.addSQL("  AND W.NODE_ID='apply'  ");
        parser.addSQL("  AND W.SUB_IBSYSID=(SELECT MAX(T.SUB_IBSYSID)  ");
        parser.addSQL("  FROM TF_B_EOP_ATTR T WHERE T.IBSYSID=C.IBSYSID AND T.NODE_ID='apply' AND T.ATTR_CODE='BIZSECURITYLV') ");
        parser.addSQL("  AND W.RECORD_NUM <> '0'  ");
        parser.addSQL("  AND W.IBSYSID=C.IBSYSID ");
        parser.addSQL("  AND C.IBSYSID= Q.BI_SN ");
        parser.addSQL("  AND Q.TEMPLET_TYPE= '0' ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') >= to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND (C.IBSYSID =:IBSYSID or :IBSYSID is null) ");
        if (StringUtils.isNotBlank(subTypeOpen)) {
            parser.addSQL(" AND C.SUB_TYPE =:SUB_TYPE ");
        } else {
            parser.addSQL(" AND C.SUB_TYPE IN ('开通勘察单','变更勘察单') ");
        }
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT C.IBSYSID,C.SUB_TYPE,C.BIZ_RANGE, ");
        parser.addSQL("  C.PRODUCT_NO,C.PRODUCT_NAME,C.APPROVAL_TIME, ");
        parser.addSQL("  C.TITLE,C.BOSS_TIME,C.ARRIVAL_TIME, ");
        parser.addSQL("  C.GROUP_ID,C.REPLY_TIME,C.ARCHIVE_TIME, ");
        parser.addSQL("  C.CUST_NAME,C.CONFIRM_TIME,REPORTED_TIME, ");
        parser.addSQL("  C.CITY_CODE,C.END_TIME,C.RSRV_STR1, ");
        parser.addSQL("  C.CUST_MANAGER_NAME,C.RSRV_STR2,C.RSRV_STR3, ");
        parser.addSQL("  C.BUSINESS_TYPE,C.RSRV_STR4,C.RSRV_STR5, ");
        parser.addSQL("  C.BUILDING_SECTION,RSRV_TAG1, ");
        parser.addSQL("       TO_CHAR(C.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL("       TO_CHAR(C.CONTRACT_TIME, 'yyyy-mm-dd hh24:mi:ss') CONTRACT_TIME, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL("       TO_CHAR(Q.FINISH_DATE,'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
        parser.addSQL(" C.RSRV_TAG2,C.RSRV_TAG3,W.ATTR_VALUE,W.RECORD_NUM,Q.STATE,Q.ACCEPT_STAFF_ID ");
        parser.addSQL(" FROM TF_B_EOP_LINEREPORT C,TF_BH_EOP_ATTR W,TF_BH_EWE Q ");
        parser.addSQL("  WHERE W.ATTR_CODE='BIZSECURITYLV' ");
        parser.addSQL("  AND W.NODE_ID='apply'  ");
        parser.addSQL("  AND W.SUB_IBSYSID=(SELECT MAX(T.SUB_IBSYSID)  ");
        parser.addSQL("  FROM TF_BH_EOP_ATTR T WHERE T.IBSYSID=C.IBSYSID AND T.NODE_ID='apply' AND T.ATTR_CODE='BIZSECURITYLV') ");
        parser.addSQL("  AND W.RECORD_NUM <> '0'  ");
        parser.addSQL("  AND W.IBSYSID=C.IBSYSID ");
        parser.addSQL("  AND C.IBSYSID= Q.BI_SN ");
        parser.addSQL("  AND Q.TEMPLET_TYPE= '0' ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') >= to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND (C.IBSYSID =:IBSYSID or :IBSYSID is null) ");
        if (StringUtils.isNotBlank(subTypeOpen)) {
            parser.addSQL(" AND C.SUB_TYPE =:SUB_TYPE ");
        } else {
            parser.addSQL(" AND C.SUB_TYPE IN ('开通勘察单','变更勘察单') ");
        }

        parser.addSQL(" )Z ORDER BY Z.IBSYSID  ");

        IDataset attrlineInfos = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(attrlineInfos)) {
            for (Object attrIf : attrlineInfos) {
                IData attlineInfo = (IData) attrIf;
                String ibsysId = attlineInfo.getString("IBSYSID");
                String recordNum = attlineInfo.getString("RECORD_NUM");
                String state = attlineInfo.getString("STATE", "");
                String acceptStaffId = attlineInfo.getString("ACCEPT_STAFF_ID", "");
                String dealStaffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", acceptStaffId);
                attlineInfo.put("ACCEPT_STAFF_NAME", dealStaffName);

                if ("4".equals(state)) {
                    attlineInfo.put("STATE_TYPE", "已撤单");
                } else if ("9".equals(state)) {
                    attlineInfo.put("STATE_TYPE", "已完成");
                } else {
                    attlineInfo.put("STATE_TYPE", "未完成");
                }

                String groupId = attlineInfo.getString("GROUP_ID", "");
                IData data = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                if (IDataUtil.isNotEmpty(data)) {
                    String servLevel = data.getString("SERV_LEVEL", "");
                    if (StringUtils.isBlank(servLevel)) {
                        servLevel = "4";
                    }
                    String servicelevel = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[] { "CUSTGROUP_SERVLEVEL", servLevel });
                    attlineInfo.put("SERVICELEVEL", servicelevel);
                }

                IData params1 = new DataMap();
                params1.put("IBSYSID", ibsysId);
                params1.put("RECORD_NUM", recordNum);

                SQLParser parser2 = new SQLParser(params1);
                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.ATTR_CODE ");
                parser2.addSQL(" FROM TF_B_EOP_ATTR C ");
                parser2.addSQL(" WHERE C.ATTR_CODE = 'PRODUCTNO' ");
                parser2.addSQL(" AND C.NODE_ID = 'apply'  ");
                parser2.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
                parser2.addSQL(" AND C.IBSYSID = :IBSYSID ");
                parser2.addSQL(" UNION ALL ");
                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.ATTR_CODE ");
                parser2.addSQL(" FROM TF_BH_EOP_ATTR C ");
                parser2.addSQL(" WHERE C.ATTR_CODE = 'PRODUCTNO' ");
                parser2.addSQL(" AND C.NODE_ID = 'apply'  ");
                parser2.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
                parser2.addSQL(" AND C.IBSYSID = :IBSYSID ");

                IDataset attrData2 = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(attrData2)) {
                    String productNo = attrData2.first().getString("ATTR_VALUE");
                    attlineInfo.put("PRODUCTNO", productNo);
                }
            }

        }

        return attrlineInfos;

    }

    public static IDataset queryReportClear(IData map, Pagination pagination) throws Exception {
        String subTypeOpen = map.getString("SUB_TYPE_OPEN");
        if (StringUtils.isNotEmpty(subTypeOpen)) {
            if ("1".equals(subTypeOpen)) {
                subTypeOpen = "开通单";
            } else if ("2".equals(subTypeOpen)) {
                subTypeOpen = "变更开通单";
            }
        }
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("START_DATE", map.getString("ACCEPT_START"));
        params.put("END_DATE", map.getString("ACCEPT_END"));
        params.put("SUB_TYPE", subTypeOpen);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT * FROM ( SELECT C.IBSYSID,C.SUB_TYPE,C.BIZ_RANGE, ");
        parser.addSQL("  C.PRODUCT_NO,C.PRODUCT_NAME,C.APPROVAL_TIME, ");
        parser.addSQL("  C.TITLE,C.BOSS_TIME,C.ARRIVAL_TIME, ");
        parser.addSQL("  C.GROUP_ID,C.REPLY_TIME,C.ARCHIVE_TIME, ");
        parser.addSQL("  C.CUST_NAME,C.CONFIRM_TIME,REPORTED_TIME, ");
        parser.addSQL("  C.CITY_CODE,C.END_TIME,C.RSRV_STR1, ");
        parser.addSQL("  C.CUST_MANAGER_NAME,C.RSRV_STR2,C.RSRV_STR3, ");
        parser.addSQL("  C.BUSINESS_TYPE,C.RSRV_STR4,C.RSRV_STR5, ");
        parser.addSQL("  C.BUILDING_SECTION,RSRV_TAG1, ");
        parser.addSQL("       TO_CHAR(C.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL("       TO_CHAR(C.CONTRACT_TIME, 'yyyy-mm-dd hh24:mi:ss') CONTRACT_TIME, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,'' FINISH_DATE, ");
        parser.addSQL(" C.RSRV_TAG2,C.RSRV_TAG3,W.ATTR_VALUE,W.RECORD_NUM,Q.STATE,Q.ACCEPT_STAFF_ID  ");
        parser.addSQL(" FROM TF_B_EOP_LINEREPORT C,TF_B_EOP_ATTR W,TF_B_EWE Q,TF_B_EOP_EOMS_STATE H ");
        parser.addSQL("  WHERE W.ATTR_CODE='BIZSECURITYLV' ");
        parser.addSQL("  AND W.NODE_ID='apply'  ");
        parser.addSQL("  AND W.IBSYSID=C.IBSYSID ");
        parser.addSQL("  AND W.SUB_IBSYSID=(SELECT MAX(T.SUB_IBSYSID)  ");
        parser.addSQL("  FROM TF_B_EOP_ATTR T WHERE T.IBSYSID=C.IBSYSID AND T.NODE_ID='apply' AND T.ATTR_CODE='BIZSECURITYLV' ) ");
        parser.addSQL("  AND W.RECORD_NUM <> '0' ");
        parser.addSQL("  AND C.IBSYSID= Q.BI_SN ");
        parser.addSQL("  AND Q.TEMPLET_TYPE= '0' ");
        parser.addSQL("  AND C.IBSYSID = H.IBSYSID ");
        parser.addSQL("  AND C.PRODUCT_NO = H.PRODUCT_NO ");
        parser.addSQL("  AND W.RECORD_NUM = H.RECORD_NUM ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') >= to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND (C.IBSYSID =:IBSYSID or :IBSYSID is null) ");
        if (StringUtils.isNotBlank(subTypeOpen)) {
            parser.addSQL(" AND C.SUB_TYPE =:SUB_TYPE ");
        } else {
            parser.addSQL(" AND C.SUB_TYPE IN ('开通单','变更开通单') ");
        }
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT C.IBSYSID,C.SUB_TYPE,C.BIZ_RANGE, ");
        parser.addSQL("  C.PRODUCT_NO,C.PRODUCT_NAME,C.APPROVAL_TIME, ");
        parser.addSQL("  C.TITLE,C.BOSS_TIME,C.ARRIVAL_TIME, ");
        parser.addSQL("  C.GROUP_ID,C.REPLY_TIME,C.ARCHIVE_TIME, ");
        parser.addSQL("  C.CUST_NAME,C.CONFIRM_TIME,REPORTED_TIME, ");
        parser.addSQL("  C.CITY_CODE,C.END_TIME,C.RSRV_STR1, ");
        parser.addSQL("  C.CUST_MANAGER_NAME,C.RSRV_STR2,C.RSRV_STR3, ");
        parser.addSQL("  C.BUSINESS_TYPE,C.RSRV_STR4,C.RSRV_STR5, ");
        parser.addSQL("  C.BUILDING_SECTION,RSRV_TAG1, ");
        parser.addSQL("       TO_CHAR(C.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL("       TO_CHAR(C.CONTRACT_TIME, 'yyyy-mm-dd hh24:mi:ss') CONTRACT_TIME, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("       TO_CHAR(C.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL("       TO_CHAR(Q.FINISH_DATE,'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
        parser.addSQL(" C.RSRV_TAG2,C.RSRV_TAG3,W.ATTR_VALUE,W.RECORD_NUM,Q.STATE,Q.ACCEPT_STAFF_ID ");
        parser.addSQL(" FROM TF_B_EOP_LINEREPORT C,TF_BH_EOP_ATTR W,TF_BH_EWE Q,TF_BH_EOP_EOMS_STATE H ");
        parser.addSQL("  WHERE W.ATTR_CODE='BIZSECURITYLV' ");
        parser.addSQL("  AND W.NODE_ID='apply'  ");
        parser.addSQL("  AND W.IBSYSID=C.IBSYSID ");
        parser.addSQL("  AND W.SUB_IBSYSID=(SELECT MAX(T.SUB_IBSYSID)  ");
        parser.addSQL("  FROM TF_BH_EOP_ATTR T WHERE T.IBSYSID=C.IBSYSID AND T.NODE_ID='apply' AND T.ATTR_CODE='BIZSECURITYLV') ");
        parser.addSQL("  AND W.RECORD_NUM <> '0' ");
        parser.addSQL("  AND C.IBSYSID= Q.BI_SN ");
        parser.addSQL("  AND Q.TEMPLET_TYPE= '0' ");
        parser.addSQL("  AND C.IBSYSID = H.IBSYSID ");
        parser.addSQL("  AND C.PRODUCT_NO = H.PRODUCT_NO ");
        parser.addSQL("  AND W.RECORD_NUM = H.RECORD_NUM ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') >= to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND to_char(C.ACCEPT_TIME, 'yyyymmddss') <= to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmddss') ");
        parser.addSQL(" AND (C.IBSYSID =:IBSYSID or :IBSYSID is null) ");
        if (StringUtils.isNotBlank(subTypeOpen)) {
            parser.addSQL(" AND C.SUB_TYPE =:SUB_TYPE ");
        } else {
            parser.addSQL(" AND C.SUB_TYPE IN ('开通单','变更开通单') ");
        }

        parser.addSQL(" )Z ORDER BY Z.IBSYSID  ");

        IDataset attrlineInfos = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));

        if (IDataUtil.isNotEmpty(attrlineInfos)) {
            for (Object attrIf : attrlineInfos) {
                IData attlineInfo = (IData) attrIf;
                String ibsysId = attlineInfo.getString("IBSYSID");
                String recordNum = attlineInfo.getString("RECORD_NUM");
                String state = attlineInfo.getString("STATE", "");
                String acceptStaffId = attlineInfo.getString("ACCEPT_STAFF_ID", "");
                String dealStaffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", acceptStaffId);
                attlineInfo.put("ACCEPT_STAFF_NAME", dealStaffName);

                if ("4".equals(state)) {
                    attlineInfo.put("STATE_TYPE", "已撤单");
                } else if ("9".equals(state)) {
                    attlineInfo.put("STATE_TYPE", "已完成");
                } else {
                    attlineInfo.put("STATE_TYPE", "未完成");
                }

                String groupId = attlineInfo.getString("GROUP_ID", "");
                IData data = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                if (IDataUtil.isNotEmpty(data)) {
                    String servLevel = data.getString("SERV_LEVEL", "");
                    if (StringUtils.isBlank(servLevel)) {
                        servLevel = "4";
                    }
                    String servicelevel = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[] { "CUSTGROUP_SERVLEVEL", servLevel });
                    attlineInfo.put("SERVICELEVEL", servicelevel);
                }

                IData params1 = new DataMap();
                params1.put("IBSYSID", ibsysId);
                params1.put("RECORD_NUM", recordNum);

                SQLParser parser2 = new SQLParser(params1);
                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.ATTR_CODE ");
                parser2.addSQL(" FROM TF_B_EOP_ATTR C ");
                parser2.addSQL(" WHERE C.ATTR_CODE = 'PRODUCTNO' ");
                parser2.addSQL(" AND C.NODE_ID = 'apply'  ");
                parser2.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
                parser2.addSQL(" AND C.IBSYSID = :IBSYSID ");
                parser2.addSQL(" UNION ALL ");
                parser2.addSQL(" SELECT C.IBSYSID, ");
                parser2.addSQL(" C.ATTR_VALUE, ");
                parser2.addSQL(" C.RECORD_NUM, ");
                parser2.addSQL(" C.ATTR_CODE ");
                parser2.addSQL(" FROM TF_BH_EOP_ATTR C ");
                parser2.addSQL(" WHERE C.ATTR_CODE = 'PRODUCTNO' ");
                parser2.addSQL(" AND C.NODE_ID = 'apply'  ");
                parser2.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
                parser2.addSQL(" AND C.IBSYSID = :IBSYSID ");

                IDataset attrData2 = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(attrData2)) {
                    String productNo = attrData2.first().getString("ATTR_VALUE");
                    attlineInfo.put("PRODUCTNO", productNo);
                }
            }

        }

        return attrlineInfos;
    }

    public static IDataset querySummarizeEoms(IData map) throws Exception {

        IDataset eomsLiset = new DatasetList();
        IDataset attrDateInfos = new DatasetList();
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("RECORD_NUM", map.getString("RECORD_NUM"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT * FROM ( ");
        parser.addSQL(" SELECT C.GROUP_SEQ, ");
        parser.addSQL(" C.IBSYSID, ");
        parser.addSQL(" C.RECORD_NUM ");
        parser.addSQL(" FROM TF_B_EOP_EOMS C ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID = :IBSYSID ");
        parser.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
        parser.addSQL(" AND C.OPER_TYPE = 'notifyWorkSheet' ");
        parser.addSQL("  UNION ALL ");
        parser.addSQL(" SELECT C.GROUP_SEQ, ");
        parser.addSQL(" C.IBSYSID, ");
        parser.addSQL(" C.RECORD_NUM ");
        parser.addSQL(" FROM TF_BH_EOP_EOMS C ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID = :IBSYSID ");
        parser.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
        parser.addSQL(" AND C.OPER_TYPE = 'notifyWorkSheet' ");
        parser.addSQL(" ) Z ORDER BY Z.GROUP_SEQ ASC ");
        IDataset eomsInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isEmpty(eomsInfos)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据工单号【" + map.getString("IBSYSID") + "】资管未发送'notifyWorkSheet'报文，无法获取到资管状态！");
        }
        for (Object object : eomsInfos) {
            IData eomsInfo = (IData) object;
            IData params1 = new DataMap();
            params1.put("IBSYSID", eomsInfo.getString("IBSYSID"));
            params1.put("RECORD_NUM", eomsInfo.getString("RECORD_NUM"));
            params1.put("GROUP_SEQ", eomsInfo.getString("GROUP_SEQ"));

            SQLParser parser1 = new SQLParser(params1);
            parser1.addSQL(" SELECT C.IBSYSID, ");
            parser1.addSQL(" C.ATTR_CODE, ");
            parser1.addSQL(" C.ATTR_VALUE ");
            parser1.addSQL(" FROM TF_B_EOP_ATTR C ");
            parser1.addSQL(" WHERE 1 = 1 ");
            parser1.addSQL(" AND C.IBSYSID = :IBSYSID ");
            parser1.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
            parser1.addSQL(" AND C.GROUP_SEQ = :GROUP_SEQ ");
            parser1.addSQL("  UNION ALL ");
            parser1.addSQL(" SELECT C.IBSYSID, ");
            parser1.addSQL(" C.ATTR_CODE, ");
            parser1.addSQL(" C.ATTR_VALUE ");
            parser1.addSQL(" FROM TF_BH_EOP_ATTR C ");
            parser1.addSQL(" WHERE 1 = 1 ");
            parser1.addSQL(" AND C.IBSYSID = :IBSYSID ");
            parser1.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
            parser1.addSQL(" AND C.GROUP_SEQ = :GROUP_SEQ ");
            IData attrParam = new DataMap();
            IDataset attrInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
            IData attrDateInfo = new DataMap();
            if (IDataUtil.isNotEmpty(attrInfos)) {
                for (Object object2 : attrInfos) {
                    IData attrInfo = (IData) object2;
                    // 把资管传的“变成空格
                    if ("ReachTimer".equals(attrInfo.getString("ATTR_CODE"))) {
                        String reachTimer = attrInfo.getString("ATTR_VALUE", "");
                        StringBuilder reachTimerValue = new StringBuilder(reachTimer);
                        if(reachTimerValue.length()>=11){
                        	reachTimerValue.replace(10, 11, " ");
                        }
                        attrParam.put(attrInfo.getString("ATTR_CODE").toUpperCase(), reachTimerValue);
                    } else if ("PreFinishTimer".equals(attrInfo.getString("ATTR_CODE", ""))) {
                        String reachTimer = attrInfo.getString("ATTR_VALUE", "");
                        StringBuilder reachTimerValue = new StringBuilder(reachTimer);
                        if(reachTimerValue.length()>=11){
                            reachTimerValue.replace(10, 11, " ");
                        }
                        attrDateInfo.put(attrInfo.getString("ATTR_CODE", "").toUpperCase(), reachTimerValue);
                    } else if ("PreNodeName".equals(attrInfo.getString("ATTR_CODE", ""))) {
                        attrDateInfo.put(attrInfo.getString("ATTR_CODE", "").toUpperCase(), attrInfo.getString("ATTR_VALUE", ""));
                    }else if ("Direction".equals(attrInfo.getString("ATTR_CODE", ""))) {
                        attrDateInfo.put(attrInfo.getString("ATTR_CODE", "").toUpperCase(), attrInfo.getString("ATTR_VALUE", ""));
                    }  else {
                        attrParam.put(attrInfo.getString("ATTR_CODE", "").toUpperCase(), attrInfo.getString("ATTR_VALUE", ""));

                    }
                }
            }
            if(attrParam.getString("DIRECTIONNOW", "").equals("A端")||attrParam.getString("DIRECTIONNOW", "").equals("Z端")){//AZ端加上前缀
            	attrParam.put("NODENAME",attrParam.getString("DIRECTIONNOW", "")+attrParam.getString("NODENAME", ""));
            }
            if(attrDateInfo.getString("DIRECTION", "").equals("A端")||attrDateInfo.getString("DIRECTION", "").equals("Z端")){//AZ端加上前缀
            	attrDateInfo.put("PRENODENAME",attrDateInfo.getString("DIRECTION", "")+attrDateInfo.getString("PRENODENAME", ""));
            }
            attrParam.put("IBSYSID", eomsInfo.getString("IBSYSID"));
            attrParam.put("GROUP_SEQ", eomsInfo.getString("GROUP_SEQ"));
            attrParam.put("RECORD_NUM", eomsInfo.getString("RECORD_NUM"));
            attrParam.put("CLASS", "link");
            eomsLiset.add(attrParam);
            attrDateInfos.add(attrDateInfo);
        }
        // 把流出时间拼到当前节点
        if (IDataUtil.isNotEmpty(attrDateInfos)) {
            for (Object object2 : attrDateInfos) {
                IData attrdatas = (IData) object2;
                String preNodeName = attrdatas.getString("PRENODENAME", "");
                for (Object object3 : eomsLiset) {
                    IData eomss = (IData) object3;
                    String nodename = eomss.getString("NODENAME", "");
                    if (StringUtils.isNotBlank(nodename)) {
                        if (nodename.equals(preNodeName)) {
                            eomss.put("PREFINISHTIMER_1", attrdatas.getString("PREFINISHTIMER", ""));
                        }
                    }
                }
            }
        }
        IData eomsInfo = (IData) eomsLiset.get(eomsLiset.size() - 1);
        eomsInfo.put("CLASS", "level on");

        return eomsLiset;

    }

    public static IData queryEomsNodeDetail(IData map) throws Exception {

        IData params1 = new DataMap();
        params1.put("IBSYSID", map.getString("IBSYSID"));
        params1.put("RECORD_NUM", map.getString("RECORD_NUM"));
        params1.put("GROUP_SEQ", map.getString("GROUP_SEQ"));

        SQLParser parser1 = new SQLParser(params1);
        parser1.addSQL(" SELECT C.IBSYSID, ");
        parser1.addSQL(" C.ATTR_CODE, ");
        parser1.addSQL(" C.ATTR_VALUE ");
        parser1.addSQL(" FROM TF_B_EOP_ATTR C ");
        parser1.addSQL(" WHERE 1 = 1 ");
        parser1.addSQL(" AND C.IBSYSID = :IBSYSID ");
        parser1.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
        parser1.addSQL(" AND C.GROUP_SEQ = :GROUP_SEQ ");
        parser1.addSQL("  UNION ALL ");
        parser1.addSQL(" SELECT C.IBSYSID, ");
        parser1.addSQL(" C.ATTR_CODE, ");
        parser1.addSQL(" C.ATTR_VALUE ");
        parser1.addSQL(" FROM TF_BH_EOP_ATTR C ");
        parser1.addSQL(" WHERE 1 = 1 ");
        parser1.addSQL(" AND C.IBSYSID = :IBSYSID ");
        parser1.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
        parser1.addSQL(" AND C.GROUP_SEQ = :GROUP_SEQ ");
        IData attrParam = new DataMap();
        IDataset attrInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
        for (Object object2 : attrInfos) {
            IData attrInfo = (IData) object2;
            if ("ReachTimer".equals(attrInfo.getString("ATTR_CODE"))) {
                String reachTimer = attrInfo.getString("ATTR_VALUE");
                StringBuilder reachTimerValue = new StringBuilder(reachTimer);
                reachTimerValue.replace(10, 11, " ");
                attrParam.put(attrInfo.getString("ATTR_CODE").toUpperCase(), reachTimerValue);
            } else if ("PreFinishTimer".equals(attrInfo.getString("ATTR_CODE"))) {
                String reachTimer = attrInfo.getString("ATTR_VALUE");
                StringBuilder reachTimerValue = new StringBuilder(reachTimer);
                reachTimerValue.replace(10, 11, " ");
                attrParam.put(attrInfo.getString("ATTR_CODE").toUpperCase(), reachTimerValue);
            } else {
                attrParam.put(attrInfo.getString("ATTR_CODE").toUpperCase(), attrInfo.getString("ATTR_VALUE"));
            }
        }

        return attrParam;

    }

    public static IDataset queryBusiInfos(IData map, Pagination pagination) throws Exception {

        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("CITY_CODE", map.getString("CITY_CODE"));
        params.put("CUST_NAME", map.getString("CUST_NAME"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.BPM_TEMPLET_ID, ");
        parser.addSQL(" C.IBSYSID, ");
        parser.addSQL(" C.GROUP_ID, ");
        parser.addSQL(" C.CUST_NAME, ");
        parser.addSQL(" TO_CHAR(C.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        parser.addSQL(" F.CITY_CODE, ");
        parser.addSQL(" F.BUSIFORM_ID, ");
        parser.addSQL(" F.ACCEPT_STAFF_ID ");
        parser.addSQL(" FROM TF_Bh_EOP_SUBSCRIBE C, ");
        parser.addSQL("      TF_Bh_EOP_OTHER D, ");
        parser.addSQL("      TF_Bh_EWE_NODE E, ");
        parser.addSQL("      TF_Bh_EWE F ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID = D.IBSYSID ");
        parser.addSQL(" AND C.IBSYSID = F.BI_SN ");
        parser.addSQL(" AND D.NODE_ID = E.PRE_NODE_ID ");
        parser.addSQL(" AND C.BPM_TEMPLET_ID = 'FOURMANAGE' ");
        parser.addSQL(" AND F.TEMPLET_TYPE = '0' ");
        parser.addSQL(" AND D.ATTR_VALUE = '1' ");
        parser.addSQL(" AND D.ATTR_CODE = 'AUDIT_RESULT' ");
        parser.addSQL(" AND D.NODE_ID = 'infoReview' ");
        parser.addSQL(" AND E.BUSIFORM_ID = F.BUSIFORM_ID ");
        parser.addSQL("  AND (C.IBSYSID=:IBSYSID or :IBSYSID is null) ");
        parser.addSQL("  AND (C.CUST_NAME=:CUST_NAME or :CUST_NAME is null) ");
        parser.addSQL("  AND (F.CITY_CODE=:CITY_CODE or :CITY_CODE is null) ");
        parser.addSQL(" ORDER BY C.ACCEPT_TIME DESC ");
        IDataset attrInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

        if (IDataUtil.isNotEmpty(attrInfos)) {
            for (Object object : attrInfos) {
                IData busiInfo = (IData) object;
                IData params1 = new DataMap();
                String dealStaffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", busiInfo.getString("ACCEPT_STAFF_ID"));
                busiInfo.put("STAFF_NAME", dealStaffName);
                params1.put("BPM_TEMPLET_ID", busiInfo.getString("BPM_TEMPLET_ID"));

                SQLParser parser1 = new SQLParser(params1);
                parser1.addSQL(" SELECT C.BPM_TEMPLET_ID, ");
                parser1.addSQL("  C.TEMPLET_NAME, ");
                parser1.addSQL("  C.VALID_TAG ");
                parser1.addSQL("  FROM TD_B_EWE_FLOW_TEMPLET C ");
                parser1.addSQL("  WHERE 1=1 ");
                parser1.addSQL("  AND C.VALID_TAG='0' ");
                parser1.addSQL("  AND C.BPM_TEMPLET_ID= :BPM_TEMPLET_ID ");
                IDataset templetInfos = Dao.qryByParse(parser1, Route.CONN_CRM_CEN);
                if (IDataUtil.isNotEmpty(templetInfos)) {
                    busiInfo.put("TEMPLET_NAME", templetInfos.getData(0).getString("TEMPLET_NAME"));
                }
            }
        }

        return attrInfos;

    }

    public static IDataset queryEomsStateInfo(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL(" C.BUSI_STATE, ");
        parser.addSQL(" C.PRODUCT_NO, ");
        parser.addSQL(" C.RECORD_NUM, ");
        parser.addSQL(" C.SERIALNO, ");
        parser.addSQL(" C.TRADE_ID, ");
        parser.addSQL(" C.DEAL_TYPE ");
        parser.addSQL(" FROM TF_B_EOP_EOMS_STATE C ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID = :IBSYSID ");
        parser.addSQL(" ORDER BY RECORD_NUM ASC");
        IDataset attrInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(attrInfos)) {
            for (Object object : attrInfos) {
                IData stateInfo = (IData) object;
                String busiType = stateInfo.getString("BUSI_STATE");
                IData params1 = new DataMap();
                params1.put("RECORD_NUM", stateInfo.getString("RECORD_NUM"));
                params1.put("IBSYSID", stateInfo.getString("IBSYSID"));
                SQLParser parser1 = new SQLParser(params1);
                parser1.addSQL(" SELECT  T.RECORD_NUM,T.SUB_IBSYSID,T.GROUP_SEQ,T.PRODUCT_ID,T.SHEETTYPE ");
                parser1.addSQL("  FROM TF_B_EOP_EOMS T ");
                parser1.addSQL("  WHERE 1=1 ");
                parser1.addSQL("  AND T.OPER_TYPE IN ('replyWorkSheet','checkinWorkSheet') ");
                parser1.addSQL("  AND T.RECORD_NUM= :RECORD_NUM ");
                parser1.addSQL("  AND T.IBSYSID= :IBSYSID ");
                IDataset eomsInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(eomsInfos)) {
                    stateInfo.put("FLAG", "true");
                } else {
                    stateInfo.put("FLAG", "false");
                }
                if ("9".equals(busiType) || "I".equals(busiType)) {
                    stateInfo.put("FLAG", "true");
                }
                SQLParser parser2 = new SQLParser(params1);
                parser2.addSQL(" SELECT T.SHEETTYPE ");
                parser2.addSQL("  FROM TF_B_EOP_EOMS T ");
                parser2.addSQL("  WHERE 1=1 ");
                parser2.addSQL("  AND T.RECORD_NUM= :RECORD_NUM ");
                parser2.addSQL("  AND T.IBSYSID= :IBSYSID ");
                IDataset eomsInfoss = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(eomsInfoss)) {
                    stateInfo.put("SHEETTYPE", eomsInfoss.getData(0).getString("SHEETTYPE"));
                }
                IData params3 = new DataMap();
                params3.put("RECORD_NUM", stateInfo.getString("RECORD_NUM"));
                params3.put("BUSIFORM_ID", map.getString("BUSIFORM_ID"));

                SQLParser parser3 = new SQLParser(params3);
                parser3.addSQL(" SELECT T.SUB_BUSIFORM_ID ");
                parser3.addSQL("  FROM TF_B_EWE_RELE T ");
                parser3.addSQL("  WHERE 1=1 ");
                parser3.addSQL("  AND T.RELE_VALUE= :RECORD_NUM ");
                parser3.addSQL("  AND T.RELE_CODE= 'RECORD_NUM' ");
                parser3.addSQL("  AND T.BUSIFORM_ID= :BUSIFORM_ID ");
                IDataset releInfos = Dao.qryByParse(parser3, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(releInfos)) {
                    stateInfo.put("SUB_BUSIFORM_ID", releInfos.getData(0).getString("SUB_BUSIFORM_ID"));
                }

                SQLParser parser4 = new SQLParser(params1);
                parser4.addSQL(" SELECT T.SHEETTYPE ");
                parser4.addSQL("  FROM TF_B_EOP_EOMS T ");
                parser4.addSQL("  WHERE 1=1 ");
                parser4.addSQL("  AND T.RECORD_NUM= :RECORD_NUM ");
                parser4.addSQL("  AND T.IBSYSID= :IBSYSID ");
                parser4.addSQL("  AND T.OPER_TYPE= 'agreeCancelWorkSheet' ");
                IDataset eomsInfosss = Dao.qryByParse(parser4, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(eomsInfosss)) {
                    stateInfo.put("AGREECANCEL_SIZE", eomsInfosss.size());
                }
            }
        }
        // 去掉状态是已撤单的专线
        Iterator<Object> attrInfo = attrInfos.iterator();
        while (attrInfo.hasNext()) {
            IData attrData = (IData) attrInfo.next();
            String StateType = attrData.getString("BUSI_STATE");
            if ("C".equals(StateType)) {
                attrInfo.remove();
            }

        }
        return attrInfos;
    }

    public static IDataset getTimerTaskWorkformNewAttrInfo(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL(" E.ATTR_CODE, ");
        parser.addSQL(" E.ATTR_VALUE ");
        parser.addSQL(" FROM TF_Bh_EOP_SUBSCRIBE C, ");
        parser.addSQL("      TF_Bh_EOP_OTHER D, ");
        parser.addSQL("      TF_Bh_EOP_ATTR E ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID like :IBSYSID || '%' ");
        parser.addSQL(" AND C.IBSYSID = D.IBSYSID ");
        parser.addSQL(" AND E.IBSYSID = D.IBSYSID ");
        parser.addSQL(" AND E.NODE_ID = D.NODE_ID ");
        parser.addSQL(" AND ( (D.NODE_ID='infoReview' and D.ATTR_CODE='AUDIT_RESULT' and D.ATTR_VALUE='1' ) OR ");
        parser.addSQL(" (D.NODE_ID='infoApprove' and D.ATTR_CODE='AUDIT_RESULT' and D.ATTR_VALUE='1' and C.BPM_TEMPLET_ID='TIMERREVIEWFOURMANAGE') OR ");
        parser.addSQL(" (D.NODE_ID='infoPrvApprove' and D.ATTR_CODE='AUDIT_RESULT' and D.ATTR_VALUE='1') ");
        parser.addSQL("  ) ");

        IDataset attrInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        IDataset newAttrListInfos = new DatasetList();
        IData newAttrListInfo = new DataMap();
        if (IDataUtil.isNotEmpty(attrInfos)) {
            for (Object object2 : attrInfos) {
                IData eopAttrInfo = (IData) object2;
                String attrCode = eopAttrInfo.getString("ATTR_CODE");
                String attrValue = eopAttrInfo.getString("ATTR_VALUE");
                newAttrListInfo.put(attrCode, attrValue);
            }
        }
        newAttrListInfos.add(newAttrListInfo);

        return newAttrListInfos;
    }

    public static IDataset queryCheckRecordInfos(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        if ("CHECK".equals(map.getString("FLAG"))) {
            params.put("CHECK_TYPE", "1");
        } else if ("AUDIT".equals(map.getString("FLAG"))) {
            params.put("CHECK_TYPE", "2");
        }

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT E.CHECK_ID, ");
        parser.addSQL(" E.IBSYSID, ");
        parser.addSQL(" E.CHECK_TYPE, ");
        parser.addSQL(" E.BUSI_TYPE, ");
        parser.addSQL(" E.CHECK_STAFF_ID, ");
        parser.addSQL(" TO_CHAR(E.CHECK_TIME, 'yyyy-mm-dd') CHECK_TIME, ");
        parser.addSQL(" E.CHECK_RESULT, ");
        parser.addSQL(" TO_CHAR(E.INSERT_TIME, 'yyyy-mm-dd') INSERT_TIME, ");
        parser.addSQL(" TO_CHAR(E.UPDATE_TIME, 'yyyy-mm-dd') UPDATE_TIME, ");
        parser.addSQL(" E.REMARK, ");
        parser.addSQL(" E.RSRV_STR1, ");
        parser.addSQL(" E.RSRV_STR2, ");
        parser.addSQL(" E.RSRV_STR3 ");
        parser.addSQL(" FROM TF_B_EOP_BUSI_CHECK_RECORD E ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND E.IBSYSID = :IBSYSID ");
        parser.addSQL(" AND E.BUSI_TYPE = 'FOURMANAGE' ");
        parser.addSQL(" AND E.CHECK_TYPE = :CHECK_TYPE ");

        IDataset attrInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        return attrInfos;
    }

    public static void updateCheckRecordInfo(IData map) throws Exception {
        IData params = new DataMap();
        params.put("CHECK_RESULT", map.getString("CHECK_RESULT"));
        params.put("REMARK", map.getString("REMARK"));
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("CHECK_ID", map.getString("CHECK_ID"));
        params.put("UPDATE_TIME", SysDateMgr.getSysDate());

        StringBuilder parser = new StringBuilder();
        parser.append(" UPDATE TF_B_EOP_BUSI_CHECK_RECORD SET ");
        parser.append(" CHECK_RESULT=:CHECK_RESULT, ");
        parser.append(" UPDATE_TIME=to_date(:UPDATE_TIME,'yyyy-mm-dd'), ");
        parser.append(" REMARK=:REMARK ");
        parser.append(" WHERE ");
        parser.append(" CHECK_ID=:CHECK_ID ");

        Dao.executeUpdate(parser, params, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static void addCheckRecordInfo(IData map) throws Exception {
        IData params = new DataMap();

        params.put("CHECK_ID", SeqMgr.getAttrSeq());
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("CHECK_TYPE", map.getString("CHECK_TYPE"));
        params.put("BUSI_TYPE", map.getString("BUSI_TYPE"));
        params.put("CHECK_RESULT", map.getString("CHECK_RESULT"));
        params.put("CHECK_TIME", SysDateMgr.getSysDate());
        params.put("CHECK_STAFF_ID", map.getString("CHECK_STAFF_ID"));
        params.put("INSERT_TIME", SysDateMgr.getSysDate());
        params.put("REMARK", map.getString("REMARK"));

        Dao.insert("TF_B_EOP_BUSI_CHECK_RECORD", params, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryTimerTaskWorkformInfos(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT D.SUB_IBSYSID, ");
        parser.addSQL(" E.IBSYSID, ");
        parser.addSQL(" E.BPM_TEMPLET_ID, ");
        parser.addSQL(" E.BUSI_CODE ");
        parser.addSQL(" FROM TF_B_EOP_SUBSCRIBE E, ");
        parser.addSQL("      TF_B_EOP_OTHER D ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND D.IBSYSID = E.IBSYSID ");
        parser.addSQL(" AND E.BPM_TEMPLET_ID IN ('FOURMANAGE','FOURMANAGE') ");
        parser.addSQL(" AND D.ATTR_VALUE = '2' ");
        parser.addSQL(" AND D.ATTR_CODE = 'AUDIT_RESULT' ");
        parser.addSQL(" AND D.NODE_ID = 'infoReview' ");
        parser.addSQL(" AND (:IBSYSID IS NULL OR E.IBSYSID=:IBSYSID) ");

        IDataset otherInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

        return otherInfos;
    }

    public static IData getTimerTaskWorkformNewAttr(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL(" C.BPM_TEMPLET_ID, ");
        parser.addSQL(" C.CUST_NAME, ");
        parser.addSQL(" C.GROUP_ID, ");
        parser.addSQL(" C.RSRV_STR3, ");
        parser.addSQL(" C.RSRV_STR4, ");
        parser.addSQL(" E.ATTR_CODE, ");
        parser.addSQL(" E.ATTR_VALUE, ");
        parser.addSQL(" E.ATTR_NAME, ");
        parser.addSQL(" E.RECORD_NUM ");
        parser.addSQL(" FROM TF_B_EOP_SUBSCRIBE C, ");
        parser.addSQL("      TF_B_EOP_OTHER D, ");
        parser.addSQL("      TF_B_EOP_ATTR E ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID like :IBSYSID || '%' ");
        parser.addSQL(" AND C.IBSYSID = D.IBSYSID ");
        parser.addSQL(" AND E.IBSYSID = D.IBSYSID ");
        parser.addSQL(" AND E.NODE_ID = D.NODE_ID ");
        parser.addSQL(" AND ( (D.NODE_ID='infoReview' and D.ATTR_CODE='AUDIT_RESULT' and D.ATTR_VALUE='2' ) OR ");
        parser.addSQL(" (D.NODE_ID='infoApprove' and D.ATTR_CODE='AUDIT_RESULT' and D.ATTR_VALUE='2' and C.BPM_TEMPLET_ID='TIMERREVIEWFOURMANAGE') OR ");
        parser.addSQL(" (D.NODE_ID='infoPrvApprove' and D.ATTR_CODE='AUDIT_RESULT' and D.ATTR_VALUE='2') ");
        parser.addSQL("  ) ");

        IDataset attrInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        IData paramData = new DataMap();
        // 拼DIRECTLINE_DATA数据
        IDataset directlineData = new DatasetList();
        IDataset eomsAttrlist = new DatasetList();
        if (IDataUtil.isNotEmpty(attrInfos)) {
            IData params2 = new DataMap();
            params2.put("IBSYSID", map.getString("IBSYSID"));

            SQLParser parser2 = new SQLParser(params2);
            parser2.addSQL(" SELECT C.IBSYSID, ");
            parser2.addSQL(" C.ATTR_CODE, ");
            parser2.addSQL(" C.ATTR_VALUE, ");
            parser2.addSQL(" C.ATTR_NAME, ");
            parser2.addSQL(" C.RECORD_NUM ");
            parser2.addSQL(" FROM TF_B_EOP_ATTR C ");
            parser2.addSQL(" WHERE 1 = 1 ");
            parser2.addSQL(" AND C.IBSYSID = :IBSYSID ");
            parser2.addSQL(" AND C.NODE_ID='apply' ");
            IDataset attrInfosS = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));
            IDataset eopattrList = new DatasetList();
            for (Object object2 : attrInfosS) {
                IData eopAttrInfo = (IData) object2;
                if ("1".equals(eopAttrInfo.getString("RECORD_NUM"))) {
                    IData attrInfo = new DataMap();
                    String attrCode = eopAttrInfo.getString("ATTR_CODE");
                    String attrValue = eopAttrInfo.getString("ATTR_VALUE");
                    String attrName = eopAttrInfo.getString("ATTR_NAME");
                    attrInfo.put("ATTR_CODE", "pattr_" + attrCode);
                    attrInfo.put("ATTR_VALUE", attrValue);
                    attrInfo.put("ATTR_NAME", attrName);
                    eopattrList.add(attrInfo);
                }
                if ("0".equals(eopAttrInfo.getString("RECORD_NUM"))) {
                    IData attrInfo = new DataMap();
                    String attrCode = eopAttrInfo.getString("ATTR_CODE");
                    String attrValue = eopAttrInfo.getString("ATTR_VALUE");
                    String attrName = eopAttrInfo.getString("ATTR_NAME");
                    attrInfo.put("ATTR_CODE", attrCode);
                    attrInfo.put("ATTR_VALUE", attrValue);
                    attrInfo.put("ATTR_NAME", attrName);
                    eomsAttrlist.add(attrInfo);
                }
            }

            directlineData.add(eopattrList);
            paramData.put("DIRECTLINE_DATA", directlineData);// 专线属性
            paramData.put("EOMS_ATTR_LIST", eomsAttrlist);// 公共属性
            // 拼BUSI_SPEC_RELE数据
            String bmTempId = attrInfos.getData(0).getString("BPM_TEMPLET_ID");
            IData params1 = new DataMap();
            params1.put("BPM_TEMPLET_ID", bmTempId);
            SQLParser parser1 = new SQLParser(params1);
            parser1.addSQL(" SELECT C.BUSIFORM_OPER_TYPE, ");
            parser1.addSQL("        C.BPM_TEMPLET_ID, ");
            parser1.addSQL("        C.BUSI_CODE, ");
            parser1.addSQL("        C.BUSI_TYPE, ");
            parser1.addSQL("        C.CONDITION_ID, ");
            parser1.addSQL("        TO_CHAR(C.CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss') CREATE_DATE, ");
            parser1.addSQL("        C.IN_MODE_CODE, ");
            parser1.addSQL("        C.REMARK, ");
            parser1.addSQL("        C.BPM_TEMPLET_ID TEMPLET_ID, ");
            parser1.addSQL("        TO_CHAR(C.UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss') UPDATE_DATE, ");
            parser1.addSQL("        C.VALID_TAG ");
            parser1.addSQL(" FROM TD_B_EWE_BUSI_SPEC_RELE C ");
            parser1.addSQL(" WHERE 1 = 1 ");
            parser1.addSQL(" AND C.BPM_TEMPLET_ID = :BPM_TEMPLET_ID ");

            IDataset busiSpecReleInfos = Dao.qryByParse(parser1, Route.CONN_CRM_CEN);
            if (IDataUtil.isNotEmpty(busiSpecReleInfos)) {
                paramData.put("BUSI_SPEC_RELE", busiSpecReleInfos.getData(0));
            }
            // 拼CUST_DATA数据
            IData custData = new DataMap();
            custData.put("GROUP_ID", attrInfos.getData(0).getString("GROUP_ID"));
            custData.put("CUST_NAME", attrInfos.getData(0).getString("CUST_NAME"));
            paramData.put("CUST_DATA", custData);
            // 拼CUST_DATA数据
            IData commonData = new DataMap();
            commonData.put("OFFER_ID", "110000009983");
            paramData.put("COMMON_DATA", commonData);
            // 拼NODE_TEMPLETE数据
            IData nodeTemplete = new DataMap();
            nodeTemplete.put("BPM_TEMPLET_ID", bmTempId);
            nodeTemplete.put("NODE_ID", "apply");
            paramData.put("NODE_TEMPLETE", nodeTemplete);
            // 拼OFFER_DATA数据
            IData offerData = new DataMap();
            offerData.put("OFFER_CODE", "9983");
            offerData.put("OFFER_ID", "110000009983");
            offerData.put("OFFER_NAME", "国内400（商品）");
            offerData.put("OFFER_TYPE", "P");
            offerData.put("OPER_CODE", "0");
            paramData.put("OFFER_DATA", offerData);
            // 拼ORDER_DATA数据
            IData orderData = new DataMap();
            String urgencyLevel = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[] { "URGENCY_LEVEL", attrInfos.getData(0).getString("RSRV_STR3") });
            orderData.put("TITLE", attrInfos.getData(0).getString("RSRV_STR4"));
            orderData.put("URGENCY_LEVEL", urgencyLevel);
            paramData.put("ORDER_DATA", orderData);
        }
        return paramData;
    }

    public static IDataset queryDestroyAttrInfoList(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT C.RECORD_NUM, ");
        parser.addSQL(" D.SUB_IBSYSID, ");
        parser.addSQL(" D.NODE_ID ");
        parser.addSQL(" FROM TF_B_EOP_EOMS_STATE C, ");
        parser.addSQL("      TF_B_EOP_EOMS D ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND C.IBSYSID = :IBSYSID ");
        parser.addSQL(" AND C.IBSYSID = D.IBSYSID ");
        parser.addSQL(" AND D.OPER_TYPE = 'newWorkSheet' ");

        IDataset eomsInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

        IDataset attrList = new DatasetList();
        if (IDataUtil.isNotEmpty(eomsInfos)) {
            for (Object object : eomsInfos) {
                IData eomsInfo = (IData) object;
                IData params1 = new DataMap();
                params1.put("SUB_IBSYSID", eomsInfo.getString("SUB_IBSYSID"));
                params1.put("NODE_ID", eomsInfo.getString("NODE_ID"));
                params1.put("RECORD_NUM", eomsInfo.getString("RECORD_NUM"));

                SQLParser parser1 = new SQLParser(params1);
                parser1.addSQL(" SELECT C.RECORD_NUM, ");
                parser1.addSQL(" C.ATTR_CODE, ");
                parser1.addSQL(" C.ATTR_VALUE ");
                parser1.addSQL(" FROM TF_B_EOP_ATTR C ");
                parser1.addSQL(" WHERE 1 = 1 ");
                parser1.addSQL(" AND C.SUB_IBSYSID = :SUB_IBSYSID ");
                parser1.addSQL(" AND C.NODE_ID = :NODE_ID ");
                parser1.addSQL(" AND C.RECORD_NUM = :RECORD_NUM ");
                IDataset afttrInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(afttrInfos)) {
                    IData newAttrListInfo = new DataMap();
                    for (Object object2 : afttrInfos) {
                        IData eopAttrInfo = (IData) object2;
                        String attrCode = eopAttrInfo.getString("ATTR_CODE");
                        String attrValue = eopAttrInfo.getString("ATTR_VALUE");
                        newAttrListInfo.put(attrCode, attrValue);
                    }
                    newAttrListInfo.put("RECORD_NUM", eomsInfo.getString("RECORD_NUM"));
                    newAttrListInfo.put("SUB_IBSYSID", eomsInfo.getString("SUB_IBSYSID"));
                    attrList.add(newAttrListInfo);
                }
            }
        }

        return attrList;
    }

    public static void updateEweStepByState(IData map) throws Exception {
        IData params = new DataMap();
        params.put("BUSIFORM_NODE_ID", map.getString("BUSIFORM_NODE_ID"));
        params.put("BUSIFORM_ID", map.getString("BUSIFORM_ID"));
        params.put("STATE", map.getString("STATE"));
        params.put("UPDATE_TIME", SysDateMgr.getLastSecond(SysDateMgr.getSysTime()));

        StringBuilder parser = new StringBuilder();
        parser.append(" UPDATE TF_B_EWE_STEP SET ");
        parser.append(" STATE='0', ");
        parser.append(" UPDATE_DATE=to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        parser.append(" WHERE ");
        parser.append(" BUSIFORM_NODE_ID=:BUSIFORM_NODE_ID ");
        parser.append(" AND BUSIFORM_ID=:BUSIFORM_ID ");
        parser.append(" AND STATE=:STATE ");

        Dao.executeUpdate(parser, params, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static void insertAttachList(IData attach) throws Exception {

        IData wfAttach = new DataMap();
        wfAttach.put("IBSYSID", attach.getString("IBSYSID"));
        wfAttach.put("SEQ", SeqMgr.getAttrSeq());
        wfAttach.put("SUB_IBSYSID", attach.getString("SUB_IBSYSID"));
        wfAttach.put("NODE_ID", attach.getString("NODE_ID"));
        wfAttach.put("DISPLAY_NAME", attach.getString("FILE_NAME"));
        wfAttach.put("ATTACH_NAME", attach.getString("FILE_NAME"));
        wfAttach.put("VALID_TAG", EcEsopConstants.STATE_VALID);
        wfAttach.put("FILE_ID", attach.getString("FILE_ID"));
        wfAttach.put("ATTACH_TYPE", attach.getString("ATTACH_TYPE"));
        wfAttach.put("ATTACH_CITY_CODE", CSBizBean.getVisit().getCityCode());
        wfAttach.put("ATTACH_EPARCHY_CODE", CSBizBean.getVisit().getLoginEparchyCode());
        wfAttach.put("ATTACH_DEPART_ID", CSBizBean.getVisit().getDepartId());
        wfAttach.put("ATTACH_DEPART_NAME", CSBizBean.getVisit().getDepartName());
        wfAttach.put("ATTACH_STAFF_ID", CSBizBean.getVisit().getStaffId());
        wfAttach.put("ATTACH_STAFF_NAME", CSBizBean.getVisit().getStaffName());
        wfAttach.put("ATTACH_STAFF_PHONE", CSBizBean.getVisit().getSerialNumber());
        wfAttach.put("RECORD_NUM", "0");
        wfAttach.put("GROUP_SEQ", "0");
        wfAttach.put("REMARK", attach.getString("REMARK"));
        wfAttach.put("ATTACH_LENGTH", attach.getString("FILE_SIZE"));
        wfAttach.put("UPDATE_TIME", SysDateMgr.getSysTime());
        wfAttach.put("INSERT_TIME", SysDateMgr.getSysTime());
        wfAttach.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());

        Dao.insert("TF_B_EOP_ATTACH", wfAttach, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryReadSourceconfirm(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        IDataset otherInfos = new DatasetList();

        SQLParser parser1 = new SQLParser(params);
        parser1.addSQL(" SELECT R.* FROM (SELECT T.SHEETTYPE,T.RECORD_NUM,ROW_NUMBER() ");
        parser1.addSQL(" OVER(PARTITION BY T.RECORD_NUM ORDER BY T.RECORD_NUM DESC) G ");
        parser1.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID=:IBSYSID ) R ");
        parser1.addSQL(" WHERE R.G<=1 ");

        IDataset sheettypeInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));

        if (IDataUtil.isNotEmpty(sheettypeInfos)) {
            String sheetType = sheettypeInfos.first().getString("SHEETTYPE");
            if ("31".equals(sheetType) || "35".equals(sheetType)) {
                SQLParser parser = new SQLParser(params);
                parser.addSQL(" SELECT T.ATTR_CODE,T.ATTR_NAME,T.ATTR_VALUE ");
                parser.addSQL(" FROM TF_B_EOP_ATTR T, ");
                parser.addSQL(" (SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.SUB_IBSYSID, ");
                parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G ");
                parser.addSQL(" FROM TF_B_EOP_EOMS T  WHERE  T.OPER_TYPE = 'replyWorkSheet' AND T.IBSYSID=:IBSYSID ) R");
                parser.addSQL(" WHERE 1=1 ");
                parser.addSQL(" AND T.SUB_IBSYSID = R.SUB_IBSYSID ");
                parser.addSQL(" AND T.GROUP_SEQ = R.GROUP_SEQ ");
                parser.addSQL(" AND T.RECORD_NUM = R.RECORD_NUM ");
                parser.addSQL(" AND R.G<=1 ");
                otherInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
            } else {
                for (Object object : sheettypeInfos) {
                    IData shetypeInfo = (IData) object;
                    IData params1 = new DataMap();
                    params1.put("IBSYSID", map.getString("IBSYSID"));
                    params1.put("RECORD_NUM", shetypeInfo.getString("RECORD_NUM"));
                    SQLParser parser = new SQLParser(params1);
                    parser.addSQL(" SELECT T.ATTR_CODE,T.ATTR_NAME,T.ATTR_VALUE ");
                    parser.addSQL(" FROM TF_B_EOP_ATTR T, ");
                    parser.addSQL(" (SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.SUB_IBSYSID, ");
                    parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G ");
                    parser.addSQL(" FROM TF_B_EOP_EOMS T  WHERE  T.OPER_TYPE = 'replyWorkSheet' AND T.IBSYSID=:IBSYSID AND T.RECORD_NUM=:RECORD_NUM  ) R");
                    parser.addSQL(" WHERE T.SUB_IBSYSID = R.SUB_IBSYSID ");
                    parser.addSQL(" AND T.GROUP_SEQ = R.GROUP_SEQ ");
                    parser.addSQL(" AND T.RECORD_NUM = R.RECORD_NUM ");
                    parser.addSQL(" AND R.G<=1 ");
                    IDataset eomsInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
                    otherInfos.addAll(eomsInfos);
                }
            }
        }

        return otherInfos;
    }

    public static IDataset qryEomsStateInfos(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT W.RECORD_NUM FROM ( ");
        parser.addSQL(" SELECT T.RECORD_NUM ");
        parser.addSQL(" FROM TF_B_EOP_ATTR T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.IBSYSID = :IBSYSID ");
        parser.addSQL(" GROUP BY T.RECORD_NUM  ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT T.RECORD_NUM ");
        parser.addSQL(" FROM TF_BH_EOP_ATTR T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.IBSYSID = :IBSYSID ");
        parser.addSQL(" GROUP BY T.RECORD_NUM ");
        parser.addSQL(" ) W ORDER BY W.RECORD_NUM ASC  ");

        IDataset recordNumInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

        return recordNumInfos;
    }

    public static IDataset querydataLineAttrInfoList(IData param) throws Exception {

        IDataset attrInfos = new DatasetList();
        // 获取派送资管的最新的派单信息
        if ("1".equals(param.getString("FLAG"))) {

            IData params1 = new DataMap();
            params1.put("IBSYSID", param.getString("IBSYSID"));
            params1.put("RECORD_NUM", param.getString("RECORD_NUM"));
            // 获取最新的GROUP_SEQ
            SQLParser parser3 = new SQLParser(params1);
            parser3.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID,R.SUB_IBSYSID  FROM ");
            parser3.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID,T.SUB_IBSYSID, ");
            parser3.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
            parser3.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'renewWorkSheet') R ");
            parser3.addSQL(" WHERE 1=1 ");
            parser3.addSQL(" AND R.G<=1 ");
            parser3.addSQL(" UNION ALL ");
            parser3.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID,R.SUB_IBSYSID  FROM ");
            parser3.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID,T.SUB_IBSYSID, ");
            parser3.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
            parser3.addSQL(" FROM TF_BH_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'renewWorkSheet') R ");
            parser3.addSQL(" WHERE 1=1 ");
            parser3.addSQL(" AND R.G<=1 ");
            IDataset eomsInfos = Dao.qryByParse(parser3, Route.getJourDb(Route.CONN_CRM_CG));
            String groupSeq = "";
            String subIbsysid = param.getString("SUB_IBSYSID", "");
            if (IDataUtil.isNotEmpty(eomsInfos)) {
                groupSeq = eomsInfos.getData(0).getString("GROUP_SEQ");
                subIbsysid = eomsInfos.getData(0).getString("SUB_IBSYSID");
            } else {
                SQLParser parser4 = new SQLParser(params1);
                parser4.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID,R.SUB_IBSYSID  FROM ");
                parser4.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID,T.SUB_IBSYSID, ");
                parser4.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
                parser4.addSQL(" FROM TF_B_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'newWorkSheet') R ");
                parser4.addSQL(" WHERE 1=1 ");
                parser4.addSQL(" AND R.G<=1 ");
                parser4.addSQL(" UNION ALL ");
                parser4.addSQL(" SELECT R.GROUP_SEQ,R.NODE_ID,R.RECORD_NUM,R.IBSYSID,R.SUB_IBSYSID  FROM ");
                parser4.addSQL(" ( SELECT T.GROUP_SEQ,T.IBSYSID,T.RECORD_NUM,T.NODE_ID,T.SUB_IBSYSID, ");
                parser4.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.GROUP_SEQ DESC) G  ");
                parser4.addSQL(" FROM TF_BH_EOP_EOMS T WHERE T.IBSYSID =:IBSYSID AND T.RECORD_NUM =:RECORD_NUM  AND T.OPER_TYPE = 'newWorkSheet') R ");
                parser4.addSQL(" WHERE 1=1 ");
                parser4.addSQL(" AND R.G<=1 ");
                IDataset eomsInfosss = Dao.qryByParse(parser4, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(eomsInfosss)) {
                    groupSeq = eomsInfosss.getData(0).getString("GROUP_SEQ");
                    subIbsysid = eomsInfosss.getData(0).getString("SUB_IBSYSID");
                }
            }

            IData params2 = new DataMap();
            params2.put("IBSYSID", param.getString("IBSYSID"));
            params2.put("RECORD_NUM", param.getString("RECORD_NUM"));
            params2.put("GROUP_SEQ", groupSeq);
            params2.put("SUB_IBSYSID", subIbsysid);

            SQLParser parser2 = new SQLParser(params2);
            parser2.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, ");
            parser2.addSQL(" T.NODE_ID, ");
            parser2.addSQL(" T.GROUP_SEQ, ");
            parser2.addSQL(" T.ATTR_CODE, ");
            parser2.addSQL(" T.ATTR_NAME, ");
            parser2.addSQL(" T.ATTR_VALUE, ");
            parser2.addSQL(" T.UPDATE_TIME, ");
            parser2.addSQL(" T.RECORD_NUM ");
            parser2.addSQL(" FROM TF_B_EOP_ATTR T ");
            parser2.addSQL(" WHERE 1=1 ");
            parser2.addSQL(" AND T.RECORD_NUM =:RECORD_NUM ");
            parser2.addSQL(" AND T.GROUP_SEQ =:GROUP_SEQ ");
            parser2.addSQL(" AND T.IBSYSID =:IBSYSID ");
            parser2.addSQL(" AND T.SUB_IBSYSID =:SUB_IBSYSID ");
            parser2.addSQL(" UNION ALL ");
            parser2.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, ");
            parser2.addSQL(" T.NODE_ID, ");
            parser2.addSQL(" T.GROUP_SEQ, ");
            parser2.addSQL(" T.ATTR_CODE, ");
            parser2.addSQL(" T.ATTR_NAME, ");
            parser2.addSQL(" T.ATTR_VALUE, ");
            parser2.addSQL(" T.UPDATE_TIME, ");
            parser2.addSQL(" T.RECORD_NUM ");
            parser2.addSQL(" FROM TF_BH_EOP_ATTR T ");
            parser2.addSQL(" WHERE 1=1 ");
            parser2.addSQL(" AND T.RECORD_NUM =:RECORD_NUM ");
            parser2.addSQL(" AND T.GROUP_SEQ =:GROUP_SEQ ");
            parser2.addSQL(" AND T.IBSYSID =:IBSYSID ");
            parser2.addSQL(" AND T.SUB_IBSYSID =:SUB_IBSYSID ");
            attrInfos = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));

        } else {

            IData params = new DataMap();
            params.put("RECORD_NUM", param.getString("RECORD_NUM"));
            params.put("NODE_ID", param.getString("NODE_ID"));
            params.put("IBSYSID", param.getString("IBSYSID"));
            params.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
            SQLParser parser = new SQLParser(params);
            parser.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, ");
            parser.addSQL(" T.GROUP_SEQ, ");
            parser.addSQL(" T.SEQ, ");
            parser.addSQL(" T.ATTR_CODE, ");
            parser.addSQL(" T.ATTR_NAME, ");
            parser.addSQL(" T.ATTR_VALUE, ");
            parser.addSQL(" T.UPDATE_TIME, ");
            parser.addSQL(" T.RECORD_NUM, ");
            parser.addSQL(" T.ACCEPT_MONTH ");
            parser.addSQL(" FROM TF_B_EOP_ATTR T ");
            parser.addSQL(" WHERE 1=1 ");
            parser.addSQL(" AND T.RECORD_NUM =:RECORD_NUM ");
            parser.addSQL(" AND T.NODE_ID =:NODE_ID ");
            parser.addSQL(" AND T.IBSYSID =:IBSYSID ");
            parser.addSQL(" AND T.SUB_IBSYSID =:SUB_IBSYSID ");
            parser.addSQL(" UNION ALL ");
            parser.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, ");
            parser.addSQL(" T.GROUP_SEQ, ");
            parser.addSQL(" T.SEQ, ");
            parser.addSQL(" T.ATTR_CODE, ");
            parser.addSQL(" T.ATTR_NAME, ");
            parser.addSQL(" T.ATTR_VALUE, ");
            parser.addSQL(" T.UPDATE_TIME, ");
            parser.addSQL(" T.RECORD_NUM, ");
            parser.addSQL(" T.ACCEPT_MONTH ");
            parser.addSQL(" FROM TF_BH_EOP_ATTR T ");
            parser.addSQL(" WHERE 1=1 ");
            parser.addSQL(" AND T.RECORD_NUM =:RECORD_NUM ");
            parser.addSQL(" AND T.NODE_ID =:NODE_ID ");
            parser.addSQL(" AND T.IBSYSID =:IBSYSID ");
            parser.addSQL(" AND T.SUB_IBSYSID =:SUB_IBSYSID ");
            attrInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        }

        return attrInfos;
    }

    public static IDataset queryEopotherInfos(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("NODE_ID", map.getString("NODE_ID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT * FROM ");
        parser.addSQL(" (SELECT T.SUB_IBSYSID,T.IBSYSID,T.ATTR_CODE,T.ATTR_NAME,T.ATTR_VALUE, ");
        parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.SUB_IBSYSID DESC) G ");
        parser.addSQL(" FROM TF_B_EOP_OTHER T  WHERE T.IBSYSID=:IBSYSID AND NODE_ID=:NODE_ID ) R ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND R.G<=1 ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT * FROM ");
        parser.addSQL(" (SELECT T.SUB_IBSYSID,T.IBSYSID,T.ATTR_CODE,T.ATTR_NAME,T.ATTR_VALUE, ");
        parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.IBSYSID ORDER BY T.SUB_IBSYSID DESC) G ");
        parser.addSQL(" FROM TF_BH_EOP_OTHER T  WHERE T.IBSYSID=:IBSYSID  AND NODE_ID=:NODE_ID ) R ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND R.G<=1 ");
        IDataset otherInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        IDataset otherInfoss = new DatasetList();
        if (IDataUtil.isNotEmpty(otherInfos)) {
            String subIbsysId = otherInfos.getData(0).getString("SUB_IBSYSID");
            IData params1 = new DataMap();
            params1.put("SUB_IBSYSID", subIbsysId);

            SQLParser parser1 = new SQLParser(params1);
            parser1.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, ");
            parser1.addSQL(" T.ATTR_CODE, ");
            parser1.addSQL(" T.ATTR_NAME, ");
            parser1.addSQL(" T.ATTR_VALUE ");
            parser1.addSQL(" FROM TF_B_EOP_OTHER T ");
            parser1.addSQL(" WHERE 1=1 ");
            parser1.addSQL(" AND SUB_IBSYSID=:SUB_IBSYSID ");
            parser1.addSQL(" UNION ALL ");
            parser1.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, ");
            parser1.addSQL(" T.ATTR_CODE, ");
            parser1.addSQL(" T.ATTR_NAME, ");
            parser1.addSQL(" T.ATTR_VALUE ");
            parser1.addSQL(" FROM TF_BH_EOP_OTHER T ");
            parser1.addSQL(" WHERE 1=1 ");
            parser1.addSQL(" AND SUB_IBSYSID=:SUB_IBSYSID ");
            otherInfoss = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
        }
        return otherInfoss;
    }

    public static void updateSurveyUnbundie(IData map) throws Exception {
        IData params = new DataMap();
        params.put("PRODUCTNO", map.getString("PRODUCTNO"));
        params.put("STATE", map.getString("STATE"));
        params.put("UPDATE_TIME", SysDateMgr.getLastSecond(SysDateMgr.getSysTime()));
        // 先查询是否存在，再修改
        SQLParser parsers = new SQLParser(params);
        parsers.addSQL(" SELECT T.STATE,");
        parsers.addSQL(" T.POOL_VALUE, ");
        parsers.addSQL(" T.POOL_CODE ");
        parsers.addSQL(" FROM TF_B_EOP_SUBSCRIBE_POOL T ");
        parsers.addSQL(" WHERE 1=1 ");
        parsers.addSQL(" AND T.POOL_VALUE =:PRODUCTNO ");
        parsers.addSQL(" AND T.STATE =:STATE ");

        IDataset subscribePoolInfos = Dao.qryByParse(parsers, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(subscribePoolInfos)) {
            StringBuilder parser = new StringBuilder();
            parser.append(" UPDATE TF_B_EOP_SUBSCRIBE_POOL SET ");
            parser.append(" STATE='F', ");
            parser.append(" UPDATE_DATE=to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') ");
            parser.append(" WHERE ");
            parser.append(" POOL_VALUE=:PRODUCTNO ");
            parser.append(" AND STATE=:STATE ");

            Dao.executeUpdate(parser, params, Route.getJourDb(Route.CONN_CRM_CG));
        }

    }

    public static IDataset queryAttachList(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT E.FILE_ID, ");
        parser.addSQL(" E.IBSYSID, ");
        parser.addSQL(" E.ATTACH_TYPE, ");
        parser.addSQL(" E.ATTACH_NAME ");
        parser.addSQL(" FROM TF_B_EOP_ATTACH E ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND E.IBSYSID = :IBSYSID ");
        parser.addSQL(" AND E.FILE_ID = :FILE_ID ");
        parser.addSQL(" AND E.ATTACH_TYPE = :ATTACH_TYPE ");

        IDataset otherInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

        return otherInfos;
    }

    // 查询附件
    public static IDataset qryGroupAttach(IData params) throws Exception {

        IData param = new DataMap();
        param.put("IBSYSID", params.getString("IBSYSID"));

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM ( ");
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL(" C.SEQ, ");
        parser.addSQL(" C.GROUP_SEQ, ");
        parser.addSQL(" C.SUB_IBSYSID, ");
        parser.addSQL(" C.NODE_ID, ");
        parser.addSQL(" C.DISPLAY_NAME, ");
        parser.addSQL(" C.ATTACH_NAME, ");
        parser.addSQL(" C.ATTACH_LENGTH, ");
        parser.addSQL(" C.ATTACH_URL, ");
        parser.addSQL(" C.ATTACH_LOCAL_PATH, ");
        parser.addSQL(" C.ATTACH_CITY_CODE, ");
        parser.addSQL(" C.ATTACH_EPARCHY_CODE, ");
        parser.addSQL(" C.ATTACH_DEPART_ID, ");
        parser.addSQL(" C.ATTACH_DEPART_NAME, ");
        parser.addSQL(" C.ATTACH_STAFF_ID, ");
        parser.addSQL(" C.ATTACH_STAFF_NAME, ");
        parser.addSQL(" C.ATTACH_STAFF_PHONE, ");
        parser.addSQL(" C.FILE_ID, ");
        parser.addSQL(" TO_CHAR(C.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" C.REMARK, ");
        parser.addSQL(" TO_CHAR(C.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" C.ACCEPT_MONTH, ");
        parser.addSQL(" C.VALID_TAG, ");
        parser.addSQL(" C.ATTACH_TYPE, ");
        parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY C.FILE_ID ORDER BY C.GROUP_SEQ DESC) G ");
        parser.addSQL(" FROM TF_B_EOP_ATTACH C WHERE C.IBSYSID=:IBSYSID AND  C.ATTACH_TYPE <> 'Z' ) R ");
        // 判断是客户经理节点，只展示最新的附件
        if ("true".equals(params.getString("CUSTMG", ""))) {
            parser.addSQL(" ,(SELECT * FROM (SELECT MAX(GROUP_SEQ) GROUP_SEQ FROM TF_B_EOP_ATTACH E  ");
            parser.addSQL(" WHERE  E.IBSYSID=:IBSYSID AND  E.ATTACH_TYPE <> 'Z' ");
            parser.addSQL(" GROUP BY GROUP_SEQ order by GROUP_SEQ desc ) T WHERE  ROWNUM=1 ) W ");
            parser.addSQL(" WHERE 1 = 1 ");
            parser.addSQL(" AND R.G<=1 ");
            parser.addSQL(" AND R.GROUP_SEQ = W.GROUP_SEQ ");
        } else {
            parser.addSQL(" WHERE 1 = 1 ");
            parser.addSQL(" AND R.G<=1 ");
        }

        IDataset attachInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

        return attachInfos;
    }

    public static IDataset querydataLineChangemode(IData map) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("ATTR_CODE", map.getString("ATTR_CODE"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.ATTR_VALUE ");
        parser.addSQL("  FROM TF_B_EOP_ATTR T ");
        parser.addSQL(" WHERE T.IBSYSID = :IBSYSID ");
        parser.addSQL("  AND T.ATTR_CODE = :ATTR_CODE ");
        parser.addSQL(" ORDER BY T.SUB_IBSYSID DESC ");

        IDataset otherInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        IDataset attrInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(otherInfos)) {
            attrInfos.add(otherInfos.getData(0));
        }

        return attrInfos;
    }

    public static void updateDatecontractInfo(IData map) throws Exception {
        IData params = new DataMap();
        params.put("CONTRACT_ID", map.getString("CONTRACT_ID"));
        params.put("UPDATE_TIME", SysDateMgr.getLastSecond(SysDateMgr.getSysTime()));
        params.put("FILE_LIST_NAME", map.getString("FILE_LIST_NAME"));
        // 先查询是否存在，再修改
        SQLParser parsers = new SQLParser(params);
        parsers.addSQL(" SELECT T.CONTRACT_ID,");
        parsers.addSQL(" T.CUST_ID ");
        parsers.addSQL(" FROM TF_F_CUST_CONTRACT T ");
        parsers.addSQL(" WHERE T.CONTRACT_ID =:CONTRACT_ID ");

        IDataset contractInfos = Dao.qryByParse(parsers, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(contractInfos)) {
            StringBuilder parser = new StringBuilder();
            parser.append(" UPDATE TF_F_CUST_CONTRACT SET ");
            parser.append(" CONTRACT_FILE_ID=:FILE_LIST_NAME, ");
            parser.append(" UPDATE_TIME=to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') ");
            parser.append(" WHERE ");
            parser.append(" CONTRACT_ID=:CONTRACT_ID ");

            Dao.executeUpdate(parser, params, Route.CONN_CRM_CG);
        } else {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据合同编码【 " + map.getString("CONTRACT_ID") + " 】未获取到客管系统合同附件信息！");

        }
    }

    public static IDataset queryAuditWorkform(IData map, Pagination pagination) throws Exception {
        IData params = new DataMap();
        params.put("STAFF_ID", map.getString("STAFF_ID"));
        params.put("IBSYSID", map.getString("IBSYSID"));
        params.put("ACCEPT_START", map.getString("ACCEPT_START"));
        params.put("ACCEPT_END", map.getString("ACCEPT_END"));
        params.put("GROUP_ID", map.getString("GROUP_ID"));
        // 稽核结果 0-不通过 1 通过
        String auditResult = map.getString("AUDIT_RESULT");
        params.put("AUDIT_RESULT", auditResult);
        IDataset myworkformList = new DatasetList();
        SQLParser parser = new SQLParser(params);
        if (StringUtils.isNotBlank(auditResult)) {
            parser.addSQL("select c.ibsysid,c.record_num,c.product_id,c.product_name,c.trade_id, ");
            parser.addSQL("product_no,a.attr_value,d.group_id,g.city_code,O.ATTR_VALUE AUDIT_VALUE,N.ACCEPT_STAFF_ID ");
            parser.addSQL("from tf_b_ewe n,tf_b_eop_product_sub c,tf_b_eop_eoms_state t,tf_b_eop_attr a, ");
            parser.addSQL("tf_b_eop_subscribe d,tf_f_cust_group g,TF_BH_EOP_OTHER O ");
            parser.addSQL("where n.bpm_templet_id in ('EDIRECTLINEOPENPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEPBOSS') ");
            parser.addSQL("and (:ACCEPT_START is null or to_char(n.create_date, 'yyyymmddss') >= to_char(to_date(:ACCEPT_START, 'yyyy-mm-dd'), 'yyyymmddss'))  ");
            parser.addSQL(" AND (:ACCEPT_END is null or to_char(n.create_date, 'yyyymmddss') <= to_char(to_date(:ACCEPT_END, 'yyyy-mm-dd'), 'yyyymmddss'))  ");
            parser.addSQL("and c.ibsysid=n.bi_sn ");
            parser.addSQL(" and c.trade_id is not null ");
            parser.addSQL("and t.ibsysid = c.ibsysid ");
            parser.addSQL(" and t.record_num = c.record_num ");
            parser.addSQL(" and a.ibsysid = t.ibsysid ");
            parser.addSQL(" and a.node_id='apply' ");
            parser.addSQL(" and a.attr_code='AUDITSTAFF' ");
            parser.addSQL(" and a.sub_ibsysid = (select max(g.SUB_IBSYSID) from tf_b_eop_attr g ");
            parser.addSQL(" where g.ibsysid = a.ibsysid ");
            parser.addSQL(" and g.node_id = 'apply' ");
            parser.addSQL(" and g.attr_code = 'AUDITSTAFF') ");
            parser.addSQL("  and d.ibsysid = c.ibsysid ");
            parser.addSQL("  and d.group_id=g.group_id ");
            parser.addSQL("AND(:IBSYSID is null or n.BI_SN=:IBSYSID) ");
            parser.addSQL("AND(:STAFF_ID is null or a.attr_value=:STAFF_ID) ");
            parser.addSQL("AND(:GROUP_ID is null or d.group_id=:GROUP_ID) ");
            parser.addSQL("AND O.NODE_ID = 'auditInfo' ");
            parser.addSQL("AND O.ATTR_CODE IN ('AUDITESOPINFO', 'AUDITAUTH') ");
            // 稽核通过
            if ("1".equals(auditResult)) {
                parser.addSQL("AND O.ATTR_VALUE IN('8','4','1') ");
            } else {
                parser.addSQL("AND O.ATTR_VALUE IN('2','3','5''6','7') ");
            }
            parser.addSQL("AND O.SUB_IBSYSID = ");
            parser.addSQL("(SELECT MAX(T.SUB_IBSYSID)          FROM TF_BH_EOP_OTHER T ");
            parser.addSQL("WHERE T.NODE_ID = 'auditInfo'            AND T.ATTR_CODE IN ('AUDITESOPINFO', 'AUDITAUTH') ");
            parser.addSQL("AND T.IBSYSID = C.IBSYSID ");
            parser.addSQL("AND T.RECORD_NUM = C.RECORD_NUM) ");
            parser.addSQL(" union ");
            parser.addSQL("select c.ibsysid,c.record_num,c.product_id,c.product_name,c.trade_id, ");
            parser.addSQL("t.product_no,a.attr_value,d.group_id,g.city_code,O.ATTR_VALUE AUDIT_VALUE,N.ACCEPT_STAFF_ID ");
            parser.addSQL("from tf_bh_ewe n,tf_bh_eop_product_sub c,tf_bh_eop_eoms_state t, ");
            parser.addSQL("tf_bh_eop_attr a,tf_bh_eop_subscribe d,tf_f_cust_group g,TF_BH_EOP_OTHER O ");
            parser.addSQL("where n.bpm_templet_id in ('EDIRECTLINEOPENPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEPBOSS') ");
            parser.addSQL("and (:ACCEPT_START is null or to_char(n.create_date, 'yyyymmddss') >= to_char(to_date(:ACCEPT_START, 'yyyy-mm-dd'), 'yyyymmddss'))  ");
            parser.addSQL(" AND (:ACCEPT_END is null or to_char(n.create_date, 'yyyymmddss') <= to_char(to_date(:ACCEPT_END, 'yyyy-mm-dd'), 'yyyymmddss'))  ");
            parser.addSQL("and c.ibsysid=n.bi_sn ");
            parser.addSQL(" and c.trade_id is not null ");
            parser.addSQL("and t.ibsysid = c.ibsysid ");
            parser.addSQL(" and t.record_num = c.record_num ");
            parser.addSQL(" and a.ibsysid = t.ibsysid ");
            parser.addSQL(" and a.node_id='apply' ");
            parser.addSQL(" and a.attr_code='AUDITSTAFF' ");
            parser.addSQL(" and a.sub_ibsysid = (select max(g.SUB_IBSYSID) from tf_bh_eop_attr g ");
            parser.addSQL(" where g.ibsysid = a.ibsysid ");
            parser.addSQL(" and g.node_id = 'apply' ");
            parser.addSQL(" and g.attr_code = 'AUDITSTAFF') ");
            parser.addSQL("  and d.ibsysid = c.ibsysid ");
            parser.addSQL("  and d.group_id=g.group_id ");
            parser.addSQL("AND(:IBSYSID is null or n.BI_SN=:IBSYSID) ");
            parser.addSQL("AND(:STAFF_ID is null or a.attr_value=:STAFF_ID) ");
            parser.addSQL("AND(:GROUP_ID is null or d.group_id=:GROUP_ID) ");
            parser.addSQL("AND O.NODE_ID = 'auditInfo' ");
            parser.addSQL("AND O.ATTR_CODE IN ('AUDITESOPINFO', 'AUDITAUTH') ");
            // 稽核通过
            if ("1".equals(auditResult)) {
                parser.addSQL("AND O.ATTR_VALUE IN('8','4','1') ");
            } else {
                parser.addSQL("AND O.ATTR_VALUE IN('2','3','5''6','7') ");
            }
            parser.addSQL("AND O.SUB_IBSYSID = ");
            parser.addSQL("(SELECT MAX(T.SUB_IBSYSID)          FROM TF_BH_EOP_OTHER T ");
            parser.addSQL("WHERE T.NODE_ID = 'auditInfo'            AND T.ATTR_CODE IN ('AUDITESOPINFO', 'AUDITAUTH') ");
            parser.addSQL("AND T.IBSYSID = C.IBSYSID ");
            parser.addSQL("AND T.RECORD_NUM = C.RECORD_NUM) ");
            parser.addSQL(" ORDER BY IBSYSID DESC ");
        } else {
            parser.addSQL("select c.ibsysid,c.record_num,c.product_id,c.product_name,c.trade_id, ");
            parser.addSQL("t.product_no,a.attr_value,d.group_id,g.city_code,N.ACCEPT_STAFF_ID ");
            parser.addSQL("from tf_b_ewe n,tf_b_eop_product_sub c,tf_b_eop_eoms_state t,tf_b_eop_attr a,tf_b_eop_subscribe d,tf_f_cust_group g ");
            parser.addSQL("where n.bpm_templet_id in ('EDIRECTLINEOPENPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEPBOSS') ");
            parser.addSQL("and (:ACCEPT_START is null or to_char(n.create_date, 'yyyymmddss') >= to_char(to_date(:ACCEPT_START, 'yyyy-mm-dd'), 'yyyymmddss'))  ");
            parser.addSQL(" AND (:ACCEPT_END is null or to_char(n.create_date, 'yyyymmddss') <= to_char(to_date(:ACCEPT_END, 'yyyy-mm-dd'), 'yyyymmddss'))  ");
            parser.addSQL("and c.ibsysid=n.bi_sn ");
            parser.addSQL(" and c.trade_id is not null ");
            parser.addSQL("and t.ibsysid = c.ibsysid ");
            parser.addSQL(" and t.record_num = c.record_num ");
            parser.addSQL(" and a.ibsysid = t.ibsysid ");
            parser.addSQL(" and a.node_id='apply' ");
            parser.addSQL(" and a.attr_code='AUDITSTAFF' ");
            parser.addSQL(" and a.sub_ibsysid = (select max(g.SUB_IBSYSID) from tf_b_eop_attr g ");
            parser.addSQL(" where g.ibsysid = a.ibsysid ");
            parser.addSQL(" and g.node_id = 'apply' ");
            parser.addSQL(" and g.attr_code = 'AUDITSTAFF') ");
            parser.addSQL("  and d.ibsysid = c.ibsysid ");
            parser.addSQL("  and d.group_id=g.group_id ");
            parser.addSQL("AND(:IBSYSID is null or n.BI_SN=:IBSYSID) ");
            parser.addSQL("AND(:STAFF_ID is null or a.attr_value=:STAFF_ID) ");
            parser.addSQL("AND(:GROUP_ID is null or d.group_id=:GROUP_ID) ");
            parser.addSQL(" union ");
            parser.addSQL("select c.ibsysid,c.record_num,c.product_id,c.product_name,c.trade_id, ");
            parser.addSQL("t.product_no,a.attr_value,d.group_id,g.city_code,N.ACCEPT_STAFF_ID ");
            parser.addSQL("from tf_bh_ewe n,tf_bh_eop_product_sub c,tf_bh_eop_eoms_state t,tf_bh_eop_attr a,tf_bh_eop_subscribe d,tf_f_cust_group g ");
            parser.addSQL("where n.bpm_templet_id in ('EDIRECTLINEOPENPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEPBOSS') ");
            parser.addSQL("and (:ACCEPT_START is null or to_char(n.create_date, 'yyyymmddss') >= to_char(to_date(:ACCEPT_START, 'yyyy-mm-dd'), 'yyyymmddss'))  ");
            parser.addSQL(" AND (:ACCEPT_END is null or to_char(n.create_date, 'yyyymmddss') <= to_char(to_date(:ACCEPT_END, 'yyyy-mm-dd'), 'yyyymmddss'))  ");
            parser.addSQL("and c.ibsysid=n.bi_sn ");
            parser.addSQL(" and c.trade_id is not null ");
            parser.addSQL("and t.ibsysid = c.ibsysid ");
            parser.addSQL(" and t.record_num = c.record_num ");
            parser.addSQL(" and a.ibsysid = t.ibsysid ");
            parser.addSQL(" and a.node_id='apply' ");
            parser.addSQL(" and a.attr_code='AUDITSTAFF' ");
            parser.addSQL(" and a.sub_ibsysid = (select max(g.SUB_IBSYSID) from tf_bh_eop_attr g ");
            parser.addSQL(" where g.ibsysid = a.ibsysid ");
            parser.addSQL(" and g.node_id = 'apply' ");
            parser.addSQL(" and g.attr_code = 'AUDITSTAFF') ");
            parser.addSQL("  and d.ibsysid = c.ibsysid ");
            parser.addSQL("  and d.group_id=g.group_id ");
            parser.addSQL(" AND(:IBSYSID is null or n.BI_SN=:IBSYSID) ");
            parser.addSQL(" AND(:STAFF_ID is null or a.attr_value=:STAFF_ID) ");
            parser.addSQL(" AND(:GROUP_ID is null or d.group_id=:GROUP_ID) ");
            parser.addSQL(" ORDER BY IBSYSID DESC ");
        }
        myworkformList = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        getAuditworkform(myworkformList);// 拼加参数
        return myworkformList;
    }

    public static IDataset getAuditworkform(IDataset myworkformList) throws Exception {
        if (IDataUtil.isNotEmpty(myworkformList)) {
            for (Object object : myworkformList) {
                IData myworkformInfo = (IData) object;
                String staffId = myworkformInfo.getString("ATTR_VALUE", "");
                // 获取员工姓名
                String staffName = "";
                if (StringUtils.isNotEmpty(staffId)) {
                    staffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
                }
                myworkformInfo.put("STAFF_NAME", staffName);
                // 获取归属地市
                String groupCity = myworkformInfo.getString("CITY_CODE", "");
                String cityName = "";
                if (StringUtils.isNotEmpty(groupCity)) {
                    cityName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "CUST_CITY_CODE", groupCity });
                }
                myworkformInfo.put("CITY_NAME", cityName);
                String auditValue = myworkformInfo.getString("AUDIT_VALUE");
                String tradeId = myworkformInfo.getString("TRADE_ID");
                IData map = new DataMap();
                map.put("INFO_SIGN", tradeId);
                map.put("INFO_CHILD_TYPE", "41");
                // 查询待阅
                IData infoId = CSAppCall.callOne("SS.WorkTaskMgrSVC.queryWorkInfoByInfoSign", map);
                String auditFlag = "否";
                if (IDataUtil.isNotEmpty(infoId)) {
                    String info = infoId.getString("INFO_STATUS");
                    if ("9".equals(info)) {
                        auditFlag = "是";
                    }
                }
                myworkformInfo.put("AUDIT_FLAG", auditFlag);
                // 稽核结果
                String AUDIT_RESULT = "";
                if ("是".equals(auditFlag)) {
                    if (StringUtils.isNotBlank(auditValue)) {
                        if ("4".equals(auditValue) || "8".equals(auditValue) || "1".equals(auditValue)) {
                            AUDIT_RESULT = "稽核通过";
                        } else {
                            AUDIT_RESULT = "稽核不通过";
                        }
                    } else {
                        IData params = new DataMap();
                        params.put("IBSYSID", myworkformInfo.getString("IBSYSID"));
                        params.put("RECORD_NUM", myworkformInfo.getString("RECORD_NUM"));
                        SQLParser parser = new SQLParser(params);
                        parser.addSQL("  select ATTR_VALUE from tf_bh_eop_other o   ");
                        parser.addSQL(" where o.node_id = 'auditInfo' ");
                        parser.addSQL("   and o.attr_code in ('AUDITESOPINFO','AUDITAUTH') ");
                        parser.addSQL(" and o.ibsysid = :IBSYSID ");
                        parser.addSQL(" and o.RECORD_NUM = :RECORD_NUM ");
                        parser.addSQL("  and o.sub_ibsysid = (select max(t.sub_ibsysid)  ");
                        parser.addSQL("  from tf_bh_eop_other t ");
                        parser.addSQL(" where t.node_id = 'auditInfo' ");
                        parser.addSQL(" and t.attr_code in ('AUDITESOPINFO','AUDITAUTH')");
                        parser.addSQL(" and t.ibsysid = :IBSYSID ");
                        parser.addSQL(" and t.RECORD_NUM = :RECORD_NUM) ");
                        IDataset otherInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
                        if (IDataUtil.isNotEmpty(otherInfos)) {
                            IData other = otherInfos.first();
                            String attrValue = other.getString("ATTR_VALUE");
                            if ("4".equals(attrValue) || "8".equals(attrValue) || "1".equals(attrValue)) {
                                AUDIT_RESULT = "稽核通过";
                            } else {
                                AUDIT_RESULT = "稽核不通过";
                            }
                        } else {
                            AUDIT_RESULT = "稽核通过";
                        }
                    }
                    myworkformInfo.put("AUDIT_RESULT", AUDIT_RESULT);
                    // 查询稽核意见和时间
                    if (!"".equals(myworkformInfo.getString("IBSYSID"))) {
                        IData remarkInfos = queryRemarkInfo(myworkformInfo);
                        if (IDataUtil.isNotEmpty(remarkInfos)) {
                            IDataset infos = new DatasetList();
                            Iterator<String> iterator = remarkInfos.keySet().iterator();
                            while (iterator.hasNext()) {
                                // 循环遍历返回值结果集 查询专线实例号是否包含在key中
                                String key = iterator.next();
                                if (key.contains(myworkformInfo.getString("PRODUCT_NO"))) {
                                    infos = remarkInfos.getDataset(key);
                                }
                                if (IDataUtil.isNotEmpty(infos)) {
                                    for (Object obj : infos) {
                                        IData remarkData = (IData) obj;
                                        // 一次整改
                                        if ("aRectification".equals(remarkData.getString("NODE_ID"))) {
                                            String onceRectifyTtme = remarkData.getString("UPDATE_TIME");
                                            String onceRectifyRemark = remarkData.getString("ATTR_VALUE");
                                            myworkformInfo.put("ONCE_RECTIFY_TIME", onceRectifyTtme);
                                            myworkformInfo.put("ONCE_RECTIFY_REMARK", onceRectifyRemark);
                                            continue;
                                        }// 一次稽核
                                        else if ("apply".equals(remarkData.getString("NODE_ID"))) {
                                            String onceAuditTtme = remarkData.getString("UPDATE_TIME");
                                            String onceAuditRemark = remarkData.getString("ATTR_VALUE");
                                            myworkformInfo.put("ONCE_AUDIT_TIME", onceAuditTtme);
                                            myworkformInfo.put("ONCE_AUDIT_REMARK", onceAuditRemark);
                                            continue;
                                        }// 二次整改
                                        else if ("twoRectification".equals(remarkData.getString("NODE_ID"))) {
                                            String twiceeRectifyTtme = remarkData.getString("UPDATE_TIME");
                                            String twiceRectifyRemark = remarkData.getString("ATTR_VALUE");
                                            myworkformInfo.put("TWICE_RECTIFY_TIME", twiceeRectifyTtme);
                                            myworkformInfo.put("TWICE_RECITY_REMARK", twiceRectifyRemark);
                                            continue;
                                        }// 二次稽核
                                        else if ("aCheckRectification".equals(remarkData.getString("NODE_ID"))) {
                                            String twiceAuditTtme = remarkData.getString("UPDATE_TIME");
                                            String twiceAuditRemark = remarkData.getString("ATTR_VALUE");
                                            myworkformInfo.put("TWICE_AUDIT_TIME", twiceAuditTtme);
                                            myworkformInfo.put("TWICE_AUDIT_REMARK", twiceAuditRemark);
                                            continue;
                                        }// 三次稽核
                                        else if ("twoCheckRectification".equals(remarkData.getString("NODE_ID"))) {
                                            String threeAuditTtme = remarkData.getString("UPDATE_TIME");
                                            String threeAuditRemark = remarkData.getString("ATTR_VALUE");
                                            myworkformInfo.put("THREE_AUDIT_TIME", threeAuditTtme);
                                            myworkformInfo.put("THREE_AUDIT_REMARK", threeAuditRemark);
                                            continue;
                                        }
                                        
                                    }
                                    break;
                                }
                            }
                        }

                    }

                }

            }
        }
        return myworkformList;

    }

    public static IData queryRemarkInfo(IData data) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", data.getString("IBSYSID"));
        SQLParser parser1 = new SQLParser(params);
        parser1.addSQL("SELECT T.IBSYSID,T.RSRV_STR1 ");
        parser1.addSQL("FROM TF_B_EOP_MODI_TRACE T ");
        parser1.addSQL("WHERE T.MAIN_IBSYSID=:IBSYSID ");
        parser1.addSQL("UNION ");
        parser1.addSQL("SELECT T.IBSYSID,T.RSRV_STR1 ");
        parser1.addSQL("FROM TF_BH_EOP_MODI_TRACE T ");
        parser1.addSQL("WHERE T.MAIN_IBSYSID=:IBSYSID ");
        IDataset ibsysInfo = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(ibsysInfo)) {
            IData results = new DataMap();
            for (Object obj : ibsysInfo) {
                IData idInfo = (IData) obj;
                String newIbsysid = idInfo.getString("IBSYSID");
                IData param = new DataMap();
                param.put("IBSYSID", newIbsysid);
                SQLParser parser = new SQLParser(param);
                parser.addSQL("SELECT T.NODE_ID, T.ATTR_VALUE,TO_CHAR(T.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss')UPDATE_TIME ");
                parser.addSQL("  FROM TF_BH_EOP_ATTR T ");
                parser.addSQL(" WHERE T.IBSYSID=:IBSYSID ");
                parser.addSQL("    AND T.ATTR_CODE = 'REMARK' ");
                parser.addSQL("UNION ");
                parser.addSQL("   SELECT T.NODE_ID, T.ATTR_VALUE,TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss')UPDATE_TIME ");
                parser.addSQL("   FROM TF_B_EOP_ATTR T ");
                parser.addSQL(" WHERE T.IBSYSID=:IBSYSID ");
                parser.addSQL(" AND T.ATTR_CODE = 'REMARK' ");
                IDataset result = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
                if (!"".equals(idInfo.getString("RSRV_STR1")) && idInfo.getString("RSRV_STR1") != null) {
                    results.put(idInfo.getString("RSRV_STR1"), result);
                }
            }
            return results;
        }
        return null;
    }

}
