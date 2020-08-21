package com.asiainfo.veris.crm.iorder.web.igroup.offercha.jwtvpn;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class JwtvpnHandler extends BizHttpHandler
{
	public void createSerialNumber() throws Exception
    {
		IData data = this.getData();

        IData svcData = new DataMap();
        svcData.put("WORK_TYPE_CODE", data.getString("WORK_TYPE_CODE"));
        svcData.put(Route.USER_EPARCHY_CODE, data.getString("GRP_USER_EPARCHY_CODE"));

        IData vpnNoData = CSViewCall.callone(this, "SS.VpnUnitSVC.parentVpmnNoCrt", svcData);
        
        IData results = new DataMap();
        results.put("AJAX_DATA", vpnNoData);
        
        String ajaxdatastr = results.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }

    }
	
	public void creatshortcode() throws Exception
    {
		IData data = this.getData();
		IData results = new DataMap();
        IData paramresult = new DataMap();
        boolean result = false;

        String memUserId = data.getString("MEB_USER_ID", "");

        IData memUserInfo = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, memUserId, Route.getCrmDefaultDb(), false);

        IData data1 = new DataMap();
        data1.put("SERIAL_NUMBER", memUserInfo.getString("SERIAL_NUMBER", ""));
        data1.put("USER_ID_A", data.getString("GRP_USER_ID", ""));
        data1.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        data1.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());

        IDataset idataset = CSViewCall.call(this, "SS.VpnUnitSVC.createShortCode", data1);
        String shortcode = "";
        if (IDataUtil.isNotEmpty(idataset))
        {
            shortcode = idataset.getData(0).getString("SHORT_CODE", "");
        }

        IData map = new DataMap();
        map.put("SHORT_CODE", shortcode);

        result = true;
        paramresult.put("RESULT", result);
        paramresult.put("SHORT_CODE", shortcode);
        
        results.put("AJAX_DATA", paramresult);
        
        String ajaxdatastr = results.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }

    }
	
	// 验证短号码
	public void validchk() throws Exception
    {
		IData data = this.getData();
        IData data1 = new DataMap();
        data1.put("SHORT_CODE", data.getString("SHORT_CODE", ""));
        data1.put("USER_ID_A", data.getString("GRP_USER_ID", ""));
        data1.put("EPARCHY_CODE", data.getString("MEB_EPARCHY_CODE", Route.getCrmDefaultDb()));
        data1.put(Route.ROUTE_EPARCHY_CODE, data.getString("MEB_EPARCHY_CODE", Route.getCrmDefaultDb()));
        IData reData = CSViewCall.callone(this, "SS.VpnUnitSVC.shortCodeValidateVpn", data1);

        IData results = new DataMap();
        results.put("AJAX_DATA", reData);
        String ajaxdatastr = results.getString("AJAX_DATA", "");
        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }
    }
}
