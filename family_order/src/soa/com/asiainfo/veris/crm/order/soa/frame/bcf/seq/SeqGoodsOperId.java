package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqGoodsOperId extends SeqBase {

	public SeqGoodsOperId() {
		super("SEQ_GOODS_OPERID", 100);
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