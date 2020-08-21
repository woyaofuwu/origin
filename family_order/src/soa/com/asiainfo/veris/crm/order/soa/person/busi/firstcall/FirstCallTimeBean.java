
package com.asiainfo.veris.crm.order.soa.person.busi.firstcall;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSmsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class FirstCallTimeBean extends CSBizBean
{

    public int deleteFirstCallTime(String serialNuber, String userId) throws Exception
    {
        IData param = new DataMap();
        StringBuilder sql = new StringBuilder(150);
        param.put("SERIAL_NUMBER", serialNuber);
        param.put("USER_ID", userId);
        sql.append(" DELETE FROM TP_F_USER_FIRSTCALLTIME T WHERE T.SERIAL_NUMBER=:SERIAL_NUMBER AND T.USER_ID=:USER_ID ");
        return Dao.executeUpdate(sql, param);
    }

    public String getSmsContent(IData templateInfo, IData params) throws Exception
    {
        if (IDataUtil.isEmpty(templateInfo))
        {
            CSAppException.apperr(BizException.CRM_BIZ_3);
        }
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());

        StringBuilder sb = new StringBuilder();
        sb.append(templateInfo.getString("TEMPLATE_CONTENT1", ""));
        sb.append(templateInfo.getString("TEMPLATE_CONTENT2", ""));
        sb.append(templateInfo.getString("TEMPLATE_CONTENT3", ""));
        sb.append(templateInfo.getString("TEMPLATE_CONTENT4", ""));
        sb.append(templateInfo.getString("TEMPLATE_CONTENT5", ""));

        String templateContent = sb.toString();
        if (IDataUtil.isNotEmpty(params))
        {
            exector.prepare(params);// 模板变量解析
            templateContent = exector.applyTemplate(templateContent);
        }
        return templateContent;
    }

    public IDataset getTemplateIdByCode(String tradeTypeCode, String brandCode, String productId, String cancelTag, String eparchyCode, String inModeCode) throws Exception
    {
        IDataset smsTemplateInfos = TradeSmsInfoQry.getTradeSmsInfos(tradeTypeCode, brandCode, productId, cancelTag, eparchyCode, inModeCode, null);
        return smsTemplateInfos;
    }

    public IData getTemplateInfo(String templateId) throws Exception
    {
        IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId);
        return templateInfo;
    }

    public void insTiaRecv(String iv_sync_sequence, String userId, String iv_opendate,String bet_month) throws Exception
    {
        IData param = new DataMap();
        param.put("SYNC_SEQUENCE", iv_sync_sequence);
        param.put("USER_ID", userId);
        param.put("START_DATE", iv_opendate);
        param.put("BET_MONTH", bet_month);
        Dao.executeUpdateByCodeCode("TI_A_SYNC_RECV", "INS_TI_RECV", param,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * 通过partition_id,inst_id同步优惠表 @yanwu
     * @param iv_sync_sequence
     * @param userId
     * @param instId
     * @throws Exception
     */
    public void insTibDiscnt_instId(String iv_sync_sequence, String userId, String instId) throws Exception
    {
        IDataset UserDiscntInfos=UserDiscntInfoQry.quyUserDiscntByUserIdAndInstId(userId,instId);
        if(IDataUtil.isNotEmpty(UserDiscntInfos)){
        	for(int i=0;i<UserDiscntInfos.size();i++){
        		  IData param = UserDiscntInfos.getData(i);
        	      param.put("SYNC_SEQUENCE", iv_sync_sequence);
        	      param.put("USER_ID", userId);
        	      param.put("INST_ID", instId);
        	      param.put("PRODUCT_ID", param.getString("PRODUCT_ID","-1"));
                  param.put("PACKAGE_ID", param.getString("PACKAGE_ID","-1"));
        	     Dao.executeUpdateByCodeCode("TI_B_USER_DISCNT", "INS_TIBDISCNT_USERDISCNT_ID", param,Route.getJourDb(BizRoute.getRouteId()));
        	}
        } 
    }

    public void insTibDiscnt(String iv_sync_sequence, String userId) throws Exception
    {
        IDataset UserDiscntInfos=UserDiscntInfoQry.getSpecDiscnt(userId);
        if(IDataUtil.isNotEmpty(UserDiscntInfos)){
        	for(int i=0;i<UserDiscntInfos.size();i++){
        		IData param =UserDiscntInfos.getData(i);
                param.put("PRODUCT_ID", param.getString("PRODUCT_ID","-1"));
                param.put("PACKAGE_ID", param.getString("PACKAGE_ID","-1"));
                param.put("SYNC_SEQUENCE", iv_sync_sequence);
                param.put("USER_ID", userId);
        	    Dao.executeUpdateByCodeCode("TI_B_USER_DISCNT", "INS_TIBDISCNT_FROM_USERDISCNT", param,Route.getJourDb(BizRoute.getRouteId()));
        	}
        } 
    }

    public void insTibUser(String iv_sync_sequence, String userId) throws Exception
    {
    	IDataset UserInfos=UserInfoQry.getUserInfoAndproductInfoByuserInfo(userId);
        if(IDataUtil.isNotEmpty(UserInfos)){
        	for(int i=0;i<UserInfos.size();i++){
        		 IData param = UserInfos.getData(i);
        	     param.put("SYNC_SEQUENCE", iv_sync_sequence);
        	     param.put("USER_ID", userId);
        	     param.put("MODIFY_TAG", "8");
        	     param.put("TRADE_ID", "");
        	     Dao.executeUpdateByCodeCode("TI_B_USER", "INS_TI_B_USER", param,Route.getJourDb(BizRoute.getRouteId()));
        	}
        } 
       
    }
    
    public void insTiSync(String iv_sync_sequence) throws Exception
    {
        IData synchInfoData = new DataMap();
        synchInfoData.put("SYNC_SEQUENCE", iv_sync_sequence);
        String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);
        synchInfoData.put("SYNC_DAY", syncDay);
        synchInfoData.put("SYNC_TYPE", "0");
        synchInfoData.put("TRADE_ID", "0");
        synchInfoData.put("STATE", "0");
        synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
        synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        Dao.insert("TI_B_SYNCHINFO", synchInfoData,Route.getJourDb(BizRoute.getRouteId()));
    }

    public void insTradeH(String iv_cust_id, String iv_cust_name, String v_userid, String iv_serial_number, String iv_eparchy_code, String iv_city_code, String iv_product_id, String iv_brand_code, String iv_in_staff_id, String iv_in_depart_id)
            throws Exception
    {
        IData tradehInfo = new DataMap();
        tradehInfo.put("TRADE_ID", SeqMgr.getTradeId());
        tradehInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        tradehInfo.put("ORDER_ID", SeqMgr.getOrderId());
        tradehInfo.put("TRADE_TYPE_CODE", "86");
        tradehInfo.put("PRIORITY", "320");
        tradehInfo.put("SUBSCRIBE_TYPE", "0");
        tradehInfo.put("SUBSCRIBE_STATE", "9");
        tradehInfo.put("NEXT_DEAL_TAG", "0");
        tradehInfo.put("IN_MODE_CODE", "0");
        tradehInfo.put("CUST_ID", iv_cust_id);
        tradehInfo.put("CUST_NAME", iv_cust_name);
        tradehInfo.put("USER_ID", v_userid);
        tradehInfo.put("SERIAL_NUMBER", iv_serial_number);
        tradehInfo.put("NET_TYPE_CODE", "00");
        tradehInfo.put("EPARCHY_CODE", iv_eparchy_code);
        tradehInfo.put("CITY_CODE", iv_city_code);
        tradehInfo.put("PRODUCT_ID", iv_product_id);
        tradehInfo.put("BRAND_CODE", iv_brand_code);
        tradehInfo.put("CUST_ID_B", "0");
        tradehInfo.put("USER_ID_B", "0");
        tradehInfo.put("ACCT_ID_B", "0");
        tradehInfo.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        tradehInfo.put("TRADE_STAFF_ID", iv_in_staff_id);
        tradehInfo.put("TRADE_DEPART_ID", iv_in_depart_id);
        tradehInfo.put("TRADE_CITY_CODE", iv_city_code);
        tradehInfo.put("TRADE_EPARCHY_CODE", iv_eparchy_code);
        tradehInfo.put("TERM_IP", "10.0.0.0");
        tradehInfo.put("OPER_FEE", "0");
        tradehInfo.put("FOREGIFT", "0");
        tradehInfo.put("ADVANCE_PAY", "0");
        tradehInfo.put("FEE_STATE", "0");
        tradehInfo.put("FEE_TIME", SysDateMgr.getSysTime());
        tradehInfo.put("FEE_STAFF_ID", iv_in_staff_id);
        tradehInfo.put("PROCESS_TAG_SET", "0000000000000001100E10000000000000000000");
        tradehInfo.put("OLCOM_TAG", "1");
        tradehInfo.put("FINISH_DATE", SysDateMgr.getSysTime());
        tradehInfo.put("EXEC_TIME", SysDateMgr.getSysTime());
        tradehInfo.put("CANCEL_TAG", "0");
        tradehInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
        tradehInfo.put("UPDATE_STAFF_ID", iv_in_staff_id);
        tradehInfo.put("UPDATE_DEPART_ID", iv_in_depart_id);
        tradehInfo.put("PF_TYPE", "0");
        Dao.insert("TF_BH_TRADE", tradehInfo,Route.getJourDb(BizRoute.getRouteId()));
    }

    public void insTradeStaffH(String iv_cust_id, String iv_cust_name, String v_userid, String iv_serial_number, String iv_eparchy_code, String iv_city_code, String iv_product_id, String iv_brand_code, String iv_in_staff_id, String iv_in_depart_id)
            throws Exception
    {
        IData tradehInfo = new DataMap();
        tradehInfo.put("TRADE_ID", SeqMgr.getTradeId());
        tradehInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        tradehInfo.put("ORDER_ID", SeqMgr.getOrderId());
        tradehInfo.put("TRADE_TYPE_CODE", "86");
        tradehInfo.put("PRIORITY", "320");
        tradehInfo.put("SUBSCRIBE_TYPE", "0");
        tradehInfo.put("SUBSCRIBE_STATE", "9");
        tradehInfo.put("NEXT_DEAL_TAG", "0");
        tradehInfo.put("IN_MODE_CODE", "0");
        tradehInfo.put("CUST_ID", iv_cust_id);
        tradehInfo.put("CUST_NAME", iv_cust_name);
        tradehInfo.put("USER_ID", v_userid);
        tradehInfo.put("SERIAL_NUMBER", iv_serial_number);
        tradehInfo.put("NET_TYPE_CODE", "00");
        tradehInfo.put("EPARCHY_CODE", iv_eparchy_code);
        tradehInfo.put("CITY_CODE", iv_city_code);
        tradehInfo.put("PRODUCT_ID", iv_product_id);
        tradehInfo.put("BRAND_CODE", iv_brand_code);
        tradehInfo.put("CUST_ID_B", "0");
        tradehInfo.put("USER_ID_B", "0");
        tradehInfo.put("ACCT_ID_B", "0");
        tradehInfo.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        tradehInfo.put("TRADE_STAFF_ID", iv_in_staff_id);
        tradehInfo.put("TRADE_DEPART_ID", iv_in_depart_id);
        tradehInfo.put("TRADE_CITY_CODE", iv_city_code);
        tradehInfo.put("TRADE_EPARCHY_CODE", iv_eparchy_code);
        tradehInfo.put("TERM_IP", "10.0.0.0");
        tradehInfo.put("OPER_FEE", "0");
        tradehInfo.put("FOREGIFT", "0");
        tradehInfo.put("ADVANCE_PAY", "0");
        tradehInfo.put("FEE_STATE", "0");
        tradehInfo.put("FEE_TIME", SysDateMgr.getSysTime());
        tradehInfo.put("FEE_STAFF_ID", iv_in_staff_id);
        tradehInfo.put("PROCESS_TAG_SET", "0000000000000001100E10000000000000000000");
        tradehInfo.put("OLCOM_TAG", "1");
        tradehInfo.put("FINISH_DATE", SysDateMgr.getSysTime());
        tradehInfo.put("EXEC_TIME", SysDateMgr.getSysTime());
        tradehInfo.put("CANCEL_TAG", "0");
        tradehInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
        tradehInfo.put("UPDATE_STAFF_ID", iv_in_staff_id);
        tradehInfo.put("UPDATE_DEPART_ID", iv_in_depart_id);
        tradehInfo.put("PF_TYPE", "0");
        tradehInfo.put("DAY", SysDateMgr.getCurDay());
        Dao.insert("TF_BH_TRADE_STAFF", tradehInfo,Route.getJourDb(BizRoute.getRouteId()));
    }

    public void insUserExpand(IData input) throws Exception
    {
        Dao.insert("TF_F_USER_EXPAND", input);
    }

    public void transHisFirstCall(IData input) throws Exception
    {
        Dao.insert("TP_FH_USER_FIRSTCALLTIME", input);
    }

    public int updateCreditMinDate(String userId, String betMonth, String opendate) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("BET_MONTH", betMonth);
        param.put("UPDATE_TIME", opendate);
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE tf_o_addcredit_user SET start_date  = ADD_MONTHS(start_date, :BET_MONTH),end_date  = ADD_MONTHS(end_date, :BET_MONTH), ");
        sql.append(" update_time = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        sql.append(" WHERE user_id = :USER_ID ");
        return Dao.executeUpdate(sql, param);
    }

    public int updateUserExpandTime(String userId, String updateType, String remark) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("UPDATE_TYPE", updateType);
        param.put("REMARK", remark);
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TF_F_USER_EXPAND SET UPDATE_TIME = SYSDATE , REMARK = :REMARK ");
        sql.append(" WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        sql.append(" AND USER_ID = :USER_ID ");
        sql.append(" AND UPDATE_TYPE = :UPDATE_TYPE ");
        return Dao.executeUpdate(sql, param);
    }

    public int updUserDiscntDate(String iv_opendateStr,String iv_bet_month, String userId, String productId, String packageID) throws Exception
    {
        IData param = new DataMap();
        param.put("BET_MONTH", iv_bet_month);
        param.put("USER_ID", userId);
        param.put("START_DATE", iv_opendateStr);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageID);
        return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "UPD_DISCNT_BY_SALEACTIVE", param);
    }

    public int updUserDiscntEndDate(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "UPD_DISCNT_BY_COMPARA", param);
    }
    
    public int upd60676070UserDiscntStartDate(String userId ,String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("END_DATE", endDate);
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TF_F_USER_DISCNT A ");
        sql.append(" SET A.START_DATE = SYSDATE , A.END_DATE=decode(A.DISCNT_CODE,'6070',to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),A.END_DATE) ") ;
        sql.append(" WHERE A.DISCNT_CODE IN (6067,6070) ") ;
        sql.append(" AND A.USER_ID = :USER_ID ") ;
        sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
        sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ") ;
        return Dao.executeUpdate(sql, param);
    }
    
    public int del33753376UserDiscnt(String userId ,String iv_product_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        if("10003370".equals(iv_product_id) || "10003373".equals(iv_product_id)){
        	  StringBuilder sql = new StringBuilder();
              //sql.append(" DELETE FROM TF_F_USER_DISCNT T ");
              sql.append(" DELETE FROM TF_F_USER_DISCNT T WHERE T.DISCNT_CODE = 3375  AND T.USER_ID = :USER_ID ") ;
             return Dao.executeUpdate(sql, param);            
        }else{
        	StringBuilder sql = new StringBuilder();
            //sql.append(" DELETE FROM TF_F_USER_DISCNT T ");
            sql.append(" DELETE FROM TF_F_USER_DISCNT T WHERE T.DISCNT_CODE = 3376 AND T.USER_ID = :USER_ID ") ;
             return Dao.executeUpdate(sql, param);     
        }
    }
    
    public int del20160406UserDiscnt(String userId ,String iv_product_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM TF_F_USER_DISCNT T WHERE T.DISCNT_CODE = 20160406  AND T.USER_ID = :USER_ID ") ;
        return Dao.executeUpdate(sql, param);         
    }
    
    public int ins20160406UserDiscnt(String userId ,String iv_product_id  ,String iv_in_staff_id ,String iv_in_depart_id) throws Exception
    {
        IData param = new DataMap();
        if("10003373".equals(iv_product_id)){
        	String instId = SeqMgr.getInstId();
    		IData cond = new DataMap();
    		cond.put("USER_ID",userId);
    		cond.put("USER_ID_A","-1");
    		cond.put("PRODUCT_ID","-1");
    		cond.put("PACKAGE_ID","-1");
    		cond.put("DISCNT_CODE","20160406");
    		cond.put("SPEC_TAG","0");
    		cond.put("RELATION_TYPE_CODE","");
    		cond.put("INST_ID",instId);
    		cond.put("CAMPN_ID","");
    		cond.put("START_DATE",SysDateMgr.getSysTime());
    		cond.put("END_DATE",SysDateMgr.getAddMonthsLastDay(4, SysDateMgr.getSysTime()));
    		cond.put("SPEC_TAG","0");
    		cond.put("UPDATE_TIME",SysDateMgr.getSysTime());
    		cond.put("UPDATE_STAFF_ID",iv_in_staff_id);
    		cond.put("UPDATE_DEPART_ID",iv_in_depart_id);
    		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", cond);           
        }else{
        	String instId = SeqMgr.getInstId();
    		IData cond = new DataMap();
    		cond.put("USER_ID",userId);
    		cond.put("USER_ID_A","-1");
    		cond.put("PRODUCT_ID","-1");
    		cond.put("PACKAGE_ID","-1");
    		cond.put("DISCNT_CODE","20160406");
    		cond.put("SPEC_TAG","0");
    		cond.put("RELATION_TYPE_CODE","");
    		cond.put("INST_ID",instId);
    		cond.put("CAMPN_ID","");
    		cond.put("START_DATE",SysDateMgr.getSysTime());
    		cond.put("END_DATE",SysDateMgr.getAddMonthsLastDay(4, SysDateMgr.getSysTime()));
    		cond.put("SPEC_TAG","0");
    		cond.put("UPDATE_TIME",SysDateMgr.getSysTime());
    		cond.put("UPDATE_STAFF_ID",iv_in_staff_id);
    		cond.put("UPDATE_DEPART_ID",iv_in_depart_id);
    		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", cond);      
        }
        
    }
    
    public int ins33753376UserDiscnt(String userId ,String iv_product_id  ,String iv_in_staff_id ,String iv_in_depart_id) throws Exception
    {
        IData param = new DataMap();
        if("10003370".equals(iv_product_id) || "10003373".equals(iv_product_id)){
        	String instId = SeqMgr.getInstId();
    		IData cond = new DataMap();
    		cond.put("USER_ID",userId);
    		cond.put("USER_ID_A","-1");
    		cond.put("PRODUCT_ID",iv_product_id);
    		cond.put("PACKAGE_ID",iv_product_id);//产品ID和包ID一样
    		cond.put("DISCNT_CODE","3375");
    		cond.put("SPEC_TAG","0");
    		cond.put("RELATION_TYPE_CODE","");
    		cond.put("INST_ID",instId);
    		cond.put("CAMPN_ID","");
    		cond.put("START_DATE",SysDateMgr.getSysTime());
    		cond.put("END_DATE",SysDateMgr.getAddMonthsLastDay(1, SysDateMgr.getSysTime()));
    		cond.put("SPEC_TAG","0");
    		cond.put("UPDATE_TIME",SysDateMgr.getSysTime());
    		cond.put("UPDATE_STAFF_ID",iv_in_staff_id);
    		cond.put("UPDATE_DEPART_ID",iv_in_depart_id);
    		insOfferRel(cond);
    		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", cond);           
        }else{
        	String instId = SeqMgr.getInstId();
    		IData cond = new DataMap();
    		cond.put("USER_ID",userId);
    		cond.put("USER_ID_A","-1");
    		cond.put("PRODUCT_ID",iv_product_id);
    		cond.put("PACKAGE_ID",iv_product_id);//产品ID和包ID一样
    		cond.put("DISCNT_CODE","3376");
    		cond.put("SPEC_TAG","0");
    		cond.put("RELATION_TYPE_CODE","");
    		cond.put("INST_ID",instId);
    		cond.put("CAMPN_ID","");
    		cond.put("START_DATE",SysDateMgr.getSysTime());
    		cond.put("END_DATE",SysDateMgr.getAddMonthsLastDay(1, SysDateMgr.getSysTime()));
    		cond.put("SPEC_TAG","0");
    		cond.put("UPDATE_TIME",SysDateMgr.getSysTime());
    		cond.put("UPDATE_STAFF_ID",iv_in_staff_id);
    		cond.put("UPDATE_DEPART_ID",iv_in_depart_id);
    		insOfferRel(cond);
    		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", cond);      
        }
        
    }
    
    public int upd33703372UserDiscnt(String userId ,String iv_product_id  ,String iv_in_staff_id ,String iv_in_depart_id) throws Exception
    {
    	String start_date = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), 1);
        if("10003370".equals(iv_product_id)){
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("START_DATE", start_date);
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE TF_F_USER_DISCNT A ");
            sql.append(" SET A.START_DATE =  to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'), ") ;
            sql.append("     A.REMARK = '客户实名制首话单激活绑定' ") ;
            sql.append(" WHERE A.DISCNT_CODE IN (3370) ") ;
            sql.append(" AND A.USER_ID = :USER_ID ") ;
            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
            sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ") ;
            return Dao.executeUpdate(sql, param);        
        }else{
			IData param = new DataMap();
			param.put("USER_ID", userId);
			param.put("START_DATE", start_date);
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE TF_F_USER_DISCNT A ");
			sql.append(" SET A.START_DATE =  to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'), ");
			sql.append("     A.REMARK = '客户实名制首话单激活绑定' ") ;
			sql.append(" WHERE A.DISCNT_CODE IN (3372) ");
			sql.append(" AND A.USER_ID = :USER_ID ");
			sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
			sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ");
            return Dao.executeUpdate(sql, param);       
        }
        
    }
    
    /**
     * REQ201606070017 神州行超享卡优化规则（首月优惠及约定在网）
     * chenxy3 20160627
     * */
    public int upd33773378UserDiscnt(String userId ,String iv_product_id  ,String iv_in_staff_id ,String iv_in_depart_id) throws Exception
    {
    	String start_date = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), 1);
        if("10003373".equals(iv_product_id)){
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("START_DATE", start_date);
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE TF_F_USER_DISCNT A ");
            sql.append(" SET A.START_DATE =  to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'), ") ;
            sql.append("     A.REMARK = '客户实名制首话单激活绑定' ") ;
            sql.append(" WHERE A.DISCNT_CODE IN (3377) ") ;
            sql.append(" AND A.USER_ID = :USER_ID ") ;
            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
            sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ") ;
            return Dao.executeUpdate(sql, param);        
        }else{
			IData param = new DataMap();
			param.put("USER_ID", userId);
			param.put("START_DATE", start_date);
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE TF_F_USER_DISCNT A ");
			sql.append(" SET A.START_DATE =  to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'), ");
			sql.append("     A.REMARK = '客户实名制首话单激活绑定' ") ;
			sql.append(" WHERE A.DISCNT_CODE IN (3378) ");
			sql.append(" AND A.USER_ID = :USER_ID ");
			sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
			sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ");
            return Dao.executeUpdate(sql, param);       
        } 
    }
    /**
     * REQ201606070017 神州行超享卡优化规则（首月优惠及约定在网）
     * chenxy3 20160627
     * */
    public static IDataset check33753376Exist(String userId,String iv_product_id) throws Exception
    {  
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        SQLParser parser = new SQLParser(param);  
        if("10003370".equals(iv_product_id) || "10003373".equals(iv_product_id)){
        	parser.addSQL("select t.* FROM TF_F_USER_DISCNT T WHERE T.DISCNT_CODE = 3375  AND T.USER_ID = :USER_ID"); 
        }else{
        	parser.addSQL("select t.* FROM TF_F_USER_DISCNT T WHERE T.DISCNT_CODE = 3376  AND T.USER_ID = :USER_ID"); 
        }
    	return Dao.qryByParse(parser);
    }
    
    public int updUserDiscnt(String userId ,String iv_product_id  ,String discntCode ,String enableFlag ,String iv_in_staff_id ,String iv_in_depart_id) throws Exception
    {
    		String start_date = "";
    		if("0".equals(enableFlag))
    			start_date = SysDateMgr.getSysTime();
    		else
    			start_date = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), 1);
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("START_DATE", start_date);
            param.put("DISCNT_CODE", discntCode);
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE TF_F_USER_DISCNT A ");
            sql.append(" SET A.START_DATE =  to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') ") ;
            sql.append(" WHERE A.DISCNT_CODE IN (:DISCNT_CODE) ") ;
            sql.append(" AND A.USER_ID = :USER_ID ") ;
            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
            sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ") ;
            return Dao.executeUpdate(sql, param);        
    }
    
    /**
     * 
     * @param userId
     * @param iv_product_id
     * @param discntCode
     * @param enableFlag
     * @param strEndOffset
     * @param strEndUnit
     * @param strIsFlag
     * @param iv_in_staff_id
     * @param iv_in_depart_id
     * @throws Exception
     */
    public int updUserDiscntA(String userId, String iv_product_id,  String discntCode, 
    		                  String enableFlag, String strEndOffset, String strEndUnit,
    		                  String strIsFlag, String iv_in_staff_id, String iv_in_depart_id) throws Exception
    {
    	
    	IData input = new DataMap();
    	input.put("USER_ID", userId);
    	input.put("DISCNT_CODE", discntCode);
        SQLParser parser = new SQLParser(input);
        if(StringUtils.isBlank(strIsFlag))
        {
        	parser.addSQL("SELECT T.* FROM TF_F_USER_DISCNT T WHERE T.DISCNT_CODE = :DISCNT_CODE AND T.USER_ID = :USER_ID AND T.PARTITION_ID = MOD(:USER_ID, 10000) AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ORDER BY T.END_DATE DESC ");
        }
        else
        {
        	parser.addSQL("SELECT T.* FROM TF_F_USER_DISCNT T WHERE T.DISCNT_CODE = :DISCNT_CODE AND T.USER_ID = :USER_ID AND T.PARTITION_ID = MOD(:USER_ID, 10000) ORDER BY T.END_DATE DESC ");
        }
         
        IDataset idsDs = Dao.qryByParse(parser);
        if(IDataUtil.isNotEmpty(idsDs))
        {
        	IData idD = idsDs.getData(0);
        	String strStartDate = idD.getString("START_DATE");
        	String strEndDate = idD.getString("END_DATE");
        	/*String strInstID = idD.getString("INST_ID");
        	String strPartitionID = idD.getString("PARTITION_ID");
        	IData param = new DataMap();
        	StringBuilder sql = new StringBuilder();*/
        	
        	if("0".equals(enableFlag))
        	{
        		strStartDate = SysDateMgr.getSysTime();
        	}
        	else if("1".equals(enableFlag))
        	{
        		strStartDate = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), 1);
        	}
        	
        	if("0".equals(strEndUnit) && StringUtils.isNotBlank(strEndOffset))
        	{
        		strEndDate = SysDateMgr.endDateOffset(strStartDate, strEndOffset, "3");  //失效偏移单位：0:天 1:自然天 2:月 3:自然月 4:年 5:自然年
        	}
        	else if("1".equals(strEndUnit) && StringUtils.isNotBlank(strEndOffset))
        	{
        		strEndDate = SysDateMgr.endDateOffset(strStartDate, strEndOffset, "1");  //失效偏移单位：0:天 1:自然天 2:月 3:自然月 4:年 5:自然年
    		}
        	
        	idD.put("DISCNT_CODE", discntCode);
        	idD.put("START_DATE", strStartDate);
        	idD.put("END_DATE", strEndDate);
        	idD.put("REMARK", "TD_S_COMMPARA_9217配置激活的优惠");
    	
        	Dao.update("TF_F_USER_DISCNT", idD, new String[] { "INST_ID", "PARTITION_ID" });
        	//return 1;
        	
        } 
        
        return 1;
        
    }

    public int updUserModeDate(String openDate, String modModeFlag, String modDateFlag, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("OPEN_DATE", openDate);
        param.put("NEED_MODIFY_MODE", modModeFlag);
        param.put("NEED_MODIFY_DATE", modDateFlag);
        param.put("USER_ID", userId);
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TF_F_USER SET OPEN_DATE = to_date(:OPEN_DATE,'yyyy-mm-dd hh24:mi:ss'),ACCT_TAG = '0',FIRST_CALL_TIME = to_date(:OPEN_DATE,'yyyy-mm-dd hh24:mi:ss'), ");
        sql.append(" OPEN_MODE = DECODE(:NEED_MODIFY_MODE, '1', '0', OPEN_MODE),  ");
        sql.append(" IN_DATE = DECODE(:NEED_MODIFY_DATE, '1', to_date(:OPEN_DATE,'yyyy-mm-dd hh24:mi:ss'), IN_DATE), ");
        sql.append(" USER_TYPE_CODE = DECODE(USER_TYPE_CODE,'F','0',USER_TYPE_CODE) ");
        sql.append(" WHERE USER_ID = :USER_ID ");
        sql.append(" AND PARTITION_ID = MOD(:USER_ID, 10000) ");
        return Dao.executeUpdate(sql, param);
    }

    public void updUserSaleActive(String iv_opendateStr,String iv_bet_month, String userId, String productId, String packageID) throws Exception
    {
        IData param = new DataMap();
        param.put("BET_MONTH", iv_bet_month);
        param.put("USER_ID", userId);
        param.put("START_DATE", iv_opendateStr);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageID);
        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_SALEACTIVE_DATE", param);
    }

    public int updUserSaleDeposit(String iv_opendateStr,String iv_bet_month, String userId, String productId, String packageID) throws Exception
    {
        IData param = new DataMap();
        param.put("BET_MONTH", iv_bet_month);
        param.put("USER_ID", userId);
        param.put("START_DATE", iv_opendateStr);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageID);
        return Dao.executeUpdateByCodeCode("TF_F_USER_SALE_DEPOSIT", "UPD_SALDEPOSIT_DATE", param);
    }

    public int addTrade(String TRADE_ID,String ACCEPT_MONTH,String BATCH_ID,String ORDER_ID,String PROD_ORDER_ID,String BPM_ID,
			String CAMPN_ID,String TRADE_TYPE_CODE,String PRIORITY,String SUBSCRIBE_TYPE,String SUBSCRIBE_STATE,String NEXT_DEAL_TAG,
			String IN_MODE_CODE,String CUST_ID,String CUST_NAME,String USER_ID,String ACCT_ID,String SERIAL_NUMBER,
			String NET_TYPE_CODE,String EPARCHY_CODE,String CITY_CODE,String PRODUCT_ID,String BRAND_CODE,String CUST_ID_B,
			String USER_ID_B,String ACCT_ID_B,String SERIAL_NUMBER_B,String CUST_CONTACT_ID,String SERV_REQ_ID,String INTF_ID,
			String ACCEPT_DATE,String TRADE_STAFF_ID,String TRADE_DEPART_ID,String TRADE_CITY_CODE,String TRADE_EPARCHY_CODE,
			String TERM_IP,String OPER_FEE,String FOREGIFT,String ADVANCE_PAY,String INVOICE_NO,String FEE_STATE,String FEE_TIME,
			String FEE_STAFF_ID,String PROCESS_TAG_SET,String OLCOM_TAG,String FINISH_DATE,String EXEC_TIME,String EXEC_ACTION,
			String EXEC_RESULT,String EXEC_DESC,String CANCEL_TAG,String CANCEL_DATE,String CANCEL_STAFF_ID,String CANCEL_DEPART_ID,
			String CANCEL_CITY_CODE,String CANCEL_EPARCHY_CODE,String UPDATE_TIME,String UPDATE_STAFF_ID,String UPDATE_DEPART_ID,
			String REMARK,String RSRV_STR1,String RSRV_STR2,String RSRV_STR3,String RSRV_STR4,String RSRV_STR5,String RSRV_STR6,
			String RSRV_STR7,String RSRV_STR8,String RSRV_STR9,String RSRV_STR10) throws Exception{
		IData cond = new DataMap();
		cond.put("TRADE_ID",TRADE_ID);
		cond.put("ACCEPT_MONTH",ACCEPT_MONTH);
		cond.put("BATCH_ID",BATCH_ID);
		cond.put("ORDER_ID",ORDER_ID);
		cond.put("PROD_ORDER_ID",PROD_ORDER_ID);
		cond.put("BPM_ID",BPM_ID);
		cond.put("CAMPN_ID",CAMPN_ID);
		cond.put("TRADE_TYPE_CODE",TRADE_TYPE_CODE);
		cond.put("PRIORITY",PRIORITY);
		cond.put("SUBSCRIBE_TYPE",SUBSCRIBE_TYPE);
		cond.put("SUBSCRIBE_STATE",SUBSCRIBE_STATE);
		cond.put("NEXT_DEAL_TAG",NEXT_DEAL_TAG);
		cond.put("IN_MODE_CODE",IN_MODE_CODE);
		cond.put("CUST_ID",CUST_ID);
		cond.put("CUST_NAME",CUST_NAME);
		cond.put("USER_ID",USER_ID);
		cond.put("ACCT_ID",ACCT_ID);
		cond.put("SERIAL_NUMBER",SERIAL_NUMBER);
		cond.put("NET_TYPE_CODE",NET_TYPE_CODE);
		cond.put("EPARCHY_CODE",EPARCHY_CODE);
		cond.put("CITY_CODE",CITY_CODE);
		cond.put("PRODUCT_ID",PRODUCT_ID);
		cond.put("BRAND_CODE",BRAND_CODE);
		cond.put("CUST_ID_B",CUST_ID_B);
		cond.put("USER_ID_B",USER_ID_B);
		cond.put("ACCT_ID_B",ACCT_ID_B);
		cond.put("SERIAL_NUMBER_B",SERIAL_NUMBER_B);
		cond.put("CUST_CONTACT_ID",CUST_CONTACT_ID);
		cond.put("SERV_REQ_ID",SERV_REQ_ID);
		cond.put("INTF_ID",INTF_ID);
		cond.put("ACCEPT_DATE",ACCEPT_DATE);
		cond.put("TRADE_STAFF_ID",TRADE_STAFF_ID);
		cond.put("TRADE_DEPART_ID",TRADE_DEPART_ID);
		cond.put("TRADE_CITY_CODE",TRADE_CITY_CODE);
		cond.put("TRADE_EPARCHY_CODE",TRADE_EPARCHY_CODE);
		cond.put("TERM_IP",TERM_IP);
		cond.put("OPER_FEE",OPER_FEE);
		cond.put("FOREGIFT",FOREGIFT);
		cond.put("ADVANCE_PAY",ADVANCE_PAY);
		cond.put("INVOICE_NO",INVOICE_NO);
		cond.put("FEE_STATE",FEE_STATE);
		cond.put("FEE_TIME",FEE_TIME);
		cond.put("FEE_STAFF_ID",FEE_STAFF_ID);
		cond.put("PROCESS_TAG_SET",PROCESS_TAG_SET);
		cond.put("OLCOM_TAG",OLCOM_TAG);
		cond.put("FINISH_DATE",FINISH_DATE);
		cond.put("EXEC_TIME",EXEC_TIME);
		cond.put("EXEC_ACTION",EXEC_ACTION);
		cond.put("EXEC_RESULT",EXEC_RESULT);
		cond.put("EXEC_DESC",EXEC_DESC);
		cond.put("CANCEL_TAG",CANCEL_TAG);
		cond.put("CANCEL_DATE",CANCEL_DATE);
		cond.put("CANCEL_STAFF_ID",CANCEL_STAFF_ID);
		cond.put("CANCEL_DEPART_ID",CANCEL_DEPART_ID);
		cond.put("CANCEL_CITY_CODE",CANCEL_CITY_CODE);
		cond.put("CANCEL_EPARCHY_CODE",CANCEL_EPARCHY_CODE);
		cond.put("UPDATE_TIME",UPDATE_TIME);
		cond.put("UPDATE_STAFF_ID",UPDATE_STAFF_ID);
		cond.put("UPDATE_DEPART_ID",UPDATE_DEPART_ID);
		cond.put("REMARK",REMARK);
		cond.put("RSRV_STR1",RSRV_STR1);
		cond.put("RSRV_STR2",RSRV_STR2);
		cond.put("RSRV_STR3",RSRV_STR3);
		cond.put("RSRV_STR4",RSRV_STR4);
		cond.put("RSRV_STR5",RSRV_STR5);
		cond.put("RSRV_STR6",RSRV_STR6);
		cond.put("RSRV_STR7",RSRV_STR7);
		cond.put("RSRV_STR8",RSRV_STR8);
		cond.put("RSRV_STR9",RSRV_STR9);
		cond.put("RSRV_STR10",RSRV_STR10);
		return Dao.executeUpdateByCodeCode("TF_B_TRADE", "INS_TRADE", cond,Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public int addOrder(String order_id,String accept_month,String batch_id,String batch_count,String succ_total,
			String fail_total,String order_type_code,String trade_type_code,String priority,String order_state,
			String next_deal_tag,String in_mode_code,String cust_id,String cust_name,String pspt_type_code,
			String pspt_id,String eparchy_code,String city_code,String auth_code,String actor_name,
			String actor_phone,String actor_pspt_type_code,String actor_pspt_id,String accept_date,String trade_staff_id,
			String trade_depart_id,String trade_city_code,String trade_eparchy_code,String term_ip,String oper_fee,
			String foregift,String advance_pay,String invoice_no,String fee_state,String fee_time,String fee_staff_id,
			String process_tag_set,String finish_date,String exec_time,String exec_action,String exec_result,
			String exec_desc,String cust_idea,String hq_tag,String decompose_rule_id,String dispatch_rule_id,
			String cust_contact_id,String serv_req_id,String contract_id,String solution_id,String cancel_tag,
			String cancel_date,String cancel_staff_id,String cancel_depart_id,String cancel_city_code,
			String cancel_eparchy_code,String update_time,String update_staff_id,String update_depart_id,
			String remark,String rsrv_str1,String rsrv_str2,String rsrv_str3,String rsrv_str4,String rsrv_str5,
			String rsrv_str6,String rsrv_str7,String rsrv_str8,String rsrv_str9,String rsrv_str10) throws Exception{
		IData cond = new DataMap();
		cond.put("ORDER_ID",order_id);
		cond.put("ACCEPT_MONTH",accept_month);
		cond.put("BATCH_ID",batch_id);
		cond.put("BATCH_COUNT",batch_count);
		cond.put("SUCC_TOTAL",succ_total);
		cond.put("FAIL_TOTAL",fail_total);
		cond.put("ORDER_TYPE_CODE",order_type_code);
		cond.put("TRADE_TYPE_CODE",trade_type_code);
		cond.put("PRIORITY",priority);
		cond.put("ORDER_STATE",order_state);
		cond.put("NEXT_DEAL_TAG",next_deal_tag);
		cond.put("IN_MODE_CODE",in_mode_code);
		cond.put("CUST_ID",cust_id);
		cond.put("CUST_NAME",cust_name);
		cond.put("PSPT_TYPE_CODE",pspt_type_code);
		cond.put("PSPT_ID",pspt_id);
		cond.put("EPARCHY_CODE",eparchy_code);
		cond.put("CITY_CODE",city_code);
		cond.put("AUTH_CODE",auth_code);
		cond.put("ACTOR_NAME",actor_name);
		cond.put("ACTOR_PHONE",actor_phone);
		cond.put("ACTOR_PSPT_TYPE_CODE",actor_pspt_type_code);
		cond.put("ACTOR_PSPT_ID",actor_pspt_id);
		cond.put("ACCEPT_DATE",accept_date);
		cond.put("TRADE_STAFF_ID",trade_staff_id);
		cond.put("TRADE_DEPART_ID",trade_depart_id);
		cond.put("TRADE_CITY_CODE",trade_city_code);
		cond.put("TRADE_EPARCHY_CODE",trade_eparchy_code);
		cond.put("TERM_IP",term_ip);
		cond.put("OPER_FEE",oper_fee);
		cond.put("FOREGIFT",foregift);
		cond.put("ADVANCE_PAY",advance_pay);
		cond.put("INVOICE_NO",invoice_no);
		cond.put("FEE_STATE",fee_state);
		cond.put("FEE_TIME",fee_time);
		cond.put("FEE_STAFF_ID",fee_staff_id);
		cond.put("PROCESS_TAG_SET",process_tag_set);
		cond.put("FINISH_DATE",finish_date);
		cond.put("EXEC_TIME",exec_time);
		cond.put("EXEC_ACTION",exec_action);
		cond.put("EXEC_RESULT",exec_result);
		cond.put("EXEC_DESC",exec_desc);
		cond.put("CUST_IDEA",cust_idea);
		cond.put("HQ_TAG",hq_tag);
		cond.put("DECOMPOSE_RULE_ID",decompose_rule_id);
		cond.put("DISPATCH_RULE_ID",dispatch_rule_id);
		cond.put("CUST_CONTACT_ID",cust_contact_id);
		cond.put("SERV_REQ_ID",serv_req_id);
		cond.put("CONTRACT_ID",contract_id);
		cond.put("SOLUTION_ID",solution_id);
		cond.put("CANCEL_TAG",cancel_tag);
		cond.put("CANCEL_DATE",cancel_date);
		cond.put("CANCEL_STAFF_ID",cancel_staff_id);
		cond.put("CANCEL_DEPART_ID",cancel_depart_id);
		cond.put("CANCEL_CITY_CODE",cancel_city_code);
		cond.put("CANCEL_EPARCHY_CODE",cancel_eparchy_code);
		cond.put("UPDATE_TIME",update_time);
		cond.put("UPDATE_STAFF_ID",update_staff_id);
		cond.put("UPDATE_DEPART_ID",update_depart_id);
		cond.put("REMARK",remark);
		cond.put("RSRV_STR1",rsrv_str1);
		cond.put("RSRV_STR2",rsrv_str2);
		cond.put("RSRV_STR3",rsrv_str3);
		cond.put("RSRV_STR4",rsrv_str4);
		cond.put("RSRV_STR5",rsrv_str5);
		cond.put("RSRV_STR6",rsrv_str6);
		cond.put("RSRV_STR7",rsrv_str7);
		cond.put("RSRV_STR8",rsrv_str8);
		cond.put("RSRV_STR9",rsrv_str9);
		cond.put("RSRV_STR10",rsrv_str10);
		return Dao.executeUpdateByCodeCode("TF_B_ORDER", "INSERT_ORDER", cond,Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public int addTradeSvcState(String INST_ID,String TRADE_ID,String ACCEPT_MONTH,String USER_ID,String SERVICE_ID,
			String MAIN_TAG,String STATE_CODE,String START_DATE,String MODIFY_TAG) throws Exception{
		IData cond = new DataMap();
		cond.put("INST_ID",INST_ID);
		cond.put("TRADE_ID",TRADE_ID);
		cond.put("ACCEPT_MONTH",ACCEPT_MONTH);
		cond.put("USER_ID",USER_ID);
		cond.put("SERVICE_ID",SERVICE_ID);
		cond.put("MAIN_TAG",MAIN_TAG);
		cond.put("STATE_CODE",STATE_CODE);
		cond.put("START_DATE",START_DATE);
		cond.put("MODIFY_TAG",MODIFY_TAG);
		return Dao.executeUpdateByCodeCode("TF_B_TRADE_SVCSTATE", "INSERT_TRADE_SVCSTATE", cond,Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * @author yanwu
	 * @param userId
	 * @param iv_product_id
	 * @param iv_packge_id
	 * @param iv_in_staff_id
	 * @param iv_in_depart_id
	 * @return
	 * @throws Exception
	 */
	public int ins6633UserDiscnt(String userId, String iv_product_id, String iv_packge_id, 
								 String iv_in_staff_id, String iv_in_depart_id) throws Exception
    {
    	String instId = SeqMgr.getInstId();
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("USER_ID_A", "-1");
		cond.put("PRODUCT_ID", iv_product_id);
		cond.put("PACKAGE_ID", iv_packge_id);
		cond.put("DISCNT_CODE", "6633");
		cond.put("SPEC_TAG","0");
		cond.put("RELATION_TYPE_CODE", "");
		cond.put("INST_ID", instId);
		cond.put("CAMPN_ID", "");
		cond.put("START_DATE", SysDateMgr.getSysTime());
		cond.put("END_DATE", SysDateMgr.getTheLastTime());
		cond.put("UPDATE_TIME",SysDateMgr.getSysTime());
		cond.put("UPDATE_STAFF_ID",iv_in_staff_id);
		cond.put("UPDATE_DEPART_ID",iv_in_depart_id);
		insOfferRel(cond);
		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", cond);      
        
    }
	
	public int del9227UserDiscnt(IData param) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM TF_F_USER_DISCNT T WHERE T.DISCNT_CODE = :DISCNT_CODE  AND T.USER_ID = :USER_ID ") ;
        return Dao.executeUpdate(sql, param);         
    }
    
    public int ins9227UserDiscnt(IData param) throws Exception
    {
        String instId = SeqMgr.getInstId();
		IData cond = new DataMap();
		cond.put("USER_ID",param.getString("USER_ID"));
		cond.put("USER_ID_A","-1");
		cond.put("PRODUCT_ID","-1");
		cond.put("PACKAGE_ID","-1");
		cond.put("DISCNT_CODE",param.getString("DISCNT_CODE"));
		cond.put("SPEC_TAG","0");
		cond.put("RELATION_TYPE_CODE","");
		cond.put("INST_ID",instId);
		cond.put("CAMPN_ID","");
		cond.put("START_DATE",param.getString("START_DATE"));
		cond.put("END_DATE",param.getString("END_DATE"));
		cond.put("SPEC_TAG","0");
		cond.put("REMARK","首话单激活绑定");
		cond.put("UPDATE_TIME",SysDateMgr.getSysTime());
		cond.put("UPDATE_STAFF_ID",param.getString("UPDATE_STAFF_ID"));
		cond.put("UPDATE_DEPART_ID",param.getString("UPDATE_DEPART_ID"));
		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", cond);         
    }
    
    public int ins9227UserDiscntnew(IData param) throws Exception
    {
        String instId = SeqMgr.getInstId();
		IData cond = new DataMap();
		cond.put("USER_ID",param.getString("USER_ID"));
		cond.put("USER_ID_A","-1");
		cond.put("PRODUCT_ID","-1");
		cond.put("PACKAGE_ID","-1");
		cond.put("DISCNT_CODE",param.getString("DISCNT_CODE"));
		cond.put("SPEC_TAG","0");
		cond.put("RELATION_TYPE_CODE","");
		cond.put("INST_ID",instId);
		cond.put("CAMPN_ID","");
		cond.put("START_DATE",param.getString("START_DATE"));
		cond.put("END_DATE",param.getString("END_DATE"));
		cond.put("SPEC_TAG","0");
		cond.put("REMARK","首话单激活一次绑定");
		cond.put("UPDATE_TIME",SysDateMgr.getSysTime());
		cond.put("UPDATE_STAFF_ID",param.getString("UPDATE_STAFF_ID"));
		cond.put("UPDATE_DEPART_ID",param.getString("UPDATE_DEPART_ID"));
		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", cond);         
    }
    
    public int ins9228UserDiscntnew(IData param) throws Exception
    {
		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", param);         
    }
    
    public int insOfferRel(IData param) throws Exception
    {
        String instId = SeqMgr.getInstId();
        IData data = new DataMap();
		data.put("OFFER_CODE",param.getString("PRODUCT_ID","-1"));
		data.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
		data.put("OFFER_INS_ID",SeqMgr.getInstId());
		data.put("USER_ID",param.getString("USER_ID"));
		data.put("GROUP_ID", param.getString("PACKAGE_ID","-1"));
		data.put("REL_OFFER_CODE",param.getString("DISCNT_CODE"));
		data.put("REL_OFFER_TYPE",BofConst.ELEMENT_TYPE_CODE_DISCNT);
		data.put("REL_OFFER_INS_ID",param.getString("INST_ID"));
		data.put("REL_USER_ID",param.getString("USER_ID"));
		data.put("REL_TYPE",BofConst.OFFER_REL_TYPE_LINK);
		data.put("START_DATE",param.getString("START_DATE"));
		data.put("END_DATE",param.getString("END_DATE"));
		data.put("UPDATE_TIME",param.getString("UPDATE_TIME"));
		data.put("UPDATE_STAFF_ID",param.getString("UPDATE_STAFF_ID"));
		data.put("UPDATE_DEPART_ID",param.getString("UPDATE_DEPART_ID"));
		data.put("INST_ID", instId);
		data.put("REMARK", "");
		return Dao.executeUpdateByCodeCode("TF_F_USER_OFFER_REL", "INS_OFFER_REL", data);      
    }
    
    /**
     * REQ201904240016 预开户号码激活后，修改基础套餐及其必选优惠的开始时间
     * wangsc10 20190902--更新优惠的的开始时间
     * */
    public int updUserDiscntStartDate(String userId, String discntCode, String iv_opendate) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DISCNT_CODE", discntCode);
        param.put("START_DATE", iv_opendate);
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TF_F_USER_DISCNT A ");
        sql.append(" SET A.START_DATE = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),A.UPDATE_TIME = SYSDATE") ;
        sql.append(" WHERE A.USER_ID = :USER_ID") ;
        sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
        sql.append(" AND A.DISCNT_CODE = :DISCNT_CODE") ;
        sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ") ;
        Dao.executeUpdate(sql, param);
        return 1;
    }
    
    /**
     * REQ201904240016 预开户号码激活后，修改基础套餐及其必选优惠的开始时间
     * wangsc10 20190902--更新优惠的的开始时间
     * */
    public int updUserProductStartDate(String userId, String productId, String iv_opendate) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        param.put("START_DATE", iv_opendate);
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TF_F_USER_PRODUCT A ");
        sql.append(" SET A.START_DATE = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),A.UPDATE_TIME = SYSDATE") ;
        sql.append(" WHERE A.USER_ID = :USER_ID") ;
        sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
        sql.append(" AND A.PRODUCT_ID = :PRODUCT_ID") ;
        sql.append(" AND A.MAIN_TAG = '1'") ;
        sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ") ;
        Dao.executeUpdate(sql, param);
        return 1;
    }
    /**
     * REQ201904240016 预开户号码激活后，修改基础套餐及其必选优惠的开始时间
     * wangsc10 20190902--更新用户重要信息异动的开始时间
     * */
    public void updTfFUserInfochangeStartDate(String userId, String productId, String iv_opendate) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("START_DATE", iv_opendate);
        Dao.executeUpdateByCodeCode("TF_F_USER_INFOCHANGE", "UPD_START_USERINFO", inparam);
    }
    /**
     * REQ201904240016 预开户号码激活后，修改基础套餐及其必选优惠的开始时间
     * wangsc10 20190902--插入用户重要信息异动给账务
     * */
    public void updTiBUserInfochangeStartDate(String iv_sync_sequence,String userId, String productId) throws Exception
    {
        IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SYNC_SEQUENCE", iv_sync_sequence);
		param.put("PRODUCT_ID", productId);
		IDataset userInfoChanges = Dao.qryByCode("TF_F_USER_INFOCHANGE", "SEL_USERINFOCHANGE_BYUSERID", param);
		if (IDataUtil.isNotEmpty(userInfoChanges))
		{
			Dao.insert("TI_B_USER_INFOCHANGE", userInfoChanges, Route.getJourDbDefault());
		}
    }
}
