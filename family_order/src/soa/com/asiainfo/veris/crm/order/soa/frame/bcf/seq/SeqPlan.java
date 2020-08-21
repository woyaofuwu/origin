package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqPlan extends SeqBase
{

	public SeqPlan() 
	{
		super("SEQ_PLAN", 100);
	}

	@Override
	public String getNextval(String arg0, String arg1) throws Exception 
	{
		return getNextval(arg0);
	}
	
	public String getNextval(String connName) throws Exception 
	{
		String nextval = nextval(connName);

		if (nextval == null) 
		{
			return "";
		}

		return nextval;
	}
}