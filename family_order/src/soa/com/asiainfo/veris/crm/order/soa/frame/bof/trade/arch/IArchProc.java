package com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch;



public interface IArchProc {
	
	public abstract void arch(String tabName,String tradeId,String acceptMonth,String cancelTag)throws Exception;
}
