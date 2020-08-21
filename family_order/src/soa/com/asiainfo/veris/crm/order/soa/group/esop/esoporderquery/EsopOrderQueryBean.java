package com.asiainfo.veris.crm.order.soa.group.esop.esoporderquery;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.staffdept.StaffDeptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;

public class EsopOrderQueryBean {

    public IDataset queryWaitCheck(IData param, Pagination pagination) throws Exception {
        boolean flag = false;
        String templetIdStr = "";
        if(StringUtils.isBlank(param.getString("SUB_TYPE_CODE"))) {
            flag = true;
            templetIdStr = getTempletId();
        }
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT DISTINCT A.BPM_TEMPLET_ID,A.IBSYSID,E.ATTR_VALUE PRODUCT_NO,B.ACCEPT_STAFF_ID, ");
        sql.addSQL(" A.RSRV_STR4 TITLE,C.DEAL_STAFF_ID,A.ACCEPT_TIME RECEIVE_TIME,TO_CHAR(ROUND((SYSDATE - A.ACCEPT_TIME) * 24, 1), 'FM999999990.0') || '小时' WAIT_TIME,'待归档' NODE_NAME ");
        sql.addSQL(" FROM TF_B_EOP_SUBSCRIBE A,TF_B_EWE B,TF_B_EWE_NODE C,TF_B_EWE_STEP D,TF_B_EOP_ATTR E ");
        sql.addSQL(" WHERE A.IBSYSID=B.BI_SN ");
        sql.addSQL(" AND A.IBSYSID=E.IBSYSID ");
        sql.addSQL(" AND B.BUSIFORM_ID=C.BUSIFORM_ID ");
        sql.addSQL(" AND C.BUSIFORM_NODE_ID=D.BUSIFORM_NODE_ID ");
        sql.addSQL(" AND e.attr_code='PRODUCTNO' ");
        sql.addSQL(" AND C.STATE NOT IN ('M','6') ");
        sql.addSQL(" AND B.TEMPLET_TYPE='0' ");
        sql.addSQL(" AND A.IBSYSID =:IBSYSID ");
        sql.addSQL(" AND E.ATTR_VALUE =:PRODUCT_NO ");
        sql.addSQL(" AND A.RSRV_STR4 LIKE '%'|| :TITLE || '%' ");
        if(flag) {
            sql.addSQL(" AND A.BPM_TEMPLET_ID IN (" + templetIdStr + ") ");
        } else {
            sql.addSQL(" AND A.BPM_TEMPLET_ID =:SUB_TYPE_CODE ");
        }
        if(StringUtils.isNotBlank(param.getString("STAFF_ID"))) {
            sql.addSQL(" AND C.DEAL_STAFF_ID =:STAFF_ID ");
            sql.addSQL(" union all ");
            sql.addSQL(" SELECT DISTINCT A.BPM_TEMPLET_ID,A.IBSYSID,E.ATTR_VALUE PRODUCT_NO,B.ACCEPT_STAFF_ID, ");
            sql.addSQL(" A.RSRV_STR4 TITLE,C.DEAL_STAFF_ID,A.ACCEPT_TIME RECEIVE_TIME,TO_CHAR(ROUND((SYSDATE - A.ACCEPT_TIME) * 24, 1), 'FM999999990.0') || '小时' WAIT_TIME,'待归档' NODE_NAME ");
            sql.addSQL(" FROM TF_B_EOP_SUBSCRIBE A,TF_B_EWE B,TF_B_EWE_NODE C,TF_B_EWE_STEP D,TF_B_EOP_ATTR E ");
            sql.addSQL(" WHERE A.IBSYSID=B.BI_SN ");
            sql.addSQL(" AND A.IBSYSID=E.IBSYSID ");
            sql.addSQL(" AND B.BUSIFORM_ID=C.BUSIFORM_ID ");
            sql.addSQL(" AND C.BUSIFORM_NODE_ID=D.BUSIFORM_NODE_ID ");
            sql.addSQL(" AND e.attr_code='PRODUCTNO' ");
            sql.addSQL(" AND C.STATE NOT IN ('M','6') ");
            sql.addSQL(" AND B.TEMPLET_TYPE='0' ");
            sql.addSQL(" AND A.IBSYSID =:IBSYSID ");
            sql.addSQL(" AND E.ATTR_VALUE =:PRODUCT_NO ");
            sql.addSQL(" AND A.RSRV_STR4 LIKE '%'|| :TITLE || '%' ");
            sql.addSQL(" AND C.DEAL_STAFF_ID IS NULL ");
            sql.addSQL(" AND B.ACCEPT_STAFF_ID =:STAFF_ID ");
            if(flag) {
                sql.addSQL(" AND A.BPM_TEMPLET_ID IN (" + templetIdStr + ") ");
            } else {
                sql.addSQL(" AND A.BPM_TEMPLET_ID =:SUB_TYPE_CODE ");
            }
        }

        IDataset qryList = Dao.qryByParse(sql, pagination, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isNotEmpty(qryList)) {
            for (int i = 0; i < qryList.size(); i++) {
                IData qryData = qryList.getData(i);
                qryData.put("FLOW_TEMPLET_TYPE", StaticUtil.getStaticValue("EOP_SUB_TYPE_CODE", qryData.getString("BPM_TEMPLET_ID")));
                String staffId = qryData.getString("DEAL_STAFF_ID");
                if(StringUtils.isBlank(staffId)) {
                    staffId = qryData.getString("ACCEPT_STAFF_ID");
                }
                IData inparam = new DataMap();
                inparam.put("STAFF_ID", staffId);
                IData staffData = StaffDeptInfoQry.getStaffInfo(inparam);
                qryData.put("STAFF_ID", staffData.getString("STAFF_ID"));
                qryData.put("STAFF_NAME", staffData.getString("STAFF_NAME"));
                qryData.put("STAFF_PHONE", staffData.getString("SERIAL_NUMBER"));

            }
        }

        return qryList;

    }

