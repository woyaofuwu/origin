package com.asiainfo.veris.crm.order.soa.person.busi.custspecialuser;

import org.apache.log4j.Logger;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class ImportCustSpecialUser extends ImportTaskExecutor{
	
	protected static Logger log = Logger.getLogger(ImportCustSpecialUser.class);

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
            	info.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            	info.put("UPDATE_TIME", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss"));
            	custlist.add(info);
            }
        }
		
		CustSpecialUserBean bean = BeanManager.createBean(CustSpecialUserBean.class);
		bean.delRecordList(custlist);
		bean.addRecordList(custlist);
		
		return new DatasetList();
	}

	
}
