package com.asiainfo.veris.crm.iorder.web.igroup.offercha.vpmn;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizHttpHandler;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public class SuperteleHandler extends BizHttpHandler
{
    public void filterCustManagers() throws Exception
    {

        IData pagedata = this.getData();

        IData param = new DataMap();
        IData resultData = new DataMap();
        IData paramData = new DataMap();
        param.put("CUST_MANAGER_NAME", pagedata.getString("CUST_MANAGER_NAME"));
        IDataset managerInfos = CSViewCall.call(this, "CS.StaffInfoQrySVC.qryFilterManagerIdJobType", param);
        if (IDataUtil.isNotEmpty(managerInfos))
        {
            int len = managerInfos.size();
            for (int i = 0; i < len; i++)
            {
                IData infoData = managerInfos.getData(i);
                String rsrvStr1 = infoData.getString("CUST_MANAGER_NAME");
                String rsrvStr2 = infoData.getString("CUST_MANAGER_ID");
                infoData.put("CUST_MANAGER_NAME", rsrvStr1 + "|" + rsrvStr2);
            }
        }


        resultData.put("DATA_VAL", managerInfos);

        String ajaxdatastr = resultData.getString("DATA_VAL", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DatasetList(ajaxdatastr));
        }
    }

    public void createSerialNumber() throws Exception
    {

        IData results = new DataMap();
        IData paramresult = new DataMap();
        boolean result = false;
        // String work_yype_code = data.getString("WORK_TYPE_CODE");
        IData pagedata = this.getData();
        String tradeEparchyCode = getVisit().getStaffEparchyCode();

        if (StringUtils.isBlank(tradeEparchyCode) || tradeEparchyCode.length() != 4 || !StringUtils.isNumeric(tradeEparchyCode))
        {
            tradeEparchyCode = Route.getCrmDefaultDb();
        }
        pagedata.put("USER_EPARCHY_CODE", tradeEparchyCode);
        for (int i = 0; i < 1000; i++)
        {
            String vpn_no = "";// j2ee VpnUnit.vpmnNoCrt(pd, work_yype_code); // VPMN产品编码生成规则

            IDataset vpnnoData = CSViewCall.call(this, "SS.VpnUnitSVC.vpmnNoCrt", pagedata);
            if (IDataUtil.isNotEmpty(vpnnoData))
            {
                vpn_no = vpnnoData.getData(0).getString("VPN_NO");
            }

            IData info = new DataMap();
            info.put("SERIAL_NUMBER", vpn_no);
            IData userinfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, vpn_no, false);
            if (IDataUtil.isEmpty(userinfo))
            {
                result = true;
                paramresult.put("VPN_NO", vpn_no);
                break;
            }
            if (i == 999)
            {
                result = false;
                paramresult.put("ERROR_MESSAGE", "该VPMN集团类型的VPN_NO已经全部被使用，请重新选择VPMN集团类型！");
            }
        }

        paramresult.put("RESULT", result);
        results.put("AJAX_DATA", paramresult);

        String ajaxdatastr = results.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }
    }

    public void querySCPInfos() throws Exception
    {

        IData data = this.getData();
        IData results = new DataMap();
        IData paramresult = new DataMap();
        boolean result = false;
        String vpnType = data.getString("VPN_GRP_ATTR", "");
        String tradeEparchyCode = getVisit().getStaffEparchyCode();

        if (StringUtils.isBlank(tradeEparchyCode) || tradeEparchyCode.length() != 4 || !StringUtils.isNumeric(tradeEparchyCode))
        {
            tradeEparchyCode = Route.getCrmDefaultDb();
        }
        String scpCode = "10";
        if (vpnType.equals("A"))
        {

            IDataset datas = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, "CGM", "246", "HNHK", tradeEparchyCode);
            if (IDataUtil.isNotEmpty(datas))
            {
                scpCode = datas.getData(0).getString("PARA_CODE1");
            }
        }// add by lixiuyu@20100824 新增智能网系统（SCP6)
        else if (vpnType.equals("9"))
        {
            scpCode = "12";
        }
        // end by lixiuyu@20100824
        // add by wangyf6@20120112 新增智能网系统(SCP7)
        else if (vpnType.equals("8"))
        {
            scpCode = "13";
        }
        // end by wangyf6@20120112

        IData inparam = new DataMap();
        inparam.put("TYPE_ID", "TD_B_SCP");
        inparam.put("DATA_ID", scpCode);
        IDataset datas = StaticUtil.getStaticList("TD_B_SCP", scpCode);
        IData data1 = new DataMap();
        if (IDataUtil.isNotEmpty(datas))
        {
            data1 = datas.getData(0);
            paramresult.put("DATA_NAME", data1.getString("DATA_NAME", ""));
            paramresult.put("DATA_ID", data1.getString("DATA_ID", ""));
        }
        result = true;
        paramresult.put("RESULT", result);
        results.put("AJAX_DATA", paramresult);
        String ajaxdatastr = results.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }
    }
    
    // 海南自动生成短号
    public void creatshortcode() throws Throwable
    {
        IData data = this.getData();

        IData data1 = new DataMap();
        data1.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        data1.put("USER_ID_A", data.getString("EC_USER_ID", ""));
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

        IData shortAttr = new DataMap();
        shortAttr.put("AJAX_DATA", map);
        String ajaxdatastr = shortAttr.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }
    
    }
    
    // 验证短号码
    public void validchk() throws Throwable
    {
        IData data = this.getData();
        IData data1 = new DataMap();
        data1.put("SHORT_CODE", data.getString("SHORT_CODE", ""));
        data1.put("USER_ID_A", data.getString("EC_USER_ID", ""));
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
