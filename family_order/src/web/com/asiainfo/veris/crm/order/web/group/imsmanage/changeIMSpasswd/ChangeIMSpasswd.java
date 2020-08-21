
package com.asiainfo.veris.crm.order.web.group.imsmanage.changeIMSpasswd;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class ChangeIMSpasswd extends GroupBasePage
{
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String serialNumber = data.getString("SERIAL_NUMBER");
        String sn_user = data.getString("SERIAL_NUMBER1");
        String user_id_a = data.getString("USER_ID_A"); // 集团用户id
        String eparchy_code = data.getString(Route.ROUTE_EPARCHY_CODE);
        String newPassword = data.getString("USER_PASSWD2");
        if (!serialNumber.equals(sn_user))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_700);
        }
        // 得到地州
        if (StringUtils.isBlank(eparchy_code))
        {
            IData userinfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, serialNumber);
            eparchy_code = userinfo.getString("EPARCHY_CODE");
        }

        String impuinfos = data.getString("IMPUINFO");
        IData impuinfo = new DataMap(impuinfos); // IMPU信息 idata存储

        IData conParams = new DataMap();
        conParams.put("SERIAL_NUMBER", serialNumber);
        conParams.put("IMPUINFO", impuinfo);
        conParams.put("USER_ID", user_id_a); // 集团用户ID
        conParams.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
        conParams.put("USER_PASSWD2", newPassword);
        IDataset result = CSViewCall.call(this, "CS.ChangeIMSpasswdSVC.changeIMSpasswd", conParams);

        setAjax(result);
    }

    /**
     * 成员号码信息查询
     */
    public void queryMemberInfo(IRequestCycle cycle) throws Exception
    {
        String serialNumber = getParameter("SERIAL_NUMBER");
        String userType = getParameter("USER_TYPE");
        if (StringUtils.isBlank(serialNumber))
        {
            CSViewException.apperr(ParamException.CRM_PARAM_269);
        }
        if (StringUtils.isBlank(userType))
        {
            CSViewException.apperr(ParamException.CRM_PARAM_276);
        }

        // 查询三户信息
        IData mebUCAInfoData = UCAInfoIntfViewUtil.qryMebUCAInfoBySn(this, serialNumber);

        IData mebUserInfoData = mebUCAInfoData.getData("MEB_USER_INFO");
        IData mebCustInfoData = mebUCAInfoData.getData("MEB_CUST_INFO");
        IData mebAcctInfoData = mebUCAInfoData.getData("MEB_ACCT_INFO");

        String userId = mebUserInfoData.getString("USER_ID");
        String eparchyCode = mebUserInfoData.getString("EPARCHY_CODE");
        String net_type_code = mebUserInfoData.getString("NET_TYPE_CODE");
        // 外省号码不能办理该业务
        if (StringUtils.isNotBlank(net_type_code) && "06".equals(net_type_code))
        {
            CSViewException.apperr(GrpException.CRM_GRP_411);
        }

        // 查询IMPU信息
        IData iparam = new DataMap();
        iparam.put("USER_ID", userId);
        iparam.put("RSRV_STR1", userType);// 用户类型
        iparam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset impuInfos = CSViewCall.call(this, "CS.UserImpuInfoQrySVC.queryUserImpuInfoByUserType", iparam);
        if (IDataUtil.isEmpty(impuInfos))
        {
            CSViewException.apperr(GrpException.CRM_GRP_223, serialNumber);
        }
        IData impuInfo = impuInfos.getData(0);

        // 调用后台服务，根据user_id_b查询成员所对应的集团用户信息
        IDataset uuInfo = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdB(this, userId, eparchyCode);
        if (IDataUtil.isEmpty(uuInfo))
        {
            CSViewException.apperr(GrpException.CRM_GRP_613, serialNumber);
        }
        String user_id_a = uuInfo.getData(0).getString("USER_ID_A");
        mebUserInfoData.put("USER_ID_A", user_id_a);

        IDataset uuInfo1 = DataHelper.filter(uuInfo, "RELATION_TYPE_CODE=S1");// 多媒体桌面电话RELATION_TYPE_CODE为S1
        // IDataset ecFetionProductOfferInfo = CSViewCall.call(this,
        // "CS.UserGrpPkgInfoQrySVC.qryECFetionProductOfferId", iparam);
        // 如果用户类型为2，判断是否具有企业飞信的订购关系；
        if ("2".equals(userType))
        {
            // if (IDataUtil.isEmpty(ecFetionProductOfferInfo))
            // {
            CSViewException.apperr(GrpException.CRM_GRP_222, serialNumber);
            // }
        }
        // 如果用户类型不为2，判断是否具有多媒体电话的UU关系
        if (!"2".equals(userType) && IDataUtil.isEmpty(uuInfo1))
        {
            CSViewException.apperr(GrpException.CRM_GRP_614, serialNumber);
        }

        // 查询用户的费用情况
        IData oweFeeData = new DataMap();
        oweFeeData.put("USER_ID", userId);
        oweFeeData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        // @return 欠费信息，RSRV_NUM1:往月欠费 RSRV_NUM2:实时欠费 RSRV_NUM3:实时结余
        IData oweFee = CSViewCall.callone(this, "CS.UserOwenInfoQrySVC.getOweFeeByUserId", oweFeeData);

        setUserInfo(mebUserInfoData);
        setImpuInfo(impuInfo);
        setOweInfo(oweFee);
        setCustInfo(mebCustInfoData);
        setAcctInfo(mebAcctInfoData);
    }

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setImpuInfo(IData impuInfo);

    public abstract void setInfo(IData info);

    public abstract void setOweInfo(IData oweInfo);

    public abstract void setUserInfo(IData userInfo);
}
