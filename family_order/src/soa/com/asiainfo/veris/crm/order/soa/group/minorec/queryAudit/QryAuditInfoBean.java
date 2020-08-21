package com.asiainfo.veris.crm.order.soa.group.minorec.queryAudit;

import java.util.Iterator;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.SubReleInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherHBean;

public class QryAuditInfoBean {

    public static IDataset qryInfoByBpmTempletIdAndBiSn(IData param) throws Exception {
        IData params = new DataMap();
        params.put("BI_SN", param.getString("IBSYSID"));
        params.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
        params.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        params.put("BUSIFORM_ID", param.getString("BUSIFORM_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.BUSIFORM_ID,T.BI_SN,T.ACCEPT_MONTH,T.BPM_TEMPLET_ID,T.TEMPLET_TYPE,T.BUSIFORM_OPER_TYPE, ");
        parser.addSQL(" T.IN_MODE_CODE,T.BUSI_TYPE,T.BUSI_CODE,T.REMARK,T.STATE,T.EPARCHY_CODE,T.CITY_CODE,T.ACCEPT_DEPART_ID, ");
        parser.addSQL(" T.UPDATE_DEPART_ID,T.UPDATE_STAFF_ID,T.ACCEPT_STAFF_ID, ");
        parser.addSQL(" TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE, ");
        parser.addSQL(" C.GROUP_ID,C.CUST_NAME,P.USER_ID USER_ID_A,P.TRADE_ID,P.RECORD_NUM,P.SERIAL_NUMBER ");
        parser.addSQL(" FROM TF_B_EWE T, TF_B_EOP_SUBSCRIBE C, TF_B_EOP_PRODUCT P ");
        parser.addSQL(" WHERE T.BPM_TEMPLET_ID =:BPM_TEMPLET_ID ");
        parser.addSQL(" AND T.BI_SN =:BI_SN  ");
        parser.addSQL(" AND C.IBSYSID = T.BI_SN ");
        parser.addSQL(" AND P.IBSYSID = T.BI_SN ");
        parser.addSQL(" AND P.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.BUSIFORM_ID =:BUSIFORM_ID ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT T.BUSIFORM_ID,T.BI_SN,T.ACCEPT_MONTH,T.BPM_TEMPLET_ID,T.TEMPLET_TYPE,T.BUSIFORM_OPER_TYPE, ");
        parser.addSQL(" T.IN_MODE_CODE,T.BUSI_TYPE,T.BUSI_CODE,T.REMARK,T.STATE,T.EPARCHY_CODE,T.CITY_CODE,T.ACCEPT_DEPART_ID, ");
        parser.addSQL(" T.UPDATE_DEPART_ID,T.UPDATE_STAFF_ID,T.ACCEPT_STAFF_ID, ");
        parser.addSQL(" TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE, ");
        parser.addSQL(" C.GROUP_ID,C.CUST_NAME,P.USER_ID USER_ID_A,P.TRADE_ID ,P.RECORD_NUM,P.SERIAL_NUMBER");
        parser.addSQL(" FROM TF_BH_EWE T, TF_BH_EOP_SUBSCRIBE C, TF_BH_EOP_PRODUCT P ");
        parser.addSQL(" WHERE T.BPM_TEMPLET_ID =:BPM_TEMPLET_ID ");
        parser.addSQL(" AND T.BI_SN =:BI_SN ");
        parser.addSQL(" AND C.IBSYSID = T.BI_SN ");
        parser.addSQL(" AND P.IBSYSID = T.BI_SN ");
        parser.addSQL(" AND P.PRODUCT_ID =:PRODUCT_ID ");
        parser.addSQL(" AND T.BUSIFORM_ID =:BUSIFORM_ID ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IData qryAuditInfoByIbsysid(IData param) throws Exception
    {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT T.ATTR_CODE,T.ATTR_VALUE, ");
        sql.addSQL(" DECODE(SIGN(ATTR_VALUE - 1),0,'是',-1,'否')TARIFF_FLAG ");
        sql.addSQL(" FROM TF_B_EOP_ATTR T ");
        sql.addSQL(" WHERE T.IBSYSID=:IBSYSID AND T.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND T.ATTR_CODE = 'TARIFF_FLAG' ");
        IDataset results = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        return results.first();
    }
    
    public static IDataset qryElecDataForAudit(IData param) throws Exception {
        IData params = new DataMap();
        params.put("AGREEMENT_ID", param.getString("AGREEMENT_ID"));
        String paramCodes = param.getString("PARAM_CODE");
        StringBuilder parserBuilder = new StringBuilder();
        parserBuilder.append(" SELECT T.ATTR_CODE,T.ATTR_VALUE ");
        parserBuilder.append(" FROM TF_F_ELECTRONIC_ARCHIVES_ATTR T ");
        parserBuilder.append(" WHERE T.ARCHIVES_ID = ");
        parserBuilder.append(" (SELECT C.* FROM(SELECT T.ARCHIVES_ID ");
        parserBuilder.append(" FROM TF_F_ELECTRONIC_AGREEMENT T ");
        parserBuilder.append(" WHERE T.AGREEMENT_ID =:AGREEMENT_ID)C WHERE ROWNUM=1) ");
        parserBuilder.append(" AND T.ATTR_CODE IN ( ");
        parserBuilder.append(paramCodes);
        parserBuilder.append(" ) ");
        parserBuilder.append(" AND T.END_DATE > SYSDATE ");
        return Dao.qryBySql(parserBuilder, params, Route.CONN_CRM_CG);
    }
    
    public static IDataset qryFinishDataInDiscnt(IData param) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID", param.getString("USER_ID"));
        params.put("DISCNT_CODE", param.getString("DISCNT_CODE"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.USER_ID,T.USER_ID_A FROM TF_F_USER_DISCNT T  ");
        parser.addSQL(" WHERE T.DISCNT_CODE=:DISCNT_CODE  ");
        parser.addSQL(" AND T.USER_ID_A=:USER_ID ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        IDataset results = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        if(IDataUtil.isEmpty(results)) {
            SQLParser parser2 = new SQLParser(params);
            parser2.addSQL(" SELECT T.USER_ID,T.USER_ID_A FROM TF_F_USER_DISCNT T  ");
            parser2.addSQL(" WHERE T.DISCNT_CODE=:DISCNT_CODE  ");
            parser2.addSQL(" AND T.USER_ID=:USER_ID ");
            parser2.addSQL(" AND T.END_DATE > SYSDATE ");
            results = Dao.qryByParse(parser2, Route.CONN_CRM_CG);
        }
        return results;
    }
    
    public static IDataset qryFinishDataInSVC(IData param) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID", param.getString("USER_ID"));
        params.put("SERVICE_ID", param.getString("SERVICE_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.USER_ID,T.USER_ID_A FROM TF_F_USER_SVC T   ");
        parser.addSQL(" WHERE T.SERVICE_ID=:SERVICE_ID ");
        parser.addSQL(" AND T.USER_ID=:USER_ID ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        IDataset results = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        if(IDataUtil.isEmpty(results)) {
            SQLParser parser2 = new SQLParser(params);
            parser2.addSQL(" SELECT T.USER_ID,T.USER_ID_A FROM TF_F_USER_SVC T   ");
            parser2.addSQL(" WHERE T.SERVICE_ID=:SERVICE_ID ");
            parser2.addSQL(" AND T.USER_ID_A=:USER_ID ");
            parser2.addSQL(" AND T.END_DATE > SYSDATE ");
            results = Dao.qryByParse(parser2, Route.CONN_CRM_CG);
        }
        return results;
    }
    
    public static IDataset qryFinishDataInUserAttr(IData param) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID", param.getString("USER_ID"));
        params.put("DISCNT_CODE", param.getString("DISCNT_CODE"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.ATTR_CODE, T.ATTR_VALUE ");
        parser.addSQL(" FROM TF_F_USER_ATTR T ");
        parser.addSQL(" WHERE T.USER_ID =:USER_ID ");
        parser.addSQL(" AND T.RELA_INST_ID = (SELECT T.INST_ID ");
        parser.addSQL(" FROM TF_F_USER_DISCNT T  ");
        parser.addSQL(" WHERE T.DISCNT_CODE =:DISCNT_CODE ");
        parser.addSQL(" AND T.USER_ID =:USER_ID ");
        parser.addSQL(" AND T.END_DATE > SYSDATE) ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    public static IDataset qryFinishDataInRelaionUU(IData param) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID", param.getString("USER_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT COUNT(1) NUM ");
        parser.addSQL(" FROM TF_F_RELATION_UU T ");
        parser.addSQL(" WHERE T.USER_ID_A =:USER_ID ");
        parser.addSQL(" AND T.RELATION_TYPE_CODE = 'MR' ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    public static IDataset qryFinishDataForBandwith(IData param) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID", param.getString("USER_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.PRODUCT_ID ");
        parser.addSQL(" FROM TF_F_USER_PRODUCT T ");
        parser.addSQL(" WHERE T.USER_ID = (SELECT C.USER_ID_B ");
        parser.addSQL(" FROM (SELECT T.USER_ID_B ");
        parser.addSQL(" FROM TF_F_RELATION_UU T ");
        parser.addSQL(" WHERE T.USER_ID_A =:USER_ID ");
        parser.addSQL(" AND T.RELATION_TYPE_CODE = 'MR' ");
        parser.addSQL(" AND T.END_DATE > SYSDATE) C ");
        parser.addSQL(" WHERE ROWNUM = 1) ");
        IDataset results = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return results;
    }
    
    public static IDataset qryEweInfoByBusiformId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("BUSIFORM_ID", param.getString("BUSIFORM_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.BUSIFORM_ID,T.BI_SN,T.ACCEPT_MONTH,T.BPM_TEMPLET_ID,T.TEMPLET_TYPE,T.BUSIFORM_OPER_TYPE,T.IN_MODE_CODE,T.BUSI_TYPE, ");
        parser.addSQL(" T.BUSI_CODE,T.REMARK,T.STATE,T.EPARCHY_CODE,T.CITY_CODE,T.ACCEPT_DEPART_ID,T.UPDATE_DEPART_ID,T.UPDATE_STAFF_ID, ");
        parser.addSQL(" T.ACCEPT_STAFF_ID,TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE ");
        parser.addSQL(" FROM TF_B_EWE T ");
        parser.addSQL(" WHERE T.BUSIFORM_ID =:BUSIFORM_ID ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT T.BUSIFORM_ID,T.BI_SN,T.ACCEPT_MONTH,T.BPM_TEMPLET_ID,T.TEMPLET_TYPE,T.BUSIFORM_OPER_TYPE,T.IN_MODE_CODE,T.BUSI_TYPE, ");
        parser.addSQL(" T.BUSI_CODE,T.REMARK,T.STATE,T.EPARCHY_CODE,T.CITY_CODE,T.ACCEPT_DEPART_ID,T.UPDATE_DEPART_ID,T.UPDATE_STAFF_ID, ");
        parser.addSQL(" T.ACCEPT_STAFF_ID,TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE ");
        parser.addSQL(" FROM TF_BH_EWE T ");
        parser.addSQL(" WHERE T.BUSIFORM_ID =:BUSIFORM_ID ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryEopProductByIbsysidAndBusiformId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("BUSIFORM_ID", param.getString("BUSIFORM_ID"));
        params.put("IBSYSID", param.getString("IBSYSID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.PRODUCT_ID,T.PRODUCT_NAME,T.RECORD_NUM ");
        parser.addSQL(" FROM TF_B_EOP_PRODUCT T ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.RSRV_STR2 =:BUSIFORM_ID ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryEopProductHByIbsysidAndBusiformId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("BUSIFORM_ID", param.getString("BUSIFORM_ID"));
        params.put("IBSYSID", param.getString("IBSYSID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.PRODUCT_ID,T.PRODUCT_NAME,T.RECORD_NUM ");
        parser.addSQL(" FROM TF_BH_EOP_PRODUCT T ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.RSRV_STR2 =:BUSIFORM_ID ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryParentBpmtempletIdByIbsysId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.BPM_TEMPLET_ID ");
        parser.addSQL(" FROM TF_B_EWE T ");
        parser.addSQL(" WHERE T.BI_SN =:IBSYSID ");
        parser.addSQL(" AND T.TEMPLET_TYPE = '0' ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT T.BPM_TEMPLET_ID ");
        parser.addSQL(" FROM TF_BH_EWE T ");
        parser.addSQL(" WHERE T.BI_SN =:IBSYSID ");
        parser.addSQL(" AND T.TEMPLET_TYPE = '0' ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryDataLineWorkForm(IData param, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("STAFF_ID", param.getString("STAFF_ID"));
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("ACCEPT_START", param.getString("ACCEPT_START"));
        params.put("ACCEPT_END", param.getString("ACCEPT_END"));
        params.put("GROUP_ID", param.getString("GROUP_ID"));
        SQLParser parser = new SQLParser(params);
        // 稽核结果 0-不通过 1 通过
        String auditResult = param.getString("AUDIT_RESULT");
        if (StringUtils.isBlank(auditResult))
        {
            // 不做稽核是否通过条件过滤
            parser.addSQL(" SELECT E.BUSIFORM_ID,E.BPM_TEMPLET_ID,E.ACCEPT_STAFF_ID, ");
            parser.addSQL(" P.IBSYSID,P.PRODUCT_NAME,P.UPDATE_TIME,P.RECORD_NUM, ");
            parser.addSQL(" O.ATTR_VALUE AUDITSTAFF, ");
            parser.addSQL(" G.CITY_CODE,G.GROUP_ID ");
            parser.addSQL(" FROM TF_BH_EOP_PRODUCT P,TF_BH_EWE E,TF_BH_EOP_OTHER O,TF_F_USER U,TF_F_CUST_GROUP G ");
            parser.addSQL(" WHERE TO_CHAR(E.BUSIFORM_ID) = P.RSRV_STR2 ");
            parser.addSQL(" AND E.BPM_TEMPLET_ID IN('SUMBUSINESSTVOPEN','FUSECOMMUNICATION_SUB_OPEN','FUSECOMMUNICATION_ESP_OPEN','CLOUDTAVERNOPEN','CLOUDWIFIOPEN') ");
            parser.addSQL(" AND P.USER_ID = U.USER_ID ");
            parser.addSQL(" AND U.CUST_ID = G.CUST_ID ");
            parser.addSQL(" AND O.IBSYSID=P.IBSYSID ");
            parser.addSQL(" AND O.ATTR_CODE='AUDITSTAFF' ");
            parser.addSQL(" AND O.SUB_IBSYSID=(SELECT MAX(C.SUB_IBSYSID) FROM TF_BH_EOP_OTHER C WHERE C.IBSYSID=P.IBSYSID AND C.ATTR_CODE='AUDITSTAFF') ");
            parser.addSQL(" AND (:ACCEPT_START is null or to_char(E.CREATE_DATE, 'yyyymmddss') >= to_char(to_date(:ACCEPT_START, 'yyyy-mm-dd'), 'yyyymmddss')) ");
            parser.addSQL(" AND (:ACCEPT_END is null or to_char(E.CREATE_DATE, 'yyyymmddss') <= to_char(to_date(:ACCEPT_END, 'yyyy-mm-dd'), 'yyyymmddss')) ");
            parser.addSQL(" AND(:IBSYSID is null or P.IBSYSID=:IBSYSID) ");
            parser.addSQL(" AND(:STAFF_ID is null or O.ATTR_VALUE=:STAFF_ID) ");
            parser.addSQL(" AND(:GROUP_ID is null or G.GROUP_ID=:GROUP_ID) ");
            //去除产品变更
            parser.addSQL(" AND NOT EXISTS ( ");
            parser.addSQL(" SELECT 1 FROM TF_BH_EWE EE ");
            parser.addSQL(" WHERE EE.BI_SN=E.BI_SN AND EE.TEMPLET_TYPE='0' AND EE.BPM_TEMPLET_ID='MINORECSPEEDINESSCHANGE') ");
            parser.addSQL(" ORDER BY UPDATE_TIME DESC ");
        }
        else
        {
            parser.addSQL(" SELECT E.BUSIFORM_ID,E.BPM_TEMPLET_ID,E.ACCEPT_STAFF_ID, ");
            parser.addSQL(" P.IBSYSID,P.PRODUCT_NAME,P.UPDATE_TIME,P.RECORD_NUM, ");
            parser.addSQL(" O.ATTR_VALUE AUDITSTAFF, ");
            parser.addSQL(" G.CITY_CODE,G.GROUP_ID ");
            parser.addSQL(" FROM TF_BH_EOP_PRODUCT P,TF_BH_EWE E,TF_BH_EOP_OTHER O,TF_F_USER U,TF_F_CUST_GROUP G,TF_BH_EOP_OTHER A ");
            parser.addSQL(" WHERE TO_CHAR(E.BUSIFORM_ID) = P.RSRV_STR2 ");
            parser.addSQL(" AND E.BPM_TEMPLET_ID IN('SUMBUSINESSTVOPEN','FUSECOMMUNICATION_SUB_OPEN','FUSECOMMUNICATION_ESP_OPEN','CLOUDTAVERNOPEN','CLOUDWIFIOPEN') ");
            parser.addSQL(" AND P.USER_ID = U.USER_ID ");
            parser.addSQL(" AND U.CUST_ID = G.CUST_ID ");
            parser.addSQL(" AND O.IBSYSID=P.IBSYSID ");
            parser.addSQL(" AND O.ATTR_CODE='AUDITSTAFF' ");
            parser.addSQL(" AND O.SUB_IBSYSID=(SELECT MAX(C.SUB_IBSYSID) FROM TF_BH_EOP_OTHER C WHERE C.IBSYSID=P.IBSYSID AND C.ATTR_CODE='AUDITSTAFF')  ");
            parser.addSQL(" AND A.IBSYSID=P.IBSYSID ");
            parser.addSQL(" AND A.NODE_ID='auditInfo' ");
            parser.addSQL(" AND A.SUB_IBSYSID=(SELECT MAX(C.SUB_IBSYSID) FROM TF_BH_EOP_OTHER C WHERE C.IBSYSID=P.IBSYSID AND P.RECORD_NUM=C.RECORD_NUM AND C.NODE_ID='auditInfo') ");
            parser.addSQL(" AND (:ACCEPT_START is null or to_char(E.CREATE_DATE, 'yyyymmddss') >= to_char(to_date(:ACCEPT_START, 'yyyy-mm-dd'), 'yyyymmddss')) ");
            parser.addSQL(" AND (:ACCEPT_END is null or to_char(E.CREATE_DATE, 'yyyymmddss') <= to_char(to_date(:ACCEPT_END, 'yyyy-mm-dd'), 'yyyymmddss')) ");
            parser.addSQL(" AND(:IBSYSID is null or P.IBSYSID=:IBSYSID) ");
            parser.addSQL(" AND(:STAFF_ID is null or O.ATTR_VALUE=:STAFF_ID) ");
            parser.addSQL(" AND(:GROUP_ID is null or G.GROUP_ID=:GROUP_ID) ");
            //去除产品变更
            parser.addSQL(" AND NOT EXISTS ( ");
            parser.addSQL(" SELECT 1 FROM TF_BH_EWE EE ");
            parser.addSQL(" WHERE EE.BI_SN=E.BI_SN AND EE.TEMPLET_TYPE='0' AND EE.BPM_TEMPLET_ID='MINORECSPEEDINESSCHANGE') ");
            // 稽核通过
            if ("1".equals(auditResult))
            {
                parser.addSQL("AND A.ATTR_VALUE IN('8','4','1') ");
            }
            else
            {
                parser.addSQL("AND A.ATTR_VALUE IN('2','3','5''6','7') ");
            }
            parser.addSQL(" ORDER BY UPDATE_TIME DESC ");
        }
        IDataset workFormList = Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
        getDataLineWorkForm(workFormList);
        return workFormList;
    }
    
    /**
    * @Title: getDataLineWorkForm 
    * @Description: 拼接稽核信息 
    * @param workFormList
    * @author zhangzg
    * @date 2019年11月26日下午8:30:56 
     */
    public static IDataset getDataLineWorkForm(IDataset workFormList) throws Exception
    {
        if (IDataUtil.isNotEmpty(workFormList))
        {
            Iterator<Object> iterator = workFormList.iterator();
            while(iterator.hasNext()) {
                IData data = (IData) iterator.next();
                String ibsysId = data.getString("IBSYSID");
                String staffId = data.getString("AUDITSTAFF");
                String cityCode = data.getString("CITY_CODE");
                String recordNum = data.getString("RECORD_NUM");
                String bpmtempletId = data.getString("BPM_TEMPLET_ID");
                IData param = new DataMap();
                param.put("ATTR_CODE", "AUDITESOPINFO");
                param.put("IBSYSID", ibsysId);
                //查询业务类型
                String BPM_TEMPLET_NAME = "";
                // 若当前流程为子流程 则查询父流程
                IDataset tmpInfos = SubReleInfoQry.qryInfoByTemplet(bpmtempletId);
                String parentBpmTepletId = "";
                if (IDataUtil.isNotEmpty(tmpInfos))
                {
                    IDataset parentBpmtempletId = QryAuditInfoBean.qryParentBpmtempletIdByIbsysId(param);
                    if(IDataUtil.isNotEmpty(parentBpmtempletId)) {
                        parentBpmTepletId = parentBpmtempletId.getData(0).getString("BPM_TEMPLET_ID");
                    }
                }
                // 获取员工姓名
                String staffName = "";
                if (StringUtils.isNotEmpty(staffId))
                {
                    staffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
                }
                data.put("STAFF_NAME", staffName);
                // 获取归属地市
                String cityName = "";
                if (StringUtils.isNotEmpty(cityCode))
                {
                    cityName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                    { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
                    { "CUST_CITY_CODE", cityCode });
                }
                data.put("CITY_NAME", cityName);
                // 查询对应对应流程名
                BPM_TEMPLET_NAME = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                        { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
                                { "MINOREC_AUDIT_BPM", bpmtempletId });
                if (StringUtils.isBlank(BPM_TEMPLET_NAME) && StringUtils.isNotBlank(parentBpmTepletId))
                {
                    BPM_TEMPLET_NAME = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
                                    { "MINOREC_AUDIT_BPM", parentBpmTepletId });
                }
                data.put("BPM_TEMPLET_NAME", BPM_TEMPLET_NAME);
                // 查询是否已稽核
                IDataset auditResults = WorkformOtherHBean.qryOtherByIbsysidAttrCodeRecordNum(ibsysId, "AUDITESOPINFO", recordNum);
                String AUDIT_FLAG = "是";
                if (IDataUtil.isEmpty(auditResults))
                {
                    AUDIT_FLAG = "否";
                    data.put("AUDIT_FLAG", AUDIT_FLAG);
                    continue;
                }
                data.put("AUDIT_FLAG", AUDIT_FLAG);
                // 查询稽核结果
                param.put("RECORD_NUM",recordNum);
                IData auditValueInfo = qryAuditValueByIbsysId(param);
                String auditValue = "";
                if (IDataUtil.isNotEmpty(auditValueInfo))
                {
                    auditValue = auditValueInfo.getString("ATTR_VALUE");
                }
                String AUDIT_RESULT = "稽核不通过";
                if (StringUtils.isNotBlank(auditValue))
                {
                    if ("4".equals(auditValue) || "8".equals(auditValue) || "1".equals(auditValue))
                    {
                        AUDIT_RESULT = "稽核通过";
                    }
                }
                data.put("AUDIT_RESULT", AUDIT_RESULT);
                // 查询稽核信息
                param.put("BUSIFORM_ID", data.getString("BUSIFORM_ID"));
                IDataset auditRemarkList = queryRemarkInfo(param);
                if (IDataUtil.isNotEmpty(auditRemarkList))
                {
                    for (Object auditObj : auditRemarkList)
                    {
                        IData auditRemarkInfo = (IData) auditObj;
                        // 一次整改
                        if ("onceCorrection".equals(auditRemarkInfo.getString("NODE_ID")))
                        {
                            String onceRectifyTtme = auditRemarkInfo.getString("UPDATE_TIME");
                            String onceRectifyRemark = auditRemarkInfo.getString("ATTR_VALUE");
                            data.put("ONCE_RECTIFY_TIME", onceRectifyTtme);
                            data.put("ONCE_RECTIFY_REMARK", onceRectifyRemark);
                            continue;
                        } // 一次稽核
                        else if ("apply".equals(auditRemarkInfo.getString("NODE_ID")))
                        {
                            String onceAuditTtme = auditRemarkInfo.getString("UPDATE_TIME");
                            String onceAuditRemark = auditRemarkInfo.getString("ATTR_VALUE");
                            data.put("ONCE_AUDIT_TIME", onceAuditTtme);
                            data.put("ONCE_AUDIT_REMARK", onceAuditRemark);
                            continue;
                        } // 二次整改
                        else if ("twiceCorrection".equals(auditRemarkInfo.getString("NODE_ID")))
                        {
                            String twiceeRectifyTtme = auditRemarkInfo.getString("UPDATE_TIME");
                            String twiceRectifyRemark = auditRemarkInfo.getString("ATTR_VALUE");
                            data.put("TWICE_RECTIFY_TIME", twiceeRectifyTtme);
                            data.put("TWICE_RECITY_REMARK", twiceRectifyRemark);
                            continue;
                        } // 二次稽核
                        else if ("onceCorrectionCheck".equals(auditRemarkInfo.getString("NODE_ID")))
                        {
                            String twiceAuditTtme = auditRemarkInfo.getString("UPDATE_TIME");
                            String twiceAuditRemark = auditRemarkInfo.getString("ATTR_VALUE");
                            data.put("TWICE_AUDIT_TIME", twiceAuditTtme);
                            data.put("TWICE_AUDIT_REMARK", twiceAuditRemark);
                            continue;
                        } // 三次稽核
                        else if ("twiceCorrectionCheck".equals(auditRemarkInfo.getString("NODE_ID")))
                        {
                            String threeAuditTtme = auditRemarkInfo.getString("UPDATE_TIME");
                            String threeAuditRemark = auditRemarkInfo.getString("ATTR_VALUE");
                            data.put("THREE_AUDIT_TIME", threeAuditTtme);
                            data.put("THREE_AUDIT_REMARK", threeAuditRemark);
                            continue;
                        }
                    }
                }
            }
        }

        return workFormList;
    }
    
    /**
    * @Title: qryAuditValueByIbsysId 
    * @Description: 查询稽核结果 
    * @param IBSYSID
    * @author zhangzg
    * @date 2019年11月26日下午7:59:33
     */
    public static IData qryAuditValueByIbsysId(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("RECORD_NUM", param.getString("RECORD_NUM"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.ATTR_VALUE ");
        parser.addSQL(" FROM TF_BH_EOP_OTHER T ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.SUB_IBSYSID = (SELECT MAX(C.SUB_IBSYSID) ");
        parser.addSQL(" FROM TF_BH_EOP_OTHER C ");
        parser.addSQL(" WHERE C.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND C.NODE_ID = 'auditInfo' ");
        parser.addSQL(" AND C.RECORD_NUM =:RECORD_NUM) ");
        IDataset results = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if(IDataUtil.isNotEmpty(results)) {
            return results.first();
        }
        return null;
    }
    
    /**
    * @Title: queryRemarkInfo 
    * @Description: 查询稽核信息 
    * @param IBSYSID
    * @author zhangzg
    * @date 2019年11月26日下午7:59:30
     */
    public static IDataset queryRemarkInfo(IData data) throws Exception
    {
        IData params = new DataMap();
        params.put("IBSYSID", data.getString("IBSYSID"));
        params.put("BUSIFORM_ID", data.getString("BUSIFORM_ID"));
        SQLParser parser1 = new SQLParser(params);
        parser1.addSQL("SELECT T.IBSYSID,T.RSRV_STR1 ");
        parser1.addSQL("FROM TF_B_EOP_MODI_TRACE T ");
        parser1.addSQL("WHERE T.MAIN_IBSYSID=:IBSYSID ");
        parser1.addSQL("AND T.ATTR_MAIN_VALUE=:BUSIFORM_ID ");
        parser1.addSQL("UNION ");
        parser1.addSQL("SELECT T.IBSYSID,T.RSRV_STR1 ");
        parser1.addSQL("FROM TF_BH_EOP_MODI_TRACE T ");
        parser1.addSQL("WHERE T.MAIN_IBSYSID=:IBSYSID ");
        parser1.addSQL("AND T.ATTR_MAIN_VALUE=:BUSIFORM_ID ");
        IDataset ibsysInfo = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isNotEmpty(ibsysInfo))
        {
            String newIbsysid = ibsysInfo.getData(0).getString("IBSYSID");
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
            IDataset results = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
            if(IDataUtil.isNotEmpty(results)) {
                return results;
            }
        }
        return null;
    }

    public static IDataset qrySubRelaInfoByTemplet(IData data) throws Exception
    {
        IData param = new DataMap();
        param.put("BPM_TEMPLET_ID", data.getString("BPM_TEMPLET_ID"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.PARENT_BPM_TEMPLET_ID,T.PARENT_NODE_ID,T.BPM_TEMPLET_ID,T.VALID_TAG,T.IS_MULTI,T.RELA_SVC, ");
        parser.addSQL(" T.ACCEPT_DEPART_ID,T.UPDATE_DEPART_ID,T.UPDATE_STAFF_ID,T.ACCEPT_STAFF_ID,T.CREATE_DATE,T.UPDATE_DATE ");
        parser.addSQL(" FROM TD_B_EWE_SUB_RELA T ");
        parser.addSQL(" WHERE T.BPM_TEMPLET_ID = :BPM_TEMPLET_ID ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    public static IDataset qryRecordNumInEopProductByIbsysid(IData data) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", data.getString("IBSYSID"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.RECORD_NUM ");
        parser.addSQL(" FROM TF_B_EOP_PRODUCT T ");
        parser.addSQL(" WHERE T.RSRV_STR2 = (SELECT C.ATTR_MAIN_VALUE ");
        parser.addSQL(" FROM TF_B_EOP_MODI_TRACE C ");
        parser.addSQL(" WHERE C.IBSYSID =:IBSYSID) ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT T.RECORD_NUM ");
        parser.addSQL(" FROM TF_BH_EOP_PRODUCT T ");
        parser.addSQL(" WHERE T.RSRV_STR2 = (SELECT C.ATTR_MAIN_VALUE ");
        parser.addSQL(" FROM TF_B_EOP_MODI_TRACE C ");
        parser.addSQL(" WHERE C.IBSYSID =:IBSYSID) ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryEweReleBySubBusiformId(String subBusiformId) throws Exception {
        IData param = new DataMap();
        param.put("SUB_BUSIFORM_ID", subBusiformId);
        return Dao.qryByCode("TF_BH_EWE_RELE", "SEL_BY_SUBBUSIFORMID", param, Route.getJourDb(Route.getCrmDefaultDb()));
    }
    
    public static IData qryProductHByPk(String ibsysid, String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.IBSYSID,T.TRADE_ID,T.PRODUCT_TYPE_CODE,T.PRODUCT_ID,T.PRODUCT_NAME ");
        parser.addSQL(" FROM TF_BH_EOP_PRODUCT T ");
        parser.addSQL(" WHERE IBSYSID = :IBSYSID ");
        parser.addSQL(" AND RECORD_NUM = :RECORD_NUM ");
        IDataset productInfos = Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isNotEmpty(productInfos))
        {
            return productInfos.first();
        }
        return new DataMap();
    }
    
    public static IData qryProductByPk(String ibsysid, String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.IBSYSID,T.TRADE_ID,T.PRODUCT_TYPE_CODE,T.PRODUCT_ID,T.PRODUCT_NAME ");
        parser.addSQL(" FROM TF_B_EOP_PRODUCT T ");
        parser.addSQL(" WHERE IBSYSID = :IBSYSID ");
        parser.addSQL(" AND RECORD_NUM = :RECORD_NUM ");
        IDataset productInfos = Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isNotEmpty(productInfos))
        {
            return productInfos.first();
        }
        return new DataMap();
    }
    
    public static IData qryRecordNumByAuditIbsysid(String ibsysid) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_BH_EWE_RELE T ");
        parser.addSQL(" WHERE T.SUB_BUSIFORM_ID = (SELECT C.ATTR_MAIN_VALUE ");
        parser.addSQL(" FROM TF_B_EOP_MODI_TRACE C ");
        parser.addSQL(" WHERE C.IBSYSID =:IBSYSID) ");
        IDataset productInfos = Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isNotEmpty(productInfos))
        {
            return productInfos.first();
        }
        return new DataMap();
    }
    
    public static IDataset qryAuditInfoEweByBpmAndIbsysId(IData param) throws Exception
    {
        IData params = new DataMap();
        params.put("BUSIFORM_ID", param.getString("BUSIFORM_ID"));
        params.put("IBSYSID", param.getString("IBSYSID"));
        params.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.BUSIFORM_ID,T.BI_SN,T.ACCEPT_MONTH,T.BPM_TEMPLET_ID,T.TEMPLET_TYPE,T.BUSIFORM_OPER_TYPE,T.IN_MODE_CODE, ");
        parser.addSQL(" T.BUSI_TYPE,T.BUSI_CODE,T.REMARK,T.STATE,T.EPARCHY_CODE,T.CITY_CODE,T.ACCEPT_DEPART_ID,T.UPDATE_DEPART_ID,T.UPDATE_STAFF_ID, ");
        parser.addSQL(" T.ACCEPT_STAFF_ID,TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE,C.GROUP_ID,C.CUST_NAME,P.USER_ID USER_ID_A,P.SERIAL_NUMBER ");
        parser.addSQL(" FROM TF_B_EWE T,TF_B_EOP_SUBSCRIBE C,TF_B_EOP_PRODUCT P ");
        parser.addSQL(" WHERE T.BPM_TEMPLET_ID = :BPM_TEMPLET_ID ");
        parser.addSQL(" AND T.BI_SN = :IBSYSID ");
        parser.addSQL(" AND T.BUSIFORM_ID=:BUSIFORM_ID ");
        parser.addSQL(" AND C.IBSYSID = T.BI_SN ");
        parser.addSQL(" AND P.RSRV_STR2 = TO_CHAR(T.BUSIFORM_ID) ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT T.BUSIFORM_ID,T.BI_SN,T.ACCEPT_MONTH,T.BPM_TEMPLET_ID,T.TEMPLET_TYPE,T.BUSIFORM_OPER_TYPE,T.IN_MODE_CODE, ");
        parser.addSQL(" T.BUSI_TYPE,T.BUSI_CODE,T.REMARK,T.STATE,T.EPARCHY_CODE,T.CITY_CODE,T.ACCEPT_DEPART_ID,T.UPDATE_DEPART_ID,T.UPDATE_STAFF_ID, ");
        parser.addSQL(" T.ACCEPT_STAFF_ID,TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE, ");
        parser.addSQL(" TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE,C.GROUP_ID,C.CUST_NAME,P.USER_ID USER_ID_A,P.SERIAL_NUMBER ");
        parser.addSQL(" FROM TF_BH_EWE T,TF_BH_EOP_SUBSCRIBE C,TF_BH_EOP_PRODUCT P ");
        parser.addSQL(" WHERE T.BPM_TEMPLET_ID = :BPM_TEMPLET_ID ");
        parser.addSQL(" AND T.BI_SN = :IBSYSID ");
        parser.addSQL(" AND T.BUSIFORM_ID=:BUSIFORM_ID ");
        parser.addSQL(" AND C.IBSYSID = T.BI_SN ");
        parser.addSQL(" AND P.RSRV_STR2 = TO_CHAR(T.BUSIFORM_ID) ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryEspProductInfosBySerialNumber(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.ATTR_CODE, T.ATTR_VALUE ");
        parser.addSQL(" FROM TF_F_USER_ATTR T, TF_F_USER U ");
        parser.addSQL(" WHERE T.USER_ID = U.USER_ID ");
        parser.addSQL(" AND U.SERIAL_NUMBER =:SERIAL_NUMBER ");
        parser.addSQL(" AND T.INST_TYPE='P' ");
        IDataset productInfos = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        if(DataUtils.isNotEmpty(productInfos))
        {
            return productInfos;
        }
        return new DatasetList();
    }
    
    public static IDataset qrySubscribeInfoForAuditChg(String ibsysid) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.IBSYSID, T.BPM_TEMPLET_ID, T.BUSI_CODE, T.RSRV_STR7 ");
        parser.addSQL(" FROM TF_BH_EOP_SUBSCRIBE T ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.RSRV_STR7 = 'ChgWn' ");
        return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
    }
}
