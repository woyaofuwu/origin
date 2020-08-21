package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqGoodsGoodsId extends SeqBase {

	public SeqGoodsGoodsId() {
		super("SEQ_GOODS_GOODS_ID", 100);
	}

	public String getNextval(String connName) throws Exception {
		String nextval = nextval(connName);

		if (nextval == null) {
			return "";
		}

		return nextval;
	}

	public String getNextval(String s, String s1) throws Exception {
		return getNextval(s);
	}

}
