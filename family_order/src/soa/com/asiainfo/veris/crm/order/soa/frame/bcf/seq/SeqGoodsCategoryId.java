package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqGoodsCategoryId extends SeqBase {

	public SeqGoodsCategoryId() {
		super("SEQ_CATEGORY_ID", 100);
	}

	public String getNextval(String connName) throws Exception {
		String nextval = nextval(connName);

		if (nextval == null) {
			return "";
		}

		return nextval;
	}

	public String getNextval(String arg0, String arg1) throws Exception {
		return getNextval(arg0);
	}
}