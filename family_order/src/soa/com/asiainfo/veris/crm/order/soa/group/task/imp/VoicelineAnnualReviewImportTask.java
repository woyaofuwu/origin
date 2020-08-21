package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class VoicelineAnnualReviewImportTask extends ImportTaskExecutor{

	@Override
	public IDataset executeImport(IData data, IDataset dataset) throws Exception {
		if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_138);
        }
		for(int i =0;i< dataset.size();i++){
			IData importData =  dataset.getData(i);
			IDataUtil.chkParam(importData,"parr_ORDER_ID");
			IDataUtil.chkParam(importData,"parr_GROUP_ID");
			IDataUtil.chkParam(importData,"AUTH_DATE");
			IDataUtil.chkParam(importData,"VIOLATION_RECORDS");
			IDataUtil.chkParam(importData,"GROUP_NAME");
			IDataUtil.chkParam(importData,"ANNUAL_REVIEW_RESULT");
			
			importData.put("ORDER_ID", importData.getString("parr_ORDER_ID",""));
			importData.put("GROUP_ID", importData.getString("parr_GROUP_ID",""));
			importData.put("ANNUAL_REVIEW_DATE", importData.getString("AUTH_DATE",""));
			importData.put("VIOLATION_RECORD", importData.getString("VIOLATION_RECORDS",""));
			
			importData.put("IN_STAFF_ID",getVisit().getStaffId());
			importData.put("IN_DEPART_ID", getVisit().getDepartId());
			IData result= CSAppCall.callOne("SS.BusiCheckVoiceSVC.submitAnnualReviewInfo", importData);
		}
		return null;
	}

}
