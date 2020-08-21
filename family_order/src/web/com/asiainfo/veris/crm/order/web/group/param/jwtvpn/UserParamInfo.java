
package com.asiainfo.veris.crm.order.web.group.param.jwtvpn;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    public IData createSerialNumber(IBizCommon bp, IData data) throws Exception
    {
        IData retData = new DataMap();

        IData svcData = new DataMap();
        svcData.put("WORK_TYPE_CODE", data.getString("WORK_TYPE_CODE"));
        svcData.put(Route.USER_EPARCHY_CODE, data.getString("GRP_USER_EPARCHY_CODE"));

        IData vpnNoData = CSViewCall.callone(bp, "SS.VpnUnitSVC.parentVpmnNoCrt", svcData);

        retData.put("AJAX_DATA", vpnNoData);

        return retData;

    }

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        String userId = data.getString("USER_ID");// 必传

        // 查询用户VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, userId);

        if (IDataUtil.isNotEmpty(userVpnData))
        {
            parainfo.putAll(userVpnData);
        }

        parainfo.put("pam_MAX_LINKMAN_NUM", userVpnData.getString("MAX_LINKMAN_NUM"));
        String defaultFunctlags = "00000000000000000000";
        String functlags = userVpnData.getString("FUNC_TLAGS", "00000000000000000000");
        if (functlags.length() < 40)
        {
            functlags = StrUtil.replacStrByint(defaultFunctlags, functlags, 1, functlags.length());
        }

        // 表格参数start

        IDataset datas = AttrItemInfoIntfViewUtil.qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(bp, data.getString("PRODUCT_ID", ""), "P", "2", data.getString("USER_EPARCHY_CODE"));
        if (IDataUtil.isNotEmpty(datas))
        {
            for (int i = 0, iSize = data.size(); i < iSize; i++)
            {
                IData idata = datas.getData(i);
                String attrCode = idata.getString("ATTR_CODE");
                int codeNumber = Integer.valueOf(attrCode.substring(attrCode.length() - 2));
                idata.put("ATTR_INIT_VALUE", functlags.substring(codeNumber - 1, codeNumber));
            }
        }
        // 表格参数end

        // 分解拼串数据
        String callnettype = userVpnData.getString("CALL_NET_TYPE", "");// 呼叫网络类型
        IData callNetType = new DataMap();
        if (callnettype.length() == 4)
        {
            String CALL_NET_TYPE1 = callnettype.substring(0, 1);// 内网
            callNetType.put("CALL_NET_TYPE1", CALL_NET_TYPE1);

            String CALL_NET_TYPE2 = callnettype.substring(1, 2); // 网间
            callNetType.put("CALL_NET_TYPE2", CALL_NET_TYPE2);

            String CALL_NET_TYPE3 = callnettype.substring(2, 3); // 网外
            callNetType.put("CALL_NET_TYPE3", CALL_NET_TYPE3);

            String CALL_NET_TYPE4 = callnettype.substring(3, 4);// 网外号码组
            callNetType.put("CALL_NET_TYPE4", CALL_NET_TYPE4);
        }
        IData userattritem = IDataUtil.iDataA2iDataB(callNetType, "ATTR_VALUE"); // 转格式为可ognl:getAttrItemValue('CALL_AREA_TYPE','ATTR_VALUE')
        transComboBoxValue(userattritem, getAttrItem());
        this.getAttrItem().putAll(userattritem);

        parainfo.put("VPN_CLASS", userVpnData.getString("RSRV_STR5", ""));
        parainfo.put("VPN_AREA", userVpnData.getString("RSRV_STR2", ""));
        IData map = new DataMap();
        IDataset managerInfos = CSViewCall.call(bp, "CS.StaffInfoQrySVC.qryManagerIdJobType", map);
        for (int i = 0, iSize = managerInfos.size(); i < iSize; i++)
        {
            IData manager = managerInfos.getData(i);
            String managerCode = manager.getString("CUST_MANAGER_ID");
            String managerName = manager.getString("CUST_MANAGER_NAME");
            manager.put("ALL_SHOW_FIELD", managerCode + "|" + managerName);
        }
        parainfo.put("managerInfos", managerInfos);
        parainfo.put("userParamInfos", datas);
        parainfo.put("METHOD_NAME", "ChgUs");

        result.put("PARAM_INFO", parainfo);

        result.put("ATTRITEM", this.getAttrItem());
        result.put("ATTRITEMSET", this.getAttrItemSet());
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        String cityCode = ((CSBasePage) bp).getVisit().getCityCode();
        if ("HK".equals(cityCode.substring(0, 2)))
        {
            parainfo.put("MAX_LINKMAN_NUM", "10");
        }
        else
        {
            parainfo.put("MAX_LINKMAN_NUM", "5");
        }

        IDataset datas = AttrItemInfoIntfViewUtil.qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(bp, data.getString("PRODUCT_ID", ""), "P", "2", data.getString("USER_EPARCHY_CODE"));
        IData map = new DataMap();
        IDataset managerInfos = CSViewCall.call(bp, "CS.StaffInfoQrySVC.qryManagerIdJobType", map);
        for (int i = 0, iSize = managerInfos.size(); i < iSize; i++)
        {
            IData manager = managerInfos.getData(i);
            String managerCode = manager.getString("CUST_MANAGER_ID");
            String managerName = manager.getString("CUST_MANAGER_NAME");
            manager.put("ALL_SHOW_FIELD", managerCode + "|" + managerName);
        }
        parainfo.put("managerInfos", managerInfos);
        parainfo.put("userParamInfos", datas);
        parainfo.put("METHOD_NAME", "CrtUs");
        result.put("PARAM_INFO", parainfo);
        return result;
    }
}
