package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqGoodsPriceId extends SeqBase {

	public SeqGoodsPriceId() {
		super("SEQ_GOODS_PRICE_ID", 100);
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