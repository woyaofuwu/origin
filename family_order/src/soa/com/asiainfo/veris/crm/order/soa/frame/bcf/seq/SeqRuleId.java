package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqRuleId extends SeqBase {

	public SeqRuleId() {
		super("seq_rule_id", 100);
	}

	@Override
	public String getNextval(String connName) throws Exception {
		String nextval = nextval(connName);

		if (nextval == null) {
			return "";
		}

		return nextval;
	}

	public String getNextval(String connName, String s1) throws Exception {
		return getNextval(connName);
	}
}