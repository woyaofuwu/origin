package com.asiainfo.veris.crm.iorder.web.igroup.offercha.parentvpn;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ParentvpnHandler extends BizHttpHandler
{
	public void createSerialNumber() throws Exception
    {
		IData data = this.getData();

        IData svcData = new DataMap();
        svcData.put("WORK_TYPE_CODE", data.getString("WORK_TYPE_CODE"));
        svcData.put(Route.USER_EPARCHY_CODE, data.getString("GRP_USER_EPARCHY_CODE"));

        IData vpnNoData = CSViewCall.callone(this, "SS.VpnUnitSVC.createParentVpnSN", svcData);
        
        IData results = new DataMap();
        results.put("AJAX_DATA", vpnNoData);
        
        String ajaxdatastr = results.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }

    }
}
