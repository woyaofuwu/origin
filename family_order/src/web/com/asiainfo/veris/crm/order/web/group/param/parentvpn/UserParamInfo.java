
package com.asiainfo.veris.crm.order.web.group.param.parentvpn;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{
    /**
     * 创建服务号码
     * 
     * @param bp
     * @param data
     * @return
     * @throws Exception
     */
    public IData createSerialNumber(IBizCommon bp, IData data) throws Exception
    {
        IData retData = new DataMap();

        IData svcData = new DataMap();
        svcData.put("WORK_TYPE_CODE", data.getString("WORK_TYPE_CODE"));
        svcData.put(Route.USER_EPARCHY_CODE, data.getString("GRP_USER_EPARCHYCODE"));

        IData vpnNoData = CSViewCall.callone(bp, "SS.VpnUnitSVC.createParentVpnSN", svcData);

        retData.put("AJAX_DATA", vpnNoData);

        return retData;
    }

    @Override
    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData retData = super.initChgUs(bp, data);

        // 查询VPN用户信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, data.getString("USER_ID", ""), false);

        if (IDataUtil.isEmpty(userVpnData))
        {
            CSViewException.apperr(GrpException.CRM_GRP_130, data.getString("USER_ID"));
        }

        String callNetType = userVpnData.getString("CALL_NET_TYPE", ""); // 呼叫网络类型

        if (callNetType.length() == 4)
        {
            userVpnData.put("CALL_NET_TYPE1", callNetType.substring(0, 1));
            userVpnData.put("CALL_NET_TYPE2", callNetType.substring(1, 2));
            userVpnData.put("CALL_NET_TYPE3", callNetType.substring(2, 3));
            userVpnData.put("CALL_NET_TYPE4", callNetType.substring(3, 4));
        }

        // 保存参数信息
        IData userParamData = IDataUtil.iDataA2iDataB(userVpnData, "ATTR_VALUE");
        transComboBoxValue(userParamData, getAttrItem());
        getAttrItem().putAll(userParamData);

        IData paramInfo = retData.getData("PARAM_INFO");

        // 获取客户经理信息
        IDataset custManagerList = qryCustManagerList(bp, data);

        paramInfo.put("CUST_MANAGER", custManagerList);
        paramInfo.put("METHOD", "ChgUs");

        return retData;
    }

    @Override
    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData retData = super.initCrtUs(bp, data);

        IData paramInfo = retData.getData("PARAM_INFO");

        IData maxLinkmanData = new DataMap();

        if ("HK".equals(((CSBasePage) bp).getVisit().getCityCode().substring(0, 2)))
        {
            maxLinkmanData.put("ATTR_VALUE", "10");
        }
        else
        {
            maxLinkmanData.put("ATTR_VALUE", "5");
        }

        getAttrItem().put("MAX_LINKMAN_NUM", maxLinkmanData);

        // 获取客户经理信息
        IDataset custManagerList = qryCustManagerList(bp, data);

        paramInfo.put("CUST_MANAGER", custManagerList);
        paramInfo.put("METHOD", "CrtUs");

        return retData;
    }

    /**
     * 获取客户经理信息
     * 
     * @param bp
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryCustManagerList(IBizCommon bp, IData data) throws Exception
    {
        IData svcData = new DataMap();

        IDataset custManagerList = CSViewCall.call(bp, "CS.StaffInfoQrySVC.qryManagerIdJobType", svcData);

        if (IDataUtil.isNotEmpty(custManagerList))
        {
            for (int i = 0, row = custManagerList.size(); i < row; i++)
            {
                IData custManagerData = custManagerList.getData(i);

                String custManagerId = custManagerData.getString("CUST_MANAGER_ID");
                String custManagerName = custManagerData.getString("CUST_MANAGER_NAME");

                custManagerData.put("ATTR_FIELD_CODE", custManagerId);
                custManagerData.put("ATTR_FIELD_NAME", custManagerId + "|" + custManagerName);
            }
        }

        return custManagerList;
    }
}
