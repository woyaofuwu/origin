package com.asiainfo.veris.crm.iorder.web.igroup.offercha.centrexsupertel;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public class CentrexsupertelParamHandler extends BizHttpHandler{

    public void checkSuperTeleInfo() throws Exception
    {
        IData data = this.getData();

        IData result = new DataMap();

        String serialNumber = data.getString("SERIAL_NUMBER");
        // 查用户信息
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, serialNumber);
        String userId = mebUserInfoData.getString("USER_ID", "");
        String custId = mebUserInfoData.getString("CUST_ID", "");
        String eparchyCode = mebUserInfoData.getString("EPARCHY_CODE");
        String netTypeCode = mebUserInfoData.getString("NET_TYPE_CODE");
        // 查客户信息
        IData mebCustInfoData = UCAInfoIntfViewUtil.qryMebCustInfoByCustIdAndRoute(this, custId, eparchyCode);
        String custName = mebCustInfoData.getString("CUST_NAME", "");

        if (!"05".equals(netTypeCode)) // 固话号码
        {
            CSViewException.apperr(GrpException.CRM_GRP_632, serialNumber);
        }

        // 融合总机（总机号）
        IDataset uuInfoSet = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, userId, "S4", eparchyCode, false);

        if (IDataUtil.isNotEmpty(uuInfoSet))
        {
            CSViewException.apperr(GrpException.CRM_GRP_631, serialNumber);
        }
        
        // 融合总机（总机号）必须订购多媒体桌面电话
        IDataset uuS1InfoSet = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, userId,"S1", eparchyCode, false);

        if (IDataUtil.isEmpty(uuS1InfoSet))
        {
            CSViewException.apperr(GrpException.CRM_GRP_884, serialNumber);
        }
        
        mebUserInfoData.put("FLAG", "0");
        mebUserInfoData.put("EXCHANGETELE_SN", serialNumber);
        mebUserInfoData.put("CUST_NAME", custName);

        result.put("AJAX_DATA", mebUserInfoData);
     
        this.setAjax(new DataMap(result));
        
    }
    
    public void checkSuperTelOper() throws Throwable
    {
        IData data = this.getData();
        
        IData ajaxData = new DataMap();
        
        String serialNumber = data.getString("SERIAL_NUMBER");
        // 查用户信息
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, serialNumber);
        String userId = mebUserInfoData.getString("USER_ID", "");
        String eparchyCode = mebUserInfoData.getString("EPARCHY_CODE","");
        // 东信北邮centrex平台说固话及不论普通还是管理员，都能做为话务员；手机不能作为话务员
        IData userInfo = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, userId, eparchyCode);

        if (IDataUtil.isEmpty(userInfo))
        {
            ajaxData.put("RESULT", "false");
            ajaxData.put("ERROR_MESSAGE", "根据用户ID没有查询到成员用户信息!");
        }

        if (!"05".equals(userInfo.getString("NET_TYPE_CODE")))
        {
            ajaxData.put("RESULT", "false");
            ajaxData.put("ERROR_MESSAGE", "手机号不能作为话务员！");
        }
        else
        {
            ajaxData.put("RESULT", "true");
        }
        
        this.setAjax(new DataMap(ajaxData));
    }
}
