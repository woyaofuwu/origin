
package com.asiainfo.veris.crm.order.web.group.param.imsvpn;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.group.grpimsutil.GrpImsUtilView;

public class CentrexMemParamInfo extends IProductParamDynamic
{
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        String meb_user_id = data.getString("MEB_USER_ID", "");
        String grp_user_id = data.getString("GRP_USER_ID", "");
        String eparchyCode = data.getString("MEB_EPARCHY_CODE", "");

        IData attritemset = getAttrItem();

        IData idata = new DataMap();
        idata.put("USER_ID", meb_user_id);
        idata.put("USER_ID_A", grp_user_id);
        idata.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IData uservpninfo = CSViewCall.callone(bp, "CS.UserVpnInfoQrySVC.getMemberVpnByUserId", idata);

        // 防止资源表与VPN_MEB中短号不一致，导致变更不删除原有资源，短号从资源表获取。
        IDataset userresinfo = CSViewCall.call(bp, "CS.UserResInfoQrySVC.getUserResByUserIdA", idata);
        if (IDataUtil.isNotEmpty(userresinfo))
        {
            IData userres = userresinfo.getData(0);
            uservpninfo.put("SHORT_CODE", userres.getString("RES_CODE", ""));
        }
        IData userattritem = IDataUtil.iDataA2iDataB(uservpninfo, "ATTR_VALUE");
        transComboBoxValue(userattritem, getAttrItem());
        attritemset.putAll(userattritem);

        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(bp, meb_user_id, eparchyCode, false);
        if (IDataUtil.isEmpty(mebUserInfoData))
        {
            CSViewException.apperr(GrpException.CRM_GRP_261);
        }
        String netTypeCode = mebUserInfoData.getString("NET_TYPE_CODE");

        IData net = new DataMap();
        net.put("ATTR_VALUE", netTypeCode);
        attritemset.put("NET_TYPE_CODE", net);

        setImsVpnDiscnt(bp, attritemset);

        result.put("ATTRITEM", attritemset);
        IData parainfo = result.getData("PARAM_INFO");
        parainfo.put("METHOD_NAME", "ChgMb");
        result.put("PARAM_INFO", parainfo);

        return result;
    }

    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);

        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
            parainfo = result.getData("PARAM_INFO");
        IData attritemset = getAttrItem();

        String eparchyCode = data.getString("MEB_EPARCHY_CODE", "");
        String grpUserId = data.getString("GRP_USER_ID", "");
        String mebUserId = data.getString("MEB_USER_ID");
        // 1、查询集团用户信息
        IData grpUserInfoData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, grpUserId);
        String cust_id = grpUserInfoData.getString("CUST_ID", ""); // 集团客户id

        // 2、查成员用户信息
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(bp, mebUserId, eparchyCode, false);
        if (IDataUtil.isEmpty(mebUserInfoData))
        {
            CSViewException.apperr(GrpException.CRM_GRP_261);
        }
        String netTypeCode = mebUserInfoData.getString("NET_TYPE_CODE");

        String userType = "0";// =0 (SIP终端): IMS SIP-UE 用户

        IData inparam = new DataMap();
        inparam.put("USER_ID", mebUserId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset impuInfos = CSViewCall.call(bp, "CS.UserImpuInfoQrySVC.queryUserImpuInfo", inparam);
        if (IDataUtil.isNotEmpty(impuInfos))
        {
            IData datatmp = impuInfos.getData(0);
            userType = datatmp.getString("RSRV_STR1", ""); // 用户类型
        }

        if (!"05".equals(netTypeCode))
        {
            userType = "4";// =4 : 传统移动用户
        }

        IData idata = new DataMap();
        idata.put("CUST_ID", cust_id);
        idata.put("MEB_USER_ID", mebUserId);
        idata.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        boolean crtFlag = GrpImsUtilView.getCreateMebFlag(bp, idata); // 判断成员是否已经订购其他ims产品，如果没有订购则返回true
        if (!crtFlag) // 如果短号已经存在，则获取impu表中的短号
        {
            String shortcode = GrpImsUtilView.getImpuStr4(bp, mebUserId, userType, 1, eparchyCode);
            if (StringUtils.isNotBlank(shortcode))
            {
                IData res = new DataMap();
                res.put("ATTR_VALUE", shortcode);
                attritemset.put("SHORT_CODE", res);
            }
        }

        setImsVpnDiscnt(bp, attritemset);

        IData net = new DataMap();
        net.put("ATTR_VALUE", netTypeCode);
        attritemset.put("NET_TYPE_CODE", net);

        result.put("ATTRITEM", attritemset);
        result.put("PARAM_INFO", parainfo);

        return result;
    }

    public void setImsVpnDiscnt(IBizCommon bp, IData attritemset) throws Throwable
    {
        IDataset defaultDiscntset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndEparchyCode(bp, "CGM", "8001", ((CSBasePage) bp).getTradeEparchyCode());
        if (IDataUtil.isEmpty(defaultDiscntset))
        {
            CSViewException.apperr(UserDiscntException.CRM_USER_DISCNT_3);
        }
        String IMS_VPN_DISCNT_00 = "";
        String IMS_VPN_DISCNT_05 = "";
        for (int i = 0, size = defaultDiscntset.size(); i < size; i++)
        {
            IData defaultDiscnt = defaultDiscntset.getData(i);
            String discntinfo = defaultDiscnt.getString("PARA_CODE1", "");
            String nettypecode = defaultDiscnt.getString("PARAM_CODE", "");
            if ("00".equals(nettypecode))
            {
                if (StringUtils.isBlank(IMS_VPN_DISCNT_00))
                    IMS_VPN_DISCNT_00 = discntinfo;
                else
                    IMS_VPN_DISCNT_00 = IMS_VPN_DISCNT_00 + "," + discntinfo;
            }
            else if ("05".equals(nettypecode))
            {
                if (StringUtils.isBlank(IMS_VPN_DISCNT_05))
                    IMS_VPN_DISCNT_05 = discntinfo;
                else
                    IMS_VPN_DISCNT_05 = IMS_VPN_DISCNT_05 + "," + discntinfo;
            }
        }
        IData dis00 = new DataMap();
        dis00.put("ATTR_VALUE", IMS_VPN_DISCNT_00);
        attritemset.put("IMS_VPN_DISCNT_00", dis00);

        IData dis05 = new DataMap();
        dis05.put("ATTR_VALUE", IMS_VPN_DISCNT_05);
        attritemset.put("IMS_VPN_DISCNT_05", dis05);
    }

    /**
     * 验证短号
     * 
     * @param bp
     * @param data
     * @return
     * @throws Throwable
     */
    public IData validchk(IBizCommon bp, IData data) throws Throwable
    {
        IData result = new DataMap();

        String short_code = data.getString("SHORT_CODE"); // 短号
        String user_id_a = data.getString("USER_ID"); // 集团用户user_id
        String eparchy_code = data.getString("MEB_EPARCHY_CODE"); // 成员地州

        IData datatemp = new DataMap();
        datatemp.put("SHORT_CODE", short_code);
        datatemp.put("USER_ID_A", user_id_a);
        datatemp.put("EPARCHY_CODE", eparchy_code);

        GrpImsUtilView grpImsUtilView = new GrpImsUtilView();
        boolean flag = true;

        flag = grpImsUtilView.checkImsVpnShortCode(bp, datatemp);

        datatemp.put("RESULT", flag);
        result.put("AJAX_DATA", datatemp);
        setAttrItem(new DataMap());
        return result;
    }
}
