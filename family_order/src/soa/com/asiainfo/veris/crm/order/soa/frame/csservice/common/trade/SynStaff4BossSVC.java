package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SynStaff4BossSVC extends CSBizService{
	 private final static Logger logger = Logger.getLogger(SynStaff4BossSVC.class);
	 private static final long serialVersionUID = 1L;
	 
	 public IDataset synSysDataRight4BBOSS(IData input) throws Exception
	    {
		 SynStaff4Boss.synSysDataRight4BBOSS(input);
	     return new DatasetList();
	    }
	public IDataset synSysDataRight5BBOSS(IData input) throws Exception
	{
		SynStaff4Boss.synSysDataRight5BBOSS(input);
		return new DatasetList();
	}
	 
}
