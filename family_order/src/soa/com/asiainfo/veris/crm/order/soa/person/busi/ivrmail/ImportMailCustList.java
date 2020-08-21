package com.asiainfo.veris.crm.order.soa.person.busi.ivrmail;

import org.apache.log4j.Logger;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;



public class ImportMailCustList extends ImportTaskExecutor {
	
	protected static Logger log = Logger.getLogger(ImportMailCustList.class);

	@Override
	public IDataset executeImport(IData paramIData, IDataset paramIDataset)
			throws Exception {
		// TODO Auto-generated method stub
		
		IDataset sectionlist = StaticUtil.getStaticList("IVR_139MAIL_CUST_SECTIONLIST");
		IData name2id = new DataMap();

		for( int i = 0; i < sectionlist.size();i++){
			String section_name = sectionlist.getData(i).getString("DATA_NAME");
			String section_id = sectionlist.getData(i).getString("DATA_ID");
			name2id.put(section_name, section_id);
		}
		
		IDataset custlist = new DatasetList();
		if (IDataUtil.isNotEmpty(paramIDataset))
        {
            for (int i = 0; i < paramIDataset.size(); i++){
            	IData info = new DataMap();
            	info.putAll(paramIDataset.getData(i));
            	info.put("CREATE_STAFFID", getVisit().getStaffId());
            	info.put("CREATE_DATE", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss"));
            	info.put("SECTION_ID", name2id.getString(info.getString("SECTION_NAME"),""));
            	custlist.add(info);
            }
        }
		
		MailCustManageBean bean = BeanManager.createBean(MailCustManageBean.class);
		bean.delRecordList(custlist);
		bean.addRecordList(custlist);
		
		return new DatasetList();
	}

}
