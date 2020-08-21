package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.uiopreqquery;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;

public class UiopReqExport extends ExportTaskExecutor{

	@Override
	public IDataset executeExport(IData paramIData, Pagination paramPagination)
			throws Exception {
		// TODO Auto-generated method stub
		UiopReqBean bean = BeanManager.createBean(UiopReqBean.class);
		IDataset list = bean.exportQueryList(paramIData);
		return list;
	}

}
