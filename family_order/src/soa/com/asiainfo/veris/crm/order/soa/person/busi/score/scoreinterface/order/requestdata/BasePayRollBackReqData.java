
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BasePayRollBackReqData extends BaseReqData
{
    private String TRADE_SEQ;

    private String P_TRADE_SEQ;

    private String TRADE_TIME;

    private String ORGID;

    private String TRADE_ID;
    
    private String F_ORDER_ID;
    
    private String MOBILE;
    
    private String REFUND_POINT;

	public String getTRADE_SEQ() {
		return TRADE_SEQ;
	}

	public void setTRADE_SEQ(String tRADE_SEQ) {
		TRADE_SEQ = tRADE_SEQ;
	}

	public String getP_TRADE_SEQ() {
		return P_TRADE_SEQ;
	}

	public void setP_TRADE_SEQ(String p_TRADE_SEQ) {
		P_TRADE_SEQ = p_TRADE_SEQ;
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

	public String getREFUND_POINT() {
		return REFUND_POINT;
	}

	public void setREFUND_POINT(String rEFUND_POINT) {
		REFUND_POINT = rEFUND_POINT;
	}

}
