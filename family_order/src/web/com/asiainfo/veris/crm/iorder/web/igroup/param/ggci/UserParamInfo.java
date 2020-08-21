package com.asiainfo.veris.crm.iorder.web.igroup.param.ggci;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;

public abstract class UserParamInfo extends CSBasePage{
	
	public abstract void setCond(IData cond);
	
	public void initial(IRequestCycle cycle) throws Throwable {
		IData data = getData();
		IDataset ggciFeetypecodes = StaticUtil.getStaticList("GGCI_FEE_TYPECODE");
	    setGgciFeetypecodes(ggciFeetypecodes);
	    
		String operType = data.getString("OPER_TYPE");
		setCond(data);
		if (EcConstants.FLOW_ID_EC_CREATE.equals(operType)) {
			initCrtUs(cycle);
		}
		if (EcConstants.FLOW_ID_EC_CHANGE.equals(operType)) {
			initChgUs(cycle);
		}
		
	}
	
	 public void initChgUs(IRequestCycle cycle) throws Throwable
	    {
	        IData parainfo = this.getData("PARAM_INFO");

	        String productNo = parainfo.getString("PRODUCT_ID");
	     
	       
	     
	    }

	    public void initCrtUs(IRequestCycle cycle) throws Throwable
	    {

	        IData parainfo = new DataMap();
	        
	        parainfo = this.getData("PARAM_INFO");
	        String productNo = parainfo.getString("PRODUCT_ID");

	        parainfo.put("VISP_INFO", new DatasetList());
	        parainfo.put("SYS_DATE_NOW", SysDateMgr.getSysDate());
	       
	    }
	    
	 public   abstract void  setGgciFeetypecodes(IDataset ggciFeetypecodes);
	 public   abstract void  setInfo(IData info);

}
