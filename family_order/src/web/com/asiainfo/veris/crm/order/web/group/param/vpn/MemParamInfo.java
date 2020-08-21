
package com.asiainfo.veris.crm.order.web.group.param.vpn;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.staff.staffbbossinfo.StaffGrpRightInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public class MemParamInfo extends IProductParamDynamic
{

    // 海南自动生成短号
    public IData creatshortcode(IBizCommon bp, IData data) throws Throwable
    {
        IData results = new DataMap();
        IData paramresult = new DataMap();
        boolean result = false;

        String memUserId = data.getString("MEB_USER_ID", "");
        IData memUserInfo = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(bp, memUserId, Route.getCrmDefaultDb(), true);
        IData data1 = new DataMap();
        data1.put("SERIAL_NUMBER", memUserInfo.getString("SERIAL_NUMBER", ""));
        data1.put("USER_ID_A", data.getString("GRP_USER_ID", ""));
        data1.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        data1.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());

        IDataset idataset = CSViewCall.call(bp, "SS.VpnUnitSVC.createShortCode", data1);
        String shortcode = "";
        if (IDataUtil.isNotEmpty(idataset))
        {
            shortcode = idataset.getData(0).getString("SHORT_CODE", "");
        }

        IData map = new DataMap();
        map.put("SHORT_CODE", shortcode);

        IData attr = super.getAttrItem();
        IData shortAttr = new DataMap();
        if (IDataUtil.isEmpty(attr))
        {
            attr = new DataMap();
            shortAttr.put("ATTR_VALUE", shortcode);
            attr.put("SHORT_CODE", shortAttr);
        }
        else
        {
            attr.getData("SHORT_CODE").put("ATTR_VALUE", shortcode);
        }

        this.setAttrItem(attr);

        result = true;
        paramresult.put("RESULT", result);
        paramresult.put("SHORT_CODE", shortcode);
        results.put("AJAX_DATA", paramresult);
        return results;
    }

    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }
        String grpUserId = data.getString("GRP_USER_ID"); // 集团userid
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, grpUserId, false);
        String userProductCode = userInfo.getString("SERIAL_NUMBER");
        String staffId = ((GroupBasePage) bp).getVisit().getStaffId();
        String rightCode = "VPN_SHORT_CODE";
        boolean shortCodeRight = false; // false:没有短号权限
        // 成员短号操作权限：先判断该VPMN集团是否有短号权限限制
        IDataset shortCodeRight1 = StaffGrpRightInfoIntfViewUtil.qryGrpRightInfosByStaffIdRightCodeUserProductCode(bp, null, rightCode, userProductCode);
        // 有短号权限限制，再判断该员工是否有短号权限
        if (IDataUtil.isNotEmpty(shortCodeRight1))
        {
            IDataset shortCodeRight2 = DataHelper.filter(shortCodeRight1, "STAFF_ID=" + staffId);
            if (IDataUtil.isNotEmpty(shortCodeRight2))
            {
                shortCodeRight = true;
            }
        }
        else
        {// 没有短号权限限制就视为有短号权限
            shortCodeRight = true;
        }
        if ("SUPERUSR".equals(staffId) || shortCodeRight)
        {
            parainfo.put("RIGHT_CODE_chg", "yes");
        }
        IData inparam = new DataMap();
        inparam.put("USER_ID", grpUserId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());

        // 查询用户VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, grpUserId, false);

        if (IDataUtil.isEmpty(userVpnData))
        {
            userVpnData = new DataMap();
        }
        String vpn_scare_code = userVpnData.getString("VPN_SCARE_CODE", "");
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CGM");
        iparam.put("PARAM_ATTR", "80");
        iparam.put("PARAM_CODE", null);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset defualtDiscntset = CSViewCall.call(bp, "CS.ParamInfoQrySVC.getCommparaByCode", iparam);
        String PROVICE_VPN_DISCNT = "";
        if (IDataUtil.isNotEmpty(defualtDiscntset))
        {
            for (int i = 0; i < defualtDiscntset.size(); i++)
            {
                String discntinfo = defualtDiscntset.getData(i).getString("PARA_CODE1", "");
                if ("".equals(PROVICE_VPN_DISCNT))
                    PROVICE_VPN_DISCNT = discntinfo;
                else
                    PROVICE_VPN_DISCNT = PROVICE_VPN_DISCNT + "," + discntinfo;
            }
        }
        parainfo.put("VPN_SCARE_CODE", vpn_scare_code);
        parainfo.put("PROVICE_VPN_DISCNT", PROVICE_VPN_DISCNT);
        String memUserId = data.getString("MEB_USER_ID", "");
        String memEparchyCode = data.getString("MEB_EPARCHY_CODE", "");
        inparam.clear();
        inparam.put("USER_ID", memUserId);
        inparam.put("USER_ID_A", grpUserId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, memEparchyCode);
        IData userattrinfo = CSViewCall.callone(bp, "CS.UserVpnInfoQrySVC.getMemberVpnByUserId", inparam);

        // 防止资源表与VPN_MEB中短号不一致，导致变更不删除原有资源，短号从资源表获取。
        inparam.clear();
        inparam.put("USER_ID", memUserId);
        inparam.put("USER_ID_A", grpUserId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
        IDataset userresinfo = CSViewCall.call(bp, "CS.UserResInfoQrySVC.getUserResByUserIdA", inparam);
        if (IDataUtil.isNotEmpty(userresinfo))
        {
            IData userres = userresinfo.getData(0);
            userattrinfo.put("SHORT_CODE", userres.getString("RES_CODE", ""));

        }

        IData userattritem = IDataUtil.iDataA2iDataB(userattrinfo, "ATTR_VALUE");
        transComboBoxValue(userattritem, getAttrItem());
        super.setAttrItem(userattritem);
        result.put("PARAM_INFO", parainfo);
        result.put("ATTRITEM", this.getAttrItem());
        return result;
    }

    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        String grpuserId = data.getString("GRP_USER_ID");
        // 集团用户信息查询
        IData grpUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, grpuserId, false);
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_28, grpuserId);
        }
        String userProductCode = "";
        if (IDataUtil.isNotEmpty(grpUserInfo))
        {
            userProductCode = grpUserInfo.getString("SERIAL_NUMBER", "");
        }

        String staffId = ((CSBasePage) bp).getVisit().getStaffId();
        String rightCode = "VPN_SHORT_CODE";
        boolean shortCodeRight = false; // false:没有短号权限
        // 成员短号操作权限：先判断该VPMN集团是否有短号权限限制
        IDataset shortCodeRight1 = StaffGrpRightInfoIntfViewUtil.qryGrpRightInfosByStaffIdRightCodeUserProductCode(bp, null, rightCode, userProductCode);

        // 有短号权限限制，判断该员工是否有短号权限
        if (IDataUtil.isNotEmpty(shortCodeRight1))
        {
            IDataset shortCodeRight2 = DataHelper.filter(shortCodeRight1, "STAFF_ID=" + staffId);
            if (IDataUtil.isNotEmpty(shortCodeRight2))
            {
                shortCodeRight = true;
            }
        }
        else
        {// 没有短号权限限制就就视为有短号权限
            shortCodeRight = true;
        }

        IData param = new DataMap();
        param.put("USER_ID", grpuserId);
        param.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
        IData vpninfo = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, grpuserId);
        String vpn_scare_code = vpninfo.getString("VPN_SCARE_CODE", "");
        IDataset defualtDiscntset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndEparchyCode(bp, "CGM", "80", Route.getCrmDefaultDb());

        String PROVICE_VPN_DISCNT = "";
        if (IDataUtil.isNotEmpty(defualtDiscntset))
        {
            for (int i = 0; i < defualtDiscntset.size(); i++)
            {
                String discntinfo = defualtDiscntset.getData(i).getString("PARA_CODE1", "");
                if ("".equals(PROVICE_VPN_DISCNT))
                    PROVICE_VPN_DISCNT = discntinfo;
                else
                    PROVICE_VPN_DISCNT = PROVICE_VPN_DISCNT + "," + discntinfo;
            }
        }
        parainfo.put("VPN_SCARE_CODE", vpn_scare_code);
        parainfo.put("PROVICE_VPN_DISCNT", PROVICE_VPN_DISCNT);

        IData info = new DataMap();

        info.put("USER_ID", grpuserId);
        // 集团定制信息
        IDataset userSvcInfos = CSViewCall.call(bp, "CS.GrpUserPkgInfoQrySVC.getGrpCustomizeServByUserId", info);

        if (IDataUtil.isNotEmpty(userSvcInfos))
        {
            for (int i = 0; i < userSvcInfos.size(); i++)
            {
                IData svcInfo = (IData) userSvcInfos.get(i);
                if ("861".equals(svcInfo.getString("ELEMENT_ID", "")))
                {
                    parainfo.put("SERVICE_ID", "861");
                }
            }
        }
        if (("SUPERUSR".equals(staffId) || shortCodeRight) && "861".equals(parainfo.getString("SERVICE_ID", "")))
        {
            parainfo.put("RIGHT_CODE", "yes"); // 自动生成短号
        }

        result.put("PARAM_INFO", parainfo);
        return result;
    }

    // 验证短号码
    public IData validchk(IBizCommon bp, IData data) throws Throwable
    {
        IData data1 = new DataMap();
        data1.put("SHORT_CODE", data.getString("SHORT_CODE", ""));
        data1.put("USER_ID_A", data.getString("GRP_USER_ID", ""));
        data1.put("EPARCHY_CODE", data.getString("MEB_EPARCHY_CODE", Route.getCrmDefaultDb()));
        data1.put(Route.ROUTE_EPARCHY_CODE, data.getString("MEB_EPARCHY_CODE", Route.getCrmDefaultDb()));
        IData reData = CSViewCall.callone(bp, "SS.VpnUnitSVC.shortCodeValidateVpn", data1);

        IData results = new DataMap();
        results.put("AJAX_DATA", reData);
        return results;
    }
}
