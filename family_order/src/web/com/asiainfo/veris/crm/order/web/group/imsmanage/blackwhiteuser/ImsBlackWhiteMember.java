
package com.asiainfo.veris.crm.order.web.group.imsmanage.blackwhiteuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class ImsBlackWhiteMember extends GroupBasePage
{
    public boolean check(IData data) throws Exception
    {
        Boolean bool = false;
        String eparchyCode = data.getString("EPARCHY_CODE");
        IDataset relauuInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdB(this, data.getString("USER_ID"), eparchyCode);
        if (IDataUtil.isNotEmpty(relauuInfos))
        {
            for (int i = 0, size = relauuInfos.size(); i < size; i++)
            {
                IData relauuInfo = (IData) relauuInfos.get(i);
                String relationTypeCode = relauuInfo.getString("RELATION_TYPE_CODE");
                // 判断是否多媒体桌面电话成员
                if ("E1".equals(relationTypeCode))
                {
                    bool = true;
                    break;
                }
                // 判断是否融合一号通成员
                if ("E2".equals(relationTypeCode))
                {
                    bool = true;
                    break;
                }
                // 判断是否融合总机成员
                if ("E3".equals(relationTypeCode))
                {
                    bool = true;
                    break;
                }
                // 判断是否融合V网成员
                if ("20".equals(relationTypeCode))
                {
                    String userId = relauuInfo.getString("USER_ID_A");

                    IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userId);

                    if (IDataUtil.isNotEmpty(userVpnData))
                    {
                        String userVpnCode = userVpnData.getString("VPN_USER_CODE");

                        if ("2".equals(userVpnCode))
                        {
                            bool = true;
                            break;
                        }
                    }
                }

            }
        }
        return bool;
    }

    /**
     * 查询成员黑白名单开关是否打开
     * 
     * @author luoyong
     * @throws Throwable
     */
    public void chkMebBwOpen(IRequestCycle cycle) throws Exception
    {
        String eparchyCode = getParameter("EPARCHY_CODE");
        String utypecode = getParameter("USER_TYPE_CODE1"); // 通过参数传过来，不从getData()中取
        String userId = getParameter("USER_ID");

        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("ATTR_CODE", utypecode);
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        boolean flag = true;
        String attrVal = "-1";
        IDataset attrlist = CSViewCall.call(this, "CS.UserBlackWhiteInfoQrySVC.getBwOpenTag", data);
        if (IDataUtil.isNotEmpty(attrlist))
        {
            attrVal = attrlist.getData(0).getString("ATTR_VALUE");
        }
        if ("0".equals(attrVal))
        {
            String msg = "";
            if ("PersonalCallerBlack".equals(utypecode))
            {
                msg = "该用户[主叫黑名单]尚未开通,请先到多媒体桌面电话界面开通！";
            }
            else if ("PersonalCalleeBlack".equals(utypecode))
            {
                msg = "该用户[被叫黑名单]尚未开通,请先到多媒体桌面电话界面开通！";
            }
            else if ("PersonalCallerWhite".equals(utypecode))
            {
                msg = "该用户[主叫白名单]尚未开通,请先到多媒体桌面电话界面开通！";
            }
            else if ("PersonalCalleeWhite".equals(utypecode))
            {
                msg = "该用户[被叫白名单]尚未开通,请先到多媒体桌面电话界面开通！";
            }
            data.put("ERROR_MESSAGE", msg);
            flag = false;
        }

        if (!flag)
        {
            data.put("RESULT", "false");
        }
        setAjax(data);

    }

    /**
     * 作用：查询集团基本信息
     * 
     * @author luojh
     * @param cycle
     * @exception Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData conParams = getData();
        String groupId = conParams.getString("cond_GROUP_ID", "");
        if (StringUtils.isBlank(groupId))
        {
            groupId = conParams.getString("POP_cond_GROUP_ID");
        }

        // 调用后台服务，查集团客户信息
        IData custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);

        String custId = custInfo.getString("CUST_ID", "");
        // 调用后台服务，根据CUST_ID查询集团的用户信息
        IDataset userInfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustId(this, custId, false);

        IDataset imsInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(userInfos))
        {
            imsInfos.addAll(DataHelper.filter(userInfos, "PRODUCT_ID=2222")); // 测试的时间PRODUCT_ID填上.
        }
        if (IDataUtil.isEmpty(imsInfos))
        {
            IData errresult = setRetInfo(-1, "查询集团信息失败", "该集团客户编码[" + groupId + "]没有办理多媒体桌面电话产品，不能办理当前业务！");
            setAjax(errresult);
            return;
        }
        for (int i = 0; i < imsInfos.size(); i++)
        {
            IData imsData = imsInfos.getData(i);
            String product_id = imsData.getString("PRODUCT_ID");
            String serial_number = imsData.getString("SERIAL_NUMBER");
            String open_date = imsData.getString("OPEN_DATE");
            String user_id = imsData.getString("USER_ID");
            String eparchy_code = imsData.getString("EPARCHY_CODE");

            String imsName = product_id + "|" + serial_number + "|" + open_date + "|" + user_id;
            imsData.put("IMS_NAME", imsName);
            imsData.put("EPARCHY_CODE", eparchy_code);
        }
        setUserInfos(imsInfos);
        IData con = new DataMap();

        setGroupInfo(custInfo);
    }

    /**
     * 提交黑名单信息
     * 
     * @author tengg
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        String bw_list = data.getString("BW_LISTS"); // 得到表格的数据

        IDataset bw_lists = new DatasetList(bw_list);
        String grpUserId = data.getString("IMS_USER_ID", ""); // IMS用户user_id
        String userTypecode = data.getString("USER_TYPE_CODE").substring(0, 2); // luoyong 2011-3-21
        String bizcode = data.getString("USER_TYPE_CODE").substring(2); // luoyong 2011-3-21

        String user_eparchy_code = data.getString("USER_EPARCHY_CODE"); // 用户归属地州

        param.put("GRP_USER_ID", grpUserId);
        param.put("USER_ID", grpUserId);
        param.put("USER_TYPE_CODE", userTypecode);
        param.put("BIZ_CODE", bizcode);
        param.put("BW_LISTS", bw_lists);
        param.put(Route.USER_EPARCHY_CODE, user_eparchy_code); // 用户归属地州设置为交易地州

        IDataset resultInfos = new DatasetList();

        // 调用后台服务,提交数据
        resultInfos = CSViewCall.call(this, "SS.ImsBlackWhiteMemberBeanSVC.crtTrade", param);

        setAjax(resultInfos);
    }

    /**
     * 作用:根据serialNumber 获用户信息 (USER_ID)
     * 
     * @author
     * @param superSerialNumber
     * @throws Exception
     */
    public IData queryMebInfoBySn(String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        IDataset userInfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySn", data);
        if (IDataUtil.isEmpty(userInfos))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_472, serialNumber);
        }
        return userInfos.getData(0);
    }

    /**
     * 查询个人客户信息
     * 
     * @author luoyong
     * @throws Exception
     */
    public void queryMemberInfo(IRequestCycle cycle) throws Exception
    {
        IData mebInfo = new DataMap();
        IData conParams = getData();
        String serialNumber = conParams.getString("cond_SERIAL_NUMBER");
        // 查询用户信息
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        IData mebUCAInfoData = UCAInfoIntfViewUtil.qryMebUCAInfoBySn(this, serialNumber);
        IData userInfo = mebUCAInfoData.getData("MEB_USER_INFO");
        IData custInfo = mebUCAInfoData.getData("MEB_CUST_INFO");
        setMebCustInfo(custInfo);
        setMebUserInfo(userInfo);
        Boolean bool = check(userInfo);
        if (!bool)
        {
            CSViewException.apperr(GrpException.CRM_GRP_267, serialNumber);
        }
        // 用户、成员选择
        String userType = conParams.getString("USER_TYPE");
        IData con = new DataMap();
        con.put("USER_TYPE", userType);
        con.put("M_USER_ID", userInfo.getString("USER_ID"));
        mebInfo.putAll(custInfo);
        mebInfo.putAll(userInfo);
        setCondition(con);
        setMebInfo(mebInfo);
    }

    /**
     * 作用: 根据EC_USER_ID 查询下面的黑白名单
     * 
     * @author
     * @param cycle
     * @throws Exception
     */
    public void refreshListArea(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String ecUserId = param.getString("GRP_USER_ID", ""); // 取js中 设置的userId
        if (StringUtils.isBlank(ecUserId))
        {
            setInfos(new DatasetList());
            return;
        }
        String userTypecode = param.getString("USER_TYPE_CODE").substring(0, 2); // luoyong
        String bizcode = param.getString("USER_TYPE_CODE").substring(2); // luoyong
        String userType = param.getString("USER_TYPE", "");

        String eparchycode = Route.CONN_CRM_CG;

        IData params = new DataMap();
        params.put("EC_USER_ID", ecUserId);
        params.put("USER_TYPE_CODE", userTypecode);
        params.put("BIZ_CODE", bizcode);

        if ("M".equals(userType))
        {
            eparchycode = param.getString("EPARCHY_CODE", "");
            params.put("SERVICE_ID", "8171");
        }
        else
        {
            params.put("SERVICE_ID", "817");
        }

        IData data = new DataMap();
        data.put("USER_ID", ecUserId);
        data.put("REMOVE_TAG", "0");
        data.put(Route.ROUTE_EPARCHY_CODE, eparchycode);
        // 调用后台服务，查询用户信息

        IData userInfo = CSViewCall.callone(this, "CS.UserInfoQrySVC.getGrpUserInfoByUserId", data);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_496, ecUserId);
        }
        setUserEparchyCode(userInfo.getString("EPARCHY_CODE")); // 用户归属地州

        params.put(Route.ROUTE_EPARCHY_CODE, eparchycode);
        // 调用后台服务，查询成员黑白名单信息

        IDataset blackWhiteLists = CSViewCall.call(this, "CS.UserBlackWhiteInfoQrySVC.getBlackWhitedataByGSS", params);
        setInfos(blackWhiteLists);
    }

    public abstract void setCondition(IData condition);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setMebCustInfo(IData mebCustInfo);

    public abstract void setMebInfo(IData mebInfo);

    public abstract void setMebUserInfo(IData mebUserInfo);

    public abstract void setUserEparchyCode(String userEparchyCode);

    public abstract void setUserInfos(IDataset userInfos);
}
