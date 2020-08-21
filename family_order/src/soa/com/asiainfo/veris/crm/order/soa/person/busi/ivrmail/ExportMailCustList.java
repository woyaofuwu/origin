package com.asiainfo.veris.crm.order.soa.person.busi.ivrmail;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;


public class ExportMailCustList extends ExportTaskExecutor {

	@Override
	public IDataset executeExport(IData paramIData, Pagination paramPagination)
			throws Exception {
		// TODO Auto-generated method stub
		MailCustManageBean bean = BeanManager.createBean(MailCustManageBean.class);
		IDataset list = bean.exportQueryList(paramIData);
		
		for(int i = 0; i<list.size(); i++){
			list.getData(i).put("SECTION_NAME", StaticUtil.getStaticValue("IVR_139MAIL_CUST_SECTIONLIST", list.getData(i).getString("SECTION_ID", "")));
		}
		
		return list;
	}

}
