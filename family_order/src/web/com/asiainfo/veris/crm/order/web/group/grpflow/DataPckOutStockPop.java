
package com.asiainfo.veris.crm.order.web.group.grpflow;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;


public abstract class DataPckOutStockPop extends CSBasePage
{
	private static final Logger log = Logger.getLogger(DataPckOutStockPop.class);
   /**
    * 查询已订购流量包
    * @param cycle
    * @throws Throwable
    */
    public void qryGroupFlowOrderDetail(IRequestCycle cycle) throws Throwable
    {
    	IData param = getData();
    	IDataOutput dataOutput = CSViewCall.callPage(this, "SS.GprsSaleForGrpSVC.qryGroupFlowOrderDetail", param, getPagination("ActiveNav"));	
		IDataset Lists=dataOutput.getData();
		setInfos(Lists);
		setPageCount(dataOutput.getDataCount());
		  
    }
    
  
    public abstract void setPageCount(long pageCount);
    
	public abstract void setInfos(IDataset infos);

    

}
