package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class WorkformProxySVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IDataset qryProxyByStaffIdA(IData param) throws Exception
    {
		String staffIdA =  param.getString("STAFF_ID_A");
		IDataset ProxyInfo =  WorkformProxyBean.qryProxyByStaffIdA(staffIdA,getPagination());
		return ProxyInfo;
    }
	
	public IDataset qryProxyByStaffIdB(IData param) throws Exception
    {
		String staffIdB =  param.getString("STAFF_ID_B");
		IDataset ProxyInfo =  WorkformProxyBean.qryProxyByStaffIdB(staffIdB,getPagination());
		return ProxyInfo;
    }
	
	public IDataset getProxyshistoryByStaffb(IData param) throws Exception
    {
		String staffIdB =  param.getString("STAFF_ID_B");
		IDataset ProxyInfo =  WorkformProxyBean.getProxyshistoryByStaffb(staffIdB,getPagination());
		return ProxyInfo;
    }
	
	public IDataset getStaffInfo(IData param) throws Exception
    {
		String rightCode =  param.getString("RIGHT_CODE");
		IDataset ProxyInfo =  WorkformProxyBean.getStaffInfo(rightCode);
		return ProxyInfo;
    }
	
	public IDataset isExistsStaffB(IData param) throws Exception
    {
		String staffIdB =  param.getString("STAFF_ID_B");
		String roleId = param.getString("ROLE_ID");
		IDataset ProxyInfo =  WorkformProxyBean.isExistsStaffB(staffIdB,roleId);
		return ProxyInfo;
    }
	
	public IDataset insertProxyInfo(IData param) throws Exception
    {
		param.put("PROXY_ID", SeqMgr.getAsynId());
		param.put("UPDATE_TIME", SysDateMgr.getSysTime());
		WorkformProxyBean.insertProxyInfo(param);
		return new DatasetList();
    }
	
	public IDataset deleteProxy(IData param) throws Exception
    {
		String proxtId =  param.getString("PROXY_ID");
		WorkformProxyBean.deleteProxy(proxtId);
		return new DatasetList();
    }
	
	public IDataset queryProxy(IData param) throws Exception
    {
		String proxyId =  param.getString("PROXY_ID");
		IDataset ProxyInfo =  WorkformProxyBean.queryProxy(proxyId);
		return ProxyInfo;
    }
	
	
	
	
}
