
package com.asiainfo.veris.crm.order.web.group.imsmanage.outgrpMain;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
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
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class OutGrpMain extends GroupBasePage
{
    /*
     * 网外关系新增
     */
    public void addOutUU(IRequestCycle cycle) throws Exception
    {
        IDataset result = new DatasetList();
        IData inparam = getData();

        String deal_type = inparam.getString("CTL_TYPE", "");
        String user_eparchy_code = inparam.getString("USER_EPARCHY_CODE", ""); // 用户归属地州
        String grp_user_id = inparam.getString("GRP_USER_ID"); // 集团userId
        String out_grp_num = inparam.getString("out_grp_num"); // 网外号码

        if (StringUtils.isBlank(out_grp_num))
        {
            CSViewException.apperr(GrpException.CRM_GRP_612);
        }

        IData param = new DataMap();
        // 用户归属地州设置为交易地州
        param.put(Route.USER_EPARCHY_CODE, user_eparchy_code);
        if (deal_type.equals("0"))// 集团网外号码新增
        {
            // 查集团未完工工单
            getMainTradeByCond(grp_user_id, "3915");

            param.put("USER_ID", grp_user_id); // 集团userId
            param.put("OUT_GRP_NUM", out_grp_num);// 取网外号码

            result = CSViewCall.call(this, "SS.OutGrpMainSVC.addOutNumGrpUU", param);
        }
        else if (deal_type.equals("1"))
        {
            param.put("SERIAL_NUMBER", getParameter("cond_mem_sn", ""));// 成员手机号码
            param.put("OUT_GRP_NUM", out_grp_num);// 取网外号码
            param.put("USER_ID", grp_user_id); // 集团user_id
            result = CSViewCall.call(this, "SS.OutGrpMainSVC.addOutNumMebUU", param);
        }

        setAjax(result);
    }

    /**
     * 激发成员网外信息时清除集团的网外关系
     * 
     * @param cycle
     * @throws Exception
     */

    public void delGrpOutinfo(IRequestCycle cycle) throws Exception
    {
        IDataset setList = new DatasetList();
        setOutgrpInfoList(setList);
    }

    /*
     * 删除网外关系
     */
    public void delOutGrpInfo(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        IDataset outparams = new DatasetList();
        String szoutPone = paramData.getString("OUT_GRP_LIST");
        String grp_user_id = paramData.getString("GRP_USER_ID", "");

        String deal_type = paramData.getString("CTL_TYPE", "");

        String user_eparchy_code = paramData.getString("USER_EPARCHY_CODE", ""); // 用户归属地州
        IData param = new DataMap();

        // 设置用户归属地州为交易地州
        param.put(Route.USER_EPARCHY_CODE, user_eparchy_code);
        // 0为集团网外业务
        if (deal_type.equals("0"))
        {
            // 查集团未完工工单
            getMainTradeByCond(grp_user_id, "3915");

            String[] a = szoutPone.split("_");

            // 设置删除操作的数据集
            param.put("USER_ID", a[0]);// 集团的
            param.put("USER_ID_B", a[1]);// 网外的
            param.put("SERIAL_NUMBER_B", a[2]);// 网外的

            outparams = CSViewCall.call(this, "SS.OutGrpMainSVC.delOutNumGrpUU", param);
        }

        // 1为成员网外业务
        if (deal_type.equals("1"))
        {
            String[] a = szoutPone.split("_");

            // 设置删除操作的数据集
            param.put("USER_ID_B", a[0]);// 网外的
            param.put("SERIAL_NUMBER", a[1]);// 成员的
            param.put("SERIAL_NUMBER_B", a[2]);// 网外的
            param.put("USER_ID", grp_user_id); // 集团user_id

            outparams = CSViewCall.call(this, "SS.OutGrpMainSVC.delOutNumMebUU", param);
        }

        setAjax(outparams);
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
        if (StringUtils.isBlank(grp_serial))
        {
            CSViewException.apperr(GrpException.CRM_GRP_33);
        }

        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", grp_serial);
        // 查用户信息
        IDataset userlist = getUserInfos(inparam);

        IData grpUserInfo = userlist.getData(0);
        String cust_id = grpUserInfo.getString("CUST_ID", "");
        String user_id = grpUserInfo.getString("USER_ID", "");
        String product_id = userlist.getData(0).getString("PRODUCT_ID", "");

        if (!product_id.equals("8001"))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_767);
        }

        // 查询集团客户资料
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        IDataset outparams = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", param);
        if (IDataUtil.isEmpty(outparams))
        {
            CSViewException.apperr(GrpException.CRM_GRP_190);
        }
        IData grpinfo = outparams.getData(0);
        // 查集团VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, user_id, false);

        if (IDataUtil.isEmpty(userVpnData))
        {
            CSViewException.apperr(GrpException.CRM_GRP_336);
        }

        setGrpuserid(user_id);
        setGrpcustinfo(grpinfo);
        setGrpuserinfo(grpUserInfo);
        setVpmn(userVpnData);
    }

    /**
     * 查询集团用户的网外信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getGrpOutinfo(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData();
        IData crtl_type = new DataMap();
        String grpuserid = paramData.getString("phone_user_id");
        String deal_type = paramData.getString("CTL_TYPE", "");
        if (StringUtils.isBlank(deal_type))
        {
            deal_type = "0"; // 默认集团业务
        }
        crtl_type.put("CRTL_TYPE", deal_type);
        IData inparam = new DataMap();
        inparam.put("USER_ID_A", grpuserid);

        IDataOutput outInfo = CSViewCall.callPage(this, "CS.RelaUUInfoQrySVC.getGrpOutinfo", inparam, getPagination("ActiveNav"));
        IDataset result = outInfo.getData();
        long outInfoCount = outInfo.getDataCount();

        setOutgrpInfoList(result);
        setInfosCount(outInfoCount);
        setCondition(crtl_type);
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
        String grpid = paramData.getString("phone_user_id");
        if (StringUtils.isBlank(mem_sn))
        {
            CSViewException.apperr(GrpException.CRM_GRP_30);
        }

        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", mem_sn);
        // 查询用户信息
        IData userdata = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySn", inparam);
        if (IDataUtil.isEmpty(userdata))
        {
            CSViewException.apperr(GrpException.CRM_GRP_468, mem_sn);
        }

        String user_id_mem = userdata.getString("USER_ID", "");
        String eparchy_code = userdata.getString("EPARCHY_CODE");

        IData vpnData = new DataMap();
        vpnData.put("USER_ID_A", grpid);
        vpnData.put("USER_ID_B", user_id_mem);
        vpnData.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
        IDataset outIsMem = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.fn_isVpmnMEM", vpnData);
        if (IDataUtil.isEmpty(outIsMem))
        {
            CSViewException.apperr(GrpException.CRM_GRP_39);
        }

        IData mebData = new DataMap();
        mebData.put("USER_ID_MEM", user_id_mem);
        mebData.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
        IDataset outparams = CSViewCall.call(this, "CS.UserInfoQrySVC.getMemInfo", mebData);
        if (IDataUtil.isNotEmpty(outparams))
        {
            IData mem_data = outparams.getData(0);
            setMeminfo(mem_data);
        }

        // 回设条件
        IData con_param = new DataMap();
        con_param.put("cond_mem_serial", mem_sn);
        setCondition(con_param);
    }

    /**
     * 查询成员的网外号码信息
     * 
     * @param cycle
     * @throws Exception
     */

    public void getMemOutGrp(IRequestCycle cycle) throws Exception
    {
        IData inparam = new DataMap();
        IData data = getData();
        String mem_sn = data.getString("cond_mem_sn", "000000");// 成员手机号码
        String mem_user_id = data.getString("mem_user_id", "0000");
        String deal_type = data.getString("CTL_TYPE", "");
        if (StringUtils.isBlank(deal_type))
        {
            deal_type = "1";
        }

        inparam.put("SERIAL_NUMBER", mem_sn);
        IDataset mofficelist = CSViewCall.call(this, "CS.RouteInfoQrySVC.getEparchyCodeBySn", inparam);
        if (IDataUtil.isEmpty(mofficelist))
        {
            CSViewException.apperr(GrpException.CRM_GRP_494, mem_sn);
        }
        String routeEparchyCode = mofficelist.getData(0).getString("EPARCHY_CODE");

        inparam.clear();
        inparam.put("USER_ID_A", mem_user_id);
        inparam.put("SERIAL_NUMBER_A", mem_sn);
        inparam.put("RELATION_TYPE_CODE", "MO");
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);

        IDataOutput mebOutGrpInfo = CSViewCall.callPage(this, "CS.RelaUUInfoQrySVC.getMemOutGrpNumber", inparam, getPagination("ActiveNav2"));
        IDataset outparams = mebOutGrpInfo.getData();
        long outParamsCount = mebOutGrpInfo.getDataCount();

        setOutgrpInfoList(outparams);
        setInfosCount(outParamsCount);

        // 回设条件
        IData con_param = new DataMap();
        con_param.put("cond_mem_serial", mem_sn);
        con_param.put("CRTL_TYPE", deal_type);
        setCondition(con_param);
    }

    /**
     * 根据sn查询用户信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    private IDataset getUserInfos(IData param) throws Exception
    {
        String grp_serial = param.getString("SERIAL_NUMBER");

        IDataset userlist = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", param);

        if (IDataUtil.isEmpty(userlist))
        {
            CSViewException.apperr(GrpException.CRM_GRP_471, grp_serial);
        }
        return userlist;
    }

    public abstract void setCondition(IData Con);

    public abstract void setGrpcustinfo(IData grpcust);

    public abstract void setGrpuserid(String grpid);

    public abstract void setGrpuserinfo(IData grpUserinfo);

    public abstract void setInfosCount(long count);

    public abstract void setMeminfo(IData memdata);

    public abstract void setOutgrpInfoList(IDataset outgrplist);

    public abstract void setOutPhoneInfo(IData outphone);

    public abstract void setStartEndData(IData time);

    public abstract void setVpmn(IData GrpPhoneinfo);
}
