package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class ViceRealInfoReRegBean extends CSBizBean {
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public IDataset qryHdhSynInfo(IData input)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TP_F_HDH_SYNINFO WHERE SERIAL_NUMBER_B =:SERIAL_NUMBER_B and end_date >sysdate");
		
		return Dao.qryBySql(sql, input,Route.CONN_CRM_CEN); 
	}
	
	public IDataset qryHdhPicSynInfo(String seq)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TL_B_HDH_PICSYN WHERE SEQ_ID =:SEQ_ID ");
		
		IData input = new DataMap();
		input.put("SEQ_ID", seq);
		
		return Dao.qryBySql(sql, input,Route.CONN_CRM_CEN); 
	}
	
	public IDataset qryHdhSynInfoByPk(String seqId)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TP_F_HDH_SYNINFO WHERE SEQ_ID =:SEQ_ID");
		IData input = new DataMap();
		input.put("SEQ_ID", seqId);
		
		return Dao.qryBySql(sql, input,Route.CONN_CRM_CEN); 
	}
	
	public IDataset qryHdhSynInfo(String snb)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TP_F_HDH_SYNINFO WHERE SERIAL_NUMBER_B =:SERIAL_NUMBER_B and end_date >sysdate");
		
		IData input = new DataMap();
		input.put("SERIAL_NUMBER_B", snb);
		
		return Dao.qryBySql(sql, input,Route.CONN_CRM_CEN); 
	}
	
	public IDataset qryHdhSynInfo(String sna,String snb)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TP_F_HDH_SYNINFO WHERE SERIAL_NUMBER =:SERIAL_NUMBER and SERIAL_NUMBER_B =:SERIAL_NUMBER_B and end_date >sysdate");
		
		IData input = new DataMap();
		input.put("SERIAL_NUMBER", sna);
		input.put("SERIAL_NUMBER_B", snb);
		
		return Dao.qryBySql(sql, input,Route.CONN_CRM_CEN); 
	}
	
	public int updateViceAsynInfo(IData param)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE TP_F_HDH_SYNINFO ");
		sql.append("SET ADDRESS =:ADDRESS, ");
		sql.append("F_PICNAME_Z =:F_PICNAME_Z, ");
		sql.append("F_PICNAME_T =:F_PICNAME_T, ");
		sql.append("F_PICNAME_F =:F_PICNAME_F, ");
		sql.append("UPDATE_TIME =SYSDATE,UPDATE_STAFF_ID=:UPDATE_STAFF_ID,UPDATE_DEPART_ID=:UPDATE_DEPART_ID ");
		sql.append(" WHERE  end_date >sysdate and SERIAL_NUMBER =:SERIAL_NUMBER");
		sql.append(" AND SERIAL_NUMBER_B =:SERIAL_NUMBER_B ");
		
		return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
	}
	
	public int addViceAsynInfo(IData param)throws Exception
	{
		StringBuilder sql = new StringBuilder(2000);
		sql.append(" INSERT INTO TP_F_HDH_SYNINFO ");
		sql.append("(SEQ_ID,USER_ID, SERIAL_NUMBER, OPR_CODE, CUST_NAME, PROVINCE_CODE, CUST_TYPE, CATEGORY, PSPT_TYPE_CODE,  ");
		sql.append("PSPT_ID,ADDRESS, F_PICNAME_T,F_PICNAME_Z,F_PICNAME_F,BRAND_CODE,F_PROVINCE_CODE,U_PARTITION_ID,  ");
		sql.append("USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE, ROLE_TYPE_CODE, ROLE_CODE_A, ROLE_CODE_B,ORDERNO,  ");
		sql.append("SHORT_CODE,INST_ID,U_START_DATE,U_END_DATE, START_DATE,END_DATE, IN_STAFF_ID,IN_DEPART_ID,  ");
		sql.append("UPDATE_TIME,UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2,RSRV_NUM3,  ");
		sql.append("RSRV_STR1,RSRV_STR2, RSRV_STR3, RSRV_DATE1,RSRV_DATE2,RSRV_DATE3, RSRV_TAG1, RSRV_TAG2,RSRV_TAG3,SYN_TIME,PLAT_SEQ_ID) ");
		//========================================================
		sql.append("VALUES(:SEQ_ID,:USER_ID,:SERIAL_NUMBER,:OPR_CODE,:CUST_NAME,:PROVINCE_CODE,:CUST_TYPE,:CATEGORY,:PSPT_TYPE_CODE, ");
		sql.append(":PSPT_ID,:ADDRESS,:F_PICNAME_T,:F_PICNAME_Z,:F_PICNAME_F,:BRAND_CODE,:F_PROVINCE_CODE,:U_PARTITION_ID, ");
		sql.append(":USER_ID_B,:SERIAL_NUMBER_B,:RELATION_TYPE_CODE,:ROLE_TYPE_CODE,:ROLE_CODE_A,:ROLE_CODE_B,:ORDERNO, ");
		sql.append(":SHORT_CODE,:INST_ID,to_date(:U_START_DATE,'yyyy-mm-dd hh24:mi:ss'),to_date(:U_END_DATE,'yyyy-mm-dd hh24:mi:ss'),to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),:IN_STAFF_ID,:IN_DEPART_ID, ");
		sql.append("sysdate,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,:REMARK,:RSRV_NUM1,:RSRV_NUM2,:RSRV_NUM3, ");
		sql.append(":RSRV_STR1,:RSRV_STR2,:RSRV_STR3,:RSRV_DATE1,:RSRV_DATE2,:RSRV_DATE3,:RSRV_TAG1,:RSRV_TAG2,:RSRV_TAG3,sysdate,:PLAT_SEQ_ID) ");

		return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
	}
	
	public int endViceAsynInfo(IData param)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE TP_F_HDH_SYNINFO ");
		sql.append("SET END_DATE =SYSDATE,UPDATE_TIME=SYSDATE,U_END_DATE=to_date(:U_END_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		sql.append(" WHERE  END_DATE >sysdate and SERIAL_NUMBER =:SERIAL_NUMBER");
		sql.append(" AND SERIAL_NUMBER_B =:SERIAL_NUMBER_B ");
		
		return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
	}
	
	
	public void changeViceRealInfo(String fMsisdn,String addr,String custName,String psptType,String psptId)throws Exception
	{
		IData custInfoCharge = new DataMap();
		custInfoCharge.put("SERIAL_NUMBER", fMsisdn);
		UcaData uca = UcaDataFactory.getNormalUca(fMsisdn);
		custInfoCharge.putAll(uca.getCustPerson().toData());
		custInfoCharge.putAll(uca.getCustomer().toData());
		custInfoCharge.put("PSPT_ADDR", addr);
		custInfoCharge.put("IS_REAL_NAME", "1");
		custInfoCharge.put("CUST_NAME", custName);
		custInfoCharge.put("PSPT_ID", psptId);
		custInfoCharge.put("PSPT_TYPE_CODE", psptType);
		CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", custInfoCharge);
	}
	
	public void chgBackViceRealInfo(String fMsisdn)throws Exception
	{
		IData custInfoCharge = new DataMap();
		custInfoCharge.put("SERIAL_NUMBER", fMsisdn);
		UcaData uca = UcaDataFactory.getNormalUca(fMsisdn);
		custInfoCharge.putAll(uca.getCustPerson().toData());
		custInfoCharge.putAll(uca.getCustomer().toData());
		custInfoCharge.put("PSPT_ADDR", "");
		custInfoCharge.put("CUST_NAME", uca.getCustPerson().getRsrvStr6());
		custInfoCharge.put("PSPT_ID", uca.getCustPerson().getRsrvStr5());
		custInfoCharge.put("PSPT_TYPE_CODE", uca.getCustPerson().getRsrvStr4());
		CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", custInfoCharge);
	}
	
	public void chgBackViceRealInfo(BusiTradeData btd,String fMsisdn,String fCustName,String fPsType,String fPsId)throws Exception
	{
		UcaData uca = UcaDataFactory.getNormalUca(fMsisdn);
		
		CustomerTradeData customerTd = uca.getCustomer();
		customerTd.setModifyTag(BofConst.MODIFY_TAG_UPD);
		customerTd.setCustName(fCustName);
		customerTd.setPsptId(fPsId);
		customerTd.setPsptTypeCode(fPsType);
		
		btd.add(fMsisdn, customerTd);
		
		CustPersonTradeData custPersonTd = uca.getCustPerson();
		custPersonTd.setModifyTag(BofConst.MODIFY_TAG_UPD);
		custPersonTd.setCustName(fCustName);
		custPersonTd.setPsptId(fPsId);
		custPersonTd.setPsptTypeCode(fPsType);
		
		btd.add(fMsisdn, custPersonTd);
	}
	
	public void chgBackViceRealInfo(String fMsisdn,String fCustName,String fPsType,String fPsId)throws Exception
	{
		IData custInfoCharge = new DataMap();
		custInfoCharge.put("SERIAL_NUMBER", fMsisdn);
		UcaData uca = UcaDataFactory.getNormalUca(fMsisdn);
		custInfoCharge.putAll(uca.getCustPerson().toData());
		custInfoCharge.putAll(uca.getCustomer().toData());
		custInfoCharge.put("PSPT_ADDR", "");
		custInfoCharge.put("CUST_NAME", fCustName);
		custInfoCharge.put("PSPT_ID", fPsId);
		custInfoCharge.put("PSPT_TYPE_CODE", fPsType);
		CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", custInfoCharge);
	}
	
	public void picInfoSyn(IData input) throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE TL_B_HDH_PICSYN ");
		sql.append("SET RESULT_STATE =:RESULT_STATE ");
		sql.append("WHERE SEQ_ID =:SEQ_ID ");
		Dao.executeUpdate(sql, input, Route.CONN_CRM_CEN);		
	}

}
