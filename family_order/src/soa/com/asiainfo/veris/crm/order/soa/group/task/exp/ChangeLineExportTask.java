
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;

public class ChangeLineExportTask extends ExportTaskExecutor {

	@Override
	public IDataset executeExport(IData inParam, Pagination pg) throws Exception {
		//判断哪些专线被勾选导出
		IDataset lineNoInfos = new DatasetList(inParam.getString("CHANGELINENOS"));
		return lineNoInfos;
	}
}
