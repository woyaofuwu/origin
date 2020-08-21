package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqSPSeqID extends SeqBase {
	public SeqSPSeqID() {
		super("SEQ_SP_SEQ_ID", 200);
	}

	@Override
	public String getNextval(String connName) throws Exception {
		String nextval = nextval(connName);

		if (nextval == null) {
			return "";
		}

		return nextval;
	}

	@Override
	public String getNextval(String connName, String s1) throws Exception {
		return getNextval(connName);
	}
}
