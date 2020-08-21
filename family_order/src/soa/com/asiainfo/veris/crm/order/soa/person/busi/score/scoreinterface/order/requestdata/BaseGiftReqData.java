
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BaseGiftReqData extends BaseReqData
{
    private String TRADE_SEQ;

    private String TRADE_TIME;

    private String ORGID;

    private String TRADE_ID;

    private String F_ORDER_ID;
    
    private String MOBILE;
    
    private String ADD_POINT;
    
    private String ACTION_TYPE;
    
    private String COMMENTS;
    
    private String VALIDATE_TIME;
    
    private String POINT_TYPE;

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

	public String getTRADE_ID() {
		return TRADE_ID;
	}

	public void setTRADE_ID(String tRADE_ID) {
		TRADE_ID = tRADE_ID;
	}

	public String getF_ORDER_ID() {
		return F_ORDER_ID;
	}

	public void setF_ORDER_ID(String f_ORDER_ID) {
		F_ORDER_ID = f_ORDER_ID;
	}

	public String getMOBILE() {
		return MOBILE;
	}

	public void setMOBILE(String mOBILE) {
		MOBILE = mOBILE;
	}

	public String getADD_POINT() {
		return ADD_POINT;
	}

	public void setADD_POINT(String aDD_POINT) {
		ADD_POINT = aDD_POINT;
	}

	public String getACTION_TYPE() {
		return ACTION_TYPE;
	}

	public void setACTION_TYPE(String aCTION_TYPE) {
		ACTION_TYPE = aCTION_TYPE;
	}

	public String getCOMMENTS() {
		return COMMENTS;
	}

	public void setCOMMENTS(String cOMMENTS) {
		COMMENTS = cOMMENTS;
	}

	public String getVALIDATE_TIME() {
		return VALIDATE_TIME;
	}

	public void setVALIDATE_TIME(String vALIDATE_TIME) {
		VALIDATE_TIME = vALIDATE_TIME;
	}

	public String getPOINT_TYPE() {
		return POINT_TYPE;
	}

	public void setPOINT_TYPE(String pOINT_TYPE) {
		POINT_TYPE = pOINT_TYPE;
	}
    
}
