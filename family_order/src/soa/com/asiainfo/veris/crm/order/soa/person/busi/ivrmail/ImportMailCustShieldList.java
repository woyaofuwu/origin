package com.asiainfo.veris.crm.order.soa.person.busi.ivrmail;

import org.apache.log4j.Logger;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;


public class ImportMailCustShieldList extends ImportTaskExecutor {
	
	protected static Logger log = Logger.getLogger(ImportMailCustShieldList.class);

	@Override
	public IDataset executeImport(IData paramIData, IDataset paramIDataset)
			throws Exception {
		// TODO Auto-generated method stub
		
		IDataset custlist = new DatasetList();
		if (IDataUtil.isNotEmpty(paramIDataset))
        {
            for (int i = 0; i < paramIDataset.size(); i++){
            	IData info = new DataMap();
            	info.putAll(paramIDataset.getData(i));
            	info.put("CREATE_STAFFID", getVisit().getStaffId());
            	info.put("CREATE_DATE", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss"));
            	custlist.add(info);
            }
        }
		
		MailCustShieldBean bean = BeanManager.createBean(MailCustShieldBean.class);
		bean.delRecordList(custlist);
		bean.addRecordList(custlist);
		
		return new DatasetList();
	}

}
