
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ScoreDonateRollBackRequestData extends BaseReqData
{
	 private String TRADE_SEQ = ""; // 交易流水号

	 private String SERIAL_NUMBER  = ""; // 手机号码

	 private String SCORE_VALUE  = ""; // 积分值
	 
	 private String TRADE_ID  = ""; // 转赠流水
	 

	public String getTRADE_SEQ() {
		return TRADE_SEQ;
	}

	public void setTRADE_SEQ(String tRADE_SEQ) {
		TRADE_SEQ = tRADE_SEQ;
	}

	public String getSERIAL_NUMBER() {
		return SERIAL_NUMBER;
	}

	public void setSERIAL_NUMBER(String sERIAL_NUMBER) {
		SERIAL_NUMBER = sERIAL_NUMBER;
	}

	public String getSCORE_VALUE() {
		return SCORE_VALUE;
	}

	public void setSCORE_VALUE(String sCORE_VALUE) {
		SCORE_VALUE = sCORE_VALUE;
	}

	public String getTRADE_ID() {
		return TRADE_ID;
	}

	public void setTRADE_ID(String tRADE_ID) {
		TRADE_ID = tRADE_ID;
	}
	 
}
