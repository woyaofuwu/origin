package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SubscribeViewInfoBean {

    // 查询附件
    public static IDataset qryGroupAttach(IData params) throws Exception {

        IDataset attachInfos = new DatasetList();

        IData param = new DataMap();
        param.put("IBSYSID", params.getString("IBSYSID"));
        SQLParser parser1 = new SQLParser(param);
        parser1.addSQL(" SELECT * FROM ( ");
        parser1.addSQL(" SELECT C.ATTACH_TYPE,ROW_NUMBER() ");
        parser1.addSQL(" OVER(PARTITION BY C.ATTACH_TYPE ORDER BY C.ATTACH_TYPE DESC) G ");
        parser1.addSQL(" FROM TF_B_EOP_ATTACH C WHERE C.IBSYSID=:IBSYSID ) W ");
        parser1.addSQL(" WHERE 1 = 1 ");
        parser1.addSQL(" AND W.G<=1 ");
        IDataset attachTypeInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));

        if (IDataUtil.isNotEmpty(attachTypeInfos)) {
            for (Object object : attachTypeInfos) {
                IData attachTypeInfo = (IData) object;
                IData param1 = new DataMap();
                param1.put("IBSYSID", params.getString("IBSYSID"));
                param1.put("ATTACH_TYPE", attachTypeInfo.getString("ATTACH_TYPE"));

                SQLParser parser = new SQLParser(param1);
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
                parser.addSQL(" C.ATTACH_TYPE FROM ");
                parser.addSQL(" (SELECT * FROM (SELECT T.SUB_IBSYSID ");
                parser.addSQL(" FROM TF_B_EOP_ATTACH T WHERE T.IBSYSID =:IBSYSID ");
                parser.addSQL(" AND T.ATTACH_TYPE = :ATTACH_TYPE ");
                parser.addSQL(" GROUP BY SUB_IBSYSID ");
                parser.addSQL(" ORDER BY SUB_IBSYSID DESC) A WHERE ROWNUM = 1) E,  ");
                parser.addSQL(" (SELECT * FROM (SELECT T.GROUP_SEQ ");
                parser.addSQL(" FROM TF_B_EOP_ATTACH T WHERE T.IBSYSID =:IBSYSID ");
                parser.addSQL(" AND T.ATTACH_TYPE = :ATTACH_TYPE ");
                parser.addSQL(" GROUP BY GROUP_SEQ ");
                parser.addSQL(" ORDER BY GROUP_SEQ DESC) A WHERE ROWNUM = 1) Q, ");
                parser.addSQL(" TF_B_EOP_ATTACH C  ");
                parser.addSQL(" WHERE E.SUB_IBSYSID = C.SUB_IBSYSID  ");
                parser.addSQL(" AND C.IBSYSID=:IBSYSID ");
                parser.addSQL(" AND C.ATTACH_TYPE=:ATTACH_TYPE ");
                parser.addSQL(" AND C.GROUP_SEQ=Q.GROUP_SEQ ");

                IDataset accachList = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(accachList)) {
                    IData map = new DataMap();
                    String accactTypeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "EOP_ATTACH_TYPE", attachTypeInfo.getString("ATTACH_TYPE") });
                    String accactType = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMVALUE" }, "PARAMNAME", new String[] { "EOP_ATTACH_TYPE", accactTypeName });
                    map.put("LIST", accachList);
                    map.put("TITLE", accactTypeName);
                    map.put("TYPE", accactType);
                    attachInfos.add(map);
                }
            }
        }

        return attachInfos;
    }

    // 查询未完成工单的属性信息
    public static IDataset qryEweServiceAttributes(String ibsysid, String sub_ibsysid, String record_num) throws Exception {

        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("SUB_IBSYSID", sub_ibsysid);
        param.put("RECORD_NUM", record_num);
        return Dao.qryByCode("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_SUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));

    }

    public static IDataset qryEopServiceAttributes(String ibsysid) throws Exception {

        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_ATTR", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));

    }

    // 查询未完成的工单历史结点
    public static IDataset queryWorkFormNodeInfo(String ibsysid) throws Exception {

        IData param = new DataMap();
        param.put("BI_SN", ibsysid);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SElECT T.BI_SN,");
        parser.addSQL(" K.SUB_BI_SN, ");
        parser.addSQL(" K.BUSIFORM_NODE_ID, ");
        parser.addSQL(" k.Node_Id, ");
        parser.addSQL(" T.EPARCHY_CODE, ");
        parser.addSQL(" k.CREATE_DATE INSERT_TIME, ");
        parser.addSQL(" k.FLOW_EXPECT_TIME PLAN_FINISH_TIME, ");
        parser.addSQL(" to_char(k.FLOW_REAL_TIME,'yyyy-mm-dd hh24:mi:ss') as DEAL_TIME, ");
        parser.addSQL(" K.UPDATE_STAFF_ID, ");
        parser.addSQL(" K.DEAL_STAFF_ID, ");
        parser.addSQL(" '未完成' FLOW_STATE_DESC, ");
        parser.addSQL(" '未完成' NODE_STATE, ");
        parser.addSQL(" '1' NODE_DEAL_STATE, ");
        parser.addSQL(" T.BPM_TEMPLET_ID ");
        parser.addSQL(" FROM TF_B_EWE T, ");
        parser.addSQL("      TF_B_EWE_NODE_TRA K ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.BUSIFORM_ID = K.BUSIFORM_ID ");
        parser.addSQL(" AND T.BI_SN = :BI_SN ");
        parser.addSQL(" AND T.TEMPLET_TYPE = '0' ");
        parser.addSQL(" ORDER BY K.CREATE_DATE ASC,K.FLOW_REAL_TIME  ASC ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    // 根据node_id和ibsysid查询 工单的节点属性
    public static IDataset qryEweAttributesByNodeIdIbsysid(String ibsysid, String node_id, String record_num) throws Exception {

        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", node_id);
        param.put("RECORD_NUM", record_num);

        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_NODEID", param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    // 查询订单的详细订单信息
    public static IDataset qryWorkformNodeByIbsysid(String ibsysid, String node_id) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", node_id);
        return Dao.qryByCode("TF_B_EOP_NODE", "SEL_BY_IBSYSID_NODEID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    // 查询已经完成的工单轨迹
    public static IDataset queryFinishWorksheet(String ibsysid, String busiformId) throws Exception {
        IData param = new DataMap();
        param.put("BI_SN", ibsysid);
        param.put("BUSIFORM_ID", busiformId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SElECT T.BI_SN,");
        parser.addSQL(" K.SUB_BI_SN, ");
        parser.addSQL(" K.BUSIFORM_NODE_ID, ");
        parser.addSQL(" k.Node_Id, ");
        parser.addSQL(" T.EPARCHY_CODE, ");
        parser.addSQL(" k.CREATE_DATE INSERT_TIME, ");
        parser.addSQL(" k.FLOW_EXPECT_TIME PLAN_FINISH_TIME, ");
        parser.addSQL(" to_char(k.FLOW_REAL_TIME,'yyyy-mm-dd hh24:mi:ss') as DEAL_TIME, ");
        parser.addSQL(" K.UPDATE_STAFF_ID, ");
        parser.addSQL(" K.DEAL_STAFF_ID, ");
        parser.addSQL(" '已完成' FLOW_STATE_DESC, ");
        parser.addSQL(" '已完成' NODE_STATE, ");
        parser.addSQL(" '1' NODE_DEAL_STATE, ");
        parser.addSQL(" T.BPM_TEMPLET_ID ");
        parser.addSQL(" FROM TF_BH_EWE T, ");
        parser.addSQL("      TF_BH_EWE_NODE K ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.BUSIFORM_ID = K.BUSIFORM_ID ");
        parser.addSQL(" AND T.BI_SN = :BI_SN ");
        parser.addSQL(" AND T.TEMPLET_TYPE = '0' ");
        parser.addSQL(" AND T.BUSIFORM_ID = :BUSIFORM_ID ");
        parser.addSQL(" ORDER BY K.CREATE_DATE ASC,K.FLOW_REAL_TIME  ASC ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    // 查询已经完成订单的详细信息
    public static IDataset qryFinishWorkformNodeByIbsysid(String ibsysid, String nodeid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", nodeid);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, ");
        parser.addSQL(" T.NODE_ID,T.CITY_CODE,T.EPARCHY_CODE, ");
        parser.addSQL(" T.ACCEPT_MONTH,T.DEPART_ID,T.DEPART_NAME, ");
        parser.addSQL(" T.PRODUCT_ID,T.STAFF_ID,T.STAFF_NAME, ");
        parser.addSQL(" T.ORDER_ID,T.STAFF_PHONE,T.REMARK, ");
        parser.addSQL(" T.DEAL_STATE,T.RSRV_STR1,T.RSRV_STR2, ");
        parser.addSQL(" T.DEAL_OPTION,T.RSRV_STR3,T.RSRV_STR4, ");
        parser.addSQL(" T.DEAL_SRC,T.RSRV_STR5,T.RSRV_STR5, ");
        parser.addSQL(" T.DEAL_DESC,T.RSRV_STR7,T.TRANSFER_PARAM, ");
        parser.addSQL(" T.ROLE_ID,T.WORKFORM_TYPE, ");
        parser.addSQL(" TO_CHAR(T.DEAL_TIME, 'YYYY-MM-DD HH24:MI:SS') DEAL_TIME, ");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME, 'YYYY-MM-DD HH24:MI:SS') INSERT_TIME ");
        parser.addSQL(" FROM TF_B_EOP_NODE T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.NODE_ID =:NODE_ID ");
        parser.addSQL(" AND T.IBSYSID =:IBSYSID ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT T.SUB_IBSYSID, T.IBSYSID, ");
        parser.addSQL(" T.NODE_ID,T.CITY_CODE,T.EPARCHY_CODE, ");
        parser.addSQL(" T.ACCEPT_MONTH,T.DEPART_ID,T.DEPART_NAME, ");
        parser.addSQL(" T.PRODUCT_ID,T.STAFF_ID,T.STAFF_NAME, ");
        parser.addSQL(" T.ORDER_ID,T.STAFF_PHONE,T.REMARK, ");
        parser.addSQL(" T.DEAL_STATE,T.RSRV_STR1,T.RSRV_STR2, ");
        parser.addSQL(" T.DEAL_OPTION,T.RSRV_STR3,T.RSRV_STR4, ");
        parser.addSQL(" T.DEAL_SRC,T.RSRV_STR5,T.RSRV_STR5, ");
        parser.addSQL(" T.DEAL_DESC,T.RSRV_STR7,T.TRANSFER_PARAM, ");
        parser.addSQL(" T.ROLE_ID,T.WORKFORM_TYPE, ");
        parser.addSQL(" TO_CHAR(T.DEAL_TIME, 'YYYY-MM-DD HH24:MI:SS') DEAL_TIME, ");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME, 'YYYY-MM-DD HH24:MI:SS') INSERT_TIME ");
        parser.addSQL(" FROM TF_BH_EOP_NODE T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.NODE_ID =:NODE_ID ");
        parser.addSQL(" AND T.IBSYSID =:IBSYSID ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    // 根据ibsysid和sub_ibsysid查询已完成的工单的属性信息
    public static IDataset qryFinishEweServiceAttributes(String ibsysid, String sub_ibsysid, String record_num) throws Exception {

        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("SUB_IBSYSID", sub_ibsysid);
        param.put("RECORD_NUM", record_num);
        return Dao.qryByCode("TF_BH_EOP_ATTR", "SEL_BY_IBSYSID_SUBIBSYSID", param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    // 根据ibsysid 和 node_id 查询已完成的工单的属性信息
    public static IDataset qryFinishEweAttributesByNodeIdIbsysid(String ibsysid, String node_id, String record_num) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", node_id);
        param.put("RECORD_NUM", record_num);

        return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_BY_IBSYSID_NODEID", param, Route.getJourDb(Route.CONN_CRM_CG));

    }

    // 根据sub_ibsysid和group_seq 查询已经完成的工单(等待资管回复节点)属性信息
    public static IDataset qryFinishEweAttributesByGroupSeq(String sub_ibsysid, String group_seq) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", sub_ibsysid);
        param.put("GROUP_SEQ", group_seq);

        return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_BY_IBSYSID_GROUPSEQ", param, Route.getJourDb(Route.CONN_CRM_CG));

    }

    // 根据sub_ibsysid和group_seq 查询未完成的工单(等待资管回复节点)属性信息
    public static IDataset qrEweAttributesByGroupSeq(String sub_ibsysid, String group_seq) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", sub_ibsysid);
        param.put("GROUP_SEQ", group_seq);

        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_GROUPSEQ", param, Route.getJourDb(Route.CONN_CRM_CG));

    }

    // 查询已经完成的工单的附件
    public static IDataset qryFinishGroupAttach(String ibsysid) throws Exception {
        IDataset attachInfos = new DatasetList();

        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        SQLParser parser1 = new SQLParser(param);
        parser1.addSQL(" SELECT * FROM ( ");
        parser1.addSQL(" SELECT C.ATTACH_TYPE,ROW_NUMBER() ");
        parser1.addSQL(" OVER(PARTITION BY C.ATTACH_TYPE ORDER BY C.ATTACH_TYPE DESC) G ");
        parser1.addSQL(" FROM TF_BH_EOP_ATTACH C WHERE C.IBSYSID=:IBSYSID ) W ");
        parser1.addSQL(" WHERE 1 = 1 ");
        parser1.addSQL(" AND W.G<=1 ");
        IDataset attachTypeInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));

        if (IDataUtil.isNotEmpty(attachTypeInfos)) {
            for (Object object : attachTypeInfos) {
                IData attachTypeInfo = (IData) object;
                IData param1 = new DataMap();
                param1.put("IBSYSID", ibsysid);
                param1.put("ATTACH_TYPE", attachTypeInfo.getString("ATTACH_TYPE"));

                SQLParser parser = new SQLParser(param1);
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
                parser.addSQL(" C.ATTACH_TYPE FROM ");
                parser.addSQL(" (SELECT * FROM (SELECT T.SUB_IBSYSID ");
                parser.addSQL(" FROM TF_BH_EOP_ATTACH T WHERE T.IBSYSID =:IBSYSID ");
                parser.addSQL(" AND T.ATTACH_TYPE = :ATTACH_TYPE ");
                parser.addSQL(" GROUP BY SUB_IBSYSID ");
                parser.addSQL(" ORDER BY SUB_IBSYSID DESC) A WHERE ROWNUM = 1) E,  ");
                parser.addSQL(" (SELECT * FROM (SELECT T.GROUP_SEQ ");
                parser.addSQL(" FROM TF_BH_EOP_ATTACH T WHERE T.IBSYSID =:IBSYSID ");
                parser.addSQL(" AND T.ATTACH_TYPE = :ATTACH_TYPE ");
                parser.addSQL(" GROUP BY GROUP_SEQ ");
                parser.addSQL(" ORDER BY GROUP_SEQ DESC) A WHERE ROWNUM = 1) Q, ");
                parser.addSQL(" TF_BH_EOP_ATTACH C  ");
                parser.addSQL(" WHERE E.SUB_IBSYSID = C.SUB_IBSYSID  ");
                parser.addSQL(" AND C.IBSYSID=:IBSYSID ");
                parser.addSQL(" AND C.ATTACH_TYPE=:ATTACH_TYPE ");
                parser.addSQL(" AND C.GROUP_SEQ=Q.GROUP_SEQ ");

                IDataset accachList = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
                if (IDataUtil.isNotEmpty(accachList)) {
                    IData map = new DataMap();
                    String accactTypeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "EOP_ATTACH_TYPE", attachTypeInfo.getString("ATTACH_TYPE") });
                    String accactType = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMVALUE" }, "PARAMNAME", new String[] { "EOP_ATTACH_TYPE", accactTypeName });
                    map.put("LIST", accachList);
                    map.put("TITLE", accactTypeName);
                    map.put("TYPE", accactType);
                    attachInfos.add(map);
                }
            }
        }
        return attachInfos;
    }

    public static IDataset qryFinishAttrByGroupSeqRecordNum(String sub_ibsysid, String group_seq, String record_num) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", sub_ibsysid);
        param.put("GROUP_SEQ", group_seq);
        param.put("RECORD_NUM", record_num);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT /*+index(T,IDX_TF_BH_EOP_ATTR_04)*/* ");
        parser.addSQL("  FROM TF_Bh_EOP_ATTR T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.SUB_IBSYSID = :SUB_IBSYSID ");
        parser.addSQL(" AND T.GROUP_SEQ = :GROUP_SEQ ");
        parser.addSQL(" AND T.RECORD_NUM = :RECORD_NUM ");
        return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
    }
}
