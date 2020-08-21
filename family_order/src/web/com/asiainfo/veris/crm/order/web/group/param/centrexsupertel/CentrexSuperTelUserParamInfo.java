
package com.asiainfo.veris.crm.order.web.group.param.centrexsupertel;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userotherinfo.UserOtherInfoIntfViewUtil;

public class CentrexSuperTelUserParamInfo extends IProductParamDynamic
{
    /**
     * 验证总机号码
     * 
     * @param bp
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkSuperTeleInfo(IBizCommon bp, IData data) throws Exception
    {
        IData result = new DataMap();

        String serialNumber = data.getString("SERIAL_NUMBER");
        // 查用户信息
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoBySn(bp, serialNumber);
        String userId = mebUserInfoData.getString("USER_ID", "");
        String custId = mebUserInfoData.getString("CUST_ID", "");
        String eparchyCode = mebUserInfoData.getString("EPARCHY_CODE");
        String netTypeCode = mebUserInfoData.getString("NET_TYPE_CODE");
        // 查客户信息
        IData mebCustInfoData = UCAInfoIntfViewUtil.qryMebCustInfoByCustIdAndRoute(bp, custId, eparchyCode);
        String custName = mebCustInfoData.getString("CUST_NAME", "");

        if (!"05".equals(netTypeCode)) // 固话号码
        {
            CSViewException.apperr(GrpException.CRM_GRP_632, serialNumber);
        }

        // 融合总机（总机号）
        IDataset uuInfoSet = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(bp, userId, "S4", eparchyCode, false);

        if (IDataUtil.isNotEmpty(uuInfoSet))
        {
            CSViewException.apperr(GrpException.CRM_GRP_631, serialNumber);
        }
        
        // 融合总机（总机号）必须订购多媒体桌面电话
        IDataset uuS1InfoSet = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(bp, userId,"S1", eparchyCode, false);

        if (IDataUtil.isEmpty(uuS1InfoSet))
        {
            CSViewException.apperr(GrpException.CRM_GRP_884, serialNumber);
        }
        
        mebUserInfoData.put("FLAG", "0");
        mebUserInfoData.put("EXCHANGETELE_SN", serialNumber);
        mebUserInfoData.put("CUST_NAME", custName);

        result.put("AJAX_DATA", mebUserInfoData);
        return result;
    }

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);

        IData paramInfo = result.getData("PARAM_INFO");

        String userId = data.getString("USER_ID", "");

        // 查询用户VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, userId);

        IData userAttrItem = IDataUtil.iDataA2iDataB(userVpnData, "ATTR_VALUE");

        IData attrItem = getAttrItem();
        super.transComboBoxValue(userAttrItem, attrItem);
        attrItem.putAll(userAttrItem);

        IDataset otherInfoList = UserOtherInfoIntfViewUtil.qryGrpSuperTeleInfoByUserIdAndRsrvValueCode(bp, data.getString("USER_ID", ""));
        IDataset superInfoList = new DatasetList();

        if (IDataUtil.isNotEmpty(otherInfoList))
        {
            for (int i = 0, row = otherInfoList.size(); i < row; i++)
            {
                IData otherData = otherInfoList.getData(i);

                IData tempOther = new DataMap();

                tempOther.put("EXCHANGETELE_SN", otherData.getString("RSRV_VALUE", ""));
                tempOther.put("E_CUST_NAME", otherData.getString("RSRV_STR1", ""));
                tempOther.put("E_CUST_ID", otherData.getString("RSRV_STR5", ""));
                tempOther.put("E_USER_ID", otherData.getString("RSRV_STR4", ""));
                tempOther.put("E_BRAND_CODE", otherData.getString("RSRV_STR2", ""));
                tempOther.put("E_EPARCHY_CODE", otherData.getString("RSRV_STR3", ""));
                tempOther.put("E_EPARCHY_NAME", otherData.getString("EPARCHY_NAME"));
                tempOther.put("MAXWAITINGLENGTH", otherData.getString("RSRV_STR6", ""));
                tempOther.put("CALLCENTERTYPE", otherData.getString("RSRV_STR7", ""));
                tempOther.put("CALLCENTERSHOW", otherData.getString("RSRV_STR8", ""));
                tempOther.put("CORP_REGCODE", otherData.getString("RSRV_STR9", ""));
                tempOther.put("CORP_DEREGCODE", otherData.getString("RSRV_STR10", ""));

                superInfoList.add(tempOther);
            }
        }
        paramInfo.put("SUPER_INFOS", superInfoList);
        paramInfo.put("METHOD", "ChgUs");
        paramInfo.put("PARAM_INFO", "CtrUs");
        return result;

    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData paramInfo = result.getData("PARAM_INFO");

        IData param = new DataMap();
        param.put("CUST_ID", data.getString("CUST_ID", ""));

        IDataset uservpninfos = CSViewCall.call(bp, "CS.UserVpnInfoQrySVC.getUserVPNInfoByCstId", param);
        if (IDataUtil.isNotEmpty(uservpninfos))
        {
            paramInfo.put("VPN_INFOS", uservpninfos);
        }

        paramInfo.put("METHOD", "CtrUs");
        result.put("PARAM_INFO", paramInfo);

        return result;
    }
}
