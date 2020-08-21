package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;

/**
 **和商务TV成员号码导出
 * @author zhanzg
 * @Date 2019年10月14日
 */
public class SumBusinessTVMemberExportTask extends ExportTaskExecutor{

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
