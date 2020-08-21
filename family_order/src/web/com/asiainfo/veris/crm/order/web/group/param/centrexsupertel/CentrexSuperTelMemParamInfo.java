
package com.asiainfo.veris.crm.order.web.group.param.centrexsupertel;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userotherinfo.UserOtherInfoIntfViewUtil;

public class CentrexSuperTelMemParamInfo extends IProductParamDynamic
{

    /**
     * 校验是否可以做为话务员,只有多媒体桌面电话的普通成员才能做为话务员
     * 
     * @param
     * @param indata
     * @return
     * @throws Throwable
     */
    public IData checkSuperTelOper(IBizCommon bp, IData indata) throws Throwable
    {
        IData ajaxData = new DataMap();

        // 东信北邮centrex平台说固话及不论普通还是管理员，都能做为话务员；手机不能作为话务员
        IData userInfo = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(bp, indata.getString("MEB_USER_ID"), indata.getString("MEB_EPARCHY_CODE"));

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
        IData returnData = new DataMap();
        returnData.put("AJAX_DATA", ajaxData);

        return returnData;
    }

    /**
     * 作用：变更成员时的初始化
     * 
     * @author
     * @exception Throwable
     */
    @Override
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData paramInfo = result.getData("PARAM_INFO");

        IData inparam = new DataMap();
        String userId = data.getString("MEB_USER_ID", "");
        String userIdA = data.getString("GRP_USER_ID", "");

        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", userIdA);
        inparam.put("USER_ID_B", userId);
        inparam.put("RELATION_TYPE_CODE", "S3");
        inparam.put("EPARCHY_CODE", data.getString("MEB_EPARCHY_CODE"));
        inparam.put(Route.ROUTE_EPARCHY_CODE, data.getString("MEB_EPARCHY_CODE"));

        IData uuInfo_S3 = RelationUUInfoIntfViewUtil.qryRelaUUInfoByUserIdBAndUserIdARelationTypeCode(bp, userId, userIdA, "S3", data.getString("MEB_EPARCHY_CODE"), false);
        if (IDataUtil.isEmpty(uuInfo_S3))
        {
            uuInfo_S3 = new DataMap();
        }

        IData mebResInfo = CSViewCall.callone(bp, "CS.UserResInfoQrySVC.getUserResByUserIdA", inparam);
        if (IDataUtil.isNotEmpty(mebResInfo))
        {
            uuInfo_S3.put("SHORT_CODE", mebResInfo.getString("RES_CODE", ""));
            uuInfo_S3.put("hidden_SHORT_CODE", mebResInfo.getString("RES_CODE", ""));
        }

        IDataset superTels = UserOtherInfoIntfViewUtil.qryGrpUserOtherInfosByUserIdAndRsrvValueCode(bp, userIdA, "MUTISUPERTEL");
        if (IDataUtil.isNotEmpty(superTels))
        {
            paramInfo.put("SUPERTELLIST", superTels);
        }

        inparam.clear();
        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", userIdA);
        inparam.put(Route.ROUTE_EPARCHY_CODE, data.getString("MEB_EPARCHY_CODE"));

        IData vpnMebInfo = CSViewCall.callone(bp, "CS.UserVpnInfoQrySVC.getMemberVpnByUserId", inparam);
        uuInfo_S3.put("SUPERTELNUMBER", vpnMebInfo.getString("RSRV_STR2", ""));
        uuInfo_S3.put("OPERATORPRIONTY", vpnMebInfo.getString("RSRV_STR3", ""));

        IData uuAttrItem = IDataUtil.iDataA2iDataB(uuInfo_S3, "ATTR_VALUE");
        transComboBoxValue(uuAttrItem, getAttrItem());
        getAttrItem().putAll(uuAttrItem);
        result.put("PARAM_INFO", paramInfo);

        return result;
    }

    /**
     * 作用：新增成员时的初始化
     * 
     * @author
     * @exception Throwable
     */
    @Override
    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData paramInfo = result.getData("PARAM_INFO");

        // VPMN关系
        IData relaData20 = RelationUUInfoIntfViewUtil.qryRelaUUInfoByUserIdBAndRelationTypeCode(bp, data.getString("MEB_USER_ID", ""), "20", data.getString("MEB_EPARCHY_CODE"), false);

        IData vpnMebData = new DataMap();
        if (IDataUtil.isNotEmpty(relaData20))
        {
            vpnMebData.put("SHORT_CODE", relaData20.getString("SHORT_CODE", ""));
        }
        else
        {
            vpnMebData.put("FLAG", "0");
        }

        IDataset superTelList = UserOtherInfoIntfViewUtil.qryGrpUserOtherInfosByUserIdAndRsrvValueCode(bp, data.getString("GRP_USER_ID"), "MUTISUPERTEL");
        if (IDataUtil.isNotEmpty(superTelList))
        {
            paramInfo.put("SUPERTELLIST", superTelList);
        }

        IData userAttrItem = IDataUtil.iDataA2iDataB(vpnMebData, "ATTR_VALUE");
        IData attrItem = this.getAttrItem();
        attrItem.putAll(userAttrItem);

        this.setAttrItem(userAttrItem);
        result.put("PARAM_INFO", paramInfo);

        return result;
    }

    /**
     * 成员新增短号验证
     * 
     * @param
     * @param indata
     * @throws Exception
     */
    public IData shortNumValidateSuperTeleMember(IBizCommon bp, IData indata) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SHORT_CODE", indata.getString("SHORT_NUM"));
        inparam.put("USER_ID", indata.getString("USER_ID_A"));
        IData validateData = CSViewCall.callone(bp, "CS.GroupImsUtilSVC.shortCodeValidateSupTeleMeb", inparam);

        IData returnData = new DataMap();
        returnData.put("AJAX_DATA", validateData);

        return returnData;
    }
}
