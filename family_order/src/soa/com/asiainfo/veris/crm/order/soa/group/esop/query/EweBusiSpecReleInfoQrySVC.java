package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EweBusiSpecReleInfoQrySVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;

	public static IDataset qryInfoByBpmTempletId(IData idata) throws Exception {
		String bpmTempletId = idata.getString("BPM_TEMPLET_ID");
		String validTag = idata.getString("VALID_TAG");
		return EweBusiSpecReleInfoQry.qryInfoByBpmTempletId(bpmTempletId,validTag);
	}
	
}
