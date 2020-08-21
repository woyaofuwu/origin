package com.asiainfo.veris.crm.order.soa.frame.bof.callPf;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.ailk.common.data.IDataset;

public class CallPfDealProxy implements InvocationHandler{

	private ICallPfDeal proxyImpl;
	
	public CallPfDealProxy(ICallPfDeal callPfDealImpl){
		this.proxyImpl = callPfDealImpl;
	}
	
	public static ICallPfDeal getInstance(String className)throws Exception{
		return CallPfDealConfig.getCallPfDealInstance(className);
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stub
		IDataset results = (IDataset)method.invoke(this.proxyImpl, args);
		return results;
	}

}
