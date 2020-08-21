package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;

/**
 **云WiFi安审版成员号码导出
 * @author xieqj
 * @Date 2019年10月11日
 */
public class CloudWifiExportTask extends ExportTaskExecutor{

	/**
	 ** 成员号码导出
	 * @param data 入参
	 * @param pag
	 * @return
	 * @throws Exception
	 * @Date 2019年10月11日
	 * @author xieqj 
	 */
	@Override
	public IDataset executeExport(IData data, Pagination pag) throws Exception {
		IDataset excelDataList = new DatasetList();
		IDataset memberList = new DatasetList(data.getString("mebList"));
		for (Object object : memberList) {
			IData memberData = (IData) object;
			 
			excelDataList.add(memberData);
		}
		return excelDataList;
	}
	
	 

}
