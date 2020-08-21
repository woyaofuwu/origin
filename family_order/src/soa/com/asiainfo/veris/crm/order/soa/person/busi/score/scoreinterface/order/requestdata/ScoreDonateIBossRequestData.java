
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ScoreDonateIBossRequestData extends BaseReqData
{
    private String TRADE_SEQ = ""; // 交易流水号

    private String TRADE_TIME = ""; // 交易时间

    private String ORGID = ""; // 渠道标识

    private String L_MOBILE = ""; // 转让人手机号码

    private String B_MOBILE = ""; // 受让人手机号码
    
    private String TRANSFER_POINT = ""; // 转赠积分值

    private String CC_PASSWD = ""; // 客服密码
    
    private String COMMENTS = ""; // 备注说明
    
    private String SCORE = ""; // 转让人原积分
    
    private String SUM_SCORE = ""; // 转让人总积分
    
    private String OBJ_SCORE = ""; // 受让人原积分
    
    private String OBJ_SUM_SCORE = ""; // 受让人总积分

    private String OBJ_USERID = ""; // 受让人原积分
    
	public String getTRADE_SEQ() {
		return TRADE_SEQ;
	}

	public void setTRADE_SEQ(String tRADE_SEQ) {
		TRADE_SEQ = tRADE_SEQ;
	}

	public String getTRADE_TIME() {
		return TRADE_TIME;
	}

	public void setTRADE_TIME(String tRADE_TIME) {
		TRADE_TIME = tRADE_TIME;
	}

	public String getORGID() {
		return ORGID;
	}

	public void setORGID(String oRGID) {
		ORGID = oRGID;
	}

	public String getL_MOBILE() {
		return L_MOBILE;
	}

	public void setL_MOBILE(String l_MOBILE) {
		L_MOBILE = l_MOBILE;
	}

	public String getB_MOBILE() {
		return B_MOBILE;
	}

	public void setB_MOBILE(String b_MOBILE) {
		B_MOBILE = b_MOBILE;
	}

	public String getTRANSFER_POINT() {
		return TRANSFER_POINT;
	}

	public void setTRANSFER_POINT(String tRANSFER_POINT) {
		TRANSFER_POINT = tRANSFER_POINT;
	}

	public String getCC_PASSWD() {
		return CC_PASSWD;
	}

	public void setCC_PASSWD(String cC_PASSWD) {
		CC_PASSWD = cC_PASSWD;
	}

	public String getCOMMENTS() {
		return COMMENTS;
	}

	public void setCOMMENTS(String cOMMENTS) {
		COMMENTS = cOMMENTS;
	}

	public String getSCORE() {
		return SCORE;
	}

	public void setSCORE(String sCORE) {
		SCORE = sCORE;
	}

	public String getOBJ_SCORE() {
		return OBJ_SCORE;
	}

	public void setOBJ_SCORE(String oBJ_SCORE) {
		OBJ_SCORE = oBJ_SCORE;
	}

	public String getOBJ_USERID() {
		return OBJ_USERID;
	}

	public void setOBJ_USERID(String oBJ_USERID) {
		OBJ_USERID = oBJ_USERID;
	}

	public String getSUM_SCORE() {
		return SUM_SCORE;
	}

	public void setSUM_SCORE(String sUM_SCORE) {
		SUM_SCORE = sUM_SCORE;
	}

	public String getOBJ_SUM_SCORE() {
		return OBJ_SUM_SCORE;
	}

	public void setOBJ_SUM_SCORE(String oBJ_SUM_SCORE) {
		OBJ_SUM_SCORE = oBJ_SUM_SCORE;
	}

}
