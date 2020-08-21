package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;

import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
public abstract class FamilyOrderStatusQuery extends PersonBasePage {
	 
	public abstract void setOrderListInfos(IDataset orderListInfos);
	  
	  /**
		 * 家庭网订单状态查询
		 * @param cycle
		 * @throws Exception
		 */
		public void familyOrderStatusQuery(IRequestCycle cycle) throws Exception{
			IData pageData = getData();
			IDataset rtDataset = CSViewCall.call(this,
					"SS.FamilyAllNetBusiManageSVC.familyOrderStatusQuery", pageData);
			System.out.println(rtDataset);
			setAjax(rtDataset);
			setOrderListInfos(rtDataset.getData(0).getDataset("ORDER_LIST"));
		}
}