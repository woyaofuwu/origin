package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class VoicelineAnnualReviewExportTask extends ExportTaskExecutor{

	@Override
	public IDataset executeExport(IData data, Pagination pg) throws Exception {
		data.put("GROUP_NAME", data.getString("CUST_NAME",""));
		IDataset infos= CSAppCall.call("SS.BusiCheckVoiceSVC.queryVoicelineAnnualInfo", data);
		return infos;
	}

}