    public IData smsSender2DealStaff(IData param) throws Exception {
        String ibsysid = param.getString("IBSYSID", "");
        String title = param.getString("TITLE", "");
        String staff = param.getString("STAFF_ID", "");
        String staffSn = param.getString("STAFF_PHONE", "");
        if(StringUtils.isNotBlank(staffSn)) {

            //CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到工号" + staff + "的手机号码！");
            String smsContent = "工单号为[" + ibsysid + "]工单主题为[" + title + "]的工单已达到您的账号，当前为【ESOP待归档】节点，请及时处理。";
            IData smsdata = new DataMap();
            smsdata.put("EPARCHY_CODE", "0898");
            smsdata.put("RECV_OBJECT", staffSn);// 工号手机号码
            smsdata.put("NOTICE_CONTENT", smsContent);
            smsdata.put("RECV_ID", "-1");
            smsdata.put("SMS_TYPE_CODE", "20");// 用户办理业务通知
            smsdata.put("FORCE_START_TIME", "");
            smsdata.put("FORCE_END_TIME", "");
            smsdata.put("REMARK", "");
            SmsSend.insSms(smsdata);
        }
        return new DataMap();
    }

    public static IDataset queryCheckinForm(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(
                " select * from (select decode(a.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') SUB_TYPE, ");
        sql.addSQL("       a.bpm_templet_id SUB_TYPE_CODE,TOTAL,nvl(UNCHECKIN,0) UNCHECKIN,nvl(CHECKIN,0)   CHECKIN, ");
        sql.addSQL("        round(decode(total,0,0,nvl(CHECKIN,0)/total),2)*100||'%' CHECKIN_PER ");
        sql.addSQL("    from (select a.bpm_templet_id, count(1) total ");
        sql.addSQL("          from (select * from TF_B_EOP_SUBSCRIBE t1 ");
        sql.addSQL("                union all ");
        sql.addSQL("                select * from TF_BH_EOP_SUBSCRIBE t2 where t2.deal_state='9') a ");
        sql.addSQL("          where a.bpm_templet_id in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL("          and to_char(a.accept_time,'yyyymmdd')>=to_char(to_date(:BEGIN_DATE,'yyyy-MM-dd'),'yyyymmdd') ");
        sql.addSQL("          and to_char(a.accept_time,'yyyymmdd')<=to_char(to_date(:END_DATE,'yyyy-MM-dd'),'yyyymmdd') ");
        sql.addSQL("          group by a.bpm_templet_id) a, ");
        sql.addSQL("          (select a.bpm_templet_id, count(1) uncheckin from TF_B_EOP_SUBSCRIBE a ");
        sql.addSQL("           where a.bpm_templet_id in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL("           and to_char(a.accept_time,'yyyymmdd')>=to_char(to_date(:BEGIN_DATE,'yyyy-MM-dd'),'yyyymmdd') ");
        sql.addSQL("           and to_char(a.accept_time,'yyyymmdd')<=to_char(to_date(:END_DATE,'yyyy-MM-dd'),'yyyymmdd') ");
        sql.addSQL("           group by a.bpm_templet_id) b, ");
        sql.addSQL("          (select a.bpm_templet_id, count(1) checkin from TF_BH_EOP_SUBSCRIBE a where a.deal_state='9' ");
        sql.addSQL("           and a.bpm_templet_id in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL("           and to_char(a.accept_time,'yyyymmdd')>=to_char(to_date(:BEGIN_DATE,'yyyy-MM-dd'),'yyyymmdd') ");
        sql.addSQL("           and to_char(a.accept_time,'yyyymmdd')<=to_char(to_date(:END_DATE,'yyyy-MM-dd'),'yyyymmdd') ");
        sql.addSQL("           group by a.bpm_templet_id) c ");
        sql.addSQL("    where a.bpm_templet_id = b.bpm_templet_id(+) ");
        sql.addSQL("    and a.bpm_templet_id = c.bpm_templet_id(+) ");
        sql.addSQL("    order by decode(a.bpm_templet_id,'ERESOURCECONFIRMZHZG',1,'EDIRECTLINEOPENPBOSS',2,'ECHANGERESOURCECONFIRM',3,'EDIRECTLINECHANGEPBOSS',4,'DIRECTLINECHANGESIMPLE',5,'EDIRECTLINECHANGEFEE',6,'DIRECTLINECANCEL',7,8)) ");
        sql.addSQL(" union all ");
        sql.addSQL(" select '合计'  SUB_TYPE,'TOTAL'SUB_TYPE_CODE, TOTAL, nvl(UNCHECKIN,0) UNCHECKIN, nvl(CHECKIN,0)   CHECKIN, round(decode(total,0,0,nvl(CHECKIN,0)/total),2)*100||'%' CHECKIN_PER ");
        sql.addSQL(" from(select'total'  bid, count(1) total ");
        sql.addSQL("      from(select * from TF_B_EOP_SUBSCRIBE t1 ");
        sql.addSQL("           union all ");
        sql.addSQL("           select * from TF_BH_EOP_SUBSCRIBE t2 where t2.deal_state='9') a ");
        sql.addSQL("          where a.bpm_templet_id in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL("          and to_char(a.accept_time,'yyyymmdd')>=to_char(to_date(:BEGIN_DATE,'yyyy-MM-dd'),'yyyymmdd') ");
        sql.addSQL("          and to_char(a.accept_time,'yyyymmdd')<=to_char(to_date(:END_DATE,'yyyy-MM-dd'),'yyyymmdd')) a, ");
        sql.addSQL("         (select 'total'  bid, count(1) uncheckin from TF_B_EOP_SUBSCRIBE a ");
        sql.addSQL("          where a.bpm_templet_id in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL("          and to_char(a.accept_time,'yyyymmdd')>=to_char(to_date(:BEGIN_DATE,'yyyy-MM-dd'),'yyyymmdd') ");
        sql.addSQL("          and to_char(a.accept_time,'yyyymmdd')<=to_char(to_date(:END_DATE,'yyyy-MM-dd'),'yyyymmdd')) b, ");
        sql.addSQL("         (select 'total'  bid, count(1) checkin from TF_BH_EOP_SUBSCRIBE a where a.deal_state='9' ");
        sql.addSQL("          and a.bpm_templet_id in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL("          and to_char(a.accept_time,'yyyymmdd') >=to_char(to_date(:BEGIN_DATE,'yyyy-MM-dd'),'yyyymmdd') ");
        sql.addSQL("          and to_char(a.accept_time,'yyyymmdd') <=to_char(to_date(:END_DATE, 'yyyy-MM-dd'),'yyyymmdd')) c ");
        sql.addSQL(" where a.bid = b.bid(+) ");
        sql.addSQL(" and a.bid = c.bid(+) ");
        return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }

    //合计的发起总量
    public static IDataset showCheckinDetailTotalAll(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(
                " select decode(t.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') as SUB_TYPE, ");
        sql.addSQL(" t.IBSYSID,t.RSRV_STR4,t.BUSI_CODE PRODUCT_ID,t.GROUP_ID,to_char(t.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME,'未归档' as STATE ");
        sql.addSQL(" from tf_b_eop_subscribe t ");
        sql.addSQL(" where t.BPM_TEMPLET_ID in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') >= to_char(to_date(:START_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') <= to_char(to_date(:END_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" union all ");
        sql.addSQL(
                " select decode(t.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') as SUB_TYPE, ");
        sql.addSQL(" t.IBSYSID,t.RSRV_STR4,t.BUSI_CODE PRODUCT_ID,t.GROUP_ID,to_char(t.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME,'已归档' as STATE ");
        sql.addSQL(" from tf_bh_eop_subscribe t ");
        sql.addSQL(" where 1=1 ");
        sql.addSQL(" and t.BPM_TEMPLET_ID in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') >= to_char(to_date(:START_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') <= to_char(to_date(:END_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" and t.deal_state = '9'  ");
        IDataset result = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        return getProductName(result);
    }

    //合计的未归档
    public static IDataset showCheckinDetailUncheckinAll(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(
                " select decode(t.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') as SUB_TYPE, ");
        sql.addSQL(" t.IBSYSID,t.RSRV_STR4,t.BUSI_CODE PRODUCT_ID,t.GROUP_ID,to_char(t.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME,'未归档' as STATE ");
        sql.addSQL(" from tf_b_eop_subscribe t ");
        sql.addSQL(" where 1=1 ");
        sql.addSQL(" and t.BPM_TEMPLET_ID in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') >= to_char(to_date(:START_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') <= to_char(to_date(:END_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" order by t.accept_time desc ");
        IDataset result = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        return getProductName(result);
    }

    //合计的已归档
    public static IDataset showCheckinDetailCheckinAll(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(
                " select decode(t.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') as SUB_TYPE, ");
        sql.addSQL(" t.IBSYSID,t.RSRV_STR4,t.BUSI_CODE PRODUCT_ID,t.GROUP_ID,to_char(t.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME,'已归档' as STATE ");
        sql.addSQL(" from tf_bh_eop_subscribe t ");
        sql.addSQL(" where 1=1 ");
        sql.addSQL(" and t.BPM_TEMPLET_ID in ('ERESOURCECONFIRMZHZG','EDIRECTLINEOPENPBOSS','ECHANGERESOURCECONFIRM','EDIRECTLINECHANGEPBOSS','DIRECTLINECHANGESIMPLE','EDIRECTLINECHANGEFEE','DIRECTLINECANCEL') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') >= to_char(to_date(:START_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') <= to_char(to_date(:END_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" and t.deal_state = '9' ");
        sql.addSQL(" order by t.accept_time desc ");
        IDataset result = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        return getProductName(result);
    }

    //RESOURCECONFIRM（开通勘察）、CHANGERESOURCECONFIRM（变更勘察）等发起总量
    public static IDataset showCheckinDetailTotal(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(
                " select decode(t.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') as SUB_TYPE, ");
        sql.addSQL(" t.IBSYSID, ");
        sql.addSQL(" t.RSRV_STR4, ");
        sql.addSQL(" t.BUSI_CODE PRODUCT_ID, ");
        sql.addSQL(" t.GROUP_ID, ");
        sql.addSQL(" to_char(t.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        sql.addSQL(" '未归档' as STATE ");
        sql.addSQL(" from tf_b_eop_subscribe t ");
        sql.addSQL(" where t.BPM_TEMPLET_ID = :ROWS  ");
        sql.addSQL(" and to_char(t.accept_time, 'yyyymmdd') >= ");
        sql.addSQL(" to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmdd') ");
        sql.addSQL(" and to_char(t.accept_time, 'yyyymmdd') <= ");
        sql.addSQL(" to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmdd') ");
        sql.addSQL(" union all ");
        sql.addSQL(
                " select decode(t.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') as SUB_TYPE, ");
        sql.addSQL(" t.IBSYSID, ");
        sql.addSQL(" t.RSRV_STR4, ");
        sql.addSQL(" t.BUSI_CODE PRODUCT_ID, ");
        sql.addSQL(" t.GROUP_ID, ");
        sql.addSQL(" to_char(t.ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME, ");
        sql.addSQL(" '已归档' as STATE ");
        sql.addSQL(" from tf_bh_eop_subscribe t ");
        sql.addSQL(" where 1 = 1 ");
        sql.addSQL(" and t.BPM_TEMPLET_ID = :ROWS ");
        sql.addSQL(" and to_char(t.accept_time, 'yyyymmdd') >= ");
        sql.addSQL(" to_char(to_date(:START_DATE, 'yyyy-mm-dd'), 'yyyymmdd') ");
        sql.addSQL(" and to_char(t.accept_time, 'yyyymmdd') <= ");
        sql.addSQL(" to_char(to_date(:END_DATE, 'yyyy-mm-dd'), 'yyyymmdd') ");
        sql.addSQL(" and t.deal_state = '9'  ");
        IDataset result = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        return getProductName(result);
    }

    //RESOURCECONFIRM（开通勘察）、CHANGERESOURCECONFIRM（变更勘察）等未归档
    public static IDataset showCheckinDetailUncheckin(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(
                " select decode(t.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') as SUB_TYPE, ");
        sql.addSQL(" t.IBSYSID,t.RSRV_STR4,t.BUSI_CODE PRODUCT_ID,t.GROUP_ID,to_char(t.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME,'未归档' as STATE ");
        sql.addSQL(" from tf_b_eop_subscribe t ");
        sql.addSQL(" where 1=1 ");
        sql.addSQL(" and t.BPM_TEMPLET_ID = :ROWS ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') >= to_char(to_date(:START_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') <= to_char(to_date(:END_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" order by t.accept_time desc  ");
        IDataset result = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        return getProductName(result);
    }

    //RESOURCECONFIRM（开通勘察）、CHANGERESOURCECONFIRM（变更勘察）等已归档
    public static IDataset showCheckinDetailCheckin(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(
                " select decode(t.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') as SUB_TYPE, ");
        sql.addSQL(" t.IBSYSID,t.RSRV_STR4,t.BUSI_CODE PRODUCT_ID,t.GROUP_ID,to_char(t.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME,'已归档' as STATE ");
        sql.addSQL(" from tf_bh_eop_subscribe t ");
        sql.addSQL(" where 1=1 ");
        sql.addSQL(" and t.BPM_TEMPLET_ID = :ROWS ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') >= to_char(to_date(:START_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" and to_char(t.accept_time,'yyyymmdd') <= to_char(to_date(:END_DATE,'yyyy-mm-dd'),'yyyymmdd') ");
        sql.addSQL(" and t.deal_state = '9' ");
        sql.addSQL(" order by t.accept_time desc ");
        IDataset result = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        return getProductName(result);
    }

    private static IDataset getProductName(IDataset result) throws Exception {
        if(DataUtils.isNotEmpty(result)) {
            for (int i = 0; i < result.size(); i++) {
                IData data = result.getData(i);
                String productId = data.getString("PRODUCT_ID");
                String productName = UProductInfoQry.getProductNameByProductId(productId);
                data.put("PRODUCT_NAME", productName);
            }
        }
        return result;
    }

    public static IDataset queryIntfErrorFormInfo(IData param) throws Exception {
        String stepStr = getStepIdStr();
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" select a.bpm_templet_id SUB_TYPE_CODE,TOTAL,nvl(ERR_COUNT, 0) ERR_COUNT, ");
        sql.addSQL(" round(decode(total,0,0,nvl(ERR_COUNT,0)/total),2)*100||'%' ERR_COUNT_PER ");
        sql.addSQL(" FROM ( SELECT a.bpm_templet_id,COUNT(1) total FROM  ");
        sql.addSQL("          (SELECT DISTINCT a.* FROM  tf_b_eop_subscribe a ");
        sql.addSQL("           WHERE 1=1 ");
        sql.addSQL("           and a.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND a.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND a.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           union ALL ");
        sql.addSQL("           SELECT DISTINCT b.* FROM  tf_bh_eop_subscribe b ");
        sql.addSQL("           WHERE 1=1 ");
        sql.addSQL("           and b.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND  b.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND  b.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND  b.deal_state='9') a ");
        sql.addSQL("        group BY a.bpm_templet_id ) a, ");
        sql.addSQL("      ( SELECT t.bpm_templet_id,COUNT(1) err_count FROM ");
        sql.addSQL("          (SELECT DISTINCT a.* FROM tf_b_eop_subscribe a,tf_b_ewe b,TF_B_EWE_STEP c ");
        sql.addSQL("           WHERE a.ibsysid=b.bi_sn ");
        sql.addSQL("           AND b.busiform_id=c.busiform_id ");
        sql.addSQL("           AND a.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND a.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND a.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND c.state IN ('M','6') ");
        sql.addSQL("           AND c.step_id IN (" + stepStr + ") ");
        sql.addSQL("           union ALL ");
        sql.addSQL("           SELECT DISTINCT a.* FROM tf_bh_eop_subscribe a,tf_bh_ewe b,TF_Bh_EWE_STEP c ");
        sql.addSQL("           WHERE a.ibsysid=b.bi_sn ");
        sql.addSQL("           AND b.busiform_id=c.busiform_id ");
        sql.addSQL("           AND a.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND  a.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND  a.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND c.state IN ('M','6') ");
        sql.addSQL("           AND c.step_id IN (" + stepStr + ") ");
        sql.addSQL("           AND a.deal_state='9' ) t ");
        sql.addSQL("        group BY t.bpm_templet_id ) b ");
        sql.addSQL(" WHERE a.bpm_templet_id=b.bpm_templet_id(+) ");
        return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }

    private static String getStepIdStr() throws Exception {
        IData param = new DataMap();
        param.put("LOAD_SVC", "SS.WorkformNewWorkSVC.execute");
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT DISTINCT (t.step_id) FROM td_b_ewe_step t ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND T.LOAD_SVC=:LOAD_SVC ");
        IDataset stepList = Dao.qryByParse(sql, Route.CONN_CRM_CEN);
        String stepStr = "";
        if(DataUtils.isNotEmpty(stepList)) {
            for (int i = 0; i < stepList.size(); i++) {
                IData stepData = stepList.getData(i);
                stepStr += stepData.getString("STEP_ID") + ",";
            }
        }
        if(StringUtils.isNotBlank(stepStr)) {
            stepStr = stepStr.substring(0, stepStr.length() - 1);
        } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询数据失败,请检查td_b_ewe_step表派单服务配置！");
        }
        return stepStr;
    }

    public static IDataset queryDetailIntfErrorFormTotalInfo(IData param) throws Exception {
        String stepStr = getStepIdStr();
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" select DISTINCT a.bpm_templet_id,a.ibsysid,a.RSRV_STR4,a.GROUP_ID,a.BUSI_CODE PRODUCT_ID,a.ACCEPT_TIME ");
        sql.addSQL(" FROM ( SELECT DISTINCT a.bpm_templet_id,a.ibsysid,a.RSRV_STR4,a.GROUP_ID,a.BUSI_CODE,a.ACCEPT_TIME FROM  ");
        sql.addSQL("          (SELECT a.* FROM  tf_b_eop_subscribe a ");
        sql.addSQL("           WHERE 1=1 ");
        sql.addSQL("           and a.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND a.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND a.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           union ALL ");
        sql.addSQL("           SELECT b.* FROM  tf_bh_eop_subscribe b ");
        sql.addSQL("           WHERE 1=1 ");
        sql.addSQL("           and b.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND  b.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND  b.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND  b.deal_state='9') a) a, ");
        sql.addSQL("      ( SELECT DISTINCT a.bpm_templet_id,a.ibsysid,a.RSRV_STR4,a.GROUP_ID,a.BUSI_CODE,a.ACCEPT_TIME FROM ");
        sql.addSQL("          (SELECT a.* FROM tf_b_eop_subscribe a,tf_b_ewe b,TF_B_EWE_STEP c ");
        sql.addSQL("           WHERE a.ibsysid=b.bi_sn ");
        sql.addSQL("           AND b.busiform_id=c.busiform_id ");
        sql.addSQL("           AND a.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND a.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND a.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND c.state IN ('M','6') ");
        sql.addSQL("           AND c.step_id IN (" + stepStr + ") ");
        sql.addSQL("           union ALL ");
        sql.addSQL("           SELECT a.* FROM tf_bh_eop_subscribe a,tf_bh_ewe b,TF_Bh_EWE_STEP c ");
        sql.addSQL("           WHERE a.ibsysid=b.bi_sn ");
        sql.addSQL("           AND b.busiform_id=c.busiform_id ");
        sql.addSQL("           AND a.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND  a.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND  a.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND c.state IN ('M','6') ");
        sql.addSQL("           AND c.step_id IN (" + stepStr + ") ");
        sql.addSQL("           AND a.deal_state='9' ) a ) b  ");
        sql.addSQL(" WHERE a.bpm_templet_id=b.bpm_templet_id(+) ");
        IDataset total = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(total)) {
            return total;
        }
        for (int i = 0; i < total.size(); i++) {
            IData data = total.getData(i);
            String ibsysid = data.getString("IBSYSID");
            IDataset errorInfos = getErrorInfos(ibsysid, stepStr);
            if(DataUtils.isEmpty(errorInfos)) {
                errorInfos = getHErrorInfos(ibsysid, stepStr);
                if(DataUtils.isEmpty(errorInfos)) {
                    data.put("INFO", "");
                    continue;
                }
            }
            IData errorInfo = errorInfos.first();
            data.put("INFO", errorInfo.getString("LOG_INFO", "") + errorInfo.getString("LOG_INFO1", "") + errorInfo.getString("LOG_INFO2", "") + errorInfo.getString("LOG_INFO3", "") + errorInfo.getString("LOG_INFO4", ""));

        }
        return getProductName(total);
    }

    public static IDataset queryDetailIntfErrorFormInfo(IData param) throws Exception {
        String stepStr = getStepIdStr();
        SQLParser sql = new SQLParser(param);
        sql.addSQL("       SELECT DISTINCT a.bpm_templet_id,a.ibsysid,a.RSRV_STR4,a.GROUP_ID,a.BUSI_CODE PRODUCT_ID,a.ACCEPT_TIME FROM ");
        sql.addSQL("          (SELECT a.* FROM tf_b_eop_subscribe a,tf_b_ewe b,TF_B_EWE_STEP c ");
        sql.addSQL("           WHERE a.ibsysid=b.bi_sn ");
        sql.addSQL("           AND b.busiform_id=c.busiform_id ");
        sql.addSQL("           AND a.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND a.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND a.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND c.state IN ('M','6') ");
        sql.addSQL("           AND c.step_id IN (" + stepStr + ") ");
        sql.addSQL("           union ALL ");
        sql.addSQL("           SELECT a.* FROM tf_bh_eop_subscribe a,tf_bh_ewe b,TF_Bh_EWE_STEP c ");
        sql.addSQL("           WHERE a.ibsysid=b.bi_sn ");
        sql.addSQL("           AND b.busiform_id=c.busiform_id ");
        sql.addSQL("           AND a.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL("           AND  a.accept_time>=to_date(:BEGIN_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND  a.accept_time<=to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL("           AND c.state IN ('M','6') ");
        sql.addSQL("           AND c.step_id IN (" + stepStr + ") ");
        sql.addSQL("           AND a.deal_state='9' ) a  ");
        IDataset errorTotal = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(errorTotal)) {
            return errorTotal;
        }
        for (int i = 0; i < errorTotal.size(); i++) {
            IData data = errorTotal.getData(i);
            String ibsysid = data.getString("IBSYSID");
            IDataset errorInfos = getErrorInfos(ibsysid, stepStr);
            if(DataUtils.isEmpty(errorInfos)) {
                errorInfos = getHErrorInfos(ibsysid, stepStr);
                if(DataUtils.isEmpty(errorInfos)) {
                    data.put("INFO", "");
                    continue;
                }
            }
            IData errorInfo = errorInfos.first();
            data.put("INFO", errorInfo.getString("LOG_INFO", "") + errorInfo.getString("LOG_INFO1", "") + errorInfo.getString("LOG_INFO2", "") + errorInfo.getString("LOG_INFO3", "") + errorInfo.getString("LOG_INFO4", ""));
        }
        return getProductName(errorTotal);
    }

    public static IDataset getErrorInfos(String ibsysid, String stepStr) throws Exception {
        IData param = new DataMap();
        param.put("BI_SN", ibsysid);
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" select a.* from TF_B_EWE_ERROR_LOG a,tf_b_ewe b ");
        sql.addSQL(" where a.busiform_id=b.busiform_id ");
        sql.addSQL(" and b.BI_SN=:BI_SN ");
        sql.addSQL(" and a.step_id in (" + stepStr + ") ");
        return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset getHErrorInfos(String ibsysid, String stepStr) throws Exception {
        IData param = new DataMap();
        param.put("BI_SN", ibsysid);
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" select a.* from TF_BH_EWE_ERROR_LOG a,tf_bh_ewe b ");
        sql.addSQL(" where a.busiform_id=b.busiform_id ");
        sql.addSQL(" and b.BI_SN=:BI_SN ");
        sql.addSQL(" and a.step_id in (" + stepStr + ") ");
        return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static String getTempletId() throws Exception {
        IData param = new DataMap();
        param.put("TYPE_ID", "EOP_SUB_TYPE_CODE");
        IDataset templetList = Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPE", param, Route.CONN_CRM_CEN);
        String templet = "";
        if(DataUtils.isNotEmpty(templetList)) {
            //CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TYPE_ID=EOP_SUB_TYPE_CODE在TD_S_STATIC表中未查询到流程ID列表！");
            for (int i = 0; i < templetList.size(); i++) {
                IData templetData = templetList.getData(i);
                templet += "'" + templetData.getString("DATA_ID") + "',";
            }
            templet = templet.substring(0, templet.length() - 1);
        }
        return templet;
    }

    public static IData chckeLineData(IData param) throws Exception {

        IData result = new DataMap();
        result.put("FLAG", true);
        String ibsysid = param.getString("IBSYSID");
        IDataset directlinelist = param.getDataset("DIRECTLINE_DATA");
        IDataset dataLinelist = new DatasetList();

        for (int i = 0, size = directlinelist.size(); i < size; i++) {
            IDataset directline = directlinelist.getDataset(i);
            IData dataLine = new DataMap();
            for (int j = 0; j < directline.size(); j++) {
                dataLine.put(directline.getData(j).getString("ATTR_CODE"), directline.getData(j).getString("ATTR_VALUE"));
            }
            dataLinelist.add(dataLine);
        }
        IData input = new DataMap();
        input.put("IBSYSID", ibsysid);
        input.put("NODE_ID", "apply");
        IDataset oldLineInfos = WorkformAttrBean.getNewLineInfoList(input);

        if(oldLineInfos.size() != dataLinelist.size()) {
            result.put("FLAG", false);
            result.put("ERROR", "审核驳回不允许增减专线条数！");
        } else {
            for (int i = 0; i < dataLinelist.size(); i++) {
                IData dataLine = dataLinelist.getData(i);
                boolean isHave = true;
                for (int j = 0; j < oldLineInfos.size(); j++) {
                    IData oldLineInfo = oldLineInfos.getData(j);
                    String productNo = dataLine.getString("PRODUCTNO");
                    if(StringUtils.isBlank(productNo)){
                        productNo = dataLine.getString("pattr_PRODUCTNO");
                    }
                    if(StringUtils.equals(productNo, oldLineInfo.getString("PRODUCTNO"))) {
                        isHave = false;
                        break;
                    }
                }
                if(isHave) {
                    result.put("FLAG", false);
                    result.put("ERROR", "专线【" + dataLine.getString("PRODUCTNO") + "】不在审核驳回单中,请修改后提交！");
                    break;
                }
            }
        }

        return result;
    }
}
