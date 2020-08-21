
package com.asiainfo.veris.crm.order.web.group.imsmanage.closegrpmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class CloseGrpMain extends GroupBasePage
{
    /*
     * 闭合群关系新增
     */
    public void addCloseGrpuu(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        String deal_type = paramData.getString("CTL_TYPE", "");
        String strVpnUserId = paramData.getString("USER_ID", "-1");// 从前台取得用户USER_ID

        String close_grp_name = paramData.getString("close_grp_name", "");// 从前台获取闭合群名称
        String discnt_code = paramData.getString("discnt_code", "");
        String MAX_USER_LIMIT = paramData.getString("max_users");// 最大用户数
        String access_code = paramData.getString("access_code", "");// 代答接入码
        String hunting_team_type = paramData.getString("hunting_team_type", "");// 寻呼组类型

        String user_eparchy_code = paramData.getString("USER_EPARCHY_CODE", ""); // 用户归属地州
        IData param = new DataMap();
        // 用户归属地州设置为交易地州
        param.put(Route.USER_EPARCHY_CODE, user_eparchy_code);
        // 闭合群的新增
        if (deal_type.equals("0"))
        {
            IData inparam = new DataMap();

            // 新增前检查闭合群名称的唯一性
            inparam.put("USER_ID_A", strVpnUserId);
            inparam.put("RELATION_TYPE", "VB");

            // 通过USER_ID_A(集团用户ID)获取已有的VB关系
            IDataset outparams = CSViewCall.call(this, "SS.CloseGrpMgrSVC.getRelaData", inparam);
            if (IDataUtil.isNotEmpty(outparams))
            {
                for (int i = 0, size = outparams.size(); i < size; i++)
                {
                    IData bhData = outparams.getData(i);
                    String bh_name = bhData.getString("RSRV_STR1", "");
                    if (bh_name.equals(close_grp_name))
                    {
                        CSViewException.apperr(GrpException.CRM_GRP_331, bh_name);
                    }
                }
            }

            // 查集团未完工工单
            getMainTradeByCond(strVpnUserId, "1070");

            IData userDatavpn = getUserVpn(strVpnUserId);
            String vpn_no = userDatavpn.getString("VPN_NO");

            param.put("USER_ID", strVpnUserId); // 集团user_id
            param.put("RSRV_STR1", close_grp_name);// 闭合群名称保存在 uu表RSRV_STR1
            param.put("RSRV_STR2", MAX_USER_LIMIT);// 最大用户数保存在STR2字段
            param.put("RSRV_STR3", discnt_code);// 优惠编码保存在STR3字段
            param.put("VPN_NO", vpn_no); // 集团vpn_no

            IDataset result = CSViewCall.call(this, "SS.OutGrpMainSVC.addCloseGrpUU", param);
            setAjax(result);// 传递受理标示到前台
        }

        // 闭合群成员业务类型
        if (deal_type.equals("1"))
        {
            String serial_number_b = paramData.getString("cond_mem_sn", "");// 从前台取手机号
            String user_id_mem = paramData.getString("MEM_USER_ID"); // 前台不好传，直接从后台查成员的USER_ID
            String eparchy_code = paramData.getString("MEM_EPARCHY_CODE", "");

            String strCloseUserId = paramData.getString("cond_CLOSEGRPLIST", "");
            // 判断是否已是闭合群成员            IData input = new DataMap();

            input.put("USER_ID_A", strCloseUserId);
            input.put("USER_ID_B", user_id_mem);
            input.put("RELATION_TYPE_CODE", "MB");
            input.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
            IDataset outMemCloseInfo = CSViewCall.call(this, "SS.OutGrpMainSVC.getMemOutGrp", input);
            if (IDataUtil.isNotEmpty(outMemCloseInfo))
            {
                IData ids = outMemCloseInfo.getData(0);

                CSViewException.apperr(GrpException.CRM_GRP_441, serial_number_b, ids.getString("USER_ID_A"));
            }

            // 查询闭合群用户信息及闭合群的优惠编码
            IData data = new DataMap();
            data.put("USER_ID", strCloseUserId);
            IDataset userlist = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserInfoByUserIdForGrp", data);

            if (IDataUtil.isEmpty(userlist))
            {
                CSViewException.apperr(CrmUserException.CRM_USER_940, strCloseUserId);
            }
            IData userinfo = userlist.getData(0);
            String strDiscntCode = userinfo.getString("RSRV_STR3", "");
            String strCloseUserSn = userinfo.getString("SERIAL_NUMBER", "");

            IData userDatavpn = getUserVpn(strVpnUserId);
            String vpn_no = userDatavpn.getString("VPN_NO");

            // 开始设置新增参数
            param.put("USER_ID", strVpnUserId); // 集团user_id
            param.put("VPN_NO", vpn_no); // 集团vpn_no

            param.put("SERIAL_NUMBER", serial_number_b); // 成员sn

            param.put("USER_ID_A", strCloseUserId); // 闭合群userid
            param.put("SERIAL_NUMBER_A", strCloseUserSn); // 闭合群sn

            param.put("DISCNT_CODE", strDiscntCode);// 优惠编码
            param.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
            // 传给优惠表的数据

            IDataset result = CSViewCall.call(this, "SS.OutGrpMainSVC.addCloseMebUU", param);
            setAjax(result);// 传递受理标示到前台
        }

        // 寻呼组的新增
        if (deal_type.equals("2"))
        {
            // 查集团未完工工单
            getMainTradeByCond(strVpnUserId, "1070");

            IData userDatavpn = getUserVpn(strVpnUserId);
            String vpn_no = userDatavpn.getString("VPN_NO");

            // 开始设置UU新增参数
            param.put("USER_ID", strVpnUserId);
            param.put("RSRV_STR1", hunting_team_type);// 寻呼组类型保存在 uu表RSRV_STR1
            param.put("VPN_NO", vpn_no); // 集团vpn_no

            IDataset result = CSViewCall.call(this, "SS.OutGrpMainSVC.addHuntGrpUU", param);
            setAjax(result);// 传递受理标示到前台
        }
        // 代答组的新增
        if (deal_type.equals("3"))
        {
            // 查集团未完工工单
            getMainTradeByCond(strVpnUserId, "1070");

            IData userDatavpn = getUserVpn(strVpnUserId);
            String vpn_no = userDatavpn.getString("VPN_NO");

            // 开始设置UU新增参数
            param.put("USER_ID", strVpnUserId);
            param.put("RSRV_STR1", access_code);// 代答码保存在 uu表RSRV_STR1
            param.put("VPN_NO", vpn_no); // 集团vpn_no

            IDataset result = CSViewCall.call(this, "SS.OutGrpMainSVC.addDaidaGrpUU", param);
            setAjax(result);// 传递受理标示到前台
        }
    }

    /**
     * 根据成员闭合群信息，处理客户资料
     * 
     * @param dataset
     * @throws Exception
     */
    public void dealCloseMemInfo(IDataset dataset) throws Exception
    {
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, closeSize = dataset.size(); i < closeSize; i++)
            {
                IData closeData = dataset.getData(i);
                String serialNumber = closeData.getString("SERIAL_NUMBER_B"); // 成员sn

                IData userData = new DataMap();
                userData.put("SERIAL_NUMBER", serialNumber);
                // 查用户信息
                IData userInfo = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySn", userData);

                String custId = userInfo.getString("CUST_ID");
                String eparchyCode = userInfo.getString("EPARCHY_CODE");
                IData custData = new DataMap();
                custData.put("CUST_ID", custId);
                custData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
                // 查客户信息
                IData custInfo = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryCustInfoByCustId", custData);
                String custName = custInfo.getString("CUST_NAME");

                closeData.put("MEM_CUST_NAME", custName);
            }
            setMemclosegrpInfoList(dataset); // 设置成员闭合群信息
        }
    }

    /*
     * 删除闭合群（集团、成员）
     */
    public void delCloseGrpInfo(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        String grpUserId = paramData.getString("USER_ID", "-1");// 集团用户id
        String deal_type = paramData.getString("CTL_TYPE", "");
        String user_eparchy_code = paramData.getString("USER_EPARCHY_CODE", ""); // 用户归属地州

        IData param = new DataMap();
        // 设置用户归属地州为交易地州
        param.put(Route.USER_EPARCHY_CODE, user_eparchy_code);

        // 0为集团闭合群删除
        if (deal_type.equals("0"))
        {
            String szoutPone = paramData.getString("outgrplist");
            String[] a = StringUtils.split(szoutPone, "_");
            String strVpnUserId = a[0]; // v网用户ID
            String strCloseUserId = a[1]; // 闭合群虚拟ID
            String strCloseUserSn = a[2]; // 闭合群Sn

            IData userDatavpn = getUserVpn(strVpnUserId);
            String vpn_no = userDatavpn.getString("VPN_NO");

            String MAX_USER_LIMIT = "100";

            IData querycond = new DataMap();
            querycond.put("USER_ID_LIST", strCloseUserId);
            querycond.put("USER_ID_A", strCloseUserId);
            querycond.put("RELATION_TYPE_CODE", "MB");
            IDataset mblist = CSViewCall.call(this, "SS.OutGrpMainSVC.getCloseGrpMemAllDb", querycond);
            if (IDataUtil.isNotEmpty(mblist))
            {
                CSViewException.apperr(GrpException.CRM_GRP_448);
            }

            // 查集团未完工工单
            getMainTradeByCond(strVpnUserId, "1070");

            // 设置删除操作的数据集
            param.put("USER_ID", strVpnUserId);
            param.put("USER_ID_B", strCloseUserId);
            param.put("SERIAL_NUMBER_B", strCloseUserSn);
            param.put("RSRV_STR2", MAX_USER_LIMIT);
            param.put("VPN_NO", vpn_no); // 集团vpn_no

            IDataset outparams = CSViewCall.call(this, "SS.OutGrpMainSVC.delCloseGrpUU", param);
            setAjax(outparams);// 传递受理标示到前台
        }

        // 集团成员闭合群删除
        if (deal_type.equals("1"))
        {
            String memcloseGrplist = paramData.getString("memclosegrplist");
            String[] a = StringUtils.split(memcloseGrplist, "_");

            String strUserIdA = a[0];
            String strCloseUserSn = a[2];
            String strDiscntCode = a[3];
            String strSnB = a[4];

            if (StringUtils.isBlank(strDiscntCode))
            {
                CSViewException.apperr(GrpException.CRM_GRP_518);
            }

            // 查询当前的集团用户资料
            IData userDatavpn = getUserVpn(grpUserId);
            String vpn_no = userDatavpn.getString("VPN_NO");

            // 设置删除操作的数据集
            param.put("USER_ID_A", strUserIdA); // 闭合群user_id
            param.put("SERIAL_NUMBER_A", strCloseUserSn); // 闭合群sn
            param.put("SERIAL_NUMBER", strSnB); // 成员sn
            param.put("DISCNT_CODE", strDiscntCode);
            param.put("USER_ID", grpUserId);// 得到VPMN集团用户ID
            param.put("VPN_NO", vpn_no); // 集团vpn_no

            IData userDataMap = UCAInfoIntfViewUtil.qryUserInfoBySnNoProduct(this, strSnB);
            String eparchy_code = userDataMap.getString("EPARCHY_CODE");
            param.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);

            IDataset outparams = CSViewCall.call(this, "SS.OutGrpMainSVC.delCloseMebUU", param);
            setAjax(outparams);// 传递受理标示到前台
        }
        // 2：寻呼组业务
        if (deal_type.equals("2"))
        {
            String szoutPone = paramData.getString("xunhugrplist");
            String[] a = StringUtils.split(szoutPone, "_");
            String strVpnUserId = a[0]; // v网用户ID
            String strCloseUserId = a[1]; // 虚拟ID
            String strCloseSn = a[2]; // 虚拟sn
            IData userDatavpn = getUserVpn(strVpnUserId);
            String vpn_no = userDatavpn.getString("VPN_NO");

            // 查集团未完工工单
            getMainTradeByCond(strVpnUserId, "1070");

            // 设置删除操作的数据集
            param.put("USER_ID", strVpnUserId);
            param.put("USER_ID_B", strCloseUserId);
            param.put("SERIAL_NUMBER_B", strCloseSn);
            param.put("VPN_NO", vpn_no);

            IDataset outparams = CSViewCall.call(this, "SS.OutGrpMainSVC.delHuntGrpUU", param);
            setAjax(outparams);// 传递受理标示到前台

        }
        // 3：代答组
        if (deal_type.equals("3"))
        {
            String szoutPone = paramData.getString("daidagrplist");
            String[] a = StringUtils.split(szoutPone, "_");
            String strVpnUserId = a[0]; // v网用户ID
            String strCloseUserId = a[1]; // 虚拟ID
            String strCloseSn = a[2]; // 虚拟sn

            IData userDatavpn = getUserVpn(strVpnUserId);
            String vpn_no = userDatavpn.getString("VPN_NO");

            // 查集团未完工工单
            getMainTradeByCond(strVpnUserId, "1070");

            // 设置删除操作的数据集
            param.put("USER_ID", strVpnUserId);
            param.put("USER_ID_B", strCloseUserId);
            param.put("SERIAL_NUMBER_B", strCloseSn);
            param.put("VPN_NO", vpn_no);

            IDataset outparams = CSViewCall.call(this, "SS.OutGrpMainSVC.delDaidaGrpUU", param);
            setAjax(outparams);// 传递受理标示到前台
        }
    }

    /**
     * 根据userId查闭合群
     * 
     * @param userId
     * @param relationType
     * @throws Exception
     */
    public IDataset getCloseGrpList(String userId, String relationType) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID_A", userId);
        data.put("RELATION_TYPE", relationType);
        // 查集团用户下的闭合群
        IDataset grpCloseList = CSViewCall.call(this, "SS.CloseGrpMgrSVC.getRelaData", data);
        if (IDataUtil.isNotEmpty(grpCloseList))
        {
            setClosegrplist(grpCloseList);
        }
        return grpCloseList;
    }

    /**
     * 查询集团用户的代答组信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getDaiDainfo(IRequestCycle cycle) throws Exception
    {
        IData paramData = new DataMap();
        IData ctl_type = new DataMap();
        String grpuserid = paramData.getString("phone_user_id");
        String deal_type = paramData.getString("CTL_TYPE", "");

        ctl_type.put("CTL_TYPE", deal_type);

        IData inparam = new DataMap();
        inparam.put("USER_ID_A", grpuserid);
        inparam.put("RELATION_TYPE", "DD");

        // 查代答组信息
        IDataOutput dd = CSViewCall.callPage(this, "SS.CloseGrpMgrSVC.getRelaData", inparam, getPagination("ActiveNav3"));
        IDataset result = dd.getData();
        long daiDaCount = dd.getDataCount();
        if (IDataUtil.isNotEmpty(result))
        {
            setOutgrpInfoList(result);
        }
        setInfosCount(daiDaCount);
        setCondition(ctl_type);
    }

    /**
     * 查询集团客户信息
     * 
     * @param cycle
     * @throws Exception
     */

    public void getGrpCustInfos(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        String grp_serial = paramData.getString("cond_QueryCondition", "");
        IData grpUserInfoData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, grp_serial);
        setGrpuserinfo(grpUserInfoData);
        // 业务限制
        String user_brand = grpUserInfoData.getString("BRAND_CODE", "");
        if (!user_brand.equals("VPMN") && !user_brand.equals("VPCN"))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_768);
        }
        String cust_id = grpUserInfoData.getString("CUST_ID", "");

        // 查询集团客户资料
        IData inparam = new DataMap();
        inparam.put("CUST_ID", cust_id);
        IDataset outparams = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", inparam);
        if (IDataUtil.isNotEmpty(outparams))
        {
            IData grpinfo = outparams.getData(0);
            setGrpcustinfo(grpinfo);
        }
    }

    /**
     * 查询集团闭合群信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getGrpOutinfo(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        IData ctl_type = new DataMap();
        String grpuserid = paramData.getString("phone_user_id");
        String deal_type = paramData.getString("CTL_TYPE", "");

        if (StringUtils.isBlank(deal_type))
        {
            deal_type = "0"; // 默认集团闭合群业务
        }
        ctl_type.put("CTL_TYPE", deal_type);

        IData inparam = new DataMap();
        inparam.put("USER_ID_A", grpuserid);
        inparam.put("RELATION_TYPE", "VB");

        // 调用后台服务，查集团闭合群信息
        IDataOutput retData = CSViewCall.callPage(this, "SS.CloseGrpMgrSVC.getRelaData", inparam, getPagination("ActiveNav"));
        IDataset result = retData.getData();
        long closeGrpCount = retData.getDataCount();
        if (IDataUtil.isNotEmpty(result))
        {
            setOutgrpInfoList(result);
        }
        setInfosCount(closeGrpCount);
        setCondition(ctl_type);
    }

    public void getInitvalue(IRequestCycle cycle) throws Exception
    {
        IData initvalue = new DataMap();
        initvalue.put("MAX_USERS", "100");

        setInitmaxusers(initvalue);
        IData inparam = new DataMap();

        // 取TD_B_DISCNT
        IDataset out_discnt = CSViewCall.call(this, "CS.DiscntInfoQrySVC.getDiscntlist", inparam);
        setDiscntlist(out_discnt);
    }

    /**
     * 根据业务类型，user_id查用户未完工工单
     * 
     * @param user_id_a
     * @param trade_type_code
     * @throws Exception
     */
    private void getMainTradeByCond(String user_id, String tradeTypeCode) throws Exception
    {
        IData input = new DataMap();
        input.put("USER_ID_B", user_id); // 集团user_id
        input.put("TRADE_TYPE_CODE", tradeTypeCode);

        IDataset colsegrpset = CSViewCall.call(this, "CS.TradeInfoQrySVC.getMainTradeByCond", input);
        if (IDataUtil.isNotEmpty(colsegrpset))
        {
            IData map = colsegrpset.getData(0);
            String tradeId = map.getString("TRADE_ID");
            String acceptDate = map.getString("ACCEPT_DATE");

            CSViewException.apperr(GrpException.CRM_GRP_94, tradeTypeCode, tradeId, acceptDate);
        }
    }

    public void getMemCloseBySn(IRequestCycle cycle) throws Exception
    {
        IData pamraData = getData();
        String mem_sn = pamraData.getString("MEM_SN", "");
        String strCloseUserId = pamraData.getString("cond_CLOSEGRPLIST", ""); // 闭合群列表
        IData userdata = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, mem_sn);

        String user_id_mem = userdata.getString("USER_ID", "");
        String eparchy_code = userdata.getString("EPARCHY_CODE", "");

        IData inparam = new DataMap();
        inparam.put("USER_ID_A", strCloseUserId);
        inparam.put("USER_ID_B", user_id_mem);
        inparam.put("RELATION_TYPE_CODE", "MB");
        inparam.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
        IDataset outMemCloseInfo = CSViewCall.call(this, "SS.OutGrpMainSVC.getMemOutGrp", inparam);
        setMemclosegrpInfoList(outMemCloseInfo);// 设置成员闭合群信息

    }

    /*
     * 查询成员的闭合群列表
     */
    public void getMemCloseGrp(String userId, String closeUserId) throws Exception
    {
        String closeUserList = "";
        IData inparam = new DataMap();
        // 查闭合群列表
        IDataset grpCloseList = getCloseGrpList(userId, "VB");

        // 没有选择闭合群，则按集团所有的闭合群，查成员闭合群的信息
        if (StringUtils.isBlank(closeUserId))
        {
            for (int i = 0, size = grpCloseList.size(); i < size; i++)
            {
                IData idData = grpCloseList.getData(i);
                String strUserIdB = idData.getString("USER_ID_B", "");
                if (closeUserList.length() != 0)
                {
                    closeUserList = closeUserList + ", " + strUserIdB;
                }
                else
                {
                    closeUserList = strUserIdB;
                }
            }
        }

        inparam.put("USER_ID_LIST", closeUserList);
        inparam.put("USER_ID_A", closeUserId);
        inparam.put("RELATION_TYPE_CODE", "MB");
        // 调用后台服务，查成员闭合群信息(UU关系)
        IDataset outMemCloseInfo = CSViewCall.call(this, "SS.OutGrpMainSVC.getCloseGrpMemAllDb", inparam);

        // 处理客户资料
        dealCloseMemInfo(outMemCloseInfo);
    }

    /**
     * 查询成员的信息
     * 
     * @param cycle
     * @throws Exception
     */

    public void getMemInfo(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        String mem_sn = paramData.getString("MEM_SN", "");
        String grpUserId = paramData.getString("phone_user_id"); // 集团用户userId
        String deal_type = paramData.getString("CTL_TYPE", "");

        if (StringUtils.isBlank(mem_sn))
        {
            // 查成员闭合群信息，没有选择闭合群
            getMemCloseGrp(grpUserId, "");
        }
        else
        {
            IData inparam = new DataMap();
            inparam.put("SERIAL_NUMBER", mem_sn);
            IData userdata = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySn", inparam);
            if (IDataUtil.isEmpty(userdata))
            {
                CSViewException.apperr(GrpException.CRM_GRP_101, mem_sn);
            }
            String user_id_mem = userdata.getString("USER_ID", "");
            String eparchy_code = userdata.getString("EPARCHY_CODE", "");
            setMeminfo(userdata);

            IData param = new DataMap();
            param.put("USER_ID_A", grpUserId);
            param.put("SERIAL_NUMBER_B", mem_sn);
            param.put("USER_ID_B", user_id_mem);
            param.put("RELATION_TYPE_CODE", "20");
            param.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);

            IDataset outIsMem = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.checkMemRelaByUserIdb", param);
            if (IDataUtil.isEmpty(outIsMem))
            {
                CSViewException.apperr(GrpException.CRM_GRP_27, mem_sn);
            }

            // 查闭合群列表
            getCloseGrpList(grpUserId, "VB");

            IData input = new DataMap();
            input.put("RELATION_TYPE_CODE", "MB");
            input.put("USER_ID_B", user_id_mem);
            input.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);

            // 通过USER_ID_A(集团用户ID)获取已有的VB关系
            IDataOutput dd = CSViewCall.callPage(this, "SS.OutGrpMainSVC.getMemOutGrp", input, getPagination("ActiveNav1"));
            IDataset outMemCloseInfo = dd.getData();
            long tt = dd.getDataCount();

            // 处理客户资料
            dealCloseMemInfo(outMemCloseInfo);
            setInfosCount(tt);

            // 回设条件
            IData con_param = new DataMap();
            con_param.put("cond_mem_serial", mem_sn);
            con_param.put("CTL_TYPE", deal_type);
            setCondition(con_param);
        }
    }

    /**
     * 查用户vpn信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public IData getUserVpn(String userId) throws Exception
    {
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userId, false);

        if (IDataUtil.isEmpty(userVpnData))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_726);
        }
        return userVpnData;
    }

    /**
     * 查询集团用户的寻呼组信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getXunhuinfo(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        IData ctl_type = new DataMap();
        String grpuserid = paramData.getString("phone_user_id");
        String deal_type = paramData.getString("CTL_TYPE", "");
        ctl_type.put("CTL_TYPE", deal_type);

        IData inparam = new DataMap();
        inparam.put("USER_ID_A", grpuserid);
        inparam.put("RELATION_TYPE", "XH");

        // 查寻呼组信息
        IDataOutput retData = CSViewCall.callPage(this, "SS.CloseGrpMgrSVC.getRelaData", inparam, getPagination("ActiveNav2"));
        IDataset result = retData.getData();
        long huntDataCount = retData.getDataCount();
        if (IDataUtil.isNotEmpty(result))
        {
            setOutgrpInfoList(result);
        }
        setInfosCount(huntDataCount);
        setCondition(ctl_type);
    }

    public abstract void setClosegrplist(IDataset closelist);

    public abstract void setCondition(IData Con);

    public abstract void setDiscntlist(IDataset discntlist);

    public abstract void setGrpcustinfo(IData grpcust);

    public abstract void setGrpuserid(String grpid);

    public abstract void setGrpuserinfo(IData grpUserinfo);

    public abstract void setInfosCount(long count);

    public abstract void setInitmaxusers(IData time);

    public abstract void setMemclosegrpInfoList(IDataset memcloselist);

    public abstract void setMeminfo(IData memdata);

    public abstract void setOutgrpInfoList(IDataset outgrplist);

    public abstract void setOutPhoneInfo(IData outphone);

    public abstract void setVpmn(IData GrpPhoneinfo);
}
