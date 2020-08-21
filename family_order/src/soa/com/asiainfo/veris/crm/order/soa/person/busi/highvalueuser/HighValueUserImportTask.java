package com.asiainfo.veris.crm.order.soa.person.busi.highvalueuser;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;

public class HighValueUserImportTask extends ImportTaskExecutor{

	@Override
	public IDataset executeImport(IData paramIData, IDataset paramIDataset) throws Exception {
		// TODO Auto-generated method stub
		System.out.print("wuhao");
		HighValueUserEntryBean bean = BeanManager.createBean(HighValueUserEntryBean.class);
		IDataset errlist = new DatasetList();

		//
		for( int i = 0; i < paramIDataset.size(); i++ ){
			IData data = paramIDataset.getData(i);
			IData insData=new DataMap();  
			
	    	insData.put("SERIAL_NUMBER_B",data.getString("SERIAL_NUMBER_B",""));
	    	insData.put("SERIAL_NUMBER",data.getString("SERIAL_NUMBER",""));
	    	
	    	try{
	    		CSAppCall.call("SS.HighValueUserEntrySVC.insertHighUser", insData);
	    	}
	    	catch(Exception e){
	    		data.put("IMPORT_RESULT", false);
				data.put("IMPORT_ERROR", "导入失败:"+e.getMessage());
				errlist.add(data);
	    	}		
		}
		return errlist;
	}
}
