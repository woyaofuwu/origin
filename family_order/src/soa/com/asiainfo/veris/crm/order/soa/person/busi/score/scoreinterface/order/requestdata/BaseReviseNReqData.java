
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BaseReviseNReqData extends BaseReqData
{
    private String TRADE_SEQ;

    private String TRADE_TIME;

    private String ORGID;

    private String MOBILE;

    private String OPR_TYPE;
    
    private String REVISE_POINT;

    private String TRADE_ID;
    
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

	public String getMOBILE() {
		return MOBILE;
	}

	public void setMOBILE(String mOBILE) {
		MOBILE = mOBILE;
	}

	public String getOPR_TYPE() {
		return OPR_TYPE;
	}

	public void setOPR_TYPE(String oPR_TYPE) {
		OPR_TYPE = oPR_TYPE;
	}

	public String getREVISE_POINT() {
		return REVISE_POINT;
	}

	public void setREVISE_POINT(String rEVISE_POINT) {
		REVISE_POINT = rEVISE_POINT;
	}

	public String getTRADE_ID() {
		return TRADE_ID;
	}

	public void setTRADE_ID(String tRADE_ID) {
		TRADE_ID = tRADE_ID;
	}

}
