
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import java.util.Iterator;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class ChangeZhzgExportTask extends ExportTaskExecutor {

	@Override
	public IDataset executeExport(IData inParam, Pagination pg) throws Exception {
		IDataset inputs = new DatasetList(inParam.getString("CHANGEDATALINENOS"));
		IDataset newInput = new DatasetList();
		for (int i = 0; i < inputs.size(); i++) {
			IData input = inputs.getData(i);
			Iterator<String> itr = input.keySet().iterator();
			while (itr.hasNext()) {
				String attrCode = itr.next();
				String attrValue = input.getString(attrCode);
				if(attrCode.equals("ISPREOCCUPY")) {
					attrValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "IF_CHOOSE_CONFCRM", attrValue });
				}else if(attrCode.equals("ROUTEMODE")) {
					attrValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "ROUTEMODE", attrValue });
				}
				input.put(attrCode, attrValue);
			}
			newInput.add(input);
		}
		return newInput;
		
	}
}
