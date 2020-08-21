package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformPresentNodeSVC extends CSBizService{

	private static final long serialVersionUID = 1L;

	public static IDataset qryPresentNodeByIbsysid(IData param) throws Exception
    {
    	String ibsysid = param.getString("IBSYSID");
    	String bpm_templet_d = param.getString("BPM_TEMPLET_ID");
    	
        return WorkformPresentNodeBean.qryPresentNodeByIbsysid(ibsysid,bpm_templet_d);
    }
	
}
