package com.asiainfo.veris.crm.order.soa.frame.bof.callPf;

import java.lang.reflect.Proxy;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public final class CallPfDealConfig {

	private static IData callPfDealMap = new DataMap();
	
	public static ICallPfDeal getCallPfDealInstance(String className)throws Exception{
		if(callPfDealMap.containsKey(className)){
			return (ICallPfDeal)callPfDealMap.get(className);
		}else{
			ICallPfDeal obj = (ICallPfDeal)Class.forName(className).newInstance();
			CallPfDealProxy proxy = new CallPfDealProxy(obj);
			ICallPfDeal iCallPfDeal = (ICallPfDeal)Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), proxy);
			callPfDealMap.put(className, iCallPfDeal);
			return iCallPfDeal;
		}
	}
}
