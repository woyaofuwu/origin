package com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch;

import java.lang.reflect.Proxy;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

public class ArchProcConfig {

	private static IData archProcMap = new DataMap();
	private static IArchProc defaultarchProcProxy = null;
	
	static{//加载默认配置
		try{
			IDataset implClass = BofQuery.qryDefaultArchImpClass();//查询默认配置
			String className = implClass.first().getString("CLASS_NAME");
			IArchProc archProc = (IArchProc)Class.forName(className).newInstance();
			ArchProcProxy proxy = new ArchProcProxy(archProc);
			defaultarchProcProxy = (IArchProc)Proxy.newProxyInstance(archProc.getClass().getClassLoader(), archProc.getClass().getInterfaces(), proxy);
		}catch (Exception e) {
			// TODO: handle exception
			try {
				CSAppException.apperr(CrmCommException.CRM_COMM_1176);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public static IArchProc getArchProcInstance(String tabName)throws Exception{
		if(archProcMap.containsKey(tabName)){
			return (IArchProc)archProcMap.get(tabName);
		}
		IDataset implClass = BofQuery.qryArchImpClassByTabName(tabName);
		if(IDataUtil.isEmpty(implClass)){
			archProcMap.put(tabName, defaultarchProcProxy);
			return defaultarchProcProxy;
		}
		String className = implClass.first().getString("CLASS_NAME");
		IArchProc archProc = (IArchProc)Class.forName(className).newInstance();
		ArchProcProxy proxy = new ArchProcProxy(archProc);
		IArchProc archProcProxy = (IArchProc)Proxy.newProxyInstance(archProc.getClass().getClassLoader(), archProc.getClass().getInterfaces(), proxy);
		archProcMap.put(tabName, archProcProxy);
		return archProcProxy;
	}
}
