
package com.asiainfo.veris.crm.iorder.web.person.createnpuser;


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CheckBeforeNpUser extends PersonBasePage
{
		public void onTradeSubmit(IRequestCycle cycle) throws Exception {
			IData pageData = getData();
			pageData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
			IData result = CSViewCall.call(this, "SS.CheckBeforeNpUserSVC.insUserNpCheck", pageData).getData(0);
			setAjax(result);
		}
}
