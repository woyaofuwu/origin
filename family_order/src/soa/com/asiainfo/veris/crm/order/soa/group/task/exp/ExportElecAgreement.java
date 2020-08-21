package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.iorder.soa.group.param.minorec.elecagreement.AgreementInfoBean;

public class ExportElecAgreement extends ExportTaskExecutor  {

	/**
	 * 入网协议对应的产品id （不存在实质的关联产品）
	 */
	private static final String NET_SERVICE_PRODUCT_ID = "-1";
	
	
	@Override
	public IDataset executeExport(IData data, Pagination pag) throws Exception {
		//查询条件
		IData queryData = new DataMap();
		String startDate = data.getString("START_DATE");
		String endDate = data.getString("END_DATE");
		queryData.put("START_DATE", startDate);
		queryData.put("END_DATE", endDate);
		IDataset resultDataset = AgreementInfoBean.elecAgreementSumReportForm(queryData , pag);
		
		for (int i = 0, len = resultDataset.size(); i < len; i++) {
			IData elecAgreement = resultDataset.getData(i);
			String productId = elecAgreement.getString("PRODUCT_ID");
			if (!NET_SERVICE_PRODUCT_ID.equals(productId)) {
        		String productName =  StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "ELECPRODUCT", productId});
				elecAgreement.put("PRODUCT_ID", productName);
			} else {
				elecAgreement.put("PRODUCT_ID", "");
			}
		}
		return resultDataset;
	}

}
