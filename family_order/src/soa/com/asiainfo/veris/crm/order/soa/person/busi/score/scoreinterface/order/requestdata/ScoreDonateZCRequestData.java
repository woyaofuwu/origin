package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ScoreDonateZCRequestData extends  BaseReqData{
	
	public String getSERIAL_NUMBER() {
		return SERIAL_NUMBER;
	}
	public void setSERIAL_NUMBER(String sERIAL_NUMBER) {
		SERIAL_NUMBER = sERIAL_NUMBER;
	}
	public String getOBJECT_SERIAL_NUMBER() {
		return OBJECT_SERIAL_NUMBER;
	}
	public void setOBJECT_SERIAL_NUMBER(String oBJECT_SERIAL_NUMBER) {
		OBJECT_SERIAL_NUMBER = oBJECT_SERIAL_NUMBER;
	}
	public String getDONATE_SCORE() {
		return DONATE_SCORE;
	}
	public void setDONATE_SCORE(String dONATE_SCORE) {
		DONATE_SCORE = dONATE_SCORE;
	}
	public String getVALID_DATE() {
		return VALID_DATE;
	}
	public void setVALID_DATE(String vALID_DATE) {
		VALID_DATE = vALID_DATE;
	}
	public String getSCORE() {
		return SCORE;
	}
	public void setSCORE(String sCORE) {
		SCORE = sCORE;
	}
	public String getSCORE_TYPE_CODE() {
		return SCORE_TYPE_CODE;
	}
	public void setSCORE_TYPE_CODE(String sCORE_TYPE_CODE) {
		SCORE_TYPE_CODE = sCORE_TYPE_CODE;
	}
	private String SERIAL_NUMBER = ""; //赠送号码
	private String OBJECT_SERIAL_NUMBER= ""; //被赠送号码
	private String DONATE_SCORE = ""; //赠送积分
	private String VALID_DATE = ""; //有效期
	private String SCORE_TYPE_CODE = "";
	private String SCORE = "";
	public String getA_TRADE_TYPE_CODE() {
		return A_TRADE_TYPE_CODE;
	}
	public void setA_TRADE_TYPE_CODE(String a_TRADE_TYPE_CODE) {
		A_TRADE_TYPE_CODE = a_TRADE_TYPE_CODE;
	}
	public String getB_TRADE_TYPE_CODE() {
		return B_TRADE_TYPE_CODE;
	}
	public void setB_TRADE_TYPE_CODE(String b_TRADE_TYPE_CODE) {
		B_TRADE_TYPE_CODE = b_TRADE_TYPE_CODE;
	}
	private String A_TRADE_TYPE_CODE = "";
	private String B_TRADE_TYPE_CODE = "";
	
}
